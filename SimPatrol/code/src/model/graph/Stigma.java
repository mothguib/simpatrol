/* Stigma.java */

/* The package of this class. */
package model.graph;

/* Imported classes and/or interfaces. */
import model.interfaces.XMLable;

/** Implements an eventual stigma deposited by a patroller. */
public class Stigma implements XMLable {
	/* Methods. */
	public String toXML(int identation) {
		// holds the answer being constructed
		StringBuffer buffer = new StringBuffer();
		
		// applies the identation
		for(int i = 0; i < identation; i++)
			buffer.append("\t");
		
		// fills the buffer
		buffer.append("<stigma/>\n");		
		
		// returns the buffer content
		return buffer.toString();
	}

	public String getObjectId() {
		// a stigma doesn't need an id
		return null;
	}

	public void setObjectId(String object_id) {
		// a stigma doesn't need an id
		// so, do nothing		
	}
}
