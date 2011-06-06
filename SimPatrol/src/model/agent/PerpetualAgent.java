/* PerpetualAgent.java */

/* The package of this class. */
package model.agent;

/* Imported classes and/or interfaces. */
import model.graph.Node;
import model.permission.ActionPermission;
import model.permission.PerceptionPermission;

/**
 * Implements the agents that compound the closed societies of SimPatrol.
 */
public final class PerpetualAgent extends Agent {
	/* Methods. */
	/**
	 * Constructor.
	 * 
	 * @param node
	 *            The node that the agent comes from.
	 * @param label
	 *            The label of the agent.
	 * @param allowed_perceptions
	 *            The allowed perceptions to the agent.
	 * @param allowed_actions
	 *            The allowed actions to the agent.
	 */
	public PerpetualAgent(String label, Node node,
			PerceptionPermission[] allowed_perceptions,
			ActionPermission[] allowed_actions) {
		super(label, node, allowed_perceptions, allowed_actions);
	}
}