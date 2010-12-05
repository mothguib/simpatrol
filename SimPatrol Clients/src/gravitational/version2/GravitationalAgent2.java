package gravitational.version2;

import java.io.IOException;

import util.graph2.Graph;
import util.graph2.GraphTranslator;
import dummy_client.TcpConnection;


public class GravitationalAgent2 implements Runnable  {
	protected String identifier;
	protected TcpConnection connection;
	protected boolean working;
	
	private String nextNode;
	
	private GravManager gravManager;
	
	
	public GravitationalAgent2(String agentIdentifier, TcpConnection agentConnection, GravManager board) {
		identifier = agentIdentifier;
		connection = agentConnection;
		gravManager = board;
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

			waitForInitialNodeAndGraph();

			while (working) {
				visitAndGoToNextNode();
				waitForGraph();
				chooseAndGoToNextNode();
				waitForArrival();				
			}

		} catch (Exception exc) {
			working = false;
			exc.printStackTrace();
			
		}
	
		System.out.println("AGENT: finished");	
	}


	/**
	 * Sends messages to visit the current node then wait a little 
	 * (because only in the next cycle the agent will a goto action). 
	 */
	private void visitAndGoToNextNode() throws IOException {
		this.connection.send("<action type=\"2\"/>");
		
		_PRINT("visiting \"" + nextNode + "\"");
		
		Thread.yield();
	}
	
	/**
	 * Chooses the new goal node, considering that the agent has arrived 
	 * at the last goal.
	 */
	private void chooseAndGoToNextNode() throws IOException {
		String currNodeId = this.nextNode;

		this.nextNode = gravManager.getStrongestNeighbor(currNodeId);
		
		// sends a message to other agents
		this.connection.send("<action type=\"3\" message=\"" + this.identifier 
								+ "#" + currNodeId + "##" + this.nextNode + "\"/>");

		// inform the gravity manager
		this.gravManager.setNextNode(this.identifier, currNodeId, this.nextNode);
		
		// go to the chosen node
		this.connection.send("<action type=\"1\" node_id=\"" + nextNode + "\"/>");
	}
	
	/**
	 * Waits the perception of the graph and of the starting node of the agent.
	 */
	private void waitForInitialNodeAndGraph() {
		boolean agentArrived = false;
		boolean graphPerceived = false;
		String[] messages;
		
		_PRINT("awaiting arrival message");
		
		while (!(agentArrived && graphPerceived) && this.working) {
			Thread.yield();
			
			messages = connection.retrieveMessages();

			for (int i = 0; i < messages.length; i++) {
				if (!agentArrived) {
					agentArrived = perceiveArrivalMessage(messages[i]);
					
					if (agentArrived) {	_PRINT("agent starting at: " + nextNode); }
				}
				
				if (!graphPerceived) {
					graphPerceived = perceiveGraphUpdate(messages[i]);
				}
				
				perceiveBroadcastOfChosenNode(messages[i]);
			}
		}
		
	}

	/**
	 * Wait until a graph is perceived.
	 */
	private void waitForGraph() {
		boolean graphPerceived = false;
		String[] messages;
		
		_PRINT("awaiting graph message");
		
		while (!graphPerceived && this.working) {
			Thread.yield();
			
			messages = connection.retrieveMessages();

			for (int i = 0; i < messages.length; i++) {
				if (!graphPerceived) {
					graphPerceived = perceiveGraphUpdate(messages[i]);
				}
				
				perceiveBroadcastOfChosenNode(messages[i]);
			}
		}

		//_PRINT("graph perceived");
	}
	
	/**
	 * Waits the agent to arrive at his destiny node.
	 */
	private void waitForArrival() {
		boolean agentArrived = false;
		boolean graphPerceived;
		String[] messages;
		
		_PRINT("awaiting arrival message");
		
		while (!agentArrived && this.working) {
			Thread.yield();
			
			messages = connection.retrieveMessages();

			for (int i = 0; i < messages.length; i++) {
				agentArrived = perceiveArrivalMessage(messages[i]);
				if (agentArrived) {
					_PRINT("agent arrived: " + nextNode);
					break;
				}
				
				graphPerceived = perceiveGraphUpdate(messages[i]);
				if (graphPerceived) {
					continue;
				}
				
				perceiveBroadcastOfChosenNode(messages[i]);
			}
		}
		
	}
	
	/**
	 * Checks if the given message is the perception of a graph. If it is,
	 * the manager is updated with this graph. 
	 */
	private boolean perceiveGraphUpdate(String message) {
		try {			
			Graph[] perceivedGraphs = GraphTranslator.getGraphs(message);
			if (perceivedGraphs.length > 0) {
				gravManager.setGraph(perceivedGraphs[0]);
				return true;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		return false;
	}
	
	/**
	 * Checks if the given message is a broadcast (which is used only
	 * to inform about the nodes chosen by each agent). If it is, the
	 * manager is updated with this information.
	 */
	private boolean perceiveBroadcastOfChosenNode(String perception) {
		
		if (perception.indexOf("<perception type=\"3\"") > -1
				|| perception.indexOf("<perception type=\"7\"") > -1) {
			
			int messageIndex = perception.indexOf("message=\"");
			
			String message = perception.substring(messageIndex + 9);
			message = message.substring(0, message.indexOf("\""));

			int firstMarkIndex  = message.indexOf('#');
			int secondMarkIndex = message.indexOf("##");
			
			String agentId = message.substring(0, firstMarkIndex);
			String currNodeId = message.substring(firstMarkIndex+1, secondMarkIndex);
			String nextNodeId = message.substring(secondMarkIndex+2);
			
			_PRINT("Received <" + agentId + ", " + currNodeId + ", " + nextNodeId + ">");
			
			this.gravManager.setNextNode(agentId, currNodeId, nextNode);
		}
		
		return false;
	}


	/**
	 * Checks if the given message is a perception and if it indicates
	 * that the agent has arrived at the next node. 
	 */
	private boolean perceiveArrivalMessage(String message) {

		if (message.indexOf("<perception type=\"4\"") > -1) {
			int nodeIndex = message.indexOf("node_id=\"");
			
			message = message.substring(nodeIndex + 9);
			
			String nodeId = message.substring(0, message.indexOf("\""));

			// for the start of simulation
			if (nextNode == null) {
				nextNode = nodeId;
				return true;
			} else if (nodeId.equals(nextNode)) {
				return true;
			}
		}
		
		return false;
	}
	
	private void _PRINT(String message) {
		System.out.println(identifier.toUpperCase() + ": " + message);
	}
	
}


