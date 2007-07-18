/* Simulator.java */

/* The package of this class. */
package control.simulator;

/* Imported classes and/or interfaces. */
import java.net.SocketException;
import java.util.HashSet;
import java.util.Set;
import model.agent.Agent;
import model.agent.OpenSociety;
import model.agent.Society;
import model.graph.Graph;
import model.interfaces.Dynamic;
import model.interfaces.Mortal;
import model.metric.Metric;
import control.daemon.ActionDaemon;
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
	private MainDaemon main_daemon;
	
	/** The set of perception daemons of SimPatrol. */
	private Set<PerceptionDaemon> perception_daemons;
	
	/** The set of action daemons of SimPatrol. */
	private Set<ActionDaemon> action_daemons;
	
	/** The set of metric daemons of Simpatrol. */
	private Set<MetricDaemon> metric_daemons;
	
	/** The graph of the simulation. */
	private Graph graph;
	
	/** The societies of the simulation. */
	private Set<Society> societies;
	
	/** Holds the current state of the simulator.
	 *  @see SimulatorStates */
	private int state;
	
	/** Defines, in milliseconds, the duration of a cycle of perceptions
	 *  in the simulation. */
	private int cycle_duration;
	
	/* Methods. */
	/** Constructor.
	 * 
	 *  @param local_socket_number The number of the UDP socket of the main connection.
	 *  @param cycle_duration The duration, in milliseconds, of a cycle of perceptions. 
	 *  @throws SocketException */
	public Simulator(int local_socket_number, int cycle_duration) throws SocketException {
		// creates, starts and configures the main daemon
		this.main_daemon = new MainDaemon("main daemon", this);
		this.main_daemon.start(local_socket_number);
		MainDaemon.setSimulator(this);
		
		// initiates the sets of agent_daemons
		this.perception_daemons = new HashSet<PerceptionDaemon>();
		this.action_daemons = new HashSet<ActionDaemon>();
		
		// initiates the sets of metric_daemons
		this.metric_daemons = new HashSet<MetricDaemon>();
		Metric.setSimulator(this);
		
		// nullifies the graph of the simulation
		this.graph = null;
		
		// initates the set of societies of the simulation
		this.societies = new HashSet<Society>();
		
		// sets the current state as CONFIGURING
		this.state = SimulatorStates.CONFIGURING;
		
		// configures the duration of a cycle of perceptions
		this.cycle_duration = cycle_duration;
	}
	
	/** Adds a given perception daemon to the set of perception daemons.
	 *  
	 *  @param perception_daemon The perception daemon to be added. */
	public void addPerceptionDaemon(PerceptionDaemon perception_daemon) {
		this.perception_daemons.add(perception_daemon);
	}
	
	/** Adds a given action daemon to the set of action daemons.
	 * 
	 *  @param action_daemon The action daemon to be added. */
	public void addActionDaemon(ActionDaemon action_daemon) {
		this.action_daemons.add(action_daemon);
	}
	
	/** Adds a given metric daemon to the set of metric daemons.
	 * 
	 *  @param metric_daemon The metric daemon to be added. */
	public void addMetricDaemon(MetricDaemon metric_daemon) {
		this.metric_daemons.add(metric_daemon);
	}
	
	/** Configures the graph os the simulation.
	 * 
	 *  @param graph The graph of the simulation. */
	public void setGraph(Graph graph) {
		this.graph = graph;
	}
	
	/** Returns the graph of the simulation.
	 * 
	 *  @return The graph of the simulation. */
	public Graph getGraph() {
		return this.graph;
	}
	
	/** Adds a given society of agents to the simulation.
	 * 
	 *  @param society The society to be added.*/
	public void addSociety(Society society) {
		this.societies.add(society);
	}
	
	/** Returns the societies of the simulation.
	 * 
	 *  @return The societies of the simulation. */		
	public Society[] getSocieties() {
		Object[] societies_array = this.societies.toArray();
		Society[] answer = new Society[societies_array.length];
		for(int i = 0; i < answer.length; i++)
			answer[i] = (Society) societies_array[i];
		
		return answer;
	}
	
	/** Returns the state of the simulator.
	 * 
	 *  @return The state of the simulator.
	 *  @see SimulatorStates */
	public int getState() {
		return this.state;
	}	
	
	/** Returns the duration of a cycle of perceptions in the simulation.
	 * 
	 *  @return The duration of a cycle of perceptions. */
	public int getCycle_duration() {
		return this.cycle_duration;
	}
	
	/** Stops and removes the perception and action daemons of a given agent,
	 *  probably because it has died.
	 *  
	 *  @param agent The agent who has died. */
	public void stopAgentDaemons(Agent agent) {
		// finds the perception daemon
		PerceptionDaemon perception_daemon = null;
		
		Object[] perception_daemons_array = this.perception_daemons.toArray();
		for(int i = 0; i < perception_daemons_array.length; i++)
			if(((PerceptionDaemon) perception_daemons_array[i]).getAgent().equals(agent)) {
				perception_daemon = (PerceptionDaemon) perception_daemons_array[i];
				break;
			}
		
		// finds the action daemon
		ActionDaemon action_daemon = null;
		
		Object[] action_daemons_array = this.action_daemons.toArray();
		for(int i = 0; i < action_daemons_array.length; i++)
			if(((ActionDaemon) action_daemons_array[i]).getAgent().equals(agent)) {
				action_daemon = (ActionDaemon) action_daemons_array[i];
				break;
			}
		
		// stops the daemons
		perception_daemon.stopWorking();
		action_daemon.stopWorking();
		
		// removes the daemons
		this.perception_daemons.remove(perception_daemon);
		this.action_daemons.remove(action_daemon);
	}
	
	/** Obtains the dynamic objects of the simulation.
	 * 
	 *  @return The dynamic objects of the simulation.
	 *  @developer New dynamic classes must change this method. */
	protected Dynamic[] getDynamicObjects() {
		// holds the dynamic objects
		Set<Dynamic> dynamic_objects = new HashSet<Dynamic>();
		
		// obtains the dynamic objects from the graph of the environment
		Dynamic[] dynamic_from_graph = this.graph.getDynamicObjects();
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
	protected Mortal[] getMortalObjects() {
		// holds the mortal objects
		Set<Mortal> mortal_objects = new HashSet<Mortal>();
		
		// obtains the mortal objects from the societies (mortal agents) of the environment
		// for each society
		Object[] societies_array = this.societies.toArray();
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
	
	/** Stops the perception daemons of the simulator. */
	private void stopPerceptionDaemons() {
		// for each perception daemon, stops it
		Object[] perception_daemons_array = this.perception_daemons.toArray();
		for(int i = 0; i < perception_daemons_array.length; i++)
			((PerceptionDaemon) perception_daemons_array[i]).stopWorking();
	}
	
	/** Stops the action daemons of the simulator. */
	private void stopActionDaemons() {
		// for each action daemon, stops it
		Object[] action_daemons_array = this.action_daemons.toArray();
		for(int i = 0; i < action_daemons_array.length; i++)
			((ActionDaemon) action_daemons_array[i]).stopWorking();
	}
	
	/** Stops the metric daemons of the simulator. */
	private void stopMetricDaemons() {
		// for each metric daemon, stops it
		Object[] metric_daemons_array = this.metric_daemons.toArray();
		for(int i = 0; i < metric_daemons_array.length; i++)
			((MetricDaemon) metric_daemons_array[i]).stopWorking();
	}
	
	/** Starts the simulation.
	 * 
	 *  @param simulation_time The time of simulation. */
	public void startSimulation(int simulation_time) {
		// changes the state of the simulator
		this.state = SimulatorStates.SIMULATING;
		
		// initiates the metrics controlled by the metric daemons
		Object[] metric_daemons_array = this.metric_daemons.toArray();
		for(int i = 0; i < metric_daemons_array.length; i++)
			((MetricDaemon) metric_daemons_array[i]).startMetric();
	}
	
	/** Stops the simulation. */
	public void stopSimulation() {
		// stops the main daemon
		this.main_daemon.stopWorking();
		
		// stops the perception daemons
		this.stopPerceptionDaemons();
		
		// stops the action daemons
		this.stopActionDaemons();
		
		// stops the metric daemons
		this.stopMetricDaemons();
	}
}