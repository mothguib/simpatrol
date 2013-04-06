package strategies.grav.core.propagators;

import util.graph2.Edge;
import util.graph2.Graph;


/**
 * In this implementation, the resulting force in a specific edge x->y (in this specific
 * direction) is calculated as the SUM of all forces produced in x->y.
 * 
 * @see GravityPropagatorMixed
 * @author Pablo A. Sampaio
 */
public class GravityPropagatorMixedSum extends GravityPropagatorMixed {
	
	public GravityPropagatorMixedSum(Graph graph, double exponent) {
		super(graph, exponent);
	}

	@Override
	public void applyGravities(int attractor, double attractorMass) {
		assert (attractorMass >= 0.0d);
		assert (masses[attractor] == -1.0d);

		applyGravitiesInternal(attractor, attractorMass);
		
		masses[attractor] = attractorMass;
	}
	
	@Override
	public void undoGravities(int attractor) {
		assert (masses[attractor] >= 0.0d);

		applyGravitiesInternal(attractor, -masses[attractor]);
		masses[attractor] = -1.0d;
	}
	
	private void applyGravitiesInternal(int attractor, double attractorMass) {
		int numVertices = gravities.length;

		for (int node = 0; node < numVertices; node++) {
			if (node == attractor) {
				continue;
			}
			for (Edge outEdge : super.graph.getOutEdges(node)) {
				int intermedNode = outEdge.getTargetIndex(); 
				double distIntermedAttractor = super.shortestPaths.getDistance(intermedNode, attractor);
				gravities[node][intermedNode] += attractorMass / Math.pow(outEdge.getLength()+distIntermedAttractor, distanceExponent); 
			}
		}
	}
	
	@Override
	public void undoAllGravities() { 	
		// specialized implementation
		for (int attractor = 0; attractor < masses.length; attractor++) {
			masses[attractor] = -1.0d;
			for (int dest = 0; dest < masses.length; dest ++) {
				gravities[attractor][dest] = 0.0d;
			}
		}
	}

}
