/* Message.java */

/* The package of this class. */
package view.message;

/* Imported classes and/or interfaces. */
import model.interfaces.XMLable;

/** Implements the messages received and sent between SimPatrol and
 *  the external actors. */
public final class Message implements XMLable {
	/* Attributes. */
	/** The content of the message. */
	private XMLable content;
	
	/* Methods. */
	/** Constructor.
	 *  @param content The content of the message. */
	public Message(XMLable content) {
		this.content = content;
	}
	
	/** Returns the content of the message.
	 *  @return The content of the message. */
	public XMLable getContent() {
		return this.content;
	}
	
	public String toXML(int identation) {
		// holds the answer for the method
		StringBuffer buffer = new StringBuffer();
		
		// applies the identation and fills the "message" tag
		for(int i = 0; i < identation; i++) buffer.append("\t");
		buffer.append("<message>\n");
		
		// puts the content
		buffer.append(this.content.toXML(identation + 1));
		
		// closed the "message" tag
		for(int i = 0; i < identation; i++) buffer.append("\t");
		buffer.append("</message>\n");
		
		// returns the answer for the method
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