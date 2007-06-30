/* AgentDaemon.java */

/* The package of this class. */
package control.daemon;

/* Imported classes and/or interfaces. */
import util.Queue;
import view.connection.AgentConnection;
import model.agent.Agent;

/** Implements the daemons of SimPatrol that attend
 *  an agent's intentions of actions or requisitions for perceptions. */
public abstract class AgentDaemon extends Daemon {
	/* Attributes. */
	/** The agent whose intentions are attended. */
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
	 *  @param agent The agent whose intentions are attended. */
	public AgentDaemon(Agent agent) {
		this.buffer = new Queue<String>();
		this.agent = agent;
	}
	
	/** Configures the connection of the daemon. */
	public void setConnection(AgentConnection connection) {
		this.connection = connection;
	}
}