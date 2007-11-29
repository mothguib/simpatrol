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
	/**
	 * The object id of the edge. Not part of the patrol problem modelling.
	 */
	private String id;

	/** The emitter of this edge, if it is an arc. */
	private Vertex emitter;

	/** The collector of this edge, if it is an arc. */
	private Vertex collector;

	/** The lenght of the edge. */
	private double length;

	/* Methods. */
	/**
	 * Contructor for non-oriented edges (non-arcs).
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
	 * Contructor for eventually oriented edges (arcs).
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

	/**
	 * Contructor for eventually oriented edges (arcs). The id of the edge is
	 * needed.
	 * 
	 * @param emitter
	 *            The emitter vertex, if the edge is an arc.
	 * @param collector
	 *            The collector vertex, if the edge is an arc.
	 * @param oriented
	 *            TRUE if the edge is an arc.
	 * @param length
	 *            The length of the edge.
	 * @param id
	 *            The object id of the edge.
	 */
	private Edge(Vertex emitter, Vertex collector, boolean oriented,
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
			// adds the edge in both emitter and collector vertexes
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