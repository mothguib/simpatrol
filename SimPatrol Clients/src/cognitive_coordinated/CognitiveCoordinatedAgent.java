package cognitive_coordinated;

import java.io.IOException;
import java.util.LinkedList;
import org.xml.sax.SAXException;
import util.Keyboard;
import util.graph.Edge;
import util.graph.Graph;
import util.graph.GraphTranslator;
import util.graph.Node;
import util.net.TCPClientConnection;
import util.net.UDPClientConnection;
import common.Agent;


/**
 * Implements cognitive coordinated agents, as it is described in the work of
 * MACHADO [2002].
 */
public final class CognitiveCoordinatedAgent extends Agent {
	
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


	/** Constructor. */
	public CognitiveCoordinatedAgent() {
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

			// obtains the id of the current node
			int node_id_index = perception.indexOf("node_id=\"");
			perception = perception.substring(node_id_index + 9);
			String node_id = perception
					.substring(0, perception.indexOf("\""));

			// obtains the elapsed length on the current edge
			int elapsed_length_index = perception.indexOf("elapsed_length=\"");
			double elapsed_length = 0.0;
			if (elapsed_length_index > -1) {
				perception = perception.substring(elapsed_length_index + 16);
				elapsed_length = Double.parseDouble(perception.substring(0,
					perception.indexOf("\"")));
			} 

			// returns the answer of the method
			return new StringAndDouble(node_id, elapsed_length);
		}

		return null;
	}

	/**
	 * Perceives a message sent by the coordinator with the goal node.
	 * 
	 * @see CognitiveCoordinatorAgent_OLD
	 * @param perception
	 *            The perception sent by SimPatrol server.
	 * @return The id of the goal node.
	 */
	private String perceiveGoalNode(String perception) {
		if (perception.indexOf("<perception type=\"3\"") > -1) {
			// obtains the sent message
			int message_index = perception.indexOf("message=\"");
			perception = perception.substring(message_index + 9);
			String message = perception.substring(0, perception.indexOf("\""));

			// if the message has the "###" conventional mark
			int mark_index = message.indexOf("###");
			if (mark_index > -1)
				// if this message was sent to this agent
				if (message.substring(0, mark_index).equals(this.id))
					// returns the id of the goal node
					return message.substring(mark_index + 3);
		}
		return null;
	}

	/**
	 * Perceives the graph to be patroled.
	 * 
	 * @param perception
	 *            The perception sent by SimPatrol server.
	 * @return The perceived graph.
	 * @throws IOException
	 * @throws SAXException
	 */
	private Graph perceiveGraph(String perception) throws SAXException,
			IOException {
		Graph[] parsed_perception = GraphTranslator.getGraphs(GraphTranslator
				.parseString(perception));
		if (parsed_perception.length > 0)
			return parsed_perception[0];

		return null;
	}

	/**
	 * Lets the agent ask for a goal node to the coordinator.
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
	 *            The id of the current node of the agent.
	 * @param goal
	 *            The id of the goal node of the agent.
	 * @param graph
	 *            The graph just perceived by the agent.
	 */
	private void plan(String position, String goal, Graph graph) {
		// obtains the node of the beginning
		Node begin_node = new Node("");
		begin_node.setObjectId(position);

		// obtains the goal node
		Node end_node = new Node("");
		end_node.setObjectId(goal);

		// obtains the dijkstra path
		Graph path = graph.getDijkstraPath(begin_node, end_node);

		// adds the ordered nodes in the plan of the agent
		Node[] path_nodes = path.getNodees();
		for (int i = 0; i < path_nodes.length; i++)
			if (path_nodes[i].equals(begin_node)) {
				begin_node = path_nodes[i];
				break;
			}

		if (begin_node.getEdges().length > 0) {
			Node current_node = begin_node.getEdges()[0]
					.getOtherNode(begin_node);
			Edge[] current_node_edges = current_node.getEdges();
			this.PLAN.add(current_node.getObjectId());

			while (current_node_edges.length > 1) {
				Node next_node = current_node_edges[0]
						.getOtherNode(current_node);

				if (this.PLAN.contains(next_node.getObjectId())
						|| next_node.equals(begin_node)) {
					current_node = current_node_edges[1]
							.getOtherNode(current_node);
				} else
					current_node = next_node;

				this.PLAN.add(current_node.getObjectId());
				current_node_edges = current_node.getEdges();
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
			// obtains the id of the next node
			String next_node = this.PLAN.remove();

			// sends the action to visit the current node
			this.connection.send("<action type=\"2\"/>");

			// sends the action to go to the next node
			this.connection.send("<action type=\"1\" node_id=\""
					+ next_node + "\"/>");
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
				/*try {
					Thread.sleep(1);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}*/
			}

			// lets the agent ask for a goal node to the coordinator
			try {
				this.requestGoal();
			} catch (IOException e) {
				e.printStackTrace();
			}

			// tracks the time the agent has been waiting for a message from the
			// coordinator
			long begin_waiting_time = System.nanoTime();

			// while the agent did not perceive its position, its goal node
			// and the graph of the simulation
			String goal_node = null;
			Graph current_graph = null;

			while ((goal_node == null || current_graph == null)
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
						// tries to obtain the goal node
						String perceived_goal_node = this
								.perceiveGoalNode(perceptions[i]);
						if (perceived_goal_node != null) {
							goal_node = perceived_goal_node;
							this.goal = goal_node;
						} else {
							// tries to obtain the graph of the simulation
							Graph perceived_graph = null;
							try {
								perceived_graph = this
										.perceiveGraph(perceptions[i]);
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
					if (goal_node != null && current_graph != null)
						break;
					// else if the simulation is a real time one
					else if (this.connection instanceof UDPClientConnection) {
						// counts the time the agent has been waiting for a
						// message from the coordinator
						long end_wainting_time = System.nanoTime();

						// if the agent waited too long, resends a request for a
						// new goal node
						if (end_wainting_time - begin_waiting_time >= this.WAITING_TIME
								* Math.pow(10, 9)) {
							try {
								this.requestGoal();
							} catch (IOException e) {
								e.printStackTrace();
							}

							begin_waiting_time = System.nanoTime();
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
			this.plan(this.position.STRING, goal_node, current_graph);

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
				if (current_position != null && !current_position.equals(this.position)) {
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

			CognitiveCoordinatedAgent agent = new CognitiveCoordinatedAgent();
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
					.println("Usage \"java cognitive_coordinated.CognitiveCoordinatedAgent\n"
							+ "<IP address> <Remote socket number> <Is real time simulator? (true | false)>\"");
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
