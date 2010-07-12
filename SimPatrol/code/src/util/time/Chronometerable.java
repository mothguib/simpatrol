/* Chronometerable.java */

/* The package of this interface. */
package util.time;

/**
 * Helps the objects that implement it have their actions chronometerized.
 * 
 * @see Chronometer
 */
public interface Chronometerable {
	/** Forces the objects to have a "start acting" method. */
	public void startActing();

	/** Forces the objects to have a "stop acting" method. */
	public void stopActing();
}