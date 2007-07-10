/* Stigma.java */

/* The package of this class. */
package model.stigma;

/* Imported classes and/or interfaces. */
import model.graph.Edge;
import model.graph.Vertex;
import view.XMLable;

/** Implements an eventual stigma deposited by a patroller on the graph. */
public class Stigma implements XMLable {
	/* Attributes. */
	/** The eventual vertex where the stigma was deposited. */
	private Vertex vertex;
	
	/** The eventual edge where the stigma was deposited. */
	private Edge edge;
	
	/* Methods. */
	/** Constructor.
	 * 
	 *  @param vertex The vertex where the stigma was deposited. */
	public Stigma(Vertex vertex) {
		this.vertex = vertex;
		this.edge = null;
	}
	
	/** Constructor.
	 * 
	 *  @param edge The edge where the stigma was deposited. */
	public Stigma(Edge edge) {
		this.vertex = null;
		this.edge = edge;
	}
	
	public String fullToXML(int identation) {
		// holds the answer being constructed
		StringBuffer buffer = new StringBuffer();
		
		// applies the identation
		for(int i = 0; i < identation; i++)
			buffer.append("\t");
		
		// fills the buffer
		if(this.vertex != null)
			buffer.append("<stigma vertex_id=\"" + this.vertex.getObjectId() +
					      "\"/>\n");
		else
			buffer.append("<stigma edge_id=\"" + this.edge.getObjectId() +
		      "\"/>\n");
		
		// returns the buffer content
		return buffer.toString();
	}
	
	public String reducedToXML(int identation) {
		// a stigma doesn't have a lighter version
		return this.fullToXML(identation);		
	}
	
	public String getObjectId() {
		// a stigma doesn't need an id
		return null;
	}

	public void setObjectId(String object_id) {
		// a stigma doesn't need an id
		// so, do nothing		
	}
}
