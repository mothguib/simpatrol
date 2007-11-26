/* Configuration.java */

/* The package of this class. */
package control.configuration;

/* Imported classes and/or interfaces. */
import view.XMLable;

/**
 * Implements objects that express configurations of a simulation.
 */
public abstract class Configuration implements XMLable {
	/* Methods. */
	public String reducedToXML(int identation) {
		// a configuration doesn't have a lighter XML version
		return this.fullToXML(identation);
	}

	public String getObjectId() {
		// a configuration doesn't need an id
		return null;
	}

	public void setObjectId(String object_id) {
		// a configuration doesn't need an id
		// so, do nothing
	}
}