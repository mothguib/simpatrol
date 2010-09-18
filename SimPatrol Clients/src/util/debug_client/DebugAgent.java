package util.debug_client;

import java.io.IOException;


/**
 * A simple agent that just goes back and forth between two nodes. 
 * This is the threaded version. (TODO: listener version).
 *  
 * @author Pablo
 */
public class DebugAgent extends Thread {
	private String identifier;
	private TcpConnection connection;
	boolean working;
	
	// nodes that the agent will visit
	private String[] nodes;
	private int nextNode;
	
	// variables to control messages synchronously printed in the console
	private long timeToPrint; 
	private static final long PRINT_INTERVAL = 1000; //1 sec
	
	
	/**
	 * The agent will walk to nodes "currentNode" and "neighborNode", alternately. 
	 */
	public DebugAgent(String agentId, TcpConnection con, String currentNode, String neighborNode) {
		identifier = agentId;
		connection = con;
		
		nodes = new String[]{ currentNode, neighborNode }; // current must be the first
		nextNode = 0;
	}
	
	public void startWorking() {
		connection.start();	
		working = true;
		super.start();
	}	

	public void stopWorking() {
		working = false;
	}
	
	@Override
	public void run() {
	
		try {

			while (working) {

				visitAndGoToNextNode();
				waitForAgentArrival();
				
			}

		} catch (Exception exc) {
			asyncPrint("Error");
			working = false;
			exc.printStackTrace();
			
		}
		
		System.out.println("Agent client finished.");
	}
	
	
	/**
	 * Waits the agent to arrive at his destiny node. Only returns when
	 * the agent arrives. 
	 */
	private void waitForAgentArrival() {
		String[] messages;
		boolean agentArrived = false;
		int i;
		
		messages = connection.getBufferAndFlush();

		while (!agentArrived && this.working) {
			syncPrint("Waiting agent to arrive at node \"" + nodes[nextNode] + "\"");
			
			messages = connection.getBufferAndFlush();
			
			i = 0;
			while (i < messages.length && !agentArrived) {
				agentArrived = agentArrivalPerceived(messages[i]);
				i ++;
			}			

		}
		
	}
	
	
	/**
	 * Checks if the given message is a perception and if it indicates
	 * that the agent has arrived at the node planned. 
	 */
	private boolean agentArrivalPerceived(String message) {

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
	 * Send messages to visit the current node and to goe to the next node. 
	 */
	private void visitAndGoToNextNode() throws IOException {
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
	private void syncPrint(String message) {
		if (System.currentTimeMillis() >= timeToPrint) {
			System.out.printf("[%s]: %s.\n", identifier, message);
			timeToPrint = System.currentTimeMillis() + PRINT_INTERVAL;
		}
	}

	
	/**
	 * Prints the message in the console (doesn't have to respect the minimum
	 * time interval since last message) 
	 */
	private void asyncPrint(String message) {
		System.out.printf("[%s]: %s.\n", identifier, message);
		timeToPrint = System.currentTimeMillis() + PRINT_INTERVAL;
	}

	
}
