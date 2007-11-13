/* VertexEnablingEvent.java */

/* The package of this class. */
package logger.event;

/** Implements the events that are related to the enabling of a vertex. */
public final class VertexEnablingEvent extends Event {
	/* Attributes. */
	/** The id of the vertex being enabled / disabled. */
	private String vertex_id;

	/* Methods. */
	/**
	 * Constructor.
	 * 
	 * @param vertex_id
	 *            The id of the vertex being enabled / disabled.
	 */
	public VertexEnablingEvent(String vertex_id) {
		this.vertex_id = vertex_id;
	}

	public String fullToXML(int identation) {
		// holds the answer for the method
		StringBuffer buffer = new StringBuffer();

		// applies the identation
		for (int i = 0; i < identation; i++)
			buffer.append("\t");

		// fills the buffer
		buffer.append("<event type=\""
				+ EventTypes.VERTEX_CHANGING_ENABLING_EVENT + "\" time=\""
				+ simulator.getElapsedTime() + "\" vertex_id=\""
				+ this.vertex_id + "\"/>\n");

		// returns the answer
		return buffer.toString();
	}
}
