/* RechargeAction.java */

/* The package of this class. */
package model.action;

/* Imported classes and/or interfaces. */
import model.limitation.SpeedLimitation;
import model.limitation.StaminaLimitation;

/**
 * Implements the action of recharging the agent's stamina.
 * 
 * Its effect can be controlled by stamina and speed limitations.
 * 
 * @see StaminaLimitation
 * @see SpeedLimitation
 */
public final class RechargeAction extends CompoundAction {
	/* Attributes. */
	/** The value to be added to the agent's stamina. */
	private double stamina;

	/* Methods. */
	/**
	 * Constructor.
	 * 
	 * @param stamina
	 *            The value to be added to the agent's stamina.
	 */
	public RechargeAction(double stamina) {
		this.stamina = stamina;
	}

	/**
	 * Returns the value to be added to the agent's stamina.
	 * 
	 * @return The value to be added to the agent's stamina.
	 */
	public double getStamina() {
		return this.stamina;
	}

	public String fullToXML(int identation) {
		// holds the answer for the method
		StringBuffer buffer = new StringBuffer();

		// applies the identation
		for (int i = 0; i < identation; i++)
			buffer.append("\t");

		// fills the buffer
		buffer.append("<action type=\"" + ActionTypes.RECHARGE
				+ "\" stamina=\"" + this.stamina + "\"/>\n");

		// returns the answer
		return buffer.toString();
	}
}