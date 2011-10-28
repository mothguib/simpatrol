package open.HPCC;

import java.io.IOException;
import java.util.LinkedList;

import javax.xml.parsers.ParserConfigurationException;

import org.xml.sax.SAXException;

import util.StringAndDouble;
import util.graph.Edge;
import util.graph.Graph;
import util.graph.Node;

import common.OpenAgent;

public class OpenHPCCAgent extends OpenAgent {

	/** The plan of walking through the graph. */
	private final LinkedList<String> PLAN;
	
	/** The current goal of the agent. */
	private String goal;
	private double length_to_goal;

	/** Holds if the simulation is a real time one. */
	private static boolean is_real_time_simulation;

	/**
	 * The time interval the agent is supposed to wait for a message sent by the
	 * coordinator. Measured in seconds.
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
	public OpenHPCCAgent(String id, boolean is_real_time) {
		super(id);
		this.PLAN = new LinkedList<String>();
		this.goal = null;
		this.graph = null;
		this.current_position = null;
		is_real_time_simulation = is_real_time;
	}
	
	
	public OpenHPCCAgent(String id, boolean is_real_time, double entering_time, double quitting_time, String society_id) {
		super(id, entering_time, quitting_time, society_id);

		this.PLAN = new LinkedList<String>();
		this.goal = null;
		this.graph = null;
		this.current_position = null;
		is_real_time_simulation = is_real_time;
	}
	
	
	/**
	 * Perceives a message sent by the coordinator with the goal node.
	 * 
	 * @see OpenHPCCCoordinator
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

			// if the message has the "###" conventioned mark
			int mark_index = message.indexOf("###");
			if (mark_index > -1)
				// if this message was sent to this agent
				if (message.substring(0, mark_index).equals(this.agent_id))
					// returns the id of the goal vertex
					return message.substring(mark_index + 3);
		}
		return null;
	}
	
	
	/**
	 * Lets the agent ask for a goal vertex to the coordinator.
	 * 
	 * @throws IOException
	 */
	private void requestGoal() throws IOException {
		if (this.current_position != null)
			this.SendMessage(this.agent_id
					+ "###" + this.current_position.STRING, "coordinator");
	}
	
	private void requestGoal(String current_node) throws IOException {
			this.SendMessage(this.agent_id
					+ "###" + current_node, "coordinator");
	}
	
	
	
	/**
	 * Lets the agent ask for a goal vertex to the coordinator.
	 * 
	 * @throws IOException
	 */
	private void sendQuitMessage() throws IOException {
			this.SendMessage(this.agent_id
					+ "###QUIT", "coordinator");
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
		// obtains the vertex of the beginning
		Node begin_node = new Node("");
		begin_node.setObjectId(position);

		// obtains the goal vertex
		Node end_node = new Node("");
		end_node.setObjectId(goal);

		// obtains the dijkstra path
		Graph path = graph.getIdlenessedDijkstraPath(begin_node, end_node);

		// adds the ordered vertexes in the plan of the agent
		Node[] path_nodes = path.getNodes();
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
				Node next_vertex = current_node_edges[0]
						.getOtherNode(current_node);

				if (this.PLAN.contains(next_vertex.getObjectId())
						|| next_vertex.equals(begin_node)) {
					current_node = current_node_edges[1]
							.getOtherNode(current_node);
				} else
					current_node = next_vertex;

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
			// obtains the id of the next vertex
			String next_node = this.PLAN.remove();
			
			length_to_goal = graph.getEdge(graph.getNode(this.current_position.STRING), graph.getNode(next_node)).getLength();
			this.visitCurrentNode(this.current_position.STRING);
			this.GoTo(next_node);
		}
	}
	
	
	
	@Override
	protected void inactive_run() {
		boolean time_actualized = false;
		String[] perceptions;
    	
		/**
		 * perceive graph and position
		 */
		perceptions = this.connection.getBufferAndFlush();
		if(perceptions.length == 0)
			perceptions = new String[] {""};
		
		while (!time_actualized || perceptions.length != 0) {
			
			for(int i = perceptions.length - 1; i > -1; i--){
				time_actualized |= perceiveTime(perceptions[i]);
				if(time_actualized)
					break;
			}
			
			perceptions = this.connection.getBufferAndFlush();
		}

	}

	@Override
	protected void activating_run() {
    	this.Activate(this.Society_id);
    	
		while (this.current_position == null && this.graph == null) {
			// obtains the perceptions sent by SimPatrol server
			String[] perceptions = this.connection.getBufferAndFlush();

			// for each perception, starting from the most recent one
			for (int i = perceptions.length - 1; i >= 0; i--) {
				
				perceiveTime(perceptions[i]);
				
				// tries to obtain the current position
				StringAndDouble sent_position = null;
				sent_position= this.perceiveCurrentPosition(perceptions[i]);
				
				if(sent_position != null )
					this.current_position = sent_position;

				
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
		
		this.inactive = false;
		this.engaging_in = false;
		
		

	}

	@Override
	protected void active_run() {
		if(this.PLAN.size() == 0 && (this.current_position == null || this.current_position.double_value == 0)){
			// lets the agent ask for a goal node to the coordinator
			if(this.current_position != null)
				try {
					this.requestGoal();
					if(OpenHPCCCoordinator.NO_COORD_HACK)
						this.Wait();
				} catch (IOException e) {
					e.printStackTrace();
				}
	
			// while the agent did not perceive its goal vertex
			// and the graph of the simulation
			String goal_node = null;
			Graph current_graph = this.graph;
			boolean time_actualized = false;
			
			if(!OpenHPCCCoordinator.NO_COORD_HACK)
				time_actualized = true;
			
			while ((current_graph == null || !time_actualized || goal_node == null) && !this.stop_working) {
				// obtains the perceptions sent by SimPatrol server
				String[] perceptions = this.connection.getBufferAndFlush();
	
				// for each perception, starting from the most recent one
				for (int i = perceptions.length - 1; i >= 0; i--) {
					
					//if(OpenHPCCCoordinator.NO_COORD_HACK)
						time_actualized |= perceiveTime(perceptions[i]);
					
					// tries to update the current position
					StringAndDouble perceived_position = this.perceiveCurrentPosition(perceptions[i]);
					if (perceived_position != null)
						if(this.current_position == null){
							this.current_position = perceived_position;
							try {
								this.requestGoal();
							} catch (IOException e) {
								e.printStackTrace();
							}
						}
						else 
							this.current_position = perceived_position;
					else {
						// tries to obtain the goal vertex
						String perceived_goal_node = this.perceiveGoalNode(perceptions[i]);
						if (perceived_goal_node != null) {
							goal_node = perceived_goal_node;
							this.goal = goal_node;
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
					 *
					// if the needed perceptions were obtained, breaks the loop
					if (goal_vertex != null && current_graph != null)
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
				
				if(OpenHPCCCoordinator.NO_COORD_HACK && time_actualized && goal_node == null){
					this.Wait();
					time_actualized = false;
				}
			}
	
			// lets the agent plan its actions
			this.plan(this.current_position.STRING, goal_node, current_graph);
			this.graph = current_graph;
			
		}
		
		// executes next step of the planning
		try {
			this.executeNextStep();
		} catch (IOException e) {
			e.printStackTrace();
		}

		// while the goal was not achieved
		
		StringAndDouble current_position = this.current_position;
		while ((current_position.STRING.equals(this.current_position.STRING) || current_position.double_value != 0 ) && !this.stop_working) {
			// perceives the current position of the agent
			
			String[] perceptions = this.connection.getBufferAndFlush();

			// for each perception, starting from the most recent one
			for (int i = perceptions.length - 1; i >= 0; i--){
				
				perceiveTime(perceptions[i]);
			
				// tries to obtain the current position
				if ((current_position = this.perceiveCurrentPosition(perceptions[i])) != null)
					break;
			}
		
			// if the the current perceived position is different from the
			// previous one
			if (current_position != null && !current_position.STRING.equals(this.current_position.STRING)){
				// updates the position of the agent
				this.current_position = current_position;
				break;
			}
			else {
				current_position = this.current_position;
			}
		}
	}
	

	@Override
	protected void deactivating_run() {
		try {
			this.sendQuitMessage();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		this.Deactivate();
		this.inactive = true;
		this.engaging_out = false;

	}
	
}
