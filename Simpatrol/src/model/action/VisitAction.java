/* VisitAction.java */

/* The package of this class. */
package model.action;

/* Imported classes and/or interfaces. */
import model.limitation.StaminaLimitation;

/**
 * Implements the action of visiting a vertex.
 * 
 * Its effect can be controlled by stamina limitations.
 * 
 * @see StaminaLimitation
 */
public final class VisitAction extends AtomicAction {
	/* Methods. */
	public String fullToXML(int identation) {
		// holds the answer for the method
		StringBuffer buffer = new StringBuffer();

		// applies the identation
		for (int i = 0; i < identation; i++)
			buffer.append("\t");

		// fills the buffer
		buffer.append("<action type=\"" + ActionTypes.VISIT + "\"/>\n");

		// returns the answer
		return buffer.toString();
	}
}