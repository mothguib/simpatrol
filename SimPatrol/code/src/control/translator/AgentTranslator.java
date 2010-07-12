/* AgentTranslator.java */

/* The package of this class. */
package control.translator;

/* Imported classes and/or interfaces. */
import model.agent.Agent;
import model.agent.PerpetualAgent;
import model.agent.SeasonalAgent;
import model.etpd.EventTimeProbabilityDistribution;
import model.graph.Edge;
import model.graph.Graph;
import model.graph.Node;
import model.permission.ActionPermission;
import model.permission.PerceptionPermission;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import control.exception.EdgeNotFoundException;
import control.exception.NodeNotFoundException;

/**
 * Implements a translator that obtains Agent objects from XML source elements.
 * 
 * @see Agent
 */
public abstract class AgentTranslator extends Translator {
	/**
	 * Obtains the agents from the given XML element.
	 * 
	 * @param xml_element
	 *            The XML source containing the agents.
	 * @param are_perpetual_agents
	 *            TRUE, if the society of the agents is closed, FALSE if not.
	 * @param graph
	 *            The graph of the simulation.
	 * @return The agents from the XML source.
	 * @throws NodeNotFoundException
	 * @throws EdgeNotFoundException
	 */
	public static Agent[] getAgents(Element xml_element,
			boolean are_perpetual_agents, Graph graph)
			throws NodeNotFoundException, EdgeNotFoundException {
		// obtains the nodes and edges of the graph
		Node[] nodes = graph.getNodes();
		Edge[] edges = graph.getEdges();

		// obtains the nodes with the "agent" tag
		NodeList agent_nodes = xml_element.getElementsByTagName("agent");

		// the answer of the method
		Agent[] answer = new Agent[agent_nodes.getLength()];

		// for all the occurrences
		for (int i = 0; i < answer.length; i++) {
			// obtains the current agent element
			Element agent_element = (Element) agent_nodes.item(i);

			// obtains the data
			String id = agent_element.getAttribute("id");
			String label = agent_element.getAttribute("label");
			String str_state = agent_element.getAttribute("state");
			String node_id = agent_element.getAttribute("node_id");
			String edge_id = agent_element.getAttribute("edge_id");
			String str_elapsed_length = agent_element
					.getAttribute("elapsed_length");
			String str_stamina = agent_element.getAttribute("stamina");
			String str_max_stamina = agent_element.getAttribute("max_stamina");

			// finds the node of the agent
			Node node = null;
			for (int j = 0; j < nodes.length; j++)
				if (nodes[j].getObjectId().equals(node_id)) {
					node = nodes[j];
					break;
				}

			// if no valid node was found, throws exception
			if (node == null)
				throw new NodeNotFoundException();

			// finds the eventual edge of the agent
			Edge edge = null;
			if (edge_id.length() > 0) {
				for (int j = 0; j < edges.length; j++)
					if (edges[j].getObjectId().equals(edge_id)) {
						edge = edges[j];
						break;
					}

				// if no valid edge was found, throws exception
				if (edge == null)
					throw new EdgeNotFoundException();
			}

			// instantiates the new agent (perpetual or seasonal)
			Agent agent = null;
			if (are_perpetual_agents) {
				// new perpetual agent
				agent = new PerpetualAgent(label, node,
						getAllowedPerceptions(agent_element),
						getAllowedActions(agent_element));
			} else {
				// obtains the eventual death time pd
				EventTimeProbabilityDistribution[] read_death_tpd = EventTimeProbabilityDistributionTranslator
						.getEventTimeProbabilityDistribution(agent_element);
				EventTimeProbabilityDistribution death_tpd = null;
				if (read_death_tpd.length > 0)
					death_tpd = read_death_tpd[0];

				// new seasonal agent
				agent = new SeasonalAgent(label, node,
						getAllowedPerceptions(agent_element),
						getAllowedActions(agent_element), death_tpd);
			}

			// configures the new agent...
			// id configuration
			agent.setObjectId(id);

			// state configuration
			if (str_state.length() > 0)
				agent.setAgentState(Integer.parseInt(str_state));

			// edge and elapsed length configuration
			if (edge != null) {
				double elapsed_length = 0;
				if (str_elapsed_length.length() > 0)
					elapsed_length = Double.parseDouble(str_elapsed_length);

				agent.setEdge(edge, elapsed_length);
			}

			// stamina configuration
			if (str_max_stamina.length() > 0)
				agent.setMax_stamina(Double.parseDouble(str_max_stamina));
			if (str_stamina.length() > 0)
				agent.setStamina(Double.parseDouble(str_stamina));

			// puts on the answer
			answer[i] = agent;
		}

		// returns the answer
		return answer;
	}

	/**
	 * Obtains the allowed perceptions from the given XML element.
	 * 
	 * @param xml_element
	 *            The XML source containing the perception permissions.
	 * @return The allowed perceptions from the XML source.
	 */
	private static PerceptionPermission[] getAllowedPerceptions(
			Element xml_element) {
		// obtains the nodes with the "allowed_perception" tag
		NodeList allowed_perception_nodes = xml_element
				.getElementsByTagName("allowed_perception");

		// the answer of the method
		PerceptionPermission[] answer = new PerceptionPermission[allowed_perception_nodes
				.getLength()];

		// for all the occurrences
		for (int i = 0; i < answer.length; i++) {
			// obtains the current allowed_perception element
			Element allowed_perception_element = (Element) allowed_perception_nodes
					.item(i);

			// obtains its data
			int perception_type = Integer.parseInt(allowed_perception_element
					.getAttribute("type"));

			// instantiates the new allowed_perception
			PerceptionPermission allowed_perception = new PerceptionPermission(
					LimitationTranslator
							.getLimitations(allowed_perception_element),
					perception_type);

			// puts on the answer
			answer[i] = allowed_perception;
		}

		// returns the answer
		return answer;
	}

	/**
	 * Obtains the allowed actions from the given XML element.
	 * 
	 * @param xml_element
	 *            The XML source containing the action permissions.
	 * @return The allowed actions from the XML source.
	 */
	private static ActionPermission[] getAllowedActions(Element xml_element) {
		// obtains the nodes with the "allowed_action" tag
		NodeList allowed_action_nodes = xml_element
				.getElementsByTagName("allowed_action");

		// the answer of the method
		ActionPermission[] answer = new ActionPermission[allowed_action_nodes
				.getLength()];

		// for all the occurrences
		for (int i = 0; i < answer.length; i++) {
			// obtains the current allowed_perception element
			Element allowed_action_element = (Element) allowed_action_nodes
					.item(i);

			// obtains its data
			int action_type = Integer.parseInt(allowed_action_element
					.getAttribute("type"));

			// instantiates the new allowed_action
			ActionPermission allowed_action = new ActionPermission(
					LimitationTranslator.getLimitations(allowed_action_element),
					action_type);

			// puts on the answer
			answer[i] = allowed_action;
		}

		// returns the answer
		return answer;
	}
}