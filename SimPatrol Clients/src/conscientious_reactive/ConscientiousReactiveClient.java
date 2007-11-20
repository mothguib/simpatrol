/* ConscientiousReactiveClient.java */

/* The package of this class. */
package conscientious_reactive;

/* Imported classes and/or interfaces. */
import java.io.IOException;
import java.net.UnknownHostException;
import java.util.HashSet;

import common.Agent;
import common.Client;

/**
 * Implements a client that connects to the SimPatrol server and configures it,
 * letting conscientious reactive agent clients connect to it, in the sequence.
 */
public class ConscientiousReactiveClient extends Client {
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
	 *            index 0. The file that will save the mean instantaneous
	 *            idlenesses; index 1. The file that will save the max
	 *            instantaneous idlenesses; index 2. The file that will save the
	 *            mean idlenesses; index 3. The file that will save the max
	 *            idlenesses;
	 * @param metrics_collecting_rate
	 *            The time interval used to collect the metrics.
	 * @param time_of_simulation
	 *            The time of simulation.
	 * @param is_real_time_simulator
	 *            TRUE if the simulator is a real time one, FALSE if not.
	 * 
	 * @throws IOException
	 * @throws UnknownHostException
	 */
	public ConscientiousReactiveClient(String remote_socket_address,
			int remote_socket_number, String environment_file_path,
			String[] metrics_file_paths, int metrics_collecting_rate,
			int time_of_simulation, boolean is_real_time_simulator)
			throws UnknownHostException, IOException {
		super(remote_socket_address, remote_socket_number,
				environment_file_path, metrics_file_paths,
				metrics_collecting_rate, time_of_simulation,
				is_real_time_simulator);
	}

	protected void createAndStartAgents(int[] socket_numbers)
			throws IOException {
		this.agents = new HashSet<Agent>();

		for (int i = 0; i < socket_numbers.length; i++) {
			ConscientiousReactiveAgent agent = null;

			if (this.IS_REAL_TIME_SIMULATOR)
				agent = new RealTimeConscientiousReactiveAgent(this.CONNECTION
						.getRemoteSocketAdress(), socket_numbers[i]);
			else
				agent = new CycledConscientiousReactiveAgent(this.CONNECTION
						.getRemoteSocketAdress(), socket_numbers[i]);

			this.agents.add(agent);
			agent.start();
		}
	}
}