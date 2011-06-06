package gravitational;


public enum MassGrowth {
	
	ARITHMETIC ("A") {
		@Override
		public double getVertexMass(double baseMass, double idleness) {
			return baseMass * idleness;
		}
	},
	
	GEOMETRIC  ("G") {
		@Override
		public double getVertexMass(double baseMass, double idleness) {
			return geometricGrowth(baseMass, GROWTH_RATE, idleness - 1);
		}
	},
	
	NO_GROWTH  ("N") {
		@Override
		public double getVertexMass(double baseMass, double idleness) {
			return baseMass;
		}
	};
	
	
	private static double GROWTH_RATE = 0.05d;
	private String identifier; 
	
	
	private MassGrowth(String s) {
		identifier = s;
	}
	
	public String getIdentifier() {
		return identifier;
	}
	
	public abstract double getVertexMass(double baseMass, double idleness);
	
	private static double geometricGrowth(double startMass, double growthRate, double idleness) {
		return startMass * Math.pow(1.0d + growthRate, idleness);
	}

}
