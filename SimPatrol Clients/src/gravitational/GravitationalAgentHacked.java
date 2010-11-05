package gravitational;

import gravitational.gravity_manager.GravityManager;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

import util.graph2.Graph;
import util.graph2.GraphTranslator;

import dummy_client.TcpConnection;

/**
 * Version of gravitational agent without coordinator, but sharing objects 
 * between them. 
 * 
 * Attention: only one agent of the team should have perception of the graph.
 *  
 * @author Pablo Sampaio
 */
public class GravitationalAgentHacked implements Runnable {
	protected String identifier;
	protected TcpConnection connection;
	protected boolean working;
	
	private String goalNode;
	
	private BlackBoard blackboard;
	
	
	public GravitationalAgentHacked(String agentIdentifier, TcpConnection agentConnection, BlackBoard sharedBoard) {
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
	
	private boolean perceiveGraphToUpdateBlackBoard(String message) {
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
					perceiveGraphToUpdateBlackBoard(messages[i]);
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



class BlackBoard {
	private MassGrowth massGrowth;
	private double distanceExponent;
	private GravitiesCombinator gravityCombinator;
	
	private Graph graph;
	private GravityManager gravityManager;
	
	private List<String>[] visitsScheduledPerNode; //for each node, a list of agents' identifiers
	
	
	BlackBoard(MassGrowth mgrowth, double exponent, GravitiesCombinator gcomb) {
		massGrowth = mgrowth;
		distanceExponent = exponent;
		gravityCombinator = gcomb;
	}

	
	synchronized void setGraph(Graph g) {
		if (this.graph == null) {
			this.graph = g;
			setupGravityManager();
		} else {
			this.graph = g;
			recalculateGravities();
		}
	}
	
	@SuppressWarnings("unchecked")
	private void setupGravityManager() {
		gravityManager = gravityCombinator.createGravityManager(graph, distanceExponent);		

		visitsScheduledPerNode = new List[graph.getNumVertices()];
		for (int i = 0; i < visitsScheduledPerNode.length; i++) {
			visitsScheduledPerNode[i] = new LinkedList<String>();
		}

		recalculateGravities();
	}
	
	private void recalculateGravities() {
		int numVertices = gravityManager.getNumVertices();

		gravityManager.undoAllGravities();

		double idleness;
		double nodeMass;
		
		// for each node, applies its gravity to attract agents
		for (int node = 0; node < numVertices; node++) {
			if (visitsScheduledPerNode[node].size() > 0) {
				nodeMass = 0.0d;
			} else {	
				idleness   = graph.getNode(node).getIdleness();
				nodeMass = massGrowth.getVertexMass(1.0d, idleness);
			}
			
			gravityManager.applyGravity(node, nodeMass);
		}
		
	}
	
	synchronized String selectGoalNode(String agentId, String currNodeId) {
		if (graph == null) {
			return null;
		}
		System.out.println("BBoard: " + agentId + " selecting from " + currNodeId);
		int currNode = graph.getNode(currNodeId).getIndex();
		
		int goalNode = currNode;
		double goalGravity = -1.0d;
		
		// chooses the neighbor with higher gravity 
		for (Integer neighbor : graph.getSuccessors(currNode)) {
			if (gravityManager.getGravity(currNode,neighbor) > goalGravity) {
				goalNode = neighbor;
				goalGravity = gravityManager.getGravity(currNode,neighbor);
			}
		}
		
		visitsScheduledPerNode[currNode].remove(agentId);
		visitsScheduledPerNode[goalNode].add(agentId); 

		// if it is the only agent going to the node, undo the gravity (mass is zeroed)
		if (visitsScheduledPerNode[goalNode].size() == 1) {
			gravityManager.undoGravity(goalNode);
		}
		
		return graph.getNode(goalNode).getIdentifier();
	}
	
}