/* AgentTranslator.java */

/* The package of this class. */
package util.agent;

/* Imported classes and/or interfaces. */
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import util.Translator;
import util.graph.Edge;
import util.graph.Graph;
import util.graph.Node;

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
	 */
	public static Agent[] getAgents(Element xml_element, Graph graph) {
		// obtains the vertexes and edges of the graph
		Node[] vertexes = graph.getNodes();
		Edge[] edges = graph.getEdges();

		// obtains the nodes with the "agent" tag
		NodeList agent_nodes = xml_element.getElementsByTagName("agent");

		// the answer of the method
		Agent[] answer = new Agent[agent_nodes.getLength()];

		// for all the ocurrences
		for (int i = 0; i < answer.length; i++) {
			// obtains the current agent element
			Element agent_element = (Element) agent_nodes.item(i);

			// obtains the data
			String id = agent_element.getAttribute("id");
			String label = agent_element.getAttribute("label");
			String str_state = agent_element.getAttribute("state");
			String vertex_id = agent_element.getAttribute("node_id");
			String edge_id = agent_element.getAttribute("edge_id");
			String str_elapsed_length = agent_element
					.getAttribute("elapsed_length");

			// finds the vertex of the agent
			Node vertex = null;
			for (int j = 0; j < vertexes.length; j++)
				if (vertexes[j].getObjectId().equals(vertex_id)) {
					vertex = vertexes[j];
					break;
				}

			// finds the eventual edge of the agent
			Edge edge = null;
			if (edge_id.length() > 0)
				for (int j = 0; j < edges.length; j++)
					if (edges[j].getObjectId().equals(edge_id)) {
						edge = edges[j];
						break;
					}

			// instatiates the new agent (perpetual or seasonal)
			Agent agent = new Agent(label, vertex);

			// configures the new agent...
			// id configuration
			agent.setObjectId(id);

			// state configuration
			if (str_state.length() > 0)
				agent.setState(Integer.parseInt(str_state));

			// edge and elapsed length configuration
			if (edge != null) {
				int elapsed_length = 0;
				if (str_elapsed_length.length() > 0)
					elapsed_length = Integer.parseInt(str_elapsed_length);

				agent.setEdge(edge, elapsed_length);
			}

			// puts on the answer
			answer[i] = agent;
		}

		// returns the answer
		return answer;
	}
	
}


