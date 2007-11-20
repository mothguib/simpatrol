/* Stigma.java */

/* The package of this class. */
package model.stigma;

/* Imported classes and/or interfaces. */
import model.graph.Edge;
import model.graph.Vertex;
import view.XMLable;

/**
 * Implements an eventual stigma deposited by a patroller on the graph.
 * 
 * @developer New Stigma subclasses should override some methods of this class.
 */
public class Stigma implements XMLable {
	/* Attributes. */
	/** The eventual vertex where the stigma was deposited. */
	private Vertex vertex;

	/** The eventual edge where the stigma was deposited. */
	private Edge edge;

	/* Methods. */
	/**
	 * Constructor.
	 * 
	 * @param vertex
	 *            The vertex where the stigma was deposited.
	 */
	public Stigma(Vertex vertex) {
		this.vertex = vertex;
		this.edge = null;
	}

	/**
	 * Constructor.
	 * 
	 * @param edge
	 *            The edge where the stigma was deposited.
	 */
	public Stigma(Edge edge) {
		this.vertex = null;
		this.edge = edge;
	}

	/**
	 * Returns the eventual vertex of the stigma.
	 * 
	 * @return The eventual vertex where the stigma is deposited.
	 */
	public Vertex getVertex() {
		return this.vertex;
	}

	/**
	 * Returns the eventual edge of the stigma.
	 * 
	 * @return The eventual edge where the stigma is deposited.
	 */
	public Edge getEdge() {
		return this.edge;
	}

	/**
	 * Obtains a copy of the stigma, given the copy of its vertex.
	 * 
	 * @param vertex_copy
	 *            The copy of the vertex of the stigma.
	 * @return The copy of the stigma.
	 * @developer New Stigma subclasses should override this method.
	 */
	public Stigma getCopy(Vertex vertex_copy) {
		return new Stigma(vertex_copy);
	}

	/**
	 * Obtains a copy of the stigma, given the copy of its edge.
	 * 
	 * @param edge_copy
	 *            The copy of the edge of the stigma.
	 * @return The copy of the stigma.
	 * @developer New Stigma subclasses should override this method.
	 */
	public Stigma getCopy(Edge edge_copy) {
		return new Stigma(edge_copy);
	}

	public String fullToXML(int identation) {
		// holds the answer being constructed
		StringBuffer buffer = new StringBuffer();

		// applies the identation
		for (int i = 0; i < identation; i++)
			buffer.append("\t");

		// fills the buffer
		if (this.vertex != null)
			buffer.append("<stigma vertex_id=\"" + this.vertex.getObjectId()
					+ "\"/>\n");
		else
			buffer.append("<stigma edge_id=\"" + this.edge.getObjectId()
					+ "\"/>\n");

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