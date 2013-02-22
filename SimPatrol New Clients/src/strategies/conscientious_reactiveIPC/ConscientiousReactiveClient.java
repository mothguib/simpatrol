/* ConscientiousReactiveClient.java */

/* The package of this class. */
package strategies.conscientious_reactiveIPC;

/* Imported classes and/or interfaces. */
import java.io.IOException;
import java.net.UnknownHostException;
import java.util.HashSet;

import agent_library.connections.IpcConnection;

import util.net.TCPClientConnection;
import util.net.UDPClientConnection;
import common.Agent;
import common.Client;


/**
 * Implements a client that connects to the SimPatrol server and configures it,
 * letting conscientious reactive agent clients connect to it, in the sequence.
 */
public final class ConscientiousReactiveClient extends Client {
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
	public ConscientiousReactiveClient(String remote_socket_address,
			int remote_socket_number, String environment_file_path,
			String log_file_path, double time_of_simulation,
			boolean is_real_time_simulator) throws UnknownHostException,
			IOException {
		super(remote_socket_address, remote_socket_number, environment_file_path, 
				log_file_path, time_of_simulation, is_real_time_simulator);
	}
	public ConscientiousReactiveClient(String args[]) throws UnknownHostException,
			IOException {
		super(args);
	}

	protected void createAndStartAgents(String[] agent_ids, int[] socket_numbers)
			throws IOException {
		this.agents = new HashSet<Agent>();

		for (int i = 0; i < agent_ids.length; i++) {
			ConscientiousReactiveAgent agent = new ConscientiousReactiveAgent();

			if (this.IS_REAL_TIME_SIMULATOR)
				agent.setConnection(new UDPClientConnection(this.CONNECTION
						.getRemoteSocketAdress(), socket_numbers[i]));
			else{
				IpcConnection connection = new IpcConnection(agent_ids[i]);
				agent.setConnection2(connection);
			//	agent.setConnection(new TCPClientConnection(this.CONNECTION
				//		.getRemoteSocketAdress(), socket_numbers[i]));
			}
			agent.start();
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
	 */
	public static void main(String[] args) {
		System.out.println("Conscientious reactive agents!");

		try {
			
			ConscientiousReactiveClient client = new ConscientiousReactiveClient(
					args);
			client.start();
		} catch (Exception e) {
			System.out
					.println("Usage \"java conscientious_reactive.ConscientiousReactiveClient "
								+ "<IP address> <Remote socket number> <Environment file path> "
								+ "<Log file name> <Time of simulation> <Is real time simulator? (true|false)>\"");
		}
	}
}