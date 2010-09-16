/* GraphTranslator.java */

/* The package of this class. */
package control.translator;

/* Imported classes and/or interfaces. */
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import control.exception.EdgeNotFoundException;
import control.exception.VertexNotFoundException;
import model.etpd.EventTimeProbabilityDistribution;
import model.graph.DynamicEdge;
import model.graph.DynamicVertex;
import model.graph.Edge;
import model.graph.Graph;
import model.graph.Vertex;
import model.stigma.Stigma;

/**
 * Implements a translator that obtains Graph objects from XML source elements.
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
	 * @throws VertexNotFoundException
	 * @throws EdgeNotFoundException
	 */
	public static Graph[] getGraphs(Element xml_element)
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
}