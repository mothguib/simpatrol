package reactive_with_flags;

import java.io.IOException;
import java.net.UnknownHostException;

public class Main {
	/**
	 * @param args Arguments:
	 *    index 0: The IP address of the SimPatrol server.
	 *    index 1: The number of the socket that the server is supposed to listen to this client.
	 *    index 2: The path of the file that contains the environment.
	 *    index 3. The path of the file that will save the mean instantaneous idlenesses;
	 *    index 4. The path of the file that will save the max instantaneous idlenesses;
	 *    index 5. The path of the file that will save the mean idlenesses;
	 *    index 6. The path of the file that will save the max idlenesses;
	 *    index 7: The time of simulation.
	 *    index 8: false if the simulator is a cycled one, true if not
	 *    
	 * @throws IOException 
	 * @throws UnknownHostException */
	public static void main(String[] args) throws UnknownHostException, IOException {
		System.out.println("Reactive with flags agents!");
		String remote_socket_address = args[0];
		int remote_socket_number = Integer.parseInt(args[1]);
		String environment_file_path = args[2];
		String[] metric_file_paths = {args[3], args[4], args[5], args[6]};
		int time_of_simulation = Integer.parseInt(args[7]);
		boolean is_real_time_simulator = Boolean.parseBoolean(args[8]);
			
		ReactiveWithFlagsClient client = new ReactiveWithFlagsClient(remote_socket_address, remote_socket_number, environment_file_path, metric_file_paths, time_of_simulation, is_real_time_simulator);
		client.start();
	}
}