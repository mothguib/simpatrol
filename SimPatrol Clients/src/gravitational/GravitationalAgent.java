package gravitational;

import java.io.IOException;

import dummy_client.TcpConnection;


public class GravitationalAgent implements Runnable {
	protected String identifier;
	protected TcpConnection connection;
	protected boolean working;
	
	private String goalNode;
	
	
	public GravitationalAgent(String agentIdentifier, TcpConnection agentConnection) {
		identifier = agentIdentifier;
		connection = agentConnection;
	}

	
	public void startWorking() {
		connection.start();	
		working = true;
		
		Thread thread = new Thread(this);
		thread.start();
	}	

	public void stopWorking() {
		//asyncPrint("Preparing to finish..");
		working = false;
		connection.stopWorking();
	}
	
	public void run() {		
		
		try {

			// to perceive the initial node
			waitForArrivalMessage();

			while (working) {
				sendRequestForGoalNode();
				waitForGoalNodeMessage();
				visitAndGoToGoalNode();
				waitForArrivalMessage();				
			}

		} catch (Exception exc) {
			working = false;
			exc.printStackTrace();
			
		}
	
		System.out.println("AGENT: finished");	
	}

	/**
	 * Sends a message to the coordinator asking for the next goal node.
	 */
	private void sendRequestForGoalNode() throws IOException {
		// sends a message with the identifier of this agent and its current node (last goal)
		this.connection.send("<action type=\"3\" message=\"REQ##" + this.identifier + "###" + goalNode + "\"/>");
		
		PRINT("requesting goal node - <action type=\"3\" message=\"REQ##" + this.identifier + "###" + goalNode + "\"/>");
	}

	/**
	 * Sends messages to visit the current node and to go to the next node. 
	 */
	private void visitAndGoToGoalNode() throws IOException {
		// visits current node
		this.connection.send("<action type=\"2\"/>");

		// go to the goal node
		this.connection.send("<action type=\"1\" node_id=\"" + goalNode + "\"/>");
		
		PRINT("visiting and going to goal...");
	}
	
	/**
	 * Waits for a message from the coordinator informing the goal node of
	 * this agent.  
	 */
	private void waitForGoalNodeMessage() {
		String goal = null;
		String[] messages;
		
		PRINT("awaiting goal node...");
		
		while (goal == null && this.working) {
			Thread.yield();
			
			messages = connection.retrieveMessages();

			for (int i = 0; i < messages.length; i++) {
				PRINT("received - " + messages[i]);
				goal = perceiveGoalNode(messages[i]);
				
				if (goal != null) {
					goalNode = goal;
					PRINT("goal node received - " + goal);
					break;
				}
			}
		}

	}

	/**
	 * Checks if the perception is a broadcast from the coordinator informing
	 * the goal node of this agent. 
	 */
	private String perceiveGoalNode(String perception) {
		String agentId;
		String goalNode;
		
		int markIndex = perception.indexOf("message=\"ANS##");
		
		// perceives a message from coordinator
		if (markIndex > -1) {			
			perception = perception.substring(markIndex + 14);
			perception = perception.substring(0, perception.indexOf("\""));

			markIndex = perception.indexOf("###");

			agentId  = perception.substring(0, markIndex);
			goalNode = perception.substring(markIndex + 3);
			
			// if the message is for this agent
			if (agentId.equals(this.identifier)) {
				return goalNode;
			}

		}
		
		return null;
	}

	/**
	 * Waits the agent to arrive at his destiny node.
	 */
	private void waitForArrivalMessage() {
		boolean agentArrived = false;
		String[] messages;
		
		PRINT("awaiting arrival message");
		
		while (!agentArrived && this.working) {
			Thread.yield();
			
			messages = connection.retrieveMessages();

			for (int i = 0; i < messages.length; i++) {
				//PRINT("received - " + messages[i]);
				agentArrived = perceiveArrivalMessage(messages[i]);
				if (agentArrived) {
					PRINT("agent arrived: " + goalNode);
					break;
				}
			}
		}
		
	}
	
	/**
	 * Checks if the given message is a perception and if it indicates
	 * that the agent has arrived at the goal node. 
	 */
	private boolean perceiveArrivalMessage(String message) {

		if (message.indexOf("<perception type=\"4\"") > -1) {
			int nodeIndex = message.indexOf("node_id=\"");
			
			message = message.substring(nodeIndex + 9);
			
			String nodeId = message.substring(0, message.indexOf("\""));

			// for the start of simulation
			if (goalNode == null) {
				goalNode = nodeId;
				return true;
			} else if (nodeId.equals(goalNode)) {
				return true;
			}
		}
		
		return false;
	}
	
	private void PRINT(String message) {
		System.out.println(identifier.toUpperCase() + ": " + message);
	}
	
}
