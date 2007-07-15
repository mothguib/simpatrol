/* DynamicEdge.java */

/* The package of this class. */
package model.graph;

/* Imported classes and/or interfaces. */
import model.etpd.EventTimeProbabilityDistribution;
import model.interfaces.Dynamic;

/** Implements dynamic edges of a Graph object,
 *  that can appear and disappear with a specific event time
 *  probability distribution. */
public final class DynamicEdge extends Edge implements Dynamic {
	/* Attributes. */
	/** The time probability distribution for the edge appearing. */
	private EventTimeProbabilityDistribution appearing_tpd;

	/** The time probability distribution for the edge disappearing. */
	private EventTimeProbabilityDistribution disappearing_tpd;
	
	/* Methods. */
	/** Contructor for non-oriented dynamic edges (dynamic non-arcs).
	 * 
	 *  @param vertex_1 One of the vertexes of the edge.
	 *  @param vertex_2 Another vertex of the edge.
	 *  @param length The length of the edge
	 *  @param appearing_tpd The time probability distribution for the edge appearing.
	 *  @param diappearing_tpd The time probability distribution for the edge disappearing.
	 *  @param is_appearing TRUE, if the edge is appearing, FALSE if not. */
	public DynamicEdge(Vertex vertex_1, Vertex vertex_2, double length, EventTimeProbabilityDistribution appearing_tpd, EventTimeProbabilityDistribution disappearing_tpd, boolean is_appearing) {
		this(vertex_1, vertex_2, false, length, appearing_tpd, disappearing_tpd, is_appearing);
	}
	
	/** Contructor for eventually oriented dynamic edges (dynamic arcs).
	 * 
	 *  @param emitter The emitter vertex, if the edge is an arc.
	 *  @param collector The collector vertex, if the edge is an arc.
	 *  @param oriented TRUE if the edge is an arc.
	 *  @param length The length of the edge.
	 *  @param appearing_tpd The time probability distribution for the edge appearing.
	 *  @param diappearing_tpd The time probability distribution for the edge disappearing.
	 *  @param is_appearing TRUE, if the edge is appearing, FALSE if not. */	
	public DynamicEdge(Vertex emitter, Vertex collector, boolean oriented, double length, EventTimeProbabilityDistribution appearing_tpd, EventTimeProbabilityDistribution disappearing_tpd, boolean is_appearing) {
		super(emitter, collector, oriented, length);
		this.appearing_tpd = appearing_tpd;
		this.disappearing_tpd = disappearing_tpd;
		
		// configures the is_appearing attribute, based on
		// emitter and collector vertexes
		this.is_appearing = is_appearing;
		
		if(emitter instanceof DynamicVertex)
			if(!((DynamicVertex) emitter).isAppearing())
				this.is_appearing = false;
		
		if(collector instanceof DynamicVertex) 
			if(!((DynamicVertex) collector).isAppearing())
				this.is_appearing = false;		
	}
	
	/** Contructor for eventually oriented dynamic edges (dynamic arcs).
	 * 
	 *  @param emitter The emitter vertex, if the edge is an arc.
	 *  @param collector The collector vertex, if the edge is an arc.
	 *  @param oriented TRUE if the edge is an arc.
	 *  @param length The length of the edge.
	 *  @param appearing_tpd The time probability distribution for the edge appearing.
	 *  @param diappearing_tpd The time probability distribution for the edge disappearing.
	 *  @param is_appearing TRUE, if the edge is appearing, FALSE if not. */	
	protected DynamicEdge(Vertex emitter, Vertex collector, boolean oriented, double length, EventTimeProbabilityDistribution appearing_tpd, EventTimeProbabilityDistribution disappearing_tpd, boolean is_appearing, String id) {		
		super(emitter, collector, oriented, length, id);
		this.appearing_tpd = appearing_tpd;
		this.disappearing_tpd = disappearing_tpd;
		
		// configures the is_appearing attribute, based on
		// emitter and collector vertexes
		this.is_appearing = is_appearing;
		
		if(emitter instanceof DynamicVertex)
			if(!((DynamicVertex) emitter).isAppearing())
				this.is_appearing = false;
		
		if(collector instanceof DynamicVertex) 
			if(!((DynamicVertex) collector).isAppearing())
				this.is_appearing = false;		
	}	
	
	/** Obtains a copy of the edge with the given copies of vertexes.
	 * 
	 *  @param copy_emitter The copy of the emitter.
	 *  @param copy_collector The copy of the collector.
	 *  @return The copy of the edge.*/
	public DynamicEdge getCopy(Vertex copy_emitter, Vertex copy_collector) {
		// registers if the original edge is oriented
		boolean oriented = !this.emitter.isCollectorOf(this);
		
		// the copy		
		DynamicEdge answer = new DynamicEdge(copy_emitter, copy_collector, oriented, this.length, this.appearing_tpd, this.disappearing_tpd, this.is_appearing, id);
		answer.visibility = this.visibility;
		answer.is_appearing = this.is_appearing;
		
		// returns the answer
		return answer;
	}
	
	public String fullToXML(int identation) {
		// holds the answer being constructed
		StringBuffer buffer = new StringBuffer(super.fullToXML(identation));
		
		// removes the closing of the xml tag
		int last_valid_index = buffer.indexOf("/>");
		buffer.replace(last_valid_index, last_valid_index + 2, ">");
		
		// adds the time probability distributions
		buffer.append(this.appearing_tpd.fullToXML(identation + 1));
		buffer.append(this.disappearing_tpd.fullToXML(identation + 1));
		
		// applies the identation
		for(int i = 0; i < identation; i++) buffer.append("\t");
		
		// closes the tags
		buffer.append("</edge>\n");
		
		// returns the buffer content
		return buffer.toString();
	}
	
	public EventTimeProbabilityDistribution getAppearingTPD() {
		return this.appearing_tpd;
	}
	
	public EventTimeProbabilityDistribution getDisappearingTPD() {
		return this.disappearing_tpd;
	}
}