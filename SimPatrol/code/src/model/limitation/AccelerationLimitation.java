/* AccelerationLimitation.java */

/* The package of this class. */
package model.limitation;

/* Imported classes and/or interfaces. */
import model.action.GoToAction;

/**
 * Implements the limitations that control the acceleration of the movement in a
 * permission that lets an agent move through the graph to be patrolled.
 * 
 * @see GoToAction
 */
public final class AccelerationLimitation extends Limitation {
	/* Attributes. */
	/** The acceleration limit, measured in depth/sec^2 or depth/cycle^2. */
	private double acceleration;

	/* Methods. */
	/**
	 * Constructor.
	 * 
	 * @param acceleration
	 *            The acceleration limit, measured in depth/sec^2 or
	 *            depth/cycle^2.
	 */
	public AccelerationLimitation(double acceleration) {
		this.acceleration = acceleration;
	}

	/**
	 * Returns the acceleration limit imposed by the limitation.
	 * 
	 * @return The acceleration limit imposed by the limitation.
	 */
	public double getAcceleration() {
		return this.acceleration;
	}

	public String fullToXML(int identation) {
		// holds the answer for the method
		StringBuffer buffer = new StringBuffer();

		// applies the identation
		for (int i = 0; i < identation; i++)
			buffer.append("\t");

		// fills the buffer
		buffer.append("<limitation type=\"" + LimitationTypes.ACCELERATION
				+ "\">\n");

		// puts the parameters of the limitation
		for (int i = 0; i < identation + 1; i++)
			buffer.append("\t");
		buffer.append("<lmt_parameter value=\"" + this.acceleration + "\"/>\n");

		// closes the main tag
		for (int i = 0; i < identation; i++)
			buffer.append("\t");
		buffer.append("</limitation>\n");

		// returns the answer
		return buffer.toString();
	}
}