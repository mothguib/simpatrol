/* QTable.java */

/* The package of this class.  */
package gray_box_learner.q_learning_engine;

/* Imported classes and/or interfaces. */
import java.io.IOException;
import java.util.LinkedList;

import util.file.FileReader;
import cern.jet.random.Empirical;
import cern.jet.random.EmpiricalWalker;
import cern.jet.random.engine.MersenneTwister;

/**
 * Implements the table that holds the estimated values for the possible actions
 * in the q-learning algorithm.
 */
public class QTable {
	/* Attributes. */
	/** The values given to each action per state. */
	private final double[][] VALUES;

	/** Registers the number of times an action is executed given a state. */
	private final int[][] USES;

	/** Holds the number of possible actions for each state. */
	private final int[] ACTIONS_PER_STATE_COUNT;

	/**
	 * Random number distributor used to decide if the agent shall execute an
	 * exploration or an exploitation action.
	 */
	private final EmpiricalWalker ACTION_MODE_RN_DISTRIBUTION;

	/* Methods. */
	/**
	 * Constructor.
	 * 
	 * @param states_count
	 *            The number of possible states.
	 * @param actions_count
	 *            The number of possible actions per state.
	 * @param e
	 *            The probability of an agent choose an exploration action
	 *            (e-greedy value).
	 */
	public QTable(int states_count, int actions_count, double e) {
		this.VALUES = new double[states_count][actions_count];
		this.USES = new int[states_count][actions_count];
		this.ACTIONS_PER_STATE_COUNT = new int[states_count];

		double[] action_mode_distribution = { e, 1 - e };
		this.ACTION_MODE_RN_DISTRIBUTION = new EmpiricalWalker(
				action_mode_distribution, Empirical.NO_INTERPOLATION,
				new MersenneTwister());
	}

	/**
	 * Constructor.
	 * 
	 * @param states_count
	 *            The number of possible states.
	 * @param actions_count
	 *            The number of possible actions per state.
	 * @param e
	 *            The probability of an agent choose an exploration action
	 *            (e-greedy value).
	 * @param file_path
	 *            The path of the file containing the values of the q-table.
	 * 
	 * @throws IOException
	 */
	public QTable(int states_count, int actions_count, double e,
			String file_path) throws IOException {
		this(states_count, actions_count, e);

		FileReader file_reader = new FileReader(file_path);
		for (int i = 0; i < states_count; i++)
			for (int j = 0; j < actions_count; j++)
				this.VALUES[i][j] = file_reader.readDouble();

		for (int i = 0; i < states_count; i++)
			for (int j = 0; j < actions_count; j++)
				this.USES[i][j] = file_reader.readInt();

		for (int i = 0; i < states_count; i++)
			this.ACTIONS_PER_STATE_COUNT[i] = file_reader.readInt();

		file_reader.close();
	}

	/**
	 * Returns a randomly chosen action (exploration action), given the current
	 * state id.
	 * 
	 * @param state_id
	 *            The id of the current state.
	 * @return A randomly chosen action id.
	 */
	private int getExplorationActionId(int state_id) {
		// the id of the action, randomly obtained
		int action_id = (int) (Math.random() * this.ACTIONS_PER_STATE_COUNT[state_id]);

		// returns the chosen action id
		return action_id;
	}

	/**
	 * Returns the best action (exploitation action), given the current state
	 * id.
	 * 
	 * @param state_id
	 *            The id of the current state.
	 * @return The id of the best action.
	 */
	public int getExploitationActionId(int state_id) {
		// holds the best value found in the q-table
		double best_q_value = (-1) * Double.MAX_VALUE;

		// holds the best action ids
		LinkedList<Integer> best_action_ids = new LinkedList<Integer>();

		// for each action of the given state, finds the best one
		int actions_count = this.ACTIONS_PER_STATE_COUNT[state_id];

		for (int i = 0; i < actions_count; i++)
			if (this.VALUES[state_id][i] > best_q_value) {
				best_q_value = this.VALUES[state_id][i];
				best_action_ids.clear();
				best_action_ids.add(new Integer(i));
			} else if (this.VALUES[state_id][i] == best_q_value)
				best_action_ids.add(new Integer(i));

		// chooses a best action at random
		int action_id = 0;
		if (best_action_ids.size() > 0) {
			int action_id_index = (int) (Math.random() * best_action_ids.size());
			action_id = best_action_ids.get(action_id_index).intValue();
		}

		// returns the chosen action id
		return action_id;
	}

	/**
	 * Returns the id of the action correspondent to the given state id.
	 * 
	 * @param state_id
	 *            The id of the current state.
	 * @param is_learning_phase
	 *            TRUE, if the agent is in the learning phase of the q-learning
	 *            algorithm, FALSE if not.
	 * @return The if of the related action.
	 */
	public int getActionId(int state_id, boolean is_learning_phase) {
		if (this.ACTION_MODE_RN_DISTRIBUTION.nextInt() == 0)
			return this.getExplorationActionId(state_id);
		else
			return this.getExploitationActionId(state_id);
	}

	/**
	 * Returns the value of the q-table, given the state id and the action id.
	 * 
	 * @param state_id
	 *            The id of the state of the desired value.
	 * @param action_id
	 *            The id of the action of the desired value.
	 * @return The value of correspondent q-table item.
	 */
	public double getValue(int state_id, int action_id) {
		return this.VALUES[state_id][action_id];
	}

	/**
	 * Configures the value of the q-table, given the id of the correspondent
	 * state and the id of the correspondent action.
	 * 
	 * @param state_id
	 *            The id of the state related to the given value.
	 * @param action_id
	 *            The id of the action related to the given value.
	 * @param value
	 *            The value to be set onto the q-table.
	 */
	public void setValue(int state_id, int action_id, double value) {
		this.VALUES[state_id][action_id] = value;
	}

	/**
	 * Returns the number of times an action was executed, given a state id.
	 * 
	 * @param state_id
	 *            The id of the state related to the desired value.
	 * @param action_id
	 *            The id of the action related to the desired value.
	 * @return The number of times the related action was executed.
	 */
	public int getUse(int state_id, int action_id) {
		return this.USES[state_id][action_id];
	}

	/**
	 * Registers that a given action was executed in a given state.
	 * 
	 * @param state_id
	 *            The id of the state when the action was executed.
	 * @param action_id
	 *            The id of the executed action.
	 */
	public void setUse(int state_id, int action_id) {
		this.USES[state_id][action_id]++;
	}

	/**
	 * Configures the number of possible actions for a given state id.
	 * 
	 * @param state_id
	 *            The id of the state of which number of possible actions is
	 *            being configured.
	 * @param actions_count
	 *            The number of possible actions.
	 */
	public void setPossibleActionsCount(int state_id, int actions_count) {
		this.ACTIONS_PER_STATE_COUNT[state_id] = actions_count;
	}

	/**
	 * Converts the q-table to a string.
	 * 
	 * @return A string version of the q-table.
	 */
	public String toString() {
		StringBuffer answer = new StringBuffer();

		for (int i = 0; i < this.VALUES.length; i++)
			for (int j = 0; j < this.VALUES[i].length; j++)
				answer.append(this.VALUES[i][j] + "\n");

		for (int i = 0; i < this.USES.length; i++)
			for (int j = 0; j < this.USES[i].length; j++)
				answer.append(this.USES[i][j] + "\n");

		for (int i = 0; i < this.ACTIONS_PER_STATE_COUNT.length; i++)
			answer.append(this.ACTIONS_PER_STATE_COUNT[i] + "\n");

		return answer.toString();
	}
}