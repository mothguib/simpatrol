/* TeleportAction.java (2.0) */
package br.org.simpatrol.server.model.action;

/* Imported classes and/or interfaces. */
import java.util.Set;

import br.org.simpatrol.server.model.graph.Edge;
import br.org.simpatrol.server.model.graph.Vertex;
import br.org.simpatrol.server.model.interfaces.Visible;

/**
 * Implements the actions of teleport an agent.
 * 
 * @author Daniel Henriques Moreira (dhenriques@gmail.com)
 */
public class TeleportAction extends AtomicAction {
	/* Attributes. */
	/** The new vertex that the agent must be coming from, after the teleport. */
	private Vertex vertex;

	/** The edge where the agent shall be, after the teleport. */
	private Edge edge;

	/**
	 * Registers where the agent shall be on the edge, i.e. how much of the edge
	 * must remain for the agent to pass through it, after the teleport.
	 */
	private double elapsedLength;

	/**
	 * The set of objects that shall become visible as a result of the teleport.
	 */
	private Set<Visible> visibleObjects;

	/* Methods. */
	/**
	 * Constructor.
	 * 
	 * @param vertex
	 *            The vertex that the agent must be coming from, after the
	 *            teleport.
	 * @param edge
	 *            The edge where the agent shall be, after the teleport.
	 * @param elapsedLength
	 *            How much of the edge shall remain for the agent to trespass
	 *            it.
	 */
	public TeleportAction(Vertex vertex, Edge edge, double elapsedLength) {
		this(vertex, edge, elapsedLength, null);
	}

	/**
	 * Constructor.
	 * 
	 * @param vertex
	 *            The vertex that the agent must be coming from, after the
	 *            teleport.
	 * @param edge
	 *            The edge where the agent shall be, after the teleport.
	 * @param elapsedLength
	 *            How much of the edge shall remain for the agent to trespass
	 *            it.
	 * @param visibleObjects
	 *            The objects that shall become visible as a result of the
	 *            teleport.
	 */
	protected TeleportAction(Vertex vertex, Edge edge, double elapsedLength,
			Set<Visible> visibleObjects) {
		super();
		this.vertex = vertex;
		this.edge = edge;
		this.elapsedLength = elapsedLength;
		this.visibleObjects = visibleObjects;
	}

	/**
	 * Returns the goal vertex of the teleport.
	 * 
	 * @return The goal vertex of the teleport.
	 */
	public Vertex getVertex() {
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
	 * @return How much of the edge shall remain for the agent to trespass it,
	 *         after the teleport
	 */
	public double getElapsedLength() {
		return this.elapsedLength;
	}

	/**
	 * Assures the visibility of the objects that must become visible as a
	 * result of the teleport.
	 */
	public void assureTeleportVisibilityEffect() {
		// assures the visibility of the destiny of the teleport
		this.vertex.setVisible(true);
		if (this.edge != null)
			this.edge.setVisible(true);

		// assures the visibility of the other registered objects
		if (this.visibleObjects != null)
			for (Visible visible : this.visibleObjects)
				visible.setVisible(true);
	}

	protected void initActionType() {
		this.actionType = ActionTypes.TELEPORT;
	}

	public String fullToXML() {
		// holds the answer for the method
		StringBuffer buffer = new StringBuffer();

		// fills the buffer
		buffer.append("<action type=\"" + this.actionType.getType()
				+ "\" vertex_id=\"" + this.vertex.getId());

		if (this.edge != null)
			buffer.append("\" edge_id=\"" + this.edge.getId()
					+ "\" elapsed_length=\"" + this.elapsedLength);

		buffer.append("\"/>");

		// returns the answer
		return buffer.toString();
	}
}