/* BroadcastPerception.java (2.0) */
package br.org.simpatrol.server.model.perception;

/**
 * Implements the ability of an agent to receive broadcasted messages.
 * 
 * @author Daniel Henriques Moreira (dhenriques@gmail.com)
 */
public class BroadcastPerception extends Perception {
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
		super();
		this.message = message;
	}

	protected void initPerceptionType() {
		this.perceptionType = PerceptionTypes.BROADCAST;
	}

	public String fullToXML() {
		// holds the answer for the method
		StringBuffer buffer = new StringBuffer();

		// mounts the "perception" tag
		buffer.append("<perception type=\"" + this.perceptionType.getType()
				+ "\" message=\"" + this.message + "\"/>");

		// returns the answer
		return buffer.toString();
	}
}