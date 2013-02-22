package strategies.grav.core.propagators;

import strategies.grav.core.GravityPropagator;
import util.graph2.Graph;


/**
 * In each node, it is calculated a kind of "combined mass" with its own mass and the 
 * gravities (or masses) propagated from all the other nodes. The gravity in a specific
 * direction of an edge is this "combined mass" divided by the edge's length.
 * 
 * @see GravityPropagator 
 * @author Pablo A. Sampaio
 */		
public abstract class GravityPropagatorNode extends GravityPropagator {
	protected double[] combinedMasses;  //for each node
	protected boolean useDistance;

	
	GravityPropagatorNode(Graph graph, double exponent, boolean divideByDistance) {
		super(graph, exponent);
		
		for (int v = 0; v < graph.getNumVertices(); v++) {
			this.propagationFactor[v][v] = 1.0d; //so that the mass of the own node mass is also  
			                                     //used in the calculation of its "combined mass" 
		}

		int numVertices = graph.getNumVertices();
		combinedMasses = new double[numVertices];
		
		this.useDistance = divideByDistance;
	}
	
	/**
	 * Retorna a força da gravidade (combinada) que atrai agentes posicionados 
	 * em "start" para seguirem para o vértice "neighbor". 
	 */
	public double getGravity(int start, int neighbor) {
		if (!graph.existsEdge(start, neighbor)) {
			return 0.0d;
		}
		if (useDistance) {
			return combinedMasses[neighbor] / super.graph.getLength(start, neighbor);
		} else {
			return combinedMasses[neighbor];
		}
	}
	
}
