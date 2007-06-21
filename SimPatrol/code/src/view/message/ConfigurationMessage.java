/* ConfigurationMessage.java */

/* The package of this class. */
package view.message;

/* Imported classes and/or interfaces. */
import control.configuration.Configuration;

/** Implements the messages received by SimPatrol that
 *  contains configurations for the simulation. */
public class ConfigurationMessage extends Message {
	/* Methods. */
	/** Constructor.
	 *  @param content The configuration of the message. */
	public ConfigurationMessage(Configuration content) {
		super(content);
	}
}
