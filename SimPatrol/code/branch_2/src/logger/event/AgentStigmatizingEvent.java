/* AgentStigmatizingEvent.java */

/* The package of this class. */
package logger.event;

/* Imported classes and/or interfaces. */
import model.stigma.Stigma;

/**
 * Implements the events that are related to an agent depositing stigmas on the
 * graph.
 */
public final class AgentStigmatizingEvent extends AgentEvent {
	/* Attributes. */
	/** The stigma deposited by the agent. */
	private Stigma stigma;

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
		this.stigma = stigma;
	}

	public String fullToXML(int identation) {
		// holds the answer for the method
		StringBuffer buffer = new StringBuffer();

		// applies the identation
		for (int i = 0; i < identation; i++)
			buffer.append("\t");

		// fills the buffer
		buffer.append("<event type=\"" + EventTypes.AGENT_STIGMATIZING_EVENT
				+ "\" time=\"" + simulator.getElapsedTime() + "\" agent_id=\""
				+ this.agent_id + "\">\n");

		// puts the stigma on the buffer
		buffer.append(stigma.fullToXML(identation + 1));

		// closes the tag
		for (int i = 0; i < identation; i++)
			buffer.append("\t");
		buffer.append("</event>\n");

		// returns the answer
		return buffer.toString();
	}
}
