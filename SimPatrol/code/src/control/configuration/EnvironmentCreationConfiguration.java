/* EnvironmentCreationConfiguration.java */

/* The package of this class. */
package control.configuration;

import model.Environment;

/** Implements objects that express configurations to create
 *  the environment of the simulation. */
public class EnvironmentCreationConfiguration extends Configuration {
	/* Attributes. */
	/** The environment being created. */
	private Environment environment;
	
	/* Methods. */
	/** Constructor. */
	public EnvironmentCreationConfiguration(String sender_address, int sender_socket, Environment environment) {
		super(sender_address, sender_socket);
		this.environment = environment;
	}
	
	/** Returns the environment of the configuration.
	 *  @return The environment breing created. */
	public Environment getEnvironment() {
		return this.environment;
	}
	
	public int getType() {
		return ConfigurationTypes.ENVIRONMENT_CREATION;
	}

	public String toXML(int identation) {
		// holds the answerto the method
		StringBuffer buffer = new StringBuffer();
		
		// applies the identation and fills the "configuration" tag
		for(int i = 0; i < identation; i++) buffer.append("/t");
		buffer.append("<configuration sender_adress = \"" + this.sender_address +
				      "\" sender_socket=\"" + this.sender_socket +
				      "\" type = \"" + this.getType() +
				      "\"/>\n");
		
		// puts the environment
		buffer.append(this.environment.toXML(identation + 1));
		
		// closes the tag
		for(int i = 0; i < identation; i++) buffer.append("/t");
		buffer.append("</configuration>\n");		
		
		// return the answer to the method
		return buffer.toString();
	}
}