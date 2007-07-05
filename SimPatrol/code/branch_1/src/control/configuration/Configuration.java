/* Configuration.java */

/* The package of this class. */
package control.configuration;

/* Imported classes and/or interfaces. */
import model.interfaces.XMLable;

/** Implements objects that express configurations to
 *  a simulation of the patrolling task. */
public abstract class Configuration implements XMLable {
	/* Attributes. */
	/** The IP address of the sender of the configuration. */
	protected String sender_address;
	
	/** The number of the UDP socket of the sender. */
	protected int sender_socket;
	
	/* Methods. */
	/** Constructor.
	 *  @param sender_address The The IP address of the sender of the configuration.
	 *  @param sender_socket The number of the UDP socket of the sender. */
	public Configuration(String sender_address, int sender_socket) {
		this.sender_address = sender_address;
		this.sender_socket = sender_socket;		
	}
	
	/** Returns the IP address of the sender of the message
	 *  that contained the configuration.
	 *  @return The IP address of the sender of the configuration. */
	public String getSender_address() {
		return this.sender_address;
	}
	
	/** Returns the number of the UDP socket connection of
	 *  the sender of the message that contained the
	 *  configuration.
	 *  @return The number of the UDP socket of the sender. */
	public int getSender_socket() {
		return this.sender_socket;
	}
	
	/** Returns the type of the configuration.
	 *  @return The type of the configuration. 
	 *  @see ConfigurationTypes */
	protected abstract int getType();
	
	public String getObjectId() {
		// a configuration doesn't need an id
		return null;
	}

	public void setObjectId(String object_id) {
		// a configuration doesn't need an id
		// so, do nothing
	}
}