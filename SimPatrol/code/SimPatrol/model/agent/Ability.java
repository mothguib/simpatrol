/* Ability.java (2.0) */
package br.org.simpatrol.server.model.agent;

/* Imported classes and/or interfaces. */
import java.util.Set;

import br.org.simpatrol.server.model.interfaces.XMLable;
import br.org.simpatrol.server.model.limitation.Limitation;

/**
 * Implements the abilities of an agent in SimPatrol.
 * 
 * @author Daniel Henriques Moreira (dhenriques@gmail.com)
 */
public abstract class Ability implements XMLable {
	/* Attributes. */
	/** The limitations imposed to the ability of the agent. */
	protected Set<Limitation> limitations;

	/* Methods. */
	/**
	 * Constructor.
	 * 
	 * @param limitations
	 *            The limitations imposed to the ability of the agent.
	 */
	public Ability(Set<Limitation> limitations) {
		this.limitations = limitations;
	}

	/**
	 * Returns the limitations imposed to the ability of the agent.
	 * 
	 * @return The limitations imposed to the ability of the agent.
	 */
	public Set<Limitation> getLimitations() {
		return this.limitations;
	}

	public String reducedToXML() {
		// an ability doesn't have a lighter version
		return this.fullToXML();
	}

	public String getId() {
		// an ability doesn't need an id
		return null;
	}
}