/* DynamicNode.java */

/* The package of this class. */
package model.graph;

/* Imported classes and/or interfaces. */
import java.util.HashSet;
import java.util.Set;
import model.etpd.EventTimeProbabilityDistribution;
import model.interfaces.Dynamic;

/**
 * Implements dynamic Node of a Graph object, that can become disabled or
 * enabled with a specific event time probability distribution.
 */
public final class DynamicNode extends Node implements Dynamic {
	/* Attributes. */
	/** Registers if the node is enabled. */
	private boolean is_enabled;

	/** The time probability distribution for the node enabling. */
	private EventTimeProbabilityDistribution enabling_tpd;

	/** The time probability distribution for the node disabling. */
	private EventTimeProbabilityDistribution disabling_tpd;

	/** Memorizes which edges were enabled before the node became disabled. */
	private Set<Edge> enabled_edges;

	/* Methods. */
	/**
	 * Constructor.
	 * 
	 * @param label
	 *            The label of the node.
	 * @param enabling_tpd
	 *            The time probability distribution for the node enabling.
	 * @param disabling_tpd
	 *            The time probability distribution for the node disabling.
	 * @param is_enabled
	 *            TRUE, if the node is enabled, FALSE if not.
	 */
	public DynamicNode(String label,
			EventTimeProbabilityDistribution enabling_tpd,
			EventTimeProbabilityDistribution disabling_tpd, boolean is_enabled) {
		super(label);
		this.enabling_tpd = enabling_tpd;
		this.disabling_tpd = disabling_tpd;
		this.is_enabled = is_enabled;
		this.enabled_edges = new HashSet<Edge>();
	}

	/**
	 * Verifies if a given edge is in the memory of enabled edges.
	 * 
	 * @param edge
	 *            The edge to be verified.
	 * @return TRUE, if the edge is in the memory of enabled edges, FALSE if
	 *         not.
	 */
	public boolean isInEnabledEdges(Edge edge) {
		return this.enabled_edges.contains(edge);
	}

	/**
	 * Adds a given edge to the memory of enabled edges of the node.
	 * 
	 * @param edge
	 *            The edge to be added to the memory.
	 */
	public void addEnabledEdge(Edge edge) {
		this.enabled_edges.add(edge);
	}

	public boolean isEnabled() {
		return this.is_enabled;
	}

	public void setIsEnabled(boolean enabled) {
		this.is_enabled = enabled;

		// if is_enabled is FALSE
		if (!enabled) {
			this.enabled_edges = new HashSet<Edge>();

			// memorizes the in_edges and hides them
			if (this.in_edges != null)
				for (Edge edge : this.in_edges)
					if (edge.isEnabled()) {
						this.enabled_edges.add(edge);
						edge.setIsEnabled(false);
					}

			// memorizes the out_edges and hides them
			if (this.out_edges != null)
				for (Edge edge : this.out_edges)
					if (edge.isEnabled()) {
						this.enabled_edges.add(edge);
						edge.setIsEnabled(false);
					}
		}
		// if is_enabled is TRUE
		else {
			// makes appear the memorized enabled edges
			for (Edge edge : this.enabled_edges)
				edge.setIsEnabled(true);

			// clears the memorized enabled edges
			this.enabled_edges.clear();

			// resets its idleness
			this.last_visit_time = time_counter.getElapsedTime();
		}
	}

	/**
	 * Returns a copy of the node, with no edges.
	 * 
	 * @return The copy of the node, without the edges.
	 */
	public DynamicNode getCopy() {
		DynamicNode answer = new DynamicNode(this.label, this.enabling_tpd,
				this.disabling_tpd, this.is_enabled);
		answer.id = this.id;
		answer.priority = this.priority;
		answer.visibility = this.visibility;
		answer.fuel = this.fuel;
		answer.last_visit_time = this.last_visit_time;

		return answer;
	}

	public String fullToXML(int identation) {
		// holds the answer being constructed
		StringBuffer buffer = new StringBuffer(super.fullToXML(identation));

		// finds the enabled attribute, atualizing it if necessary
		if (!this.is_enabled) {
			int index_enabled_value = buffer.lastIndexOf("is_enabled=\"true\"");
			buffer.replace(index_enabled_value + 12,
					index_enabled_value + 12 + 4, "false");
		}

		// removes the xml tag closing signals
		int last_valid_index = buffer.indexOf("/>");
		buffer.replace(last_valid_index, last_valid_index + 2, ">");

		// adds the time probability distributions
		if(enabling_tpd != null)
		buffer.append(this.enabling_tpd.fullToXML(identation + 1));
		if(disabling_tpd != null)
		buffer.append(this.disabling_tpd.fullToXML(identation + 1));

		// applies the identation
		for (int i = 0; i < identation; i++)
			buffer.append("\t");

		// closes the tags
		buffer.append("</node>\n");

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