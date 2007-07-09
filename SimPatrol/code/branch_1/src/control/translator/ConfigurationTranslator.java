/* ConfigurationTranslator.java */

/* The package of this class. */
package control.translator;

/* Imported classes and/or interfaces. */
import java.io.IOException;
import javax.xml.parsers.ParserConfigurationException;
import model.agent.Agent;
import model.agent.Society;
import model.graph.Graph;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;
import control.configuration.AgentCreationConfiguration;
import control.configuration.Configuration;
import control.configuration.ConfigurationTypes;
import control.configuration.GraphCreationConfiguration;
import control.configuration.Orientation;
import control.configuration.SimulationStartConfiguration;
import control.configuration.SocietiesCreationConfiguration;

/** Implements a translator that obtains configurations and orientations
 *  from a given XML source.
 *  
 *  @see Configuration
 *  @see Orientation
 *  @developer New configurations must change this class. */
public abstract class ConfigurationTranslator extends Translator {
	/* Methods. */
	/** Obtains the configuration from the XML string, except
	 *  for the "societies creation" and "agent creation" configuration.
	 *  
	 *  To obtain a "societies creation" configuration, use
	 *  getSocietiesCreationConfiguration(String xml_string, Graph graph).
	 *  @see SocietiesCreationConfiguration
	 *  
	 *  To obtain an "agent creation" configuration, use
	 *  getAgentCreationConfiguration(String xml_string, Graph graph).
	 *  @see AgentCreationConfiguration
	 *  
	 *  @param xml_string The XML source containing the configuration.
	 *  @return The configuration from the XML source. 
	 *  @throws IOException 
	 *  @throws SAXException 
	 *  @throws ParserConfigurationException 
	 *  @developer New configurations must change this method. */
	public static Configuration getConfiguration(String xml_string) throws ParserConfigurationException, SAXException, IOException {
		// parses the string in order to obtain the "configuration" element
		Element configuration_element = parseString(xml_string);
		
		// obtains its data
		String sender_address = configuration_element.getAttribute("sender_address");
		int sender_socket = Integer.parseInt(configuration_element.getAttribute("sender_socket"));
		int type = Integer.parseInt(configuration_element.getAttribute("type"));
		
		// creates the new configuration
		// developer: new configuration must change this code 
		switch(type) {
			case(ConfigurationTypes.GRAPH_CREATION): {
				// the graph to be read
				Graph graph = null;
				
				// tries to obtain the graph from the tag content
				Graph[] read_graph = GraphTranslator.getGraphs(configuration_element);
				
				// if there's a graph, it's ok
				if(read_graph.length > 0) graph = read_graph[0];
				// if not, obtains it from the eventual path held at the "parameter" attribute
				else {
					String path = configuration_element.getAttribute("parameter");
					graph = GraphTranslator.getGraph(path);
				}
				
				// return the new configuration as the answer of the method
				return new GraphCreationConfiguration(sender_address, sender_socket, graph);
			}
			case(ConfigurationTypes.SIMULATION_START): {
				// obtains the "parameter" attribute
				// (actually the time of simulation)
				int simulation_time = Integer.parseInt(configuration_element.getAttribute("parameter"));
				
				// returns the new configuration as the answer of the method
				return new SimulationStartConfiguration(sender_address, sender_socket, simulation_time);
			}
		}
		
		// default answer
		return null;		
	}
	
	/** Obtains the configuration to add new societies from the given XML string.
	 * 
	 *  @param xml_string The XML source containing the configuration.
	 *  @param graph The graph obtained from a previous "graph creation" configuration.
	 *  @return The configuration from the XML source. 
	 *  @throws IOException 
	 *  @throws SAXException 
	 *  @throws ParserConfigurationException */
	public static SocietiesCreationConfiguration getSocietiesCreationConfiguration(String xml_string, Graph graph) throws ParserConfigurationException, SAXException, IOException {
		// parses the string in order to obtain the "configuration" element
		Element configuration_element = parseString(xml_string);
		
		// obtains the data
		String sender_address = configuration_element.getAttribute("sender_address");
		int sender_socket = Integer.parseInt(configuration_element.getAttribute("sender_socket"));
		int type = Integer.parseInt(configuration_element.getAttribute("type"));
		
		// creates the new configuration
		if(type == ConfigurationTypes.SOCIETIES_CREATION) {
			// the societies to be read
			Society[] societies = null;
			
			// tries to obtain the societies from the tag content
			societies = SocietyTranslator.getSocieties(configuration_element, graph);
			
			// if there are no societies, obtains it from the eventual path held at the "parameter" attribute
			if(societies.length == 0) {
				String path = configuration_element.getAttribute("parameter");
				societies = SocietyTranslator.getSocieties(path, graph);
			}
			
			// returns the new configuration as the answer of the method
			return new SocietiesCreationConfiguration(sender_address, sender_socket, societies);
		}
		
		// default answer
		return null;
	}
	
	/** Obtains the configuration to add a new agent from the given XML string.
	 * 
	 *  @param xml_string The XML source containing the configuration.
	 *  @param graph The graph obtained from a previous "graph creation" configuration.
	 *  @return The configuration from the XML source. 
	 *  @throws IOException 
	 *  @throws SAXException 
	 *  @throws ParserConfigurationException */
	public static AgentCreationConfiguration getAgentCreationConfiguration(String xml_string, Graph graph) throws ParserConfigurationException, SAXException, IOException {
		// parses the string in order to obtain the "configuration" element
		Element configuration_element = parseString(xml_string);
		
		// obtains the data
		String sender_address = configuration_element.getAttribute("sender_address");
		int sender_socket = Integer.parseInt(configuration_element.getAttribute("sender_socket"));
		int type = Integer.parseInt(configuration_element.getAttribute("type"));
		
		// creates the new configuration
		if(type == ConfigurationTypes.AGENT_CREATION) {
			// obtains the current "parameter" attribute
			// (actually a society id)
			String society_id = configuration_element.getAttribute("parameter");
			
			// obtains the agent from the tag
			// (always obtains it as a seasonal agent) 
			Agent agent = SocietyTranslator.getAgents(configuration_element, false, graph)[0];
			
			// return the new agent creation configuration
			return new AgentCreationConfiguration(sender_address, sender_socket, agent, society_id);
		}
		
		// defautl answer
		return null;
	}
	
	/** Obtains the orientation from the given XML string.
	 * 
	 *  @param xml_string The XML source containing the orientation.
	 *  @return The orientation from the XML source. 
	 *  @throws IOException 
	 *  @throws SAXException 
	 *  @throws ParserConfigurationException */
	public static Orientation getOrientation(String xml_string) throws ParserConfigurationException, SAXException, IOException {
		// parses the given string, in order to obtain the orientation element
		Element orientation_element = parseString(xml_string);
		
		// obtains its message
		String message = orientation_element.getAttribute("message");
		
		// obtains its items
		IntAndString[] items = getOrientationItems(orientation_element);			
		
		// creates the new orientation
		Orientation orientation = null;
		if(message.length() > 0) orientation = new Orientation(message);
		else orientation = new Orientation();
		
		// adds its items
		for(int j = 0; j < items.length; j++)
			orientation.addItem(items[j].int_value, items[j].string);
		
		// returns the new orientation
		return orientation;
	}
	
	/** Obtains the items of a specific orientation, given its XML source.
	 * 
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

			// obtains its data
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