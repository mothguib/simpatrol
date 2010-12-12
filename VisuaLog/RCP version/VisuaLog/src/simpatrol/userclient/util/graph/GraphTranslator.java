/* GraphTranslator.java */

/* The package of this class. */
package simpatrol.userclient.util.graph;

/* Imported classes and/or interfaces. */
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import simpatrol.userclient.util.Translator;
/**
 * Implements a translator that obtain Graph Java objects from XML files.
 * 
 * @see Graph
 */
public abstract class GraphTranslator extends Translator {
	/* Methods. */


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

			// obtains the vertexes
			Node[] vertexes = getNodes(graph_element);

			// obtains the edges
			getEdges(graph_element, vertexes);

			// obtains the new graph
			Graph graph = new Graph(label, vertexes);

			// adds the new graph to the answer
			answer[i] = graph;
		}

		// returns the answer
		return answer;
	}

	/**
	 * Obtains the vertexes from the given XML element.
	 * 
	 * @param xml_element
	 *            The XML source containing the vertexes.
	 * @return The vertexes from the XML source.
	 */
	private static Node[] getNodes(Element xml_element) {
		// obtains the nodes with the "vertex" tag
		NodeList vertex_nodes = xml_element.getElementsByTagName("node");

		// the answer to the method
		Node[] answer = new Node[vertex_nodes.getLength()];

		// for each ocurrence
		for (int i = 0; i < answer.length; i++) {
			// obtains the current vertex element
			Element vertex_element = (Element) vertex_nodes.item(i);

			// obtains its data
			String id = vertex_element.getAttribute("id");
			String label = vertex_element.getAttribute("label");
			String str_priority = vertex_element.getAttribute("priority");
			String str_idleness = vertex_element.getAttribute("idleness");
			String str_fuel = vertex_element.getAttribute("fuel");

			// instatiates the new vertex
			Node current_vertex = new Node(label);

			// configures the new vertex...
			// id configuration
			current_vertex.setObjectId(id);

			// priority configuration
			double priority = 1.0d;
			if (str_priority.length() > 0)
				priority = Double.parseDouble(str_priority);
			current_vertex.setPriority(priority);

			// idleness configuration
			int idleness = 0;
			if (str_idleness.length() > 0)
				idleness = (int)Double.parseDouble(str_idleness);
			current_vertex.setIdleness(idleness);

			// fuel configuration
			boolean fuel = false;
			if (str_fuel.length() > 0)
				fuel = Boolean.parseBoolean(str_fuel);
			current_vertex.setFuel(fuel);

			// adds the new vertex to the answer
			answer[i] = current_vertex;
		}

		// returns the answer
		return answer;
	}

	/**
	 * Obtains the edges from the given XML element.
	 * 
	 * @param xml_element
	 *            The XML source containing the edges.
	 * @param vertexes
	 *            The set of vertexes read from the XML source.
	 * @return The edges from the XML source.
	 */
	private static Edge[] getEdges(Element xml_element, Node[] vertexes) {
		// obtains the nodes with the "edge" tag
		NodeList edge_nodes = xml_element.getElementsByTagName("edge");

		// the answer to the method
		Edge[] answer = new Edge[edge_nodes.getLength()];

		// for each ocurrence
		for (int i = 0; i < answer.length; i++) {
			// obtains the current edge element
			Element edge_element = (Element) edge_nodes.item(i);

			// obtains its data
			String id = edge_element.getAttribute("id");
			String emitter_id = edge_element.getAttribute("source");
			String collector_id = edge_element.getAttribute("target");
			String str_oriented = edge_element.getAttribute("directed");
			double length = Double.parseDouble(edge_element
					.getAttribute("length"));

			// finds the correspondent emitter and collector vertexes
			Node emitter = null;
			Node collector = null;

			for (int j = 0; j < vertexes.length; j++) {
				Node current_vertex = vertexes[j];

				if (current_vertex.getObjectId().equals(emitter_id)) {
					emitter = current_vertex;
					if (collector != null)
						break;
				}

				if (current_vertex.getObjectId().equals(collector_id)) {
					collector = current_vertex;
					if (emitter != null)
						break;
				}
			}

			// decides if the edge is oriented
			boolean oriented = false;
			if (str_oriented.length() > 0)
				oriented = Boolean.parseBoolean(str_oriented);

			// instantiates the new edge (normal or dynamic)
			Edge current_edge = new Edge(emitter, collector, oriented, length);

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