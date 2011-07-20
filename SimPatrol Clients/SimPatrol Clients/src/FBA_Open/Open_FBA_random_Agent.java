package FBA_Open;

import java.util.LinkedList;

import FBA.ComplexBid;
import FBA.SpeechAct;
import FBA.SpeechActPerformative;

public class Open_FBA_random_Agent extends Open_FBA_Agent {

	public Open_FBA_random_Agent(String id, double entering_time, double quitting_time, 
									int number_of_agents, LinkedList<String> nodes,
									double idleness_rate_for_path, double idleness_rate_for_auction, 
									String society_id) {
		super(id, entering_time, quitting_time, number_of_agents, nodes, 
								idleness_rate_for_path, idleness_rate_for_auction, society_id);
	}
	
	public Open_FBA_random_Agent(String id, int number_of_agents, LinkedList<String> nodes,
									double idleness_rate_for_path, double idleness_rate_for_auction) {
		super(id, number_of_agents, nodes, idleness_rate_for_path, idleness_rate_for_auction);
	}
	

	protected SpeechAct manage_Quit_Message(SpeechAct quitting_act){
		return  new SpeechAct(quitting_act.getTransactionId(), SpeechActPerformative.PROPOSE, this.agent_id, quitting_act.getSender());
	}
	

    
    protected void manage_Quit_Protocol(){
    	String[] agents = new String[this.FBABuyers.size() - this.Quitting_agents.size()];
    	int[] nb_node_by_agent = new int[agents.length];
    	int l = 0;
    	for(int i = 0; i < agents.length; i++){
    		if(this.Quitting_agents.indexOf(this.FBABuyers.get(i)) == -1){
    			agents[l] = this.FBABuyers.get(i);
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
    	
    	for(int k = 0; k < agents.length; k++){
    		String[] nodes = new String[2*nb_node_by_agent[k]];
    		for(int i = 0; i < nb_node_by_agent[k]; i++){
    			if(this.myNodes.size() > 0){
    				int rand = (int)(Math.random() * this.myNodes.size());
    				String node = this.getMyNodes(rand);
    				nodes[2*i]  = node;
    				nodes[2*i + 1] = String.valueOf((int) this.estimatedIdleness(node));
    				this.myNodes.remove(rand);
    			}
    			else {
    				String[] nodes2 = new String[2*i];
    				for(int j = 0; j < 2*i; j++)
    					nodes2[j] = nodes[j];
    				nodes = nodes2;
    				break;
    			}
    		}
    		ComplexBid bid = new ComplexBid(null, nodes, null, null);
    		SpeechAct act = new SpeechAct(myCurrentTransaction, SpeechActPerformative.ACCEPT, this.agent_id, agents[k], bid);
			this.SendSpeechAct(act);
    	}
    	this.inactive = true;
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
		
		String[] nodes_to_send = new String[2*nb_nodes_to_send];
		for(int i = 0; i < nb_nodes_to_send; i++){
			int rand = (int)(Math.random() * this.myNodes.size());
			String node = this.getMyNodes(rand);
			int node_idle = (int)this.estimatedIdleness(rand);
			nodes_to_send[2*i] = node;
			nodes_to_send[2*i + 1] = String.valueOf(node_idle);
			this.removeFromMyNodes(node);
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
	protected void setScenario() {
		/*
		this.Society_id = "s0";
		this.agents_num = 2;
		
		if(this.agent_id.contains("a" + Open_FBA_Agent.NB_AGENTS_AT_START + "@")){
			this.entering_time = 100;
			//this.quitting_time = 3000;
			this.agents_num = 3;
		}
		*/
	}

	@Override
	protected SpeechAct quit_Message(int id) {
		return new SpeechAct(id, SpeechActPerformative.QUIT, this.agent_id, "all_agents");
	}


    
    	
}

