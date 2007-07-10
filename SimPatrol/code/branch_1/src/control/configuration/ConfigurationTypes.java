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
}