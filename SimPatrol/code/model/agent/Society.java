/* Society.java (2.0) */
package br.org.simpatrol.server.model.agent;

/* Imported classes and/or interfaces. */
import java.util.Set;

import br.org.simpatrol.server.model.interfaces.XMLable;

/**
 * Implements the societies of agents in SimPatrol.
 * 
 * @see Agent
 * @author Daniel Henriques Moreira (dhenriques@gmail.com)
 */
public abstract class Society implements XMLable {
	/* Attributes. */
	/** The id of the society. */
	private String id;

	/** The label of the society. */
	private String label;

	/** The set of agents of the society. */
	protected Set<? extends Agent> agents;

	/* Methods. */
	/**
	 * Constructor.
	 * 
	 * @param id
	 *            The id of the society.
	 * @param label
	 *            The label of the society.
	 * @param agents
	 *            The agents that compound the society.
	 */
	public Society(String id, String label, Set<? extends Agent> agents) {
		this.id = id;
		this.label = label;
		this.agents = agents;
	}

	/**
	 * Adds a given agent to the society.
	 * 
	 * @param agent
	 *            The agent to be added.
	 * @return TRUE if the agent was added successfully, FALSE if not.
	 */
	public abstract boolean addAgent(Agent agent);

	public String fullToXML() {
		// holds the answer being constructed
		StringBuffer buffer = new StringBuffer();

		// fills the buffer
		buffer.append("<society id=\"" + this.id + "\" label=\"" + this.label);

		// inserts the agents, if there are some, and closes the main tag
		if (this.agents.isEmpty())
			buffer.append("\"/>");
		else {
			buffer.append("\">");

			for (Agent agent : this.agents)
				buffer.append(agent.fullToXML());

			// finishes the buffer content
			buffer.append("</society>");
		}

		// returns the buffer content
		return buffer.toString();
	}

	public String reducedToXML() {
		// holds the answer being constructed
		StringBuffer buffer = new StringBuffer();

		// fills the buffer
		buffer.append("<society id=\"" + this.id + "\" label=\"" + this.label);

		// inserts the agents, if there are some, and closes the main tag
		if (this.agents.isEmpty())
			buffer.append("\"/>");
		else {
			buffer.append("\">");

			for (Agent agent : this.agents)
				buffer.append(agent.reducedToXML());

			// finishes the buffer content
			buffer.append("</society>");
		}

		// returns the buffer content
		return buffer.toString();
	}

	public String getId() {
		return this.id;
	}
}