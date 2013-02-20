package HPCC;

import java.io.IOException;
import java.util.LinkedList;

import javax.xml.parsers.ParserConfigurationException;

import open.HPCC.OpenHPCCCoordinator;

import org.xml.sax.SAXException;

import util.HS_Graph;
import util.StringAndDouble;
import util.graph.Edge;
import util.graph.Graph;
import util.graph.Node;

import common.OpenAgent;

public class HS_OpenHPCCAgent extends OpenAgent {

	/** The plan of walking through the graph. */
	private final LinkedList<String> PLAN;
	
	/** The current goal of the agent. */
	private String goal;
	private String next_node_to_goal;
//	private boolean moving = false;
	private double length_to_goal;
	
	private boolean time_updated = false;

	/** Holds if the simulation is a real time one. */
	private static boolean is_real_time_simulation;

	/**
	 * The time interval the agent is supposed to wait for a message sent by the
	 * coordinator. Measured in seconds.
	 */
	private final int WAITING_TIME = 30; // 30 seconds
	
	
	private final HS_Graph complete_graph;
	private final int MAX_SEARCH_HOPS;
	
	//private HS_Graph graph;

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
	public HS_OpenHPCCAgent(String id, boolean is_real_time, HS_Graph complete_graph, int max_search_hops) {
		super(id);
		this.complete_graph = complete_graph;
		this.MAX_SEARCH_HOPS = max_search_hops;
		
		this.PLAN = new LinkedList<String>();
		this.goal = null;
		this.graph = null;
		this.current_position = null;
		is_real_time_simulation = is_real_time;
	}
	
	
	public HS_OpenHPCCAgent(String id, boolean is_real_time, double entering_time, double quitting_time, String society_id
			, HS_Graph complete_graph, int max_search_hops) {
		super(id, entering_time, quitting_time, society_id);
		this.complete_graph = complete_graph;
		this.MAX_SEARCH_HOPS = max_search_hops;
		
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
					+ "###" + this.current_position.STRING + "#" + (int)this.time, "coordinator");
	}
	
	private void requestGoal(String current_node) throws IOException {
			this.SendMessage(this.agent_id
					+ "###" + current_node + "#" + (int)this.time, "coordinator");
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
	
	private void sendVisitMessage(String node) throws IOException {
			this.SendMessage(this.agent_id
					+ "###VISIT" + node + "#" + (int)this.time, "coordinator");
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
		Graph work_graph = null;
		this.PLAN.clear();
		
		//is the goal in the graph ?
		if(graph.getNode(goal) == null && graph.getNodes().length != 0){
			// what is the average idleness ?
			double av_idle = 0;
			Node[] nodes = graph.getNodes();
			for(int i = 0; i < nodes.length; i++)
				av_idle += nodes[i].getIdleness();
			av_idle /= nodes.length;
			
			int nbhops = MAX_SEARCH_HOPS;
			HS_Graph graph2 = complete_graph.getSubgraphByHops(complete_graph.getNode(position), MAX_SEARCH_HOPS);
			while(graph2.getNode(goal) == null){
				nbhops++;
				graph2 = complete_graph.getSubgraphByHops(complete_graph.getNode(position), nbhops);
			}
			
			for(int i = 0; i < graph2.getNodes().length; i++){
				if(graph.getNode(graph2.getNodes()[i].getObjectId()) != null)
					graph2.getNodes()[i].setIdleness(graph.getNode(graph2.getNodes()[i].getObjectId()).getIdleness());
				else 
					graph2.getNodes()[i].setIdleness(av_idle);
			}
			
			double D = graph2.getDiameter();
			work_graph = graph2;
			
		}
		else 
			work_graph = new HS_Graph("bla", graph.getNodes());
		
		
		
		
		
		// obtains the vertex of the beginning
		Node begin_node = new Node("");
		begin_node.setObjectId(position);

		// obtains the goal vertex
		Node end_node = new Node("");
		end_node.setObjectId(goal);

		// obtains the dijkstra path
		Graph path = work_graph.getIdlenessedDijkstraPath(begin_node, end_node);

		
		// adds the ordered vertexes in the plan of the agent
		Node[] path_nodes = path.getNodes();
		for (int i = 0; i < path_nodes.length; i++)
			if (path_nodes[i].equals(begin_node)) {
				begin_node = path_nodes[i];
				break;
			}

		if (begin_node.getEdges().length > 0) {
			Node current_node = begin_node.getEdges()[0].getOtherNode(begin_node);
			Edge[] current_node_edges = current_node.getEdges();
			this.PLAN.add(current_node.getObjectId());

			while (current_node_edges.length > 1) {
				Node next_vertex = current_node_edges[0].getOtherNode(current_node);

				if (this.PLAN.contains(next_vertex.getObjectId())
						|| next_vertex.equals(begin_node)) {
					current_node = current_node_edges[1].getOtherNode(current_node);
				} else
					current_node = next_vertex;

				this.PLAN.add(current_node.getObjectId());
				current_node_edges = current_node.getEdges();
			}
		}
		
		this.graph = work_graph;
	}


	
	/**
	 * Lets the agent execute next step of its planning.
	 * 
	 * @throws IOException
	 */
	private void executeNextStep() throws IOException {
		if(this.current_position.STRING.equals(this.next_node_to_goal)){
			this.next_node_to_goal = null;
		}
		// verify that the current position is the node we tried to have
		if(this.next_node_to_goal != null && !this.current_position.STRING.equals(this.next_node_to_goal)){
			this.sendVisitMessage(this.current_position.STRING);
			this.visitCurrentNode(this.current_position.STRING);
		}
		
		
		else if (!this.PLAN.isEmpty()) {
			// obtains the id of the next vertex
			this.next_node_to_goal = this.PLAN.remove();
			
			while(this.next_node_to_goal.equals(this.current_position.STRING))
				if(!this.PLAN.isEmpty())
					this.next_node_to_goal = this.PLAN.remove();
				else
					break;
			
			if(!this.next_node_to_goal.equals(this.current_position.STRING)){
				length_to_goal = complete_graph.getEdge(complete_graph.getNode(this.current_position.STRING), complete_graph.getNode(this.next_node_to_goal)).getLength();
				this.sendVisitMessage(this.current_position.STRING);
				this.visitCurrentNode(this.current_position.STRING);
				this.GoTo(this.next_node_to_goal);
			}
			
		}
		else 
			this.next_node_to_goal = null;
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
		if(this.PLAN.size() == 0 && this.next_node_to_goal == null && 
				(this.current_position == null || this.current_position.double_value == 0)){
			// lets the agent ask for a goal node to the coordinator
			if(this.current_position != null)
				try {
					this.requestGoal();
					if(HS_OpenHPCCCoordinator.NO_COORD_HACK)
						this.Wait();
				} catch (IOException e) {
					e.printStackTrace();
				}
		}
	
		// while the agent did not perceive its goal vertex
		// and the graph of the simulation
		String goal_node = null;
		Graph current_graph = this.graph;

		boolean time_needs_update = (this.next_node_to_goal != null && !this.current_position.STRING.equals(this.next_node_to_goal));
		//if(!HS_OpenHPCCCoordinator.NO_COORD_HACK)
		//	time_actualized = true;

		while ((current_graph == null || (time_needs_update && !this.time_updated) || goal_node == null) && !this.stop_working) {
			if(this.next_node_to_goal != null)
				goal_node = this.next_node_to_goal;

			// obtains the perceptions sent by SimPatrol server
			String[] perceptions = this.connection.getBufferAndFlush();

			// for each perception, starting from the most recent one
			for (int i = perceptions.length - 1; i >= 0; i--) {

				//if(HS_OpenHPCCCoordinator.NO_COORD_HACK)
				this.time_updated |= perceiveTime(perceptions[i]);

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
					if (goal_node == null && perceived_goal_node != null) {
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

			if(HS_OpenHPCCCoordinator.NO_COORD_HACK && this.time_updated && goal_node == null){
				this.Wait();
				this.time_updated = false;
			}
		}
	
		// lets the agent plan its actions
		if(this.next_node_to_goal == null)
			this.plan(this.current_position.STRING, goal_node, current_graph);
		//this.graph = current_graph;
		
		// executes next step of the planning
		try {
			this.executeNextStep();
		} catch (IOException e) {
			e.printStackTrace();
		}
		if(this.next_node_to_goal != null && !this.current_position.STRING.equals(this.next_node_to_goal))
			this.time_updated = false;
		
		// while the goal was not achieved
		
		StringAndDouble current_position = this.current_position;
		while ((current_position.STRING.equals(this.current_position.STRING) || current_position.double_value != 0 ) && 
				this.next_node_to_goal != null && !this.stop_working) {
			// perceives the current position of the agent
			
			
			String[] perceptions = this.connection.getBufferAndFlush();

			// for each perception, starting from the most recent one
			for (int i =0; i < perceptions.length; i++)		
				this.time_updated |= perceiveTime(perceptions[i]);
			
				// tries to obtain the current position
			for (int i = perceptions.length - 1; i >= 0; i--)	
				if ((current_position = this.perceiveCurrentPosition(perceptions[i])) != null)
					break;
			
		
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
