package gray_box_learner.q_learning_engine;

import java.util.LinkedList;
import java.util.List;

public abstract class QState {
	
	/**
	 * List that holds the number of possible values for each item of a state.
	 * Shared by all the q-learning engines.
	 */
	protected static List<Integer> state_items_cardinality;
	
	/**
	 * Configures the number of possible values for each item of a state. *
	 * 
	 * @param state_items_cardinality
	 *            Array that holds the number of possible values for each item
	 *            of a state.
	 */
	public static void setStateItemsCardinality(int[] state_items_cardinality) {
		QState.state_items_cardinality = new LinkedList<Integer>();

		for (int i = 0; i < state_items_cardinality.length; i++)
			QState.state_items_cardinality.add(new Integer(state_items_cardinality[i]));
	}
	
	/**
	 * Returns the id of the state corresponding to the given values for each of
	 * its item.
	 * 
	 * @param state_items_values
	 *            Array that holds the values for each item of a state.
	 * @return The id of the correspondent state. *
	 */
	public abstract int getStateId();

}
