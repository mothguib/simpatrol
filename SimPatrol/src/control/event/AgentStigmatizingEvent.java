/* AgentStigmatizingEvent.java */

/* The package of this class. */
package control.event;

/* Imported classes and/or interfaces. */
import model.stigma.Stigma;

/**
 * Implements the events that are related to an agent depositing stigmas on the
 * graph.
 */
public final class AgentStigmatizingEvent extends AgentEvent {
	/* Attributes. */
	/** The stigma deposited by the agent. */
	private final Stigma STIGMA;

	/* Methods. */
	/**
	 * Constructor.
	 * 
	 * @param agent_id
	 *            The id of the agent to which this event is related to.
	 * @param stigma
	 *            The stigma deposited by the agent.
	 */
	public AgentStigmatizingEvent(String agent_id, Stigma stigma) {
		super(agent_id);
		this.STIGMA = stigma;
	}

	public String fullToXML(int identation, double event_time) {
		// holds the answer for the method
		StringBuffer buffer = new StringBuffer();

		// applies the identation
		for (int i = 0; i < identation; i++)
			buffer.append("\t");

		// fills the buffer
		buffer.append("<event type=\"" + EventTypes.AGENT_STIGMATIZING
				+ "\" time=\"" + event_time + "\" agent_id=\"" + this.AGENT_ID
				+ "\">\n");

		// puts the stigma on the buffer
		buffer.append(STIGMA.fullToXML(identation + 1));

		// closes the tag
		for (int i = 0; i < identation; i++)
			buffer.append("\t");
		buffer.append("</event>\n");

		// returns the answer
		return buffer.toString();
	}
}
