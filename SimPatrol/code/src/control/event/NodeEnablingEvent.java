/* NodeEnablingEvent.java */

/* The package of this class. */
package control.event;

/** Implements the events that are related to the enabling of a node. */
public final class NodeEnablingEvent extends Event {
	/* Attributes. */
	/** The id of the node being enabled / disabled. */
	private final String NODE_ID;

	/* Methods. */
	/**
	 * Constructor.
	 * 
	 * @param node_id
	 *            The id of the node being enabled / disabled.
	 */
	public NodeEnablingEvent(String node_id) {
		this.NODE_ID = node_id;
	}

	public String fullToXML(int identation, double event_time) {
		// holds the answer for the method
		StringBuffer buffer = new StringBuffer();

		// applies the identation
		for (int i = 0; i < identation; i++)
			buffer.append("\t");

		// fills the buffer
		buffer.append("<event type=\"" + EventTypes.NODE_ENABLING
				+ "\" time=\"" + event_time + "\" node_id=\""
				+ this.NODE_ID + "\"/>\n");

		// returns the answer
		return buffer.toString();
	}
}
