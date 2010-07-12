/* DynamicVertex.java (2.0) */
package br.org.simpatrol.server.model.graph;

/* Imported classes and/or interfaces. */
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import br.org.simpatrol.server.control.robot.Bot;
import br.org.simpatrol.server.model.etpd.EventTimeProbabilityDistribution;
import br.org.simpatrol.server.model.interfaces.Dynamic;

/**
 * Implements dynamic vertexes of a {@link Graph} object, that can become
 * disabled or enabled with a specific event time probability distribution (
 * {@link EventTimeProbabilityDistribution} object).
 * 
 * @author Daniel Henriques Moreira (dhenriques@gmail.com)
 */
public class DynamicVertex extends Vertex implements Dynamic {
	/* Attributes. */
	/**
	 * Enumerates the dynamic methods of this class (i.e. the methods that are
	 * called automatically by a {@link Bot} object).
	 */
	public static enum DYN_METHODS {
		ENABLE("enable"), DISABLE("disable");

		private final String METHOD_NAME;

		private DYN_METHODS(String methodName) {
			this.METHOD_NAME = methodName;
		}

		public String getMethodName() {
			return this.METHOD_NAME;
		}
	}

	/**
	 * Maps the name of the dynamic methods (i.e. the methods that are called
	 * automatically by a {@link Bot} object) with their
	 * {@link EventTimeProbabilityDistribution} objects.
	 */
	private Map<String, EventTimeProbabilityDistribution> dynMap;

	/** Registers if the vertex is enabled. */
	private boolean enabled;

	/** Memorizes which edges were enabled before the vertex became disabled. */
	private Set<Edge> enabledEdges;

	/* Methods. */
	/**
	 * Constructor.
	 * 
	 * @param id
	 *            The id of the vertex.
	 * @param label
	 *            The label of the vertex.
	 * @param enablingTPD
	 *            The time probability distribution for the vertex enabling.
	 * @param disablingTPD
	 *            The time probability distribution for the vertex disabling.
	 * @param enabled
	 *            TRUE, if the vertex is enabled, FALSE if not.
	 */
	public DynamicVertex(String id, String label,
			EventTimeProbabilityDistribution enablingTPD,
			EventTimeProbabilityDistribution disablingTPD, boolean isEnabled) {
		super(id, label);

		this.dynMap = new HashMap<String, EventTimeProbabilityDistribution>();
		this.dynMap.put(DynamicVertex.DYN_METHODS.ENABLE.getMethodName(),
				enablingTPD);
		this.dynMap.put(DynamicVertex.DYN_METHODS.DISABLE.getMethodName(),
				disablingTPD);

		this.enabled = isEnabled;
		this.enabledEdges = new HashSet<Edge>();
	}

	/**
	 * Constructor.
	 * 
	 * @param id
	 *            The id of the vertex.
	 * @param label
	 *            The label of the vertex.
	 * @param dynMap
	 *            The map that correlates the name of the dynamic methods (i.e.
	 *            the methods that are called automatically by a {@link Bot}
	 *            object) with their {@link EventTimeProbabilityDistribution}
	 *            objects.
	 * @param enabled
	 *            TRUE, if the vertex is enabled, FALSE if not.
	 */
	private DynamicVertex(String id, String label,
			Map<String, EventTimeProbabilityDistribution> dynMap,
			boolean isEnabled) {
		super(id, label);
		this.dynMap = dynMap;
		this.enabled = isEnabled;
		this.enabledEdges = new HashSet<Edge>();
	}

	/** Configures the {@link #enabled} attribute as TRUE. */
	public void enable() {
		this.enabled = true;

		// makes appear the memorized enabled edges
		for (Edge edge : this.enabledEdges)
			edge.setEnabled(true);

		// clears the memorized enabled edges
		this.enabledEdges.clear();

		// resets its idleness
		this.visit();
	}

	/** Configures the {@link #enabled} attribute as FALSE. */
	public void disable() {
		this.enabled = false;

		// memorizes the inEdges and hides them
		if (this.inEdges != null)
			for (Edge edge : this.inEdges)
				if (edge.isEnabled()) {
					this.enabledEdges.add(edge);
					edge.setEnabled(false);
				}

		// memorizes the outEdges and hides them
		if (this.outEdges != null)
			for (Edge edge : this.outEdges)
				if (edge.isEnabled()) {
					this.enabledEdges.add(edge);
					edge.setEnabled(false);
				}
	}

	public boolean isEnabled() {
		return this.enabled;
	}

	/**
	 * Adds a given edge to the memory of enabled edges of the vertex.
	 * 
	 * @param edge
	 *            The edge to be added to the memory.
	 */
	public void addEnabledEdge(Edge edge) {
		// if the edge is connected to this vertex
		if ((this.inEdges != null && this.inEdges.contains(edge))
				|| (this.outEdges != null && this.outEdges.contains(edge)))
			this.enabledEdges.add(edge);
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
		return this.enabledEdges.contains(edge);
	}

	public DynamicVertex getCopyWithoutEdges() {
		DynamicVertex answer = new DynamicVertex(this.id, this.label,
				this.dynMap, this.enabled);
		answer.hashcode = this.hashCode();
		answer.priority = this.priority;
		answer.visible = this.visible;
		answer.fuel = this.fuel;
		answer.lastVisitTime = this.lastVisitTime;

		return answer;
	}

	public String fullToXML() {
		// holds the answer being constructed
		StringBuffer buffer = new StringBuffer(super.fullToXML());

		// finds the enabled attribute, updating it if necessary
		if (!this.enabled) {
			int indexEnabledValue = buffer.lastIndexOf("is_enabled=\"true\"");
			buffer.replace(indexEnabledValue + 12, indexEnabledValue + 12 + 4,
					"false");
		}

		// removes the xml tag closing signals
		int lastValidIndex = buffer.indexOf("/>");
		buffer.replace(lastValidIndex, lastValidIndex + 2, ">");

		// adds the time probability distributions
		buffer.append(this.dynMap.get(
				DynamicVertex.DYN_METHODS.ENABLE.getMethodName()).fullToXML());
		buffer.append(this.dynMap.get(
				DynamicVertex.DYN_METHODS.DISABLE.getMethodName()).fullToXML());

		// closes the tags
		buffer.append("</vertex>");

		// returns the buffer content
		return buffer.toString();
	}

	public EventTimeProbabilityDistribution getETPD(String methodName) {
		return this.dynMap.get(methodName);
	}
}