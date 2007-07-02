/* Perception.java */

/* The package of this class. */
package model.perception;

/* Imported classes and/or interfaces. */
import model.interfaces.XMLable;

/** Implements the perceptions of the agents of SimPatrol. */
public abstract class Perception implements XMLable {
	public String getObjectId() {
		// a perception doesn't need an id
		return null;
	}

	public void setObjectId(String object_id) {
		// a perception doesn't need an id
		// so do nothing
	}
}
