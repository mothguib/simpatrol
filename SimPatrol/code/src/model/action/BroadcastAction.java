/* BroadcastAction.java */

/* The package of this class. */
package model.action;

/* Imported classes and/or interfaces. */
import model.limitation.DepthLimitation;
import model.limitation.StaminaLimitation;

/**
 * Implements the action of broadcasting a message through the graph.
 * 
 * Its effect can be controlled by depth and stamina limitations.
 * 
 * @see DepthLimitation
 * @see StaminaLimitation
 */
public final class BroadcastAction extends AtomicAction {
	/* Attributes. */
	/** The message to be broadcasted. */
	private String message;

	/** The depth of the graph that the broadcasted message must reach. */
	private int message_depth;

	/* Methods. */
	/**
	 * Constructor.
	 * 
	 * @param message
	 *            The message to be broadcasted.
	 * @param message_depth
	 *            The depth of the graph that the broadcasted message must
	 *            reach.
	 */
	public BroadcastAction(String message, int message_depth) {
		this.message = message;
		this.message_depth = message_depth;
	}

	/**
	 * Returns the broadcasted message.
	 * 
	 * @return The message to be broadcasted.
	 */
	public String getMessage() {
		return this.message;
	}

	/**
	 * Returns the depth of the graph that the broadcasted message must reach.
	 * 
	 * @return The depth of the graph that the broadcasted message must reach.
	 */
	public int getMessage_depth() {
		return this.message_depth;
	}

	public String fullToXML(int identation) {
		// holds the answer for the method
		StringBuffer buffer = new StringBuffer();

		// applies the identation
		for (int i = 0; i < identation; i++)
			buffer.append("\t");

		// fills the buffer
		buffer.append("<action type=\"" + ActionTypes.BROADCAST
				+ "\" message=\"" + this.message + "\" message_depth=\""
				+ this.message_depth + "\"/>\n");

		// returns the answer
		return buffer.toString();
	}
}