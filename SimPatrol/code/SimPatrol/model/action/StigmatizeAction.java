/* StigmatizeAction.java (2.0) */
package br.org.simpatrol.server.model.action;

import br.org.simpatrol.server.model.stigma.Stigma;

/**
 * Implements the action of depositing a {@link Stigma} object on the graph of
 * the simulation.
 * 
 * @author Daniel Henriques Moreira (dhenriques@gmail.com)
 */
public class StigmatizeAction extends AtomicAction {
	/* Attributes. */
	/** The stigma to be deposited. */
	private Stigma stigma;

	/* Methods. */
	/**
	 * Constructor.
	 * 
	 * @param stigma
	 *            The stigma to be deposited on the graph of the simulation.
	 */
	public StigmatizeAction(Stigma stigma) {
		super();
		this.stigma = stigma;
	}

	/**
	 * Returns the stigma to be deposited on the graph of the simulation.
	 * 
	 * @return The stigma to be deposited on the graph of the simulation.
	 */
	public Stigma getStigma() {
		return this.stigma;
	}

	protected void initActionType() {
		this.actionType = ActionTypes.STIGMATIZE;
	}

	public String fullToXML() {
		// holds the answer for the method
		StringBuffer buffer = new StringBuffer();

		// fills the buffer
		buffer.append("<action type=\"" + this.actionType.getType() + "\">");
		buffer.append(this.stigma.fullToXML());
		buffer.append("</action>");

		// returns the answer
		return buffer.toString();
	}
}