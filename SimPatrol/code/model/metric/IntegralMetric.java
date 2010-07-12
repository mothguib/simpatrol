/* IntegralMetric.java (2.0) */
package br.org.simpatrol.server.model.metric;

/* Imported classes and/or interfaces. */
import java.util.HashMap;
import java.util.Map;

import br.org.simpatrol.server.control.robot.Bot;
import br.org.simpatrol.server.model.etpd.EventTimeProbabilityDistribution;
import br.org.simpatrol.server.model.etpd.UniformEventTimeProbabilityDistribution;
import br.org.simpatrol.server.model.interfaces.Dynamic;

/**
 * Implements the metrics of the patrolling task of which value must be
 * calculated regularly.
 * 
 * @author Daniel Henriques Moreira (dhenriques@gmail.com)
 */
public abstract class IntegralMetric extends Metric implements Dynamic {
	/* Attributes. */
	/**
	 * Enumerates the dynamic methods of this class (i.e. the methods that are
	 * called automatically by a {@link Bot} object).
	 */
	public static enum DYN_METHODS {
		COLLECT("collect");

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

	/** Holds the previously collected value of the metric. */
	protected double previousValue;

	/* Methods. */
	/**
	 * Constructor.
	 * 
	 * @param initialValue
	 *            The initial value of the metric.
	 */
	public IntegralMetric(double initialValue) {
		super();

		this.dynMap = new HashMap<String, EventTimeProbabilityDistribution>();
		this.dynMap.put(IntegralMetric.DYN_METHODS.COLLECT.getMethodName(),
				new UniformEventTimeProbabilityDistribution(0, 1));

		this.previousValue = initialValue;
	}

	/**
	 * Collects / updates / calculates the value of the metric at the current
	 * moment.
	 */
	public abstract void collect();

	public EventTimeProbabilityDistribution getETPD(String methodName) {
		return this.dynMap.get(methodName);
	}
}
