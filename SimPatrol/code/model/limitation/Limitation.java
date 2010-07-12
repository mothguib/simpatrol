/* Limitation.java (2.0) */
package br.org.simpatrol.server.model.limitation;

/* Imported classes and/or interfaces. */
import br.org.simpatrol.server.model.agent.Ability;
import br.org.simpatrol.server.model.interfaces.XMLable;

/**
 * Implements the limitations imposed to the abilities of the agents in
 * SimPatrol.
 * 
 * @see Ability
 * @author Daniel Henriques Moreira (dhenriques@gmail.com)
 */
public abstract class Limitation implements XMLable {
	/* Attributes. */
	/** Holds the type of the limitation. */
	protected LimitationTypes limitationType;

	/* Methods. */
	/** Constructor. */
	public Limitation() {
		this.initActionType();
	}

	/**
	 * Initiates the type of the limitation.
	 * 
	 * {@link #limitationType}
	 */
	protected abstract void initActionType();

	public String reducedToXML() {
		// a limitation doesn't have a lighter version
		return this.fullToXML();
	}

	public String getId() {
		// a limitation doesn't need an id
		return null;
	}
}