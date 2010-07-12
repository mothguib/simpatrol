/* ActionDaemon.java */

/* The package of this class. */
package control.daemon;

/* Imported classes and/or interfaces. */
import model.agent.Agent;

/**
 * Implements the daemons of SimPatrol that attend an agent's intentions of
 * actions.
 */
public final class ActionDaemon extends AgentDaemon {
	/* Attributes. */
	/* Methods. */
	/**
	 * Constructor.
	 * 
	 * Doesn't initiate its own connection, as it will be shared with a
	 * PerceptionDaemon object. So the connection must be set by the
	 * setConenction() method.
	 * 
	 * @see PerceptionDaemon
	 * 
	 * @param thread_name
	 *            The name of the thread of the daemon.
	 * @param agent
	 *            The agent whose intentions are attended.
	 */
	public ActionDaemon(String thread_name, Agent agent) {
		super(thread_name, agent);
	}

	/** Indicates that the daemon must stop working. */
	public void stopWorking() {
		super.stopWorking();
	}

	public void run() {
		while (!this.stop_working)
			;
		// TODO implementar!!!
	}
}