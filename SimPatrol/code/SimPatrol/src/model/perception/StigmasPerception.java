/* StimasPerception.java */

/* The package of this class. */
package model.perception;

/* Imported classes and/or interfaces. */
import java.util.HashSet;
import java.util.Set;
import model.stigma.Stigma;

/**
 * Implements the ability of an agent to perceive stigmas deposited on the graph
 * of the simulation.
 */
public final class StigmasPerception extends Perception {
	/* Attributes. */
	/** The perceived stigmas. */
	private Set<Stigma> stigmas;

	/* Methods. */
	/**
	 * Constructor.
	 * 
	 * @param perceived_stigmas
	 *            The perceived stigmas.
	 */
	public StigmasPerception(Stigma[] perceived_stigmas) {
		this.stigmas = new HashSet<Stigma>();
		for (int i = 0; i < perceived_stigmas.length; i++)
			this.stigmas.add(perceived_stigmas[i]);
	}

	public String fullToXML(int identation) {
		// holds the answer for the method
		StringBuffer buffer = new StringBuffer();

		// applies the identation and opens the "perception" tag
		for (int i = 0; i < identation; i++)
			buffer.append("\t");
		buffer
				.append("<perception type=\"" + PerceptionTypes.STIGMAS
						+ "\">\n");

		// puts the stigmas, in a lighter version
		for (Stigma stigma : this.stigmas)
			buffer.append(stigma.reducedToXML(identation + 1));

		// applies the identation and closes the "perception" tag
		for (int i = 0; i < identation; i++)
			buffer.append("\t");
		buffer.append("</perception>\n");

		// returns the answer
		return buffer.toString();
	}
}
