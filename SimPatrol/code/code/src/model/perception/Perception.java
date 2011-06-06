/* Perception.java */

/* The package of this class. */
package model.perception;

/* Imported classes and/or interfaces. */
import model.limitation.DepthLimitation;
import model.limitation.StaminaLimitation;
import view.XMLable;

/**
 * Implements the perceptions of the agents of SimPatrol.
 * 
 * The production of perceptions can be controlled by stamina and depth
 * limitations.
 * 
 * @see StaminaLimitation
 * @see DepthLimitation
 */
public abstract class Perception implements XMLable {
	/* Methods. */
	public String reducedToXML(int identation) {
		// a perception doesn't have a lighter version
		return this.fullToXML(identation);
	}

	public String getObjectId() {
		// a perception doesn't need an id
		return null;
	}

	public void setObjectId(String object_id) {
		// a perception doesn't need an id
		// so do nothing
	}
}
