/* EnvironmentTranslator.java */

/* The package of this class. */
package util;

/* Imported classes and/or interfaces. */
import java.io.IOException;
import java.io.StringReader;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import com.sun.org.apache.xerces.internal.parsers.DOMParser;

import util.agents.SocietyImage;
import util.agents.SocietyTranslator;
import util.graph.Graph;
import util.graph.GraphTranslator;

/**
 * Implements a translator that obtains Environment objects from XML source
 * elements.
 * 
 * @see Environment
 */
public abstract class EnvironmentTranslator {
	/* Methods. */
	
	/**
	 * Parses a given XML string.
	 * 
	 * @param xml_string
	 *            The string of the XML source containing the objects to be
	 *            translated.
	 * @throws IOException
	 * @throws SAXException
	 */
	public static Element parseString(String xml_string) throws SAXException,
			IOException {
		InputSource is = new InputSource(new StringReader(xml_string));

		DOMParser parser = new DOMParser();
		parser.parse(is);

		Document doc = parser.getDocument();
		return doc.getDocumentElement();
	}
	
	/**
	 * Parses a given XML file.
	 * 
	 * @param xml_file_path
	 *            The path of the XML file containing the objects to be
	 *            translated.
	 * @throws ParserConfigurationException
	 * @throws IOException
	 * @throws SAXException
	 */
	protected static Element parseFile(String xml_file_path)
			throws ParserConfigurationException, SAXException, IOException {
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		DocumentBuilder db = dbf.newDocumentBuilder();
		Document doc = db.parse(xml_file_path);

		return doc.getDocumentElement();
	}
	
	
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
			throws ParserConfigurationException, SAXException, IOException {
		// parses the file containing the environment
		Element environment_element = parseFile(xml_file_path);

		// obtains the graph of the environment
		Graph graph = GraphTranslator.getGraphs(environment_element)[0];

		// obtains the societies of the environment
		SocietyImage[] societies = SocietyTranslator.getSocieties(environment_element);
		
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
	public static Environment[] getEnvironments(Element xml_element){
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
			SocietyImage[] societies = SocietyTranslator.getSocieties(environment_element);

			
			// adds the new environment to the answer
			answer[i] = new Environment(graph, societies);
		}

		// returns the answer
		return answer;
	}
}