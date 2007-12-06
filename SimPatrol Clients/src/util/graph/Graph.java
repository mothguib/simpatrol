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
	private static final double IDLENESSES_WEIGHT = 0.5;

	/**
	 * Table that holds, for each vertex, its list of distances to the other
	 * vertexes of the graph.
	 */
	private static List<DistancesList> distances_table;

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
		IdlenessedVertexDistanceEdge.graph = this;

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
	 * Fills the table that holds, for each vertex, its list of distances to the
	 * other vertexes of the graph.
	 */
	private void calculateDistances() {
		if (distances_table == null)
			distances_table = new LinkedList<DistancesList>();

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
								.get(i).DISTANCE);

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
			}

			// adds the list to the table of distances
			distances_table.add(list);
		}
	}

	/** Returns the smallest and biggest idlenesses of the graph. */
	public int[] getSmallestAndBiggestIdlenesses() {
		int smallest_idleness = Integer.MAX_VALUE;
		int biggest_idleness = -1;

		Object[] vertexes_array = this.vertexes.toArray();
		for (int i = 0; i < vertexes_array.length; i++) {
			int idleness = ((Vertex) vertexes_array[i]).getIdleness();

			if (idleness > biggest_idleness)
				biggest_idleness = idleness;

			if (idleness < smallest_idleness)
				smallest_idleness = idleness;
		}

		int[] answer = { smallest_idleness, biggest_idleness };
		return answer;
	}

	/**
	 * Returns the smallest and biggest distances among the vertexes of the
	 * graph.
	 */
	public double[] getSmallestAndBiggestDistances() {
		double[] answer = { 0, 0 };

		if (distances_table == null)
			return answer;

		if (distances_table.isEmpty())
			this.calculateDistances();

		double smallest_distance = Double.MAX_VALUE;
		double biggest_distance = -1;

		for (int i = 0; i <distances_table.size(); i++)
			for (int j = i; j < distances_table.size(); j++) {
				double distance = distances_table.get(i).DISTANCES_LIST
						.get(j).DISTANCE;

				if (distance > biggest_distance)
					biggest_distance = distance;

				if (distance < smallest_distance)
					smallest_distance = distance;
			}

		answer[0] = smallest_distance;
		answer[1] = biggest_distance;
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
		// for each vertex of the graph, correlates it with its distance
		// to the begin_vertex, as well as the last edge to reach it
		LinkedList<IdlenessedVertexDistanceEdge> vertexes_with_distances_list = new LinkedList<IdlenessedVertexDistanceEdge>();

		Object[] vertexes_array = this.vertexes.toArray();
		for (int i = 0; i < vertexes_array.length; i++) {
			Vertex vertex = (Vertex) vertexes_array[i];

			if (vertex.equals(begin_vertex))
				vertexes_with_distances_list
						.add(new IdlenessedVertexDistanceEdge(vertex, 0, null));
			else
				vertexes_with_distances_list
						.add(new IdlenessedVertexDistanceEdge(vertex,
								Double.MAX_VALUE, null));
		}

		// mounts a heap with the vertex-distance-edge trios
		Object[] vertexes_with_distances_array = vertexes_with_distances_list
				.toArray();
		IdlenessedVertexDistanceEdge[] vertexes_with_distance = new IdlenessedVertexDistanceEdge[vertexes_with_distances_array.length];
		for (int i = 0; i < vertexes_with_distances_array.length; i++)
			vertexes_with_distance[i] = (IdlenessedVertexDistanceEdge) vertexes_with_distances_array[i];

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
					IdlenessedVertexDistanceEdge neighbour_with_distance = null;

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

	/* Methods. */
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

			// obtains the biggest and smallest idlenesses of the graph
			int[] bound_idlenesses = graph.getSmallestAndBiggestIdlenesses();

			// obtains the biggest and smallest distances of the graph
			double[] bound_distances = graph.getSmallestAndBiggestDistances();

			// obtains the value for this object
			double this_norm_idleness = 0;
			if (bound_idlenesses[0] < bound_idlenesses[1])
				this_norm_idleness = (bound_idlenesses[1] - this.VERTEX
						.getIdleness())
						* Math.pow((bound_idlenesses[1] - bound_idlenesses[0]),
								-1);

			double this_norm_distance = 0;
			if (bound_distances[0] < bound_distances[1])
				this_norm_distance = (this.distance - bound_distances[0])
						* Math.pow((bound_distances[1] - bound_distances[0]),
								-1);

			double this_value = Graph.getIdlenessesWeight()
					* this_norm_idleness + (1 - Graph.getIdlenessesWeight())
					* this_norm_distance;

			// obtains the value for the other object
			double other_norm_idleness = 0;
			if (bound_idlenesses[0] < bound_idlenesses[1])
				other_norm_idleness = (bound_idlenesses[1] - ((IdlenessedVertexDistanceEdge) object).VERTEX
						.getIdleness())
						* Math.pow((bound_idlenesses[1] - bound_idlenesses[0]),
								-1);

			double other_norm_distance = 0;
			if (bound_distances[0] < bound_distances[1])
				other_norm_distance = (((IdlenessedVertexDistanceEdge) object).distance - bound_distances[0])
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