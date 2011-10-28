/* CognitiveCoordinatedClient.java */

/* The package of this class. */
package closed.CognitiveCoordinated;

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
 * letting cognitive coordinated agent clients connect to it, in the sequence.
 */
public final class CognitiveCoordinatedClient extends Client {
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
	 * 
	 * @throws IOException
	 * @throws UnknownHostException
	 */
	public CognitiveCoordinatedClient(String remote_socket_address,
			int remote_socket_number, String environment_file_path,
			String log_file_path, int time_of_simulation,
			boolean is_real_time_simulator) throws UnknownHostException,
			IOException {
		super(remote_socket_address, remote_socket_number,
				environment_file_path, log_file_path, time_of_simulation,
				is_real_time_simulator);
	}

	protected void createAndStartAgents(String[] agent_ids, int[] socket_numbers)
			throws IOException {
		this.agents = new HashSet<Agent>();

		for (int i = 0; i < agent_ids.length; i++) {
			Agent agent = null;

			if (agent_ids[i].equals("coordinator"))
				agent = new CognitiveCoordinatorAgent(agent_ids.length - 1);
			else
				agent = new CognitiveCoordinatedAgent(agent_ids[i],
						this.IS_REAL_TIME_SIMULATOR);

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
	 *            false if the simulator is a cycled one, true if not.
	 */
	public static void main(String[] args) {
		System.out.println("Cognitive coordinated agents!");

		try {
			String remote_socket_address = args[0];
			int remote_socket_number = Integer.parseInt(args[1]);
			String environment_file_path = args[2];
			String log_file_path = args[3];
			int time_of_simulation = Integer.parseInt(args[4]);
			boolean is_real_time_simulator = Boolean.parseBoolean(args[5]);

			CognitiveCoordinatedClient client = new CognitiveCoordinatedClient(
					remote_socket_address, remote_socket_number,
					environment_file_path, log_file_path, time_of_simulation,
					is_real_time_simulator);
			client.start();
		} catch (Exception e) {
			System.out.println(e.getMessage());
			System.out
					.println("Usage \"java cognitive_coordinated.CognitiveCoordinatedClient\n"
							+ "<IP address> <Remote socket number> <Environment file path>\n"
							+ "<Log file name> <Time of simulation> <Is real time simulator? (true | false)>\"");
		}
	}
}
