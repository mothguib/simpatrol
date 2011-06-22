package FBA;

import java.io.IOException;
import java.util.LinkedList;
import java.util.Vector;

import javax.xml.parsers.ParserConfigurationException;

import org.xml.sax.SAXException;

import util.StringAndDouble;
import util.graph.Edge;
import util.graph.Graph;
import util.graph.GraphTranslator;
import util.graph.Node;
import util.net.UDPClientConnection;

public class FlexibleBidder2Agent extends CommunicatorAgent2 {
	
	
	protected int cycles_without_exchange = 0;
	protected int agents_num;
	protected int nb_min_nodes;
	protected int nb_max_propositions;
	protected final static int NETWORK_QUALITY = 5;
	protected final static int MAX_VISITS_WITHOUT_EXCHANGE = 10;
	
	
	protected boolean first_time = true;
	
	protected double visit_cost;
	
	// importance of idleness in calculating heuristic destination
	protected double idleness_rate_p; //path_idleness_rate; (not used here)
	// importance of idleness in calculating nodes to trade
	protected double idleness_rate_a; // distance_rate;
	
	/** The plan of walking through the graph. */
	protected LinkedList<String> plan;
	protected double length_of_last_edge = 0;
	 
	protected LinkedList<TransactionNodes> engaged_transactions;
	protected int nb_engaged_nodes = 0; 
	 
	// Select node(s) to exchange. the agent can have only one node...
	protected LinkedList<String> bids;
	protected String best_proposition1 = null;
	protected String best_proposition2 = null;
 	
 	//transactions
	protected static int transaction_id = 0;
	protected LinkedList<String> FBABuyers;
	protected LinkedList<ComplexBid> ComplexPropositions;
	protected int myCurrentTransaction = -1;
	protected int received_answers = 0;
	
	//received propositions
	protected String best_offer1 = null;
	protected int best_offer1_idle = 0;
	protected String best_offer2 = null;
	protected int best_offer2_idle = 0;
	protected String special_offer = null;
	protected int special_offer_idle = 0;
	protected int best_buyer = -1;
	 
	
	public FlexibleBidder2Agent(String id, int number_of_agents, LinkedList<String> nodes, double idleness_rate_for_path, double idleness_rate_for_auction) {
		super(id, nodes);
		
		plan = new LinkedList<String>();
		engaged_transactions = new LinkedList<TransactionNodes>();
		
		agents_num = number_of_agents;
		idleness_rate_p = idleness_rate_for_path;
		idleness_rate_a = idleness_rate_for_auction;
		nb_min_nodes = (int) Math.min(2, Math.ceil((double)this.myNodes.size() / 2.0)) ;
		nb_max_propositions = (int) Math.ceil((double)this.myNodes.size() / 2.0) ;
		
		if(this.getMyNodes().size() > 0){
			String message = "Agent " + this.agent_id + " starting with ";
			for(String bla : this.getMyNodes())
				message += bla + ", ";
			message = message.substring(0, message.length() - 2) + ".";
			System.out.println(message);
		}
		else {
			System.out.println("Agent " + this.agent_id + " starting.");
		}
		
	}
	
	
	public FlexibleBidder2Agent(String id, double entering_time, double quitting_time, 
											int number_of_agents, LinkedList<String> nodes, 
											double idleness_rate_for_path, double idleness_rate_for_auction) {
		super(id, entering_time, quitting_time, nodes);
		
		plan = new LinkedList<String>();
		engaged_transactions = new LinkedList<TransactionNodes>();
		
		agents_num = number_of_agents;
		idleness_rate_p = idleness_rate_for_path;
		idleness_rate_a = idleness_rate_for_auction;
		nb_min_nodes = (int) Math.min(2, Math.ceil((double)this.myNodes.size() / 2.0)) ;
		nb_max_propositions = (int) Math.ceil((double)this.myNodes.size() / 2.0) ;
		
		if(this.getMyNodes().size() > 0){
			String message = "Agent " + this.agent_id + " starting with ";
			for(String bla : this.getMyNodes())
				message += bla + ", ";
			message = message.substring(0, message.length() - 2) + ".";
			System.out.println(message);
		}
		else {
			System.out.println("Agent " + this.agent_id + " starting.");
		}
		
	}

	
	/**
	 * Lets the agent perceive its current position.
	 * 
	 * @param perceptions
	 *            The current perceptions of the agent.
	 * @return The current position of the agent, as a pair "current vertex id -
	 *         elapsed length on the current edge".
	 */
	protected StringAndDouble perceiveCurrentPosition(String perception) {
		if (perception.indexOf("<perception type=\"4\"") > -1) {
			// obtains the id of the current vertex
			int vertex_id_index = perception.indexOf("node_id=\"");
			perception = perception.substring(vertex_id_index + 9);
			String vertex_id = perception.substring(0, perception.indexOf("\""));

			// obtains the elapsed length on the current edge
			double elapsed_length = 0;
			int elapsed_length_index = perception.indexOf("elapsed_length=\"");
			if(elapsed_length_index != -1){
				perception = perception.substring(elapsed_length_index + 16);
				elapsed_length = Double.parseDouble(perception.substring(0, perception.indexOf("\"")));
			}
			// returns the answer of the method
			return new StringAndDouble(vertex_id, elapsed_length);
		}

		// default answer
		return null;
	}
	
	/**
	 * Perceives the graph to be patrolled.
	 * 
	 * @param perception
	 *            The perception sent by SimPatrol server.
	 * @return The perceived graph.
	 * @throws IOException
	 * @throws SAXException
	 * @throws ParserConfigurationException
	 */
	protected Graph perceiveGraph(String perception) throws ParserConfigurationException, SAXException, IOException {
		Graph[] parsed_perception = GraphTranslator.getGraphs(GraphTranslator.parseString(perception));
		if (parsed_perception.length > 0)
			return parsed_perception[0];

		return null;
	}
	
	/**
	 * Lets the agent visit the current node
	 * 
	 * @throws IOException
	 */
	protected void visitCurrentNode(String node_name) throws IOException {
		String message = "<action type=\"2\"/>";
		this.connection.send(message);
		
		this.setIdleness(node_name, 0);
		
		System.out.println("Agent " + this.agent_id + " visiting " + node_name);
		
	}
	
	
	/**
	 * Lets the agent go to the next node
	 * 
	 * @param next_vertex_id
	 *            The next node the agent is supposed to go to.
	 * @throws IOException
	 */
	protected void GoTo(String next_vertex_id) throws IOException {
		String message = "<action type=\"1\" node_id=\"" + next_vertex_id + "\"/>";
		this.connection.send(message);
	}
	
	
	/**
	 * Calculates a heuristic path in the graph from position to goal
	 * 
	 * @param position
	 * 				the id of the starting node
	 * @param goal
	 * 				the id of the destination node
	 * @param graph
	 *				the graph in which the agent moves
	 */
	protected void Pathfinding(String position, String goal, Graph graph) {
		this.plan.clear();
		
		if(position.equals(goal)){
			plan.add(position);
			return;
		}
		
		// obtains the vertex of the beginning
		Node begin_vertex = new Node("");
		begin_vertex.setObjectId(position);

		// obtains the goal vertex
		Node end_vertex = new Node("");
		end_vertex.setObjectId(goal);

		// obtains the dijkstra path
		Graph path = graph.getIdlenessedDijkstraPath(begin_vertex, end_vertex);

		// adds the ordered vertexes in the plan of the agent
		Node[] path_vertexes = path.getNodees();
		for (int i = 0; i < path_vertexes.length; i++)
			if (path_vertexes[i].equals(begin_vertex)) {
				begin_vertex = path_vertexes[i];
				break;
			}
		
		if (begin_vertex.getEdges().length > 0) {
			Node current_vertex = begin_vertex.getEdges()[0].getOtherNode(begin_vertex);
			Edge[] current_vertex_edges = current_vertex.getEdges();
			this.plan.add(current_vertex.getObjectId());

			while (current_vertex_edges.length > 1) {
				Node next_vertex = current_vertex_edges[0].getOtherNode(current_vertex);

				if (this.plan.contains(next_vertex.getObjectId()) || next_vertex.equals(begin_vertex)) {
					current_vertex = current_vertex_edges[1].getOtherNode(current_vertex);
				} else
					current_vertex = next_vertex;

				this.plan.add(current_vertex.getObjectId());
				current_vertex_edges = current_vertex.getEdges();
			}
		}
	}
	

	/**
	 * Chooses heuristically the best node in myNodes to go to
	 * 
	 * @param current_node
	 * 					the current node
	 * @param idleness_rate
	 * 					the importance of idleness in the heuristic calculus
	 * @return
	 * 			the id (String) of the next node to go to
	 */
	protected String NextHeuristicNode(String current_node, double idleness_rate){
		if(myNodes.size() == 0)
			return null;
		if(myNodes.size() == 1)
			return this.getMyNodes(0);
		
		double max_idleness = this.calculateHighestIdleness();
		double min_idleness = this.calculateMinorIdleness();
		
		double max_distance = this.calculateHighestDistance(this.getMyNodes());
		double min_distance = this.calculateMinorDistance(this.getMyNodes());
		
		double max_node_value = Double.NEGATIVE_INFINITY;
		LinkedList<String> nodes_max = new LinkedList<String>();
		
		for(StringAndDouble node : this.myNodes){
			if(!node.STRING.equals(current_node)){
				//double idleness = graph.getNode(node).getIdleness();
				double idleness = node.double_value;
				double distance = graph.getDistance(graph.getNode(current_node), graph.getNode(node.STRING));
				double idleness_weight, distance_weight;
				if(max_idleness == min_idleness)
					idleness_weight = 0;
				else
					idleness_weight = (idleness - min_idleness) / (max_idleness - min_idleness);
				if(max_distance == min_distance)
					distance_weight = 0;
				else
					distance_weight = (max_distance - distance) / (max_distance - min_distance);
				
				double node_value = (idleness_rate * idleness_weight) + ((1 - idleness_rate) * distance_weight);
				if(node_value > max_node_value){
					nodes_max.clear();
					nodes_max.add(node.STRING);
					max_node_value = node_value;
				}
				else if(Math.abs(node_value - max_node_value) < Math.pow(10, -8)){
					nodes_max.add(node.STRING);
				}
			}
		}
		
		int pos = (int) (Math.random() * nodes_max.size() );
		return nodes_max.get(pos);	
	}
	
	
	
	/****
	 * Return a list of nodes that are interesting to trade, or random nodes if no exchange has
	 * taken place for too long (more than MAX_CYCLES_WITHOUT_EXCHANGE)
	 * 
	 * 
	 * @return nodes as a LinkedList<String> of their ids
	 */
	protected LinkedList<String> ChooseNodesForAuction(){
		if(this.myNodes.size() <= nb_min_nodes)
			return new LinkedList<String>();
		double cost = 0, currentcost = 0;
		double idleness = 0, idle_max = 0, idle_min = 0;
		//double distance = 0, dist_max = 0, dist_min = 0;
		LinkedList<String> mynewnodes = new LinkedList<String>();
		double newpathcost = 0, path_gain = 0;
		
		String inserted = null;
		StringAndDouble current;
		//StringAndDouble temp;
		String answer = null;
		
		LinkedList<String> returnSet = new LinkedList<String>();
		
		if(this.cycles_without_exchange < FlexibleBidder2Agent.MAX_VISITS_WITHOUT_EXCHANGE){
			idle_max = this.calculateHighestIdleness();
			idle_min = this.calculateMinorIdleness();
			
			for(int i = 0; i < 2; i++){
				for(int j = 0; j < this.myNodes.size(); j++){
					current = this.myNodes.get(j);
					
					// we don't trade nodes already engaged in another trade
					if(this.isNodeEngaged(current.STRING))
						continue;
					
					if((inserted == null)||(!inserted.equals(current.STRING))){
						if((idle_max - idle_min) > 0)
							// version originale : plus l'oisiveté est haute, plus on garde le noeud
							// idleness = (idle_max - current.double_value) / (idle_max - idle_min);
							
							// version test : plus l'oisiveté est haute, plus on devrait se débarasser du noeud...
							idleness = (current.double_value - idle_min) / (idle_max - idle_min);
						else
							idleness = 0;
						
						mynewnodes.clear();
						for(String node : this.getMyNodes())
							if(!node.equals(current.STRING))
								mynewnodes.add(node);
						mynewnodes = this.OrderNodes(mynewnodes);
						newpathcost = this.CalculatePathCost(mynewnodes);
						
						path_gain = (this.visit_cost - newpathcost) / this.visit_cost;
						
						cost = (1 - this.idleness_rate_a)*path_gain + this.idleness_rate_a * idleness;
						if(cost > currentcost){
							answer = current.STRING;
							currentcost = cost;
						}
						cost = 0;
					}
				}
				
				if(answer != null){
					returnSet.add(answer);
					currentcost = 0;
					inserted = answer;
					answer = null;
				} 
			}
		} 
		// too much time without exchange --> random exchange
		else {
			//System.out.println(this.agent_id + " : Random exchange");
			for(int i = 0; i < 2; i++){
				int j = (int) Math.random() * this.myNodes.size();
				int k = 0;
				while(this.isNodeEngaged(this.getMyNodes(j)) && k < (2 * this.myNodes.size())){
					j = (int) Math.random() * this.myNodes.size();
					k++;
				}
				if(k != 2 * this.myNodes.size()){
					answer = this.getMyNodes(j);
					returnSet.add(answer);
				}
			}
		}
		
		return returnSet;
		
	}
	
	
	/****
	 *  Takes into account the received speechAct and calculates the appropriate answer :
	 *  	- A trade proposition for an INFORM act
	 *  	- nothing for a PROPOSE act (but the propositions are added to those who need to be tested
	 * 		- the exchange is realized in case of an ACCEPT act
	 * 
	 *  
	 * @param act the received SpeechAct
	 * @return the SpeechAct to send to the sender of the treated SpeechAct
	 */
	protected SpeechAct negociate(SpeechAct act){
		ComplexBid proposition, bid;
		SpeechAct answer;
		
		switch(act.getPerformative()){
		case INFORM :
			// check that there is a proposition
			proposition = this.emitProposition(act.getPropositions());
			// if there is something to propose
			if((proposition.getBidsForFirst() != null && proposition.getBidsForFirst().length > 0)
					|| (proposition.getBidsForSecond() != null && proposition.getBidsForSecond().length > 0)
					|| (proposition.getBidsForBoth() != null && proposition.getBidsForBoth().length > 0)
					|| (proposition.getDoubleBidsForBoth() != null && proposition.getDoubleBidsForBoth().size() > 0)){
				answer = new SpeechAct(act.getTransactionId(), SpeechActPerformative.PROPOSE, this.agent_id, act.getSender(), proposition);
				
				TransactionNodes mytrans = new TransactionNodes(act.getTransactionId(), act.getPropositions(), proposition);
				this.engaged_transactions.add(mytrans);
				this.nb_engaged_nodes = this.NbEngagedNodes();
			} else { //there is nothing to propose
				answer = new SpeechAct(act.getTransactionId(), SpeechActPerformative.REFUSE, this.agent_id, act.getSender(), act.getPropositions(), 1);
			}
			break;
			
		case REJECT :
			
			TransactionNodes mytrans = this.getEngagedTransacFromId(act.getTransactionId());
			if(mytrans != null){
				this.engaged_transactions.remove(mytrans);
				this.nb_engaged_nodes = this.NbEngagedNodes();
			}
			
			return null;
			
		case PROPOSE :
			// check the proposition
			if(FBABuyers == null)
				FBABuyers = new LinkedList<String>();
			FBABuyers.add(act.getSender());
			bid = act.getComplexBid();
			if(ComplexPropositions == null)
				ComplexPropositions = new LinkedList<ComplexBid>();
			ComplexPropositions.add(bid);
			return null;
			
		case ACCEPT :
			proposition = act.getComplexBid();
			// Since the exchange has been accepted, we swap the exchanged nodes
			// for those given by the other part
			
			if(proposition.getBidsForFirst().length > 0){
				LinkedList<String> NodeToDelete = new LinkedList<String>();
				for(int i = 0; i < proposition.getBidsForFirst().length; i++)
					NodeToDelete.add(proposition.getBidsForFirst()[i]);
				LinkedList<String> NodeToAdd = new LinkedList<String>();
				for(int i = 0; i < proposition.getBidsForSecond().length; i++)
					NodeToAdd.add(proposition.getBidsForSecond()[i]);
				
				for(int i = 0; i < NodeToDelete.size(); i++){
					this.removeFromMyNodes(NodeToDelete.get(i));
					this.exchanges++;		
				}
				
				for(int i = 0; i < NodeToAdd.size(); i += 2){
					this.addToMyNodes(NodeToAdd.get(i), Integer.valueOf(NodeToAdd.get(i+1)));
				}
				
				TransactionNodes mytrans2 = this.getEngagedTransacFromId(act.getTransactionId());
				if(mytrans2 != null){
					this.engaged_transactions.remove(mytrans2);
					this.nb_engaged_nodes = this.NbEngagedNodes();
				}
				
				String message = "Agent " + this.agent_id + " : exchanging ACCEPT #" + act.getTransactionId() + " ";
				for(String bla : NodeToDelete)
					message += bla + " and ";
				message = message.substring(0, message.length() - 4) + " for ";
				for(int i = 0; i < NodeToAdd.size(); i+=2)
					message += NodeToAdd.get(i) + "(" +  NodeToAdd.get(i+1) + ") and ";
				message = message.substring(0, message.length() - 4) + ".";
				System.out.println(message);
				
				
			}
			this.OrderMyNodes();
			this.visit_cost = this.CalculateMyPathCost();
			
//			String message = "Agent " + this.agent_id + " 's nodes : ";
//			for(String bla : this.getMyNodes())
//				message += bla + ", ";
//			message = message.substring(0, message.length() - 2) + ".";
//			System.out.println(message);
			System.out.println("Agent " + this.agent_id + ", new visit cost : " + this.visit_cost);
			
			return null;
			
		case REFUSE :
			return null;
			
		default :
			// failure in communication
			answer = new SpeechAct(act.getTransactionId(), SpeechActPerformative.NOT_UNDERSTOOD, this.agent_id, 
										act.getSender(), act.getContainedNodes(), 0);
			
		}
		
		return answer;
		
	}

	
	/**
	 * Create an appropriate ComplexBid answer for the given proposition by calculating 
	 * the most interesting nodes that can be given in exchange of those proposed
	 * 
	 * @param received_offers : a LinkedList<String> of the ids of the nodes proposed for exchange
	 * 
	 * @return a ComplexBid answering the propositions
	 */
    protected ComplexBid emitProposition(LinkedList<String> received_offers){
    	double currentcost = this.visit_cost;
    	String offer;
    	String current, current2;
    	
    	LinkedList<String> choice = new LinkedList<String>();
    	LinkedList<String> subset = null, subset1 = null, subset2 = null;
    	LinkedList<String> offers = received_offers;
    	
    	Vector<String[]> auxiliary = null;
    	
    	ComplexBid returnSet;
    	
    	double[] values;
    	
    	// possible exchange 1 for 1 node
    	for(int k = 0; k < offers.size(); k++){
    		offer = offers.get(k);
    		values = new double[this.myNodes.size()];
    		for(int i = 0; i < this.myNodes.size(); i++){
    			choice.clear();
    			current = this.getMyNodes(i);
    			
    			// we don't trade nodes already engaged in another trade
    			if(this.isNodeEngaged(current))
    				continue;
    			
    			for(int j = 0; j < this.myNodes.size(); j++){
    				if(i != j)
    					choice.add(this.getMyNodes(j));
    				else
    					choice.add(offer);
    			}
    			values[i] = this.CalculatePathCost(this.OrderNodes(choice));
    		}
    		
    		// order
    		LinkedList<String> nodes = new LinkedList<String>();
    		LinkedList<String> mynodes_copy = this.getMyNodes();
    		while(values.length > 0 && nodes.size() < this.nb_max_propositions){
    			double min = values[0];
    			int indice = 0;
    			for(int i = 0; i < values.length; i++){
    				if(values[i] < min){
    					min = values[i];
    					indice = i;
    				}
    			}
    			if(min >= currentcost){
    				values = new double[0];
    				break;
    			}
    			else {
    				if(values[indice] != 0){   // engaged nodes
    					nodes.add(mynodes_copy.get(indice));
    				}
    				mynodes_copy.remove(indice);
    				double[] mynewvalues = new double[values.length - 1];
    				int i = 0, j = 0;
    				while(i < mynewvalues.length){
    					if(j != indice){
    						mynewvalues[i] = values[j];
    						i++;
    					}
    					j++;		
    				}
    				values = mynewvalues;
    			}
    		}
    			
    		if(k == 0)
    			subset = nodes;
    		else
    			subset1 = nodes;
    		
    	}
    	
    	// possible exchanges 1 for 2 proposed 
    	if(offers.size() == 2){
    		values = new double[this.myNodes.size()];
			for(int i = 0; i < this.myNodes.size(); i++){
				choice.clear();
				current = this.getMyNodes(i);
				
				// we don't trade nodes already engaged in another trade
				if(this.isNodeEngaged(current))
					continue;
				
				for(int j = 0; j < this.myNodes.size(); j++){
					if(i != j)
						choice.add(this.getMyNodes(j));
					else {
						for(int k = 0; k < offers.size(); k++){
							// because offers is of size 2...
							choice.add(offers.get(k));
						}
					}
				}
				
				values[i] = this.CalculatePathCost(this.OrderNodes(choice));
    		}
    		
    		// order
    		LinkedList<String> nodes = new LinkedList<String>();
    		LinkedList<String> mynodes_copy = this.getMyNodes();
    		while(values.length > 0 && nodes.size() < this.nb_max_propositions){
    			double min = values[0];
    			int indice = 0;
    			for(int i = 0; i < values.length; i++){
    				if(values[i] < min){
    					min = values[i];
    					indice = i;
    				}
    			}
    			if(min >= currentcost){
    				values = new double[0];
    				break;
    			}
    			else {
    				if(values[indice] != 0){   // engaged nodes
    					nodes.add(mynodes_copy.get(indice));
    				}
    				mynodes_copy.remove(indice);
    				double[] mynewvalues = new double[values.length - 1];
    				int i = 0, j = 0;
    				while(i < mynewvalues.length){
    					if(j != indice){
    						mynewvalues[i] = values[j];
    						i++;
    					}
    					j++;		
    				}
    				values = mynewvalues;
    			}
    		}	
	
			subset2 = nodes;

		}
		
		// possible exchanges 2 for 2 or 2 for 1 proposed
    	if(this.myNodes.size() > this.nb_min_nodes && offers.size() == 2){
    		double[][] values2 = new double[this.myNodes.size()][this.myNodes.size()];
			for(int i = 0; i < this.myNodes.size(); i++){
				current = this.getMyNodes(i);
				
				// we don't trade nodes already engaged in another trade
				if(this.isNodeEngaged(current))
					continue;
				
				for(int l = i+1; l < this.myNodes.size(); l++){
					current2 = this.getMyNodes(l);
					
					// we don't trade nodes already engaged in another trade
	    			if(this.isNodeEngaged(current2))
	    				continue;
					
					choice.clear();
					for(int j = 0; j < this.myNodes.size(); j++){
						if((i != j) && (l != j))
							choice.add(this.getMyNodes(j));
					}
					for(int k = 0; k < offers.size(); k++){
						// because offers is of size 2...
						choice.add(offers.get(k));
					}
					
					values2[i][l] = this.CalculatePathCost(this.OrderNodes(choice));
				}
			}
			
			auxiliary = new Vector<String[]>();
			LinkedList<String> proposed_nodes = new LinkedList<String>();
			boolean[][] intoaccount = new boolean[values2.length][values2.length];
			for(int i = 0; i < values2.length; i++)
				for(int j = 0; j < values2.length; j++)
					intoaccount[i][j] = (values2[i][j] == 0);
			
			while(proposed_nodes.size() < this.nb_max_propositions){
				double min = Double.MAX_VALUE;
				int indice1 = 0, indice2 = 0;
				for(int i = 0; i < values2.length; i++)
					for(int j = 0; j < values2.length; j++)
						if(!intoaccount[i][j] && values2[i][j] < min){
							min = values2[i][j];
							indice1 = i;
							indice2 = j;
						}
				
				if(min >= currentcost)
					break;
				else {
					int id1 = (int) this.estimatedIdleness(this.getMyNodes(indice1));
					id1 = (id1 == -1)? 0 : id1;
					int id2 = (int) this.estimatedIdleness(this.getMyNodes(indice2));
					id2 = (id2 == -1)? 0 : id2;
					
					String[] subsubset = { this.getMyNodes(indice1), Integer.toString(id1), 
								this.getMyNodes(indice2), Integer.toString(id2)};
					auxiliary.add(subsubset);
					intoaccount[indice1][indice2] = true;
					
					if(proposed_nodes.indexOf(this.getMyNodes(indice1)) == -1)
						proposed_nodes.add(this.getMyNodes(indice1));
					if(proposed_nodes.indexOf(this.getMyNodes(indice2)) == -1)
						proposed_nodes.add(this.getMyNodes(indice2));
				}
			}
			if(auxiliary.size() == 0)
				auxiliary = null;
			
    	}
		
    	int id;
		String[] subset_list = new String[0];
		if(subset != null && (this.myNodes.size() - this.nb_engaged_nodes > this.nb_min_nodes)){
			subset_list = new String[2*subset.size()];
			for(int i = 0; i < subset.size(); i++){
				subset_list[2*i] = subset.get(i);
				id = (int) this.estimatedIdleness(subset.get(i));
				id = (id == -1)? 0 : id;
				subset_list[2*i+1] = Integer.toString(id);
			}
		}
		
		String[] subset1_list = new String[0];
		if(subset1 != null && (this.myNodes.size() - this.nb_engaged_nodes > this.nb_min_nodes)){
			subset1_list = new String[2*subset1.size()];
			for(int i = 0; i < subset1.size(); i++){
				subset1_list[2*i] = subset1.get(i);
				id = (int) this.estimatedIdleness(subset1.get(i));
				id = (id == -1)? 0 : id;
				subset1_list[2*i+1] = Integer.toString(id);
			}
		}
		
		String[] subset2_list = new String[0];
		if(subset2 != null && (this.myNodes.size() - this.nb_engaged_nodes > this.nb_min_nodes)){
			subset2_list = new String[2*subset2.size()];
			for(int i = 0; i < subset2.size(); i++){
				subset2_list[2*i] = subset2.get(i);
				id = (int) this.estimatedIdleness(subset2.get(i));
				id = (id == -1)? 0 : id;
				subset2_list[2*i+1] = Integer.toString(id);
			}
		}
		
		if(this.myNodes.size() - this.nb_engaged_nodes <= this.nb_min_nodes + 1)
			auxiliary = null;
		
		/*if(auxiliary != null)
			while(auxiliary.size() > 3)
				auxiliary.remove(3);
		*/
		
		returnSet = new ComplexBid(subset_list, subset1_list, subset2_list, auxiliary);
		return returnSet;
		
    }
	
	
    /**
     * Broadcasts the given speechAct
     * 
     * @param act : the SpeechAct to send
     * @throws IOException
     */
    protected void SendSpeechAct(SpeechAct act) throws IOException{
    	String act_str = act.toString();
    	
    	this.connection.send("<action type=\"3\" message=\"" + act_str + "\"/>");
			// if the simulation is a real time one, sends the message more 4
			// times
		if (this.connection instanceof UDPClientConnection)
			for (int j = 0; j < FlexibleBidder2Agent.NETWORK_QUALITY; j++) {

				// sends a message with the orientation
				this.connection.send("<action type=\"3\" message=\""+ act_str + "\"/>");
			}
			
    }
    
    
	/**
	 * chooses what to exchange and sends auction
	 */
    protected void SendNewProposition(){
    	bids = this.ChooseNodesForAuction();
    	
    	if(bids.size() == 0 || (this.myNodes.size() - this.nb_engaged_nodes) <= nb_min_nodes){
    		myCurrentTransaction = -2;
    		return;
    	}
    	
		best_proposition1 = bids.get(0);
		if(bids.size() > 1){
			if(bids.get(1).equals(bids.get(0)) || (this.myNodes.size() - this.nb_engaged_nodes) == nb_min_nodes + 1){
				bids.remove(1);
				best_proposition2 = null;
			}
			else
				best_proposition2 = bids.get(1);
		}
		this.nb_engaged_nodes = this.NbEngagedNodes();

    	
    	int my_trans_id = FlexibleBidder2Agent.transaction_id;
    	FlexibleBidder2Agent.transaction_id++;
    	
    	SpeechAct act = new SpeechAct(my_trans_id, SpeechActPerformative.INFORM, this.agent_id, "all_agents", bids, 1);
    	myCurrentTransaction = my_trans_id;
    	try {
    		SendSpeechAct(act);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
    }
    
    
    /**
     * Manages the messages received
     * 
     * @param perceptions
     * 					The received perceptions
     */
    protected void ManageAnswers(String[] perceptions){
		SpeechAct received_act, response_act;
		
		for (int i = 0; i < perceptions.length; i++) {
			if (perceptions[i].indexOf("<perception type=\"3\"") > -1) {
				String message = perceptions[i].substring(perceptions[i].indexOf("message=\"")+ 9, perceptions[i].lastIndexOf("\"/>"));
				received_act = SpeechAct.fromString(message);				
				if(received_act.getTransactionId() == this.myCurrentTransaction && received_act.getReceiver().equals(this.agent_id)){
					// the message is for me : it's an answer to my own auction
					this.negociate(received_act);
					received_answers++;
				}
				else if(received_act.getReceiver().equals(this.agent_id) || received_act.getReceiver().equals("all_agents")){
					received_act.setReceiver(this.agent_id);
					response_act = this.negociate(received_act);
					if(response_act != null)
						try {
							SendSpeechAct(response_act);
						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}	
				}
				
			}
		}
    }
    
    
    /**
     * Processes the received answers
     * Calculates which of the received answers is the most interesting
     */
    protected void ProcessAnswers(){
    	
    	LinkedList<String> choice = new LinkedList<String>();
    	boolean exchange_done = false;
    	
		SpeechAct reject = new SpeechAct(this.myCurrentTransaction, SpeechActPerformative.REJECT, this.agent_id, "", new LinkedList<String>(), 0);
		ComplexBid current_bid;
		String[] prop_for_1, prop_for_2, prop_for_both;
		Vector<String[]> double_prop;
		
		double actual_cost = 0;
		double smallest_cost = this.visit_cost;
    	
    	if(this.ComplexPropositions != null && this.ComplexPropositions.size()>0){
    		for(int i = 0; i < this.ComplexPropositions.size(); i++){
    			current_bid = this.ComplexPropositions.get(i);
    			prop_for_1 = current_bid.getBidsForFirst();
    			prop_for_2 = current_bid.getBidsForSecond();
    			prop_for_both = current_bid.getBidsForBoth();
    			double_prop = current_bid.getDoubleBidsForBoth();
    			
    			// test all those proposed for 1rst and choose the best
    			if(prop_for_1 != null && prop_for_1.length > 0){
    				for(int k = 0; k <  prop_for_1.length; k+=2){
    					for(int j = 0; j < this.myNodes.size(); j++){
    						if(this.getMyNodes(j).equals(best_proposition1))
    							// we change only the node we proposed with the one proposed in exchange
    							choice.add(prop_for_1[k]);
    						else
    							choice.add(this.getMyNodes(j));
    							
    					}
    							
    					// check if the cost decreases when exchanging my worst node
    					// with node offered
    					actual_cost = this.CalculatePathCost(this.OrderNodes(choice));
    					if(actual_cost < smallest_cost){
    						best_offer1 = prop_for_1[k];
    						best_offer1_idle = Integer.valueOf(prop_for_1[k+1]);
    						best_offer2 = null;
    						best_offer2_idle = 0;
    						special_offer = null;
    						special_offer_idle = 0;
    						
    						smallest_cost = actual_cost;
    						best_buyer = i;
    					}
    					
    					choice.clear();
    					
    				}
    			}
    			
        		// test all those proposed for 2nd and choose the best
    			if(prop_for_2 != null && prop_for_2.length > 0 && best_proposition2 != null){
    				for(int k = 0; k <  prop_for_2.length; k+=2){
    					for(int j = 0; j < this.myNodes.size(); j++){
    						if(this.getMyNodes(j).equals(best_proposition2))
    							// we change only the node we proposed with the one proposed in exchange
    							choice.add(prop_for_2[k]);
    						else
    							choice.add(this.getMyNodes(j));
    							
    					}
    							
    					// check if the cost decreases when exchanging my worst node
    					// with node offered
    					actual_cost = this.CalculatePathCost(this.OrderNodes(choice));
    					if(actual_cost < smallest_cost){
    						best_offer1 = null;
    						best_offer1_idle = 0;
    						best_offer2 = prop_for_2[k];
    						best_offer2_idle = Integer.valueOf(prop_for_2[k+1]);
    						special_offer = null;
    						special_offer_idle = 0;
    						
    						smallest_cost = actual_cost;
    						best_buyer = i;
    					}	
    					
    					choice.clear();
    				}
    			}

        		// test all those proposed for both and choose the best
    			if(best_proposition2 != null && prop_for_both != null && prop_for_both.length > 0){
    				for(int k = 0; k <  prop_for_both.length; k+=2){
    					for(int j = 0; j < this.myNodes.size(); j++){
    						if(!this.getMyNodes(j).equals(best_proposition1) && !this.getMyNodes(j).equals(best_proposition2))
    							choice.add(this.getMyNodes(j));
    					}
    					
    					choice.add(prop_for_both[k]);
    							
    					// check if the cost decreases when exchanging my worst node
    					// with node offered
    					actual_cost = this.CalculatePathCost(this.OrderNodes(choice));
    					if(actual_cost < smallest_cost){
    						best_offer1 = null;
    						best_offer1_idle = 0;
    						best_offer2 = null;
    						best_offer2_idle = 0;
    						special_offer = prop_for_both[k];
    						special_offer_idle = Integer.valueOf(prop_for_both[k+1]);
    						
    						smallest_cost = actual_cost;
    						best_buyer = i;
    					}	
    					
    					choice.clear();
    				}
    			}
    			
        		// test double exchanges (or 2 for 1)
    			if(double_prop != null && double_prop.size() > 0){
    				for(int k = 0; k <  double_prop.size(); k++){
    					String[] subprop = double_prop.get(k);
    					
    					for(int j = 0; j < this.myNodes.size(); j++){
    						if(!this.getMyNodes(j).equals(best_proposition1)  && (best_proposition2 == null || !this.getMyNodes(j).equals(best_proposition2)))
    							choice.add(this.getMyNodes(j));
    					}
    					
    					choice.add(subprop[0]);
    					choice.add(subprop[2]);
    							
    					// check if the cost decreases when exchanging my worst node
    					// with node offered
    					actual_cost = this.CalculatePathCost(this.OrderNodes(choice));
    					if(actual_cost < smallest_cost){
    						best_offer1 = subprop[0];
    						best_offer1_idle = Integer.valueOf(subprop[1]);
    						best_offer2 = subprop[2];
    						best_offer2_idle = Integer.valueOf(subprop[3]);
    						special_offer = null;
    						special_offer_idle = 0;
    						
    						smallest_cost = actual_cost;
    						best_buyer = i;
    					}	
    					
    					choice.clear();
    				}
    			}	
    		}
    	}
    	
    	
    	// process exchange values
    	SpeechAct act;
    	
    	if(FBABuyers != null && FBABuyers.size() > 0){
    		for(int i = 0; i < FBABuyers.size(); i++){
    			String buyer = FBABuyers.get(i);
				if(i != best_buyer){
					reject.setReceiver(buyer);
					try {
						this.SendSpeechAct(reject);
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
				else {
					if(best_offer1 != null && best_offer2 != null){
						String[] winner_bid = {best_offer1 , best_offer2};
						String[] bids_str = new String[2*bids.size()];
						for(int i1 = 0; i1 < bids.size(); i1++){
							bids_str[2*i1] = bids.get(i1);
							bids_str[2*i1+1] = Integer.toString((int) this.estimatedIdleness(bids.get(i1)));
						}
						ComplexBid troc = new ComplexBid(winner_bid, bids_str, null, null);		
						act = new SpeechAct(this.myCurrentTransaction, SpeechActPerformative.ACCEPT, this.agent_id, buyer, troc);
						try {
							this.SendSpeechAct(act);
						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						
						this.exchanges +=2 ;
						exchange_done = true;
						
						this.removeFromMyNodes(best_proposition1);
						this.removeFromMyNodes(best_proposition2);
						//LinkedList<String> old_positions = this.getMyNodes();
						this.addToMyNodes(best_offer1, best_offer1_idle);
						this.addToMyNodes(best_offer2, best_offer2_idle);
						this.OrderMyNodes();
						this.visit_cost = this.CalculateMyPathCost();
						
						System.out.println("Agent " + this.agent_id + " trans #" + reject.getTransactionId() + " : exchanging " + best_proposition1 + " and " + best_proposition2 +
								" for " + best_offer1 + "(" +  best_offer1_idle  + ") and " + best_offer2 + "(" + best_offer2_idle + ")");
						
						
						//this.restartAverageIdleness(old_positions);
						//this.mountPriorityQueue();
						//this.myPriorityQueueIndex = 0;
					}
					else if(best_offer1 != null){
						String[] winner_bid = {best_offer1};
						String[] bids_str = {bids.get(0), Integer.toString((int) this.estimatedIdleness(bids.get(0)))};
						ComplexBid troc = new ComplexBid(winner_bid, bids_str, null, null);
						act = new SpeechAct(this.myCurrentTransaction, SpeechActPerformative.ACCEPT, this.agent_id, buyer, troc);
						try {
							this.SendSpeechAct(act);
						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						
						this.exchanges++ ;
						exchange_done = true;
						
						this.removeFromMyNodes(best_proposition1);
						//LinkedList<String> old_positions = this.getMyNodes();
						this.addToMyNodes(best_offer1, best_offer1_idle);
						this.OrderMyNodes();
						this.visit_cost = this.CalculateMyPathCost();
						
						System.out.println("Agent " + this.agent_id + " trans #" + reject.getTransactionId() + " : exchanging first" + best_proposition1 +
								" for " + best_offer1  + "(" +  best_offer1_idle  + ")");
						
						//this.restartAverageIdleness(old_positions);
						//this.mountPriorityQueue();
						//this.myPriorityQueueIndex = 0;
					}
					else if(best_offer2 != null){
						String[] winner_bid = {best_offer2};
						String[] bids_str = {bids.get(1), Integer.toString((int) this.estimatedIdleness(bids.get(1)))};
						ComplexBid troc = new ComplexBid(winner_bid, bids_str, null, null);
						act = new SpeechAct(this.myCurrentTransaction, SpeechActPerformative.ACCEPT, this.agent_id, buyer, troc);
						try {
							this.SendSpeechAct(act);
						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						
						this.exchanges++ ;
						exchange_done = true;
						
						this.removeFromMyNodes(best_proposition2);
						//LinkedList<String> old_positions = this.getMyNodes();
						this.addToMyNodes(best_offer2, best_offer2_idle);
						this.OrderMyNodes();
						this.visit_cost = this.CalculateMyPathCost();
						
						System.out.println("Agent " + this.agent_id + " trans #" + reject.getTransactionId() +  " : exchanging second " + best_proposition2 +
								" for " + best_offer2  + "(" +  best_offer2_idle  + ")");
						
						//this.restartAverageIdleness(old_positions);
						//this.mountPriorityQueue();
						//this.myPriorityQueueIndex = 0;
					}
					else if(special_offer != null){
						String[] winner_bid = {special_offer};
						String[] bids_str = new String[2*bids.size()];
						for(int i1 = 0; i1 < bids.size(); i1++){
							bids_str[2*i1] = bids.get(i1);	
							bids_str[2*i1+1] = Integer.toString((int) this.estimatedIdleness(bids.get(i1)));
						}
						ComplexBid troc = new ComplexBid(winner_bid, bids_str, null, null);
						act = new SpeechAct(this.myCurrentTransaction, SpeechActPerformative.ACCEPT, this.agent_id, buyer, troc);
						try {
							this.SendSpeechAct(act);
						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						
						this.exchanges +=2 ;
						exchange_done = true;
						
						this.removeFromMyNodes(best_proposition1);
						this.removeFromMyNodes(best_proposition2);
						//LinkedList<String> old_positions = this.getMyNodes();
						this.addToMyNodes(special_offer, special_offer_idle);
						
						this.OrderMyNodes();
						this.visit_cost = this.CalculateMyPathCost();
						
						System.out.println("Agent " + this.agent_id + " trans #" + reject.getTransactionId() +  " : exchanging special " + best_proposition1 + " and " + best_proposition2 +
								" for " + special_offer  + "(" +  special_offer_idle  + ")");
						
						//this.restartAverageIdleness(old_positions);
						//this.mountPriorityQueue();
						//this.myPriorityQueueIndex = 0;
					}
					
				}
    		}
    		
    	}
    	
    	// update nb_cycles without exchange
    	if(!exchange_done)
    		this.cycles_without_exchange++;
    	else
    		this.cycles_without_exchange = 0;
    	
    	this.best_proposition1 = null;
		this.best_proposition2 = null;
		this.nb_engaged_nodes = this.NbEngagedNodes();
		
		
    	if(FBABuyers != null)
    		FBABuyers.clear();
    	
    	if(ComplexPropositions != null)
    		ComplexPropositions.clear();
    	
    	this.best_offer1 = null;
    	this.best_offer1_idle = 0;
    	this.best_offer2 = null;
    	this.best_offer2_idle = 0;
    	this.special_offer = null;
    	this.special_offer_idle = 0;
    	this.best_buyer = -1;
    	
    	
    	this.myCurrentTransaction = -1;
    	this.received_answers = 0;
    	
//		String message = "Agent " + this.agent_id + " 's nodes : ";
//		for(String bla : this.getMyNodes())
//			message += bla + ", ";
//		message = message.substring(0, message.length() - 2) + ".";
//		System.out.println(message);
    	System.out.println("Agent " + this.agent_id + ", new visit cost : " + this.visit_cost);
    	
    }
    
    
    protected boolean isNodeEngaged(String node){
    	
    	if((best_proposition1 != null && best_proposition1.equals(node)) 
    			|| (best_proposition2 != null && best_proposition2.equals(node)))
    		return true;
    	
    	for(TransactionNodes transac : engaged_transactions){
    		for(String node2 : transac.out_nodes){
    			boolean engaged = (node2.equals(node));
    			if(engaged)
    				return true;
    		}
    		
    	}
    	
    	return false;
    }

    
    protected TransactionNodes getEngagedTransacFromId(int id){
    	for(TransactionNodes transac : engaged_transactions){
    		if(transac.transaction_id == id)
    			return transac;
    	}
    	
    	return null;
    }
    
    
	protected int NbEngagedNodes(){
		LinkedList<String> engaged = new LinkedList<String>();
		if(best_proposition1 != null)
			engaged.add(best_proposition1);
    	if(best_proposition2 != null && engaged.indexOf(best_proposition2)==-1)
    		engaged.add(best_proposition2);
		
		for(TransactionNodes transac : engaged_transactions){
    		for(String node2 : transac.out_nodes){
    			if(engaged.indexOf(node2)==-1)
    				engaged.add(node2);
    		}
		}
		
		return engaged.size();
	}
	
    protected void active_run(){
		// starts its connection
		this.connection.start();

		// the current position of the agent
		StringAndDouble current_position = null;
		boolean pos_actualized = false;
		boolean graph_actualized = false;
		
		int position = 0;
		
		LinkedList<String> received_messages = new LinkedList<String>();
		
		// while the agent is supposed to work
		while (!this.stop_working) {
	    	received_messages.clear();
	    	pos_actualized = false;
			graph_actualized = false;
			String[] perceptions;
			
			if(this.myCurrentTransaction == -2)
				this.myCurrentTransaction = -1;
	    	
			/**
			 * perceive graph and position
			 */
			perceptions = this.connection.getBufferAndFlush();
			if(perceptions.length == 0)
				perceptions = new String[] {""};
			
			while (!pos_actualized || !graph_actualized || perceptions.length != 0) {

				for(int i = perceptions.length - 1; i > -1; i--){
				// tries to obtain the current position
					StringAndDouble current_current_position = this.perceiveCurrentPosition(perceptions[i]);
					if (current_current_position != null && 
							(current_position == null || 
									!current_current_position.STRING.equals(current_position.STRING)
									|| (this.myNodes.size() == 1))){
						current_position = current_current_position;
						pos_actualized = true;
						break;
					}
				}
				
				for(int i = perceptions.length - 1; i > -1; i--){
					// tries to obtain the current position
						Graph current_graph = null;
						try {
							if(!perceptions[i].equals(""))
								current_graph = this.perceiveGraph(perceptions[i]);
						} catch (Exception e) {
							// TODO Auto-generated catch block
							//e.printStackTrace();
						} 
						if (current_graph != null) {
							graph = current_graph;
							graph_actualized = true;
							break;
						}
					}
				
				for(int i = 0; i < perceptions.length; i++){
					if(perceptions[i].indexOf("<perception type=\"3\"") > -1)
						received_messages.add(perceptions[i]);
				}
				
				
				/**
				 * Auction time !
				 */
				
				if(!this.first_time){
					String[] rec_mess_str = new String[received_messages.size()];
					for(int i = 0; i < received_messages.size(); i++)
						rec_mess_str[i] = received_messages.get(i);
						
					this.ManageAnswers(rec_mess_str);
					received_messages.clear();
					
					if(received_answers == agents_num - 1  && this.myCurrentTransaction != -1)
						this.ProcessAnswers();
					
					
					if(this.myCurrentTransaction == -1 && agents_num > 1)
						this.SendNewProposition();
				}
				
				
				perceptions = this.connection.getBufferAndFlush();
			}
			
	    	if(this.first_time){
	    		this.OrderMyNodes();
				this.visit_cost = this.CalculateMyPathCost();
	    		this.first_time = false;
	    		//this.mountPriorityQueue();
	    	}
			
			// if on a node
	    	
		/*	*//**
			 * Auction time !
			 *//*
			
			String[] rec_mess_str = new String[received_messages.size()];
			for(int i = 0; i < received_messages.size(); i++)
				rec_mess_str[i] = received_messages.get(i);
				
			this.ManageAnswers(rec_mess_str);
			received_messages.clear();
			
			if(received_answers == agents_num - 1  && this.myCurrentTransaction != -1)
				this.ProcessAnswers();
			
			perceptions = this.connection.getBufferAndFlush();
			while(perceptions.length > 0){
				this.ManageAnswers(perceptions);
				
				if(received_answers == agents_num - 1  && this.myCurrentTransaction != -1)
					this.ProcessAnswers();
				
				perceptions = this.connection.getBufferAndFlush();	
			}
			
			
			if(received_answers == agents_num - 1 && this.myCurrentTransaction != -1)
				this.ProcessAnswers();
			
			
			if(this.myCurrentTransaction == -1)
				this.SendNewProposition();*/
			
			if(current_position.double_value == 0){
					
				/**
				 * update idlenesses
				 */
				this.updateAllIdleness(length_of_last_edge);
				
				
				/**
				 *  choose where to move next when on a node
				 */
				
				int position_in_plan = plan.indexOf(current_position.STRING);
				
				if(position_in_plan > -1)
					for(int i = 0; i <= position_in_plan; i++)
						plan.poll();
					
				if(plan.size() > 0){
					try {
						this.visitCurrentNode(current_position.STRING);
						this.GoTo(plan.get(0));
						Edge edge = this.graph.getEdge(this.graph.getNode(current_position.STRING), this.graph.getNode(plan.get(0)));
						length_of_last_edge = 1 + Math.ceil(edge.getLength());
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
				else {
					position = this.indexInMyNodes(current_position.STRING);
				
					if(position > -1){
					
						String next_node = this.NextHeuristicNode(current_position.STRING, this.idleness_rate_p);
						this.Pathfinding(current_position.STRING, next_node, graph);
						
						try {
							this.visitCurrentNode(current_position.STRING);
							if(plan.get(0).equals(current_position.STRING))
								length_of_last_edge = 1;
							else {
								this.GoTo(plan.get(0));
								Edge edge = this.graph.getEdge(this.graph.getNode(current_position.STRING), this.graph.getNode(plan.get(0)));
								length_of_last_edge = 1 + Math.ceil(edge.getLength());
							}
						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
					else {
						// the agent losts his way
						String next_node = this.NextHeuristicNode(current_position.STRING, this.idleness_rate_p);
						this.Pathfinding(current_position.STRING, next_node, graph);
						try {
							this.visitCurrentNode(current_position.STRING);
							if(plan.get(0).equals(current_position.STRING))
								length_of_last_edge = 1;
							else {
								this.GoTo(plan.get(0));
								Edge edge = this.graph.getEdge(this.graph.getNode(current_position.STRING), this.graph.getNode(plan.get(0)));
								length_of_last_edge = 1 + Math.ceil(edge.getLength());
							}
						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
				}
			}			
		}
		// stops the connection of the agent
		try {
			this.connection.stopWorking();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}


	
    
    
    /**
     * These functions are not used and are here to comply to the OpenAgent heritage
     */
    @Override
	protected void inactive_run() {
		// TODO Auto-generated method stub
		
	}


	@Override
	protected void activating_run() {
		// TODO Auto-generated method stub
		
	}


	@Override
	protected void deactivating_run() {
		// TODO Auto-generated method stub
		
	}
    	
    
    
}

