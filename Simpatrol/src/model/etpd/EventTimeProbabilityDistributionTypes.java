/* EventTimeProbabilityDistributionTypes.java */

/* The package of this class. */
package model.etpd;

/**
 * Holds the event time probability distribution types.
 * 
 * @see EventTimeProbabilityDistribution
 * @developer New ETPD types must be added here.
 */
public abstract class EventTimeProbabilityDistributionTypes {
	public static final int UNIFORM = 0;

	public static final int EMPIRICAL = 1;

	public static final int NORMAL = 2;

	public static final int SPECIFIC = 3;
}
