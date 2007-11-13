/* AgentEvent.java */

/* The package of this class. */
package logger.event;

/** Implements the events that are related to an agent. */
public abstract class AgentEvent extends Event {
	/* Attributes. */
	/** The id of the agent to which this event is related to. */
	protected String agent_id;

	/* Methods. */
	/**
	 * Constructor.
	 * 
	 * @param agent_id
	 *            The id of the agent to which this event is related to.
	 */
	public AgentEvent(String agent_id) {
		this.agent_id = agent_id;
	}
}
