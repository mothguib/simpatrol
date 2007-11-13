/* EdgeEnablingEvent.java */

/* The package of this class. */
package logger.event;

/** Implements the events that are related to the enabling of an edge. */
public final class EdgeEnablingEvent extends Event {
	/* Attributes. */
	/** The id of the edge being enabled / disabled. */
	private String edge_id;

	/* Methods. */
	/**
	 * Constructor.
	 * 
	 * @param edge_id
	 *            The id of the edge being enabled / disabled.
	 */
	public EdgeEnablingEvent(String edge_id) {
		this.edge_id = edge_id;
	}

	public String fullToXML(int identation) {
		// holds the answer for the method
		StringBuffer buffer = new StringBuffer();

		// applies the identation
		for (int i = 0; i < identation; i++)
			buffer.append("\t");

		// fills the buffer
		buffer.append("<event type=\""
				+ EventTypes.EDGE_CHANGING_ENABLING_EVENT + "\" time=\""
				+ simulator.getElapsedTime() + "\" edge_id=\"" + this.edge_id
				+ "\"/>\n");

		// returns the answer
		return buffer.toString();
	}
}
