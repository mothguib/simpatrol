package closed.gray_box_learner;


import java.io.IOException;
import java.util.LinkedList;

import org.xml.sax.SAXException;

import closed.gray_box_learner.q_learning_engine.QLearningEngine;
import closed.gray_box_learner.q_learning_engine.QState;
import closed.gray_box_learner.q_learning_engine.QStateSantana2004;

import util.StringAndDouble;
import util.graph.Edge;
import util.graph.Graph;
import util.graph.GraphTranslator;
import util.graph.Node;

import common.Agent;

public class MyGBLearnerAgent extends Agent {
	/* Attributes. */
	// holds the current perceptions of the agent
	public static boolean GENERALIZED;
	
	LinkedList<String> perceptions;
	
	// tries to find the ua and mo values
	boolean perceived_ua_mo = false;
	
	/** The engine that implements the q-learning algorithm. */
	private QLearningEngine learning_engine;

	/**
	 * Holds the neighborhood currently perceived by the agent, as well as the
	 * respective idlenesses.
	 * 
	 * ordered by name !
	 */
	private LinkedList<StringAndDouble> neighborhood;

	/** Holds the id of this agent. */
	private String id;

	/**
	 * Holds the directory to record the q-table values learned by the agent.
	 * Shared among all the gray-box learner agents.
	 */
	private static String q_table_dir;

	/** Holds the duration of the last action executed by the agent. */
	private double last_action_duration;

	/** The id of the current node. */
	private String node_id;

	/** The id of the last node visited by the agent. */
	private String last_node_id;

	/**
	 * The id of the node with the biggest idleness in the
	 * neighborhood.
	 */
	private String biggest_neighbor_id;
	private String smallest_neighbor_id;

	/**
	 * Vector containing the index of neighboring nodes already chosen as the next node of another
	 * agent.
	 * 
	 * na is as long as number of actions per state (ie the number of neighbors of the node)
	 */
	private int[] target_neighbors;
	
	// tries to perceive the nid attribute
	boolean perceived_nid = false;
	
	private boolean stopped = false;

	/* Methods. */
	/** Constructor. */
	public MyGBLearnerAgent() {
		this.learning_engine = null;

		this.neighborhood = null;
		this.id = null;

		this.last_action_duration = 0;

		this.node_id = "";
		this.last_node_id = "v1";
		this.biggest_neighbor_id = "";
	}
	
	public boolean isStopped() {
		return this.stopped;
	}

	/**
	 * Configures some parameters of the learning engine.
	 * 
	 * @param e
	 *            The probability of an agent choose an exploration action.
	 * @param alfa_decay_rate
	 *            The rate of the decaying of the alpha value in the q-learning
	 *            algorithm.
	 * @param gama
	 *            The discount factor in the q-learning algorithm.
	 * @param state_items_cardinality
	 *            Array that holds the number of possible values for each item
	 *            of a state.
	 * @param actions_per_state_count
	 *            The maximum number of possible actions per state.
	 */
	public static void configureLearningEngine(double e,
			double alfa_decay_rate, double gama, int[] state_items_cardinality,
			int actions_per_state_count) {
		QLearningEngine.setQLearningConfiguration(e, alfa_decay_rate, gama);
		QLearningEngine.setStateItemsCardinality(state_items_cardinality);
		QLearningEngine.setActionsPerStateCount(actions_per_state_count);
		
	}
	
	/**
	 * Configures the directory to record the q-table values.
	 * 
	 * @param q_table_dir
	 *            The directory to record the q-table values learned by the
	 *            agent.
	 */
	public static void setQtableDirectory(String q_table_dir) {
		MyGBLearnerAgent.q_table_dir = q_table_dir;
	}

	/**
	 * Configures if the agents is learning to patrol the environment.
	 * 
	 * @param is_learning_phase
	 *            TRUE if the agent is in its learning phase, FALSE if not.
	 */
	public static void setIsLearningPhase(boolean is_learning_phase) {
		QLearningEngine.setIsLearningPhase(is_learning_phase);
	}

	/**
	 * Lets the agent perceive the id of its current node, given the string of
	 * a perception.
	 * 
	 * @param perception
	 *            A string representing the current perception of the agent.
	 * @return TRUE if the agent perceived a new node, FALSE if not.
	 */
	private boolean perceiveCurrentPosition(String perception) {
		if (perception.indexOf("<perception type=\"4\"") > -1) {
			// obtains the id of the agent, if it's still null
			if (this.id == null) {
				int agent_id_index = perception.indexOf("<agent id=\"");
				perception = perception.substring(agent_id_index + 11);
				this.id = perception.substring(0, perception.indexOf("\""));
			}

			// obtains the id of the current node
			int node_id_index = perception.indexOf("node_id=\"");
			perception = perception.substring(node_id_index + 9);
			String node_id = perception.substring(0, perception.indexOf("\""));;

			// if such nid is different from the previous nid, updates it
			if (!this.node_id.equals(node_id)) {
				// registers the previous nid temporarily on the ua attribute
				this.last_node_id = this.node_id;

				// updates the nid
				this.node_id = node_id;

				// signalizes that the nid was updated
				return true;
			}
		}

		return false;
	}
	
	
	/**
	 * Lets the agent perceive the index of the id of the node with the biggest
	 * idleness in the neighborhood, as well as the index of the id of the last
	 * node visited by the agent.
	 * 
	 * @param perception
	 *            A string representing the current perception of the agent.
	 * @return TRUE if the agent perceived the items, FALSE if not.
	 * @throws IOException
	 * @throws SAXException
	 */
	private boolean perceiveNeighbors(String perception) throws SAXException,
			IOException {
		if (perception.indexOf("<perception type=\"0\"") > -1) {
			
			Graph[] graph_perception = GraphTranslator.getGraphs(GraphTranslator
					.parseString(perception));
			
			if(graph_perception.length <= 0)
				return false;
			
			// holds the id of the nodes found in the neighborhood, as well
			// as the respective idlenesses
			LinkedList<StringAndDouble> neighborhood = new LinkedList<StringAndDouble>();
			
			// gets all the neighbors of the current node
			for( Node node : graph_perception[0].getNodes()){
				if(node.getLabel().equals(this.node_id)){
					Edge[] edges = node.getEdges();
					for( Edge edge : edges){
						Node neighbor = edge.getOtherNode(node);
						neighborhood.add(new StringAndDouble(neighbor.getLabel(), neighbor.getIdleness()));
					}
					break;
				}
					
			}

			// sorts the nodes of the neighborhood by name
			for (int i = 0; i < neighborhood.size(); i++) {
				StringAndDouble current_node_idleness = neighborhood.get(i);

				for (int j = i + 1; j < neighborhood.size(); j++) {
					StringAndDouble other_node_idleness = neighborhood.get(j);

					if (Integer.parseInt(current_node_idleness.STRING
							.substring(1)) > Integer
							.parseInt(other_node_idleness.STRING.substring(1))) {
						neighborhood.set(i, other_node_idleness);
						neighborhood.set(j, current_node_idleness);
						current_node_idleness = other_node_idleness;
					}
				}
			}

			// configures the duration of the last action
			this.perceiveLastActionDuration(graph_perception[0]);

			// configures the mo value
			// holds the biggest idleness found by the agent
			double biggest_idleness = (-1) * Double.MAX_VALUE;
			double smallest_idleness = Double.MAX_VALUE;

			// for each node in the neighborhood, finds the one with the
			// biggest idleness
			for (StringAndDouble node_idleness : neighborhood){
				if (node_idleness.double_value > biggest_idleness) {
					this.biggest_neighbor_id = node_idleness.STRING;
					biggest_idleness = node_idleness.double_value;
				}
				else if (node_idleness.double_value < smallest_idleness) {
					this.smallest_neighbor_id = node_idleness.STRING;
					smallest_idleness = node_idleness.double_value;
				}
			}

			// configures the current neighborhood of the agent
			this.neighborhood = neighborhood;
			this.target_neighbors = new int[neighborhood.size()];

			// returns the success of the operation
			return true;
		}

		return false;
	}
	
	/**
	 * Perceives the index of the id of a node already chosen as the next node
	 * of another agent in the neighborhood.
	 * 
	 * @param perception
	 *            A string representing the current perception of the agent.
	 * @return TRUE if the agent perceived the items, FALSE if not.
	 */
	private boolean perceiveTargets(String perception) {
		if (perception.indexOf("<perception type=\"3\"") > -1) {
			// obtains the sent message
			int message_index = perception.indexOf("message=\"");
			perception = perception.substring(message_index + 9);
			String message = perception.substring(0, perception.indexOf("\""));

			// such message is the goal of an agent, so tries to configure the
			// na attribute
			for (int i = 0; i < this.neighborhood.size(); i++)
				if (this.neighborhood.get(i).STRING.equals(message)) {
					//System.err.println("Somebody going to " + message);

					this.target_neighbors[i] = 1;
					return true;
				}
		}

		return false;
	}
	
	/**
	 * Lets the agent configure the duration of its last action, given a
	 * perception of the environment. (ie the length of the edge between nid and ua)
	 * 
	 * @param perception
	 *            A string representing a perception of the environment.
	 */
	private void perceiveLastActionDuration(Graph graph) {
		
		for(Edge edge : graph.getEdges()){
			boolean is_connected_to_nid = false;
			boolean is_connected_to_ua = false;
			for(Node node : edge.getNodees()){
				is_connected_to_nid |= node.getLabel().equals(node_id);
				is_connected_to_ua |= node.getLabel().equals(last_node_id);
			}
			if(is_connected_to_nid && is_connected_to_ua){
				this.last_action_duration = edge.getLength();
				return;
			}
			
		}

		this.last_action_duration = 0;
	}
	
	/**
	 * Sends messages to the SimPatrol server to go to a node of the
	 * neighborhood, as well as broadcast a message with such node id. Returns
	 * the current idleness of such node.
	 * 
	 * @param action_id
	 *            The id of the action to be executed by the agent.
	 * @return The idleness of the node chosen as the next goal.
	 * @throws IOException
	 */
	private void goToAndSendMessage(int action_id) throws IOException {
		// obtains the goal node and its idleness
		StringAndDouble goal_node_idleness = this.neighborhood.get(action_id);

		// the goal node
		String goal_node = goal_node_idleness.STRING;
		//System.err.println("Goal node " + goal_node);

		// sends the message with its goal node
		String message_2 = "<action type=\"3\" message=\"" + goal_node
				+ "\"/>";
		this.connection.send(message_2);

		// sends the go to action
		String message_1 = "<action type=\"1\" node_id=\"" + goal_node
				+ "\"/>";
		this.connection.send(message_1);
		
	}

	private double getWLUReward(int action_id){
		// returns the idleness of the goal node, if nobody will visit it too
		if (this.target_neighbors[action_id] == 1)
			return 0;
		else if (this.biggest_neighbor_id.equals(this.neighborhood.get(action_id).STRING)) {
			System.err.println("Going to the idlest.");
			return Math.pow(this.neighborhood.get(action_id).double_value, 2);
		} else
			return this.neighborhood.get(action_id).double_value;
	}
	
	private double getSUReward(int action_id){
		return this.neighborhood.get(action_id).double_value;
	}
	
	/**
	 * Sends a message to the SimPatrol server to register a visit onto the
	 * current node of the agent.
	 * 
	 * @throws IOException
	 */
	private void visit() throws IOException {
		// sends the order to the server to visit the current position
		String message = "<action type=\"2\"/>";
		this.connection.send(message);
	}
	
	private QState getQStateValue() {
		// we get the node idea between 0 and n_max - 1
		// because the nodes are "v1" to "vn-max"
		int nid = Integer.valueOf(this.node_id.substring(1)) - 1;
		
		int origin_index = 0;
		int biggest_index = 0;
		
		int i = 0;
		for(StringAndDouble neighbor : neighborhood){
			if(neighbor.STRING.equals(this.last_node_id))
				origin_index = i;
			if(neighbor.STRING.equals(this.biggest_neighbor_id))
				biggest_index = i;
				
		}
		
		return new QStateSantana2004(nid, origin_index, biggest_index, this.target_neighbors);
	}

	
	public void run() {
		// starts its connection
		this.connection.start();

		// holds the current perceptions of the agent
		LinkedList<String> perceptions = new LinkedList<String>();

		// tries to perceive the nid attribute
		boolean perceived_nid = false;
		while (!perceived_nid) {
			// fills the list with the current perceptions of the agent
			String[] current_perceptions = this.connection.getBufferAndFlush();
			for (int i = 0; i < current_perceptions.length; i++)
				perceptions.add(current_perceptions[i]);

			// for each perception of the list, tries to find the nid value
			for (int i = perceptions.size() - 1; i >= 0; i--) {
				perceived_nid = this.perceiveCurrentPosition(perceptions.get(i));

				if (perceived_nid) {
					System.err.println("current node " + this.node_id);
					break;
				}
			}
		}

		// tries to find the ua and mo values
		boolean perceived_mo = false;
		while (!perceived_mo) {
			// fills the list with the current perceptions of the agent
			String[] current_perceptions = this.connection.getBufferAndFlush();
			for (int i = 0; i < current_perceptions.length; i++)
				perceptions.add(current_perceptions[i]);

			// for each perception of the list, tries to find the ua and mo
			// values
			for (int i = perceptions.size() - 1; i >= 0; i--) {
				try {
					perceived_mo = this.perceiveNeighbors(perceptions.get(i));
				} catch (SAXException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				}

				if (perceived_mo) {
					System.err.println(this.neighborhood);
					break;
				}
			}
		}

		// perceives targets
		{
			// fills the list with the current perceptions of the agent
			String[] current_perceptions = this.connection.getBufferAndFlush();
			for (int i = 0; i < current_perceptions.length; i++)
				perceptions.add(current_perceptions[i]);

			// for each perception of the list, tries to find the na value
			for (int i = perceptions.size() - 1; i >= 0; i--) {
				this.perceiveTargets(perceptions.get(i));
			}
		}

		// mounts the array with the current state item values
		QState state_values = this.getQStateValue();

		// initializes the q-learning engine
		this.learning_engine = new QLearningEngine(state_values, MyGBLearnerAgent.q_table_dir + this.id + ".txt");
		this.learning_engine.start();

		// while the agent is supposed to work
		while (!this.stop_working) {
			// resets the perceptions
			perceptions.clear();
			
			// visits the current node
			try {
				this.visit();
			} catch (IOException e) {
				e.printStackTrace();
			}

			// configures the number of possible actions for the current state
			this.learning_engine.setPossibleActionsCount(this.neighborhood.size());

			// holds the reward for the current action
			double reward = 0;

			// executes the next action (the -1 compensates a +1 in the QLearning_engine...
			int action_id = this.learning_engine.getActionId() - 1;
			try {
				this.goToAndSendMessage(action_id);
			} catch (IOException e) {
				e.printStackTrace();
			}
			reward = this.getSUReward(action_id);

			// tries to perceive the nid attribute
			perceived_nid = false;
			while (!perceived_nid && !this.stop_working) {
				// fills the list with the current perceptions of the agent
				String[] current_perceptions = this.connection.getBufferAndFlush();
				
				for (int i = 0; i < current_perceptions.length; i++)
					perceptions.add(current_perceptions[i]);

				// for each perception of the list, tries to find the nid value
				for (int i = perceptions.size() - 1; i >= 0; i--) {
					perceived_nid = this.perceiveCurrentPosition(perceptions.get(i));

					if (perceived_nid) {
						System.err.println("current node nid= " + this.node_id);
						break;
					}
				}
			}

			// tries to find the ua and mo values
			perceived_mo = false;
			while (!perceived_mo && !this.stop_working) {
				// fills the list with the current perceptions of the agent
				String[] current_perceptions = this.connection.getBufferAndFlush();
				
				for (int i = 0; i < current_perceptions.length; i++)
					perceptions.add(current_perceptions[i]);

				// for each perception of the list, tries to find the ua and mo
				// values
				for (int i = perceptions.size() - 1; i >= 0; i--) {
					try {
						perceived_mo = this.perceiveNeighbors(perceptions.get(i));
					} catch (SAXException e) {
						e.printStackTrace();
					} catch (IOException e) {
						e.printStackTrace();
					}

					if (perceived_mo) {
						System.err.println(this.neighborhood);
						break;
					}
				}
			}

			// verifies if this thread shall continue working
			if (this.stop_working)
				break;

			// tries to find the na value
			{
				// fills the list with the current perceptions of the agent
				String[] current_perceptions = this.connection.getBufferAndFlush();
				
				for (int i = 0; i < current_perceptions.length; i++)
					perceptions.add(current_perceptions[i]);

				// for each perception of the list, tries to find the na value
				for (int i = perceptions.size() - 1; i >= 0; i--) {
					this.perceiveTargets(perceptions.get(i));

				}
			}

			// sets the duration of the last action to the engine
			this.learning_engine.setLastActionDuration(this.last_action_duration);
			System.err.println("Action duration " + this.last_action_duration);

			// configures the reward of the action
			this.learning_engine.setReward(reward + this.last_action_duration);

			// mounts the array with the next state item values and configures
			// it into the engine
			QState next_item_values = this.getQStateValue();
			this.learning_engine.setNextState(next_item_values);
			
			
			while(!this.learning_engine.is_updated())
				try {
					sleep(2);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
		}

		// stops the q-learning engine
		this.learning_engine.stopWorking();
		
		this.stopped = true;

		// stops the connection of the agent
		try {
			this.connection.stopWorking();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	
}
