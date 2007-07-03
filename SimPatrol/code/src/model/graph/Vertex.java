/* Vertex.java */

/* The package of this class. */
package model.graph;

/* Imported classes and/or interfaces. */
import java.util.HashSet;
import java.util.Set;

import control.simulator.CycledSimulator;
import control.simulator.RealTimeSimulator;
import model.interfaces.XMLable;

/** Implements the vertexes of a Graph object.
 *  @see Graph */
public class Vertex implements XMLable {
	/* Atributes. */
	/** The object id of the vertex.
	 *  Not part of the patrol problem modelling. */
	private String id;
	
	/** The set of edges whose emitter is this vertex. */
	protected Set<Edge> in_edges;

	/** The set of edges whose collector is this vertex. */
	protected Set<Edge> out_edges;

	/** The set of stigmas eventually deposited by a patroller. */
	protected Set<Stigma> stigmas;

	/** The label of the vertex. */
	private String label;

	/** The priority to visit this vertex.
	 *  Its default value is ZERO. */
	private int priority = 0;

	/** Expresses if this vertex is visible in the graph.
	 *  Its default value is TRUE. */
	private boolean visibility = true;
	
	/** Registers the last time when this vertex
	 *  was visited by an agent. Measured in cycles,
	 *  if the simulator is a cycled one, or in seconds,
	 *  if it's a real time one.
	 *  @see CycledSimulator
	 *  @see RealTimeSimulator */
	private int last_visit_time;
	
	/** Expresses if this vertex is a point of recharging the energy
	 *  of the patrollers.
	 *  Its default value is FALSE. */
	private boolean fuel = false;
	
	/* Methods. */
	/** Constructor.
	 *  @param label The label of the vertex. */
	public Vertex(String label) {
		this.label = label;
		this.in_edges = null;
		this.out_edges = null;
		this.stigmas = null;
		this.last_visit_time = 0;
	}
	
	/** Adds the passed edge to the vertex.
	 *  @param edge The edge added to the vertex. It cannot be an arc. */
	public void addEdge(Edge edge) {
		// as the edge is not an arc, it must be added to both
		// in and out edge sets
		if(this.in_edges == null) this.in_edges = new HashSet<Edge>();
		if(this.out_edges == null) this.out_edges = new HashSet<Edge>();
		
		this.in_edges.add(edge);
		this.out_edges.add(edge);
	}
	
	/** Adds the passed edge as a way out arc to the vertex.
	 *  @param out_arc The edge whose emitter is this vertex. */
	public void addOutEdge(Edge out_arc) {
		if(this.out_edges == null) this.out_edges = new HashSet<Edge>();
		this.out_edges.add(out_arc);
	}
	
	/** Adds the passed edge as a way in arc to the vertex.
	 *  @param in_arc The edge whose collector is this vertex. */
	public void addInEdge(Edge in_arc) {
		if(this.in_edges == null) this.in_edges = new HashSet<Edge>();			
		this.in_edges.add(in_arc);
	}
	
	/** Returns the set of edges of the vertex.
	 *  @return The edges associated with the vertex.*/
	public Edge[] getEdges() {
		int in_edges_size = 0;
		int out_edge_size = 0;
		
		if(this.in_edges != null) in_edges_size = this.in_edges.size();
		if(this.out_edges != null) out_edge_size = this.out_edges.size();
		
		Edge[] answer = new Edge[in_edges_size + out_edge_size];
				
		if(this.in_edges != null) {
			Object[] in_edges_array = this.in_edges.toArray();
			
			for(int i = 0; i < in_edges_array.length; i++)
				answer[i] = (Edge) in_edges_array[i];			
		}
		
		if(this.out_edges != null) {
			Object[] out_edges_array = this.out_edges.toArray();
			
			for(int i = 0; i < out_edges_array.length; i++)
				answer[i + in_edges_size] = (Edge) out_edges_array[i];			
		}
		
		return answer;		
	}
	
	/** Configures the set of stigmas of the vertex.
	 *  @param stigmas The set of stigmas. */
	public void setStigmas(Stigma[] stigmas) {
		if(stigmas.length > 0) {
			this.stigmas = new HashSet<Stigma>();
			
			for(int i = 0; i < stigmas.length; i++)
				this.stigmas.add(stigmas[i]);
		}
		else this.stigmas = null;
	}
	
	/** Obtains the set of stigmas of the vertex.
	 *  @return The set of stigmas of the vertex.*/
	public Stigma[] getStigmas() {
		Stigma[] answer = new Stigma[0];
		
		if(this.stigmas != null) {
			answer = new Stigma[this.stigmas.size()];
			
			Object[] stigmas_array = this.stigmas.toArray();			
			for(int i = 0; i < stigmas_array.length; i++)
				answer[i] = (Stigma) stigmas_array[i];
		}
		
		return answer;
	}	
	
	/** Configures the priority of the vertex.
	 *  @param priority The priority. */
	public void setPriority(int priority) {
		this.priority = priority;
	}
	
	/** Configures the visibility of the vertex.
	 *  @param visibility The visibility. */
	public void setVisibility(boolean visibility) {
		this.visibility = visibility;
	}
	
	/** Configures the last time this vertex was visited.
	 *  @param time The time of the last visit, measured in cycles or in seconds. */
	public void setLast_visit_time(int time) {
		this.last_visit_time = time;
	}
	
	/** Configures if the vertex is a fuel recharging point.
	 *  @param fuel TRUE, if the vertex is a fuel recharging point, FALSE if not. */
	public void setFuel(boolean fuel) {
		this.fuel = fuel;
	}
	
	/** Verifies if the vertex is the collector of a given edge.
	 *  @param edge The edge whose collector is supposed to be the vertex.
	 *  @return TRUE if the vertex is the collector of the edge, FALSE if not. */
	public boolean isCollectorOf(Edge edge) {
		return this.in_edges.contains(edge);
	}
	
	/** Verifies if the vertex is the emitter of a given edge.
	 *  @param edge The edge whose emitter is supposed to be the vertex.
	 *  @return TRUE if the vertex is the emitter of the edge, FALSE if not. */
	public boolean isEmitterOf(Edge edge) {
		return this.out_edges.contains(edge);
	}
	
	/** Configures the idleness of the vertex.
	 *  @param idleness The idleness of the vertex, measured in cycles, or in seconds. */
	public void setIdleness(int idleness) {
		this.last_visit_time = this.last_visit_time - idleness;
	}
	
	/** Calculates the idleness of the vertex at the current moment.
	 *  @param current_time The current time to be considered in the calculation of the idleness. */
	public int getIdleness(int current_time) {
		return current_time - this.last_visit_time;
	}
	
	/** Returns a copy of the vertex, with no edges.
	 *  @return The copy of the vertex, without the edges. */
	public Vertex getCopy() {
		Vertex answer = new Vertex(this.label);
		answer.id = this.id;
		answer.stigmas = this.stigmas;
		answer.priority = this.priority;
		answer.visibility = this.visibility;
		answer.last_visit_time = this.last_visit_time;
		answer.fuel = this.fuel;
		
		return answer;
	}
	
	/** Returns all the vertexes in the neighbourhood.
	 *  @return The set of vertexes in the neighbourhood. */
	public Vertex[] getNeighbourhood() {
		// holds the set of neighbour vertexes
		Set<Vertex> neighbourhood = new HashSet<Vertex>(); 
		
		// for each edge whose emitter is this vertex
		if(this.out_edges != null) {
			Object[] out_edges_array = this.out_edges.toArray();
			for(int i = 0; i < out_edges_array.length; i++) {
				// obtains the other vertex
				Vertex other_vertex = ((Edge) out_edges_array[i]).getOtherVertex(this);
				
				// adds it to set of neighbours
				neighbourhood.add(other_vertex);
			}
		}
		
		// for each edge whose collector is this vertex
		if(this.in_edges != null) {
			Object[] in_edges_array = this.in_edges.toArray();
			for(int i = 0; i < in_edges_array.length; i++) {
				// obtains the other vertex
				Vertex other_vertex = ((Edge) in_edges_array[i]).getOtherVertex(this);
				
				// adds it to set of neighbours
				neighbourhood.add(other_vertex);
			}
		}
		
		// mounts and returns the answer
		Object[] neighbourhood_array = neighbourhood.toArray();
		Vertex[] answer = new Vertex[neighbourhood_array.length];
		for(int i = 0; i < neighbourhood_array.length; i++)
			answer[i] = (Vertex) neighbourhood_array[i];
		return answer;
	}
	
	/** Returns all the edges between this vertex and the given one.
	 *  @param vertex The adjacent vertex whose edges shared with this vertex are to be returned.
	 *  @return The edges in common between this vertex and the given one. */
	public Edge[] getConnectingEdges(Vertex vertex) {
		// holds the answer to the method
		Set<Edge> shared_edges = new HashSet<Edge>();
		
		// for each edge whose emitter is this vertex
		if(this.out_edges != null) {
			Object[] out_edges_array = this.out_edges.toArray();
			for(int i = 0; i < out_edges_array.length; i++) {
				// obtains the current edge
				Edge current_edge = (Edge) out_edges_array[i];
				
				// if the given vertex is the collector of the current edge,
				// adds it to the answer
				if(vertex.isCollectorOf(current_edge))
					shared_edges.add(current_edge);
			}
		}
		
		// for each edge whose collector is this vertex
		if(this.in_edges != null) {
			Object[] in_edges_array = this.in_edges.toArray();
			for(int i = 0; i < in_edges_array.length; i++) {
				// obtains the current edge
				Edge current_edge = (Edge) in_edges_array[i];
				
				// if the given vertex is the emitter of the current edge,
				// adds it to the answer
				if(vertex.isEmitterOf(current_edge))
					shared_edges.add(current_edge);
			}
		}
		
		// mounts and returns the answer
		Object[] shared_edges_array = shared_edges.toArray();
		Edge[] answer = new Edge[shared_edges_array.length];
		for(int i = 0; i < shared_edges_array.length; i++)
			answer[i] = (Edge) shared_edges_array[i];
		return answer;
	}
	
	/** Obtains the XML version of this vertex at the current moment.
	 *  @param identation The identation to organize the XML. 
	 *  @param current_time The current time, measured in cycles or in seconds.
	 *  @return The XML version of this vertex at the current moment. */
	public String toXML(int identation, int current_time) {
		// holds the answer being constructed
		StringBuffer buffer = new StringBuffer();
		
		// applies the identation
		for(int i = 0; i < identation; i++) buffer.append("\t");
		
		// fills the buffer 
		buffer.append("<vertex id=\"" + this.id + 
				      "\" label=\"" + this.label +
				      "\" priority=\"" + this.priority +
				      "\" visibility=\"" + this.visibility +
				      "\" idleness=\"" + this.getIdleness(current_time) +
				      "\" fuel=\"" + this.fuel +
					  "\" is_appearing=\"true");
		
		// treats the ocurrency of stigmas
		if(this.stigmas != null) {
			buffer.append("\">\n");
			
			Object[] stigmas_array = this.stigmas.toArray();			
			for(int i = 0; i < stigmas_array.length; i++)
				buffer.append(((Stigma) stigmas_array[i]).toXML(identation + 1));
			
			// applies the identation and closes the tag
			for(int i = 0; i < identation; i++) buffer.append("\t");
			buffer.append("</vertex>\n");
		}
		else buffer.append("\"/>\n");
		
		// returns the buffer content
		return buffer.toString();
	}
	
	/** Give preference to use this.toXML(int identation, int current_time) 
	 * @deprecated */
	public String toXML(int identation) {
		return this.toXML(identation, (int) (System.currentTimeMillis() / 1000));
	}
	
	public boolean equals(Object object) {
		if(object instanceof XMLable)
			return this.id.equals(((XMLable) object).getObjectId());
		else return super.equals(object);
	} 
	
	public String getObjectId() {
		return this.id;
	}

	public void setObjectId(String object_id) {
		this.id = object_id;		
	}
}