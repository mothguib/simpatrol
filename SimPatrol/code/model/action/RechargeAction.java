/* RechargeAction.java (2.0) */
package br.org.simpatrol.server.model.action;

/* Imported classes and/or interfaces. */
import java.util.LinkedList;
import java.util.List;

import br.org.simpatrol.server.model.agent.Agent;
import br.org.simpatrol.server.model.environment.Environment;
import br.org.simpatrol.server.model.limitation.AccelerationLimitation;
import br.org.simpatrol.server.model.limitation.Limitation;
import br.org.simpatrol.server.model.limitation.SpeedLimitation;

/**
 * Implements the actions of recharging the stamina of an agent.
 * 
 * @author Daniel Henriques Moreira (dhenriques@gmail.com)
 */
public class RechargeAction extends CompoundAction {
	/* Attributes. */
	/** The value to be added to the stamina of the agent. */
	private double stamina;

	/**
	 * The initial speed of the recharge action. Measured in
	 * "stamina unities per second". Its default value is 1.0.
	 */
	private double initialSpeed = 1.0;

	/**
	 * The acceleration of the recharge action. Measured in
	 * "stamina unities/sec^2". Its default value is ZERO.
	 */
	private double acceleration = 0.0;

	/* Methods. */
	/**
	 * Constructor.
	 * 
	 * @param stamina
	 *            The value to be added to the stamina of the agent.
	 */
	public RechargeAction(double stamina) {
		super();
		this.stamina = stamina;
	}

	/**
	 * Configures the initial speed of the recharge action.
	 * 
	 * @param initialSpeed
	 *            The initial speed of the recharge action. Measured in
	 *            "depth unities per second".
	 */
	public void setInitialSpeed(double initialSpeed) {
		this.initialSpeed = initialSpeed;
	}

	/**
	 * Configures the acceleration of the recharge action.
	 * 
	 * @param acceleration
	 *            The acceleration of the recharge action. Measured in
	 *            "depth unities/sec^2".
	 */

	public void setAcceleration(double acceleration) {
		this.acceleration = acceleration;
	}

	protected void initActionType() {
		this.actionType = ActionTypes.RECHARGE;
	}

	public List<List<? extends AtomicAction>> parse(Agent agent,
			Environment environment, double timeRate, Limitation... limitations) {
		// holds an eventual acc limitation to be applied to the recharge action
		AccelerationLimitation accelerationLimitation = null;

		// holds an eventual speed limitation to be applied to the recharge
		// action
		SpeedLimitation speedLimitation = null;

		// tries to find such limitation among the given ones
		for (Limitation limitation : limitations) {
			if (limitation instanceof AccelerationLimitation)
				accelerationLimitation = (AccelerationLimitation) limitation;
			else if (limitation instanceof SpeedLimitation)
				speedLimitation = (SpeedLimitation) limitation;

			if (accelerationLimitation != null && speedLimitation != null)
				break;
		}

		// holds the acceleration of the goto action
		double acceleration = this.acceleration;
		if (accelerationLimitation != null
				&& acceleration > accelerationLimitation.getAcceleration())
			acceleration = accelerationLimitation.getAcceleration();

		// holds the initial speed of the goto action
		double currentInitialSpeed = this.initialSpeed;
		if (speedLimitation != null
				&& currentInitialSpeed > speedLimitation.getSpeed())
			currentInitialSpeed = speedLimitation.getSpeed();

		// holds the needed atomic recharge actions
		List<AtomicRechargeAction> atomicRechargeActions = new LinkedList<AtomicRechargeAction>();

		// creates the plan of atomic recharge actions
		while (true) {
			// if the stamina of the recharge action is negative or equals to
			// zero, quits the main loop
			if (this.stamina <= 0)
				break;

			// holds how much stamina each atomic recharge action must recharge
			double staminaFactor = currentInitialSpeed * timeRate;

			// if the stamina factor is valid
			if (staminaFactor > 0) {
				// updates how much stamina remains to add to the agent
				double remainedStamina = this.stamina - staminaFactor;

				// if the remained stamina is negative
				if (remainedStamina < 0) {
					// updates the stamina factor
					staminaFactor = this.stamina;

					// updates the stamina value of the recharge action with
					// zero
					this.stamina = 0;
				}
				// else, updates the stamina value of the recharge action with
				// the remained value
				else
					this.stamina = remainedStamina;

				// adds the adequate atomic recharge action to the plan
				atomicRechargeActions.add(new AtomicRechargeAction(
						staminaFactor));
			}

			// else, quits the main loop
			else
				break;

			// updates the current speed of the recharge action
			// v = v0 + a*t
			currentInitialSpeed = currentInitialSpeed + acceleration * timeRate;
			if (currentInitialSpeed > speedLimitation.getSpeed())
				currentInitialSpeed = speedLimitation.getSpeed();
		}

		// if the plan is empty, returns null
		if (atomicRechargeActions.isEmpty())
			return null;
		// else, mounts and returns an answer
		else {
			List<List<? extends AtomicAction>> answer = new LinkedList<List<? extends AtomicAction>>();

			for (AtomicRechargeAction atomicRechargeAction : atomicRechargeActions) {
				List<AtomicRechargeAction> atomicRechargeActionList = new LinkedList<AtomicRechargeAction>();
				atomicRechargeActionList.add(atomicRechargeAction);

				answer.add(atomicRechargeActionList);
			}

			return answer;
		}
	}

	public String fullToXML() {
		// holds the answer for the method
		StringBuffer buffer = new StringBuffer();

		// fills the buffer
		buffer.append("<action type=\"" + this.actionType.getType()
				+ "\" initial_speed=\"" + this.initialSpeed
				+ "\" acceleration=\"" + this.acceleration + "\" stamina=\""
				+ this.stamina + "\"/>");

		// returns the answer
		return buffer.toString();
	}
}