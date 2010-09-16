/* Orientation.java */

/* The package of this class. */
package control.configuration;

/* Imported classes and/or interfaces. */
import java.util.LinkedList;
import java.util.List;
import view.XMLable;

/**
 * Implements objects that represent the orientations given by SimPatrol in
 * order to satisfy a configuration.
 * 
 * For example, given a configuration to create a new agent, an orientation must
 * be sent in order to diffuse its UDP port.
 * 
 * @see Configuration
 */
public final class Orientation implements XMLable {
	/* Attributes. */
	/** The eventual content of the orientation. */
	private List<SocketNumberAndAgentID> content;

	/** The eventual message of the orientation. */
	private String message;

	/* Methods. */
	/** Constructor. */
	public Orientation() {
		this.content = null;
		this.message = null;
	}

	/**
	 * Constructor.
	 * 
	 * @param message
	 *            The message of the orientation.
	 */
	public Orientation(String message) {
		this.content = null;
		this.message = message;
	}

	/**
	 * Adds the given socket number and correspondent agent id to the
	 * orientation.
	 * 
	 * @param socket_number
	 *            The number of the socket to be cited by the orientation.
	 * @param agent_id
	 *            The id of the agent to be cited by the orientation.
	 */
	public void addItem(int socket_number, String agent_id) {
		if (this.content == null)
			this.content = new LinkedList<SocketNumberAndAgentID>();
		this.content.add(new SocketNumberAndAgentID(socket_number, agent_id));
	}

	public String fullToXML(int identation) {
		// holds the answer for the method
		StringBuffer buffer = new StringBuffer();

		// applies the identation
		for (int i = 0; i < identation; i++)
			buffer.append("\t");

		// opens the "orientation" tag
		if (this.message == null) {
			if (this.content == null)
				buffer.append("<orientation/>\n");
			else
				buffer.append("<orientation>\n");
		} else {
			if (this.content == null)
				buffer.append("<orientation message=\"" + this.message
						+ "\"/>\n");
			else
				buffer.append("<orientation message=\"" + this.message
						+ "\">\n");
		}

		// for each eventual item of the content of the orientation
		if (this.content != null)
			for (SocketNumberAndAgentID item : this.content) {
				// applies the identation
				for (int j = 0; j < identation + 1; j++)
					buffer.append("\t");

				// puts the item
				buffer.append("<ort_item agent_id=\"" + item.AGENT_ID
						+ "\" socket=\"" + item.SOCKET_NUMBER + "\"/>\n");
			}

		// closes the main tag, if needed
		if (this.content != null) {
			for (int i = 0; i < identation; i++)
				buffer.append("\t");
			buffer.append("</orientation>\n");
		}

		// return the answer
		return buffer.toString();
	}

	public String reducedToXML(int identation) {
		// an orientation doesn't have a lighter XML version
		return this.fullToXML(identation);
	}

	public String getObjectId() {
		// an orientation doesn't need an id
		return null;
	}

	public void setObjectId(String object_id) {
		// an orientation doesn't need an id
		// so, do nothing
	}
}

/**
 * Internal class that holds together the number of the socket and the id of the
 * agent connected through it.
 */
final class SocketNumberAndAgentID {
	/** The number of the socket. */
	public final int SOCKET_NUMBER;

	/** The id of agent. */
	public final String AGENT_ID;

	/**
	 * Constructor.
	 * 
	 * @param socket_number
	 *            The number of the socket.
	 * @param agent_id
	 *            The id of agent.
	 */
	public SocketNumberAndAgentID(int socket_number, String agent_id) {
		this.SOCKET_NUMBER = socket_number;
		this.AGENT_ID = agent_id;
	}
}