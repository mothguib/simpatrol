/* GrayBoxLearnerClient.java */

/* The package of this class. */
package gray_box_learner;

/* Imported classes and/or interfaces. */
import java.io.IOException;
import java.net.UnknownHostException;
import java.util.HashSet;
import util.net.TCPClientConnection;
import util.net.UDPClientConnection;
import common.Agent;
import common.Client;

/**
 * Implements a client that connects to the SimPatrol server and configures it,
 * letting gray box learner agent clients connect to it, in the sequence.
 */
public final class GrayBoxLearnerClient extends Client {
	/* Methods. */
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
	 * 
	 * @throws IOException
	 * @throws UnknownHostException
	 */
	public GrayBoxLearnerClient(String remote_socket_address,
			int remote_socket_number, String environment_file_path,
			String[] metrics_file_paths, int metrics_collecting_rate,
			String log_file_path, int time_of_simulation,
			boolean is_real_time_simulator, double e, double alfa_decay_rate,
			double gama, String q_table_dir, boolean is_learning_phase,
			int[] state_items_cardinality, int actions_per_state_count)
			throws UnknownHostException, IOException {
		super(remote_socket_address, remote_socket_number,
				environment_file_path, metrics_file_paths,
				metrics_collecting_rate, log_file_path, time_of_simulation,
				is_real_time_simulator);
		GrayBoxLearnerAgent.configureLearningEngine(e, alfa_decay_rate, gama,
				state_items_cardinality, actions_per_state_count);
		GrayBoxLearnerAgent.setQtableDirectory(q_table_dir);
		GrayBoxLearnerAgent.setIsLearningPhase(is_learning_phase);
	}

	protected void createAndStartAgents(String[] agent_ids, int[] socket_numbers)
			throws IOException {
		this.agents = new HashSet<Agent>();

		for (int i = 0; i < agent_ids.length; i++) {
			GrayBoxLearnerAgent agent = new GrayBoxLearnerAgent();

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
			String remote_socket_address = args[0];
			int remote_socket_number = Integer.parseInt(args[1]);
			String environment_file_path = args[2];
			String[] metric_file_paths = { args[3], args[4], args[5], args[6] };
			int metrics_collecting_rate = Integer.parseInt(args[7]);
			String log_file_path = args[8];
			int time_of_simulation = Integer.parseInt(args[9]);
			boolean is_real_time_simulator = Boolean.parseBoolean(args[10]);
			double e = Double.parseDouble(args[11]);
			double alfa_decay_rate = Double.parseDouble(args[12]);
			double gama = Double.parseDouble(args[13]);
			String q_table_dir = args[14];
			boolean is_learning_phase = Boolean.parseBoolean(args[15]);

			int actions_per_state_count = Integer.parseInt(args[16]);
			int[] state_items_cardinality = new int[args.length - 17];
			for (int i = 0; i < state_items_cardinality.length; i++)
				state_items_cardinality[i] = Integer.parseInt(args[i + 17]);

			GrayBoxLearnerClient client = new GrayBoxLearnerClient(
					remote_socket_address, remote_socket_number,
					environment_file_path, metric_file_paths,
					metrics_collecting_rate, log_file_path, time_of_simulation,
					is_real_time_simulator, e, alfa_decay_rate, gama,
					q_table_dir, is_learning_phase, state_items_cardinality,
					actions_per_state_count);
			client.start();
		} catch (Exception e) {
			System.out
					.println("Usage \"java cognitive_coordinated.CognitiveCoordinatedClient\n"
							+ "<IP address> <Remote socket number> <Environment file path>\n"
							+ "<Metric file name 1 | \"\"> <Metric file name 2 | \"\"> <Metric file name 3 | \"\"> <Metric file name 4 | \"\">\n"
							+ "<Metric collecting rate> <Log file name> <Time of simulation> <Is real time simulator? (true | false)>\n"
							+ "<E-greedy value> <Alpha decay rate> <Discount factor>\n"
							+ "<Q-table values directory> <Is learning phase? (true | false)>\n"
							+ "<Number of possible actions per state>\n"
							+ "<Cardinality of a item of a state>\"");
		}
	}
}