/* Robot.java */

/* The package of this class. */
package control.robot;

/* Imported classes and/or interfaces. */
import control.simulator.RealTimeSimulator;
import util.clock.Clock;
import util.clock.Clockable;

/** Implements a robot that acts oriented by signals from a clock. */
public abstract class Robot implements Clockable {
	/* Attributes. */
	/** The clock of the robot. */
	private final Clock CLOCK;
	
	/** The simulator of the patrolling task, performed by SimPatrol.
	 *  Shared among all the robots. */
	protected static RealTimeSimulator simulator;
	
	/* Methods. */
	/** Constructor.
	 * 
	 *  @param clock_thread_name The name of the thread of the clock of this robot. */
	public Robot(String clock_thread_name) {
		this.CLOCK = new Clock(clock_thread_name, this);
	}
	
	/** Sets the simulator of the robot.
	 * 
	 *  @param simpatrol_simulator The simulator of SimPatrol. */
	public static void setSimulator(RealTimeSimulator simpatrol_simulator) {
		simulator = simpatrol_simulator;
	}
	
	/** Starts the clock's work, and so the robot's. */
	public void start() {
		this.CLOCK.start();
	}
	
	/** Stops the clock's work and so the robot's. */
	public void stopWorking() {
		this.CLOCK.stopWorking();
	}
}