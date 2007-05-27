/* Stigma.java */

/* The package of this class. */
package model.graph;

/* Imported classes and/or interfaces. */
import model.agent.Agent;
import model.interfaces.XMLable;

/** Implements an eventual stigma deposited by a patroller. */
public class Stigma implements XMLable {
	/* Attributes. */
	/** The agent patroller that deposited the stigma. */
	private Agent agent;
	
	/* Methods. */
	/** Constructor.
	 *  @param agent The agent patroller that deposited the stigma. */
	public Stigma(Agent agent) {
		this.agent = agent;
	}
}
