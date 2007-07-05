/* ConfigurationTypes.java */

/* The package of this class. */
package control.configuration;

/** Holds the types of configurations to a simulation. */
public abstract class ConfigurationTypes {
	/** Configurations to create the environment of the simulation. */
	public static final int ENVIRONMENT_CREATION = 0;
	
	/** Configurations to add an agent to the simulation. */
	public static final int AGENT_CREATION = 1;
	
	/** Configurations to start the simulation. */
	public static final int SIMULATION_CONFIGURATION = 2;
}
