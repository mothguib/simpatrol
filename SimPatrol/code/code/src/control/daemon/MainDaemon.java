/* MainDaemon.java */

/* The package of this class. */
package control.daemon;

/* Imported classes and/or interfaces. */
import java.io.IOException;

import javax.xml.parsers.ParserConfigurationException;

import org.xml.sax.SAXException;
import util.net.SocketNumberGenerator;
import view.connection.ServerSideAgentTCPConnection;
import view.connection.AgentUDPConnection;
import view.connection.Connection;
import view.connection.ServerSideTCPConnection;
import view.connection.UDPConnection;
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
import control.configuration.EventsCollectingConfiguration;
import control.configuration.MetricCreationConfiguration;
import control.configuration.Orientation;
import control.configuration.SimulationStartConfiguration;
import control.event.Logger;
import control.exception.EdgeNotFoundException;
import control.exception.EnvironmentNotValidException;
import control.exception.NodeNotFoundException;
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
 * @modeler This class must have its behavior modeled.
 */
public final class MainDaemon extends Daemon implements IMessageObserver {
	/* Attributes. */
	/** A generator of numbers for socket connections. */
	private SocketNumberGenerator socket_number_generator;

	/* Methods. */
	/**
	 * Constructor.
	 * 
	 * @param thread_name
	 *            The name of the thread of the daemon.
	 */
	public MainDaemon(String thread_name, Simulator simulator) {
		super(thread_name);
		this.socket_number_generator = null;
		this.connection = new ServerSideTCPConnection(thread_name
				+ "'s connection", this.BUFFER);
	}

	/**
	 * Attends a given "event collecting" configuration, sending the
	 * correspondent orientation to the remote contact.
	 * 
	 * @param configuration
	 *            The configuration to be attended.
	 */
	private void attendEventCollectingConfiguration(
			EventsCollectingConfiguration configuration) {
		// creates an UDP connection to send the events
		UDPConnection connection = new UDPConnection(
				"event collecting's connection", null);

		// tries to start the connection's work
		boolean socket_exception_happened = false;
		do {
			try {
				connection.start(this.socket_number_generator
						.generateSocketNumber());
				socket_exception_happened = false;
			} catch (IOException e) {
				socket_exception_happened = true;
			}
		} while (socket_exception_happened);

		// adds the connection to the control.event of events
		Logger.addConnection(connection);

		// mounts the orientation containing the reserved socket number
		Orientation orientation = new Orientation(String.valueOf(connection
				.getSocketNumber()));

		// sends the created orientation to the remote contact
		this.connection.send(orientation.fullToXML(0));
	}

	/**
	 * Attends a given "environment creation" configuration, sending the
	 * correspondent orientation to the remote contact.
	 * 
	 * @param configuration
	 *            The configuration to be attended.
	 */
	private void attendEnvironmentCreationConfiguration(
			EnvironmentCreationConfiguration configuration) {
		// obtains the environment from the configuration
		Environment environment = configuration.getEnvironment();

		// sets the environment to the simulator
		simulator.setEnvironment(environment);

		// mounts the orientation containing the agents and the respective
		// numbers of their socket connections
		Orientation orientation = new Orientation();

		// for each society
		Society[] societies = environment.getSocieties();
		for (int i = 0; i < societies.length; i++) {
			// for each agent
			Agent[] agents = societies[i].getAgents();
			for (int j = 0; j < agents.length; j++) {
				// creates and starts its perception and action daemons
				int socket_number = this.createAndStartAgentDaemons(agents[j]);

				// fills the orientation
				orientation.addItem(socket_number, agents[j].getObjectId());
			}
		}

		// sends the created orientation to the remote contact
		this.connection.send(orientation.fullToXML(0));
	}

	/**
	 * Attends a given "agent creation" configuration, sending the correspondent
	 * orientation to the remote contact.
	 * 
	 * @param configuration
	 *            The configuration to be attended.
	 */
	private void attendAgentCreationConfiguration(
			AgentCreationConfiguration configuration) {
		// obtains the agent from the configuration
		Agent agent = configuration.getAgent();

		// obtains the id of the society where the agent must be added
		String society_id = configuration.getSociety_id();

		// obtains the societies from the environment of the simulator
		Society[] societies = simulator.getEnvironment().getSocieties();

		// finds the society with the given id
		Society society = null;
		for (int i = 0; i < societies.length; i++)
			if (societies[i].getObjectId().equals(society_id)) {
				society = societies[i];
				break;
			}

		// if a valid society was found
		if (society != null) {
			// if the simulator is in the configuring state or the society is
			// open
			if (simulator.getState() == SimulatorStates.CONFIGURING
					|| society instanceof OpenSociety) {
				// if can add the agent to the society successfully
				if (society.addAgent(agent)) {
					// complete the process...
					this.completeAgentCreationAttendment(agent, society);
				}
				// else, sends an orientation reporting error
				else {
					this.connection.send(new Orientation(
							"Agent already exists.").fullToXML(0));
				}
			}
			// if not, sends an orientation reporting error
			else {
				this.connection.send(new Orientation("Closed society.")
						.fullToXML(0));
			}
		}
		// if not, sends an orientation reporting error
		else {
			this.connection.send(new Orientation("Society not found.")
					.fullToXML(0));
		}
	}

	/**
	 * Completes the attendment of an "agent creation" configuration.
	 * 
	 * @param agent
	 *            The new agent to be created.
	 * @param society
	 *            The society where the agent is supposed to be added.
	 */
	private void completeAgentCreationAttendment(Agent agent, Society society) {
		// creates and starts its perception and action daemons
		int socket_number = this.createAndStartAgentDaemons(agent);

		// if the simulator is a real time one and its state is SIMULATING
		// and the society is open
		if (simulator instanceof RealTimeSimulator
				&& simulator.getState() == SimulatorStates.SIMULATING
				&& society instanceof OpenSociety) {
			// creates and starts its mortal controller robot
			((RealTimeSimulator) simulator)
					.createAndStartMortalityControlerRobot((Mortal) agent);

			// creates and starts its eventual stamina controller
			// robot
			((RealTimeSimulator) simulator)
					.createAndStartStaminaControlerRobot(agent);
		}

		// sends an orientation to the sender of the configuration
		this.connection.send(new Orientation(String.valueOf(socket_number))
				.fullToXML(0));
	}

	/**
	 * Attends a given "agent death" configuration, sending the correspondent
	 * orientation to the remote contact.
	 * 
	 * @param configuration
	 *            The configuration to be attended.
	 */
	private void attendAgentDeathConfiguration(
			AgentDeathConfiguration configuration) {
		// obtains the agent's id from the configuration
		String agent_id = configuration.getObjectId();

		// obtains the agent with such id, from the environment
		Agent agent = null;
		Society[] societies = simulator.getEnvironment().getSocieties();
		for (int i = 0; i < societies.length; i++) {
			if (agent != null)
				break;

			if (societies[i] instanceof OpenSociety) {
				Agent[] agents = societies[i].getAgents();

				for (int j = 0; j < agents.length; j++)
					if (agents[j].getObjectId().equals(agent_id)) {
						agent = agents[j];
						break;
					}
			}
		}

		// if an agent was found
		if (agent != null) {
			// FINISH HIM!
			((Mortal) agent).die();

			// stops the agent's action and perception daemons
			simulator.stopAndRemoveAgentDaemons(agent);

			// if the simulator is a real time one
			if (simulator instanceof RealTimeSimulator) {
				// removes and stops its eventual stamina controller robot
				((RealTimeSimulator) simulator)
						.stopAndRemoveStaminaControllerRobot(agent);

				// removes and stops its mortality controller robot
				((RealTimeSimulator) simulator)
						.stopAndRemoveMortalityControllerRobot((Mortal) agent);
			}
			// else, removes the eventual
			// "agent - action spent stamina - perception spent stamina" trio
			// memorized in the cycled simulator
			else
				((CycledSimulator) simulator).removeAgentSpentStaminas(agent);

			// sends an empty orientation to the sender of configuration
			this.connection.send(new Orientation().fullToXML(0));
		}
		// if not, sends an orientation reporting error
		else
			this.connection.send(new Orientation("Seasonal agent not found.")
					.fullToXML(0));
	}

	/**
	 * Attends a given "metric creation" configuration, sending the
	 * correspondent orientation to the remote contact.
	 * 
	 * @param configuration
	 *            The configuration to be attended.
	 */
	private void attendMetricCreationConfiguration(
			MetricCreationConfiguration configuration) {
		// obtains the metric of the configuration
		Metric metric = configuration.getMetric();

		// obtains the duration of a cycle of measurement of the metric
		double cycle_duration = configuration.getCycle_duration();

		// creates and starts a metric daemon for the metric
		int socket_number = this.createAndStartMetricDaemon(metric,
				cycle_duration);

		// sends an orientation with the number of the UDP socket
		// used to output the metric
		this.connection.send(new Orientation(String.valueOf(socket_number))
				.fullToXML(0));
	}

	/**
	 * Attends a given "simulation start" configuration, sending the
	 * correspondent orientation to the remote contact.
	 * 
	 * @param configuration
	 *            The configuration to be attended.
	 */
	private void attendSimulationStartConfiguration(
			SimulationStartConfiguration configuration) {
		// obtains the time of simulation
		double simulation_time = configuration.getSimulation_time();

		// if the simulator is in the CONFIGURING state
		if (simulator.getState() == SimulatorStates.CONFIGURING) {
			// starts the simulation
			simulator.startSimulation(simulation_time);

			// sends an empty orientation to the sender of configuration
			this.connection.send(new Orientation().fullToXML(0));
		}
		// if not, sends an orientation reporting error
		else
			this.connection.send(new Orientation(
					"Simulator already simulating.").fullToXML(0));
	}

	/**
	 * Creates the agent daemons (perception and action daemons) for the given
	 * agent, starts these daemons and adds them to the simulator.
	 * 
	 * @param agent
	 *            The agent of which daemons are to be created.
	 * @return The number of the UDP socket created for the agent daemons.
	 */
	private int createAndStartAgentDaemons(Agent agent) {
		// creates a perception daemon
		PerceptionDaemon perception_daemon = new PerceptionDaemon(agent
				.getObjectId()
				+ "'s perception daemon", agent);

		// creates an action daemon
		ActionDaemon action_daemon = new ActionDaemon(agent.getObjectId()
				+ "'s action daemon", agent);

		// creates a new agent connection
		Connection connection = null;
		if (simulator instanceof RealTimeSimulator)
			connection = new AgentUDPConnection(agent.getObjectId()
					+ "'s connection", perception_daemon.BUFFER,
					action_daemon.BUFFER);
		else
			connection = new ServerSideAgentTCPConnection(agent.getObjectId()
					+ "'s connection", perception_daemon.BUFFER,
					action_daemon.BUFFER);

		// configures the perception and action daemons' connection
		perception_daemon.setConnection(connection);
		action_daemon.setConnection(connection);

		// if the simulator is a real time one, lets both daemons work
		if (simulator instanceof RealTimeSimulator) {
			perception_daemon.setIs_blocked(false);
			action_daemon.setIs_blocked(false);
		}

		// starts the daemons
		boolean socket_exception_happened = false;
		do {
			try {
				int socket_number = this.socket_number_generator
						.generateSocketNumber();

				perception_daemon.start(socket_number);
				action_daemon.start(socket_number);

				socket_exception_happened = false;
			} catch (IOException e) {
				socket_exception_happened = true;
			}
		} while (socket_exception_happened);

		// adds the daemons to the simulator
		simulator.addPerceptionDaemon(perception_daemon);
		simulator.addActionDaemon(action_daemon);

		// returns the obtained number for the sockets of the daemons
		return perception_daemon.getConnectionSocketNumber();
	}

	/**
	 * Creates the metric daemon for the given metric, starts the daemon and
	 * adds it to the simulator.
	 * 
	 * @param metric
	 *            The metric of which daemon is to be created.
	 * @param cycle_duration
	 *            The duration, in seconds, of a cycle of measurement of the
	 *            given metric.
	 * @return The number of the UDP socket created for the metric daemon.
	 */
	private int createAndStartMetricDaemon(Metric metric, double cycle_duration) {
		// creates the metric daemon
		StringBuffer thread_name_buffer = new StringBuffer();
		thread_name_buffer.append("metric " + metric.getClass().getName()
				+ "'s daemon");
		MetricDaemon metric_daemon = new MetricDaemon(thread_name_buffer
				.toString(), metric, cycle_duration);

		// starts the daemon
		boolean socket_exception_happened = false;
		do {
			try {
				metric_daemon.start(this.socket_number_generator
						.generateSocketNumber());
				socket_exception_happened = false;
			} catch (IOException e) {
				socket_exception_happened = true;
			}
		} while (socket_exception_happened);

		// adds the daemon to the simulator
		simulator.addMetricDaemon(metric_daemon);

		// if the simulator is already simulating, starts the metric daemon's
		// clock
		if (simulator.getState() == SimulatorStates.SIMULATING)
			metric_daemon.startMetric();

		// returns the obtained number for the sockets of the daemons
		return metric_daemon.getConnectionSocketNumber();
	}

	/**
	 * Resets the main daemon's connection.
	 * 
	 * @throws IOException
	 */
	public void resetConnection() throws IOException {
		synchronized (simulator) {
			simulator.getState(); // synchronization

			this.BUFFER.clear();
			int local_socket_number = this.connection.getSocketNumber();
			this.connection.stopActing();

			// waits until the connection is finished
			while (this.connection.isAlive())
				;

			// creates a new connection and starts it
			this.connection = new ServerSideTCPConnection(this.getName()
					+ "'s connection", this.BUFFER);
			this.connection.start(local_socket_number);
		}
	}

	public void start(int local_socket_number) throws IOException {
		// super class code execution
		super.start(local_socket_number);

		// starts the socket number generator
		this.socket_number_generator = new SocketNumberGenerator(
				local_socket_number);
	}

	public void stopActing() {
		// used by AspectJ
		super.stopActing();
	}

	/**
	 * @developer New configurations must change this method.
	 * @modeler This method must be modeled.
	 */
	public void run() {
		while (this.is_active) {
			// tries to obtain a string message from the buffer while this
			// daemon is supposed to work
			String message = null;
			while (message == null && this.is_active){
				//try {
					message = this.BUFFER.remove();
					//Thread.sleep(1);
				//} catch (InterruptedException e) {
					// TODO Auto-generated catch block
				//	e.printStackTrace();
				//}				
				}

			synchronized (simulator) {
				simulator.getState(); // synchronization

				// if the message is not null and this daemon is active
				if (message != null && this.is_active) {
					// obtains the configuration from the string message
					Configuration configuration = null;

					try {
						configuration = ConfigurationTranslator
								.getConfiguration(message);
					} catch (SAXException e) {
						this.connection
								.send(new Orientation(
										"XML error. Configuration is wrongly formatted.")
										.fullToXML(0));
						e.printStackTrace(); // traced XML error
					} catch (ParserConfigurationException e) {
						this.connection
								.send(new Orientation(
										"XML error. Configuration is wrongly formatted.")
										.fullToXML(0));
						e.printStackTrace(); // traced XML error
					} catch (IOException e) {
						this.connection
								.send(new Orientation(
										"IO error. Configuration could not be obtained from source.")
										.fullToXML(0));
						e.printStackTrace(); // traced IO error
					} catch (NodeNotFoundException e) {
						this.connection
								.send(new Orientation(
										"Content error. Given node id does not exist.")
										.fullToXML(0));
						e.printStackTrace(); // traced Node XML error
					} catch (EdgeNotFoundException e) {
						this.connection.send(new Orientation(
								"Content error. Given edge id does not exist.")
								.fullToXML(0));
						e.printStackTrace(); // traced Edge XML error
					} catch (EnvironmentNotValidException e) {
						this.connection
								.send(new Orientation(
										"Content error. Given environment is wrongly formatted.")
										.fullToXML(0));
						e.printStackTrace(); // traced Environment XML error
					}

					// if configuration is still not valid, tries to obtain it
					// as an "agent creation" configuration
					if (configuration == null)
						try {
							configuration = ConfigurationTranslator
									.getAgentCreationConfiguration(message,
											simulator.getEnvironment()
													.getGraph());
						} catch (SAXException e) {
							this.connection
									.send(new Orientation(
											"XML error. Configuration is wrongly formatted.")
											.fullToXML(0));
							e.printStackTrace(); // traced XML error
						} catch (IOException e) {
							this.connection
									.send(new Orientation(
											"IO error. Configuration could not be obtained from source.")
											.fullToXML(0));
							e.printStackTrace(); // traced IO error
						} catch (NodeNotFoundException e) {
							this.connection
									.send(new Orientation(
											"Content error. Given node id does not exist.")
											.fullToXML(0));
							e.printStackTrace(); // traced Node XML error
						} catch (EdgeNotFoundException e) {
							this.connection
									.send(new Orientation(
											"Content error. Given edge id does not exist.")
											.fullToXML(0));
							e.printStackTrace(); // traced Edge XML error
						}
					// if the configuration is an "environment creation"
					if (configuration instanceof EnvironmentCreationConfiguration) {
						// if the simulator is not simulating yet
						if (simulator.getState() == SimulatorStates.CONFIGURING)
							this
									.attendEnvironmentCreationConfiguration((EnvironmentCreationConfiguration) configuration);
						else
							this.connection.send(new Orientation(
									"Simulator already simulating.")
									.fullToXML(0));
					}

					// else if the configuration is an "agent creation"
					else if (configuration instanceof AgentCreationConfiguration) {
						// if the environment of the simulator is valid
						if (simulator.getEnvironment() != null)
							this
									.attendAgentCreationConfiguration((AgentCreationConfiguration) configuration);
						else
							this.connection
									.send(new Orientation(
											"Environment (graph + societies) not set yet.")
											.fullToXML(0));
					}

					// else if the configuration is a "metric creation"
					else if (configuration instanceof MetricCreationConfiguration) {
						// if the environment of the simulator is valid
						if (simulator.getEnvironment() != null)
							this
									.attendMetricCreationConfiguration((MetricCreationConfiguration) configuration);
						else
							this.connection
									.send(new Orientation(
											"Environment (graph + societies) not set yet.")
											.fullToXML(0));
					}

					// else if the configuration is a "simulation start"
					else if (configuration instanceof SimulationStartConfiguration) {
						// if the environment of the simulator is valid
						if (simulator.getEnvironment() != null)
							this
									.attendSimulationStartConfiguration((SimulationStartConfiguration) configuration);
						else
							this.connection
									.send(new Orientation(
											"Environment (graph + societies) not set yet.")
											.fullToXML(0));
					}

					// else if the configuration is an "agent death"
					else if (configuration instanceof AgentDeathConfiguration) {
						// if the environment of the simulator is valid
						if (simulator.getEnvironment() != null)
							this
									.attendAgentDeathConfiguration((AgentDeathConfiguration) configuration);
						else
							this.connection
									.send(new Orientation(
											"Environment (graph + societies) not set yet.")
											.fullToXML(0));
					}

					// else if the configuration is an "event collecting"
					else if (configuration instanceof EventsCollectingConfiguration) {
						this
								.attendEventCollectingConfiguration((EventsCollectingConfiguration) configuration);
					}

					// developer: new configurations must add code here
				}
			}
		}
	}
	
	public void update() {
		if(this.is_active) {
			// tries to obtain a string message from the buffer while this
			// daemon is supposed to work
			String message = null;	
			message = this.BUFFER.remove();	
			

			synchronized (simulator) {
				simulator.getState(); // synchronization

				// if the message is not null and this daemon is active
				if (message != null && this.is_active) {
					// obtains the configuration from the string message
					Configuration configuration = null;

					try {
						configuration = ConfigurationTranslator
								.getConfiguration(message);
					} catch (SAXException e) {
						this.connection
								.send(new Orientation(
										"XML error. Configuration is wrongly formatted.")
										.fullToXML(0));
						e.printStackTrace(); // traced XML error
					} catch (ParserConfigurationException e) {
						this.connection
								.send(new Orientation(
										"XML error. Configuration is wrongly formatted.")
										.fullToXML(0));
						e.printStackTrace(); // traced XML error
					} catch (IOException e) {
						this.connection
								.send(new Orientation(
										"IO error. Configuration could not be obtained from source.")
										.fullToXML(0));
						e.printStackTrace(); // traced IO error
					} catch (NodeNotFoundException e) {
						this.connection
								.send(new Orientation(
										"Content error. Given node id does not exist.")
										.fullToXML(0));
						e.printStackTrace(); // traced Node XML error
					} catch (EdgeNotFoundException e) {
						this.connection.send(new Orientation(
								"Content error. Given edge id does not exist.")
								.fullToXML(0));
						e.printStackTrace(); // traced Edge XML error
					} catch (EnvironmentNotValidException e) {
						this.connection
								.send(new Orientation(
										"Content error. Given environment is wrongly formatted.")
										.fullToXML(0));
						e.printStackTrace(); // traced Environment XML error
					}

					// if configuration is still not valid, tries to obtain it
					// as an "agent creation" configuration
					if (configuration == null)
						try {
							configuration = ConfigurationTranslator
									.getAgentCreationConfiguration(message,
											simulator.getEnvironment()
													.getGraph());
						} catch (SAXException e) {
							this.connection
									.send(new Orientation(
											"XML error. Configuration is wrongly formatted.")
											.fullToXML(0));
							e.printStackTrace(); // traced XML error
						} catch (IOException e) {
							this.connection
									.send(new Orientation(
											"IO error. Configuration could not be obtained from source.")
											.fullToXML(0));
							e.printStackTrace(); // traced IO error
						} catch (NodeNotFoundException e) {
							this.connection
									.send(new Orientation(
											"Content error. Given node id does not exist.")
											.fullToXML(0));
							e.printStackTrace(); // traced Node XML error
						} catch (EdgeNotFoundException e) {
							this.connection
									.send(new Orientation(
											"Content error. Given edge id does not exist.")
											.fullToXML(0));
							e.printStackTrace(); // traced Edge XML error
						}
					// if the configuration is an "environment creation"
					if (configuration instanceof EnvironmentCreationConfiguration) {
						// if the simulator is not simulating yet
						if (simulator.getState() == SimulatorStates.CONFIGURING)
							this
									.attendEnvironmentCreationConfiguration((EnvironmentCreationConfiguration) configuration);
						else
							this.connection.send(new Orientation(
									"Simulator already simulating.")
									.fullToXML(0));
					}

					// else if the configuration is an "agent creation"
					else if (configuration instanceof AgentCreationConfiguration) {
						// if the environment of the simulator is valid
						if (simulator.getEnvironment() != null)
							this
									.attendAgentCreationConfiguration((AgentCreationConfiguration) configuration);
						else
							this.connection
									.send(new Orientation(
											"Environment (graph + societies) not set yet.")
											.fullToXML(0));
					}

					// else if the configuration is a "metric creation"
					else if (configuration instanceof MetricCreationConfiguration) {
						// if the environment of the simulator is valid
						if (simulator.getEnvironment() != null)
							this
									.attendMetricCreationConfiguration((MetricCreationConfiguration) configuration);
						else
							this.connection
									.send(new Orientation(
											"Environment (graph + societies) not set yet.")
											.fullToXML(0));
					}

					// else if the configuration is a "simulation start"
					else if (configuration instanceof SimulationStartConfiguration) {
						// if the environment of the simulator is valid
						if (simulator.getEnvironment() != null)
							this
									.attendSimulationStartConfiguration((SimulationStartConfiguration) configuration);
						else
							this.connection
									.send(new Orientation(
											"Environment (graph + societies) not set yet.")
											.fullToXML(0));
					}

					// else if the configuration is an "agent death"
					else if (configuration instanceof AgentDeathConfiguration) {
						// if the environment of the simulator is valid
						if (simulator.getEnvironment() != null)
							this
									.attendAgentDeathConfiguration((AgentDeathConfiguration) configuration);
						else
							this.connection
									.send(new Orientation(
											"Environment (graph + societies) not set yet.")
											.fullToXML(0));
					}

					// else if the configuration is an "event collecting"
					else if (configuration instanceof EventsCollectingConfiguration) {
						this
								.attendEventCollectingConfiguration((EventsCollectingConfiguration) configuration);
					}

					// developer: new configurations must add code here
				}
			}
		}
		
	}
}