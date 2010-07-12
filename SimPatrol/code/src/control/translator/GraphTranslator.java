/* GraphTranslator.java */

/* The package of this class. */
package control.translator;

/* Imported classes and/or interfaces. */
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import control.exception.EdgeNotFoundException;
import control.exception.NodeNotFoundException;
import model.etpd.EventTimeProbabilityDistribution;
import model.graph.DynamicEdge;
import model.graph.DynamicNode;
import model.graph.Edge;
import model.graph.Graph;
import model.graph.Node;
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
	 * @throws NodeNotFoundException
	 * @throws EdgeNotFoundException
	 */
	public static Graph[] getGraphs(Element xml_element)
			throws NodeNotFoundException, EdgeNotFoundException {
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
			String str_visibility = node_element.getAttribute("visibility");
			String str_idleness = node_element.getAttribute("idleness");
			String str_fuel = node_element.getAttribute("fuel");
			String str_is_enabled = node_element.getAttribute("is_enabled");

			// obtains the eventual time probability distributions
			EventTimeProbabilityDistribution[] etpds = EventTimeProbabilityDistributionTranslator
					.getEventTimeProbabilityDistribution(node_element);

			// instantiates the new node (normal or dynamic)
			Node current_node = null;
			if (etpds.length == 0) {
				// new normal node
				current_node = new Node(label);
			} else {
				// verifies if the node is enabled
				boolean is_enabled = true;
				if (str_is_enabled.length() > 0)
					is_enabled = Boolean.parseBoolean(str_is_enabled);

				// new dynamic node
				current_node = new DynamicNode(label, etpds[0], etpds[1],
						is_enabled);
			}

			// configures the new node...
			// id configuration
			current_node.setObjectId(id);

			// priority configuration
			int priority = 0;
			if (str_priority.length() > 0)
				priority = Integer.parseInt(str_priority);
			current_node.setPriority(priority);

			// visibility configuration
			boolean visibility = true;
			if (str_visibility.length() > 0)
				visibility = Boolean.parseBoolean(str_visibility);
			current_node.setVisibility(visibility);

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
	 * @throws NodeNotFoundException
	 */
	private static Edge[] getEdges(Element xml_element, Node[] nodes)
			throws NodeNotFoundException {
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
			String source_id = edge_element.getAttribute("source");
			String target_id = edge_element.getAttribute("target");
			String str_directed = edge_element.getAttribute("directed");
			double length = Double.parseDouble(edge_element
					.getAttribute("length"));
			String str_visibility = edge_element.getAttribute("visibility");
			String str_is_enabled = edge_element.getAttribute("is_enabled");
			String str_is_in_dynamic_source_memory = edge_element
					.getAttribute("is_in_dynamic_source_memory");
			String str_is_in_dynamic_target_memory = edge_element
					.getAttribute("is_in_dynamic_target_memory");

			// obtains the eventual time probability distributions
			EventTimeProbabilityDistribution[] etpds = EventTimeProbabilityDistributionTranslator
					.getEventTimeProbabilityDistribution(edge_element);

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

			// if no source neither target are valid, throw exception
			if (source == null || target == null)
				throw new NodeNotFoundException();

			// decides if the edge is directed
			boolean directed = false;
			if (str_directed.length() > 0)
				directed = Boolean.parseBoolean(str_directed);

			// instantiates the new edge (normal or dynamic)
			Edge current_edge = null;
			if (etpds.length == 0) {
				// new normal edge
				current_edge = new Edge(source, target, directed, length);
			} else {
				// decides if the edge is enabled
				boolean is_enabled = true;
				if (str_is_enabled.length() > 0)
					is_enabled = Boolean.parseBoolean(str_is_enabled);

				// new dynamic edge
				current_edge = new DynamicEdge(source, target, directed,
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

			// decides if the edge is in the source and target enabling
			// memories
			boolean is_in_dynamic_source_memory = false;
			boolean is_in_dynamic_target_memory = false;
			if (str_is_in_dynamic_source_memory.length() > 0)
				is_in_dynamic_source_memory = Boolean
						.parseBoolean(str_is_in_dynamic_source_memory);
			if (str_is_in_dynamic_target_memory.length() > 0)
				is_in_dynamic_target_memory = Boolean
						.parseBoolean(str_is_in_dynamic_target_memory);

			// if the source is a dynamic node and
			// the current edge is in its memory of enabled edges
			if (source instanceof DynamicNode
					&& is_in_dynamic_source_memory)
				((DynamicNode) source).addEnabledEdge(current_edge);

			// if the target is a dynamic node and
			// the current edge is in its memory of enabled edges
			if (target instanceof DynamicNode
					&& is_in_dynamic_target_memory)
				((DynamicNode) target).addEnabledEdge(current_edge);

			// adds the new edge to the answer
			answer[i] = current_edge;
		}

		// returns the answer
		return answer;
	}
}