/* Action.java (2.0) */
package br.org.simpatrol.server.model.action;

/* Imported classes and/or interfaces. */
import br.org.simpatrol.server.model.agent.Agent;
import br.org.simpatrol.server.model.interfaces.XMLable;

/**
 * Implements the actions of the agents of SimPatrol.
 * 
 * @see Agent
 * @author Daniel Henriques Moreira (dhenriques@gmail.com)
 */
public abstract class Action implements XMLable {
	/* Attributes. */
	/** Holds the type of the action. */
	protected ActionTypes actionType;

	/* Methods. */
	/** Constructor. */
	public Action() {
		this.initActionType();
	}

	/**
	 * Initiates the type of the action.
	 * 
	 * {@link #actionType}
	 */
	protected abstract void initActionType();

	public String reducedToXML() {
		// an action doesn't have a lighter version
		return this.fullToXML();
	}

	public String getId() {
		// an action doesn't need an id
		return null;
	}
}