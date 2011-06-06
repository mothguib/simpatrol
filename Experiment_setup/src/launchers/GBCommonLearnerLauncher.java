package launchers;

import gray_box_learner.MyGBCommonLearnerAgent;
import gray_box_learner.MyGBLearnerAgent;
import gray_box_learner.q_learning_engine.CommonQLearningEngine;

import java.io.IOException;
import java.net.UnknownHostException;
import java.util.HashSet;

import util.net.TCPClientConnection;
import util.net.UDPClientConnection;

import common.Agent;

public class GBCommonLearnerLauncher extends Launcher {
	

	double e;
	double alfa_decay_rate;
	double gama;
	String q_table_file;
	boolean is_learning_phase;
	int[] state_items_cardinality;
	int actions_per_state_count;
	
	CommonQLearningEngine QLE;
	
	/**
	 * Constructor.
	 * 
	 * @param remote_socket_address
	 *            The IP address of the SimPatrol server.
	 * @param remote_socket_number
	 *            The number of the socket that the server is supposed to listen
	 *            to this client.
	 * @param environment_file_path
	 *            The path of the file that contains the environment.
	 * @param metrics_file_paths
	 *            The paths of the files that will save the collected metrics:
	 *            index 0: The file that will save the mean instantaneous
	 *            idlenesses; index 1: The file that will save the max
	 *            instantaneous idlenesses; index 2: The file that will save the
	 *            mean idlenesses; index 3: The file that will save the max
	 *            idlenesses;
	 * @param metrics_collection_rate
	 *            The time interval used to collect the metrics.
	 * @param log_file_path
	 *            The path of the file to log the simulation.
	 * @param time_of_simulation
	 *            The time of simulation.
	 * @param is_real_time_simulator
	 *            TRUE if the simulator is a real time one, FALSE if not.
	 * @param e
	 *            The probability of an agent choose an exploration action.
	 * @param alfa_decay_rate
	 *            The rate of the decaying of the alpha value in the q-learning
	 *            algorithm.
	 * @param gama
	 *            The discount factor in the q-learning algorithm. *
	 * @param q_table_dir
	 *            The directory to record the q-table values learned by the
	 *            agent.
	 * @param is_learning_phase
	 *            TRUE if the agent is in its learning phase, FALSE if not.
	 * @param state_items_cardinality
	 *            Array that holds the number of possible values for each item
	 *            of a state.
	 * @param actions_per_state_count
	 *            The maximum number of possible actions per state.
	 * @param generalized
	 * 			  If the agents are GGBLA agents
	 * 
	 * @throws IOException
	 * @throws UnknownHostException
	 */
	public GBCommonLearnerLauncher(String environment_dir_path, String env_gen_name,
			int numEnv, String log_dir_path, 
			String log_gen_name,
			int time_of_simulation,
			double e, double alfa_decay_rate,
			double gama, String q_table_file, boolean is_learning_phase,
			int[] state_items_cardinality, int actions_per_state_count,
			boolean generalized)
			throws UnknownHostException, IOException {
		super(environment_dir_path, 
				env_gen_name, numEnv, 
				log_dir_path, log_gen_name,
				time_of_simulation);
		MyGBCommonLearnerAgent.GENERALIZED = generalized;
		this.e = e;
		this.alfa_decay_rate = alfa_decay_rate;
		this.gama = gama;
		this.q_table_file = q_table_file;
		this.is_learning_phase = is_learning_phase;
		this.state_items_cardinality = state_items_cardinality;
		this.actions_per_state_count = actions_per_state_count;
	}

	
	protected void createAndStartAgents(String[] agent_ids, int[] socket_numbers)
			throws IOException {
		this.agents = new HashSet<Agent>();
		
		CommonQLearningEngine.configureLearningEngine(e, alfa_decay_rate, gama, state_items_cardinality, actions_per_state_count);
		QLE = new CommonQLearningEngine(q_table_file, agent_ids.length);	
		QLE.setIsLearningPhase(is_learning_phase);
		QLE.start();
		
		
		for (int i = 0; i < agent_ids.length; i++) {
			MyGBCommonLearnerAgent agent = new MyGBCommonLearnerAgent(QLE);

			if (this.IS_REAL_TIME_SIMULATOR)
				agent.setConnection(new UDPClientConnection(this.CONNECTION
						.getRemoteSocketAdress(), socket_numbers[i]));
			else
				agent.setConnection(new TCPClientConnection(this.CONNECTION
						.getRemoteSocketAdress(), socket_numbers[i]));

			agent.start();
			this.agents.add(agent);
		}
	}
	
	/** Stops the agents. */
	protected void stopAgents() {
		if (this.agents != null) {
			Object[] agents_array = this.agents.toArray();
			for (int i = 0; i < agents_array.length; i++)
				((Agent) agents_array[i]).stopWorking();
			QLE.stopWorking();
			try {
				sleep(5000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			this.agents.clear();
		}
	}

	/**
	 * Turns this class into an executable one.
	 * 
	 * @param args
	 *            Arguments: index 0: The IP address of the SimPatrol server.
	 *            index 1: The number of the socket that the server is supposed
	 *            to listen to this client. index 2: The path of the file that
	 *            contains the environment. index 3. The path of the file that
	 *            will save the mean instantaneous idlenesses; index 4. The path
	 *            of the file that will save the max instantaneous idlenesses;
	 *            index 5. The path of the file that will save the mean
	 *            idlenesses; index 6. The path of the file that will save the
	 *            max idlenesses; index 7: The time interval used to collect the
	 *            metrics; index 8: The path of the file that will save the
	 *            collected events; index 9: The time of simulation. index 10:
	 *            false if the simulator is a cycled one, true if not. index 11:
	 *            The probability of an agent choose an exploration action.
	 *            index 12: The rate of the decaying of the alpha value in the
	 *            q-learning algorithm. index 13: The discount factor in the
	 *            q-learning algorithm. index 14: The directory to salve the
	 *            q-table values. index 15: "true" if the agent is learning to
	 *            patrol the environment, "false" if not. index 16: The number
	 *            of possible actions per state of the environment. other
	 *            indexes: The cardinality of each item of a state.
	 */
	public static void main(String[] args) {
		System.out.println("Gray box learner agents!");

		try {
			String environment_dir_path = args[0];
			String env_gen_name = args[1];
			int numEnv  = Integer.parseInt(args[2]);
			String log_dir_path = args[3];
			String log_gen_name = args[4];
			int time_of_simulation= Integer.parseInt(args[5]);
			double e = Double.parseDouble(args[6]);
			double alfa_decay_rate = Double.parseDouble(args[7]);
			double gama = Double.parseDouble(args[8]);
			String q_table_file = args[9];
			boolean is_learning_phase = Boolean.parseBoolean(args[10]);

			int actions_per_state_count = Integer.parseInt(args[11]);
			int[] state_items_cardinality = new int[args.length - 12];
			for (int i = 0; i < state_items_cardinality.length; i++)
				state_items_cardinality[i] = Integer.parseInt(args[i + 12]);
			
			boolean generalized;
			if (state_items_cardinality.length == 3) {
				System.out.println("Generalized!");
				generalized = true;
			} else {
				generalized = false;
			}

			GBCommonLearnerLauncher client = new GBCommonLearnerLauncher(
					environment_dir_path, env_gen_name,
					numEnv, log_dir_path, 
					log_gen_name,
					time_of_simulation, e, alfa_decay_rate, gama,
					q_table_file, is_learning_phase, state_items_cardinality,
					actions_per_state_count, generalized);
			client.start();
		} catch (Exception e) {
			System.out
					.println("Usage \"java launchers.GBCommonLearnerLauncher\n"
							+ "<Environment directory path> <Environment generic name> <number of environments>\n"
							+ "<log directory path> <Log generic name> <num of cycle in simulations> \n" 
							+ "<E-greedy value> <Alpha decay rate> <Discount factor>\n"
							+ "<Q-table values file path> <Is learning phase? (true | false)>\n"
							+ "<Number of possible actions per state>\n"
							+ "<Cardinality of a item of a state>\"");
		}
	}

}

