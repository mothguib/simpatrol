/* GraphTranslator.java (2.0) */
package br.org.simpatrol.server.model.graph;

/* Imported classes and/or interfaces. */
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import br.org.simpatrol.server.model.etpd.EventTimeProbabilityDistribution;
import br.org.simpatrol.server.model.etpd.EventTimeProbabilityDistributionTranslator;
import br.org.simpatrol.server.model.stigma.Stigma;
import br.org.simpatrol.server.model.stigma.StigmaTranslator;
import br.org.simpatrol.server.util.translator.XMLToObjectTranslationException;
import br.org.simpatrol.server.util.translator.XMLToObjectTranslator;

/**
 * Implements a translator that obtains {@link Graph} objects from XML source
 * elements.
 * 
 * @author Daniel Henriques Moreira (dhenriques@gmail.com)
 */
public class GraphTranslator extends XMLToObjectTranslator {
	/* Methods. */
	/**
	 * Obtains the graphs from the given XML element.
	 * 
	 * @param xmlElement
	 *            The XML source containing the graphs.
	 * 
	 * @return The graphs from the XML source.
	 * @throws XMLToObjectTranslationException
	 *             See the message for details.
	 * @throws GraphWithoutVertexesException
	 *             A graph must have at least one vertex.
	 */
	public List<Graph> getGraphs(Element xmlElement)
			throws XMLToObjectTranslationException,
			GraphWithoutVertexesException {
		// obtains the nodes with the "graph" tag
		NodeList graphNode = xmlElement.getElementsByTagName("graph");

		// the answer to the method
		List<Graph> answer = new ArrayList<Graph>(graphNode.getLength());

		// for each graph_node
		for (int i = 0; i < graphNode.getLength(); i++) {
			// obtains the current graph element
			Element graphElement = (Element) graphNode.item(i);

			// obtains the data
			String label = graphElement.getAttribute("label");

			// obtains the vertexes
			List<Vertex> vertexes = this.getVertexes(graphElement);

			// obtains the edges
			this.getEdges(graphElement, vertexes);

			// obtains the new graph
			Set<Vertex> vertexesSet = new HashSet<Vertex>();
			for (Vertex vertex : vertexes)
				vertexesSet.add(vertex);
			Graph graph = new Graph(label, vertexesSet);

			// obtains the stigmas
			List<Stigma> stigmas = (new StigmaTranslator()).getStigmas(
					graphElement, graph);

			// adds the obtained stigmas to the graph
			if (stigmas != null)
				for (Stigma currentStigma : stigmas)
					graph.addStigma(currentStigma);

			// adds the new graph to the answer
			answer.add(graph);
		}

		// returns the answer
		if (answer.size() > 0)
			return answer;
		else
			return null;
	}

	/**
	 * Obtains the vertexes from the given XML element.
	 * 
	 * @param xmlElement
	 *            The XML source containing the vertexes.
	 * 
	 * @return The vertexes from the XML source.
	 * @throws XMLToObjectTranslationException
	 *             See the message for details.
	 */
	private List<Vertex> getVertexes(Element xmlElement)
			throws XMLToObjectTranslationException {
		// obtains the nodes with the "vertex" tag
		NodeList vertexNodes = xmlElement.getElementsByTagName("vertex");

		// the answer to the method
		List<Vertex> answer = new ArrayList<Vertex>(vertexNodes.getLength());

		// for each occurrence
		for (int i = 0; i < vertexNodes.getLength(); i++) {
			// obtains the current vertex element
			Element vertexElement = (Element) vertexNodes.item(i);

			// obtains its data
			String id = vertexElement.getAttribute("id");
			String label = vertexElement.getAttribute("label");
			String strPriority = vertexElement.getAttribute("priority");
			String strVisibility = vertexElement.getAttribute("visibility");
			String strIdleness = vertexElement.getAttribute("idleness");
			String strFuel = vertexElement.getAttribute("fuel");
			String strIsEnabled = vertexElement.getAttribute("is_enabled");

			// obtains the eventual time probability distributions
			List<EventTimeProbabilityDistribution> etpds = new EventTimeProbabilityDistributionTranslator()
					.getEventTimeProbabilityDistribution(vertexElement);

			// instantiates the new vertex (normal or dynamic)
			Vertex currentVertex = null;
			if (etpds == null || etpds.isEmpty()) {
				// new normal vertex
				currentVertex = new Vertex(id, label);
			} else {
				// verifies if the vertex is enabled
				boolean isEnabled = true;
				if (strIsEnabled.length() > 0)
					isEnabled = Boolean.parseBoolean(strIsEnabled);

				// new dynamic vertex
				currentVertex = new DynamicVertex(id, label, etpds.get(0),
						etpds.get(1), isEnabled);
			}

			// priority configuration
			int priority = 0;
			if (strPriority.length() > 0)
				priority = Integer.parseInt(strPriority);
			currentVertex.setPriority(priority);

			// visibility configuration
			boolean visibility = true;
			if (strVisibility.length() > 0)
				visibility = Boolean.parseBoolean(strVisibility);
			currentVertex.setVisible(visibility);

			// idleness configuration
			double idleness = 0;
			if (strIdleness.length() > 0)
				idleness = Double.parseDouble(strIdleness);
			currentVertex.setIdleness(idleness);

			// fuel configuration
			boolean fuel = false;
			if (strFuel.length() > 0)
				fuel = Boolean.parseBoolean(strFuel);
			currentVertex.setFuel(fuel);

			// adds the new vertex to the answer
			answer.add(currentVertex);
		}

		// returns the answer
		if (answer.size() > 0)
			return answer;
		else
			return null;
	}

	/**
	 * Obtains the edges from the given XML element.
	 * 
	 * @param xmlElement
	 *            The XML source containing the edges.
	 * @param vertexes
	 *            The vertexes read from the XML source.
	 * 
	 * @return The edges from the XML source.
	 * @throws XMLToObjectTranslationException
	 *             See the message for details.
	 */
	private List<Edge> getEdges(Element xmlElement, List<Vertex> vertexes)
			throws XMLToObjectTranslationException {
		// obtains the nodes with the "edge" tag
		NodeList edgeNodes = xmlElement.getElementsByTagName("edge");

		// the answer to the method
		List<Edge> answer = new ArrayList<Edge>(edgeNodes.getLength());

		// for each occurrence
		for (int i = 0; i < edgeNodes.getLength(); i++) {
			// obtains the current edge element
			Element edgeElement = (Element) edgeNodes.item(i);

			// obtains its data
			String id = edgeElement.getAttribute("id");
			String emitterId = edgeElement.getAttribute("emitter_id");
			String collectorId = edgeElement.getAttribute("collector_id");
			String strOriented = edgeElement.getAttribute("oriented");
			double length = Double.parseDouble(edgeElement
					.getAttribute("length"));
			String strFreeWidth = edgeElement.getAttribute("free_width");
			String strVisibility = edgeElement.getAttribute("visibility");
			String strIsEnabled = edgeElement.getAttribute("is_enabled");
			String strIsInDynamicEmitterMemory = edgeElement
					.getAttribute("is_in_dynamic_emitter_memory");
			String strIsInDynamicCollectorMemory = edgeElement
					.getAttribute("is_in_dynamic_collector_memory");

			// obtains the eventual time probability distributions
			List<EventTimeProbabilityDistribution> etpds = new EventTimeProbabilityDistributionTranslator()
					.getEventTimeProbabilityDistribution(edgeElement);

			// finds the correspondent emitter and collector vertexes
			Vertex emitter = null;
			Vertex collector = null;

			if (vertexes != null)
				for (Vertex currentVertex : vertexes) {
					if (currentVertex.getId().equals(emitterId)) {
						emitter = currentVertex;
						if (collector != null)
							break;
					}

					if (currentVertex.getId().equals(collectorId)) {
						collector = currentVertex;
						if (emitter != null)
							break;
					}
				}

			// if no emitter neither collector are valid, throws exception
			if (emitter == null || collector == null)
				throw new XMLToObjectTranslationException(
						"Vertexes not found when translating the edge.");

			// decides if the edge is oriented
			boolean oriented = false;
			if (strOriented.length() > 0)
				oriented = Boolean.parseBoolean(strOriented);

			// instantiates the new edge (normal or dynamic)
			Edge currentEdge = null;
			if (etpds == null || etpds.isEmpty()) {
				// new normal edge
				currentEdge = new Edge(id, emitter, collector, oriented, length);
			} else {
				// decides if the edge is enabled
				boolean isEnabled = true;
				if (strIsEnabled.length() > 0)
					isEnabled = Boolean.parseBoolean(strIsEnabled);

				// new dynamic edge
				currentEdge = new DynamicEdge(id, emitter, collector, oriented,
						length, etpds.get(0), etpds.get(1), isEnabled);
			}

			// configures the new edge...
			// free width configuration
			int freeWidth = -1;
			if (strFreeWidth.length() > 0)
				freeWidth = Integer.parseInt(strFreeWidth);
			currentEdge.setFreeWidth(freeWidth);

			// visibility configuration
			boolean visibility = true;
			if (strVisibility.length() > 0)
				visibility = Boolean.parseBoolean(strVisibility);
			currentEdge.setVisible(visibility);

			// decides if the edge is in the emitter and collector enabling
			// memories
			boolean isInDynamicEmitterMemory = false;
			boolean isInDynamicCollectorMemory = false;
			if (strIsInDynamicEmitterMemory.length() > 0)
				isInDynamicEmitterMemory = Boolean
						.parseBoolean(strIsInDynamicEmitterMemory);
			if (strIsInDynamicCollectorMemory.length() > 0)
				isInDynamicCollectorMemory = Boolean
						.parseBoolean(strIsInDynamicCollectorMemory);

			// if the emitter is a dynamic vertex and
			// the current edge is in its memory of enabled edges
			if (emitter instanceof DynamicVertex && isInDynamicEmitterMemory)
				((DynamicVertex) emitter).addEnabledEdge(currentEdge);

			// if the collector is a dynamic vertex and
			// the current edge is in its memory of enabled edges
			if (collector instanceof DynamicVertex
					&& isInDynamicCollectorMemory)
				((DynamicVertex) collector).addEnabledEdge(currentEdge);

			// adds the new edge to the answer
			answer.add(currentEdge);
		}

		// returns the answer
		if (answer.size() > 0)
			return answer;
		else
			return null;
	}
}