/* MainDaemon.java */

/* The package of this class. */
package control.daemon;

/* Imported classes and/or interfaces. */
import java.io.IOException;
import java.net.SocketException;
import javax.xml.parsers.ParserConfigurationException;
import org.xml.sax.SAXException;
import util.udp.SocketNumberGenerator;
import view.connection.AgentConnection;
import model.Environment;
import model.agent.Agent;
import model.agent.OpenSociety;
import model.agent.Society;
import model.interfaces.Mortal;
import model.metric.Metric;
import control.configuration.AgentCreationConfiguration;
import control.configuration.AgentDeathConfiguration;
import control.configuration.Configuration;
import control.configuration.EnvironmentCreationConfiguration;
import control.configuration.MetricCreationConfiguration;
import control.configuration.Orientation;
import control.configuration.SimulationStartConfiguration;
import control.simulator.CycledSimulator;
import control.simulator.RealTimeSimulator;
import control.simulator.Simulator;
import control.simulator.SimulatorStates;
import control.translator.ConfigurationTranslator;

/**
 * Implements the main daemon of SimPatrol, the one that listens to the
 * configurations of the simulation of the patrolling task.
 * 
 * @developer New configurations must change this class.
 * @modeller This class must have its behaviour modelled.
 */
public final class MainDaemon extends Daemon {
	/* Attributes. */
	/** A generator of numbers for socket connections. */
	private SocketNumberGenerator socket_number_generator;

	/* Methods. */
	/** Constructor.
	 * 
	 *  @param thread_name The name of the thread of the daemon. */
	public MainDaemon(String thread_name, Simulator simulator) {
		super(thread_name);
		this.socket_number_generator = null;
	}
	
	/** Attends a given "environment creation" configuration, sending the correspondent
	 *  orientation to the remote contact.
	 * 
	 *  @param configuration The configuration to be attended.
	 *  @throws IOException */
	private void attendEnvironmentCreationConfiguration(EnvironmentCreationConfiguration configuration) throws IOException {
		// screen message
		System.out.println("[SimPatrol.MainDaemon]: \"Environment's creation\" configuration received.");
		
		// obtains the environment from the configuration
		Environment environment = configuration.getEnvironment();
		
		// sets the environment to the simulator
		simulator.setEnvironment(environment);
		
		// mounts the orientation containing the agents and the respective
		// numbers of their UDP socket connections
		Orientation orientation = new Orientation();
		
		// for each society
		Society[] societies = environment.getSocieties();
		for(int i = 0; i < societies.length; i++) {
			// for each agent
			Agent[] agents = societies[i].getAgents();
			for(int j = 0; j < agents.length; j++) {
				// creates and starts its perception and action daemons
				int socket_number = this.createAndStartAgentDaemons(agents[j]);
				
				// fills the orientation
				orientation.addItem(socket_number, agents[j].getObjectId());
			}
		}
		
		// sends the created orientation to the remote contact
		this.connection.send(orientation.fullToXML(0), configuration.getSender_address(), configuration.getSender_socket());
	}
	
	/** Attends a given "agent creation" configuration, sending the correspondent
	 *  orientation to the remote contact.
	 * 
	 *  @param configuration The configuration to be attended.
	 *  @throws IOException */
	private void attendAgentCreationConfiguration(AgentCreationConfiguration configuration) throws IOException {
		// screen message
		System.out.println("[SimPatrol.MainDaemon]: \"Agent's creation\" configuration received.");
		
		// obtains the agent from the configuration
		Agent agent = configuration.getAgent();
		
		// obtains the id of the society where the agent must be added
		String society_id = configuration.getSociety_id();
		
		// obtains the societies from the environment of the simulator
		Society[] societies = simulator.getEnvironment().getSocieties();
		
		// finds the society with the given id
		Society society = null;
		for(int i = 0; i < societies.length; i++)
			if(societies[i].getObjectId().equals(society_id)) {
				society = societies[i];
				break;
			}
		
		// if a valid society was found
		if(society != null) {
			// if the simulator is in the configuring state or the society is open
			if(simulator.getState() == SimulatorStates.CONFIGURING || society instanceof OpenSociety) {
				// adds the agent to the society
				society.addAgent(agent);
				
				// creates and starts its perception and action daemons
				int socket_number = this.createAndStartAgentDaemons(agent);
				
				// if the simulator is a rt one and its state is SIMULATING
				if(simulator instanceof RealTimeSimulator && simulator.getState() == SimulatorStates.SIMULATING) {
					// creates and starts its mortal controller robot
					((RealTimeSimulator) simulator).createAndStartMortalityControlerRobot((Mortal) agent);
					
					// creates and starts its eventual stamina controller robot
					((RealTimeSimulator) simulator).createAndStartStaminaControlerRobot(agent);
				}
				
				// sends an orientation to the sender of the configuration
				Orientation orientation = new Orientation();
				orientation.addItem(socket_number, agent.getObjectId());
				this.connection.send(orientation.fullToXML(0), configuration.getSender_address(), configuration.getSender_socket());				
			}
			// if not, sends an orientation reporting error
			else this.connection.send(new Orientation("Closed society.").fullToXML(0), configuration.getSender_address(), configuration.getSender_socket());
		}
		// if not, sends an orientation reporting error
		else this.connection.send(new Orientation("Society not found.").fullToXML(0), configuration.getSender_address(), configuration.getSender_socket());
	}
	
	/** Attends a given "agent death" configuration, sending the correspondent
	 *  orientation to the remote contact.
	 * 
	 *  @param configuration The configuration to be attended.
	 *  @throws IOException */
	private void attendAgentDeathConfiguration(AgentDeathConfiguration configuration) throws IOException {
		// screen message
		System.out.println("[SimPatrol.MainDaemon]: \"Agent's death\" configuration received."); 
		
		// obtains the agent's id from the configuration
		String agent_id = configuration.getObjectId();
		
		// obtains the agent with such id, from the environment
		Agent agent = null;
		Society[] societies = simulator.getEnvironment().getSocieties();
		for(int i = 0; i < societies.length; i++) {
			if(agent != null) break;
			
			if(societies[i] instanceof OpenSociety) {
				Agent[] agents = societies[i].getAgents();
				
				for(int j = 0; j < agents.length; j++)
					if(agents[j].getObjectId().equals(agent_id)) {
						agent = agents[j];
						break;
					}
			}
		}
		
		// if an agent was found
		if(agent != null) {
			// FINISH HIM! =D
			((Mortal) agent).die();
			
			// stops the agent's action and perception daemons
			simulator.stopAndRemoveAgentDaemons(agent);
			
			// if the simulator is a realtime one
			if(simulator instanceof RealTimeSimulator) {
				// removes and stops its eventual stamina controller robot 
				((RealTimeSimulator) simulator).stopAndRemoveStaminaControllerRobot(agent);
				
				// removes and stops its mortality controller robot
				((RealTimeSimulator) simulator).stopAndRemoveMortalityControllerRobot((Mortal) agent);
			}
			// else, removes the eventual
			// "agent - action spent stamina - perception spent stamina" trio
			// memorized in the cycled simulator
			else ((CycledSimulator) simulator).removeAgentSpentStaminas(agent);
			
			// sends an empty orientation to the sender of configuration
			this.connection.send(new Orientation().fullToXML(0), configuration.getSender_address(), configuration.getSender_socket());
		}
		// if not, sends an orientation reporting error
		else this.connection.send(new Orientation("Seasonal agent not found.").fullToXML(0), configuration.getSender_address(), configuration.getSender_socket());
	}
	
	/** Attends a given "metric creation" configuration, sending the
	 *  correspondent orientation to the remote contact.
	 * 
	 *  @param configuration The configuration to be attended.
	 *  @throws IOException */
	private void attendMetricCreationConfiguration(MetricCreationConfiguration configuration) throws IOException {
		// screen message
		System.out.println("[SimPatrol.MainDaemon]: \"Metric creation\" configuration received.");
				
		// obtains the metric of the configuration
		Metric metric = configuration.getMetric();
		
		// obtains the duration of a cycle of measurement of the metric
		int cycle_duration = configuration.getCycle_duration();
		
		// creates and starts a metric daemon for the metric
		int socket_number = this.createAndStartMetricDaemon(metric, cycle_duration);
		
		// sends an orientation with the number of the UDP socket
		// used to externalize the metric
		this.connection.send(new Orientation(String.valueOf(socket_number)).fullToXML(0), configuration.getSender_address(), configuration.getSender_socket());
	}
	
	/** Attends a given "simulation start" configuration, sending the
	 *  correspondent orientation to the remote contact.
	 *
	 *  @param configuration The configuration to be attended.
	 *  @throws IOException */
	private void attendSimulationStartConfiguration(SimulationStartConfiguration configuration) throws IOException {
		// screen message
		System.out.println("[SimPatrol.MainDaemon]: \"Start simulation\" configuration received.");
		
		// obtains the time of simulation
		int simulation_time = configuration.getSimulation_time();

		// if the simulator is in the CONFIGURING state
		if (simulator.getState() == SimulatorStates.CONFIGURING) {
			// starts the simulation
			simulator.startSimulation(simulation_time);

			// sends an empty orientation to the sender of configuration
			this.connection.send(new Orientation().fullToXML(0), configuration.getSender_address(), configuration.getSender_socket());
		}
		// if not, sends an orientation reporting error
		else this.connection.send(new Orientation("Simulator already simulating.").fullToXML(0), configuration.getSender_address(), configuration.getSender_socket());		
	}
	
	/** Creates the agent daemons (perception and action daemons) for the given
	 *  agent, starts these daemons and adds them to the simulator.
	 * 
	 *  @param agent The agent of which daemons are to be created.
	 *  @return The number of the UDP socket created for the agent daemons. */
	private int createAndStartAgentDaemons(Agent agent) {
		// creates a perception daemon
		PerceptionDaemon perception_daemon = new PerceptionDaemon(agent.getObjectId() + "'s perception daemon", agent);
		
		// creates an action daemon
		ActionDaemon action_daemon = new ActionDaemon(agent.getObjectId() + "'s action daemon", agent);
		
		// creates a new agent connection
		AgentConnection connection = new AgentConnection(agent.getObjectId() + "'s connection", perception_daemon.buffer, action_daemon.buffer);
		
		// configures the perception and action daemons' connection
		perception_daemon.setConnection(connection);
		action_daemon.setConnection(connection);
		
		// if the simulator is a real time one, lets both action daemons work
		if(simulator instanceof RealTimeSimulator) {
			perception_daemon.setCan_work(true);
			action_daemon.setCan_work(true);
		}
		
		// starts the daemons
		boolean socket_exception_happened = true;
		do {
			try {
				perception_daemon.start(this.socket_number_generator.generateSocketNumber());
				action_daemon.start(this.socket_number_generator.generateSocketNumber());
				
				socket_exception_happened = false;
			} catch (SocketException e) { socket_exception_happened = true; }
		} while (socket_exception_happened);
		
		// adds the daemons to the simulator
		simulator.addPerceptionDaemon(perception_daemon);
		simulator.addActionDaemon(action_daemon);
		
		// returns the obtained number for the sockets of the daemons
		return perception_daemon.getUDPSocketNumber();
	}
	
	/** Creates the metric daemon for the given metric, starts this daemon and
	 *  adds it to the simulator.
	 * 
	 *  @param metric The metric of which daemon is to be created.
	 *  @param cycle_duration The duration of a cycle of measurement of the given metric.
	 *  @return The number of the UDP socket created for the metric daemon. */
	private int createAndStartMetricDaemon(Metric metric, int cycle_duration) {
		// creates the metric daemon
		StringBuffer thread_name_buffer = new StringBuffer();
		thread_name_buffer.append("metric " + metric.getType() + "'s daemon");
		MetricDaemon metric_daemon = new MetricDaemon(thread_name_buffer.toString(), metric, cycle_duration);
		
		// starts the daemon
		boolean socket_exception_happened = true;
		do {
			try {
				metric_daemon.start(this.socket_number_generator.generateSocketNumber());
				socket_exception_happened = false;
			} catch (SocketException e) { socket_exception_happened = true; }
		} while (socket_exception_happened);
		
		// adds the daemon to the simulator
		simulator.addMetricDaemon(metric_daemon);
		
		// if the simulator is already simulating, starts the metric daemon's
		// clock
		if(simulator.getState() == SimulatorStates.SIMULATING)
			metric_daemon.startMetric();
		
		// returns the obtained number for the sockets of the daemons
		return metric_daemon.getUDPSocketNumber();
	}
	
	public void start(int local_socket_number) throws SocketException {
		// starts the socket number generator
		this.socket_number_generator = new SocketNumberGenerator(local_socket_number);
		
		// calls the super method
		super.start(local_socket_number);
		
		// screen message
		System.out.println("[SimPatrol.MainDaemon]: Started working.");
	}
	
	public void stopWorking() {
		super.stopWorking();
		
		// screen message
		System.out.println("[SimPatrol.MainDaemon]: Stopped working.");
	}
	
	/** @developer New configuration must change this method.
	 *  @modeller This method must be modelled. */
	public void run() {
		while(!this.stop_working) {
			// obtains a string message from the buffer
			String message = null;
			while(message == null)
				message = this.buffer.remove();
			
			// obtains the configuration from the string message
			Configuration configuration = null;
			try { configuration = ConfigurationTranslator.getConfiguration(message); }
			catch (ParserConfigurationException e) { e.printStackTrace(); }
			catch (SAXException e) { e.printStackTrace(); }
			catch (IOException e) { e.printStackTrace(); }
			
			// if configuration is still not valid, tries to obtain it
			// as an "agent creation" configuration
			if(configuration == null)
				try { configuration = ConfigurationTranslator.getAgentCreationConfiguration(message, simulator.getEnvironment().getGraph()); }
				catch (ParserConfigurationException e) { e.printStackTrace(); }
				catch (SAXException e) { e.printStackTrace(); }
				catch (IOException e) { e.printStackTrace(); }
			
			// if the configuration is an "environment creation"
			if(configuration instanceof EnvironmentCreationConfiguration) {
				// if the simulator is not simulating yet
				if(simulator.getState() == SimulatorStates.CONFIGURING) {
					try { this.attendEnvironmentCreationConfiguration((EnvironmentCreationConfiguration)configuration); }
					catch (IOException e) { e.printStackTrace(); }
				}
				else {
					try { this.connection.send(new Orientation("Simulator already simulating.").fullToXML(0), configuration.getSender_address(), configuration.getSender_socket());
					} catch (IOException e) { e.printStackTrace(); }
				}
			}
			
			// else if the configuration is an "agent creation"
			else if(configuration instanceof AgentCreationConfiguration) {
				// if the environment of the simulator is valid
				if(simulator.getEnvironment() != null) {
					try { this.attendAgentCreationConfiguration((AgentCreationConfiguration)configuration); }
					catch (IOException e) { e.printStackTrace(); }
				}
				else {
					try { this.connection.send(new Orientation("Environment (graph + societies) not set yet.").fullToXML(0), configuration.getSender_address(), configuration.getSender_socket());
					} catch (IOException e) { e.printStackTrace(); }
				}
			}
			
			// else if the configuration is a "metric creation"
			else if(configuration instanceof MetricCreationConfiguration) {
				// if the environment of the simulator is valid
				if(simulator.getEnvironment() != null) {
					try { this.attendMetricCreationConfiguration((MetricCreationConfiguration)configuration); }
					catch (IOException e) { e.printStackTrace(); }
				}
				else {
					try { this.connection.send(new Orientation("Environment (graph + societies) not set yet.").fullToXML(0), configuration.getSender_address(), configuration.getSender_socket());
					} catch (IOException e) { e.printStackTrace(); }
				}
			}
			
			// else if the configuration is a "simulation start"
			else if(configuration instanceof SimulationStartConfiguration) {
				// if the environment of the simulator is valid
				if(simulator.getEnvironment() != null) {
					try { this.attendSimulationStartConfiguration((SimulationStartConfiguration)configuration); }
					catch (IOException e) { e.printStackTrace(); }
				}
				else {
					try { this.connection.send(new Orientation("Environment (graph + societies) not set yet.").fullToXML(0), configuration.getSender_address(), configuration.getSender_socket());
					} catch (IOException e) { e.printStackTrace(); }
				}
			}
			
			// else if the configuration is an "agent death"
			else if(configuration instanceof AgentDeathConfiguration) {
				// if the environment of the simulator is valid
				if(simulator.getEnvironment() != null) {
					try { this.attendAgentDeathConfiguration((AgentDeathConfiguration) configuration); }
					catch (IOException e) { e.printStackTrace(); }
				}
				else {
					try { this.connection.send(new Orientation("Environment (graph + societies) not set yet.").fullToXML(0), configuration.getSender_address(), configuration.getSender_socket());
					} catch (IOException e) { e.printStackTrace(); }
				}
				
			}
			
			// developer: new configurations must add code here
		}
	}
}