/* Edge.java */

/* The package of this class. */
package model.graph;

/* Imported classes and/or interfaces. */
import view.XMLable;

/** Implements the edges of a Graph object.
 *  @see Graph */
public class Edge implements XMLable {
	/* Attributes. */
	/** The object id of the edge.
	 *  Not part of the patrol problem modelling. */
	protected String id;
	
	/** The emitter of this edge, if it is an arc. */
	protected Vertex emitter;

	/** The collector of this edge, if it is an arc. */
	protected Vertex collector;
	
	/** The lenght of the edge. */
	protected double length;

	/** Expresses if this edge is visible in the graph.
	 *  Its default value is TRUE. */
	protected boolean visibility = true;
	
	/** Verifies if the edge is appearing.
	 * 
	 *  An edge can disappear, if one of its vertexes is dynamic. */
	protected boolean is_appearing;
	
	/* Methods. */
	/** Contructor for non-oriented edges (non-arcs).
	 * 
	 *  @param vertex_1 One of the vertexes of the edge.
	 *  @param vertex_2 Another vertex of the edge.
	 *  @param length The length of the edge. */
	public Edge(Vertex vertex_1, Vertex vertex_2, double length) {
		this(vertex_1, vertex_2, false, length);
	}
	
	/** Contructor for eventually oriented edges (arcs).
	 * 
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
		
		// configures the is_appearing attribute, based on the
		// emitter and collector vertexes
		this.is_appearing = true;
		
		if(emitter instanceof DynamicVertex)
			if(!((DynamicVertex) emitter).isAppearing())
				this.is_appearing = false;
		
		if(collector instanceof DynamicVertex) 
			if(!((DynamicVertex) collector).isAppearing())
				this.is_appearing = false;
	}
	
	/** Contructor for eventually oriented edges (arcs).
	 *  The id of the edge is needed.
	 * 
	 *  @param emitter The emitter vertex, if the edge is an arc.
	 *  @param collector The collector vertex, if the edge is an arc.
	 *  @param oriented TRUE if the edge is an arc.
	 *  @param length The length of the edge.
	 *  @param id The object id of the edge. */
	protected Edge(Vertex emitter, Vertex collector, boolean oriented, double length, String id) {
		this.id = id;
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
		
		// configures the is_appearing attribute, based on the
		// emitter and collector vertexes
		this.is_appearing = true;
		
		if(emitter instanceof DynamicVertex)
			if(!((DynamicVertex) emitter).isAppearing())
				this.is_appearing = false;
		
		if(collector instanceof DynamicVertex) 
			if(!((DynamicVertex) collector).isAppearing())
				this.is_appearing = false;
	}
	
	/** Configures the visibility of the edge.
	 * 
	 *  @param visibility The visibility. */
	public void setVisibility(boolean visibility) {
		this.visibility = visibility;
	}
	
	/** Verifies the visibility of the edge.
	 * 
	 *  @return TRUE if the edge is visible, FALSE if not. */
	public boolean isVisible() {
		return this.visibility;
	}	
	
	/** Returns if the edge is appearing.
	 * 
	 *  An edge can have dynamic behavior, if one of its vertexes
	 *  is dynamic.
	 * 
	 *  @return TRUE, if the edge is appearing, FALSE if not. */	
	public boolean isAppearing() {
		return this.is_appearing;
	}
	
	/** Configures if the edge is appearing.
	 * 
	 *  An edge can have dynamic behavior, if one of its vertexes
	 *  is dynamic.
	 *  
	 *  @param is_appearing TRUE, if the edge is appearing, FALSE if not. */
	public void setIsAppearing(boolean is_appearing) {		
		// if is_appearing is TRUE
		// verifies if its nodes are appearing
		if(is_appearing) {
			// if the emitter is a dynamic vertex and is not appearing
			if(this.emitter instanceof DynamicVertex &&
					!((DynamicVertex)this.emitter).isAppearing())
				return;
			
			// if the collector is a dynamic vertex and is not appearing
			if(this.collector instanceof DynamicVertex &&
					!((DynamicVertex)this.collector).isAppearing())
				return;			
		}
		
		this.is_appearing = is_appearing;
		
		// screen message
		System.out.println("[SimPatrol.Event] " + this.getObjectId() + " appearing " + this.is_appearing + ".");
	}
	
	/** Returns the vertex of the edge that is not the
	 *  one passed as a parameter.
	 *  
	 *  @param vertex The vertex whose pair is wanted.
	 *  @return The other vertex of the edge. */
	public Vertex getOtherVertex(Vertex vertex) {
		if(this.collector.equals(vertex)) return this.emitter;
		else return this.collector;
	}
	
	/** Obtains a copy of the edge with the given copies of vertexes.
	 * 
	 *  @param copy_emitter The copy of the emitter.
	 *  @param copy_collector The copy of the collector.
	 *  @return The copy of the edge.*/
	public Edge getCopy(Vertex copy_emitter, Vertex copy_collector) {
		// registers if the original edge is oriented
		boolean oriented = !this.emitter.isCollectorOf(this);
		
		// the copy		
		Edge copy_edge = new Edge(copy_emitter, copy_collector, oriented, this.length, this.id);
		copy_edge.visibility = this.visibility;
		copy_edge.is_appearing = this.is_appearing;
		
		// returns the answer
		return copy_edge;
	}
	
	public String fullToXML(int identation) {
		// holds the answer being constructed
		StringBuffer buffer = new StringBuffer();
		
		// applies the identation
		for(int i = 0; i < identation; i++) buffer.append("\t");
		
		// verifies if the emitter is a dynamic
		// vertex and if the edge is in its memory
		// of appearing edges
		boolean is_in_dynamic_emitter_memory = false;
		if(this.emitter instanceof DynamicVertex)
			if(((DynamicVertex)this.emitter).isInAppearingEdges(this))
				is_in_dynamic_emitter_memory = true;
		
		// verifies if the collector is a dynamic
		// vertex and if the edge is in its memory
		// of appearing edges
		boolean is_in_dynamic_collector_memory = false;
		if(this.collector instanceof DynamicVertex)
			if(((DynamicVertex)this.collector).isInAppearingEdges(this))
				is_in_dynamic_collector_memory = true;
		
		// registers if the edge is oriented
		boolean oriented = !this.emitter.isCollectorOf(this);
		
		// fills the buffer 
		buffer.append("<edge id=\"" + this.id + 
				      "\" emitter_id=\"" + this.emitter.getObjectId() +
				      "\" collector_id=\"" + this.collector.getObjectId() +
				      "\" oriented=\"" + oriented +
				      "\" length=\"" + this.length +
				      "\" visibility=\"" + this.visibility +
				      "\" is_appearing=\"" + this.is_appearing +
				      "\" is_in_dynamic_emitter_memory=\"" + is_in_dynamic_emitter_memory +
				      "\" is_in_dynamic_collector_memory=\"" + is_in_dynamic_collector_memory +
					  "\"/>\n");
		
		// returns the buffer content
		return buffer.toString();
	}
	
	public String reducedToXML(int identation) {
		// holds the answer being constructed
		StringBuffer buffer = new StringBuffer();
		
		// applies the identation
		for(int i = 0; i < identation; i++) buffer.append("\t");
		
		// registers if the edge is oriented
		boolean oriented = !this.emitter.isCollectorOf(this);
		
		// fills the buffer 
		buffer.append("<edge id=\"" + this.id + 
				      "\" emitter_id=\"" + this.emitter.getObjectId() +
				      "\" collector_id=\"" + this.collector.getObjectId() +
				      "\" oriented=\"" + oriented +
				      "\" length=\"" + this.length +
					  "\"/>\n");
		
		// returns the buffer content
		return buffer.toString();
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