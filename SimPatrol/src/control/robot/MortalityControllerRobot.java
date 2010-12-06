/* MortalityControllerRobot.java */

/* The package of this class. */
package control.robot;

/* Imported classes and/or interfaces. */
import model.agent.Agent;
import model.etpd.EventTimeProbabilityDistribution;
import model.interfaces.Mortal;
import control.simulator.RealTimeSimulator;

/**
 * Implements the robots that assure the death of the mortal objects of a
 * simulation.
 * 
 * Used by real time simulators.
 * 
 * @see RealTimeSimulator
 */
public final class MortalityControllerRobot extends Robot {
	/* Attributes. */
	/** The mortal object to be controlled. */
	private final Mortal OBJECT;

	/* Methods. */
	/**
	 * Constructor.
	 * 
	 * @param clock_thread_name
	 *            The name of the thread of the clock of this robot.
	 * @param object
	 *            The mortal object to be controlled.
	 */
	public MortalityControllerRobot(String clock_thread_name, Mortal object) {
		super(clock_thread_name);
		this.OBJECT = object;
	}

	/**
	 * Returns the mortal object controlled by the robot.
	 * 
	 * @return The mortal object controlled by the robot.
	 */
	public Mortal getObject() {
		return this.OBJECT;
	}

	public void act() {
		synchronized (simulator) {
			simulator.getState(); // synchronization

			// obtains the probability distribution for the death of the
			// mortal object
			EventTimeProbabilityDistribution death_tpd = this.OBJECT
					.getDeathTPD();

			// if there's a death tpd and the object must die now
			if (death_tpd != null && death_tpd.nextBoolean()) {
				// kills the object
				this.OBJECT.die();

				// if the object is an agent, stops its daemons
				if (this.OBJECT instanceof Agent) {
					simulator.stopAndRemoveAgentDaemons((Agent) this.OBJECT);

					// stops and removes its eventual stamina controller
					// robot
					simulator
							.stopAndRemoveStaminaControllerRobot((Agent) this.OBJECT);
				}

				// stops this robot
				this.stopActing();

				// removes this robot from the real time simulator
				simulator.removeMortalityControllerRobot(this);

				// quits the method
				return;
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