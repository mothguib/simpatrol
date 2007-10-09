/* SimulationStartConfiguration.java */

/* The package of this class. */
package control.configuration;

/** Implements objects that express configurations to start
 *  a simulation with a specific time of duration. */
public final class SimulationStartConfiguration extends Configuration {
	/* Attributes. */
	/** The time of simulation. */
	private int simulation_time;
	
	/* Methods. */
	/** Constructor.
	 * 
	 *  @param simulation_time The time of simulation. */	
	public SimulationStartConfiguration(int simulation_time) {
		super();
		this.simulation_time = simulation_time;
	}
	
	/** Returns the time of simulation.
	 * 
	 *  @return The time of simulation. */
	public int getSimulation_time() {
		return this.simulation_time;
	}
	
	@Override
	protected int getType() {
		return ConfigurationTypes.SIMULATION_START; 
	}
	
	public String fullToXML(int identation) {
		// holds the answer to the method
		StringBuffer buffer = new StringBuffer();
		
		// applies the identation and fills the "configuration" tag
		for(int i = 0; i < identation; i++) buffer.append("/t");
		buffer.append("<configuration type=\"" + ConfigurationTypes.SIMULATION_START +
				      "\" parameter=\"" + this.simulation_time +
				      "\"/>\n");
		
		// return the answer to the method
		return buffer.toString();
	}
}