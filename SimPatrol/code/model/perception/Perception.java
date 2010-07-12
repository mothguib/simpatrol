/* Perception.java (2.0) */
package br.org.simpatrol.server.model.perception;

/* Imported classes and/or interfaces. */
import br.org.simpatrol.server.model.agent.Agent;
import br.org.simpatrol.server.model.interfaces.XMLable;

/**
 * Implements the perceptions of the agents of SimPatrol.
 * 
 * @see Agent
 * @author Daniel Henriques Moreira (dhenriques@gmail.com)
 */
public abstract class Perception implements XMLable {
	/* Attributes. */
	/** Holds the type of the perception. */
	protected PerceptionTypes perceptionType;

	/* Methods. */
	/** Constructor. */
	public Perception() {
		this.initPerceptionType();
	}

	/**
	 * Initializes the type of the perception.
	 * 
	 * {@link #perceptionType}
	 */
	protected abstract void initPerceptionType();

	public String reducedToXML() {
		// a perception doesn't have a lighter version
		return this.fullToXML();
	}

	public String getId() {
		// a perception doesn't need an id
		return null;
	}
}
