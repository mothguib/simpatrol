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

/**
 * Implements graphs that represent the territories to be patrolled.
 * 
 * @developer New dynamic objects that eventually are part of a graph, must
 *            change this class.
 */
public final class Graph {
	/* Attributes. */
	/** The label of the graph. */
	private String label;

	/** The set of vertexes of the graph. */
	private Set<Vertex> vertexes;

	/** The set of edges of the graph. */
	private Set<Edge> edges;

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
		Object[] vertexes_array = this.vertexes.toArray();
		for (int i = 0; i < vertexes_array.length; i++) {
			Vertex current_vertex = (Vertex) vertexes_array[i];

			Edge[] current_edges = current_vertex.getEdges();
			for (int j = 0; j < current_edges.length; j++)
				if (this.vertexes.contains(current_edges[j]
						.getOtherVertex(current_vertex)))
					this.edges.add(current_edges[j]);
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
		Object[] vertexes_array = this.vertexes.toArray();
		Vertex[] answer = new Vertex[vertexes_array.length];

		for (int i = 0; i < answer.length; i++)
			answer[i] = (Vertex) vertexes_array[i];

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
	 * Verifies if the given vertex is part of the graph.
	 * 
	 * @param vertex
	 *            The vertex to be verified.
	 * @return TRUE if the vertex is part of the graph, FALSE if not.
	 */
	public boolean hasVertex(Vertex vertex) {
		Object[] vertexes_array = this.vertexes.toArray();
		for (int i = 0; i < vertexes_array.length; i++)
			if (((Vertex) vertexes_array[i]).equals(vertex))
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
	 * Obtains a subgraph from the graph, starting from the given vertex and
	 * walking in depth-first mode, until the given depth is reached.
	 * 
	 * If the given depth is set to -1, the entire graph's reaching elements are
	 * returned.
	 * 
	 * @param vertex
	 *            The starting point to obtain the subgraph.
	 * @param depth
	 *            The depth to reach when walking in depth-first mode.
	 * @return A subgraph starting from the given vertex and with the given
	 *         depth.
	 */
	public Graph getSubgraph(Vertex vertex, int depth) {
		// if the given depth is -1, returns the entire visible graph
		if (depth == -1)
			return this.getGraph(vertex);

		// the answer for the method
		Vertex[] starting_vertex = { vertex.getCopy() };
		Graph answer = new Graph(this.label, starting_vertex);
		answer.edges = new HashSet<Edge>();

		// expands the answer until the given depth is reached
		this.addDepth(answer, vertex, depth, new HashSet<Vertex>());

		// if there are no edges in the answer, nullifies its set of edges
		if (answer.edges.size() == 0)
			answer.edges = null;

		// returns the answer
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
		// for each enabled vertex of the graph, correlates it with its distance
		// to
		// the begin_vertex, as well as the last edge to reach it
		LinkedList<VertexDistanceEdge> vertexes_with_distances_list = new LinkedList<VertexDistanceEdge>();

		Object[] vertexes_array = this.vertexes.toArray();
		for (int i = 0; i < vertexes_array.length; i++) {
			Vertex vertex = (Vertex) vertexes_array[i];

			if (vertex.equals(begin_vertex))
				vertexes_with_distances_list.add(new VertexDistanceEdge(vertex,
						0, null));
			else
				vertexes_with_distances_list.add(new VertexDistanceEdge(vertex,
						Double.MAX_VALUE, null));
		}

		// mounts a heap with the vertex-distance-edge trios
		Object[] vertexes_with_distances_array = vertexes_with_distances_list
				.toArray();
		VertexDistanceEdge[] vertexes_with_distance = new VertexDistanceEdge[vertexes_with_distances_array.length];
		for (int i = 0; i < vertexes_with_distances_array.length; i++)
			vertexes_with_distance[i] = (VertexDistanceEdge) vertexes_with_distances_array[i];

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

			// obtains the neighbourhood where the current vertex is an emitter
			Vertex[] neighbourhood = current_vertex.getCollectorNeighbourhood();

			// for each vertex of the neighbourhood
			for (int i = 0; i < neighbourhood.length; i++) {
				// obtains all the edges between the current vertex and the
				// current
				// neighbour, of which emitter is the current vertex
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
					// neighbour
					VertexDistanceEdge neighbour_with_distance = null;

					for (int j = 0; j < vertexes_with_distance.length; j++)
						if (vertexes_with_distance[j].VERTEX
								.equals(neighbourhood[i])) {
							neighbour_with_distance = vertexes_with_distance[j];
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

				// atualizes the current vertex and current vertex copy
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
	 * Obtains the elements connected with the given starting vertex.
	 * 
	 * @param starting_vertex
	 *            The vertex to start walking into the graph in a breadth-first
	 *            manner.
	 * @return The obtained graph.
	 */
	private Graph getGraph(Vertex starting_vertex) {
		// holds the vertexes to be treated
		List<Vertex> pending_vertexes = new LinkedList<Vertex>();

		// holds the vertexes already treated
		List<Vertex> expanded_vertexes = new LinkedList<Vertex>();

		// the answer for the method
		Vertex[] initial_vertexes = { starting_vertex.getCopy() };
		Graph answer = new Graph(this.label, initial_vertexes);
		answer.edges = new HashSet<Edge>();

		// adds the starting vertex to the ones to be treated
		pending_vertexes.add(starting_vertex);

		// while there are still vertexes to treat
		while (pending_vertexes.size() > 0) {
			// removes the current vertex from the ones to be treated
			Vertex current_vertex = pending_vertexes.remove(0);

			// if it was not expanded yet
			if (!expanded_vertexes.contains(current_vertex)) {
				// adds it to the expanded ones
				expanded_vertexes.add(current_vertex);

				// obtains its copy from the answer
				Vertex current_vertex_copy = answer.getVertex(current_vertex
						.getObjectId());

				// obtains its neighbourhood
				Vertex[] neighbourhood = current_vertex.getNeighbourhood();

				// for each neighbour
				for (int i = 0; i < neighbourhood.length; i++) {
					// if it is not in the already expanded ones
					if (!expanded_vertexes.contains(neighbourhood[i])) {
						// tries to obtain a copy of it from the answer
						Vertex current_neighbour_copy = answer
								.getVertex(neighbourhood[i].getObjectId());

						// registers if there's already a copy of the
						// current neighbour in
						// the answer
						boolean neighbour_copy_exists = true;

						// if the copy is not valid, creates a new one
						if (current_neighbour_copy == null) {
							current_neighbour_copy = neighbourhood[i].getCopy();
							neighbour_copy_exists = false;
						}

						// obtains all the edges between the current
						// vertex and its current neighbour
						Edge[] edges = current_vertex
								.getConnectingEdges(neighbourhood[i]);

						// registers if there's some visivible and enabled
						// edge between
						// the current vertex and its current neighbour
						boolean visible_edge_exists = (edges.length > 0);

						// for each edge
						for (int j = 0; j < edges.length; j++) {
							// obtains a copy of it
							Edge edge_copy = null;
							if (current_vertex.isEmitterOf(edges[j]))
								edge_copy = edges[j].getCopy(
										current_vertex_copy,
										current_neighbour_copy);
							else
								edge_copy = edges[j].getCopy(
										current_neighbour_copy,
										current_vertex_copy);

							// adds to the answer
							answer.edges.add(edge_copy);
						}

						// if there's some visible and enabled edge
						if (visible_edge_exists) {
							// if the current copy is not in the answer yet,
							// adds it
							if (!neighbour_copy_exists)
								answer.vertexes.add(current_neighbour_copy);

							// adds the current copy to the pending ones
							pending_vertexes.add(neighbourhood[i]);
						}
					}

				}
			}
		}

		// if there are no egdes in the answer, nullifies it
		if (answer.edges.size() == 0)
			answer.edges = null;

		// returns the answer for the method
		return answer;
	}

	/**
	 * Expands the given subgraph until the given depth is reached, in a
	 * depth-first recursive manner. The vertex where the expansion is started
	 * and a set of already expanded vertexes must be informed.
	 * 
	 * @param subgraph
	 *            The subgraph to be expanded.
	 * @param starting_vertex
	 *            The vertex where the expansion is started.
	 * @param depth
	 *            The depth limit for the expansion.
	 * @param already_expanded_vertexes
	 *            The vertexes not to be expanded.
	 */
	private void addDepth(Graph subgraph, Vertex starting_vertex, int depth,
			Set<Vertex> already_expanded_vertexes) {
		// if the depth is valid
		if (depth > -1) {
			// tries to obtain a copy of the starting vertex from the given
			// subgraph
			Vertex starting_vertex_copy = subgraph.getVertex(starting_vertex
					.getObjectId());

			// if the copy is null, creates it and adds to the subgraph
			if (starting_vertex_copy == null) {
				starting_vertex_copy = starting_vertex.getCopy();
				subgraph.vertexes.add(starting_vertex_copy);
			}

			// adds the starting vertex to the vertexes already expanded
			already_expanded_vertexes.add(starting_vertex);

			// if there's still depth to expand in the given subgraph
			if (depth > 0) {
				// obtains the neighbourhood of the starting vertex
				Vertex[] neighbourhood = starting_vertex.getNeighbourhood();

				// for each vertex of the neighbourhood
				for (int i = 0; i < neighbourhood.length; i++) {
					// if it isn't in the vertexes already expandend
					if (!already_expanded_vertexes.contains(neighbourhood[i])) {
						// tries to obtain a copy of it from the given
						// subgraph
						Vertex neighbour_copy = subgraph
								.getVertex(neighbourhood[i].getObjectId());

						// registers if there's already a copy of the
						// current neighbour in
						// the given subgraph
						boolean neighbour_copy_exists = true;

						// if the copy is null, creates it
						if (neighbour_copy == null) {
							neighbour_copy = neighbourhood[i].getCopy();
							neighbour_copy_exists = false;
						}

						// obtains the edges between the starting vertex and
						// its
						// current neighbour
						Edge[] edges = starting_vertex
								.getConnectingEdges(neighbourhood[i]);

						// registers if some of the connecting edges is
						// visible and enabled
						boolean visible_edge_exists = (edges.length > 0);

						// for each edge
						for (int j = 0; j < edges.length; j++) {
							// if there isn't a copy of it in the given
							// subgraph
							if (subgraph.getEdge(edges[j].getObjectId()) == null) {
								// creates the copy and adds to the
								// subgraph
								Edge current_edge_copy = null;
								if (starting_vertex.isEmitterOf(edges[j]))
									current_edge_copy = edges[j].getCopy(
											starting_vertex_copy,
											neighbour_copy);
								else
									current_edge_copy = edges[j].getCopy(
											neighbour_copy,
											starting_vertex_copy);

								subgraph.edges.add(current_edge_copy);
							}
						}

						// if there's some visible edge
						if (visible_edge_exists) {
							// if the copy of the current neighbour is not
							// in the subgraph,
							// adds it
							if (!neighbour_copy_exists)
								subgraph.vertexes.add(neighbour_copy);

							// calls this method recurvely, starting from
							// the current
							// neighbour
							this.addDepth(subgraph, neighbourhood[i],
									depth - 1, already_expanded_vertexes);
						}
					}
				}
			}
		}
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
		Object[] vertexes_array = this.vertexes.toArray();
		for (int i = 0; i < vertexes_array.length; i++) {
			Vertex current_vertex = (Vertex) vertexes_array[i];
			if (current_vertex.getObjectId().equals(id))
				return current_vertex;
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
}

/**
 * Internal class that holds together a vertex, the distance of the path to
 * reach it from another considered vertex, and the last edge of such path.
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