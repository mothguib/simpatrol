/* DynamicityControlleRobot.java */

/* The package of this class. */
package control.robot;

/* Imported classes and/or interfaces. */
import control.simulator.RealTimeSimulator;
import model.interfaces.Dynamic;

/**
 * Implements the robots that assure dynamic behaviour to the dynamic objects of
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

	public void act(int time_gap) {
		synchronized (simulator) {
			simulator.getState();

			for (int i = 0; i < time_gap; i++) {
				// if the dynamic object is enabled
				if (this.OBJECT.isEnabled()) {
					// atualizes the enabling tpd
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

					// atualizes the disabling tpd
					this.OBJECT.getDisablingTPD().nextBoolean();
				}
			}
		}
	}

	public void start() {
		super.start();
	}

	public void stopWorking() {
		super.stopWorking();
	}

	/**
	 * @return The current Dynamic OBJECT
	 */
	public Dynamic getOBJECT() {
		return OBJECT;
	}
}