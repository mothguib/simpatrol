/* AgentTeleportingEvent.java */

/* The package of this class. */
package control.event;

/** Implements the events that are related to the teleport of an agent. */
public final class AgentTeleportingEvent extends AgentEvent {
	/* Attributes. */
	/** The id of the node to where the agent teleported. */
	private final String NODE_ID;

	/** The id of the edge to where the agent teleported. */
	private final String EDGE_ID;

	/** The length of the edge already trespassed by the agent. */
	private final double LENGTH;

	/* Methods. */
	/**
	 * Constructor.
	 * 
	 * @param agent_id
	 *            The id of the agent to which this event is related to.
	 * @param node_id
	 *            The id of the node to where the agent teleported.
	 * @param edge_id
	 *            The id of the edge to where the agent teleported.
	 * @param length
	 *            The length of the edge already trespassed by the agent.
	 */
	public AgentTeleportingEvent(String agent_id, String node_id,
			String edge_id, double length) {
		super(agent_id);
		this.NODE_ID = node_id;
		this.EDGE_ID = edge_id;
		this.LENGTH = length;
	}

	public String fullToXML(int identation, double event_time) {
		// holds the answer for the method
		StringBuffer buffer = new StringBuffer();

		// applies the identation
		for (int i = 0; i < identation; i++)
			buffer.append("\t");

		// fills the buffer
		buffer.append("<event type=\"" + EventTypes.AGENT_TELEPORTING
				+ "\" time=\"" + event_time + "\" agent_id=\"" + this.AGENT_ID
				+ "\" node_id=\"" + this.NODE_ID);

		// if the edge id is valid
		if (this.EDGE_ID != null)
			buffer.append("\" edge_id= \"" + this.EDGE_ID + "\" length=\""
					+ this.LENGTH);

		// closes the tag
		buffer.append("\"/>\n");

		// returns the answer
		return buffer.toString();
	}
}
