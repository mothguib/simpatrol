/* SpeedLimitation.java (2.0) */
package br.org.simpatrol.server.model.limitation;

/* Imported classes and/or interfaces. */
import br.org.simpatrol.server.model.action.GoToAction;
import br.org.simpatrol.server.model.action.RechargeAction;

/**
 * Implements the limitations that control the speed related to some abilities
 * of the agents in SimPatrol.
 * 
 * Such abilities are related to the execution of the {@link GoToAction} and
 * {@link RechargeAction} actions.
 * 
 * @author Daniel Henriques Moreira (dhenriques@gmail.com)
 */
public class SpeedLimitation extends Limitation {
	/* Attributes. */
	/** The speed limit, measured in unities of action per second. */
	private double speed;

	/* Methods. */
	/**
	 * Constructor.
	 * 
	 * @param speed
	 *            The speed limit, measured in unities of action per second.
	 */
	public SpeedLimitation(double speed) {
		super();
		this.speed = speed;
	}

	/**
	 * Returns the speed limit imposed by the limitation.
	 * 
	 * @return The speed limit imposed by the limitation, measured in unities of
	 *         action per second.
	 */
	public double getSpeed() {
		return this.speed;
	}

	protected void initActionType() {
		this.limitationType = LimitationTypes.SPEED;
	}

	public String fullToXML() {
		// holds the answer for the method
		StringBuffer buffer = new StringBuffer();

		// fills the buffer
		buffer.append("<limitation type=\"" + this.limitationType.getType()
				+ "\">");

		// puts the parameters of the limitation
		buffer.append("<lmt_parameter value=\"" + this.speed + "\"/>");

		// closes the main tag
		buffer.append("</limitation>");

		// returns the answer
		return buffer.toString();
	}
}