package strategies.grav.core.propagators;

import util.graph2.Graph;


/** 
 * The "combined mass" holds the sum of the values.
 * 
 * @see GravityPropagatorNode
 * @author Pablo A. Sampaio
 */
public class GravityPropagatorNodeSum extends GravityPropagatorNode {

	public GravityPropagatorNodeSum(Graph graph, double exponent, boolean useDist) {
		super(graph, exponent, useDist);
	}

	@Override
	public void applyGravities(int attractor, double attractorMass) {
		assert (attractorMass >= 0.0d);
		assert (masses[attractor] == -1.0d);
		
		for (int attracted = 0; attracted < masses.length; attracted++) {
			// obs.: se destiny == origin, a gravidade é adicionada do valor da massa (ok)
			combinedMasses[attracted] += attractorMass * propagationFactor[attracted][attractor];
		}
		
		masses[attractor] = attractorMass;
	}

	@Override
	public void undoGravities(int attractor) {
		assert (masses[attractor] >= 0.0d);
		
		for (int attracted = 0; attracted < masses.length; attracted++) {
			combinedMasses[attracted] -= masses[attractor] * propagationFactor[attracted][attractor];
		}
		
		masses[attractor] = -1.0d;
	}


	public String toString() {
		StringBuilder builder = new StringBuilder();

		builder.append("[ ");
		for (int i = 0; i < combinedMasses.length; i++) {
			builder.append(graph.getNode(i).getIdentifier().toUpperCase());
			builder.append(": ");
			builder.append(combinedMasses[i]);
			builder.append(", ");
		}
		builder.append("]\n");
		
		builder.append(super.toString());

		return builder.toString();
	}
	
}
