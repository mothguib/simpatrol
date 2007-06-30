/* Simulator.java */

/* The package of this class. */
package control.simulator;

/* Imported classes and/or interfaces. */
import java.io.IOException;
import java.net.SocketException;
import java.util.HashSet;
import java.util.Set;
import javax.xml.parsers.ParserConfigurationException;
import model.Environment;
import model.agent.Agent;
import model.agent.OpenSociety;
import model.agent.Society;
import model.interfaces.Dynamic;
import model.interfaces.Mortal;
import org.xml.sax.SAXException;

import util.udp.SocketNumberGenerator;
import control.daemon.ActionDaemon;
import control.daemon.MainDaemon;
import control.daemon.PerceptionDaemon;

/** Implements the simulator of SimPatrol. */
public abstract class Simulator {
	/* Attributes. */
	/** The main daemon of SimPatrol. */
	private MainDaemon main_daemon;
	
	/** The set of perception daemons of SimPatrol. */
	private Set<PerceptionDaemon> perception_daemons;
	
	/** The set of action daemons of SimPatrol. */
	private Set<ActionDaemon> action_daemons;
	
	/** The generator of the numbers for the eventually created UDP sockets. */
	private SocketNumberGenerator socket_number_generator;
	
	/** Holds the current state of the simulator.
	 *  @see SimulatorStates */
	private int state;
	
	/** The environment of the simulation. */
	protected Environment environment;
	
	/* Methods. */
	/** Constructor.
	 *  @param local_socket_number The number of the UDP socket of the main connection.
	 *  @throws IOException 
	 *  @throws SAXException 
	 *  @throws ParserConfigurationException */
	public Simulator(int local_socket_number) throws ParserConfigurationException, SAXException, IOException {
		// creates and starts the main daemon
		this.main_daemon = new MainDaemon(this);
		this.main_daemon.start(local_socket_number);
		
		// initiates the sets of agent_daemons
		this.perception_daemons = new HashSet<PerceptionDaemon>();
		this.action_daemons = new HashSet<ActionDaemon>();
		
		// initiates the generator of the numbers of the UDP sockets
		this.socket_number_generator = new SocketNumberGenerator(local_socket_number);
		
		// sets the current state as CONFIGURING
		this.state = SimulatorStates.CONFIGURING;
		
		// nullifies the environment
		this.environment = null;		
	}
	
	/** Adds a given perception daemon to the set of perception daemons,
	 *  and starts it.
	 *  @param perception_daemon The perception daemon to be added. 
	 *  @throws SocketException */
	public void addPerceptionDaemon(PerceptionDaemon perception_daemon) throws SocketException {
		this.perception_daemons.add(perception_daemon);
		perception_daemon.start(this.socket_number_generator.generateSocketNumber());
	}
	
	/** Adds a given action daemon to the set of action daemons,
	 *  and starts it.
	 *  @param action_daemon The action daemon to be added. 
	 *  @throws SocketException */
	public void addActionDaemon(ActionDaemon action_daemon) throws SocketException {
		this.action_daemons.add(action_daemon);
		action_daemon.start(this.socket_number_generator.generateSocketNumber());
	}
	
	/** Returns the state of the simulator.
	 *  @return The state of the simulator.
	 *  @see SimulatorStates */
	public int getState() {
		return this.state;
	}
	
	/** Configures the environment of the simulation.
	 *  @param environment The environment of the simulation. */
	public void setEnvironment(Environment environment) {
		this.environment = environment;
	}
	
	/** Returns the environment of the simulation.
	 *  @return The environment of the simulation. */
	public Environment getEnvironment() {
		return this.environment;
	}
	
	/** Obtains the dynamic objects of the simulation.
	 *  @return The dynamic objects of the simulation. */
	protected Dynamic[] getDynamicObjects() {
		// holds the dynamic objects
		Set<Dynamic> dynamic_objects = new HashSet<Dynamic>();
		
		// obtains the dynamic objects from the graph of the environment
		Dynamic[] dynamic_from_graph = this.environment.getGraph().getDynamicObjects();
		for(int i = 0; i < dynamic_from_graph.length; i++)
			dynamic_objects.add(dynamic_from_graph[i]);
		
		// WARNING other eventual dynamic objects shall be obtained here
		
		// returns the answer
		Object[] dynamic_objects_array = dynamic_objects.toArray();
		Dynamic[] answer = new Dynamic[dynamic_objects_array.length];
		for(int i = 0; i <answer.length; i++)
			answer[i] = (Dynamic) dynamic_objects_array[i];		
		return answer;
	}
	
	/** Obtains the mortal objects of the simulation.
	 *  @return The mortal objects of the simulation. */
	protected Mortal[] getMortalObjects() {
		// holds the mortal objects
		Set<Mortal> mortal_objects = new HashSet<Mortal>();
		
		// obtains the mortal objects from the societies (mortal agents) of the environment
		// for each society
		Society[] societies = this.getEnvironment().getSocieties();
		for(int i = 0; i < societies.length; i++)
			// if the society is an open one
			if(societies[i] instanceof OpenSociety) {
				// obtains its agents
				Agent[] agents = ((OpenSociety) societies[i]).getAgents();
				
				// adds each one to the set of mortal objects
				for(int j = 0; j < agents.length; j++)
					mortal_objects.add((Mortal) agents[j]);
			}
		
		// WARNING other eventual mortal objects shall be obtained here
		
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
	
	/** Starts the simulation.
	 *  @param simulation_time The time of simulation. */
	public void startSimulation(int simulation_time) {
		// changes the state of simulator
		this.state = SimulatorStates.SIMULATING;
	}
	
	/** Stops the simulation. */
	public void stopSimulation() {
		// stops the main daemon
		this.main_daemon.stopWorking();
		
		// stops the perception daemons
		this.stopPerceptionDaemons();
		
		// stops the action daemons
		this.stopActionDaemons();
	}
}