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
public class DynamicVertex extends Vertex implements Dynamic {
	/* Attributes. */
	/** Registers if the vertex is appearing. */
	private boolean is_appearing;
	
	/** The event time probability distribution for the vertex appearing. */
	private EventTimeProbabilityDistribution appearing_pd;
	
	/** The event time probability distribution for the vertex disappearing. */
	private EventTimeProbabilityDistribution disappearing_pd;
	
	/** Memorizes which edges were appearing before the vertex disappeared. */
	private Set<Edge> appearing_edges;
	
	/* Methods. */
	/** Constructor.
	 * @param label The label of the vertex.
	 * @param appearing_pd The time probability distribution for the vertex appearing.
	 * @param diappearing_pd The time probability distribution for the vertex disappearing.
	 * @param is_appearing TRUE, if the vertex is appearing, FALSE if not. */
	public DynamicVertex(String label, EventTimeProbabilityDistribution appearing_pd, EventTimeProbabilityDistribution disappearing_pd, boolean is_appearing) {
		super(label);
		this.appearing_pd = appearing_pd;
		this.disappearing_pd = disappearing_pd;
		this.is_appearing = is_appearing;		
		this.appearing_edges = new HashSet<Edge>();
	}

	/** Returns if the vertex is appearing.
	 *  @return TRUE, if the vertex is appearing, FALSE if not. */
	public boolean isAppearing() {
		return this.is_appearing;
	}
	
	/** Configures if the vertex is appearing.
	 *  @param is_appearing TRUE, if the vertex is appearing, FALSE if not. */
	public void setIsAppearing(boolean is_appearing) {
		this.is_appearing = is_appearing;
		// TODO retirar codigo abaixo!!
		System.out.println(this.getObjectId() + " appearing " + this.is_appearing);
		
		// if is_appering is FALSE, memorizes the edges that are appearing
		if(!is_appearing) {
			this.appearing_edges = new HashSet<Edge>();
			
			// memorizes the in_edges and hides them
			if(this.in_edges != null) {
				Object[] edges_array = this.in_edges.toArray();
				for(int i = 0; i < edges_array.length; i++)
					if(((Edge) edges_array[i]).isAppearing()) {
						this.appearing_edges.add((Edge) edges_array[i]);
						((Edge) edges_array[i]).setIsAppearing(is_appearing);
					}
			}
			
			// memorizes the out_edges and hides them
			if(this.out_edges != null) {
				Object[] edges_array = this.out_edges.toArray();
				for(int i = 0; i < edges_array.length; i++)
					if(((Edge) edges_array[i]).isAppearing()) {
						this.appearing_edges.add((Edge) edges_array[i]);
						((Edge) edges_array[i]).setIsAppearing(is_appearing);
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
	
	public String toXML(int identation) {
		// holds the answer being constructed
		StringBuffer buffer = new StringBuffer(super.toXML(identation));
		
		// finds the appearing attribute, atualizing it if necessary
		if(!this.is_appearing) {
			int index_appearing_value = buffer.lastIndexOf("is_appearing=\"true\"");
			buffer.replace(index_appearing_value + 14, index_appearing_value + 4, "false");
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
		buffer.append(this.appearing_pd.toXML(identation + 1));
		buffer.append(this.disappearing_pd.toXML(identation + 1));
		
		// applies the identation
		for(int i = 0; i < identation; i++)
			buffer.append("\t");
		
		// closes the tags
		buffer.append("</vertex>\n");
		
		// returns the buffer content
		return buffer.toString();
	}
	
	public EventTimeProbabilityDistribution getAppearingETPD() {
		return this.appearing_pd;
	}
	
	public EventTimeProbabilityDistribution getDisappearingETPD() {
		return this.disappearing_pd;
	}
}