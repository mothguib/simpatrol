/* MainDaemon.java */

/* The package of this class. */
package control.daemon;

/* Imported classes and/or interfaces. */
import java.io.IOException;
import java.net.SocketException;
import java.util.HashSet;
import java.util.Set;
import javax.xml.parsers.ParserConfigurationException;
import org.xml.sax.SAXException;
import util.udp.SocketNumberGenerator;
import view.connection.AgentConnection;
import model.agent.Agent;
import model.agent.ClosedSociety;
import model.agent.OpenSociety;
import model.agent.SeasonalAgent;
import model.agent.Society;
import model.graph.Graph;
import control.configuration.AgentCreationConfiguration;
import control.configuration.Configuration;
import control.configuration.GraphCreationConfiguration;
import control.configuration.Orientation;
import control.configuration.SimulationStartConfiguration;
import control.configuration.SocietiesCreationConfiguration;
import control.simulator.Simulator;
import control.simulator.SimulatorStates;
import control.translator.ConfigurationTranslator;

/** Implements the main daemon of SimPatrol, the one that
 *  listens to the configurations to the simulation of the
 *  patrolling task.
 *  
 *  @developer New configurations must change this class.
 *  @modelled This class must have its behaviour modelled. */
public final class MainDaemon extends Daemon {
	/* Attributes. */
	/** Registers if the daemon must stop working. */
	private boolean stop_working;
	
	/** The simulator of SimPatrol. */
	private Simulator simulator;
	
	/** A generator of numbers for socket connections. */
	private SocketNumberGenerator socket_number_generator;
	
	/* Methods. */
	/** Constructor.
	 * 
	 *  @param thread_name The name of the thread of the daemon.
	 *  @param simulator The simulator of SimPatrol. */
	public MainDaemon(String thread_name, Simulator simulator) {
		super(thread_name);
		this.stop_working = false;
		this.simulator = simulator;
		this.socket_number_generator = null;
	}
	
	/** Indicates that the daemon must stop working. */
	public void stopWorking() {
		this.stop_working = true;
		
		// stops its connection
		this.connection.stopWorking();
		
		// screen message
		System.out.println("[SimPatrol.MainDaemon] Stopped working.");
	}
	
	/** Attends a given "graph creation" configuration, sending the
	 *  correspondent orientation to the remote contact.
	 * 
	 *  @param configuration The configuration to be attended. 
	 *  @throws IOException */
	private void attendGraphCreationConfiguration(GraphCreationConfiguration configuration) throws IOException {
		// obtains the graph from the configuration
		Graph graph = configuration.getGraph();
		
		// sets the graph to the simulator
		this.simulator.setGraph(graph);
		
		// sends an empty orientation to the remote sender of the configuration
		this.connection.send(new Orientation().toXML(0), configuration.getSender_address(), configuration.getSender_socket());
		
		// screen message
		System.out.println("[SimPatrol.MainDaemon] Graph obtained:");
		System.out.print(graph.toXML(0));
	}
	
	/** Attends a given "societies creation" configuration, sending the
	 *  correspondent orientation to the remote contact.
	 * 
	 *  @param configuration The configuration to be attended. 
	 *  @throws IOException */
	private void attendSocietiesCreationConfiguration(SocietiesCreationConfiguration configuration) throws IOException {
		// obtains the societies from the configuration
		Society[] societies = configuration.getSocieties();
		
		// if the obtained societies are valid
		if(this.areValidSocieties(societies)) {
			// the orientation to be sent to the remote sender of the configuration
			Orientation orientation = new Orientation();
			
			// for each society
			for(int i = 0; i < societies.length; i++) {
				// adds it to the simulator
				this.simulator.addSociety(societies[i]);
				
				// obtains its agents
				Agent[] agents = societies[i].getAgents();
				
				// for each agent
				for(int j = 0; j < agents.length; j++) {
					// creates and starts its perception and action daemons
					int socket_number = this.createAndStartAgentDaemons(agents[j]);
					
					// fills the orientation
					orientation.addItem(socket_number, agents[j].getObjectId());
				}
			}
			
			// sends the created orientation to the remote contact
			this.connection.send(orientation.toXML(0), configuration.getSender_address(), configuration.getSender_socket());
			
			// screen message
			System.out.println("[SimPatrol.MainDaemon] Societies obtained:");
			for(int i = 0; i < societies.length; i++) System.out.print(societies[i].toXML(0));
		}
		// if not, sends an orientation reporting the error
		else this.connection.send(new Orientation("Duplicated society id, or agent id.").toXML(0), configuration.getSender_address(), configuration.getSender_socket());
	}
	
	/** Attends a given "agent creation" configuration, sending the
	 *  correspondent orientation to the remote contact.
	 * 
	 *  @param configuration The configuration to be attended. 
	 *  @throws IOException */
	private void attendAgentCreationConfiguration(AgentCreationConfiguration configuration) throws IOException {
		// obtains the agent from the configuration
		Agent agent = configuration.getAgent();
		
		// if the obtained agent is valid
		SeasonalAgent[] temp_agents = {(SeasonalAgent) agent};
		Society[] temp_societies = {new OpenSociety("", temp_agents)};
		temp_societies[0].setObjectId(this.getName());
		if(this.areValidSocieties(temp_societies)) {
			// obtains the id of its society
			String society_id = configuration.getSociety_id();
			
			// obtains the societies of the simulator
			Society[] simulator_societies = this.simulator.getSocieties();
			
			// finds the society with the given id
			Society society = null;
			for(int i = 0; i < simulator_societies.length; i++)
				if(simulator_societies[i].getObjectId().equals(society_id)) {
					society = simulator_societies[i];
					break;
				}
			
			// if the society was found
			if(society != null) {
				// if the simulator is in the CONFIGURING state
				if(this.simulator.getState() == SimulatorStates.CONFIGURING) {
					// if the society is a closed one, casts the agent to a perpetual one
					if(society instanceof ClosedSociety)
						agent = ((SeasonalAgent) agent).getPerpetualVersion();
					
					// adds the agent to the society
					society.addAgent(agent);
					
					// creates and starts its perception and action daemons
					int socket_number = this.createAndStartAgentDaemons(agent);
					
					// sends an orientation to the sender of the configuration
					Orientation orientation = new Orientation();
					orientation.addItem(socket_number, agent.getObjectId());
					this.connection.send(orientation.toXML(0), configuration.getSender_address(), configuration.getSender_socket());
					
					// screen message
					System.out.println("[SimPatrol.MainDaemon] Agent obtained:");
					System.out.print(agent.toXML(0));
				}
				// if not, if the society is an open one
				else if(society instanceof OpenSociety) {
					// adds the agent to the society
					society.addAgent(agent);
					
					// creates and starts its perception and action daemons
					int socket_number = this.createAndStartAgentDaemons(agent);
					
					// sends an orientation to the sender of the configuration
					Orientation orientation = new Orientation();
					orientation.addItem(socket_number, agent.getObjectId());
					this.connection.send(orientation.toXML(0), configuration.getSender_address(), configuration.getSender_socket());
					
					// screen message
					System.out.println("[SimPatrol.MainDaemon] Agent obtained:");
					System.out.print(agent.toXML(0));
				}
				// if not, sends an orientation reporting error
				else this.connection.send(new Orientation("Closed society.").toXML(0), configuration.getSender_address(), configuration.getSender_socket());
			}
			// if not, sends an orientation reporting the error
			else this.connection.send(new Orientation("Society not found.").toXML(0), configuration.getSender_address(), configuration.getSender_socket());
		}
		// if not, sends an orientation reporting the error
		else this.connection.send(new Orientation("Duplicated agent.").toXML(0), configuration.getSender_address(), configuration.getSender_socket());
	}
	
	/** Attends a given "simultion start" configuration, sending the
	 *  correspondent orientation to the remote contact.
	 * 
	 *  @param configuration The configuration to be attended. 
	 *  @throws IOException */
	private void attendSimulationStartConfiguration(SimulationStartConfiguration configuration) throws IOException {
		// obtains the time of simulation
		int simulation_time = configuration.getSimulation_time();
		
		// if the simulator is in the CONFIGURING state
		if(this.simulator.getState() == SimulatorStates.CONFIGURING) {
			// starts the simulation
			this.simulator.startSimulation(simulation_time);
			
			// sends an empty orientation to the sender of configuration
			this.connection.send(new Orientation().toXML(0), configuration.getSender_address(), configuration.getSender_socket());
			
			// screen message
			System.out.println("[SimPatrol.MainDaemon] Simulation started:");
			System.out.println("Planned simulation time: " + configuration.getSimulation_time());			
		}
		// if not, sends an orientation reporting error
		else this.connection.send(new Orientation("Simulator already simulating.").toXML(0), configuration.getSender_address(), configuration.getSender_socket());
	}
	
	/** Verifies if one of the given societies already exists in
	 *  the simulator, or there's some repeated society among
	 *  the given ones.
	 *  
	 *  Also verifies if one of the agents of the given societies already
	 *  exists in the simulator, or there's some repeated agent among
	 *  the given ones.  
	 *  
	 *  @param societies The societies to be verified. 
	 *  @return TRUE, if the societies are valid, FALSE if not. */
	private boolean areValidSocieties(Society[] societies) {
		// holds all the societies as a set
		Set<Society> societies_set = new HashSet<Society>();
		
		// holds all the agents as a set
		Set<Agent> agents_set = new HashSet<Agent>();
		
		// obtains the societies from the simulator
		Society[] simulator_societies = this.simulator.getSocieties();
		
		// for each society of the simulator
		for(int i = 0; i < societies.length; i++) {
			// adds it to the set of societies
			societies_set.add(simulator_societies[i]);
			
			// adds its agents to the set of agents
			Agent[] agents = simulator_societies[i].getAgents();
			for(int j = 0; j < agents.length; j++)
				agents_set.add(agents[j]);
		}
		
		// for each given society
		for(int i = 0; i < societies.length; i++)
			// if the current given society already exists in the set of societies,
			// return FALSE
			if(societies_set.contains(societies[i])) return false;
			// if not
			else {
				// adds it to the set of societies
				societies_set.add(societies[i]);

				// obtains its agents
				Agent[] agents = societies[i].getAgents();
				
				// for each agent
				for(int j = 0; j < agents.length; j++)
					// if the current agent already exists in the set of agents,
					// return false
					if(agents_set.contains(agents[j])) return false;
				    // if not, adds it to the set of agents
					else agents_set.add(agents[j]);
			}
		
		// default answer
		return true;
	}
	
	/** Creates the agent daemons (perception and action daemons) for the given agent,
	 *  starts these daemons and adds them to the simulator.
	 *  
	 *  @param agent The agent whose daemons are to be created.
	 *  @return The number of the UDP socket created for the agent daemons. */
	private int createAndStartAgentDaemons(Agent agent) {
		// creates a perception daemon
		PerceptionDaemon perception_daemon = new PerceptionDaemon(agent.getObjectId() + "'s perception daemon", agent, this.simulator.getGraph(), this.simulator.getCycle_duration());
		
		// creates an action daemon
		ActionDaemon action_daemon = new ActionDaemon(agent.getObjectId() + "'s action daemon", agent);
		
		// creates a new agent connection
		AgentConnection connection = new AgentConnection(agent.getObjectId() + "'s connection", perception_daemon.buffer, action_daemon.buffer);
		
		// configures the perception and action daemons' connection
		perception_daemon.setConnection(connection);
		action_daemon.setConnection(connection);
		
		// starts the daemons
		boolean socket_exception_happened = true;
		do {
			try {
				perception_daemon.start(this.socket_number_generator.generateSocketNumber());
				action_daemon.start(this.socket_number_generator.generateSocketNumber());
				
				socket_exception_happened = false;
			} catch (SocketException e) {
				socket_exception_happened = true;
			}
						
		} while(socket_exception_happened);
		
		// adds the daemons to the simulator
		this.simulator.addPerceptionDaemon(perception_daemon);
		this.simulator.addActionDaemon(action_daemon);
		
		// returns the obtained number for the sockets of the daemons
		return perception_daemon.getUDPSocketNumber();
	}
	
	public void start(int local_socket_number) throws SocketException {
		// starts the socket number generator
		this.socket_number_generator = new SocketNumberGenerator(local_socket_number);
		
		// calls the super method
		super.start(local_socket_number);
	}
	
	/** @developer New configuration must change this method.
	 *  @modeller This method must me modelled. */
	public void run() {
		// 1st. listens to a configuration of "graph creation"
		while(this.simulator.getGraph() == null) {
			// screen message
			System.out.println("[SimPatrol.MainDaemon] Listening to a graph creation configuration...");
			
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
			
			// if the configuration is a "graph creation" configuration, attends it
			if(configuration instanceof GraphCreationConfiguration)
				try { this.attendGraphCreationConfiguration((GraphCreationConfiguration) configuration); }
				catch (IOException e) { e.printStackTrace(); }
			else
				try { this.connection.send(new Orientation("Waiting for a graph creation configuration.").toXML(0), configuration.getSender_address(), configuration.getSender_socket()); }
				catch (IOException e) { e.printStackTrace(); }
		}
		
		// 2nd.
		// forever, listens a message of "simulation start" or
		// "societies creation", or "agent creation"
		while(!this.stop_working) {
			// screen message
			System.out.println("[SimPatrol.MainDaemon] Listening to a configuration...");
			
			// obtains a string message from the buffer
			String message = null;
			while(message == null)
				message = this.buffer.remove();
			
			// tries to obtain the configuration from the string message
			Configuration configuration = null;
			try { configuration = ConfigurationTranslator.getConfiguration(message); }
			catch (ParserConfigurationException e) { e.printStackTrace(); }
			catch (SAXException e) { e.printStackTrace(); }
			catch (IOException e) { e.printStackTrace(); }
			
			// if the configuration is a "simulation start" configuration, attends it
			if(configuration instanceof SimulationStartConfiguration)
				try { this.attendSimulationStartConfiguration((SimulationStartConfiguration) configuration); }
				catch (IOException e) { e.printStackTrace(); }
			else {
				SocietiesCreationConfiguration societies_creation_configuration = null;
				try { societies_creation_configuration = ConfigurationTranslator.getSocietiesCreationConfiguration(message, this.simulator.getGraph()); }
				catch (ParserConfigurationException e) { e.printStackTrace(); }
				catch (SAXException e) { e.printStackTrace(); }
				catch (IOException e) { e.printStackTrace(); }
				
				// if the read configuration is valid, attends it
				if(societies_creation_configuration != null)
					try { this.attendSocietiesCreationConfiguration(societies_creation_configuration); }
					catch (IOException e) { e.printStackTrace(); }
				else {
					AgentCreationConfiguration agent_creation_configuration = null;
					try { agent_creation_configuration = ConfigurationTranslator.getAgentCreationConfiguration(message, this.simulator.getGraph()); }
					catch (ParserConfigurationException e) { e.printStackTrace(); }
					catch (SAXException e) { e.printStackTrace(); }
					catch (IOException e) { e.printStackTrace(); }
					
					// if the configuration is valid, attends it
					if(agent_creation_configuration != null)
						try { this.attendAgentCreationConfiguration(agent_creation_configuration); }
						catch (IOException e) { e.printStackTrace(); }
					else
						try { this.connection.send(new Orientation("Waiting for a valid configuration.").toXML(0), configuration.getSender_address(), configuration.getSender_socket()); }
						catch (IOException e) { e.printStackTrace(); }					
				}
			}
		}
	}
}