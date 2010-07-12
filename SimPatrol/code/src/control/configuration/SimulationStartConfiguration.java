/* SimulationStartConfiguration.java */

/* The package of this class. */
package control.configuration;

/**
 * Implements objects that express configurations to start a simulation with a
 * specific time of duration.
 */
public final class SimulationStartConfiguration extends Configuration {
	/* Attributes. */
	/** The time of simulation. */
	private final double SIMULATION_TIME;

	/* Methods. */
	/**
	 * Constructor.
	 * 
	 * @param simulation_time
	 *            The time of simulation.
	 */
	public SimulationStartConfiguration(double simulation_time) {
		this.SIMULATION_TIME = simulation_time;
	}

	/**
	 * Returns the time of simulation.
	 * 
	 * @return The time of simulation.
	 */
	public double getSimulation_time() {
		return this.SIMULATION_TIME;
	}

	public String fullToXML(int identation) {
		// holds the answer to the method
		StringBuffer buffer = new StringBuffer();

		// applies the identation and fills the "configuration" tag
		for (int i = 0; i < identation; i++)
			buffer.append("/t");
		buffer.append("<configuration type=\""
				+ ConfigurationTypes.SIMULATION_START + "\" parameter=\""
				+ this.SIMULATION_TIME + "\"/>\n");

		// return the answer to the method
		return buffer.toString();
	}
}