/* Chronometerable.java */

/* The package of this interface. */
package util.chronometer;

/** Lets the objects that implement it be chronometerized. */
public interface Chronometerable {
	/** Forces the objects to have a start working method. */
	public void startWorking();
	
	/** Forces the objects to have a finish working method.*/
	public void stopWorking();		
}