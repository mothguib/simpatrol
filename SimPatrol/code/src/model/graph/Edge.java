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

	/** The source of this edge, if it is an arc. */
	protected Node source;

	/** The target of this edge, if it is an arc. */
	protected Node target;

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
	 * An edge can become disabled, if one of its nodes is dynamic.
	 */
	protected boolean is_enabled;

	/* Methods. */
	/**
	 * Constructor for non-directed edges (non-arcs).
	 * 
	 * @param node_1
	 *            One of the nodes of the edge.
	 * @param node_2
	 *            Another node of the edge.
	 * @param length
	 *            The length of the edge.
	 */
	public Edge(Node node_1, Node node_2, double length) {
		this(node_1, node_2, false, length);
	}

	/**
	 * Constructor for eventually directed edges (arcs).
	 * 
	 * @param source
	 *            The source node, if the edge is an arc.
	 * @param target
	 *            The target node, if the edge is an arc.
	 * @param directed
	 *            TRUE if the edge is an arc, FALSE if not.
	 * @param length
	 *            The length of the edge.
	 */
	public Edge(Node source, Node target, boolean directed,
			double length) {
		this.source = source;
		this.target = target;

		// if the edge is an arc...
		if (directed) {
			// adds the edge as a way out arc in the source
			this.source.addOutEdge(this);

			// adds the edge as a way in arc in the target
			this.target.addInEdge(this);
		} else {
			// adds the edge in both source and target nodes
			this.source.addEdge(this);
			this.target.addEdge(this);
		}

		this.length = length;

		// configures the is_enabled attribute, based on the
		// source and target nodes
		this.is_enabled = true;

		if (source instanceof DynamicNode)
			if (!((DynamicNode) source).isEnabled())
				this.is_enabled = false;

		if (target instanceof DynamicNode)
			if (!((DynamicNode) target).isEnabled())
				this.is_enabled = false;
	}
	
	/**
	 * Contructor for eventually oriented edges (arcs). The id of the edge is
	 * needed.
	 * 
	 * @param emitter
	 *            The emitter vertex, if the edge is an arc.
	 * @param target
	 *            The target vertex, if the edge is an arc.
	 * @param oriented
	 *            TRUE if the edge is an arc.
	 * @param length
	 *            The length of the edge.
	 * @param id
	 *            The object id of the edge.
	 */
	protected Edge(Node emitter, Node target, boolean oriented,
			double length, String id) {
		this.id = id;
		this.source = emitter;
		this.target = target;

		// if the edge is an arc...
		if (oriented) {
			// adds the edge as a way out arc in the source
			this.source.addOutEdge(this);

			// adds the edge as a way in arc in the target
			this.target.addInEdge(this);
		} else {
			// adds the edge in both source and target vertexes
			this.source.addEdge(this);
			this.target.addEdge(this);
		}

		this.length = length;

		// configures the is_enabled attribute, based on the
		// source and target vertexes
		this.is_enabled = true;

		if (emitter instanceof DynamicNode)
			if (!((DynamicNode) emitter).isEnabled())
				this.is_enabled = false;

		if (target instanceof DynamicNode)
			if (!((DynamicNode) target).isEnabled())
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
	 * Returns the length of the edge.
	 * 
	 * @return The length of the edge.
	 */
	public void setLength(Double len) {
		this.length = len;
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
	 * An edge can have dynamic behavior, if one of its nodes is dynamic.
	 * 
	 * @return TRUE, if the edge is enabled, FALSE if not.
	 */
	public boolean isEnabled() {
		return this.is_enabled;
	}

	/**
	 * Configures if the edge is enabled.
	 * 
	 * An edge can have dynamic behavior, if one of its nodes is dynamic.
	 * 
	 * @param is_enabled
	 *            TRUE, if the edge is enabled, FALSE if not.
	 */
	public void setIsEnabled(boolean is_enabled) {
		// if is_enabled is TRUE
		// verifies if its nodes are enabled
		if (is_enabled) {
			// if the source is a dynamic node and is not enabled
			if (this.source instanceof DynamicNode
					&& !((DynamicNode) this.source).isEnabled())
				return;

			// if the target is a dynamic node and is not enabled
			if (this.target instanceof DynamicNode
					&& !((DynamicNode) this.target).isEnabled())
				return;
		}

		this.is_enabled = is_enabled;
	}

	/**
	 * Returns the node of the edge that is not the one passed as a parameter.
	 * 
	 * If the given node is not an source or target of this edge, returns
	 * null.
	 * 
	 * @param node
	 *            The node whose pair is wanted.
	 * @return The other node of the edge.
	 */
	public Node getOtherNode(Node node) {
		if (this.target.equals(node))
			return this.source;
		else if (this.source.equals(node))
			return this.target;
		else
			return null;
	}
	
	/**

	 * Returns the source of the edge
	 *
	 */
	public Node getSource() {
			return this.source;

	}
	
	/**
	 * Returns the collector of the edge.
	 * 
	 */
	public Node getTarget() {
		return this.target;
	}

	/**
	 * Obtains a copy of the edge with the given copies of nodes.
	 * 
	 * @param source_copy
	 *            The copy of the source.
	 * @param target_copy
	 *            The copy of the target.
	 * @return The copy of the edge.
	 */
	public Edge getCopy(Node source_copy, Node target_copy) {
		// registers if the original edge is directed
		boolean directed = !this.source.isTargetOf(this);

		// the copy
		Edge edge_copy = new Edge(source_copy, target_copy, directed,
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

		// verifies if the source is a dynamic
		// node and if the edge is in its memory
		// of enabled edges
		boolean is_in_dynamic_source_memory = false;
		if (this.source instanceof DynamicNode)
			if (((DynamicNode) this.source).isInEnabledEdges(this))
				is_in_dynamic_source_memory = true;

		// verifies if the target is a dynamic
		// node and if the edge is in its memory
		// of enabled edges
		boolean is_in_dynamic_target_memory = false;
		if (this.target instanceof DynamicNode)
			if (((DynamicNode) this.target).isInEnabledEdges(this))
				is_in_dynamic_target_memory = true;

		// registers if the edge is directed
		boolean directed = !this.source.isTargetOf(this);

		// fills the buffer
		buffer.append("<edge id=\"" + this.id + "\" source_id=\""
				+ this.source.getObjectId() + "\" target_id=\""
				+ this.target.getObjectId() + "\" directed=\"" + directed
				+ "\" length=\"" + this.length + "\" visibility=\""
				+ this.visibility + "\" is_enabled=\"" + this.is_enabled
				+ "\" is_in_dynamic_source_memory=\""
				+ is_in_dynamic_source_memory
				+ "\" is_in_dynamic_target_memory=\""
				+ is_in_dynamic_target_memory + "\"/>\n");

		// returns the buffer content
		return buffer.toString();
	}

	public String reducedToXML(int identation) {
		// holds the answer being constructed
		StringBuffer buffer = new StringBuffer();

		// applies the identation
		for (int i = 0; i < identation; i++)
			buffer.append("\t");

		// registers if the edge is directed
		boolean directed = !this.source.isTargetOf(this);

		// fills the buffer
		buffer.append("<edge id=\"" + this.id + "\" source_id=\""
				+ this.source.getObjectId() + "\" target_id=\""
				+ this.target.getObjectId() + "\" directed=\"" + directed
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