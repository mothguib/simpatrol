/* GraphTranslator.java */

/* The package of this class. */
package util;

/* Imported classes and/or interfaces. */
import java.io.File;
import java.io.IOException;
import java.util.LinkedList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;
import control.exception.EdgeNotFoundException;
import control.exception.VertexNotFoundException;
import control.translator.EventTimeProbabilityDistributionTranslator;
import control.translator.StigmaTranslator;
import model.etpd.EventTimeProbabilityDistribution;
import model.graph.DynamicEdge;
import model.graph.DynamicVertex;
import model.graph.Edge;
import model.graph.Graph;
import model.graph.Vertex;
import model.stigma.Stigma;

/**
 * Implements a translator that obtains Graph objects from file source elements.
 * 
 * @see Graph
 */
public abstract class GraphTranslator {
	/* Methods. */
	public static Graph getGraphFromMachadoModel(String file_path)
			throws IOException {
		FileReader file_reader = new FileReader(file_path);

		LinkedList<String> vertexes_codes_ids = new LinkedList<String>();
		LinkedList<Vertex> vertexes = new LinkedList<Vertex>();
		vertexes.add(new Vertex("null"));

		int ref_value = file_reader.readInt();
		for (int i = 0; i < ref_value + 1; i++)
			file_reader.readLine();

		int vertexes_count = 0;
		int edges_count = file_reader.readInt();
		for (int i = 0; i < edges_count; i++) {
			int x1 = file_reader.readInt();
			int y1 = file_reader.readInt();

			int x2 = file_reader.readInt();
			int y2 = file_reader.readInt();

			String vertex_code_1 = String.valueOf(x1) + String.valueOf(y1);
			String vertex_code_2 = String.valueOf(x2) + String.valueOf(y2);

			int vertex_id_1 = 0;
			int vertex_id_2 = 0;

			int pos = vertexes_codes_ids.indexOf(vertex_code_1);
			if (pos > -1 && pos % 2 != 0)
				pos = vertexes_codes_ids.subList(pos + 1,
						vertexes_codes_ids.size()).indexOf(vertex_code_1);

			if (pos > -1)
				vertex_id_1 = Integer.parseInt(vertexes_codes_ids.get(pos + 1));
			else {
				vertexes_count++;
				vertex_id_1 = vertexes_count;

				vertexes_codes_ids.add(vertex_code_1);
				vertexes_codes_ids.add(String.valueOf(vertex_id_1));

				Vertex current_vertex = new Vertex("v"
						+ String.valueOf(vertex_id_1));
				current_vertex.setObjectId("v" + String.valueOf(vertex_id_1));
				vertexes.add(current_vertex);
			}

			pos = vertexes_codes_ids.indexOf(vertex_code_2);
			if (pos > -1 && pos % 2 != 0)
				pos = vertexes_codes_ids.subList(pos + 1,
						vertexes_codes_ids.size()).indexOf(vertex_code_2);

			if (pos > -1)
				vertex_id_2 = Integer.parseInt(vertexes_codes_ids.get(pos + 1));
			else {
				vertexes_count++;
				vertex_id_2 = vertexes_count;

				vertexes_codes_ids.add(vertex_code_2);
				vertexes_codes_ids.add(String.valueOf(vertex_id_2));

				Vertex current_vertex = new Vertex("v"
						+ String.valueOf(vertex_id_2));
				current_vertex.setObjectId("v" + String.valueOf(vertex_id_2));
				vertexes.add(current_vertex);
			}

			Vertex vertex_1 = vertexes.get(vertex_id_1);
			Vertex vertex_2 = vertexes.get(vertex_id_2);

			double edge_length = Math.sqrt(Math.pow(x1 - x2, 2)
					+ Math.pow(y1 - y2, 2));
			Edge current_edge = new Edge(vertex_1, vertex_2, edge_length);
			current_edge.setObjectId("e" + String.valueOf(i + 1));
		}

		Vertex[] graph_vertexes = new Vertex[vertexes.size() - 1];
		for (int i = 0; i < graph_vertexes.length; i++)
			graph_vertexes[i] = vertexes.get(i + 1);

		int label_pos = file_path.lastIndexOf("/");
		if (label_pos == -1)
			label_pos = file_path.lastIndexOf("\\");

		return new Graph(file_path.substring(label_pos + 1), graph_vertexes);
	}

	/**
	 * Obtains graphs from the pointed XML file.
	 * 
	 * @param xml_file_path
	 *            The path to the XML file containing the graphs.
	 * @return The graphs from the XML file.
	 * @throws IOException
	 * @throws SAXException
	 * @throws ParserConfigurationException
	 * @throws ParserConfigurationException
	 * @throws EdgeNotFoundException
	 * @throws VertexNotFoundException
	 */
	public static Graph[] getGraphFromSimPatrolXML(String xml_file_path)
			throws ParserConfigurationException, SAXException, IOException,
			VertexNotFoundException, EdgeNotFoundException {
		// parses the file containing the environment
		Element graph_element = parseFile(xml_file_path);

		// returns the graph
		return getGraphs(graph_element);
	}

	/**
	 * Obtains the graphs from the given XML element.
	 * 
	 * @param xml_element
	 *            The XML source containing the graphs.
	 * @return The graphs from the XML source.
	 * @throws VertexNotFoundException
	 * @throws EdgeNotFoundException
	 */
	private static Graph[] getGraphs(Element xml_element)
			throws VertexNotFoundException, EdgeNotFoundException {
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
			Vertex[] vertexes = getVertexes(graph_element);

			// obtains the edges
			getEdges(graph_element, vertexes);

			// obtains the new graph
			Graph graph = new Graph(label, vertexes);

			// obtains the stigmas
			Stigma[] stigmas = StigmaTranslator
					.getStigmas(graph_element, graph);

			// adds the obtained stigmas to the graph
			for (int j = 0; j < stigmas.length; j++)
				graph.addStigma(stigmas[j]);

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
	private static Vertex[] getVertexes(Element xml_element) {
		// obtains the nodes with the "vertex" tag
		NodeList vertex_nodes = xml_element.getElementsByTagName("vertex");

		// the answer to the method
		Vertex[] answer = new Vertex[vertex_nodes.getLength()];

		// for each occurrence
		for (int i = 0; i < answer.length; i++) {
			// obtains the current vertex element
			Element vertex_element = (Element) vertex_nodes.item(i);

			// obtains its data
			String id = vertex_element.getAttribute("id");
			String label = vertex_element.getAttribute("label");
			String str_priority = vertex_element.getAttribute("priority");
			String str_visibility = vertex_element.getAttribute("visibility");
			String str_idleness = vertex_element.getAttribute("idleness");
			String str_fuel = vertex_element.getAttribute("fuel");
			String str_is_enabled = vertex_element.getAttribute("is_enabled");

			// obtains the eventual time probability distributions
			EventTimeProbabilityDistribution[] etpds = EventTimeProbabilityDistributionTranslator
					.getEventTimeProbabilityDistribution(vertex_element);

			// instantiates the new vertex (normal or dynamic)
			Vertex current_vertex = null;
			if (etpds.length == 0) {
				// new normal vertex
				current_vertex = new Vertex(label);
			} else {
				// verifies if the vertex is enabled
				boolean is_enabled = true;
				if (str_is_enabled.length() > 0)
					is_enabled = Boolean.parseBoolean(str_is_enabled);

				// new dynamic vertex
				current_vertex = new DynamicVertex(label, etpds[0], etpds[1],
						is_enabled);
			}

			// configures the new vertex...
			// id configuration
			current_vertex.setObjectId(id);

			// priority configuration
			int priority = 0;
			if (str_priority.length() > 0)
				priority = Integer.parseInt(str_priority);
			current_vertex.setPriority(priority);

			// visibility configuration
			boolean visibility = true;
			if (str_visibility.length() > 0)
				visibility = Boolean.parseBoolean(str_visibility);
			current_vertex.setVisibility(visibility);

			// idleness configuration
			double idleness = 0;
			if (str_idleness.length() > 0)
				idleness = Double.parseDouble(str_idleness);
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
	 * @throws VertexNotFoundException
	 */
	private static Edge[] getEdges(Element xml_element, Vertex[] vertexes)
			throws VertexNotFoundException {
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
			String emitter_id = edge_element.getAttribute("emitter_id");
			String collector_id = edge_element.getAttribute("collector_id");
			String str_oriented = edge_element.getAttribute("oriented");
			double length = Double.parseDouble(edge_element
					.getAttribute("length"));
			String str_visibility = edge_element.getAttribute("visibility");
			String str_is_enabled = edge_element.getAttribute("is_enabled");
			String str_is_in_dynamic_emitter_memory = edge_element
					.getAttribute("is_in_dynamic_emitter_memory");
			String str_is_in_dynamic_collector_memory = edge_element
					.getAttribute("is_in_dynamic_collector_memory");

			// obtains the eventual time probability distributions
			EventTimeProbabilityDistribution[] etpds = EventTimeProbabilityDistributionTranslator
					.getEventTimeProbabilityDistribution(edge_element);

			// finds the correspondent emitter and collector vertexes
			Vertex emitter = null;
			Vertex collector = null;

			for (int j = 0; j < vertexes.length; j++) {
				Vertex current_vertex = vertexes[j];

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

			// if no emitter neither collector are valid, throw exception
			if (emitter == null || collector == null)
				throw new VertexNotFoundException();

			// decides if the edge is oriented
			boolean oriented = false;
			if (str_oriented.length() > 0)
				oriented = Boolean.parseBoolean(str_oriented);

			// instantiates the new edge (normal or dynamic)
			Edge current_edge = null;
			if (etpds.length == 0) {
				// new normal edge
				current_edge = new Edge(emitter, collector, oriented, length);
			} else {
				// decides if the edge is enabled
				boolean is_enabled = true;
				if (str_is_enabled.length() > 0)
					is_enabled = Boolean.parseBoolean(str_is_enabled);

				// new dynamic edge
				current_edge = new DynamicEdge(emitter, collector, oriented,
						length, etpds[0], etpds[1], is_enabled);
			}

			// configures the new edge...
			// id configuration
			current_edge.setObjectId(id);

			// visibility configuration
			boolean visibility = true;
			if (str_visibility.length() > 0)
				visibility = Boolean.parseBoolean(str_visibility);
			current_edge.setVisibility(visibility);

			// decides if the edge is in the emitter and collector enabling
			// memories
			boolean is_in_dynamic_emitter_memory = false;
			boolean is_in_dynamic_collector_memory = false;
			if (str_is_in_dynamic_emitter_memory.length() > 0)
				is_in_dynamic_emitter_memory = Boolean
						.parseBoolean(str_is_in_dynamic_emitter_memory);
			if (str_is_in_dynamic_collector_memory.length() > 0)
				is_in_dynamic_collector_memory = Boolean
						.parseBoolean(str_is_in_dynamic_collector_memory);

			// if the emitter is a dynamic vertex and
			// the current edge is in its memory of enabled edges
			if (emitter instanceof DynamicVertex
					&& is_in_dynamic_emitter_memory)
				((DynamicVertex) emitter).addEnabledEdge(current_edge);

			// if the collector is a dynamic vertex and
			// the current edge is in its memory of enabled edges
			if (collector instanceof DynamicVertex
					&& is_in_dynamic_collector_memory)
				((DynamicVertex) collector).addEnabledEdge(current_edge);

			// adds the new edge to the answer
			answer[i] = current_edge;
		}

		// returns the answer
		return answer;
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
	private static Element parseFile(String xml_file_path)
			throws ParserConfigurationException, SAXException, IOException {
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		DocumentBuilder db = dbf.newDocumentBuilder();
		Document doc = db.parse(new File(xml_file_path));
		Element e = doc.getElementById("a");

		return doc.getDocumentElement();
	}
}