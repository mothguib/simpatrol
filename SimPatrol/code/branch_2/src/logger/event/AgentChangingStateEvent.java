/* AgentChangingStateEvent.java */

/* The package of this class. */
package logger.event;

/** Implements the events that are related to the changing of state of an agent. */
public final class AgentChangingStateEvent extends AgentEvent {
	/* Methods. */
	/**
	 * Constructor.
	 * 
	 * @param agent_id
	 *            The id of the agent to which this event is related to.
	 */
	public AgentChangingStateEvent(String agent_id) {
		super(agent_id);
	}

	public String fullToXML(int identation) {
		// holds the answer for the method
		StringBuffer buffer = new StringBuffer();

		// applies the identation
		for (int i = 0; i < identation; i++)
			buffer.append("\t");

		// fills the buffer
		buffer.append("<event type=\"" + EventTypes.AGENT_CHANGING_STATE_EVENT
				+ "\" time=\"" + simulator.getElapsedTime() + "\" agent_id=\""
				+ this.agent_id + "\"/>\n");

		// returns the answer
		return buffer.toString();
	}
}