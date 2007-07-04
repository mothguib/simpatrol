/* DynamicVertex.java */

/* The package of this class. */
package model.graph;

/* Imported classes and/or interfaces. */
import java.util.HashSet;
import java.util.Set;
import model.interfaces.Dynamic;
import util.etpd.EventTimeProbabilityDistribution;

/** Implements dynamic vertexes of a Graph object,
 *  that can appear and disappear with a specific event time
 *  probability distribution. */
public final class DynamicVertex extends Vertex implements Dynamic {
	/* Attributes. */
	/** Registers if the vertex is appearing. */
	private boolean is_appearing;
	
	/** The time probability distribution for the vertex appearing. */
	private EventTimeProbabilityDistribution appearing_tpd;
	
	/** The time probability distribution for the vertex disappearing. */
	private EventTimeProbabilityDistribution disappearing_tpd;
	
	/** Memorizes which edges were appearing before the vertex eventually disappeared. */
	private Set<Edge> appearing_edges;
	
	/* Methods. */
	/** Constructor.
	 * @param label The label of the vertex.
	 * @param appearing_tpd The time probability distribution for the vertex appearing.
	 * @param diappearing_tpd The time probability distribution for the vertex disappearing.
	 * @param is_appearing TRUE, if the vertex is appearing, FALSE if not. */
	public DynamicVertex(String label, EventTimeProbabilityDistribution appearing_tpd, EventTimeProbabilityDistribution disappearing_tpd, boolean is_appearing) {
		super(label);
		this.appearing_tpd = appearing_tpd;
		this.disappearing_tpd = disappearing_tpd;
		this.is_appearing = is_appearing;
		this.appearing_edges = new HashSet<Edge>();
	}
	
	/** Returns a copy of the vertex, with no edges.
	 *  @return The copy of the vertex, without the edges. */
	public DynamicVertex getCopy() {
		DynamicVertex answer = (DynamicVertex) super.getCopy();
		answer.is_appearing = this.is_appearing;
		answer.appearing_tpd = this.appearing_tpd;
		answer.disappearing_tpd = this.disappearing_tpd;
		answer.appearing_edges = this.appearing_edges;
		
		return answer;
	}
	
	/** Verifies if a given edge is in the memory of appearing edges.
	 *  @param edge The edge to be verified.
	 *  @return TRUE, if the edge is in the memory of appearing edges, FALSE if not. */
	public boolean isInAppearingEdges(Edge edge) {
		return this.appearing_edges.contains(edge);
	}
	
	/** Adds a given edge to the memory of appearing edges of the vertex.
	 *  @param edge The edge to be added to the memory. */
	public void addAppearingEdge(Edge edge) {
		this.appearing_edges.add(edge);
	}	
	
	/** Returns if the vertex is appearing.
	 *  @return TRUE, if the vertex is appearing, FALSE if not. */
	public boolean isAppearing() {
		return this.is_appearing;
	}
	
	public void setIsAppearing(boolean is_appearing) {
		this.is_appearing = is_appearing;
		
		// screen message
		System.out.println("[SimPatrol.Event] " + this.getObjectId() + " appearing " + this.is_appearing + ".");
		
		// if is_appering is FALSE, memorizes the edges that are appearing
		// and resets its idleness
		if(!is_appearing) {
			this.appearing_edges = new HashSet<Edge>();
			
			// memorizes the in_edges and hides them
			if(this.in_edges != null) {
				Object[] edges_array = this.in_edges.toArray();
				for(int i = 0; i < edges_array.length; i++)
					if(((Edge) edges_array[i]).isAppearing()) {
						this.appearing_edges.add((Edge) edges_array[i]);
						((Edge) edges_array[i]).setIsAppearing(false);
					}
			}
			
			// memorizes the out_edges and hides them
			if(this.out_edges != null) {
				Object[] edges_array = this.out_edges.toArray();
				for(int i = 0; i < edges_array.length; i++)
					if(((Edge) edges_array[i]).isAppearing()) {
						this.appearing_edges.add((Edge) edges_array[i]);
						((Edge) edges_array[i]).setIsAppearing(false);
					}
			}
		}
		// if is_appearing is TRUE...
		else {
			// makes appear the memorized appearing edges
			Object[] edges_array = this.appearing_edges.toArray();
			for(int i = 0; i < edges_array.length; i++)
				((Edge) edges_array[i]).setIsAppearing(true);
			
			// clears the memorized appearing edges
			this.appearing_edges.clear();
		}
	}
	
	/** Obtains the XML version of this vertex at the current moment.
	 *  @param identation The identation to organize the XML. 
	 *  @param current_time The current time, measured in cycles or in seconds.
	 *  @return The XML version of this vertex at the current moment. */	
	public String toXML(int identation, int current_time) {
		// holds the answer being constructed
		StringBuffer buffer = new StringBuffer(super.toXML(identation, current_time));
		
		// finds the appearing attribute, atualizing it if necessary
		if(!this.is_appearing) {
			int index_appearing_value = buffer.lastIndexOf("is_appearing=\"true\"");
			if(index_appearing_value > -1) buffer.replace(index_appearing_value + 14, index_appearing_value + 14 + 4, "false");
			else {
				int index_bigger = buffer.indexOf(">");
				buffer.insert(index_bigger, " is_appearing=\"false\"");
			}
		}
		
		// removes the closing of the xml tag
		int last_valid_index = 0;
		if(this.stigmas == null) last_valid_index = buffer.indexOf("/>");
		else {
			StringBuffer closing_tag = new StringBuffer();			
			for(int i = 0; i < identation; i++) closing_tag.append("\t");
			closing_tag.append("</vertex>");
			
			last_valid_index = buffer.indexOf(closing_tag.toString());
		}
		
		buffer.delete(last_valid_index, buffer.length());

		// adds the time probability distributions
		buffer.append(this.appearing_tpd.toXML(identation + 1));
		buffer.append(this.disappearing_tpd.toXML(identation + 1));
		
		// applies the identation
		for(int i = 0; i < identation; i++) buffer.append("\t");
		
		// closes the tags
		buffer.append("</vertex>\n");
		
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