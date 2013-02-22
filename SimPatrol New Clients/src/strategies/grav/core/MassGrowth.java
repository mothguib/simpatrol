package strategies.grav.core;


public enum MassGrowth {
	
	ARITHMETIC ("Ar") {
		@Override
		public double getVertexMass(double baseMass, double idleness, double priority) {
			return baseMass * idleness * priority; 
		}
	},
	
	GEOMETRIC  ("Ge") {
		@Override
		public double getVertexMass(double baseMass, double idleness, double priority) {
			if (idleness == 0) return 0.0d;
			return baseMass * Math.pow(1.0d + GROWTH_RATE, priority * (idleness - 1));
		}
	},
	
	NO_GROWTH  ("No") {
		@Override
		public double getVertexMass(double baseMass, double idleness, double priority) {
			return baseMass;
		}
	};
	
	
	private static double GROWTH_RATE = 0.05d;
	private String identifier; 
	
	
	private MassGrowth(String s) {
		identifier = s;
	}
	
	public String toString() {
		return identifier;
	}
	
	public double getVertexMass(double baseMass, double idleness) {
		return getVertexMass(baseMass, idleness, 1.0d);
	}
	
	public abstract double getVertexMass(double baseMass, double idleness, double priority);

}
