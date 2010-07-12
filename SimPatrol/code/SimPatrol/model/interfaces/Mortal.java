/* Mortal.java */

/* The package of this interface. */
package model.interfaces;

/* Imported classes and/or interfaces. */
import model.etpd.EventTimeProbabilityDistribution;

/** Lets the objects that implement it die (i.e. disappear forever). */
public interface Mortal {
	/**
	 * Returns the probability distribution for the death time of the mortal
	 * object.
	 * 
	 * @return The event time probability distribution for the death of the
	 *         object.
	 */
	public EventTimeProbabilityDistribution getDeathTPD();

	/** Kills the mortal object. */
	public void die();
}