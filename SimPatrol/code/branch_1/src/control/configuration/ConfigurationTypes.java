/* ConfigurationTypes.java */

/* The package of this class. */
package control.configuration;

/** Holds the types of configurations of a simulation.
 * 
 *  @developer New configurations must be added here. */
public abstract class ConfigurationTypes {
	/* Attributes. */
	/** Configurations to create the graph of the simulation. */
	public static final int GRAPH_CREATION = 0;
	
	/** Configurations to create the societies of the simulation. */
	public static final int SOCIETIES_CREATION = 1;
	
	/** Configurations to add an agent to the simulation. */
	public static final int AGENT_CREATION = 2;
	
	/** Configurations to start the simulation. */
	public static final int SIMULATION_START = 3;
	
	/* Methods. */
	/** Returns a string containing the types of configurations.
	 * 
	 *  @developer New configurations must change this method. */
	public static final String getResume() {
		StringBuffer buffer = new StringBuffer();
		buffer.append("Configuration types:\n");
		buffer.append("value   description          body                    and/or   parameter\n");
		buffer.append("  0     GRAPH CREATION       Graph object            OR       string path\n");
		buffer.append("  1     SOCIETIES CREATION   Society objects (1..n)  OR       string path\n");
		buffer.append("  2     AGENT CREATION       Agent object            AND      ignored\n");
		buffer.append("  3     SIMULATION START     ignored                 AND      simulation time\n");
		
		return buffer.toString();
	}
}