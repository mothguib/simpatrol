/* Graph.java */

/* The package of this class. */
package util.graph;

/* Imported classes and/or interfaces. */
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import util.heap.MinimumHeap;
import util.heap.Comparable;

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
	private static final double IDLENESSES_WEIGHT = 0.2;

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
		for (Node node : this.nodes) {
			Edge[] current_edges = node.getEdges();
			for (int i = 0; i < current_edges.length; i++)
				if (this.nodes.contains(current_edges[i]
						.getOtherNode(node)))
					this.edges.add(current_edges[i]);
		}

		if (this.edges.size() == 0)
			this.edges = null;
	}

	/**
	 * Obtains the nodes of the graph.
	 * 
	 * @return The nodes of the graph.
	 */
	public Node[] getNodees() {
		Node[] answer = new Node[this.nodes.size()];
		int i = 0;
		for (Node node : this.nodes) {
			answer[i] = node;
			i++;
		}

		return answer;
	}

	/**
	 * Obtains the edges of the graph.
	 * 
	 * @return The edges of the graph.
	 */
	public Edge[] getEdges() {
		if (this.edges != null) {
			Edge[] answer = new Edge[this.edges.size()];
			int i = 0;
			for (Edge edge : this.edges) {
				answer[i] = edge;
				i++;
			}

			return answer;
		}

		return new Edge[0];
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
		for (Node current_node : this.nodes)
			if (current_node.equals(node))
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
		if (this.edges != null)
			for (Edge current_edge : this.edges)
				if (current_edge.equals(edge))
					return true;

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
	public double[] getSmallestAndBiggestIdlenesses() {
		double smallest_idleness = Double.MAX_VALUE;
		double biggest_idleness = -1;

		for (Node node : this.nodes) {
			double idleness = node.getIdleness();

			if (idleness > biggest_idleness)
				biggest_idleness = idleness;

			if (idleness < smallest_idleness)
				smallest_idleness = idleness;
		}

		double[] answer = { smallest_idleness, biggest_idleness };
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

		for (Node node : this.nodes)
			if (node.equals(begin_node))
				nodes_with_distances_list.add(new NodeDistanceEdge(node,
						0, null));
			else
				nodes_with_distances_list.add(new NodeDistanceEdge(node,
						Double.MAX_VALUE, null));

		// mounts a heap with the node-distance-edge trios
		NodeDistanceEdge[] nodes_with_distance = new NodeDistanceEdge[nodes_with_distances_list
				.size()];
		int index = 0;
		for (NodeDistanceEdge node_with_distance : nodes_with_distances_list) {
			nodes_with_distance[index] = node_with_distance;
			index++;
		}

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

			// obtains the neighborhood where the current node is an source
			Node[] neighbourhood = current_node.getCollectorNeighbourhood();

			// for each node of the neighborhood
			for (int i = 0; i < neighbourhood.length; i++) {
				// obtains all the edges between the current node and the
				// current neighbor, of which source is the current node
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
					// neighbor
					NodeDistanceEdge neighbour_with_distance = null;

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

				// updates the current node and current node copy
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

		for (Node node : this.nodes)
			if (node.equals(begin_node))
				nodes_with_distances_list
						.add(new IdlenessedNodeDistanceEdge(node, 0, null));
			else
				nodes_with_distances_list
						.add(new IdlenessedNodeDistanceEdge(node,
								Double.MAX_VALUE, null));

		// mounts a heap with the node-distance-edge trios
		IdlenessedNodeDistanceEdge[] nodes_with_distance = new IdlenessedNodeDistanceEdge[nodes_with_distances_list
				.size()];
		int index = 0;
		for (IdlenessedNodeDistanceEdge node_with_distance : nodes_with_distances_list) {
			nodes_with_distance[index] = node_with_distance;
			index++;
		}

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

			// obtains the neighborhood where the current node is an source
			Node[] neighbourhood = current_node.getCollectorNeighbourhood();

			// for each node of the neighborhood
			for (int i = 0; i < neighbourhood.length; i++) {
				// obtains all the edges between the current node and the
				// current
				// neighbor, of which source is the current node
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
					// neighbor
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

				// updates the current node and current node copy
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
	 * Returns the minimum (M) weight (W) spanning (S) tree (T) of the graph.
	 * The algorithm used is the Kruskal's.
	 * 
	 * @return The minimum-weight spanning tree of the graph.
	 */
	private Graph getMWST() {
		// minimum heap with the edges of the graph, based on their lengths
		MinimumHeap heap = new MinimumHeap(this.getEdges());

		// set that contains the sets of nodes
		HashSet<HashSet<Node>> node_sets = new HashSet<HashSet<Node>>();

		// holds the copies of each node of the graph
		LinkedList<Node> node_copies = new LinkedList<Node>();

		// creates a set for each node of the graph and obtains a copy of it
		for (Node node : this.nodes) {
			// set creation
			HashSet<Node> set = new HashSet<Node>();
			set.add(node);
			node_sets.add(set);

			// node copy
			node_copies.add(node.getCopy());
		}

		// for each edge held in the minimum heap
		while (!heap.isEmpty()) {
			// obtains the current edge
			Edge current_edge = (Edge) heap.removeSmallest();

			// obtains its nodes
			Node[] current_nodes = current_edge.getNodees();

			// finds the set of the first node
			for (HashSet<Node> node_set : node_sets)
				if (node_set.contains(current_nodes[0])) {
					// if such set does not contain the second node of the
					// edge
					if (!node_set.contains(current_nodes[1])) {
						// obtains the copies of such nodes
						Node node_copy_1 = null;
						Node node_copy_2 = null;

						for (Node node_copy : node_copies) {
							if (node_copy.equals(current_nodes[0]))
								node_copy_1 = node_copy;
							else if (node_copy.equals(current_nodes[1]))
								node_copy_2 = node_copy;

							if (node_copy_1 != null && node_copy_2 != null)
								break;
						}

						// connects them based on the current edge
						current_edge.getCopy(node_copy_1, node_copy_2);

						// unify the sets of the two nodes
						// finds the set of the second node
						for (HashSet<Node> other_node_set : node_sets)
							if (other_node_set.contains(current_nodes[1])) {
								// removes the other set from the list of sets
								node_sets.remove(other_node_set);

								// adds its elements to the set of the 1st
								// node
								node_set.addAll(other_node_set);

								// quits the loop
								break;
							}
					}

					// quits the loop
					break;
				}

			// if the set of sets of nodes has only one element, quits
			// the
			// loop
			if (node_sets.size() == 1)
				break;
		}

		// mounts and returns the answer of the method
		Node[] answer_nodes = new Node[node_copies.size()];
		int i = 0;
		for (Node node : node_copies) {
			answer_nodes[i] = node;
			i++;
		}

		return new Graph("MWST", answer_nodes);
	}

	/**
	 * Returns the minimum (M) weight (W) perfect (P) matching (M) of the graph,
	 * in terms of edges, related to the given nodes.
	 * 
	 * @param nodes
	 *            The nodes to be considered in the matching.
	 * @param ignored_edges
	 *            The edges to be ignored in the matching constitution.
	 * @return The edges constituting the matching.
	 */
	private Edge[] getMWPM(Node[] nodes, Edge[] ignored_edges) {
		// adds the given nodes to a set
		HashSet<Node> given_nodes_set = new HashSet<Node>();
		for (int i = 0; i < nodes.length; i++)
			given_nodes_set.add(nodes[i]);

		// adds the given edges to be ignored to a set
		HashSet<Edge> ignored_edges_set = new HashSet<Edge>();
		for (int i = 0; i < ignored_edges.length; i++)
			ignored_edges_set.add(ignored_edges[i]);

		// holds the edges to be answered by this method (proper edges)
		HashSet<Edge> proper_edges = new HashSet<Edge>();

		// list that holds the sets of possible edges connecting the given
		// nodes among themselves
		LinkedList<HashSet<Edge>> possible_edges = new LinkedList<HashSet<Edge>>();

		// for each given node
		for (int i = 0; i < nodes.length; i++) {
			// obtains the current given node
			Node current_given_node = nodes[i];

			// obtains the edges of the current node
			Edge[] current_given_node_edges = current_given_node.getEdges();

			// creates a set of edges for the current node
			HashSet<Edge> current_given_node_edges_set = new HashSet<Edge>();

			// for each edge of the current given node
			for (int j = 0; j < current_given_node_edges.length; j++) {
				// obtains the current edge
				Edge current_edge = current_given_node_edges[j];

				// if such edge is not in the set of ignored edges
				if (!ignored_edges_set.contains(current_edge))
					// if the other node of such edge is among the given ones
					if (given_nodes_set.contains(current_edge
							.getOtherNode(current_given_node)))
						// adds the current edge to the set of possible edges of
						// the current node
						current_given_node_edges_set.add(current_edge);
			}

			// adds the set of possible edges to the proper list
			possible_edges.add(current_given_node_edges_set);
		}

		// holds the already treated nodes
		HashSet<Node> treated_nodes = new HashSet<Node>();

		// while the "treated nodes" set is not equals to the "given
		// nodes" set
		// used for set cardinality control
		int considered_cardinality = 0;
		while (treated_nodes.size() != given_nodes_set.size()) {
			// holds the lists of possible edges that have the current
			// considered cardinality
			LinkedList<HashSet<Edge>> current_considered_edges_list = new LinkedList<HashSet<Edge>>();

			// for each set of possible edges
			for (HashSet<Edge> current_set : possible_edges)
				if (current_set != null
						&& current_set.size() == considered_cardinality)
					current_considered_edges_list.add(current_set);

			// if the current considered edges list is empty
			if (current_considered_edges_list.isEmpty())
				// increases the current considered cardinality
				considered_cardinality++;
			// else, if the current cardinality is zero
			else if (considered_cardinality == 0)
				// for each current considered set
				for (HashSet<Edge> current_edges_set : current_considered_edges_list) {
					// obtains the position of such set in the list of possible
					// edges
					int pos = possible_edges.indexOf(current_edges_set);

					// nullifies the list of possible edges in such position
					possible_edges.set(pos, null);

					// adds the correspondent given node to the treated
					// nodes
					treated_nodes.add(nodes[pos]);
				}
			// else...
			else {
				// obtains the edge with the smallest length among the current
				// considered sets of edges
				Edge smallest_edge = null;
				double smallest_length = Double.MAX_VALUE;

				// for each current considered set
				for (HashSet<Edge> current_edges_set : current_considered_edges_list)
					// for each edge of such current set
					for (Edge current_edge : current_edges_set)
						// if the current edge is smaller than the smallest
						// found one
						if (current_edge.getLength() < smallest_length) {
							// updates the information
							smallest_edge = current_edge;
							smallest_length = current_edge.getLength();
						}

				// adds the current edge to the proper edges
				proper_edges.add(smallest_edge);

				// obtains the nodes of the smallest edge
				Node[] smallest_edge_nodes = smallest_edge.getNodees();

				// adds them to the treated ones
				treated_nodes.add(smallest_edge_nodes[0]);
				treated_nodes.add(smallest_edge_nodes[1]);

				// removes all the possible edges that connect at least one of
				// such nodes
				// for each set of possible edges
				for (HashSet<Edge> current_possible_edges_set : possible_edges)
					// if the current possible edges set is not null
					if (current_possible_edges_set != null) {
						// holds the edges to be removed
						HashSet<Edge> to_remove_edges = new HashSet<Edge>();

						// for each edge in it
						for (Edge current_possible_edge : current_possible_edges_set)
							// if one of the nodes of the smallest edge is
							// connected by such edge
							if (smallest_edge_nodes[0]
									.isEmitterOf(current_possible_edge)
									|| smallest_edge_nodes[0]
											.isCollectorOf(current_possible_edge)
									|| smallest_edge_nodes[1]
											.isEmitterOf(current_possible_edge)
									|| smallest_edge_nodes[1]
											.isCollectorOf(current_possible_edge))
								// marks the current edge to be removed
								to_remove_edges.add(current_possible_edge);

						// removes the marked edges
						current_possible_edges_set.removeAll(to_remove_edges);
					}

				// sets the current considered cardinality to zero
				considered_cardinality = 0;
			}
		}

		// mounts the answer of the method
		Edge[] answer = new Edge[proper_edges.size()];
		int i = 0;
		for (Edge edge : proper_edges) {
			answer[i] = edge;
			i++;
		}

		// returns such answer
		return answer;
	}

	/**
	 * Tries to return an hamiltonian cycle (i.e. a sequence of nodes).
	 * Ignores the orientation of the edges. Constructed to support the
	 * algorithm based on Christophide's to solve the TSP problem. It must be
	 * called from the minimum-weight spanning tree mixed with the
	 * minimum-weight perfect matching. Additionally, the original complete
	 * graph must be passed as an argument.
	 * 
	 * @param original_graph
	 *            The original complete graph, from where the minimum-weight
	 *            spanning tree was obtained.
	 * @return A try for an hamiltonian cycle.
	 */
	private Node[] getHamiltonianCycle(Graph original_graph) {
		// holds the answer for this method
		LinkedList<Node> hamiltonian_cycle = new LinkedList<Node>();

		// holds the ids of the already treated nodes
		LinkedList<String> treated_nodes_ids = new LinkedList<String>();

		// holds the possible expansions for each treated node
		LinkedList<HashSet<Node>> possible_expansions = new LinkedList<HashSet<Node>>();

		// obtains a node from this graph, to start walking
		Node current_node = (Node) this.nodes.toArray()[0];

		// registers the last position where the hamiltonian cycle was
		// broken and turned back
		int turn_back_pos = -1;

		// while true...
		while (true) {
			// adds the current node to the hamiltonian cycle
			hamiltonian_cycle.add(current_node);

			// for the current node
			String current_node_id = current_node.getObjectId();
			if (!treated_nodes_ids.contains(current_node_id)) {
				// adds its id in the already treated ones
				treated_nodes_ids.add(current_node_id);

				// creates a set to hold its possible expansions
				possible_expansions.add(new HashSet<Node>());

				// removes it from the sets of possible expansions of the other
				// nodes
				for (HashSet<Node> expansion_set : possible_expansions)
					expansion_set.remove(current_node);
			}

			// expands the current node
			// obtains its edges
			Edge[] current_node_edges = current_node.getEdges();

			// holds the chosen next node
			Node next_node = null;

			// holds the possible next nodes
			LinkedList<Node> possible_next_nodes = new LinkedList<Node>();

			// for each edge
			for (int i = 0; i < current_node_edges.length; i++) {
				// obtains the current edge
				Edge current_edge = current_node_edges[i];

				// if the other node of such edge was not treated yet
				Node other_node = current_edge
						.getOtherNode(current_node);
				if (!treated_nodes_ids.contains(other_node.getObjectId()))
					// adds it as a possible next node
					possible_next_nodes.add(other_node);
			}

			// if the possible next nodes set is empty
			if (possible_next_nodes.isEmpty()) {
				// if all the nodes were already put in the solution
				if (treated_nodes_ids.size() == this.nodes.size()) {
					// finds in the original graph, the Dijkstra's path to reach
					// the first node of the hamiltonian cycle from the
					// current one
					Graph dijkstra_path = original_graph.getDijkstraPath(
							current_node, hamiltonian_cycle.getFirst());

					// adds the nodes of such path to the hamiltonian cycle
					Node[] dijkstra_path_nodes = dijkstra_path
							.getNodees();
					HashSet<Edge> considered_dijkstra_edges = new HashSet<Edge>();

					Node dijkstra_path_node = null;
					for (int i = 0; i < dijkstra_path_nodes.length; i++) {
						dijkstra_path_node = dijkstra_path_nodes[i];

						if (dijkstra_path_node.equals(current_node))
							break;
					}

					do {
						Edge[] dijkstra_path_edges = dijkstra_path_node
								.getEdges();

						if (considered_dijkstra_edges
								.contains(dijkstra_path_edges[0])) {
							dijkstra_path_node = dijkstra_path_edges[1]
									.getOtherNode(dijkstra_path_node);
							considered_dijkstra_edges
									.add(dijkstra_path_edges[1]);
						} else {
							dijkstra_path_node = dijkstra_path_edges[0]
									.getOtherNode(dijkstra_path_node);
							considered_dijkstra_edges
									.add(dijkstra_path_edges[0]);
						}

						hamiltonian_cycle.add(this
								.getNode(dijkstra_path_node.getObjectId()));
					} while (dijkstra_path_node.getDegree() > 1);

					// quits the loop
					break;
				}
				// else
				else {
					// obtains the original version of the current node
					Node original_current_node = original_graph
							.getNode(current_node.getObjectId());

					// obtains the edges of such node
					Edge[] original_edges = original_current_node.getEdges();

					// mounts a heap with such edges, based on their lengths
					MinimumHeap heap = new MinimumHeap(original_edges);

					// holds the original version of the next node
					Node original_next_node = null;

					// tries to set such node
					while (!heap.isEmpty()) {
						Node current_original_next_node_candidate = ((Edge) heap
								.removeSmallest())
								.getOtherNode(original_current_node);

						if (!treated_nodes_ids
								.contains(current_original_next_node_candidate
										.getObjectId())) {
							original_next_node = current_original_next_node_candidate;
							break;
						}
					}

					// if the original next node is valid, obtains its copy
					// version
					if (original_next_node != null)
						next_node = this.getNode(original_next_node
								.getObjectId());
					// else, tries to set the next node as one of the nodes
					// to be expanded (the answer is no more an hamiltonian
					// cycle)
					else {
						for (int i = 0; i < current_node_edges.length; i++) {
							// obtains the current edge
							Edge current_edge = current_node_edges[i];

							// obtains the other node of such edge
							Node other_node = current_edge
									.getOtherNode(current_node);

							// if such other node still can be expanded, sets
							// it as the next node
							int set_pos = treated_nodes_ids
									.indexOf(other_node.getObjectId());
							HashSet<Node> other_node_expansion_set = possible_expansions
									.get(set_pos);
							if (other_node_expansion_set.size() > 0) {
								next_node = other_node;
								break;
							}
						}

						// if the next node is still null
						if (next_node == null)
							// if it's possible to turn back
							if (turn_back_pos > -1) {
								// sets the next node
								next_node = hamiltonian_cycle
										.get(turn_back_pos);

								// decreases the turn back registered position
								turn_back_pos--;
							}
					}
				}
			}
			// else if the current node has more than one possible next node
			else if (possible_next_nodes.size() > 1) {
				// obtains the set that holds the possible expansions for the
				// current node
				int set_pos = treated_nodes_ids.indexOf(current_node
						.getObjectId());
				HashSet<Node> current_node_expansion_set = possible_expansions
						.get(set_pos);

				// holds the smallest degree found among the possible next
				// nodes
				double smallest_degree = Double.MAX_VALUE;

				// for each candidate node
				for (Node next_node_candidate : possible_next_nodes) {
					// adds it in the expansion set of the current node
					current_node_expansion_set.add(next_node_candidate);

					// sets the next node as the one with the smallest degree
					double current_degree = next_node_candidate.getDegree();

					if (current_degree < smallest_degree) {
						smallest_degree = current_degree;
						next_node = next_node_candidate;
					}
				}

				// configures the turn back position
				turn_back_pos = hamiltonian_cycle.size() - 1;
			}
			// else
			else {
				// sets the next node as the unique one
				next_node = possible_next_nodes.getFirst();

				// configures the turn back position
				turn_back_pos = hamiltonian_cycle.size() - 1;
			}

			// if the next node is valid
			if (next_node != null)
				// sets it as the current one
				current_node = next_node;
			// else, quits the loop (it is not possible to walk anymore)
			else
				break;
		}

		// mounts the answer of the method
		Node[] answer = new Node[hamiltonian_cycle.size()];
		int i = 0;
		for (Node node : hamiltonian_cycle) {
			answer[i] = node;
			i++;
		}

		return answer;
	}

	/**
	 * Tries to returns a solution for the TSP problem, in terms of a sequence
	 * of nodes. It is based on the Christophide's algorithm. Ignores the
	 * orientation of the nodes.
	 * 
	 * @return The sequence of nodes that try to represent a TSP solution.
	 */
	public Node[] getTSPSolution() {
		// obtains the minimum weight spanning tree
		Graph mws_tree = this.getMWST();

		// obtains the edges of such tree
		Edge[] mws_tree_edges = mws_tree.getEdges();
		for (int i = 0; i < mws_tree_edges.length; i++)
			mws_tree_edges[i] = this.getEdge(mws_tree_edges[i].getObjectId());

		// obtains the nodes of such tree that have odd degree
		HashSet<Node> odd_degree_nodes_set = new HashSet<Node>();

		Node[] mws_tree_nodes = mws_tree.getNodees();
		for (int i = 0; i < mws_tree_nodes.length; i++) {
			Node current_node = mws_tree_nodes[i];

			if (current_node.getDegree() % 2 > 0)
				odd_degree_nodes_set.add(current_node);
		}

		Node[] odd_degree_nodes = new Node[odd_degree_nodes_set
				.size()];
		int i = 0;
		for (Node odd_degree_node : odd_degree_nodes_set) {
			odd_degree_nodes[i] = this.getNode(odd_degree_node
					.getObjectId());
			i++;
		}

		// obtains the edges that must be added to the mws tree in order to help
		// to find a TSP solution
		Edge[] additional_edges = this.getMWPM(odd_degree_nodes,
				mws_tree_edges);

		// adds such edges to the mws tree
		for (i = 0; i < additional_edges.length; i++) {
			// obtains the current additional edge
			Edge current_add_edge = additional_edges[i];

			// finds the nodes of the current edge in the tree
			Node[] current_add_nodes = current_add_edge.getNodees();
			Node node_1 = null;
			Node node_2 = null;

			for (int j = 0; j < mws_tree_nodes.length; j++) {
				Node current_tree_node = mws_tree_nodes[j];

				if (current_tree_node.equals(current_add_nodes[0]))
					node_1 = current_tree_node;
				else if (current_tree_node.equals(current_add_nodes[1]))
					node_2 = current_tree_node;

				if (node_1 != null && node_2 != null)
					break;
			}

			// adds the current edge to the (no-more) tree
			Edge current_add_edge_copy = current_add_edge.getCopy(node_1,
					node_2);
			mws_tree.edges.add(current_add_edge_copy);
		}

		// returns the hamiltonian candidate cycle
		return mws_tree.getHamiltonianCycle(this);
	}

	/**
	 * Tries to returns a solution for the TSP problem, in terms of a sequence
	 * of nodes. It is based on the Christophide's algorithm. Ignores the
	 * orientation of the nodes.
	 * 
	 * Just another try... ;)
	 * 
	 * @return The sequence of nodes that try to represent a TSP solution.
	 */
	public Node[] getTSPSolution2() {
		// obtains the minimum weight spanning tree
		Graph mws_tree = this.getMWST();

		// the current degree to be treated by this algorithm
		int current_degree = 1;

		// while true...
		while (true) {
			// holds the number of node of the mws tree that have degree
			// smaller than the currently considered degree
			int small_degree_count = 0;

			// obtains, in a list, all the nodes that have the current degree
			LinkedList<Node> current_mws_nodes = new LinkedList<Node>();
			for (Node current_mws_node : mws_tree.getNodees()) {
				// holds the degree of the current node
				int current_mws_node_degree = current_mws_node.getDegree();

				// if such degree is equals to the current considered one, adds
				// the current node to the list of currently considered
				// nodes
				if (current_mws_node_degree == current_degree)
					current_mws_nodes.add(current_mws_node);

				// else, if such degree is smaller than the current considered
				// one, increases the number of nodes of the mws tree that
				// have degree smaller than the currently considered degree
				else if (current_mws_node_degree < current_degree)
					small_degree_count++;
			}

			// if the the number of node of the mws tree that have degree
			// smaller than the current considered degree is equals to the
			// number of node of the mws tree, quits the loop
			if (small_degree_count == mws_tree.getNodees().length)
				break;

			// if the current considered degree is 1...
			if (current_degree == 1) {
				// for each node, tries to combine them 2 by 2, based on their
				// connecting edges
				LinkedList<Node> nodes_indexes = new LinkedList<Node>();
				LinkedList<HashSet<Edge>> connecting_possibilities = new LinkedList<HashSet<Edge>>();

				for (Node current_mws_node : current_mws_nodes) {
					nodes_indexes.add(current_mws_node);
					connecting_possibilities.add(new HashSet<Edge>());
				}

				for (Node current_node : current_mws_nodes)
					for (Node other_node : current_mws_nodes)
						if (!current_node.equals(other_node)) {
							// obtains the current node and the other
							// node from this graph
							Node current_node_org = this
									.getNode(current_node.getObjectId());
							Node other_node_org = this
									.getNode(other_node.getObjectId());

							// if the two nodes are connected by an edge
							// that is not part of the mws tree, registers
							// it
							Edge[] connecting_edges = current_node_org
									.getConnectingEdges(other_node_org);
							if (connecting_edges.length > 0) {
								Edge connecting_edge = connecting_edges[0];

								if (mws_tree.getEdge(connecting_edge
										.getObjectId()) == null) {
									int index = nodes_indexes
											.indexOf(current_node);
									connecting_possibilities.get(index).add(
											connecting_edge);
								}
							}
						}

				// holds the already treated nodes
				HashSet<Node> treated_nodes = new HashSet<Node>();

				// holds the cardinality being currently considered related to
				// connecting possibilities
				int current_cardinality = 0;

				// while the number of treated nodes is smaller than the
				// number of nodes currently considered
				while (treated_nodes.size() < current_mws_nodes.size()) {
					// holds the index of the current node
					int node_index = -1;

					// for each item of the connecting possibilities
					for (HashSet<Edge> possible_edges : connecting_possibilities) {
						node_index++;

						// if such set has the current cardinality
						if (possible_edges.size() == current_cardinality)
							if (current_cardinality == 0)
								// adds the current node to the treated ones
								treated_nodes.add(nodes_indexes
										.get(node_index));

							else {
								// obtains the smallest edge from the current
								// set of
								// possible edges
								Edge smallest_edge = null;
								double smallest_length = Double.MAX_VALUE;

								for (Edge current_edge : possible_edges)
									if (current_edge.getLength() < smallest_length) {
										smallest_edge = current_edge;
										smallest_length = current_edge
												.getLength();
									}

								// obtains the two nodes of such edge
								Node[] smallest_edge_nodes = smallest_edge
										.getNodees();

								// obtains the nodes from the mws tree
								// related to
								// such edge
								Node node_1 = mws_tree
										.getNode(smallest_edge_nodes[0]
												.getObjectId());
								Node node_2 = mws_tree
										.getNode(smallest_edge_nodes[1]
												.getObjectId());

								// adds a copy of the smallest edge to the mws
								// tree
								Edge smallest_edge_copy = smallest_edge
										.getCopy(node_1, node_2);
								mws_tree.edges.add(smallest_edge_copy);

								// removes, from the possible edges, all the
								// edges that are related to the two
								// nodes
								for (HashSet<Edge> edges_set : connecting_possibilities) {
									// holds the edges to be removed
									HashSet<Edge> to_remove_edges = new HashSet<Edge>();

									for (Edge edge : edges_set) {
										Node[] nodes = edge.getNodees();

										if (nodes[0]
												.equals(smallest_edge_nodes[0])
												|| nodes[0]
														.equals(smallest_edge_nodes[1])
												|| nodes[1]
														.equals(smallest_edge_nodes[0])
												|| nodes[1]
														.equals(smallest_edge_nodes[1]))
											to_remove_edges.add(edge);
									}

									// removes the edges
									edges_set.removeAll(to_remove_edges);
								}

								// decreases the current cardinality to -1
								current_cardinality = -1;

								// quits the loop
								break;
							}
					}

					// increases the considered cardinality
					current_cardinality++;
				}

				// for the current nodes that still have degree one
				for (Node current_mws_node : current_mws_nodes)
					if (current_mws_node.getDegree() == 1) {
						// obtains the node from the mws tree to which the
						// current node is connected
						Node other_mws_node = current_mws_node.getEdges()[0]
								.getOtherNode(current_mws_node);

						// obtains the current and the other node from this
						// graph
						Node current_mws_node_org = this
								.getNode(current_mws_node.getObjectId());
						Node other_mws_node_org = this
								.getNode(other_mws_node.getObjectId());

						// obtains the neighborhood of the other node and adds
						// it to a set
						HashSet<Node> other_node_neighborhood = new HashSet<Node>();
						Node[] other_node_neighbors = other_mws_node_org
								.getNeighbourhood();
						for (Node neighbor : other_node_neighbors)
							other_node_neighborhood.add(neighbor);

						// holds the candidate edges to establish an alternative
						// route
						HashSet<Edge> candidate_edges = new HashSet<Edge>();

						// for each edge of the current node
						Edge[] current_node_edges = current_mws_node_org
								.getEdges();
						for (Edge current_edge : current_node_edges)
							// if the other node of the current edge is in the
							// neighborhood of the other node
							if (other_node_neighborhood.contains(current_edge
									.getOtherNode(current_mws_node_org)))
								// adds the current edge as a candidate one
								candidate_edges.add(current_edge);

						// takes the smallest edge among the candidate ones
						double smallest_length = Double.MAX_VALUE;
						Edge smallest_edge = null;

						for (Edge edge : candidate_edges)
							if (edge.getLength() < smallest_length) {
								smallest_edge = edge;
								smallest_length = edge.getLength();
							}

						// removes the edge connecting the other node of the
						// mws tree to the other node of the chosen edge
						if (smallest_edge != null) {
							Node third_mws_node = mws_tree
									.getNode(smallest_edge.getOtherNode(
											current_mws_node_org)
											.getObjectId());
							Edge[] to_be_removed_edge = other_mws_node
									.getConnectingEdges(third_mws_node);
							if (to_be_removed_edge.length > 0) {
								mws_tree.edges.remove(to_be_removed_edge[0]);
								to_be_removed_edge[0].disconnect();
							}

							// adds the smallest edge to the mws tree
							Edge smallest_edge_copy = smallest_edge.getCopy(
									current_mws_node, third_mws_node);
							mws_tree.edges.add(smallest_edge_copy);
						}
					}

				// increases the current degree
				current_degree++;
			}

			// else if it is 2...
			else if (current_degree == 2)
				// do nothing, just increase the considered degree count
				current_degree++;

			// else...
			else {
				// obtains the current node that has the biggest edges
				Node to_treat_mws_node = null;
				double biggest_edges_legth_sum = Double.MAX_VALUE * -1;

				for (Node current_mws_node : current_mws_nodes) {
					// obtains the edges of the current node
					Edge[] current_mws_node_edges = current_mws_node
							.getEdges();

					// holds the sum of the lengths of such edges
					double current_edges_length_sum = 0;
					for (Edge current_edge : current_mws_node_edges)
						current_edges_length_sum = current_edges_length_sum
								+ current_edge.getLength();

					// if such sum is bigger than the last recorded one, updates
					// everything
					if (current_edges_length_sum > biggest_edges_legth_sum) {
						to_treat_mws_node = current_mws_node;
						biggest_edges_legth_sum = current_edges_length_sum;
					}
				}

				// for such node, sorts its edges based on their length
				LinkedList<Edge> sorted_edges = new LinkedList<Edge>();

				for (Edge current_edge : to_treat_mws_node.getEdges()) {
					for (int i = 0; i < sorted_edges.size(); i++)
						if (sorted_edges.get(i).getLength() < current_edge
								.getLength()) {
							sorted_edges.add(i, current_edge);
							break;
						}

					if (!sorted_edges.contains(current_edge))
						sorted_edges.add(current_edge);
				}

				// tries to substitute a pair of edges of such list by another
				// one, on the mws tree
				for (Edge current_edge : sorted_edges) {
					for (Edge other_edge : sorted_edges)
						if (!current_edge.equals(other_edge)) {
							// obtains the original node from this graph that
							// is equivalent to the treated one
							Node to_treat_mws_node_org = this
									.getNode(to_treat_mws_node
											.getObjectId());

							// obtains the original edges from this graph
							Edge current_edge_org = this.getEdge(current_edge
									.getObjectId());
							Edge other_edge_org = this.getEdge(other_edge
									.getObjectId());

							// obtains the two nodes that are connected to
							// the treated one
							Node node_1 = current_edge_org
									.getOtherNode(to_treat_mws_node_org);
							Node node_2 = other_edge_org
									.getOtherNode(to_treat_mws_node_org);

							// if such nodes are connected one to the other,
							// obtains the edge connecting them
							Edge connecting_edge = null;
							Edge[] connecting_edges = node_1
									.getConnectingEdges(node_2);
							if (connecting_edges.length > 0)
								connecting_edge = connecting_edges[0];
							connecting_edges = null;

							// if such edge is not in the mws tree yet
							if (connecting_edge != null
									&& mws_tree.getEdge(connecting_edge
											.getObjectId()) == null) {
								// adds such edge to the mws tree
								Edge connecting_edge_copy = connecting_edge
										.getCopy(node_1, node_2);
								mws_tree.edges.add(connecting_edge_copy);

								// removes the current edge and other edge from
								// the mws tree
								current_edge.disconnect();
								other_edge.disconnect();
								mws_tree.edges.remove(current_edge);
								mws_tree.edges.remove(other_edge);

								// reduces the current considered degree to 0
								current_degree = 0;

								// breaks the loop
								break;
							}
						}

					// if the current degree was reduced to one, breaks the loop
					if (current_degree == 0)
						break;
				}

				// increases the current degree
				current_degree++;
			}
		}

		// returns the hamiltonian candidate cycle
		return mws_tree.getHamiltonianCycle(this);
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
		for (Node node : this.nodes)
			if (node.getObjectId().equals(id))
				return node;

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
		if (this.edges != null)
			for (Edge edge : this.edges)
				if (edge.getObjectId().equals(id))
					return edge;

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

			Node[] nodes = graph.getNodees();
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
	
	public String fullToXML(int identation) {
		StringBuffer buffer = new StringBuffer();

		// applies the identation
		for (int i = 0; i < identation; i++)
			buffer.append("\t");

		buffer.append("<graph label=\"" + this.label + "\">\n");

		// inserts the nodes
		for (Node node : this.nodes) {
			buffer.append(node.fullToXML(identation + 1));
		}

		// inserts the edges
		for (Edge edge : this.edges)
			buffer.append(edge.fullToXML(identation + 1));

		// applies the identation
		for (int i = 0; i < identation; i++)
			buffer.append("\t");
		buffer.append("</graph>\n");

		return buffer.toString();
	}
	
}

/**
 * Internal class that holds, for a specific node, its distance to others kept
 * in a list.
 */
final class DistancesList {
	/** The node of which distances are held. */
	public final Node NODE;

	/** The list of nodes and their respective distances. */
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
			double[] bound_idlenesses = graph.getSmallestAndBiggestIdlenesses();

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