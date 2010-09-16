/* CompoundActionsParser.java */

/* The package of this class. */
package control.parser;

/* Imported classes and/or interfaces. */
import java.util.LinkedList;
import model.action.AtomicRechargeAction;
import model.action.CompoundAction;
import model.action.GoToAction;
import model.action.RechargeAction;
import model.action.TeleportAction;
import model.agent.Agent;
import model.graph.Edge;
import model.graph.Graph;
import model.graph.Vertex;
import model.interfaces.Visible;

/**
 * Parses the compound actions of the agents of SimPatrol into time chained
 * atomic actions.
 * 
 * @see CompoundAction
 * @developer New CompoundAction classes must change this one.
 */
public abstract class CompoundActionsParser {
	/* Methods. */
	/**
	 * Parses the GoToAction objects into time chained TeleportAction objects.
	 * 
	 * @param action
	 *            The action to be parsed.
	 * @param agent
	 *            The agent intending to execute the action.
	 * @param path
	 *            The path the agent shall trespass on the Graph.
	 * @param depth_limitation
	 *            The maximum number of edges the path shall be trespassed.
	 * @param speed_limitation
	 *            The maximum speed that the agent can reach when moving.
	 *            Measured in depth/sec or depth/cycle.
	 * @param acceleration_limitation
	 *            The maximum acceleration that the agent can assume when
	 *            moving. Measured in depth/sec^2 or depth/cycle^2.
	 * @param time_rate
	 *            The time rate used to generate the time chain of atomic
	 *            actions. Measured in seconds, or in cycles.
	 * @return The correspondent time chained actions of teleport.
	 */
	public static TeleportAction[] parseGoToAction(GoToAction action,
			Agent agent, Graph path, int depth_limitation,
			double speed_limitation, double acceleration_limitation,
			double time_rate) {
		// constant acceleration
		double acceleration = action.getAcceleration();

		// current initial speed
		double current_initial_speed = action.getInitial_speed();

		// if such values were not informed (i.e. they are -1),
		// sets their values as the acceleration and speed limitations
		if (acceleration == -1)
			acceleration = acceleration_limitation;
		if (current_initial_speed == -1)
			current_initial_speed = speed_limitation;

		// if both values are still -1, sets the acceleration
		// as zero and speed as 1
		if (acceleration == -1 && current_initial_speed == -1) {
			acceleration = 0;
			current_initial_speed = 1;
		}
		// else, each one being equal to -1 must be changed to 0
		else if (acceleration == -1)
			acceleration = 0;
		else if (current_initial_speed == -1)
			current_initial_speed = 0;

		// if the acceleration exceeds the acceleration limitation,
		// sets it as the acceleration limitation
		if (acceleration_limitation > -1
				&& acceleration > acceleration_limitation)
			acceleration = acceleration_limitation;

		// if the initial speed exceeds the speed limitation,
		// sets it as the speed limitation
		if (speed_limitation > -1 && current_initial_speed > speed_limitation)
			current_initial_speed = speed_limitation;

		// current displacement
		double current_displacement = 0;

		// current vertex positions
		Vertex current_graph_vertex = agent.getVertex();
		Vertex current_path_vertex = null;

		Vertex[] path_vertexes = path.getVertexes();
		for (int i = 0; i < path_vertexes.length; i++)
			if (path_vertexes[i].equals(current_graph_vertex)) {
				current_path_vertex = path_vertexes[i];
				break;
			}

		// current edge positions
		Edge current_graph_edge = agent.getEdge();

		Edge current_path_edge = null;
		if (current_path_vertex.getEdges().length > 0)
			current_path_edge = current_path_vertex.getEdges()[0];
		else
			return new TeleportAction[0];

		if (current_graph_edge == null) {
			Edge[] graph_edges = current_graph_vertex.getEdges();

			for (int i = 0; i < graph_edges.length; i++)
				if (graph_edges[i].equals(current_path_edge)) {
					current_graph_edge = graph_edges[i];
					break;
				}
		}

		// current elapsed length
		double current_elapsed_length = agent.getElapsed_length();

		// the obtained teleport actions
		LinkedList<TeleportAction> teleport_actions = new LinkedList<TeleportAction>();

		// parses the compound action
		while (true) {
			// calculates the current displacement
			// d = v0 * t + a * t^2 / 2
			current_displacement = current_initial_speed * time_rate
					+ acceleration * Math.pow(time_rate, 2) * 0.5;

			// if the current displacement is 0 (no speed neither acceleration)
			// return an empty plan
			if (current_displacement == 0)
				return new TeleportAction[0];

			// calculates the next position of the agent on the graph
			double remained_length = current_path_edge.getLength()
					- current_elapsed_length;

			if (remained_length > current_displacement) {
				current_elapsed_length = current_elapsed_length
						+ current_displacement;

				// adds the next teleport...
				teleport_actions.add(new TeleportAction(current_graph_vertex,
						current_graph_edge, current_elapsed_length));
			} else {
				// the objects that shall become visible during the teleport
				LinkedList<Visible> visible_objects = new LinkedList<Visible>();

				while (current_displacement >= remained_length) {
					current_displacement = current_displacement
							- remained_length;
					current_path_vertex = current_path_edge
							.getOtherVertex(current_path_vertex);
					current_graph_vertex = current_graph_edge
							.getOtherVertex(current_graph_vertex);

					visible_objects.add(current_graph_vertex);

					depth_limitation--;

					Edge[] edges_current_path_vertex = current_path_vertex
							.getEdges();
					if (edges_current_path_vertex.length <= 1
							|| depth_limitation == 0) {
						// adds the next teleport and returns the answer
						teleport_actions.add(new TeleportAction(
								current_graph_vertex, null, 0, visible_objects
										.toArray(new Visible[0])));

						return teleport_actions.toArray(new TeleportAction[0]);
					}

					current_path_edge = edges_current_path_vertex[0];
					if (current_path_edge.equals(current_graph_edge))
						current_path_edge = edges_current_path_vertex[1];

					Edge[] graph_edges = current_graph_vertex.getEdges();
					for (int i = 0; i < graph_edges.length; i++)
						if (graph_edges[i].equals(current_path_edge)) {
							current_graph_edge = graph_edges[i];
							break;
						}

					visible_objects.add(current_graph_edge);

					remained_length = current_path_edge.getLength();
				}

				if (current_displacement > 0) {
					current_elapsed_length = current_displacement;

					teleport_actions.add(new TeleportAction(
							current_graph_vertex, current_graph_edge,
							current_elapsed_length));
				} else
					current_elapsed_length = 0;
			}

			// updates the current initial speed
			// v = v0 + a*t
			current_initial_speed = current_initial_speed + acceleration
					* time_rate;
			if (speed_limitation > -1
					&& current_initial_speed > speed_limitation)
				current_initial_speed = speed_limitation;
		}
	}

	/**
	 * Parses the RechargeAction objects into time chained AtomicRechargeAction
	 * objects.
	 * 
	 * @param action
	 *            The action to be parsed.
	 * @param speed_limitation
	 *            The maximum speed that the agent can recharge its stamina.
	 *            Measured in stamina/sec or stamina/cycle.
	 * @param time_rate
	 *            The time rate used to generate the time chain of atomic
	 *            actions. Measured in seconds, or in cycles.
	 * @return The correspondent time chained actions of recharging.
	 */
	public static AtomicRechargeAction[] parseRechargeAction(
			RechargeAction action, double speed_limitation, double time_rate) {
		// holds how much stamina each atomic recharging action must recharge
		double stamina_factor = 0;
		if (speed_limitation > -1)
			stamina_factor = speed_limitation * time_rate;
		else
			stamina_factor = action.getStamina();

		// if the stamina factor is zero, returns an empty plan
		if (stamina_factor == 0)
			return new AtomicRechargeAction[0];

		// holds the needed atomic actions
		LinkedList<AtomicRechargeAction> atomic_actions = new LinkedList<AtomicRechargeAction>();

		// fills the plan of needed atomic recharging actions
		int needed_atomic_actions_count = (int) (action.getStamina() / stamina_factor);
		for (int i = 0; i < needed_atomic_actions_count; i++)
			atomic_actions.add(new AtomicRechargeAction(stamina_factor));

		double remained_stamina = action.getStamina() % stamina_factor;
		if (remained_stamina > 0)
			atomic_actions.add(new AtomicRechargeAction(remained_stamina));

		// returns the answer
		return atomic_actions.toArray(new AtomicRechargeAction[0]);
	}
}