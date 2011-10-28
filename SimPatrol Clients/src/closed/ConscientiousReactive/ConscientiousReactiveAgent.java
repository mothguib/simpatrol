/* ConscientiousReactiveAgent.java */

/* The package of this class. */
package closed.ConscientiousReactive;

/* Imported classes and/or interfaces. */
import java.io.IOException;
import java.util.LinkedList;

import util.Keyboard;
import util.StringAndDouble;
import util.net.TCPClientConnection;
import util.net.UDPClientConnection;

import common.Agent;

/**
 * Implements the conscientious reactive agents, as it is described in the work
 * of [MACHADO, 2002].
 */
public class ConscientiousReactiveAgent extends Agent {
	/* Attributes. */
	/** Memorizes the last time this agent visited the vertexes. */
	private LinkedList<StringAndDouble> vertexes_idlenesses;

	/** Lets the agent count the time. */
	private int time_counting;

	/* Methods. */
	/** Constructor. */
	public ConscientiousReactiveAgent() {
		this.vertexes_idlenesses = new LinkedList<StringAndDouble>();
		this.time_counting = 0;
	}

	/**
	 * Lets the agent perceive its current position.
	 * 
	 * @param perceptions
	 *            The current perceptions of the agent.
	 * @return The current position of the agent, as a pair "current vertex id -
	 *         elapsed length on the current edge".
	 */
	private StringAndDouble perceiveCurrentPosition(String perception) {
		// tries to obtain the most recent self perception of the agent

		if (perception.indexOf("<perception type=\"4\"") > -1) {
			// obtains the id of the current vertex
			int vertex_id_index = perception.indexOf("node_id=\"");
			perception = perception.substring(vertex_id_index + 9);
			String vertex_id = perception.substring(0, perception.indexOf("\""));

			// obtains the elapsed length on the current edge
			double elapsed_length = 0;
			int elapsed_length_index = perception.indexOf("elapsed_length=\"");
			if(elapsed_length_index != -1){
				perception = perception.substring(elapsed_length_index + 16);
				elapsed_length = Double.parseDouble(perception.substring(0, perception.indexOf("\"")));
			}
			// returns the answer of the method
			return new StringAndDouble(vertex_id, elapsed_length);
		}

		// default answer
		return null;
	}

	/**
	 * Lets the agent perceive the neighbourhood.
	 * 
	 * @param perceptions
	 *            The current perceptions of the agent.
	 * @return The id of the vertexes in the neighbourhood.
	 */
	private String[] perceiveNeighbourhood(String perception) {
		// tries to obtain the most recent perception of the neighbourhood
		if (perception.indexOf("<perception type=\"0\"") > -1) {
			// holds the answer for the method
			LinkedList<String> vertexes = new LinkedList<String>();

			// holds the index of the next vertex
			int next_vertex_index = perception.indexOf("<node ");

			// while there are vertexes to be read
			while (next_vertex_index > -1) {
				// updates the current perception
				perception = perception.substring(next_vertex_index);

				// obtains the id of the current vertex
				int current_vertex_id_index = perception.indexOf("id=\"");
				perception = perception.substring(current_vertex_id_index + 4);
				String vertex_id = perception.substring(0, perception.indexOf("\""));

				// adds the id of the vertex to the list of obtained
				// vertexes
				vertexes.add(vertex_id);

				// obtains the index of the next vertex
				next_vertex_index = perception.indexOf("<node ");
			}

			// mounts and returns the answer of the method
			String[] answer = new String[vertexes.size()];
			for (int j = 0; j < answer.length; j++)
				answer[j] = vertexes.get(j);

			return answer;
		}

		// default answer
		return new String[0];
	}

	/**
	 * Lets the agent think and decide its next vertex, based upon the given
	 * current position, and the idleness memorized by the agent.
	 * 
	 * The id of the next chosen vertex is returned.
	 * 
	 * @param current_position
	 *            The current position of the agent.
	 * @param neighbourhood
	 *            The neighbourhood of where the agent is.
	 * @return The next vertex the agent must take.
	 */
	private String decideNextVertex(StringAndDouble current_position,
			String[] neighbourhood) {
		// obtains the id of the current vertex where the agent is or comes from
		String current_vertex_id = current_position.STRING;

		// holds the vertex with the highest idleness
		// in the neighbourhood
		String next_vertex = null;

		// holds the smallest visiting time found by the agent
		double visiting_time = Integer.MAX_VALUE;

		// for each vertex in the neighbourhood, finds the one
		// with the smallest visiting time, ignoring the current vertex
		// of the agent
		for (int i = 0; i < neighbourhood.length; i++) {
			String vertex_id = neighbourhood[i];

			// if the obtained vertex is not the current one
			if (!vertex_id.equals(current_vertex_id)) {
				// registers if the obtained vertex is in the memory of the
				// agent
				boolean is_memorized = false;

				// holds the visiting time of the obtained vertex
				double current_visiting_time = -1;

				// tries to find the visiting time of the obtained vertex
				// in the memory of the agent
				for (int j = 0; j < this.vertexes_idlenesses.size(); j++) {
					StringAndDouble memorized_item = this.vertexes_idlenesses
							.get(j);

					// if the memorized item is about the obtained vertex
					if (memorized_item.STRING.equals(vertex_id)) {
						// the vertex was found, so set its visit time
						current_visiting_time = memorized_item.double_value;

						// registers that the obtained vertex is already
						// memorized
						is_memorized = true;

						// quits the loop
						break;
					}
				}

				// if the obtained vertex is not memorized
				if (!is_memorized)
					// adds it to the memory of the agent
					this.vertexes_idlenesses.add(new StringAndDouble(vertex_id,
							current_visiting_time));

				// if the current visiting time is smaller than the oficial
				// visiting time
				if (current_visiting_time < visiting_time) {
					// atualizes the next vertex
					next_vertex = vertex_id;

					// atualizes the oficial visiting time
					visiting_time = current_visiting_time;
				}
			}
		}

		// this chooses randomly between the smallest neighbours
		LinkedList<StringAndDouble> smallest = new LinkedList<StringAndDouble>();
		for (StringAndDouble node : vertexes_idlenesses) {
			if(!node.STRING.equals(current_position.STRING))
				for( String node_name : neighbourhood)
					if((node.STRING.equals(node_name)) && (node.double_value == visiting_time)){
						smallest.add(node);
						break;
					}
		}
		
		int rand = (int) (Math.random() * smallest.size());
		next_vertex = smallest.get(rand).STRING;
		
		// returns the answer of the method
		return next_vertex;
	}

	/**
	 * Lets the agent visit the current vertex.
	 * 
	 * @param current_position
	 *            The current position of the agent, to be visited.
	 * @throws IOException
	 */
	private void visitCurrentPosition(StringAndDouble current_position)
			throws IOException {
		// atualizes the memory of the agent
		boolean memorized = false;
		for (int i = 0; i < this.vertexes_idlenesses.size(); i++) {
			StringAndDouble memorized_item = this.vertexes_idlenesses.get(i);

			if (memorized_item.STRING.equals(current_position.STRING)) {
				memorized_item.double_value = this.time_counting;
				memorized = true;
				break;
			}
		}
		
		if(!memorized){
			vertexes_idlenesses.add(new StringAndDouble(current_position.STRING, this.time_counting));
		}

		// send the order to the server to visit the current position
		String message = "<action type=\"2\"/>";
		this.connection.send(message);
	}

	/**
	 * Lets the agent go to the given vertex.
	 * 
	 * @param The
	 *            next vertex to where the agent is supposed to go.
	 * @throws IOException
	 */
	private void goTo(String next_vertex_id) throws IOException {
		String message = "<action type=\"1\" node_id=\"" + next_vertex_id
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
			// 1st. lets the agent perceive...

			// the neighbourhood of where the agent is
			String[] neighbourhood = new String[0];
			boolean pos_actualized = false;
			
			// while the current position or neighbourhood are not valid
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
	
						// tries to obtain the neighbourhood
						String[] current_neighbourhood = this.perceiveNeighbourhood(perceptions[i]);
						if (current_neighbourhood.length > 0)
							neighbourhood = current_neighbourhood;
					}
					
					if(pos_actualized && neighbourhood.length != 0)
						break;
				}
			}

			// 2nd. lets the agent think
			// if the current position is on a vertex
			// (i.e. the elapsed length is 0), choose next vertex
			String next_vertex_id = null;
			if (current_position.double_value == 0)
				next_vertex_id = this.decideNextVertex(current_position,
						neighbourhood);
			// else: the agent is walking on an edge,
			// so it doesn't have to decide its next position

			// 3rd. lets the agent act,
			// if the next vertex was decided
			if (next_vertex_id != null) {
				// 3.1. lets the agent visit the current vertex
				try {
					this.visitCurrentPosition(current_position);
				} catch (IOException e) {
					e.printStackTrace();
				}

				// 3.2. lets the agent go to the next vertex
				try {
					this.goTo(next_vertex_id);
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			// else: the agent is already walking, so do nothing

			// increments the agent's counting of time
			this.time_counting++;
		}

		// stops the connection of the agent
		try {
			this.connection.stopWorking();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Turns this class into an executable one. Useful when running this agent in
	 * an individual machine.
	 * 
	 * @param args
	 *            Arguments: index 0: The IP address of the SimPatrol server.
	 *            index 1: The number of the socket that the server is supposed
	 *            to listen to this client. index 2: "true", if the simulation
	 *            is a real time one, "false" if not.
	 */
	public static void main(String args[]) {
		try {
			String server_address = args[0];
			int server_socket_number = Integer.parseInt(args[1]);
			boolean is_real_time_simulation = Boolean.parseBoolean(args[2]);

			ConscientiousReactiveAgent agent = new ConscientiousReactiveAgent();
			if (is_real_time_simulation)
				agent.setConnection(new UDPClientConnection(server_address,
						server_socket_number));
			else
				agent.setConnection(new TCPClientConnection(server_address,
						server_socket_number));

			agent.start();

			System.out.println("Press [t] key to terminate this agent.");
			String key = "";
			while (!key.equals("t"))
				key = Keyboard.readLine();

			agent.stopWorking();
		} catch (Exception e) {
			System.out
					.println("Usage \"java cognitive_coordinated.CognitiveCoordinatedAgent\n"
							+ "<IP address> <Remote socket number> <Is real time simulator? (true | false)>\"");
		}
	}
}
