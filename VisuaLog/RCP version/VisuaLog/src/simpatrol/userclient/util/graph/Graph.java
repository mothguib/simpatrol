/* Graph.java */

/* The package of this class. */
package simpatrol.userclient.util.graph;

/* Imported classes and/or interfaces. */
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import simpatrol.userclient.util.heap.Comparable;
import simpatrol.userclient.util.heap.MinimumHeap;

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

	/**
	 * Table that holds, for each node, its list of distances to the other
	 * nodes of the graph.
	 */
	private static List<DistancesList> distances_table;

	/** Holds the biggest distance between the nodes of the graph. */
	private static double biggest_distance;

	/** Holds the smallest distance between the nodes of the graph. */
	private static double smallest_distance;

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
	 * Fills the table that holds, for each node, its list of distances to the
	 * other nodes of the graph.
	 */
	private void calculateDistances() {
		distances_table = new LinkedList<DistancesList>();

		// initiates the bound distances
		biggest_distance = -1;
		smallest_distance = Double.MAX_VALUE;

		// for each node of the graph, obtains its distance to the other
		// nodes
		Object[] nodes_array = this.nodes.toArray();
		for (int i = 0; i < nodes_array.length; i++) {
			// obtains the current node
			Node node = (Node) nodes_array[i];

			// creates a list of distances for such node
			DistancesList list = new DistancesList(node);

			// for each one of the other nodes
			// 1st. for the ones previously treated, copy them...
			for (int j = 0; j < i; j++) {
				// obtains the line previously added to the table
				DistancesList previous_list = distances_table.get(j);

				// mounts an item copying data from the previous line
				NodeWithDistance new_item = new NodeWithDistance(
						previous_list.NODE, previous_list.DISTANCES_LIST
								.get(i - 1).DISTANCE);

				// adds it to the current list
				list.DISTANCES_LIST.add(new_item);
			}

			// 2nd. for the ones not treated yet
			for (int j = i + 1; j < nodes_array.length; j++) {
				// obtains the other node not treated yet
				Node other_node = (Node) nodes_array[j];

				// obtains the minimum path between the two considered
				// nodes
				Graph path = this.getDijkstraPath(node, other_node);

				// obtains the length of such path
				double path_length = 0;
				Edge[] path_edges = path.getEdges();
				for (int k = 0; k < path_edges.length; k++)
					path_length = path_length + path_edges[k].getLength();

				// adds a new item to the current list
				list.DISTANCES_LIST.add(new NodeWithDistance(other_node,
						path_length));

				// verifies if such path is one of the bounds of distance
				if (path_length > biggest_distance)
					biggest_distance = path_length;

				if (path_length < smallest_distance)
					smallest_distance = path_length;
			}

			// adds the list to the table of distances
			distances_table.add(list);
		}
	}

	/**
	 * Returns, for two given nodes, the distance between them.
	 * 
	 * @return The distance between the two given nodes. Returns the maximum
	 *         possible value if the nodes are not in the same partition.
	 */
	public double getDistance(Node node_1, Node node_2) {
		if (distances_table == null || distances_table.isEmpty())
			this.calculateDistances();

		for (int i = 0; i < distances_table.size(); i++)
			if (distances_table.get(i).NODE.equals(node_1)) {
				List<NodeWithDistance> list = distances_table.get(i).DISTANCES_LIST;

				for (int j = 0; j < list.size(); j++)
					if (list.get(j).NODE.equals(node_2))
						return list.get(j).DISTANCE;
			}

		return Double.MAX_VALUE;
	}

	/** Returns the smallest and biggest idlenesses of the graph. */
	public int[] getSmallestAndBiggestIdlenesses() {
		int smallest_idleness = Integer.MAX_VALUE;
		int biggest_idleness = -1;

		Object[] nodes_array = this.nodes.toArray();
		for (int i = 0; i < nodes_array.length; i++) {
			int idleness = ((Node) nodes_array[i]).getIdleness();

			if (idleness > biggest_idleness)
				biggest_idleness = idleness;

			if (idleness < smallest_idleness)
				smallest_idleness = idleness;
		}

		int[] answer = { smallest_idleness, biggest_idleness };
		return answer;
	}

	/**
	 * Returns the smallest and biggest distances among the nodes of the
	 * graph.
	 */
	public double[] getSmallestAndBiggestDistances() {
		if (distances_table == null || distances_table.isEmpty())
			this.calculateDistances();

		double[] answer = { smallest_distance, biggest_distance };
		return answer;
	}

	/**
	 * Returns the minimum path between two given nodes, using the Dijkstra's
	 * algorithm.
	 * 
	 * @param begin_node
	 *            The first node of the desired path.
	 * @param end_node
	 *            The last node of the desired path.
	 * @return The minimum path between two given nodes.
	 */
	public Graph getDijkstraPath(Node begin_node, Node end_node) {
		// for each node of the graph, correlates it with its distance
		// to the begin_node, as well as the last edge to reach it
		LinkedList<NodeDistanceEdge> nodes_with_distances_list = new LinkedList<NodeDistanceEdge>();

		Object[] nodes_array = this.nodes.toArray();
		for (int i = 0; i < nodes_array.length; i++) {
			Node node = (Node) nodes_array[i];

			if (node.equals(begin_node))
				nodes_with_distances_list.add(new NodeDistanceEdge(node,
						0, null));
			else
				nodes_with_distances_list.add(new NodeDistanceEdge(node,
						Double.MAX_VALUE, null));
		}

		// mounts a heap with the node-distance-edge trios
		Object[] nodes_with_distances_array = nodes_with_distances_list
				.toArray();
		NodeDistanceEdge[] nodes_with_distance = new NodeDistanceEdge[nodes_with_distances_array.length];
		for (int i = 0; i < nodes_with_distances_array.length; i++)
			nodes_with_distance[i] = (NodeDistanceEdge) nodes_with_distances_array[i];

		MinimumHeap heap = new MinimumHeap(nodes_with_distance);

		// while the heap is not empty
		while (!heap.isEmpty()) {
			// removes the minimum element of the heap
			NodeDistanceEdge minimum = (NodeDistanceEdge) heap
					.removeSmallest();

			// if the distance set to the minimum element is the maximum
			// possible double value,
			// return null (i.e. the graph is disconnected, and the end_node
			// is unreachable)
			if (minimum.distance == Double.MAX_VALUE)
				return null;

			// if the minimum element has the end_node, quits the loop
			if (minimum.NODE.equals(end_node))
				break;

			// obtains the current node to be expanded
			Node current_node = minimum.NODE;

			// obtains the neighbourhood where the current node is an emitter
			Node[] neighbourhood = current_node.getCollectorNeighbourhood();

			// for each node of the neighbourhood
			for (int i = 0; i < neighbourhood.length; i++) {
				// obtains all the edges between the current node and the
				// current
				// neighbour, of which emitter is the current node
				Edge[] edges = current_node
						.getConnectingOutEdges(neighbourhood[i]);

				// finds the smallest enabled edge
				Edge smallest_edge = null;
				double smallest_length = Double.MAX_VALUE;

				for (int j = 0; j < edges.length; j++)
					if (edges[j].getLength() < smallest_length) {
						smallest_edge = edges[j];
						smallest_length = edges[j].getLength();
					}

				// if there's a smallest edge
				if (smallest_edge != null) {
					// obtains the node-distance-edge trio of the current
					// neighbour
					NodeDistanceEdge neighbour_with_distance = null;

					for (int j = 0; j < nodes_with_distance.length; j++)
						if (nodes_with_distance[j].NODE
								.equals(neighbourhood[i])) {
							neighbour_with_distance = nodes_with_distance[j];
							break;
						}

					// verifies if it's necessary to atualize the
					// neighbour's trio
					if (neighbour_with_distance.distance > minimum.distance
							+ smallest_edge.getLength()) {
						neighbour_with_distance.distance = minimum.distance
								+ smallest_edge.getLength();
						neighbour_with_distance.edge = smallest_edge;
					}
				}

			}

			// assures the heap structure is correct
			heap.assureMinimumHeap();
		}

		// mounts the answer of the method...
		// sets the current node being added to the answer as the end_node
		Node current_node = end_node;

		// obtains a copy of the current node
		Node current_node_copy = current_node.getCopy();

		// the answer of the method
		Node[] nodes_answer = { current_node_copy };
		Graph answer = new Graph("dijkstra's path", nodes_answer);
		answer.edges = new HashSet<Edge>();

		// keep on mounting the answer...
		while (true) {
			// finds the node-distance-edge trio of the current node
			NodeDistanceEdge current_node_with_distance = null;
			for (int i = 0; i < nodes_with_distance.length; i++)
				if (nodes_with_distance[i].NODE.equals(current_node)) {
					current_node_with_distance = nodes_with_distance[i];
					break;
				}

			// sets the next_edge
			Edge next_edge = current_node_with_distance.edge;

			// if the next edge is valid
			if (next_edge != null) {
				// obtains the next node
				Node next_node = next_edge.getOtherNode(current_node);

				// obtains copies of the next edge and next node
				Node next_node_copy = next_node.getCopy();
				Edge next_edge_copy = next_edge.getCopy(current_node_copy,
						next_node_copy);

				// adds the copies to the answer
				answer.nodes.add(next_node_copy);
				answer.edges.add(next_edge_copy);

				// atualizes the current node and current node copy
				current_node = next_node;
				current_node_copy = next_node_copy;
			}
			// if not, break the loop
			else
				break;
		}

		// returns the answer
		if (answer.edges.isEmpty())
			answer.edges = null;
		return answer;
	}

	/**
	 * Returns the minimum path between two given nodes, using the Dijkstra's
	 * algorithm. The evaluation of the paths is based not only in the distance
	 * of the nodes, but also in their idlenesses.
	 * 
	 * @param begin_node
	 *            The first node of the desired path.
	 * @param end_node
	 *            The last node of the desired path.
	 * @return The minimum path between two given nodes.
	 */
	public Graph getIdlenessedDijkstraPath(Node begin_node,
			Node end_node) {
		// configures the trios "node - distance - edge"
		IdlenessedNodeDistanceEdge.graph = this;

		// for each node of the graph, correlates it with its distance
		// to the begin_node, as well as the last edge to reach it
		LinkedList<IdlenessedNodeDistanceEdge> nodes_with_distances_list = new LinkedList<IdlenessedNodeDistanceEdge>();

		Object[] nodes_array = this.nodes.toArray();
		for (int i = 0; i < nodes_array.length; i++) {
			Node node = (Node) nodes_array[i];

			if (node.equals(begin_node))
				nodes_with_distances_list
						.add(new IdlenessedNodeDistanceEdge(node, 0, null));
			else
				nodes_with_distances_list
						.add(new IdlenessedNodeDistanceEdge(node,
								Double.MAX_VALUE, null));
		}

		// mounts a heap with the node-distance-edge trios
		Object[] nodes_with_distances_array = nodes_with_distances_list
				.toArray();
		IdlenessedNodeDistanceEdge[] nodes_with_distance = new IdlenessedNodeDistanceEdge[nodes_with_distances_array.length];
		for (int i = 0; i < nodes_with_distances_array.length; i++)
			nodes_with_distance[i] = (IdlenessedNodeDistanceEdge) nodes_with_distances_array[i];

		MinimumHeap heap = new MinimumHeap(nodes_with_distance);

		// while the heap is not empty
		while (!heap.isEmpty()) {
			// removes the minimum element of the heap
			IdlenessedNodeDistanceEdge minimum = (IdlenessedNodeDistanceEdge) heap
					.removeSmallest();

			// if the distance set to the minimum element is the maximum
			// possible double value,
			// return null (i.e. the graph is disconnected, and the end_node
			// is unreachable)
			if (minimum.distance == Double.MAX_VALUE)
				return null;

			// if the minimum element has the end_node, quits the loop
			if (minimum.NODE.equals(end_node))
				break;

			// obtains the current node to be expanded
			Node current_node = minimum.NODE;

			// obtains the neighbourhood where the current node is an emitter
			Node[] neighbourhood = current_node.getCollectorNeighbourhood();

			// for each node of the neighbourhood
			for (int i = 0; i < neighbourhood.length; i++) {
				// obtains all the edges between the current node and the
				// current
				// neighbour, of which emitter is the current node
				Edge[] edges = current_node
						.getConnectingOutEdges(neighbourhood[i]);

				// finds the smallest enabled edge
				Edge smallest_edge = null;
				double smallest_length = Double.MAX_VALUE;

				for (int j = 0; j < edges.length; j++)
					if (edges[j].getLength() < smallest_length) {
						smallest_edge = edges[j];
						smallest_length = edges[j].getLength();
					}

				// if there's a smallest edge
				if (smallest_edge != null) {
					// obtains the node-distance-edge trio of the current
					// neighbour
					IdlenessedNodeDistanceEdge neighbour_with_distance = null;

					for (int j = 0; j < nodes_with_distance.length; j++)
						if (nodes_with_distance[j].NODE
								.equals(neighbourhood[i])) {
							neighbour_with_distance = nodes_with_distance[j];
							break;
						}

					// verifies if it's necessary to update the
					// neighbour's trio
					if (neighbour_with_distance.distance > minimum.distance
							+ smallest_edge.getLength()) {
						neighbour_with_distance.distance = minimum.distance
								+ smallest_edge.getLength();
						neighbour_with_distance.edge = smallest_edge;
					}
				}

			}

			// assures the heap structure is correct
			heap.assureMinimumHeap();
		}

		// mounts the answer of the method...
		// sets the current node being added to the answer as the end_node
		Node current_node = end_node;

		// obtains a copy of the current node
		Node current_node_copy = current_node.getCopy();

		// the answer of the method
		Node[] nodes_answer = { current_node_copy };
		Graph answer = new Graph("dijkstra's path", nodes_answer);
		answer.edges = new HashSet<Edge>();

		// keep on mounting the answer...
		while (true) {
			// finds the node-distance-edge trio of the current node
			IdlenessedNodeDistanceEdge current_node_with_distance = null;
			for (int i = 0; i < nodes_with_distance.length; i++)
				if (nodes_with_distance[i].NODE.equals(current_node)) {
					current_node_with_distance = nodes_with_distance[i];
					break;
				}

			// sets the next_edge
			Edge next_edge = current_node_with_distance.edge;

			// if the next edge is valid
			if (next_edge != null) {
				// obtains the next node
				Node next_node = next_edge.getOtherNode(current_node);

				// obtains copies of the next edge and next node
				Node next_node_copy = next_node.getCopy();
				Edge next_edge_copy = next_edge.getCopy(current_node_copy,
						next_node_copy);

				// adds the copies to the answer
				answer.nodes.add(next_node_copy);
				answer.edges.add(next_edge_copy);

				// atualizes the current node and current node copy
				current_node = next_node;
				current_node_copy = next_node_copy;
			}
			// if not, break the loop
			else
				break;
		}

		// returns the answer
		if (answer.edges.isEmpty())
			answer.edges = null;
		return answer;
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

/**
 * Internal class that holds, for a specific node, its distance to others kept
 * in a list.
 */
final class DistancesList {
	/** The node of which distances are held. */
	public final Node NODE;

	/** The list of nodees and their respective distances. */
	public final List<NodeWithDistance> DISTANCES_LIST;

	/**
	 * Constructor.
	 * 
	 * @param node
	 *            The node of which list is being held.
	 */
	public DistancesList(Node node) {
		this.NODE = node;
		this.DISTANCES_LIST = new LinkedList<NodeWithDistance>();
	}
}

/** Internal class that holds together a node and the distance to reach it. */
final class NodeWithDistance {
	/** The node. */
	public final Node NODE;

	/** The distance to reach it. */
	public final double DISTANCE;

	/**
	 * Constructor.
	 * 
	 * @param node
	 *            The node.
	 * @param distance
	 *            The distance to reach the node.
	 */
	public NodeWithDistance(Node node, double distance) {
		this.NODE = node;
		this.DISTANCE = distance;
	}
}

/**
 * Internal class that holds together a node, the distance of the path to
 * reach it from another considered node, and the last edge of such path.
 * 
 * This class also implements the interface Comparable, in order to provide a
 * "isSmallerThan" method that considers the distance of the path, in the
 * comparison with a given object.
 */
final class NodeDistanceEdge implements Comparable {
	/** The node. */
	public final Node NODE;

	/** The distance to the node. */
	public double distance;

	/** The last edge to reach the node. */
	public Edge edge;

	/**
	 * Constructor.
	 * 
	 * @param node
	 *            The node of which distance is being held.
	 * @param distance
	 *            The distance to reach the node.
	 * @param edge
	 *            The last edge to reach the node.
	 */
	public NodeDistanceEdge(Node node, double distance, Edge edge) {
		this.NODE = node;
		this.distance = distance;
		this.edge = edge;
	}

	public boolean isSmallerThan(Comparable object) {
		if (object instanceof NodeDistanceEdge)
			if (this.distance < ((NodeDistanceEdge) object).distance)
				return true;

		return false;
	}
}

/**
 * Internal class that holds together a node, the distance of the path to
 * reach it from another considered node, and the last edge of such path.
 * 
 * This class also implements the interface Comparable, in order to provide a
 * "isSmallerThan" method that considers not only the distance of the path, but
 * also the idleness of the held node, in the comparison with a given object.
 */
final class IdlenessedNodeDistanceEdge implements Comparable {
	/** The node. */
	public final Node NODE;

	/** The distance to the node. */
	public double distance;

	/** The last edge to reach the node. */
	public Edge edge;

	/** The graph of the simulation. */
	public static Graph graph;

	/**
	 * Constructor.
	 * 
	 * @param node
	 *            The node of which distance is being held.
	 * @param distance
	 *            The distance to reach the node.
	 * @param edge
	 *            The last edge to reach the node.
	 */
	public IdlenessedNodeDistanceEdge(Node node, double distance,
			Edge edge) {
		this.NODE = node;
		this.distance = distance;
		this.edge = edge;
	}

	public boolean isSmallerThan(Comparable object) {
		if (object instanceof IdlenessedNodeDistanceEdge) {
			if (this.distance == 0)
				return true;
			else if (((IdlenessedNodeDistanceEdge) object).distance == 0)
				return false;
			else if (this.distance == Double.MAX_VALUE)
				return false;
			else if (((IdlenessedNodeDistanceEdge) object).distance == Double.MAX_VALUE)
				return true;

			// obtains the biggest and smallest idlenesses of the graph
			int[] bound_idlenesses = graph.getSmallestAndBiggestIdlenesses();

			// obtains the biggest and smallest distances of the graph
			double[] bound_distances = graph.getSmallestAndBiggestDistances();

			// obtains the value for this object
			double this_norm_idleness = 0;
			if (bound_idlenesses[0] < bound_idlenesses[1])
				this_norm_idleness = (this.NODE.getIdleness() - bound_idlenesses[0])
						* Math.pow((bound_idlenesses[1] - bound_idlenesses[0]),
								-1);

			double this_norm_distance = 0;
			if (bound_distances[0] < bound_distances[1])
				this_norm_distance = (bound_distances[1] - this.distance)
						* Math.pow((bound_distances[1] - bound_distances[0]),
								-1);

			double this_value = Graph.getIdlenessesWeight()
					* this_norm_idleness + (1 - Graph.getIdlenessesWeight())
					* this_norm_distance;

			// obtains the value for the other object
			double other_norm_idleness = 0;
			if (bound_idlenesses[0] < bound_idlenesses[1])
				other_norm_idleness = (((IdlenessedNodeDistanceEdge) object).NODE
						.getIdleness() - bound_idlenesses[0])
						* Math.pow((bound_idlenesses[1] - bound_idlenesses[0]),
								-1);

			double other_norm_distance = 0;
			if (bound_distances[0] < bound_distances[1])
				other_norm_distance = (bound_distances[1] - ((IdlenessedNodeDistanceEdge) object).distance)
						* Math.pow((bound_distances[1] - bound_distances[0]),
								-1);

			double other_value = Graph.getIdlenessesWeight()
					* other_norm_idleness + (1 - Graph.getIdlenessesWeight())
					* other_norm_distance;

			/*
			 * Specially here, if a node has greater value than another one,
			 * then it is smaller than the another one (so we can use a minimum
			 * heap).
			 */
			if (this_value > other_value)
				return true;
		}

		return false;
	}
}