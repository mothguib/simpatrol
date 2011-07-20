package FBA_Open;

import java.util.LinkedList;

import FBA.SpeechAct;

public class Open_FBA_coopByNodeGroup_Agent extends Open_FBA_Agent {

	
	public Open_FBA_coopByNodeGroup_Agent(String id, double entering_time, double quitting_time, 
											int number_of_agents, LinkedList<String> nodes,
											double idleness_rate_for_path, double idleness_rate_for_auction, 
											String society_id) {
		super(id, entering_time, quitting_time, number_of_agents, nodes, 
				idleness_rate_for_path, idleness_rate_for_auction, society_id);
	}

	public Open_FBA_coopByNodeGroup_Agent(String id, int number_of_agents, LinkedList<String> nodes,
											double idleness_rate_for_path, double idleness_rate_for_auction) {
		super(id, number_of_agents, nodes, idleness_rate_for_path, idleness_rate_for_auction);
	}

	
	@Override
	protected SpeechAct enter_Message(int id) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected SpeechAct manage_Enter_Message(SpeechAct quitting_act) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected void manage_Enter_Protocol() {
		// TODO Auto-generated method stub

	}
	
	
	@Override
	protected SpeechAct quit_Message(int id) {
		// TODO Auto-generated method stub
		return null;
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
