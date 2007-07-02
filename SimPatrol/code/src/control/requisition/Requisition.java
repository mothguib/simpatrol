/* Requisition.java */

/* The package of this class. */
package control.requisition;

/* Imported classes and/or interfaces. */
import model.interfaces.XMLable;
import model.perception.PerceptionTypes;

/** Implements the requisitions of perceptions made by
 *  external actors to the simulator. */
public final class Requisition implements XMLable {
	/* Attributes. */
	/** The type of the required perception.
	 *  @see PerceptionTypes */
	private int perception_type;
	
	/* Methods. */
	/** Constructor.
	 *  @param perception_type The type of the required perception. */
	public Requisition(int perception_type) {
		this.perception_type = perception_type;
	}
	
	/** Returns the type of perception required by the requisition.
	 *  @return The type of the required perception. */
	public int getPerception_type() {
		return this.perception_type;
	}
	
	public String toXML(int identation) {
		// holds the answer for the method
		StringBuffer buffer = new StringBuffer();
		
		// applies the identation
		for(int i = 0; i < identation; i++) buffer.append("\t");
		
		// fills the buffer
		buffer.append("<requisition perception_type=\"" + this.perception_type + "\"/>\n");
				      
		// returns the answer
		return buffer.toString();
	}
	
	public String getObjectId() {
		// a requisition doesn't need an id
		return null;
	}
	
	public void setObjectId(String object_id) {
		// a requisition doesn't need an id
		// so, do nothing
	}
}