/* CognitiveCoordinatedAgent.java */

/* The package of this class. */
package cognitive_coordinated;

/* Imported classes and/or interfaces. */
import java.io.IOException;
import java.util.LinkedList;
import javax.xml.parsers.ParserConfigurationException;
import org.xml.sax.SAXException;

import util.Keyboard;
import util.Translator;
import util.graph.Edge;
import util.graph.Graph;
import util.graph.Vertex;
import util.net.TCPClientConnection;
import util.net.UDPClientConnection;
import common.Agent;

/**
 * Implements cognitive coordinated agents, as it is described in the work of
 * MACHADO [2002].
 */
public final class CognitiveCoordinatedAgent extends Agent {
	/* Attributes. */
	/** The id of this agent. */
	private final String ID;

	/** The plan of walking through the graph. */
	private final LinkedList<String> PLAN;

	/* Methods. */
	/**
	 * Contructor.
	 * 
	 * @param id
	 *            The id of this agent.
	 */
	public CognitiveCoordinatedAgent(String id) {
		this.ID = id;
		this.PLAN = new LinkedList<String>();
	}

	/**
	 * Perceives and returns the current position.
	 * 
	 * @param perception
	 *            The perception sent by SimPatrol server.
	 * @return The current position.
	 */
	private StringAndDouble perceivePosition(String perception) {
		if (perception.indexOf("<perception type=\"4\"") > -1) {
			// obtains the id of the current vertex
			int vertex_id_index = perception.indexOf("vertex_id=\"");
			perception = perception.substring(vertex_id_index + 11);
			String vertex_id = perception
					.substring(0, perception.indexOf("\""));

			// obtains the elapsed length on the current edge
			int elapsed_length_index = perception.indexOf("elapsed_length=\"");
			perception = perception.substring(elapsed_length_index + 16);
			double elapsed_length = Double.parseDouble(perception.substring(0,
					perception.indexOf("\"")));

			// returs the answer of the method
			return new StringAndDouble(vertex_id, elapsed_length);
		}

		return null;
	}

	/**
	 * Perceives a message sent by the coordinator with the goal vertex.
	 * 
	 * @see CognitiveCoordinatorAgent
	 * @param perception
	 *            The perception sent by SimPatrol server.
	 * @return The id of the goal vertex.
	 */
	private String perceiveGoalVertex(String perception) {
		if (perception.indexOf("<perception type=\"3\"") > -1) {
			// obtains the sent message
			int message_index = perception.indexOf("message=\"");
			perception = perception.substring(message_index + 9);
			String message = perception.substring(0, perception.indexOf("\""));

			// if the message has the "###" conventioned mark
			int mark_index = message.indexOf("###");
			if (mark_index > -1)
				// if this message was sent to this agent
				if (message.substring(0, mark_index).equals(this.ID))
					// returns the id of the goal vertex
					return message.substring(mark_index + 3);
		}
		return null;
	}

	/**
	 * Perceives the graph to be patrolled.
	 * 
	 * @param perception
	 *            The perception sent by SimPatrol server.
	 * @return The perceived graph.
	 * @throws IOException
	 * @throws SAXException
	 * @throws ParserConfigurationException
	 */
	private Graph perceiveGraph(String perception)
			throws ParserConfigurationException, SAXException, IOException {
		Graph[] parsed_perception = Translator.getGraphs(Translator
				.parseString(perception));
		if (parsed_perception.length > 0)
			return parsed_perception[0];

		return null;
	}

	/**
	 * Lets the agent ask for a goal vertex to the coordinator.
	 * 
	 * @throws IOException
	 */
	private void requestGoal() throws IOException {
		this.connection
				.send("<action type=\"3\" message=\"" + this.ID + "\"/>");
	}

	/**
	 * Lets the agent plan its actions.
	 * 
	 * @param position
	 *            The id of the current vertex of the agent.
	 * @param goal
	 *            The id of the goal vertex of the agent.
	 * @param graph
	 *            The graph just perceived by the agent.
	 */
	private void plan(String position, String goal, Graph graph) {
		// obtains the vertex of the beginning
		Vertex begin_vertex = new Vertex("");
		begin_vertex.setObjectId(position);

		// obtains the goal vertex
		Vertex end_vertex = new Vertex("");
		end_vertex.setObjectId(goal);

		// obtains the dijkstra path
		Graph path = graph.getDijkstraPath(begin_vertex, end_vertex);

		// adds the ordered vertexes in the plan of the agent
		Vertex[] path_vertexes = path.getVertexes();
		for (int i = 0; i < path_vertexes.length; i++)
			if (path_vertexes[i].equals(begin_vertex)) {
				begin_vertex = path_vertexes[i];
				break;
			}

		Vertex current_vertex = begin_vertex.getEdges()[0]
				.getOtherVertex(begin_vertex);
		Edge[] current_vertex_edges = begin_vertex.getEdges();
		this.PLAN.add(current_vertex.getObjectId());

		while (current_vertex_edges.length > 1) {
			if (this.PLAN.contains(current_vertex_edges[0].getOtherVertex(
					current_vertex).getObjectId())) {
				current_vertex = current_vertex_edges[1]
						.getOtherVertex(current_vertex);
				this.PLAN.add(current_vertex.getObjectId());
			} else {
				current_vertex = current_vertex_edges[0]
						.getOtherVertex(current_vertex);
				this.PLAN.add(current_vertex.getObjectId());
			}

			current_vertex_edges = current_vertex.getEdges();
		}
	}

	/**
	 * Lets the agent execute next step of its planning.
	 * 
	 * @throws IOException
	 */
	private void executeNextStep() throws IOException {
		if (!this.PLAN.isEmpty()) {
			// obtains the id of the next vertex
			String next_vertex = this.PLAN.remove();

			// sends the action to the server
			this.connection.send("<action type=\"1\" vertex_id=\""
					+ next_vertex + "\"/>");
		}
	}

	public void run() {
		while (!this.stop_working) {
			// lets the agent ask for a goal vertex to the coordinator
			try {
				this.requestGoal();
			} catch (IOException e) {
				e.printStackTrace();
			}

			// perceives the position of the agent, goal vertex and graph of
			// the simulation
			StringAndDouble position = null;
			String goal_vertex = null;
			Graph graph = null;

			while ((position == null || goal_vertex == null || graph == null)
					&& !this.stop_working) {
				// obtains the perceptions sent by SimPatrol server
				String[] perceptions = this.connection.getBufferAndFlush();

				// for each perception, starting from the most recent one
				for (int i = perceptions.length - 1; i >= 0; i--) {
					// tries to obtain the current position
					if ((position = this.perceivePosition(perceptions[i])) == null)
						// tries to obtain the goal vertex
						if ((goal_vertex = this
								.perceiveGoalVertex(perceptions[i])) == null)
							try {
								graph = this.perceiveGraph(perceptions[i]);
							} catch (ParserConfigurationException e) {
								e.printStackTrace();
							} catch (SAXException e) {
								e.printStackTrace();
							} catch (IOException e) {
								e.printStackTrace();
							}

					// if the needed perceptions were obtained, breaks the loop
					if (position != null && goal_vertex != null
							&& graph != null)
						break;
				}
			}

			// lets the agent plan its actions
			this.plan(position.STRING, goal_vertex, graph);

			// executes next step of the planning
			try {
				this.executeNextStep();
			} catch (IOException e) {
				e.printStackTrace();
			}

			// while the planning was not entirely executed
			while ((position.DOUBLE != 0 || !this.PLAN.isEmpty())
					&& !this.stop_working) {
				// perceives the current position of the agent
				StringAndDouble current_position = null;
				while (current_position == null && !this.stop_working) {
					// obtains the perceptions sent by SimPatrol server
					String[] perceptions = this.connection.getBufferAndFlush();

					// for each perception, starting from the most recent one
					for (int i = perceptions.length - 1; i >= 0; i--)
						// tries to obtain the current position
						if ((current_position = this
								.perceivePosition(perceptions[i])) != null)
							break;
				}

				// if the the current perceived position is different from the
				// previous one
				if (!current_position.equals(position)) {
					// updates the position of the agent
					position = current_position;

					// if the agent trespassed an edge entirely, executes next
					// step of the plan
					if (position.DOUBLE == 0)
						try {
							this.executeNextStep();
						} catch (IOException e) {
							e.printStackTrace();
						}
				}
			}
		}
	}

	/**
	 * Turns this class into an executable one. Util when running this agent in
	 * an individual machine.
	 * 
	 * @param args
	 *            Arguments: index 0: The IP address of the SimPatrol server.
	 *            index 1: The number of the socket that the server is supposed
	 *            to listen to this client. index 2: "true", if the simulation
	 *            is a real time one, "false" if not. index 3: The ID of the
	 *            agent.
	 */
	public static void main(String args[]) {
		try {
			String server_address = args[0];
			int server_socket_number = Integer.parseInt(args[1]);
			boolean is_real_time_simulation = Boolean.parseBoolean(args[2]);
			String agent_id = args[3];

			CognitiveCoordinatedAgent agent = new CognitiveCoordinatedAgent(
					agent_id);
			if (is_real_time_simulation)
				agent.setConnection(new UDPClientConnection(server_address,
						server_socket_number));
			else
				agent.setConnection(new TCPClientConnection(server_address,
						server_socket_number));

			agent.start();

			System.out.println("Press [t] key to terminate this agent.");
			String key = "";
			while (!key.equals("t"))
				key = Keyboard.readLine();

			agent.stopWorking();
		} catch (Exception e) {
			System.out
					.println("Usage \"java cognitive_coordinated.CognitiveCoordinatedAgent\n" +
							"<IP address> <Remote socket number> <Is real time simulator? (true | false)> <Agent ID>\"");
		}
	}
}

/** Internal class that holds together a string and a double value. */
final class StringAndDouble {
	/* Attributes */
	/** The string value. */
	public final String STRING;

	/** The double value. */
	public final double DOUBLE;

	/* Methods. */
	/**
	 * Constructor.
	 * 
	 * @param string
	 *            The string value of the pair.
	 * @param double_value
	 *            The double value of the pair.
	 */
	public StringAndDouble(String string, double double_value) {
		this.STRING = string;
		this.DOUBLE = double_value;
	}

	/**
	 * Compares a given StringAndDouble object to this one.
	 * 
	 * @param object
	 *            The object to be compared.
	 * @return TRUE if the object is equal, FALSE if not.
	 */
	public boolean equals(StringAndDouble object) {
		if (object != null && this.STRING.equals(object.STRING)
				&& this.DOUBLE == object.DOUBLE)
			return true;

		return false;
	}
}
