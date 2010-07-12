/* Orientation.java */

/* The package of this class. */
package control.configuration;

/* Imported classes and/or interfaces. */
import java.util.LinkedList;
import java.util.List;
import model.interfaces.XMLable;

/** Implements objects that represent the orientations
 *  given by SimPatrol in order to satisfy a configuration.
 *  
 *  For example, given a configuration to create a new agent,
 *  an orientation must be sent in order to difuse its UDP
 *  socket connection number. 
 *  
 *  @see Configuration */
public final class Orientation implements XMLable {
	/* Attributes. */
	/** The content of the orientation. */
	private List<SocketNumberAndAgentID> content;
	
	/* Methods. */
	/** Constructor. */
	public Orientation() {
		this.content = new LinkedList<SocketNumberAndAgentID>();
	}
	
	/** Adds the given socket number and correspondant
	 *  agent id to the orientation.
	 *  @param socket_number The number of the socket to be cited by the orientation.
	 *  @param agent_id The id of the agent to be cited by the orientation. */
	public void addItem(int socket_number, String agent_id) {
		this.content.add(new SocketNumberAndAgentID(socket_number, agent_id));
	}
	
	public String getObjectId() {
		// an orientation doesn't need an id
		return null;
	}

	public void setObjectId(String object_id) {
		// an orientation doesn't need an id
		// so, do nothing
	}

	public String toXML(int identation) {
		// holds the answer for the method
		StringBuffer buffer = new StringBuffer();
		
		// applies the identation
		for(int i = 0; i < identation; i++) buffer.append("\t");
		
		// opens the "orientation" tag 
		if(this.content.size() > 0) buffer.append("<orientation>\n");
		else buffer.append("<orientation/>\n");
		
		// for each item of the content of the orientation		
		for(int i = 0; i < this.content.size(); i++) {
			// obtains the item
			SocketNumberAndAgentID item = this.content.get(i);
			
			// applies the identation
			for(int j = 0; j < identation + 1; j++) buffer.append("\t");
			
			// puts the item
			buffer.append("<ort_item agent_id=\"" + item.agent_id +
					      "\" socket=\"" + item.socket_number +
					      "\"/>\n");
		}
		
		// closes the main tag, if needed
		if(this.content.size() > 0) {
			for(int i = 0; i < identation; i++) buffer.append("\t");
			buffer.append("</orientation>\n");			
		}
		
		// return the answer
		return buffer.toString();
	}
}

/** Internal class that holds together the number
 *  of the socket and the id of the agent connected
 *  through it.  */
final class SocketNumberAndAgentID {
	/** The number of the socket. */
	public final int socket_number;
	
	/** The id of agent. */
	public final String agent_id;
	
	/** Constructor.
	 *  @param socket_number The number of the socket.
	 *  @param agent_id The id of agent. */
	public SocketNumberAndAgentID(int socket_number, String agent_id) {
		this.socket_number = socket_number;
		this.agent_id = agent_id;
	}	
}