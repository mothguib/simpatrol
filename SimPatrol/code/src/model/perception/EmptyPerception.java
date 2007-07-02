/* EmptyPerception.java */

/* The package of this class. */
package model.perception;

/** Implements the perceptions that actually don't require anything. */
public final class EmptyPerception extends ProactivePerception {
	/* Methods. */
	public String toXML(int identation) {
		// holds the answer for the method
		StringBuffer buffer = new StringBuffer();
		
		// applies the identation
		for(int i = 0; i < identation; i++) buffer.append("\t");
		
		// fills the buffer
		buffer.append("<perception/>\n");
				      
		// returns the answer
		return buffer.toString();
	}
}