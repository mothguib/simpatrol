/* DynamicEdge.java (2.0) */
package br.org.simpatrol.server.model.graph;

/* Imported classes and/or interfaces. */
import java.util.HashMap;
import java.util.Map;

import br.org.simpatrol.server.control.robot.Bot;
import br.org.simpatrol.server.model.etpd.EventTimeProbabilityDistribution;
import br.org.simpatrol.server.model.interfaces.Dynamic;

/**
 * Implements dynamic edges of a {@link Graph} object, that can become disabled
 * or enabled with a specific event time probability distribution (
 * {@link EventTimeProbabilityDistribution} object).
 * 
 * @author Daniel Henriques Moreira (dhenriques@gmail.com)
 */
public class DynamicEdge extends Edge implements Dynamic {
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

	/* Methods. */
	/**
	 * Constructor for non-oriented dynamic edges (dynamic non-arcs).
	 * 
	 * @param id
	 *            The id of the edge.
	 * @param vertex_1
	 *            One of the vertexes of the edge.
	 * @param vertex_2
	 *            Another vertex of the edge.
	 * @param length
	 *            The length of the edge
	 * @param enablingTPD
	 *            The time probability distribution for the edge enabling.
	 * @param disablingTPD
	 *            The time probability distribution for the edge disabling.
	 * @param isEnabled
	 *            TRUE, if the edge is enabled, FALSE if not.
	 */
	public DynamicEdge(String id, Vertex vertex_1, Vertex vertex_2,
			double length, EventTimeProbabilityDistribution enablingTPD,
			EventTimeProbabilityDistribution disablingTPD, boolean isEnabled) {
		this(id, vertex_1, vertex_2, false, length, enablingTPD, disablingTPD,
				isEnabled);
	}

	/**
	 * Constructor for eventually oriented dynamic edges (dynamic arcs).
	 * 
	 * @param id
	 *            The id of the edge.
	 * @param emitter
	 *            The emitter vertex, if the edge is an arc.
	 * @param collector
	 *            The collector vertex, if the edge is an arc.
	 * @param oriented
	 *            TRUE if the edge is an arc, FALSE if not.
	 * @param length
	 *            The length of the edge.
	 * @param enablingTPD
	 *            The time probability distribution for the edge enabling.
	 * @param disablingTPD
	 *            The time probability distribution for the edge disabling.
	 * @param isEnabled
	 *            TRUE, if the edge is enabled, FALSE if not.
	 */
	public DynamicEdge(String id, Vertex emitter, Vertex collector,
			boolean oriented, double length,
			EventTimeProbabilityDistribution enablingTPD,
			EventTimeProbabilityDistribution disablingTPD, boolean isEnabled) {
		super(id, emitter, collector, oriented, length);

		this.dynMap = new HashMap<String, EventTimeProbabilityDistribution>();
		this.dynMap.put(DynamicEdge.DYN_METHODS.ENABLE.getMethodName(),
				enablingTPD);
		this.dynMap.put(DynamicEdge.DYN_METHODS.DISABLE.getMethodName(),
				disablingTPD);

		// configures the enabled attribute, based on
		// emitter and collector vertexes
		this.enabled = isEnabled;

		if (emitter instanceof DynamicVertex)
			if (!((DynamicVertex) emitter).isEnabled())
				this.enabled = false;

		if (collector instanceof DynamicVertex)
			if (!((DynamicVertex) collector).isEnabled())
				this.enabled = false;
	}

	/**
	 * Constructor for eventually oriented dynamic edges (dynamic arcs).
	 * 
	 * @param id
	 *            The id of the edge.
	 * @param emitter
	 *            The emitter vertex, if the edge is an arc.
	 * @param collector
	 *            The collector vertex, if the edge is an arc.
	 * @param oriented
	 *            TRUE if the edge is an arc, FALSE if not.
	 * @param length
	 *            The length of the edge.
	 * @param dynMap
	 *            The map that correlates the name of the dynamic methods (i.e.
	 *            the methods that are called automatically by a {@link Bot}
	 *            object) with their {@link EventTimeProbabilityDistribution}
	 *            objects.
	 * @param isEnabled
	 *            TRUE, if the edge is enabled, FALSE if not.
	 */
	private DynamicEdge(String id, Vertex emitter, Vertex collector,
			boolean oriented, double length,
			Map<String, EventTimeProbabilityDistribution> dynMap,
			boolean isEnabled) {
		super(id, emitter, collector, oriented, length);

		this.dynMap = dynMap;

		// configures the enabled attribute, based on
		// emitter and collector vertexes
		this.enabled = isEnabled;

		if (emitter instanceof DynamicVertex)
			if (!((DynamicVertex) emitter).isEnabled())
				this.enabled = false;

		if (collector instanceof DynamicVertex)
			if (!((DynamicVertex) collector).isEnabled())
				this.enabled = false;
	}

	/** Configures the {@link #enabled} attribute as TRUE. */
	public void enable() {
		this.setEnabled(true);
	}

	/** Configures the {@link #enabled} attribute as FALSE. */
	public void disable() {
		this.setEnabled(false);
	}

	public DynamicEdge getCopy(Vertex emitterCopy, Vertex collectorCopy) {
		// registers if the original edge is oriented
		boolean oriented = !this.emitter.isCollectorOf(this);

		// the copy
		DynamicEdge answer = new DynamicEdge(this.id, emitterCopy,
				collectorCopy, oriented, this.length, this.dynMap,
				this.enabled);
		answer.hashcode = this.hashCode();
		answer.visible = this.visible;

		// returns the answer
		return answer;
	}

	public String fullToXML() {
		// holds the answer being constructed
		StringBuffer buffer = new StringBuffer(super.fullToXML());

		// removes the xml closing tag
		int lastValidIndex = buffer.indexOf("/>");
		buffer.replace(lastValidIndex, lastValidIndex + 2, ">");

		// adds the time probability distributions
		buffer.append(this.dynMap.get(
				DynamicEdge.DYN_METHODS.ENABLE.getMethodName()).fullToXML());
		buffer.append(this.dynMap.get(
				DynamicEdge.DYN_METHODS.DISABLE.getMethodName()).fullToXML());

		// closes the tags
		buffer.append("</edge>");

		// returns the buffer content
		return buffer.toString();
	}

	public EventTimeProbabilityDistribution getETPD(String methodName) {
		return this.dynMap.get(methodName);
	}
}