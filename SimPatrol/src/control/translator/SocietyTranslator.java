/* SocietyTranslator.java */

/* The package of this class. */
package control.translator;

/* Imported classes and/or interfaces. */
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import control.exception.EdgeNotFoundException;
import control.exception.VertexNotFoundException;
import model.agent.Agent;
import model.agent.ClosedSociety;
import model.agent.OpenSociety;
import model.agent.PerpetualAgent;
import model.agent.SeasonalAgent;
import model.agent.Society;
import model.graph.Graph;

/**
 * Implements a translator that obtains Society objects from XML source
 * elements.
 * 
 * @see Society
 */
public abstract class SocietyTranslator extends Translator {
	/* Methods */
	/**
	 * Obtains the societies from the given XML element.
	 * 
	 * @param xml_element
	 *            The XML source containing the societies.
	 * @param graph
	 *            The graph of the simulation.
	 * @return The societies from the XML source.
	 * @throws EdgeNotFoundException
	 * @throws VertexNotFoundException
	 */
	public static Society[] getSocieties(Element xml_element, Graph graph)
			throws VertexNotFoundException, EdgeNotFoundException {
		// obtains the nodes with the "society" tag
		NodeList society_nodes = xml_element.getElementsByTagName("society");

		// the answer of the method
		Society[] answer = new Society[society_nodes.getLength()];

		// for all the occurrences
		for (int i = 0; i < answer.length; i++) {
			// obtains the current society element
			Element society_element = (Element) society_nodes.item(i);

			// obtains its data
			String id = society_element.getAttribute("id");
			String label = society_element.getAttribute("label");
			String str_is_closed = society_element.getAttribute("is_closed");

			// decides if the society is closed or not
			boolean is_closed = true;
			if (str_is_closed.length() > 0)
				is_closed = Boolean.parseBoolean(str_is_closed);

			// obtains the agents
			boolean are_perpetual_agents = true;
			if (!is_closed)
				are_perpetual_agents = false;
			Agent[] agents = AgentTranslator.getAgents(society_element,
					are_perpetual_agents, graph);

			// creates the society (closed or open)
			Society society = null;
			if (is_closed) {
				// agents as perpetual agents
				PerpetualAgent[] perpetual_agents = new PerpetualAgent[agents.length];
				for (int j = 0; j < perpetual_agents.length; j++)
					perpetual_agents[j] = (PerpetualAgent) agents[j];

				// new closed society
				society = new ClosedSociety(label, perpetual_agents);
			} else {
				// agents as seasonal agents
				SeasonalAgent[] seasonal_agents = new SeasonalAgent[agents.length];
				for (int j = 0; j < seasonal_agents.length; j++)
					seasonal_agents[j] = (SeasonalAgent) agents[j];

				// new open society
				society = new OpenSociety(label, seasonal_agents);
			}

			// configures the society and puts in the answer
			society.setObjectId(id);
			answer[i] = society;
		}

		// returns the answer
		return answer;
	}
}