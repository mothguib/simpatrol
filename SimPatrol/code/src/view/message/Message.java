/* Message.java */

/* The package of this class. */
package view.message;

/* Imported classes and/or interfaces. */
import model.interfaces.XMLable;

/** Implements the messages received and sent between SimPatrol and
 *  the external actors. */
public class Message implements XMLable {
	/* Attributes. */
	/** Holds the type of the message.
	 *  @see MessageTypes */
	private int message_type;
	
	/** The content of the message. */
	private XMLable content;
	
	/** The IP address of the sender of the message. */
	private String sender_address;
	
	/** The number of the UDP socket of the sender of the message. */
	private int sender_socket_number;
	
	/* Methods. */
	/** Constructor.
	 *  @param message_type The type of the message.
	 *  @param content The content of the message.
	 *  @see MessageTypes */
	public Message(int message_type, XMLable content) {
		this.message_type = message_type;
		this.content = content;
		this.sender_address = null;
		this.sender_socket_number = -1;
	}	
	
	/** Constructor.
	 *  @param message_type The type of the message.
	 *  @param content The content of the message.
	 *  @param sender_address The address of the sender of the message.
	 *  @param sender_socket_number The number of the UDP socket of the sender.
	 *  @see MessageTypes */
	public Message(int message_type, XMLable content, String sender_address, int sender_socket_number) {
		this.message_type = message_type;
		this.content = content;
		this.sender_address = sender_address;
		this.sender_socket_number = sender_socket_number;
	}
	
	/** Returns the type of the message.
	 *  @return The type of the message.
	 *  @see MessageTypes */
	public int getType() {
		return this.message_type;
	}
	
	/** Returns the content of the message.
	 *  @return The content of the message. */
	public XMLable getContent() {
		return this.content;
	}
	
	/** Returns the IP address of the sender of the message.
	 *  @return The IP address fo the sender of the message. */
	public String getSender_address() {
		return this.sender_address;
	}
	
	/** Returns the number of the UDP socket of the sender of the message.
	 *  @return The number of the UDP socket of the sender of the message. */
	public int getSender_socket_number() {
		return this.sender_socket_number;
	}
	
	public String toXML(int identation) {
		// holds the answer for the method
		StringBuffer buffer = new StringBuffer();
		
		// applies the identation
		for(int i = 0; i < identation; i++) buffer.append("\t");
		
		// fills the buffer
		buffer.append("<message type=\"" + this.message_type);		
		
		if(this.sender_address != null)
			buffer.append("\" sender_address=\"" + this.sender_address);
		
		if(this.sender_socket_number > -1)
			buffer.append("\" sender_socket=\"" + this.sender_socket_number);
		
		// puts the "content" of the message, if it exists, and closes the tag
		if(this.content != null) {
			buffer.append("\">\n");
			buffer.append(this.content.toXML(identation + 1));
			
			for(int i = 0; i < identation; i++) buffer.append("\t");
			buffer.append("</message>\n");
		}
		else buffer.append("\"/>\n");
		
		// returns the answer
		return buffer.toString();		
	}
	
	public String getObjectId() {
		// a message doesn't need an id
		return null;
	}

	public void setObjectId(String object_id) {
		// a message doesn't need an id
		// so, do nothing
	}	
}
