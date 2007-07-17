/* AgentDaemon.java */

/* The package of this class. */
package control.daemon;

/* Imported classes and/or interfaces. */
import util.Queue;
import view.connection.AgentConnection;
import model.agent.Agent;

/** Implements the daemons of SimPatrol that attend
 *  an agent's feelings of perceptions, or intentions of actions. */
public abstract class AgentDaemon extends Daemon {
	/* Attributes. */
	/** The agent whose perceptions are produced and
	 *  whose intentions are attended. */
	protected Agent agent;
	
	/* Methods. */	
	/** Constructor.
	 * 
	 *  Doesn't initiate its own connection, as its subclasses (PerceptionDaemon
	 *  and ActionDaemon) will share one. So the connection must be set by the
	 *  setConenction() method.
	 *  @see PerceptionDaemon
	 *  @see ActionDaemon
	 * 
	 *  @param thread_name The name of the thread of the daemon.
	 *  @param agent The agent whose perceptions are produced and intentions are attended. */
	public AgentDaemon(String thread_name, Agent agent) {
		super(thread_name);
		this.buffer = new Queue<String>();
		this.agent = agent;
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
}