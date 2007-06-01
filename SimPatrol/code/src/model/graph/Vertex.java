/* Vertex.java */

/* The package of this class. */
package model.graph;

/* Imported classes and/or interfaces. */
import java.util.HashSet;
import java.util.Set;
import model.interfaces.XMLable;

/** Implements the vertexes of a Graph object.
 *  @see Graph */
public class Vertex implements XMLable {
	/* Atributes. */
	/** The object id of the vertex.
	 *  Not part of the patrol problem modelling. */
	private String id;
	
	/** The set of edges whose emitter is this vertex.
	 *  Its default value is NULL. */
	protected Set<Edge> in_edges = null;

	/** The set of edges whose collector is this vertex.
	 *  Its defaultvalue is NULL. */
	protected Set<Edge> out_edges = null;

	/** The set of stigmas eventually deposited by a patroller.
	 *  Its default value is NULL. */
	protected Set<Stigma> stigmas = null;

	/** The label of the vertex. */
	private String label;

	/** The priority to visit this vertex.
	 *  Its default value is ZERO. */
	private int priority = 0;

	/** Expresses if this vertex is visible in the graph.
	 *  Its default value is TRUE. */
	private boolean visibility = true;

	/** Registers the idleness of this vertex. */
	private int idleness;

	/** Expresses if this vertex is a point of recharging the energy
	 *  of the patrollers.
	 *  Its default value is FALSE. */
	private boolean fuel = false;
	
	/* Methods. */
	/** Constructor.
	 *  @param label The label of the vertex. */
	public Vertex(String label) {
		this.label = label;
		this.idleness = 0;
	}
	
	/** Adds the passed edge to the vertex.
	 *  @param edge The edge added to the vertex. It cannot be an arc. */
	public void addEdge(Edge edge) {
		// as the edge is not an arc, it must be added to both
		// in and out edge sets
		if(this.in_edges == null) this.in_edges = new HashSet<Edge>();
		if(this.out_edges == null) this.out_edges = new HashSet<Edge>();
		
		this.in_edges.add(edge);
		this.out_edges.add(edge);
	}
	
	/** Adds the passed edge as a way out arc to the vertex.
	 *  @param out_arc The edge whose emitter is this vertex. */
	public void addOutEdge(Edge out_arc) {
		if(this.out_edges == null) this.out_edges = new HashSet<Edge>();
		this.out_edges.add(out_arc);
	}
	
	/** Adds the passed edge as a way in arc to the vertex.
	 *  @param in_arc The edge whose collector is this vertex. */
	public void addInEdge(Edge in_arc) {
		if(this.in_edges == null) this.in_edges = new HashSet<Edge>();			
		this.in_edges.add(in_arc);
	}
	
	/** Returns the edges set of the vertex.
	 *  @return The edges associated with the vertex.*/
	public Edge[] getEdges() {
		int in_edges_size = 0;
		int out_edge_size = 0;
		
		if(this.in_edges != null) in_edges_size = this.in_edges.size();
		if(this.out_edges != null) out_edge_size = this.out_edges.size();
		
		Edge[] answer = new Edge[in_edges_size + out_edge_size];
				
		if(this.in_edges != null) {
			Object[] in_edges_array = this.in_edges.toArray();
			
			for(int i = 0; i < in_edges_array.length; i++)
				answer[i] = (Edge) in_edges_array[i];			
		}
		
		if(this.out_edges != null) {
			Object[] out_edges_array = this.out_edges.toArray();
			
			for(int i = 0; i < out_edges_array.length; i++)
				answer[i + in_edges_size] = (Edge) out_edges_array[i];			
		}
		
		return answer;		
	}
	
	/** Configures the set of stigmas of the vertex.
	 *  @param stigmas The set of stigmas. */
	public void setStigmas(Stigma[] stigmas) {
		if(stigmas.length > 0) {
			this.stigmas = new HashSet<Stigma>();
			
			for(int i = 0; i < stigmas.length; i++)
				this.stigmas.add(stigmas[i]);
		}
		else this.stigmas = null;
	}
	
	/** Configures the priority of the vertex.
	 * @param priority The priority. */
	public void setPriority(int priority) {
		this.priority = priority;
	}
	
	/** Configures the visibility of the vertex.
	 * @param visibility The visibility. */
	public void setVisibility(boolean visibility) {
		this.visibility = visibility;
	}
	
	/** Configures the idleness of the vertex.
	 * @param idleness The idleness. */
	public void setIdleness(int idleness) {
		this.idleness = idleness;
	}
	
	/** Configures if the vertex is a fuel recharging point.
	 * @param fuel TRUE, if the vertex is a fuel recharging point, FALSE if not. */
	public void setFuel(boolean fuel) {
		this.fuel = fuel;
	}	
	
	public String toXML(int identation) {
		// holds the answer being constructed
		StringBuffer buffer = new StringBuffer();
		
		// applies the identation
		for(int i = 0; i < identation; i++)
			buffer.append("\t");
		
		// fills the buffer 
		buffer.append("<vertex id=\"" + this.id + 
				      "\" label=\"" + this.label +
				      "\" priority=\"" + this.priority +
				      "\" visibility=\"" + this.visibility +
				      "\" idleness=\"" + this.idleness +
				      "\" fuel=\"" + this.fuel +
					  "\" is_appearing=\"true\"");
		
		// treats the ocurrency of stigmas
		if(this.stigmas != null) {
			buffer.append("\">\n");
			
			Object[] stigmas_array = this.stigmas.toArray();			
			for(int i = 0; i < stigmas_array.length; i++)
				buffer.append(((Stigma) stigmas_array[i]).toXML(identation + 1));
			
			// applies the identation
			for(int i = 0; i < identation; i++)
				buffer.append("\t");
			
			buffer.append("</vertex>\n");
		}
		else buffer.append("\"/>\n");
		
		// returns the buffer content
		return buffer.toString();
	}
	
	public String getObjectId() {
		return this.id;
	}

	public void setObjectId(String object_id) {
		this.id = object_id;		
	}
}