package strategies.cc;

import java.util.ArrayList;
import java.util.LinkedList;

import util.graph.Graph;
import util.graph.Node;
import util.heap.Comparable;
import util.heap.MinimumHeap;
import agent_library.basic_agents.AgentStoppedException;
import agent_library.basic_agents.ThreadAgent;
import agent_library.connections.ClientConnection;


/**
 * The coordinator agent of the "Cognitive Coordinated" (CC) strategy. This version doesn't
 * extend the CoordinatorAgent class. It is kept mainly for didatical purposes.
 * <br><br>
 * The coordinated agents are the generic ones provided by the library, in the package "agent_library.coordinated_agents". 
 *
 * @author Pablo A. Sampaio
 */
public final class CcCoordinatorAgentOld extends ThreadAgent {
	private static long TIME_WAITING_GRAPH = 20000;
	
	private final ArrayList<String> receivedBroadcasts;

	private Graph graph;

	private MinimumHeap heap; 		// heap with the nodes, based on their idlenesses
	private boolean needsToRecalculateHeap;
	
	/**
	 * List that holds in the ith position the id of the agent, and in the
	 * (i+1)th position the id of the node to be visited by such agent.
	 */
	private final LinkedList<String> AGENTS_GOALS;  //TODO: mudar!
	

	public CcCoordinatorAgentOld(String id, ClientConnection conn) {
		super(id, conn, false);
		this.receivedBroadcasts = new ArrayList<String>(); //buffer allocated once
		this.AGENTS_GOALS = new LinkedList<String>();
		this.graph = null;
		this.heap = null;
	}

	
	public void run() {
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
		Graph newGraph = perceiveGraphBlocking(TIME_WAITING_GRAPH);
		
		if (newGraph != null) {
			this.graph = newGraph;
			this.needsToRecalculateHeap = true;			
		} else {			
			throw new RuntimeException("Initial graph perception not received!!!");			
		}
		
	}


	private void waitGraphPerception() throws AgentStoppedException {
		Graph newGraph = perceiveGraphBlocking(5);
		
		if (newGraph != null) {
			printDebug("Graph perceived.");
			this.graph = newGraph;
			this.needsToRecalculateHeap = true;
		}		
	}

	private void attendRequests() throws AgentStoppedException {
		if (!hasNewBroadcasts()) {
			return;
		}
		
		this.retrieveBroadcasts(receivedBroadcasts);

		String answerMessage = "";
		
		for (int i = 0; i < receivedBroadcasts.size(); i++) {
			String message = this.receivedBroadcasts.get(i);
			//printDebug("Received: " + message);
			
			int markPosition = message.indexOf("###");
			
			String agentId       = message.substring(0, markPosition);
			String agentCurrNode = message.substring(markPosition + 3);
			String agentNextNode;

			if (needsToRecalculateHeap) {
				recalculateHeap();
				needsToRecalculateHeap = false;
			}				
			
			agentNextNode = selectGoalNode(agentId, agentCurrNode);

			//printDebug("Coordinator will send " + agentId + " to " + agentNextNode);
			answerMessage += agentId + "###" + agentNextNode + "###";
		}
		
		if (!answerMessage.equals("")) {
			printDebug("Coordinator sending: " + answerMessage);
			this.actSendBroadcast(answerMessage);
		}

	}
	
	private String selectGoalNode(String agentId, String agentCurrentNode) {
		String nodeId = ((ComparableNode) heap.removeSmallest()).theNode.getObjectId();
		
		while ((nodeId.equals(agentCurrentNode) || this.AGENTS_GOALS.contains(nodeId))
				&& !heap.isEmpty()) {
			nodeId = ((ComparableNode) heap.removeSmallest()).theNode.getObjectId();
		}

		// updates the agents and nodes memory
		int agentIndex = this.AGENTS_GOALS.indexOf(agentId);
		if (agentIndex > -1)
			this.AGENTS_GOALS.set(agentIndex + 1, nodeId);
		else {
			this.AGENTS_GOALS.add(agentId);
			this.AGENTS_GOALS.add(nodeId);
		}

		return nodeId;
	}
	
	private void recalculateHeap() {
		Node[] nodes = this.graph.getNodees();
		ComparableNode[] comparableNodes = new ComparableNode[nodes.length];
		
		// for debug
		for (Node n : nodes) {
			printDebug(n.getObjectId() + ": " + n.getIdleness());
		}
		
		for (int i = 0; i < comparableNodes.length; i++) {
			comparableNodes[i] = new ComparableNode(nodes[i]);
		}

		this.heap = new MinimumHeap(comparableNodes);
	}


	/**
	 * Auxiliary class.
	 */
	private class ComparableNode implements Comparable {
		public final Node theNode;

		public ComparableNode(Node node) {
			this.theNode = node;
		}

		public boolean isSmallerThan(Comparable object) {
			if (object instanceof ComparableNode)
				if (this.theNode.getIdleness() > ((ComparableNode) object).theNode.getIdleness())
					return true;
			return false;
		}
	}
	
}
