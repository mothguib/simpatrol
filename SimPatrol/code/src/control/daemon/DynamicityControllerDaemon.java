/* DynamicityControllerDaemon.java */

/* The package of this class. */
package control.daemon;

/* Imported classes and/or interfaces. */
import control.simulator.RealTimeSimulator;
import model.interfaces.Dynamic;

/** Implements the daemons that assure dynamic behaviour
 *  to the dynamic objects.
 *  Used by real time simulators.
 *  @see RealTimeSimulator */
public class DynamicityControllerDaemon extends ClockedDaemon {
	/* Attributes. */
	/** The dynamic object to be controlled. */
	private Dynamic object;
	
	/* Methods. */
	/** Constructor.
	 *  @param object The dynamic object to be controlled. */
	public DynamicityControllerDaemon(Dynamic object) {		
		this.object = object;
	}
	
	public void act() {
		// if the dynamic object is appering
		if(this.object.isAppearing()) {
			// atualizes the appearing tpd
			this.object.getAppearingTPD().nextBoolean();
			
			// verifies if the object must disappear now
			if(this.object.getDisappearingTPD().nextBoolean())
				this.object.setIsAppearing(false);
		}
		// else
		else {
			// verifies if the object must appear now
			if(this.object.getAppearingTPD().nextBoolean())
				this.object.setIsAppearing(true);
			
			// atualizes the disappearing tpd
			this.object.getDisappearingTPD().nextBoolean();
		}
	}
}