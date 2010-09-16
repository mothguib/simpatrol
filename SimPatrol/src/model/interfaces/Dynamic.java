/* Dynamic.java */

/* The package of this interface. */
package model.interfaces;

/* Imported classes and/or interfaces. */
import model.etpd.EventTimeProbabilityDistribution;

/**
 * Lets the objects that implement it have a dynamic enabling behavior.
 */
public interface Dynamic {
	/**
	 * Returns the probability distribution for the enabling time of the dynamic
	 * object.
	 * 
	 * @return The event time probability distribution for the enabling of the
	 *         object.
	 */
	public EventTimeProbabilityDistribution getEnablingTPD();

	/**
	 * Returns the probability distribution for the disabling time of the
	 * dynamic object.
	 * 
	 * @return The event time probability distribution for the disabling of the
	 *         object.
	 */
	public EventTimeProbabilityDistribution getDisablingTPD();

	/**
	 * Returns if the dynamic object is enabled.
	 * 
	 * @return TRUE, if the object is enabled, FALSE if not.
	 */
	public boolean isEnabled();

	/**
	 * Configures if the dynamic object is enabled.
	 * 
	 * @param is_enabled
	 *            TRUE, if the object is enabled, FALSE if not.
	 */
	public void setIsEnabled(boolean is_enabled);
}