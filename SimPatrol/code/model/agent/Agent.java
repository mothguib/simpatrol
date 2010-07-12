/* Agent.java (2.0) */
package br.org.simpatrol.server.model.agent;

/* Imported classes and/or interfaces. */
import java.util.Set;

import br.org.simpatrol.server.model.graph.Edge;
import br.org.simpatrol.server.model.graph.Vertex;
import br.org.simpatrol.server.model.interfaces.Stamined;
import br.org.simpatrol.server.model.interfaces.XMLable;

/**
 * Implements the internal agents of SimPatrol.
 * 
 * @author Daniel Henriques Moreira (dhenriques@gmail.com)
 */
public abstract class Agent implements XMLable, Stamined {
	/* Attributes. */
	/** The id of the agent. */
	private String id;

	/** The label of the agent. */
	private String label;

	/** The vertex from where the agent comes. */
	private Vertex vertex;

	/** The edge where the agent is. */
	private Edge edge;

	/**
	 * Registers where the agent is on the edge, i.e. how much of the edge the
	 * agent already trespassed.
	 */
	private double elapsedLength;

	/**
	 * The stamina of the agent. Its default value is 1.0.
	 */
	private double stamina = 1.0;

	/**
	 * The maximum value for the stamina of the agent. Its default value is 1.0.
	 */
	private double maxStamina = 1.0;

	/** The set of abilities related to the perceptions of the agent. */
	protected Set<PerceptionAbility> perceptionAbilities;

	/** The set of abilities related to the actions of the agent. */
	protected Set<ActionAbility> actionAbilities;

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
	public Agent(String id, String label, Vertex vertex,
			Set<PerceptionAbility> perceptionAbilities,
			Set<ActionAbility> actionAbilities) {
		this.id = id;
		this.label = label;
		this.vertex = vertex;
		this.edge = null;
		this.elapsedLength = 0;
		this.perceptionAbilities = perceptionAbilities;
		this.actionAbilities = actionAbilities;
	}

	/**
	 * Returns the vertex from where the agent comes.
	 * 
	 * @return The vertex from where the agent comes.
	 */
	public Vertex getVertex() {
		return this.vertex;
	}

	/**
	 * Configures the edge where the agent is, as well as its position on it.
	 * 
	 * @param edge
	 *            The edge where the agent is. NULL if the agent in on a vertex.
	 * @param elapsedLength
	 *            Registers where the agent is on the edge. If such value is
	 *            bigger than the length of the edge, then the elapsed length
	 *            will be set as the length of the edge.
	 */
	public void setEdge(Edge edge, double elapsedLength) {
		this.edge = edge;

		if (this.edge == null)
			this.elapsedLength = 0;
		else
			this.elapsedLength = elapsedLength;

		if (this.edge != null && this.edge.getLength() < this.elapsedLength)
			this.elapsedLength = this.edge.getLength();
	}

	/**
	 * Returns the edge where the agent is.
	 * 
	 * @return The edge where the agent is, or NULL if it is on a vertex.
	 */
	public Edge getEdge() {
		return this.edge;
	}

	/**
	 * Returns where the agent is on the edge, i.e. how much of the edge the
	 * agent already trespassed.
	 * 
	 * @return How much of the edge the agent already trespassed, or ZERO if it
	 *         is on a vertex.
	 */
	public double getElapsedLength() {
		return this.elapsedLength;
	}

	public void setStamina(double stamina) {
		if (stamina <= this.maxStamina)
			this.stamina = stamina;
		else
			this.stamina = this.maxStamina;
	}

	public double getStamina() {
		return this.stamina;
	}

	public void decStamina(double factor) {
		this.stamina = this.stamina - factor;

		if (this.stamina < 0) {
			this.stamina = 0;
		}
	}

	public void incStamina(double factor) {
		this.stamina = this.stamina + factor;

		if (this.stamina > this.maxStamina)
			this.stamina = this.maxStamina;
	}

	public void setMaxStamina(double maxStamina) {
		this.maxStamina = maxStamina;

		if (this.stamina > this.maxStamina)
			this.stamina = this.maxStamina;
	}

	public double getMaxStamina() {
		return this.maxStamina;
	}

	public String fullToXML() {
		// holds the answer being constructed
		StringBuffer buffer = new StringBuffer();

		// fills the buffer
		buffer.append("<agent id=\"" + this.id + "\" label=\"" + this.label
				+ "\" vertex_id=\"" + this.vertex.getId());

		if (this.edge != null) {
			buffer.append("\" edge_id=\"" + this.edge.getId()
					+ "\" elapsed_length=\"" + this.elapsedLength);
		}

		buffer.append("\" stamina=\"" + this.stamina);
		buffer.append("\" max_stamina=\"" + this.maxStamina);

		// puts the eventual perception abilities
		if (this.perceptionAbilities != null
				&& this.perceptionAbilities.size() > 0) {
			buffer.append("\">");

			for (PerceptionAbility ability : this.perceptionAbilities)
				buffer.append(ability.fullToXML());
		}

		// puts the eventual action abilities
		if (this.actionAbilities != null && this.actionAbilities.size() > 0) {
			if (this.perceptionAbilities == null
					|| this.perceptionAbilities.size() == 0)
				buffer.append("\">");

			for (ActionAbility ability : this.actionAbilities)
				buffer.append(ability.fullToXML());
		}

		// closes the main tag
		if ((this.perceptionAbilities == null || this.perceptionAbilities
				.size() == 0)
				&& (this.actionAbilities == null || this.actionAbilities.size() == 0))
			buffer.append("\"/>");
		else
			buffer.append("</agent>");

		// returns the buffer content
		return buffer.toString();
	}

	public String reducedToXML() {
		// holds the answer being constructed
		StringBuffer buffer = new StringBuffer();

		// fills the buffer
		buffer.append("<agent id=\"" + this.id + "\" label=\"" + this.label
				+ "\" vertex_id=\"" + this.vertex.getId());

		if (this.edge != null) {
			buffer.append("\" edge_id=\"" + this.edge.getId()
					+ "\" elapsed_length=\"" + this.elapsedLength);
		}

		buffer.append("\"/>");

		// returns the buffer content
		return buffer.toString();
	}

	public String getId() {
		return this.id;
	}
}