/* DynamicityControlleRobot.java */

/* The package of this class. */
package control.robot;

/* Imported classes and/or interfaces. */
import control.simulator.RealTimeSimulator;
import model.interfaces.Dynamic;

/**
 * Implements the robots that assure dynamic behavior to the dynamic objects of
 * the simulation.
 * 
 * Used by real time simulators.
 * 
 * @see RealTimeSimulator
 */
public final class DynamicityControllerRobot extends Robot {
	/* Attributes. */
	/** The dynamic object to be controlled. */
	private final Dynamic OBJECT;

	/* Methods. */
	/**
	 * Constructor.
	 * 
	 * @param clock_thread_name
	 *            The name of the thread of the clock of this robot.
	 * @param object
	 *            The dynamic object to be controlled.
	 */
	public DynamicityControllerRobot(String clock_thread_name, Dynamic object) {
		super(clock_thread_name);
		this.OBJECT = object;
	}

	/**
	 * Returns the dynamic object controlled by this robot.
	 * 
	 * @return The dynamic object controlled by this robot.
	 */
	public Dynamic getObject() {
		return OBJECT;
	}

	public void act() {
		synchronized (simulator) {
			simulator.getState(); // synchronization

			// if the dynamic object is enabled
			if (this.OBJECT.isEnabled()) {
				// updates the enabling tpd
				this.OBJECT.getEnablingTPD().nextBoolean();

				// verifies if the object must be disabled now
				if (this.OBJECT.getDisablingTPD().nextBoolean())
					this.OBJECT.setIsEnabled(false);
			}
			// else
			else {
				// verifies if the object must be enabled now
				if (this.OBJECT.getEnablingTPD().nextBoolean())
					this.OBJECT.setIsEnabled(true);

				// updates the disabling tpd
				this.OBJECT.getDisablingTPD().nextBoolean();
			}
		}
	}

	public void start() {
		// used by AspectJ
		super.start();
	}

	public void stopActing() {
		// used by AspectJ
		super.stopActing();
	}
}