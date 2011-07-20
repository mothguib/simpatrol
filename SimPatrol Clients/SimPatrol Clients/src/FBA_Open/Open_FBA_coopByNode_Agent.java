package FBA_Open;

import java.util.LinkedList;

import util.StringAndDouble;

import FBA.ComplexBid;
import FBA.SpeechAct;
import FBA.SpeechActPerformative;

public class Open_FBA_coopByNode_Agent extends Open_FBA_Agent {

	public Open_FBA_coopByNode_Agent(String id, double entering_time, double quitting_time, 
										int number_of_agents, LinkedList<String> nodes,
										double idleness_rate_for_path, double idleness_rate_for_auction, 
										String society_id) {
		super(id, entering_time, quitting_time, number_of_agents, nodes, 
				idleness_rate_for_path, idleness_rate_for_auction, society_id);
	}

	public Open_FBA_coopByNode_Agent(String id, int number_of_agents, LinkedList<String> nodes,
										double idleness_rate_for_path, double idleness_rate_for_auction) {
		super(id, number_of_agents, nodes, idleness_rate_for_path, idleness_rate_for_auction);
	}

	

	@Override
	protected SpeechAct enter_Message(int id) {
		return new SpeechAct(id, SpeechActPerformative.ENTER, this.agent_id, "all_agents");
	}

	@Override
	protected SpeechAct manage_Enter_Message(SpeechAct entering_act) {
		int nb_nodes_to_send = (int)((double)this.myNodes.size() * (1 - (double)this.agents_num / (double)(this.agents_num + 1)));
		if(nb_nodes_to_send == 0 && this.myNodes.size() > 1)
			if(Math.random() > ((double)this.agents_num / (double)(this.agents_num + 1)))
				nb_nodes_to_send = 1;
		
		LinkedList<StringAndDouble> costlist = new LinkedList<StringAndDouble>();
		
		StringAndDouble current;
		double idle_max = this.calculateHighestIdleness();
		double idle_min = this.calculateMinorIdleness();
		double idleness = 0, cost, pathcost, path_gain;
		LinkedList<String> pathlist = new LinkedList<String>();
		

		for(int j = 0; j < this.myNodes.size(); j++){
			current = this.myNodes.get(j);
			
			if((idle_max - idle_min) > 0)
					// version originale : plus l'oisiveté est haute, plus on garde le noeud
					// idleness = (idle_max - current.double_value) / (idle_max - idle_min);
					
					// version test : plus l'oisiveté est haute, plus on devrait se débarasser du noeud...
				idleness = (current.double_value - idle_min) / (idle_max - idle_min);
			else
				idleness = 0;
				
			pathlist.clear();
			for(String node : this.getMyNodes())
				if(!node.equals(current.STRING))
					pathlist.add(node);
			pathlist = this.OrderNodes(pathlist);
			pathcost = this.CalculatePathCost(pathlist);
				
			path_gain = (this.visit_cost - pathcost) / this.visit_cost;
			cost = (1 - this.idleness_rate_a)*path_gain + this.idleness_rate_a * idleness;

			costlist.add(new StringAndDouble(current.STRING, cost));
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
    		costlist.add(new StringAndDouble(proposed_nodes[i], cost));
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
	protected void manage_Quit_Protocol() {
		String[] agents = new String[this.FBABuyers.size() - this.Quitting_agents.size()];
		StringAndDouble[][] nodes_list = new StringAndDouble[agents.length][];
		
    	int[] nb_node_by_agent = new int[agents.length];
    	int l = 0;
    	for(int i = 0; i < agents.length; i++){
    		if(this.Quitting_agents.indexOf(this.FBABuyers.get(i)) == -1){
    			agents[l] = this.FBABuyers.get(i);
    			
    			String[] nodes = this.ComplexPropositions.get(i).getBidsForFirst();
    			nodes_list[l] = new StringAndDouble[nodes.length / 2];
    			for(int k = 0; k < nodes_list[l].length; k++)
    				nodes_list[l][k] = new StringAndDouble(nodes[2*k], Double.valueOf(nodes[2*k + 1]));
    			
    			l++;
    		}
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
    		String[] nodes_to_send = new String[2 * nb_node_by_agent[j]];
    		for(int k = 0; k < nb_node_by_agent[j]; k++){
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
	protected void setScenario() {
		// TODO Auto-generated method stub

	}

}
