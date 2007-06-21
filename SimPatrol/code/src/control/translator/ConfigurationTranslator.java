/* ConfigurationTranslator.java */

/* The package of this class. */
package control.translator;

/* Imported classes and/or interfaces. */
import model.Environment;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import control.configuration.Configuration;
import control.configuration.ConfigurationTypes;
import control.configuration.EnvironmentCreationConfiguration;

/** Implements a translator that obtains configurations
 *  from a given XML source.
 *  @see Configuration */
public abstract class ConfigurationTranslator extends Translator {
	/* Methods. */
	/** Obtains the configurations from the given XML element.
	 *  @param xml_element The XML source containing the configurations.
	 *  @return The configurations from the XML source. */
	public static Configuration[] getConfigurations(Element xml_element) {
		// obtains the nodes with the "configuration" tag
		NodeList configuration_node = xml_element.getElementsByTagName("configuration");
		
		// the answer to the method
		Configuration[] answer = new Configuration[configuration_node.getLength()];
		
		// for each configuration_node
		for(int i = 0; i < answer.length; i++) {
			// obtains the current configuration element
			Element configuration_element = (Element) configuration_node.item(i);
			
			// obtains its data
			String sender_address = configuration_element.getAttribute("sender_address");
			int sender_socket = Integer.parseInt(configuration_element.getAttribute("sender_socket"));
			int type = Integer.parseInt(configuration_element.getAttribute("type"));
			
			// creates the new configuration
			switch(type) {
				case(ConfigurationTypes.ENVIRONMENT_CREATION): {
					// obtains the environment from the tag content
					Environment environment = EnvironmentTranslator.getEnvironments(configuration_element)[0];
					
					// new environment creation configuration
					answer[i] = new EnvironmentCreationConfiguration(sender_address, sender_socket, environment);
					break;
				}
			}
		}
		
		// returns the answer
		return answer;
	}
}
