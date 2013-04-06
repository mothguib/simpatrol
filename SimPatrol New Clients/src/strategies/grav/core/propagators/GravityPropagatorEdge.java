package strategies.grav.core.propagators;

import strategies.grav.core.GravityPropagator;
import util.graph2.Graph;


/**
 * Propagates the attraction forces (gravities) through the edges (arcs) of the graph.
 * <br><br>
 * The attraction force produced in x by a node z, actuates through the edge x->y which is
 * the first edge of the shortest path x->y...->z. (Thus, it attracts an agent placed in
 * node x to the node y, although it was produced by node z).
 * <br><br>
 * All forces actuating in direction x->y are combined using one of the forces combinators 
 * (e.g. max or sum), which are implemented in subclasses of this class. 
 * 
 * @see GravityPropagator 
 * @author Pablo A. Sampaio
 */
public abstract class GravityPropagatorEdge extends GravityPropagator {
	
	protected double[][] gravities;

	
	GravityPropagatorEdge(Graph g, double exponent) {
		super(g, exponent);
		
		int numVertices = g.getNumVertices();
		gravities = new double[numVertices][numVertices];
	}

	public double getGravity(int attracted, int neighbor) {
		return gravities[attracted][neighbor];
	}
	
	
}
