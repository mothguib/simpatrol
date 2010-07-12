/* StaminaLimitation.java (2.0) */
package br.org.simpatrol.server.model.limitation;

/* Imported classes and/or interfaces. */
import br.org.simpatrol.server.model.action.Action;
import br.org.simpatrol.server.model.action.CompoundAction;
import br.org.simpatrol.server.model.perception.Perception;

/**
 * Implements the limitations that control the stamina of an agent. It can be
 * applied to the sense of any {@link Perception} object, and to the execution
 * of any {@link Action} object.
 * 
 * @author Daniel Henriques Moreira (dhenriques@gmail.com)
 */
public class StaminaLimitation extends Limitation {
	/* Attributes. */
	/**
	 * The cost of stamina associated with the sense of a perception, or with
	 * the execution of an action.
	 * 
	 * In the case of the sense of a {@link Perception} object, or in the case
	 * of the execution of a {@link CompoundAction} object, this value is
	 * reduced from the stamina of the agent in every second.
	 */
	private double cost;

	/* Methods. */
	/**
	 * Constructor.
	 * 
	 * @param cost
	 *            The cost of stamina associated with the sense of a perception,
	 *            or with the execution of an action.
	 */
	public StaminaLimitation(double cost) {
		super();
		this.cost = cost;
	}

	/**
	 * Returns the cost of the limitation.
	 * 
	 * @return The cost of the limitation, related to the amount of stamina
	 *         spent with the sense of a perception, or with the execution of an
	 *         action. In the case of the sense of a {@link Perception} object,
	 *         or in the case of the execution of a {@link CompoundAction}
	 *         object, this value is reduced from the stamina of the agent in
	 *         every second.
	 */
	public double getCost() {
		return this.cost;
	}

	protected void initActionType() {
		this.limitationType = LimitationTypes.STAMINA;
	}

	public String fullToXML() {
		// holds the answer for the method
		StringBuffer buffer = new StringBuffer();

		// fills the buffer
		buffer.append("<limitation type=\"" + this.limitationType.getType()
				+ "\">");

		buffer.append("<lmt_parameter value=\"" + this.cost + "\"/>");

		// closes the main tag
		buffer.append("</limitation>");

		// returns the answer
		return buffer.toString();
	}
}