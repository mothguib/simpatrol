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

	/** The emitter of this edge, if it is an arc. */
	private Vertex emitter;

	/** The collector of this edge, if it is an arc. */
	private Vertex collector;

	/** The length of the edge. */
	private double length;

	/* Methods. */
	/**
	 * Constructor for non-oriented edges (non-arcs).
	 * 
	 * @param vertex_1
	 *            One of the vertexes of the edge.
	 * @param vertex_2
	 *            Another vertex of the edge.
	 * @param length
	 *            The length of the edge.
	 */
	public Edge(Vertex vertex_1, Vertex vertex_2, double length) {
		this(vertex_1, vertex_2, false, length);
	}

	/**
	 * Constructor for eventually oriented edges (arcs).
	 * 
	 * @param emitter
	 *            The emitter vertex, if the edge is an arc.
	 * @param collector
	 *            The collector vertex, if the edge is an arc.
	 * @param oriented
	 *            TRUE if the edge is an arc.
	 * @param length
	 *            The length of the edge.
	 */
	public Edge(Vertex emitter, Vertex collector, boolean oriented,
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
			// adds the edge in both emitter and collector vertexes
			this.emitter.addEdge(this);
			this.collector.addEdge(this);
		}

		this.length = length;
	}

	/** Disconnects the edge, updating the emitter and collector references. */
	public void disconnect() {
		this.emitter.removeEdge(this);
		this.collector.removeEdge(this);
	}

	/**
	 * Returns the vertexes of this edge (1st the emitter, 2nd the collector, if
	 * the edge is oriented).
	 * 
	 * @return The vertexes connected by this edge.
	 */
	public Vertex[] getVertexes() {
		Vertex[] answer = { this.emitter, this.collector };
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
	 * Returns the vertex of the edge that is not the one passed as a parameter.
	 * 
	 * @param vertex
	 *            The vertex whose pair is wanted.
	 * @return The other vertex of the edge.
	 */
	public Vertex getOtherVertex(Vertex vertex) {
		if (this.collector.equals(vertex))
			return this.emitter;
		else
			return this.collector;
	}

	/**
	 * Obtains a copy of the edge with the given copies of vertexes.
	 * 
	 * @param copy_emitter
	 *            The copy of the emitter.
	 * @param copy_collector
	 *            The copy of the collector.
	 * @return The copy of the edge.
	 */
	public Edge getCopy(Vertex copy_emitter, Vertex copy_collector) {
		// registers if the original edge is oriented
		boolean oriented = !this.emitter.isCollectorOf(this);

		// the copy
		Edge copy_edge = new Edge(copy_emitter, copy_collector, oriented,
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