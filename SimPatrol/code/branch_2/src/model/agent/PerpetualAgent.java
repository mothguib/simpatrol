/* PerpetualAgent.java */

/* The package of this class. */
package model.agent;

/* Imported classes and/or interfaces. */
import model.graph.Vertex;
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
	 * @param vertex
	 *            The vertex that the agent comes from.
	 * @param label
	 *            The label of the agent.
	 * @param allowed_perceptions
	 *            The allowed perceptions to the agent.
	 * @param allowed_actions
	 *            The allowed actions to the agent.
	 */
	public PerpetualAgent(String label, Vertex vertex,
			PerceptionPermission[] allowed_perceptions,
			ActionPermission[] allowed_actions) {
		super(label, vertex, allowed_perceptions, allowed_actions);
	}
}