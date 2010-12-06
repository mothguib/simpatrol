/* CycledCoordinatorAgent.java */

/* The package of this class. */
package cycled_OLD;

/* Imported classes and/or interfaces. */
import java.io.IOException;
import java.util.LinkedList;
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
 * Implements a coordinator that solves the TSP problem for a perceived graph,
 * in order to send the solution to the cycled agents in the environment, as
 * well synchronize them. Based in the work of [Chevaleyre, 2005].
 */
public class CycledCoordinatorAgent_OLD extends Agent_OLD {
	/* Attributes. */
	/**
	 * Expresses the quality of the network (i.e. the number of times the
	 * coordinator must send orientations to the other agents, due to UDP packet
	 * loss).
	 */
	private final static int NETWORK_QUALITY = 9;
	private final static int NB_AGENTS_PER_MESS = 5;

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
	
	LinkedList<String> oriented_agents;

	/* Methods. */
	/** Constructor. */
	public CycledCoordinatorAgent_OLD() {
		this.PLAN = new LinkedList<String>();
		this.solution_length = 0;
		this.AGENTS_POSITIONS = new LinkedList<String>();
		this.graph = null;
		this.oriented_agents = new LinkedList<String>();
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
			Node[] tsp_solution = null;
			double best_solution_length = Double.MAX_VALUE;

			for (int i = 0; i < 200; i++) {
				//System.out.println("i " + i);
				Node[] solution = graph.getTSPSolution();
				double solution_length = 0;

				for (int j = 1; j < solution.length; j++){
					Edge[] edges = graph.getDijkstraPath(solution[j - 1], solution[j]).getEdges();
					double length = 0;
					for(Edge edge : edges)
						length += Math.ceil(edge.getLength());
					solution_length += length;
				}
				
				//System.out.println("solution length " + solution_length);
				
				// we take into account the fact that the agents are taking 1 cycle to visit a node
				solution_length += solution.length - 1;

				if (solution_length < best_solution_length) {
					tsp_solution = solution;
					best_solution_length = solution_length;
				}
			}
			
			for (int i = 0; i < tsp_solution.length; i++) {
				if (i < tsp_solution.length - 1)
					this.PLAN.add(tsp_solution[i].getObjectId());
			}
			this.solution_length = best_solution_length;
			
			
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
		
		for (String plan : this.PLAN) {
			for (int i = 0; i < this.AGENTS_POSITIONS.size(); i = i + 2)
				if (this.AGENTS_POSITIONS.get(i + 1).equals(plan) 
						&& !sorted_agents.contains(this.AGENTS_POSITIONS.get(i))){
					sorted_agents.add(this.AGENTS_POSITIONS.get(i));
				}

			// if all the agents were sorted, quits the loop
			if (sorted_agents.size() >= this.AGENTS_POSITIONS.size() / 2)
				break;
		}
		
		if(sorted_agents.size() / 2 > 2){
			while(sorted_agents.get(0).equals(sorted_agents.get(1))){
				sorted_agents = new LinkedList<String>();
				this.PLAN.add(this.PLAN.pop());
				for (String plan : this.PLAN) {
					for (int i = 0; i < this.AGENTS_POSITIONS.size(); i = i + 2)
						if (this.AGENTS_POSITIONS.get(i + 1).equals(plan) 
								&& !sorted_agents.contains(this.AGENTS_POSITIONS.get(i))){
							sorted_agents.add(this.AGENTS_POSITIONS.get(i));
						}
					
					if (sorted_agents.size() >= this.AGENTS_POSITIONS.size() / 2)
						break;
				}
			}
		}

		// holds the distance that must exist between two consecutive agents
		double distance = Math.ceil(this.solution_length
				* Math.pow(sorted_agents.size(), -1));

		// holds how many agents must pass the current one
		int let_pass = sorted_agents.size() - 1 - oriented_agents.size();
		int sent = 0;

		StringBuffer orientation = new StringBuffer();
		for (String solution_step : this.PLAN)
			orientation.append(solution_step + ",");
		orientation.deleteCharAt(orientation.lastIndexOf(","));
		
		for (int i = sorted_agents.size() - 1; i >= 0; i--) {
			if(sent >= NB_AGENTS_PER_MESS)
				break;
			if(oriented_agents.contains(sorted_agents.get(i)))
				continue;
			
			orientation.append("###");
			orientation.append(sorted_agents.get(i) + ";");
			// let pass : 0 if it's the first, one for the others
			if(i==0)
				orientation.append(0 + ";");
			else
				orientation.append(1 + ";");
			// time to wait : 0 for the first, the let_pass * distance after the first agent has passed
			if (i == 0)
				orientation.append(0);
			else
				orientation.append(let_pass * distance);
			
			// decrements the let pass value
			let_pass--;
			sent++;	
			oriented_agents.add(sorted_agents.get(i));
		}
				
		this.connection.send("<action type=\"3\" message=\"" + orientation.toString() + "\"/>");
		try {
			sleep(5);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
			// if the simulation is a real time one, sends the message more 4
			// times
		if (this.connection instanceof UDPClientConnection_OLD)
			for (int j = 0; j < CycledCoordinatorAgent_OLD.NETWORK_QUALITY; j++) {
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
							perceived_tsp_solution = this.perceiveTSPSolution(perceptions[i]);
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
					if(oriented_agents.size() ==  this.AGENTS_POSITIONS.size() / 2)
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
	
	public void update(){

		if(!this.stop_working) {
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
	} else{
		// stops the connection of the agent
		try {
			this.connection.stopWorking();
		} catch (IOException e) {
			e.printStackTrace();
		}
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

			CycledCoordinatorAgent_OLD coordinator = new CycledCoordinatorAgent_OLD();
			if (is_real_time_simulation)
				coordinator.setConnection(new UDPClientConnection_OLD(
						server_address, server_socket_number));
			else
				coordinator.setConnection(new TCPClientConnection_OLD(
						server_address, server_socket_number));

			coordinator.start();

			System.out.println("Press [t] key to terminate this agent.");
			String key = "";
			while (!key.equals("t"))
				key = Keyboard.readLine();

			coordinator.stopWorking();
		} catch (Exception e) {
			System.out
					.println("Usage \"java heuristic_cognitive_coordinated.HeuristicCognitiveCoordinatorAgent\n"
							+ "<IP address> <Remote socket number> <Is real time simulator? (true | false)>\"");
		}
	}	
}