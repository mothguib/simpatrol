/* Node.java */

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
 * Implements the Node of a Graph object.
 * 
 * @see Graph
 */
public class Node implements XMLable, Visible {
	/* Attributes. */
	/**
	 * The object id of the node. Not part of the patrolling problem modeling.
	 */
	protected String id;

	/** The label of the node. */
	protected String label;

	/** The set of edges whose source is this node. */
	protected Set<Edge> in_edges;

	/** The set of edges whose target is this node. */
	protected Set<Edge> out_edges;

	/**
	 * The priority to visit this node. Its default value is ZERO.
	 */
	protected int priority = 0;

	/**
	 * Expresses if this node is visible in the graph. Its default value is
	 * TRUE.
	 */
	protected boolean visibility = true;

	/**
	 * Expresses if this node is a point of recharging energy to the
	 * patrollers. Its default value is FALSE.
	 */
	protected boolean fuel = false;

	/**
	 * Registers the last time when this node was visited by an agent.
	 * Measured in cycles, if the simulator is a cycled one, or in seconds, if
	 * it's a real time one.
	 * 
	 * @see CycledSimulator
	 * @see RealTimeSimulator
	 */
	protected double last_visit_time;

	/**
	 * Counts the time. Shared by all the Node.
	 */
	protected static TimeSensible time_counter;

	/* Methods. */
	/**
	 * Constructor.
	 * 
	 * @param label
	 *            The label of the node.
	 */
	public Node(String label) {
		this.label = label;
		this.in_edges = null;
		this.out_edges = null;
		this.last_visit_time = 0;
	}

	/**
	 * Adds the passed edge to the node.
	 * 
	 * @param edge
	 *            The edge added to the node. It cannot be an arc.
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
	 * Adds the passed edge as a way out arc to the node.
	 * 
	 * @param out_arc
	 *            The edge whose source is this node.
	 */
	public void addOutEdge(Edge out_arc) {
		if (this.out_edges == null)
			this.out_edges = new HashSet<Edge>();
		this.out_edges.add(out_arc);
	}

	/**
	 * Adds the passed edge as a way in arc to the node.
	 * 
	 * @param in_arc
	 *            The edge whose target is this node.
	 */
	public void addInEdge(Edge in_arc) {
		if (this.in_edges == null)
			this.in_edges = new HashSet<Edge>();
		this.in_edges.add(in_arc);
	}

	/**
	 * Returns the set of edges of the node.
	 * 
	 * @return The edges associated with the node.
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
	 * Configures the priority of the node.
	 * 
	 * @param priority
	 *            The priority.
	 */
	public void setPriority(int priority) {
		this.priority = priority;
	}

	/**
	 * Configures the visibility of the node.
	 * 
	 * @param visibility
	 *            The visibility.
	 */
	public void setVisibility(boolean visibility) {
		this.visibility = visibility;
	}

	/**
	 * Verifies the visibility of the node.
	 * 
	 * @return TRUE if the node is visible, FALSE if not.
	 */
	public boolean isVisible() {
		return this.visibility;
	}

	/**
	 * Verifies if the node is a fuel recharging point.
	 * 
	 * @return TRUE, if the node is a fuel recharging point, FALSE if not.
	 */
	public boolean isFuel() {
		return this.fuel;
	}

	/**
	 * Configures if the node is a fuel recharging point.
	 * 
	 * @param fuel
	 *            TRUE, if the node is a fuel recharging point, FALSE if not.
	 */
	public void setFuel(boolean fuel) {
		this.fuel = fuel;
	}

	/**
	 * Configures the last time this node was visited.
	 * 
	 * @param time
	 *            The time of the last visit, measured in cycles or in seconds.
	 */
	public void setLast_visit_time(double time) {
		this.last_visit_time = time;
	}

	/**
	 * Configures the time counter of the Node.
	 * 
	 * @param counter
	 *            The time counter.
	 */
	public static void setTime_counter(TimeSensible counter) {
		Node.time_counter = counter;
	}

	/**
	 * Configures the idleness of the node.
	 * 
	 * Pay attention not to use "setIdleness(0)" in order to register a visit
	 * onto this node. Use "setLast_visit_time(int time)" instead.
	 * 
	 * @param idleness
	 *            The idleness of the node, measured in cycles, or in seconds.
	 */
	public void setIdleness(double idleness) {
		this.last_visit_time = this.last_visit_time - idleness;
	}

	/**
	 * Calculates the idleness of the node at the current moment.
	 * 
	 * @return The idleness of the node.
	 */
	public double getIdleness() {
		if (Node.time_counter != null)
			return Node.time_counter.getElapsedTime() - this.last_visit_time;
		else
			return Math.abs(this.last_visit_time);
	}

	/**
	 * Verifies if the node is the target of a given edge.
	 * 
	 * @param edge
	 *            The edge whose target is supposed to be the node.
	 * @return TRUE if the node is the target of the edge, FALSE if not.
	 */
	public boolean isCollectorOf(Edge edge) {
		return this.in_edges.contains(edge);
	}

	/**
	 * Verifies if the node is the source of a given edge.
	 * 
	 * @param edge
	 *            The edge whose source is supposed to be the node.
	 * @return TRUE if the node is the source of the edge, FALSE if not.
	 */
	public boolean isEmitterOf(Edge edge) {
		return this.out_edges.contains(edge);
	}

	/**
	 * Returns a copy of the node, with no edges.
	 * 
	 * @return The copy of the node, without the edges.
	 */
	public Node getCopy() {
		Node answer = new Node(this.label);
		answer.id = this.id;
		answer.priority = this.priority;
		answer.visibility = this.visibility;
		answer.fuel = this.fuel;
		answer.last_visit_time = this.last_visit_time;

		return answer;
	}

	/**
	 * Returns all the Node in the neighbourhood.
	 * 
	 * @return The set of Node in the neighbourhood.
	 */
	public Node[] getNeighbourhood() {
		// holds the set of neighbor Node
		Set<Node> neighbourhood = new HashSet<Node>();

		// for each edge whose source is this node
		if (this.out_edges != null)
			for (Edge edge : this.out_edges) {
				// obtains the other node
				Node other_node = edge.getOtherNode(this);

				// adds it to set of neighbors
				neighbourhood.add(other_node);
			}

		// for each edge whose target is this node
		if (this.in_edges != null)
			for (Edge edge : this.in_edges) {
				// obtains the other node
				Node other_node = edge.getOtherNode(this);

				// adds it to set of neighbors
				neighbourhood.add(other_node);
			}

		// returns the answer
		return neighbourhood.toArray(new Node[0]);
	}

	/**
	 * Returns the Node in the neighborhood of which source is this one.
	 * 
	 * @return The set of Node in the neighborhood of which source is this
	 *         one.
	 */
	public Node[] getCollectorNeighbourhood() {
		// holds the set of neighbor Node
		Set<Node> neighbourhood = new HashSet<Node>();

		// for each edge whose source is this node
		if (this.out_edges != null)
			for (Edge edge : this.out_edges) {
				// obtains the other node
				Node other_node = edge.getOtherNode(this);

				// adds it to set of neighbors
				neighbourhood.add(other_node);
			}

		// returns the answer
		return neighbourhood.toArray(new Node[0]);
	}

	/**
	 * Returns all the edges between this node and the given one.
	 * 
	 * @param node
	 *            The adjacent node of which edges shared with this node are
	 *            to be returned.
	 * @return The edges in common between this node and the given one.
	 */
	public Edge[] getConnectingEdges(Node node) {
		// holds the answer to the method
		Set<Edge> shared_edges = new HashSet<Edge>();

		// for each edge whose source is this node
		if (this.out_edges != null)
			for (Edge edge : this.out_edges)
				// if the given node is the target of the current edge,
				// adds it to the answer
				if (node.isCollectorOf(edge))
					shared_edges.add(edge);

		// for each edge whose target is this node
		if (this.in_edges != null)
			for (Edge edge : this.in_edges)
				// if the given node is the target of the current edge,
				// adds it to the answer
				if (node.isCollectorOf(edge))
					shared_edges.add(edge);

		// returns the answer
		return shared_edges.toArray(new Edge[0]);
	}

	/**
	 * Returns all the edges between this node and the given one, of which
	 * source is this node.
	 * 
	 * @param node
	 *            The adjacent node of which edges shared with this node are
	 *            to be returned.
	 * @return The edges in common between this node and the given one, of
	 *         which source is this node.
	 */
	public Edge[] getConnectingOutEdges(Node node) {
		// holds the answer to the method
		Set<Edge> shared_edges = new HashSet<Edge>();

		// for each edge whose source is this node
		if (this.out_edges != null)
			for (Edge edge : this.out_edges)
				// if the given node is the target of the current edge,
				// adds it to the answer
				if (node.isCollectorOf(edge))
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
		buffer.append("<node id=\"" + this.id + "\" label=\"" + this.label
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
		buffer.append("<node id=\"" + this.id + "\" label=\"" + this.label
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