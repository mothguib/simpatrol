/* HeuristicCognitiveCoordinatedAgent.java */

/* The package of this class. */
package heuristic_cognitive_coordinated;

/* Imported classes and/or interfaces. */
import java.io.IOException;
import java.util.Calendar;
import java.util.LinkedList;
import javax.xml.parsers.ParserConfigurationException;
import org.xml.sax.SAXException;
import util.Keyboard;
import util.graph.Edge;
import util.graph.Graph;
import util.graph.GraphTranslator;
import util.graph.Vertex;
import util.net.TCPClientConnection;
import util.net.UDPClientConnection;
import common.Agent;

/**
 * Implements heuristic cognitive coordinated agents, as it is described in the
 * work of Almeida [2003].
 */
public final class HeuristicCognitiveCoordinatedAgent extends Agent {
	/* Attributes. */
	/** The id of this agent. */
	private String id;

	/** The plan of walking through the graph. */
	private final LinkedList<String> PLAN;

	/** The current goal of the agent. */
	private String goal;

	/** The graph perceived by the agent. */
	private Graph graph;

	/** The current position of the agent. */
	private StringAndDouble position;

	/**
	 * The time interval the agent is supposed to wait for a message sent by the
	 * coordinator. Measured in seconds.
	 */
	private final int WAITING_TIME = 30; // 30 seconds

	/* Methods. */
	/** Constructor. */
	public HeuristicCognitiveCoordinatedAgent() {
		this.id = null;
		this.PLAN = new LinkedList<String>();
		this.goal = null;
		this.graph = null;
		this.position = null;
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
			// obtains the id of the agent, if necessary
			if (this.id == null) {
				int id_index = perception.indexOf("<agent id=\"");
				perception = perception.substring(id_index + 11);
				this.id = perception.substring(0, perception.indexOf("\""));
			}

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
	 * @see HeuristicCognitiveCoordinatorAgent
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

			// if the message has the "###" convention mark
			int mark_index = message.indexOf("###");
			if (mark_index > -1)
				// if this message was sent to this agent
				if (message.substring(0, mark_index).equals(this.id))
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
	 */
	private Graph perceiveGraph(String perception)
			throws ParserConfigurationException, SAXException, IOException {
		Graph[] parsed_perception = GraphTranslator.getGraphs(GraphTranslator
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
		if (this.position != null)
			this.connection.send("<action type=\"3\" message=\"" + this.id
					+ "###" + this.position.STRING + "\"/>");
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
		Graph path = graph.getIdlenessedDijkstraPath(begin_vertex, end_vertex);

		// adds the ordered vertexes in the plan of the agent
		Vertex[] path_vertexes = path.getVertexes();
		for (int i = 0; i < path_vertexes.length; i++)
			if (path_vertexes[i].equals(begin_vertex)) {
				begin_vertex = path_vertexes[i];
				break;
			}

		if (begin_vertex.getEdges().length > 0) {
			Vertex current_vertex = begin_vertex.getEdges()[0]
					.getOtherVertex(begin_vertex);
			Edge[] current_vertex_edges = current_vertex.getEdges();
			this.PLAN.add(current_vertex.getObjectId());

			while (current_vertex_edges.length > 1) {
				Vertex next_vertex = current_vertex_edges[0]
						.getOtherVertex(current_vertex);

				if (this.PLAN.contains(next_vertex.getObjectId())
						|| next_vertex.equals(begin_vertex)) {
					current_vertex = current_vertex_edges[1]
							.getOtherVertex(current_vertex);
				} else
					current_vertex = next_vertex;

				this.PLAN.add(current_vertex.getObjectId());
				current_vertex_edges = current_vertex.getEdges();
			}
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

			// sends the action to visit the current vertex
			this.connection.send("<action type=\"2\"/>");

			// sends the action to go to the next vertex
			this.connection.send("<action type=\"1\" vertex_id=\""
					+ next_vertex + "\"/>");
		}
	}

	public void run() {
		// starts its connection
		this.connection.start();

		while (!this.stop_working) {
			// lets the agent perceive its current position
			while (this.position == null) {
				// obtains the perceptions sent by SimPatrol server
				String[] perceptions = this.connection.getBufferAndFlush();

				// for each perception, starting from the most recent one
				for (int i = perceptions.length - 1; i >= 0; i--) {
					// tries to obtain the current position
					this.position = this.perceivePosition(perceptions[i]);

					if (this.position != null)
						break;
				}
			}

			// lets the agent ask for a goal vertex to the coordinator
			try {
				this.requestGoal();
			} catch (IOException e) {
				e.printStackTrace();
			}

			// tracks the time the agent has been waiting for a message from the
			// coordinator
			int begin_waiting_time = Calendar.getInstance()
					.get(Calendar.SECOND);

			// while the agent did not perceive its goal vertex
			// and the graph of the simulation
			String goal_vertex = null;
			Graph current_graph = null;

			while ((goal_vertex == null || current_graph == null)
					&& !this.stop_working) {
				// obtains the perceptions sent by SimPatrol server
				String[] perceptions = this.connection.getBufferAndFlush();

				// for each perception, starting from the most recent one
				for (int i = perceptions.length - 1; i >= 0; i--) {
					// tries to update the current position
					StringAndDouble perceived_position = this
							.perceivePosition(perceptions[i]);
					if (perceived_position != null)
						this.position = perceived_position;
					else {
						// tries to obtain the goal vertex
						String perceived_goal_vertex = this
								.perceiveGoalVertex(perceptions[i]);
						if (perceived_goal_vertex != null) {
							goal_vertex = perceived_goal_vertex;
							this.goal = goal_vertex;
						} else {
							// tries to obtain the graph of the simulation
							Graph perceived_graph = null;
							try {
								perceived_graph = this
										.perceiveGraph(perceptions[i]);
							} catch (ParserConfigurationException e) {
								e.printStackTrace();
							} catch (SAXException e) {
								e.printStackTrace();
							} catch (IOException e) {
								e.printStackTrace();
							}

							if (perceived_graph != null)
								current_graph = perceived_graph;
						}
					}

					// if the needed perceptions were obtained, breaks the loop
					if (goal_vertex != null && current_graph != null)
						break;
					// else if the simulation is a real time one
					else if (this.connection instanceof UDPClientConnection) {
						// counts the time the agent has been waiting for a
						// message from the coordinator
						int end_wainting_time = Calendar.getInstance().get(
								Calendar.SECOND);

						if (end_wainting_time < begin_waiting_time)
							end_wainting_time = end_wainting_time + 60;

						// if the agent waited too long, resends a request for a
						// new goal vertex
						if (end_wainting_time - begin_waiting_time >= this.WAITING_TIME) {
							try {
								this.requestGoal();
							} catch (IOException e) {
								e.printStackTrace();
							}

							begin_waiting_time = Calendar.getInstance().get(
									Calendar.SECOND);
						}
					}
					// else, if the perceived graph is not the one previously
					// perceived
					else if (current_graph != null
							&& !current_graph.equals(this.graph)) {
						// memorizes the perceived graph
						this.graph = current_graph;

						// sends a message of "do nothing" due to
						// synchronization reasons
						try {
							this.connection.send("<action type=\"-1\"/>");
						} catch (IOException e) {
							e.printStackTrace();
						}
					}
				}
			}

			// lets the agent plan its actions
			this.plan(this.position.STRING, goal_vertex, current_graph);

			// executes next step of the planning
			try {
				this.executeNextStep();
			} catch (IOException e) {
				e.printStackTrace();
			}

			// while the goal was not achieved
			while (!this.position.STRING.equals(this.goal)
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
				if (!current_position.equals(this.position)) {
					// updates the position of the agent
					this.position = current_position;

					// if the agent trespassed an edge entirely, executes next
					// step of the plan
					if (this.position.DOUBLE == 0)
						try {
							this.executeNextStep();
						} catch (IOException e) {
							e.printStackTrace();
						}
				}
			}
		}

		// stops the connection of the agent
		try {
			this.connection.stopWorking();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Turns this class into an executable one. Useful when running this agent
	 * in an individual machine.
	 * 
	 * @param args
	 *            Arguments: index 0: The IP address of the SimPatrol server.
	 *            index 1: The number of the socket that the server is supposed
	 *            to listen to this client. index 2: "true", if the simulation
	 *            is a real time one, "false" if not.
	 */
	public static void main(String args[]) {
		try {
			String server_address = args[0];
			int server_socket_number = Integer.parseInt(args[1]);
			boolean is_real_time_simulation = Boolean.parseBoolean(args[2]);

			HeuristicCognitiveCoordinatedAgent agent = new HeuristicCognitiveCoordinatedAgent();
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
					.println("Usage \"java heuristic_cognitive_coordinated.HeuristicCognitiveCoordinatedAgent\n"
							+ "<IP address> <Remote socket number> <Is real time simulator? (true | false)>\"");
		}
	}
}

/** Internal class that holds together a string and a double value. */
final class StringAndDouble {
	/** The string value. */
	public final String STRING;

	/** The double value. */
	public final double DOUBLE;

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