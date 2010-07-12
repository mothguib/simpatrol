/* Node.java */

/* The package of this class. */
package util.graph;

/* Imported classes and/or interfaces. */
import java.util.HashSet;
import java.util.Set;

/**
 * Implements the nodes of a Graph object.
 * 
 * @see Graph
 */
public final class Node {
	/* Attributes. */
	/** The object id of the node. */
	private String id;

	/** The label of the node. */
	private String label;

	/** The set of edges whose source is this node. */
	private Set<Edge> in_edges;

	/** The set of edges whose target is this node. */
	private Set<Edge> out_edges;

	/** The idleness of the node. */
	private double idleness;

	/**
	 * The priority to visit this node. Its default value is ZERO.
	 */
	private int priority = 0;

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
	 * Removes a given edge from the node.
	 * 
	 * @param edge
	 *            The edge to be removed from the node.
	 */
	public void removeEdge(Edge edge) {
		if (this.in_edges != null)
			this.in_edges.remove(edge);

		if (this.out_edges != null)
			this.out_edges.remove(edge);
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
	 * Returns the degree of the node.
	 * 
	 * @return The degree of the node.
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
	 * Returns the set of edges of the node.
	 * 
	 * @return The edges associated with the node.
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
	 * Configures the priority of the node.
	 * 
	 * @param priority
	 *            The priority.
	 */
	public void setPriority(int priority) {
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
	public void setIdleness(double idleness) {
		this.idleness = idleness;
	}

	/**
	 * Returns the idleness of the node.
	 * 
	 * @return The idleness of the node.
	 */
	public double getIdleness() {
		return this.idleness;
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
		answer.idleness = this.idleness;
		answer.fuel = this.fuel;

		return answer;
	}

	/**
	 * Returns all the nodes in the neighborhood.
	 * 
	 * @return The set of nodes in the neighborhood.
	 */
	public Node[] getNeighbourhood() {
		// holds the set of neighbor nodes
		Set<Node> neighbourhood = new HashSet<Node>();

		// for each edge whose source is this node
		if (this.out_edges != null)
			for (Edge edge : this.out_edges) {
				Node other_node = edge.getOtherNode(this);
				neighbourhood.add(other_node);
			}

		// for each edge whose source is this node
		if (this.in_edges != null)
			for (Edge edge : this.in_edges) {
				Node other_node = edge.getOtherNode(this);
				neighbourhood.add(other_node);
			}

		// mounts and returns the answer
		Node[] answer = new Node[neighbourhood.size()];
		int i = 0;
		for (Node node : neighbourhood) {
			answer[i] = node;
			i++;
		}

		return answer;
	}

	/**
	 * Returns the nodes in the neighborhood of which source is this one.
	 * 
	 * @return The set of nodes in the neighborhood of which source is this
	 *         one.
	 */
	public Node[] getCollectorNeighbourhood() {
		// holds the set of neighbor nodes
		Set<Node> neighbourhood = new HashSet<Node>();

		// for each edge whose source is this node
		if (this.out_edges != null)
			for (Edge edge : this.out_edges) {
				Node other_node = edge.getOtherNode(this);
				neighbourhood.add(other_node);
			}

		// mounts and returns the answer
		Node[] answer = new Node[neighbourhood.size()];
		int i = 0;
		for (Node node : neighbourhood) {
			answer[i] = node;
			i++;
		}

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

		// for each edge whose source is this node
		if (this.out_edges != null)
			for (Edge edge : this.out_edges)
				if (node.isCollectorOf(edge))
					shared_edges.add(edge);

		// for each edge whose target is this node
		if (this.in_edges != null)
			for (Edge edge : this.in_edges)
				if (node.isEmitterOf(edge))
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
				if (node.isCollectorOf(edge))
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