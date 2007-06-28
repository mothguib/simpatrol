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
	private Clock clock = new Clock(this);
	
	/* Methods. */
	/** Starts the clock's work, and so the robot's. */
	public void startWorking() {
		this.clock.start();
	}
	
	/** Stops the clock's work and so the robot's. */
	public void stopWorking() {
		this.clock.stopWorking();
	}
}
