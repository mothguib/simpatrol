package gravitational.version2;

import gravitational.GravitiesCombinator;
import gravitational.MassGrowth;
import gravitational.gravity_manager.GravityManager;

import java.util.LinkedList;
import java.util.List;

import util.graph2.Graph;


class GravManager {
	private boolean propagateByEdge;
	private MassGrowth massGrowth;
	private double distanceExponent;
	private GravitiesCombinator gravityCombinator;
	
	private Graph graph;
	private GravityManager gravityManager;
	
	private List<String>[] visitsScheduledPerNode; //for each node, keeps a list of agents' identifiers
	
	
	GravManager(boolean edgePropagated, MassGrowth mgrowth, double exponent, GravitiesCombinator gcomb) {
		propagateByEdge = edgePropagated;
		massGrowth = mgrowth;
		distanceExponent = exponent;
		gravityCombinator = gcomb;
	}

	
	void setGraph(Graph g) {
		if (this.graph == null) {
			this.graph = g;
			setupGravityManager();
		} else {
			this.graph = g;
		}
	}
	
	@SuppressWarnings("unchecked")
	private void setupGravityManager() {
		if (propagateByEdge) {
			gravityManager = gravityCombinator.createGravityManager(graph, distanceExponent);
		} else {
			gravityManager = gravityCombinator.createVGravityManager(graph, distanceExponent);
		}

		visitsScheduledPerNode = new List[graph.getNumVertices()];
		for (int i = 0; i < visitsScheduledPerNode.length; i++) {
			visitsScheduledPerNode[i] = new LinkedList<String>();
		}
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
	
	void setNextNode(String agentId, String lastNodeId, String nextNodeId) {
		int lastNodeIndex = graph.getNode(lastNodeId).getIndex();
		int nextNodeIndex = graph.getNode(nextNodeId).getIndex();
		
		visitsScheduledPerNode[lastNodeIndex].remove(agentId);
		visitsScheduledPerNode[nextNodeIndex].add(agentId);
	}
	
	String getStrongestNeighbor(String currNodeId) {
		if (graph == null) {
			return null;
		}
		
		recalculateGravities();
		
		int currNode = graph.getNode(currNodeId).getIndex();

		int goalNode = currNode;
		double goalGravity = -1.0d;
		
		// chooses the neighbor with the highest gravity 
		for (Integer neighbor : graph.getSuccessors(currNode)) {
			if (gravityManager.getGravity(currNode,neighbor) > goalGravity) {
				goalNode = neighbor;
				goalGravity = gravityManager.getGravity(currNode,neighbor);
			}
		}
		
		return graph.getNode(goalNode).getIdentifier();
	}
	
}
