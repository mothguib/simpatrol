/* AgentsPerception.java */

/* The package of this class. */
package model.perception;

/* Imported classes and/or interfaces. */
import java.util.HashSet;
import java.util.Set;
import model.agent.Agent;

/** Implements the ability of an agent to perceive others. */
public final class AgentsPerception extends Perception {
	/* Attributes. */
	/** The perceived agents. */
	private Set<Agent> agents;

	/* Methods. */
	/**
	 * Constructor.
	 * 
	 * @param perceived_agents
	 *            The perceived agents.
	 */
	public AgentsPerception(Agent[] perceived_agents) {
		this.agents = new HashSet<Agent>();
		for (int i = 0; i < perceived_agents.length; i++)
			this.agents.add(perceived_agents[i]);
	}

	public String fullToXML(int identation) {
		// holds the answer for the method
		StringBuffer buffer = new StringBuffer();

		// applies the identation and opens the "perception" tag
		for (int i = 0; i < identation; i++)
			buffer.append("\t");
		buffer.append("<perception type=\"" + PerceptionTypes.AGENTS + "\">\n");

		// puts the agents, in a lighter version
		for (Agent agent : this.agents)
			buffer.append(agent.reducedToXML(identation + 1));

		// applies the identation and closes the "perception" tag
		for (int i = 0; i < identation; i++)
			buffer.append("\t");
		buffer.append("</perception>\n");

		// returns the answer
		return buffer.toString();
	}
}