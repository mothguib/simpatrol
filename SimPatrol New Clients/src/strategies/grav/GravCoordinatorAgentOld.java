package strategies.grav;

import java.util.ArrayList;

import util.graph2.Graph;
import util.graph2.Node;
import agent_library.basic_agents.AgentStoppedException;
import agent_library.basic_agents.ThreadAgent;
import agent_library.connections.ClientConnection;


/**
 * Coordinator agent of the "Gravitational" strategies, implemented by extending "ThreadAgent" 
 * directly (and not by extending the "CoordinatorAgent" class).
 * 
 * @see GravCoordinatorAgent
 * @author Pablo A. Sampaio
 */
public final class GravCoordinatorAgentOld extends ThreadAgent {
	private static long TIME_WAITING_GRAPH = 30000;
	
	private final ArrayList<String> receivedBroadcasts;
	
	private Graph graph;
	
	private GravityManager gravitationalForces;
	private boolean recalculateGravities;
	

	public GravCoordinatorAgentOld(String id, ClientConnection conn, GravityManager manager) {
		super(id, conn, true);
		this.receivedBroadcasts = new ArrayList<String>(); //buffer allocated once
		this.graph = null;
		this.gravitationalForces = manager;
	}

	
	public void run() {
		printDebug("Starting with parameters " + gravitationalForces.toString());
		int currentTurn = 0;
		
		try {
			
			this.waitInitialGraphPerception();
		
			while (!this.stopRequested) {
				
				int turn = super.getCurrentTurn();
				if (turn != currentTurn) {
					currentTurn = turn;
					printDebug("Current turn: " + currentTurn);
				}
				
				this.attendRequests();
				
				this.waitGraphPerception();
			}
		
		} catch (AgentStoppedException e) {
			//ok...
			
		}
		
		printDebug("Stopped!");
	}

	private void waitInitialGraphPerception() throws AgentStoppedException {
		Graph newGraph = perceiveGraph2Blocking(TIME_WAITING_GRAPH);
		
		if (newGraph != null) {
			this.graph = newGraph;
			this.gravitationalForces.setup(this.graph);
			this.recalculateGravities = false;			
		} else {			
			throw new RuntimeException("Initial graph perception not received!!!");			
		}
		
	}


	private void waitGraphPerception() throws AgentStoppedException {
		Graph newGraph = perceiveGraph2Blocking(5);
		
		if (newGraph != null) {
			//printDebug("Graph perceived.");

			// updates the idlenesses in the current graph, because the indexes of the nodes
			// in the new graph may be different (this would affect all calculations of gravities)
			Node node;
			for (int nodeIndex = 0; nodeIndex < this.graph.getNumVertices(); nodeIndex++) {
				node = this.graph.getNode(nodeIndex);
				node.setIdleness( newGraph.getNode(node.getIdentifier()).getIdleness() );
			}
			
			this.recalculateGravities = true; // recalculates only when a request is received
		}
		
	}

	private boolean attendRequests() throws AgentStoppedException {
		if (!hasNewBroadcasts()) {
			return false;
		}
		
		this.retrieveBroadcastsBlocking(receivedBroadcasts, 5);

		String answerMessage = "";
		
		for (int i = 0; i < receivedBroadcasts.size(); i++) {
			String message = this.receivedBroadcasts.get(i);
			//printDebug("Received: " + message);
			
			int markPosition = message.indexOf("###");
			
			String agentId = message.substring(0, markPosition);
			String agentCurrentNode = message.substring(markPosition + 3);
			String agentNextNode;

			if (recalculateGravities) {
				gravitationalForces.update(this.graph);
				recalculateGravities = false;
			}				
			
			agentNextNode = this.gravitationalForces.selectGoalNode(agentId, agentCurrentNode);

			//printDebug("Coordinator will send " + agentId + " to " + agentNextNode);
			answerMessage += agentId + "###" + agentNextNode + "###";
		}
		
		if (!answerMessage.equals("")) {
			printDebug("Coordinator sending: " + answerMessage);
			this.actSendBroadcast(answerMessage);
			return true;
		} else {
			return false;
		}

	}

}
