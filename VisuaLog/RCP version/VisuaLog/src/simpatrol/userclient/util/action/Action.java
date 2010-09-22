/* Action.java */

/* The package of this class. */
package simpatrol.userclient.util.action;

import simpatrol.userclient.util.agent.Agent;

/* Imported classes and/or interfaces. */

/**
 * Implements the actions of the agents of SimPatrol.
 * 
 * @see Agent
 */
public abstract class Action {
	/* Methods. */

	public String getObjectId() {
		// an action doesn't need an id
		return null;
	}

	public void setObjectId(String object_id) {
		// an action doesn't need an id
		// so do nothing
	}
	
	public abstract boolean perform_action(Agent agent);
}