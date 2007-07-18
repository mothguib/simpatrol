/* MetricDaemon.java */

/* The package of this class. */
package control.daemon;

/* Imported classes and/or interfaces. */
import java.io.IOException;
import util.clock.Clock;
import util.clock.Clockable;
import model.metric.Metric;

/** Implements the daemons of SimPatrol that gather
 *  the chosen metrics of the simulation.
 *  
 *  @modeller This class must have its behaviour modelled. */
public final class MetricDaemon extends Daemon implements Clockable {
	/* Attributes. */
	/** The clock that controls the daemon's work. */
	private Clock clock;	
	
	/** The metric to be collected. */
	private Metric metric;
	
	/* Methods. */
	/** Constructor.
	 * 
	 *  @param thread_name The name of the thread of the daemon.
	 *  @param metric The metric to be collected by the daemon.
	 *  @param cycle_duration The duration, in seconds of a cycle of measurement of the metric. */	
	public MetricDaemon(String thread_name, Metric metric, int cycle_duration) {
		super(thread_name);
		
		this.clock = new Clock(thread_name + "'s clock", this);
		this.clock.setStep(cycle_duration);
		
		this.metric = metric;
	}
	
	/** Indicates that the daemon must stop working. */
	public void stopWorking() {
		super.stopWorking();		
		this.clock.stopWorking();
		
		// screen message
		System.out.println("[SimPatrol.MetricDaemon(" + this.metric.getType() + ")]: Stopped working.");
	}
	
	public void startClock() {
		this.clock.start();
	}
	
	/** @modeller This method must be modelled. */
	public void act(int time_gap) {
		try { this.connection.send(this.metric.fullToXML(0)); }
		catch (IOException e) { e.printStackTrace(); }
	}
}