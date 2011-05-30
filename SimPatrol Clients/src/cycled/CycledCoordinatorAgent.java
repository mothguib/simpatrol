package cycled;

import java.io.IOException;
import java.util.LinkedList;
import org.xml.sax.SAXException;
import util.Keyboard;
import util.graph.Graph;
import util.graph.GraphTranslator;
import util.graph.Node;
import util.net.TCPClientConnection;
import util.net.UDPClientConnection;
import common.Agent;


/**
 * Implements a coordinator that solves the TSP problem for a perceived graph,
 * in order to send the solution to the cycled agents in the environment, as
 * well synchronize them. Based in the work of [Chevaleyre, 2005].
 */
public class CycledCoordinatorAgent extends Agent {
	
	/**
	 * Expresses the quality of the network (i.e. the number of times the
	 * coordinator must send orientations to the other agents, due to UDP packet
	 * loss).
	 */
	private final static int NETWORK_QUALITY = 9;

	/**
	 * The sequence of nodes to be visited by the agents. Actually a TSP
	 * solution for the graph of the environment.
	 */
	protected final LinkedList<String> PLAN;

	/** The length of the TSP solution found by this agent. */
	protected double solution_length;

	/**
	 * List that holds in the ith position the id of the agent, and in the
	 * (i+1)th position the id of the node where the agent is.
	 */
	private final LinkedList<String> AGENTS_POSITIONS;

	/** Holds the graph currently perceived by this agent. */
	private Graph graph;
	
	// registers if the coordinator already sent orientation to the other
	// agents
	boolean sent_orientation = false;

	// registers if the agent perceived the tsp solution
	boolean perceived_tsp_solution = false;

	// registers if the agent perceived the other agents
	boolean perceived_other_agents = false;


	/** Constructor. */
	public CycledCoordinatorAgent() {
		this.PLAN = new LinkedList<String>();
		this.solution_length = 0;
		this.AGENTS_POSITIONS = new LinkedList<String>();
		this.graph = null;
	}

	/**
	 * Lets the agent perceive the graph of the environment and calculate its
	 * TSP solution.
	 * 
	 * @param perception
	 *            The current perception of the agent.
	 * @return TRUE if the agent obtained a TSP solution, FALSE if not.
	 * @throws IOException
	 * @throws SAXException
	 */
	protected boolean perceiveTSPSolution(String perception)
			throws SAXException, IOException {
		// tries to obtain a graph from the perception
		Graph[] graph_perception = GraphTranslator.getGraphs(GraphTranslator
				.parseString(perception));

		// if a graph was perceived
		if (graph_perception.length > 0) {
			// obtains the graph
			this.graph = graph_perception[0];

			// obtains the TSP solution
			Node[] tsp_solution = this.graph.getTSPSolution();

			// adds the solution to the plan of the agent and calculates the
			// total length of such solution
			for (int i = 0; i < tsp_solution.length; i++) {
				if (i < tsp_solution.length - 1)
					this.PLAN.add(tsp_solution[i].getObjectId());

				if (i > 0)
					this.solution_length = this.solution_length
							+ Math.ceil(this.graph.getDijkstraPath(
									tsp_solution[i - 1], tsp_solution[i])
									.getEdges()[0].getLength());
			}

			System.err.println("Solution length: " + this.solution_length);

			// returns the success of such perception
			return true;
		}

		// default answer
		return false;
	}

	/**
	 * Lets the coordinator perceive the position of the other agents.
	 * 
	 * @param perception
	 *            The current perception of the coordinator.
	 * @return TRUE if the coordinator perceived the other agents, FALSE if not.
	 */
	private boolean perceiveAgentsPositions(String perception) {
		if (perception.indexOf("<perception type=\"1\"") > -1) {
			int agent_id_index = perception.indexOf("<agent id=\"");
			while (agent_id_index > -1) {
				perception = perception.substring(agent_id_index + 11);
				String agent_id = perception.substring(0, perception
						.indexOf("\""));

				int agent_node_id_index = perception.indexOf("node_id=\"");
				perception = perception.substring(agent_node_id_index + 9);
				String agent_node_id = perception.substring(0, perception
						.indexOf("\""));

				this.AGENTS_POSITIONS.add(agent_id);
				this.AGENTS_POSITIONS.add(agent_node_id);

				agent_id_index = perception.indexOf("<agent id=\"");
			}

			System.err.println("Perceived " + this.AGENTS_POSITIONS.size() / 2
					+ " agents");

			// returns the success of the perception
			return true;
		}

		// default answer
		return false;
	}

	/**
	 * Lets the coordinator send orientations to the other agents.
	 * 
	 * @throws IOException
	 */
	private void sendOrientation() throws IOException {
		// sorts the agents based on their positions and the TSP calculated
		// solution
		LinkedList<String> sorted_agents = new LinkedList<String>();

		for (String solution_step : this.PLAN) {
			for (int i = 0; i < this.AGENTS_POSITIONS.size(); i = i + 2)
				if (this.AGENTS_POSITIONS.get(i + 1).equals(solution_step)){
					String currentSorted = this.AGENTS_POSITIONS.get(i);
					boolean alreadySorted = false;
					for(int k = 0; k < sorted_agents.size(); k++){
						if( sorted_agents.get(k).equals(currentSorted) ) {
							alreadySorted = true;
							break;
						}
					}
					if(!alreadySorted) sorted_agents.add(this.AGENTS_POSITIONS.get(i));
				}
			// if all the agents were sorted, quits the loop
			if (sorted_agents.size() == this.AGENTS_POSITIONS.size() / 2)
				break;
		}

		// holds the distance that must exist between two consecutive agents
		double distance = Math.ceil(this.solution_length
				* Math.pow(sorted_agents.size(), -1));

		// holds how many agents must pass the current one
		int let_pass = sorted_agents.size() - 1;

		// for each agent, in the reverse order
		for (int i = sorted_agents.size() - 1; i >= 0; i--) {
			// mounts an orientation message
			StringBuffer orientation = new StringBuffer();
			orientation.append(sorted_agents.get(i) + "###");

			for (String solution_step : this.PLAN)
				orientation.append(solution_step + ",");

			orientation.deleteCharAt(orientation.lastIndexOf(","));
			orientation.append("###");
			orientation.append(let_pass + ";");

			if (i == 0)
				orientation.append(0);
			else
				orientation.append(distance);

			// decrements the let pass value
			let_pass--;

			// sends a message with the orientation
			this.connection.send("<action type=\"3\" message=\""
					+ orientation.toString() + "\"/>");

			// if the simulation is a real time one, sends the message more 4
			// times
			if (this.connection instanceof UDPClientConnection)
				for (int j = 0; j < CycledCoordinatorAgent.NETWORK_QUALITY; j++) {
					/*try {
						this.sleep(5000);
					} catch (InterruptedException ie) {
						// do nothing
					}*/

					// sends a message with the orientation
					this.connection.send("<action type=\"3\" message=\""
							+ orientation.toString() + "\"/>");
				}
			
			System.err.println("Sent orientation.");
		}
	}
	
	public void run() {
		// starts its connection
		this.connection.start();

		// registers if the coordinator already sent orientation to the other
		// agents
		boolean sent_orientation = false;

		// registers if the agent perceived the tsp solution
		boolean perceived_tsp_solution = false;

		// registers if the agent perceived the other agents
		boolean perceived_other_agents = false;

		while (!this.stop_working) {
			if (!sent_orientation) {
				// obtains the current perceptions
				String[] perceptions = this.connection.getBufferAndFlush();

				// if the tsp solution was not perceived yet, tries to perceive
				// it
				if (!perceived_tsp_solution)
					for (int i = 0; i < perceptions.length; i++) {
						try {
							perceived_tsp_solution = this
									.perceiveTSPSolution(perceptions[i]);
						} catch (SAXException e) {
							e.printStackTrace();
						} catch (IOException e) {
							e.printStackTrace();
						}

						if (perceived_tsp_solution)
							break;
					}

				// if the other agents were not perceived yet, tries to perceive
				// them
				if (!perceived_other_agents)
					for (int i = 0; i < perceptions.length; i++) {
						perceived_other_agents = this
								.perceiveAgentsPositions(perceptions[i]);

						if (perceived_other_agents)
							break;
					}

				// if the coordinator perceived everything
				if (perceived_tsp_solution && perceived_other_agents) {
					// sends a proper orientation
					try {
						this.sendOrientation();
					} catch (IOException e) {
						e.printStackTrace();
					}

					// registers such action
					sent_orientation = true;
				}
			}
			// else, lets the agent do nothing
			else {
				// obtains the current perceptions
				String[] perceptions = this.connection.getBufferAndFlush();

				// for each one, tries to obtain the currently perceived graph
				Graph[] current_graph = new Graph[0];
				for (int i = 0; i < perceptions.length; i++) {
					try {
						current_graph = GraphTranslator
								.getGraphs(GraphTranslator
										.parseString(perceptions[i]));
					} catch (SAXException e) {
						e.printStackTrace();
					} catch (IOException e) {
						e.printStackTrace();
					}

					// if obtained a graph
					if (current_graph.length > 0) {
						// if the obtained graph is different from the current
						// one
						if (!current_graph[0].equals(this.graph)) {
							// updates the current graph
							this.graph = current_graph[0];

							// lets the agent do nothing
							try {
								this.connection.send("<action type=\"-1\"/>");
							} catch (IOException e) {
								e.printStackTrace();
							}
						}

						// quits the loop
						break;
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
	 * @param args Arguments: 
	 *             index 0: The IP address of the SimPatrol server.
	 *             index 1: The number of the socket that the server is supposed to listen to this client. 
	 *             index 2: "true", if the simulation is a real time one, "false" if not.
	 */
	public static void main(String args[]) {
		try {
			String server_address = args[0];
			int server_socket_number = Integer.parseInt(args[1]);
			boolean is_real_time_simulation = Boolean.parseBoolean(args[2]);

			CycledCoordinatorAgent coordinator = new CycledCoordinatorAgent();
			if (is_real_time_simulation)
				coordinator.setConnection(new UDPClientConnection(
						server_address, server_socket_number));
			else
				coordinator.setConnection(new TCPClientConnection(
						server_address, server_socket_number));

			coordinator.start();

			System.out.println("Press [t] key to terminate this agent.");
			String key = "";
			while (!key.equals("t"))
				key = Keyboard.readLine();

			coordinator.stopWorking();
		} catch (Exception e) {
			System.out
					.println("Usage:\n  \"java cycled.CycledCoordinatorAgent "
							+ "<IP address> <Remote socket number> <Is real time simulator? (true | false)>\"");
		}
	}	
}