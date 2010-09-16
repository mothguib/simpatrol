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
	 * Constructor for non-oriented dynamic edges (dynamic non-arcs).
	 * 
	 * @param vertex_1
	 *            One of the vertexes of the edge.
	 * @param vertex_2
	 *            Another vertex of the edge.
	 * @param length
	 *            The length of the edge
	 * @param enabling_tpd
	 *            The time probability distribution for the edge enabling.
	 * @param disabling_tpd
	 *            The time probability distribution for the edge disabling.
	 * @param is_enabled
	 *            TRUE, if the edge is enabled, FALSE if not.
	 */
	public DynamicEdge(Vertex vertex_1, Vertex vertex_2, double length,
			EventTimeProbabilityDistribution enabling_tpd,
			EventTimeProbabilityDistribution disabling_tpd, boolean is_enabled) {
		this(vertex_1, vertex_2, false, length, enabling_tpd, disabling_tpd,
				is_enabled);
	}

	/**
	 * Constructor for eventually oriented dynamic edges (dynamic arcs).
	 * 
	 * @param emitter
	 *            The emitter vertex, if the edge is an arc.
	 * @param collector
	 *            The collector vertex, if the edge is an arc.
	 * @param oriented
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
	public DynamicEdge(Vertex emitter, Vertex collector, boolean oriented,
			double length, EventTimeProbabilityDistribution enabling_tpd,
			EventTimeProbabilityDistribution disabling_tpd, boolean is_enabled) {
		super(emitter, collector, oriented, length);
		this.enabling_tpd = enabling_tpd;
		this.disabling_tpd = disabling_tpd;

		// configures the is_enabled attribute, based on
		// emitter and collector vertexes
		this.is_enabled = is_enabled;

		if (emitter instanceof DynamicVertex)
			if (!((DynamicVertex) emitter).isEnabled())
				this.is_enabled = false;

		if (collector instanceof DynamicVertex)
			if (!((DynamicVertex) collector).isEnabled())
				this.is_enabled = false;
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
	public DynamicEdge getCopy(Vertex emitter_copy, Vertex collector_copy) {
		// registers if the original edge is oriented
		boolean oriented = !this.emitter.isCollectorOf(this);

		// the copy
		DynamicEdge answer = new DynamicEdge(emitter_copy, collector_copy,
				oriented, this.length, this.enabling_tpd, this.disabling_tpd,
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

	public EventTimeProbabilityDistribution getEnablingTPD() {
		return this.enabling_tpd;
	}

	public EventTimeProbabilityDistribution getDisablingTPD() {
		return this.disabling_tpd;
	}
}