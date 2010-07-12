/* AgentEvent.java */

/* The package of this class. */
package control.event;

/** Implements the events that are related to an agent. */
public abstract class AgentEvent extends Event {
	/* Attributes. */
	/** The id of the agent to which this event is related to. */
	protected final String AGENT_ID;

	/* Methods. */
	/**
	 * Constructor.
	 * 
	 * @param agent_id
	 *            The id of the agent to which this event is related to.
	 */
	public AgentEvent(String agent_id) {
		this.AGENT_ID = agent_id;
	}
}