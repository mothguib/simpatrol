package open.FBA2;

import java.util.LinkedList;

import closed.FBA.ComplexBid;
import closed.FBA.SpeechAct;
import closed.FBA.SpeechActPerformative;

import util.StringAndDouble;
import util.graph.Node;


/**
 * Proximity Open_FBA_Agent
 * 
 * Enter :  - each agent proposes the group of nodes that add the most path to the path it has to walk.
 * 				Only the nodes that are at a distance smaller than @param max_dist of one of its own nodes are considered
 * 
 * QUIT  :  - each agent gives a list of priorities over groups of nodes of the quitting agent.
 * 				Only the nodes that are at a distance smaller than @param max_dist of one of its own nodes are considered
 * 				The preferred groups are those adding the smallest distance to their path
 * 			the quitting agent tries at best to respect these priorities, and distributes any remaining node by proximity to those
 * 			already given
 * 
 * 
 * @author pouletc
 *
 */
public class Open_FBA_Proximity_Agent extends Open_FBA_Agent {

	protected double MAX_DIST;
	
	
	public Open_FBA_Proximity_Agent(String id, double entering_time, double quitting_time, 
										int number_of_agents, LinkedList<String> nodes,
										double idleness_rate_for_path, double idleness_rate_for_auction, 
										double max_dist, String society_id) {
		super(id, entering_time, quitting_time, number_of_agents, nodes, 
				idleness_rate_for_path, idleness_rate_for_auction, society_id);
		
		MAX_DIST = max_dist;
	}

	public Open_FBA_Proximity_Agent(String id, int number_of_agents, LinkedList<String> nodes,
										double idleness_rate_for_path, double idleness_rate_for_auction,
										double max_dist) {
		super(id, number_of_agents, nodes, idleness_rate_for_path, idleness_rate_for_auction);
		
		MAX_DIST = max_dist;
	}

	

	@Override
	protected SpeechAct enter_Message(int id) {
		LinkedList<String> my_position = new LinkedList<String>();
		my_position.add(this.current_position.STRING);
		return new SpeechAct(id, SpeechActPerformative.ENTER, this.agent_id, "all_agents", my_position, 1);
	}

	@Override
	/**
	 * When receiving an ENTER message, the agent gives to the new agent some nodes
	 * the number of nodes given is chosen so that each agent in the system has the same number of nodes in average
	 * the nodes chosen are those who cost the most in the node list, according to the chosen heuristic
	 */
	protected SpeechAct manage_Enter_Message(SpeechAct entering_act) {
		
		if(this.myNodes.size() <= 2)
			return new SpeechAct(entering_act.getTransactionId(), SpeechActPerformative.PROPOSE, 
					this.agent_id, entering_act.getSender(), new ComplexBid(null, null, null, null));
		
		int maxnb_nodes_to_send = this.myNodes.size() / 2;
		
		String agent_position = entering_act.getPropositions().getFirst();
		
		LinkedList<StringAndDouble> costlist = new LinkedList<StringAndDouble>();
		
		StringAndDouble current;
		double pathcost, path_gain;
		LinkedList<String> pathlist = new LinkedList<String>();
		

		for(int j = 0; j < this.myNodes.size(); j++){
			current = this.myNodes.get(j);
			if(!this.isNodeEngaged(current.STRING))
				if(this.graph.getDistance(this.graph.getNode(agent_position), this.graph.getNode(current.STRING)) <= this.MAX_DIST){
					pathlist = this.getMyNodes();
					pathlist.remove(current.STRING);
					pathcost = this.CalculatePathCost(this.OrderNodes(pathlist));
					
					path_gain = (this.visit_cost - pathcost);
	
					costlist.add(new StringAndDouble(current.STRING, path_gain));
				}
		}
		
		LinkedList<StringAndDouble> ordered_costlist = new LinkedList<StringAndDouble>();
		while(costlist.size() > 0){
			double cost_max = - Double.MAX_VALUE;
			int index = -1;
			for(int j = 0; j < costlist.size(); j++)
				if(costlist.get(j).double_value > cost_max){
					cost_max = costlist.get(j).double_value;
					index = j;
				}
			
			if(index != -1){
				ordered_costlist.add(costlist.get(index));
				costlist.remove(index);
			}
		}
		
		int nb_nodes_to_send = Math.min(maxnb_nodes_to_send, ordered_costlist.size());
		String[] nodes_to_send = new String[2*nb_nodes_to_send];
		for(int i = 0; i < nb_nodes_to_send; i++){
			nodes_to_send[2*i] = ordered_costlist.get(i).STRING;
			nodes_to_send[2*i + 1] = String.valueOf((int)this.estimatedIdleness(ordered_costlist.get(i).STRING));
			//this.removeFromMyNodes(ordered_costlist.get(i).STRING);
		}
		
		ComplexBid bid = null;
		if(nodes_to_send.length != 0){
			bid = new ComplexBid(nodes_to_send, null, null, null);
		}
		else {
			double min_dist = Double.MAX_VALUE;
			int index = -1;
			for(int i = 0; i < this.myNodes.size(); i++){
				if(!this.isNodeEngaged(this.myNodes.get(i).STRING)){
					double dist = this.graph.getDistance(this.graph.getNode(agent_position), this.graph.getNode(this.myNodes.get(i).STRING));
					if( dist < min_dist){
						index = i;
						min_dist = dist;
					}
				}
			}
			
			if(index != -1){
				String[] nodes = new String[]{this.myNodes.get(index).STRING, String.valueOf((int)this.estimatedIdleness(this.myNodes.get(index).STRING))};
				bid = new ComplexBid(null, nodes, null, null);
			}
		}
		
		if(bid == null)
			bid = new ComplexBid(null, null, null, null);
		
		SpeechAct act = new SpeechAct(entering_act.getTransactionId(), SpeechActPerformative.PROPOSE, 
																	this.agent_id, entering_act.getSender(), bid);
		
		this.plan.clear();
		
		return act;
	}
	
	@Override
	protected void manage_Enter_Protocol() {
		if(this.ComplexPropositions != null && this.ComplexPropositions.size()>0){
			LinkedList<StringAndDouble> second_choices = new LinkedList<StringAndDouble>();
			LinkedList<String> second_choices_agents = new LinkedList<String>();
			
			for(int i = 0; i < this.ComplexPropositions.size(); i++){
				String[] nodesToAdd = this.ComplexPropositions.get(i).getBidsForFirst();
				if(nodesToAdd != null && nodesToAdd.length > 0){
					String added_nodes = "";
					for(int j = 0; j < nodesToAdd.length; j+=2){
						this.addToMyNodes(nodesToAdd[j], Double.valueOf(nodesToAdd[j+1]));
						added_nodes += nodesToAdd[j] + " (" + nodesToAdd[j+1] + "), ";
					}
					
					if(!added_nodes.equals(""))
						added_nodes = added_nodes.substring(0, added_nodes.length() - 2) + ";";
					
					SpeechAct act = new SpeechAct(this.myCurrentTransaction, SpeechActPerformative.ACCEPT, 
							this.agent_id, this.FBABuyers.get(i));
					this.SendSpeechAct(act);
					
					System.out.println("Enter " + this.agent_id + ": from " + this.FBABuyers.get(i) + " : " + added_nodes);
				}
				else if(this.ComplexPropositions.get(i).getBidsForSecond() != null){
					String[] nodes = this.ComplexPropositions.get(i).getBidsForSecond();
					second_choices.add(new StringAndDouble(nodes[0], Double.valueOf(nodes[1])));
					second_choices_agents.add(this.FBABuyers.get(i));
				}
				else {
					SpeechAct act = new SpeechAct(this.myCurrentTransaction, SpeechActPerformative.ACCEPT, 
							this.agent_id, this.FBABuyers.get(i));
					this.SendSpeechAct(act);
				}
			}
			
			while(this.myNodes.size() < 4 && second_choices_agents.size() > 0){
				double min_cost = Double.MAX_VALUE;
				int index = -1;
				for(int i = 0; i < second_choices.size(); i++){
					LinkedList<String> pathlist = this.getMyNodes();
					pathlist.add(second_choices.get(i).STRING);
					double pathcost = this.CalculatePathCost(this.OrderNodes(pathlist));
					if(pathcost < min_cost){
						min_cost = pathcost;
						index = i;
					}
				}
				if(index != -1){
					this.addToMyNodes(second_choices.get(index).STRING, Double.valueOf(second_choices.get(index).double_value));
					SpeechAct act = new SpeechAct(this.myCurrentTransaction, SpeechActPerformative.ACCEPT, 
							this.agent_id, second_choices_agents.get(index));
					this.SendSpeechAct(act);
					
					System.out.println("Enter " + this.agent_id + ": from " + second_choices_agents.get(index) + " : " + second_choices.get(index).STRING);
					
					second_choices.remove(index);
					second_choices_agents.remove(index);
				}	
			}
			
			for(int i = 0; i < second_choices_agents.size(); i++){
				SpeechAct act = new SpeechAct(this.myCurrentTransaction, SpeechActPerformative.REJECT, 
						this.agent_id, second_choices_agents.get(i));
				this.SendSpeechAct(act);
			}
    		
        	if(FBABuyers != null)
        		FBABuyers.clear();
        	if(ComplexPropositions != null)
        		ComplexPropositions.clear();

        	this.myCurrentTransaction = -1;
        	this.received_answers = 0;
			
		}
	}
	
	
	@Override
	protected SpeechAct quit_Message(int id) {
		String[] nodes = new String[this.myNodes.size()];
		for(int i = 0; i < this.myNodes.size(); i++)
			nodes[i] = this.getMyNodes(i);
		
		ComplexBid bid = new ComplexBid(nodes, null, null, null);
		SpeechAct act = new SpeechAct(id, SpeechActPerformative.QUIT, this.agent_id, "all_agents", bid);
		
		return act;
	}
	
	
	@Override
	/**
	 * when receiving a QUIT message with a list of nodes, the agent answers by giving its preferences 
	 * the node which is the closer (ie which minimizes the new path cost) is first in the preference order.
	 * it also sends for each node the associated cost
	 */
	protected SpeechAct manage_Quit_Message(SpeechAct quitting_act) {
		String[] proposed_nodes = quitting_act.getComplexBid().getBidsForFirst();
		
		double cost;
		LinkedList<StringAndDouble> costlist = new LinkedList<StringAndDouble>();
		
		
		LinkedList<String> nodes = new LinkedList<String>();
		for(int j = 0; j < this.myNodes.size(); j++)
			nodes.add(this.getMyNodes(j));
		
		for(int i = 0; i < proposed_nodes.length; i++){
			if(this.get_minDistance_to_nodes(proposed_nodes[i], nodes) < this.MAX_DIST){
				nodes.add(proposed_nodes[i]);
				cost = this.CalculatePathCost(this.OrderNodes(nodes));
				costlist.add(new StringAndDouble(proposed_nodes[i], cost - this.visit_cost));
				nodes.remove(proposed_nodes[i]);
			}
		}
		
		LinkedList<StringAndDouble> ordered_costlist = new LinkedList<StringAndDouble>();
		while(costlist.size() > 0){
			double cost_min = Double.MAX_VALUE;
			int index = -1;
			for(int j = 0; j < costlist.size(); j++)
				if(costlist.get(j).double_value < cost_min){
					cost_min = costlist.get(j).double_value;
					index = j;
				}
			
			if(index != -1){
				ordered_costlist.add(costlist.get(index));
				costlist.remove(index);
			}
		}
		
		String[] bid = new String[2 * ordered_costlist.size()];
		for(int k = 0; k < ordered_costlist.size(); k++){
			bid[2*k] = ordered_costlist.get(k).STRING;
			bid[2*k + 1] = String.valueOf(ordered_costlist.get(k).double_value);
		}
		
		ComplexBid mybid = new ComplexBid(bid, null, null, null);
		
		return new SpeechAct(quitting_act.getTransactionId(), SpeechActPerformative.PROPOSE, this.agent_id, 
									quitting_act.getSender(), mybid);
	}
	
	
	@Override
	/**
	 * the quitting agent has received a list of preferences on its nodes from the other agents.
	 * It distributes the nodes to them by giving a node to the agent whose order of preference on this node is the highest
	 */
	protected void manage_Quit_Protocol() {
		for(int i = this.FBABuyers.size() - 1; i > 0; i--)
    		if(!this.known_agents.contains(this.FBABuyers.get(i))){
    			SpeechAct reject = new SpeechAct(this.myCurrentTransaction, SpeechActPerformative.REJECT, this.agent_id, FBABuyers.get(i), new LinkedList<String>(), 0);
    			this.SendSpeechAct(reject);
    			this.FBABuyers.remove(i);
    			this.ComplexPropositions.remove(i);
    		}
		
		String[] agents = new String[this.FBABuyers.size()];
		StringAndDouble[][] nodes_list = new StringAndDouble[agents.length][];
		
    	int[] nb_classified_node_by_agent = new int[agents.length];
    	for(int i = 0; i < agents.length; i++){
    		agents[i] = this.FBABuyers.get(i);

    		String[] nodes = this.ComplexPropositions.get(i).getBidsForFirst();
    		nodes_list[i] = new StringAndDouble[nodes.length / 2];
    		for(int k = 0; k < nodes_list[i].length; k++)
    			nodes_list[i][k] = new StringAndDouble(nodes[2*k], Double.valueOf(nodes[2*k + 1]));

    		nb_classified_node_by_agent[i] = nodes.length / 2;

    	}  	
    	
    	int[] current_index = new int[agents.length];
    	String[][] distributed_nodes = new String[agents.length][this.myNodes.size()];
    	int[] current_distributed_index = new int[agents.length];
    	LinkedList<String> mynodes_copy = this.getMyNodes();
    	boolean done = false;
    	// on attribue selon les préférences
    	while(!done){
    		int index = -1;
    		double cost_min = Double.MAX_VALUE;
    		
    		for(int i = 0; i < nodes_list.length; i++){
    			if(nodes_list[i] != null){
		    		while((current_index[i] < nodes_list[i].length) && 
		    				(mynodes_copy.indexOf(nodes_list[i][current_index[i]].STRING) == -1 ))
		    			current_index[i]++;
		    				
		    		if(current_index[i] < nodes_list[i].length)
		    			if(nodes_list[i][current_index[i]].double_value < cost_min){
		    				cost_min = nodes_list[i][current_index[i]].double_value;
		    				index = i;
		    			}		
    			}
    		}
    		
    		if(index != -1){
	    		distributed_nodes[index][current_distributed_index[index]] = nodes_list[index][current_index[index]].STRING;
	    		mynodes_copy.remove(nodes_list[index][current_index[index]].STRING);
	    		current_distributed_index[index]++;
	    		current_index[index]++;
    		}
    		
    		
    		if(mynodes_copy.size() == 0)
    			break;
    		done = true;
    		for(int i = 0; i < agents.length; i++)
    			done &= (current_index[i] >= nb_classified_node_by_agent[i]);
    	}
    	
    	// les préférences étant incomplètes, on attribue les noeuds qui restent par proximité
    	while(mynodes_copy.size() > 0){
    		String current = mynodes_copy.getFirst();
    		Double min_dist = Double.MAX_VALUE;
    		LinkedList<Integer> index = new LinkedList<Integer>();
    		
    		for(int i = 0; i < agents.length; i++){
    			double dist = this.get_minDistance_to_nodes(current, distributed_nodes[i], current_distributed_index[i]);
    			if(Math.abs(dist - min_dist) < 0.001){
    				index.add(i);
    			}
    			else if(dist < min_dist){
    				min_dist = dist;
    				index.clear();
    				index.add(i);
    			}
    		}
    		
    		int winning_index = index.get((int)(Math.random() * index.size()));
    		distributed_nodes[winning_index][current_distributed_index[winning_index]] = current;
    		mynodes_copy.remove(current);
    		current_distributed_index[winning_index]++;
    	}
    	
    	
    	for(int j = 0; j < agents.length; j++){
    		String[] nodes_to_send = new String[2 * current_distributed_index[j]];
    		for(int k = 0; k < current_distributed_index[j]; k++){
    			nodes_to_send[2*k] = distributed_nodes[j][k];
    			nodes_to_send[2*k+1] = String.valueOf((int)this.estimatedIdleness(distributed_nodes[j][k]));
    			
    			this.removeFromMyNodes(distributed_nodes[j][k]);
    		}
    		
    		ComplexBid bid = new ComplexBid(null, nodes_to_send, null, null);
    		SpeechAct act = new SpeechAct(myCurrentTransaction, SpeechActPerformative.ACCEPT, this.agent_id, agents[j], bid);
			this.SendSpeechAct(act);
    	}
    	
    	this.inactive = true;

	}


	
	protected double get_minDistance_to_nodes(String target, LinkedList<String> nodes){
		double min_dist = Double.MAX_VALUE;
		Node target_node = this.graph.getNode(target);
		
		for(int i = 0; i < nodes.size(); i++){
			double dist = this.graph.getDistance(target_node, this.graph.getNode(nodes.get(i)));
			if(dist < min_dist)
				min_dist = dist;
		}
		
		return min_dist;
	}
	
	protected double get_minDistance_to_nodes(String target, String[] nodes, int last_index){
		double min_dist = Double.MAX_VALUE;
		Node target_node = this.graph.getNode(target);
		
		for(int i = 0; i < last_index; i++){
			double dist = this.graph.getDistance(target_node, this.graph.getNode(nodes[i]));
			if(dist < min_dist)
				min_dist = dist;
		}
		
		return min_dist;
	}

	@Override
	protected boolean enterCondition() {
		if(this.current_position == null)
			return false;
		return true;
	}

}
