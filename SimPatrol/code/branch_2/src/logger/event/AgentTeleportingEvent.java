/* AgentTeleportingEvent.java */

/* The package of this class. */
package logger.event;

/** Implements the events that are related to the teleporting of an agent. */
public final class AgentTeleportingEvent extends AgentEvent {
	/* Attributes. */
	/** The id of the vertex to where the agent teleported. */
	private String vertex_id;

	/** The id of the edge to where the agent teleported. */
	private String edge_id;

	/** The length of the edge already trespassed by the agent. */
	private double length;

	/* Methods. */
	/**
	 * Constructor.
	 * 
	 * @param agent_id
	 *            The id of the agent to which this event is related to.
	 * @param vertex_id
	 *            The id of the vertex to where the agent teleported.
	 * @param edge_id
	 *            The id of the edge to where the agent teleported.
	 * @param length
	 *            The length of the edge already trespassed by the agent.
	 */
	public AgentTeleportingEvent(String agent_id, String vertex_id,
			String edge_id, double length) {
		super(agent_id);
		this.vertex_id = vertex_id;
		this.edge_id = edge_id;
		this.length = length;
	}

	public String fullToXML(int identation) {
		// holds the answer for the method
		StringBuffer buffer = new StringBuffer();

		// applies the identation
		for (int i = 0; i < identation; i++)
			buffer.append("\t");

		// fills the buffer
		buffer.append("<event type=\"" + EventTypes.AGENT_TELEPORTING_EVENT
				+ "\" time=\"" + simulator.getElapsedTime() + "\" agent_id=\""
				+ this.agent_id + "\" vertex_id=\"" + this.vertex_id);

		// if the edge id is valid
		if (this.edge_id != null)
			buffer.append("\" edge_id= \"" + this.edge_id + "\" length=\""
					+ this.length);

		// closes the tag
		buffer.append("\"/>\n");

		// returns the answer
		return buffer.toString();
	}
}
