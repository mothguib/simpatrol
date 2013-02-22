package strategies.hpcc;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;

import util.graph.Edge;
import util.graph.Graph;
import util.graph.Node;
import util.heap.Comparable;
import util.heap.MinimumHeap;
import agent_library.basic_agents.AgentStoppedException;
import agent_library.connections.ClientConnection;
import agent_library.coordinated_agents.CoordinatorAgent;


/**
 * The coordinator agent of the "Heuristic Pathfinder Cognitive Coordinated" (HPCC) strategy (Almeida et al.,2003).
 * <br><br>
 * Observation: The coordinated agents are the generic ones provided by the library, in the package "agent_library.coordinated_agents". 
 *
 * @author Pablo A. Sampaio
 */
public class HpccCoordinatorAgent extends CoordinatorAgent {
	private static final double IDLENESSES_RATE = 0.2;

	private Map<String,Integer> nodeIndexMap;  //associates an integer index to each node
	
	private double[][] distancesTable;         //minimum distances between all nodes	
	
	private LinkedList<String> agentsGoals;    //holds in the ith position the id of the agent, and 
                                               //in the (i+1)th position the id of the node to be visited
	
	private Map<String,Queue<String>> agentsPlans; //holds a sequence of nodes for each agent
	

	public HpccCoordinatorAgent(String id, ClientConnection conn) {
		super(id, conn, false);
		this.agentsGoals = new LinkedList<String>();
		this.agentsPlans = new HashMap<String,Queue<String>>();
	}

	@Override
	protected void onFirstGraphPerception() throws AgentStoppedException {
		Node[] nodes = super.perceiveGraph().getNodees();
		
		distancesTable = new double[nodes.length][nodes.length];
		nodeIndexMap = new HashMap<String, Integer>();
		
		for(int i = 0; i < nodes.length; i++){
			nodeIndexMap.put(nodes[i].getObjectId(), i);
		}
		
		for (int i = 0; i < nodes.length; i++) {
			for (int k=0; k < nodes.length; k++) {
				if (i != k) distancesTable[i][k] = Double.POSITIVE_INFINITY;
				else distancesTable[i][k] = 0;
			}
		}
		
		// set the edge lengths as initial distances
		for (int i=0; i < nodes.length; i++){
			Edge[] edges = nodes[i].getEdges();
			for (Edge edge: edges){
				Node otherNode = edge.getOtherNode(nodes[i]);
				int otherNodeIndex = nodeIndexMap.get(otherNode.getObjectId());
				distancesTable[i][otherNodeIndex] = edge.getLength();
			}
		}

		// computes distances with Floyd-Warshall algorithm
		for (int i = 0; i < nodes.length; i++) {
			for (int j = 0; j < nodes.length; j++) {
				for (int k = 0; k < nodes.length; k++) {
					if ( distancesTable[j][k] > (distancesTable[j][i] + distancesTable[i][k]) ) {
						distancesTable[j][k] = distancesTable[j][i] + distancesTable[i][k];
					}					
				}
			}
		}
		
	}

	@Override
	protected String selectGoalNode(String agentId, String agentCurrNode) throws AgentStoppedException {
		Queue<String> plan;
		
		if (this.agentsPlans.containsKey(agentId)) {
			//printDebug("Choosing from " + agentId + "'s plan...");
			plan = this.agentsPlans.get(agentId);
		} else {
			//printDebug("Creating plan for " + agentId);
			plan = this.createAgentPlan(agentId, agentCurrNode);
			this.agentsPlans.put(agentId, plan);
		}
		
		String nextNode = plan.poll();
		
		if (plan.isEmpty()) {
			this.agentsPlans.remove(agentId);
		}
		
		//printDebug("Chosen node: " + nextNode);		
		return nextNode;
	}
	
	private Queue<String> createAgentPlan(String agentId, String agentCurrNode) throws AgentStoppedException {
		int currentNodeIndex = this.nodeIndexMap.get(agentCurrNode);
		
		Graph graph = super.perceiveGraph();

		// 1. Mounts a heap with the nodes, based on their idlenesses and their distances to the reference position
		Node[] nodes = graph.getNodees();
		
		double[] boundIdlenesses = graph.getSmallestAndBiggestIdlenesses();
		double[] boundDistances  = graph.getSmallestAndBiggestDistances();
		
		ComparableNode[] comparableNodes = new ComparableNode[nodes.length];
		double nodeValue;
		
		for (int i = 0; i < nodes.length; i++) {
			nodeValue = this.calculateNodeValue(nodes[i], currentNodeIndex, boundIdlenesses, boundDistances);
			comparableNodes[i] = new ComparableNode(nodes[i], nodeValue);
		}
			
		MinimumHeap heap = new MinimumHeap(comparableNodes);

		// 2. Chooses the goal node to be visited
		String goalNode = ((ComparableNode) heap.removeSmallest()).nodeId;
		while ((goalNode.equals(agentCurrNode) || this.agentsGoals.contains(goalNode))
		        && !heap.isEmpty()) {
			goalNode = ((ComparableNode) heap.removeSmallest()).nodeId;
		}

		int agentIndex = this.agentsGoals.indexOf(agentId);
		if (agentIndex > -1) {
			this.agentsGoals.set(agentIndex + 1, goalNode);
		} else {
			this.agentsGoals.add(agentId);
			this.agentsGoals.add(goalNode);
		}
		
		// 3. Calculates the path from current node to the chosen node
		Queue<String> plan = this.findPathSpecial(graph, agentCurrNode, goalNode);
		
		//printDebug("Plan: " + plan);
		return plan;                
	}

	
	private Queue<String> findPathSpecial(Graph graph, String agentCurrNode, String goalNode) {
		LinkedList<String> path = new LinkedList<String>();
		
		Node beginNode = new Node(""); beginNode.setObjectId(agentCurrNode);
		Node endNode = new Node(""); endNode.setObjectId(goalNode);

		Graph pathGraph = graph.getIdlenessedDijkstraPath(beginNode, endNode);

		Node[] pathNodes = pathGraph.getNodees();
		for (int i = 0; i < pathNodes.length; i++) {
			if (pathNodes[i].equals(beginNode)) {
				beginNode = pathNodes[i];
				break;
			}
		}

		if (beginNode.getEdges().length > 0) {
			Node currentNode = beginNode.getEdges()[0].getOtherNode(beginNode);
			path.add(currentNode.getObjectId());

			Edge[] currentNodeEdges = currentNode.getEdges();
			while (currentNodeEdges.length > 1) {
				Node nextNode = currentNodeEdges[0].getOtherNode(currentNode);

				if (path.contains(nextNode.getObjectId()) || nextNode.equals(beginNode)) {
					currentNode = currentNodeEdges[1].getOtherNode(currentNode);
				} else {
					currentNode = nextNode;
				}

				path.add(currentNode.getObjectId());
				currentNodeEdges = currentNode.getEdges();
			}
		}

		return path;
	}

	/**
	 * Evaluates a node based not only on its idlenesses, but also on its distance to a given reference node.
	 */
	private double calculateNodeValue(Node node, int originIndex, double[] boundIdlenesses, double[] boundDistances) {
		int nodeIndex   = this.nodeIndexMap.get(node.getObjectId());
		
		//the higher the idleness, the higher is the normalized value
		double normIdleness = (node.getIdleness() - boundIdlenesses[0]) / (boundIdlenesses[1] - boundIdlenesses[0]);

		double distanceFromReference = this.distancesTable[originIndex][nodeIndex];

		//the lower the distance, the higher is the normalized value
		double normalizedDistance = (boundDistances[1] - distanceFromReference) / (boundDistances[1] - boundDistances[0]);

		return IDLENESSES_RATE*normIdleness + (1.0d - IDLENESSES_RATE)*normalizedDistance;
	}

	@Override
	protected void onNewGraphPerceptionAndNewRequests() {
		//does nothing (computations are agent's specific and, therefore, are done in "createAgentPlan()")
	}

	
	/**
	 * Auxiliary class. 
	 */
	private class ComparableNode implements Comparable {
		final String nodeId;
		final double value;
		
		public ComparableNode(Node node, double nodeValue) {
			this.nodeId = node.getObjectId();
			this.value = nodeValue;
		}
		
		public boolean isSmallerThan(Comparable object) {
			if (object instanceof ComparableNode) {
				ComparableNode otherNode = ((ComparableNode) object);		
				return (this.value > otherNode.value);
			}
			return false;
		}		
	}
	
}

