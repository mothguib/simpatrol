/* Agent.java */

/* The package of this class. */
package common;

/* Imported classes and/or interfaces. */
import util.net.ClientConnection;

/**
 * The remote agents that connect to SimPatrol and implement a patrolling
 * strategy.
 */
public abstract class Agent extends Thread {
	/* Attributes. */
	/** Registers if the agent shall stop working. */
	protected boolean stop_working;

	/** The connection of the agent. */
	protected ClientConnection connection;

	/* Methods. */
	/** Constructor. */
	public Agent() {
		this.stop_working = false;
	}

	/** Indicates that the agent must stop working. */
	public void stopWorking() {
		this.stop_working = true;
	}

	/** Configures the connection of the agent. */
	public void setConnection(ClientConnection connection) {
		this.connection = connection;
	}
}
