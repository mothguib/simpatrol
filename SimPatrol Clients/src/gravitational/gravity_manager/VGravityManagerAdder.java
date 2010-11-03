package gravitational.gravity_manager;

import util.graph2.Graph;


public class VGravityManagerAdder extends VGravityManager {

	public VGravityManagerAdder(Graph graph, double exponent) {
		super(graph, exponent);
	}

	@Override
	public void applyGravity(int origin, double originMass) {
		assert (originMass >= 0.0d);
		assert (masses[origin] == -1.0d);
		
		for (int destiny = 0; destiny < masses.length; destiny++) {
			// se destiny == origin, a gravidade é adicionada do valor da massa (ok)
			vertexGravities[destiny] += originMass * propagationFactor[destiny][origin];
		}
		
		masses[origin] = originMass;
	}

	@Override
	public void undoGravity(int origin) {
		assert (masses[origin] >= 0.0d);
		
		for (int destiny = 0; destiny < masses.length; destiny++) {
			vertexGravities[destiny] -= masses[origin] * propagationFactor[destiny][origin];
		}
		
		masses[origin] = -1.0d;
	}

}
