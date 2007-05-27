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
		this.label = label;
		this.vertexes = vertexes;
		this.edges = new HashSet<Edge>();
		
		// for each vertex, adds its edges to the set of edges
		Vertex[] vertexes_array = (Vertex[]) this.vertexes.toArray();
		for(int i = 0; i < vertexes_array.length; i++)
			this.edges.addAll(vertexes_array[i].getEdges());
	}
}