/* BroadcastAction.java (2.0) */
package br.org.simpatrol.server.model.action;

/**
 * Implements the actions of broadcasting messages through the graph of the
 * simulation.
 * 
 * @author Daniel Henriques Moreira (dhenriques@gmail.com)
 */
public class BroadcastAction extends AtomicAction {
	/* Attributes. */
	/** The message to be broadcasted. */
	private String message;

	/** The depth of the graph that the broadcasted message must reach. */
	private int messageDepth;

	/* Methods. */
	/**
	 * Constructor.
	 * 
	 * @param message
	 *            The message to be broadcasted.
	 * @param messageDepth
	 *            The depth of the graph that the broadcasted message must
	 *            reach.
	 */
	public BroadcastAction(String message, int messageDepth) {
		super();
		this.message = message;
		this.messageDepth = messageDepth;
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
	public int getMessageDepth() {
		return this.messageDepth;
	}

	protected void initActionType() {
		this.actionType = ActionTypes.BROADCAST;
	}

	public String fullToXML() {
		// holds the answer for the method
		StringBuffer buffer = new StringBuffer();

		// fills the buffer
		buffer.append("<action type=\"" + this.actionType.getType()
				+ "\" message=\"" + this.message + "\" message_depth=\""
				+ this.messageDepth + "\"/>");

		// returns the answer
		return buffer.toString();
	}
}