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
	/* Attributes. */
	/** The object id of the vertex. */
	private String id;

	/** The label of the vertex. */
	private String label;

	/** The set of edges whose emitter is this vertex. */
	private Set<Edge> in_edges;

	/** The set of edges whose collector is this vertex. */
	private Set<Edge> out_edges;

	/** The idleness of the vertex. */
	private double idleness;

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
	 * Removes a given edge from the vertex.
	 * 
	 * @param edge
	 *            The edge to be removed from the vertex.
	 */
	public void removeEdge(Edge edge) {
		if (this.in_edges != null)
			this.in_edges.remove(edge);

		if (this.out_edges != null)
			this.out_edges.remove(edge);
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
	 * Returns the degree of the vertex.
	 * 
	 * @return The degree of the vertex.
	 */
	public int getDegree() {
		int answer = 0;

		if (this.in_edges != null)
			answer = this.in_edges.size();

		if (this.out_edges != null) {
			if (this.in_edges == null)
				answer = this.out_edges.size();
			else
				for (Edge edge : this.out_edges)
					if (!this.in_edges.contains(edge))
						answer++;
		}

		return answer;
	}

	/**
	 * Returns the set of edges of the vertex.
	 * 
	 * @return The edges associated with the vertex.
	 */
	public Edge[] getEdges() {
		Set<Edge> edges = new HashSet<Edge>();

		if (this.in_edges != null)
			for (Edge edge : this.in_edges)
				edges.add(edge);

		if (this.out_edges != null)
			for (Edge edge : this.out_edges)
				edges.add(edge);

		Edge[] answer = new Edge[edges.size()];
		int i = 0;
		for (Edge edge : edges) {
			answer[i] = edge;
			i++;
		}

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
	public void setIdleness(double idleness) {
		this.idleness = idleness;
	}

	/**
	 * Returns the idleness of the vertex.
	 * 
	 * @return The idleness of the vertex.
	 */
	public double getIdleness() {
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
	 * Returns all the vertexes in the neighborhood.
	 * 
	 * @return The set of vertexes in the neighborhood.
	 */
	public Vertex[] getNeighbourhood() {
		// holds the set of neighbor vertexes
		Set<Vertex> neighbourhood = new HashSet<Vertex>();

		// for each edge whose emitter is this vertex
		if (this.out_edges != null)
			for (Edge edge : this.out_edges) {
				Vertex other_vertex = edge.getOtherVertex(this);
				neighbourhood.add(other_vertex);
			}

		// for each edge whose emitter is this vertex
		if (this.in_edges != null)
			for (Edge edge : this.in_edges) {
				Vertex other_vertex = edge.getOtherVertex(this);
				neighbourhood.add(other_vertex);
			}

		// mounts and returns the answer
		Vertex[] answer = new Vertex[neighbourhood.size()];
		int i = 0;
		for (Vertex vertex : neighbourhood) {
			answer[i] = vertex;
			i++;
		}

		return answer;
	}

	/**
	 * Returns the vertexes in the neighborhood of which emitter is this one.
	 * 
	 * @return The set of vertexes in the neighborhood of which emitter is this
	 *         one.
	 */
	public Vertex[] getCollectorNeighbourhood() {
		// holds the set of neighbor vertexes
		Set<Vertex> neighbourhood = new HashSet<Vertex>();

		// for each edge whose emitter is this vertex
		if (this.out_edges != null)
			for (Edge edge : this.out_edges) {
				Vertex other_vertex = edge.getOtherVertex(this);
				neighbourhood.add(other_vertex);
			}

		// mounts and returns the answer
		Vertex[] answer = new Vertex[neighbourhood.size()];
		int i = 0;
		for (Vertex vertex : neighbourhood) {
			answer[i] = vertex;
			i++;
		}

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
		if (this.out_edges != null)
			for (Edge edge : this.out_edges)
				if (vertex.isCollectorOf(edge))
					shared_edges.add(edge);

		// for each edge whose collector is this vertex
		if (this.in_edges != null)
			for (Edge edge : this.in_edges)
				if (vertex.isEmitterOf(edge))
					shared_edges.add(edge);

		// mounts and returns the answer
		Edge[] answer = new Edge[shared_edges.size()];
		int i = 0;
		for (Edge edge : shared_edges) {
			answer[i] = edge;
			i++;
		}

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
		if (this.out_edges != null)
			for (Edge edge : this.out_edges)
				if (vertex.isCollectorOf(edge))
					shared_edges.add(edge);

		// mounts and returns the answer
		Edge[] answer = new Edge[shared_edges.size()];
		int i = 0;
		for (Edge edge : shared_edges) {
			answer[i] = edge;
			i++;
		}

		return answer;
	}

	public String getLabel() {
		return this.label;
	}

	public boolean equals(Object object) {
		if (this.id != null && object instanceof Vertex)
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