/* AgentCommunicationEvent.java */

/* The package of this class. */
package logger.event;

/**
 * Implements the events that are related to the exchanging of messages among
 * the agents.
 */
public abstract class AgentMessageExchangingEvent extends AgentEvent {
	/* Attributes. */
	/** The message broadcasted by the agent. */
	protected String message;

	/* Methods. */
	/**
	 * Constructor.
	 * 
	 * @param agent_id
	 *            The id of the agent to which this event is related to.
	 * @param message
	 *            The message broadcasted by the agent.
	 */
	public AgentMessageExchangingEvent(String agent_id, String message) {
		super(agent_id);
		this.message = message;
	}
}
