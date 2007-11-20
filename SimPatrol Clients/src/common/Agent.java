/* Agent.java */

/* The package of this class. */
package common;

/**
 * The remote agents that connect to SimPatrol and implmenet a patrolling
 * strategy.
 */
public abstract class Agent extends Thread {
	/* Attributes. */
	/** Registers if the agent shall stop working. */
	protected boolean stop_working;

	/* Methods. */
	/** Constructor. */
	public Agent() {
		this.stop_working = false;
	}
	
	/** Indicates that the agent must stop working. */
	public void stopWorking() {
		this.stop_working = true;
	}
}
