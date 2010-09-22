/* VisitAction.java */

/* The package of this class. */
package util.action;

import util.agent.Agent;
import util.agent.AgentStates;

/* Imported classes and/or interfaces. */


/**
 * Implements the action of visiting a vertex.
 * 
 * Its effect can be controlled by stamina limitations.
 * 
 * @see StaminaLimitation
 */
public final class VisitAction extends AtomicAction {

	@Override
	public boolean perform_action(Agent agent) {
		agent.setState(AgentStates.VISITING);
		return true;
	}
	/* Methods. */

}