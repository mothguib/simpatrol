/* MetricTypes.java */

/* The package of this class. */
package model.metric;

/**
 * Holds the types of metrics implemented by SimPatrol.
 * 
 * @see Metric
 * @developer New Metric types must be added here.
 */
public abstract class MetricTypes {
	public static final int MEAN_INSTANTANEOUS_IDLENESS = 0;

	public static final int MAX_INSTANTANEOUS_IDLENESS = 1;

	public static final int MEAN_IDLENESS = 2;

	public static final int MAX_IDLENESS = 3;
}
