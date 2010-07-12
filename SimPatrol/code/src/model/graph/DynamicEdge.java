/* DynamicEdge.java */

/* The package of this class. */
package model.graph;

/* Imported classes and/or interfaces. */
import model.etpd.EventTimeProbabilityDistribution;
import model.interfaces.Dynamic;

/**
 * Implements dynamic edges of a Graph object, that can become disabled or
 * enabled with a specific event time probability distribution.
 */
public final class DynamicEdge extends Edge implements Dynamic {
	/* Attributes. */
	/** The time probability distribution for the edge enabling. */
	private EventTimeProbabilityDistribution enabling_tpd;

	/** The time probability distribution for the edge disabling. */
	private EventTimeProbabilityDistribution disabling_tpd;

	/* Methods. */
	/**
	 * Constructor for non-directed dynamic edges (dynamic non-arcs).
	 * 
	 * @param node_1
	 *            One of the nodes of the edge.
	 * @param node_2
	 *            Another node of the edge.
	 * @param length
	 *            The length of the edge
	 * @param enabling_tpd
	 *            The time probability distribution for the edge enabling.
	 * @param disabling_tpd
	 *            The time probability distribution for the edge disabling.
	 * @param is_enabled
	 *            TRUE, if the edge is enabled, FALSE if not.
	 */
	public DynamicEdge(Node node_1, Node node_2, double length,
			EventTimeProbabilityDistribution enabling_tpd,
			EventTimeProbabilityDistribution disabling_tpd, boolean is_enabled) {
		this(node_1, node_2, false, length, enabling_tpd, disabling_tpd,
				is_enabled);
	}

	/**
	 * Constructor for eventually directed dynamic edges (dynamic arcs).
	 * 
	 * @param source
	 *            The source node, if the edge is an arc.
	 * @param target
	 *            The target node, if the edge is an arc.
	 * @param directed
	 *            TRUE if the edge is an arc, FALSE if not.
	 * @param length
	 *            The length of the edge.
	 * @param enabling_tpd
	 *            The time probability distribution for the edge enabling.
	 * @param disabling_tpd
	 *            The time probability distribution for the edge disabling.
	 * @param is_enabled
	 *            TRUE, if the edge is enabled, FALSE if not.
	 */
	public DynamicEdge(Node source, Node target, boolean directed,
			double length, EventTimeProbabilityDistribution enabling_tpd,
			EventTimeProbabilityDistribution disabling_tpd, boolean is_enabled) {
		super(source, target, directed, length);
		this.enabling_tpd = enabling_tpd;
		this.disabling_tpd = disabling_tpd;

		// configures the is_enabled attribute, based on
		// source and target nodes
		this.is_enabled = is_enabled;

		if (source instanceof DynamicNode)
			if (!((DynamicNode) source).isEnabled())
				this.is_enabled = false;

		if (target instanceof DynamicNode)
			if (!((DynamicNode) target).isEnabled())
				this.is_enabled = false;
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
	public DynamicEdge getCopy(Node source_copy, Node target_copy) {
		// registers if the original edge is directed
		boolean directed = !this.source.isTargetOf(this);

		// the copy
		DynamicEdge answer = new DynamicEdge(source_copy, target_copy,
				directed, this.length, this.enabling_tpd, this.disabling_tpd,
				this.is_enabled);
		answer.id = this.id;
		answer.visibility = this.visibility;

		// returns the answer
		return answer;
	}

	public String fullToXML(int identation) {
		// holds the answer being constructed
		StringBuffer buffer = new StringBuffer(super.fullToXML(identation));

		// removes the xml closing tag
		int last_valid_index = buffer.indexOf("/>");
		buffer.replace(last_valid_index, last_valid_index + 2, ">");

		// adds the time probability distributions
		buffer.append(this.enabling_tpd.fullToXML(identation + 1));
		buffer.append(this.disabling_tpd.fullToXML(identation + 1));

		// applies the identation
		for (int i = 0; i < identation; i++)
			buffer.append("\t");

		// closes the tags
		buffer.append("</edge>\n");

		// returns the buffer content
		return buffer.toString();
	}
	
	public void setEnablingTPD(EventTimeProbabilityDistribution tpd) {
		this.enabling_tpd = tpd;
	}

	public EventTimeProbabilityDistribution getEnablingTPD() {
		return this.enabling_tpd;
	}
	
	public void setDisablingTPD(EventTimeProbabilityDistribution tpd) {
		this.disabling_tpd = tpd;
	}	

	public EventTimeProbabilityDistribution getDisablingTPD() {
		return this.disabling_tpd;
	}
}