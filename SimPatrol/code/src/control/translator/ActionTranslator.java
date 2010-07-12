/* ActionTranslator.java */

/* The package of this class. */
package control.translator;

/* Imported classes and/or interfaces. */
import java.io.IOException;
import org.w3c.dom.Element;
import org.xml.sax.SAXException;
import control.exception.EdgeNotFoundException;
import control.exception.NodeNotFoundException;
import model.action.Action;
import model.action.ActionTypes;
import model.action.AtomicRechargeAction;
import model.action.BroadcastAction;
import model.action.BroadcastSocietyAction;
import model.action.GoToAction;
import model.action.RechargeAction;
import model.action.StigmatizeAction;
import model.action.TeleportAction;
import model.action.VisitAction;
import model.graph.Edge;
import model.graph.Graph;
import model.graph.Node;

/**
 * Implements a translator that obtains Action objects from XML source elements.
 * 
 * @see Action
 * @developer New Action subclasses must change this class.
 */
public abstract class ActionTranslator extends Translator {
	/* Methods. */
	/**
	 * Obtains the action from the given XML string, except for an eventual
	 * TeleportAction action and for an eventual GoToAction action.
	 * 
	 * To obtain actions of teleport, use getTeleportAction(String xml_string,
	 * Graph graph).
	 * 
	 * @see TeleportAction
	 * 
	 * To obtain actions of movement through the graph, use getGoToAction(String
	 * xml_string, Graph graph).
	 * @see GoToAction
	 * 
	 * @param xml_string
	 *            The XML source containing the actions.
	 * @return The action from the XML source.
	 * @throws IOException
	 * @throws SAXException
	 * @developer New Action subclasses must change this method.
	 */
	public static Action getAction(String xml_string) throws SAXException,
			IOException {
		// parses the xml string into a xml element
		Element action_element = parseString(xml_string);

		// obtains the type of the current action
		int action_type = Integer.parseInt(action_element.getAttribute("type"));

		// switches the action type
		switch (action_type) {
		// if it is a visit action, returns it
		case ActionTypes.VISIT: {
			return new VisitAction();
		}

			// if it is a broadcast action
		case ActionTypes.BROADCAST: {
			// obtains the message
			String message = action_element.getAttribute("message");

			// obtains the depth of the message
			int message_depth = -1;
			String str_message_depth = action_element
					.getAttribute("message_depth");
			if (str_message_depth.length() > 0)
				message_depth = Integer.parseInt(str_message_depth);

			// returns the action
			return new BroadcastAction(message, message_depth);
		}
		// if it is a broadcast action
		case ActionTypes.BROADCAST_SOCIETY: {
			// obtains the message
			String message = action_element.getAttribute("message");

			// obtains the depth of the message
			int message_depth = -1;
			String str_message_depth = action_element
					.getAttribute("message_depth");
			if (str_message_depth.length() > 0)
				message_depth = Integer.parseInt(str_message_depth);

			// returns the action
			return new BroadcastSocietyAction(message, message_depth);
		}

			// if it is a stigmatize action, returns it
		case ActionTypes.STIGMATIZE: {
			return new StigmatizeAction();
		}

			// if it is an atomic recharge action
		case ActionTypes.ATOMIC_RECHARGE: {
			// obtains the stamina value to be added to the agent
			double stamina = 0;
			String str_stamina = action_element.getAttribute("stamina");
			if (str_stamina.length() > 0)
				stamina = Double.parseDouble(str_stamina);

			// return the action
			return new AtomicRechargeAction(stamina);
		}

			// if it is a recharge action
		case ActionTypes.RECHARGE: {
			// obtains the stamina value to be added to the agent
			double stamina = 0;
			String str_stamina = action_element.getAttribute("stamina");
			if (str_stamina.length() > 0)
				stamina = Double.parseDouble(str_stamina);

			// return the action
			return new RechargeAction(stamina);
		}

			// developer: new actions must add code here
		}

		// default answer
		return null;
	}

	/**
	 * Obtains the action of teleport from the given XML string.
	 * 
	 * @param xml_string
	 *            The XML source containing the action.
	 * @param graph
	 *            The graph of the simulation.
	 * @return The action of teleport from the XML source.
	 * @throws IOException
	 * @throws SAXException
	 * @throws NodeNotFoundException
	 * @throws EdgeNotFoundException
	 */
	public static TeleportAction getTeleportAction(String xml_string,
			Graph graph) throws SAXException, IOException,
			NodeNotFoundException, EdgeNotFoundException {
		// parses the string in order to obtain the "action" element
		Element action_element = parseString(xml_string);

		// verifies the type of the action
		int type = Integer.parseInt(action_element.getAttribute("type"));
		if (type != ActionTypes.TELEPORT)
			return null;

		// obtains the data from the action
		String node_id = action_element.getAttribute("node_id");
		String edge_id = action_element.getAttribute("edge_id");
		String str_elapsed_length = action_element
				.getAttribute("elapsed_length");

		// tries to find the correspondent node from the graph,
		// given its id
		Node node = null;
		Node[] nodes = graph.getNodes();
		for (int i = 0; i < nodes.length; i++)
			if (nodes[i].getObjectId().equals(node_id)) {
				node = nodes[i];
				break;
			}

		// if no valid node was found, throws exception
		if (node == null)
			throw new NodeNotFoundException();

		// tries to find the correspondent edge from the graph,
		// given its id
		Edge edge = null;
		if (edge_id.length() > 0) {
			Edge[] edges = graph.getEdges();
			for (int i = 0; i < edges.length; i++)
				if (edges[i].getObjectId().equals(edge_id)) {
					edge = edges[i];
					break;
				}

			// if no valid edge was found, throws exception
			if (edge == null)
				throw new EdgeNotFoundException();
		}

		// tries to obtain the elapsed length of the teleport
		double elapsed_length = 0;
		if (str_elapsed_length.length() > 0)
			elapsed_length = Double.parseDouble(str_elapsed_length);

		// return the obtained teleport action
		return new TeleportAction(node, edge, elapsed_length);
	}

	/**
	 * Obtains the action of moving on the graph from the given XML string.
	 * 
	 * @param xml_string
	 *            The XML source containing the action.
	 * @param graph
	 *            The graph of the simulation.
	 * @return The action of moving on the graph from the XML source.
	 * @throws IOException
	 * @throws SAXException
	 * @throws NodeNotFoundException
	 */
	public static GoToAction getGoToAction(String xml_string, Graph graph)
			throws SAXException, IOException, NodeNotFoundException {
		// parses the string in order to obtain the "action" element
		Element action_element = parseString(xml_string);

		// verifies the type of the action
		int type = Integer.parseInt(action_element.getAttribute("type"));
		if (type != ActionTypes.GOTO)
			return null;

		// obtains the data from the action
		String str_initial_speed = action_element.getAttribute("initial_speed");
		String str_acceleration = action_element.getAttribute("acceleration");
		String goal_node_id = action_element.getAttribute("node_id");

		// tries to obtain the initial speed of the movement
		// (the default value is -1)
		double initial_speed = -1;
		if (str_initial_speed.length() > 0)
			initial_speed = Double.parseDouble(str_initial_speed);

		// tries to obtain the acceleration of the movement
		// (the default value is -1)
		double acceleration = -1;
		if (str_acceleration.length() > 0)
			acceleration = Double.parseDouble(str_acceleration);

		// tries to find the correspondent node from the graph,
		// given its id
		Node goal_node = null;
		Node[] nodes = graph.getNodes();
		for (int i = 0; i < nodes.length; i++)
			if (nodes[i].getObjectId().equals(goal_node_id)) {
				goal_node = nodes[i];
				break;
			}
		// if no valid node was found, throws exception
		if (goal_node == null)
			throw new NodeNotFoundException();

		// returns the obtained goto action
		return new GoToAction(initial_speed, acceleration, goal_node);
	}
}