/* Graph.java */

/* The package of this class. */
package model.graph;

/* Imported classes and/or interfaces. */
import java.util.HashSet;
import java.util.Set;

import model.interfaces.Dynamic;
import model.interfaces.XMLable;

/** Implements graphs that represent the territories to be
 *  patrolled. */
public class Graph implements XMLable {
	/* Attributes. */
	/** The object id of the graph.
	 *  Not part of the patrol problem modelling. */
	private String id;
	
	/** The set of vertexes of the graph. */
	private Set<Vertex> vertexes;
	
	/** The set of edges of the graph.
	 *  Its default value is null. */
	private Set<Edge> edges = null;

	/** The label of the graph. */
	private String label;
	
	/* Methods. */
	/** Constructor.
	 *  @param label The label of the graph.
	 *  @param vertexes The vertexes of the graph. */
	public Graph(String label, Vertex[] vertexes) {
		this.label = label;
		
		this.vertexes = new HashSet<Vertex>();
		for(int i = 0; i < vertexes.length; i++)
			this.vertexes.add(vertexes[i]);
		
		this.edges = new HashSet<Edge>();
		
		// for each vertex, adds its edges to the set of edges
		for(int i = 0; i < vertexes.length; i++) {
			Edge[] current_edges = vertexes[i].getEdges();
			for(int j = 0; j < current_edges.length; j++)
				this.edges.add(current_edges[j]);
		}
		
		if(this.edges.size() == 0)
			this.edges = null;
	}
	
	/** Obtains the dynamic objects in the graph.
	 *  @return The dynamic vertexes and edges. */
	public Dynamic[] getDynamicComponents() {
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
		
		// returns the answer
		Object[] dynamic_objects_array = dynamic_objects.toArray();
		Dynamic[] answer = new Dynamic[dynamic_objects_array.length];
		for(int i = 0; i <answer.length; i++)
			answer[i] = (Dynamic) dynamic_objects_array[i];		
		return answer;
	}
	
	public String toXML(int identation) {
		// holds the answer being constructed
		StringBuffer buffer = new StringBuffer();
		
		// applies the identation
		for(int i = 0; i < identation; i++)
			buffer.append("\t");
		
		// fills the buffer 
		buffer.append("<graph id=\"" + this.id + 
				      "\" label=\"" + this.label +
				      "\">\n");
		
		// inserts the vertexes
		Object[] vertexes_array = this.vertexes.toArray();
		for(int i = 0; i < vertexes_array.length; i++)
			buffer.append(((Vertex) vertexes_array[i]).toXML(identation + 1));
		
		// inserts the edges
		if(this.edges != null) {
			Object[] edges_array = this.edges.toArray();
			for (int i = 0; i < edges_array.length; i++)
				buffer.append(((Edge) edges_array[i]).toXML(identation + 1));
		}
		
		// finishes the buffer content
		for(int i = 0; i < identation; i++)
			buffer.append("\t");

		buffer.append("</graph>\n");		
		
		// returns the buffer content
		return buffer.toString();
	}

	public String getObjectId() {
		return this.id;
	}

	public void setObjectId(String object_id) {
		this.id = object_id;
	}
}