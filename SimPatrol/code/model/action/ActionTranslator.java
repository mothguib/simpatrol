/* ActionTranslator.java (2.0) */
package br.org.simpatrol.server.model.action;

/* Imported classes and/or interfaces. */
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import br.org.simpatrol.server.model.agent.Agent;
import br.org.simpatrol.server.model.graph.Edge;
import br.org.simpatrol.server.model.graph.Graph;
import br.org.simpatrol.server.model.graph.Vertex;
import br.org.simpatrol.server.model.stigma.Stigma;
import br.org.simpatrol.server.model.stigma.StigmaTranslator;
import br.org.simpatrol.server.util.translator.XMLToObjectTranslationException;
import br.org.simpatrol.server.util.translator.XMLToObjectTranslator;

/**
 * Implements a translator that obtains {@link Action} objects from XML source
 * elements.
 * 
 * @author Daniel Henriques Moreira (dhenriques@gmail.com)
 */
public class ActionTranslator extends XMLToObjectTranslator {
	/* Methods. */
	/**
	 * Obtains the actions from the given XML element, except for the
	 * {@link TeleportAction} actions, for the {@link GoToAction} actions, and
	 * for the {@link StigmatizeAction} actions.
	 * 
	 * To obtain {@link GoToAction} objects, or {@link TeleportAction} objects,
	 * or {@link StigmatizeAction} objects, use
	 * {@link #getActions(Element, Graph, Agent)}.
	 * 
	 * @param xmlElement
	 *            The XML source containing the actions.
	 * 
	 * @return The actions from the XML source.
	 * @throws XMLToObjectTranslationException
	 *             See the message for details.
	 */
	public List<? extends Action> getActions(Element xmlElement)
			throws XMLToObjectTranslationException {
		return this.getActions(xmlElement, null, null);
	}

	/**
	 * Obtains the actions from the given XML element, except for the
	 * {@link StigmatizeAction} actions.
	 * 
	 * To obtain {@link StigmatizeAction} objects use
	 * {@link #getActions(Element, Agent))} or
	 * {@link #getActions(Element, Graph, Agent)}.
	 * 
	 * @param xmlElement
	 *            The XML source containing the actions.
	 * @param graph
	 *            The {@link Graph} object of the simulation.
	 * 
	 * @return The actions from the XML source.
	 * @throws XMLToObjectTranslationException
	 *             See the message for details.
	 */
	public List<? extends Action> getActions(Element xmlElement, Graph graph)
			throws XMLToObjectTranslationException {
		return this.getActions(xmlElement, graph, null);
	}

	/**
	 * Obtains the actions from the given XML element, except for the
	 * {@link GoToAction} actions, and for the {@link TeleportAction} actions.
	 * 
	 * To obtain {@link GoToAction} objects, or {@link TeleportAction} objects,
	 * use {@link #getActions(Element, Graph)} or
	 * {@link #getActions(Element, Graph, Agent)}.
	 * 
	 * @param xmlElement
	 *            The XML source containing the actions.
	 * @param agent
	 *            The {@link Agent} object responsible for the obtained actions.
	 * 
	 * @return The actions from the XML source.
	 * @throws XMLToObjectTranslationException
	 *             See the message for details.
	 */
	public List<? extends Action> getActions(Element xmlElement, Agent agent)
			throws XMLToObjectTranslationException {
		return this.getActions(xmlElement, null, agent);
	}

	/**
	 * Obtains the actions from the given XML element.
	 * 
	 * @param xmlElement
	 *            The XML source containing the actions.
	 * @param graph
	 *            The {@link Graph} object of the simulation.
	 * @param agent
	 *            The {@link Agent} object responsible for the obtained actions.
	 * 
	 * @return The actions from the XML source.
	 * @throws XMLToObjectTranslationException
	 *             See the message for details.
	 */
	public List<? extends Action> getActions(Element xmlElement, Graph graph,
			Agent agent) throws XMLToObjectTranslationException {
		if (graph != null)
			try {
				// tries to obtain goto actions
				return this.getGoToActions(xmlElement, graph);
			} catch (XMLToObjectTranslationException exception1) {
				exception1.printStackTrace();

				try {
					// if failed for the type of the action, tries to obtain
					// teleport actions
					return this.getTeleportActions(xmlElement, graph);
				} catch (XMLToObjectTranslationException exception2) {
					exception2.printStackTrace();

					// if failed again for the type of the action, tries to
					// obtain stigmatize actions
					if (agent != null)
						try {
							return this.getStigmatizeActions(xmlElement, agent);
						} catch (XMLToObjectTranslationException exception3) {
							exception3.printStackTrace();

							// if failed again for the type of the action, tries
							// to obtain other types of actions
							return this.getSimpleActions(xmlElement);
						}
					else
						return this.getSimpleActions(xmlElement);
				}
			}
		else if (agent != null)
			try {
				// tries to obtain stigmatize actions
				return this.getStigmatizeActions(xmlElement, agent);
			} catch (XMLToObjectTranslationException exception) {
				exception.printStackTrace();

				// if failed for the type of the action, tries
				// to obtain other types of actions
				return this.getSimpleActions(xmlElement);
			}
		else
			return this.getSimpleActions(xmlElement);
	}

	/**
	 * Obtains the actions from the given XML element, except for the
	 * {@link TeleportAction} actions, for the {@link GoToAction} actions, and
	 * for the {@link StigmatizeAction} actions.
	 * 
	 * To obtain {@link TeleportAction} objects, use
	 * {@link #getTeleportActions(Element, Graph)}.
	 * 
	 * To obtain {@link GoToAction} objects, use
	 * {@link #getGoToActions(Element, Graph)}.
	 * 
	 * To obtain {@link StigmatizeAction} objects, use
	 * {@link #getStigmatizeActions(Element, Agent)}.
	 * 
	 * @param xmlElement
	 *            The XML source containing the actions.
	 * 
	 * @return The actions from the XML source.
	 * @throws XMLToObjectTranslationException
	 *             See the message for details.
	 */
	private List<Action> getSimpleActions(Element xmlElement)
			throws XMLToObjectTranslationException {
		// obtains the nodes with the "action" tag
		NodeList actionNode = xmlElement.getElementsByTagName("action");

		// holds all the obtained actions
		List<Action> answer = new ArrayList<Action>(actionNode.getLength());

		// for each action node
		for (int i = 0; i < actionNode.getLength(); i++) {
			// obtains the current action element
			Element actionElement = (Element) actionNode.item(i);

			// the current action to be obtained
			Action action = null;

			// obtains the type of the action
			byte type = Byte.parseByte(actionElement.getAttribute("type"));

			// 1st. if the type is of a visit action
			if (type == ActionTypes.VISIT.getType()) {
				action = new VisitAction();
			}

			// 2nd. else, if the type is of a broadcast
			else if (type == ActionTypes.BROADCAST.getType()) {
				// obtains the message, and the depth it must reach
				String message = actionElement.getAttribute("message");
				int messageDepth = Integer.parseInt(actionElement
						.getAttribute("message_depth"));

				action = new BroadcastAction(message, messageDepth);
			}

			// 3rd. else, if the type is of a recharge action
			else if (type == ActionTypes.RECHARGE.getType()) {
				// obtains the data of such action
				double stamina = Double.parseDouble(actionElement
						.getAttribute("stamina"));
				String strInitialSpeed = actionElement
						.getAttribute("initial_speed");
				String strAcceleration = actionElement
						.getAttribute("acceleration");

				action = new RechargeAction(stamina);

				// if an initial speed was found in the XML source
				if (strInitialSpeed.length() > 0)
					((RechargeAction) action).setInitialSpeed(Double
							.parseDouble(strInitialSpeed));

				// if an acceleration was found in the XML source
				if (strAcceleration.length() > 0)
					((RechargeAction) action).setAcceleration(Double
							.parseDouble(strAcceleration));
			}

			// 4th. else, if the type is of a atomic recharge action
			else if (type == ActionTypes.ATOMIC_RECHARGE.getType()) {
				double stamina = Double.parseDouble(actionElement
						.getAttribute("stamina"));
				action = new AtomicRechargeAction(stamina);
			}

			// else, throws an action type not valid exception
			else
				throw new XMLToObjectTranslationException(
						"Action type not valid.");

			// adds the current action to the list of actions, if it's valid
			answer.add(action);
		}

		// returns the answer
		if (answer.size() > 0)
			return answer;
		else
			return null;
	}

	/**
	 * Obtains {@link TeleportAction} objects from the given XML element.
	 * 
	 * @param xmlElement
	 *            The XML source containing the {@link TeleportAction} objects.
	 * @param graph
	 *            The {@link Graph} object of the simulation.
	 * 
	 * @return The {@link TeleportAction} objects obtained from the XML source.
	 * @throws XMLToObjectTranslationException
	 *             See the message for details.
	 */
	private List<TeleportAction> getTeleportActions(Element xmlElement,
			Graph graph) throws XMLToObjectTranslationException {
		// obtains the nodes with the "action" tag
		NodeList actionNode = xmlElement.getElementsByTagName("action");

		// holds all the obtained actions
		List<TeleportAction> answer = new ArrayList<TeleportAction>(actionNode
				.getLength());

		// for each action node
		for (int i = 0; i < actionNode.getLength(); i++) {
			// obtains the current action element
			Element actionElement = (Element) actionNode.item(i);

			// obtains the data from the action
			byte type = Byte.parseByte(actionElement.getAttribute("type"));
			String vertexId = actionElement.getAttribute("vertex_id");
			String edgeId = actionElement.getAttribute("edge_id");
			String strElapsedLength = actionElement
					.getAttribute("elapsed_length");

			// if the type is not a teleport action, throws an exception
			if (type != ActionTypes.TELEPORT.getType())
				throw new XMLToObjectTranslationException(
						"The expected action type was "
								+ ActionTypes.TELEPORT.getType() + ", but "
								+ type + " was found.");

			// with the obtained vertex id, tries to find the correspondent
			// vertex from the graph
			Vertex vertex = null;
			Set<Vertex> vertexes = graph.getVertexes();
			for (Vertex currentVertex : vertexes)
				if (currentVertex.getId().equals(vertexId)) {
					vertex = currentVertex;
					break;
				}

			// if no valid vertex was found, throws an exception
			if (vertex == null)
				throw new XMLToObjectTranslationException(
						"There is no vertex with the given vertex_id.");

			// tries to obtain an edge with the eventually given edge id
			Edge edge = null;
			double elapsedLength = 0;

			if (edgeId.length() > 0) {
				Set<Edge> edges = graph.getEdges();
				for (Edge currentEdge : edges)
					if (currentEdge.getId().equals(edgeId)) {
						edge = currentEdge;

						// tries to obtain the elapsed length on the found edge
						if (strElapsedLength.length() > 0) {
							elapsedLength = Double
									.parseDouble(strElapsedLength);

							// if the obtained elapsed length is greater than
							// the length of the obtained edge, throws an
							// exception
							if (elapsedLength > edge.getLength())
								throw new XMLToObjectTranslationException(
										"The elapsed_length attribute is greater than the length of the edge with the given edge_id.");
						}
						// else, throws an exception
						else
							throw new XMLToObjectTranslationException(
									"There is no valid elapsed_length attribute.");

						break;
					}

				// if no valid edge was found, throws an exception
				if (edge == null)
					throw new XMLToObjectTranslationException(
							"There is no edge with the given edge_id.");
			}

			// the obtained action
			TeleportAction action = new TeleportAction(vertex, edge,
					elapsedLength);

			// adds the current action to the list of actions
			answer.add(action);
		}

		// returns the answer
		if (answer.size() > 0)
			return answer;
		else
			return null;
	}

	/**
	 * Obtains {@link GoToAction} objects from the given XML element.
	 * 
	 * @param xmlElement
	 *            The XML source containing the {@link GoToAction} objects.
	 * @param graph
	 *            The {@link Graph} object of the simulation.
	 * 
	 * @return The {@link GoToAction} objects obtained from the XML source.
	 * @throws XMLToObjectTranslationException
	 *             See the message for details.
	 */
	private List<GoToAction> getGoToActions(Element xmlElement, Graph graph)
			throws XMLToObjectTranslationException {
		// obtains the nodes with the "action" tag
		NodeList actionNode = xmlElement.getElementsByTagName("action");

		// holds all the obtained actions
		List<GoToAction> answer = new ArrayList<GoToAction>(actionNode
				.getLength());

		// for each action node
		for (int i = 0; i < actionNode.getLength(); i++) {
			// obtains the current action element
			Element actionElement = (Element) actionNode.item(i);

			// obtains the data from the action
			byte type = Byte.parseByte(actionElement.getAttribute("type"));
			String goalVertexId = actionElement.getAttribute("vertex_id");
			String strInitialSpeed = actionElement
					.getAttribute("initial_speed");
			String strAcceleration = actionElement.getAttribute("acceleration");

			// if the type is not a goto action, throws an exception
			if (type != ActionTypes.GOTO.getType())
				throw new XMLToObjectTranslationException(
						"The expected action type was "
								+ ActionTypes.GOTO.getType() + ", but " + type
								+ " was found.");

			// with the obtained vertex id, tries to find the correspondent
			// vertex from the graph
			Vertex goalVertex = null;
			Set<Vertex> vertexes = graph.getVertexes();
			for (Vertex currentVertex : vertexes)
				if (currentVertex.getId().equals(goalVertexId)) {
					goalVertex = currentVertex;
					break;
				}

			// if no valid vertex was found, throws an exception
			if (goalVertex == null)
				throw new XMLToObjectTranslationException(
						"There is no vertex with the given vertex_id.");

			// the obtained action
			GoToAction action = new GoToAction(goalVertex);

			// if an initial speed was found in the XML source
			if (strInitialSpeed.length() > 0)
				// configures it on the obtained action
				action.setInitialSpeed(Double.parseDouble(strInitialSpeed));

			// if an acceleration was found in the XML source
			if (strAcceleration.length() > 0)
				// configures it on the obtained action
				action.setAcceleration(Double.parseDouble(strAcceleration));

			// adds the current action to the list of actions
			answer.add(action);
		}

		// returns the answer
		if (answer.size() > 0)
			return answer;
		else
			return null;
	}

	/**
	 * Obtains {@link StigmatizeAction} objects from the given XML element.
	 * 
	 * @param xmlElement
	 *            The XML source containing the {@link StigmatizeAction}
	 *            objects.
	 * @param agent
	 *            The agent responsible for the {@link StigmatizeAction} object.
	 * 
	 * @return The {@link StigmatizeAction} objects obtained from the XML
	 *         source.
	 * @throws XMLToObjectTranslationException
	 *             See the message for details.
	 */
	private List<StigmatizeAction> getStigmatizeActions(Element xmlElement,
			Agent agent) throws XMLToObjectTranslationException {
		// obtains the nodes with the "action" tag
		NodeList actionNode = xmlElement.getElementsByTagName("action");

		// holds all the obtained actions
		List<StigmatizeAction> answer = new ArrayList<StigmatizeAction>(
				actionNode.getLength());

		// the translator of stigmas
		StigmaTranslator stigmaTranslator = new StigmaTranslator();

		// for each action node
		for (int i = 0; i < actionNode.getLength(); i++) {
			// obtains the current action element
			Element actionElement = (Element) actionNode.item(i);

			// the current action to be obtained
			StigmatizeAction action = null;

			// the type of the current action
			byte type = Byte.parseByte(actionElement.getAttribute("type"));

			// if the type is a stigmatize ation
			if (type == ActionTypes.STIGMATIZE.getType()) {
				// tries to obtain the stigma from the action element
				List<Stigma> stigmas = stigmaTranslator
						.getStigmasFromStigmatizeAction(actionElement, agent);
				action = new StigmatizeAction(stigmas.get(0));
			}
			// else, throws an exception
			else
				throw new XMLToObjectTranslationException(
						"The expected action type was "
								+ ActionTypes.STIGMATIZE.getType() + ", but "
								+ type + " was found.");

			// adds the current perception to the list of perceptions
			answer.add(action);
		}

		// returns the answer
		if (answer.size() > 0)
			return answer;
		else
			return null;
	}
}