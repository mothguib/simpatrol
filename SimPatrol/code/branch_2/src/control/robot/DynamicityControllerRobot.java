/* DynamicityControlleRobot.java */

/* The package of this class. */
package control.robot;

/* Imported classes and/or interfaces. */
import control.simulator.RealTimeSimulator;
import model.interfaces.Dynamic;

/** Implements the robots that assure dynamic behaviour
 *  to the dynamic objects of the simulation.
 *  
 *  Used by real time simulators.
 *  
 *  @see RealTimeSimulator */
public final class DynamicityControllerRobot extends Robot {
	/* Attributes. */
	/** The dynamic object to be controlled. */
	private Dynamic object;
	
	/* Methods. */
	/** Constructor.
	 * 
	 *  @param clock_thread_name The name of the thread of the clock of this robot.
	 *  @param object The dynamic object to be controlled. */
	public DynamicityControllerRobot(String clock_thread_name, Dynamic object) {
		super(clock_thread_name);
		this.object = object;
	}
	
	public void act(int time_gap) {
		for(int i = 0; i < time_gap; i++) {		
			// if the dynamic object is enabled
			if(this.object.isEnabled()) {			
				// atualizes the enabling tpd
				this.object.getEnablingTPD().nextBoolean();
			
				// verifies if the object must be disabled now
				if(this.object.getDisablingTPD().nextBoolean())
					this.object.setIsEnabled(false);
			}
			// else
			else {
				// verifies if the object must be enabled now
				if(this.object.getEnablingTPD().nextBoolean())
					this.object.setIsEnabled(true);
				
				// atualizes the disabling tpd
				this.object.getDisablingTPD().nextBoolean();
			}
		}
	}
	
	public void start() {
		super.start();
		
		// screen message
		System.out.println("[SimPatrol.DynamicityRobot(" + this.object + ")]: Started working.");
	}
	
	public void stopWorking() {
		super.stopWorking();
		
		// screen message
		System.out.println("[SimPatrol.DynamicityRobot(" + this.object + ")]: Stopped working.");
	}
}