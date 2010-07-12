/* AgentReceivingMessageEvent.java */

/* The package of this class. */
package control.event;

/**
 * Implements the events that are related to the receiving of messages by an
 * agent.
 */
public final class AgentReceivingMessageEvent extends
		AgentMessageExchangingEvent {
	/* Methods. */
	/**
	 * Constructor.
	 * 
	 * @param agent_id
	 *            The id of the agent to which this event is related to.
	 * @param message
	 *            The message broadcasted by the agent.
	 */
	public AgentReceivingMessageEvent(String agent_id, String message) {
		super(agent_id, message);
	}

	public String fullToXML(int identation, double event_time) {
		// holds the answer for the method
		StringBuffer buffer = new StringBuffer();

		// applies the identation
		for (int i = 0; i < identation; i++)
			buffer.append("\t");

		// fills the buffer
		buffer.append("<event type=\"" + EventTypes.AGENT_RECEIVING_MESSAGE
				+ "\" time=\"" + event_time + "\" agent_id=\"" + this.AGENT_ID
				+ "\" message=\"" + this.MESSAGE + "\"/>\n");

		// returns the answer
		return buffer.toString();
	}
}
