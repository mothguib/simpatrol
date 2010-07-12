/* Edge.java (2.0) */
package br.org.simpatrol.server.model.graph;

/* Imported classes and/or interfaces. */
import br.org.simpatrol.server.model.interfaces.Visible;
import br.org.simpatrol.server.model.interfaces.XMLable;

/**
 * Implements the edges of a {@link Graph} object.
 * 
 * @author Daniel Henriques Moreira (dhenriques@gmail.com)
 */
public class Edge implements XMLable, Visible {
	/* Attributes. */
	/** The id of the edge. */
	protected String id;

	/** The hashcode of the edge. Its default value is ZERO. */
	protected int hashcode = 0;

	/** The emitter of the edge, if it is an arc. */
	protected Vertex emitter;

	/** The collector of the edge, if it is an arc. */
	protected Vertex collector;

	/** The length of the edge. */
	protected double length;

	/**
	 * Expresses how many agents can access and walk through this edge at the
	 * current time. Any value smaller than zero means that every agent in the
	 * simulation can walk through this edge at any time. Its default value is
	 * -1.
	 */
	private int freeWidth = -1;

	/**
	 * Expresses if this edge is visible in the graph. Its default value is
	 * TRUE.
	 */
	protected boolean visible = true;

	/**
	 * Verifies if the edge is enabled.
	 * 
	 * An edge can become disabled, if one of its vertexes is dynamic (
	 * {@link DynamicVertex}).
	 */
	protected boolean enabled;

	/* Methods. */
	/**
	 * Constructor for non-oriented edges (non-arcs).
	 * 
	 * @param id
	 *            The id of the edge.
	 * @param vertex_1
	 *            One of the vertexes of the edge.
	 * @param vertex_2
	 *            Another vertex of the edge.
	 * @param length
	 *            The length of the edge.
	 */
	public Edge(String id, Vertex vertex_1, Vertex vertex_2, double length) {
		this(id, vertex_1, vertex_2, false, length);
	}

	/**
	 * Constructor for eventually oriented edges (arcs).
	 * 
	 * @param id
	 *            The id of the edge.
	 * @param emitter
	 *            The emitter vertex, if the edge is an arc.
	 * @param collector
	 *            The collector vertex, if the edge is an arc.
	 * @param isOriented
	 *            TRUE if the edge is an arc, FALSE if not.
	 * @param length
	 *            The length of the edge.
	 */
	public Edge(String id, Vertex emitter, Vertex collector,
			boolean isOriented, double length) {
		this.id = id;
		this.emitter = emitter;
		this.collector = collector;

		// if the edge is an arc...
		if (isOriented) {
			// adds the edge as a way-out arc in the emitter
			this.emitter.addOutEdge(this);

			// adds the edge as a way-in arc in the collector
			this.collector.addInEdge(this);
		} else {
			// adds the edge in both emitter and collector vertexes
			this.emitter.addEdge(this);
			this.collector.addEdge(this);
		}

		this.length = length;

		// configures the enabled attribute, based on the
		// emitter and collector vertexes
		this.enabled = true;

		if (this.emitter instanceof DynamicVertex)
			if (!((DynamicVertex) this.emitter).isEnabled())
				this.enabled = false;

		if (this.collector instanceof DynamicVertex)
			if (!((DynamicVertex) this.collector).isEnabled())
				this.enabled = false;
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
	 * Configures how many agents can access and walk through this edge at the
	 * current time. Any value set smaller than zero means that every agent in
	 * the simulation can walk through this edge at any time.
	 * 
	 * @param freeWidth
	 *            The value to be set.
	 */
	public void setFreeWidth(int freeWidth) {
		this.freeWidth = freeWidth;
	}

	/**
	 * Returns how many agents can access and walk through this edge at the
	 * current time.
	 * 
	 * @return How many agents can access and walk through this edge at the
	 *         current time. Any value smaller than zero means that every agent
	 *         in the simulation can walk through this edge at any time.
	 */
	public int getFreeWidth() {
		return this.freeWidth;
	}

	/**
	 * Increases in one unity how many agents can access and walk through this
	 * edge at the current time.
	 */
	public void incFreeWidth() {
		this.freeWidth++;
	}

	/**
	 * Decreases in one unity how many agents can access and walk through this
	 * edge at the current time.
	 */
	public void decFreeWidth() {
		this.freeWidth--;
	}

	/**
	 * Configures the visibility of the edge.
	 * 
	 * @param visibility
	 *            The visibility.
	 */
	public void setVisible(boolean visibility) {
		this.visible = visibility;
	}

	/**
	 * Verifies the visibility of the edge.
	 * 
	 * @return TRUE if the edge is visible, FALSE if not.
	 */
	public boolean isVisible() {
		return this.visible;
	}

	/**
	 * Configures if the edge is enabled.
	 * 
	 * An edge can have dynamic behavior, if one of its vertexes is dynamic (
	 * {@link DynamicVertex}).
	 * 
	 * @param isEnabled
	 *            TRUE, if the edge is enabled, FALSE if not.
	 */
	public void setEnabled(boolean isEnabled) {
		// if enabled is TRUE
		// verifies if its vertexes are enabled
		if (isEnabled) {
			// if the emitter is a dynamic vertex and is not enabled
			if (this.emitter instanceof DynamicVertex
					&& !((DynamicVertex) this.emitter).isEnabled())
				return;

			// if the collector is a dynamic vertex and is not enabled
			if (this.collector instanceof DynamicVertex
					&& !((DynamicVertex) this.collector).isEnabled())
				return;
		}

		this.enabled = isEnabled;
	}

	/**
	 * Returns if the edge is enabled.
	 * 
	 * An edge can have dynamic behavior, if one of its vertexes is dynamic (
	 * {@link DynamicVertex}).
	 * 
	 * @return TRUE, if the edge is enabled, FALSE if not.
	 */
	public boolean isEnabled() {
		return this.enabled;
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
	 * @param emitterCopy
	 *            The copy of the emitter.
	 * @param collectorCopy
	 *            The copy of the collector.
	 * @return The copy of the edge.
	 */
	public Edge getCopy(Vertex emitterCopy, Vertex collectorCopy) {
		// registers if the original edge is oriented
		boolean oriented = !this.emitter.isCollectorOf(this);

		// the copy
		Edge edgeCopy = new Edge(this.id, emitterCopy, collectorCopy, oriented,
				this.length);
		edgeCopy.hashcode = this.hashCode();
		edgeCopy.visible = this.visible;
		edgeCopy.enabled = this.enabled;

		// returns the answer
		return edgeCopy;
	}

	public String fullToXML() {
		// holds the answer being constructed
		StringBuffer buffer = new StringBuffer();

		// verifies if the emitter is a dynamic
		// vertex and if the edge is in its memory
		// of enabled edges
		boolean isInDynamicEmitterMemory = false;
		if (this.emitter instanceof DynamicVertex)
			if (((DynamicVertex) this.emitter).isInEnabledEdges(this))
				isInDynamicEmitterMemory = true;

		// verifies if the collector is a dynamic
		// vertex and if the edge is in its memory
		// of enabled edges
		boolean isInDynamicCollectorMemory = false;
		if (this.collector instanceof DynamicVertex)
			if (((DynamicVertex) this.collector).isInEnabledEdges(this))
				isInDynamicCollectorMemory = true;

		// registers if the edge is oriented
		boolean oriented = !this.emitter.isCollectorOf(this);

		// fills the buffer
		buffer.append("<edge id=\"" + this.id + "\" emitter_id=\""
				+ this.emitter.getId() + "\" collector_id=\""
				+ this.collector.getId() + "\" oriented=\"" + oriented
				+ "\" length=\"" + this.length + "\" free_width=\""
				+ this.freeWidth + "\" visibility=\"" + this.visible
				+ "\" is_enabled=\"" + this.enabled
				+ "\" is_in_dynamic_emitter_memory=\""
				+ isInDynamicEmitterMemory
				+ "\" is_in_dynamic_collector_memory=\""
				+ isInDynamicCollectorMemory + "\"/>");

		// returns the buffer content
		return buffer.toString();
	}

	public String reducedToXML() {
		// holds the answer being constructed
		StringBuffer buffer = new StringBuffer();

		// registers if the edge is oriented
		boolean oriented = !this.emitter.isCollectorOf(this);

		// fills the buffer
		buffer.append("<edge id=\""
				+ this.id
				+ "\" emitter_id=\""
				+ this.emitter.getId()
				+ "\" collector_id=\""
				+ this.collector.getId()
				+ "\" oriented=\""
				+ oriented
				+ "\" length=\""
				+ this.length
				+ (this.freeWidth > -1 ? "\" free_width=\"" + this.freeWidth
						: "") + "\"/>");

		// returns the buffer content
		return buffer.toString();
	}

	public String getId() {
		return this.id;
	}

	public boolean equals(Object object) {
		if (object instanceof Edge)
			return this.id.equals(((Edge) object).getId());
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