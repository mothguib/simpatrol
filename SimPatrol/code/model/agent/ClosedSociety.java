/* ClosedSociety.java (2.0) */
package br.org.simpatrol.server.model.agent;

/* Imported classes and/or interfaces. */
import java.util.HashSet;
import java.util.Set;

/**
 * Implements the closed societies of agents in SimPatrol.
 * 
 * @see PerpetualAgent
 * @author Daniel Henriques Moreira (dhenriques@gmail.com)
 */
public class ClosedSociety extends Society {
	/* Methods. */
	/**
	 * Constructor.
	 * 
	 * @param id
	 *            The id of the closed society.
	 * @param label
	 *            The label of the closed society.
	 * @param agents
	 *            The perpetual agents that compound the closed society.
	 */
	public ClosedSociety(String id, String label, Set<PerpetualAgent> agents) {
		super(id, label, agents);
	}

	@SuppressWarnings("unchecked")
	public boolean addAgent(Agent agent) {
		if (agent instanceof PerpetualAgent) {
			if (this.agents == null)
				this.agents = new HashSet<PerpetualAgent>();

			((Set<PerpetualAgent>) this.agents).add((PerpetualAgent) agent);
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

		// configures this society as a closed one
		buffer.insert(labelLastIndex + 1, " is_closed=\"true\"");

		// returns the answer
		return buffer.toString();
	}
}