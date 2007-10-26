/* Simulator.java */

/* The package of this class. */
package control.simulator;

/* Imported classes and/or interfaces. */
import java.io.IOException;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
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

/** Implements the simulator of SimPatrol.
 * 
 *  @developer New dynamic classes must change this class.
 *  @developer New mortal classes must change this class. */
public abstract class Simulator {
	/* Attributes. */
	/** The main daemon of SimPatrol. */
	private final MainDaemon MAIN_DAEMON;
	
	/** The set of perception daemons of SimPatrol. */
	protected final Set<PerceptionDaemon> PERCEPTION_DAEMONS;
	
	/** The set of action daemons of SimPatrol. */
	protected final Set<ActionDaemon> ACTION_DAEMONS;
	
	/** The set of metric daemons of Simpatrol. */
	private final Set<MetricDaemon> METRIC_DAEMONS;
	
	/** The environment (graph + societies) of the simulation. */
	private Environment environment;
	
	/** Holds the current state of the simulator.
	 *  @see SimulatorStates */
	private int state;
	
	/** Defines, in seconds, the time rate to produce the
	 *  agents' perceptions and to attend the eventual atomic actions
	 *  that compound an eventual compound action. */
	private final double ATUALIZATION_TIME_RATE;
	
	/* Methods. */
	/** Constructor.
	 * 
	 *  @param local_socket_number The number of the UDP socket of the main connection.
	 *  @param atualization_time_rate The time rate to atualize the internal model of the simulation.
	 *  @throws IOException */
	public Simulator(int local_socket_number, double atualization_time_rate) throws IOException {		
		// creates, starts and configures the main daemon
		Daemon.setSimulator(this);
		this.MAIN_DAEMON = new MainDaemon("main daemon", this);
		this.MAIN_DAEMON.start(local_socket_number);		
		
		// initiates the sets of agent_daemons
		this.PERCEPTION_DAEMONS = Collections.synchronizedSet(new HashSet<PerceptionDaemon>());
		this.ACTION_DAEMONS = Collections.synchronizedSet(new HashSet<ActionDaemon>());
		
		// initiates the sets of metric_daemons
		this.METRIC_DAEMONS = Collections.synchronizedSet(new HashSet<MetricDaemon>());
		Metric.setSimulator(this);
		
		// nullifies the environment of the simulator
		this.environment = null;
		
		// sets the current state as CONFIGURING
		this.state = SimulatorStates.CONFIGURING;
		
		// configures the model's atualization time rate
		this.ATUALIZATION_TIME_RATE = atualization_time_rate;
		
		// screen message
		System.out.println("[SimPatrol.Simulator]: Online.");
	}
	
	/** Adds a given perception daemon to the set of perception daemons.
	 *  
	 *  @param perception_daemon The perception daemon to be added. */
	public void addPerceptionDaemon(PerceptionDaemon perception_daemon) {
		this.PERCEPTION_DAEMONS.add(perception_daemon);
	}
	
	/** Returns the perception daemon of which agent is the given one.
	 * 
	 *  @param agent The agent of which perception daemon is wanted.
	 *  @return The perception daemon of the given agent. */
	public PerceptionDaemon getPerceptionDaemon(Agent agent) {
		// finds the perception daemon of which agent is the given one
		Object[] perception_daemons_array = this.PERCEPTION_DAEMONS.toArray();
		for(int i = 0; i < perception_daemons_array.length; i++)
			if(((PerceptionDaemon) perception_daemons_array[i]).getAgent().equals(agent))
				return (PerceptionDaemon) perception_daemons_array[i];
		
		// defaut answer
		return null;
	}
	
	/** Adds a given action daemon to the set of action daemons.
	 * 
	 *  @param action_daemon The action daemon to be added. */
	public void addActionDaemon(ActionDaemon action_daemon) {
		this.ACTION_DAEMONS.add(action_daemon);
	}
	
	/** Returns the action daemon of a given agent.
	 * 
	 *  @return The action daemon of the given agent. */
	public ActionDaemon getActionDaemon(Agent agent) {
		Object[] action_daemons_array = this.ACTION_DAEMONS.toArray();
		for(int i = 0; i < action_daemons_array.length; i++)
			if(((ActionDaemon) action_daemons_array[i]).getAgent().equals(agent))
				return (ActionDaemon) action_daemons_array[i];
		
		return null;
	}
	
	/** Returns the action daemons of the simulator.
	 * 
	 *  @return The action daemons of the simulator.*/
	public ActionDaemon[] getActionDaemons() {
		Object[] action_daemons_array = this.ACTION_DAEMONS.toArray();
		ActionDaemon[] answer = new ActionDaemon[action_daemons_array.length];
		
		for(int i = 0; i < answer.length; i++)
			answer[i] = (ActionDaemon) action_daemons_array[i];
		
		return answer;
	}
	
	/** Adds a given metric daemon to the set of metric daemons.
	 * 
	 *  @param metric_daemon The metric daemon to be added. */
	public void addMetricDaemon(MetricDaemon metric_daemon) {
		this.METRIC_DAEMONS.add(metric_daemon);
	}
	
	/** Configures the environment of the simulation. */
	public void setEnvironment(Environment environment) {
		this.environment = environment;
	}
	
	/** Returns the environment of the simulation. */	
	public Environment getEnvironment() {
		return this.environment;
	}
	
	/** Returns the state of the simulator.
	 * 
	 *  @return The state of the simulator.
	 *  @see SimulatorStates */
	public int getState() {
		return this.state;
	}	
	
	/** Returns the time rate, in seconds, to atualize the
	 *  internal model of the simulation.
	 *  
	 *  @return The time rate to actualize the internal model. */
	public double getAtualization_time_rate() {
		return this.ATUALIZATION_TIME_RATE;
	}
	
	/** Stops and removes the perception and action daemons of a given agent,
	 *  probably because it has died.
	 *  
	 *  @param agent The agent who has died. 
	 *  @throws IOException */
	public void stopAndRemoveAgentDaemons(Agent agent) throws IOException {
		// finds the perception daemon
		PerceptionDaemon perception_daemon = null;
		
		Object[] perception_daemons_array = this.PERCEPTION_DAEMONS.toArray();
		for(int i = 0; i < perception_daemons_array.length; i++)
			if(((PerceptionDaemon) perception_daemons_array[i]).getAgent().equals(agent)) {
				perception_daemon = (PerceptionDaemon) perception_daemons_array[i];
				break;
			}
		
		// finds the action daemon
		ActionDaemon action_daemon = null;
		
		Object[] action_daemons_array = this.ACTION_DAEMONS.toArray();
		for(int i = 0; i < action_daemons_array.length; i++)
			if(((ActionDaemon) action_daemons_array[i]).getAgent().equals(agent)) {
				action_daemon = (ActionDaemon) action_daemons_array[i];
				break;
			}
		
		// stops the daemons
		perception_daemon.stopWorking();
		action_daemon.stopWorking();
		
		// removes the daemons
		this.PERCEPTION_DAEMONS.remove(perception_daemon);
		this.ACTION_DAEMONS.remove(action_daemon);
	}
	
	/** Obtains the dynamic objects of the simulation.
	 * 
	 *  @return The dynamic objects of the simulation.
	 *  @developer New dynamic classes must change this method. */
	public Dynamic[] getDynamicObjects() {
		// holds the dynamic objects
		Set<Dynamic> dynamic_objects = new HashSet<Dynamic>();
		
		// obtains the dynamic objects from the graph of the environment
		Dynamic[] dynamic_from_graph = this.environment.getGraph().getDynamicObjects();
		for(int i = 0; i < dynamic_from_graph.length; i++)
			dynamic_objects.add(dynamic_from_graph[i]);
		
		// developer: eventual new dynamic objects must be treated here
		
		// returns the answer
		Object[] dynamic_objects_array = dynamic_objects.toArray();
		Dynamic[] answer = new Dynamic[dynamic_objects_array.length];
		for(int i = 0; i <answer.length; i++)
			answer[i] = (Dynamic) dynamic_objects_array[i];		
		return answer;
	}
	
	/** Obtains the mortal objects of the simulation.
	 * 
	 *  @return The mortal objects of the simulation.
	 *  @developer New mortal classes must change this method. */
	public Mortal[] getMortalObjects() {
		// holds the mortal objects
		Set<Mortal> mortal_objects = new HashSet<Mortal>();
		
		// obtains the mortal objects from the societies (mortal agents) of the environment
		// for each society
		Society[] societies_array = this.environment.getSocieties();
		for(int i = 0; i < societies_array.length; i++)
			// if the society is an open one
			if(societies_array[i] instanceof OpenSociety) {
				// obtains its agents
				Agent[] agents = ((OpenSociety) societies_array[i]).getAgents();
				
				// adds each one to the set of mortal objects
				for(int j = 0; j < agents.length; j++)
					mortal_objects.add((Mortal) agents[j]);
			}
		
		// developer: eventual new mortal objects must be treated here
		
		// returns the answer
		Object[] mortal_objects_array = mortal_objects.toArray();
		Mortal[] answer = new Mortal[mortal_objects_array.length];
		for(int i = 0; i <answer.length; i++)
			answer[i] = (Mortal) mortal_objects_array[i];		
		return answer;
	}
	
	/** Returns the agents that must have their stamina values controlled.
	 * 
	 *  @return The agents that must have their stamina feature controlled. */
	public Agent[] getStaminedObjects() {
		// holds the wanted object
		HashSet<Agent> stamined_objects = new HashSet<Agent>();
		
		// obtains all the stamined agents
		// for each society
		Society[] societies = this.environment.getSocieties();
		for(int i = 0; i < societies.length; i++) {
			// for each agent
			Agent[] agents = societies[i].getAgents();
			for(int j = 0; j < agents.length; j++) {
				// for each allowed perception
				PerceptionPermission[] allowed_perceptions = agents[j].getAllowedPerceptions();
				for(int k = 0; k < allowed_perceptions.length; k++) {
					// if there's a stamina limitation, adds the agent to the answer
					Limitation[] limitations = allowed_perceptions[k].getLimitations();
					for(int l = 0; l < limitations.length; l++) {
						if(limitations[l] instanceof StaminaLimitation) {
							stamined_objects.add(agents[j]);
							break;
						}
					}
					
					if(stamined_objects.contains(agents[j]))
						break;
				}
				
				if(stamined_objects.contains(agents[j]))
					break;
				
				// for each allowed action
				ActionPermission[] allowed_actions = agents[j].getAllowedActions();
				for(int k = 0; k < allowed_actions.length; k++) {
					// if there's a stamina limitation, adds the agent to the answer
					Limitation[] limitations = allowed_actions[k].getLimitations();
					for(int l = 0; l < limitations.length; l++) {
						if(limitations[l] instanceof StaminaLimitation) {
							stamined_objects.add(agents[j]);
							break;
						}
					}
					
					if(stamined_objects.contains(agents[j]))
						break;
				}
			}
		}
		
		// developer: new stamined classes can change the code here
		
		// mounts the answer to method
		Object[] agents_array = stamined_objects.toArray();
		Agent[] answer = new Agent[agents_array.length];
		for(int i = 0; i < answer.length; i++)
			answer[i] = (Agent) agents_array[i];
		
		// returns the answer
		return answer;
	}
	
	/** Stops and removes the perception daemons of the simulator. 
	 * 
	 *  @throws IOException */
	private void stopAndRemovePerceptionDaemons() throws IOException {
		// for each perception daemon, stops it
		Object[] perception_daemons_array = this.PERCEPTION_DAEMONS.toArray();
		for(int i = 0; i < perception_daemons_array.length; i++)
			((PerceptionDaemon) perception_daemons_array[i]).stopWorking();
		
		// removes all perceptions daemons
		this.PERCEPTION_DAEMONS.clear();
	}
	
	/** Stops and removes the action daemons of the simulator. 
	 * 
	 *  @throws IOException */
	private void stopAndRemoveActionDaemons() throws IOException {
		// for each action daemon, stops it
		Object[] action_daemons_array = this.ACTION_DAEMONS.toArray();
		for(int i = 0; i < action_daemons_array.length; i++)
			((ActionDaemon) action_daemons_array[i]).stopWorking();
		
		// removes all action daemons
		this.ACTION_DAEMONS.clear();
	}
	
	/** Stops and removes the metric daemons of the simulator. 
	 * 
	 *  @throws IOException */
	private void stopAndRemoveMetricDaemons() throws IOException {
		// for each metric daemon, stops it
		Object[] metric_daemons_array = this.METRIC_DAEMONS.toArray();
		for(int i = 0; i < metric_daemons_array.length; i++)
			((MetricDaemon) metric_daemons_array[i]).stopWorking();
		
		// removes all the metric daemons
		this.METRIC_DAEMONS.clear();
	}
	
	/** Starts the simulation.
	 * 
	 *  @param simulation_time The time of simulation. */
	public void startSimulation(int simulation_time) {
		// changes the state of the simulator
		this.state = SimulatorStates.SIMULATING;
		
		// initiates the metrics controlled by the metric daemons
		Object[] metric_daemons_array = this.METRIC_DAEMONS.toArray();
		for(int i = 0; i < metric_daemons_array.length; i++)
			((MetricDaemon) metric_daemons_array[i]).startMetric();
	}
	
	/** Stops the simulation. 
	 * 
	 *  @throws IOException 
	 *  @throws InterruptedException */
	public void stopSimulation() throws IOException, InterruptedException {
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
	}
	
	/** Finishes the simulator's work. 
	 *
	 *  @throws IOException 
	 *  @throws InterruptedException */
	public void exit() throws IOException, InterruptedException {
		// if the simulator is simulating, stops it
		if(this.state == SimulatorStates.SIMULATING)
			this.stopSimulation();
		
		// stops the main daemon
		this.MAIN_DAEMON.stopWorking();
		
		// screen message
		System.out.println("[SimPatrol.Simulator]: offline.");
	}
}