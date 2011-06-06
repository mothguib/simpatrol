package launchers;

import java.io.IOException;
import java.net.UnknownHostException;
import java.util.HashSet;
import java.util.LinkedList;

import org.xml.sax.SAXException;

import util.file.FileReader;
import util.graph.Graph;
import util.graph.GraphTranslator;
import util.graph.Node;
import util.net.TCPClientConnection;
import util.net.UDPClientConnection;
import FBA.FlexibleBidder2Agent;

import common.Agent;

public class FBA2Launcher extends Launcher {

	
	double idleness_rate_for_path, idleness_rate_for_auction;
	StringBuffer buffer;

	public FBA2Launcher(String environment_dir_path, String env_gen_name,
			int numEnv, String log_dir_path, String log_gen_name,
			int time_of_simulation,
			double idleness_rate_for_path, double idleness_rate_for_auction)
			throws UnknownHostException, IOException {
		
		super(environment_dir_path, env_gen_name, numEnv, 
				log_dir_path, log_gen_name,
				time_of_simulation);
		
		this.idleness_rate_for_path = idleness_rate_for_path;
		this.idleness_rate_for_auction = idleness_rate_for_auction;
		
		buffer = new StringBuffer();
		String env_file = this.ENVIRONMENT_DIR_PATH + "/" + this.ENVIRONMENT_GEN_NAME + "_" + this.NUM_ENV + ".txt";
		FileReader file_reader = new FileReader(env_file);
		while (!file_reader.isEndOfFile()) {
			buffer.append(file_reader.readLine());
		}

		// closes the read file
		file_reader.close();
		
	}
	
	
	@Override
	protected void createAndStartAgents(String[] agent_ids, int[] socket_numbers)
			throws IOException {
		
			Graph parsed_graph = null;
			try {
				parsed_graph = GraphTranslator.getGraphs(GraphTranslator.parseString(buffer.toString()))[0];
			} catch (SAXException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			Node[] nodes = parsed_graph.getNodees();
			int[] distributed = new int[nodes.length];
			
			int node_by_agent = nodes.length / agent_ids.length;
			int more_than_necessary = nodes.length % agent_ids.length;
			
			this.agents = new HashSet<Agent>();
			
			for (int i = 0; i < agent_ids.length; i++) {
				int nb_nodes;
				if(more_than_necessary > 0){
					nb_nodes = node_by_agent + 1;
					more_than_necessary--;
				}
				else
					nb_nodes = node_by_agent;
				
				LinkedList<String> mynodes = new LinkedList<String>(); 
				for(int j = 0; j < nb_nodes; j++){
					int rand = (int) (Math.random() * nodes.length );
					boolean left = true;
					while(distributed[rand] != 0 && left)
						rand = (int) (Math.random() * nodes.length );
					
					mynodes.add(nodes[rand].getObjectId());
					distributed[rand] = 1;
					
					left = false;
					for (int k = 0; k < distributed.length; k++)
						left |= (distributed[k] == 0);
				}
				
				
				Agent agent = new FlexibleBidder2Agent(agent_ids[i], agent_ids.length, mynodes, idleness_rate_for_path, idleness_rate_for_auction);
			
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
	
	public static void main(String[] args) {
		System.out.println("Flexible Bidder 2 agents!");

		try {
			String environment_dir_path = args[0];
			String env_gen_name = args[1];
			int numEnv  = Integer.parseInt(args[2]);
			String log_dir_path = args[3];
			String log_gen_name = args[4];
			int time_of_simulation= Integer.parseInt(args[5]);
			double idleness_rate_for_path = Double.parseDouble(args[6]);
			double idleness_rate_for_auction = Double.parseDouble(args[7]);
			
			
			FBA2Launcher client = new FBA2Launcher(
					environment_dir_path, 
					env_gen_name, numEnv, 
					log_dir_path, log_gen_name, 
					time_of_simulation, idleness_rate_for_path, idleness_rate_for_auction);
			client.start();
		} catch (Exception e) {
			System.out
					.println("Usage \"java FBA.FBALauncher2\n"
							+ "<Environment directory path> <Environment generic name> <number of environments>\n"
							+ "<log directory path> <Log generic name> <num of cycle in simulations> \n" 
							+ "<rate of idleness in calculating heuristic destination> <rate of idleness in calculating nodes to trade>");
		}
	}

}
