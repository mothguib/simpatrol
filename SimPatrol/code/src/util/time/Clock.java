/* Clock.java */

/* The package of this class. */
package util.time;

/**
 * Implements a real time clock, that with a specific periodicity (measured in
 * seconds), sends signals to a Clockable object in order to let it act.
 * 
 * @see Clockable
 */
public final class Clock extends Thread {
	/* Attributes. */
	/** Registers if the clock is active. */
	private boolean is_active;

	/** The object to be clocked. */
	private final Clockable OBJECT;

	/**
	 * The clock's counting step. Its default value is 1 second.
	 */
	private double step = 1;

	/* Methods. */
	/**
	 * Constructor.
	 * 
	 * @param name
	 *            The name of the thread of the clock.
	 * @param object
	 *            The object to be clocked.
	 */
	public Clock(String name, Clockable object) {
		super(name);
		this.OBJECT = object;
		this.is_active = true;
	}

	/**
	 * Changes the clock's counting step.
	 * 
	 * @param step
	 *            The counting step, measured in seconds.
	 */
	public void setStep(double step) {
		this.step = step;
	}

	/** Indicates that the clock must stop acting. */
	public void stopActing() {
		this.is_active = false;

	}

	public void run() {
		while (this.is_active) {
			try {
				Clock.sleep((long) (this.step * 1000));
			} catch (InterruptedException e) {
				e.printStackTrace(); // traced interrupted exception
			}

			if (this.is_active)
				this.OBJECT.act();
		}
	}
}