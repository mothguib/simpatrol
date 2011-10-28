package util.graph2;

import java.io.IOException;
import java.io.StringReader;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import com.sun.org.apache.xerces.internal.parsers.DOMParser;


/**
 * Implements a translator that convert from XML to an object of type
 * "util.graph2.Graph".
 */
public abstract class GraphTranslator {

	/**
	 * Parses a given XML string.
	 */
	private static Element parseString(String xmlString) throws SAXException, IOException {
		InputSource is = new InputSource(new StringReader(xmlString));

		DOMParser parser = new DOMParser();
		parser.parse(is);

		Document doc = parser.getDocument();
		return doc.getDocumentElement();
	}

	/**
	 * Obtains the graphs from the given XML element.
	 */
	public static Graph[] getGraphs(String message) throws SAXException, IOException {
		Element xmlElement = parseString(message);
		NodeList graphNodes = xmlElement.getElementsByTagName("graph");

		Graph[] answer = new Graph[graphNodes.getLength()];

		// for each xml node of type "graph"
		for (int i = 0; i < answer.length; i++) {
			Element graphElement = (Element) graphNodes.item(i);

			answer[i] = createGraph(graphElement);

			addEdges(answer[i], graphElement);
		}

		return answer;
	}


	/**
	 * Creates the Graph object with information of all nodes taken from 
	 * the XML message. No edges are created.
	 */
	private static Graph createGraph(Element xmlGraphElement) {
		String graphLabel = xmlGraphElement.getAttribute("label");
		NodeList graphNodes = xmlGraphElement.getElementsByTagName("node");
		
		Graph answer = new Graph(graphLabel, graphNodes.getLength(), Representation.LISTS);

		// for xml node of type "node"
		for (int i = 0; i < graphNodes.getLength(); i++) {

			Element nodeElement = (Element) graphNodes.item(i);

			//String label     = nodeElement.getAttribute("label");
			String id          = nodeElement.getAttribute("id");
			String strPriority = nodeElement.getAttribute("priority");
			String strIdleness = nodeElement.getAttribute("idleness");
			String strFuel     = nodeElement.getAttribute("fuel");

			Node node = answer.addNode(id);

			if (strPriority.length() > 0) {
				node.setPriority(Double.parseDouble(strPriority));
			}
			
			if (strIdleness.length() > 0) {
				node.setIdleness(Double.parseDouble(strIdleness));
			}
			
			if (strFuel.length() > 0) {
				node.setFuel(Boolean.parseBoolean(strFuel));
			}
		}

		return answer;
	}

	/**
	 * Reads the edges' information from the XML message and adds them to the graph. 
	 */
	private static void addEdges(Graph graph, Element xmlGraphElement) {
		NodeList edgeNodes = xmlGraphElement.getElementsByTagName("edge");

		for (int i = 0; i < edgeNodes.getLength(); i++) {
			Element edgeElement = (Element) edgeNodes.item(i);

			String id          = edgeElement.getAttribute("id");
			String sourceId    = edgeElement.getAttribute("source");
			String targetId    = edgeElement.getAttribute("target");
			
			boolean isDirected = Boolean.parseBoolean(edgeElement.getAttribute("directed")); 
			double length      = Double.parseDouble(edgeElement.getAttribute("length"));

			// finds the correspondent source and target nodes
			Node source = graph.getNode(sourceId);
			Node target = graph.getNode(targetId);

			// adds the edge
			graph.addEdge(id, source, target, length);
			if (! isDirected) {
				graph.addEdge(id, target, source, length);	
			}
		}

	}

	
	// TODO: criar aqui uma conversão PARA xml (ao invés do grafo)
	
}
