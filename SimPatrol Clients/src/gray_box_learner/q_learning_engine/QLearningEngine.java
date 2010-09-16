/* QlearningEngine.java */

/* The package of this class.  */
package gray_box_learner.q_learning_engine;

/* Imported classes and/or interfaces. */
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import util.file.FileWriter;

/** Engine that implements the q-learning algorithm. */
public class QLearningEngine extends Thread {
	/* Attributes. */
	/** Registers if this thread shall execute its run() method. */
	private boolean is_active;

	/**
	 * The mode of execution of the q-learning algorithm. TRUE if it is in the
	 * learning phase, FALSE if not. Shared among all the q-learning engines.
	 */
	private static boolean is_learning_phase;

	/**
	 * List that holds the number of possible values for each item of a state.
	 * Shared by all the q-learning engines.
	 */
	private static List<Integer> state_items_cardinality;

	/**
	 * Holds the maximum number of possible actions per state. Shared among all
	 * the q-learning engines.
	 */
	private static int actions_per_state_count;

	/**
	 * Holds all the configuration parameters of the q-learning algorithm.
	 * Shared by all the q-learning engines.
	 */
	private static QLearningConfiguration configuration;

	/** The table that holds the estimated values for each action, given a state. */
	private QTable q_table;

	/** The path of the file that holds the values of the q-table. */
	private String q_table_file_path;

	/** The id of the current state of the environment. */
	private int current_state_id;

	/** The id of the next state of the environment. */
	private int next_state_id;

	/** The id of the last action executed. */
	private int last_action_id;

	/** The duration of the last action executed. */
	private double last_action_duration;

	/** The reward related to the last action executed. */
	private double reward;

	/* Methods. */
	/**
	 * Constructor.
	 * 
	 * @param current_state_items_values
	 *            Array that holds the values for each item of the current
	 *            state.
	 * @param q_table_file_path
	 *            The path of the file to record the q-table values.
	 */
	public QLearningEngine(int[] current_state_items_values,
			String q_table_file_path) {
		this.is_active = false;

		int states_count = 1;
		for (Integer i : QLearningEngine.state_items_cardinality)
			states_count = states_count * i.intValue();

		this.q_table_file_path = q_table_file_path;
		try {
			this.q_table = new QTable(states_count,
					QLearningEngine.actions_per_state_count,
					QLearningEngine.configuration.getE(), q_table_file_path);
		} catch (IOException e) {
			this.q_table = new QTable(states_count,
					QLearningEngine.actions_per_state_count,
					QLearningEngine.configuration.getE());
		}

		this.current_state_id = this.getStateId(current_state_items_values);
		this.next_state_id = -1;

		this.last_action_id = -1;
		this.last_action_duration = -1;

		this.reward = -1;
	}

	/**
	 * Configures the number of possible values for each item of a state. *
	 * 
	 * @param state_items_cardinality
	 *            Array that holds the number of possible values for each item
	 *            of a state.
	 */
	public static void setStateItemsCardinality(int[] state_items_cardinality) {
		QLearningEngine.state_items_cardinality = new LinkedList<Integer>();

		for (int i = 0; i < state_items_cardinality.length; i++)
			QLearningEngine.state_items_cardinality.add(new Integer(
					state_items_cardinality[i]));
	}

	/**
	 * Configures the number of possible actions per state.
	 * 
	 * @param value
	 *            The maximum number of possible actions per state.
	 */
	public static void setActionsPerStateCount(int value) {
		QLearningEngine.actions_per_state_count = value;
	}

	/**
	 * Configures the mode of execution of the q-learning algorithm.
	 * 
	 * @param is_learning_phase
	 *            TRUE if it is in the learning phase, FALSE if not.
	 */
	public static void setIsLearningPhase(boolean is_learning_phase) {
		QLearningEngine.is_learning_phase = is_learning_phase;
	}

	/**
	 * Sets the configuration parameters of the q-learning engine.
	 * 
	 * @param e
	 *            The probability of an agent choose an exploration action.
	 * @param alfa_decay_rate
	 *            The rate of the decaying of the alpha value in the q-learning
	 *            algorithm.
	 * @param gama
	 *            The discount factor in the q-learning algorithm.
	 */
	public static void setQLearningConfiguration(double e,
			double alfa_decay_rate, double gama) {
		QLearningEngine.configuration = new QLearningConfiguration(e,
				alfa_decay_rate, gama);
	}

	/**
	 * Returns the id of the state corresponding to the given values for each of
	 * its item.
	 * 
	 * @param state_items_values
	 *            Array that holds the values for each item of a state.
	 * @return The id of the correspondent state. *
	 */
	private int getStateId(int[] state_items_values) {
		int answer = 0;

		for (int i = 0; i < state_items_values.length; i++) {
			int partial_answer = state_items_values[i] - 1;

			for (int j = i + 1; j < state_items_values.length; j++)
				partial_answer = partial_answer
						* QLearningEngine.state_items_cardinality.get(j)
								.intValue();

			answer = answer + partial_answer;
		}

		return answer;
	}

	/**
	 * Returns the id of the action correspondent to the current state.
	 * 
	 * @return the id of the action correspondent to the current state.
	 */
	public int getActionId() {
		int action_id = this.q_table.getActionId(this.current_state_id,
				QLearningEngine.is_learning_phase);

		this.q_table.setUse(this.current_state_id, action_id);

		this.last_action_id = action_id;

		return action_id + 1;
	}

	/**
	 * Configures the number of possible actions for the current state id.
	 * 
	 * @param actions_count
	 *            The number of possible actions for the current state id.
	 */
	public void setPossibleActionsCount(int actions_count) {
		this.q_table.setPossibleActionsCount(this.current_state_id,
				actions_count);
	}

	/**
	 * Configures the duration of the last action executed.
	 * 
	 * @param duration
	 *            The duration of the last action executed.
	 */
	public void setLastActionDuration(double duration) {
		this.last_action_duration = duration;
	}

	/**
	 * Configures the reward for the last action executed by the agent.
	 * 
	 * @param reward
	 *            The reward related to the last action executed by the agent.
	 */
	public void setReward(double reward) {
		this.reward = reward;
	}

	/**
	 * Configures the next state of the environment.
	 * 
	 * @param next_state_items_values
	 *            Array that holds the values for each item of the next state of
	 *            the environment.
	 */
	public void setNextState(int[] next_state_items_values) {
		this.next_state_id = this.getStateId(next_state_items_values);
	}

	/**
	 * Updates the values of the q-table, as defined by the q-learning
	 * algorithm.
	 */
	private void updateQTable() {
		if (QLearningEngine.is_learning_phase) {
			double current_value = this.q_table.getValue(this.current_state_id,
					this.last_action_id);

			double alfa = Math.pow((2 + this.q_table.getUse(
					this.current_state_id, this.last_action_id)
					* Math.pow(QLearningEngine.configuration
							.getAlfa_decay_rate(), -1)), -1);

			double to_floor_value = this.reward
					+ Math.pow(QLearningEngine.configuration.getGama(),
							this.last_action_duration)
					* this.q_table.getValue(this.next_state_id, this.q_table
							.getExploitationActionId(this.next_state_id))
					- current_value;

			double floor_value = Math.floor(to_floor_value);

			double new_value = current_value + alfa * floor_value;
			this.q_table.setValue(this.current_state_id, this.last_action_id,
					new_value);
		}
	}

	/** Makes this thread stop working. */
	public void stopWorking() {
		this.is_active = false;

		// saves the q-learning values into the proper file
		if (QLearningEngine.is_learning_phase)
			try {
				FileWriter file_writer = new FileWriter(this.q_table_file_path);
				file_writer.print(this.q_table.toString());
				file_writer.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
	}

	public void start() {
		this.is_active = true;
		super.start();
	}

	public void run() {
		while (this.is_active) {
			// waits while the last action was not executed
			while (this.last_action_id == -1 || this.last_action_duration == -1)
				if (!this.is_active)
					return;

			// waits while the reward was not determined
			while (this.reward == -1)
				if (!this.is_active)
					return;

			// waits while the next state was not determined
			while (this.next_state_id == -1)
				if (!this.is_active)
					return;

			// updates the q-table
			this.updateQTable();

			// updates the current state
			this.current_state_id = this.next_state_id;

			// resets the last action, as well as its duration
			this.last_action_id = -1;
			this.last_action_duration = -1;

			// resets the reward
			this.reward = -1;

			// resets the next state
			this.next_state_id = -1;
		}
	}
}