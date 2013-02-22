package agent_library.coordinated_agents;

import java.util.ArrayList;

import agent_library.basic_agents.AgentStoppedException;
import agent_library.basic_agents.ThreadAgent;
import agent_library.connections.ClientConnection;

/**
 * Abstract class that represents a coordinator agent the coordinates other agents according to the following protocol:
 * <br><br>
 *  1) It receives messages from the coordinated agents in the form:
 * <br><br><b>
 * [agent-id]###[current-node]
 * <br><br></b>
 * 2) Then it answers (maybe all of them) with a message in the form:
 * <br><br><b>
 * [agent-id-1]###[next-node-1]###[agent-id-2]###[next-node-2]###[...]###
 * <br><br></b>
 * 3) It does nothing, otherwise.
 * <br><br>
 * A proper implementation doesn't need to access anything from the core API, except for non-blocking 
 * methods to perceive graphs (which should only be called inside implementations of one of the
 * <b>on[*]GraphPerception</b> methods).
 * 
 * @author Pablo A. Sampaio
 */
public abstract class CoordinatorAgent extends ThreadAgent {
	protected final ArrayList<String> receivedBroadcasts;

	
	public CoordinatorAgent(String id, ClientConnection conn, boolean useGraph2) {
		super(id, conn, useGraph2);
		this.receivedBroadcasts = new ArrayList<String>(); //buffer allocated once
	}
	
	/**
	 * Selects the next node of the given agent.
	 */
	protected abstract String selectGoalNode(String agentId, String agentCurrentNode) throws AgentStoppedException;
	
	/**
	 * May optionally be overridden to do initial computations with the graph.
	 */
	protected void onFirstGraphPerception() throws AgentStoppedException {
	}
	
	/**
	 * Called when there is a new graph perception and there are agents' requests for nodes. 
	 * It allows a subclass to update information used to choose the next nodes of each agent.
	 * @throws AgentStoppedException 
	 */
	protected abstract void onNewGraphPerceptionAndNewRequests() throws AgentStoppedException;

	
	public void run() {
		int currentTurn = -1;
		int turn;
		
		try {
			
			super.waitNewGraphPerception(30000);  //30s
			
			if (super.hasNewGraphPerception()) {
				onFirstGraphPerception();
			} else {
				throw new RuntimeException("Initial graph perception not received until timeout!!!");
			}
		
			while (!this.stopRequested) {
				
				turn = super.getCurrentTurn();
				if (turn != currentTurn) {
					currentTurn = turn;
					printDebug("Current turn: " + currentTurn);
				}
				
				this.attendRequests();
				
				super.agentSleep(010);
			}
		
		} catch (AgentStoppedException e) {
			//ok...
			
		}
		
		printDebug("Stopped!");
	}

	private void attendRequests() throws AgentStoppedException {
		super.retrieveBroadcasts(receivedBroadcasts);

		String answerMessage = "";
		
		for (int i = 0; i < receivedBroadcasts.size(); i++) {
			String message = this.receivedBroadcasts.get(i);
			//printDebug("Received: " + message);
			
			int markPosition = message.indexOf("###");
			
			String agentId       = message.substring(0, markPosition);
			String agentCurrNode = message.substring(markPosition + 3);
			String agentNextNode;

			if (super.hasNewGraphPerception()) {
				this.onNewGraphPerceptionAndNewRequests();  //call to subclass method
			}				
			
			agentNextNode = selectGoalNode(agentId, agentCurrNode);  //call to subclass method

			answerMessage += agentId + "###" + agentNextNode + "###";
		}
		
		if (!answerMessage.equals("")) {
			printDebug("Coordinator sending: " + answerMessage);
			super.actSendBroadcast(answerMessage);
		}

	}
	
	protected void printDebug(String message) {
		if (identifier != null) {
			System.out.println(identifier.toUpperCase() + ": " + message);
		}
	}

}
