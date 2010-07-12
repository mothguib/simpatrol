/* Robot.java */

/* The package of this class. */
package control.robot;

/* Imported classes and/or interfaces. */
import util.clock.Clock;
import util.clock.Clockable;

/** Implements a robot that acts oriented by signals from a clock. */
public abstract class Robot implements Clockable {
	/* Attributes. */
	/** The clock of the robot. */
	private Clock clock;
	
	/* Methods. */
	/** Constructor.
	 *  @param clock_thread_name The name of the thread of the clock of this robot. */
	public Robot(String clock_thread_name) {
		this.clock = new Clock(clock_thread_name, this);
	}
	
	/** Starts the clock's work, and so the robot's.
	 *  @param name The name of the thread of the clock of this robot. */
	public void startWorking() {
		this.clock.start();
	}
	
	/** Stops the clock's work and so the robot's. */
	public void stopWorking() {
		this.clock.stopWorking();
	}
}