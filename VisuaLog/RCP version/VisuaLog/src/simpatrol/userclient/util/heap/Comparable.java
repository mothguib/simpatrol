/* Comparable.java */

/* The package of this class. */
package simpatrol.userclient.util.heap;

/** Lets the objects that implement it be compared among themselves. */
public interface Comparable {
	/**
	 * Verifies if the object is smaller than a given one.
	 * 
	 * @param object
	 *            The another object to be compared to this.
	 * @return TRUE if this object is smaller than the given one, FALSE if not.
	 */
	public boolean isSmallerThan(Comparable object);
}
