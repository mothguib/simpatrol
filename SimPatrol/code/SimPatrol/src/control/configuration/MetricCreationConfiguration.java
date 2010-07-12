/* MetricCreationConfiguration.java */

/* The package of this class. */
package control.configuration;

/* Imported classes and/or interfaces. */
import model.metric.Metric;

/**
 * Implements objects that express configurations to add metrics to a
 * simulation.
 */
public final class MetricCreationConfiguration extends Configuration {
	/* Attributes. */
	/** The new metric to be added to the simulation. */
	private final Metric METRIC;

	/** The duration, in seconds, of a cycle of measurement of the metric. */
	private final double CYCLE_DURATION;

	/* Methods. */
	/**
	 * Constructor.
	 * 
	 * @param metric
	 *            The metric to be added to the simulation.
	 * @param cycle_duration
	 *            The duration, in seconds, of a cycle of measurement of the
	 *            metric.
	 */
	public MetricCreationConfiguration(Metric metric, double cycle_duration) {
		this.METRIC = metric;
		this.CYCLE_DURATION = cycle_duration;
	}

	/**
	 * Returns the metric of the configuration.
	 * 
	 * @return The metric of the configuration.
	 */
	public Metric getMetric() {
		return this.METRIC;
	}

	/**
	 * Returns the duration, in seconds, of a cycle of measurement of the
	 * metric.
	 * 
	 * @return The duration of a cycle of measurement of the metric.
	 */
	public double getCycle_duration() {
		return this.CYCLE_DURATION;
	}

	public String fullToXML(int identation) {
		// holds the answer to the method
		StringBuffer buffer = new StringBuffer();

		// applies the identation and fills the "configuration" tag
		for (int i = 0; i < identation; i++)
			buffer.append("/t");
		buffer.append("<configuration type=\""
				+ ConfigurationTypes.METRIC_CREATION + "\" parameter=\""
				+ this.CYCLE_DURATION + "\">\n");

		// puts the metric
		buffer.append(this.METRIC.fullToXML(identation + 1));

		// closes the tag
		for (int i = 0; i < identation; i++)
			buffer.append("/t");
		buffer.append("</configuration>\n");

		// return the answer to the method
		return buffer.toString();
	}
}