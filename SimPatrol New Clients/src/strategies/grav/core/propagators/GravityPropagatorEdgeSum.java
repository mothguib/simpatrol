package strategies.grav.core.propagators;

import util.graph2.Graph;


/**
 * Com esta classe, a gravidade atuando sobre uma direção específica de uma aresta é dada 
 * pela SOMA das gravidades presentes naquela direção da aresta.
 * 
 * @see GravityPropagatorEdge
 * @author Pablo A. Sampaio
 */
public class GravityPropagatorEdgeSum extends GravityPropagatorEdge {
	
	public GravityPropagatorEdgeSum(Graph graph, double exponent) {
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

		int nextFromAttracted;

		for (int attracted = 0; attracted < numVertices; attracted++) {
			if (attracted != attractor) {
				nextFromAttracted = shortestPaths.getSourceSuccessor(attracted, attractor);
				gravities[attracted][nextFromAttracted] += attractorMass * propagationFactor[attracted][attractor]; 
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
