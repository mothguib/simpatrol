/* Graph.java */

/* The package of this class. */
package model.graph;

/* Imported classes and/or interfaces. */
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import util.data_structures.Comparable;
import util.data_structures.MinimumHeap;
import view.XMLable;
import model.interfaces.Dynamic;
import model.stigma.Stigma;

/**
 * Implements graphs that represent the territories to be patrolled.
 * 
 * @developer New dynamic objects that eventually are part of a graph, must
 *            change this class.
 */
public final class Graph implements XMLable {
	/* Attributes. */
	/** The label of the graph. */
	private String label;

	/** The set of nodes of the graph. */
	private Set<Node> nodes;

	/** The set of edges of the graph. */
	private Set<Edge> edges;

	/** The set of stigmas deposited on the graph. */
	private Set<Stigma> stigmas;

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
				this.edges.add(current_edges[i]);
		}

		if (this.edges.size() == 0)
			this.edges = null;

		this.stigmas = null;
	}

	/**
	 * Obtains the nodes of the graph.
	 * 
	 * @return The nodes of the graph.
	 */
	public Node[] getNodes() {
		return this.nodes.toArray(new Node[0]);
	}

	/**
	 * Obtains the edges of the graph.
	 * 
	 * @return The edges of the graph.
	 */
	public Edge[] getEdges() {
		if (this.edges != null)
			return this.edges.toArray(new Edge[0]);

		return new Edge[0];
	}

	/**
	 * Returns the stigmas of the graph.
	 * 
	 * @return The stigmas deposited on the graph.
	 */
	public Stigma[] getStigmas() {
		if (this.stigmas != null)
			return this.stigmas.toArray(new Stigma[0]);

		return new Stigma[0];
	}

	/**
	 * Adds the given stigma to the graph.
	 * 
	 * @param stigma
	 *            The stigma to be added to the graph.
	 */
	public void addStigma(Stigma stigma) {
		if (this.stigmas == null)
			this.stigmas = Collections.synchronizedSet(new HashSet<Stigma>());
		this.stigmas.add(stigma);
	}

	/**
	 * Obtains the label of the graph.
	 * 
	 * @return The label of the graph.
	 */
	public String getLabel() {
		return this.label;
	}
	
	public void setLabel(String label) {
		this.label = label;
	}
	/**
	 * Obtains the dynamic objects of the graph.
	 * 
	 * @return The dynamic objects.
	 * @developer New dynamic objects that eventually are part of a graph, must
	 *            change this method.
	 */
	public Dynamic[] getDynamicObjects() {
		// the set of dynamic objects
		Set<Dynamic> dynamic_objects = new HashSet<Dynamic>();

		// searches for dynamic nodes
		for (Node node : this.nodes)
			if (node instanceof Dynamic)
				dynamic_objects.add((Dynamic) node);

		// searches for dynamic edges
		if (this.edges != null)
			for (Edge edge : this.edges)
				if (edge instanceof Dynamic)
					dynamic_objects.add((Dynamic) edge);

		// developer: new dynamic objects must be searched here

		// returns the answer
		return dynamic_objects.toArray(new Dynamic[0]);
	}

	/**
	 * Verifies if the given node is part of the graph.
	 * 
	 * @param node
	 *            The node to be verified.
	 * @return TRUE if the node is part of the graph, FALSE if not.
	 * @see XMLable
	 */
	public boolean hasNode(Node node) {
		if (this.nodes.contains(node))
			return true;
		else
			for (Node compared_node : this.nodes)
				// uses XMLable's "equals" method (based on objects' ids)
				if (node.equals(compared_node))
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
			if (this.edges.contains(edge))
				return true;
			else
				for (Edge compared_edge : this.edges)
					// uses XMLable's "equals" method (based on objects' ids)
					if (edge.equals(compared_edge))
						return true;
		}

		return false;
	}

	/**
	 * Obtains a subgraph from the graph, starting from the given node and
	 * walking in depth-first mode, until the given depth is reached.
	 * 
	 * Only the enabled and visible elements (nodes and edges) are added to
	 * the subgraph.
	 * 
	 * If the given depth is set to -1, the entire visible and enabled graph's
	 * reaching elements are returned.
	 * 
	 * @param node
	 *            The starting point to obtain the subgraph.
	 * @param depth
	 *            The depth to reach when walking in depth-first mode.
	 * @return A subgraph starting from the given node and with the given
	 *         depth.
	 */
	public Graph getVisibleEnabledSubgraph(Node node, int depth) {
		// if the given depth is -1, returns the entire visible graph
		if (depth == -1)
			return this.getVisibleEnabledGraph(node);

		// if the given starting node is not visible or enabled, returns null
		if (!node.isVisible()
				|| (node instanceof DynamicNode && !((DynamicNode) node)
						.isEnabled()))
			return null;

		// the answer for the method
		Node[] starting_node_set = { node.getCopy() };
		Graph answer = new Graph(this.label, starting_node_set);
		answer.edges = new HashSet<Edge>();

		// expands the answer until the given depth is reached
		this.addVisibleEnabledDepth(answer, node, depth,
				new HashSet<Node>());

		// if there are no edges in the answer, nullifies its set of edges
		if (answer.edges.size() == 0)
			answer.edges = null;

		// adds the stigmas to the answer (only the correct ones)
		if (this.stigmas != null)
			// for each stigma
			for (Stigma stigma : this.stigmas) {
				// tries to obtain its node
				Node stigma_node = stigma.getNode();

				// if the node is valid (not null)
				if (stigma_node != null) {
					// tries to obtain a copy of it from the answer
					Node stigma_node_copy = answer.getNode(stigma_node
							.getObjectId());

					// if the copy is valid, adds a copy of the stigma to the
					// answer
					if (stigma_node_copy != null)
						answer.addStigma(stigma.getCopy(stigma_node_copy));
				}
				// if not, obtains its edge
				else {
					Edge stigma_edge = stigma.getEdge();

					// tries to obtain a copy of such edge from the answer
					Edge stigma_edge_copy = answer.getEdge(stigma_edge
							.getObjectId());

					// if the copy is valid, adds a copy of the stigma to
					// the answer
					if (stigma_edge_copy != null)
						answer.addStigma(new Stigma(stigma_edge_copy));
				}
			}

		// returns the answer
		return answer;
	}

	/**
	 * Obtains a subgraph from the graph, starting from the given node and
	 * walking in depth-first mode, until the given depth is reached.
	 * 
	 * Only the enabled elements (nodes and edges, visible or not) are added
	 * to the subgraph.
	 * 
	 * If the given depth is set to -1, the entire enabled graph's reaching
	 * elements are returned.
	 * 
	 * @param node
	 *            The starting point to obtain the subgraph.
	 * @param depth
	 *            The depth to reach when walking in depth-first mode.
	 * @return A subgraph starting from the given node and with the given
	 *         depth.
	 */
	public Graph getEnabledSubgraph(Node node, int depth) {
		// if the given depth is -1, returns the entire enabled graph
		if (depth == -1)
			return this.getEnabledGraph(node);

		// if the given starting node is not enabled, returns null
		if (node instanceof DynamicNode
				&& !((DynamicNode) node).isEnabled())
			return null;

		// the answer for the method
		Node[] starting_node_set = { node.getCopy() };
		Graph answer = new Graph(this.label, starting_node_set);
		answer.edges = new HashSet<Edge>();

		// expands the answer until the given depth is reached
		this.addEnabledDepth(answer, node, depth, new HashSet<Node>());

		// if there are no edges in the answer, nullifies its set of edges
		if (answer.edges.size() == 0)
			answer.edges = null;

		// adds the stigmas to the answer (only the correct ones)
		if (this.stigmas != null)
			for (Stigma stigma : this.stigmas) {
				// tries to obtain its node
				Node stigma_node = stigma.getNode();

				// if the node is valid (not null)
				if (stigma_node != null) {
					// tries to obtain a copy of it from the answer
					Node stigma_node_copy = answer.getNode(stigma_node
							.getObjectId());

					// if the copy is valid, adds a copy of the stigma to the
					// answer
					if (stigma_node_copy != null)
						answer.addStigma(stigma.getCopy(stigma_node_copy));
				}
				// if not, obtains its edge
				else {
					Edge stigma_edge = stigma.getEdge();

					// tries to obtain a copy of it from the answer
					Edge stigma_edge_copy = answer.getEdge(stigma_edge
							.getObjectId());

					// if the copy is valid, adds a copy of the stigma to
					// the answer
					if (stigma_edge_copy != null)
						answer.addStigma(new Stigma(stigma_edge_copy));
				}
			}

		// returns the answer
		return answer;
	}

	/**
	 * Returns the minimum path between two given nodes, using the Dijkstra's
	 * algorithm.
	 * 
	 * Only the enabled elements (node and edges, visible or not) are
	 * considered to build the path.
	 * 
	 * @param begin_node
	 *            The first node of the desired path.
	 * @param end_node
	 *            The last node of the desired path.
	 * @return The minimum path between two given nodes.
	 */
	public Graph getEnabledDijkstraPath(Node begin_node, Node end_node) {
		// if one of the given nodes is not enabled, returns null
		if ((begin_node instanceof DynamicNode && !((DynamicNode) begin_node)
				.isEnabled())
				|| (end_node instanceof DynamicNode && !((DynamicNode) end_node)
						.isEnabled()))
			return null;

		// for each enabled node of the graph, correlates it with its distance
		// to the begin_node, as well as the last edge to reach it
		LinkedList<NodeDistanceEdge> nodes_with_distances_list = new LinkedList<NodeDistanceEdge>();

		for (Node node : this.nodes)
			if (!(node instanceof DynamicNode)
					|| ((DynamicNode) node).isEnabled())
				if (node.equals(begin_node))
					nodes_with_distances_list.add(new NodeDistanceEdge(
							node, 0, null));
				else
					nodes_with_distances_list.add(new NodeDistanceEdge(
							node, Double.MAX_VALUE, null));

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

			// obtains the neighbourhood where the current node is an source
			Node[] neighbourhood = current_node.getCollectorNeighbourhood();

			// for each node of the neighbourhood
			for (int i = 0; i < neighbourhood.length; i++) {
				// if the current neighbour is enabled
				if (!(neighbourhood[i] instanceof DynamicNode)
						|| ((DynamicNode) neighbourhood[i]).isEnabled()) {
					// obtains all the edges between the current node and the
					// current neighbour, of which source is the current node
					Edge[] edges = current_node
							.getConnectingOutEdges(neighbourhood[i]);

					// finds the smallest enabled edge
					Edge smallest_edge = null;
					double smallest_length = Double.MAX_VALUE;

					for (int j = 0; j < edges.length; j++)
						if (edges[j].is_enabled
								&& edges[j].getLength() < smallest_length) {
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
	 * Obtains the visible and enabled elements connected with the given
	 * starting node.
	 * 
	 * @param starting_node
	 *            The node to start walking into the graph in a breadth-first
	 *            manner.
	 * @return The obtained graph.
	 */
	private Graph getVisibleEnabledGraph(Node starting_node) {
		// if the starting node is not visible or is not enabled, returns null
		if (!starting_node.isVisible()
				|| (starting_node instanceof DynamicNode && !((DynamicNode) starting_node)
						.isEnabled()))
			return null;

		// holds the nodes to be treated
		List<Node> pending_nodes = new LinkedList<Node>();

		// holds the nodes already treated
		List<Node> expanded_nodes = new LinkedList<Node>();

		// the answer for the method
		Node[] initial_nodes = { starting_node.getCopy() };
		Graph answer = new Graph(this.label, initial_nodes);
		answer.edges = new HashSet<Edge>();

		// adds the starting node to the ones to be treated
		pending_nodes.add(starting_node);

		// while there are still nodes to treat
		while (pending_nodes.size() > 0) {
			// removes the current node from the ones to be treated
			Node current_node = pending_nodes.remove(0);

			// if it was not expanded yet
			if (!expanded_nodes.contains(current_node)) {
				// adds it to the expanded ones
				expanded_nodes.add(current_node);

				// obtains its copy from the answer
				Node current_node_copy = answer.getNode(current_node
						.getObjectId());

				// obtains its neighborhood
				Node[] neighbourhood = current_node.getNeighbourhood();

				// for each neighbor
				for (int i = 0; i < neighbourhood.length; i++) {
					// if the current neighbor is visible and is enabled
					if (neighbourhood[i].isVisible()
							&& (!(neighbourhood[i] instanceof DynamicNode) || ((DynamicNode) neighbourhood[i])
									.isEnabled())) {
						// if it is not in the already expanded ones
						if (!expanded_nodes.contains(neighbourhood[i])) {
							// tries to obtain a copy of it from the answer
							Node current_neighbour_copy = answer
									.getNode(neighbourhood[i].getObjectId());

							// registers if there's already a copy of the
							// current neighbour in the answer
							boolean neighbour_copy_exists = true;

							// if the copy is not valid, creates a new one
							if (current_neighbour_copy == null) {
								current_neighbour_copy = neighbourhood[i]
										.getCopy();
								neighbour_copy_exists = false;
							}

							// obtains all the edges between the current
							// node and its current neighbour
							Edge[] edges = current_node
									.getConnectingEdges(neighbourhood[i]);

							// registers if there's some visible and enabled
							// edge between the current node and its current
							// neighbor
							boolean visible_edge_exists = false;

							// for each edge
							for (int j = 0; j < edges.length; j++) {
								// if the current edge is visible and is enabled
								if (edges[j].isVisible() && edges[j].is_enabled) {
									visible_edge_exists = true;

									// obtains a copy of it
									Edge edge_copy = null;
									if (current_node.isSourceOf(edges[j]))
										edge_copy = edges[j].getCopy(
												current_node_copy,
												current_neighbour_copy);
									else
										edge_copy = edges[j].getCopy(
												current_neighbour_copy,
												current_node_copy);

									// adds to the answer
									answer.edges.add(edge_copy);
								}
							}

							// if there's some visible and enabled edge
							if (visible_edge_exists) {
								// if the current copy is not in the answer yet,
								// adds it
								if (!neighbour_copy_exists)
									answer.nodes.add(current_neighbour_copy);

								// adds the current copy to the pending ones
								pending_nodes.add(neighbourhood[i]);
							}
						}
					}
				}
			}
		}

		// if there are no edges in the answer, nullifies it
		if (answer.edges.size() == 0)
			answer.edges = null;

		// adds the stigmas to the answer (only the correct ones)
		if (this.stigmas != null)
			for (Stigma stigma : this.stigmas) {
				// tries to obtain its node
				Node stigma_node = stigma.getNode();

				// if the node is valid (not null)
				if (stigma_node != null) {
					// tries to obtain a copy of it from the answer
					Node stigma_node_copy = answer.getNode(stigma_node
							.getObjectId());

					// if the copy is valid, adds a copy of the stigma to the
					// answer
					if (stigma_node_copy != null)
						answer.addStigma(stigma.getCopy(stigma_node_copy));
				}
				// if not, obtains its edge
				else {
					Edge stigma_edge = stigma.getEdge();

					// tries to obtain a copy of it from the answer
					Edge stigma_edge_copy = answer.getEdge(stigma_edge
							.getObjectId());

					// if the copy is valid, adds a copy of the stigma to
					// the answer
					if (stigma_edge_copy != null)
						answer.addStigma(new Stigma(stigma_edge_copy));
				}
			}

		// returns the answer for the method
		return answer;
	}

	/**
	 * Obtains the enabled elements (visible or not) connected with the given
	 * starting node.
	 * 
	 * @param starting_node
	 *            The node to start walking into the graph in a breadth-first
	 *            manner.
	 * @return The obtained graph.
	 */
	private Graph getEnabledGraph(Node starting_node) {
		// if the starting node is not enabled, returns null
		if (starting_node instanceof DynamicNode
				&& !((DynamicNode) starting_node).isEnabled())
			return null;

		// holds the nodes to be treated
		List<Node> pending_nodes = new LinkedList<Node>();

		// holds the nodes already treated
		List<Node> expanded_nodes = new LinkedList<Node>();

		// the answer for the method
		Node[] initial_nodes = { starting_node.getCopy() };
		Graph answer = new Graph(this.label, initial_nodes);
		answer.edges = new HashSet<Edge>();

		// adds the starting node to the ones to be treated
		pending_nodes.add(starting_node);

		// while there are still nodes to treat
		while (pending_nodes.size() > 0) {
			// removes the current node from the ones to be treated
			Node current_node = pending_nodes.remove(0);

			// if it was not expanded yet
			if (!expanded_nodes.contains(current_node)) {
				// adds it to the expanded ones
				expanded_nodes.add(current_node);

				// obtains its copy from the answer
				Node current_node_copy = answer.getNode(current_node
						.getObjectId());

				// obtains its neighbourhood
				Node[] neighbourhood = current_node.getNeighbourhood();

				// for each neighbor
				for (int i = 0; i < neighbourhood.length; i++) {
					// if the current neighbor is enabled
					if (!(neighbourhood[i] instanceof DynamicNode)
							|| ((DynamicNode) neighbourhood[i]).isEnabled()) {
						// if it is not in the already expanded ones
						if (!expanded_nodes.contains(neighbourhood[i])) {
							// tries to obtain a copy of it from the answer
							Node current_neighbour_copy = answer
									.getNode(neighbourhood[i].getObjectId());

							// registers if there's already a copy of the
							// current neighbour in the answer
							boolean neighbour_copy_exists = true;

							// if the copy is not valid, creates a new one
							if (current_neighbour_copy == null) {
								current_neighbour_copy = neighbourhood[i]
										.getCopy();
								neighbour_copy_exists = false;
							}

							// obtains all the edges between the current
							// node and its current neighbor
							Edge[] edges = current_node
									.getConnectingEdges(neighbourhood[i]);

							// registers if there's some enabled edge between
							// the current node and its current neighbor
							boolean enabled_edge_exists = false;

							// for each edge
							for (int j = 0; j < edges.length; j++) {
								// if the current edge is enabled
								if (edges[j].isEnabled()) {
									enabled_edge_exists = true;

									// obtains a copy of it
									Edge edge_copy = null;
									if (current_node.isSourceOf(edges[j]))
										edge_copy = edges[j].getCopy(
												current_node_copy,
												current_neighbour_copy);
									else
										edge_copy = edges[j].getCopy(
												current_neighbour_copy,
												current_node_copy);

									// adds to the answer
									answer.edges.add(edge_copy);
								}
							}

							// if there's some enabled edge
							if (enabled_edge_exists) {
								// if the current copy is not in the answer yet,
								// adds it
								if (!neighbour_copy_exists)
									answer.nodes.add(current_neighbour_copy);

								// adds the current copy to the pending ones
								pending_nodes.add(neighbourhood[i]);
							}
						}
					}
				}
			}
		}

		// if there are no edges in the answer, nullifies it
		if (answer.edges.size() == 0)
			answer.edges = null;

		// adds the stigmas to the answer (only the correct ones)
		if (this.stigmas != null)
			// for each stigma
			for (Stigma stigma : this.stigmas) {
				// tries to obtain its node
				Node stigma_node = stigma.getNode();

				// if the node is valid (not null)
				if (stigma_node != null) {
					// tries to obtain a copy of it from the answer
					Node stigma_node_copy = answer.getNode(stigma_node
							.getObjectId());

					// if the copy is valid, adds a copy of the stigma to the
					// answer
					if (stigma_node_copy != null)
						answer.addStigma(stigma.getCopy(stigma_node_copy));
				}
				// if not, obtains its edge
				else {
					Edge stigma_edge = stigma.getEdge();

					// tries to obtain a copy of it from the answer
					Edge stigma_edge_copy = answer.getEdge(stigma_edge
							.getObjectId());

					// if the copy is valid, adds a copy of the stigma to
					// the answer
					if (stigma_edge_copy != null)
						answer.addStigma(new Stigma(stigma_edge_copy));
				}
			}

		// returns the answer for the method
		return answer;
	}

	/**
	 * Expands the given subgraph until the given depth is reached, in a
	 * depth-first recursive manner. The node where the expansion is started
	 * and a set of already expanded nodes must be informed.
	 * 
	 * Only the visible and enabled elements (nodes and edges) are
	 * respectively expanded and considered.
	 * 
	 * @param subgraph
	 *            The subgraph to be expanded.
	 * @param starting_node
	 *            The node where the expansion is started.
	 * @param depth
	 *            The depth limit for the expansion.
	 * @param already_expanded_nodes
	 *            The nodes not to be expanded.
	 */
	private void addVisibleEnabledDepth(Graph subgraph, Node starting_node,
			int depth, Set<Node> already_expanded_nodes) {
		// if the starting node is not visible or is not enabled, quits the
		// method
		if (!starting_node.isVisible()
				|| (starting_node instanceof DynamicNode && !((DynamicNode) starting_node)
						.isEnabled()))
			return;

		// if the depth is valid
		if (depth > -1) {
			// tries to obtain a copy of the starting node from the given
			// subgraph
			Node starting_node_copy = subgraph.getNode(starting_node
					.getObjectId());

			// if the copy is null, creates it and adds to the subgraph
			if (starting_node_copy == null) {
				starting_node_copy = starting_node.getCopy();
				subgraph.nodes.add(starting_node_copy);
			}

			// adds the starting node to the nodes already expanded
			already_expanded_nodes.add(starting_node);

			// if there's still depth to expand in the given subgraph
			if (depth > 0) {
				// obtains the neighborhood of the starting node
				Node[] neighbourhood = starting_node.getNeighbourhood();

				// for each node of the neighborhood
				for (int i = 0; i < neighbourhood.length; i++) {
					// if the current node is visible and enabled
					if (neighbourhood[i].isVisible()
							&& (!(neighbourhood[i] instanceof DynamicNode) || ((DynamicNode) neighbourhood[i])
									.isEnabled())) {
						// if it isn't in the nodes already expanded
						if (!already_expanded_nodes
								.contains(neighbourhood[i])) {
							// tries to obtain a copy of it from the given
							// subgraph
							Node neighbour_copy = subgraph
									.getNode(neighbourhood[i].getObjectId());

							// registers if there's already a copy of the
							// current neighbour in the given subgraph
							boolean neighbour_copy_exists = true;

							// if the copy is null, creates it
							if (neighbour_copy == null) {
								neighbour_copy = neighbourhood[i].getCopy();
								neighbour_copy_exists = false;
							}

							// obtains the edges between the starting node and
							// its current neighbor
							Edge[] edges = starting_node
									.getConnectingEdges(neighbourhood[i]);

							// registers if some of the connecting edges is
							// visible and enabled
							boolean visible_edge_exists = false;

							// for each edge
							for (int j = 0; j < edges.length; j++) {
								// if the current edge is visible and enabled
								if (edges[j].isVisible()
										&& edges[j].isEnabled()) {
									visible_edge_exists = true;

									// if there isn't a copy of it in the given
									// subgraph
									if (subgraph
											.getEdge(edges[j].getObjectId()) == null) {
										// creates the copy and adds to the
										// subgraph
										Edge current_edge_copy = null;
										if (starting_node
												.isSourceOf(edges[j]))
											current_edge_copy = edges[j]
													.getCopy(
															starting_node_copy,
															neighbour_copy);
										else
											current_edge_copy = edges[j]
													.getCopy(neighbour_copy,
															starting_node_copy);

										subgraph.edges.add(current_edge_copy);
									}
								}
							}

							// if there's some visible edge
							if (visible_edge_exists) {
								// if the copy of the current neighbour is not
								// in the subgraph, adds it
								if (!neighbour_copy_exists)
									subgraph.nodes.add(neighbour_copy);

								// calls this method recursively, starting from
								// the current neighbour
								this.addVisibleEnabledDepth(subgraph,
										neighbourhood[i], depth - 1,
										already_expanded_nodes);
							}
						}
					}
				}
			}
		}
	}

	/**
	 * Expands the given subgraph until the given depth is reached, in a
	 * depth-first recursive manner. The node where the expansion is started
	 * and a set of already expanded nodes must be informed.
	 * 
	 * Only the enabled elements (nodes and edges, visible or not) are
	 * respectively expanded and considered.
	 * 
	 * @param subgraph
	 *            The subgraph to be expanded.
	 * @param starting_node
	 *            The node where the expansion is started.
	 * @param depth
	 *            The depth limit for the expansion.
	 * @param already_expanded_nodes
	 *            The nodes not to be expanded.
	 */
	private void addEnabledDepth(Graph subgraph, Node starting_node,
			int depth, Set<Node> already_expanded_nodes) {
		// if the starting node is not enabled, quits the method
		if (starting_node instanceof DynamicNode
				&& !((DynamicNode) starting_node).isEnabled())
			return;

		// if the depth is valid
		if (depth > -1) {
			// tries to obtain a copy of the starting node from the given
			// subgraph
			Node starting_node_copy = subgraph.getNode(starting_node
					.getObjectId());

			// if the copy is null, creates it and adds to the subgraph
			if (starting_node_copy == null) {
				starting_node_copy = starting_node.getCopy();
				subgraph.nodes.add(starting_node_copy);
			}

			// adds the starting node to the nodes already expanded
			already_expanded_nodes.add(starting_node);

			// if there's still depth to expand in the given subgraph
			if (depth > 0) {
				// obtains the neighbourhood of the starting node
				Node[] neighbourhood = starting_node.getNeighbourhood();

				// for each node of the neighborhood
				for (int i = 0; i < neighbourhood.length; i++) {
					// if the current node is enabled
					if (!(neighbourhood[i] instanceof DynamicNode)
							|| ((DynamicNode) neighbourhood[i]).isEnabled()) {
						// if it isn't in the nodes already expanded
						if (!already_expanded_nodes
								.contains(neighbourhood[i])) {
							// tries to obtain a copy of it from the given
							// subgraph
							Node neighbour_copy = subgraph
									.getNode(neighbourhood[i].getObjectId());

							// registers if there's already a copy of the
							// current neighbor in the given subgraph
							boolean neighbour_copy_exists = true;

							// if the copy is null, creates it
							if (neighbour_copy == null) {
								neighbour_copy = neighbourhood[i].getCopy();
								neighbour_copy_exists = false;
							}

							// obtains the edges between the starting node and
							// its current neighbor
							Edge[] edges = starting_node
									.getConnectingEdges(neighbourhood[i]);

							// registers if some of the connecting edges is
							// enabled
							boolean enabled_edge_exists = false;

							// for each edge
							for (int j = 0; j < edges.length; j++) {
								// if the current edge is enabled
								if (edges[j].isEnabled()) {
									enabled_edge_exists = true;

									// if there isn't a copy of it in the given
									// subgraph
									if (subgraph
											.getEdge(edges[j].getObjectId()) == null) {
										// creates the copy and adds to the
										// subgraph
										Edge current_edge_copy = null;
										if (starting_node
												.isSourceOf(edges[j]))
											current_edge_copy = edges[j]
													.getCopy(
															starting_node_copy,
															neighbour_copy);
										else
											current_edge_copy = edges[j]
													.getCopy(neighbour_copy,
															starting_node_copy);

										subgraph.edges.add(current_edge_copy);
									}
								}
							}

							// if there's some enabled edge
							if (enabled_edge_exists) {
								// if the copy of the current neighbour is not
								// in the subgraph, adds it
								if (!neighbour_copy_exists)
									subgraph.nodes.add(neighbour_copy);

								// calls this method recursively, starting from
								// the current neighbor
								this.addEnabledDepth(subgraph,
										neighbourhood[i], depth - 1,
										already_expanded_nodes);
							}
						}
					}
				}
			}
		}
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

	public String fullToXML(int identation) {
		// holds the answer being constructed
		StringBuffer buffer = new StringBuffer();

		// applies the identation
		for (int i = 0; i < identation; i++)
			buffer.append("\t");

		// fills the buffer
		buffer.append("<graph label=\"" + this.label + "\">\n");

		// inserts the nodes
		for (Node node : this.nodes)
			buffer.append(node.fullToXML(identation + 1));

		// inserts the edges
		if (this.edges != null)
			for (Edge edge : this.edges)
				buffer.append(edge.fullToXML(identation + 1));

		// inserts the stigmas
		if (this.stigmas != null)
			for (Stigma stigma : this.stigmas)
				buffer.append(stigma.fullToXML(identation + 1));

		// finishes the buffer content
		for (int i = 0; i < identation; i++)
			buffer.append("\t");
		buffer.append("</graph>\n");

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
		buffer.append("<graph label=\"" + this.label + "\">\n");

		// inserts the lighter version of the nodes
		for (Node node : this.nodes)
			buffer.append(node.reducedToXML(identation + 1));

		// inserts the lighter version of the edges
		if (this.edges != null)
			for (Edge edge : this.edges)
				buffer.append(edge.reducedToXML(identation + 1));

		// finishes the buffer content
		for (int i = 0; i < identation; i++)
			buffer.append("\t");
		buffer.append("</graph>\n");

		// returns the buffer content
		return buffer.toString();
	}

	public String getObjectId() {
		// a graph doesn't need an id
		return null;
	}

	public void setObjectId(String object_id) {
		// a graph doesn't need an id
		// so, do nothing
	}
}

/**
 * Internal class that holds together a node, the distance of the path to
 * reach it from another considered node, and the last edge of such path.
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