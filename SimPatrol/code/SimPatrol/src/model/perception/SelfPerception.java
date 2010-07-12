/* SelfPerception.java */

/* The package of this class. */
package model.perception;

/* Imported classes and/or interfaces. */
import model.agent.Agent;

/** Implements the ability of an agent to perceive itself. */
public class SelfPerception extends Perception {
	/* Attributes. */
	/** The agent itself. */
	private Agent itself;

	/* Methods. */
	/**
	 * Constructor.
	 * 
	 * @param itself
	 *            The agent itself.
	 */
	public SelfPerception(Agent itelf) {
		this.itself = itelf;
	}

	public String fullToXML(int identation) {
		// holds the answer for the method
		StringBuffer buffer = new StringBuffer();

		// applies the identation and opens the "perception" tag
		for (int i = 0; i < identation; i++)
			buffer.append("\t");
		buffer.append("<perception type=\"" + PerceptionTypes.SELF + "\">\n");

		// puts the agent
		buffer.append(this.itself.fullToXML(identation + 1));

		// applies the identation and closes the "perception" tag
		for (int i = 0; i < identation; i++)
			buffer.append("\t");
		buffer.append("</perception>\n");

		// returns the answer
		return buffer.toString();
	}
}