/* Timemeterable.java */

/* The package of this class. */
package util.timemeter;

/** Lets the objects that implement it count somehow the time. */
public interface Timemeterable {
	/** Forces the objects that implement it return the elapsed time. */
	public int getElapsedTime();
}