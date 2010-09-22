/* TeleportAction.java */

/* The package of this class. */
package util.action;

/* Imported classes and/or interfaces. */

import util.agent.Agent;
import util.agent.AgentStates;
import util.graph.Edge;
import util.graph.Node;

/**
 * Implements the action of teleporting an agent.
 * 
 * Its effect can be controlled by depth and stamina limitations.
 * 
 * @see DepthLimitation
 * @see StaminaLimitation
 */
public final class TeleportAction extends AtomicAction {
	/* Attributes. */
	/** The new vertex the agent is coming from, after the teleport. */
	private Node vertex;

	/** The edge the agent is, after the teleport. */
	private Edge edge;

	/**
	 * Registers where the agent is on the edge, i.e. how much of the edge
	 * remains for the agent to pass through it, after the teleport.
	 */
	private double elapsed_length;

	/* Methods. */
	/**
	 * Constructor.
	 * 
	 * @param vertex
	 *            The vertex th agent is coming from, after the teleport.
	 * @param edge
	 *            The edge the agent is, after the teleport.
	 * @param elapsed_length
	 *            How much of the edge remains for the agent to trespass it.
	 */
	public TeleportAction(Node vertex, Edge edge, double elapsed_length) {
		this.vertex = vertex;
		this.edge = edge;
		this.elapsed_length = elapsed_length;
	}


	/**
	 * Returns the goal vertex of the teleport.
	 * 
	 * @return The goal vertex of the teleport.
	 */
	public Node getVertex() {
		return this.vertex;
	}

	/**
	 * Returns the goal edge of the teleport.
	 * 
	 * @return The goal edge of the teleport.
	 */
	public Edge getEdge() {
		return this.edge;
	}

	/**
	 * Returns where the agent must be at the edge, after the teleport.
	 * 
	 * @return The elapsed lenght of the goal edge.
	 */
	public double getElapsed_length() {
		return this.elapsed_length;
	}


	@Override
	public boolean perform_action(Agent agent) {
		agent.setState(AgentStates.MOVING);
		agent.setNode(vertex);
		agent.setEdge(edge, elapsed_length);
		return false;
		
	}


}