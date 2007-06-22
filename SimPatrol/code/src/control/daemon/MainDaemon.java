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
import view.message.Message;
import model.Environment;
import model.agent.Agent;
import model.agent.Society;
import model.interfaces.XMLable;
import control.configuration.EnvironmentCreationConfiguration;
import control.simulator.Simulator;
import control.translator.MessageTranslator;

/** Implements the main daemon of SimPatrol, the one that
 *  controls the graph remote submission, as well as the creation
 *  of societies and agents. */
public final class MainDaemon extends Daemon {
	/* Attributes. */
	/** Registers if the daemon shall stop working. */
	private boolean stop_working;
	
	/** The simulator of SimPatrol. */
	private Simulator simulator;

	/** The generator of numbers for the UDP socket connections. */
	private SocketNumberGenerator socket_number_generator;
	
	/** The environment of the simulation. */
	private Environment environment;
		
	/* Methods. */
	/** Constructor.
	 *  @param simulator The SimPatrol's simulator.
	 * 	@param local_socket_number The number of the local UDP socket.
	 *  @throws SocketException */
	public MainDaemon(Simulator simulator, int local_socket) throws SocketException {
		super(local_socket);
		this.simulator = simulator;
		this.stop_working = false;
		this.socket_number_generator = new SocketNumberGenerator(local_socket);
		this.environment = null;
	}
	
	/** Indicates that the daemon must stop working. */
	public void stopWorking() {
		this.stop_working = true;
	}
	
	/** Obtains the environment of the simulation. 
	 *  @throws IOException 
	 *  @throws SAXException 
	 *  @throws ParserConfigurationException */
	private void listenToEnvironment() throws ParserConfigurationException, SAXException, IOException {
		// screen message
		System.out.println("[SimPatrol.MainDaemon] Listening to the environment's configuration...");
		
		// while there's no environment
		while(this.environment == null) {
			// listens to some message
			String str_message = null;
			do {
				str_message = this.buffer.remove();
			} while(str_message == null);
			
			// mounts the message and obtains its content
			XMLable content = MessageTranslator.getMessage(str_message).getContent();
			
			// if the content is an environment creation configuration
			if(content instanceof EnvironmentCreationConfiguration) {
				// obtains the environment
				this.environment = ((EnvironmentCreationConfiguration) content).getEnvironment();
				
				// screen message
				System.out.println("[SimPatrol.MainDaemon] Environment obtained:");
				System.out.print(this.environment.toXML(0));
				
				// activates the agent daemons for the eventual agents
				Society[] societies = this.environment.getSocieties();
				for(int i = 0; i < societies.length; i++)
					this.addAgentDaemons(societies[i].getAgents());				
				
				// sends the orientation message to the remote contact
				// TODO implementar!
			}
		}
	}
	
	/** Adds agent daemons to the simulator, based on the given agents.
	 *  @param agents The agents whose daemons must be created. */
	private void addAgentDaemons(Agent[] agents) {
		// for each agent
		for(int i = 0; i < agents.length; i++) {
			// generates a socket number
			int socket_number = this.socket_number_generator.generateSocketNumber();
			
			// creates a perception daemon
			PerceptionDaemon perception_daemon = null;
			while(perception_daemon == null) {
				try { perception_daemon = new PerceptionDaemon(agents[i]); }
				catch(SocketException e) { perception_daemon = null; };
			}
			
			// creates an action daemon
			ActionDaemon action_daemon = null;
			while(action_daemon == null) {
				try { action_daemon = new ActionDaemon(agents[i]); }
				catch(SocketException e) { action_daemon = null; };
			}
			
			// creates a new agent connection
			AgentConnection connection = null;
			try { connection = new AgentConnection(socket_number, perception_daemon.getBuffer(), action_daemon.getBuffer()); }
			catch(SocketException e) {
				// try again
				Agent[] try_again = {agents[i]};
				this.addAgentDaemons(try_again);
			}
			
			// configures the perception and action daemons connection
			perception_daemon.setConnection(connection);
			action_daemon.setConnection(connection);
			
			// adds the daemons to the simulator
			this.simulator.addPerceptionDaemon(perception_daemon);
			this.simulator.addActionDaemon(action_daemon);
			
			// starts the daemons
			perception_daemon.start();
			action_daemon.start();
		}		
	}
	
	public void run() {
		// listens to the environment
		try { this.listenToEnvironment(); }
		catch (ParserConfigurationException e1) { e1.printStackTrace(); }
		catch (SAXException e1) { e1.printStackTrace(); }
		catch (IOException e1) { e1.printStackTrace(); }
		
		// listens to configurations
		// screen message
		System.out.println("[SimPatrol.MainDaemon] Listening to some configuration...");
		
		// whenever the daemon is active
		while(!this.stop_working) {
			// tries to obtain a message from the buffer
			String str_message = this.buffer.remove();
			
			// if there's a message
			if(str_message != null) {
				// obtains the message object
				Message message = null;
				try { message = MessageTranslator.getMessage(str_message); }
				catch (ParserConfigurationException e) { e.printStackTrace(); }
				catch (SAXException e) { e.printStackTrace();}
				catch (IOException e) { e.printStackTrace(); }
				
				// depending on the type of the message
				// TODO implementar!!
				
				// screen message
				System.out.println("[SimPatrol.MainDaemon] Listening to some configuration...");
			}
		}
		
		// stops the connection
		this.connection.stopWorking();
	}
}