package launchers;

import heuristic_cognitive_coordinated_OLD.HeuristicCognitiveCoordinatedAgent_OLD;
import heuristic_cognitive_coordinated_OLD.HeuristicCognitiveCoordinatorAgent_OLD;

import java.io.IOException;
import java.net.UnknownHostException;
import java.util.HashSet;

import util.net_OLD.TCPClientConnection_OLD;
import util.net_OLD.UDPClientConnection_OLD;

import common_OLD.Agent_OLD;

public class HeuristicCogCoordLauncher extends Launcher {

	public HeuristicCogCoordLauncher(String environment_dir_path,
			String env_gen_name, int numEnv, String log_dir_path,
			String log_gen_name, int time_of_simulation)
			throws UnknownHostException, IOException {
		super(environment_dir_path, 
				env_gen_name, numEnv, 
				log_dir_path, log_gen_name,
				time_of_simulation);
	}

	@Override
	protected void createAndStartAgents(String[] agent_ids, int[] socket_numbers) throws IOException {
		this.agents = new HashSet<Agent_OLD>();
		
		for (int i = 0; i < agent_ids.length; i++) {
			Agent_OLD agent = null;
		
			if (agent_ids[i].equals("coordinator"))
				agent = new HeuristicCognitiveCoordinatorAgent_OLD();
			else
				agent = new HeuristicCognitiveCoordinatedAgent_OLD(agent_ids[i],
						this.IS_REAL_TIME_SIMULATOR);
		
			if (this.IS_REAL_TIME_SIMULATOR)
				agent.setConnection(new UDPClientConnection_OLD(this.CONNECTION
						.getRemoteSocketAdress(), socket_numbers[i]));
			else
				agent.setConnection(new TCPClientConnection_OLD(this.CONNECTION
						.getRemoteSocketAdress(), socket_numbers[i]));
		
			agent.start();
		}
	}
	
	
	public static void main(String[] args) {
		System.out.println("Heuristic cognitive coordinated agents!");

		try {
			String environment_dir_path = args[0];
			String env_gen_name = args[1];
			int numEnv  = Integer.parseInt(args[2]);
			String log_dir_path = args[3];
			String log_gen_name = args[4];
			int time_of_simulation= Integer.parseInt(args[5]);

			HeuristicCogCoordLauncher client = new HeuristicCogCoordLauncher(
					environment_dir_path, 
					env_gen_name, numEnv, 
					log_dir_path, log_gen_name, 
					time_of_simulation);
			client.start();
		} catch (Exception e) {
			System.out
					.println("Usage \"java launchers.HeuristicCogCoordLauncher\n"
							+ "<Environment directory path> <Environment generic name> <number of environments>\n"
							+ "<log directory path> <Log generic name> <num of cycle in simulations> \n" 
							+ "It will launch N simulations with the environments ENV_DIR_PATH\\ENV_GEN_NAME_i.txt \n"
							+ "and save the logs as LOG_DIR_PATH\\LOG_GEN_NAME_i.txt");
		}
	}

}
