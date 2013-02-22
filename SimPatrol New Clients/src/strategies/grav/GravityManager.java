package strategies.grav;


import java.util.LinkedList;
import java.util.List;

import strategies.grav.core.ForceCombination;
import strategies.grav.core.ForcePropagation;
import strategies.grav.core.GravityPropagator;
import strategies.grav.core.MassGrowth;
import util.graph2.Graph;


/**
 * Class used by the coordinator agent to choose agents' next nodes based on the gravities.
 * 
 * @author Pablo A. Sampaio
 */
public class GravityManager {
	private ForcePropagation propagation;
	private MassGrowth massGrowth;
	private double exponent;
	private ForceCombination forceCombination;
	
	private Graph graph;
	private GravityPropagator gravities;
	
	private List<String>[] visitsScheduledPerNode; //for each node, holds a list of agents' identifiers
	
	
	public GravityManager(ForcePropagation propagation, MassGrowth growth, double distExponent, ForceCombination combination) {
		this.propagation = propagation;
		this.massGrowth = growth;
		this.exponent = distExponent;
		this.forceCombination = combination;
	}
	
	@SuppressWarnings("unchecked")
	public void setup(Graph g) {
		this.graph = g;
		
		gravities = GravityPropagator.create(propagation, exponent, forceCombination, graph);		

		visitsScheduledPerNode = new List[graph.getNumVertices()];
		for (int i = 0; i < visitsScheduledPerNode.length; i++) {
			visitsScheduledPerNode[i] = new LinkedList<String>();
		}

		calculateGravities();
	}
	
	// should receive a graph with the same nodes and same indexes
	// for each node as the current graph
	public void update(Graph g) {
		this.graph = g;
		calculateGravities();
	}
	
	private void calculateGravities() {
		int numVertices = graph.getNumVertices();

		gravities.undoAllGravities();

		double idleness;
		double nodeMass;
		//double nodePriority;
		
		printDebug("Recalculating gravities...");
		
		// for each node, applies its gravity to attract agents
		for (int node = 0; node < numVertices; node++) {
			if (visitsScheduledPerNode[node].size() > 0) {
				nodeMass = 0.0d;
			} else {	
				idleness = graph.getNode(node).getIdleness();
				//nodePriority   = graph.getNode(node).getPriority();
				nodeMass = massGrowth.getVertexMass(1.0d, idleness);
			}
			
			gravities.applyGravities(node, nodeMass);
		}
		
	}
	
	/*synchronized*/ String selectGoalNode(String agentId, String currNodeId) {
		if (this.graph == null) {
			printDebug("Grafo nulo!");
			return null;
		}
		//printDebug(agentId + " selecting from " + currNodeId);
		int currNodeIndex = graph.getNode(currNodeId).getIndex();
		
		int goalNodeIndex = currNodeIndex;
		double goalGravity = -1.0d;
		
		// chooses the neighbor with highest gravity 
		for (Integer neighbor : graph.getSuccessors(currNodeIndex)) {
			if (gravities.getGravity(currNodeIndex,neighbor) > goalGravity) {
				goalNodeIndex = neighbor;
				goalGravity = gravities.getGravity(currNodeIndex,neighbor);
			}
		}
		
		visitsScheduledPerNode[currNodeIndex].remove(agentId);
		visitsScheduledPerNode[goalNodeIndex].add(agentId); 

		//printDebug("Gravities (before undo): \n" + gravities);
		//printDebug("Agent will go to: " + graph.getNode(goalNodeIndex));
		
		// if it is the only agent going to the node, undo the gravity (mass is zeroed)
		if (visitsScheduledPerNode[goalNodeIndex].size() == 1) {
			gravities.undoGravities(goalNodeIndex);
			//printDebug("Gravities (after undo): \n" + gravities);
		}
		
		return graph.getNode(goalNodeIndex).getIdentifier();
	}
	
	public String toString() {
		return "" + propagation + "-" + massGrowth + "-" + exponent + "-" + forceCombination;
	}
	
	private void printDebug(String message) {
		System.out.println("GRAV-MANAGER: " + message);
	}
	
}