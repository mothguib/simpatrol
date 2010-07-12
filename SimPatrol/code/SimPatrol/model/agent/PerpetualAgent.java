/* PerpetualAgent.java (2.0) */
package br.org.simpatrol.server.model.agent;

/* Imported classes and/or interfaces. */
import java.util.Set;

import br.org.simpatrol.server.model.graph.Vertex;

/**
 * Implements the agents that compound closed societies in SimPatrol.
 * 
 * @see ClosedSociety
 * @author Daniel Henriques Moreira (dhenriques@gmail.com)
 */
public class PerpetualAgent extends Agent {
	/* Methods. */
	/**
	 * Constructor.
	 * 
	 * @param id
	 *            The id of the agent.
	 * @param label
	 *            The label of the agent.
	 * @param vertex
	 *            The vertex from where the agent comes.
	 * @param perceptionAbilities
	 *            The abilities related to the perceptions of the agent.
	 * @param actionAbilities
	 *            The abilities related to the actions of the agent.
	 */
	public PerpetualAgent(String id, String label, Vertex vertex,
			Set<PerceptionAbility> perceptionAbilities,
			Set<ActionAbility> actionAbilities) {
		super(id, label, vertex, perceptionAbilities, actionAbilities);
	}
}