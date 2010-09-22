/* AtomicRechargeAction.java */

/* The package of this class. */
package util.action;

import util.agent.Agent;
import util.agent.AgentStates;


/**
 * Implements the atomic action of recharging the agent's stamina.
 * 
 * Its effect can be controlled by stamina and speed limitations.
 * 
 * @see StaminaLimitation
 * @see SpeedLimitation
 */
public final class AtomicRechargeAction extends AtomicAction {
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
	public AtomicRechargeAction(double stamina) {
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

	@Override
	public boolean perform_action(Agent agent) {
		agent.setState(AgentStates.RECHARGING);
		return false;
		
	}

}