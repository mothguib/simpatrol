/* VertexEnablingEvent.java */

/* The package of this class. */
package control.event;

/** Implements the events that are related to the enabling of a vertex. */
public final class VertexEnablingEvent extends Event {
	/* Attributes. */
	/** The id of the vertex being enabled / disabled. */
	private final String VERTEX_ID;

	/* Methods. */
	/**
	 * Constructor.
	 * 
	 * @param vertex_id
	 *            The id of the vertex being enabled / disabled.
	 */
	public VertexEnablingEvent(String vertex_id) {
		this.VERTEX_ID = vertex_id;
	}

	public String fullToXML(int identation, double event_time) {
		// holds the answer for the method
		StringBuffer buffer = new StringBuffer();

		// applies the identation
		for (int i = 0; i < identation; i++)
			buffer.append("\t");

		// fills the buffer
		buffer.append("<event type=\"" + EventTypes.VERTEX_ENABLING
				+ "\" time=\"" + event_time + "\" vertex_id=\""
				+ this.VERTEX_ID + "\"/>\n");

		// returns the answer
		return buffer.toString();
	}
}
