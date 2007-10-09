/* EnvironmentCreationConfiguration.java */

/* The package of this class. */
package control.configuration;

/* Imported classes and/or interfaces. */
import model.Environment;

/** Implements objects that express configurations to set the environment
 *  (graph + societies) of a simulation.
 *  
 *  @see Environment */
public final class EnvironmentCreationConfiguration extends Configuration {
	/* Attributes. */
	/** The environment to be set to the simulation. */
	private Environment environment;
	
	/* Methods. */
	/** Constructor.
	 * 
	 *  @param environment The environment to be set. */	
	public EnvironmentCreationConfiguration(Environment environment) {
		super();
		this.environment = environment;
	}
	
	/** Returns the environment of the configuration. */
	public Environment getEnvironment() {
		return this.environment;
	}
	
	public String fullToXML(int identation) {
		// holds the answer to the method
		StringBuffer buffer = new StringBuffer();
		
		// applies the identation and fills the "configuration" tag
		for(int i = 0; i < identation; i++) buffer.append("/t");
		buffer.append("<configuration type=\"" + ConfigurationTypes.ENVIRONMENT_CREATION +
				      "\">\n");
		
		// puts the environment
		buffer.append(this.environment.fullToXML(identation + 1));
		
		// closes the tag
		for(int i = 0; i < identation; i++) buffer.append("/t");
		buffer.append("</configuration>\n");
		
		// return the answer to the method
		return buffer.toString();
	}
}