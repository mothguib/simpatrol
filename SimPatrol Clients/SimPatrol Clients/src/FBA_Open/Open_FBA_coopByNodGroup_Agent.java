package FBA_Open;

import java.util.LinkedList;

import util.StringAndDouble;

import FBA.ComplexBid;
import FBA.SpeechAct;
import FBA.SpeechActPerformative;

public class Open_FBA_coopByNodGroup_Agent extends Open_FBA_Agent {

	public Open_FBA_coopByNodGroup_Agent(String id, double entering_time, double quitting_time, 
										int number_of_agents, LinkedList<String> nodes,
										double idleness_rate_for_path, double idleness_rate_for_auction, 
										String society_id) {
		super(id, entering_time, quitting_time, number_of_agents, nodes, 
					idleness_rate_for_path, idleness_rate_for_auction, society_id);
	}

	public Open_FBA_coopByNodGroup_Agent(String id, int number_of_agents, LinkedList<String> nodes,
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
		
		//
		LinkedList<String> worst_group = new LinkedList<String>();
		double cost, max_cost = 0;
		LinkedList<String> mynodes_copy;
		
		int[] current_index = new int[nb_nodes_to_send];
		for(int i = 0; i < nb_nodes_to_send; i++)
			current_index[i] = i;
		
		while(current_index[0] < this.myNodes.size() - nb_nodes_to_send){
			mynodes_copy = this.getMyNodes();
			for(int j = 0; j < current_index.length; j++)
				mynodes_copy.remove(this.getMyNodes(current_index[j]));
			
			cost = this.CalculatePathCost(this.OrderNodes(mynodes_copy));
			
			if(cost > max_cost){
				max_cost = cost;
				worst_group.clear();
				for(int j = 0; j < current_index.length; j++)
					worst_group.add(this.getMyNodes(current_index[j]));
			}
			
			
			current_index[current_index.length - 1]++;
			for(int j = current_index.length - 1; j > 0; j--)
				if(current_index[j] >= this.myNodes.size() - (current_index.length - j)){
					current_index[j-1]++;
					for(int k = j; k < current_index.length; k++)
						current_index[k] = current_index[k-1] + 1;
				}	
		}
		
		
		
		String[] nodes_to_send = new String[2*nb_nodes_to_send];
		for(int i = 0; i < nb_nodes_to_send; i++){
			nodes_to_send[2*i] = worst_group.get(i);
			nodes_to_send[2*i + 1] = String.valueOf((int)this.estimatedIdleness(worst_group.get(i)));
			this.removeFromMyNodes(worst_group.get(i));
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
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected void manage_Quit_Protocol() {
		// TODO Auto-generated method stub

	}

	

	@Override
	protected void setScenario() {
		// TODO Auto-generated method stub

	}

}
