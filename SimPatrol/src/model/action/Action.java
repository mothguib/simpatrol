/* Action.java */

/* The package of this class. */
package model.action;

/* Imported classes and/or interfaces. */
import model.agent.Agent;
import view.XMLable;

/**
 * Implements the actions of the agents of SimPatrol.
 * 
 * @see Agent
 */
public abstract class Action implements XMLable {
	/* Methods. */
	public String reducedToXML(int identation) {
		// an action doesn't have a lighter version
		return this.fullToXML(identation);
	}

	public String getObjectId() {
		// an action doesn't need an id
		return null;
	}

	public void setObjectId(String object_id) {
		// an action doesn't need an id
		// so do nothing
	}
}