/* Agent.java */

/* The package of this class. */
package util.agent;

/* Imported classes and/or interfaces. */
import util.graph.Edge;
import util.graph.Node;

/** Implements the internal agents of SimPatrol. */
public class Agent {
	/* Attributes. */
	/**
	 * The object id of the agent. Not part of the patrolling problem modelling.
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
	protected Node node;

	/** The edge where the agent is. */
	private Edge edge;

	/**
	 * Registers where the agent is on the edge, i.e. how much of the edge
	 * remains for the agent to pass through it.
	 */
	private double elapsed_length;


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
	public Agent(String label, Node node) {
		this.label = label;
		this.node = node;

		this.edge = null;
		this.elapsed_length = 0;

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
	 * Returns the node that the agent comes from.
	 * 
	 * @return The node that agent comes from.
	 */
	public Node getNode() {
		return this.node;
	}

	/**
	 * Configures the vertex from where the agent comes from.
	 * 
	 * @param node
	 *            The vertex from where the agent comes from.
	 */
	public void setNode(Node node) {
		this.node = node;
	}

	/**
	 * Configures the edge of the agent, as well as its position on it.
	 * 
	 * @param edge
	 *            The edge of the agent.
	 * @param elapsed_length
	 *            Where the agent is on the edge.
	 */
	public void setEdge(Edge edge, double elapsed_length) {
		this.edge = edge;
		this.elapsed_length = elapsed_length;
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
	
	public void setElapsed_length(double length) {
		this.elapsed_length = length;
	}


	public String getObjectId() {
		return this.id;
	}

	public void setObjectId(String object_id) {
		this.id = object_id;
	}
}