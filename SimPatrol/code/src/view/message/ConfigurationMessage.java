/* ConfigurationMessage.java */

/* The package of this class. */
package view.message;

import model.interfaces.XMLable;

/** Implements the messages received by SimPatrol that
 *  contains configurations for the simulation. */
public class ConfigurationMessage extends Message {
	/* Methods. */
	/** Constructor.
	 *  @param content The content of the message. */
	public ConfigurationMessage(XMLable content) {
		super(content);
	}

	protected int getType() {
		return MessageTypes.CONFIGURATION;
	}
}
