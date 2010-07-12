/* Metric.java */

/* The package of this class. */
package model.metric;

/* Imported classes and/or interfaces. */
import model.Environment;
import view.XMLable;

/**
 * Implements the metrics about the environment to be patrolled. They are
 * collected during the simulation of the patrolling task.
 */
public abstract class Metric implements XMLable {
	/* Attributes. */
	/**
	 * The environment of the simulation of which metrics are to be collected.
	 * Shared among all the metrics.
	 */
	protected static Environment environment;

	/* Methods. */
	/**
	 * Configures the environment of the simulation of which metrics are to be
	 * collected.
	 * 
	 * @param simpatrol_environment
	 *            The environment of the simulation of which metrics are to be
	 *            collected.
	 */
	public static void setEnvironment(Environment simpatrol_environment) {
		environment = simpatrol_environment;
	}

	/**
	 * Returns the current value of the collected metric.
	 * 
	 * @return The current value of the metric.
	 */
	public abstract double getValue();

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
