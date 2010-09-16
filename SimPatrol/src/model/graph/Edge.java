/* Edge.java */

/* The package of this class. */
package model.graph;

/* Imported classes and/or interfaces. */
import model.interfaces.Visible;
import view.XMLable;

/**
 * Implements the edges of a Graph object.
 * 
 * @see Graph
 */
public class Edge implements XMLable, Visible {
	/* Attributes. */
	/**
	 * The object id of the edge. Not part of the patrolling problem modeling.
	 */
	protected String id;

	/** The emitter of this edge, if it is an arc. */
	protected Vertex emitter;

	/** The collector of this edge, if it is an arc. */
	protected Vertex collector;

	/** The length of the edge. */
	protected double length;

	/**
	 * Expresses if this edge is visible in the graph. Its default value is
	 * TRUE.
	 */
	protected boolean visibility = true;

	/**
	 * Verifies if the edge is enabled.
	 * 
	 * An edge can become disabled, if one of its vertexes is dynamic.
	 */
	protected boolean is_enabled;

	/* Methods. */
	/**
	 * Constructor for non-oriented edges (non-arcs).
	 * 
	 * @param vertex_1
	 *            One of the vertexes of the edge.
	 * @param vertex_2
	 *            Another vertex of the edge.
	 * @param length
	 *            The length of the edge.
	 */
	public Edge(Vertex vertex_1, Vertex vertex_2, double length) {
		this(vertex_1, vertex_2, false, length);
	}

	/**
	 * Constructor for eventually oriented edges (arcs).
	 * 
	 * @param emitter
	 *            The emitter vertex, if the edge is an arc.
	 * @param collector
	 *            The collector vertex, if the edge is an arc.
	 * @param oriented
	 *            TRUE if the edge is an arc, FALSE if not.
	 * @param length
	 *            The length of the edge.
	 */
	public Edge(Vertex emitter, Vertex collector, boolean oriented,
			double length) {
		this.emitter = emitter;
		this.collector = collector;

		// if the edge is an arc...
		if (oriented) {
			// adds the edge as a way out arc in the emitter
			this.emitter.addOutEdge(this);

			// adds the edge as a way in arc in the collector
			this.collector.addInEdge(this);
		} else {
			// adds the edge in both emitter and collector vertexes
			this.emitter.addEdge(this);
			this.collector.addEdge(this);
		}

		this.length = length;

		// configures the is_enabled attribute, based on the
		// emitter and collector vertexes
		this.is_enabled = true;

		if (emitter instanceof DynamicVertex)
			if (!((DynamicVertex) emitter).isEnabled())
				this.is_enabled = false;

		if (collector instanceof DynamicVertex)
			if (!((DynamicVertex) collector).isEnabled())
				this.is_enabled = false;
	}

	/**
	 * Returns the length of the edge.
	 * 
	 * @return The length of the edge.
	 */
	public double getLength() {
		return this.length;
	}

	/**
	 * Configures the visibility of the edge.
	 * 
	 * @param visibility
	 *            The visibility.
	 */
	public void setVisibility(boolean visibility) {
		this.visibility = visibility;
	}

	/**
	 * Verifies the visibility of the edge.
	 * 
	 * @return TRUE if the edge is visible, FALSE if not.
	 */
	public boolean isVisible() {
		return this.visibility;
	}

	/**
	 * Returns if the edge is enabled.
	 * 
	 * An edge can have dynamic behavior, if one of its vertexes is dynamic.
	 * 
	 * @return TRUE, if the edge is enabled, FALSE if not.
	 */
	public boolean isEnabled() {
		return this.is_enabled;
	}

	/**
	 * Configures if the edge is enabled.
	 * 
	 * An edge can have dynamic behavior, if one of its vertexes is dynamic.
	 * 
	 * @param is_enabled
	 *            TRUE, if the edge is enabled, FALSE if not.
	 */
	public void setIsEnabled(boolean is_enabled) {
		// if is_enabled is TRUE
		// verifies if its vertexes are enabled
		if (is_enabled) {
			// if the emitter is a dynamic vertex and is not enabled
			if (this.emitter instanceof DynamicVertex
					&& !((DynamicVertex) this.emitter).isEnabled())
				return;

			// if the collector is a dynamic vertex and is not enabled
			if (this.collector instanceof DynamicVertex
					&& !((DynamicVertex) this.collector).isEnabled())
				return;
		}

		this.is_enabled = is_enabled;
	}

	/**
	 * Returns the vertex of the edge that is not the one passed as a parameter.
	 * 
	 * If the given vertex is not an emitter or collector of this edge, returns
	 * null.
	 * 
	 * @param vertex
	 *            The vertex whose pair is wanted.
	 * @return The other vertex of the edge.
	 */
	public Vertex getOtherVertex(Vertex vertex) {
		if (this.collector.equals(vertex))
			return this.emitter;
		else if (this.emitter.equals(vertex))
			return this.collector;
		else
			return null;
	}

	/**
	 * Obtains a copy of the edge with the given copies of vertexes.
	 * 
	 * @param emitter_copy
	 *            The copy of the emitter.
	 * @param collector_copy
	 *            The copy of the collector.
	 * @return The copy of the edge.
	 */
	public Edge getCopy(Vertex emitter_copy, Vertex collector_copy) {
		// registers if the original edge is oriented
		boolean oriented = !this.emitter.isCollectorOf(this);

		// the copy
		Edge edge_copy = new Edge(emitter_copy, collector_copy, oriented,
				this.length);
		edge_copy.id = this.id;
		edge_copy.visibility = this.visibility;
		edge_copy.is_enabled = this.is_enabled;

		// returns the answer
		return edge_copy;
	}

	public String fullToXML(int identation) {
		// holds the answer being constructed
		StringBuffer buffer = new StringBuffer();

		// applies the identation
		for (int i = 0; i < identation; i++)
			buffer.append("\t");

		// verifies if the emitter is a dynamic
		// vertex and if the edge is in its memory
		// of enabled edges
		boolean is_in_dynamic_emitter_memory = false;
		if (this.emitter instanceof DynamicVertex)
			if (((DynamicVertex) this.emitter).isInEnabledEdges(this))
				is_in_dynamic_emitter_memory = true;

		// verifies if the collector is a dynamic
		// vertex and if the edge is in its memory
		// of enabled edges
		boolean is_in_dynamic_collector_memory = false;
		if (this.collector instanceof DynamicVertex)
			if (((DynamicVertex) this.collector).isInEnabledEdges(this))
				is_in_dynamic_collector_memory = true;

		// registers if the edge is oriented
		boolean oriented = !this.emitter.isCollectorOf(this);

		// fills the buffer
		buffer.append("<edge id=\"" + this.id + "\" emitter_id=\""
				+ this.emitter.getObjectId() + "\" collector_id=\""
				+ this.collector.getObjectId() + "\" oriented=\"" + oriented
				+ "\" length=\"" + this.length + "\" visibility=\""
				+ this.visibility + "\" is_enabled=\"" + this.is_enabled
				+ "\" is_in_dynamic_emitter_memory=\""
				+ is_in_dynamic_emitter_memory
				+ "\" is_in_dynamic_collector_memory=\""
				+ is_in_dynamic_collector_memory + "\"/>\n");

		// returns the buffer content
		return buffer.toString();
	}

	public String reducedToXML(int identation) {
		// holds the answer being constructed
		StringBuffer buffer = new StringBuffer();

		// applies the identation
		for (int i = 0; i < identation; i++)
			buffer.append("\t");

		// registers if the edge is oriented
		boolean oriented = !this.emitter.isCollectorOf(this);

		// fills the buffer
		buffer.append("<edge id=\"" + this.id + "\" emitter_id=\""
				+ this.emitter.getObjectId() + "\" collector_id=\""
				+ this.collector.getObjectId() + "\" oriented=\"" + oriented
				+ "\" length=\"" + this.length + "\"/>\n");

		// returns the buffer content
		return buffer.toString();
	}

	public boolean equals(Object object) {
		if (this.id != null && object instanceof XMLable)
			return this.id.equals(((XMLable) object).getObjectId());
		else
			return super.equals(object);
	}

	public String getObjectId() {
		return this.id;
	}

	public void setObjectId(String object_id) {
		this.id = object_id;
	}
}