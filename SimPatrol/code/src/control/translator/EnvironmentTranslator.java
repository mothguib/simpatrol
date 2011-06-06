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
import control.exception.NodeNotFoundException;
import model.Environment;
import model.agent.OpenSociety;
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
	 * @throws NodeNotFoundException
	 * @throws EdgeNotFoundException
	 */
	public static Environment getEnvironment(String xml_file_path)
			throws ParserConfigurationException, SAXException, IOException,
			NodeNotFoundException, EdgeNotFoundException {
		// parses the file containing the environment
		Element environment_element = parseFile(xml_file_path);

		// obtains the graph of the environment
		Graph graph = GraphTranslator.getGraphs(environment_element)[0];

		// obtains the societies of the environment
		Society[] societies = SocietyTranslator.getSocieties(
				environment_element, graph);

		// detects if there is a declared inactive society
		OpenSociety inactive_society = null;
		for(Society soc: societies){
			if(soc instanceof OpenSociety && soc.getObjectId().equals("InactiveSociety"))
				inactive_society = (OpenSociety)soc;
		}
		
		// if so, we put it apart
		if(inactive_society != null){
			Society[] societies2 = new Society[societies.length - 1];
			int i = 0;
			int j = 0;
			while( i < societies.length){
				if(!(societies[i].getObjectId().equals("InactiveSociety"))){
					societies2[j] = societies[i];
					j++;
				}
				i++;
			}
			
			return new Environment(graph, societies2, inactive_society);
		}
		
		
		// returns the answer
		return new Environment(graph, societies);
	}

	/**
	 * Obtains the environments from the given XML element.
	 * 
	 * @param xml_element
	 *            The XML source containing the environments.
	 * @return The environments from the XML source.
	 * @throws NodeNotFoundException
	 * @throws EdgeNotFoundException
	 */
	public static Environment[] getEnvironments(Element xml_element)
			throws NodeNotFoundException, EdgeNotFoundException {
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

			// detects if there is a declared inactive society
			OpenSociety inactive_society = null;
			for(Society soc: societies){
				if(soc instanceof OpenSociety && soc.getObjectId().equals("InactiveSociety"))
					inactive_society = (OpenSociety)soc;
			}
			// if so, we put it apart
			if(inactive_society != null){
				Society[] societies2 = new Society[societies.length - 1];
				int k = 0;
				int j = 0;
				while( k < societies.length){
					if(!(societies[k].getObjectId().equals("InactiveSociety"))){
						societies2[j] = societies[k];
						j++;
					}
					k++;
				}
				
				answer[i] =  new Environment(graph, societies2, inactive_society);
			}
			else {
				// adds the new environment to the answer
				answer[i] = new Environment(graph, societies);
			}
		}

		// returns the answer
		return answer;
	}
}