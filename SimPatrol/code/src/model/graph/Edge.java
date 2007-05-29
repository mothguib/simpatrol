/* Edge.java */

/* The package of this class. */
package model.graph;

/* Imported classes and/or interfaces. */
import java.util.Set;
import model.interfaces.XMLable;

/** Implements the edges of a Graph object.
 *  @see Graph */
public class Edge implements XMLable {
	/* Attributes. */
	/** The object id of the edge.
	 *  Not part of the patrol problem modelling. */
	private String id;
	
	/** The emitter of this edge, if it is an arc. */
	private Vertex emitter;

	/** The collector of this edge, if it is an arc. */
	private Vertex collector;
	
	/** The set of stigmas eventually deposited by a patroller.
	 *  Its default value is NULL. */
	protected Set<Stigma> stigmas = null;

	/** The lenght of the edge. */
	private double length;

	/** Expresses if this edge is visible in the graph.
	 *  Its default value is TRUE. */
	private boolean visibility = true;
	
	/** Registers if the edge is oriented (is an arc). */
	private boolean oriented;
	
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
		this.id = this.getClass().getName() + "@" +
        		  Integer.toHexString(this.hashCode()) + "#" +
        		  Float.toHexString(System.currentTimeMillis());
		
		this.emitter = emitter;
		this.collector = collector;
		this.oriented = oriented;
		
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

	public String getObjectId() {
		return this.id;
	}

	public String toXML(int identation) {
		// holds the answer being constructed
		StringBuffer buffer = new StringBuffer();
		
		// applies the identation
		for(int i = 0; i < identation; i++)
			buffer.append("\t");
		
		// fills the buffer 
		buffer.append("<edge id=" + this.id + 
				      " emitter_id=" + this.emitter.getObjectId() +
				      " collector_id=" + this.collector.getObjectId() +
				      " oriented=" + this.oriented +
				      " length=" + this.length +
				      " visibility=" + this.visibility);
		
		// treats the ocurrency of stigmas
		if(this.stigmas != null) {
			buffer.append(">\n");
			
			Stigma[] stigmas_array = (Stigma[]) this.stigmas.toArray();			
			for(int i = 0; i < stigmas_array.length; i++)
				buffer.append(stigmas_array[i].toXML(identation + 1));
			
			// applies the identation
			for(int i = 0; i < identation; i++)
				buffer.append("\t");
			
			buffer.append("</edge>\n");
		}
		else buffer.append("/>\n");
		
		// returns the buffer content
		return buffer.toString();
	}
}