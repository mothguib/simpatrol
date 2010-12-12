/* node.java */

/* The package of this class. */
package simpatrol.userclient.util.graph;

/* Imported classes and/or interfaces. */
import java.util.HashSet;
import java.util.Set;

/**
 * Implements the nodes of a Graph object.
 * 
 * @see Graph
 */
public final class Node {
	/* Atributes. */
	/** The object id of the node. */
	private String id;

	/** The label of the node. */
	private String label;

	/** The set of edges whose emitter is this node. */
	private Set<Edge> in_edges;

	/** The set of edges whose collector is this node. */
	private Set<Edge> out_edges;

	/** The idleness of the node. */
	private int idleness;

	/**
	 * The priority to visit this node. Its default value is ZERO.
	 */
	private double priority = 1.0d;

	/**
	 * Expresses if this node is a point of recharging the energy of the
	 * patrollers. Its default value is FALSE.
	 */
	private boolean fuel = false;

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
		this.idleness = 0;
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
	 *            The edge whose emitter is this node.
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
	 *            The edge whose collector is this node.
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
		Set<Edge> edges = new HashSet<Edge>();

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
	 * Configures the priority of the node.
	 * 
	 * @param priority
	 *            The priority.
	 */
	public void setPriority(double priority) {
		this.priority = priority;
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
	 * Configures the idleness of the node.
	 * 
	 * @param idleness
	 *            The idleness of the node, measured in cycles, or in seconds.
	 */
	public void setIdleness(int idleness) {
		this.idleness = idleness;
	}

	/**
	 * Returns the idleness of the node.
	 * 
	 * @return The idleness of the node.
	 */
	public int getIdleness() {
		return this.idleness;
	}

	/**
	 * Verifies if the node is the collector of a given edge.
	 * 
	 * @param edge
	 *            The edge whose collector is supposed to be the node.
	 * @return TRUE if the node is the collector of the edge, FALSE if not.
	 */
	public boolean isCollectorOf(Edge edge) {
		return this.in_edges.contains(edge);
	}

	/**
	 * Verifies if the node is the emitter of a given edge.
	 * 
	 * @param edge
	 *            The edge whose emitter is supposed to be the node.
	 * @return TRUE if the node is the emitter of the edge, FALSE if not.
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
		answer.idleness = this.idleness;
		answer.fuel = this.fuel;

		return answer;
	}

	/**
	 * Returns all the nodees in the neighbourhood.
	 * 
	 * @return The set of nodees in the neighbourhood.
	 */
	public Node[] getNeighbourhood() {
		// holds the set of neighbour nodes
		Set<Node> neighbourhood = new HashSet<Node>();

		// for each edge whose emitter is this node
		if (this.out_edges != null) {
			Object[] out_edges_array = this.out_edges.toArray();
			for (int i = 0; i < out_edges_array.length; i++) {
				// obtains the other node
				Node other_node = ((Edge) out_edges_array[i])
						.getOtherNode(this);

				// adds it to set of neighbours
				neighbourhood.add(other_node);
			}
		}

		// for each edge whose collector is this node
		if (this.in_edges != null) {
			Object[] in_edges_array = this.in_edges.toArray();
			for (int i = 0; i < in_edges_array.length; i++) {
				// obtains the other node
				Node other_node = ((Edge) in_edges_array[i])
						.getOtherNode(this);

				// adds it to set of neighbours
				neighbourhood.add(other_node);
			}
		}

		// mounts and returns the answer
		Object[] neighbourhood_array = neighbourhood.toArray();
		Node[] answer = new Node[neighbourhood_array.length];
		for (int i = 0; i < neighbourhood_array.length; i++)
			answer[i] = (Node) neighbourhood_array[i];
		return answer;
	}

	/**
	 * Returns the nodees in the neighbourhood of which emitter is this one.
	 * 
	 * @return The set of nodees in the neighbourhood of which emitter is this
	 *         one.
	 */
	public Node[] getCollectorNeighbourhood() {
		// holds the set of neighbour nodes
		Set<Node> neighbourhood = new HashSet<Node>();

		// for each edge whose emitter is this node
		if (this.out_edges != null) {
			Object[] out_edges_array = this.out_edges.toArray();
			for (int i = 0; i < out_edges_array.length; i++) {
				// obtains the other node
				Node other_node = ((Edge) out_edges_array[i])
						.getOtherNode(this);

				// adds it to set of neighbours
				neighbourhood.add(other_node);
			}
		}

		// mounts and returns the answer
		Object[] neighbourhood_array = neighbourhood.toArray();
		Node[] answer = new Node[neighbourhood_array.length];
		for (int i = 0; i < neighbourhood_array.length; i++)
			answer[i] = (Node) neighbourhood_array[i];
		return answer;
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

		// for each edge whose emitter is this node
		if (this.out_edges != null) {
			Object[] out_edges_array = this.out_edges.toArray();
			for (int i = 0; i < out_edges_array.length; i++) {
				// obtains the current edge
				Edge current_edge = (Edge) out_edges_array[i];

				// if the given node is the collector of the current edge,
				// adds it to the answer
				if (node.isCollectorOf(current_edge))
					shared_edges.add(current_edge);
			}
		}

		// for each edge whose collector is this node
		if (this.in_edges != null) {
			Object[] in_edges_array = this.in_edges.toArray();
			for (int i = 0; i < in_edges_array.length; i++) {
				// obtains the current edge
				Edge current_edge = (Edge) in_edges_array[i];

				// if the given node is the emitter of the current edge,
				// adds it to the answer
				if (node.isEmitterOf(current_edge))
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
	 * Returns all the edges between this node and the given one, of which
	 * emitter is this node.
	 * 
	 * @param node
	 *            The adjacent node of which edges shared with this node are
	 *            to be returned.
	 * @return The edges in common between this node and the given one, of
	 *         which emitter is this node.
	 */
	public Edge[] getConnectingOutEdges(Node node) {
		// holds the answer to the method
		Set<Edge> shared_edges = new HashSet<Edge>();

		// for each edge whose emitter is this node
		if (this.out_edges != null) {
			Object[] out_edges_array = this.out_edges.toArray();
			for (int i = 0; i < out_edges_array.length; i++) {
				// obtains the current edge
				Edge current_edge = (Edge) out_edges_array[i];

				// if the given node is the collector of the current edge,
				// adds it to the answer
				if (node.isCollectorOf(current_edge))
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
		if (this.id != null && object instanceof Node)
			return this.id.equals(((Node) object).getObjectId());
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