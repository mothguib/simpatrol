/* DynamicityControllerDaemon.java */

/* The package of this class. */
package control.daemon;

/* Imported classes and/or interfaces. */
import model.interfaces.Dynamic;

/** Implements the daemons that assures the vertexes, edges and agents 
 *  dynamic behavior. */
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
	
	/** Returns the dynamic object controlled by the daemon.
	 *  @return The dynamic object. */
	public Dynamic getDynamicObject() {
		return this.object;
	}
	
	
	public void act() {
		// if the dynamic object is appering
		if(this.object.isAppearing()) {
			// atualizes the appearing etpd
			this.object.getAppearingETPD().nextBoolean();
			
			// verifies if the object must disappear now
			if(this.object.getDisappearingETPD().nextBoolean())
				this.object.setIsAppearing(false);
		}
		// else
		else {
			// verifies if the object must appear now
			if(this.object.getAppearingETPD().nextBoolean())
				this.object.setIsAppearing(true);
			
			// atualizes the disappearing etpd
			this.object.getDisappearingETPD().nextBoolean();
		}
	}
}