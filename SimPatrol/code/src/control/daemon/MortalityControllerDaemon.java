/* MortalityControllerDaemon.java */

/* The package of this class. */
package control.daemon;

/* Imported classes and/or interfaces. */
import util.etpd.EventTimeProbabilityDistribution;
import control.simulator.RealTimeSimulator;
import model.interfaces.Mortal;

/** Implements the daemons that assure the death of the mortal objects.
 *  Used by real time simulators. 
 *  @see RealTimeSimulator */
public class MortalityControllerDaemon extends ClockedDaemon {
	/* Attributes. */
	/** The mortal object to be controlled. */
	private Mortal object;

	/** The real time simulator of the patrolling task. */
	private RealTimeSimulator simulator;
	
	/* Methods. */
	/** Constructor.
	 *  @param object The mortal object to be controlled.
	 *  @param simulator The real time simulator to have its mortal objects dead. */
	public MortalityControllerDaemon(Mortal object, RealTimeSimulator simulator) {
		this.object = object;
		this.simulator = simulator;
	}

	public void act() {
		// obtains the probability distribution for the death of the mortal object
		EventTimeProbabilityDistribution death_tpd = this.object.getDeathTPD();
		
		// verifies if the object must die now
		if(death_tpd != null && death_tpd.nextBoolean()) {
			// kills the object
			this.object.die();
			
			// stops this daemon
			this.stopWorking();
			
			// removes this daemon from the rt simulator
			this.simulator.removeMortalityControllerDaemon(this);
		}			
	}
}