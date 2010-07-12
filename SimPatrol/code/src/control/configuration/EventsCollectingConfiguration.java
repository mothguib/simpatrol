/* EventsCollectingConfiguration.java */

/* The package of this class. */
package control.configuration;

/* Imported classes and/or interfaces. */
import control.event.Event;

/**
 * Implements objects that express configurations to collect events from the
 * simulation through UDP sockets.
 * 
 * @see Event
 */
public final class EventsCollectingConfiguration extends Configuration {
	/* Methods. */
	public String fullToXML(int identation) {
		// holds the answer to the method
		StringBuffer buffer = new StringBuffer();

		// applies the identation and fills the "configuration" tag
		for (int i = 0; i < identation; i++)
			buffer.append("/t");
		buffer.append("<configuration type=\""
				+ ConfigurationTypes.EVENT_COLLECTING + "\"/>\n");

		// return the answer to the method
		return buffer.toString();
	}
}
