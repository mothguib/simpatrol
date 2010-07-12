/* TimeSensible.java */

/* The package of this class. */
package util.time;

/** Lets the objects that implement it be able to count the passage of time. */
public interface TimeSensible {
	/** Forces the objects that implement it return the elapsed time. */
	public double getElapsedTime();
}