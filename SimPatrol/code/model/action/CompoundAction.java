/* CompoundAction.java (2.0) */
package br.org.simpatrol.server.model.action;

/* Imported classes and/or interfaces. */
import java.util.List;

import br.org.simpatrol.server.model.agent.Agent;
import br.org.simpatrol.server.model.environment.Environment;
import br.org.simpatrol.server.model.limitation.Limitation;

/**
 * Implements the compound actions of the agents of SimPatrol.
 * 
 * The compound actions are those ones that must be parsed into time chained
 * {@link AtomicAction} objects.
 * 
 * @author Daniel Henriques Moreira (dhenriques@gmail.com)
 */
public abstract class CompoundAction extends Action {
	/**
	 * Parses this compound action into time chained {@link AtomicAction}
	 * objects.
	 * 
	 * @param agent
	 *            The agent responsible for the compound action to be parsed.
	 * 
	 * @param environment
	 *            The environment of the simulation.
	 * 
	 * @param timeRate
	 *            The time rate used to generate the time chain of atomic
	 *            actions. Measured in seconds.
	 * 
	 * @param limitations
	 *            The limitations imposed to this compound action and that shall
	 *            interfere on its parsing.
	 * 
	 * @return A list of lists of {@link AtomicAction} objects. The outer list
	 *         sorts the actions according to the time they shall be attended by
	 *         the simulator. E.g. the first list holds the atomic actions that
	 *         shall be attended in t=0; the second list holds the atomic
	 *         actions that shall be attended in t=1; and so ever.
	 */
	public abstract List<List<? extends AtomicAction>> parse(Agent agent,
			Environment environment, double timeRate, Limitation... limitations);
}