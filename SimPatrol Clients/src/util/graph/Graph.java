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

	/** The set of vertexes of the graph. */
	private Set<Vertex> vertexes;

	/** The set of edges of the graph. */
	private Set<Edge> edges;

	/** Holds the weight of the idlenesses in the comparison of vertexes. */
	private static final double IDLENESSES_WEIGHT = 0.2;

	/**
	 * Table that holds, for each vertex, its list of distances to the other
	 * vertexes of the graph.
	 */
	private static List<DistancesList> distances_table;

	/** Holds the biggest distance between the vertexes of the graph. */
	private static double biggest_distance;

	/** Holds the smallest distance between the vertexes of the graph. */
	private static double smallest_distance;

	/* Methods. */
	/**
	 * Constructor.
	 * 
	 * @param label
	 *            The label of the graph.
	 * @param vertexes
	 *            The vertexes of the graph.
	 */
	public Graph(String label, Vertex[] vertexes) {
		this.label = label;

		this.vertexes = new HashSet<Vertex>();
		for (int i = 0; i < vertexes.length; i++)
			this.vertexes.add(vertexes[i]);

		// for each vertex, adds its edges to the set of edges
		this.edges = new HashSet<Edge>();
		for (Vertex vertex : this.vertexes) {
			Edge[] current_edges = vertex.getEdges();
			for (int i = 0; i < current_edges.length; i++)
				if (this.vertexes.contains(current_edges[i]
						.getOtherVertex(vertex)))
					this.edges.add(current_edges[i]);
		}

		if (this.edges.size() == 0)
			this.edges = null;
	}

	/**
	 * Obtains the vertexes of the graph.
	 * 
	 * @return The vertexes of the graph.
	 */
	public Vertex[] getVertexes() {
		Vertex[] answer = new Vertex[this.vertexes.size()];
		int i = 0;
		for (Vertex vertex : this.vertexes) {
			answer[i] = vertex;
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
	 * Obtains the weight of the idlenesses in the comparison of vertexes.
	 * 
	 * @return The weight of the idlenesses in the comparison of vertexes.
	 */
	public static double getIdlenessesWeight() {
		return IDLENESSES_WEIGHT;
	}

	/**
	 * Verifies if the given vertex is part of the graph.
	 * 
	 * @param vertex
	 *            The vertex to be verified.
	 * @return TRUE if the vertex is part of the graph, FALSE if not.
	 */
	public boolean hasVertex(Vertex vertex) {
		for (Vertex current_vertex : this.vertexes)
			if (current_vertex.equals(vertex))
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
	 * Fills the table that holds, for each vertex, its list of distances to the
	 * other vertexes of the graph.
	 */
	private void calculateDistances() {
		distances_table = new LinkedList<DistancesList>();

		// initiates the bound distances
		biggest_distance = -1;
		smallest_distance = Double.MAX_VALUE;

		// for each vertex of the graph, obtains its distance to the other
		// vertexes
		Object[] vertexes_array = this.vertexes.toArray();
		for (int i = 0; i < vertexes_array.length; i++) {
			// obtains the current vertex
			Vertex vertex = (Vertex) vertexes_array[i];

			// creates a list of distances for such vertex
			DistancesList list = new DistancesList(vertex);

			// for each one of the other vertexes
			// 1st. for the ones previously treated, copy them...
			for (int j = 0; j < i; j++) {
				// obtains the line previously added to the table
				DistancesList previous_list = distances_table.get(j);

				// mounts an item copying data from the previous line
				VertexWithDistance new_item = new VertexWithDistance(
						previous_list.VERTEX, previous_list.DISTANCES_LIST
								.get(i - 1).DISTANCE);

				// adds it to the current list
				list.DISTANCES_LIST.add(new_item);
			}

			// 2nd. for the ones not treated yet
			for (int j = i + 1; j < vertexes_array.length; j++) {
				// obtains the other vertex not treated yet
				Vertex other_vertex = (Vertex) vertexes_array[j];

				// obtains the minimum path between the two considered
				// vertexes
				Graph path = this.getDijkstraPath(vertex, other_vertex);

				// obtains the length of such path
				double path_length = 0;
				Edge[] path_edges = path.getEdges();
				for (int k = 0; k < path_edges.length; k++)
					path_length = path_length + path_edges[k].getLength();

				// adds a new item to the current list
				list.DISTANCES_LIST.add(new VertexWithDistance(other_vertex,
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
	 * Returns, for two given vertexes, the distance between them.
	 * 
	 * @return The distance between the two given vertexes. Returns the maximum
	 *         possible value if the vertexes are not in the same partition.
	 */
	public double getDistance(Vertex vertex_1, Vertex vertex_2) {
		if (distances_table == null || distances_table.isEmpty())
			this.calculateDistances();

		for (int i = 0; i < distances_table.size(); i++)
			if (distances_table.get(i).VERTEX.equals(vertex_1)) {
				List<VertexWithDistance> list = distances_table.get(i).DISTANCES_LIST;

				for (int j = 0; j < list.size(); j++)
					if (list.get(j).VERTEX.equals(vertex_2))
						return list.get(j).DISTANCE;
			}

		return Double.MAX_VALUE;
	}

	/** Returns the smallest and biggest idlenesses of the graph. */
	public double[] getSmallestAndBiggestIdlenesses() {
		double smallest_idleness = Double.MAX_VALUE;
		double biggest_idleness = -1;

		for (Vertex vertex : this.vertexes) {
			double idleness = vertex.getIdleness();

			if (idleness > biggest_idleness)
				biggest_idleness = idleness;

			if (idleness < smallest_idleness)
				smallest_idleness = idleness;
		}

		double[] answer = { smallest_idleness, biggest_idleness };
		return answer;
	}

	/**
	 * Returns the smallest and biggest distances among the vertexes of the
	 * graph.
	 */
	public double[] getSmallestAndBiggestDistances() {
		if (distances_table == null || distances_table.isEmpty())
			this.calculateDistances();

		double[] answer = { smallest_distance, biggest_distance };
		return answer;
	}

	/**
	 * Returns the minimum path between two given vertexes, using the Dijkstra's
	 * algorithm.
	 * 
	 * @param begin_vertex
	 *            The first vertex of the desired path.
	 * @param end_vertex
	 *            The last vertex of the desired path.
	 * @return The minimum path between two given vertexes.
	 */
	public Graph getDijkstraPath(Vertex begin_vertex, Vertex end_vertex) {
		// for each vertex of the graph, correlates it with its distance
		// to the begin_vertex, as well as the last edge to reach it
		LinkedList<VertexDistanceEdge> vertexes_with_distances_list = new LinkedList<VertexDistanceEdge>();

		for (Vertex vertex : this.vertexes)
			if (vertex.equals(begin_vertex))
				vertexes_with_distances_list.add(new VertexDistanceEdge(vertex,
						0, null));
			else
				vertexes_with_distances_list.add(new VertexDistanceEdge(vertex,
						Double.MAX_VALUE, null));

		// mounts a heap with the vertex-distance-edge trios
		VertexDistanceEdge[] vertexes_with_distance = new VertexDistanceEdge[vertexes_with_distances_list
				.size()];
		int index = 0;
		for (VertexDistanceEdge vertex_with_distance : vertexes_with_distances_list) {
			vertexes_with_distance[index] = vertex_with_distance;
			index++;
		}

		MinimumHeap heap = new MinimumHeap(vertexes_with_distance);

		// while the heap is not empty
		while (!heap.isEmpty()) {
			// removes the minimum element of the heap
			VertexDistanceEdge minimum = (VertexDistanceEdge) heap
					.removeSmallest();

			// if the distance set to the minimum element is the maximum
			// possible double value,
			// return null (i.e. the graph is disconnected, and the end_vertex
			// is unreachable)
			if (minimum.distance == Double.MAX_VALUE)
				return null;

			// if the minimum element has the end_vertex, quits the loop
			if (minimum.VERTEX.equals(end_vertex))
				break;

			// obtains the current vertex to be expanded
			Vertex current_vertex = minimum.VERTEX;

			// obtains the neighborhood where the current vertex is an emitter
			Vertex[] neighbourhood = current_vertex.getCollectorNeighbourhood();

			// for each vertex of the neighborhood
			for (int i = 0; i < neighbourhood.length; i++) {
				// obtains all the edges between the current vertex and the
				// current neighbor, of which emitter is the current vertex
				Edge[] edges = current_vertex
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
					// obtains the vertex-distance-edge trio of the current
					// neighbor
					VertexDistanceEdge neighbour_with_distance = null;

					for (int j = 0; j < vertexes_with_distance.length; j++)
						if (vertexes_with_distance[j].VERTEX
								.equals(neighbourhood[i])) {
							neighbour_with_distance = vertexes_with_distance[j];
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
		// sets the current vertex being added to the answer as the end_vertex
		Vertex current_vertex = end_vertex;

		// obtains a copy of the current vertex
		Vertex current_vertex_copy = current_vertex.getCopy();

		// the answer of the method
		Vertex[] vertexes_answer = { current_vertex_copy };
		Graph answer = new Graph("dijkstra's path", vertexes_answer);
		answer.edges = new HashSet<Edge>();

		// keep on mounting the answer...
		while (true) {
			// finds the vertex-distance-edge trio of the current vertex
			VertexDistanceEdge current_vertex_with_distance = null;
			for (int i = 0; i < vertexes_with_distance.length; i++)
				if (vertexes_with_distance[i].VERTEX.equals(current_vertex)) {
					current_vertex_with_distance = vertexes_with_distance[i];
					break;
				}

			// sets the next_edge
			Edge next_edge = current_vertex_with_distance.edge;

			// if the next edge is valid
			if (next_edge != null) {
				// obtains the next vertex
				Vertex next_vertex = next_edge.getOtherVertex(current_vertex);

				// obtains copies of the next edge and next vertex
				Vertex next_vertex_copy = next_vertex.getCopy();
				Edge next_edge_copy = next_edge.getCopy(current_vertex_copy,
						next_vertex_copy);

				// adds the copies to the answer
				answer.vertexes.add(next_vertex_copy);
				answer.edges.add(next_edge_copy);

				// updates the current vertex and current vertex copy
				current_vertex = next_vertex;
				current_vertex_copy = next_vertex_copy;
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
	 * Returns the minimum path between two given vertexes, using the Dijkstra's
	 * algorithm. The evaluation of the paths is based not only in the distance
	 * of the vertexes, but also in their idlenesses.
	 * 
	 * @param begin_vertex
	 *            The first vertex of the desired path.
	 * @param end_vertex
	 *            The last vertex of the desired path.
	 * @return The minimum path between two given vertexes.
	 */
	public Graph getIdlenessedDijkstraPath(Vertex begin_vertex,
			Vertex end_vertex) {
		// configures the trios "vertex - distance - edge"
		IdlenessedVertexDistanceEdge.graph = this;

		// for each vertex of the graph, correlates it with its distance
		// to the begin_vertex, as well as the last edge to reach it
		LinkedList<IdlenessedVertexDistanceEdge> vertexes_with_distances_list = new LinkedList<IdlenessedVertexDistanceEdge>();

		for (Vertex vertex : this.vertexes)
			if (vertex.equals(begin_vertex))
				vertexes_with_distances_list
						.add(new IdlenessedVertexDistanceEdge(vertex, 0, null));
			else
				vertexes_with_distances_list
						.add(new IdlenessedVertexDistanceEdge(vertex,
								Double.MAX_VALUE, null));

		// mounts a heap with the vertex-distance-edge trios
		IdlenessedVertexDistanceEdge[] vertexes_with_distance = new IdlenessedVertexDistanceEdge[vertexes_with_distances_list
				.size()];
		int index = 0;
		for (IdlenessedVertexDistanceEdge vertex_with_distance : vertexes_with_distances_list) {
			vertexes_with_distance[index] = vertex_with_distance;
			index++;
		}

		MinimumHeap heap = new MinimumHeap(vertexes_with_distance);

		// while the heap is not empty
		while (!heap.isEmpty()) {
			// removes the minimum element of the heap
			IdlenessedVertexDistanceEdge minimum = (IdlenessedVertexDistanceEdge) heap
					.removeSmallest();

			// if the distance set to the minimum element is the maximum
			// possible double value,
			// return null (i.e. the graph is disconnected, and the end_vertex
			// is unreachable)
			if (minimum.distance == Double.MAX_VALUE)
				return null;

			// if the minimum element has the end_vertex, quits the loop
			if (minimum.VERTEX.equals(end_vertex))
				break;

			// obtains the current vertex to be expanded
			Vertex current_vertex = minimum.VERTEX;

			// obtains the neighborhood where the current vertex is an emitter
			Vertex[] neighbourhood = current_vertex.getCollectorNeighbourhood();

			// for each vertex of the neighborhood
			for (int i = 0; i < neighbourhood.length; i++) {
				// obtains all the edges between the current vertex and the
				// current
				// neighbor, of which emitter is the current vertex
				Edge[] edges = current_vertex
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
					// obtains the vertex-distance-edge trio of the current
					// neighbor
					IdlenessedVertexDistanceEdge neighbour_with_distance = null;

					for (int j = 0; j < vertexes_with_distance.length; j++)
						if (vertexes_with_distance[j].VERTEX
								.equals(neighbourhood[i])) {
							neighbour_with_distance = vertexes_with_distance[j];
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
		// sets the current vertex being added to the answer as the end_vertex
		Vertex current_vertex = end_vertex;

		// obtains a copy of the current vertex
		Vertex current_vertex_copy = current_vertex.getCopy();

		// the answer of the method
		Vertex[] vertexes_answer = { current_vertex_copy };
		Graph answer = new Graph("dijkstra's path", vertexes_answer);
		answer.edges = new HashSet<Edge>();

		// keep on mounting the answer...
		while (true) {
			// finds the vertex-distance-edge trio of the current vertex
			IdlenessedVertexDistanceEdge current_vertex_with_distance = null;
			for (int i = 0; i < vertexes_with_distance.length; i++)
				if (vertexes_with_distance[i].VERTEX.equals(current_vertex)) {
					current_vertex_with_distance = vertexes_with_distance[i];
					break;
				}

			// sets the next_edge
			Edge next_edge = current_vertex_with_distance.edge;

			// if the next edge is valid
			if (next_edge != null) {
				// obtains the next vertex
				Vertex next_vertex = next_edge.getOtherVertex(current_vertex);

				// obtains copies of the next edge and next vertex
				Vertex next_vertex_copy = next_vertex.getCopy();
				Edge next_edge_copy = next_edge.getCopy(current_vertex_copy,
						next_vertex_copy);

				// adds the copies to the answer
				answer.vertexes.add(next_vertex_copy);
				answer.edges.add(next_edge_copy);

				// updates the current vertex and current vertex copy
				current_vertex = next_vertex;
				current_vertex_copy = next_vertex_copy;
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

		// set that contains the sets of vertexes
		HashSet<HashSet<Vertex>> vertex_sets = new HashSet<HashSet<Vertex>>();

		// holds the copies of each vertex of the graph
		LinkedList<Vertex> vertex_copies = new LinkedList<Vertex>();

		// creates a set for each vertex of the graph and obtains a copy of it
		for (Vertex vertex : this.vertexes) {
			// set creation
			HashSet<Vertex> set = new HashSet<Vertex>();
			set.add(vertex);
			vertex_sets.add(set);

			// vertex copy
			vertex_copies.add(vertex.getCopy());
		}

		// for each edge held in the minimum heap
		while (!heap.isEmpty()) {
			// obtains the current edge
			Edge current_edge = (Edge) heap.removeSmallest();

			// obtains its vertexes
			Vertex[] current_vertexes = current_edge.getVertexes();

			// finds the set of the first vertex
			for (HashSet<Vertex> vertex_set : vertex_sets)
				if (vertex_set.contains(current_vertexes[0])) {
					// if such set does not contain the second vertex of the
					// edge
					if (!vertex_set.contains(current_vertexes[1])) {
						// obtains the copies of such vertexes
						Vertex vertex_copy_1 = null;
						Vertex vertex_copy_2 = null;

						for (Vertex vertex_copy : vertex_copies) {
							if (vertex_copy.equals(current_vertexes[0]))
								vertex_copy_1 = vertex_copy;
							else if (vertex_copy.equals(current_vertexes[1]))
								vertex_copy_2 = vertex_copy;

							if (vertex_copy_1 != null && vertex_copy_2 != null)
								break;
						}

						// connects them based on the current edge
						current_edge.getCopy(vertex_copy_1, vertex_copy_2);

						// unify the sets of the two vertexes
						// finds the set of the second vertex
						for (HashSet<Vertex> other_vertex_set : vertex_sets)
							if (other_vertex_set.contains(current_vertexes[1])) {
								// removes the other set from the list of sets
								vertex_sets.remove(other_vertex_set);

								// adds its elements to the set of the 1st
								// vertex
								vertex_set.addAll(other_vertex_set);

								// quits the loop
								break;
							}
					}

					// quits the loop
					break;
				}

			// if the set of sets of vertexes has only one element, quits
			// the
			// loop
			if (vertex_sets.size() == 1)
				break;
		}

		// mounts and returns the answer of the method
		Vertex[] answer_vertexes = new Vertex[vertex_copies.size()];
		int i = 0;
		for (Vertex vertex : vertex_copies) {
			answer_vertexes[i] = vertex;
			i++;
		}

		return new Graph("MWST", answer_vertexes);
	}

	/**
	 * Returns the minimum (M) weight (W) perfect (P) matching (M) of the graph,
	 * in terms of edges, related to the given vertexes.
	 * 
	 * @param vertexes
	 *            The vertexes to be considered in the matching.
	 * @param ignored_edges
	 *            The edges to be ignored in the matching constitution.
	 * @return The edges constituting the matching.
	 */
	private Edge[] getMWPM(Vertex[] vertexes, Edge[] ignored_edges) {
		// adds the given vertexes to a set
		HashSet<Vertex> given_vertexes_set = new HashSet<Vertex>();
		for (int i = 0; i < vertexes.length; i++)
			given_vertexes_set.add(vertexes[i]);

		// adds the given edges to be ignored to a set
		HashSet<Edge> ignored_edges_set = new HashSet<Edge>();
		for (int i = 0; i < ignored_edges.length; i++)
			ignored_edges_set.add(ignored_edges[i]);

		// holds the edges to be answered by this method (proper edges)
		HashSet<Edge> proper_edges = new HashSet<Edge>();

		// list that holds the sets of possible edges connecting the given
		// vertexes among themselves
		LinkedList<HashSet<Edge>> possible_edges = new LinkedList<HashSet<Edge>>();

		// for each given vertex
		for (int i = 0; i < vertexes.length; i++) {
			// obtains the current given vertex
			Vertex current_given_vertex = vertexes[i];

			// obtains the edges of the current vertex
			Edge[] current_given_vertex_edges = current_given_vertex.getEdges();

			// creates a set of edges for the current vertex
			HashSet<Edge> current_given_vertex_edges_set = new HashSet<Edge>();

			// for each edge of the current given vertex
			for (int j = 0; j < current_given_vertex_edges.length; j++) {
				// obtains the current edge
				Edge current_edge = current_given_vertex_edges[j];

				// if such edge is not in the set of ignored edges
				if (!ignored_edges_set.contains(current_edge))
					// if the other vertex of such edge is among the given ones
					if (given_vertexes_set.contains(current_edge
							.getOtherVertex(current_given_vertex)))
						// adds the current edge to the set of possible edges of
						// the current vertex
						current_given_vertex_edges_set.add(current_edge);
			}

			// adds the set of possible edges to the proper list
			possible_edges.add(current_given_vertex_edges_set);
		}

		// holds the already treated vertexes
		HashSet<Vertex> treated_vertexes = new HashSet<Vertex>();

		// while the "treated vertexes" set is not equals to the "given
		// vertexes" set
		// used for set cardinality control
		int considered_cardinality = 0;
		while (treated_vertexes.size() != given_vertexes_set.size()) {
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

					// adds the correspondent given vertex to the treated
					// vertexes
					treated_vertexes.add(vertexes[pos]);
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

				// obtains the vertexes of the smallest edge
				Vertex[] smallest_edge_vertexes = smallest_edge.getVertexes();

				// adds them to the treated ones
				treated_vertexes.add(smallest_edge_vertexes[0]);
				treated_vertexes.add(smallest_edge_vertexes[1]);

				// removes all the possible edges that connect at least one of
				// such vertexes
				// for each set of possible edges
				for (HashSet<Edge> current_possible_edges_set : possible_edges)
					// if the current possible edges set is not null
					if (current_possible_edges_set != null) {
						// holds the edges to be removed
						HashSet<Edge> to_remove_edges = new HashSet<Edge>();

						// for each edge in it
						for (Edge current_possible_edge : current_possible_edges_set)
							// if one of the vertexes of the smallest edge is
							// connected by such edge
							if (smallest_edge_vertexes[0]
									.isEmitterOf(current_possible_edge)
									|| smallest_edge_vertexes[0]
											.isCollectorOf(current_possible_edge)
									|| smallest_edge_vertexes[1]
											.isEmitterOf(current_possible_edge)
									|| smallest_edge_vertexes[1]
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
	 * Tries to return an hamiltonian cycle (i.e. a sequence of vertexes).
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
	private Vertex[] getHamiltonianCycle(Graph original_graph) {
		// holds the answer for this method
		LinkedList<Vertex> hamiltonian_cycle = new LinkedList<Vertex>();

		// holds the ids of the already treated vertexes
		LinkedList<String> treated_vertexes_ids = new LinkedList<String>();

		// holds the possible expansions for each treated vertex
		LinkedList<HashSet<Vertex>> possible_expansions = new LinkedList<HashSet<Vertex>>();

		// obtains a vertex from this graph, to start walking
		Vertex current_vertex = (Vertex) this.vertexes.toArray()[0];

		// registers the last position where the hamiltonian cycle was
		// broken and turned back
		int turn_back_pos = -1;

		// while true...
		while (true) {
			// adds the current vertex to the hamiltonian cycle
			hamiltonian_cycle.add(current_vertex);

			// for the current vertex
			String current_vertex_id = current_vertex.getObjectId();
			if (!treated_vertexes_ids.contains(current_vertex_id)) {
				// adds its id in the already treated ones
				treated_vertexes_ids.add(current_vertex_id);

				// creates a set to hold its possible expansions
				possible_expansions.add(new HashSet<Vertex>());

				// removes it from the sets of possible expansions of the other
				// vertexes
				for (HashSet<Vertex> expansion_set : possible_expansions)
					expansion_set.remove(current_vertex);
			}

			// expands the current vertex
			// obtains its edges
			Edge[] current_vertex_edges = current_vertex.getEdges();

			// holds the chosen next vertex
			Vertex next_vertex = null;

			// holds the possible next vertexes
			LinkedList<Vertex> possible_next_vertexes = new LinkedList<Vertex>();

			// for each edge
			for (int i = 0; i < current_vertex_edges.length; i++) {
				// obtains the current edge
				Edge current_edge = current_vertex_edges[i];

				// if the other vertex of such edge was not treated yet
				Vertex other_vertex = current_edge
						.getOtherVertex(current_vertex);
				if (!treated_vertexes_ids.contains(other_vertex.getObjectId()))
					// adds it as a possible next vertex
					possible_next_vertexes.add(other_vertex);
			}

			// if the possible next vertexes set is empty
			if (possible_next_vertexes.isEmpty()) {
				// if all the vertexes were already put in the solution
				if (treated_vertexes_ids.size() == this.vertexes.size()) {
					// finds in the original graph, the Dijkstra's path to reach
					// the first vertex of the hamiltonian cycle from the
					// current one
					Graph dijkstra_path = original_graph.getDijkstraPath(
							current_vertex, hamiltonian_cycle.getFirst());

					// adds the vertexes of such path to the hamiltonian cycle
					Vertex[] dijkstra_path_vertexes = dijkstra_path
							.getVertexes();
					HashSet<Edge> considered_dijkstra_edges = new HashSet<Edge>();

					Vertex dijkstra_path_vertex = null;
					for (int i = 0; i < dijkstra_path_vertexes.length; i++) {
						dijkstra_path_vertex = dijkstra_path_vertexes[i];

						if (dijkstra_path_vertex.equals(current_vertex))
							break;
					}

					do {
						Edge[] dijkstra_path_edges = dijkstra_path_vertex
								.getEdges();

						if (considered_dijkstra_edges
								.contains(dijkstra_path_edges[0])) {
							dijkstra_path_vertex = dijkstra_path_edges[1]
									.getOtherVertex(dijkstra_path_vertex);
							considered_dijkstra_edges
									.add(dijkstra_path_edges[1]);
						} else {
							dijkstra_path_vertex = dijkstra_path_edges[0]
									.getOtherVertex(dijkstra_path_vertex);
							considered_dijkstra_edges
									.add(dijkstra_path_edges[0]);
						}

						hamiltonian_cycle.add(this
								.getVertex(dijkstra_path_vertex.getObjectId()));
					} while (dijkstra_path_vertex.getDegree() > 1);

					// quits the loop
					break;
				}
				// else
				else {
					// obtains the original version of the current vertex
					Vertex original_current_vertex = original_graph
							.getVertex(current_vertex.getObjectId());

					// obtains the edges of such vertex
					Edge[] original_edges = original_current_vertex.getEdges();

					// mounts a heap with such edges, based on their lengths
					MinimumHeap heap = new MinimumHeap(original_edges);

					// holds the original version of the next vertex
					Vertex original_next_vertex = null;

					// tries to set such vertex
					while (!heap.isEmpty()) {
						Vertex current_original_next_vertex_candidate = ((Edge) heap
								.removeSmallest())
								.getOtherVertex(original_current_vertex);

						if (!treated_vertexes_ids
								.contains(current_original_next_vertex_candidate
										.getObjectId())) {
							original_next_vertex = current_original_next_vertex_candidate;
							break;
						}
					}

					// if the original next vertex is valid, obtains its copy
					// version
					if (original_next_vertex != null)
						next_vertex = this.getVertex(original_next_vertex
								.getObjectId());
					// else, tries to set the next vertex as one of the vertexes
					// to be expanded (the answer is no more an hamiltonian
					// cycle)
					else {
						for (int i = 0; i < current_vertex_edges.length; i++) {
							// obtains the current edge
							Edge current_edge = current_vertex_edges[i];

							// obtains the other vertex of such edge
							Vertex other_vertex = current_edge
									.getOtherVertex(current_vertex);

							// if such other vertex still can be expanded, sets
							// it as the next vertex
							int set_pos = treated_vertexes_ids
									.indexOf(other_vertex.getObjectId());
							HashSet<Vertex> other_vertex_expansion_set = possible_expansions
									.get(set_pos);
							if (other_vertex_expansion_set.size() > 0) {
								next_vertex = other_vertex;
								break;
							}
						}

						// if the next vertex is still null
						if (next_vertex == null)
							// if it's possible to turn back
							if (turn_back_pos > -1) {
								// sets the next vertex
								next_vertex = hamiltonian_cycle
										.get(turn_back_pos);

								// decreases the turn back registered position
								turn_back_pos--;
							}
					}
				}
			}
			// else if the current vertex has more than one possible next vertex
			else if (possible_next_vertexes.size() > 1) {
				// obtains the set that holds the possible expansions for the
				// current vertex
				int set_pos = treated_vertexes_ids.indexOf(current_vertex
						.getObjectId());
				HashSet<Vertex> current_vertex_expansion_set = possible_expansions
						.get(set_pos);

				// holds the smallest degree found among the possible next
				// vertexes
				double smallest_degree = Double.MAX_VALUE;

				// for each candidate vertex
				for (Vertex next_vertex_candidate : possible_next_vertexes) {
					// adds it in the expansion set of the current vertex
					current_vertex_expansion_set.add(next_vertex_candidate);

					// sets the next vertex as the one with the smallest degree
					double current_degree = next_vertex_candidate.getDegree();

					if (current_degree < smallest_degree) {
						smallest_degree = current_degree;
						next_vertex = next_vertex_candidate;
					}
				}

				// configures the turn back position
				turn_back_pos = hamiltonian_cycle.size() - 1;
			}
			// else
			else {
				// sets the next vertex as the unique one
				next_vertex = possible_next_vertexes.getFirst();

				// configures the turn back position
				turn_back_pos = hamiltonian_cycle.size() - 1;
			}

			// if the next vertex is valid
			if (next_vertex != null)
				// sets it as the current one
				current_vertex = next_vertex;
			// else, quits the loop (it is not possible to walk anymore)
			else
				break;
		}

		// mounts the answer of the method
		Vertex[] answer = new Vertex[hamiltonian_cycle.size()];
		int i = 0;
		for (Vertex vertex : hamiltonian_cycle) {
			answer[i] = vertex;
			i++;
		}

		return answer;
	}

	/**
	 * Tries to returns a solution for the TSP problem, in terms of a sequence
	 * of vertexes. It is based on the Christophide's algorithm. Ignores the
	 * orientation of the vertexes.
	 * 
	 * @return The sequence of vertexes that try to represent a TSP solution.
	 */
	public Vertex[] getTSPSolution() {
		// obtains the minimum weight spanning tree
		Graph mws_tree = this.getMWST();

		// obtains the edges of such tree
		Edge[] mws_tree_edges = mws_tree.getEdges();
		for (int i = 0; i < mws_tree_edges.length; i++)
			mws_tree_edges[i] = this.getEdge(mws_tree_edges[i].getObjectId());

		// obtains the vertexes of such tree that have odd degree
		HashSet<Vertex> odd_degree_vertexes_set = new HashSet<Vertex>();

		Vertex[] mws_tree_vertexes = mws_tree.getVertexes();
		for (int i = 0; i < mws_tree_vertexes.length; i++) {
			Vertex current_vertex = mws_tree_vertexes[i];

			if (current_vertex.getDegree() % 2 > 0)
				odd_degree_vertexes_set.add(current_vertex);
		}

		Vertex[] odd_degree_vertexes = new Vertex[odd_degree_vertexes_set
				.size()];
		int i = 0;
		for (Vertex odd_degree_vertex : odd_degree_vertexes_set) {
			odd_degree_vertexes[i] = this.getVertex(odd_degree_vertex
					.getObjectId());
			i++;
		}

		// obtains the edges that must be added to the mws tree in order to help
		// to find a TSP solution
		Edge[] additional_edges = this.getMWPM(odd_degree_vertexes,
				mws_tree_edges);

		// adds such edges to the mws tree
		for (i = 0; i < additional_edges.length; i++) {
			// obtains the current additional edge
			Edge current_add_edge = additional_edges[i];

			// finds the vertexes of the current edge in the tree
			Vertex[] current_add_vertexes = current_add_edge.getVertexes();
			Vertex vertex_1 = null;
			Vertex vertex_2 = null;

			for (int j = 0; j < mws_tree_vertexes.length; j++) {
				Vertex current_tree_vertex = mws_tree_vertexes[j];

				if (current_tree_vertex.equals(current_add_vertexes[0]))
					vertex_1 = current_tree_vertex;
				else if (current_tree_vertex.equals(current_add_vertexes[1]))
					vertex_2 = current_tree_vertex;

				if (vertex_1 != null && vertex_2 != null)
					break;
			}

			// adds the current edge to the (no-more) tree
			Edge current_add_edge_copy = current_add_edge.getCopy(vertex_1,
					vertex_2);
			mws_tree.edges.add(current_add_edge_copy);
		}

		// returns the hamiltonian candidate cycle
		return mws_tree.getHamiltonianCycle(this);
	}

	/**
	 * Tries to returns a solution for the TSP problem, in terms of a sequence
	 * of vertexes. It is based on the Christophide's algorithm. Ignores the
	 * orientation of the vertexes.
	 * 
	 * Just another try... ;)
	 * 
	 * @return The sequence of vertexes that try to represent a TSP solution.
	 */
	public Vertex[] getTSPSolution2() {
		// obtains the minimum weight spanning tree
		Graph mws_tree = this.getMWST();

		// the current degree to be treated by this algorithm
		int current_degree = 1;

		// while true...
		while (true) {
			// holds the number of vertex of the mws tree that have degree
			// smaller than the currently considered degree
			int small_degree_count = 0;

			// obtains, in a list, all the vertexes that have the current degree
			LinkedList<Vertex> current_mws_vertexes = new LinkedList<Vertex>();
			for (Vertex current_mws_vertex : mws_tree.getVertexes()) {
				// holds the degree of the current vertex
				int current_mws_vertex_degree = current_mws_vertex.getDegree();

				// if such degree is equals to the current considered one, adds
				// the current vertex to the list of currently considered
				// vertexes
				if (current_mws_vertex_degree == current_degree)
					current_mws_vertexes.add(current_mws_vertex);

				// else, if such degree is smaller than the current considered
				// one, increases the number of vertexes of the mws tree that
				// have degree smaller than the currently considered degree
				else if (current_mws_vertex_degree < current_degree)
					small_degree_count++;
			}

			// if the the number of vertex of the mws tree that have degree
			// smaller than the current considered degree is equals to the
			// number of vertex of the mws tree, quits the loop
			if (small_degree_count == mws_tree.getVertexes().length)
				break;

			// if the current considered degree is 1...
			if (current_degree == 1) {
				// for each vertex, tries to combine them 2 by 2, based on their
				// connecting edges
				LinkedList<Vertex> vertexes_indexes = new LinkedList<Vertex>();
				LinkedList<HashSet<Edge>> connecting_possibilities = new LinkedList<HashSet<Edge>>();

				for (Vertex current_mws_vertex : current_mws_vertexes) {
					vertexes_indexes.add(current_mws_vertex);
					connecting_possibilities.add(new HashSet<Edge>());
				}

				for (Vertex current_vertex : current_mws_vertexes)
					for (Vertex other_vertex : current_mws_vertexes)
						if (!current_vertex.equals(other_vertex)) {
							// obtains the current vertex and the other
							// vertex from this graph
							Vertex current_vertex_org = this
									.getVertex(current_vertex.getObjectId());
							Vertex other_vertex_org = this
									.getVertex(other_vertex.getObjectId());

							// if the two vertexes are connected by an edge
							// that is not part of the mws tree, registers
							// it
							Edge[] connecting_edges = current_vertex_org
									.getConnectingEdges(other_vertex_org);
							if (connecting_edges.length > 0) {
								Edge connecting_edge = connecting_edges[0];

								if (mws_tree.getEdge(connecting_edge
										.getObjectId()) == null) {
									int index = vertexes_indexes
											.indexOf(current_vertex);
									connecting_possibilities.get(index).add(
											connecting_edge);
								}
							}
						}

				// holds the already treated vertexes
				HashSet<Vertex> treated_vertexes = new HashSet<Vertex>();

				// holds the cardinality being currently considered related to
				// connecting possibilities
				int current_cardinality = 0;

				// while the number of treated vertexes is smaller than the
				// number of vertexes currently considered
				while (treated_vertexes.size() < current_mws_vertexes.size()) {
					// holds the index of the current vertex
					int vertex_index = -1;

					// for each item of the connecting possibilities
					for (HashSet<Edge> possible_edges : connecting_possibilities) {
						vertex_index++;

						// if such set has the current cardinality
						if (possible_edges.size() == current_cardinality)
							if (current_cardinality == 0)
								// adds the current vertex to the treated ones
								treated_vertexes.add(vertexes_indexes
										.get(vertex_index));

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

								// obtains the two vertexes of such edge
								Vertex[] smallest_edge_vertexes = smallest_edge
										.getVertexes();

								// obtains the vertexes from the mws tree
								// related to
								// such edge
								Vertex vertex_1 = mws_tree
										.getVertex(smallest_edge_vertexes[0]
												.getObjectId());
								Vertex vertex_2 = mws_tree
										.getVertex(smallest_edge_vertexes[1]
												.getObjectId());

								// adds a copy of the smallest edge to the mws
								// tree
								Edge smallest_edge_copy = smallest_edge
										.getCopy(vertex_1, vertex_2);
								mws_tree.edges.add(smallest_edge_copy);

								// removes, from the possible edges, all the
								// edges that are related to the two
								// vertexes
								for (HashSet<Edge> edges_set : connecting_possibilities) {
									// holds the edges to be removed
									HashSet<Edge> to_remove_edges = new HashSet<Edge>();

									for (Edge edge : edges_set) {
										Vertex[] vertexes = edge.getVertexes();

										if (vertexes[0]
												.equals(smallest_edge_vertexes[0])
												|| vertexes[0]
														.equals(smallest_edge_vertexes[1])
												|| vertexes[1]
														.equals(smallest_edge_vertexes[0])
												|| vertexes[1]
														.equals(smallest_edge_vertexes[1]))
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

				// for the current vertexes that still have degree one
				for (Vertex current_mws_vertex : current_mws_vertexes)
					if (current_mws_vertex.getDegree() == 1) {
						// obtains the vertex from the mws tree to which the
						// current vertex is connected
						Vertex other_mws_vertex = current_mws_vertex.getEdges()[0]
								.getOtherVertex(current_mws_vertex);

						// obtains the current and the other vertex from this
						// graph
						Vertex current_mws_vertex_org = this
								.getVertex(current_mws_vertex.getObjectId());
						Vertex other_mws_vertex_org = this
								.getVertex(other_mws_vertex.getObjectId());

						// obtains the neighborhood of the other vertex and adds
						// it to a set
						HashSet<Vertex> other_vertex_neighborhood = new HashSet<Vertex>();
						Vertex[] other_vertex_neighbors = other_mws_vertex_org
								.getNeighbourhood();
						for (Vertex neighbor : other_vertex_neighbors)
							other_vertex_neighborhood.add(neighbor);

						// holds the candidate edges to establish an alternative
						// route
						HashSet<Edge> candidate_edges = new HashSet<Edge>();

						// for each edge of the current vertex
						Edge[] current_vertex_edges = current_mws_vertex_org
								.getEdges();
						for (Edge current_edge : current_vertex_edges)
							// if the other vertex of the current edge is in the
							// neighborhood of the other vertex
							if (other_vertex_neighborhood.contains(current_edge
									.getOtherVertex(current_mws_vertex_org)))
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

						// removes the edge connecting the other vertex of the
						// mws tree to the other vertex of the chosen edge
						if (smallest_edge != null) {
							Vertex third_mws_vertex = mws_tree
									.getVertex(smallest_edge.getOtherVertex(
											current_mws_vertex_org)
											.getObjectId());
							Edge[] to_be_removed_edge = other_mws_vertex
									.getConnectingEdges(third_mws_vertex);
							if (to_be_removed_edge.length > 0) {
								mws_tree.edges.remove(to_be_removed_edge[0]);
								to_be_removed_edge[0].disconnect();
							}

							// adds the smallest edge to the mws tree
							Edge smallest_edge_copy = smallest_edge.getCopy(
									current_mws_vertex, third_mws_vertex);
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
				// obtains the current vertex that has the biggest edges
				Vertex to_treat_mws_vertex = null;
				double biggest_edges_legth_sum = Double.MAX_VALUE * -1;

				for (Vertex current_mws_vertex : current_mws_vertexes) {
					// obtains the edges of the current vertex
					Edge[] current_mws_vertex_edges = current_mws_vertex
							.getEdges();

					// holds the sum of the lengths of such edges
					double current_edges_length_sum = 0;
					for (Edge current_edge : current_mws_vertex_edges)
						current_edges_length_sum = current_edges_length_sum
								+ current_edge.getLength();

					// if such sum is bigger than the last recorded one, updates
					// everything
					if (current_edges_length_sum > biggest_edges_legth_sum) {
						to_treat_mws_vertex = current_mws_vertex;
						biggest_edges_legth_sum = current_edges_length_sum;
					}
				}

				// for such vertex, sorts its edges based on their length
				LinkedList<Edge> sorted_edges = new LinkedList<Edge>();

				for (Edge current_edge : to_treat_mws_vertex.getEdges()) {
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
							// obtains the original vertex from this graph that
							// is equivalent to the treated one
							Vertex to_treat_mws_vertex_org = this
									.getVertex(to_treat_mws_vertex
											.getObjectId());

							// obtains the original edges from this graph
							Edge current_edge_org = this.getEdge(current_edge
									.getObjectId());
							Edge other_edge_org = this.getEdge(other_edge
									.getObjectId());

							// obtains the two vertexes that are connected to
							// the treated one
							Vertex vertex_1 = current_edge_org
									.getOtherVertex(to_treat_mws_vertex_org);
							Vertex vertex_2 = other_edge_org
									.getOtherVertex(to_treat_mws_vertex_org);

							// if such vertexes are connected one to the other,
							// obtains the edge connecting them
							Edge connecting_edge = null;
							Edge[] connecting_edges = vertex_1
									.getConnectingEdges(vertex_2);
							if (connecting_edges.length > 0)
								connecting_edge = connecting_edges[0];
							connecting_edges = null;

							// if such edge is not in the mws tree yet
							if (connecting_edge != null
									&& mws_tree.getEdge(connecting_edge
											.getObjectId()) == null) {
								// adds such edge to the mws tree
								Edge connecting_edge_copy = connecting_edge
										.getCopy(vertex_1, vertex_2);
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
	 * Returns the vertex of the graph that has the given id.
	 * 
	 * @param id
	 *            The id of the wanted vertex.
	 * @return The vertex with the given id, or NULL if there's no vertex with
	 *         such id.
	 */
	private Vertex getVertex(String id) {
		for (Vertex vertex : this.vertexes)
			if (vertex.getObjectId().equals(id))
				return vertex;

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

			Vertex[] vertexes = graph.getVertexes();
			for (int i = 0; i < vertexes.length; i++) {
				Vertex vertex = this.getVertex(vertexes[i].getObjectId());
				if (vertex == null
						|| vertex.getIdleness() != vertexes[i].getIdleness())
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
 * Internal class that holds, for a specific vertex, its distance to others kept
 * in a list.
 */
final class DistancesList {
	/** The vertex of which distances are held. */
	public final Vertex VERTEX;

	/** The list of vertexes and their respective distances. */
	public final List<VertexWithDistance> DISTANCES_LIST;

	/**
	 * Constructor.
	 * 
	 * @param vertex
	 *            The vertex of which list is being held.
	 */
	public DistancesList(Vertex vertex) {
		this.VERTEX = vertex;
		this.DISTANCES_LIST = new LinkedList<VertexWithDistance>();
	}
}

/** Internal class that holds together a vertex and the distance to reach it. */
final class VertexWithDistance {
	/** The vertex. */
	public final Vertex VERTEX;

	/** The distance to reach it. */
	public final double DISTANCE;

	/**
	 * Constructor.
	 * 
	 * @param vertex
	 *            The vertex.
	 * @param distance
	 *            The distance to reach the vertex.
	 */
	public VertexWithDistance(Vertex vertex, double distance) {
		this.VERTEX = vertex;
		this.DISTANCE = distance;
	}
}

/**
 * Internal class that holds together a vertex, the distance of the path to
 * reach it from another considered vertex, and the last edge of such path.
 * 
 * This class also implements the interface Comparable, in order to provide a
 * "isSmallerThan" method that considers the distance of the path, in the
 * comparison with a given object.
 */
final class VertexDistanceEdge implements Comparable {
	/** The vertex. */
	public final Vertex VERTEX;

	/** The distance to the vertex. */
	public double distance;

	/** The last edge to reach the vertex. */
	public Edge edge;

	/**
	 * Constructor.
	 * 
	 * @param vertex
	 *            The vertex of which distance is being held.
	 * @param distance
	 *            The distance to reach the vertex.
	 * @param edge
	 *            The last edge to reach the vertex.
	 */
	public VertexDistanceEdge(Vertex vertex, double distance, Edge edge) {
		this.VERTEX = vertex;
		this.distance = distance;
		this.edge = edge;
	}

	public boolean isSmallerThan(Comparable object) {
		if (object instanceof VertexDistanceEdge)
			if (this.distance < ((VertexDistanceEdge) object).distance)
				return true;

		return false;
	}
}

/**
 * Internal class that holds together a vertex, the distance of the path to
 * reach it from another considered vertex, and the last edge of such path.
 * 
 * This class also implements the interface Comparable, in order to provide a
 * "isSmallerThan" method that considers not only the distance of the path, but
 * also the idleness of the held vertex, in the comparison with a given object.
 */
final class IdlenessedVertexDistanceEdge implements Comparable {
	/** The vertex. */
	public final Vertex VERTEX;

	/** The distance to the vertex. */
	public double distance;

	/** The last edge to reach the vertex. */
	public Edge edge;

	/** The graph of the simulation. */
	public static Graph graph;

	/**
	 * Constructor.
	 * 
	 * @param vertex
	 *            The vertex of which distance is being held.
	 * @param distance
	 *            The distance to reach the vertex.
	 * @param edge
	 *            The last edge to reach the vertex.
	 */
	public IdlenessedVertexDistanceEdge(Vertex vertex, double distance,
			Edge edge) {
		this.VERTEX = vertex;
		this.distance = distance;
		this.edge = edge;
	}

	public boolean isSmallerThan(Comparable object) {
		if (object instanceof IdlenessedVertexDistanceEdge) {
			if (this.distance == 0)
				return true;
			else if (((IdlenessedVertexDistanceEdge) object).distance == 0)
				return false;
			else if (this.distance == Double.MAX_VALUE)
				return false;
			else if (((IdlenessedVertexDistanceEdge) object).distance == Double.MAX_VALUE)
				return true;

			// obtains the biggest and smallest idlenesses of the graph
			double[] bound_idlenesses = graph.getSmallestAndBiggestIdlenesses();

			// obtains the biggest and smallest distances of the graph
			double[] bound_distances = graph.getSmallestAndBiggestDistances();

			// obtains the value for this object
			double this_norm_idleness = 0;
			if (bound_idlenesses[0] < bound_idlenesses[1])
				this_norm_idleness = (this.VERTEX.getIdleness() - bound_idlenesses[0])
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
				other_norm_idleness = (((IdlenessedVertexDistanceEdge) object).VERTEX
						.getIdleness() - bound_idlenesses[0])
						* Math.pow((bound_idlenesses[1] - bound_idlenesses[0]),
								-1);

			double other_norm_distance = 0;
			if (bound_distances[0] < bound_distances[1])
				other_norm_distance = (bound_distances[1] - ((IdlenessedVertexDistanceEdge) object).distance)
						* Math.pow((bound_distances[1] - bound_distances[0]),
								-1);

			double other_value = Graph.getIdlenessesWeight()
					* other_norm_idleness + (1 - Graph.getIdlenessesWeight())
					* other_norm_distance;

			/*
			 * Specially here, if a vertex has greater value than another one,
			 * then it is smaller than the another one (so we can use a minimum
			 * heap).
			 */
			if (this_value > other_value)
				return true;
		}

		return false;
	}
}