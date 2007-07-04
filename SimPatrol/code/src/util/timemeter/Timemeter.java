/* Timemeter.java */

/* The package of this class. */
package util.timemeter;

/** Implements an object that can count the time. */
public abstract class Timemeter {
	/* Attributes. */
	/** Holds the elapsed time. */
	protected int elapsed_time;
	
	/* Methods. */
	/** Constructor. */
	public Timemeter() {
		this.elapsed_time = 0;
	}
	
	/** Returns the elapsed time.
	 * @return The elapsed counted time. */
	public int getElapsedTime() {
		return this.elapsed_time;
	}
}