/* StigmaTranslator.java (2.0) */
package br.org.simpatrol.server.model.stigma;

/* Imported classes and/or interfaces. */
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import br.org.simpatrol.server.model.action.StigmatizeAction;
import br.org.simpatrol.server.model.agent.Agent;
import br.org.simpatrol.server.model.graph.Edge;
import br.org.simpatrol.server.model.graph.Graph;
import br.org.simpatrol.server.model.graph.Vertex;
import br.org.simpatrol.server.util.translator.XMLToObjectTranslationException;
import br.org.simpatrol.server.util.translator.XMLToObjectTranslator;

/**
 * Implements a translator that obtains {@link Stigma} objects from a given XML
 * source.
 * 
 * @author Daniel Henriques Moreira (dhenriques@gmail.com)
 */
public class StigmaTranslator extends XMLToObjectTranslator {
	/* Methods. */
	/**
	 * Obtains the stigmas from the given XML element.
	 * 
	 * @param xmlElement
	 *            The XML source containing the stigmas.
	 * @param graph
	 *            The graph of the simulation performed by SimPatrol.
	 * 
	 * @return The stigmas obtained from the XML source.
	 * @throws XMLToObjectTranslationException
	 *             See the message for details.
	 */
	public List<Stigma> getStigmas(Element xmlElement, Graph graph)
			throws XMLToObjectTranslationException {
		// obtains the nodes with the "stigma" tag
		NodeList stigmaNode = xmlElement.getElementsByTagName("stigma");

		// the answer to the method
		List<Stigma> answer = new ArrayList<Stigma>(stigmaNode.getLength());

		// for each stigma node
		for (int i = 0; i < stigmaNode.getLength(); i++) {
			// obtains the current stigma element
			Element stigmaElement = (Element) stigmaNode.item(i);

			// obtains its data
			String id = stigmaElement.getAttribute("id");
			byte type = Byte.parseByte(stigmaElement.getAttribute("type"));
			String vertexId = stigmaElement.getAttribute("vertex_id");
			String edgeId = stigmaElement.getAttribute("edge_id");

			// tries to obtain its vertex or its edge
			Vertex vertex = null;
			Edge edge = null;

			// if the vertex_id is valid
			if (vertexId.length() > 0) {
				// finds the correspondent vertex in the given graph
				Set<Vertex> vertexes = graph.getVertexes();
				for (Vertex currentVertex : vertexes)
					if (currentVertex.getId().equals(vertexId)) {
						vertex = currentVertex;
						break;
					}

				// if a valid vertex was not found, throws an exception
				if (vertex == null)
					throw new XMLToObjectTranslationException(
							"There is no vertex with the given vertex_id.");
			}
			// if not, there must be an edge_id
			else if (edgeId.length() > 0) {
				// finds the correspondent edge in the graph
				Set<Edge> edges = graph.getEdges();
				for (Edge currentEdge : edges)
					if (currentEdge.getId().equals(edgeId)) {
						edge = currentEdge;
						break;
					}

				// if a valid edge was not found, throws an exception
				if (edge == null)
					throw new XMLToObjectTranslationException(
							"There is no edge with the given edge_id.");
			} else
				throw new XMLToObjectTranslationException(
						"There is no valid vertex_id, nor edge_id.");

			// instantiates the current stigma being translated
			if (type == StigmaTypes.PHEROMONE.getType()) {
				// the stigma is a pheromone...
				double quantity = Double.parseDouble(stigmaElement
						.getAttribute("quantity"));
				double evaporationRate = Double.parseDouble(stigmaElement
						.getAttribute("evaporation_rate"));
				String strEvaporationTimeInterval = stigmaElement
						.getAttribute("evaporation_time_interval");

				Pheromone pheromone = null;
				if (vertex != null)
					pheromone = new Pheromone(id, vertex, quantity,
							evaporationRate);
				else
					pheromone = new Pheromone(id, edge, quantity,
							evaporationRate);

				if (strEvaporationTimeInterval.length() > 0)
					pheromone.setEvaporationTimeInterval(Double
							.parseDouble(strEvaporationTimeInterval));

				answer.add(pheromone);

			} else if (type == StigmaTypes.MESSAGED.getType()) {
				// the stigma is a messaged stigma
				String message = stigmaElement.getAttribute("message");

				if (vertex != null)
					answer.add(new MessagedStigma(id, vertex, message));
				else
					answer.add(new MessagedStigma(id, edge, message));
			} else {
				throw new XMLToObjectTranslationException(
						"Stigma type does not exist.");
			}
		}

		// returns the answer
		if (answer.size() > 0)
			return answer;
		else
			return null;
	}

	/**
	 * Obtains the {@link Stigma} objects from the given XML element, this
	 * obtained from a {@link StigmatizeAction} object.
	 * 
	 * @param xmlElement
	 *            The XML source containing the stigmas, obtained from a
	 *            {@link StigmatizeAction} object.
	 * @param agent
	 *            The agent responsible for the {@link StigmatizeAction} object.
	 * 
	 * @return The {@link Stigma} objects obtained from the XML source.
	 * @throws XMLToObjectTranslationException
	 *             See the message for details.
	 */
	public List<Stigma> getStigmasFromStigmatizeAction(Element xmlElement,
			Agent agent) throws XMLToObjectTranslationException {
		// obtains the nodes with the "stigma" tag
		NodeList stigmaNode = xmlElement.getElementsByTagName("stigma");

		// the answer to the method
		List<Stigma> answer = new ArrayList<Stigma>(stigmaNode.getLength());

		// for each stigma node
		for (int i = 0; i < stigmaNode.getLength(); i++) {
			// obtains the current stigma element
			Element stigmaElement = (Element) stigmaNode.item(i);

			// obtains its data
			String id = stigmaElement.getAttribute("id");
			byte type = Byte.parseByte(stigmaElement.getAttribute("type"));

			// obtains its vertex or its edge
			Vertex vertex = null;
			Edge edge = null;

			if (agent.getEdge() != null)
				edge = agent.getEdge();
			else
				vertex = agent.getVertex();

			// instantiates the current stigma being translated
			if (type == StigmaTypes.PHEROMONE.getType()) {
				// the stigma is a pheromone...
				double quantity = Double.parseDouble(stigmaElement
						.getAttribute("quantity"));
				double evaporationRate = Double.parseDouble(stigmaElement
						.getAttribute("evaporation_rate"));
				String strEvaporationTimeInterval = stigmaElement
						.getAttribute("evaporation_time_interval");

				Pheromone pheromone = null;
				if (vertex != null)
					pheromone = new Pheromone(id, vertex, quantity,
							evaporationRate);
				else
					pheromone = new Pheromone(id, edge, quantity,
							evaporationRate);

				if (strEvaporationTimeInterval.length() > 0)
					pheromone.setEvaporationTimeInterval(Double
							.parseDouble(strEvaporationTimeInterval));

				answer.add(pheromone);
			} else if (type == StigmaTypes.MESSAGED.getType()) {
				// the stigma is a messaged stigma
				String message = stigmaElement.getAttribute("message");

				if (vertex != null)
					answer.add(new MessagedStigma(id, vertex, message));
				else
					answer.add(new MessagedStigma(id, edge, message));
			} else {
				throw new XMLToObjectTranslationException(
						"Stigma type does not exist.");
			}
		}

		// returns the answer
		if (answer.size() > 0)
			return answer;
		else
			return null;
	}
}