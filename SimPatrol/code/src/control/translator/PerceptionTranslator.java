/* PerceptionTranslator.java */

/* The package of this class. */
package control.translator;

/* Imported classes and/or interfaces. */
import java.util.LinkedList;
import java.util.List;
import model.agent.Agent;
import model.graph.Graph;
import model.perception.AgentsPerception;
import model.perception.BroadcastPerception;
import model.perception.GraphPerception;
import model.perception.Perception;
import model.perception.PerceptionTypes;
import model.perception.SelfPerception;
import model.perception.StigmasPerception;
import model.stigma.Stigma;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import control.exception.EdgeNotFoundException;
import control.exception.NodeNotFoundException;

/**
 * Implements a translator that obtains Perception objects from XML source
 * elements.
 * 
 * @see Perception
 * @developer New Perception subclasses must change this class.
 */
public abstract class PerceptionTranslator extends Translator {
	/* Methods. */
	/**
	 * Obtains the perceptions from the given XML element, except for the
	 * AgentsPerception perceptions and for the StigmasPerception perceptions.
	 * 
	 * To obtain perceptions of agents, use getAgentsPerceptions(Element
	 * xml_element, Graph graph).
	 * 
	 * @see AgentsPerception
	 * 
	 * To obtain perceptions of stigmas, use getStigmasPerceptions(Element
	 * xml_element, Graph graph).
	 * @see StigmasPerception
	 * 
	 * @param xml_element
	 *            The XML source containing the perceptions.
	 * @return The perceptions from the XML source.
	 * @throws NodeNotFoundException
	 * @throws EdgeNotFoundException
	 * @developer New Perception subclasses must change this method.
	 */
	public static Perception[] getPerceptions(Element xml_element)
			throws NodeNotFoundException, EdgeNotFoundException {
		// obtains the nodes with the "perception" tag
		NodeList perception_node = xml_element
				.getElementsByTagName("perception");

		// holds all the obtained perceptions
		List<Perception> perceptions = new LinkedList<Perception>();

		// for each perception_node
		for (int i = 0; i < perception_node.getLength(); i++) {
			// obtains the current perception element
			Element perception_element = (Element) perception_node.item(i);

			// the current perception to be obtained
			Perception perception = null;

			// obtains the type of the perception
			int type = Integer
					.parseInt(perception_element.getAttribute("type"));

			// 1st. if the type is of a graph perception
			if (type == PerceptionTypes.GRAPH) {
				Graph[] read_graph = GraphTranslator
						.getGraphs(perception_element);
				if (read_graph.length > 0)
					perception = new GraphPerception(read_graph[0]);
			}

			// 2nd. else, if the perception is a broadcasted message
			else if (type == PerceptionTypes.BROADCAST) {
				String message = perception_element.getAttribute("message");
				perception = new BroadcastPerception(message);
			}

			// developer: new perceptions must add code here

			// adds the current perception to the list of perceptions, if it's
			// valid
			if (perception != null)
				perceptions.add(perception);
		}

		// returns the answer
		return perceptions.toArray(new Perception[0]);
	}

	/**
	 * Obtains the perceptions of agents (AgentsPerception objects) from the
	 * given XML element.
	 * 
	 * @param xml_element
	 *            The XML source containing the perceptions.
	 * @param graph
	 *            The previously perceived graph of the simulation.
	 * @return The perceptions from the XML source.
	 * @throws EdgeNotFoundException
	 * @throws NodeNotFoundException
	 */
	public static AgentsPerception[] getAgentsPerceptions(Element xml_element,
			Graph graph) throws NodeNotFoundException, EdgeNotFoundException {
		// obtains the nodes with the "perception" tag
		NodeList perception_node = xml_element
				.getElementsByTagName("perception");

		// holds all the obtained perceptions
		List<AgentsPerception> perceptions = new LinkedList<AgentsPerception>();

		// for each perception_node
		for (int i = 0; i < perception_node.getLength(); i++) {
			// obtains the current perception element
			Element perception_element = (Element) perception_node.item(i);

			// the current perception to be obtained
			AgentsPerception perception = null;

			// the type of the current perception
			int type = Integer
					.parseInt(perception_element.getAttribute("type"));

			// if the type is an agents perception
			if (type == PerceptionTypes.AGENTS) {
				// tries to obtain the agents from the perception element
				Agent[] agents = AgentTranslator.getAgents(perception_element,
						false, graph);
				if (agents.length > 0)
					perception = new AgentsPerception(agents);
			}

			// adds the current perception to the list of perceptions, if it's
			// valid
			if (perception != null)
				perceptions.add(perception);
		}

		// returns the answer
		return perceptions.toArray(new AgentsPerception[0]);
	}

	/**
	 * Obtains the perceptions of itself (SelfPerception objects) from the given
	 * XML element.
	 * 
	 * @param xml_element
	 *            The XML source containing the perceptions.
	 * @param graph
	 *            The previously perceived graph of the simulation.
	 * @return The perceptions from the XML source.
	 * @throws EdgeNotFoundException
	 * @throws NodeNotFoundException
	 */
	public static SelfPerception[] getSelfPerceptions(Element xml_element,
			Graph graph) throws NodeNotFoundException, EdgeNotFoundException {
		// obtains the nodes with the "perception" tag
		NodeList perception_node = xml_element
				.getElementsByTagName("perception");

		// holds all the obtained perceptions
		List<SelfPerception> perceptions = new LinkedList<SelfPerception>();

		// for each perception_node
		for (int i = 0; i < perception_node.getLength(); i++) {
			// obtains the current perception element
			Element perception_element = (Element) perception_node.item(i);

			// the current perception to be obtained
			SelfPerception perception = null;

			// the type of the current perception
			int type = Integer
					.parseInt(perception_element.getAttribute("type"));

			// if the type is a self perception
			if (type == PerceptionTypes.SELF) {
				// tries to obtain the agent itself from the perception element
				Agent[] agents = AgentTranslator.getAgents(perception_element,
						false, graph);
				if (agents.length > 0)
					perception = new SelfPerception(agents[0]);
			}

			// adds the current perception to the list of perceptions, if it's
			// valid
			if (perception != null)
				perceptions.add(perception);
		}

		// returns the answer
		return perceptions.toArray(new SelfPerception[0]);
	}

	/**
	 * Obtains the perceptions of stigmas (StigmasPerception objects) from the
	 * given XML element.
	 * 
	 * @param xml_element
	 *            The XML source containing the perceptions.
	 * @param graph
	 *            The previously perceived graph of the simulation.
	 * @return The perceptions from the XML source.
	 * @throws EdgeNotFoundException
	 * @throws NodeNotFoundException
	 */
	public static StigmasPerception[] getStigmasPerceptions(
			Element xml_element, Graph graph) throws NodeNotFoundException,
			EdgeNotFoundException {
		// obtains the nodes with the "perception" tag
		NodeList perception_node = xml_element
				.getElementsByTagName("perception");

		// holds all the obtained perceptions
		List<StigmasPerception> perceptions = new LinkedList<StigmasPerception>();

		// for each perception_node
		for (int i = 0; i < perception_node.getLength(); i++) {
			// obtains the current perception element
			Element perception_element = (Element) perception_node.item(i);

			// the current perception to be obtained
			StigmasPerception perception = null;

			// the type of the current perception
			int type = Integer
					.parseInt(perception_element.getAttribute("type"));

			// if the type is a self perception
			if (type == PerceptionTypes.STIGMAS) {
				// tries to obtain the stigmas from the perception element
				Stigma[] stigmas = StigmaTranslator.getStigmas(
						perception_element, graph);
				if (stigmas.length > 0)
					perception = new StigmasPerception(stigmas);
			}

			// adds the current perception to the list of perceptions, if it's
			// valid
			if (perception != null)
				perceptions.add(perception);
		}

		// returns the answer
		return perceptions.toArray(new StigmasPerception[0]);
	}
}