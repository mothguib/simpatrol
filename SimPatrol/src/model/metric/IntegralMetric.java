/* IntegralMetric.java */

/* The package of this class. */
package model.metric;

/* Imported classes and/or interfaces. */
import util.time.Clock;
import util.time.Clockable;

/**
 * Implements the metrics of the patrolling task of which value must be
 * calculated regularly.
 */
public abstract class IntegralMetric extends Metric implements Clockable {
	/* Attributes. */
	/** The clock that controls the collecting of the value of the metric. */
	private Clock clock;

	/** Holds the previous collected value of the metric. */
	protected double previous_value;

	/* Methods. */
	/**
	 * Constructor.
	 * 
	 * @param thread_name
	 *            The name of the thread of the clock of the metric.
	 * @param value
	 *            The initial value of the metric.
	 */
	public IntegralMetric(String thread_name, double value) {
		this.clock = new Clock(thread_name + "'s clock", this);
		this.previous_value = value;
	}

	/** Starts the collecting of the metric. */
	public void start() {
		this.clock.start();
	}

	/** Stops the collecting of the metric. */
	public void stopActing() {
		this.clock.stopActing();
	}
}
