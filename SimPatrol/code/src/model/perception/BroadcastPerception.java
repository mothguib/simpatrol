/* BroadcastPerception.java */

/* The package of this class. */
package model.perception;

/* Imported classes and/or interfaces. */
import model.limitation.StaminaLimitation;

/**
 * Implements the ability of an agent to receive broadcasted messages.
 * 
 * The production of broadcast perceptions is controlled only by stamina
 * limitations.
 * 
 * @see StaminaLimitation
 */
public final class BroadcastPerception extends Perception {
	/* Attributes. */
	/** The broadcasted message. */
	private String message;

	/* Methods. */
	/**
	 * Constructor.
	 * 
	 * @param message
	 *            The message broadcasted to the agent.
	 */
	public BroadcastPerception(String message) {
		this.message = message;
	}

	public String fullToXML(int identation) {
		// holds the answer for the method
		StringBuffer buffer = new StringBuffer();

		// applies the identation and opens the "perception" tag
		for (int i = 0; i < identation; i++)
			buffer.append("\t");
		buffer.append("<perception type=\"" + PerceptionTypes.BROADCAST
				+ "\" message=\"" + this.message + "\"/>\n");

		// returns the answer
		return buffer.toString();
	}
}