/* SpeedLimitation.java */

/* The package of this class. */
package model.limitation;

/* Imported classes and/or interfaces. */
import model.action.AtomicRechargeAction;
import model.action.GoToAction;
import model.action.RechargeAction;

/**
 * Implements the limitations that control the speed of the movement in a
 * permission that lets an agent move through the graph to be patrolled.
 * 
 * Or...
 * 
 * Implements the limitations that control the speed of recharging in a
 * permission that lets an agent recharge its stamina value.
 * 
 * @see GoToAction
 * @see AtomicRechargeAction
 * @see RechargeAction
 */
public final class SpeedLimitation extends Limitation {
	/* Attributes. */
	/**
	 * The speed limit of the movement, measured in depth per second or in depth
	 * per cycle.
	 * 
	 * Or...
	 * 
	 * The speed limit of the recharging, measured in stamina per second or in
	 * stamina per cycle.
	 */
	private double speed;

	/* Methods. */
	/**
	 * Constructor.
	 * 
	 * @param speed
	 *            The speed limit.
	 */
	public SpeedLimitation(double speed) {
		this.speed = speed;
	}

	/**
	 * Returns the speed limit imposed by the limitation.
	 * 
	 * @return The speed limit imposed by the limitation.
	 */
	public double getSpeed() {
		return this.speed;
	}

	public String fullToXML(int identation) {
		// holds the answer for the method
		StringBuffer buffer = new StringBuffer();

		// applies the identation
		for (int i = 0; i < identation; i++)
			buffer.append("\t");

		// fills the buffer
		buffer.append("<limitation type=\"" + LimitationTypes.SPEED + "\">\n");

		// puts the parameters of the limitation
		for (int i = 0; i < identation + 1; i++)
			buffer.append("\t");
		buffer.append("<lmt_parameter value=\"" + this.speed + "\"/>\n");

		// closes the main tag
		for (int i = 0; i < identation; i++)
			buffer.append("\t");
		buffer.append("</limitation>\n");

		// returns the answer
		return buffer.toString();
	}
}