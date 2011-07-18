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

public class FlexibleBidderAgent extends CommunicatorAgent {
	
	
	int cycles_without_exchange = 0;
	int agents_num;
	int nb_min_nodes;
	private final static int NETWORK_QUALITY = 5;
	private final static int MAX_VISITS_WITHOUT_EXCHANGE = 10;
	
	private static int transaction_id = 0;
	
	
	boolean first_time = true;
	
	double visit_cost;
	
	// importance of idleness in calculating heuristic destination
	double idleness_rate_p; //path_idleness_rate; (not used here)
	// importance of idleness in calculating nodes to trade
	private double idleness_rate_a; // distance_rate;
	
	
	/** The plan of walking through the graph. */
	private LinkedList<String> plan;

	private LinkedList<String> myPriorityQueue;
	private int myPriorityQueueIndex = 0;

	
	private LinkedList<String> FBABuyers;
	private LinkedList<ComplexBid> ComplexPropositions;
	private int myCurrentTransaction = -1;
	 
	private LinkedList<TransactionNodes> engaged_transactions;
	private int nb_engaged_nodes = 0; 
	 
	// Select node(s) to exchange. the agent can have only one node...
	private LinkedList<String> bids;
	private String best_proposition1 = null;
	private String best_proposition2 = null;
 	
 	//received propositions
	private String best_offer1 = null;
	private String best_offer2 = null;
	private String special_offer = null;
	private int best_buyer = -1;
	
	private int received_answers = 0;
	 
	
	public FlexibleBidderAgent(String id, int number_of_agents, LinkedList<String> nodes, double idleness_rate_for_path, double idleness_rate_for_auction) {
		super(id, nodes);
		
		plan = new LinkedList<String>();
		myPriorityQueue = new LinkedList<String>();
		engaged_transactions = new LinkedList<TransactionNodes>();
		
		agents_num = number_of_agents;
		idleness_rate_p = idleness_rate_for_path;
		idleness_rate_a = idleness_rate_for_auction;
		nb_min_nodes = (int) Math.min(2, Math.ceil((double)this.myNodes.size() / 2.0)) ;
		
		String message = "Agent " + this.agent_id + " starting with ";
		for(String bla : this.getMyNodes())
			message += bla + ", ";
		message = message.substring(0, message.length() - 2) + ".";
		System.out.println(message);
		
	}

	
	/**
	 * Lets the agent perceive its current position.
	 * 
	 * @param perceptions
	 *            The current perceptions of the agent.
	 * @return The current position of the agent, as a pair "current vertex id -
	 *         elapsed length on the current edge".
	 */
	private StringAndDouble perceiveCurrentPosition(String perception) {
		// tries to obtain the most recent self perception of the agent

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
	private Graph perceiveGraph(String perception) throws ParserConfigurationException, SAXException, IOException {
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
	private void visitCurrentNode(String node_name) throws IOException {
		String message = "<action type=\"2\"/>";
		this.connection.send(message);
		
		// this.setIdleness(node_name, 0);
		
		System.out.println("Agent " + this.agent_id + " visiting " + node_name);
		
	}
	
	
	/**
	 * Lets the agent go to the next node
	 * 
	 * @param next_vertex_id
	 *            The next node the agent is supposed to go to.
	 * @throws IOException
	 */
	private void GoTo(String next_vertex_id) throws IOException {
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
	private void Pathfinding(String position, String goal, Graph graph) {
		this.plan.clear();
		
		// obtains the vertex of the beginning
		Node begin_vertex = new Node("");
		begin_vertex.setObjectId(position);

		// obtains the goal vertex
		Node end_vertex = new Node("");
		end_vertex.setObjectId(goal);

		// obtains the dijkstra path
		Graph path = graph.getIdlenessedDijkstraPath(begin_vertex, end_vertex);

		// adds the ordered vertexes in the plan of the agent
		Node[] path_vertexes = path.getNodes();
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
	private String NextHeuristicNode(String current_node, double idleness_rate){
		if(myNodes.size() == 0)
			return null;
		if(myNodes.size() == 1)
			return this.getMyNodes(0);
		
		double max_idleness = this.calculeHighestIdleness();
		double min_idleness = this.calculeMinorIdleness();
		
		double max_distance = this.calculeHighestDistance(this.getMyNodes());
		double min_distance = this.calculeMinorDistance(this.getMyNodes());
		
		double max_node_value = Double.NEGATIVE_INFINITY;
		LinkedList<String> nodes_max = new LinkedList<String>();
		
		for(String node : this.getMyNodes()){
			if(!node.equals(current_node)){
				//double idleness = graph.getNode(node).getIdleness();
				double idleness = this.calculeAverageIdleness(node);
				double distance = graph.getDistance(graph.getNode(current_node), graph.getNode(node));
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
					nodes_max.add(node);
					max_node_value = node_value;
				}
				else if(Math.abs(node_value - max_node_value) < Math.pow(10, -8)){
					nodes_max.add(node);
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
	private LinkedList<String> ChooseNodesForAuction(){
		if(this.myNodes.size() <= nb_min_nodes)
			return new LinkedList<String>();
		double cost = 0, currentcost = 0;
		double idleness = 0, idle_aux = 0;
		
		LinkedList<String> mynewnodes = new LinkedList<String>();
		double newpathcost = 0, path_gain = 0;
		
		String inserted = null;
		String current;
		String answer = null;
		
		LinkedList<String> returnSet = new LinkedList<String>();
		
		if(this.cycles_without_exchange < FlexibleBidderAgent.MAX_VISITS_WITHOUT_EXCHANGE){
			idle_aux = this.calculeHighestIdleness() - this.calculeMinorIdleness();
	
			for(int i = 0; i < 2; i++){
				for(int j = 0; j < this.myNodes.size(); j++){
					current = this.getMyNodes(j);
					
					// we don't trade nodes already engaged in another trade
					if(this.isNodeEngaged(current))
						continue;
					
					if((inserted == null)||(!inserted.equals(current))){
						if(idle_aux > 0)
							idleness = ( this.calculeHighestIdleness() - this.calculeAverageIdleness(j) ) /idle_aux;
						else
							idleness = 0;
						
						mynewnodes.clear();
						for(String node : this.getMyNodes())
							if(!node.equals(current))
								mynewnodes.add(node);
						mynewnodes = this.OrderNodes(mynewnodes);
						newpathcost = this.CalculatePathCost(mynewnodes);
						
						path_gain = (this.visit_cost - newpathcost) / this.visit_cost;
						
						
						cost = (1 - this.idleness_rate_a)*path_gain + this.idleness_rate_a * idleness;
						if(cost > currentcost){
							answer = current;
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
			System.out.println(this.agent_id + " : Random exchange");
			this.cycles_without_exchange = 0;
			int j = this.myPriorityQueueIndex;
			while(this.isNodeEngaged(this.myPriorityQueue.get(j))){
				j++;
				if(j == this.myPriorityQueue.size()){
					j = 0;
				}
				if(j == this.myPriorityQueueIndex){
					j = -1;
					break;
				}
			}
			if(j != -1){
				answer = this.myPriorityQueue.get(j);
				returnSet.add(answer);
				this.myPriorityQueueIndex = (j == this.myPriorityQueue.size() - 1)? 0 : j+1;
				
				int k = this.myPriorityQueueIndex;
				while(this.isNodeEngaged(this.myPriorityQueue.get(k))){
					k++;
					if(k == this.myPriorityQueue.size()){
						k = 0;
					}
					if(k == this.myPriorityQueueIndex){
						k = -1;
						break;
					}
				}
				if(k != -1){
					answer = this.myPriorityQueue.get(k);
					returnSet.add(answer);
					this.myPriorityQueueIndex = (k == this.myPriorityQueue.size() - 1)? 0 : k+1;
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
	private SpeechAct negociate(SpeechAct act){
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
			// To reject, the act is sent with only the sender and the receiver
			
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
			LinkedList<String> old_positions = new LinkedList<String>();
			proposition = act.getComplexBid();
			// Since the exchange has been accepted, we swap the exchanged nodes
			// for those given by the other part
			answer = act;
			answer.setReceiver(answer.getSender());
			answer.setSender(this.agent_id);
			
			if(proposition.getBidsForFirst().length > 0){
				old_positions = this.getMyNodes();
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
				
				for(int i = 0; i < NodeToAdd.size(); i++){
					this.addToMyNodes(NodeToAdd.get(i));
					//this.setAvIdleness(NodeToAdd.get(i), Integer.valueOf(NodeToAdd.get(i+1)));
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
				for(int i = 0; i < NodeToAdd.size(); i++)
					message += NodeToAdd.get(i) + "and "; //+ "(" +  NodeToAdd.get(i+1) + ") and ";
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
			
			this.restartAverageIdleness(old_positions);
			this.mountPriorityQueue();
			this.myPriorityQueueIndex = 0;
			//break;
			
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
    private ComplexBid emitProposition(LinkedList<String> received_offers){
    	double cost = 0, currentcost = this.visit_cost;
    	String offer;
    	String current, current2, answer;
    	
    	LinkedList<String> choice = new LinkedList<String>();
    	LinkedList<String> subset = null, subset1 = null, subset2 = null;
    	LinkedList<String> offers = received_offers;
    	
    	Vector<String[]> auxiliary = null;
    	
    	ComplexBid returnSet;
    	
    	
    	// possible exchange 1 for 1 node
    	for(int k = 0; k < offers.size(); k++){
    		offer = offers.get(k);
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
    			cost = this.CalculatePathCost(this.OrderNodes(choice));
    			
    			if(cost < currentcost){
    				answer = current;
    				if(k == 0){
    					if(subset == null)
    						subset = new LinkedList<String>();
    					subset.add(answer);
    				} else {
    					if(subset1 == null)
    						subset1 = new LinkedList<String>();
    					subset1.add(answer);
    				}	
    			}
    			
    			cost = 0;
    		}
    	}
    	
    	// possible exchanges 1 for 2 proposed 
    	if(offers.size() == 2)
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
	    		cost = this.CalculatePathCost(this.OrderNodes(choice));
	    		
	
				if(cost < currentcost){
					answer = current;
					if(subset2 == null)
						subset2 = new LinkedList<String>();
					subset2.add(answer);
				}
				cost = 0;
			}
		
		// possible exchanges 2 for 2 or 2 for 1 proposed
    	if(this.myNodes.size() > this.nb_min_nodes || offers.size() == 2)
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
					cost = this.CalculatePathCost(this.OrderNodes(choice));
					
					if(cost < currentcost){
						if(auxiliary == null)
							auxiliary = new Vector<String[]>();
						String[] subsubset = { current, current2 };
						auxiliary.add(subsubset);
					}
					cost = 0;
				}
			}
		
		String[] subset_list = new String[0];
		if(subset != null && (this.myNodes.size() - this.nb_engaged_nodes > this.nb_min_nodes)){
			subset_list = new String[subset.size()];
			for(int i = 0; i < subset.size(); i++)
				subset_list[i] = subset.get(i);
		}
		
		String[] subset1_list = new String[0];
		if(subset1 != null && (this.myNodes.size() - this.nb_engaged_nodes > this.nb_min_nodes)){
			subset1_list = new String[subset1.size()];
			for(int i = 0; i < subset1.size(); i++)
				subset1_list[i] = subset1.get(i);
		}
		
		String[] subset2_list = new String[0];
		if(subset2 != null && (this.myNodes.size() - this.nb_engaged_nodes > this.nb_min_nodes)){
			subset2_list = new String[subset2.size()];
			for(int i = 0; i < subset2.size(); i++)
				subset2_list[i] = subset2.get(i);
		}
		
		if(this.myNodes.size() - this.nb_engaged_nodes <= this.nb_min_nodes + 1)
			auxiliary = null;
		
		if(auxiliary != null)
			while(auxiliary.size() > 3)
				auxiliary.remove(3);
		
		returnSet = new ComplexBid(subset_list, subset1_list, subset2_list, auxiliary);
		return returnSet;
		
    }
	
	
    /**
     * Broadcasts the given speechAct
     * 
     * @param act : the SpeechAct to send
     * @throws IOException
     */
    private void SendSpeechAct(SpeechAct act) throws IOException{
    	String act_str = act.toString();
    	
    	this.connection.send("<action type=\"3\" message=\"" + act_str + "\"/>");
			// if the simulation is a real time one, sends the message more 4
			// times
		if (this.connection instanceof UDPClientConnection)
			for (int j = 0; j < FlexibleBidderAgent.NETWORK_QUALITY; j++) {
				/*try {
					this.sleep(5000);
				} catch (InterruptedException ie) {
					// do nothing
				}*/

				// sends a message with the orientation
				this.connection.send("<action type=\"3\" message=\""+ act_str + "\"/>");
			}
			
    }
    
    
	/**
	 * chooses what to exchange and sends auction
	 */
    private void SendNewProposition(){
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
    	
    	int my_trans_id = FlexibleBidderAgent.transaction_id;
    	FlexibleBidderAgent.transaction_id++;
    	
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
    private void ManageAnswers(String[] perceptions){
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
    private void ProcessAnswers(){
    	
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
    			if(this.myCurrentTransaction == -2)
    				this.myCurrentTransaction = -1;
    			// test all those proposed for 1rst and choose the best
    			if(prop_for_1 != null && prop_for_1.length > 0){
    				for(int k = 0; k <  prop_for_1.length; k++){
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
    						best_offer2 = null;
    						special_offer = null;
    						
    						smallest_cost = actual_cost;
    						best_buyer = i;
    					}
    					
    					choice.clear();
    					
    				}
    			}
    			
        		// test all those proposed for 2nd and choose the best
    			if(prop_for_2 != null && prop_for_2.length > 0 && best_proposition2 != null){
    				for(int k = 0; k <  prop_for_2.length; k++){
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
    						best_offer2 = prop_for_2[k];
    						special_offer = null;
    						
    						smallest_cost = actual_cost;
    						best_buyer = i;
    					}	
    					
    					choice.clear();
    				}
    			}

        		// test all those proposed for both and choose the best
    			if(this.myNodes.size() > this.nb_min_nodes)
	    			if(prop_for_both != null && prop_for_both.length > 0){
	    				for(int k = 0; k <  prop_for_both.length; k++){
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
	    						best_offer2 = null;
	    						special_offer = prop_for_both[k];
	    						
	    						smallest_cost = actual_cost;
	    						best_buyer = i;
	    					}	
	    					
	    					choice.clear();
	    				}
	    			}
    			
        		// test double exchanges (or 2 for 1)
    			if(this.myNodes.size() > this.nb_min_nodes || best_proposition2 == null)
	    			if(double_prop != null && double_prop.size() > 0){
	    				for(int k = 0; k <  double_prop.size(); k++){
	    					String[] subprop = double_prop.get(k);
	    					
	    					for(int j = 0; j < this.myNodes.size(); j++){
	    						if(!this.getMyNodes(j).equals(best_proposition1)  && (best_proposition2 == null || !this.getMyNodes(j).equals(best_proposition2)))
	    							choice.add(this.getMyNodes(j));
	    					}
	    					
	    					choice.add(subprop[0]);
	    					choice.add(subprop[1]);
	    							
	    					// check if the cost decreases when exchanging my worst node
	    					// with node offered
	    					actual_cost = this.CalculatePathCost(this.OrderNodes(choice));
	    					if(actual_cost < smallest_cost){
	    						best_offer1 = subprop[0];
	    						best_offer2 = subprop[1];
	    						special_offer = null;
	    						
	    						smallest_cost = actual_cost;
	    						best_buyer = i;
	    					}	
	    					
	    					choice.clear();
	    				}
	    			}	
    		}
    	}
    	
    	if(this.myCurrentTransaction == -2)
			this.myCurrentTransaction = -1;
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
						String[] bids_str = new String[bids.size()];
						for(int i1 = 0; i1 < bids.size(); i1++)
							bids_str[i1] = bids.get(i1);
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
						LinkedList<String> old_positions = this.getMyNodes();
						this.addToMyNodes(best_offer1);
						this.addToMyNodes(best_offer2);
						this.OrderMyNodes();
						this.visit_cost = this.CalculateMyPathCost();
						
						System.out.println("Agent " + this.agent_id + " trans #" + reject.getTransactionId() + " : exchanging " + best_proposition1 + " and " + best_proposition2 +
								" for " + best_offer1 + " and " + best_offer2 );
						
						
						this.restartAverageIdleness(old_positions);
						this.mountPriorityQueue();
						this.myPriorityQueueIndex = 0;
					}
					else if(best_offer1 != null){
						String[] winner_bid = {best_offer1};
						String[] bids_str = {bids.get(0)};
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
						LinkedList<String> old_positions = this.getMyNodes();
						this.addToMyNodes(best_offer1);
						this.OrderMyNodes();
						this.visit_cost = this.CalculateMyPathCost();
						
						System.out.println("Agent " + this.agent_id + " trans #" + reject.getTransactionId() + " : exchanging first" + best_proposition1 +
								" for " + best_offer1 );
						
						this.restartAverageIdleness(old_positions);
						this.mountPriorityQueue();
						this.myPriorityQueueIndex = 0;
					}
					else if(best_offer2 != null){
						String[] winner_bid = {best_offer2};
						String[] bids_str = {bids.get(1)};
						ComplexBid troc = new ComplexBid(winner_bid, bids_str, null, null);
						act = new SpeechAct(this.myCurrentTransaction, SpeechActPerformative.ACCEPT, this.agent_id, buyer, troc);
						if(this.myCurrentTransaction == -2)
							this.myCurrentTransaction = -1;		try {
							this.SendSpeechAct(act);
						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						
						this.exchanges++ ;
						exchange_done = true;
						
						this.removeFromMyNodes(best_proposition2);
						LinkedList<String> old_positions = this.getMyNodes();
						this.addToMyNodes(best_offer2);
						this.OrderMyNodes();
						this.visit_cost = this.CalculateMyPathCost();
						
						System.out.println("Agent " + this.agent_id + " trans #" + reject.getTransactionId() +  " : exchanging second " + best_proposition2 +
								" for " + best_offer2 );
						
						this.restartAverageIdleness(old_positions);
						this.mountPriorityQueue();
						this.myPriorityQueueIndex = 0;
					}
					else if(special_offer != null){
						String[] winner_bid = {special_offer};
						String[] bids_str = new String[bids.size()];
						for(int i1 = 0; i1 < bids.size(); i1++)
							bids_str[i1] = bids.get(i1);
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
						LinkedList<String> old_positions = this.getMyNodes();
						this.addToMyNodes(special_offer);
						
						this.OrderMyNodes();
						this.visit_cost = this.CalculateMyPathCost();
						
						System.out.println("Agent " + this.agent_id + " trans #" + reject.getTransactionId() +  " : exchanging special " + best_proposition1 + " and " + best_proposition2 +
								" for " + special_offer );
						
						this.restartAverageIdleness(old_positions);
						this.mountPriorityQueue();
						this.myPriorityQueueIndex = 0;
					}
					
				}
    		}
    		
    	}
    	
    	// update nb_cycles without exchange
    	if(!exchange_done)
    		this.cycles_without_exchange++;
    	else
    		this.cycles_without_exchange = 0;
    	
    	if(this.best_proposition1 != null)
    		this.nb_engaged_nodes--;
    	if(this.best_proposition2 != null)
    		this.nb_engaged_nodes--;
    	this.best_proposition1 = null;
		this.best_proposition2 = null;
    	
    	if(FBABuyers != null)
    		FBABuyers.clear();
    	
    	if(ComplexPropositions != null)
    		ComplexPropositions.clear();
    	
    	this.best_proposition1 = null;
    	this.best_proposition2 = null;
    	this.best_offer1 = null;
    	this.best_offer2 = null;
    	this.special_offer = null;
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
    
    
    private boolean isNodeEngaged(String node){
    	
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

    
    private TransactionNodes getEngagedTransacFromId(int id){
    	for(TransactionNodes transac : engaged_transactions){
    		if(transac.transaction_id == id)
    			return transac;
    	}
    	
    	return null;
    }
    
	private int NbEngagedNodes(){
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
	
	
    public void run(){
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
	    		this.mountPriorityQueue();
	    	}
			
			if(current_position.double_value == 0){
					
				/**
				 * update idlenesses
				 */
				for(String node : this.getMyNodes())
					this.setIdleness(node, (int) graph.getNode(node).getIdleness());
				
				
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
							this.GoTo(plan.get(0));
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
							this.GoTo(plan.get(0));
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
	 * Mounts a priority queue
	 * The first node is the node that is the most interesting to remove
	 * (i.e. the one without it the remaining path is the smallest), and so on
	 * 
	 * TODO test for initialisation of actualcost
	 */
	private void mountPriorityQueue(){
		this.myPriorityQueue.clear();
		this.OrderMyNodes();
		this.visit_cost = this.CalculateMyPathCost();
		
		LinkedList<String> aux = this.getMyNodes();
		
		LinkedList<String> path = new LinkedList<String>();
		int indice = 0;
		String smallest = "";
		double actualcost;
		
		while(aux.size() > 2){
			indice = 0;
			smallest = aux.get(0);
			actualcost = visit_cost;
			
			// for all nodes in list
			// TODO : actual cost may have to be reset here...
			for(int i = 0; i < aux.size(); i++){
				// put all other nodes in path
				path.clear();
				for(int j = 0; j < aux.size(); j++){
					if(i!=j)
						path.add(aux.get(j));
				}
				// order path and calculate its cost
				double cost = this.CalculatePathCost(this.OrderNodes(path));
				// if cost inferior to actualcost
				if(cost <= actualcost){
					// actualize variables
					indice = i;
					smallest = aux.get(i);
					actualcost = cost;
				}
			}
			// remove smallest
			aux.remove(indice);
			// add smallest at the end of priority queue
			this.myPriorityQueue.add(smallest);	
		}
		
		// add end of node list to priority queue
		for(int i = 0; i < aux.size(); i++)
			this.myPriorityQueue.add(aux.get(i));
	}
	

}



