/* Simulator.java */

/* The package of this class. */
package control.simulator;

/* Imported classes and/or interfaces. */
import java.io.IOException;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import util.time.TimeSensible;
import model.Environment;
import model.agent.Agent;
import model.agent.OpenSociety;
import model.agent.Society;
import model.interfaces.Dynamic;
import model.interfaces.Mortal;
import model.limitation.Limitation;
import model.limitation.StaminaLimitation;
import model.metric.Metric;
import model.permission.ActionPermission;
import model.permission.PerceptionPermission;
import control.daemon.ActionDaemon;
import control.daemon.Daemon;
import control.daemon.MainDaemon;
import control.daemon.MetricDaemon;
import control.daemon.PerceptionDaemon;
import control.event.Logger;

/**
 * Implements the simulator of SimPatrol.
 * 
 * @developer New dynamic classes must change this class.
 * @developer New mortal classes must change this class.
 */
public abstract class Simulator implements TimeSensible {
	/* Attributes. */
	/** The main daemon of SimPatrol. */
	private final MainDaemon MAIN_DAEMON;

	/** The set of perception daemons of SimPatrol. */
	protected final Set<PerceptionDaemon> PERCEPTION_DAEMONS;

	/** The set of action daemons of SimPatrol. */
	protected final Set<ActionDaemon> ACTION_DAEMONS;

	/** The set of metric daemons of SimPatrol. */
	private final Set<MetricDaemon> METRIC_DAEMONS;

	/** The environment (graph + societies) of the simulation. */
	protected Environment environment;

	/**
	 * Holds the current state of the simulator.
	 * 
	 * @see SimulatorStates
	 */
	private int state;

	/**
	 * Defines, in seconds, the time rate to produce the agents' perceptions and
	 * to attend the eventual atomic actions that compound an eventual compound
	 * action.
	 */
	private final double UPDATE_TIME_RATE;

	/* Methods. */
	/**
	 * Constructor.
	 * 
	 * @param local_socket_number
	 *            The number of the TCP socket of the main connection.
	 * @param update_time_rate
	 *            The time rate to update the internal model of the simulation,
	 *            measured in seconds.
	 * @throws IOException
	 */
	public Simulator(int local_socket_number, double update_time_rate)
			throws IOException {
		// configures the control.event object
		Logger.setSimulator(this);

		// configures the daemon objects
		Daemon.setSimulator(this);

		// creates and starts the main daemon
		this.MAIN_DAEMON = new MainDaemon("main daemon", this);
		this.MAIN_DAEMON.start(local_socket_number);

		// initiates the sets of agent_daemons
		this.PERCEPTION_DAEMONS = Collections
				.synchronizedSet(new HashSet<PerceptionDaemon>());
		this.ACTION_DAEMONS = Collections
				.synchronizedSet(new HashSet<ActionDaemon>());

		// initiates the sets of metric_daemons
		this.METRIC_DAEMONS = Collections
				.synchronizedSet(new HashSet<MetricDaemon>());

		// nullifies the environment of the simulator
		this.environment = null;

		// sets the current state as CONFIGURING
		this.state = SimulatorStates.CONFIGURING;

		// configures the model's update time rate
		this.UPDATE_TIME_RATE = update_time_rate;
	}

	/**
	 * Adds a given perception daemon to the set of perception daemons.
	 * 
	 * @param perception_daemon
	 *            The perception daemon to be added.
	 */
	public void addPerceptionDaemon(PerceptionDaemon perception_daemon) {
		this.PERCEPTION_DAEMONS.add(perception_daemon);
	}

	/**
	 * Returns the perception daemon of which agent is the given one.
	 * 
	 * @param agent
	 *            The agent of which perception daemon is wanted.
	 * @return The perception daemon of the given agent.
	 */
	public PerceptionDaemon getPerceptionDaemon(Agent agent) {
		// finds the perception daemon of which agent is the given one
		for (PerceptionDaemon perception_daemon : this.PERCEPTION_DAEMONS)
			if (perception_daemon.getAgent().equals(agent))
				return perception_daemon;

		// default answer
		return null;
	}

	/**
	 * Adds a given action daemon to the set of action daemons.
	 * 
	 * @param action_daemon
	 *            The action daemon to be added.
	 */
	public void addActionDaemon(ActionDaemon action_daemon) {
		this.ACTION_DAEMONS.add(action_daemon);
	}

	/**
	 * Returns the action daemon of a given agent.
	 * 
	 * @return The action daemon of the given agent.
	 */
	public ActionDaemon getActionDaemon(Agent agent) {
		// finds the action daemon of which agent is the given one
		for (ActionDaemon action_daemon : this.ACTION_DAEMONS)
			if (action_daemon.getAgent().equals(agent))
				return action_daemon;

		// default answer
		return null;
	}

	/**
	 * Returns the action daemons of the simulator.
	 * 
	 * @return The action daemons of the simulator.
	 */
	public ActionDaemon[] getActionDaemons() {
		return this.ACTION_DAEMONS.toArray(new ActionDaemon[0]);
	}

	/**
	 * Adds a given metric daemon to the set of metric daemons.
	 * 
	 * @param metric_daemon
	 *            The metric daemon to be added.
	 */
	public void addMetricDaemon(MetricDaemon metric_daemon) {
		this.METRIC_DAEMONS.add(metric_daemon);
	}

	/** Configures the environment of the simulation. */
	public void setEnvironment(Environment environment) {
		this.environment = environment;

		// configures the eventual metrics of the patrolling task
		Metric.setEnvironment(this.environment);
	}

	/** Returns the environment of the simulation. */
	public Environment getEnvironment() {
		return this.environment;
	}

	/**
	 * Returns the state of the simulator.
	 * 
	 * @return The state of the simulator.
	 * @see SimulatorStates
	 */
	public int getState() {
		return this.state;
	}

	/**
	 * Returns the time rate, in seconds, to update the internal model of the
	 * simulation.
	 * 
	 * @return The time rate to update the internal model.
	 */
	public double getUpdate_time_rate() {
		return this.UPDATE_TIME_RATE;
	}

	/**
	 * Stops and removes the perception and action daemons of a given agent,
	 * probably because it has died.
	 * 
	 * @param agent
	 *            The agent who has died.
	 */
	public void stopAndRemoveAgentDaemons(Agent agent) {
		// finds the perception daemon of the agent
		PerceptionDaemon perception_daemon = null;

		for (PerceptionDaemon current_perception_daemon : this.PERCEPTION_DAEMONS)
			if (current_perception_daemon.getAgent().equals(agent)) {
				perception_daemon = current_perception_daemon;
				break;
			}

		// finds the action daemon of the agent
		ActionDaemon action_daemon = null;

		for (ActionDaemon current_action_daemon : this.ACTION_DAEMONS)
			if (current_action_daemon.getAgent().equals(agent)) {
				action_daemon = current_action_daemon;
				break;
			}

		// stops the daemons
		perception_daemon.stopActing();
		action_daemon.stopActing();

		// removes the daemons
		this.PERCEPTION_DAEMONS.remove(perception_daemon);
		this.ACTION_DAEMONS.remove(action_daemon);
	}

	/**
	 * Obtains the dynamic objects of the simulation.
	 * 
	 * @return The dynamic objects of the simulation.
	 * @developer New dynamic classes must change this method.
	 */
	public Dynamic[] getDynamicObjects() {
		// holds the dynamic objects
		Set<Dynamic> dynamic_objects = new HashSet<Dynamic>();

		// obtains the dynamic objects from the graph of the environment
		Dynamic[] dynamic_from_graph = this.environment.getGraph()
				.getDynamicObjects();
		for (int i = 0; i < dynamic_from_graph.length; i++)
			dynamic_objects.add(dynamic_from_graph[i]);

		// developer: eventual new dynamic objects must be treated here

		// returns the answer
		return dynamic_objects.toArray(new Dynamic[0]);
	}

	/**
	 * Obtains the mortal objects of the simulation.
	 * 
	 * @return The mortal objects of the simulation.
	 * @developer New mortal classes must change this method.
	 */
	public Mortal[] getMortalObjects() {
		// holds the mortal objects
		Set<Mortal> mortal_objects = new HashSet<Mortal>();

		// obtains the mortal objects from the societies of the environment
		// for each society
		Society[] societies_array = this.environment.getSocieties();
		for (int i = 0; i < societies_array.length; i++)
			// if the society is an open one
			if (societies_array[i] instanceof OpenSociety) {
				// obtains its agents
				Agent[] agents = ((OpenSociety) societies_array[i]).getAgents();

				// adds each one to the set of mortal objects
				for (int j = 0; j < agents.length; j++)
					mortal_objects.add((Mortal) agents[j]);
			}

		// developer: eventual new mortal objects must be treated here

		// returns the answer
		return mortal_objects.toArray(new Mortal[0]);
	}

	/**
	 * Returns the agents that must have their stamina feature controlled.
	 * 
	 * @return The agents that must have their stamina feature controlled.
	 */
	public Agent[] getStaminedObjects() {
		// holds the wanted object
		HashSet<Agent> stamined_objects = new HashSet<Agent>();

		// obtains all the stamined agents
		// for each society
		Society[] societies = this.environment.getSocieties();
		for (int i = 0; i < societies.length; i++) {
			// for each agent
			Agent[] agents = societies[i].getAgents();
			for (int j = 0; j < agents.length; j++) {
				// for each allowed perception
				PerceptionPermission[] allowed_perceptions = agents[j]
						.getAllowedPerceptions();
				for (int k = 0; k < allowed_perceptions.length; k++) {
					// if there's a stamina limitation, adds the agent to the
					// answer
					Limitation[] limitations = allowed_perceptions[k]
							.getLimitations();
					for (int l = 0; l < limitations.length; l++) {
						if (limitations[l] instanceof StaminaLimitation) {
							stamined_objects.add(agents[j]);
							break;
						}
					}

					if (stamined_objects.contains(agents[j]))
						break;
				}

				if (stamined_objects.contains(agents[j]))
					break;

				// for each allowed action
				ActionPermission[] allowed_actions = agents[j]
						.getAllowedActions();
				for (int k = 0; k < allowed_actions.length; k++) {
					// if there's a stamina limitation, adds the agent to the
					// answer
					Limitation[] limitations = allowed_actions[k]
							.getLimitations();
					for (int l = 0; l < limitations.length; l++) {
						if (limitations[l] instanceof StaminaLimitation) {
							stamined_objects.add(agents[j]);
							break;
						}
					}

					if (stamined_objects.contains(agents[j]))
						break;
				}
			}
		}

		// developer: new stamined classes can change the code here

		// returns the answer
		return stamined_objects.toArray(new Agent[0]);
	}

	/** Stops and removes the perception daemons of the simulator. */
	private void stopAndRemovePerceptionDaemons() {
		// for each perception daemon, stops it
		for (PerceptionDaemon perception_daemon : this.PERCEPTION_DAEMONS)
			perception_daemon.stopActing();

		// removes all perceptions daemons
		this.PERCEPTION_DAEMONS.clear();
	}

	/** Stops and removes the action daemons of the simulator. */
	private void stopAndRemoveActionDaemons() {
		// for each action daemon, stops it
		for (ActionDaemon action_daemon : this.ACTION_DAEMONS)
			action_daemon.stopActing();

		// removes all action daemons
		this.ACTION_DAEMONS.clear();
	}

	/** Stops and removes the metric daemons of the simulator. */
	private void stopAndRemoveMetricDaemons() {
		// for each metric daemon, stops it
		for (MetricDaemon metric_daemon : this.METRIC_DAEMONS)
			metric_daemon.stopActing();

		// removes all the metric daemons
		this.METRIC_DAEMONS.clear();
	}

	/**
	 * Starts the simulation.
	 * 
	 * @param simulation_time
	 *            The time of simulation, measured in seconds.
	 */
	public void startSimulation(double simulation_time) {
		// changes the state of the simulator
		this.state = SimulatorStates.SIMULATING;

		// initiates the metrics controlled by the metric daemons
		for (MetricDaemon metric_daemon : this.METRIC_DAEMONS)
			metric_daemon.startMetric();
	}

	/**
	 * Stops the simulation.
	 * 
	 * @throws IOException
	 */
	public void stopSimulation() throws IOException {
		// stops and removes the perception daemons
		this.stopAndRemovePerceptionDaemons();

		// stops and removes the the action daemons
		this.stopAndRemoveActionDaemons();

		// stops and removes the metric daemons
		this.stopAndRemoveMetricDaemons();

		// changes the state of the simulator
		this.state = SimulatorStates.CONFIGURING;

		// nullifies the environment
		this.environment = null;

		// resets the main daemon's connection
		this.MAIN_DAEMON.resetConnection();

		// closes the eventual connections reserved for logging the events
		Logger.reset();
	}

	/**
	 * Finishes the simulator's work.
	 * 
	 * @throws IOException
	 */
	public void exit() throws IOException {
		// if the simulator is simulating, stops it
		if (this.state == SimulatorStates.SIMULATING)
			this.stopSimulation();

		// stops the main daemon
		this.MAIN_DAEMON.stopActing();
	}
}