package closed.RandomReactive;

import java.io.IOException;
import java.util.LinkedList;

import util.StringAndDouble;

import common.Agent;

public class RandomReactiveAgent extends Agent {

	
	protected
		int port_number;
		String server_address;
	
	
	public RandomReactiveAgent() {
		super();
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
	
	
	public void run() {
		// starts its connection
		this.connection.start();

		// the current position of the agent
		StringAndDouble current_position = null;
		
		// while the agent is supposed to work
		while (!this.stop_working) {

			boolean pos_actualized = false;
			// the neighborhood of where the agent is
			String[] neighbourhood = new String[0];

			// while the current position or neighborhood is not valid
			while (!pos_actualized || neighbourhood.length == 0) {
				// obtains the perceptions from the server
				String[] perceptions = this.connection.getBufferAndFlush();

				for(int i = perceptions.length - 1; i > -1; i--){
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
					
				if(pos_actualized && neighbourhood.length != 0)
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

