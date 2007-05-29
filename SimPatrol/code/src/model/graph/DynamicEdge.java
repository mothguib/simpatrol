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
	/** The time probability distribution for the vertex appearing. */
	private TimeProbabilityDistribution appearing_pd;

	/** The time probability distribution for the vertex disappearing. */
	private TimeProbabilityDistribution disappearing_pd;
	
	/* Methods. */
	/** Contructor for non-oriented dynamic edges (dynamic non-arcs).
	 *  @param vertex_1 One of the vertexes of the edge.
	 *  @param vertex_2 Another vertex of the edge.
	 *  @param length The length of the edge
	 *  @param appearing_pd The time probability distribution for the vertex appearing.
	 *  @param diappearing_pd The time probability distribution for the vertex disappearing. */	
	public DynamicEdge(Vertex vertex_1, Vertex vertex_2, double length, TimeProbabilityDistribution appearing_pd, TimeProbabilityDistribution disappearing_pd) {
		super(vertex_1, vertex_2, false, length);
		this.appearing_pd = appearing_pd;
		this.disappearing_pd = disappearing_pd;
	}
	
	/** Contructor for eventually oriented dynamic edges (dynamic arcs).
	 *  @param emitter The emitter vertex, if the edge is an arc.
	 *  @param collector The collector vertex, if the edge is an arc.
	 *  @param oriented TRUE if the edge is an arc.
	 *  @param length The length of the edge.
	 *  @param appearing_pd The time probability distribution for the vertex appearing.
	 *  @param diappearing_pd The time probability distribution for the vertex disappearing. */	
	public DynamicEdge(Vertex emitter, Vertex collector, boolean oriented, double length, TimeProbabilityDistribution appearing_pd, TimeProbabilityDistribution disappearing_pd) {
		super(emitter, collector, oriented, length);
		this.appearing_pd = appearing_pd;
		this.disappearing_pd = appearing_pd;
	}
	
	public String toXML(int identation) {
		// holds the answer being constructed
		StringBuffer buffer = new StringBuffer(super.toXML(identation));
		
		// removes the closing of the xml tag
		int last_valid_index = 0;
		if(this.stigmas == null) last_valid_index = buffer.indexOf("/>");
		else last_valid_index = buffer.indexOf("</edge>");
		
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