/* AgentCreationEvent.java */

/* The package of this class. */
package control.event;

/* Imported classes and/or interfaces. */
import model.agent.Agent;

/**
 * Implements the events that are related to the creation of an agent.
 */
public final class AgentCreationEvent extends Event {
	/* Attributes. */
	/** The agent just created. */
	private final Agent AGENT;

	/** The id of the society to where the agent was added. */
	private final String SOCIETY_ID;

	/* Methods. */
	/**
	 * Constructor.
	 * 
	 * @param agent
	 *            The agent just being created.
	 * @param society_id
	 *            The id of the society to where the new agent is being added.
	 */
	public AgentCreationEvent(Agent agent, String society_id) {
		this.AGENT = agent;
		this.SOCIETY_ID = society_id;
	}

	public String fullToXML(int identation, double event_time) {
		// holds the answer for the method
		StringBuffer buffer = new StringBuffer();

		// applies the identation
		for (int i = 0; i < identation; i++)
			buffer.append("\t");

		// fills the buffer
		buffer.append("<event type=\"" + EventTypes.AGENT_STIGMATIZING
				+ "\" time=\"" + event_time + "\" society_id=\""
				+ this.SOCIETY_ID + "\">\n");

		// puts the new agent in the buffer
		buffer.append(AGENT.fullToXML(identation + 1));

		// closes the tag
		for (int i = 0; i < identation; i++)
			buffer.append("\t");
		buffer.append("</event>\n");

		// returns the answer
		return buffer.toString();
	}
}
