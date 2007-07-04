/* ConfigurationTranslator.java */

/* The package of this class. */
package control.translator;

/* Imported classes and/or interfaces. */
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import javax.xml.parsers.ParserConfigurationException;
import model.Environment;
import model.agent.Agent;
import model.graph.Graph;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;
import control.configuration.AgentCreationConfiguration;
import control.configuration.Configuration;
import control.configuration.ConfigurationTypes;
import control.configuration.EnvironmentCreationConfiguration;
import control.configuration.Orientation;
import control.configuration.SimulationConfiguration;

/** Implements a translator that obtains configurations and orientations
 *  from a given XML source.
 *  @see Configuration
 *  @see Orientation */
public abstract class ConfigurationTranslator extends Translator {
	/* Methods. */
	/** Obtains the configurations from the given XML element, except
	 *  for the "agent creation" configurations.
	 *  
	 *  To obtain "agent creation" configurations, use
	 *  getAgentCreationConfigurations(Element xml_element, Graph graph).
	 *  @see AgentCreationConfiguration
	 *  
	 *  @param xml_element The XML source containing the configurations.
	 *  @return The configurations from the XML source. 
	 *  @throws IOException 
	 *  @throws SAXException 
	 *  @throws ParserConfigurationException */
	public static Configuration[] getConfigurations(Element xml_element) throws ParserConfigurationException, SAXException, IOException {
		// obtains the nodes with the "configuration" tag
		NodeList configuration_node = xml_element.getElementsByTagName("configuration");
		
		// Holds the answer for the method
		List<Configuration> configurations = new LinkedList<Configuration>();
		
		// for each configuration_node
		for(int i = 0; i < configuration_node.getLength(); i++) {
			// obtains the current configuration element
			Element configuration_element = (Element) configuration_node.item(i);
			
			// obtains its data
			String sender_address = configuration_element.getAttribute("sender_address");
			int sender_socket = Integer.parseInt(configuration_element.getAttribute("sender_socket"));
			int type = Integer.parseInt(configuration_element.getAttribute("type"));
			
			// creates the new configuration
			switch(type) {
				case(ConfigurationTypes.ENVIRONMENT_CREATION): {
					// the environment to be read
					Environment environment = null;
					
					// tries to obtain the environment from the tag content
					Environment[] read_environment = EnvironmentTranslator.getEnvironments(configuration_element);
					
					// if there's an environment, it's ok
					if(read_environment.length > 0) environment = read_environment[0];
				    // if not, obtains it from the eventual path held in the parameter attribute
					else {
						String path = configuration_element.getAttribute("parameter");
						environment = EnvironmentTranslator.getEnvironment(path);						
					}
					
					// new environment creation configuration
					configurations.add(new EnvironmentCreationConfiguration(sender_address, sender_socket, environment));
					
					break;
				}
				case(ConfigurationTypes.SIMULATION_CONFIGURATION): {
					// obtains the parameter (actually the time of simulation)
					int simulation_time = Integer.parseInt(configuration_element.getAttribute("parameter"));
					
					// new simulation configuration
					configurations.add(new SimulationConfiguration(sender_address, sender_socket, simulation_time));
					
					break;
				}
			}
		}
		
		// mounts and returns the answer
		Configuration[] answer = new Configuration[configurations.size()];
		for(int i = 0; i < answer.length; i++)
			answer[i] = configurations.remove(i);
		return answer;		
	}
	
	/** Obtains the configurations to add new agents from the given XML element.
	 *  @param xml_element The XML source containing the configurations.
	 *  @param graph The graph obtained from a previous environment configuration.
	 *  @return The configurations from the XML source. */
	public static AgentCreationConfiguration[] getAgentCreationConfigurations(Element xml_element, Graph graph) {
		// obtains the nodes with the "configuration" tag
		NodeList configuration_node = xml_element.getElementsByTagName("configuration");
		
		// Holds the answer for the method
		List<AgentCreationConfiguration> configurations = new LinkedList<AgentCreationConfiguration>();
				
		// for each configuration_node
		for(int i = 0; i < configurations.size(); i++) {
			// obtains the current configuration element
			Element configuration_element = (Element) configuration_node.item(i);
			
			// obtains its data
			String sender_address = configuration_element.getAttribute("sender_address");
			int sender_socket = Integer.parseInt(configuration_element.getAttribute("sender_socket"));
			int type = Integer.parseInt(configuration_element.getAttribute("type"));
			
			// creates the new configuration
			if(type == ConfigurationTypes.AGENT_CREATION) {
				// obtains the current parameter (actually a society id)
				String society_id = configuration_element.getAttribute("parameter");
				
				// obtains the agent from the tag
				Agent agent = EnvironmentTranslator.getAgents(configuration_element, false, graph)[0];
				
				// new agent creation configuration
				configurations.add(new AgentCreationConfiguration(sender_address, sender_socket, agent, society_id));
			}
		}
		
		// mounts and returns the answer
		AgentCreationConfiguration[] answer = new AgentCreationConfiguration[configurations.size()];
		for(int i = 0; i < answer.length; i++)
			answer[i] = configurations.remove(i);
		return answer;
	}
	
	/** Obtains the orientations from the given XML element.
	 *  @param xml_element The XML source containing the orientations.
	 *  @return The orientations from the XML source. */
	public static Orientation[] getOrientations(Element xml_element) {
		// obtains the nodes with the "orientation" tag
		NodeList orientation_node = xml_element.getElementsByTagName("orientation");
		
		// the answer to the method
		Orientation[] answer = new Orientation[orientation_node.getLength()];
		
		// for each orientation_node
		for(int i = 0; i < answer.length; i++) {
			// obtains the current orientation element
			Element orientation_element = (Element) orientation_node.item(i);
			
			// obtains its items
			IntAndString[] items = getOrientationItems(orientation_element);			
			
			// creates the new orientation
			Orientation orientation = new Orientation();
			
			// adds its items
			for(int j = 0; j < items.length; i++)
				orientation.addItem(items[i].int_value, items[j].string);
			
			// adds the orientation to the answer
			answer[i] = orientation;
		}
		
		// returns the answer
		return answer;
	}
	
	/** Obtains the items of a specific orientation, given its XML source.
	 *  @param orientation_element The XML source containing the orientation whose items are to be read.
	 *  @return The items of the orientation. */
	private static IntAndString[] getOrientationItems(Element orientation_element) {		
		// obtains the nodes with the "ort_item" tag
		NodeList ort_item_nodes = orientation_element.getElementsByTagName("ort_item");

		// the answer for the method
		IntAndString[] answer = new IntAndString[ort_item_nodes.getLength()];

		// for each ocurrence
		for (int i = 0; i < answer.length; i++) {
			// obtains the current orientation item
			Element ort_item_element = (Element) ort_item_nodes.item(i);

			// obtains the data
			String agent_id = ort_item_element.getAttribute("agent_id");
			int socket = Integer.parseInt(ort_item_element.getAttribute("socket"));

			// adds the current item to the answer
			answer[i] = new IntAndString(socket, agent_id);
		}

		// returns the answer
		return answer;
	}
}

/** Internal class that holds together an integer value
 *  and a string.  */
final class IntAndString {
	/** The integer value. */
	public final int int_value;
	
	/** The string. */
	public final String string;
	
	/** Constructor.
	 *  @param int_value The integer value.
	 *  @param string The string. */
	public IntAndString(int int_value, String string) {
		this.int_value = int_value;
		this.string = string;
	}
}