package launchers;

import java.io.IOException;
import java.net.UnknownHostException;
import java.util.HashSet;

import util.net.TCPClientConnection;
import util.net.UDPClientConnection;

import common.Agent;

import cycled.CycledAgent;
import cycled.CycledCoordinatorAgent;

public class CycledLauncher extends Launcher {
	boolean use_precise_solution;

	public CycledLauncher(String environment_dir_path, String env_gen_name,
			int numEnv, String log_dir_path, 
			String log_gen_name,
			int time_of_simulation, boolean use_precise_solution) throws UnknownHostException, IOException {
		super(environment_dir_path, 
				env_gen_name, numEnv, 
				log_dir_path, log_gen_name,
				time_of_simulation);
		this.use_precise_solution = use_precise_solution;

	}

	@Override
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
	 * @param args
	 *            
	 */
	public static void main(String[] args) {
		System.out.println("Cycled agents!");

		try {
			String environment_dir_path = args[0];
			String env_gen_name = args[1];
			int numEnv  = Integer.parseInt(args[2]);
			String log_dir_path = args[3];
			String log_gen_name = args[4];
			int time_of_simulation= Integer.parseInt(args[5]);
			boolean use_precise_solution = Boolean.parseBoolean(args[6]);

			CycledLauncher client = new CycledLauncher(
					environment_dir_path, 
					env_gen_name, numEnv, 
					log_dir_path, log_gen_name, 
					time_of_simulation, use_precise_solution);
			client.start();
		} catch (Exception e) {
			System.out
					.println("Usage \"java launchers.CycledLauncher\n"
							+ "<Environment directory path> <Environment generic name> <number of environments>\n"
							+ "<log directory path> <Log generic name> <num of cycle in simulations> \n" 
							+ "It will launch N simulations with the environments ENV_DIR_PATH\\ENV_GEN_NAME_i.txt \n"
							+ "and save the logs as LOG_DIR_PATH\\LOG_GEN_NAME_i.txt"
							+ "<use the precise but slow reorientation method ? (true|false)>\" an\n");
		}
	}

}
