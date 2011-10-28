package test_agent;

import java.io.IOException;
import java.util.LinkedList;

import util.StringAndDouble;

import common.Agent;

public class Test_Message_Agent  extends Agent {
	
	
	private	double time = -1;
	private String id;
	private LinkedList<String> AGENTS_PERCEIVED;
	
	private boolean sent = false;
	
	public Test_Message_Agent(String id) {
		super();
		this.id = id;
	}
	
	public static void main(String[] args){
		System.out.println("compilé");
	}

	/**
	 * Lets the agent perceive the neighborhood.
	 * 
	 * @param perceptions
	 *            The current perceptions of the agent.
	 * @return The id of the nodes in the neighborhood.
	 */
	private String[] perceiveNeighbourhood(String perception) {
		// tries to obtain the most recent perception of the neighbourhood
		if (perception.indexOf("<perception type=\"0\"") > -1) {
			// holds the answer for the method
			LinkedList<String> nodes = new LinkedList<String>();

			// holds the index of the next node
			int next_node_index = perception.indexOf("<node ");

			// while there are nodes to be read
			while (next_node_index > -1) {
				// updates the current perception
				perception = perception.substring(next_node_index);

				// obtains the id of the current node
				int current_node_id_index = perception.indexOf("id=\"");
				perception = perception.substring(current_node_id_index + 4);
				String node_id = perception.substring(0, perception.indexOf("\""));

				// adds the id of the node to the list of obtained
				// nodes
				nodes.add(node_id);

				// obtains the index of the next node
				next_node_index = perception.indexOf("<node ");
			}

			// mounts and returns the answer of the method
			String[] answer = new String[nodes.size()];
			for (int j = 0; j < answer.length; j++)
				answer[j] = nodes.get(j);

			return answer;
		}

		// default answer
		return new String[0];
	}
	
	
	/**
	 * Lets the agent perceive its current position.
	 * 
	 * @param perceptions
	 *            The current perceptions of the agent.
	 * @return The current position of the agent, as a pair "current node id -
	 *         elapsed length on the current edge".
	 */
	private StringAndDouble perceiveCurrentPosition(String perception) {
		// tries to obtain the most recent self perception of the agent
		if (perception.indexOf("<perception type=\"4\"") > -1) {
			// obtains the id of the current node
			int node_id_index = perception.indexOf("node_id=\"");
			// CP : changed starting index
			perception = perception.substring(node_id_index + 9);
			String node_id = perception.substring(0, perception.indexOf("\""));

			// obtains the elapsed length on the current edge
			int elapsed_length_index = perception.indexOf("elapsed_length=\"");
			double elapsed_length = 0;
			if(elapsed_length_index != -1){
				perception = perception.substring(elapsed_length_index + 16);
				elapsed_length = Double.parseDouble(perception.substring(0, perception.indexOf("\"")));
			}

			// returs the answer of the method
			return new StringAndDouble(node_id, elapsed_length);
		}

		// default answer
		return null;
	}
	
	
	
	private boolean perceiveAgentsPositions(String perception) {
		if (perception.indexOf("<perception type=\"1\"") > -1) {
			
			int agent_id_index = perception.indexOf("<agent id=\"");
			while (agent_id_index > -1) {
				perception = perception.substring(agent_id_index + 11);
				String agent_id = perception.substring(0, perception
						.indexOf("\""));

				int agent_node_id_index = perception.indexOf("node_id=\"");
				perception = perception.substring(agent_node_id_index + 9);
				String agent_node_id = perception.substring(0, perception.indexOf("\""));

				this.AGENTS_PERCEIVED.add(agent_id);
				
				agent_id_index = perception.indexOf("<agent id=\"");
			}

			// returns the success of the perception
			return true;
		}

		// default answer
		return false;
	}
	
	/**
	 * Lets the agent go to the given node.
	 * 
	 * @param The
	 *            next node to where the agent is supposed to go.
	 * @throws IOException
	 */
	private void goTo(String next_node_id) throws IOException {
		String message = "<action type=\"1\" node_id=\"" + next_node_id
				+ "\"/>";
		this.connection.send(message);
	}
	
	
	private boolean perceiveTime(String perception){
		// tries to obtain the most recent self perception of the agent
		if (perception.indexOf("<perception type=\"-1\"") > -1) {
			// obtains the id of the current node
			int time_index = perception.indexOf("time=\"");
			// CP : changed starting index
			perception = perception.substring(time_index + 6);
			String time_str = perception.substring(0, perception.indexOf("\""));
			this.time = Double.valueOf(time_str);
			
			return true;
		}
		
		return false;
	}
	
	private boolean perceiveMessages(String perception){
		// tries to obtain the most recent self perception of the agent
		if (perception.indexOf("<perception type=\"3\"") > -1) {
			int message_index = perception.indexOf("message=\"");
			// CP : changed starting index
			perception = perception.substring(message_index + 9);
			String message_str = perception.substring(0, perception.indexOf("\""));
			
			System.out.println(message_str);
			
			return true;
		}
		
		return false;
	}
	
	private void sendMessage(String target) throws IOException{
		String message = "<action type=\"8\" target_agent=\"" + target
		+ "\" message=\"Bonjour, c'est " + this.id + "!\"/>";
		this.connection.send(message);
	}
	
	
	public void run() {
		// starts its connection
		this.connection.start();

		// the current position of the agent
		StringAndDouble current_position = null;
		AGENTS_PERCEIVED = new LinkedList<String>();
		
		// while the agent is supposed to work
		while (!this.stop_working) {
			boolean pos_actualized = false;
			boolean time_actualized = false;
			AGENTS_PERCEIVED.clear();
			// the neighborhood of where the agent is
			String[] neighbourhood = new String[0];

			// while the current position or neighborhood is not valid
			while (!pos_actualized || neighbourhood.length == 0 || !time_actualized) {
				// obtains the perceptions from the server
				String[] perceptions = this.connection.getBufferAndFlush();

				for(int i = perceptions.length - 1; i > -1; i--){
					
					time_actualized |= perceiveTime(perceptions[i]);
					
					perceiveMessages(perceptions[i]);
					
					perceiveAgentsPositions(perceptions[i]);
					
				// tries to obtain the current position
					StringAndDouble current_current_position = this.perceiveCurrentPosition(perceptions[i]);
					if (current_current_position != null && 
							(current_position == null || !current_current_position.STRING.equals(current_position.STRING))){
						current_position = current_current_position;
						pos_actualized = true;
					}
					else {
						// tries to obtain the neighborhood
						String[] current_neighbourhood = this.perceiveNeighbourhood(perceptions[i]);
						if (current_neighbourhood.length > 0)
							neighbourhood = current_neighbourhood;
					}
					
					if(this.time == 10 && !sent){
						try {
							this.sendMessage(this.AGENTS_PERCEIVED.getFirst());
						} catch (IOException e1) {
							// TODO Auto-generated catch block
							e1.printStackTrace();
						}
						sent = true;
					}
					
						if(pos_actualized && neighbourhood.length != 0 && time_actualized)
							break;
					
				}
			}
			
			// choosing the action of the agent
			if(current_position != null && current_position.double_value == 0) {
				// choosing the next node
				String next_node = current_position.STRING;
				while(next_node.equals(current_position.STRING)) {
					int index = (int) (Math.random() * neighbourhood.length);
					next_node = neighbourhood[index];
				}
				
				// sending the messages with the actions
				String message_1 = "<action type=\"2\"/>\n";
				try {
					this.connection.send(message_1);
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
				try {
					goTo(next_node);
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
		// stops the connection of the agent
		try {
			this.connection.stopWorking();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
