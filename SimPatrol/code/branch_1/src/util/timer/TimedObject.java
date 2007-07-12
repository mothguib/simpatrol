/* TimedObject.java */

/* The package of this class. */
package util.timer;

/** Lets the objects that implement it count somehow the time. */
public interface TimedObject {
	/** Forces the objects that implement it return the elapsed time. */
	public int getElapsedTime();
}