/* Graph.java */

/* The package of this class. */
package util.graph;

/* Imported classes and/or interfaces. */
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import util.heap.Comparable;
import util.heap.MinimumHeap;

/** Implements graphs that represent the territories to be patrolled. */
public final class Graph {
	/* Attributes. */
	/** The label of the graph. */
	private String label;

	/** The set of nodes of the graph. */
	private Set<Node> nodes;

	/** The set of edges of the graph. */
	private Set<Edge> edges;

	/** Holds the weight of the idlenesses in the comparison of nodes. */
	private static final double IDLENESSES_WEIGHT = 0.5;

	/* Methods. */
	/**
	 * Constructor.
	 * 
	 * @param label
	 *            The label of the graph.
	 * @param nodes
	 *            The nodes of the graph.
	 */
	public Graph(String label, Node[] nodes) {
		this.label = label;

		this.nodes = new HashSet<Node>();
		for (int i = 0; i < nodes.length; i++)
			this.nodes.add(nodes[i]);

		// for each node, adds its edges to the set of edges
		this.edges = new HashSet<Edge>();
		Object[] nodes_array = this.nodes.toArray();
		for (int i = 0; i < nodes_array.length; i++) {
			Node current_node = (Node) nodes_array[i];

			Edge[] current_edges = current_node.getEdges();
			for (int j = 0; j < current_edges.length; j++)
				if (this.nodes.contains(current_edges[j]
						.getOtherNode(current_node)))
					this.edges.add(current_edges[j]);
		}

		if (this.edges.size() == 0)
			this.edges = null;
	}

	/**
	 * Obtains the nodes of the graph.
	 * 
	 * @return The nodes of the graph.
	 */
	public Node[] getNodes() {
		Object[] nodes_array = this.nodes.toArray();
		Node[] answer = new Node[nodes_array.length];

		for (int i = 0; i < answer.length; i++)
			answer[i] = (Node) nodes_array[i];

		return answer;
	}

	/**
	 * Obtains the edges of the graph.
	 * 
	 * @return The edges of the graph.
	 */
	public Edge[] getEdges() {
		Edge[] answer = new Edge[0];

		if (this.edges != null) {
			Object[] edges_array = this.edges.toArray();
			answer = new Edge[edges_array.length];

			for (int i = 0; i < answer.length; i++)
				answer[i] = (Edge) edges_array[i];
		}

		return answer;
	}

	/**
	 * Obtains the weight of the idlenesses in the comparison of nodes.
	 * 
	 * @return The weight of the idlenesses in the comparison of nodes.
	 */
	public static double getIdlenessesWeight() {
		return IDLENESSES_WEIGHT;
	}

	/**
	 * Verifies if the given node is part of the graph.
	 * 
	 * @param node
	 *            The node to be verified.
	 * @return TRUE if the node is part of the graph, FALSE if not.
	 */
	public boolean hasNode(Node node) {
		Object[] nodes_array = this.nodes.toArray();
		for (int i = 0; i < nodes_array.length; i++)
			if (((Node) nodes_array[i]).equals(node))
				return true;

		return false;
	}

	/**
	 * Verifies if the given edge is part of the graph.
	 * 
	 * @param edge
	 *            The edge to be verified.
	 * @return TRUE if the edge is part of the graph, FALSE if not.
	 */
	public boolean hasEdge(Edge edge) {
		if (this.edges != null) {
			Object[] edges_array = this.edges.toArray();
			for (int i = 0; i < edges_array.length; i++)
				if (((Edge) edges_array[i]).equals(edge))
					return true;
		}

		return false;
	}
	
	/**
	 * Returns the node of the graph that has the given id.
	 * 
	 * @param id
	 *            The id of the wanted node.
	 * @return The node with the given id, or NULL if there's no node with
	 *         such id.
	 */
	private Node getNode(String id) {
		Object[] nodes_array = this.nodes.toArray();
		for (int i = 0; i < nodes_array.length; i++) {
			Node current_node = (Node) nodes_array[i];
			if (current_node.getObjectId().equals(id))
				return current_node;
		}

		return null;
	}

	/**
	 * Returns the edge of the graph that has the given id.
	 * 
	 * @param id
	 *            The id of the wanted edge.
	 * @return The edge with the given id, or NULL if there's no edge with such
	 *         id.
	 */
	private Edge getEdge(String id) {
		if (this.edges != null) {
			Object[] edges_array = this.edges.toArray();
			for (int i = 0; i < edges_array.length; i++) {
				Edge current_edge = (Edge) edges_array[i];
				if (current_edge.getObjectId().equals(id))
					return current_edge;
			}
		}

		return null;
	}

	/**
	 * Compares a given graph to this one.
	 * 
	 * @param graph
	 *            The graph to be compared to this one.
	 * @return TRUE if the graph is equivalent, FALSE if not.
	 */
	public boolean equals(Graph graph) {
		if (graph != null) {
			if (!graph.label.equals(this.label))
				return false;

			Node[] nodes = graph.getNodes();
			for (int i = 0; i < nodes.length; i++) {
				Node node = this.getNode(nodes[i].getObjectId());
				if (node == null
						|| node.getIdleness() != nodes[i].getIdleness())
					return false;
			}

			Edge[] edges = graph.getEdges();
			for (int i = 0; i < edges.length; i++) {
				Edge edge = this.getEdge(edges[i].getObjectId());
				if (edge == null)
					return false;
			}

			return true;
		}

		return false;
	}


}
