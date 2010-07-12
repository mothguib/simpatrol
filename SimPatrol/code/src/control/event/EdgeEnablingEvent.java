/* EdgeEnablingEvent.java */

/* The package of this class. */
package control.event;

/** Implements the events that are related to the enabling of an edge. */
public final class EdgeEnablingEvent extends Event {
	/* Attributes. */
	/** The id of the edge being enabled / disabled. */
	private final String EDGE_ID;

	/* Methods. */
	/**
	 * Constructor.
	 * 
	 * @param edge_id
	 *            The id of the edge being enabled / disabled.
	 */
	public EdgeEnablingEvent(String edge_id) {
		this.EDGE_ID = edge_id;
	}

	public String fullToXML(int identation, double event_time) {
		// holds the answer for the method
		StringBuffer buffer = new StringBuffer();

		// applies the identation
		for (int i = 0; i < identation; i++)
			buffer.append("\t");

		// fills the buffer
		buffer.append("<event type=\"" + EventTypes.EDGE_ENABLING
				+ "\" time=\"" + event_time + "\" edge_id=\"" + this.EDGE_ID
				+ "\"/>\n");

		// returns the answer
		return buffer.toString();
	}
}
