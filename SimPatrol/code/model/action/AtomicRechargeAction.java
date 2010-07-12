/* AtomicRechargeAction.java (2.0) */
package br.org.simpatrol.server.model.action;

/**
 * Implements the instantaneous actions of recharging the stamina of an agent.
 * 
 * @author Daniel Henriques Moreira (dhenriques@gmail.com)
 */
public class AtomicRechargeAction extends AtomicAction {
	/* Attributes. */
	/** The value to be added to the stamina of the agent. */
	private double stamina;

	/* Methods. */
	/**
	 * Constructor.
	 * 
	 * @param stamina
	 *            The value to be added to the stamina of the agent.
	 */
	public AtomicRechargeAction(double stamina) {
		super();
		this.stamina = stamina;
	}

	/**
	 * Returns the value to be added to the stamina of the agent.
	 * 
	 * @return The value to be added to the stamina of the agent.
	 */
	public double getStamina() {
		return this.stamina;
	}

	protected void initActionType() {
		this.actionType = ActionTypes.ATOMIC_RECHARGE;
	}

	public String fullToXML() {
		// holds the answer for the method
		StringBuffer buffer = new StringBuffer();

		// fills the buffer
		buffer.append("<action type=\"" + this.actionType.getType()
				+ "\" stamina=\"" + this.stamina + "\"/>");

		// returns the answer
		return buffer.toString();
	}
}