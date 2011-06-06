package gray_box_learner.q_learning_engine;

import java.util.LinkedList;
import java.util.List;

public class QState1 extends QState {
	
	int[] state_values;
	
	
	public QState1(int[] state_items_values){
		state_values = state_items_values;
	}
	
	
	/**
	 * Returns the id of the state corresponding to the given values for each of
	 * its item.
	 * 
	 * @param state_items_values
	 *            Array that holds the values for each item of a state.
	 * @return The id of the correspondent state. *
	 * 
	 */
	public int getStateId() {
		int answer = 0;

		for (int i = 0; i < state_values.length; i++) {
			int partial_answer = state_values[i] - 1;

			for (int j = i + 1; j < state_values.length; j++)
				partial_answer = partial_answer
						* QState.state_items_cardinality.get(j).intValue();

			answer = answer + partial_answer;
		}

		return answer;
	}

}
