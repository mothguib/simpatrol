/* ClosedSociety.java */

/* The package of this class. */
package model.agent;

/** Implements the closed societies of agents of SimPatrol. */
public final class ClosedSociety extends Society {
	/* Methods. */
	/**
	 * Constructor.
	 * 
	 * @param label
	 *            The label of the closed society.
	 * @param perpetual_agents
	 *            The perpetual agents that compound the closed society.
	 */
	public ClosedSociety(String label, PerpetualAgent[] perpetual_agents) {
		super(label, perpetual_agents);
	}

	public boolean addAgent(Agent agent) {
		// registers if the agent already exists in the society
		boolean agent_exists = false;
		Object[] agents_array = this.agents.toArray();
		for (int i = 0; i < agents_array.length; i++)
			if (agents_array[i].equals(agent))
				agent_exists = true;

		if (!agent_exists && agent instanceof PerpetualAgent) {
			this.agents.add(agent);
			return true;
		}

		return false;
	}
}