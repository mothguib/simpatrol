/* Graph.java (2.0) */
package br.org.simpatrol.server.model.graph;

/* Imported classes and/or interfaces. */
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import br.org.simpatrol.server.model.interfaces.XMLable;
import br.org.simpatrol.server.model.stigma.Stigma;
import br.org.simpatrol.server.util.data_structures.MinimumHeap;

/**
 * Implements graphs that represent the territories to be patrolled.
 * 
 * @author Daniel Henriques Moreira (dhenriques@gmail.com)
 */
public class Graph implements XMLable {
	/* Attributes. */
	/** The label of the graph. */
	private String label;

	/** The set of vertexes of the graph. */
	private Set<Vertex> vertexes;

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
	 * @param vertexes
	 *            The vertexes of the graph.
	 * @throws GraphWithoutVertexesException
	 *             Mathematically, a graph must have at least one vertex.
	 */
	public Graph(String label, Set<Vertex> vertexes)
			throws GraphWithoutVertexesException {
		this.label = label;

		if (vertexes == null || vertexes.isEmpty())
			throw new GraphWithoutVertexesException();
		this.vertexes = vertexes;

		this.edges = new HashSet<Edge>();
		for (Vertex vertex : this.vertexes) {
			Set<Edge> edges = vertex.getEdges();
			if (edges != null)
				this.edges.addAll(edges);
		}

		if (this.edges.isEmpty())
			this.edges = null;

		this.stigmas = null;
	}

	/**
	 * Obtains the vertexes of the graph.
	 * 
	 * @return The vertexes of the graph.
	 */
	public Set<Vertex> getVertexes() {
		return this.vertexes;
	}

	/**
	 * Obtains the edges of the graph.
	 * 
	 * @return The edges of the graph.
	 */
	public Set<Edge> getEdges() {
		return this.edges;
	}

	/**
	 * Returns the stigmas of the graph.
	 * 
	 * @return The stigmas deposited on the graph.
	 */
	public Set<Stigma> getStigmas() {
		return this.stigmas;
	}

	/**
	 * Adds the given stigma to the graph.
	 * 
	 * @param stigma
	 *            The stigma to be added to the graph.
	 */
	public void addStigma(Stigma stigma) {
		if (this.stigmas == null)
			this.stigmas = new HashSet<Stigma>();
		this.stigmas.add(stigma);
	}

	/**
	 * Obtains a subgraph from the graph, starting from the given vertex and
	 * walking in depth-first mode, until the given depth is reached.
	 * 
	 * Only the enabled elements (vertexes and edges, visible or not) are added
	 * to the subgraph.
	 * 
	 * If the given depth is set to -1, the entire enabled graph's reaching
	 * elements are returned.
	 * 
	 * @param vertex
	 *            The starting point to obtain the subgraph.
	 * @param depth
	 *            The depth to reach when walking in depth-first mode.
	 * @param considerVisibility
	 *            TRUE if only the visible elements must be added to the answer,
	 *            FALSE if not.
	 * @return A subgraph starting from the given vertex and with the given
	 *         depth.
	 */
	public Graph getEnabledSubgraph(Vertex vertex, int depth,
			boolean considerVisibility) {
		// if the given depth is -1, returns the entire enabled graph
		if (depth == -1)
			return this.getEnabledGraph(vertex, considerVisibility);

		// if the given starting vertex is not enabled, returns null
		if ((considerVisibility && !vertex.isVisible())
				|| (vertex instanceof DynamicVertex && !((DynamicVertex) vertex)
						.isEnabled()))
			return null;

		// the answer for the method
		Set<Vertex> startingVertexSet = new HashSet<Vertex>();
		startingVertexSet.add(vertex.getCopyWithoutEdges());
		Graph answer = null;
		try {
			answer = new Graph(this.label, startingVertexSet);
		} catch (GraphWithoutVertexesException gwve) {
			// this won't happen, since the startingVertexSet has an
			// element...
			return null;
		}
		answer.edges = new HashSet<Edge>();

		// expands the answer until the given depth is reached
		this.addEnabledDepth(answer, vertex, depth, new HashSet<Vertex>(),
				considerVisibility);

		// if there are no edges in the answer, nullifies its set of edges
		if (answer.edges.isEmpty())
			answer.edges = null;

		// adds the stigmas to the answer (only the correct ones)
		if (this.stigmas != null)
			// for each stigma
			for (Stigma stigma : this.stigmas) {
				// tries to obtain its vertex
				Vertex stigmaVertex = stigma.getVertex();

				// if the vertex is valid (not null)
				if (stigmaVertex != null) {
					// tries to obtain a copy of it from the answer
					Vertex stigmaVertexCopy = answer.getVertex(stigmaVertex
							.getId());

					// if the copy is valid, adds a copy of the stigma to the
					// answer
					if (stigmaVertexCopy != null)
						answer.addStigma(stigma.getCopy(stigmaVertexCopy));
				}
				// if not, obtains its edge
				else {
					Edge stigmaEdge = stigma.getEdge();

					// tries to obtain a copy of such edge from the answer
					Edge stigmaEdgeCopy = answer.getEdge(stigmaEdge.getId());

					// if the copy is valid, adds a copy of the stigma to
					// the answer
					if (stigmaEdgeCopy != null)
						answer.addStigma(stigma.getCopy(stigmaEdgeCopy));
				}
			}

		// returns the answer
		return answer;
	}

	/**
	 * Returns the minimum path between two given vertexes, using the Dijkstra's
	 * algorithm.
	 * 
	 * Only the enabled elements (vertex and edges, visible or not) are
	 * considered to build the path.
	 * 
	 * @param beginVertex
	 *            The first vertex of the desired path.
	 * @param endVertex
	 *            The last vertex of the desired path.
	 * @return The minimum path between two given vertexes.
	 */
	public Graph getEnabledDijkstraPath(Vertex beginVertex, Vertex endVertex) {
		// if one of the given vertexes is not enabled, returns null
		if ((beginVertex instanceof DynamicVertex && !((DynamicVertex) beginVertex)
				.isEnabled())
				|| (endVertex instanceof DynamicVertex && !((DynamicVertex) endVertex)
						.isEnabled()))
			return null;

		// for each enabled vertex of the graph, correlates it with its distance
		// to the beginVertex, as well as the last edge to reach it
		List<VertexDistanceEdge> vertexesWithDistances = new ArrayList<VertexDistanceEdge>();

		for (Vertex vertex : this.vertexes)
			if (!(vertex instanceof DynamicVertex)
					|| ((DynamicVertex) vertex).isEnabled())
				if (vertex.equals(beginVertex))
					vertexesWithDistances.add(new VertexDistanceEdge(vertex, 0,
							null));
				else
					vertexesWithDistances.add(new VertexDistanceEdge(vertex,
							Double.MAX_VALUE, null));

		// mounts a heap with the vertex-distance-edge trios
		MinimumHeap<VertexDistanceEdge> heap = new MinimumHeap<VertexDistanceEdge>(
				vertexesWithDistances);

		// while the heap is not empty
		while (!heap.isEmpty()) {
			// removes the minimum element of the heap
			VertexDistanceEdge minimum = heap.removeSmallest();

			// if the distance set to the minimum element is the maximum
			// possible double value, return null (i.e. the graph is
			// disconnected, and the endVertex is unreachable)
			if (minimum.distance == Double.MAX_VALUE)
				return null;

			// if the minimum element has the endVertex, quits the loop
			if (minimum.VERTEX.equals(endVertex))
				break;

			// obtains the current vertex to be expanded
			Vertex currentVertex = minimum.VERTEX;

			// obtains the neighborhood where the current vertex is an emitter
			Set<Vertex> neighborhood = currentVertex
					.getCollectingNeighborhood();

			// for each vertex of the neighborhood
			if (neighborhood != null)
				for (Vertex neighbor : neighborhood) {
					// if the current neighbor is enabled
					if (!(neighbor instanceof DynamicVertex)
							|| ((DynamicVertex) neighbor).isEnabled()) {
						// obtains all the edges between the current vertex and
						// the current neighbor, of which emitter is the current
						// vertex
						Set<Edge> edges = currentVertex
								.getConnectingOutEdges(neighbor);

						// finds the smallest enabled edge
						Edge smallestEdge = null;
						double smallestLength = Double.MAX_VALUE;

						if (edges != null)
							for (Edge currentEdge : edges) {
								double currentLength = currentEdge.getLength();

								if (currentEdge.isEnabled()
										&& currentLength < smallestLength) {
									smallestEdge = currentEdge;
									smallestLength = currentLength;
								}
							}

						// if there's a smallest edge
						if (smallestEdge != null) {
							// obtains the vertex-distance-edge trio of the
							// current neighbor
							VertexDistanceEdge neighborWithDistance = null;

							for (VertexDistanceEdge currentTrio : vertexesWithDistances) {
								VertexDistanceEdge trio = currentTrio;

								if (trio.VERTEX.equals(neighbor)) {
									neighborWithDistance = trio;
									break;
								}
							}

							// verifies if it's necessary to update the
							// neighbor's trio
							if (neighborWithDistance.distance > minimum.distance
									+ smallestEdge.getLength()) {
								neighborWithDistance.distance = minimum.distance
										+ smallestEdge.getLength();
								neighborWithDistance.edge = smallestEdge;
							}
						}
					}
				}

			// assures the heap structure is correct
			heap.assureMinimumHeap();
		}

		// mounts the answer of the method...
		// sets the current vertex being added to the answer as the endVertex
		Vertex currentVertex = endVertex;

		// obtains a copy of the current vertex
		Vertex currentVertexCopy = currentVertex.getCopyWithoutEdges();

		// the answer of the method
		Set<Vertex> vertexesAnswer = new HashSet<Vertex>();
		vertexesAnswer.add(currentVertexCopy);
		Graph answer = null;
		try {
			answer = new Graph("dijkstra's path", vertexesAnswer);
		} catch (GraphWithoutVertexesException gwve) {
			// this won't happen, since the vertexesAnswer set has an element...
			return null;
		}
		answer.edges = new HashSet<Edge>();

		// keep on mounting the answer...
		while (true) {
			// finds the vertex-distance-edge trio of the current vertex
			VertexDistanceEdge currentVertexWithDistance = null;
			for (VertexDistanceEdge currentTrio : vertexesWithDistances) {
				VertexDistanceEdge trio = currentTrio;

				if (trio.VERTEX.equals(currentVertex)) {
					currentVertexWithDistance = trio;
					break;
				}
			}

			// sets the nextEdge
			Edge nextEdge = currentVertexWithDistance.edge;

			// if the next edge is valid
			if (nextEdge != null) {
				// obtains the next vertex
				Vertex nextVertex = nextEdge.getOtherVertex(currentVertex);

				// obtains copies of the next edge and next vertex
				Vertex nextVertexCopy = nextVertex.getCopyWithoutEdges();
				Edge nextEdgeCopy = nextEdge.getCopy(currentVertexCopy,
						nextVertexCopy);

				// adds the copies to the answer
				answer.vertexes.add(nextVertexCopy);
				answer.edges.add(nextEdgeCopy);

				// updates the current vertex and current vertex copy
				currentVertex = nextVertex;
				currentVertexCopy = nextVertexCopy;
			}
			// if not, break the loop
			else
				break;
		}

		if (answer.edges.isEmpty())
			answer.edges = null;

		// returns the answer
		return answer;
	}

	/**
	 * Obtains the enabled elements connected with the given starting vertex.
	 * 
	 * @param startingVertex
	 *            The vertex to start walking into the graph in a breadth-first
	 *            manner.
	 * @param considerVisibility
	 *            TRUE if only the visible elements must be added to the answer,
	 *            FALSE if not.
	 * @return The obtained graph.
	 */
	private Graph getEnabledGraph(Vertex startingVertex,
			boolean considerVisibility) {
		// if the starting vertex is not enabled, returns null
		if ((considerVisibility && !startingVertex.isVisible())
				|| (startingVertex instanceof DynamicVertex && !((DynamicVertex) startingVertex)
						.isEnabled()))
			return null;

		// holds the vertexes to be treated
		List<Vertex> pendingVertexes = new LinkedList<Vertex>();

		// holds the vertexes already treated
		List<Vertex> expandedVertexes = new LinkedList<Vertex>();

		// the answer for the method
		Set<Vertex> initialVertexes = new HashSet<Vertex>();
		initialVertexes.add(startingVertex.getCopyWithoutEdges());
		Graph answer = null;
		try {
			answer = new Graph(this.label, initialVertexes);
		} catch (GraphWithoutVertexesException gwve) {
			// this won't happen, since the startingVertexSet has an
			// element...
			return null;
		}
		answer.edges = new HashSet<Edge>();

		// adds the starting vertex to the ones to be treated
		pendingVertexes.add(startingVertex);

		// while there are still vertexes to treat
		while (pendingVertexes.size() > 0) {
			// removes the current vertex from the ones to be treated
			Vertex currentVertex = pendingVertexes.remove(0);

			// if it was not expanded yet
			if (!expandedVertexes.contains(currentVertex)) {
				// adds it to the expanded ones
				expandedVertexes.add(currentVertex);

				// obtains its copy from the answer
				Vertex currentVertexCopy = answer.getVertex(currentVertex
						.getId());

				// obtains its neighborhood
				Set<Vertex> neighborhood = currentVertex.getNeighborhood();

				// for each neighbor
				if (neighborhood != null)
					for (Vertex neighbor : neighborhood) {
						// if the current neighbor is enabled
						if ((!considerVisibility || neighbor.isVisible())
								&& (!(neighbor instanceof DynamicVertex) || ((DynamicVertex) neighbor)
										.isEnabled())) {
							// if it is not in the already expanded ones
							if (!expandedVertexes.contains(neighbor)) {
								// tries to obtain a copy of it from the answer
								Vertex currentNeighborCopy = answer
										.getVertex(neighbor.getId());

								// registers if there's already a copy of the
								// current neighbor in the answer
								boolean neighborCopyExists = true;

								// if the copy is not valid, creates a new one
								if (currentNeighborCopy == null) {
									currentNeighborCopy = neighbor
											.getCopyWithoutEdges();
									neighborCopyExists = false;
								}

								// obtains all the edges between the current
								// vertex and its current neighbor
								Set<Edge> edges = currentVertex
										.getConnectingEdges(neighbor);

								// registers if there's some enabled edge
								// between the current vertex and its current
								// neighbor
								boolean enabledEdgeExists = false;

								// for each edge
								if (edges != null)
									for (Edge nearEdge : edges) {
										// if the current edge is enabled
										if ((!considerVisibility || nearEdge
												.isVisible())
												&& nearEdge.isEnabled()) {
											enabledEdgeExists = true;

											// obtains a copy of it
											Edge edgeCopy = null;
											if (currentVertex
													.isEmitterOf(nearEdge))
												edgeCopy = nearEdge.getCopy(
														currentVertexCopy,
														currentNeighborCopy);
											else
												edgeCopy = nearEdge.getCopy(
														currentNeighborCopy,
														currentVertexCopy);

											// adds to the answer
											answer.edges.add(edgeCopy);
										}
									}

								// if there's some enabled edge
								if (enabledEdgeExists) {
									// if the current copy is not in the answer
									// yet, adds it
									if (!neighborCopyExists)
										answer.vertexes
												.add(currentNeighborCopy);

									// adds the current copy to the pending ones
									pendingVertexes.add(neighbor);
								}
							}
						}
					}
			}
		}

		// if there are no edges in the answer, nullifies it
		if (answer.edges.isEmpty())
			answer.edges = null;

		// adds the stigmas to the answer (only the correct ones)
		if (this.stigmas != null)
			for (Stigma stigma : this.stigmas) {
				// tries to obtain its vertex
				Vertex stigmaVertex = stigma.getVertex();

				// if the vertex is valid (not null)
				if (stigmaVertex != null) {
					// tries to obtain a copy of it from the answer
					Vertex stigmaVertexCopy = answer.getVertex(stigmaVertex
							.getId());

					// if the copy is valid, adds a copy of the stigma to the
					// answer
					if (stigmaVertexCopy != null)
						answer.addStigma(stigma.getCopy(stigmaVertexCopy));
				}
				// if not, obtains its edge
				else {
					Edge stigmaEdge = stigma.getEdge();

					// tries to obtain a copy of it from the answer
					Edge stigmaEdgeCopy = answer.getEdge(stigmaEdge.getId());

					// if the copy is valid, adds a copy of the stigma to
					// the answer
					if (stigmaEdgeCopy != null)
						answer.addStigma(stigma.getCopy(stigmaEdgeCopy));
				}
			}

		// returns the answer for the method
		return answer;
	}

	/**
	 * Expands the given subgraph until the given depth is reached, in a
	 * depth-first recursive manner. The vertex where the expansion is started
	 * and a set of already expanded vertexes must be informed.
	 * 
	 * Only the enabled elements (vertexes and edges) are respectively expanded
	 * and considered.
	 * 
	 * @param subgraph
	 *            The subgraph to be expanded.
	 * @param startingVertex
	 *            The vertex where the expansion is started.
	 * @param depth
	 *            The depth limit for the expansion.
	 * @param alreadyExpandedVertexes
	 *            The vertexes not to be expanded.
	 * @param considerVisibility
	 *            TRUE if only the visible elements must be added to the answer,
	 *            FALSE if not.
	 */
	private void addEnabledDepth(Graph subgraph, Vertex startingVertex,
			int depth, Set<Vertex> alreadyExpandedVertexes,
			boolean considerVisibility) {
		// if the starting vertex is not enabled, quits the method
		if ((considerVisibility && !startingVertex.isVisible())
				|| (startingVertex instanceof DynamicVertex && !((DynamicVertex) startingVertex)
						.isEnabled()))
			return;

		// if the depth is valid
		if (depth > -1) {
			// tries to obtain a copy of the starting vertex from the given
			// subgraph
			Vertex startingVertexCopy = subgraph.getVertex(startingVertex
					.getId());

			// if the copy is null, creates it and adds to the subgraph
			if (startingVertexCopy == null) {
				startingVertexCopy = startingVertex.getCopyWithoutEdges();
				subgraph.vertexes.add(startingVertexCopy);
			}

			// adds the starting vertex to the vertexes already expanded
			alreadyExpandedVertexes.add(startingVertex);

			// if there's still depth to expand in the given subgraph
			if (depth > 0) {
				// obtains the neighborhood of the starting vertex
				Set<Vertex> neighborhood = startingVertex.getNeighborhood();

				// for each vertex of the neighborhood
				if (neighborhood != null)
					for (Vertex neighbor : neighborhood) {
						// if the current vertex is enabled
						if ((!considerVisibility || neighbor.isVisible())
								&& (!(neighbor instanceof DynamicVertex) || ((DynamicVertex) neighbor)
										.isEnabled())) {
							// if it is not in the vertexes already expanded
							if (!alreadyExpandedVertexes.contains(neighbor)) {
								// tries to obtain a copy of it from the given
								// subgraph
								Vertex neighborCopy = subgraph
										.getVertex(neighbor.getId());

								// registers if there's already a copy of the
								// current neighbor in the given subgraph
								boolean neighborCopyExists = true;

								// if the copy is null, creates it
								if (neighborCopy == null) {
									neighborCopy = neighbor
											.getCopyWithoutEdges();
									neighborCopyExists = false;
								}

								// obtains the edges between the starting vertex
								// and its current neighbor
								Set<Edge> edges = startingVertex
										.getConnectingEdges(neighbor);

								// registers if some of the connecting edges is
								// enabled
								boolean enabledEdgeExists = false;

								// for each edge
								if (edges != null)
									for (Edge nearEdge : edges) {
										// if the current edge is enabled
										if ((!considerVisibility || nearEdge
												.isVisible())
												&& nearEdge.isEnabled()) {
											enabledEdgeExists = true;

											// if there is not a copy of it in
											// the given subgraph
											if (subgraph.getEdge(nearEdge
													.getId()) == null) {
												// creates the copy and adds to
												// the subgraph
												Edge currentEdgeCopy = null;
												if (startingVertex
														.isEmitterOf(nearEdge))
													currentEdgeCopy = nearEdge
															.getCopy(
																	startingVertexCopy,
																	neighborCopy);
												else
													currentEdgeCopy = nearEdge
															.getCopy(
																	neighborCopy,
																	startingVertexCopy);

												subgraph.edges
														.add(currentEdgeCopy);
											}
										}
									}

								// if there's some enabled edge
								if (enabledEdgeExists) {
									// if the copy of the current neighbor is
									// not in the subgraph, adds it
									if (!neighborCopyExists)
										subgraph.vertexes.add(neighborCopy);

									// calls this method recursively, starting
									// from the current neighbor
									this.addEnabledDepth(subgraph, neighbor,
											depth - 1, alreadyExpandedVertexes,
											considerVisibility);
								}
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
		for (Vertex vertex : this.vertexes)
			if (vertex.getId().equals(id))
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
				if (edge.getId().equals(id))
					return edge;

		return null;
	}

	public String fullToXML() {
		// holds the answer being constructed
		StringBuffer buffer = new StringBuffer();

		// fills the buffer
		buffer.append("<graph label=\"" + this.label + "\">");

		// inserts the vertexes
		for (Vertex vertex : this.vertexes)
			buffer.append(vertex.fullToXML());

		// inserts the edges
		if (this.edges != null)
			for (Edge edge : this.edges)
				buffer.append(edge.fullToXML());

		// inserts the stigmas
		if (this.stigmas != null)
			for (Stigma stigma : this.stigmas)
				buffer.append(stigma.fullToXML());

		// finishes the buffer content
		buffer.append("</graph>");

		// returns the buffer content
		return buffer.toString();
	}

	public String reducedToXML() {
		// holds the answer being constructed
		StringBuffer buffer = new StringBuffer();

		// fills the buffer
		buffer.append("<graph label=\"" + this.label + "\">");

		// inserts the lighter version of the vertexes
		for (Vertex vertex : this.vertexes)
			buffer.append(vertex.reducedToXML());

		// inserts the lighter version of the edges
		if (this.edges != null)
			for (Edge edge : this.edges)
				buffer.append(edge.reducedToXML());

		// finishes the buffer content
		buffer.append("</graph>");

		// returns the buffer content
		return buffer.toString();
	}

	public String getId() {
		// a graph doesn't need an id
		return null;
	}
}

/**
 * Internal class that holds together a vertex, the distance of the path to
 * reach it from another considered vertex, and the last edge of such path.
 */
final class VertexDistanceEdge implements Comparable<VertexDistanceEdge> {
	/** The vertex. */
	final Vertex VERTEX;

	/** The distance to the vertex. */
	double distance;

	/** The last edge to reach the vertex. */
	Edge edge;

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
	VertexDistanceEdge(Vertex vertex, double distance, Edge edge) {
		this.VERTEX = vertex;
		this.distance = distance;
		this.edge = edge;
	}

	public int compareTo(VertexDistanceEdge o) {
		return (int) (this.distance - o.distance);
	}
}