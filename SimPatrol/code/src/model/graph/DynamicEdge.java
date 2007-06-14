/* DynamicEdge.java */

/* The package of this class. */
package model.graph;

/* Imported classes and/or interfaces. */
import model.interfaces.Dynamic;
import util.etpd.EventTimeProbabilityDistribution;

/** Implements dynamic edges of a Graph object,
 *  that can appear and disappear with a specific event time
 *  probability distribution. */
public class DynamicEdge extends Edge implements Dynamic {
	/* Attributes. */
	/** The event time probability distribution for the edge appearing. */
	private EventTimeProbabilityDistribution appearing_pd;

	/** The event time probability distribution for the edge disappearing. */
	private EventTimeProbabilityDistribution disappearing_pd;
	
	/* Methods. */
	/** Contructor for non-oriented dynamic edges (dynamic non-arcs).
	 *  @param vertex_1 One of the vertexes of the edge.
	 *  @param vertex_2 Another vertex of the edge.
	 *  @param length The length of the edge
	 *  @param appearing_pd The time probability distribution for the edge appearing.
	 *  @param diappearing_pd The time probability distribution for the edge disappearing.
	 *  @param is_appearing TRUE, if the edge is appearing, FALSE if not. */
	public DynamicEdge(Vertex vertex_1, Vertex vertex_2, double length, EventTimeProbabilityDistribution appearing_pd, EventTimeProbabilityDistribution disappearing_pd, boolean is_appearing) {
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
	public DynamicEdge(Vertex emitter, Vertex collector, boolean oriented, double length, EventTimeProbabilityDistribution appearing_pd, EventTimeProbabilityDistribution disappearing_pd, boolean is_appearing) {
		super(emitter, collector, oriented, length);
		this.appearing_pd = appearing_pd;
		this.disappearing_pd = appearing_pd;
		this.is_appearing = is_appearing;
	}
	
	public String toXML(int identation) {
		// holds the answer being constructed
		StringBuffer buffer = new StringBuffer(super.toXML(identation));
		
		// removes the closing of the xml tag
		int last_valid_index = 0;
		if(this.stigmas == null) last_valid_index = buffer.indexOf("/>");
		else {
			StringBuffer closing_tag = new StringBuffer();			
			for(int i = 0; i < identation; i++) closing_tag.append("\t");
			closing_tag.append("</edge>");
			
			last_valid_index = buffer.indexOf(closing_tag.toString());
		}
		
		buffer.delete(last_valid_index, buffer.length());
		
		// adds the time probability distributions
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
	
	public EventTimeProbabilityDistribution getAppearingTPD() {
		return this.appearing_pd;
	}
	
	public EventTimeProbabilityDistribution getDisappearingTPD() {
		return this.disappearing_pd;
	}
}