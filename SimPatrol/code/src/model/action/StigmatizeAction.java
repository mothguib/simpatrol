/* StigmatizeAction.java */

/* The package of this class. */
package model.action;

/* Imported classes and/or interfaces. */
import model.limitation.StaminaLimitation;

/**
 * Implements the action of depositing a stigma on the graph of the simulation.
 * 
 * Its effect can be controlled by stamina limitations.
 * 
 * @see StaminaLimitation
 */
public final class StigmatizeAction extends AtomicAction {
	/* Methods. */
	public String fullToXML(int identation) {
		// holds the answer for the method
		StringBuffer buffer = new StringBuffer();

		// applies the identation
		for (int i = 0; i < identation; i++)
			buffer.append("\t");

		// fills the buffer
		buffer.append("<action type=\"" + ActionTypes.STIGMATIZE + "\"/>\n");

		// returns the answer
		return buffer.toString();
	}
}