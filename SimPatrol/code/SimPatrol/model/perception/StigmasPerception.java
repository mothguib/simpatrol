/* StimasPerception.java (2.0) */
package br.org.simpatrol.server.model.perception;

/* Imported classes and/or interfaces. */
import java.util.Set;

import br.org.simpatrol.server.model.stigma.Stigma;

/**
 * Implements the ability of an agent to perceive stigmas deposited on the graph
 * of the simulation.
 * 
 * @see Stigma
 * @author Daniel Henriques Moreira (dhenriques@gmail.com)
 */
public class StigmasPerception extends Perception {
	/* Attributes. */
	/** The perceived stigmas. */
	private Set<Stigma> stigmas;

	/* Methods. */
	/**
	 * Constructor.
	 * 
	 * @param perceivedStigmas
	 *            The perceived stigmas.
	 */
	public StigmasPerception(Set<Stigma> perceivedStigmas) {
		super();
		this.stigmas = perceivedStigmas;
	}

	protected void initPerceptionType() {
		this.perceptionType = PerceptionTypes.STIGMAS;
	}

	public String fullToXML() {
		// holds the answer for the method
		StringBuffer buffer = new StringBuffer();

		// opens the "perception" tag
		buffer.append("<perception type=\"" + this.perceptionType.getType()
				+ "\">");

		// puts the stigmas, in a lighter version
		for (Stigma stigma : this.stigmas)
			buffer.append(stigma.reducedToXML());

		// closes the "perception" tag
		buffer.append("</perception>");

		// returns the answer
		return buffer.toString();
	}
}
