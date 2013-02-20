package HPCC;


import java.io.IOException;
import java.net.UnknownHostException;
import java.util.HashSet;
import java.util.LinkedList;

import org.xml.sax.SAXException;

import launchers.Launcher;

import closed.HPCC.HeuristicCognitiveCoordinatedAgent;
import closed.HPCC.HeuristicCognitiveCoordinatedClient;
import closed.HPCC.HeuristicCognitiveCoordinatorAgent;

import util.HS_Graph;
import util.agents.AgentImage;
import util.agents.SocietyImage;
import util.agents.SocietyTranslator;
import util.file.FileReader;
import util.graph.Graph;
import util.graph.GraphTranslator;
import util.graph.Node;
import util.net.TCPClientConnection;
import util.net.UDPClientConnection;
import util.net.TCPClientConnection;
import util.net.UDPClientConnection;

import common.Agent;

public class HS_HPCCLauncher extends Launcher {
	String env_file;
	int max_hops;
	
	
	
	public HS_HPCCLauncher(String environment_dir_path,
			String env_gen_name, int numEnv, String log_dir_path,
			String log_gen_name, int time_of_simulation, 
			boolean coord_hack, int max_graph_hops)
			throws UnknownHostException, IOException {
		super(environment_dir_path, 
				env_gen_name, numEnv, 
				log_dir_path, log_gen_name,
				time_of_simulation);
		
		env_file = this.ENVIRONMENT_DIR_PATH + "/" + this.ENVIRONMENT_GEN_NAME + "_" + this.NUM_ENV + ".txt";
		max_hops = max_graph_hops;
		HS_OpenHPCCCoordinator.NO_COORD_HACK = !coord_hack;
		
	}

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


		Node[] nodes = parsed_graph.getNodes();
		HS_Graph hs_graph = new HS_Graph("hs_graph", nodes);
		hs_graph.getDiameter();
		int[] distributed = new int[nodes.length];

		int nb_active_agents = 0;
		for(SocietyImage soc : societies)
			if(!soc.id.equals("InactiveSociety"))
				nb_active_agents += soc.agents.length;

		int node_by_agent = nodes.length / nb_active_agents;
		int more_than_necessary = nodes.length % nb_active_agents;

		this.agents = new HashSet<Agent>();

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

					Agent agent;

					if (agent_ids[corresponding_id].equals("coordinator"))
						agent = new HS_OpenHPCCCoordinator(hs_graph, max_hops);
					else
						agent = new HS_OpenHPCCAgent(soc.agents[i].id, this.IS_REAL_TIME_SIMULATOR, -1, soc.agents[i].quit_time, soc.agents[i].Society_to_join, 
								hs_graph, max_hops);

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

				// then calculate forOpenHPCCCoordinator.NO_COORD_HACK = !coord_hack; each how many agents are in the system when they enter it
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

					Agent agent;

					if (agent_ids[corresponding_id].equals("coordinator"))
						agent = new HS_OpenHPCCCoordinator(hs_graph, max_hops);
					else
						agent = new HS_OpenHPCCAgent(soc.agents[i].id, this.IS_REAL_TIME_SIMULATOR, soc.agents[i].enter_time, soc.agents[i].quit_time, soc.agents[i].Society_to_join, 
								hs_graph, max_hops);

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
		System.out.println("Heuristic cognitive coordinated agents!");

		try {
			String environment_dir_path = args[0];
			String env_gen_name = args[1];
			int numEnv  = Integer.parseInt(args[2]);
			String log_dir_path = args[3];
			String log_gen_name = args[4];
			int time_of_simulation= Integer.parseInt(args[5]);
			boolean COORD_HACK = Boolean.parseBoolean(args[6]);
			int max_graph_hops = Integer.parseInt(args[7]);
			
			HS_HPCCLauncher client = new HS_HPCCLauncher(
					environment_dir_path, 
					env_gen_name, numEnv, 
					log_dir_path, log_gen_name, 
					time_of_simulation, COORD_HACK, max_graph_hops);
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
