/* Answer.java */

/* The package of this class. */
package control.requisition;

/* Imported classes and/or interfaces. */
import model.interfaces.XMLable;
import model.perception.Perception;

/** Implements the answer given by the simulator to
 *  the requisitions of perceptions made by external actors. */
public final class Answer implements XMLable {
	/* Attributes. */
	/** The perception given by the simulator. */
	private Perception perception;
	
	/* Methods. */
	/** Constructor.
	 *  @param perception The perception given by the simulator to the requirer. */
	public Answer(Perception perception) {
		this.perception = perception;
	}
	
	public String toXML(int identation) {
		// holds the answer for the method
		StringBuffer buffer = new StringBuffer();
		
		// applies the identation
		for(int i = 0; i < identation; i++) buffer.append("\t");
		
		// fills the buffer
		if(this.perception == null) buffer.append("<answer/>\n");
		else {
			// opens the tag
			buffer.append("<answer>\n");
			
			// puts the perception
			buffer.append(this.perception.toXML(identation + 1));
			
			// applies the identation and closes the tag
			for(int i = 0; i < identation; i++) buffer.append("\t");
			buffer.append("</answer>\n");
		}
				      
		// returns the answer
		return buffer.toString();
	}
	
	public String getObjectId() {
		// an answer doesn't need an id
		return null;
	}

	public void setObjectId(String object_id) {
		// an answer doesn't need an id
		// so do nothing
	}
}
