/* AgentsPerception.java (2.0) */
package br.org.simpatrol.server.model.perception;

/* Imported classes and/or interfaces. */
import java.util.Set;

import br.org.simpatrol.server.model.agent.Agent;

/**
 * Implements the ability of an agent to perceive others.
 * 
 * @see Agent
 * @author Daniel Henriques Moreira (dhenriques@gmail.com)
 */
public class AgentsPerception extends Perception {
	/* Attributes. */
	/** The perceived agents. */
	private Set<Agent> agents;

	/* Methods. */
	/**
	 * Constructor.
	 * 
	 * @param perceivedAgents
	 *            The perceived agents.
	 */
	public AgentsPerception(Set<Agent> perceivedAgents) {
		super();
		this.agents = perceivedAgents;
	}

	protected void initPerceptionType() {
		this.perceptionType = PerceptionTypes.AGENTS;
	}

	public String fullToXML() {
		// holds the answer for the method
		StringBuffer buffer = new StringBuffer();

		// mounts the buffer content
		buffer.append("<perception type=\"" + this.perceptionType.getType()
				+ "\">");

		// puts the agents, in a lighter version
		for (Agent agent : this.agents)
			buffer.append(agent.reducedToXML());

		// closes the "perception" tag
		buffer.append("</perception>");

		// returns the answer
		return buffer.toString();
	}
}