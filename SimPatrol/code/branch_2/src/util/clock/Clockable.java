/* Clockable.java */

/* The package of this interface. */
package util.clock;

/** Lets the objects that implement it be clocked. */
public interface Clockable {
	/** Forces the objects to act when they're clocked.
	 * 
	 *  @param time_gap The gap of time separating the current and the last call to the method. */
	public void act(int time_gap);
}