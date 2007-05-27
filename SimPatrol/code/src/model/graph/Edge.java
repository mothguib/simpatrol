/* Edge.java */

/* The package of this class. */
package model.graph;

/* Imported classes and/or interfaces. */
import model.interfaces.XMLable;

/** Implements the edges of a Graph object.
 *  @see Graph */
public class Edge implements XMLable {
	/* Attributes. */
	/** The emitter of this edge, if it is an arc. */
	private Vertex emitter;

	/** The collector of this edge, if it is an arc. */
	private Vertex collector;
	
	/** The stigma eventually deposited by a patroller.
	 *  Its default value is NULL. */
	private Stigma stigma = null;

	/** The lenght of the edge. */
	private double length;

	/** Expresses if this edge is visible in the graph.
	 *  Its default value is TRUE. */
	private boolean visibility = true;
	
	/* Methods. */
	/** Contructor for non-oriented edges (non-arcs).
	 *  @param vertex_1 One of the vertexes of the edge.
	 *  @param vertex_2 Another vertex of the edge.
	 *  @param length The length of the edge. */
	public Edge(Vertex vertex_1, Vertex vertex_2, double length) {
		this(vertex_1, vertex_2, false, length);
	}
	
	/** Contructor for eventually oriented edges (arcs).
	 *  @param emitter The emitter vertex, if the edge is an arc.
	 *  @param collector The collector vertex, if the edge is an arc.
	 *  @param oriented TRUE if the edge is an arc.
	 *  @param length The length of the edge. */
	public Edge(Vertex emitter, Vertex collector, boolean oriented, double length) {
		this.emitter = emitter;
		this.collector = collector;
		
		// if the edge is an arc...
		if(oriented) {
			// adds the edge as a way out arc in the emitter 
			this.emitter.addOutEdge(this);

			// adds the edge as a way in arc in the collector
			this.collector.addInEdge(this);
		}
		else {
			// adds the edge in both emitter and collector vertexes
			this.emitter.addEdge(this);
			this.collector.addEdge(this);
		}
		
		this.length = length;
	}
}