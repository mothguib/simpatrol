/* PerceptionTranslator.java (2.0) */
package br.org.simpatrol.server.model.perception;

/* Imported classes and/or interfaces. */
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import br.org.simpatrol.server.model.agent.Agent;
import br.org.simpatrol.server.model.agent.AgentTranslator;
import br.org.simpatrol.server.model.graph.Graph;
import br.org.simpatrol.server.model.graph.GraphTranslator;
import br.org.simpatrol.server.model.graph.GraphWithoutVertexesException;
import br.org.simpatrol.server.model.stigma.Stigma;
import br.org.simpatrol.server.model.stigma.StigmaTranslator;
import br.org.simpatrol.server.util.translator.XMLToObjectTranslationException;
import br.org.simpatrol.server.util.translator.XMLToObjectTranslator;

/**
 * Implements a translator that obtains {@link Perception} objects from XML
 * source elements.
 * 
 * @author Daniel Henriques Moreira (dhenriques@gmail.com)
 */
public class PerceptionTranslator extends XMLToObjectTranslator {
	/* Methods. */
	/**
	 * Obtains the perceptions from the given XML element, except for the
	 * {@link AgentsPerception} perceptions, for the {@link StigmasPerception}
	 * perceptions, and for the perceptions of itself ({@link SelfPerception}
	 * perceptions).
	 * 
	 * To obtain {@link AgentsPerception} objects, or {@link StigmasPerception}
	 * objects, or {@link SelfPerception} objects, use
	 * {@link #getPerceptions(Element, Graph)}.
	 * 
	 * @param xmlElement
	 *            The XML source containing the perceptions.
	 * 
	 * @return The perceptions from the XML source.
	 * @throws GraphWithoutVertexesException
	 *             A graph must have at least one vertex.
	 * @throws XMLToObjectTranslationException
	 *             See the message for details.
	 */
	public List<? extends Perception> getPerceptions(Element xmlElement)
			throws XMLToObjectTranslationException,
			GraphWithoutVertexesException {
		return this.getPerceptions(xmlElement, null);
	}

	/**
	 * Obtains the perceptions from the given XML element.
	 * 
	 * @param xmlElement
	 *            The XML source containing the perceptions.
	 * @param graph
	 *            The previously perceived graph of the simulation.
	 * 
	 * @return The perceptions from the XML source.
	 * @throws GraphWithoutVertexesException
	 *             A graph must have at least one vertex.
	 * @throws XMLToObjectTranslationException
	 *             See the message for details.
	 */
	public List<? extends Perception> getPerceptions(Element xmlElement,
			Graph graph) throws XMLToObjectTranslationException,
			GraphWithoutVertexesException {
		if (graph != null)
			try {
				// tries to obtain perceptions with other agents
				return this.getAgentsPerceptions(xmlElement, graph);
			} catch (XMLToObjectTranslationException exception1) {
				exception1.printStackTrace();

				try {
					// if failed for the type of the perception, tries to obtain
					// perceptions of itself
					return this.getSelfPerceptions(xmlElement, graph);
				} catch (XMLToObjectTranslationException exception2) {
					exception2.printStackTrace();

					// if failed again for the type of the perception, tries to
					// obtain perceptions of stigmas deposited on the
					// environment
					try {
						return this.getStigmasPerceptions(xmlElement, graph);
					} catch (XMLToObjectTranslationException exception3) {
						exception3.printStackTrace();

						// if failed again for the type of the perception, tries
						// to obtain other types of perceptions
						return this.getSimplePerceptions(xmlElement);
					}
				}
			}
		else
			return this.getSimplePerceptions(xmlElement);
	}

	/**
	 * Obtains the perceptions from the given XML element, except for the
	 * {@link AgentsPerception} perceptions, for the {@link StigmasPerception}
	 * perceptions, and for the perceptions of itself ({@link SelfPerception}
	 * perceptions).
	 * 
	 * To obtain {@link AgentsPerception} objects, use
	 * {@link #getAgentsPerceptions(Element, Graph)}.
	 * 
	 * To obtain {@link StigmasPerception} objects, use
	 * {@link #getStigmasPerceptions(Element, Graph)}.
	 * 
	 * To obtain {@link SelfPerception} objects, use
	 * {@link #getSelfPerceptions(Element, Graph)}.
	 * 
	 * @param xmlElement
	 *            The XML source containing the perceptions.
	 * 
	 * @return The perceptions from the XML source.
	 * @throws GraphWithoutVertexesException
	 *             A graph must have at least one vertex.
	 * @throws XMLToObjectTranslationException
	 *             See the message for details.
	 */
	private List<Perception> getSimplePerceptions(Element xmlElement)
			throws XMLToObjectTranslationException,
			GraphWithoutVertexesException {
		// obtains the nodes with the "perception" tag
		NodeList perceptionNode = xmlElement.getElementsByTagName("perception");

		// holds all the obtained perceptions
		List<Perception> answer = new ArrayList<Perception>(perceptionNode
				.getLength());

		// a translator of graphs
		GraphTranslator graphTranslator = new GraphTranslator();

		// for each perception node
		for (int i = 0; i < perceptionNode.getLength(); i++) {
			// obtains the current perception element
			Element perceptionElement = (Element) perceptionNode.item(i);

			// the current perception to be obtained
			Perception perception = null;

			// obtains the type of the perception
			byte type = Byte.parseByte(perceptionElement.getAttribute("type"));

			// 1st. if the type is of a graph perception
			if (type == PerceptionTypes.GRAPH.getType()) {
				List<Graph> graphs = graphTranslator
						.getGraphs(perceptionElement);
				perception = new GraphPerception(graphs.get(0));
			}

			// 2nd. else, if the perception is a broadcasted message
			else if (type == PerceptionTypes.BROADCAST.getType()) {
				String message = perceptionElement.getAttribute("message");
				perception = new BroadcastPerception(message);
			}

			// else, throws a perception type not valid exception
			else
				throw new XMLToObjectTranslationException(
						"Perception type not valid.");

			// adds the current perception to the list of perceptions, if it's
			// valid
			answer.add(perception);
		}

		// returns the answer
		if (answer.size() > 0)
			return answer;
		else
			return null;
	}

	/**
	 * Obtains the perceptions of agents ({@link AgentsPerception} objects) from
	 * the given XML element.
	 * 
	 * @param xmlElement
	 *            The XML source containing the perceptions.
	 * @param graph
	 *            The previously perceived graph of the simulation.
	 * 
	 * @return The perceptions from the XML source.
	 * @throws XMLToObjectTranslationException
	 *             See the message for details.
	 */
	private List<AgentsPerception> getAgentsPerceptions(Element xmlElement,
			Graph graph) throws XMLToObjectTranslationException {
		// obtains the nodes with the "perception" tag
		NodeList perceptionNode = xmlElement.getElementsByTagName("perception");

		// holds all the obtained perceptions
		List<AgentsPerception> answer = new ArrayList<AgentsPerception>(
				perceptionNode.getLength());

		// the translator of agents
		AgentTranslator agentTranslator = new AgentTranslator();

		// for each perception node
		for (int i = 0; i < perceptionNode.getLength(); i++) {
			// obtains the current perception element
			Element perceptionElement = (Element) perceptionNode.item(i);

			// the current perception to be obtained
			AgentsPerception perception = null;

			// the type of the current perception
			byte type = Byte.parseByte(perceptionElement.getAttribute("type"));

			// if the type is of perceived agents
			if (type == PerceptionTypes.AGENTS.getType()) {
				// obtains the agents from the perception element
				Set<Agent> agents = new HashSet<Agent>(agentTranslator
						.getAgents(perceptionElement, graph));

				perception = new AgentsPerception(agents);
			}

			// else, throws a perception type not valid exception
			else
				throw new XMLToObjectTranslationException(
						"The expected perception type was "
								+ PerceptionTypes.AGENTS.getType() + ", but "
								+ type + " was found.");

			// adds the current perception to the list of perceptions
			answer.add(perception);
		}

		// returns the answer
		if (answer.size() > 0)
			return answer;
		else
			return null;
	}

	/**
	 * Obtains the perceptions of itself ({@link SelfPerception} objects) from
	 * the given XML element.
	 * 
	 * @param xmlElement
	 *            The XML source containing the perceptions.
	 * @param graph
	 *            The previously perceived graph of the simulation.
	 * 
	 * @return The perceptions from the XML source.
	 * @throws XMLToObjectTranslationException
	 *             See the message for details.
	 */
	private List<SelfPerception> getSelfPerceptions(Element xmlElement,
			Graph graph) throws XMLToObjectTranslationException {
		// obtains the nodes with the "perception" tag
		NodeList perceptionNode = xmlElement.getElementsByTagName("perception");

		// holds all the obtained perceptions
		List<SelfPerception> answer = new ArrayList<SelfPerception>(
				perceptionNode.getLength());

		// the translator of agents
		AgentTranslator agentTranslator = new AgentTranslator();

		// for each perception node
		for (int i = 0; i < perceptionNode.getLength(); i++) {
			// obtains the current perception element
			Element perceptionElement = (Element) perceptionNode.item(i);

			// the current perception to be obtained
			SelfPerception perception = null;

			// the type of the current perception
			byte type = Byte.parseByte(perceptionElement.getAttribute("type"));

			// if the type is a self perception
			if (type == PerceptionTypes.SELF.getType()) {
				// tries to obtain the agent itself from the perception element
				List<Agent> agents = agentTranslator.getAgents(
						perceptionElement, graph);
				perception = new SelfPerception(agents.get(0));
			}
			// else, throws an exception
			else
				throw new XMLToObjectTranslationException(
						"The expected perception type was "
								+ PerceptionTypes.SELF.getType() + ", but "
								+ type + " was found.");

			// adds the current perception to the list of perceptions
			answer.add(perception);
		}

		// returns the answer
		if (answer.size() > 0)
			return answer;
		else
			return null;
	}

	/**
	 * Obtains the perceptions of stigmas ({@link StigmasPerception} objects)
	 * from the given XML element.
	 * 
	 * @param xmlElement
	 *            The XML source containing the perceptions.
	 * @param graph
	 *            The previously perceived graph of the simulation.
	 * 
	 * @return The perceptions from the XML source.
	 * @throws XMLToObjectTranslationException
	 *             See the message for details.
	 */
	private List<StigmasPerception> getStigmasPerceptions(Element xmlElement,
			Graph graph) throws XMLToObjectTranslationException {
		// obtains the nodes with the "perception" tag
		NodeList perceptionNode = xmlElement.getElementsByTagName("perception");

		// holds all the obtained perceptions
		List<StigmasPerception> answer = new ArrayList<StigmasPerception>(
				perceptionNode.getLength());

		// the translator of stigmas
		StigmaTranslator stigmaTranslator = new StigmaTranslator();

		// for each perception mnode
		for (int i = 0; i < perceptionNode.getLength(); i++) {
			// obtains the current perception element
			Element perceptionElement = (Element) perceptionNode.item(i);

			// the current perception to be obtained
			StigmasPerception perception = null;

			// the type of the current perception
			byte type = Byte.parseByte(perceptionElement.getAttribute("type"));

			// if the type is a stigmas perception
			if (type == PerceptionTypes.STIGMAS.getType()) {
				// tries to obtain the stigmas from the perception element
				Set<Stigma> stigmas = new HashSet<Stigma>(stigmaTranslator
						.getStigmas(perceptionElement, graph));

				perception = new StigmasPerception(stigmas);
			}
			// else, throws an exception
			else
				throw new XMLToObjectTranslationException(
						"The expected perception type was "
								+ PerceptionTypes.STIGMAS.getType() + ", but "
								+ type + " was found.");

			// adds the current perception to the list of perceptions
			answer.add(perception);
		}

		// returns the answer
		if (answer.size() > 0)
			return answer;
		else
			return null;
	}
}