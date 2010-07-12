/* Metric.java (2.0) */
package br.org.simpatrol.server.model.metric;

/* Imported classes and/or interfaces. */
import br.org.simpatrol.server.model.environment.Environment;
import br.org.simpatrol.server.model.interfaces.XMLable;

/**
 * Implements the metrics about the environment to be patrolled. They are
 * collected during the simulation of the patrolling task.
 * 
 * @author Daniel Henriques Moreira (dhenriques@gmail.com)
 */
public abstract class Metric implements XMLable {
	/* Attributes. */
	/** Holds the type of the metric. */
	protected MetricTypes metricType;

	/**
	 * The environment of the simulation of which metrics are to be collected.
	 * Shared among all the metrics.
	 */
	protected static Environment environment;

	/* Methods. */
	/** Constructor. */
	public Metric() {
		this.initMetricType();
	}

	/**
	 * Initiates the type of the metric.
	 * 
	 * {@link #metricType}
	 */
	protected abstract void initMetricType();

	/**
	 * Configures the environment of the simulation of which metrics are to be
	 * collected.
	 * 
	 * @param patrolledEnvironment
	 *            The environment of the simulation of which metrics are to be
	 *            collected.
	 */
	public static void setEnvironment(Environment patrolledEnvironment) {
		Metric.environment = patrolledEnvironment;
	}

	/**
	 * Returns the current value of the collected metric.
	 * 
	 * @return The current value of the metric.
	 */
	protected abstract double getValue();

	public String reducedToXML() {
		// a metric doesn't have a lighter version
		return this.fullToXML();
	}

	public String getId() {
		// a metric doesn't need an id
		return null;
	}

	public String fullToXML() {
		// holds the answer of the method
		StringBuffer buffer = new StringBuffer();

		// fills the buffer
		buffer.append("<metric type=\"" + this.metricType.getType()
				+ "\" value=\"" + this.getValue() + "\"/>");

		// returns the answer
		return buffer.toString();
	}
}