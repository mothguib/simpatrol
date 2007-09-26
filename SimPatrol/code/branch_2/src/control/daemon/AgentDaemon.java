/* AgentDaemon.java */

/* The package of this class. */
package control.daemon;

/* Imported classes and/or interfaces. */
import java.net.SocketException;
import util.Queue;
import util.clock.Clock;
import util.clock.Clockable;
import view.connection.AgentConnection;
import model.agent.Agent;

/** Implements the daemons of SimPatrol that attend
 *  an agent's feelings of perceptions, or intentions of actions. */
public abstract class AgentDaemon extends Daemon implements Clockable {
	/* Attributes. */
	/** The agent whose perceptions are produced and
	 *  whose intentions are attended. */
	protected Agent agent;
	
	/** The clock that controls the daemon's work. */
	protected Clock clock;
	
	/* Methods. */	
	/** Constructor.
	 * 
	 *  Doesn't initiate its own connection, as its subclasses (PerceptionDaemon
	 *  and ActionDaemon) will share one. So the connection must be set by the
	 *  setConenction() method.
	 *  
	 *  @see PerceptionDaemon
	 *  @see ActionDaemon 
	 *  @param thread_name The name of the thread of the daemon.
	 *  @param agent The agent whose perceptions are produced and intentions are attended. */
	public AgentDaemon(String thread_name, Agent agent) {
		super(thread_name);
		this.buffer = new Queue<String>();
		this.agent = agent;
		
		this.clock = new Clock(thread_name + "'s clock", this);
	}
	
	/** Returns the agent of the daemon
	 * 
	 *  @return The agent served by the daemon. */
	public Agent getAgent() {
		return this.agent;
	}
	
	/** Configures the connection of the daemon.
	 * 
	 *  @param connection The connection of the daemon. */
	public void setConnection(AgentConnection connection) {
		this.connection = connection;
	}
	
	public void start(int local_socket_number) throws SocketException {
		super.start(local_socket_number);
		this.clock.start();
	}	
	
	/** Indicates that the daemon must stop working. */
	public void stopWorking() {
		super.stopWorking();		
		this.clock.stopWorking();
	}
}