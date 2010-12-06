/* CognitiveCoordinatedAgent.java */

/* The package of this class. */
package cognitive_coordinated_OLD;

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
import util.graph.Node;
import util.net_OLD.TCPClientConnection_OLD;
import util.net_OLD.UDPClientConnection_OLD;
import common_OLD.Agent_OLD;

/**
 * Implements cognitive coordinated agents, as it is described in the work of
 * MACHADO [2002].
 */
public final class CognitiveCoordinatedAgent_OLD extends Agent_OLD {
	/* Attributes. */
	/** The id of this agent. */
	private final String ID;

	/** The plan of walking through the graph. */
	private final LinkedList<String> PLAN;

	/** The current goal of the agent. */
	private String goal;

	/** The graph perceived by the agent. */
	private Graph graph;

	/** The current position of the agent. */
	private StringAndDouble position;

	/** Holds if the simulation is a real time one. */
	private static boolean is_real_time_simulation;

	/**
	 * The time interval the agent is supposed to wait for a message sent by the
	 * coordinator. Mesaured in seconds.
	 */
	private final int WAITING_TIME = 30; // 30 seconds

	/* Methods. */
	/**
	 * Contructor.
	 * 
	 * @param id
	 *            The id of this agent.
	 * @param is_real_time
	 *            TRUE if the simulation is a real time one, FALSE if it is a
	 *            cycled one.
	 */
	public CognitiveCoordinatedAgent_OLD(String id, boolean is_real_time) {
		this.ID = id;
		this.PLAN = new LinkedList<String>();
		this.goal = null;
		this.graph = null;
		this.position = null;
		is_real_time_simulation = is_real_time;
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
			int vertex_id_index = perception.indexOf("node_id=\"");
			perception = perception.substring(vertex_id_index + 9);
			String vertex_id = perception
					.substring(0, perception.indexOf("\""));

			// obtains the elapsed length on the current edge
			double elapsed_length = 0;
			int elapsed_length_index = perception.indexOf("elapsed_length=\"");
			if(elapsed_length_index != -1){
				perception = perception.substring(elapsed_length_index + 16);
				elapsed_length = Double.parseDouble(perception.substring(0, perception.indexOf("\"")));
			}
			// returs the answer of the method
			return new StringAndDouble(vertex_id, elapsed_length);
		}

		return null;
	}

	/**
	 * Perceives a message sent by the coordinator with the goal vertex.
	 * 
	 * @see CognitiveCoordinatorAgent_OLD
	 * @param perception
	 *            The perception sent by SimPatrol server.
	 * @return The id of the goal vertex.
	 */
	private String perceiveGoalNode(String perception) {
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
			this.connection.send("<action type=\"3\" message=\"" + this.ID
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
		Node begin_Node = new Node("");
		begin_Node.setObjectId(position);

		// obtains the goal Node
		Node end_Node = new Node("");
		end_Node.setObjectId(goal);

		// obtains the dijkstra path
		Graph path = graph.getDijkstraPath(begin_Node, end_Node);

		// adds the ordered Nodees in the plan of the agent
		Node[] path_Nodees = path.getNodees();
		for (int i = 0; i < path_Nodees.length; i++)
			if (path_Nodees[i].equals(begin_Node)) {
				begin_Node = path_Nodees[i];
				break;
			}

		if (begin_Node.getEdges().length > 0) {
			Node current_Node = begin_Node.getEdges()[0]
					.getOtherNode(begin_Node);
			Edge[] current_Node_edges = current_Node.getEdges();
			this.PLAN.add(current_Node.getObjectId());

			while (current_Node_edges.length > 1) {
				Node next_Node = current_Node_edges[0]
						.getOtherNode(current_Node);

				if (this.PLAN.contains(next_Node.getObjectId())
						|| next_Node.equals(begin_Node)) {
					current_Node = current_Node_edges[1]
							.getOtherNode(current_Node);
				} else
					current_Node = next_Node;

				this.PLAN.add(current_Node.getObjectId());
				current_Node_edges = current_Node.getEdges();
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
			// obtains the id of the next Node
			String next_Node = this.PLAN.remove();

			// sends the action to visit the current Node
			this.connection.send("<action type=\"2\"/>");

			// sends the action to go to the next Node
			this.connection.send("<action type=\"1\" node_id=\""
					+ next_Node + "\"/>");
			
			//System.out.println(this.ID + "<action type=\"1\" node_id=\"" + next_Node + "\"/>");
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
					StringAndDouble sent_position = null;
					sent_position= this.perceivePosition(perceptions[i]);
					
					if(sent_position != null )
						this.position = sent_position;

					
					Graph sentgraph = null;
					if(sent_position == null){
						try {
							sentgraph = this.perceiveGraph(perceptions[i]);
						} catch (ParserConfigurationException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						} catch (SAXException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						
						if(sentgraph != null)
							this.graph = sentgraph;
					}
					
				}
			}

			// lets the agent ask for a goal Node to the coordinator
			try {
				this.requestGoal();
			} catch (IOException e) {
				e.printStackTrace();
			}

			// tracks the time the agent has been waiting for a message from the
			// coordinator
			int begin_waiting_time = Calendar.getInstance()
					.get(Calendar.SECOND);

			// while the agent did not perceive its position, its goal Node
			// and the graph of the simulation
			String goal_Node = null;
			Graph current_graph = this.graph;

			while ((goal_Node == null || current_graph == null) && !this.stop_working) {
				// obtains the perceptions sent by SimPatrol server
				String[] perceptions = this.connection.getBufferAndFlush();

				// for each perception, starting from the most recent one
				for (int i = perceptions.length - 1; i >= 0; i--) {
					// tries to update the current position
					StringAndDouble perceived_position = this.perceivePosition(perceptions[i]);
					if (perceived_position != null)
						this.position = perceived_position;
					else {
						// tries to obtain the goal Node
						String perceived_goal_Node = this.perceiveGoalNode(perceptions[i]);
						if (perceived_goal_Node != null) {
							goal_Node = perceived_goal_Node;
							this.goal = goal_Node;
						} else {
							// tries to obtain the graph of the simulation
							Graph perceived_graph = null;
							try {
								perceived_graph = this.perceiveGraph(perceptions[i]);
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

					/* On enleve le timer 
					 * 
					 
					// if the needed perceptions were obtained, breaks the loop
					if (goal_Node != null && current_graph != null)
						break;
					// else if the simulation is a real time one
					else if (is_real_time_simulation) {
						// counts the time the agent has been waiting for a
						// message from the coordinator
						int end_wainting_time = Calendar.getInstance().get(
								Calendar.SECOND);

						if (end_wainting_time < begin_waiting_time)
							end_wainting_time = end_wainting_time + 60;

						// if the agent waited too long, resends a request for a
						// new goal Node
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
					else if (current_graph != null && !current_graph.equals(this.graph)) {
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
					
					*/
				}
			}

			// lets the agent plan its actions
			this.plan(this.position.STRING, goal_Node, current_graph);
			this.graph = current_graph;
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
	 *            is a real time one, "false" if not. index 3: The ID of the
	 *            agent.
	 */
	public static void main(String args[]) {
		try {
			String server_address = args[0];
			int server_socket_number = Integer.parseInt(args[1]);
			boolean is_real_time_simulation = Boolean.parseBoolean(args[2]);
			String agent_id = args[3];

			CognitiveCoordinatedAgent_OLD agent = new CognitiveCoordinatedAgent_OLD(
					agent_id, is_real_time_simulation);
			if (is_real_time_simulation)
				agent.setConnection(new UDPClientConnection_OLD(server_address,
						server_socket_number));
			else
				agent.setConnection(new TCPClientConnection_OLD(server_address,
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
							+ "<IP address> <Remote socket number> <Is real time simulator? (true | false)> <Agent ID>\"");
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
