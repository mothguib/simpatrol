/* ClockedDaemon.java */

/* The package of this class. */
package control.daemon;

/* Imported classes and/or interfaces. */
import util.clock.Clock;
import util.clock.Clockable;

/** Implements a daemon that acts oriented by signals from a clock. */
public abstract class ClockedDaemon extends Daemon implements Clockable {
	/* Attributes. */
	/** The clock of the daemon. */
	private Clock clock = new Clock(this);
	
	/* Methods. */
	/** Starts the clock's work, and so on the daemon's. */
	public void startWorking() {
		this.clock.start();
	}
	
	/** Stops the clock's work and so on the daemon's. */
	public void stopWorking() {
		this.clock.stopWorking();
	}
}
