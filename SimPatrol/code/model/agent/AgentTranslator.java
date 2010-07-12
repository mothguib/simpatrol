/* AgentTranslator.java (2.0) */
package br.org.simpatrol.server.model.agent;

/* Imported classes and/or interfaces. */
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import br.org.simpatrol.server.model.action.ActionTypes;
import br.org.simpatrol.server.model.etpd.EventTimeProbabilityDistribution;
import br.org.simpatrol.server.model.etpd.EventTimeProbabilityDistributionTranslator;
import br.org.simpatrol.server.model.graph.Edge;
import br.org.simpatrol.server.model.graph.Graph;
import br.org.simpatrol.server.model.graph.Vertex;
import br.org.simpatrol.server.model.limitation.Limitation;
import br.org.simpatrol.server.model.limitation.LimitationTranslator;
import br.org.simpatrol.server.model.perception.PerceptionTypes;
import br.org.simpatrol.server.util.translator.XMLToObjectTranslationException;
import br.org.simpatrol.server.util.translator.XMLToObjectTranslator;

/**
 * Implements a translator that obtains {@link Agent} objects from XML source
 * elements.
 * 
 * @author Daniel Henriques Moreira (dhenriques@gmail.com)
 */
public class AgentTranslator extends XMLToObjectTranslator {
	/* Methods. */
	/**
	 * Obtains {@link Agent} objects from the given XML element.
	 * 
	 * @param xmlElement
	 *            The XML source containing the agents.
	 * @param graph
	 *            The graph of the simulation.
	 * 
	 * @return The agents obtained from the XML source. Be aware that they are
	 *         all {@link PerpetualAgent} objects.
	 * @throws XMLToObjectTranslationException
	 *             See the message for details.
	 */
	public List<Agent> getAgents(Element xmlElement, Graph graph)
			throws XMLToObjectTranslationException {
		return this.getAgents(xmlElement, graph, true);
	}

	/**
	 * Obtains {@link Agent} objects from the given XML element.
	 * 
	 * @param xmlElement
	 *            The XML source containing the agents.
	 * @param graph
	 *            The graph of the simulation.
	 * @param arePerpetualAgents
	 *            TRUE, if the obtained agents are {@link PerpetualAgent}
	 *            objects, FALSE if they are {@link SeasonalAgent} objects.
	 * 
	 * @return The {@link Agent} objects obtained from the XML source.
	 * @throws XMLToObjectTranslationException
	 *             See the message for details.
	 */
	public List<Agent> getAgents(Element xmlElement, Graph graph,
			boolean arePerpetualAgents) throws XMLToObjectTranslationException {
		// obtains the nodes with the "agent" tag
		NodeList agentNodes = xmlElement.getElementsByTagName("agent");

		// the answer of the method
		List<Agent> answer = new ArrayList<Agent>(agentNodes.getLength());

		// a translator of etpds, if necessary
		EventTimeProbabilityDistributionTranslator etpdTranslator = null;
		if (!arePerpetualAgents)
			etpdTranslator = new EventTimeProbabilityDistributionTranslator();

		// for all the occurrences
		for (int i = 0; i < agentNodes.getLength(); i++) {
			// obtains the current agent element
			Element agentElement = (Element) agentNodes.item(i);

			// obtains its data
			String id = agentElement.getAttribute("id");
			String label = agentElement.getAttribute("label");
			String vertexId = agentElement.getAttribute("vertex_id");
			String edgeId = agentElement.getAttribute("edge_id");
			String strElapsedLength = agentElement
					.getAttribute("elapsed_length");
			String strStamina = agentElement.getAttribute("stamina");
			String strMaxStamina = agentElement.getAttribute("max_stamina");

			// finds the vertex of the agent
			Vertex vertex = null;
			for (Vertex currentVertex : graph.getVertexes())
				if (currentVertex.getId().equals(vertexId)) {
					vertex = currentVertex;
					break;
				}

			// if no valid vertex was found, throws exception
			if (vertex == null)
				throw new XMLToObjectTranslationException(
						"Vertex id not valid.");

			// finds the eventual edge of the agent
			Edge edge = null;
			if (edgeId.length() > 0) {
				for (Edge currentEdge : graph.getEdges())
					if (currentEdge.getId().equals(edgeId)) {
						edge = currentEdge;
						break;
					}

				// if no valid edge was found, throws exception
				if (edge == null)
					throw new XMLToObjectTranslationException(
							"Edge id not valid.");
			}

			// instantiates the new agent (perpetual or seasonal)
			Agent agent = null;
			if (arePerpetualAgents)
				agent = new PerpetualAgent(id, label, vertex,
						new HashSet<PerceptionAbility>(this
								.getPerceptionAbilities(agentElement)),
						new HashSet<ActionAbility>(this
								.getActionAbilities(agentElement)));
			else {
				// tries to obtain a death TPD
				List<EventTimeProbabilityDistribution> deathTPDs = etpdTranslator
						.getEventTimeProbabilityDistribution(agentElement);
				EventTimeProbabilityDistribution deathTPD = null;
				if (deathTPDs != null && !deathTPDs.isEmpty())
					deathTPD = deathTPDs.get(0);

				agent = new SeasonalAgent(id, label, vertex, deathTPD,
						new HashSet<PerceptionAbility>(this
								.getPerceptionAbilities(agentElement)),
						new HashSet<ActionAbility>(this
								.getActionAbilities(agentElement)));
			}

			// configures the new agent...
			// edge and elapsed length configuration
			if (edge != null) {
				double elapsedLength = 0;
				if (strElapsedLength.length() > 0)
					elapsedLength = Double.parseDouble(strElapsedLength);

				agent.setEdge(edge, elapsedLength);
			}

			// stamina configuration
			if (strStamina.length() > 0)
				agent.setStamina(Double.parseDouble(strStamina));
			if (strMaxStamina.length() > 0)
				agent.setMaxStamina(Double.parseDouble(strMaxStamina));

			// puts the agent in the answer
			answer.add(agent);
		}

		// returns the answer
		if (answer.size() > 0)
			return answer;
		else
			return null;
	}

	/**
	 * Obtains {@link PerceptionAbility} objects from the given XML element.
	 * 
	 * @param xmlElement
	 *            The XML source containing the perception abilities.
	 * 
	 * @return The {@link PerceptionAbility} objects obtained from the XML
	 *         source.
	 * @throws XMLToObjectTranslationException
	 *             See the message for details.
	 */
	private List<PerceptionAbility> getPerceptionAbilities(Element xmlElement)
			throws XMLToObjectTranslationException {
		// obtains the nodes with the "perception_ability" tag
		NodeList perceptionAbilityNodes = xmlElement
				.getElementsByTagName("perception_ability");

		// the answer for the method
		List<PerceptionAbility> answer = new ArrayList<PerceptionAbility>(
				perceptionAbilityNodes.getLength());

		// a translator of "limitation" objects
		LimitationTranslator limitationTranslator = new LimitationTranslator();

		// for all the occurrences
		for (int i = 0; i < perceptionAbilityNodes.getLength(); i++) {
			// obtains the current perception_ability element
			Element perceptionAbilityElement = (Element) perceptionAbilityNodes
					.item(i);

			// obtains its data
			byte perceptionType = Byte.parseByte(perceptionAbilityElement
					.getAttribute("type"));

			// the set of limitations obtained from the xml element
			Set<Limitation> limitations = new HashSet<Limitation>();

			// instantiates the new perception_ability object
			PerceptionAbility perceptionAbility = null;

			if (perceptionType == PerceptionTypes.AGENTS.getType()) {
				List<Limitation> limitationsList = limitationTranslator
						.getLimitations(perceptionAbilityElement);
				if (limitationsList != null)
					limitations.addAll(limitationsList);

				perceptionAbility = new PerceptionAbility(limitations,
						PerceptionTypes.AGENTS);
			} else if (perceptionType == PerceptionTypes.BROADCAST.getType()) {
				List<Limitation> limitationsList = limitationTranslator
						.getLimitations(perceptionAbilityElement);
				if (limitationsList != null)
					limitations.addAll(limitationsList);

				perceptionAbility = new PerceptionAbility(limitations,
						PerceptionTypes.BROADCAST);
			} else if (perceptionType == PerceptionTypes.GRAPH.getType()) {
				List<Limitation> limitationsList = limitationTranslator
						.getLimitations(perceptionAbilityElement);
				if (limitationsList != null)
					limitations.addAll(limitationsList);

				perceptionAbility = new PerceptionAbility(limitations,
						PerceptionTypes.GRAPH);
			} else if (perceptionType == PerceptionTypes.SELF.getType()) {
				List<Limitation> limitationsList = limitationTranslator
						.getLimitations(perceptionAbilityElement);
				if (limitationsList != null)
					limitations.addAll(limitationsList);

				perceptionAbility = new PerceptionAbility(limitations,
						PerceptionTypes.SELF);
			} else if (perceptionType == PerceptionTypes.STIGMAS.getType()) {
				List<Limitation> limitationsList = limitationTranslator
						.getLimitations(perceptionAbilityElement);
				if (limitationsList != null)
					limitations.addAll(limitationsList);

				perceptionAbility = new PerceptionAbility(limitations,
						PerceptionTypes.STIGMAS);
			} else
				throw new XMLToObjectTranslationException(
						"Perception type not valid.");

			// puts the perception ability in the answer
			answer.add(perceptionAbility);
		}

		// returns the answer
		if (answer.size() > 0)
			return answer;
		else
			return null;
	}

	/**
	 * Obtains {@link ActionAbility} objects from the given XML element.
	 * 
	 * @param xmlElement
	 *            The XML source containing the action abilities.
	 * 
	 * @return The {@link ActionAbility} objects obtained from the XML source.
	 * @throws XMLToObjectTranslationException
	 *             See the message for details.
	 */
	private List<ActionAbility> getActionAbilities(Element xmlElement)
			throws XMLToObjectTranslationException {
		// obtains the nodes with the "action_ability" tag
		NodeList actionAbilityNodes = xmlElement
				.getElementsByTagName("action_ability");

		// the answer for the method
		List<ActionAbility> answer = new ArrayList<ActionAbility>(
				actionAbilityNodes.getLength());

		// a translator of "limitation" objects
		LimitationTranslator limitationTranslator = new LimitationTranslator();

		// for all the occurrences
		for (int i = 0; i < actionAbilityNodes.getLength(); i++) {
			// obtains the current action_ability element
			Element actionAbilityElement = (Element) actionAbilityNodes.item(i);

			// obtains its data
			byte actionType = Byte.parseByte(actionAbilityElement
					.getAttribute("type"));

			// the set of limitations obtained from the xml element
			Set<Limitation> limitations = new HashSet<Limitation>();

			// instantiates the new action_ability object
			ActionAbility actionAbility = null;

			if (actionType == ActionTypes.ATOMIC_RECHARGE.getType()) {
				List<Limitation> limitationsList = limitationTranslator
						.getLimitations(actionAbilityElement);
				if (limitationsList != null)
					limitations.addAll(limitationsList);

				actionAbility = new ActionAbility(limitations,
						ActionTypes.ATOMIC_RECHARGE);
			} else if (actionType == ActionTypes.BROADCAST.getType()) {
				List<Limitation> limitationsList = limitationTranslator
						.getLimitations(actionAbilityElement);
				if (limitationsList != null)
					limitations.addAll(limitationsList);

				actionAbility = new ActionAbility(limitations,
						ActionTypes.BROADCAST);
			} else if (actionType == ActionTypes.GOTO.getType()) {
				List<Limitation> limitationsList = limitationTranslator
						.getLimitations(actionAbilityElement);
				if (limitationsList != null)
					limitations.addAll(limitationsList);

				actionAbility = new ActionAbility(limitations, ActionTypes.GOTO);
			} else if (actionType == ActionTypes.RECHARGE.getType()) {
				List<Limitation> limitationsList = limitationTranslator
						.getLimitations(actionAbilityElement);
				if (limitationsList != null)
					limitations.addAll(limitationsList);

				actionAbility = new ActionAbility(limitations,
						ActionTypes.RECHARGE);
			} else if (actionType == ActionTypes.STIGMATIZE.getType()) {
				List<Limitation> limitationsList = limitationTranslator
						.getLimitations(actionAbilityElement);
				if (limitationsList != null)
					limitations.addAll(limitationsList);

				actionAbility = new ActionAbility(limitations,
						ActionTypes.STIGMATIZE);
			} else if (actionType == ActionTypes.TELEPORT.getType()) {
				List<Limitation> limitationsList = limitationTranslator
						.getLimitations(actionAbilityElement);
				if (limitationsList != null)
					limitations.addAll(limitationsList);

				actionAbility = new ActionAbility(limitations,
						ActionTypes.TELEPORT);
			} else if (actionType == ActionTypes.VISIT.getType()) {
				List<Limitation> limitationsList = limitationTranslator
						.getLimitations(actionAbilityElement);
				if (limitationsList != null)
					limitations.addAll(limitationsList);

				actionAbility = new ActionAbility(limitations,
						ActionTypes.VISIT);
			} else
				throw new XMLToObjectTranslationException(
						"Action type not valid.");

			// puts the action ability in the answer
			answer.add(actionAbility);
		}

		// returns the answer
		if (answer.size() > 0)
			return answer;
		else
			return null;
	}
}