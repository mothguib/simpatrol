/* Stigma.java (2.0) */
package br.org.simpatrol.server.model.stigma;

/* Imported classes and/or interfaces. */
import br.org.simpatrol.server.model.graph.Edge;
import br.org.simpatrol.server.model.graph.Vertex;
import br.org.simpatrol.server.model.interfaces.XMLable;

/**
 * Implements an eventual stigma deposited by a patroller on the graph.
 * 
 * @author Daniel Henriques Moreira (dhenriques@gmail.com)
 */
public abstract class Stigma implements XMLable {
	/* Attributes. */
	/** The id of the stigma. */
	protected String id;

	/** The hashcode of the stigma. Its default value is ZERO. */
	protected int hashcode = 0;

	/** The eventual vertex where the stigma was deposited. */
	protected Vertex vertex;

	/** The eventual edge where the stigma was deposited. */
	protected Edge edge;

	/** Holds the type of the stigma. */
	protected StigmaTypes stigmaType;

	/* Methods. */
	/**
	 * Constructor.
	 * 
	 * @param id
	 *            The id of the stigma.
	 * @param vertex
	 *            The vertex where the stigma was deposited.
	 */
	public Stigma(String id, Vertex vertex) {
		this.id = id;
		this.vertex = vertex;
		this.edge = null;
		this.initStigmaType();
	}

	/**
	 * Constructor.
	 * 
	 * @param id
	 *            The id of the stigma.
	 * @param edge
	 *            The edge where the stigma was deposited.
	 */
	public Stigma(String id, Edge edge) {
		this.id = id;
		this.vertex = null;
		this.edge = edge;
		this.initStigmaType();
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
	 * Initializes the type of the stigma.
	 * 
	 * {@link #stigmaType}
	 */
	protected abstract void initStigmaType();

	/**
	 * Obtains a copy of the stigma, given the copy of its vertex.
	 * 
	 * @param vertexCopy
	 *            The copy of the vertex of the stigma.
	 * @return The copy of the stigma.
	 */
	public abstract Stigma getCopy(Vertex vertexCopy);

	/**
	 * Obtains a copy of the stigma, given the copy of its edge.
	 * 
	 * @param edgeCopy
	 *            The copy of the edge of the stigma.
	 * @return The copy of the stigma.
	 */
	public abstract Stigma getCopy(Edge edgeCopy);

	public String reducedToXML() {
		// a stigma doesn't have a lighter version
		return this.fullToXML();
	}

	public String getId() {
		return this.id;
	}

	public boolean equals(Object object) {
		if (object instanceof Stigma)
			return this.id.equals(((Stigma) object).getId());
		else
			return super.equals(object);
	}

	public int hashCode() {
		if (this.hashcode == 0)
			return super.hashCode();
		else
			return this.hashcode;
	}
}