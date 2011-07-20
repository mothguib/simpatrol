package FBA_Open;

import java.io.IOException;
import java.net.UnknownHostException;
import java.util.HashSet;
import java.util.LinkedList;

import org.xml.sax.SAXException;

import util.agents.AgentImage;
import util.agents.SocietyImage;
import util.agents.SocietyTranslator;
import util.file.FileReader;
import util.graph.Graph;
import util.graph.GraphTranslator;
import util.graph.Node;
import util.net.TCPClientConnection;
import util.net.UDPClientConnection;

import common.Agent;
import common.Client;

public class Open_FBA_coopByNodGroup_Client  extends Client {
	double idleness_rate_for_path, idleness_rate_for_auction;
	String env_file;
	

	public Open_FBA_coopByNodGroup_Client(String remote_socket_address, int remote_socket_number,
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
		
		SocietyImage[] societies = null;
		try {
			societies = SocietyTranslator.getSocieties(SocietyTranslator.parseString(buffer.toString()));
		} catch (SAXException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
		Node[] nodes = parsed_graph.getNodees();
		int[] distributed = new int[nodes.length];
		
		int nb_active_agents = 0;
		for(SocietyImage soc : societies)
			if(!soc.id.equals("InactiveSociety"))
				nb_active_agents += soc.agents.length;
		
		int node_by_agent = nodes.length / nb_active_agents;
		int more_than_necessary = nodes.length % nb_active_agents;
		
		this.agents = new HashSet<Agent>();
		
		Open_FBA_coopByNode_Agent.setNB_AGENTS_AT_START(agent_ids.length);
		
		for(SocietyImage soc : societies)
			if(!soc.id.equals("InactiveSociety"))
				for (int i = 0; i < soc.agents.length; i++) {
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
			
					int corresponding_id;
					for(corresponding_id = 0; corresponding_id < agent_ids.length; corresponding_id++)
						if(soc.agents[i].id.equals(agent_ids[corresponding_id]))
							break;
						
			
					Agent agent = new Open_FBA_coopByNodGroup_Agent(soc.agents[i].id, -1, soc.agents[i].quit_time, 
														nb_active_agents, mynodes, 
														idleness_rate_for_path, idleness_rate_for_auction, 
														soc.agents[i].Society_to_join);
		
					if (this.IS_REAL_TIME_SIMULATOR)
						agent.setConnection(new UDPClientConnection(this.CONNECTION
								.getRemoteSocketAdress(), socket_numbers[corresponding_id]));
					else
						agent.setConnection(new TCPClientConnection(this.CONNECTION
								.getRemoteSocketAdress(), socket_numbers[corresponding_id]));
				
					agent.start();
					this.agents.add(agent);
				}
			else if(soc.agents.length > 0){
				// first order agents by time of arrival
				AgentImage[] ordered_agents = new AgentImage[soc.agents.length];
				int[] used = new int[soc.agents.length];
				boolean all_used = false;
				int current = 0;
				
				while(!all_used){
					int time_min = Integer.MAX_VALUE;
					int indice = -1;
					for(int j = 0; j < soc.agents.length; j++){
						if(used[j] != 1 && soc.agents[j].enter_time < time_min){
							time_min = soc.agents[j].enter_time;
							indice = j;
						}
					}
					ordered_agents[current++] = soc.agents[indice];
					used[indice] = 1;
					all_used = true;
					for(int j = 0; j < soc.agents.length; j++)
						all_used &= ( used[j] == 1);
				}
				
				// then calculate for each how many agents are in the system when they enter it
				int[] nb_agents_in_system = new int[ordered_agents.length];
				for(int j = 0; j < ordered_agents.length; j++)
					nb_agents_in_system[j] = nb_active_agents + j;
				
				for(SocietyImage soc2 : societies)
					for(int j = 0; j < soc2.agents.length; j++){
						int quit_time = soc2.agents[j].quit_time;
						if(quit_time == -1)
							continue;
						for(int k = 0; k < ordered_agents.length; k++){
							if(quit_time <= ordered_agents[k].enter_time)
								nb_agents_in_system[k]--;
						}
					}
				
				for(int j = 0; j < ordered_agents.length; j++){
					int enter_time = ordered_agents[j].enter_time;
					for(int k = j+1; k < ordered_agents.length; k++){
						if(enter_time == ordered_agents[k].enter_time)
							nb_agents_in_system[k]--;
					}
				}
				
				for (int i = 0; i < soc.agents.length; i++) {

					int corresponding_id;
					for(corresponding_id = 0; corresponding_id < agent_ids.length; corresponding_id++)
						if(soc.agents[i].id.equals(agent_ids[corresponding_id]))
							break;
					
					Agent agent = new Open_FBA_coopByNodGroup_Agent(soc.agents[i].id, soc.agents[i].enter_time, soc.agents[i].quit_time, 
																nb_agents_in_system[i] + 1, new LinkedList<String>(), 
																idleness_rate_for_path, idleness_rate_for_auction, 
																soc.agents[i].Society_to_join);

					if (this.IS_REAL_TIME_SIMULATOR)
						agent.setConnection(new UDPClientConnection(this.CONNECTION
									.getRemoteSocketAdress(), socket_numbers[corresponding_id]));
					else
						agent.setConnection(new TCPClientConnection(this.CONNECTION
									.getRemoteSocketAdress(), socket_numbers[corresponding_id]));
					
					agent.start();
					this.agents.add(agent);
				}
				
				
				
				
			}
				

}

	public static void main(String[] args) {
		System.out.println("Open FBA Agents - coop by node group strategy!");

		try {
			String remote_socket_address = args[0];
			int remote_socket_number = Integer.parseInt(args[1]);
			String environment_file_path = args[2];
			String log_file_path = args[3];
			int time_of_simulation = Integer.parseInt(args[4]);
			boolean is_real_time_simulator = Boolean.parseBoolean(args[5]);
			double idleness_rate_for_path = Double.parseDouble(args[6]);
			double idleness_rate_for_auction = Double.parseDouble(args[7]);
			
			
			Open_FBA_coopByNodGroup_Client client = new Open_FBA_coopByNodGroup_Client(
					remote_socket_address, remote_socket_number,
					environment_file_path, log_file_path, time_of_simulation,
					is_real_time_simulator, idleness_rate_for_path, idleness_rate_for_auction);
			client.start();
		} catch (Exception e) {
			System.out
					.println("Usage \"java FBA_Open.Open_FBA_coopByNodGroup_Client \n"
							+ "<IP address> <Remote socket number> <Environment file path>\n"
							+ "<Log file name> <Time of simulation> <Is real time simulator? (true | false)>\"\n"
							+ "<rate of idleness in calculating heuristic destination> <rate of idleness in calculating nodes to trade>\n");
		}
	}

}
