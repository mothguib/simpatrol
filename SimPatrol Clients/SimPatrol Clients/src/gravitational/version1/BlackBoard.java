package gravitational.version1;

import gravitational.GravitiesCombinator;
import gravitational.MassGrowth;
import gravitational.gravity_manager.GravityManager;

import java.util.LinkedList;
import java.util.List;

import util.graph2.Graph;


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