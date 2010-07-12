/* SocietiesCreationConfiguration.java */

/* The package of this class. */
package control.configuration;

/* Imported classes and/or interfaces. */
import java.util.HashSet;
import java.util.Set;

import model.agent.Society;

/** Implements objects that express configurations to create
 *  societies in the simulation.
 *  
 *  @see Society */
public final class SocietiesCreationConfiguration extends Configuration {
	/* Attributes. */
	private Set<Society> societies;
	
	/* Methods. */
	/** Constructor.
	 * 
	 *  @param sender_address The IP address of the sender of the configuration.
	 *  @param sender_socket The number of the UDP socket of the sender.
	 *  @param societies The societies being created by the configuration. */
	public SocietiesCreationConfiguration(String sender_address, int sender_socket, Society[] societies) {
		super(sender_address, sender_socket);
		
		this.societies = new HashSet<Society>();
		for(int i = 0; i < societies.length; i++)
			this.societies.add(societies[i]);
	}
	
	/** Returns the societies of the configuration.
	 * 
	 *  @return The societies of the configuration. */
	public Society[] getSocieties() {
		Object[] societies_array = this.societies.toArray();
		Society[] answer = new Society[societies_array.length];
		for(int i = 0; i < answer.length; i++)
			answer[i] = (Society) societies_array[i];
		
		return answer;
	}

	@Override
	protected int getType() {
		return ConfigurationTypes.SOCIETIES_CREATION;
	}

	public String fullToXML(int identation) {
		// holds the answer to the method
		StringBuffer buffer = new StringBuffer();
		
		// applies the identation and fills the "configuration" tag
		for(int i = 0; i < identation; i++) buffer.append("/t");
		buffer.append("<configuration type=\"" + ConfigurationTypes.SOCIETIES_CREATION +
				      "\" sender_adress=\"" + this.sender_address +
				      "\" sender_socket=\"" + this.sender_socket +
				      "\">\n");
		
		// puts the societies
		Object[] societies_array = this.societies.toArray();
		for(int i = 0; i < societies_array.length; i++)
			buffer.append(((Society) societies_array[i]).fullToXML(identation + 1));
		
		// closes the tag
		for(int i = 0; i < identation; i++) buffer.append("/t");
		buffer.append("</configuration>\n");
		
		// return the answer to the method
		return buffer.toString();
	}
}