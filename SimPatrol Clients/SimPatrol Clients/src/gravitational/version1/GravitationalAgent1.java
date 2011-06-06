package gravitational.version1;

import java.io.IOException;

import util.graph2.Graph;
import util.graph2.GraphTranslator;
import dummy_client.TcpConnection;

/**
 * Version of gravitational agent in which all agents share an object, used 
 * as a kind of blackboard. 
 * 
 * The blackboard holds the calculated gravities, taking into account 
 * the places where each agent is going. When an agent decides to go to a
 * node, the mass of the node is zeroed in the blackboard, so the gravities
 * are automatically updated to all agents. 
 * 
 * @author Pablo Sampaio
 */
public class GravitationalAgent1 implements Runnable {
	protected String identifier;
	protected TcpConnection connection;
	protected boolean working;
	
	private String goalNode;
	
	private BlackBoard blackboard;
	
	
	public GravitationalAgent1(String agentIdentifier, TcpConnection agentConnection, BlackBoard sharedBoard) {
		identifier = agentIdentifier;
		connection = agentConnection;
		blackboard = sharedBoard;
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
				visitAndGoToGoalNode();
				// TODO: corrigir possível erro: deveria esperar um ciclo - ver versão 2
				waitForArrivalMessage();				
			}

		} catch (Exception exc) {
			working = false;
			exc.printStackTrace();
			
		}
	
		System.out.println("AGENT: finished");	
	}

	/**
	 * Sends messages to visit the current node and to go to the next node. 
	 */
	private void visitAndGoToGoalNode() throws IOException {
		// visits current node
		this.connection.send("<action type=\"2\"/>");
		
		// chooses the new goal, considering that the agent arrived at the last goal
		String currNodeId = this.goalNode;
		do {
			Thread.yield();
			this.goalNode = blackboard.selectGoalNode(this.identifier, currNodeId);
		} while (this.goalNode == null);

		// go to the goal node
		this.connection.send("<action type=\"1\" node_id=\"" + goalNode + "\"/>");
		
		_PRINT("visiting and going to goal...");
	}

	private boolean perceiveGraphAndUpdateBlackBoard(String message) {
		try {			
			Graph[] perceivedGraphs = GraphTranslator.getGraphs(message);
			if (perceivedGraphs.length > 0) {
				blackboard.setGraph(perceivedGraphs[0]);
				return true;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		return false;
	}

	/**
	 * Waits the agent to arrive at his destiny node.
	 */
	private void waitForArrivalMessage() {
		boolean agentArrived = false;
		String[] messages;
		
		_PRINT("awaiting arrival message");
		
		while (!agentArrived && this.working) {
			Thread.yield();
			
			messages = connection.retrieveMessages();

			for (int i = 0; i < messages.length; i++) {
				//PRINT("received - " + messages[i]);
				agentArrived = perceiveArrivalMessage(messages[i]);
				if (agentArrived) {
					_PRINT("agent arrived: " + goalNode);
					break;
				} else {
					perceiveGraphAndUpdateBlackBoard(messages[i]);
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
	
	private void _PRINT(String message) {
		System.out.println(identifier.toUpperCase() + ": " + message);
	}
	
}


