/* Metric.java */

/* The package of this class. */
package model.metric;

/* Imported classes and/or interfaces. */
import view.XMLable;
import control.simulator.Simulator;

/** Implements the metrics to be collected during the simulation
 *  of the patrolling task. */
public abstract class Metric implements XMLable {
	/* Attributes. */
	/** The simulator of the patrolling task. */
	protected static Simulator simulator;
	
	/* Methods. */
	/** Configures the simulator of the patrolling task. */
	public static void setSimulator(Simulator patrol_simulator) {
		simulator = patrol_simulator;
	}
	
	/** Returns the current value of the collected metric.
	 *  
	 *  @return The current value of the metric. */
	public abstract double getValue();
	
	/** Returns the type of the metric.
	 * 
	 *  @return The typeof the metric.
	 *  @see MetricTypes */
	public abstract int getType();
	
	public String reducedToXML(int identation) {
		// a metric doesn't have a lighter version
		return this.fullToXML(identation);
	}
	
	public String getObjectId() {
		// a metric doesn't need an id
		return null;
	}
	
	public void setObjectId(String object_id) {
		// a metric doesn't need an id
		// so, do nothing
	}	
}
