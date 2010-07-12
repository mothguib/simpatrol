/* SocietyTranslator.java (2.0) */
package br.org.simpatrol.server.model.agent;

/* Imported classes and/or interfaces. */
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import br.org.simpatrol.server.model.graph.Graph;
import br.org.simpatrol.server.util.translator.XMLToObjectTranslationException;
import br.org.simpatrol.server.util.translator.XMLToObjectTranslator;

/**
 * Implements a translator that obtains {@link Society} objects from XML source
 * elements.
 * 
 * @author Daniel Henriques Moreira (dhenriques@gmail.com)
 */
public class SocietyTranslator extends XMLToObjectTranslator {
	/* Methods */
	/**
	 * Obtains {@link Society} objects from the given XML element.
	 * 
	 * @param xmlElement
	 *            The XML source containing the societies.
	 * @param graph
	 *            The graph of the simulation.
	 * 
	 * @return The societies obtained from the XML source.
	 * @throws XMLToObjectTranslationException
	 *             See the message for details.
	 */
	public List<Society> getSocieties(Element xmlElement, Graph graph)
			throws XMLToObjectTranslationException {
		// obtains the nodes with the "society" tag
		NodeList societyNodes = xmlElement.getElementsByTagName("society");

		// the answer of the method
		List<Society> answer = new ArrayList<Society>(societyNodes.getLength());

		// a translator of "agent" objects
		AgentTranslator agentTranslator = new AgentTranslator();

		// for all the occurrences
		for (int i = 0; i < societyNodes.getLength(); i++) {
			// obtains the current society element
			Element societyElement = (Element) societyNodes.item(i);

			// obtains its data
			String id = societyElement.getAttribute("id");
			String label = societyElement.getAttribute("label");
			String strIsClosed = societyElement.getAttribute("is_closed");

			// decides if the society is closed or not
			boolean isClosed = true;
			if (strIsClosed.length() > 0)
				isClosed = Boolean.parseBoolean(strIsClosed);

			// obtains the agents
			boolean arePerpetualAgents = true;
			if (!isClosed)
				arePerpetualAgents = false;
			List<Agent> agents = agentTranslator.getAgents(societyElement,
					graph, arePerpetualAgents);

			// creates the society (closed or open)
			Society society = null;
			if (isClosed) {
				// agents as perpetual agents
				Set<PerpetualAgent> perpetualAgents = new HashSet<PerpetualAgent>(
						agents.size());
				for (Agent agent : agents)
					perpetualAgents.add((PerpetualAgent) agent);

				// new closed society
				society = new ClosedSociety(id, label, perpetualAgents);
			} else {
				// agents as seasonal agents
				Set<SeasonalAgent> seasonalAgents = new HashSet<SeasonalAgent>(
						agents.size());
				for (Agent agent : agents)
					seasonalAgents.add((SeasonalAgent) agent);

				// new open society
				society = new OpenSociety(id, label, seasonalAgents);
			}

			// adds the created society to the answer
			answer.add(society);
		}

		// returns the answer
		return answer;
	}
}