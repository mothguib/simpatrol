package strategies.cc;

import java.util.LinkedList;

import util.graph.Node;
import util.heap.Comparable;
import util.heap.MinimumHeap;
import agent_library.basic_agents.AgentStoppedException;
import agent_library.connections.ClientConnection;
import agent_library.coordinated_agents.CoordinatorAgent;


/**
 * The coordinator agent of the "Cognitive Coordinated" (CC) strategy (Machado et al.,2002).
 * <br><br>
 * Observation: The coordinated agents are the generic ones provided by the library, in the package "agent_library.coordinated_agents". 
 */
public class CcCoordinatorAgent extends CoordinatorAgent {
	private MinimumHeap heap;  //organizes the nodes based on their idlenesses
	
	private final LinkedList<String> agentsGoals;  //holds in the ith position the id of the agent, and 
	                                               //in the (i+1)th position the id of the node to be visited
	

	public CcCoordinatorAgent(String id, ClientConnection conn) {
		super(id, conn, false);
		this.agentsGoals = new LinkedList<String>();
		this.heap = null;
	}


	@Override
	protected String selectGoalNode(String agentId, String agentCurrentNode) {
		String nodeId = ((ComparableNode) heap.removeSmallest()).theNode.getObjectId();
		
		while ((nodeId.equals(agentCurrentNode) || this.agentsGoals.contains(nodeId))
				&& !heap.isEmpty()) {
			nodeId = ((ComparableNode) heap.removeSmallest()).theNode.getObjectId();
		}

		// updates the agents and nodes memory
		int agentIndex = this.agentsGoals.indexOf(agentId);
		if (agentIndex > -1) {
			this.agentsGoals.set(agentIndex + 1, nodeId);
		} else {
			this.agentsGoals.add(agentId);
			this.agentsGoals.add(nodeId);
		}

		return nodeId;
	}

	
	@Override
	protected void onNewGraphPerceptionAndNewRequests() throws AgentStoppedException {
		Node[] nodes = super.perceiveGraph().getNodees();
		ComparableNode[] comparableNodes = new ComparableNode[nodes.length];
		
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
			if (object instanceof ComparableNode) {
				if (this.theNode.getIdleness() > ((ComparableNode) object).theNode.getIdleness()) {
					return true;
				}
			}
			return false;
		}
	}


}
