/* OpenSociety.java (2.0) */
package br.org.simpatrol.server.model.agent;

/* Imported classes and/or interfaces. */
import java.util.HashSet;
import java.util.Set;

/**
 * Implements the open societies of agents in SimPatrol.
 * 
 * @see SeasonalAgent
 * @author Daniel Henriques Moreira (dhenriques@gmail.com)
 */
public class OpenSociety extends Society {
	/* Methods. */
	/**
	 * Constructor.
	 * 
	 * @param id
	 *            The id of the society
	 * @param label
	 *            The label of the closed society.
	 * @param agents
	 *            The seasonal agents that compound the open society.
	 */
	public OpenSociety(String id, String label, Set<SeasonalAgent> agents) {
		super(id, label, agents);

		// for each agent, sets its society
		for (SeasonalAgent agent : agents)
			agent.setSociety(this);
	}

	/**
	 * Removes a given agent from the society.
	 * 
	 * @param agent
	 *            The agent to be removed.
	 */
	public void removeAgent(SeasonalAgent agent) {
		this.agents.remove(agent);
	}

	@SuppressWarnings("unchecked")
	public boolean addAgent(Agent agent) {
		if (agent instanceof SeasonalAgent) {
			if (this.agents == null)
				this.agents = new HashSet<SeasonalAgent>();

			((Set<SeasonalAgent>) this.agents).add((SeasonalAgent) agent);
			((SeasonalAgent) agent).setSociety(this);
			return true;
		}

		return false;
	}

	public String fullToXML() {
		// holds the answer being constructed
		StringBuffer buffer = new StringBuffer(super.fullToXML());

		// obtains the last index of the label of the society
		int labelLastIndex = buffer.indexOf("label=\"") + 7;
		labelLastIndex = buffer.substring(labelLastIndex).indexOf("\"");

		// configures this society as an open one
		buffer.insert(labelLastIndex + 1, " is_closed=\"false\"");

		// returns the answer
		return buffer.toString();
	}
}