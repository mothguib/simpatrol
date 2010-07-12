/* ConfigurationTranslator.java */

/* The package of this class. */
package control.translator;

/* Imported classes and/or interfaces. */
import java.io.IOException;
import javax.xml.parsers.ParserConfigurationException;
import model.Environment;
import model.agent.Agent;
import model.graph.Graph;
import model.metric.Metric;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;
import control.configuration.AgentCreationConfiguration;
import control.configuration.AgentDeathConfiguration;
import control.configuration.Configuration;
import control.configuration.ConfigurationTypes;
import control.configuration.EnvironmentCreationConfiguration;
import control.configuration.EventsCollectingConfiguration;
import control.configuration.MetricCreationConfiguration;
import control.configuration.Orientation;
import control.configuration.SimulationStartConfiguration;
import control.exception.EdgeNotFoundException;
import control.exception.EnvironmentNotValidException;
import control.exception.NodeNotFoundException;

/**
 * Implements a translator that obtains configurations and orientations from a
 * given XML source.
 * 
 * @see Configuration
 * @see Orientation
 * @developer New configurations must change this class.
 */
public abstract class ConfigurationTranslator extends Translator {
	/* Methods. */
	/**
	 * Obtains the configuration from the XML string, except for the "agent
	 * creation" configuration.
	 * 
	 * To obtain an "agent creation" configuration, use
	 * getAgentCreationConfiguration(String xml_string, Graph graph).
	 * 
	 * @see AgentCreationConfiguration
	 * 
	 * @param xml_string
	 *            The XML source containing the configuration.
	 * @return The configuration from the XML source.
	 * @throws IOException
	 * @throws SAXException
	 * @throws ParserConfigurationException
	 * @throws EnvironmentNotValidException
	 * @throws NodeNotFoundException
	 * @throws EdgeNotFoundException 
	 * @developer New configurations must change this method.
	 */
	public static Configuration getConfiguration(String xml_string)
			throws SAXException, IOException, ParserConfigurationException,
			EnvironmentNotValidException, NodeNotFoundException, EdgeNotFoundException {
		// parses the string in order to obtain the "configuration" element
		Element configuration_element = parseString(xml_string);

		// obtains its data
		int type = Integer.parseInt(configuration_element.getAttribute("type"));

		// creates the new configuration
		// developer: new configuration must change this code
		switch (type) {
		case (ConfigurationTypes.ENVIRONMENT_CREATION): {
			// the environment to be read
			Environment environment = null;

			// tries to obtain the environment from the tag content
			Environment[] read_environment = EnvironmentTranslator
					.getEnvironments(configuration_element);

			// if there's an environment, it's ok
			if (read_environment.length > 0)
				environment = read_environment[0];
			// if not, obtains it from the eventual path held at the "parameter"
			// attribute
			else {
				String path = configuration_element.getAttribute("parameter");
				environment = EnvironmentTranslator.getEnvironment(path);
			}

			// if no valid environment was sent, throws exception
			if (environment == null)
				throw new EnvironmentNotValidException();

			// return the new configuration as the answer of the method
			return new EnvironmentCreationConfiguration(environment);
		}
		case (ConfigurationTypes.METRIC_CREATION): {
			// obtains the "parameter" attribute
			// (actually the duration of a cycle of measurement of the metric)
			double cycle_duration = 1;
			String str_cycle_duration = configuration_element
					.getAttribute("parameter");
			if (str_cycle_duration.length() > 0)
				cycle_duration = Double.parseDouble(str_cycle_duration);

			// obtains a metric from the tag content
			Metric[] read_metric = MetricTranslator
					.getMetrics(configuration_element);

			// returns the new configuration as the answer of the method
			return new MetricCreationConfiguration(read_metric[0],
					cycle_duration);
		}
		case (ConfigurationTypes.SIMULATION_START): {
			// obtains the "parameter" attribute
			// (actually the time of simulation)
			double simulation_time = Double.parseDouble(configuration_element
					.getAttribute("parameter"));

			// returns the new configuration as the answer of the method
			return new SimulationStartConfiguration(simulation_time);
		}
		case (ConfigurationTypes.AGENT_DEATH): {
			// obtains the "parameter" attribute
			// (actually the id of the agent to be killed)
			String agent_id = configuration_element.getAttribute("parameter");

			// returns the new configuration as the answer of the method
			return new AgentDeathConfiguration(agent_id);
		}
		case (ConfigurationTypes.EVENT_COLLECTING): {
			// returns the new configuration as the answer of the method
			return new EventsCollectingConfiguration();
		}
		}

		// default answer
		return null;
	}

	/**
	 * Obtains the configuration to add a new agent from the given XML string.
	 * 
	 * @param xml_string
	 *            The XML source containing the configuration.
	 * @param graph
	 *            The graph obtained from a previous "graph creation"
	 *            configuration.
	 * @return The configuration from the XML source.
	 * @throws IOException
	 * @throws SAXException
	 * @throws EdgeNotFoundException
	 * @throws NodeNotFoundException
	 */
	public static AgentCreationConfiguration getAgentCreationConfiguration(
			String xml_string, Graph graph) throws SAXException, IOException,
			NodeNotFoundException, EdgeNotFoundException {
		// parses the string in order to obtain the "configuration" element
		Element configuration_element = parseString(xml_string);

		// obtains the data
		int type = Integer.parseInt(configuration_element.getAttribute("type"));

		// creates the new configuration
		if (type == ConfigurationTypes.AGENT_CREATION) {
			// obtains the current "parameter" attribute
			// (actually a society id)
			String society_id = configuration_element.getAttribute("parameter");

			// obtains the agent from the tag
			// (always obtains it as a seasonal agent)
			Agent agent = AgentTranslator.getAgents(configuration_element,
					false, graph)[0];

			// return the new agent creation configuration
			return new AgentCreationConfiguration(agent, society_id);
		}

		// default answer
		return null;
	}

	/**
	 * Obtains the orientation from the given XML string.
	 * 
	 * @param xml_string
	 *            The XML source containing the orientation.
	 * @return The orientation from the XML source.
	 * @throws IOException
	 * @throws SAXException
	 */
	public static Orientation getOrientation(String xml_string)
			throws SAXException, IOException {
		// parses the given string, in order to obtain the orientation element
		Element orientation_element = parseString(xml_string);

		// obtains its message
		String message = orientation_element.getAttribute("message");

		// obtains its items
		IntAndString[] items = getOrientationItems(orientation_element);

		// creates the new orientation
		Orientation orientation = null;
		if (message.length() > 0)
			orientation = new Orientation(message);
		else
			orientation = new Orientation();

		// adds its items
		for (int j = 0; j < items.length; j++)
			orientation.addItem(items[j].INT_VALUE, items[j].STRING);

		// returns the new orientation
		return orientation;
	}

	/**
	 * Obtains the items of a specific orientation, given its XML source.
	 * 
	 * @param orientation_element
	 *            The XML source containing the orientation whose items are to
	 *            be read.
	 * @return The items of the orientation.
	 */
	private static IntAndString[] getOrientationItems(
			Element orientation_element) {
		// obtains the nodes with the "ort_item" tag
		NodeList ort_item_nodes = orientation_element
				.getElementsByTagName("ort_item");

		// the answer for the method
		IntAndString[] answer = new IntAndString[ort_item_nodes.getLength()];

		// for each occurrence
		for (int i = 0; i < answer.length; i++) {
			// obtains the current orientation item
			Element ort_item_element = (Element) ort_item_nodes.item(i);

			// obtains its data
			String agent_id = ort_item_element.getAttribute("agent_id");
			int socket = Integer.parseInt(ort_item_element
					.getAttribute("socket"));

			// adds the current item to the answer
			answer[i] = new IntAndString(socket, agent_id);
		}

		// returns the answer
		return answer;
	}
}

/**
 * Internal class that holds together an integer value and a string.
 */
final class IntAndString {
	/** The integer value. */
	public final int INT_VALUE;

	/** The string. */
	public final String STRING;

	/**
	 * Constructor.
	 * 
	 * @param int_value
	 *            The integer value.
	 * @param string
	 *            The string.
	 */
	public IntAndString(int int_value, String string) {
		this.INT_VALUE = int_value;
		this.STRING = string;
	}
}