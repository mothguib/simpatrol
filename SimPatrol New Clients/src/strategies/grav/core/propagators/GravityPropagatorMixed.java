package strategies.grav.core.propagators;

import strategies.grav.core.GravityPropagator;
import util.graph2.Graph;


/**
 * Propagates through the edges (arcs) the attraction forces that are produced by the masses.
 * However, it is more complex than the "Edge" propagation because it forces all nodes to
 * contribute in the force of each edge (somewhat similar to the propagation "Node"). 
 * <br><br>
 * For each edge x->y, all nodes z (differente from x) produces an attraction force. The 
 * distance is computed as the addition of the length of x->y and the distance of the 
 * shortest path y->...->z.  
 * <br><br>
 * As in the other types of propagation, all forces in direction x->y are combined using 
 * one of the forces combinators (e.g. max or sum), which are implemented in subclasses of 
 * this class. 
 * 
 * @see GravityPropagator 
 * @author Pablo A. Sampaio
 */
public abstract class GravityPropagatorMixed extends GravityPropagator {
	
	protected double[][] gravities;

	
	GravityPropagatorMixed(Graph g, double exponent) {
		super(g, exponent);
		
		int numVertices = g.getNumVertices();
		gravities = new double[numVertices][numVertices];
	}

	public double getGravity(int attracted, int neighbor) {
		return gravities[attracted][neighbor];
	}
	
	
}
