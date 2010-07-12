/* AccelerationLimitation.java (2.0) */
package br.org.simpatrol.server.model.limitation;

/* Imported classes and/or interfaces. */
import br.org.simpatrol.server.model.action.GoToAction;
import br.org.simpatrol.server.model.action.RechargeAction;

/**
 * Implements the limitations that control the acceleration related to some
 * abilities of the agents in SimPatrol.
 * 
 * Such abilities are related to the execution of the {@link GoToAction} and
 * {@link RechargeAction} actions.
 * 
 * @author Daniel Henriques Moreira (dhenriques@gmail.com)
 */
public class AccelerationLimitation extends Limitation {
	/* Attributes. */
	/** The acceleration limit, measured in unities of action per sec^2. */
	private double acceleration;

	/* Methods. */
	/**
	 * Constructor.
	 * 
	 * @param acceleration
	 *            The acceleration limit, measured in unities of action per
	 *            sec^2.
	 */
	public AccelerationLimitation(double acceleration) {
		super();
		this.acceleration = acceleration;
	}

	/**
	 * Returns the acceleration limit imposed by the limitation.
	 * 
	 * @return The acceleration limit imposed by the limitation, measured in
	 *         unities of action per sec^2.
	 */
	public double getAcceleration() {
		return this.acceleration;
	}

	protected void initActionType() {
		this.limitationType = LimitationTypes.ACCELERATION;
	}

	public String fullToXML() {
		// holds the answer for the method
		StringBuffer buffer = new StringBuffer();

		// fills the buffer
		buffer.append("<limitation type=\"" + this.limitationType.getType()
				+ "\">");

		// puts the parameters of the limitation
		buffer.append("<lmt_parameter value=\"" + this.acceleration + "\"/>");

		// closes the main tag
		buffer.append("</limitation>");

		// returns the answer
		return buffer.toString();
	}

}