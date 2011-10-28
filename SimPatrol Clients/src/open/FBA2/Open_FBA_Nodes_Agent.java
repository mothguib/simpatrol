package open.FBA2;

import java.util.LinkedList;

import closed.FBA.ComplexBid;
import closed.FBA.SpeechAct;
import closed.FBA.SpeechActPerformative;

import util.StringAndDouble;


/**
 * Nodes Open_FBA_Agent
 * 
 * Enter :  - each agent proposes the nodes that, each, add the most path to the path it has to walk
 * QUIT  :  - each agent gives a list of priorities over the list of nodes of the quitting agent.
 * 				The preferred nodes are those adding the smallest distance to their path
 * 			the quitting agent tries at best to respect these priorities
 * 
 * @author pouletc
 *
 */
public class Open_FBA_Nodes_Agent extends Open_FBA_Agent {

	public Open_FBA_Nodes_Agent(String id, double entering_time, double quitting_time, 
										int number_of_agents, LinkedList<String> nodes,
										double idleness_rate_for_path, double idleness_rate_for_auction, 
										String society_id) {
		super(id, entering_time, quitting_time, number_of_agents, nodes, 
				idleness_rate_for_path, idleness_rate_for_auction, society_id);
	}

	public Open_FBA_Nodes_Agent(String id, int number_of_agents, LinkedList<String> nodes,
										double idleness_rate_for_path, double idleness_rate_for_auction) {
		super(id, number_of_agents, nodes, idleness_rate_for_path, idleness_rate_for_auction);
	}

	

	@Override
	protected SpeechAct enter_Message(int id) {
		return new SpeechAct(id, SpeechActPerformative.ENTER, this.agent_id, "all_agents");
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
		
		int nb_nodes_to_send = (int)((double)this.myNodes.size() * (1 - (double)this.agents_num / (double)(this.agents_num + 1)));
		if(nb_nodes_to_send == 0)
				nb_nodes_to_send = 1;
		
		LinkedList<StringAndDouble> costlist = new LinkedList<StringAndDouble>();
		
		StringAndDouble current;
		double pathcost, path_gain;
		LinkedList<String> pathlist = new LinkedList<String>();
		

		for(int j = 0; j < this.myNodes.size(); j++){
			current = this.myNodes.get(j);				
			pathlist = this.getMyNodes();
			pathlist.remove(current.STRING);
			pathcost = this.CalculatePathCost(this.OrderNodes(pathlist));
				
			path_gain = (this.visit_cost - pathcost);

			costlist.add(new StringAndDouble(current.STRING, path_gain));
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
		
		
		String[] nodes_to_send = new String[2*nb_nodes_to_send];
		for(int i = 0; i < nb_nodes_to_send; i++){
			nodes_to_send[2*i] = ordered_costlist.get(i).STRING;
			nodes_to_send[2*i + 1] = String.valueOf((int)this.estimatedIdleness(ordered_costlist.get(i).STRING));
			this.removeFromMyNodes(ordered_costlist.get(i).STRING);
		}
		
		ComplexBid bid = new ComplexBid(nodes_to_send, null, null, null);
		SpeechAct act = new SpeechAct(entering_act.getTransactionId(), SpeechActPerformative.PROPOSE, 
																	this.agent_id, entering_act.getSender(), bid);
		
		this.plan.clear();
		
		return act;
	}
	
	@Override
	protected void manage_Enter_Protocol() {
		if(this.ComplexPropositions != null && this.ComplexPropositions.size()>0){
			for(int i = 0; i < this.ComplexPropositions.size(); i++){
				String[] nodesToAdd = this.ComplexPropositions.get(i).getBidsForFirst();
				if(nodesToAdd != null && nodesToAdd.length != 0){
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
					
					System.out.println("Enter : from " + this.FBABuyers.get(i) + " : " + added_nodes);
				}
				else {
					SpeechAct act = new SpeechAct(this.myCurrentTransaction, SpeechActPerformative.ACCEPT, 
							this.agent_id, this.FBABuyers.get(i));
					this.SendSpeechAct(act);
				}
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
    		nodes.add(proposed_nodes[i]);
    		cost = this.CalculatePathCost(this.OrderNodes(nodes));
    		costlist.add(new StringAndDouble(proposed_nodes[i], cost - this.visit_cost));
    		nodes.remove(proposed_nodes[i]);
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
		
    	int[] nb_node_by_agent = new int[agents.length];
    	for(int i = 0; i < agents.length; i++){
    		agents[i] = this.FBABuyers.get(i);

    		String[] nodes = this.ComplexPropositions.get(i).getBidsForFirst();
    		nodes_list[i] = new StringAndDouble[nodes.length / 2];
    		for(int k = 0; k < nodes_list[i].length; k++)
    			nodes_list[i][k] = new StringAndDouble(nodes[2*k], Double.valueOf(nodes[2*k + 1]));
    	}
    	int index = 0;
    	for(int i = 0; i < this.myNodes.size(); i++){
    		nb_node_by_agent[index]++;
    		index++;
    		if(index >= agents.length)
    			index = 0;
    	}
    	
    	
    	int[] current_index = new int[agents.length];
    	String[][] distributed_nodes = new String[agents.length][this.myNodes.size()];
    	int[] current_distributed_index = new int[agents.length];
    	LinkedList<String> mynodes_copy = this.getMyNodes();
    	while(mynodes_copy.size() > 0){
    		index = -1;
    		double cost_min = Double.MAX_VALUE;
    		
    		for(int i = 0; i < nodes_list.length; i++){
	    		while((current_index[i] < nodes_list[i].length) && 
	    				(mynodes_copy.indexOf(nodes_list[i][current_index[i]].STRING) == -1 ))
	    			current_index[i]++;
	    				
	    		if((current_index[i] < nodes_list[i].length) && current_distributed_index[i] < nb_node_by_agent[i])
	    			if(nodes_list[i][current_index[i]].double_value < cost_min){
	    				cost_min = nodes_list[i][current_index[i]].double_value;
	    				index = i;
	    			}		
    		}
    		
    		distributed_nodes[index][current_distributed_index[index]] = nodes_list[index][current_index[index]].STRING;
    		mynodes_copy.remove(nodes_list[index][current_index[index]].STRING);
    		current_distributed_index[index]++;
    		current_index[index]++;
    		
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


	@Override
	protected boolean enterCondition() {
		return true;
	}

}
