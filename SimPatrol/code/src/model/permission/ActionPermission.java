/* ActionPermission.java */

/* The package of this class. */
package model.permission;

/* Imported classes and/or interfaces. */
import model.limitation.Limitation;

/** Implements the permissions that control the actions of an agent
 *  in SimPatrol.  */
public class ActionPermission extends Permission {
	/* Attributes */
	/** The type of the allowed actions. */
	private int action_type;
	
	/* Methods. */
	/** Constructor.
	 *  @param limitations The limitations imposed to the agent.
	 *  @param action_type The type of the allowed actions. */
	public ActionPermission(Limitation[] limitations, int action_type) {
		super(limitations);
		this.action_type = action_type;
	}
	
	public String toXML(int identation) {
		// holds the answer for the method
		StringBuffer buffer = new StringBuffer();
		
		// applies the identation
		for(int i = 0; i < identation; i++) buffer.append("\t");
		
		// fills the buffer
		buffer.append("<allowed_action type=\"" + this.action_type);
		
		// puts the eventual limitations in the buffer
		if(this.limitations != null) {
			buffer.append("\">\n");
			
			Object[] limitations_array = this.limitations.toArray();				
			for(int i = 0; i < limitations_array.length; i++)
				buffer.append(((Limitation) limitations_array[i]).toXML(identation + 1));
			
			// closes the buffer tag
			for(int i = 0; i < identation; i++) buffer.append("\t");
			buffer.append("</allowed_action>\n");
		}
		else buffer.append("\"/>\n");

		// returns the answer
		return buffer.toString();
	}
}
