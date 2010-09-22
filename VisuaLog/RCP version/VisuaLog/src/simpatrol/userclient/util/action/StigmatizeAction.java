/* StigmatizeAction.java */

/* The package of this class. */
package simpatrol.userclient.util.action;

import simpatrol.userclient.util.agent.Agent;
import simpatrol.userclient.util.agent.AgentStates;

/* Imported classes and/or interfaces. */


/**
 * Implements the action of depositing a stigma on the graph of the simulation.
 * 
 * Its effect can be controlled by stamina limitations.
 * 
 * @see StaminaLimitation
 */
public final class StigmatizeAction extends AtomicAction {

	@Override
	public boolean perform_action(Agent agent) {
		agent.setState(AgentStates.STIGMATIZING);
		return true;
		
	}
	/* Methods. */

}