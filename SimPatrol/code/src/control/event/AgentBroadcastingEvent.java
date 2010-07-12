/* AgentBroadcastingEvent.java */

/* The package of this class. */
package control.event;

/**
 * Implements the events that are related to the broadcasting of messages from
 * an agent.
 */
public final class AgentBroadcastingEvent extends AgentMessageExchangingEvent {
	/* Methods. */
	/**
	 * Constructor.
	 * 
	 * @param agent_id
	 *            The id of the agent to which this event is related to.
	 * @param message
	 *            The message broadcasted by the agent.
	 */
	public AgentBroadcastingEvent(String agent_id, String message) {
		super(agent_id, message);
	}

	public String fullToXML(int identation, double event_time) {
		// holds the answer for the method
		StringBuffer buffer = new StringBuffer();

		// applies the identation
		for (int i = 0; i < identation; i++)
			buffer.append("\t");

		// fills the buffer
		buffer.append("<event type=\"" + EventTypes.AGENT_BROADCASTING
				+ "\" time=\"" + event_time + "\" agent_id=\"" + this.AGENT_ID
				+ "\" message=\"" + this.MESSAGE + "\"/>\n");

		// returns the answer
		return buffer.toString();
	}
}
