/* Vertex.java */

/* The package of this class. */
package model.graph;

/* Imported classes and/or interfaces. */
import java.util.HashSet;
import java.util.Set;
import model.interfaces.Visible;
import util.time.TimeSensible;
import view.XMLable;
import control.simulator.RealTimeSimulator;

/**
 * Implements the vertexes of a Graph object.
 * 
 * @see Graph
 */
public class Vertex implements XMLable, Visible {
	/* Attributes. */
	/**
	 * The object id of the vertex. Not part of the patrolling problem modeling.
	 */
	protected String id;

	/** The label of the vertex. */
	protected String label;

	/** The set of edges whose emitter is this vertex. */
	protected Set<Edge> in_edges;

	/** The set of edges whose collector is this vertex. */
	protected Set<Edge> out_edges;

	/**
	 * The priority to visit this vertex. Its default value is ZERO.
	 */
	protected int priority = 0;

	/**
	 * Expresses if this vertex is visible in the graph. Its default value is
	 * TRUE.
	 */
	protected boolean visibility = true;

	/**
	 * Expresses if this vertex is a point of recharging energy to the
	 * patrollers. Its default value is FALSE.
	 */
	protected boolean fuel = false;

	/**
	 * Registers the last time when this vertex was visited by an agent.
	 * Measured in cycles, if the simulator is a cycled one, or in seconds, if
	 * it's a real time one.
	 * 
	 * @see CycledSimulator
	 * @see RealTimeSimulator
	 */
	protected double last_visit_time;

	/**
	 * Counts the time. Shared by all the vertexes.
	 */
	protected static TimeSensible time_counter;

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
		this.last_visit_time = 0;
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
	 * Configures the visibility of the vertex.
	 * 
	 * @param visibility
	 *            The visibility.
	 */
	public void setVisibility(boolean visibility) {
		this.visibility = visibility;
	}

	/**
	 * Verifies the visibility of the vertex.
	 * 
	 * @return TRUE if the vertex is visible, FALSE if not.
	 */
	public boolean isVisible() {
		return this.visibility;
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
	 * Configures the last time this vertex was visited.
	 * 
	 * @param time
	 *            The time of the last visit, measured in cycles or in seconds.
	 */
	public void setLast_visit_time(double time) {
		this.last_visit_time = time;
	}

	/**
	 * Configures the time counter of the vertexes.
	 * 
	 * @param counter
	 *            The time counter.
	 */
	public static void setTime_counter(TimeSensible counter) {
		Vertex.time_counter = counter;
	}

	/**
	 * Configures the idleness of the vertex.
	 * 
	 * Pay attention not to use "setIdleness(0)" in order to register a visit
	 * onto this vertex. Use "setLast_visit_time(int time)" instead.
	 * 
	 * @param idleness
	 *            The idleness of the vertex, measured in cycles, or in seconds.
	 */
	public void setIdleness(double idleness) {
		this.last_visit_time = this.last_visit_time - idleness;
	}

	/**
	 * Calculates the idleness of the vertex at the current moment.
	 * 
	 * @return The idleness of the vertex.
	 */
	public double getIdleness() {
		if (Vertex.time_counter != null)
			return Vertex.time_counter.getElapsedTime() - this.last_visit_time;
		else
			return Math.abs(this.last_visit_time);
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
		answer.visibility = this.visibility;
		answer.fuel = this.fuel;
		answer.last_visit_time = this.last_visit_time;

		return answer;
	}

	/**
	 * Returns all the vertexes in the neighbourhood.
	 * 
	 * @return The set of vertexes in the neighbourhood.
	 */
	public Vertex[] getNeighbourhood() {
		// holds the set of neighbor vertexes
		Set<Vertex> neighbourhood = new HashSet<Vertex>();

		// for each edge whose emitter is this vertex
		if (this.out_edges != null)
			for (Edge edge : this.out_edges) {
				// obtains the other vertex
				Vertex other_vertex = edge.getOtherVertex(this);

				// adds it to set of neighbors
				neighbourhood.add(other_vertex);
			}

		// for each edge whose collector is this vertex
		if (this.in_edges != null)
			for (Edge edge : this.in_edges) {
				// obtains the other vertex
				Vertex other_vertex = edge.getOtherVertex(this);

				// adds it to set of neighbors
				neighbourhood.add(other_vertex);
			}

		// returns the answer
		return neighbourhood.toArray(new Vertex[0]);
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
				// obtains the other vertex
				Vertex other_vertex = edge.getOtherVertex(this);

				// adds it to set of neighbors
				neighbourhood.add(other_vertex);
			}

		// returns the answer
		return neighbourhood.toArray(new Vertex[0]);
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
				// if the given vertex is the collector of the current edge,
				// adds it to the answer
				if (vertex.isCollectorOf(edge))
					shared_edges.add(edge);

		// for each edge whose collector is this vertex
		if (this.in_edges != null)
			for (Edge edge : this.in_edges)
				// if the given vertex is the collector of the current edge,
				// adds it to the answer
				if (vertex.isCollectorOf(edge))
					shared_edges.add(edge);

		// returns the answer
		return shared_edges.toArray(new Edge[0]);
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
				// if the given vertex is the collector of the current edge,
				// adds it to the answer
				if (vertex.isCollectorOf(edge))
					shared_edges.add(edge);

		// returns the answer
		return shared_edges.toArray(new Edge[0]);
	}

	public String fullToXML(int identation) {
		// holds the answer being constructed
		StringBuffer buffer = new StringBuffer();

		// applies the identation
		for (int i = 0; i < identation; i++)
			buffer.append("\t");

		// fills the buffer
		buffer.append("<vertex id=\"" + this.id + "\" label=\"" + this.label
				+ "\" priority=\"" + this.priority + "\" visibility=\""
				+ this.visibility + "\" idleness=\"" + this.getIdleness()
				+ "\" fuel=\"" + this.fuel + "\" is_enabled=\"true" + "\"/>\n");

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
		buffer.append("<vertex id=\"" + this.id + "\" label=\"" + this.label
				+ "\" priority=\"" + this.priority + "\" idleness=\""
				+ this.getIdleness() + "\" fuel=\"" + this.fuel + "\"/>\n");

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