package dummy_client;

import java.io.IOException;


/**
 * A simple agent that just goes back and forth between two nodes. 
 * This is the listener version: the behavior of the agent is implemented
 * in methods called by the connection when a message is received (the 
 * agent is not a thread).
 *  
 * @author Pablo Sampaio
 */
public class DummyAgent implements TcpConnectionObserver {
	protected String identifier;
	protected TcpConnection connection;
	protected boolean working;
	
	// nodes that the agent will visit
	protected String[] nodes;
	protected int nextNode;
	
	// variables to control messages synchronously printed in the console
	protected long timeToPrint; 
	protected static final long PRINT_INTERVAL = 1000; //1 sec
	
	
	/**
	 * The agent will walk to nodes "currentNode" and "neighborNode", alternately. 
	 */
	public DummyAgent(String agentId, TcpConnection con, String currentNode, String neighborNode) {
		identifier = agentId;
		connection = con;
		working = false;
		
		nodes = new String[]{ currentNode, neighborNode }; // "current" must be the first
		nextNode = 0;

		timeToPrint = 0;
	}
	
	public void startWorking() {
		connection.addObserver(this);
		connection.start();	
		working = true;
	}	

	public void stopWorking() {
		working = false;
		connection.stopWorking();
		asyncPrint("Agent finished");
	}
	
	@Override
	public void start() {
		//not used
	}
	
	
	/**
	 * Main method of this agent, where his behavior is implemented.
	 */
	@Override
	public void update() {
		if (!working) {
			return;
		}
	
		if (lookForArrivalMessage()) {
			try {				
				visitAndGoToNextNode();

			} catch (Exception e) {
				asyncPrint("Error");
				e.printStackTrace();

			}		
		}

	}
	
	
	/**
	 * Checks if a message was received informing that the agent 
	 * has arrived at his destiny node. 
	 */
	protected boolean lookForArrivalMessage() {
		String[] messages;
		boolean arrived = false;
		
		messages = connection.retrieveMessages();

		for (int i = 0; i < messages.length; i++) {
			arrived = isArrivalMessage(messages[i]);
			if (arrived) {
				return true;
			}
		}

		syncPrint("Waiting agent to arrive at node \"" + nodes[nextNode] + "\"");
		return false;		
	}
	
	
	/**
	 * Checks if the given message is a perception and if it indicates
	 * that the agent has arrived at the node planned. 
	 */
	protected boolean isArrivalMessage(String message) {

		if (message.indexOf("<perception type=\"4\"") > -1) {
			int nodeIndex = message.indexOf("node_id=\"");
			
			message = message.substring(nodeIndex + 9);
			
			String nodeId = message.substring(0, message.indexOf("\""));

			if ( nodeId.equals(nodes[nextNode]) ) {
				return true;
			}
		}
		
		return false;
	}

	
	/**
	 * Send messages to visit the current node and to go to the next node. 
	 */
	protected void visitAndGoToNextNode() throws IOException {
		// send message to visit current node
		this.connection.send("<action type=\"2\"/>");

		nextNode = (nextNode + 1) % nodes.length;
		
		// send message to go to the next node
		this.connection.send("<action type=\"1\" node_id=\"" + nodes[nextNode] + "\"/>");
		asyncPrint("Going to node \"" + nodes[nextNode] + "\"");

	}
	
	
	/**
	 * Prints the message in the console, if the time interval since the last
	 * is not lower than PRINT_INTERVAL. Useful to avoid printing too much 
	 * similar messages (e.g. inside a loop).
	 */
	protected void syncPrint(String message) {
		if (System.currentTimeMillis() >= timeToPrint) {
			System.out.printf("[%s]: %s.\n", identifier, message);
			timeToPrint = System.currentTimeMillis() + PRINT_INTERVAL;
		}
	}

	
	/**
	 * Prints the message in the console (doesn't have to respect the minimum
	 * time interval since last message) 
	 */
	protected void asyncPrint(String message) {
		System.out.printf("[%s]: %s.\n", identifier, message);
		timeToPrint = System.currentTimeMillis() + PRINT_INTERVAL;
	}

	
}
