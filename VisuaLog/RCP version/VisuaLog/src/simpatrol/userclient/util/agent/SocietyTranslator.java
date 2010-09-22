/* SocietyTranslator.java */

/* The package of this class. */
package simpatrol.userclient.util.agent;

/* Imported classes and/or interfaces. */
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import simpatrol.userclient.util.Translator;
import simpatrol.userclient.util.graph.Graph;

/**
 * Implements a translator that obtains Society objects from XML source
 * elements.
 * 
 * @see Society
 */
public abstract class SocietyTranslator extends Translator{
	/* Methods */
	
	
	/**
	 * Obtains the societies from the given XML element.
	 * 
	 * @param xml_element
	 *            The XML source containing the societies.
	 * @param graph
	 *            The graph of the simulation.
	 * @return The societies from the XML source.
	 */
	public static Society[] getSocieties(Element xml_element, Graph graph) {
		// obtains the nodes with the "society" tag
		NodeList society_nodes = xml_element.getElementsByTagName("society");

		// the answer of the method
		Society[] answer = new Society[society_nodes.getLength()];

		// for all the ocurrences
		for (int i = 0; i < answer.length; i++) {
			// obtains the current society element
			Element society_element = (Element) society_nodes.item(i);

			// obtains its data
			String id = society_element.getAttribute("id");
			String label = society_element.getAttribute("label");

			// obtains the agents
			Agent[] agents = AgentTranslator.getAgents(society_element, graph);

			// creates the society (closed or open)
			Society society = new Society(label, agents);


			// configures the society and puts in the answer
			society.setObjectId(id);
			answer[i] = society;
		}

		// returns the answer
		return answer;
	}
}