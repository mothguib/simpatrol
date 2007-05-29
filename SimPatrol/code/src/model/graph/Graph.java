/* Graph.java */

/* The package of this class. */
package model.graph;

/* Imported classes and/or interfaces. */
import java.util.HashSet;
import java.util.Set;
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
	
	/** The set of edges of the graph. */
	private Set<Edge> edges;

	/** The label of the graph. */
	private String label;
	
	/* Methods. */
	/** Constructor.
	 *  @param label The label of the graph.
	 *  @param vertexes The set of vertexes of the graph. */
	public Graph(String label, Set<Vertex> vertexes) {
		this.id = this.getClass().getName() + "@" +
		          Integer.toHexString(this.hashCode()) + "#" +
		          Float.toHexString(System.currentTimeMillis());
				
		this.label = label;
		this.vertexes = vertexes;
		this.edges = new HashSet<Edge>();
		
		// for each vertex, adds its edges to the set of edges
		Vertex[] vertexes_array = (Vertex[]) this.vertexes.toArray();
		for(int i = 0; i < vertexes_array.length; i++)
			this.edges.addAll(vertexes_array[i].getEdges());
	}

	public String toXML(int identation) {
		// holds the answer being constructed
		StringBuffer buffer = new StringBuffer();
		
		// applies the identation
		for(int i = 0; i < identation; i++)
			buffer.append("\t");
		
		// fills the buffer 
		buffer.append("<graph id=" + this.id + 
				      " label=" + this.label +
				      ">\n");
		
		// inserts the vertexes
		Vertex[] vertexes_array = (Vertex[]) this.vertexes.toArray();
		for(int i = 0; i < vertexes_array.length; i++)
			buffer.append(vertexes_array[i].toXML(identation + 1));
		
		// inserts the edges
		Edge[] edges_array = (Edge[]) this.edges.toArray();
		for(int i = 0; i < edges_array.length; i++)
			buffer.append(edges_array[i].toXML(identation + 1));
		
		// applies the identation
		for(int i = 0; i < identation; i++)
			buffer.append("\t");
		
		// finishes the buffer content
		buffer.append("</graph>\n");		
		
		// returns the buffer content
		return buffer.toString();
	}

	public String getObjectId() {
		return this.id;
	}
}