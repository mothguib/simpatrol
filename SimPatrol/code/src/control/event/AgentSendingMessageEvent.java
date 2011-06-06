package control.event;

/**
 * Implements the events that are related to the sending of messages from
 * an agent.
 */
public class AgentSendingMessageEvent  extends AgentMessageExchangingEvent {
	
	String target_agent;
	
	/* Methods. */
	/**
	 * Constructor.
	 * 
	 * @param agent_id
	 *            The id of the agent to which this event is related to.
	 * @param message
	 *            The message broadcasted by the agent.
	 */
	public AgentSendingMessageEvent(String agent_id, String target_id, String message) {
		super(agent_id, message);
		this.target_agent = target_id;
	}

	public String fullToXML(int identation, double event_time) {
		// holds the answer for the method
		StringBuffer buffer = new StringBuffer();

		// applies the identation
		for (int i = 0; i < identation; i++)
			buffer.append("\t");

		// fills the buffer
		buffer.append("<event type=\"" + EventTypes.AGENT_SENDING_MESSAGE
				+ "\" time=\"" + event_time + "\" agent_id=\"" + this.AGENT_ID
				+ "\" target_id=\"" + this.target_agent
				+ "\" message=\"" + this.MESSAGE + "\"/>\n");

		// returns the answer
		return buffer.toString();
	}

}
