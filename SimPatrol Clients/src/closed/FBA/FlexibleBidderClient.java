package closed.FBA;

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

import common.Agent;
import common.Client;

/**
 * Client for the FlexibleBidderAgent class
 * 
 * @author pouletc
 *
 */
public class FlexibleBidderClient extends Client {
	
	double idleness_rate_for_path, idleness_rate_for_auction;
	String env_file;
	

	public FlexibleBidderClient(String remote_socket_address, int remote_socket_number,
			String environment_file_path, String log_file_path,
			double time_of_simulation, boolean is_real_time_simulator, 
			double idleness_rate_for_path, double idleness_rate_for_auction)
			throws UnknownHostException, IOException {
		
		super(remote_socket_address, remote_socket_number,
				environment_file_path, log_file_path, time_of_simulation,
				is_real_time_simulator);
		
		this.idleness_rate_for_path = idleness_rate_for_path;
		this.idleness_rate_for_auction = idleness_rate_for_auction;
		env_file = environment_file_path;
	}
	
	
	
	@Override
	protected void createAndStartAgents(String[] agent_ids, int[] socket_numbers)
	throws IOException {
		FileReader file_reader = new FileReader(this.env_file);

		// holds the environment obtained from the file
		StringBuffer buffer = new StringBuffer();
		while (!file_reader.isEndOfFile()) {
			buffer.append(file_reader.readLine());
		}

		// closes the read file
		file_reader.close();
		Graph parsed_graph = null;
		try {
			parsed_graph = GraphTranslator.getGraphs(GraphTranslator.parseString(buffer.toString()))[0];
		} catch (SAXException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		Node[] nodes = parsed_graph.getNodes();
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
			
			
			Agent agent = new FlexibleBidderAgent(agent_ids[i], agent_ids.length, mynodes, idleness_rate_for_path, idleness_rate_for_auction);
		
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
		System.out.println("Flexible Bidder agents!");

		try {
			String remote_socket_address = args[0];
			int remote_socket_number = Integer.parseInt(args[1]);
			String environment_file_path = args[2];
			String log_file_path = args[3];
			int time_of_simulation = Integer.parseInt(args[4]);
			boolean is_real_time_simulator = Boolean.parseBoolean(args[5]);
			double idleness_rate_for_path = Double.parseDouble(args[6]);
			double idleness_rate_for_auction = Double.parseDouble(args[7]);
			
			
			FlexibleBidderClient client = new FlexibleBidderClient(
					remote_socket_address, remote_socket_number,
					environment_file_path, log_file_path, time_of_simulation,
					is_real_time_simulator, idleness_rate_for_path, idleness_rate_for_auction);
			client.start();
		} catch (Exception e) {
			System.out
					.println("Usage \"java FBA.FlexibleBidderClient\n"
							+ "<IP address> <Remote socket number> <Environment file path>\n"
							+ "<Log file name> <Time of simulation> <Is real time simulator? (true | false)>\""
							+ "<rate of idleness in calculating heuristic destination> <rate of idleness in calculating nodes to trade>");
		}
	}

}
