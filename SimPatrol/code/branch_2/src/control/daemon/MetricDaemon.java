/* MetricDaemon.java */

/* The package of this class. */
package control.daemon;

/* Imported classes and/or interfaces. */
import java.io.IOException;
import util.clock.Clock;
import util.clock.Clockable;
import view.connection.UDPConnection;
import model.metric.IntegralMetric;
import model.metric.Metric;

/**
 * Implements the daemons of SimPatrol that gather the chosen metrics of the
 * simulation.
 * 
 * @modeller This class must have its behaviour modelled.
 */
public final class MetricDaemon extends Daemon implements Clockable {
	/* Attributes. */
	/** The clock that controls the daemon's work. */
	private Clock clock;

	/** The metric to be collected. */
	private Metric metric;

	/* Methods. */
	/**
	 * Constructor.
	 * 
	 * @param thread_name
	 *            The name of the thread of the daemon.
	 * @param metric
	 *            The metric to be collected by the daemon.
	 * @param cycle_duration
	 *            The duration, in seconds of a cycle of measurement of the
	 *            metric.
	 */
	public MetricDaemon(String thread_name, Metric metric, int cycle_duration) {
		super(thread_name);
		this.connection = new UDPConnection(thread_name + "'s  connection",
				this.BUFFER);

		this.clock = new Clock(thread_name + "'s clock", this);
		this.clock.setStep(cycle_duration);

		this.metric = metric;
	}

	/** Starts the metric counting, if the metric is an integral one. */
	public void startMetric() {
		if (this.metric instanceof IntegralMetric)
			((IntegralMetric) this.metric).start();
	}

	public void start(int local_socket_number) throws IOException {
		super.start(local_socket_number);
		this.clock.start();
	}

	public void stopWorking() throws IOException {
		super.stopWorking();
		this.clock.stopWorking();

		if (this.metric instanceof IntegralMetric) {
			((IntegralMetric) this.metric).stopWorking();
		}
	}

	/** @modeller This method must be modelled. */
	public void act(int time_gap) {
		try {
			this.connection.send(this.metric.fullToXML(0));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * @return The current metric
	 */
	public Metric getMetric() {
		return metric;
	}
}