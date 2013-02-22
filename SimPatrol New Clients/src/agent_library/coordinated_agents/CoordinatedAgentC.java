package agent_library.coordinated_agents;

import java.util.ArrayList;

import agent_library.basic_agents.AgentStoppedException;
import agent_library.basic_agents.CallbackAgent;
import agent_library.connections.ClientConnection;


/**
 * Alternative implementation of the "CoordinatedAgent" class, but without threads.
 *  
 * @see CoordinatedAgent
 * @author Pablo A. Sampaio
 */		
public final class CoordinatedAgentC extends CallbackAgent {
	private int goalReq = 0;
	private int goalRec = 0;
	
	private String delimitedIdentifier;  //string to be searched in coordinator's answer to this agent
	private ArrayList<String> messages;
	
	private enum States { S0_JUST_STARTED, S1_REQUEST_NODE, S2_WAIT_ANSWER, S3_GOING_TO_NODE };
	private States state;
	
	private String currentNode;
	private String goalNode;
	

	public CoordinatedAgentC(String id, ClientConnection conn) {
		super(id, conn, true);
		
		this.delimitedIdentifier = id + "###";
		this.messages = new ArrayList<String>(); //buffer allocated once
		this.state = States.S0_JUST_STARTED;
		
		super.setNoPerceptionTimeout(20000); //20s
	}

	private void changeState(States newState) {
		this.state = newState;
		//printDebug("New state: " + newState);
	}

	@Override
	protected void onPerception() throws AgentStoppedException {
		// behavior implemented in the specific methods
	}
	
	@Override
	protected void onSelfPerception() throws AgentStoppedException {
		if (state == States.S0_JUST_STARTED) {
			this.currentNode = super.perceiveSelf().node_id;

			printDebug("Visiting " + currentNode);			
			this.actVisit();
			
			this.changeState(States.S1_REQUEST_NODE);
			
		} else if (state == States.S1_REQUEST_NODE) {			
			printDebug("Asking node from " + this.currentNode);
			
			this.actSendBroadcast(this.identifier + "###" + currentNode); 
			this.goalReq++;
			
			this.changeState(States.S2_WAIT_ANSWER);

		} else if (state == States.S3_GOING_TO_NODE) {			
			this.currentNode = super.perceiveSelf().node_id;
			
			if (this.currentNode.equals(this.goalNode)) {
				printDebug("Arrived at " + currentNode + "!");
				printDebug("Visiting...");
				
				this.actVisit();
				
				this.changeState(States.S1_REQUEST_NODE);
				
			} else {
				//printDebug("Still going...");
				
			}

		}
	}

	@Override
	protected void onBroadcasts() throws AgentStoppedException {	
		if (state == States.S2_WAIT_ANSWER) {
			this.retrieveBroadcasts(messages);
			this.goalNode = null;
			
			String nodeReceived;			
			for (String message : messages) {
				nodeReceived = this.parseGoalNode(message);
					
				if (nodeReceived != null) {
					printDebug("Received node: " + nodeReceived);
					this.goalNode = nodeReceived;
				}
			}
			
			if (this.goalNode != null) {
				goalRec++;
				
				printDebug("Going to: " + this.goalNode);
				this.actGoto(this.goalNode);
				
				this.changeState(States.S3_GOING_TO_NODE);
			}
		}
	}

	private String parseGoalNode(String message) {
		int agentIdIndex = message.indexOf(this.delimitedIdentifier);
		if (agentIdIndex > -1) {
			message = message.substring(agentIdIndex + this.delimitedIdentifier.length());  //it is now after the mark '###'
			return message.substring(0, message.indexOf("###"));
		}
	
		return null;
	}

	@Override
	protected void onNoPerceptionTimeout() throws AgentStoppedException {
		if (state == States.S2_WAIT_ANSWER) {
			this.printDebug("Waited too long for goal node. Doing nothing..."); //TODO: remove?
			this.actDoNothing();
		
		} else {
			this.printDebug("No perception received in state " + state);
		
		}
	}
	
	@Override
	protected void onStop() {
		printDebug("Requested: " + goalReq + ". Received " + goalRec + ".");
	}


	protected void printDebug(String message) {
		if (identifier != null) {
			System.out.println(identifier.toUpperCase() + "(c): " + message);
		}
	}

}
