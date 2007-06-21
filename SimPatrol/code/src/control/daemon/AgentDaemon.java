/* AgentDaemon.java */

/* The package of this class. */
package control.daemon;

/* Imported classes and/or interfaces. */
import java.net.SocketException;
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
	 *  From super() constructor, creates its own connection, however with
	 *  the invalid socket number "-1".
	 *  
	 *  As subclassses ActionDaemons and PerceptionDaemons share the same connection,
	 *  they must have their AgentConnection object configured by
	 *  their setConnection method.
	 *  
	 *  @param agent The agent whose intentions are attended.
	 *  @see ActionDaemon
	 *  @see PerceptionDaemon
	 *  @throws SocketException */
	public AgentDaemon(Agent agent) throws SocketException {
		super(-1);
		this.agent = agent;
	}
	
	/** Configures the connection of the daemon. */
	public void setConnection(AgentConnection connection) {
		this.connection = connection;
	}
}
