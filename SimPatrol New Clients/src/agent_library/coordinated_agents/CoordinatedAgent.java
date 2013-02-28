package agent_library.coordinated_agents;

import java.util.ArrayList;

import agent_library.basic_agents.AgentStoppedException;
import agent_library.basic_agents.ThreadAgent;
import agent_library.connections.ClientConnection;
import agent_library.perceptions.AgentInformation;


/**
 * Implements a coordinated agent, which asks each node to be visited to a CoordinatorAgent by
 * sending a message:
 * <br><br>
 * [agent-id]##[current-node]
 * <br><br>
 * This class is sufficiently generic to be used (without extension) with any subclass of CoordinatorAgent.
 * 
 * @see CoordinatorAgent
 * @author Pablo A. Sampaio
 */
public final class CoordinatedAgent extends ThreadAgent {
	private static final long PERCEPTION_TIMEOUT = 20000; //20s
	
	private int goalReq = 0;
	private int goalRec = 0;
	
	private String delimitedIdentifier;  //string to be searched in coordinator's answer to this agent
	private ArrayList<String> messages;

	
	public CoordinatedAgent(String id, ClientConnection conn) {
		super(id, conn, true);
		this.delimitedIdentifier = id + "###";
		this.messages = new ArrayList<String>(); //buffer allocated once
	}

	private String parseGoalNode(String message) {
		int agentIdIndex = message.indexOf(this.delimitedIdentifier);
		if (agentIdIndex > -1) {
			message = message.substring(agentIdIndex + this.delimitedIdentifier.length());  //it is now after the mark '###'
			return message.substring(0, message.indexOf("###"));
		}	
		return null;
	}


	public void run() {
		try {
			operate();
		} catch (AgentStoppedException e) {
			//ok
		}
		
		printDebug("Stopped!");
		printDebug("Sent " + goalReq + " And Received " + goalRec);
	}
	
	private void operate() throws AgentStoppedException {
		String myPosition;
		String myGoalNode;
		
		AgentInformation agentInfo = this.perceiveSelfBlocking(PERCEPTION_TIMEOUT); //20s
		if (agentInfo == null) {
			throw new RuntimeException("Initial self perception not received!");
		}

		myPosition = agentInfo.node_id;				
		
		while (!this.stopRequested) {
			
			printDebug("Visiting.");
			this.actVisit();
			
			this.waitNewSelfPerception(PERCEPTION_TIMEOUT); //to wait the next turn
															//TODO: receive a confirmation of "action done" from simulator (in the future)
			
			printDebug("Asking node from " + myPosition);
			this.actSendBroadcast(this.identifier + "###" + myPosition);
			goalReq++;

			myGoalNode = this.receiveGoalNode();
			goalRec++;
			
			printDebug("Going to: " + myGoalNode);
			this.actGoto(myGoalNode);

			while (!this.stopRequested && !myPosition.equals(myGoalNode)) {
				AgentInformation selfInfo = this.perceiveSelfBlocking(PERCEPTION_TIMEOUT);
				if (selfInfo != null) {
					myPosition = selfInfo.node_id;
				} else {
					printDebug("Still waiting position...");
				}
			}
			
			//printDebug("New position: " + myPosition);
		}
		
	}
	
	private String receiveGoalNode() throws AgentStoppedException {
		String goalNode = null;
		
		while (goalNode == null) {
			retrieveBroadcastsBlocking(messages, PERCEPTION_TIMEOUT);
			
			if (messages.size() == 0) {
				printDebug("Waited too long for goal node. Doing nothing...");
				this.actDoNothing();	
			
			} else {
				String nodeReceived;
				for (String message : messages) {
					nodeReceived = parseGoalNode(message);
						
					if (nodeReceived != null) {
						//printDebug("Received node: " + nodeReceived);
						goalNode = nodeReceived;
						break;
					}
				}
			}
			
		}

		return goalNode;
	}

}

