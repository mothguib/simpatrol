/* Limitation.java */

/* The package of this class. */
package model.limitation;

/* Imported classes and/or interfaces. */
import model.interfaces.XMLable;

/** Implements the limitations imposed to the permissions
 *  that control the existence of the agents of SimPatrol. */
public abstract class Limitation implements XMLable {
	public String getObjectId() {
		// a limitation doesn't need an id
		return null;
	}

	public void setObjectId(String object_id) {
		// a limitation doesn't need an id
		// so, do nothing	
	}	
}
