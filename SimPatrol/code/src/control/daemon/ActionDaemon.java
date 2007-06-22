/* ActionDaemon.java */

/* The package of this class. */
package control.daemon;

/* Imported classes and/or interfaces. */
import java.net.SocketException;import model.agent.Agent;

/** Implements the daemons of SimPatrol that attend
 *  an agent's intentions of actions. */
public final class ActionDaemon extends AgentDaemon {
	/* Methods. */
	/** Constructor.
	 * 
	 *  From super() constructor, creates its own connection, however with
	 *  the invalid socket number "-1".
	 *  
	 *  As ActionDaemons and PerceptionDaemons share the same connection,
	 *  they must have their AgentConnection object configured by
	 *  their setConnection method.
	 *  
	 *  @param agent The agent whose intentions are attended.
	 *  @see PerceptionDaemon
	 *  @throws SocketException */
	public ActionDaemon(Agent agent) throws SocketException {
		super(agent);
	}

	public void run() {
		// screen message
		System.out.println("[SimPatrol.ActionDaemon(" + this.agent.getObjectId() + ")]: Listening to some intention...");
		
		// TODO implementar!!!
	}
}