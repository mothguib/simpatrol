/* Agent.java */

/* The package of this class. */
package model.agent;

/* Imported classes and/or interfaces. */
import java.util.HashSet;
import java.util.Set;

import control.translator.AgentTranslator;
import view.XMLable;
import model.graph.Vertex;
import model.graph.Edge;
import model.permission.ActionPermission;
import model.permission.PerceptionPermission;

/** Implements the internal agents of SimPatrol. */
public abstract class Agent implements XMLable {
	/* Attributes. */
	/**
	 * The object id of the agent. Not part of the patrolling problem modeling.
	 */
	private String id;

	/** The label of the agent. */
	protected String label;

	/**
	 * The state of the agent.
	 * 
	 * @see AgentStates
	 */
	private int state;

	/** The vertex that the agent comes from. */
	protected Vertex vertex;

	/** The edge where the agent is. */
	private Edge edge;

	/**
	 * Registers where the agent is on the edge, i.e. how much of the edge
	 * remains for the agent to pass through it.
	 */
	private double elapsed_length;

	/**
	 * The stamina of the agent. Its default value is 1.0.
	 */
	private double stamina = 1.0;

	/**
	 * The maximum value for the agent's stamina. Its default value is 1.0.
	 */
	private double max_stamina = 1.0;

	/** The set of allowed perceptions. */
	protected Set<PerceptionPermission> allowed_perceptions;

	/** The set of allowed actions. */
	protected Set<ActionPermission> allowed_actions;

	/* Methods. */
	/**
	 * Constructor.
	 * 
	 * @param label
	 *            The label of the agent.
	 * @param vertex
	 *            The vertex that the agent comes from.
	 * @param allowed_perceptions
	 *            The allowed perceptions to the agent.
	 * @param allowed_actions
	 *            The allowed actions to the agent.
	 */
	public Agent(String label, Vertex vertex,
			PerceptionPermission[] allowed_perceptions,
			ActionPermission[] allowed_actions) {
		this.label = label;
		this.vertex = vertex;
		this.state = AgentStates.JUST_ACTED; // agent ready to perceive!
		this.edge = null;
		this.elapsed_length = 0;

		if (allowed_perceptions != null && allowed_perceptions.length > 0) {
			this.allowed_perceptions = new HashSet<PerceptionPermission>();
			for (int i = 0; i < allowed_perceptions.length; i++)
				this.allowed_perceptions.add(allowed_perceptions[i]);
		} else
			this.allowed_perceptions = null;

		if (allowed_actions != null && allowed_actions.length > 0) {
			this.allowed_actions = new HashSet<ActionPermission>();
			for (int i = 0; i < allowed_actions.length; i++)
				this.allowed_actions.add(allowed_actions[i]);
		} else
			this.allowed_actions = null;
	}

	/**
	 * Returns the label of the agent.
	 * 
	 * @return The label of the agent.
	 */
	public String getLabel() {
		return this.label;
	}

	/**
	 * Returns the state of the agent.
	 * 
	 * @return The state of the agent.
	 * @see AgentStates
	 */
	public int getAgentState() {
		return this.state;
	}

	/**
	 * Configures the state of the agent.
	 * 
	 * @param state
	 *            The state of the agent.
	 * @see AgentStates
	 */
	public void setState(int state) {
		this.state = state;
	}

	/**
	 * Configures the state of the agent. Used by the AgentTranslator class.
	 * 
	 * @param state
	 *            The state of the agent.
	 * @see AgentStates
	 * @see AgentTranslator
	 */
	public void setAgentState(int state) {
		this.state = state;
	}

	/**
	 * Returns the vertex that the agent comes from.
	 * 
	 * @return The vertex that agent comes from.
	 */
	public Vertex getVertex() {
		return this.vertex;
	}

	/**
	 * Configures the vertex from where the agent comes from.
	 * 
	 * @param vertex
	 *            The vertex from where the agent comes from.
	 */
	public void setVertex(Vertex vertex) {
		this.vertex = vertex;
	}

	/**
	 * Configures the edge of the agent, as well as its position on it.
	 * 
	 * @param edge
	 *            The edge of the agent.
	 * @param elapsed_length
	 *            Where the agent is on the edge. If such value is bigger than
	 *            the length of the edge, then the elapsed length will be set as
	 *            the length of the edge.
	 */
	public void setEdge(Edge edge, double elapsed_length) {
		this.edge = edge;
		this.elapsed_length = elapsed_length;

		if (this.edge != null && this.edge.getLength() < this.elapsed_length)
			this.elapsed_length = this.edge.getLength();
	}

	/**
	 * Returns the edge where the agent is.
	 * 
	 * @return The edge where the agent is.
	 */
	public Edge getEdge() {
		return this.edge;
	}

	/**
	 * Returns where the agent is on the edge, i.e. how much of the edge remains
	 * for the agent to pass through it.
	 * 
	 * @return The elapsed length of the edge where the agent is.
	 */
	public double getElapsed_length() {
		return this.elapsed_length;
	}

	/**
	 * Configures the stamina of the agent.
	 * 
	 * Its value cannot be bigger than the max possible stamina value. If so,
	 * the set value will be the max possible value.
	 * 
	 * @param stamina
	 *            The stamina of the agent.
	 */
	public void setStamina(double stamina) {
		if (stamina <= this.max_stamina)
			this.stamina = stamina;
		else
			this.stamina = this.max_stamina;
	}

	/**
	 * Returns the stamina of the agent.
	 * 
	 * @return The stamina of the agent.
	 */
	public double getStamina() {
		return this.stamina;
	}

	/**
	 * Decrements the stamina of the agent by the given factor.
	 * 
	 * The stamina value cannot be smaller than zero.
	 * 
	 * @param factor
	 *            The factor to be decremented from the value of stamina.
	 */
	public void decStamina(double factor) {
		this.stamina = this.stamina - factor;

		if (this.stamina < 0) {
			this.stamina = 0;
		}
	}

	/**
	 * Increments the stamina of the agent by the given factor.
	 * 
	 * The stamina value cannot be bigger than the max possible stamina value.
	 * 
	 * @param factor
	 *            The factor to be added to the value of stamina.
	 */
	public void incStamina(double factor) {
		this.stamina = this.stamina + factor;

		if (this.stamina > this.max_stamina)
			this.stamina = this.max_stamina;
	}

	/**
	 * Configures the maximum possible value for the stamina of the agent.
	 * 
	 * If the given value is smaller than the agent's current stamina, such
	 * attribute will be changed to the given value.
	 * 
	 * @param max_stamina
	 *            The maximum possible value for the stamina of the agent.
	 */
	public void setMax_stamina(double max_stamina) {
		this.max_stamina = max_stamina;

		if (this.stamina > this.max_stamina)
			this.stamina = this.max_stamina;
	}

	/**
	 * Returns the maximum possible value for the stamina of the agent.
	 * 
	 * @return The maximum possible value for the stamina of the agent.
	 */
	public double getMax_stamina() {
		return this.max_stamina;
	}

	/**
	 * Returns the allowed perceptions for the agent.
	 * 
	 * @return The permissions of perceptions.
	 */
	public PerceptionPermission[] getAllowedPerceptions() {
		if (this.allowed_perceptions != null)
			return this.allowed_perceptions
					.toArray(new PerceptionPermission[0]);

		return new PerceptionPermission[0];
	}

	/**
	 * Returns the allowed actions for the agent.
	 * 
	 * @return The permissions of actions.
	 */
	public ActionPermission[] getAllowedActions() {
		if (this.allowed_actions != null)
			return this.allowed_actions.toArray(new ActionPermission[0]);

		return new ActionPermission[0];
	}

	public String fullToXML(int identation) {
		// holds the answer being constructed
		StringBuffer buffer = new StringBuffer();

		// applies the identation
		for (int i = 0; i < identation; i++)
			buffer.append("\t");

		// fills the buffer
		buffer.append("<agent id=\"" + this.id + "\" label=\"" + this.label
				+ "\" state=\"" + this.state + "\" vertex_id=\""
				+ this.vertex.getObjectId());

		if (this.edge != null) {
			buffer.append("\" edge_id=\"" + this.edge.getObjectId()
					+ "\" elapsed_length=\"" + this.elapsed_length);
		}

		buffer.append("\" stamina=\"" + this.stamina);
		buffer.append("\" max_stamina=\"" + this.max_stamina);

		// puts the eventual allowed perceptions
		if (this.allowed_perceptions != null) {
			buffer.append("\">\n");

			for (PerceptionPermission permission : this.allowed_perceptions)
				buffer.append(permission.fullToXML(identation + 1));
		}

		// puts the eventual allowed actions
		if (this.allowed_actions != null) {
			if (allowed_perceptions == null)
				buffer.append("\">\n");

			for (ActionPermission permission : this.allowed_actions)
				buffer.append(permission.fullToXML(identation + 1));
		}

		// closes the main tag
		if (this.allowed_perceptions == null && this.allowed_actions == null)
			buffer.append("\"/>\n");
		else {
			for (int i = 0; i < identation; i++)
				buffer.append("\t");
			buffer.append("</agent>\n");
		}

		// returns the buffer content
		return buffer.toString();
	}

	public String reducedToXML(int identation) {
		// holds the answer being constructed
		StringBuffer buffer = new StringBuffer();

		// applies the identation
		for (int i = 0; i < identation; i++)
			buffer.append("\t");

		// fills the buffer
		buffer.append("<agent id=\"" + this.id + "\" label=\"" + this.label
				+ "\" vertex_id=\"" + this.vertex.getObjectId());

		if (this.edge != null) {
			buffer.append("\" edge_id=\"" + this.edge.getObjectId()
					+ "\" elapsed_length=\"" + this.elapsed_length);
		}

		buffer.append("\"/>\n");

		// returns the buffer content
		return buffer.toString();
	}

	public boolean equals(Object object) {
		if (this.id != null && object instanceof XMLable)
			return this.id.equals(((XMLable) object).getObjectId());
		else
			return super.equals(object);
	}

	public String getObjectId() {
		return this.id;
	}

	public void setObjectId(String object_id) {
		this.id = object_id;
	}
}