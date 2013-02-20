package open.CooperativeAuctions;

import java.io.IOException;
import java.util.LinkedList;
import java.util.Vector;

import util.StringAndDouble;
import util.graph.Edge;
import util.graph.Graph;
import util.graph.Node;
import util.graph.ShortestPath.ProximityPath;
import util.graph.ShortestPath.ShortestPath;
import closed.FBA.ComplexBid;
import closed.FBA.SpeechAct;
import closed.FBA.SpeechActPerformative;
import closed.FBA.TransactionNodes.TransactionTypes;
import closed.FBA2.CommunicatorAgent2;
import closed.FBA2.TransactionNodes2;

public abstract class Cooperative_Agent extends CommunicatorAgent2 {

	/** variables linked to the other agents $
	 * 
	 * The classic cycle is to add an agent in the entering_agents when receiving an ENTER message,
	 * and transfer hit in the known_agents at the end of the ENTER transaction.
	 * An agent is transferred in the quitting_agents when receiving the QUIT message, and removed from every list
	 * when the QUIT transaction finishes
	 * 
	 * At the beginning of the simulation, agents are detected when participating to auctions and put in known_agents
	 **/
	// known active agents
	protected LinkedList<String> known_agents = new LinkedList<String>();
	// known quitting agents
	protected LinkedList<String> quitting_agents = new LinkedList<String>();
	// known entering agents
	protected LinkedList<String> entering_agents = new LinkedList<String>();
	// counts the current transitions. Agents are not allowed to trade during ENTER or QUIT event, and must REFUSE or REJECT every proposition 
	// while transition_counter!=0
	protected int transition_counter = 0;
	
	/**
	 * general variables
	 */
	
	//nb of agents in the system
	protected int agents_num;
	// nb min of nodes the agent must keep 
	protected int nb_min_nodes;
	// nb max of nodes the agent can propose in an auction (to prevent it from being unable to participate in multiple auctions at the same time)
	protected int nb_max_propositions;
	
	protected final static int NETWORK_QUALITY = 5;


	/**
	 * path and utility related variables
	 */
	protected double current_utility;
	protected ShortestPath path_calculator;
	
	
	// importance of idleness in calculating heuristic destination
	protected double idleness_rate_p; //path_idleness_rate; (not used here)
	
	/** 
	 * The plan of walking through the graph. 
	 * */
	protected LinkedList<String> plan;
	protected double length_of_last_edge = 0;
	 
	
	/**
	 * transaction related variables
	 */
	protected boolean first_time = true;
	
	// used to launch random auctions 
	protected int cycles_without_exchange = 0;
	protected final static int MAX_VISITS_WITHOUT_EXCHANGE = 10;
	
	// counter of transactions
	protected static int transaction_id = 0;
	
	// nodes that have been proposed in prior auctions that are not yet finished
	protected LinkedList<TransactionNodes2> engaged_transactions;
	protected int nb_engaged_nodes = 0;
	
	/** auctions of which the agent is the auctioneer  **/
	protected int myCurrentTransaction = -1;
	// Select node(s) to exchange. the agent can have only one node...
	
	protected LinkedList<String> bids;
	protected String best_proposition1 = null;
	protected String best_proposition2 = null;
 	
	/** received propositions  **/
		
	// list of agents interested in my auction, and their propositions
	protected LinkedList<String> FBABuyers;
	protected LinkedList<ComplexBid> ComplexPropositions;
	protected int received_answers = 0;
	
	
	
	
	
	
	/** variables linked to my own activation/deactivation mechanisms **/
	protected boolean activate_message_sent = false;
	protected boolean enter_message_sent = false;
	protected boolean quit_message_sent = false;
	
	/** variables linked to my transactions **/
	// agents active in the system when I send an INFORM message, and from which I expect an answer
	protected LinkedList<String> awaited_props = new LinkedList<String>();
	// answers received to my INFORM message
	protected LinkedList<String> received_props = new LinkedList<String>();	
	
	// used only during the ENTER phase, when I don't know which agents are likely to answer
	protected int nb_answer_awaited = -1;
	
	// period at which the agent consider that the transaction it's keeping track of is too old, and assumes
	// it has missed the closure of the transaction. At this time, the nodes associated are made available again
	protected int transaction_too_old = 500;
	
	
	protected LinkedList<Integer> cancelled_auctions = new LinkedList<Integer>();
	
	
	
	
	
	
	
	
	public Cooperative_Agent(String id, double entering_time, double quitting_time, 
			int number_of_agents, LinkedList<String> nodes,
			double idleness_rate_for_path, String society_id) {
		super(id, entering_time, quitting_time, society_id, nodes);
		
		plan = new LinkedList<String>();
		engaged_transactions = new LinkedList<TransactionNodes2>();
		
		agents_num = number_of_agents;
		idleness_rate_p = idleness_rate_for_path;
		//nb_min_nodes = (int) Math.min(2, Math.ceil((double)this.myNodes.size() / 2.0)) ;
		nb_min_nodes = 2;
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

		if(this.myNodes.size() == 0)
			this.nb_min_nodes = 2;

		received_messages = new LinkedList<String>();
		FBABuyers = new LinkedList<String>();
		ComplexPropositions = new LinkedList<ComplexBid>();
		
		path_calculator = new ProximityPath();

	}

	public Cooperative_Agent(String id, int number_of_agents, LinkedList<String> nodes,
			double idleness_rate_for_path) {
		super(id, nodes);
		
		plan = new LinkedList<String>();
		engaged_transactions = new LinkedList<TransactionNodes2>();
		
		agents_num = number_of_agents;
		idleness_rate_p = idleness_rate_for_path;
		//nb_min_nodes = (int) Math.min(2, Math.ceil((double)this.myNodes.size() / 2.0)) ;
		nb_min_nodes = 2;
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
		
		received_messages = new LinkedList<String>();
		FBABuyers = new LinkedList<String>();
		ComplexPropositions = new LinkedList<ComplexBid>();
		
		path_calculator = new ProximityPath();
	}
	
	
	
	
	
	
	/**
	 * Orders the nodes of the agent using the given ShortestPath
	 * 
	 * @param nodes : a LinkedList<String> of node Ids
	 * @return the ordered list of node labels
	 */
	protected void OrderMyNodes(){
		if(this.graph != null){
			LinkedList<String> myNodes_str = this.getMyNodes();
			myNodes_str = this.path_calculator.GetShortestPath(this.graph, myNodes_str);
			
			LinkedList<StringAndDouble> myNewNodes = new LinkedList<StringAndDouble>();
			for(int i = 0; i < myNodes_str.size(); i++){
				int j = this.indexInMyNodes(myNodes_str.get(i));
				myNewNodes.add(myNodes.get(j));	
			}
		
			this.myNodes = myNewNodes;
		}
	}
	
	
	
	
	/**
	 * Lets the agent visit the current node
	 * 
	 * @throws IOException
	 */
	protected void visitCurrentNode(String node_name) throws IOException {
		super.visitCurrentNode(node_name);
		this.setIdleness(node_name, 0);
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
		if(nodes_max.size() > 0)
			return nodes_max.get(pos);	
		else 
			return this.getMyNodes((int) (Math.random() * this.myNodes.size()));
	}

	
    /**
     * manage the movement and its planification
     */
    protected void PlanAndMove(){
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
			/**
			 * There is no plan, so we construct one
			 */
			int position = this.indexInMyNodes(current_position.STRING);
		
			// the agent knows the node it's on
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

    
    
    
    
    protected abstract double CalculateUtility(LinkedList<String> nodes);
    
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
		double currentutility = this.current_utility;
		LinkedList<String> mynewnodes = new LinkedList<String>();
		
		String inserted = null;
		StringAndDouble current;
		String answer = null;
		
		LinkedList<String> returnSet = new LinkedList<String>();
		
		if(this.cycles_without_exchange < Cooperative_Agent.MAX_VISITS_WITHOUT_EXCHANGE){		
			for(int i = 0; i < 2; i++){
				for(int j = 0; j < this.myNodes.size(); j++){
					current = this.myNodes.get(j);
					
					// we don't trade nodes already engaged in another trade
					if(this.isNodeEngaged(current.STRING))
						continue;
					
					if((inserted == null)||(!inserted.equals(current.STRING))){						
						mynewnodes.clear();
						for(String node : this.getMyNodes())
							if(!node.equals(current.STRING))
								mynewnodes.add(node);
						mynewnodes = this.path_calculator.GetShortestPath(this.graph, mynewnodes);
						double new_utility = this.CalculateUtility(mynewnodes);
						
						if(new_utility < currentutility){
							answer = current.STRING;
							currentutility = new_utility;
						}
					}
				}
				
				if(answer != null){
					returnSet.add(answer);
					currentutility = 0;
					inserted = answer;
					answer = null;
				} 
			}
		} 
		// too much time without exchange --> random exchange
		else {
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
	
	
	protected abstract LinkedList<String> GenerateAuctionLaunchMessage(LinkedList<String> proposed_nodes);
	
	
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

    	
    	int my_trans_id = this.getNewTransactionId();
    	
    	LinkedList<String> bids_completed = this.GenerateAuctionLaunchMessage(bids);
    	
    	SpeechAct act = new SpeechAct(my_trans_id, SpeechActPerformative.INFORM, this.agent_id, "all_agents", bids_completed, 1);
    	myCurrentTransaction = my_trans_id;
    	SendSpeechAct(act);
		
    }
    
	
	
	
	/**
	 * Create an appropriate ComplexBid answer for the given proposition by calculating 
	 * if there are interesting nodes that can be given in exchange of those proposed, or if it is interesting to unburden the other agent 
	 * by taking its node for free
	 * 
	 * @param received_offers : a LinkedList<String> of the ids of the nodes proposed for exchange
	 * 
	 * @return a ComplexBid answering the propositions
	 */
    protected abstract ComplexBid emitProposition(LinkedList<String> received_offers);
    
	
    
    
    protected abstract double getValueToSend(double new_utility);
    protected abstract LinkedList<String> getTransactionOutNodes(ComplexBid bid);
    
    
    /**
     * 
     * @param offers : length = 2 max !!
     * @return
     */
	protected ComplexBid emitExchangeProposition(String[] offers) {
	
		/**
		 * Check if there is an interesting exchange
		 */
		
		String offer, current, current2;
		double[] utilities;
		LinkedList<String> new_set = new LinkedList<String>();
		
		LinkedList<String> subset = null, subset1 = null, subset2 = null;
		Vector<String[]> auxiliary = null;
		
		// possible exchange 1 for 1 node
    	for(int k = 0; k < offers.length; k++){
    		offer = offers[k];
    		utilities = new double[this.myNodes.size()];
    		for(int i = 0; i < this.myNodes.size(); i++){
    			new_set.clear();
    			current = this.getMyNodes(i);
    			
    			// we don't trade nodes already engaged in another trade
    			if(this.isNodeEngaged(current))
    				continue;
    			
    			for(int j = 0; j < this.myNodes.size(); j++){
    				if(i != j)
    					new_set.add(this.getMyNodes(j));
    				else
    					new_set.add(offer);
    			}
    			utilities[i] = this.CalculateUtility(this.path_calculator.GetShortestPath(graph, new_set));
    		}
		
			// order
			LinkedList<String> nodes = new LinkedList<String>();
			LinkedList<String> mynodes_copy = this.getMyNodes();
			while(utilities.length > 0 && nodes.size() < this.nb_max_propositions){
				double min = utilities[0];
				int indice = 0;
				for(int i = 0; i < utilities.length; i++){
					if(utilities[i] < min){
						min = utilities[i];
						indice = i;
					}
				}
				if(min >= this.current_utility){
					utilities = new double[0];
					break;
				}
				else {
					if(utilities[indice] != 0){   // engaged nodes
						nodes.add(Double.toString(this.getValueToSend(utilities[indice])));
						nodes.add(mynodes_copy.get(indice));
						int id = (int) this.estimatedIdleness(mynodes_copy.get(indice));
						id = (id == -1)? 0 : id;
						nodes.add(Integer.toString(id));
					}
					mynodes_copy.remove(indice);
					double[] mynewvalues = new double[utilities.length - 1];
					int i = 0, j = 0;
					while(i < mynewvalues.length){
						if(j != indice){
							mynewvalues[i] = utilities[j];
							i++;
						}
						j++;		
					}
					utilities = mynewvalues;
				}
			}
				
			if(k == 0)
				subset = nodes;
			else
				subset1 = nodes;
		
    	}
		
    	// possible exchanges 1 for 2 proposed 
    	if(offers.length == 2){
    		utilities = new double[this.myNodes.size()];
			for(int i = 0; i < this.myNodes.size(); i++){
				new_set.clear();
				current = this.getMyNodes(i);
				
				// we don't trade nodes already engaged in another trade
				if(this.isNodeEngaged(current))
					continue;
				
				for(int j = 0; j < this.myNodes.size(); j++){
					if(i != j)
						new_set.add(this.getMyNodes(j));
					else {
						for(int k = 0; k < offers.length; k++){
							// because offers is of size 2...
							new_set.add(offers[k]);
						}
					}
				}
				
				utilities[i] = this.CalculateUtility(this.path_calculator.GetShortestPath(graph, new_set));
    		}
    		
    		// order
    		LinkedList<String> nodes = new LinkedList<String>();
    		LinkedList<String> mynodes_copy = this.getMyNodes();
    		while(utilities.length > 0 && nodes.size() < this.nb_max_propositions){
    			double min = utilities[0];
    			int indice = 0;
    			for(int i = 0; i < utilities.length; i++){
    				if(utilities[i] < min){
    					min = utilities[i];
    					indice = i;
    				}
    			}
    			if(min >= this.current_utility){
    				utilities = new double[0];
    				break;
    			}
    			else {
    				if(utilities[indice] != 0){   // engaged nodes
    					nodes.add(Double.toString(this.getValueToSend(utilities[indice])));
    					nodes.add(mynodes_copy.get(indice));
    					int id = (int) this.estimatedIdleness(mynodes_copy.get(indice));
						id = (id == -1)? 0 : id;
						nodes.add(Integer.toString(id));
    				}
    				mynodes_copy.remove(indice);
    				double[] mynewvalues = new double[utilities.length - 1];
    				int i = 0, j = 0;
    				while(i < mynewvalues.length){
    					if(j != indice){
    						mynewvalues[i] = utilities[j];
    						i++;
    					}
    					j++;		
    				}
    				utilities = mynewvalues;
    			}
    		}	
	
			subset2 = nodes;

		}
    	
    	// possible exchanges 2 for 2 or 2 for 1 proposed
    	if(this.myNodes.size() > this.nb_min_nodes && offers.length == 2){
    		double[][] utilities2 = new double[this.myNodes.size()][this.myNodes.size()];
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
					
	    			new_set.clear();
					for(int j = 0; j < this.myNodes.size(); j++){
						if((i != j) && (l != j))
							new_set.add(this.getMyNodes(j));
					}
					for(int k = 0; k < offers.length; k++){
						// because offers is of size 2...
						new_set.add(offers[k]);
					}
					
					utilities2[i][l] = this.CalculateUtility(this.path_calculator.GetShortestPath(graph, new_set));
				}
			}
			
			auxiliary = new Vector<String[]>();
			LinkedList<String> proposed_nodes = new LinkedList<String>();
			boolean[][] intoaccount = new boolean[utilities2.length][utilities2.length];
			for(int i = 0; i < utilities2.length; i++)
				for(int j = 0; j < utilities2.length; j++)
					intoaccount[i][j] = (utilities2[i][j] == 0);
			
			while(proposed_nodes.size() < this.nb_max_propositions){
				double min = Double.MAX_VALUE;
				int indice1 = 0, indice2 = 0;
				for(int i = 0; i < utilities2.length; i++)
					for(int j = 0; j < utilities2.length; j++)
						if(!intoaccount[i][j] && utilities2[i][j] < min){
							min = utilities2[i][j];
							indice1 = i;
							indice2 = j;
						}
				
				if(min >= this.current_utility)
					break;
				else {
					int id1 = (int) this.estimatedIdleness(this.getMyNodes(indice1));
					id1 = (id1 == -1)? 0 : id1;
					int id2 = (int) this.estimatedIdleness(this.getMyNodes(indice2));
					id2 = (id2 == -1)? 0 : id2;
					
					String[] subsubset = {Double.toString(this.getValueToSend(utilities2[indice1][indice2])), this.getMyNodes(indice1), Integer.toString(id1), 
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
    	
    	if(subset != null || subset1 != null || subset2 != null || auxiliary != null){
    		/**
    		 * There is an interesting exchange possible
    		 */
    		
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
    		
    		return new ComplexBid(subset_list, subset1_list, subset2_list, auxiliary);

    	}
    	
    	return new ComplexBid(null, null, null, null);
	}
    
    
    /****
	 *  Takes into account the received speechAct and calculates the appropriate answer :
	 *  	- A trade proposition for an INFORM act
	 *  	- nothing for a PROPOSE act (but the propositions are added to those who need to be tested
	 * 		- the exchange is realized in case of an ACCEPT act
	 * 
	 * 	It also takes into account the ENTER and QUIT messages, manages the status of known agents and keeps track of the nodes proposed
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
			// hack for cancelled auctions
			if(act.getComplexBid() != null &&
					act.getComplexBid().getBidsForFirst() != null && 
					act.getComplexBid().getBidsForFirst().length > 0 &&
					act.getComplexBid().getBidsForFirst()[0].equals("cancel")){
				this.cancelled_auctions.add(act.getTransactionId());
			}
			
			else {
				// an agent cannot participate in a new auction if it is entering or quitting the system
				if(this.engaging_in || this.engaging_out)
					return null;
				
				// keeping the status of the known agents
				if(!this.known_agents.contains(act.getSender())){
					if(!this.entering_agents.contains(act.getSender()) && !this.quitting_agents.contains(act.getSender())){
						this.known_agents.add(act.getSender());
						if(this.agents_num - 1 < this.known_agents.size())
							this.agents_num++;
					}
					else if(this.entering_agents.contains(act.getSender())){
						this.known_agents.add(act.getSender());
						this.entering_agents.remove(act.getSender());
						this.agents_num++;
						this.transition_counter = Math.max(0, this.transition_counter - 1);
					}
					else {
						this.known_agents.add(act.getSender());
						this.quitting_agents.remove(act.getSender());
						this.agents_num++;
						this.transition_counter = Math.max(0, this.transition_counter - 1);
					}
				}
				
				// if there is a transition, refuse
				if(this.transition_counter != 0){
					answer = new SpeechAct(act.getTransactionId(), SpeechActPerformative.REFUSE, this.agent_id, act.getSender(), act.getPropositions(), 1);
					return answer;
				}
				else if(!this.cancelled_auctions.contains(act.getTransactionId())){
					// check that there is a proposition
					proposition = this.emitProposition(act.getPropositions());
					// if there is something to propose
					if((proposition.getBidsForFirst() != null && proposition.getBidsForFirst().length > 0)
							|| (proposition.getBidsForSecond() != null && proposition.getBidsForSecond().length > 0)
							|| (proposition.getBidsForBoth() != null && proposition.getBidsForBoth().length > 0)
							|| (proposition.getDoubleBidsForBoth() != null && proposition.getDoubleBidsForBoth().size() > 0)){
						answer = new SpeechAct(act.getTransactionId(), SpeechActPerformative.PROPOSE, this.agent_id, act.getSender(), proposition);
						
						TransactionNodes2 mytrans = new TransactionNodes2(TransactionTypes.BID, act.getTransactionId(), (int)(this.time), act.getPropositions(), this.getTransactionOutNodes(proposition));
						this.engaged_transactions.add(mytrans);
						this.nb_engaged_nodes = this.NbEngagedNodes();
					} else { //there is nothing to propose
						answer = new SpeechAct(act.getTransactionId(), SpeechActPerformative.REFUSE, this.agent_id, act.getSender(), act.getPropositions(), 1);
					}
					break;
				}
			}
			
		case REJECT :
			
			TransactionNodes2 mytrans = this.getEngagedTransacFromId(act.getTransactionId());
			if(mytrans != null){
				// if nodes were engaged, free them for the next auctions 
				this.engaged_transactions.remove(mytrans);
				this.nb_engaged_nodes = this.NbEngagedNodes();
				
				// if it was an ENTER or QUIT transaction, change the status of the sender and count the transitions
				if(mytrans.Transaction_type == TransactionTypes.ENTER){
					this.entering_agents.remove(act.getSender());
					this.known_agents.add(act.getSender());
					this.transition_counter = Math.max(0, this.transition_counter - 1);
					this.agents_num++;
				}
				if(mytrans.Transaction_type == TransactionTypes.QUIT){
					this.quitting_agents.remove(act.getSender());
					if(this.awaited_props.contains(act.getSender()))
						this.awaited_props.remove(act.getSender());
					this.transition_counter = Math.max(0, this.transition_counter - 1);
					this.agents_num--;
				}

			}
			
			return null;
			
		case PROPOSE :
			// keeping the status of the known agents
			if(!this.known_agents.contains(act.getSender())){
				if(!this.entering_agents.contains(act.getSender()) && !this.quitting_agents.contains(act.getSender())){
					this.known_agents.add(act.getSender());
					if(this.agents_num - 1 < this.known_agents.size())
						this.agents_num++;
				}
				else if(this.entering_agents.contains(act.getSender())){
					this.known_agents.add(act.getSender());
					this.entering_agents.remove(act.getSender());
					this.agents_num++;
					this.transition_counter = Math.max(0, this.transition_counter - 1);
				}
				else {
					this.known_agents.add(act.getSender());
					this.quitting_agents.remove(act.getSender());
					this.agents_num++;
					this.transition_counter = Math.max(0, this.transition_counter - 1);
				}
			}
			// if there is a transition, refuse
			if(act.getTransactionId() != this.myCurrentTransaction){
				answer = new SpeechAct(this.myCurrentTransaction, SpeechActPerformative.REJECT, this.agent_id, act.getSender(), new LinkedList<String>(), 0);
				return answer;
			}
			// Add the proposition to the list of propositions to take into account
			if(FBABuyers == null)
				FBABuyers = new LinkedList<String>();
			FBABuyers.add(act.getSender());
			bid = act.getComplexBid();
			if(ComplexPropositions == null)
				ComplexPropositions = new LinkedList<ComplexBid>();
			if(bid != null)
				ComplexPropositions.add(bid);
			
			this.received_props.add(act.getSender());
			return null;
			
		case ACCEPT :
			TransactionNodes2 mytrans2 = this.getEngagedTransacFromId(act.getTransactionId());
			if(mytrans2 == null || mytrans2.Transaction_type == TransactionTypes.BID){
			// Since the exchange has been accepted, we swap the exchanged nodes
			// for those given by the other part
				proposition = act.getComplexBid();
				LinkedList<String> NodeToDelete = new LinkedList<String>();
				LinkedList<String> NodeToAdd = new LinkedList<String>();
				
				if(proposition.getBidsForFirst() != null && proposition.getBidsForFirst().length > 0)
					for(int i = 0; i < proposition.getBidsForFirst().length; i++)
						NodeToDelete.add(proposition.getBidsForFirst()[i]);
					
				if(proposition.getBidsForSecond() != null && proposition.getBidsForSecond().length > 0)
					for(int i = 0; i < proposition.getBidsForSecond().length; i++)
						NodeToAdd.add(proposition.getBidsForSecond()[i]);
					
				for(int i = 0; i < NodeToDelete.size(); i++){
					this.removeFromMyNodes(NodeToDelete.get(i));
					this.exchanges++;		
				}
					
				for(int i = 0; i < NodeToAdd.size(); i += 2){
					this.addToMyNodes(NodeToAdd.get(i), Integer.valueOf(NodeToAdd.get(i+1)));
				}
					
				this.engaged_transactions.remove(mytrans2);
				this.nb_engaged_nodes = this.NbEngagedNodes();
					
				String message = "Agent " + this.agent_id + " : exchanging ACCEPT #" + act.getTransactionId() + " ";
				for(String bla : NodeToDelete)
					message += bla + " and ";
				if(NodeToDelete.size() > 0)
					message = message.substring(0, message.length() - 4) + " for ";
				for(int i = 0; i < NodeToAdd.size(); i+=2)
					message += NodeToAdd.get(i) + "(" +  NodeToAdd.get(i+1) + ") and ";
				if(NodeToAdd.size()>0)
					message = message.substring(0, message.length() - 4) + ".";
				System.out.println(message);
			
			}
			else if(mytrans2.Transaction_type == TransactionTypes.QUIT){
				// we add the nodes sent by the quitting agent to our own list of nodes, and get the agent out of our list of agents
				proposition = act.getComplexBid();
				LinkedList<String> NodeToAdd = new LinkedList<String>();
					
				if(proposition.getBidsForSecond() != null && proposition.getBidsForSecond().length > 0)
					for(int i = 0; i < proposition.getBidsForSecond().length; i++)
						NodeToAdd.add(proposition.getBidsForSecond()[i]);
					
				for(int i = 0; i < NodeToAdd.size(); i += 2){
					this.addToMyNodes(NodeToAdd.get(i), Integer.valueOf(NodeToAdd.get(i+1)));
				}
					
				this.engaged_transactions.remove(mytrans2);
				this.nb_engaged_nodes = this.NbEngagedNodes();
				
				String message = "Agent " + this.agent_id + " : reorganising #" + act.getTransactionId() + " with : ";
				for(int i = 0; i < NodeToAdd.size(); i+=2)
					message += NodeToAdd.get(i) + "(" +  NodeToAdd.get(i+1) + ") and ";
				message = message.substring(0, message.length() - 4) + ".";
				System.out.println(message);
				
				this.quitting_agents.remove(act.getSender());
				if(this.awaited_props.contains(act.getSender()))
					this.awaited_props.remove(act.getSender());
				this.transition_counter = Math.max(0, this.transition_counter - 1);
				this.agents_num--;
			}
			else {
				//we remove the nodes sent to the entering agent, and change its status
				this.entering_agents.remove(act.getSender());
				this.known_agents.add(act.getSender());
				this.transition_counter = Math.max(0, this.transition_counter - 1);
				this.agents_num++;
				
				for(int i = 0; i < mytrans2.out_nodes.length; i++)
					this.removeFromMyNodes(mytrans2.out_nodes[i]);
				
				this.engaged_transactions.remove(mytrans2);
				this.nb_engaged_nodes = this.NbEngagedNodes();
				
			}
			// the list of watched nodes has changed, so we reorganize it
			if(this.myNodes.size() != 0){
				this.OrderMyNodes();
				this.current_utility = this.CalculateUtility(this.getMyNodes());
				System.out.println("Agent " + this.agent_id + ", new utility : " + this.current_utility);
			}
			return null;
			
		case REFUSE :
			// keeping the status of the known agents
			if(!this.known_agents.contains(act.getSender())){
				if(!this.entering_agents.contains(act.getSender()) && !this.quitting_agents.contains(act.getSender())){
					this.known_agents.add(act.getSender());
					if(this.agents_num - 1 < this.known_agents.size())
						this.agents_num++;
				}
				else if(this.entering_agents.contains(act.getSender())){
					this.known_agents.add(act.getSender());
					this.entering_agents.remove(act.getSender());
					this.agents_num++;
					this.transition_counter = Math.max(0, this.transition_counter - 1);
				}
				else {
					this.known_agents.add(act.getSender());
					this.quitting_agents.remove(act.getSender());
					this.agents_num++;
					this.transition_counter = Math.max(0, this.transition_counter - 1);
				}
			}
			// the proposition has been refused but we keep a trace of the answer
			if(act.getTransactionId() == this.myCurrentTransaction)
				this.received_props.add(act.getSender());
			return null;
			
		case ENTER :
			this.entering_agents.add(act.getSender());
			
			// an agent cannot participate in an ENTER mechanism if it is itself entering or quitting
			if(this.engaging_out || this.engaging_in)
				return null;
			
			this.transition_counter++;
			
			answer = manage_Enter_Message(act);
			mytrans = new TransactionNodes2(TransactionTypes.ENTER, act.getTransactionId(), (int)(this.time), act.getPropositions(), answer.getComplexBid());
			this.engaged_transactions.add(mytrans);
			
			break;
			
		case QUIT :
			this.quitting_agents.add(act.getSender());
			this.known_agents.remove(act.getSender());

			// an agent cannot participate in a QUIT mechanism if it is itself entering or quitting
			if(this.engaging_in || this.engaging_out)
				return null;
			
			// hack : waiting for the end of the current auction can cause deadlocks or delays, so we cancel the current auction
			if(this.myCurrentTransaction < 0)
				this.finish_current_auction();
			
			this.transition_counter++;

			answer = manage_Quit_Message(act);
			mytrans = new TransactionNodes2(TransactionTypes.QUIT, act.getTransactionId(), (int)(this.time), act.getPropositions(), answer.getComplexBid());
			this.engaged_transactions.add(mytrans);
			break;
			
		default :
			// failure in communication
			answer = new SpeechAct(act.getTransactionId(), SpeechActPerformative.NOT_UNDERSTOOD, this.agent_id, 
										act.getSender(), act.getContainedNodes(), 0);
			
		}
		
		return answer;
		
	}
    
    
    
	private void finish_current_auction() {
		ComplexBid cancel_bid = new ComplexBid(new String[]{"cancel"}, null, null, null);
		SpeechAct act = new SpeechAct(this.myCurrentTransaction, SpeechActPerformative.INFORM, this.agent_id, "", cancel_bid);
		for(String agent : this.awaited_props){
			if(!this.FBABuyers.contains(agent)){
				act.setReceiver(agent);
				this.SendSpeechAct(act);
			}
				
		}
		
		for(int i = this.FBABuyers.size() - 1; i > 0; i--)
			if(!this.known_agents.contains(this.FBABuyers.get(i))){
				SpeechAct reject = new SpeechAct(this.myCurrentTransaction, SpeechActPerformative.REJECT, this.agent_id, FBABuyers.get(i), new LinkedList<String>(), 0);
				this.SendSpeechAct(reject);
		    	
				this.FBABuyers.remove(i);
				this.ComplexPropositions.remove(i);
			}
		
		this.ProcessCorrectAnswers();
		
	}

	/**
     * Manages the messages received
     * It is the same function as the FlexibleBidderAgent2
     * 
     * @param perceptions
     * 					The received perceptions
	 * @throws IOException 
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
						SendSpeechAct(response_act);
				}
				
			}
		}
    }

    
	/**
     * Processes the received answers
     *
     * It reject every offers if there is a transition going at that time.
     * If there is no transition going on, it reject all propositions from agents not in the know_agents list (**), then call the super function
     * 
     * ** this can happen if an agent answered with a proposition, then had the time to 
     * complete the whole QUIT process BEFORE we process that proposition. It is important then to remove this proposition from eligible propositions
	 * @throws IOException 
     * 
     */
    protected void ProcessAnswers(){
    	if(this.transition_counter != 0){
    		SpeechAct reject = new SpeechAct(this.myCurrentTransaction, SpeechActPerformative.REJECT, this.agent_id, "", new LinkedList<String>(), 0);
    		if(this.FBABuyers != null)
	    		for(String buyer : this.FBABuyers){
	    			reject.setReceiver(buyer);
					this.SendSpeechAct(reject);
	    		}
    		
        	this.cycles_without_exchange++;  	
        	this.best_proposition1 = null;
    		this.best_proposition2 = null;
    		this.nb_engaged_nodes = this.NbEngagedNodes();
    		
        	if(FBABuyers != null)
        		FBABuyers.clear();
        	if(ComplexPropositions != null)
        		ComplexPropositions.clear();
        	
        	this.myCurrentTransaction = -1;
        	this.received_answers = 0;
    		
    	}
    	else {
    		for(int i = this.FBABuyers.size() - 1; i > 0; i--)
    			if(!this.known_agents.contains(this.FBABuyers.get(i))){
    				SpeechAct reject = new SpeechAct(this.myCurrentTransaction, SpeechActPerformative.REJECT, this.agent_id, FBABuyers.get(i), new LinkedList<String>(), 0);
    				this.SendSpeechAct(reject);
    		    	
    				this.FBABuyers.remove(i);
    				this.ComplexPropositions.remove(i);
    			}
    		
    		this.ProcessCorrectAnswers();
    	}
    }
 
    /**
     * Processes the correct received answers once checked that there is no transition going on
     * 
     */
    protected abstract void ProcessCorrectAnswers();
	
	
    /**
     * Sends the given speechAct. It makes a difference between a broadcast and a simple message
     * 
     * @param act : the SpeechAct to send
     */
    protected void SendSpeechAct(SpeechAct act){
    	String act_str = act.toString();
    	
    	if(act.getReceiver().equals("all_agents"))
    		this.BroadcastMessage(act_str);
    	else
    		this.SendMessage(act_str, act.getReceiver());		
    }
    
    

    /**
     * Allows to check if a node is currently engaged in an unfinished transaction 
     * 
     * @param node
     * @return
     */
    protected boolean isNodeEngaged(String node){
    	
    	if((best_proposition1 != null && best_proposition1.equals(node)) 
    			|| (best_proposition2 != null && best_proposition2.equals(node)))
    		return true;
    	
    	for(TransactionNodes2 transac : engaged_transactions){
    		for(String node2 : transac.out_nodes){
    			boolean engaged = (node2.equals(node));
    			if(engaged)
    				return true;
    		}
    		
    	}
    	
    	return false;
    }

    /**
     * Returns the TransactionNodes2 instance associated to the given transaction id
     * 
     * @param id : the transaction id
     * @return the corresponding TransactionNodes2
     */
    protected TransactionNodes2 getEngagedTransacFromId(int id){
    	for(TransactionNodes2 transac : engaged_transactions){
    		if(transac.transaction_id == id)
    			return transac;
    	}
    	
    	return null;
    }
    
    /**
     * returns the current nb of nodes engaged in unfinished transactions
     * 
     */
	protected int NbEngagedNodes(){
		LinkedList<String> engaged = new LinkedList<String>();
		if(best_proposition1 != null)
			engaged.add(best_proposition1);
    	if(best_proposition2 != null && engaged.indexOf(best_proposition2)==-1)
    		engaged.add(best_proposition2);
		
		for(TransactionNodes2 transac : engaged_transactions){
    		for(String node2 : transac.out_nodes){
    			if(engaged.indexOf(node2)==-1)
    				engaged.add(node2);
    		}
		}
		
		return engaged.size();
	}
    
    
	/**
	 * generates a new transaction id
	 * @return
	 */
	protected int getNewTransactionId(){
		int my_trans_id = Cooperative_Agent.transaction_id++;
    	return my_trans_id;
	}
	
	/**
	 * Checks if there are TransactionNodes that are too old. It means that the agent did not catch the end of a transaction and 
	 * did not free the nodes that were proposed.
	 * The nodes are made available to further transactions
	 */
    protected void checkTransactionExpiration(){
    	if(this.engaged_transactions != null)
	    	for(int i = this.engaged_transactions.size() - 1; i >= 0; i--)
	    		if(this.engaged_transactions.get(i).transaction_time < (this.time - this.transaction_too_old) || 
	    				this.cancelled_auctions.contains(this.engaged_transactions.get(i).transaction_id))
	    			this.engaged_transactions.remove(i);
    }
	
	
    /************************************************************************************
     ************************************************************************************
     * 																				    *
     * 			Management of the ENTER protocol             						    *
     *														 						    *
     * The process is :																	*
     * 		- I send an ENTER message with some kind of information						*
     *      - Others send PROPOSE messages with lists of nodes for me					*
     *      - I choose the nodes I want to watch and send ACCEPT/REJECT accordingly 	*
     * 																				    *
     ************************************************************************************
     ************************************************************************************/
    
    /**
     * returns true if the enter protocol can be launched
     */
    protected abstract boolean enterCondition();
    
    /**
     * Message to send to the other agents to signal the beginning of the enter protocol
     * It must use the SpeechActPerformative.ENTER
     */
    protected abstract SpeechAct enter_Message(int id);
    
    /**
     * manage the answer to an ENTER message
     * It must be a SpeechActPerformative.PROPOSE    (no REFUSE, or it wont be correctly processed. If needed, use an empty proposition)
     * and some of the agents have to propose nodes for the new agent
     * 
     * @param entering_act : the ENTER message
     * @return the message to send back
     */
    protected abstract SpeechAct manage_Enter_Message(SpeechAct entering_act);
    
    /**
     * manages the PROPOSE messages 
     * it must select the nodes to keep between the propositions, and send ACCEPT or REJECT messages accordingly
     */
    protected abstract void manage_Enter_Protocol();
    
    
    /************************************************************************************
     ************************************************************************************
     * 																				    *
     * 			Management of the QUIT protocol             						    *
     *														 						    *
     * The process is :																	*
     * 		- I send an QUIT message with (usually) information on the nodes I need to 	*
     * 			distribute																*
     *      - Others send PROPOSE messages to signal that they are available to receive	* 
     *      nodes, and for example some lists of nodes they want						*
     *      - I distribute the nodes to the other agents and send ACCEPT or REJECT 		*
     *     messages accordingly 														*
     * 																				    *
     * Be sure to check that every PROPOSE is still valid (see ProcessAnswers()) before *
     * distributing the nodes															*
     * The manage_Quit_Protocol function MUST put this.inactive to true					*
     * 																					*
     ************************************************************************************
     ************************************************************************************/
    
    /**
     * Message to send to the other agents to signal the beginning of the quit protocol
     * It must use the SpeechActPerformative.QUIT
     */
    protected abstract SpeechAct quit_Message(int id);
    
    /**
     * manage the answer to an QUIT message
     * It must be a SpeechActPerformative.PROPOSE    (no REFUSE, or it wont be correctly processed. If needed, use an empty proposition)
     * 
     * @param quitting_act : the QUIT message
     * @return the message to send back
     */
    protected abstract SpeechAct manage_Quit_Message(SpeechAct quitting_act); 
    
    /**
     * manages the PROPOSE messages 
     * it must distribute ALL the nodes to the agents, and send ACCEPT or REJECT messages accordingly
     * 
     * Be sure to check that every PROPOSE is still valid (see ProcessAnswers()) before distributing the nodes
     * The manage_Quit_Protocol function MUST put this.inactive to true	
     */
    protected abstract void manage_Quit_Protocol();
	
	
    protected void active_run(){
    	boolean pos_actualized = false;
    	boolean graph_actualized = false;
    	boolean time_actualized = false;
		String[] perceptions;
		
		if(this.myCurrentTransaction == -2)
			this.myCurrentTransaction = -1;
    	

		/*
		if(this.agents_num <= 4 && this.path_calculator instanceof HybridPath)
			this.path_calculator = new ProximityPath();
		if(this.agents_num > 4 && this.path_calculator instanceof ProximityPath)
			this.path_calculator = new HybridPath();
		*/
		
		
		/**
		 * perceive graph and position
		 */
		perceptions = this.connection.getBufferAndFlush();
		if(perceptions.length == 0)
			perceptions = new String[] {""};
		
		while (!pos_actualized || !graph_actualized || !time_actualized || perceptions.length != 0) {
			
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
			
			for(int i = perceptions.length - 1; i > -1; i--){
				time_actualized |= perceiveTime(perceptions[i]);
				if(time_actualized)
					break;
			}
			
			for(int i = 0; i < perceptions.length; i++){
				this.perceiveMessages(perceptions[i]);
			}
			
			
			/**
			 * Auction time !
			 */
			
			if(!this.first_time){
				// manage answers to my auction
				String[] rec_mess_str = new String[received_messages.size()];
				for(int i = 0; i < received_messages.size(); i++)
					rec_mess_str[i] = received_messages.get(i);
					
				this.ManageAnswers(rec_mess_str);
				received_messages.clear();
				
				// if I received all the answers I waited for, I proceed the answers
				boolean all_answers_received = true;
				if(this.nb_answer_awaited != -1)
					all_answers_received = (this.received_props.size() >= this.nb_answer_awaited);
				else 
					for(int i = 0; i < this.awaited_props.size(); i++)
						all_answers_received &= (this.received_props.contains(this.awaited_props.get(i)) 
								|| this.quitting_agents.contains(this.awaited_props.get(i)));
				
				if(all_answers_received  && this.myCurrentTransaction >= 0){
					this.ProcessAnswers();
					this.nb_answer_awaited = -1;
					
				}
				
				// if I have no auction of my own pending and there is no transition, I launch a new auction
				if((this.transition_counter == 0) && this.myCurrentTransaction == -1 && this.agents_num > 1){
					if(this.FBABuyers != null)
						this.FBABuyers.clear();
					if(this.ComplexPropositions != null)
						this.ComplexPropositions.clear();
					this.received_props.clear();
					this.SendNewProposition();
					if(this.known_agents.size() > 0){
						this.awaited_props.clear();
						for(int i = 0; i < this.known_agents.size(); i++)
							this.awaited_props.add(this.known_agents.get(i));
					}
					else 
						this.nb_answer_awaited = this.agents_num - 1;
				}
			}
			
			perceptions = this.connection.getBufferAndFlush();
		}
		
    	if(this.first_time){
    		this.OrderMyNodes();
			this.current_utility = this.CalculateUtility(this.getMyNodes());
    		this.first_time = false;
    	}

		
		if(current_position.double_value == 0){
			this.PlanAndMove();
		}	
		
		// this checks that no transaction closure was lost and that no node is blocked forever
		this.checkTransactionExpiration();
    }
    
    
    protected void deactivating_run(){
    	boolean pos_actualized = false;
    	boolean graph_actualized = false;
    	boolean time_actualized = false;
		String[] perceptions;
		
		if(this.myCurrentTransaction == -2)
			this.myCurrentTransaction = -1;
    	
		/**
		 * perceive graph and position
		 */
		perceptions = this.connection.getBufferAndFlush();
		if(perceptions.length == 0)
			perceptions = new String[] {""};
		
		while (!pos_actualized || !graph_actualized || !time_actualized || perceptions.length != 0) {

			for(int i = perceptions.length - 1; i > -1; i--){
			// tries to obtain the current position
				StringAndDouble current_current_position = this.perceiveCurrentPosition(perceptions[i]);
				if (current_current_position != null){
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
			
			for(int i = perceptions.length - 1; i > -1; i--){
				time_actualized |= perceiveTime(perceptions[i]);
				if(time_actualized)
					break;
			}
			
			for(int i = 0; i < perceptions.length; i++){
				this.perceiveMessages(perceptions[i]);
			}
			
			
			/**
			 * Quitting protocol !
			 */
			
			// I process the transactions I am engaged in until no transaction remains
			if(this.NbEngagedNodes() != 0){
				String[] rec_mess_str = new String[received_messages.size()];
				for(int i = 0; i < received_messages.size(); i++)
					rec_mess_str[i] = received_messages.get(i);
					
				this.ManageAnswers(rec_mess_str);
				received_messages.clear();
				
				
				boolean all_answers_received = true;
				for(int i = 0; i < this.awaited_props.size(); i++)
					all_answers_received &= (this.received_props.contains(this.awaited_props.get(i)) 
							|| this.quitting_agents.contains(this.awaited_props.get(i)));
				
				
				if(all_answers_received  && this.myCurrentTransaction >= 0)
					this.ProcessAnswers();
				
			}
			else {
				// then I launch the QUIT protocol
				if(!this.quit_message_sent){
					// I send the QUIT message
					int my_trans_id = this.getNewTransactionId();
			    	
			    	SpeechAct quit_act = this.quit_Message(my_trans_id);
			    	myCurrentTransaction = my_trans_id;
					this.SendSpeechAct(quit_act);
					this.awaited_props.clear();
					for(int i = 0; i < this.known_agents.size(); i++)
						this.awaited_props.add(this.known_agents.get(i));
					this.FBABuyers.clear();
					this.ComplexPropositions.clear();
					this.received_props.clear();
					this.quit_message_sent = true;
				}
				
				// I process the answers
				String[] rec_mess_str = new String[received_messages.size()];
				for(int i = 0; i < received_messages.size(); i++)
					rec_mess_str[i] = received_messages.get(i);
					
				this.ManageAnswers(rec_mess_str);
				received_messages.clear();
				
				// when all the answers have arrived, I finish the QUIT protocol
				boolean all_answers_received = true;
				for(int i = 0; i < this.awaited_props.size(); i++)
					all_answers_received &= (this.FBABuyers.contains(this.awaited_props.get(i)) 
							|| this.quitting_agents.contains(this.awaited_props.get(i)));
				
				
				if(all_answers_received && this.myCurrentTransaction != -1){
					this.manage_Quit_Protocol();
					this.known_agents.clear();
					this.entering_agents.clear();
					this.quitting_agents.clear();
					this.inactive = true;
					this.engaging_out = false;
				}
				
				if(this.inactive){
					this.Deactivate();
					this.quit_message_sent = false;
					return;
				}
			}
			
			
			perceptions = this.connection.getBufferAndFlush();
		}
		
		this.Wait();
		
		this.checkTransactionExpiration();
	}
    
    
    /**
     * used to know when to activate
     */
    protected void inactive_run(){
		boolean time_actualized = false;
		String[] perceptions;
    	
		/**
		 * perceive graph and position
		 */
		perceptions = this.connection.getBufferAndFlush();
		if(perceptions.length == 0)
			perceptions = new String[] {""};
		
		while (!time_actualized || perceptions.length != 0) {
			
			for(int i = perceptions.length - 1; i > -1; i--){
				time_actualized |= perceiveTime(perceptions[i]);
				if(time_actualized)
					break;
			}
			
			perceptions = this.connection.getBufferAndFlush();
		}
			
    }
    
    protected void activating_run(){
    	if(!this.activate_message_sent){
    		this.Activate(this.Society_id);
			this.activate_message_sent = true;
    	}
    	
    	boolean pos_actualized = false;
    	boolean graph_actualized = false;
    	boolean time_actualized = false;
		String[] perceptions;
		
		if(this.myCurrentTransaction == -2)
			this.myCurrentTransaction = -1;
    	
		/**
		 * perceive graph and position
		 */
		perceptions = this.connection.getBufferAndFlush();
		if(perceptions.length == 0)
			perceptions = new String[] {""};
		
		while (!pos_actualized || !graph_actualized || !time_actualized || perceptions.length != 0) {
			
			for(int i = perceptions.length - 1; i > -1; i--){
			// tries to obtain the current position
				StringAndDouble current_current_position = this.perceiveCurrentPosition(perceptions[i]);
				if (current_current_position != null){
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
			
			for(int i = perceptions.length - 1; i > -1; i--){
				time_actualized |= perceiveTime(perceptions[i]);
				if(time_actualized)
					break;
			}
			
			for(int i = 0; i < perceptions.length; i++){
				this.perceiveMessages(perceptions[i]);
			}
			
			
			/**
			 * Entering protocol !
			 */
			if(!this.enter_message_sent){
				// I send the ENTER message
				if(this.enterCondition()){
					int my_trans_id = this.getNewTransactionId();
		    	
					SpeechAct enter_act = this.enter_Message(my_trans_id);
			    	myCurrentTransaction = my_trans_id;
					this.SendSpeechAct(enter_act);
					this.nb_answer_awaited = this.agents_num - 1;
					this.received_props.clear();
					this.enter_message_sent = true;
		    	}
			}
			
			// I process the answers
			String[] rec_mess_str = new String[received_messages.size()];
			for(int i = 0; i < received_messages.size(); i++)
				rec_mess_str[i] = received_messages.get(i);
				
			this.ManageAnswers(rec_mess_str);
			received_messages.clear();

			// if the right number of answers have arrived, I finish the ENTER protocol
			if(received_answers >= (this.nb_answer_awaited - this.quitting_agents.size())  && this.myCurrentTransaction >= 0){
				this.manage_Enter_Protocol();	
				this.inactive = false;
				this.enter_message_sent = false;
				this.current_position = null;
				this.engaging_in = false;
				break;
			}
			
			perceptions = this.connection.getBufferAndFlush();
		}
		
		this.Wait();
		
    }

}
