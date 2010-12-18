package gray_box_learner;

import java.io.IOException;
import java.net.UnknownHostException;
import java.util.HashSet;

import util.file.FileReader;
import util.graph.Graph;
import util.graph.GraphTranslator;
import util.graph.Node;
import util.net.TCPClientConnection;
import util.net.UDPClientConnection;
import common.Agent;
import common.Client;


/**
 * Implements a client that connects to the SimPatrol server and configures it,
 * letting gray box learner agent clients connect to it, in the sequence.
 */
public final class GrayBoxLearnerClient extends Client {

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
	 *            The discount factor in the q-learning algorithm.
	 * @param q_table_dir
	 *            The directory to record the q-table values learned by the
	 *            agent.
	 * @param is_learning_phase
	 *            TRUE if the agent is in its learning phase, FALSE if not.
	 * @param numNodes
	 *            Number of nodes of the graph.
	 * @param maxOutDegree
	 *            The maximum number of edges that 'leaves' a node.
	 * @param generalized
	 * 			  If the agents are GGBLA agents
	 * 
	 * @throws IOException
	 * @throws UnknownHostException
	 */
	public GrayBoxLearnerClient(String remote_socket_address,
			int remote_socket_number, String environment_file_path,
			String log_file_path, int time_of_simulation,
			boolean is_real_time_simulator, double e, double alfa_decay_rate,
			double gama, String q_table_dir, boolean is_learning_phase,
			int numNodes, int maxOutDegree, boolean generalized)
			throws UnknownHostException, IOException {
		super(remote_socket_address, remote_socket_number, environment_file_path, 
				log_file_path, time_of_simulation, is_real_time_simulator);
		
		if (generalized) {
			GrayBoxLearnerAgent.configureLearningEngine(e, alfa_decay_rate, gama,
				new int[]{maxOutDegree, maxOutDegree, maxOutDegree+1}, maxOutDegree);
		} else {
			GrayBoxLearnerAgent.configureLearningEngine(e, alfa_decay_rate, gama,
				new int[]{numNodes, maxOutDegree, maxOutDegree, maxOutDegree+1}, maxOutDegree);
		}
		GrayBoxLearnerAgent.setQtableDirectory(q_table_dir);
		GrayBoxLearnerAgent.setIsLearningPhase(is_learning_phase);
		GrayBoxLearnerAgent.GENERALIZED = generalized;
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
	 * @param args List of command line arguments: 
	 *             index 0: The IP address of the SimPatrol server.
	 *             index 1: The number of the socket that the server is supposed
	 *                      to listen to this client. 
	 *             index 2: The path of the file that contains the environment. 
	 *             index 3: The path of the file that will save the collected events; 
	 *             index 4: The time of simulation. 
	 *             index 5: Indicates whether it is a real time simulation. Use "true" for realtime, 
	 *                      and "false" to cycled simulation.
	 *             index 6: The probability of an agent choose an exploration action.
	 *             index 7: The rate of the decaying of the alpha value in the q-learning algorithm. 
	 *             index 8: The discount factor in the q-learning algorithm. 
	 *             index 9: The directory to salve the q-table values. 
	 *             index 10: If it is a learning phase: "true" if the agent is learning to patrol 
	 *                       the environment, "false" if not. 
	 *             index 11: If it is the generalized (GGBLA) or the standard (GBLA) version of the agents.
	 */
	public static void main(String[] args) {
		System.out.println("Gray box learner agents!");

		try {
			String remote_socket_address = args[0];
			int remote_socket_number = Integer.parseInt(args[1]);
			String environment_file_path = args[2];
			String log_file_path = args[3];
			int time_of_simulation = Integer.parseInt(args[4]);
			boolean is_real_time_simulator = Boolean.parseBoolean(args[5]);
			double e = Double.parseDouble(args[6]);
			double alfa_decay_rate = Double.parseDouble(args[7]);
			double gama = Double.parseDouble(args[8]);
			String q_table_dir = args[9];
			boolean is_learning_phase = Boolean.parseBoolean(args[10]);
			boolean generalized = Boolean.parseBoolean(args[11]);

			FileReader freader = new FileReader(environment_file_path);
			String environment_content = freader.readWholeFile();
			Graph graph = GraphTranslator.getGraphs(GraphTranslator.parseString(environment_content))[0];
			
			int numNodes = graph.getNodees().length;
			int maxOutDegree = 0;
			for (Node n : graph.getNodees()) {
				if (n.getOutEdges().length > maxOutDegree) {
					maxOutDegree = n.getOutEdges().length;
				}
			}

			GrayBoxLearnerClient client = new GrayBoxLearnerClient(remote_socket_address, 
					remote_socket_number, environment_file_path, log_file_path, time_of_simulation,
					is_real_time_simulator, e, alfa_decay_rate, gama, q_table_dir, 
					is_learning_phase, numNodes, maxOutDegree, generalized);
		
			client.start();
		
		} catch (Exception e) {
			System.out
					.println("Usage:\n  \"java gray_box_learner.GrayBoxLearnerClient "
							+ "<IP address> <Remote socket number> <Environment file path> "
							+ "<Log file name> <Time of simulation> <Is real time simulator? (true|false)> "
							+ "<E-greedy value> <Alpha decay rate> <Discount factor> "
							+ "<Q-table values directory> <Is learning phase? (true|false)> "
							+ "<Is generalized? (true|false)>\"");
		}
	}
}