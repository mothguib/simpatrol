package agent_library.basic_agents;

import java.util.ArrayList;


/**
 * This class dispatches all the callbacks of all instances of (subclasses of) CallbackAgent. 
 * It should not be directly accessed by users of the library.
 * 
 *  @author Pablo A. Sampaio
 */
class CallbackAgentManager extends Thread {
	private ArrayList<CallbackAgent> agents;
	private ArrayList<CallbackAgent> agentsToStop;
	
	private static CallbackAgentManager instance;
	
	static CallbackAgentManager getInstance() {
		if (instance == null) {
			instance = new CallbackAgentManager();
			instance.start();
		}
		return instance;
	}
	
	private CallbackAgentManager() {
		agents = new ArrayList<CallbackAgent>();
		agentsToStop = new ArrayList<CallbackAgent>();
	}
	
	synchronized void addAgent(CallbackAgent ag) {
		agents.add(ag);
	}
	
	synchronized void removeAgent(CallbackAgent ag) {
		agents.remove(ag);
		agentsToStop.add(ag);
	}

	private synchronized void getAgents(ArrayList<CallbackAgent> answer) {
		answer.clear();
		answer.addAll(agents);
	}

	private synchronized void getAgentsToStop(ArrayList<CallbackAgent> answer) {
		answer.clear();
		answer.addAll(agentsToStop);
	}

	
	@Override
	public void run() {
		
		//super.setPriority(NORM_PRIORITY); //higher or highest priority doesn't improve performance
		
		ArrayList<CallbackAgent> agentsBuffer = new ArrayList<CallbackAgent>(); // to avoid concurrency problems
		boolean changedPerception;
		
		// for each agent:  1. tests if there's new perceptions (of each kind) 
		//                  2. notifies the agent by callback
		
		while (true) {
		
			this.getAgents(agentsBuffer);
			
			for (CallbackAgent agent : agentsBuffer) {
				changedPerception = false;
				
				try {
					
					if (agent.hasNewSelfPerception()) {
						agent.onSelfPerception();
						agent.onPerception();
						changedPerception = true;
					}
					if (agent.hasNewSocietyPerception()) {
						agent.onSocietyPerception();
						agent.onPerception();
						changedPerception = true;
					}
					if (agent.hasNewGraphPerception()) {
						agent.onGraphPerception();
						agent.onPerception();
						changedPerception = true;
					}
					if (agent.hasNewBroadcasts()) {
						agent.onBroadcasts();
						agent.onPerception();
						changedPerception = true;
					}
					
					agent.updateNoPerceptionTimeout(changedPerception);					

				} catch (AgentStoppedException e) {
					//e.printStackTrace();
					printDebug("Agent " + agent.identifier + " already stopped!");
					//removeAgent(agent); //not necessary?
					
				}			

			}
			
			this.getAgentsToStop(agentsBuffer);
			
			if (agentsBuffer.size() > 0) {
				for (CallbackAgent agent : agentsBuffer) {
					agent.onStop();
					printDebug("Removed agent: " + agent.identifier + ".");
				}
			
				synchronized (this) {
					this.agentsToStop.removeAll(agentsBuffer); //can't clear because new agents may have been added
				}
			}

		}
	}

	
	private void printDebug(String message) {
		System.out.println("CALLBK-MANAGER: " + message);
	}
	
}
