/* Edge.java */

/* The package of this class. */
package util.graph;

/**
 * Implements the edges of a Graph object.
 * 
 * @see Graph
 */
public final class Edge {
	/* Attributes. */
	/** The object id of the edge. */
	private String id;

	/** The emitter of this edge, if it is an arc. */
	private Node emitter;

	/** The collector of this edge, if it is an arc. */
	private Node collector;

	/** The lenght of the edge. */
	private double length;

	/* Methods. */
	/**
	 * Contructor for non-oriented edges (non-arcs).
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
	 * Contructor for eventually oriented edges (arcs).
	 * 
	 * @param emitter
	 *            The emitter node, if the edge is an arc.
	 * @param collector
	 *            The collector node, if the edge is an arc.
	 * @param oriented
	 *            TRUE if the edge is an arc.
	 * @param length
	 *            The length of the edge.
	 */
	public Edge(Node emitter, Node collector, boolean oriented,
			double length) {
		this.emitter = emitter;
		this.collector = collector;

		// if the edge is an arc...
		if (oriented) {
			// adds the edge as a way out arc in the emitter
			this.emitter.addOutEdge(this);

			// adds the edge as a way in arc in the collector
			this.collector.addInEdge(this);
		} else {
			// adds the edge in both emitter and collector nodees
			this.emitter.addEdge(this);
			this.collector.addEdge(this);
		}

		this.length = length;
	}

	/**
	 * Contructor for eventually oriented edges (arcs). The id of the edge is
	 * needed.
	 * 
	 * @param emitter
	 *            The emitter node, if the edge is an arc.
	 * @param collector
	 *            The collector node, if the edge is an arc.
	 * @param oriented
	 *            TRUE if the edge is an arc.
	 * @param length
	 *            The length of the edge.
	 * @param id
	 *            The object id of the edge.
	 */
	private Edge(Node emitter, Node collector, boolean oriented,
			double length, String id) {
		this.id = id;
		this.emitter = emitter;
		this.collector = collector;

		// if the edge is an arc...
		if (oriented) {
			// adds the edge as a way out arc in the emitter
			this.emitter.addOutEdge(this);

			// adds the edge as a way in arc in the collector
			this.collector.addInEdge(this);
		} else {
			// adds the edge in both emitter and collector nodes
			this.emitter.addEdge(this);
			this.collector.addEdge(this);
		}

		this.length = length;
	}

	/**
	 * Returns the length of the edge.
	 * 
	 * @return The length of the edge.
	 */
	public double getLength() {
		return this.length;
	}
	
	public Node getEmitter(){
		return this.emitter;
	}

	/**
	 * Returns the node of the edge that is not the one passed as a parameter.
	 * 
	 * @param node
	 *            The node whose pair is wanted.
	 * @return The other node of the edge.
	 */
	public Node getOtherNode(Node node) {
		if (this.collector.equals(node))
			return this.emitter;
		else
			return this.collector;
	}

	/**
	 * Obtains a copy of the edge with the given copies of nodes.
	 * 
	 * @param copy_emitter
	 *            The copy of the emitter.
	 * @param copy_collector
	 *            The copy of the collector.
	 * @return The copy of the edge.
	 */
	public Edge getCopy(Node copy_emitter, Node copy_collector) {
		// registers if the original edge is oriented
		boolean oriented = !this.emitter.isCollectorOf(this);

		// the copy
		Edge copy_edge = new Edge(copy_emitter, copy_collector, oriented,
				this.length, this.id);

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
}