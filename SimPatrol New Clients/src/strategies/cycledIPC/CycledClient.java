package strategies.cycledIPC;

import java.io.IOException;
import java.net.UnknownHostException;
import java.util.HashSet;

import agent_library.connections.IpcConnection;
import agent_library.coordinated_agents.CoordinatedAgent;
import util.net.TCPClientConnection;
import util.net.UDPClientConnection;
import view.connection.IPCConnection;
import common.Agent;
import common.Client;


/**
 * Implements a client that connects to the SimPatrol server and configures it,
 * letting cycled agent clients connect to it, in the sequence.
 */
public final class CycledClient extends Client {

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
	public CycledClient(String remote_socket_address, int remote_socket_number,
			String environment_file_path, String log_file_path,
			double time_of_simulation, boolean is_real_time_simulator)
			throws UnknownHostException, IOException {
		super(remote_socket_address, remote_socket_number, environment_file_path, 
				log_file_path, time_of_simulation, is_real_time_simulator);
	}
	
	public CycledClient(String args[])
			throws UnknownHostException, IOException {
		super(args);
	}
	
	protected void createAndStartAgents(String[] agent_ids, int[] socket_numbers)
			throws IOException {
		this.agents = new HashSet<Agent>();

		for (int i = 0; i < agent_ids.length; i++) {

			if (agent_ids[i].equals("coordinator")) {
				CycledCoordinatorAgent cagent = new CycledCoordinatorAgent();
				IpcConnection con = new IpcConnection(agent_ids[i]);				
				cagent.setIpcConnection(con);
				cagent.start();
				this.agents.add(cagent);

			} else {
				CycledAgent agent = new CycledAgent();
				IpcConnection con = new IpcConnection(agent_ids[i]);				
				agent.setIpcConnection(con);
				agent.start();
				this.agents.add(agent);

			}

//			if (this.IS_REAL_TIME_SIMULATOR)
//				agent.setConnection(new UDPClientConnection(this.CONNECTION
//						.getRemoteSocketAdress(), socket_numbers[i]));
//			else{
//				IpcConnection con = new IpcConnection(agent_ids[i]);				
//				agent.setIpcConnection(con);
//				//con.addObserver(agent);
//			}
				

//			agent.start();
//			
//			this.agents.add(agent);
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
		System.out.println("Cycled agents!");

		try {
					
			CycledClient client;
			
			
			client = new CycledClient(args);	

			client.start();

		} catch (Exception e) {			
			System.out
					.println("\nUsage:\n  java cycled.CycledClient "
								+ "<IP address> <Remote socket number> <Environment file path> "
								+ "<Log file name> <Time of simulation> <Is real time simulator? (true | false)>\" \n");
		}
		
	}
	
	
}
