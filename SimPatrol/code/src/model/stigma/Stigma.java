/* Stigma.java */

/* The package of this class. */
package model.stigma;

/* Imported classes and/or interfaces. */
import model.graph.Edge;
import model.graph.Node;
import view.XMLable;

/**
 * Implements an eventual stigma deposited by a patroller on the graph.
 * 
 * @developer New Stigma subclasses should override some methods of this class.
 */
public class Stigma implements XMLable {
	/* Attributes. */
	/** The eventual node where the stigma was deposited. */
	private Node node;

	/** The eventual edge where the stigma was deposited. */
	private Edge edge;

	/* Methods. */
	/**
	 * Constructor.
	 * 
	 * @param node
	 *            The node where the stigma was deposited.
	 */
	public Stigma(Node node) {
		this.node = node;
		this.edge = null;
	}

	/**
	 * Constructor.
	 * 
	 * @param edge
	 *            The edge where the stigma was deposited.
	 */
	public Stigma(Edge edge) {
		this.node = null;
		this.edge = edge;
	}

	/**
	 * Returns the eventual node of the stigma.
	 * 
	 * @return The eventual node where the stigma is deposited.
	 */
	public Node getNode() {
		return this.node;
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
	 * Obtains a copy of the stigma, given the copy of its node.
	 * 
	 * @param node_copy
	 *            The copy of the node of the stigma.
	 * @return The copy of the stigma.
	 * @developer New Stigma subclasses should override this method.
	 */
	public Stigma getCopy(Node node_copy) {
		return new Stigma(node_copy);
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
		if (this.node != null)
			buffer.append("<stigma node_id=\"" + this.node.getObjectId()
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