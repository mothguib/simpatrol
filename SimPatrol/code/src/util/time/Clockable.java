/* Clockable.java */

/* The package of this interface. */
package util.time;

/**
 * Lets the objects that implement it be clocked, i.e. receive signals to
 * periodically act.
 */
public interface Clockable {
	/**
	 * Forces the objects to act when they are clocked.
	 * 
	 * @param time_gap
	 *            The gap of time separating the current and the last call to
	 *            this method.
	 */
	public void act();
}