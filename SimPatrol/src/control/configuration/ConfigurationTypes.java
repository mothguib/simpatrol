/* ConfigurationTypes.java */

/* The package of this class. */
package control.configuration;

/**
 * Holds the types of configurations of a simulation.
 * 
 * @developer New configurations must be added here.
 */
public abstract class ConfigurationTypes {
	/* Attributes. */
	/**
	 * Configurations to create the graph of the simulation.
	 * 
	 * @see EnvironmentCreationConfiguration
	 */
	public static final int ENVIRONMENT_CREATION = 0;

	/**
	 * Configurations to add an agent to the simulation.
	 * 
	 * @see AgentCreationConfiguration
	 */
	public static final int AGENT_CREATION = 1;

	/**
	 * Configurations to create metrics of the simulation.
	 * 
	 * @see MetricCreationConfiguration
	 */
	public static final int METRIC_CREATION = 2;

	/**
	 * Configurations to start the simulation.
	 * 
	 * @see SimulationStartConfiguration
	 */
	public static final int SIMULATION_START = 3;

	/**
	 * Configurations to kill seasonal agents.
	 * 
	 * @see AgentCreationConfiguration
	 */
	public static final int AGENT_DEATH = 4;

	/**
	 * Configurations to collect events from the simulation through UDP sockets.
	 * 
	 * @see EventsCollectingConfiguration
	 */
	public static final int EVENT_COLLECTING = 5;
}