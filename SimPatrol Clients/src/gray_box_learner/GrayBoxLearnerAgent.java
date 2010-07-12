/* GrayBoxLearnerAgent.java */

/* The package of this class. */
package gray_box_learner;

/* Imported classes and/or interfaces. */
import gray_box_learner.q_learning_engine.QLearningEngine;
import java.io.IOException;
import java.util.LinkedList;
import org.xml.sax.SAXException;
import util.Keyboard;
import util.net.TCPClientConnection;
import util.net.UDPClientConnection;
import common.Agent;

/**
 * Implements gray box learner agents, with selfish utility, as it is described
 * in the work of SANTANA [2004].
 */
public class GrayBoxLearnerAgent extends Agent {
	/* Attributes. */
	// holds the current perceptions of the agent
	LinkedList<String> perceptions;
	
	// tries to find the ua and mo values
	boolean perceived_ua_mo = false;
	
	/** The engine that implements the q-learning algorithm. */
	private QLearningEngine learning_engine;

	/**
	 * Holds the neighborhood currently perceived by the agent, as well as the
	 * respective idlenesses.
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
	private int nid;

	/** The index of the id of the last node visited by the agent. */
	private int ua;

	/**
	 * The index of the id of the node with the biggest idleness in the
	 * neighborhood.
	 */
	private int mo;

	/**
	 * The index of the id of a node already chosen as the next node of another
	 * agent.
	 */
	private int na;
	
	// tries to perceive the nid attribute
	boolean perceived_nid = false;

	/* Methods. */
	/** Constructor. */
	public GrayBoxLearnerAgent() {
		this.learning_engine = null;

		this.neighborhood = null;
		this.id = null;

		this.last_action_duration = 0;

		this.nid = 0;
		this.ua = 0;
		this.mo = 0;
		this.na = 0;
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
		GrayBoxLearnerAgent.q_table_dir = q_table_dir;
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
	private boolean perceiveNid(String perception) {
		if (perception.indexOf("<perception type=\"4\"") > -1) {
			// obtains the id of the agent, if it's still null
			if (this.id == null) {
				int agent_id_index = perception.indexOf("<agent id=\"");
				perception = perception.substring(agent_id_index + 11);
				this.id = perception.substring(0, perception.indexOf("\""));
			}

			// obtains the id of the current node
			int node_id_index = perception.indexOf("node_id=\"");
			perception = perception.substring(node_id_index + 11);
			String node_id = perception
					.substring(0, perception.indexOf("\""));

			// converts such id to a natural number
			int nid = Integer.parseInt(node_id.substring(1));

			// if such nid is different from the previous nid, updates it
			if (this.nid != nid) {
				// registers the previous nid temporarily on the ua attribute
				this.ua = this.nid;

				// updates the nid
				this.nid = nid;

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
	private boolean perceiveUaMo(String perception) throws SAXException,
			IOException {
		if (perception.indexOf("<perception type=\"0\"") > -1) {
			// if the current perception was not obtained from the current
			// position, returns false
			// obtains a copy of the current perception
			String perception_copy = perception.substring(0);

			// obtains the index of the next source id
			int source_id_index = perception_copy.indexOf("source_id=\"");

			// while there is an source id
			while (source_id_index > -1) {
				// updates the perception copy
				perception_copy = perception_copy
						.substring(source_id_index + 12);

				// obtains the source id
				String source_id = perception_copy.substring(0,
						perception_copy.indexOf("\""));

				// obtains the index of the next target id
				int target_id_index = perception_copy
						.indexOf("target_id=\"");

				// updates the perception copy
				perception_copy = perception_copy
						.substring(target_id_index + 14);

				// obtains the target id
				String target_id = perception_copy.substring(0,
						perception_copy.indexOf("\""));

				// if none of the id is related to the nid value, returns false
				if (Integer.parseInt(source_id.substring(1)) != this.nid
						&& Integer.parseInt(target_id.substring(1)) != this.nid)
					return false;

				// updates the next source id index
				source_id_index = perception_copy.indexOf("source_id=\"");
			}

			// holds the id of the nodes found in the neighborhood, as well
			// as the respective idlenesses
			LinkedList<StringAndDouble> neighborhood = new LinkedList<StringAndDouble>();

			// holds the index of the next node
			int next_node_index = perception.indexOf("<node id=\"");

			// while there are nodes to be read
			while (next_node_index > -1) {
				// updates the perception
				perception = perception.substring(next_node_index + 12);

				// obtains the id of the current node
				String node_id = perception.substring(0, perception
						.indexOf("\""));

				// obtains the idleness of such node
				int idleness_index = perception.indexOf("idleness=\"");
				perception = perception.substring(idleness_index + 10);
				double idleness = Double.parseDouble(perception.substring(0,
						perception.indexOf("\"")));

				// adds the id of the node to the list of obtained
				// nodes, if it is not the current node of the agent
				if (Integer.parseInt(node_id.substring(1)) != this.nid)
					neighborhood.add(new StringAndDouble(node_id, idleness));

				// obtains the index of the next node
				next_node_index = perception.indexOf("<node id=\"");
			}

			// sorts the nodes of the neighborhood
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
			this.perceiveLastActionDuration(perception);

			// configures the ua value
			if (this.ua == 0)
				this.ua = 1;
			else
				for (int i = 0; i < neighborhood.size(); i++)
					if (Integer.parseInt(neighborhood.get(i).STRING
							.substring(1)) == this.ua) {
						this.ua = i + 1;
						break;
					}

			// configures the mo value
			// holds the biggest idleness found by the agent
			double biggest_idleness = (-1) * Double.MAX_VALUE;

			// for each node in the neighborhood, finds the one with the
			// biggest idleness
			for (StringAndDouble node_idleness : neighborhood)
				if (node_idleness.DOUBLE > biggest_idleness) {
					this.mo = neighborhood.indexOf(node_idleness) + 1;
					biggest_idleness = node_idleness.DOUBLE;
				}

			// configures the current neighborhood of the agent
			this.neighborhood = neighborhood;

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
	private boolean perceiveNa(String perception) {
		if (perception.indexOf("<perception type=\"3\"") > -1) {
			// obtains the sent message
			int message_index = perception.indexOf("message=\"");
			perception = perception.substring(message_index + 9);
			String message = perception.substring(0, perception.indexOf("\""));

			// such message is the goal of an agent, so tries to configure the
			// na attribute
			for (int i = 0; i < this.neighborhood.size(); i++)
				if (this.neighborhood.get(i).STRING.equals(message)) {
					System.err.println("Somebody going to " + message);

					this.na = i + 2;
					return true;
				}
		}

		return false;
	}

	/**
	 * Lets the agent configure the duration of its last action, given a
	 * perception of the environment.
	 * 
	 * @param perception
	 *            A string representing a perception of the environment.
	 */
	private void perceiveLastActionDuration(String perception) {
		int source_index = perception.indexOf("source_id=\"");
		int target_index = 0;

		while (source_index > -1) {
			perception = perception.substring(source_index + 12);
			String source_id = perception.substring(0, perception
					.indexOf("\""));

			target_index = perception.indexOf("target_id=\"");
			perception = perception.substring(target_index + 14);
			String target_id = perception.substring(0, perception
					.indexOf("\""));

			int int_source_id = Integer.parseInt(source_id.substring(1));
			int int_target_id = Integer.parseInt(target_id.substring(1));

			if ((int_source_id == this.nid && int_target_id == this.ua)
					|| (int_source_id == this.ua && int_target_id == this.nid)) {
				int length_index = perception.indexOf("length=\"");
				perception = perception.substring(length_index + 8);
				String str_length = perception.substring(0, perception
						.indexOf("\""));

				this.last_action_duration = Double.parseDouble(str_length);
				return;
			}

			source_index = perception.indexOf("source_id=\"");
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
	private double goToAndSendMessage(int action_id) throws IOException {
		// obtains the goal node and its idleness
		StringAndDouble goal_node_idleness = this.neighborhood
				.get(action_id - 1);

		// the goal node
		String goal_node = goal_node_idleness.STRING;
		System.err.println("Goal node " + goal_node);

		// sends the message with its goal node
		String message_2 = "<action type=\"3\" message=\"" + goal_node
				+ "\"/>";
		this.connection.send(message_2);

		// sends the go to action
		String message_1 = "<action type=\"1\" node_id=\"" + goal_node
				+ "\"/>";
		this.connection.send(message_1);

		// returns the idleness of the goal node, if nobody will visit it too
		if (this.na - 1 == action_id)
			return 0;
		else if (this.mo == action_id) {
			System.err.println("Going to the idlest.");
			return Math.pow(goal_node_idleness.DOUBLE, 2);
		} else
			return goal_node_idleness.DOUBLE;
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
				perceived_nid = this.perceiveNid(perceptions.get(i));

				if (perceived_nid) {
					System.err.println("current node " + this.nid);
					break;
				}
			}
		}

		// tries to find the ua and mo values
		boolean perceived_ua_mo = false;
		while (!perceived_ua_mo) {
			// fills the list with the current perceptions of the agent
			String[] current_perceptions = this.connection.getBufferAndFlush();
			for (int i = 0; i < current_perceptions.length; i++)
				perceptions.add(current_perceptions[i]);

			// for each perception of the list, tries to find the ua and mo
			// values
			for (int i = perceptions.size() - 1; i >= 0; i--) {
				try {
					perceived_ua_mo = this.perceiveUaMo(perceptions.get(i));
				} catch (SAXException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				}

				if (perceived_ua_mo) {
					System.err.println(this.neighborhood);
					break;
				}
			}
		}

		// tries to find the na value
		boolean perceived_na = false;
		{
			// fills the list with the current perceptions of the agent
			String[] current_perceptions = this.connection.getBufferAndFlush();
			for (int i = 0; i < current_perceptions.length; i++)
				perceptions.add(current_perceptions[i]);

			// for each perception of the list, tries to find the na value
			for (int i = perceptions.size() - 1; i >= 0; i--) {
				perceived_na = this.perceiveNa(perceptions.get(i));

				if (perceived_na)
					break;
			}
		}
		if (!perceived_na)
			this.na = 1;

		// mounts the array with the current state item values
		int[] state_item_values = { this.nid, this.ua, this.mo, this.na };

		// initializes the q-learning engine
		this.learning_engine = new QLearningEngine(state_item_values,
				GrayBoxLearnerAgent.q_table_dir + this.id + ".txt");
		this.learning_engine.start();

		// while the agent is supposed to work
		while (!this.stop_working) {
			// resets the perceptions
			perceptions.clear();

			// configures the number of possible actions for the current state
			this.learning_engine.setPossibleActionsCount(this.neighborhood
					.size());

			// holds the reward for the current action
			double reward = 0;

			// executes the next action
			int action_id = this.learning_engine.getActionId();
			try {
				reward = this.goToAndSendMessage(action_id);
			} catch (IOException e) {
				e.printStackTrace();
			}

			// tries to perceive the nid attribute
			perceived_nid = false;
			while (!perceived_nid && !this.stop_working) {
				// fills the list with the current perceptions of the agent
				String[] current_perceptions = this.connection
						.getBufferAndFlush();
				for (int i = 0; i < current_perceptions.length; i++)
					perceptions.add(current_perceptions[i]);

				// for each perception of the list, tries to find the nid value
				for (int i = perceptions.size() - 1; i >= 0; i--) {
					perceived_nid = this.perceiveNid(perceptions.get(i));

					if (perceived_nid) {
						System.err.println("current node nid= " + this.nid);
						break;
					}
				}
			}

			// tries to find the ua and mo values
			perceived_ua_mo = false;
			while (!perceived_ua_mo && !this.stop_working) {
				// fills the list with the current perceptions of the agent
				String[] current_perceptions = this.connection
						.getBufferAndFlush();
				for (int i = 0; i < current_perceptions.length; i++)
					perceptions.add(current_perceptions[i]);

				// for each perception of the list, tries to find the ua and mo
				// values
				for (int i = perceptions.size() - 1; i >= 0; i--) {
					try {
						perceived_ua_mo = this.perceiveUaMo(perceptions.get(i));
					} catch (SAXException e) {
						e.printStackTrace();
					} catch (IOException e) {
						e.printStackTrace();
					}

					if (perceived_ua_mo) {
						System.err.println(this.neighborhood);
						break;
					}
				}
			}

			// verifies if this thread shall continue working
			if (this.stop_working)
				break;

			// tries to find the na value
			perceived_na = false;
			{
				// fills the list with the current perceptions of the agent
				String[] current_perceptions = this.connection
						.getBufferAndFlush();
				for (int i = 0; i < current_perceptions.length; i++)
					perceptions.add(current_perceptions[i]);

				// for each perception of the list, tries to find the na value
				for (int i = perceptions.size() - 1; i >= 0; i--) {
					perceived_na = this.perceiveNa(perceptions.get(i));

					if (perceived_na)
						break;
				}
			}
			if (!perceived_na)
				this.na = 1;

			// visits the current node
			try {
				this.visit();
			} catch (IOException e) {
				e.printStackTrace();
			}

			// sets the duration of the last action to the engine
			this.learning_engine
					.setLastActionDuration(this.last_action_duration);
			System.err.println("Action duration " + this.last_action_duration);

			// configures the reward of the action
			this.learning_engine.setReward(reward + this.last_action_duration);

			// mounts the array with the next state item values and configures
			// it into the engine
			int[] next_item_values = { this.nid, this.ua, this.mo, this.na };
			this.learning_engine.setNextState(next_item_values);
		}

		// stops the q-learning engine
		this.learning_engine.stopWorking();

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
	 *            is a real time one, "false" if not. index 3: The probability
	 *            of an agent choose an exploration action. index 4: The rate of
	 *            the decaying of the alpha value in the q-learning algorithm.
	 *            index 5: The discount factor in the q-learning algorithm.
	 *            index 6: The directory to salve the q-table values. index 7:
	 *            "true" if the agent is learning to patroll the environment,
	 *            "false" if not. index 8: The number of possible actions per
	 *            state of the environment. other indexes: The cardinality of
	 *            each item of a state.
	 */
	public static void main(String args[]) {
		try {
			String server_address = args[0];
			int server_socket_number = Integer.parseInt(args[1]);
			boolean is_real_time_simulation = Boolean.parseBoolean(args[2]);

			double e = Double.parseDouble(args[3]);
			double alfa_decay_rate = Double.parseDouble(args[4]);
			double gama = Double.parseDouble(args[5]);

			String q_table_dir = args[6];
			boolean is_learning_phase = Boolean.parseBoolean(args[7]);

			int actions_per_state_count = Integer.parseInt(args[8]);
			int[] state_items_cardinality = new int[args.length - 9];
			for (int i = 0; i < state_items_cardinality.length; i++)
				state_items_cardinality[i] = Integer.parseInt(args[i + 9]);

			GrayBoxLearnerAgent.configureLearningEngine(e, alfa_decay_rate,
					gama, state_items_cardinality, actions_per_state_count);
			GrayBoxLearnerAgent.setQtableDirectory(q_table_dir);
			GrayBoxLearnerAgent.setIsLearningPhase(is_learning_phase);

			GrayBoxLearnerAgent agent = new GrayBoxLearnerAgent();

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
					.println("Usage \"java gray_box_learner.GrayBoxLearnerAgent\n"
							+ "<IP address> <Remote socket number> <Is real time simulator? (true | false)>\n"
							+ "<E-greedy value> <Alpha decay rate> <Discount factor>\n"
							+ "<Q-table values directory> <Is learning phase? (true | false)>\n"
							+ "<Number of possible actions per state>\n"
							+ "<Cardinality of a item of a state>+\"");
		}
	}

	@Override
	public void update() {
		//TODO Convert the run method to update
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

	public String toString() {
		return "{" + this.STRING + ", " + String.valueOf(this.DOUBLE) + "}";
	}
}
