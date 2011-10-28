/* CycledClient.java */

/* The package of this class. */
package closed.SingleCycle;

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
 * letting cycled agent clients connect to it, in the sequence.
 */
public final class CycledClient extends Client {
	
	boolean use_precise_solution;
	
	
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
	public CycledClient(String remote_socket_address, int remote_socket_number,
			String environment_file_path, String log_file_path,
			double time_of_simulation, boolean is_real_time_simulator, boolean use_precise_solution)
			throws UnknownHostException, IOException {
		super(remote_socket_address, remote_socket_number,
				environment_file_path, log_file_path, time_of_simulation,
				is_real_time_simulator);
		this.use_precise_solution = use_precise_solution;
	}

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
	 * @param silentMode
	 * 				TRUE if the Client must start in the silentMode 
	 * @throws IOException
	 * @throws UnknownHostException
	 */
	/*public CycledClient(String remote_socket_address, int remote_socket_number,
			String environment_file_path, String[] metrics_file_paths,
			double metrics_collecting_rate, String log_file_path,
			double time_of_simulation, boolean is_real_time_simulator, boolean silentMode)
			throws UnknownHostException, IOException {
		super(remote_socket_address, remote_socket_number,
				environment_file_path, metrics_file_paths,
				metrics_collecting_rate, log_file_path, time_of_simulation,
				is_real_time_simulator, silentMode);
	}*/
	
	protected void createAndStartAgents(String[] agent_ids, int[] socket_numbers)
			throws IOException {
		this.agents = new HashSet<Agent>();

		for (int i = 0; i < agent_ids.length; i++) {
			Agent agent = null;

			if (agent_ids[i].equals("coordinator"))
				agent = new CycledCoordinatorAgent(this.use_precise_solution);
			else
				agent = new CycledAgent(agent_ids[i]);

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
	 *              index 0: The IP address of the SimPatrol server.
	 *              index 1: The number of the socket that the server is supposed
	 *                       to listen to this client. 
	 *              index 2: The path of the file that contains the environment. 
	 *              index 3. The path of the file that will save the mean instantaneous idlenesses; 
	 *              index 4: The path of the file that will save the max instantaneous idlenesses;
	 *              index 5: The path of the file that will save the mean (overall) idlenesses; 
	 *              index 6. The path of the file that will save the max (overall) idlenesses; 
	 *              index 7: The time interval used to collect the metrics; 
	 *              index 8: The path of the file that will save the collected events; 
	 *              index 9: The time of simulation. 
	 *              index 10: Indicates whether it is a real time simulation. Use "true" for realtime, 
	 *                        and "false" to cycled simulation.
	 *              index 11 : "true" if only using the precise reorientation method, "false" if using the fast ones 
	 */
	public static void execute(String[] args) {
		System.out.println("Cycled agents!");

		try {
			String remote_socket_address = args[0];
			int remote_socket_number = Integer.parseInt(args[1]);
			String environment_file_path = args[2];
			String log_file_path = args[3];
			double time_of_simulation = Double.parseDouble(args[4]);
			boolean is_real_time_simulator = Boolean.parseBoolean(args[5]);
			boolean use_precise_solution = Boolean.parseBoolean(args[6]);
			
			CycledClient client;
			
			/*
			if ( args.length == 12 ) {
					boolean silentMode = Boolean.parseBoolean(args[11]);
					client = new CycledClient(remote_socket_address,
									remote_socket_number, environment_file_path,
									metric_file_paths, metrics_collecting_rate, log_file_path,
									time_of_simulation, is_real_time_simulator, silentMode );					
			} else {*/
				client = new CycledClient(remote_socket_address,
									remote_socket_number, environment_file_path,
									log_file_path, time_of_simulation, is_real_time_simulator, use_precise_solution);	
			//}

			client.start();

		} catch (Exception e) {			
			System.out
					.println("\nUsage:\n  java cycled.CycledClient "
								+ "<IP address> <Remote socket number> <Environment file path> "
								+ "<Log file name> <Time of simulation> <Is real time simulator? (true | false)>\""
								+ "<use the precise but slow reorientation method ? (true|false)>\" an\n");
		}
		
	}
	
	public static void main(String[] args) {
		
		if (args.length == 0) {
			
			// default parameters, in case no parameters were provided
			args = new String[]{
						"127.0.0.1",
						"5000",
						"res\\ambiente.xml",
						"tmp\\simulation_log.txt",
						"50",
						"false",
						//"true"
					};
		}
		
		execute(args);
	}
	
}
