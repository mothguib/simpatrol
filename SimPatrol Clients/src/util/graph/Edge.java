/* Edge.java */

/* The package of this class. */
package util.graph;

import util.heap.Comparable;

/**
 * Implements the edges of a Graph object.
 * 
 * @see Graph
 */
public final class Edge implements Comparable {
	/* Attributes. */
	/** The object id of the edge. */
	private String id;

	/** The source of this edge, if it is an arc. */
	private Node source;

	/** The target of this edge, if it is an arc. */
	private Node target;

	/** The length of the edge. */
	private double length;

	/* Methods. */
	/**
	 * Constructor for non-directed edges (non-arcs).
	 * 
	 * @param node_1
	 *            One of the nodes of the edge.
	 * @param node_2
	 *            Another node of the edge.
	 * @param length
	 *            The length of the edge.
	 */
	public Edge(Node node_1, Node node_2, double length) {
		this(node_1, node_2, false, length);
	}

	/**
	 * Constructor for eventually directed edges (arcs).
	 * 
	 * @param source
	 *            The source node, if the edge is an arc.
	 * @param target
	 *            The target node, if the edge is an arc.
	 * @param directed
	 *            TRUE if the edge is an arc.
	 * @param length
	 *            The length of the edge.
	 */
	public Edge(Node source, Node target, boolean directed,
			double length) {
		this.source = source;
		this.target = target;

		// if the edge is an arc...
		if (directed) {
			// adds the edge as a way out arc in the source
			this.source.addOutEdge(this);

			// adds the edge as a way in arc in the target
			this.target.addInEdge(this);
		} else {
			// adds the edge in both source and target nodes
			this.source.addEdge(this);
			this.target.addEdge(this);
		}

		this.length = length;
	}

	/** Disconnects the edge, updating the source and target references. */
	public void disconnect() {
		this.source.removeEdge(this);
		this.target.removeEdge(this);
	}

	/**
	 * Returns the nodes of this edge (1st the source, 2nd the target, if
	 * the edge is directed).
	 * 
	 * @return The nodes connected by this edge.
	 */
	public Node[] getNodees() {
		Node[] answer = { this.source, this.target };
		return answer;
	}

	/**
	 * Returns the length of the edge.
	 * 
	 * @return The length of the edge.
	 */
	public double getLength() {
		return this.length;
	}

	/**
	 * Returns the node of the edge that is not the one passed as a parameter.
	 * 
	 * @param node
	 *            The node whose pair is wanted.
	 * @return The other node of the edge.
	 */
	public Node getOtherNode(Node node) {
		if (this.target.equals(node))
			return this.source;
		else
			return this.target;
	}

	/**
	 * Obtains a copy of the edge with the given copies of nodes.
	 * 
	 * @param copy_source
	 *            The copy of the source.
	 * @param copy_target
	 *            The copy of the target.
	 * @return The copy of the edge.
	 */
	public Edge getCopy(Node copy_source, Node copy_target) {
		// registers if the original edge is directed
		boolean directed = !this.source.isCollectorOf(this);

		// the copy
		Edge copy_edge = new Edge(copy_source, copy_target, directed,
				this.length);
		copy_edge.id = this.id;

		// returns the answer
		return copy_edge;
	}

	public boolean equals(Object object) {
		if (this.id != null && object instanceof Edge)
			return this.id.equals(((Edge) object).getObjectId());
		else
			return super.equals(object);
	}

	public String getObjectId() {
		return this.id;
	}

	public void setObjectId(String object_id) {
		this.id = object_id;
	}

	/**
	 * Compares a given edge to this one, based on their lengths.
	 * 
	 * @param object
	 *            The object to be compared to this one.
	 */
	public boolean isSmallerThan(Comparable object) {
		if (object instanceof Edge)
			if (this.length < ((Edge) object).length)
				return true;

		return false;
	}
}