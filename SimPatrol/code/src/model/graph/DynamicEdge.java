/* DynamicEdge.java */

/* The package of this class. */
package model.graph;

/* Imported classes and/or interfaces. */
import util.tpd.TimeProbabilityDistribution;

/** Implements dynamic edges of a Graph object,
 *  that can appear and disappear with a specific time
 *  probability distribution. */
public class DynamicEdge extends Edge {
	/* Attributes. */
	/** Verifies if the edge is appearing. */
	private boolean is_appearing;
	
	/** The time probability distribution for the edge appearing. */
	private TimeProbabilityDistribution appearing_pd;

	/** The time probability distribution for the edge disappearing. */
	private TimeProbabilityDistribution disappearing_pd;
	
	/* Methods. */
	/** Contructor for non-oriented dynamic edges (dynamic non-arcs).
	 *  @param vertex_1 One of the vertexes of the edge.
	 *  @param vertex_2 Another vertex of the edge.
	 *  @param length The length of the edge
	 *  @param appearing_pd The time probability distribution for the edge appearing.
	 *  @param diappearing_pd The time probability distribution for the edge disappearing.
	 *  @param is_appearing TRUE, if the edge is appearing, FALSE if not. */
	public DynamicEdge(Vertex vertex_1, Vertex vertex_2, double length, TimeProbabilityDistribution appearing_pd, TimeProbabilityDistribution disappearing_pd, boolean is_appearing) {
		super(vertex_1, vertex_2, false, length);
		this.appearing_pd = appearing_pd;
		this.disappearing_pd = disappearing_pd;
		this.is_appearing = is_appearing;
	}
	
	/** Contructor for eventually oriented dynamic edges (dynamic arcs).
	 *  @param emitter The emitter vertex, if the edge is an arc.
	 *  @param collector The collector vertex, if the edge is an arc.
	 *  @param oriented TRUE if the edge is an arc.
	 *  @param length The length of the edge.
	 *  @param appearing_pd The time probability distribution for the edge appearing.
	 *  @param diappearing_pd The time probability distribution for the edge disappearing.
	 *  @param is_appearing TRUE, if the edge is appearing, FALSE if not. */	
	public DynamicEdge(Vertex emitter, Vertex collector, boolean oriented, double length, TimeProbabilityDistribution appearing_pd, TimeProbabilityDistribution disappearing_pd, boolean is_appearing) {
		super(emitter, collector, oriented, length);
		this.appearing_pd = appearing_pd;
		this.disappearing_pd = appearing_pd;
		this.is_appearing = is_appearing;
	}
	
	/** Returns if the edge is appearing.
	 *  @return TRUE, if the edge is appearing, FALSE if not. */
	public boolean isAppearing() {
		return this.is_appearing;
	}
	
	/** Configures if the edge is appearing.
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
	}
	
	/** Returns the appearing probability distribution.
	 *  @return The time probability distribution for the edge appearing. */	
	public TimeProbabilityDistribution getAppearingTPD() {
		return this.appearing_pd;
	}
	
	/** Returns the disappearing probability distribution.
	 *  @return The time probability distribution for the edge disappearing. */	
	public TimeProbabilityDistribution getDisappearingTPD() {
		return this.disappearing_pd;
	}
	
	public String toXML(int identation) {
		// holds the answer being constructed
		StringBuffer buffer = new StringBuffer(super.toXML(identation));
		
		// finds the appearing attribute, atualizing it if necessary
		if(this.is_appearing) {
			int index_appearing_value = buffer.lastIndexOf("false");
			buffer.replace(index_appearing_value, index_appearing_value + 5, "true");
		}
		
		// removes the closing of the xml tag
		int last_valid_index = 0;
		if(this.stigmas == null) last_valid_index = buffer.indexOf("/>");
		else last_valid_index = buffer.indexOf("\n\t</edge>");
		
		buffer.delete(last_valid_index, buffer.length());
		
		// adds the time probability distributions
		buffer.append("\n");
		buffer.append(this.appearing_pd.toXML(identation + 1));
		buffer.append(this.disappearing_pd.toXML(identation + 1));
		
		// applies the identation
		for(int i = 0; i < identation; i++)
			buffer.append("\t");
		
		// closes the tags
		buffer.append("</edge>\n");
		
		// returns the buffer content
		return buffer.toString();
	}
}