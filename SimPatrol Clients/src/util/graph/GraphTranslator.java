/* GraphTranslator.java */

/* The package of this class. */
package util.graph;

/* Imported classes and/or interfaces. */
import java.io.IOException;
import java.io.StringReader;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import com.sun.org.apache.xerces.internal.parsers.DOMParser;

/**
 * Implements a translator that obtain Graph Java objects from XML files.
 * 
 * @see Graph
 */
public abstract class GraphTranslator {
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
	 * Obtains the graphs from the given XML element.
	 * 
	 * @param xml_element
	 *            The XML source containing the graphs.
	 * @return The graphs from the XML source.
	 */
	public static Graph[] getGraphs(Element xml_element) {
		// obtains the nodes with the "graph" tag
		NodeList graph_node = xml_element.getElementsByTagName("graph");

		// the answer to the method
		Graph[] answer = new Graph[graph_node.getLength()];

		// for each graph_node
		for (int i = 0; i < answer.length; i++) {
			// obtains the current graph element
			Element graph_element = (Element) graph_node.item(i);

			// obtains the data
			String label = graph_element.getAttribute("label");

			// obtains the nodes
			Node[] nodes = getNodees(graph_element);

			// obtains the edges
			getEdges(graph_element, nodes);

			// obtains the new graph
			Graph graph = new Graph(label, nodes);

			// adds the new graph to the answer
			answer[i] = graph;
		}

		// returns the answer
		return answer;
	}

	/**
	 * Obtains the nodes from the given XML element.
	 * 
	 * @param xml_element
	 *            The XML source containing the nodes.
	 * @return The nodes from the XML source.
	 */
	private static Node[] getNodees(Element xml_element) {
		// obtains the nodes with the "node" tag
		NodeList node_nodes = xml_element.getElementsByTagName("node");

		// the answer to the method
		Node[] answer = new Node[node_nodes.getLength()];

		// for each occurrence
		for (int i = 0; i < answer.length; i++) {
			// obtains the current node element
			Element node_element = (Element) node_nodes.item(i);

			// obtains its data
			String id = node_element.getAttribute("id");
			String label = node_element.getAttribute("label");
			String str_priority = node_element.getAttribute("priority");
			String str_idleness = node_element.getAttribute("idleness");
			String str_fuel = node_element.getAttribute("fuel");

			// instantiates the new node
			Node current_node = new Node(label);

			// configures the new node...
			// id configuration
			current_node.setObjectId(id);

			// priority configuration
			int priority = 0;
			if (str_priority.length() > 0)
				priority = Integer.parseInt(str_priority);
			current_node.setPriority(priority);

			// idleness configuration
			double idleness = 0;
			if (str_idleness.length() > 0)
				idleness = Double.parseDouble(str_idleness);
			current_node.setIdleness(idleness);

			// fuel configuration
			boolean fuel = false;
			if (str_fuel.length() > 0)
				fuel = Boolean.parseBoolean(str_fuel);
			current_node.setFuel(fuel);

			// adds the new node to the answer
			answer[i] = current_node;
		}

		// returns the answer
		return answer;
	}

	/**
	 * Obtains the edges from the given XML element.
	 * 
	 * @param xml_element
	 *            The XML source containing the edges.
	 * @param nodes
	 *            The set of nodes read from the XML source.
	 * @return The edges from the XML source.
	 */
	private static Edge[] getEdges(Element xml_element, Node[] nodes) {
		// obtains the nodes with the "edge" tag
		NodeList edge_nodes = xml_element.getElementsByTagName("edge");

		// the answer to the method
		Edge[] answer = new Edge[edge_nodes.getLength()];

		// for each occurrence
		for (int i = 0; i < answer.length; i++) {
			// obtains the current edge element
			Element edge_element = (Element) edge_nodes.item(i);

			// obtains its data
			String id = edge_element.getAttribute("id");
			String source_id = edge_element.getAttribute("source_id");
			String target_id = edge_element.getAttribute("target_id");
			String str_directed = edge_element.getAttribute("directed");
			double length = Double.parseDouble(edge_element
					.getAttribute("length"));

			// finds the correspondent source and target nodes
			Node source = null;
			Node target = null;

			for (int j = 0; j < nodes.length; j++) {
				Node current_node = nodes[j];

				if (current_node.getObjectId().equals(source_id)) {
					source = current_node;
					if (target != null)
						break;
				}

				if (current_node.getObjectId().equals(target_id)) {
					target = current_node;
					if (source != null)
						break;
				}
			}

			// decides if the edge is directed
			boolean directed = false;
			if (str_directed.length() > 0)
				directed = Boolean.parseBoolean(str_directed);

			// instantiates the new edge (normal or dynamic)
			Edge current_edge = new Edge(source, target, directed, length);

			// configures the new edge...
			// id configuration
			current_edge.setObjectId(id);

			// adds the new edge to the answer
			answer[i] = current_edge;
		}

		// returns the answer
		return answer;
	}
}