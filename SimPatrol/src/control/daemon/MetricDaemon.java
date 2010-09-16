/* MetricDaemon.java */

/* The package of this class. */
package control.daemon;

/* Imported classes and/or interfaces. */
import java.io.IOException;
import util.time.Clock;
import util.time.Clockable;
import view.connection.UDPConnection;
import model.metric.IntegralMetric;
import model.metric.Metric;

/**
 * Implements the daemons of SimPatrol that gather the chosen metrics of the
 * simulation.
 * 
 * @modeler This class must have its behavior modeled.
 */
public final class MetricDaemon extends Daemon implements Clockable {
	/* Attributes. */
	/** The clock that controls the daemon's work. */
	private final Clock CLOCK;

	/** The metric to be collected. */
	private final Metric METRIC;

	/* Methods. */
	/**
	 * Constructor.
	 * 
	 * @param thread_name
	 *            The name of the thread of the daemon.
	 * @param metric
	 *            The metric to be collected by the daemon.
	 * @param cycle_duration
	 *            The duration, in seconds, of a cycle of measurement of the
	 *            metric.
	 */
	public MetricDaemon(String thread_name, Metric metric, double cycle_duration) {
		super(thread_name);
		this.connection = new UDPConnection(thread_name + "'s  connection",
				this.BUFFER);

		this.CLOCK = new Clock(thread_name + "'s clock", this);
		this.CLOCK.setStep(cycle_duration);

		this.METRIC = metric;
	}

	/**
	 * Returns the metric collected by this daemon.
	 * 
	 * @return The metric collected by this daemon.
	 */
	public Metric getMetric() {
		return this.METRIC;
	}

	/** Starts the metric counting, if the metric is an integral one. */
	public void startMetric() {
		if (this.METRIC instanceof IntegralMetric)
			((IntegralMetric) this.METRIC).start();
	}

	public void start(int local_socket_number) throws IOException {
		super.start(local_socket_number);
		this.CLOCK.start();
	}

	public void stopActing() {
		super.stopActing();
		this.CLOCK.stopActing();

		if (this.METRIC instanceof IntegralMetric) {
			((IntegralMetric) this.METRIC).stopActing();
		}
	}

	/** @modeler This method must be modeled. */
	public void act() {
		this.connection.send(this.METRIC.fullToXML(0));
	}
}