/* Vertex.java */

/* The package of this class. */
package util.graph;

/* Imported classes and/or interfaces. */
import java.util.HashSet;
import java.util.Set;

/**
 * Implements the vertexes of a Graph object.
 * 
 * @see Graph
 */
public final class Vertex {
	/* Atributes. */
	/**
	 * The object id of the vertex. Not part of the patrol problem modelling.
	 */
	private String id;

	/** The label of the vertex. */
	private String label;

	/** The set of edges whose emitter is this vertex. */
	private Set<Edge> in_edges;

	/** The set of edges whose collector is this vertex. */
	private Set<Edge> out_edges;

	/** The idleness of the vertex. */
	private int idleness;

	/**
	 * The priority to visit this vertex. Its default value is ZERO.
	 */
	private int priority = 0;

	/**
	 * Expresses if this vertex is a point of recharging the energy of the
	 * patrollers. Its default value is FALSE.
	 */
	private boolean fuel = false;

	/* Methods. */
	/**
	 * Constructor.
	 * 
	 * @param label
	 *            The label of the vertex.
	 */
	public Vertex(String label) {
		this.label = label;
		this.in_edges = null;
		this.out_edges = null;
		this.idleness = 0;
	}

	/**
	 * Adds the passed edge to the vertex.
	 * 
	 * @param edge
	 *            The edge added to the vertex. It cannot be an arc.
	 */
	public void addEdge(Edge edge) {
		// as the edge is not an arc, it must be added to both
		// in and out edge sets
		if (this.in_edges == null)
			this.in_edges = new HashSet<Edge>();
		if (this.out_edges == null)
			this.out_edges = new HashSet<Edge>();

		this.in_edges.add(edge);
		this.out_edges.add(edge);
	}

	/**
	 * Adds the passed edge as a way out arc to the vertex.
	 * 
	 * @param out_arc
	 *            The edge whose emitter is this vertex.
	 */
	public void addOutEdge(Edge out_arc) {
		if (this.out_edges == null)
			this.out_edges = new HashSet<Edge>();
		this.out_edges.add(out_arc);
	}

	/**
	 * Adds the passed edge as a way in arc to the vertex.
	 * 
	 * @param in_arc
	 *            The edge whose collector is this vertex.
	 */
	public void addInEdge(Edge in_arc) {
		if (this.in_edges == null)
			this.in_edges = new HashSet<Edge>();
		this.in_edges.add(in_arc);
	}

	/**
	 * Returns the set of edges of the vertex.
	 * 
	 * @return The edges associated with the vertex.
	 */
	public Edge[] getEdges() {
		HashSet<Edge> edges = new HashSet<Edge>();

		if (this.in_edges != null) {
			Object[] in_edges_array = this.in_edges.toArray();

			for (int i = 0; i < in_edges_array.length; i++)
				edges.add((Edge) in_edges_array[i]);
		}

		if (this.out_edges != null) {
			Object[] out_edges_array = this.out_edges.toArray();

			for (int i = 0; i < out_edges_array.length; i++)
				edges.add((Edge) out_edges_array[i]);
		}

		Object[] edges_array = edges.toArray();
		Edge[] answer = new Edge[edges_array.length];
		for (int i = 0; i < answer.length; i++)
			answer[i] = (Edge) edges_array[i];

		return answer;
	}

	/**
	 * Configures the priority of the vertex.
	 * 
	 * @param priority
	 *            The priority.
	 */
	public void setPriority(int priority) {
		this.priority = priority;
	}

	/**
	 * Verifies if the vertex is a fuel recharging point.
	 * 
	 * @return TRUE, if the vertex is a fuel recharging point, FALSE if not.
	 */
	public boolean isFuel() {
		return this.fuel;
	}

	/**
	 * Configures if the vertex is a fuel recharging point.
	 * 
	 * @param fuel
	 *            TRUE, if the vertex is a fuel recharging point, FALSE if not.
	 */
	public void setFuel(boolean fuel) {
		this.fuel = fuel;
	}

	/**
	 * Configures the idleness of the vertex.
	 * 
	 * @param idleness
	 *            The idleness of the vertex, measured in cycles, or in seconds.
	 */
	public void setIdleness(int idleness) {
		this.idleness = idleness;
	}

	/**
	 * Returns the idleness of the vertex.
	 * 
	 * @return The idleness of the vertex.
	 */
	public int getIdleness() {
		return this.idleness;
	}

	/**
	 * Verifies if the vertex is the collector of a given edge.
	 * 
	 * @param edge
	 *            The edge whose collector is supposed to be the vertex.
	 * @return TRUE if the vertex is the collector of the edge, FALSE if not.
	 */
	public boolean isCollectorOf(Edge edge) {
		return this.in_edges.contains(edge);
	}

	/**
	 * Verifies if the vertex is the emitter of a given edge.
	 * 
	 * @param edge
	 *            The edge whose emitter is supposed to be the vertex.
	 * @return TRUE if the vertex is the emitter of the edge, FALSE if not.
	 */
	public boolean isEmitterOf(Edge edge) {
		return this.out_edges.contains(edge);
	}

	/**
	 * Returns a copy of the vertex, with no edges.
	 * 
	 * @return The copy of the vertex, without the edges.
	 */
	public Vertex getCopy() {
		Vertex answer = new Vertex(this.label);
		answer.id = this.id;
		answer.priority = this.priority;
		answer.idleness = this.idleness;
		answer.fuel = this.fuel;

		return answer;
	}

	/**
	 * Returns all the vertexes in the neighbourhood.
	 * 
	 * @return The set of vertexes in the neighbourhood.
	 */
	public Vertex[] getNeighbourhood() {
		// holds the set of neighbour vertexes
		Set<Vertex> neighbourhood = new HashSet<Vertex>();

		// for each edge whose emitter is this vertex
		if (this.out_edges != null) {
			Object[] out_edges_array = this.out_edges.toArray();
			for (int i = 0; i < out_edges_array.length; i++) {
				// obtains the other vertex
				Vertex other_vertex = ((Edge) out_edges_array[i])
						.getOtherVertex(this);

				// adds it to set of neighbours
				neighbourhood.add(other_vertex);
			}
		}

		// for each edge whose collector is this vertex
		if (this.in_edges != null) {
			Object[] in_edges_array = this.in_edges.toArray();
			for (int i = 0; i < in_edges_array.length; i++) {
				// obtains the other vertex
				Vertex other_vertex = ((Edge) in_edges_array[i])
						.getOtherVertex(this);

				// adds it to set of neighbours
				neighbourhood.add(other_vertex);
			}
		}

		// mounts and returns the answer
		Object[] neighbourhood_array = neighbourhood.toArray();
		Vertex[] answer = new Vertex[neighbourhood_array.length];
		for (int i = 0; i < neighbourhood_array.length; i++)
			answer[i] = (Vertex) neighbourhood_array[i];
		return answer;
	}

	/**
	 * Returns the vertexes in the neighbourhood of which emitter is this one.
	 * 
	 * @return The set of vertexes in the neighbourhood of which emitter is this
	 *         one.
	 */
	public Vertex[] getCollectorNeighbourhood() {
		// holds the set of neighbour vertexes
		Set<Vertex> neighbourhood = new HashSet<Vertex>();

		// for each edge whose emitter is this vertex
		if (this.out_edges != null) {
			Object[] out_edges_array = this.out_edges.toArray();
			for (int i = 0; i < out_edges_array.length; i++) {
				// obtains the other vertex
				Vertex other_vertex = ((Edge) out_edges_array[i])
						.getOtherVertex(this);

				// adds it to set of neighbours
				neighbourhood.add(other_vertex);
			}
		}

		// mounts and returns the answer
		Object[] neighbourhood_array = neighbourhood.toArray();
		Vertex[] answer = new Vertex[neighbourhood_array.length];
		for (int i = 0; i < neighbourhood_array.length; i++)
			answer[i] = (Vertex) neighbourhood_array[i];
		return answer;
	}

	/**
	 * Returns all the edges between this vertex and the given one.
	 * 
	 * @param vertex
	 *            The adjacent vertex of which edges shared with this vertex are
	 *            to be returned.
	 * @return The edges in common between this vertex and the given one.
	 */
	public Edge[] getConnectingEdges(Vertex vertex) {
		// holds the answer to the method
		Set<Edge> shared_edges = new HashSet<Edge>();

		// for each edge whose emitter is this vertex
		if (this.out_edges != null) {
			Object[] out_edges_array = this.out_edges.toArray();
			for (int i = 0; i < out_edges_array.length; i++) {
				// obtains the current edge
				Edge current_edge = (Edge) out_edges_array[i];

				// if the given vertex is the collector of the current edge,
				// adds it to the answer
				if (vertex.isCollectorOf(current_edge))
					shared_edges.add(current_edge);
			}
		}

		// for each edge whose collector is this vertex
		if (this.in_edges != null) {
			Object[] in_edges_array = this.in_edges.toArray();
			for (int i = 0; i < in_edges_array.length; i++) {
				// obtains the current edge
				Edge current_edge = (Edge) in_edges_array[i];

				// if the given vertex is the emitter of the current edge,
				// adds it to the answer
				if (vertex.isEmitterOf(current_edge))
					shared_edges.add(current_edge);
			}
		}

		// mounts and returns the answer
		Object[] shared_edges_array = shared_edges.toArray();
		Edge[] answer = new Edge[shared_edges_array.length];
		for (int i = 0; i < shared_edges_array.length; i++)
			answer[i] = (Edge) shared_edges_array[i];
		return answer;
	}

	/**
	 * Returns all the edges between this vertex and the given one, of which
	 * emitter is this vertex.
	 * 
	 * @param vertex
	 *            The adjacent vertex of which edges shared with this vertex are
	 *            to be returned.
	 * @return The edges in common between this vertex and the given one, of
	 *         which emitter is this vertex.
	 */
	public Edge[] getConnectingOutEdges(Vertex vertex) {
		// holds the answer to the method
		Set<Edge> shared_edges = new HashSet<Edge>();

		// for each edge whose emitter is this vertex
		if (this.out_edges != null) {
			Object[] out_edges_array = this.out_edges.toArray();
			for (int i = 0; i < out_edges_array.length; i++) {
				// obtains the current edge
				Edge current_edge = (Edge) out_edges_array[i];

				// if the given vertex is the collector of the current edge,
				// adds it to the answer
				if (vertex.isCollectorOf(current_edge))
					shared_edges.add(current_edge);
			}
		}

		// mounts and returns the answer
		Object[] shared_edges_array = shared_edges.toArray();
		Edge[] answer = new Edge[shared_edges_array.length];
		for (int i = 0; i < shared_edges_array.length; i++)
			answer[i] = (Edge) shared_edges_array[i];
		return answer;
	}

	public boolean equals(Object object) {
		if (object instanceof Vertex)
			return this.id.equals(((Vertex) object).getObjectId());
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