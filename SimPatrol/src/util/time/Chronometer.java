/* Chronometer.java */

/* The package of this class. */
package util.time;

/** Implements a real time chronometer. */
public final class Chronometer extends Thread {
	/* Attributes. */
	/** The object of which action must be chronometerized. */
	private final Chronometerable OBJECT;

	/**
	 * The deadline, when the chronometerized object must stop acting. Measured
	 * in seconds.
	 */
	private final double DEADLINE;

	/* Methods. */
	/**
	 * Constructor.
	 * 
	 * @param name
	 *            The name of the thread of the chronometer.
	 * @param object
	 *            The object of which action must be chronometerized.
	 * @param deadline
	 *            The deadline, when the chronometerized object must stop
	 *            acting. Measured in seconds.
	 */
	public Chronometer(String name, Chronometerable object, double deadline) {
		super(name);
		this.OBJECT = object;
		this.DEADLINE = deadline;
	}

	public void run() {
		// lets the chronometerized object start acting
		this.OBJECT.startActing();

		// lets the chronometer sleep until the deadline
		try {
			Chronometer.sleep((long) (this.DEADLINE * 1000));
		} catch (InterruptedException e) {
			e.printStackTrace(); // traced interrupted exception
		}

		// lets the chronometerized object stop acting
		this.OBJECT.stopActing();
	}
}