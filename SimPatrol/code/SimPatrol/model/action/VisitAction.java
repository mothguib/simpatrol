/* VisitAction.java (2.0) */
package br.org.simpatrol.server.model.action;

/**
 * Implements the actions of visiting a vertex.
 * 
 * @author Daniel Henriques Moreira (dhenriques@gmail.com)
 */
public class VisitAction extends AtomicAction {
	/* Methods. */
	protected void initActionType() {
		this.actionType = ActionTypes.VISIT;
	}

	public String fullToXML() {
		// holds the answer for the method
		StringBuffer buffer = new StringBuffer();

		// fills the buffer
		buffer.append("<action type=\"" + this.actionType.getType() + "\"/>");

		// returns the answer
		return buffer.toString();
	}
}