/* MortalityControllerRobot.java */

/* The package of this class. */
package control.robot;

/* Imported classes and/or interfaces. */
import control.simulator.RealTimeSimulator;
import util.etpd.EventTimeProbabilityDistribution;
import model.interfaces.Mortal;

/** Implements the robots that assure the death of the mortal objects
 *  of a simulation.
 *  Used by real time simulators. 
 *  @see RealTimeSimulator */
public final class MortalityControllerRobot extends Robot {
	/* Attributes. */
	/** The mortal object to be controlled. */
	private Mortal object;

	/** The real time simulator of the patrolling task. */
	private RealTimeSimulator simulator;
	
	/* Methods. */
	/** Constructor.
	 *  @param object The mortal object to be controlled.
	 *  @param simulator The real time simulator to have its mortal objects dead. */
	public MortalityControllerRobot(Mortal object, RealTimeSimulator simulator) {
		this.object = object;
		this.simulator = simulator;
	}

	public void act(int time_gap) {
		for(int i = 0; i < time_gap; i++) {
			// obtains the probability distribution for the death of the mortal object
			EventTimeProbabilityDistribution death_tpd = this.object.getDeathTPD();
			
			// if there's a death tpd and the object must die now 
			if(death_tpd != null && death_tpd.nextBoolean()) {
				// kills the object
				this.object.die();
				
				// stops this robot
				this.stopWorking();
				
				// removes this robot from the rt simulator
				this.simulator.removeMortalityControllerRobot(this);
			}
		}
	}
}