/* MainDaemon.java */

/* The package of this class. */
package control.daemon;

/* Imported classes and/or interfaces. */
import java.io.IOException;
import java.net.SocketException;
import javax.xml.parsers.ParserConfigurationException;
import org.xml.sax.SAXException;
import view.connection.AgentConnection;
import view.message.Message;
import model.Environment;
import model.agent.Agent;
import model.agent.OpenSociety;
import model.agent.SeasonalAgent;
import model.agent.Society;
import control.configuration.AgentCreationConfiguration;
import control.configuration.EnvironmentCreationConfiguration;
import control.configuration.Orientation;
import control.configuration.SimulationConfiguration;
import control.simulator.Simulator;
import control.simulator.SimulatorStates;
import control.translator.MessageTranslator;

/** Implements the main daemon of SimPatrol, the one that
 *  controls the environment remote submission, as well as the creation
 *  of agents and starting of the simulation. */
public final class MainDaemon extends Daemon {
	/* Attributes. */
	/** Registers if the daemon shall stop working. */
	private boolean stop_working;
	
	/** The simulator of SimPatrol. */
	private Simulator simulator;
	
	/* Methods. */
	/** Constructor.
	 *  @param name The name of the thread of the daemon.
	 *  @param simulator The SimPatrol's simulator.
	 *  @throws SocketException */
	public MainDaemon(String name, Simulator simulator) throws SocketException {
		super(name);
		
		this.stop_working = false;		
		this.simulator = simulator;
	}
	
	/** Indicates that the daemon must stop working. */
	public void stopWorking() {
		this.stop_working = true;
		
		// stops its connection
		this.connection.stopWorking();
		
		// screen message
		System.out.println("[SimPatrol.MainDaemon] Stopped working.");
	}
	
	/** Treats a given message eventually containing
	 *  an "environment creation configuration".
	 *  @param message The message to be treated. 
	 *  @throws IOException */
	private void treatEnvironmentCreationConfiguration(Message message) throws IOException {
		// if the content of the message is
		// an "environment creation configuration"
		if(message.getContent() instanceof EnvironmentCreationConfiguration) {
			// obtains the "environment creation configuration"
			EnvironmentCreationConfiguration configuration = (EnvironmentCreationConfiguration) message.getContent();
			
			// obtains the environment
			Environment environment = configuration.getEnvironment();
			
			// screen message
			System.out.println("[SimPatrol.MainDaemon] Environment obtained:");
			System.out.print(environment.toXML(0, 0));
			
			// sets the environment of the simulator
			this.simulator.setEnvironment(environment);
			
			// creates the orientation to the remote contact
			Orientation orientation = new Orientation();
			
			// obtains the societies of the environment
			Society[] societies = environment.getSocieties();
			
			// for each society of the environment
			for(int i = 0; i < societies.length; i++) {
				// obtains its agents
				Agent[] agents = societies[i].getAgents();
				
				// for each agent
				for(int j = 0; j < agents.length; j++) {
					// obtains its perception and action daemons
					AgentDaemon[] agent_daemons = this.getAgentDaemons(agents[j]);
					
					// adds its agent daemons to the simulator
					this.simulator.addPerceptionDaemon((PerceptionDaemon) agent_daemons[0]);
					this.simulator.addActionDaemon((ActionDaemon) agent_daemons[1]);
					
					// fills the orientation
					orientation.addItem(agent_daemons[0].getUDPSocketNumber(), agents[j].getObjectId());
				}
			}
			
			// sends the orientation as a message to the remote contact
			Message orientation_message = new Message(orientation);
			this.connection.send(orientation_message.toXML(0), configuration.getSender_address(), configuration.getSender_socket());
		}
	}

	/** Treats a given message eventually containing
	 *  a "simulation configuration".
	 *  @param message The message to be treated. 
	 *  @throws IOException */
	private void treatSimulationConfiguration(Message message) throws IOException {
		// if the content of the message is
		// a "simulation configuration"
		if(message.getContent() instanceof SimulationConfiguration) {
			// obtains the "simulation configuration"
			SimulationConfiguration configuration = (SimulationConfiguration) message.getContent();
			
			// if the simulator is still in the CONFIGURING state
			if(this.simulator.getState() == SimulatorStates.CONFIGURING) {
				// starts the simulation
				this.simulator.startSimulation(configuration.getSimulation_time());
				
				// screen message
				System.out.println("[SimPatrol.MainDaemon] Simulation started:");
				System.out.println("Planned simulation time: " + configuration.getSimulation_time());
			}
			
			// sends an empty orientation message to the remote contact
			Message orientation_message = new Message(new Orientation());
			this.connection.send(orientation_message.toXML(0), configuration.getSender_address(), configuration.getSender_socket());			
		}
	}

	/** Treats a given message eventually containing
	 *  an "agent creation configuration".
	 *  @param message The message to be treated. 
	 *  @throws IOException */
	private void treatAgentCreationConfiguration(Message message) throws IOException {
		// if the content of the message is
		// an "agent creation configuration"
		if(message.getContent() instanceof AgentCreationConfiguration) {
			// obtains the "agent creation configuration"
			AgentCreationConfiguration configuration = (AgentCreationConfiguration) message.getContent();
			
			// creates the orientation to the remote contact
			Orientation orientation = new Orientation();
			
			// obtains the agent
			Agent agent = configuration.getAgent();
			
			// obtains the id of the society where the agent must be added
			String society_id = configuration.getSociety_id();
			
			// tries to find the society with the obtained id
			Society society = null;
			Society[] societies = this.simulator.getEnvironment().getSocieties();			
			for(int i = 0; i < societies.length; i++)
				if(societies[i].getObjectId().equals(society_id))
					society = societies[i];
			
			// if the society exists and is an open society
			if(society instanceof OpenSociety) {
				// adds the new agent to the society
				((OpenSociety) society).addAgent((SeasonalAgent) agent);
				
				// obtains its perception and action daemons
				AgentDaemon[] agent_daemons = this.getAgentDaemons(agent);
				
				// adds its agent daemons to the simulator
				this.simulator.addPerceptionDaemon((PerceptionDaemon) agent_daemons[0]);
				this.simulator.addActionDaemon((ActionDaemon) agent_daemons[1]);
				
				// fills the orientation
				orientation.addItem(agent_daemons[0].getUDPSocketNumber(), agent.getObjectId());
				
				// screen message
				System.out.println("[SimPatrol.MainDaemon] Agent added:");
				System.out.print(agent.toXML(0));
			}
			// else, fills the orientation with a wrong socket number (-1)		
			else orientation.addItem(-1, agent.getObjectId());
			
			// sends the orientation as a message to the remote contact
			Message orientation_message = new Message(orientation);
			this.connection.send(orientation_message.toXML(0), configuration.getSender_address(), configuration.getSender_socket());
		}
	}
	
	/** Obtains the agent daemons for the given agent. */
	private AgentDaemon[] getAgentDaemons(Agent agent) {
		// the answer for the method
		AgentDaemon[] answer = new AgentDaemon[2];
		
		// creates a perception daemon
		PerceptionDaemon perception_daemon = new PerceptionDaemon(agent.getObjectId() + "'s perception daemon", agent);
		
		// creates an action daemon
		ActionDaemon action_daemon = new ActionDaemon(agent.getObjectId() + "'s perception daemon", agent);
		
		// creates a new agent connection
		AgentConnection connection = new AgentConnection(agent.getObjectId() + "'s connection", perception_daemon.getBuffer(), action_daemon.getBuffer());
		
		// configures the perception and action daemons' connection
		perception_daemon.setConnection(connection);
		action_daemon.setConnection(connection);
		
		// adds the daemons to the answer
		answer[0] = perception_daemon;
		answer[1] = action_daemon;
		
		// returns the answer
		return answer;
	}
	
	public void run() {
		// 1st. listens to some message of "environment configuration creation"
		while(this.simulator.getEnvironment() == null) {
			// screen message
			System.out.println("[SimPatrol.MainDaemon] Listening to some environment...");
			
			// obtains a string message from the buffer
			String str_message = null;
			while(str_message == null)
				str_message = this.buffer.remove();
			
			// obtains the message object
			Message message = null;
			try { message = MessageTranslator.getMessage(str_message); }
			catch (ParserConfigurationException e) { e.printStackTrace(); }
			catch (SAXException e) { e.printStackTrace(); }
			catch (IOException e) { e.printStackTrace();}
			
			// if it's a valid message
			if(message != null)
				try { this.treatEnvironmentCreationConfiguration(message); }
				catch (IOException e) { this.simulator.setEnvironment(null); };
		}
		
		// 2nd.
		// forever, listen to some message of "agent creation configuration"
		// or "simulation configuration"
		while(!this.stop_working) {
			// screen message
			System.out.println("[SimPatrol.MainDaemon] Listening to some configuration...");
			
			// obtains a string message from the buffer
			String str_message = null;
			while(str_message == null)
				str_message = this.buffer.remove();
			
			// obtains the message object and
			// tries to obtain it as a "simulation configuration" message
			Message message = null;
			try { message = MessageTranslator.getMessage(str_message); }
			catch (ParserConfigurationException e) { e.printStackTrace(); }
			catch (SAXException e) { e.printStackTrace(); }
			catch (IOException e) { e.printStackTrace(); }
			
			// if succeded (message is not null), treats the "simulation configuration"
			if(message != null)
				try { this.treatSimulationConfiguration(message); }
				catch (IOException e1) { e1.printStackTrace(); }
				
			else {
				try { message = MessageTranslator.getAgentCreationConfigurationMessage(str_message, this.simulator.getEnvironment().getGraph()); }
				catch (ParserConfigurationException e) { e.printStackTrace(); }
				catch (SAXException e) { e.printStackTrace(); }
				catch (IOException e) { e.printStackTrace(); };
				
				// the treatment
				if(message != null)
					try { this.treatAgentCreationConfiguration(message); }
					catch (IOException e) { e.printStackTrace(); };
			}
		}
	}
}