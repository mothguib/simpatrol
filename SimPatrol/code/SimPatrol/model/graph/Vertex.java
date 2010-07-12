/* Vertex.java (2.0) */
package br.org.simpatrol.server.model.graph;

/* Imported classes and/or interfaces. */
import java.util.HashSet;
import java.util.Set;

import br.org.simpatrol.server.model.interfaces.Visible;
import br.org.simpatrol.server.model.interfaces.XMLable;
import br.org.simpatrol.server.util.time.TimeSensible;

/**
 * Implements the vertexes of a {@link Graph} object.
 * 
 * @author Daniel Henriques Moreira (dhenriques@gmail.com)
 */
public class Vertex implements XMLable, Visible {
	/* Attributes. */
	/** The id of the vertex. */
	protected String id;

	/** The hashcode of the vertex. Its default value is ZERO. */
	protected int hashcode = 0;

	/** The label of the vertex. */
	protected String label;

	/** The set of edges whose emitter is this vertex. */
	protected Set<Edge> inEdges;

	/** The set of edges whose collector is this vertex. */
	protected Set<Edge> outEdges;

	/**
	 * The priority to visit this vertex. The closest to ZERO, the highest the
	 * priority. Its default value is ZERO.
	 */
	protected int priority = 0;

	/**
	 * Expresses if this vertex is visible in the graph. Its default value is
	 * TRUE.
	 */
	protected boolean visible = true;

	/**
	 * Expresses if this vertex is a point of recharging energy to the
	 * patrollers. Its default value is FALSE.
	 */
	protected boolean fuel = false;

	/**
	 * Registers the last time when this vertex was visited by an agent.
	 * Measured in seconds.
	 */
	protected double lastVisitTime;

	/**
	 * Counts the time. Shared by all the vertexes.
	 */
	private static TimeSensible timeCounter;

	/* Methods. */
	/**
	 * Constructor.
	 * 
	 * @param id
	 *            The id of the vertex.
	 * @param label
	 *            The label of the vertex.
	 */
	public Vertex(String id, String label) {
		this.id = id;
		this.label = label;
		this.inEdges = null;
		this.outEdges = null;
		this.lastVisitTime = 0;
	}

	/**
	 * Adds the given edge to the vertex.
	 * 
	 * @param edge
	 *            The edge added to the vertex. It cannot be an arc.
	 */
	public void addEdge(Edge edge) {
		// as the edge is not an arc, it must be added to both
		// in and out edge sets
		if (this.inEdges == null)
			this.inEdges = new HashSet<Edge>();
		if (this.outEdges == null)
			this.outEdges = new HashSet<Edge>();

		this.inEdges.add(edge);
		this.outEdges.add(edge);
	}

	/**
	 * Adds the passed edge as a way-out arc to the vertex.
	 * 
	 * @param outArc
	 *            The edge whose emitter is this vertex.
	 */
	public void addOutEdge(Edge outArc) {
		if (this.outEdges == null)
			this.outEdges = new HashSet<Edge>();
		this.outEdges.add(outArc);
	}

	/**
	 * Adds the passed edge as a way-in arc to the vertex.
	 * 
	 * @param inArc
	 *            The edge whose collector is this vertex.
	 */
	public void addInEdge(Edge inArc) {
		if (this.inEdges == null)
			this.inEdges = new HashSet<Edge>();
		this.inEdges.add(inArc);
	}

	/**
	 * Configures the priority to visit the vertex.
	 * 
	 * @param priority
	 *            The priority to visit the vertex. The closest to ZERO, the
	 *            highest the priority.
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
	public void setVisible(boolean visibility) {
		this.visible = visibility;
	}

	/**
	 * Verifies the visibility of the vertex.
	 * 
	 * @return TRUE if the vertex is visible, FALSE if not.
	 */
	public boolean isVisible() {
		return this.visible;
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
	 * Verifies if the vertex is a fuel recharging point.
	 * 
	 * @return TRUE, if the vertex is a fuel recharging point, FALSE if not.
	 */
	public boolean isFuel() {
		return this.fuel;
	}

	/**
	 * Returns the set of edges of the vertex.
	 * 
	 * @return The edges associated with the vertex.
	 */
	public Set<Edge> getEdges() {
		HashSet<Edge> edges = new HashSet<Edge>();

		if (this.inEdges != null)
			edges.addAll(this.inEdges);

		if (this.outEdges != null)
			edges.addAll(this.outEdges);

		if (edges.isEmpty())
			return null;
		else
			return edges;
	}

	/**
	 * Returns all the vertexes in the neighborhood.
	 * 
	 * @return The set of vertexes in the neighborhood.
	 */
	public Set<Vertex> getNeighborhood() {
		// holds the set of neighbor vertexes
		HashSet<Vertex> neighborhood = new HashSet<Vertex>();

		// for each edge whose emitter is this vertex
		if (this.outEdges != null)
			for (Edge edge : this.outEdges) {
				// obtains the other vertex
				Vertex otherVertex = edge.getOtherVertex(this);

				// adds it to set of neighbors
				neighborhood.add(otherVertex);
			}

		// for each edge whose collector is this vertex
		if (this.inEdges != null)
			for (Edge edge : this.inEdges) {
				// obtains the other vertex
				Vertex otherVertex = edge.getOtherVertex(this);

				// adds it to set of neighbors
				neighborhood.add(otherVertex);
			}

		// returns the answer
		if (neighborhood.isEmpty())
			return null;
		else
			return neighborhood;
	}

	/**
	 * Returns the vertexes in the neighborhood of which emitter is this one.
	 * 
	 * @return The set of vertexes in the neighborhood of which emitter is this
	 *         one.
	 */
	public Set<Vertex> getCollectingNeighborhood() {
		// holds the set of neighbor vertexes
		HashSet<Vertex> neighborhood = new HashSet<Vertex>();

		// for each edge whose emitter is this vertex
		if (this.outEdges != null)
			for (Edge edge : this.outEdges) {
				// obtains the other vertex
				Vertex otherVertex = edge.getOtherVertex(this);

				// adds it to set of neighbors
				neighborhood.add(otherVertex);
			}

		// returns the answer
		if (neighborhood.isEmpty())
			return null;
		else
			return neighborhood;
	}

	/**
	 * Returns all the edges between this vertex and the given one.
	 * 
	 * @param vertex
	 *            The adjacent vertex of which edges shared with this vertex are
	 *            to be returned.
	 * @return The edges in common between this vertex and the given one.
	 */
	public Set<Edge> getConnectingEdges(Vertex vertex) {
		// holds the answer to the method
		HashSet<Edge> sharedEdges = new HashSet<Edge>();

		// for each edge whose emitter is this vertex
		if (this.outEdges != null)
			for (Edge edge : this.outEdges)
				// if the given vertex is the collector of the current edge,
				// adds it to the answer
				if (vertex.isCollectorOf(edge))
					sharedEdges.add(edge);

		// for each edge whose collector is this vertex
		if (this.inEdges != null)
			for (Edge edge : this.inEdges)
				// if the given vertex is the collector of the current edge,
				// adds it to the answer
				if (vertex.isCollectorOf(edge))
					sharedEdges.add(edge);

		// returns the answer
		if (sharedEdges.isEmpty())
			return null;
		else
			return sharedEdges;
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
	public Set<Edge> getConnectingOutEdges(Vertex vertex) {
		// holds the answer to the method
		HashSet<Edge> sharedEdges = new HashSet<Edge>();

		// for each edge whose emitter is this vertex
		if (this.outEdges != null)
			for (Edge edge : this.outEdges)
				// if the given vertex is the collector of the current edge,
				// adds it to the answer
				if (vertex.isCollectorOf(edge))
					sharedEdges.add(edge);

		// returns the answer
		if (sharedEdges.isEmpty())
			return null;
		else
			return sharedEdges;
	}

	/**
	 * Registers a visit to the vertex.
	 * 
	 * Pay attention to have set the {@link Vertex#setTimeCounter(TimeSensible)}
	 * previously, else a {@link NullPointerException} will be thrown.
	 */
	public void visit() {
		this.lastVisitTime = Vertex.timeCounter.getElapsedTime();
	}

	/**
	 * Configures the idleness of the vertex.
	 * 
	 * Pay attention to have set the {@link #setTimeCounter(TimeSensible)}
	 * previously, else a {@link NullPointerException} will be thrown.
	 * 
	 * @param idleness
	 *            The idleness of the vertex, measured in seconds.
	 */
	public void setIdleness(double idleness) {
		this.lastVisitTime = Vertex.timeCounter.getElapsedTime() - idleness;
	}

	/**
	 * Calculates the idleness of the vertex at the current moment.
	 * 
	 * Pay attention to have set the {@link #setTimeCounter(TimeSensible)}
	 * previously, else a {@link NullPointerException} will be thrown.
	 * 
	 * @return The idleness of the vertex.
	 */
	public double getIdleness() {
		return Vertex.timeCounter.getElapsedTime() - this.lastVisitTime;
	}

	/**
	 * Configures the time counter of the vertexes.
	 * 
	 * @param counter
	 *            The time counter.
	 */
	public static void setTimeCounter(TimeSensible counter) {
		Vertex.timeCounter = counter;
	}

	/**
	 * Verifies if the vertex is the collector of a given edge.
	 * 
	 * @param edge
	 *            The edge whose collector is supposed to be this vertex.
	 * @return TRUE if the vertex is the collector of the edge, FALSE if not.
	 */
	public boolean isCollectorOf(Edge edge) {
		if (this.inEdges == null)
			return false;
		else
			return this.inEdges.contains(edge);
	}

	/**
	 * Verifies if the vertex is the emitter of a given edge.
	 * 
	 * @param edge
	 *            The edge whose emitter is supposed to be this vertex.
	 * @return TRUE if the vertex is the emitter of the edge, FALSE if not.
	 */
	public boolean isEmitterOf(Edge edge) {
		if (this.outEdges == null)
			return false;
		else
			return this.outEdges.contains(edge);
	}

	/**
	 * Returns a copy of the vertex, with no edges.
	 * 
	 * @return The copy of the vertex, without the edges.
	 */
	public Vertex getCopyWithoutEdges() {
		Vertex answer = new Vertex(this.id, this.label);
		answer.hashcode = this.hashCode();
		answer.priority = this.priority;
		answer.visible = this.visible;
		answer.fuel = this.fuel;
		answer.lastVisitTime = this.lastVisitTime;

		return answer;
	}

	public String fullToXML() {
		// holds the answer being constructed
		StringBuffer buffer = new StringBuffer();

		// fills the buffer
		buffer.append("<vertex id=\"" + this.id + "\" label=\"" + this.label
				+ "\" priority=\"" + this.priority + "\" visibility=\""
				+ this.visible + "\" idleness=\"" + this.getIdleness()
				+ "\" fuel=\"" + this.fuel + "\" is_enabled=\"true" + "\"/>");

		// returns the buffer content
		return buffer.toString();
	}

	public String reducedToXML() {
		// holds the answer being constructed
		StringBuffer buffer = new StringBuffer();

		// fills the buffer
		buffer.append("<vertex id=\"" + this.id + "\" label=\"" + this.label
				+ "\" priority=\"" + this.priority + "\" idleness=\""
				+ this.getIdleness() + "\" fuel=\"" + this.fuel + "\"/>");

		// returns the buffer content
		return buffer.toString();
	}

	public String getId() {
		return this.id;
	}

	public boolean equals(Object object) {
		if (object instanceof Vertex)
			return this.id.equals(((Vertex) object).getId());
		else
			return super.equals(object);
	}

	public int hashCode() {
		if (this.hashcode == 0)
			return super.hashCode();
		else
			return this.hashcode;
	}
}