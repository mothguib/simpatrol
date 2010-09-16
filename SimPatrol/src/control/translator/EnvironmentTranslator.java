/* EnvironmentTranslator.java */

/* The package of this class. */
package control.translator;

/* Imported classes and/or interfaces. */
import java.io.IOException;
import javax.xml.parsers.ParserConfigurationException;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;
import control.exception.EdgeNotFoundException;
import control.exception.VertexNotFoundException;
import model.Environment;
import model.agent.Society;
import model.graph.Graph;

/**
 * Implements a translator that obtains Environment objects from XML source
 * elements.
 * 
 * @see Environment
 */
public abstract class EnvironmentTranslator extends Translator {
	/* Methods. */
	/**
	 * Obtains an environment from the pointed XML file.
	 * 
	 * @param xml_file_path
	 *            The path to the XML file containing the environment.
	 * @return The environment from the XML file.
	 * @throws ParserConfigurationException
	 * @throws SAXException
	 * @throws IOException
	 * @throws VertexNotFoundException
	 * @throws EdgeNotFoundException
	 */
	public static Environment getEnvironment(String xml_file_path)
			throws ParserConfigurationException, SAXException, IOException,
			VertexNotFoundException, EdgeNotFoundException {
		// parses the file containing the environment
		Element environment_element = parseFile(xml_file_path);

		// obtains the graph of the environment
		Graph graph = GraphTranslator.getGraphs(environment_element)[0];

		// obtains the societies of the environment
		Society[] societies = SocietyTranslator.getSocieties(
				environment_element, graph);

		// returns the answer
		return new Environment(graph, societies);
	}

	/**
	 * Obtains the environments from the given XML element.
	 * 
	 * @param xml_element
	 *            The XML source containing the environments.
	 * @return The environments from the XML source.
	 * @throws VertexNotFoundException
	 * @throws EdgeNotFoundException
	 */
	public static Environment[] getEnvironments(Element xml_element)
			throws VertexNotFoundException, EdgeNotFoundException {
		// obtains the nodes with the "environment" tag
		NodeList environment_node = xml_element
				.getElementsByTagName("environment");

		// the answer to the method
		Environment[] answer = new Environment[environment_node.getLength()];

		// for each environment_node
		for (int i = 0; i < answer.length; i++) {
			// obtains the current environment element
			Element environment_element = (Element) environment_node.item(i);

			// obtains the graph of the environment
			Graph graph = GraphTranslator.getGraphs(environment_element)[0];

			// obtains the societies of the environment
			Society[] societies = SocietyTranslator.getSocieties(
					environment_element, graph);

			// adds the new environment to the answer
			answer[i] = new Environment(graph, societies);
		}

		// returns the answer
		return answer;
	}
}