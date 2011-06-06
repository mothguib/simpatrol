package gray_box_learner.q_learning_engine;

import java.io.IOException;

import util.file.FileWriter;

public class CommonQLearningEngine extends Thread {

	
	/* Attributes. */
	/** Registers if this thread shall execute its run() method. */
	private boolean is_active;

	/**
	 * The mode of execution of the q-learning algorithm. TRUE if it is in the
	 * learning phase, FALSE if not. Shared among all the q-learning engines.
	 */
	private boolean is_learning_phase;

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

	
	private UpdateStructure_SingleAgent[] AgentsUpStructures;
	
	
	
	/** synchronization variable  */
	boolean updated = false;

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
	public CommonQLearningEngine(String q_table_file_path, int NbAgents) {
		this.is_active = false;
		
		int states_count = 1;
		for (Integer i : QState.state_items_cardinality)
			states_count = states_count * i.intValue();

		this.q_table_file_path = q_table_file_path;
		try {
			this.q_table = new QTable(states_count, actions_per_state_count, configuration.getE(), q_table_file_path);
		} catch (IOException e) {
			this.q_table = new QTable(states_count, actions_per_state_count, configuration.getE());
		}

		AgentsUpStructures = new UpdateStructure_SingleAgent[NbAgents];

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
		CommonQLearningEngine.setQLearningConfiguration(e, alfa_decay_rate, gama);
		CommonQLearningEngine.setStateItemsCardinality(state_items_cardinality);
		CommonQLearningEngine.setActionsPerStateCount(actions_per_state_count);
	}

	/**
	 * Configures the number of possible values for each item of a state. *
	 * 
	 * @param state_items_cardinality
	 *            Array that holds the number of possible values for each item
	 *            of a state.
	 */
	public static void setStateItemsCardinality(int[] state_items_cardinality) {
		QState.setStateItemsCardinality(state_items_cardinality);
	}

	/**
	 * Configures the number of possible actions per state.
	 * 
	 * @param value
	 *            The maximum number of possible actions per state.
	 */
	public static void setActionsPerStateCount(int value) {
		CommonQLearningEngine.actions_per_state_count = value;
	}

	/**
	 * Configures the mode of execution of the q-learning algorithm.
	 * 
	 * @param is_learning_phase
	 *            TRUE if it is in the learning phase, FALSE if not.
	 */
	public void setIsLearningPhase(boolean is_learning_phase) {
		this.is_learning_phase = is_learning_phase;
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
	public static void setQLearningConfiguration(double e, double alfa_decay_rate, double gama) {
		CommonQLearningEngine.configuration = new QLearningConfiguration(e,
				alfa_decay_rate, gama);
	}

	
	int current_init_index = 0;
	public void initAgent(String agent_id, QState qstate){
		//int i = 0;
		/*
		while(AgentsUpStructures[i] != null && i < AgentsUpStructures.length){
			if(AgentsUpStructures[i].agent_id.equals(agent_id)){
				AgentsUpStructures[i].set_current_state_id(qstate.getStateId());
				return;
			}
			i++;
		}
		*/
		
		if(current_init_index == AgentsUpStructures.length)
			System.err.println("Too much agents trying to connect to the Common Qlearning Engine!");
		else {
			AgentsUpStructures[current_init_index] = new UpdateStructure_SingleAgent(agent_id);
			AgentsUpStructures[current_init_index].set_current_state_id(qstate.getStateId());
			current_init_index++;
			return;
		}
		
		
	}
	
	
	/**
	 * Returns the id of the action correspondent to the current state.
	 * 
	 * @return the id of the action correspondent to the current state.
	 */
	public int getActionId(String agent_id) {
		for(UpdateStructure_SingleAgent UpS : this.AgentsUpStructures){
			if(UpS.agent_id.equals(agent_id)){
				int action_id = this.q_table.getActionId(UpS.get_current_state_id(), this.is_learning_phase);		
				UpS.set_last_action_id(action_id);
		
				return action_id + 1;
			}
		}
		
		return -1;

	}

	/**
	 * Configures the number of possible actions for the current state id.
	 * 
	 * @param actions_count
	 *            The number of possible actions for the current state id.
	 */
	public void setPossibleActionsCount(String agent_id, int actions_count) {
		for(UpdateStructure_SingleAgent UpS : this.AgentsUpStructures)
			if(UpS.agent_id.equals(agent_id)){
				this.q_table.setPossibleActionsCount(UpS.get_current_state_id(), actions_count);
				return;
			}
	}

	/**
	 * Configures the duration of the last action executed.
	 * 
	 * @param duration
	 *            The duration of the last action executed.
	 */
	public void setLastActionDuration(String agent_id, double duration) {
		for(UpdateStructure_SingleAgent UpS : this.AgentsUpStructures)
			if(UpS.agent_id.equals(agent_id)){
				UpS.set_last_action_duration(duration);
				return;
			}
	}

	/**
	 * Configures the reward for the last action executed by the agent.
	 * 
	 * @param reward
	 *            The reward related to the last action executed by the agent.
	 */
	public void setReward(String agent_id, double reward) {
		for(UpdateStructure_SingleAgent UpS : this.AgentsUpStructures)
			if(UpS.agent_id.equals(agent_id)){
				UpS.set_reward(reward);
				return;
			}
	}

	/**
	 * Configures the next state of the environment.
	 * 
	 * @param next_state_items_values
	 *            Array that holds the values for each item of the next state of
	 *            the environment.
	 */
	public void setNextState(String agent_id, QState qstate) {
		for(UpdateStructure_SingleAgent UpS : this.AgentsUpStructures)
			if(UpS.agent_id.equals(agent_id)){
				UpS.set_next_state_id(qstate.getStateId());
				return;
			}
	}
	

	/**
	 * Updates the values of the q-table, as defined by the q-learning
	 * algorithm.
	 */
	private void updateQTable(String agent_id) {
		if (this.is_learning_phase) {
			for(UpdateStructure_SingleAgent UpS : this.AgentsUpStructures)
				if(UpS.agent_id.equals(agent_id)){
		
					this.q_table.setUse(UpS.get_current_state_id(), UpS.get_last_action_id());
					
					double current_value = this.q_table.getValue(UpS.get_current_state_id(), UpS.get_last_action_id());
		
					double alfa = Math.pow((2 + this.q_table.getUse(UpS.get_current_state_id(), UpS.get_last_action_id())
													* Math.pow(this.configuration.getAlfa_decay_rate(), -1)),
												-1);
		
					double to_floor_value = UpS.get_reward() + Math.pow(this.configuration.getGama(), UpS.get_last_action_duration())
							* this.q_table.getValue(UpS.get_next_state_id(), this.q_table.getExploitationActionId(UpS.get_next_state_id()))
							- current_value;
		
					double floor_value = Math.floor(to_floor_value);
		
					double new_value = current_value + alfa * floor_value;
					this.q_table.setValue(UpS.get_current_state_id(), UpS.get_last_action_id(), new_value);
				}
		}
	}
	
	
	public boolean is_updated(String agent_id){
		for(UpdateStructure_SingleAgent UpS : this.AgentsUpStructures)
			if(UpS.agent_id.equals(agent_id)){
				return UpS.is_updated();
			}
		
		// if the agents hasn't been found, we answer true so that the agent does not block the whole situation
		return true;
	}

	/** Makes this thread stop working. */
	public void stopWorking() {
		this.is_active = false;

		// saves the q-learning values into the proper file
		if (this.is_learning_phase)
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
		System.out.println("QEngine started !");
		
		while (this.is_active){
			for(UpdateStructure_SingleAgent UpS : this.AgentsUpStructures)
				if(UpS != null && UpS.is_updatable()){
					this.updateQTable(UpS.agent_id);
					UpS.UpdateToNextState();
				}
		
			try {
				sleep(2);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

	}
	
	
	
	private class UpdateStructure_SingleAgent {
		
		final String agent_id;
		
		/** The id of the current state of the environment. */
		private int current_state_id = -1;

		/** The id of the next state of the environment. */
		private int next_state_id = -1;

		/** The id of the last action executed. */
		private int last_action_id = -1;

		/** The duration of the last action executed. */
		private double last_action_duration = -1;

		/** The reward related to the last action executed. */
		private double reward = -1;
		
		
		private boolean updated = false;
		
		
		UpdateStructure_SingleAgent(String id){
			agent_id = id;
		}
		

		public int get_current_state_id() {
			return current_state_id;
		}
		
		public int get_next_state_id() {
			return next_state_id;
		}
		
		public int get_last_action_id() {
			return last_action_id;
		}

		public double get_last_action_duration() {
			return last_action_duration;
		}

		public double get_reward() {
			return reward;
		}
		
		public void set_current_state_id(int stateId) {
			current_state_id = stateId;
			updated = false;
		}

		public void set_next_state_id(int stateId) {
			next_state_id = stateId;
			updated = false;
		}

		public void set_last_action_id(int action_id) {
			last_action_id = action_id;
			updated = false;
		}
		
		public void set_last_action_duration(double duration) {
			last_action_duration = duration;
			updated = false;
		}
		
		public void set_reward(double reward2) {
			reward = reward2;
			updated = false;
		}

		void UpdateToNextState(){
			this.current_state_id = this.next_state_id;

			// resets the last action, as well as its duration
			this.last_action_id = -1;
			this.last_action_duration = -1;

			// resets the reward
			this.reward = -1;

			// resets the next state
			this.next_state_id = -1;
			
			updated = true;
		}
		
		boolean is_updatable(){
			return !(this.last_action_id == -1 || this.last_action_duration == -1|| this.reward == -1 || this.next_state_id == -1);
		}
		
		boolean is_updated(){
			return updated;
		}
		
	}
	
	
	
}
	
