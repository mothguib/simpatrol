/* Graph.java */

/* The package of this class. */
package model.graph;

/* Imported classes and/or interfaces. */
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import view.XMLable;
import model.interfaces.Dynamic;

/** Implements graphs that represent the territories to be
 *  patrolled.
 *  
 *  @developer New dynamic objects that eventually are part of a graph, must change this class. */
public final class Graph implements XMLable {
	/* Attributes. */
	/** The set of vertexes of the graph. */
	private Set<Vertex> vertexes;
	
	/** The set of edges of the graph. */
	private Set<Edge> edges;

	/** The label of the graph. */
	private String label;
	
	/* Methods. */
	/** Constructor.
	 * 
	 *  @param label The label of the graph.
	 *  @param vertexes The vertexes of the graph. */
	public Graph(String label, Vertex[] vertexes) {
		this.label = label;
		
		this.vertexes = new HashSet<Vertex>();
		for(int i = 0; i < vertexes.length; i++)
			this.vertexes.add(vertexes[i]);
		
		this.edges = new HashSet<Edge>();
		
		// for each vertex, adds its edges to the set of edges
		Object[] vertexes_array = this.vertexes.toArray();
		for(int i = 0; i < vertexes_array.length; i++) {
			Vertex current_vertex = (Vertex) vertexes_array[i];
			
			Edge[] current_edges = current_vertex.getEdges();
			for(int j = 0; j < current_edges.length; j++)
				if(this.vertexes.contains(current_edges[j].getOtherVertex(current_vertex)))
					this.edges.add(current_edges[j]);
		}
		
		if(this.edges.size() == 0)
			this.edges = null;
	}
	
	/** Obtains the vertexes of the graph.
	 * 
	 *  @return The vertexes of the graph. */
	public Vertex[] getVertexes() {
		Object[] vertexes_array = this.vertexes.toArray();
		Vertex[] answer = new Vertex[vertexes_array.length];
		
		for(int i = 0; i < answer.length; i++)
			answer[i] = (Vertex) vertexes_array[i];
		
		return answer;
	}
	
	/** Obtains the edges of the graph.
	 * 
	 *  @return The edges of the graph. */
	public Edge[] getEdges() {
		Object[] edges_array = this.edges.toArray();
		Edge[] answer = new Edge[edges_array.length];
		
		for(int i = 0; i < answer.length; i++)
			answer[i] = (Edge) edges_array[i];
		
		return answer;
	}	
	
	/** Obtains the dynamic objects of the graph.
	 * 
	 *  @return The dynamic vertexes and edges.
	 *  @developer New dynamic objects that eventually are part of a graph, must change this method. */
	public Dynamic[] getDynamicObjects() {
		// the set of dynamic objects
		Set<Dynamic> dynamic_objects = new HashSet<Dynamic>();
		
		// searches for dynamic vertexes
		Object[] vertexes_array = this.vertexes.toArray();
		for(int i = 0; i < vertexes_array.length; i++)
			if(vertexes_array[i] instanceof Dynamic)
				dynamic_objects.add((Dynamic) vertexes_array[i]);
		
		// searches for dynamic edges
		if(this.edges != null) {
			Object[] edges_array = this.edges.toArray();
			for(int i = 0; i < edges_array.length; i++)
				if(edges_array[i] instanceof Dynamic)
					dynamic_objects.add((Dynamic) edges_array[i]);
		}
		
		// developer: new dynamic objects must be searched here
		
		// returns the answer
		Object[] dynamic_objects_array = dynamic_objects.toArray();
		Dynamic[] answer = new Dynamic[dynamic_objects_array.length];
		for(int i = 0; i <answer.length; i++)
			answer[i] = (Dynamic) dynamic_objects_array[i];		
		return answer;
	}
	
	/** Obtains a subgraph from the graph, starting from the given
	 *  vertex and walking in depth-first mode, until the given
	 *  depth is reached.
	 *  
	 *  Only the appearing and visible elements (vertexes and edges) are
	 *  added to the subgraph. 
	 *  
	 *  If the given depth is set to -1, the entire visible and appearing
	 *  graph's reaching elements are returned.
	 *  
	 *  @param vertex The starting point to obtain the subgraph.
	 *  @param depth The depth to reach when walking in depth-first mode.
	 *  @return A subgraph starting from the given vertex and with the given depth. */
	public synchronized Graph getVisibleSubgraph(Vertex vertex, int depth) {
		// if the given depth is -1, returns the entire visible graph
		if(depth == -1)
			return this.getVisibleGraph(vertex);
		
		// if the given starting vertex is not visibile or appearing, returns null
		if(!vertex.visibility ||
		  (vertex instanceof DynamicVertex
				  &&
		  !((DynamicVertex) vertex).isAppearing())) return null;
		
		// the answer for the method
		Vertex[] starting_vertex = {vertex.getCopy()};
		Graph answer = new Graph(this.label, starting_vertex);
		answer.edges = new HashSet<Edge>();
		
		// expands the answer until the given depth is reached
		this.addVisibleDepth(answer, vertex, depth, new HashSet<Vertex>());
		
		// if there are no edges in the answer, nullifies its set of edges
		if(answer.edges.size() == 0) answer.edges = null;
		
		// returns the answer
		return answer;
	}
	
	/** Obtains the visible and appearing elements connected with the given
	 *  starting vertex.
	 *  
	 *  @param starting_vertex The vertex to start walking into the graph in a breadth-first manner.
	 *  @return The obtained graph. */
	private synchronized Graph getVisibleGraph(Vertex starting_vertex) {
		// if the starting vertex is not visible or is not appearing, returns null
		if(!starting_vertex.visibility ||
		  (starting_vertex instanceof DynamicVertex
				  &&
		  !((DynamicVertex) starting_vertex).isAppearing())) return null;
		
		// holds the vertexes to be treated
		List<Vertex> pending_vertexes = new LinkedList<Vertex>();
		
		// holds the vertexes already treated
		List<Vertex> treated_vertexes = new LinkedList<Vertex>();
		
		// the answer for the method
		Vertex[] initial_vertexes = {starting_vertex};
		Graph answer = new Graph(this.label, initial_vertexes);
		answer.edges = new HashSet<Edge>();
		
		// adds the starting vertex to the ones to be treated
		pending_vertexes.add(starting_vertex);
		
		// while there are still vertexes to treat
		while(pending_vertexes.size() > 0) {
			// removes the current vertex from the ones to be treated
			Vertex current_vertex = pending_vertexes.remove(0);
			
			// if it was not treated yet
			if(!treated_vertexes.contains(current_vertex)) {
				// adds it to the treated ones
				treated_vertexes.add(current_vertex);
				
				// obtains its copy from the answer
				Vertex current_vertex_copy = answer.getVertex(current_vertex.getObjectId());
				
				// obtains its neighbourhood
				Vertex[] neighbourhood = current_vertex.getNeighbourhood();
				
				// for each neighbour
				for(int i = 0; i < neighbourhood.length; i++) {
					// if the current neighbour is visible and is appearing
					if(neighbourhood[i].visibility &&
					  (!(neighbourhood[i] instanceof DynamicVertex)
							  ||
					  ((DynamicVertex) neighbourhood[i]).isAppearing())) {
						// if it is not in the already treated ones
						if(!treated_vertexes.contains(neighbourhood[i])) {
							// creates a copy of it
							Vertex current_neighbour_copy = neighbourhood[i].getCopy();
							
							// adds the copy of it to the answer of the method
							answer.vertexes.add(current_neighbour_copy);
							
							// obtains all the edges between the current
							// treated vertex and its current neighbour
							Edge[] edges = current_vertex.getConnectingEdges(neighbourhood[i]);
							
							// for each edge
							for(int j = 0; j < edges.length; j++) {
								// if the current edge is visible and is appearing
								if(edges[j].isVisible() && edges[j].isAppearing()) {
									// obtains a copy of it
									Edge edge_copy = null;
									if(current_vertex.isEmitterOf(edges[j]))
										edge_copy = edges[j].getCopy(current_vertex_copy, current_neighbour_copy);
									else
										edge_copy = edges[j].getCopy(current_neighbour_copy, current_vertex_copy);
									
									// adds to the answer
									answer.edges.add(edge_copy);
								}
							}
							
							// adds the current neighbour to the vertexes to be treated
							pending_vertexes.add(neighbourhood[i]);								
						}
					}
				}
			}
		}
		
		// if there are no egdes in the answer, nullifies it
		if(answer.edges.size() == 0) answer.edges = null;
		
		// returns the answer for the method
		return answer;
	}
	
	/** Expands the given subgraph until the given depth is reached, in a depth-first
	 *  recursive manner. The vertex where the expansion is started and a set
	 *  of already expanded vertexes must be informed.
	 *  
	 *  Only the visible and appearing elements (vertexes and edges)
	 *  are respectively expanded and considered.
	 *  
	 *  @param subgraph The subgraph to be expanded.
	 *  @param starting_vertex The vertex where the expansion is started.
	 *  @param depth The depth limit for the expansion.
	 *  @param already_expanded_vertexes The vertexes not to be expanded. */
	private synchronized void addVisibleDepth(Graph subgraph, Vertex starting_vertex, int depth, Set<Vertex> already_expanded_vertexes) {
		// if the starting vertex is not visible or is not appearing, quits the method
		if(!starting_vertex.visibility ||
		  (starting_vertex instanceof DynamicVertex
				  &&
		  !((DynamicVertex) starting_vertex).isAppearing())) return;
		
		// if the depth is valid
		if(depth > -1) {
			// tries to obtain a copy of the starting vertex from the given subgraph
			Vertex starting_vertex_copy = subgraph.getVertex(starting_vertex.getObjectId());
			
			// if the copy is null, creates it and adds to the subgraph
			if(starting_vertex_copy == null) {
				starting_vertex_copy = starting_vertex.getCopy();
				subgraph.vertexes.add(starting_vertex_copy);
			}
			
			// adds the starting vertex to the vertexes already expanded
			already_expanded_vertexes.add(starting_vertex);
			
			// if there's still depth to expand in the given subgraph
			if(depth > 0) {
				// obtains the neighbourhood of the starting vertex
				Vertex[] neighbourhood = starting_vertex.getNeighbourhood();
				
				// for each vertex of the neighbourhood
				for(int i = 0; i < neighbourhood.length; i++) {
					// if the current vertex is visible and appearing
					if(neighbourhood[i].isVisible() &&
							(!(neighbourhood[i] instanceof DynamicVertex)
									||
					        ((DynamicVertex) neighbourhood[i]).isAppearing())) {
						// if it isn't in the vertexes already expandend
						if(!already_expanded_vertexes.contains(neighbourhood[i])) {
							// tries to obtain a copy of it from the given subgraph
							Vertex neighbour_copy = subgraph.getVertex(neighbourhood[i].getObjectId());
							
							// if the copy is null, creates it and adds to the subgraph
							if(neighbour_copy == null) {
								neighbour_copy = neighbourhood[i].getCopy();
								subgraph.vertexes.add(neighbour_copy);
							}
							
							// obtains the edge between the starting vertex and its
							// current neighbour
							Edge[] edges = starting_vertex.getConnectingEdges(neighbourhood[i]);
							
							// for each edge
							for(int j = 0; j < edges.length; j++) {
								// if the current edge is visible and appearing
								if(edges[j].isVisible() && edges[j].isAppearing()) {
									// if there isn't a copy of it in the given
									// subgraph
									if(subgraph.getEdge(edges[j].getObjectId()) == null) {
										// creates the copy and adds to the subgraph
										Edge current_edge_copy = null;
										if(starting_vertex.isEmitterOf(edges[j]))
											current_edge_copy = edges[j].getCopy(starting_vertex_copy, neighbour_copy);
										else
											current_edge_copy = edges[j].getCopy(neighbour_copy, starting_vertex_copy);
										
										subgraph.edges.add(current_edge_copy);
									}								
								}
							}
							
							// calls this method recursively, starting from the
							// current neighbour
							this.addVisibleDepth(subgraph, neighbourhood[i], depth - 1, already_expanded_vertexes);
						}
					}
				}
			}
		}
	}
	
	/** Returns the vertex of the graph that has the given id.
	 * 
	 *  @param id The id of the wanted vertex.
	 *  @return The vertex with the given id, or NULL if there's no vertex with such id. */	 
	private Vertex getVertex(String id) {
		Object[] vertexes_array = this.vertexes.toArray();
		for(int i = 0; i < vertexes_array.length; i++) {
			Vertex current_vertex = (Vertex) vertexes_array[i]; 
			if(current_vertex.getObjectId().equals(id))
				return current_vertex;
		}
		
		return null;
	}
	
	/** Returns the edge of the graph that has the given id.
	 * 
	 *  @param id The id of the wanted edge.
	 *  @return The edge with the given id, or NULL if there's no edge with such id. */
	private Edge getEdge(String id) {
		if(this.edges != null) {
			Object[] edges_array = this.edges.toArray();
			for(int i = 0; i < edges_array.length; i++) {
				Edge current_edge = (Edge) edges_array[i]; 
				if(current_edge.getObjectId().equals(id))
					return current_edge;
			}
		}
		
		return null;
	}
	
	public String fullToXML(int identation) {
		// holds the answer being constructed
		StringBuffer buffer = new StringBuffer();
		
		// applies the identation
		for(int i = 0; i < identation; i++) buffer.append("\t");
		
		// fills the buffer 
		buffer.append("<graph label=\"" + this.label + "\">\n");
		
		// inserts the vertexes
		Object[] vertexes_array = this.vertexes.toArray();
		for(int i = 0; i < vertexes_array.length; i++)
			buffer.append(((Vertex) vertexes_array[i]).fullToXML(identation + 1));
		
		// inserts the edges
		if(this.edges != null) {
			Object[] edges_array = this.edges.toArray();
			for (int i = 0; i < edges_array.length; i++)
				buffer.append(((Edge) edges_array[i]).fullToXML(identation + 1));
		}
		
		// finishes the buffer content
		for(int i = 0; i < identation; i++) buffer.append("\t");
		buffer.append("</graph>\n");		
		
		// returns the buffer content
		return buffer.toString();
	}
	
	public String reducedToXML(int identation) {
		// holds the answer being constructed
		StringBuffer buffer = new StringBuffer();
		
		// applies the identation
		for(int i = 0; i < identation; i++) buffer.append("\t");
		
		// fills the buffer 
		buffer.append("<graph label=\"" + this.label + "\">\n");
		
		// inserts the lighter version of the vertexes
		Object[] vertexes_array = this.vertexes.toArray();
		for(int i = 0; i < vertexes_array.length; i++)
			buffer.append(((Vertex) vertexes_array[i]).reducedToXML(identation + 1));
		
		// inserts the lighter version of the edges
		if(this.edges != null) {
			Object[] edges_array = this.edges.toArray();
			for (int i = 0; i < edges_array.length; i++)
				buffer.append(((Edge) edges_array[i]).reducedToXML(identation + 1));
		}
		
		// finishes the buffer content
		for(int i = 0; i < identation; i++) buffer.append("\t");
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