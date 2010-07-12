/* ConscientiousReactiveAgent.java */

/* The package of this class. */
package conscientious_reactive;

/* Imported classes and/or interfaces. */
import java.io.IOException;
import java.util.LinkedList;
import util.Keyboard;
import util.net.TCPClientConnection;
import util.net.UDPClientConnection;
import common.Agent;

/**
 * Implements the conscientious reactive agents, as it is described in the work
 * of [MACHADO, 2002].
 */
public class ConscientiousReactiveAgent extends Agent {
	/* Attributes. */
	/** Memorizes the last time this agent visited the nodes. */
	private final LinkedList<StringAndDouble> NODEES_IDLENESSES;

	/** Lets the agent count the time. */
	private int time_counting;

	/* Methods. */
	/** Constructor. */
	public ConscientiousReactiveAgent() {
		this.NODEES_IDLENESSES = new LinkedList<StringAndDouble>();
		this.time_counting = 0;
	}

	/**
	 * Lets the agent perceive its current position.
	 * 
	 * @param perceptions
	 *            The current perceptions of the agent.
	 * @return The current position of the agent, as a pair "current node id -
	 *         elapsed length on the current edge".
	 */
	private StringAndDouble perceiveCurrentPosition(String[] perceptions) {
		// tries to obtain the most recent self perception of the agent
		int perceptions_count = perceptions.length;

		for (int i = perceptions_count - 1; i > -1; i--) {
			String current_perception = perceptions[i];

			if (current_perception.indexOf("<perception type=\"4\"") > -1) {
				// obtains the id of the current node
				int node_id_index = current_perception
						.indexOf("node_id=\"");
				current_perception = current_perception
						.substring(node_id_index + 11);
				String node_id = current_perception.substring(0,
						current_perception.indexOf("\""));

				// obtains the elapsed length on the current edge
				int elapsed_length_index = current_perception
						.indexOf("elapsed_length=\"");
				current_perception = current_perception
						.substring(elapsed_length_index + 16);
				double elapsed_length = Double.parseDouble(current_perception
						.substring(0, current_perception.indexOf("\"")));

				// returs the answer of the method
				return new StringAndDouble(node_id, elapsed_length);
			}
		}

		// default answer
		return null;
	}

	/**
	 * Lets the agent perceive the neighborhood.
	 * 
	 * @param perceptions
	 *            The current perceptions of the agent.
	 * @return The id of the nodes in the neighborhood.
	 */
	private String[] perceiveNeighbourhood(String[] perceptions) {
		// tries to obtain the most recent perception of the neighbourhood
		int perceptions_count = perceptions.length;

		for (int i = perceptions_count - 1; i > -1; i--) {
			String current_perception = perceptions[i];

			if (current_perception.indexOf("<perception type=\"0\"") > -1) {
				// holds the answer for the method
				LinkedList<String> nodes = new LinkedList<String>();

				// holds the index of the next node
				int next_node_index = current_perception.indexOf("<node ");

				// while there are nodes to be read
				while (next_node_index > -1) {
					// updates the current perception
					current_perception = current_perception
							.substring(next_node_index);

					// obtains the id of the current node
					int current_node_id_index = current_perception
							.indexOf("id=\"");
					current_perception = current_perception
							.substring(current_node_id_index + 4);
					String node_id = current_perception.substring(0,
							current_perception.indexOf("\""));

					// adds the id of the node to the list of obtained
					// nodes
					nodes.add(node_id);

					// obtains the index of the next node
					next_node_index = current_perception.indexOf("<node ");
				}

				// mounts and returns the answer of the method
				String[] answer = new String[nodes.size()];
				for (int j = 0; j < answer.length; j++)
					answer[j] = nodes.get(j);

				return answer;
			}
		}

		// default answer
		return new String[0];
	}

	/**
	 * Lets the agent think and decide its next node, based upon the given
	 * current position, and the idleness memorized by the agent.
	 * 
	 * The id of the next chosen node is returned.
	 * 
	 * @param current_position
	 *            The current position of the agent.
	 * @param neighbourhood
	 *            The neighborhood of where the agent is.
	 * @return The next node the agent must take.
	 */
	private String decideNextNode(StringAndDouble current_position,
			String[] neighbourhood) {
		// obtains the id of the current node where the agent is or comes from
		String current_node_id = current_position.STRING;

		// holds the node with the highest idleness
		// in the neighbourhood
		String next_node = null;

		// holds the smallest visiting time found by the agent
		double visiting_time = Double.MAX_VALUE;

		// for each node in the neighbourhood, finds the one
		// with the smallest visiting time, ignoring the current node
		// of the agent
		for (int i = 0; i < neighbourhood.length; i++) {
			String node_id = neighbourhood[i];

			// if the obtained node is not the current one
			if (!node_id.equals(current_node_id)) {
				// registers if the obtained node is in the memory of the
				// agent
				boolean is_memorized = false;

				// holds the visiting time of the obtained node
				double current_visiting_time = -1;

				// tries to find the visiting time of the obtained node
				// in the memory of the agent
				for (int j = 0; j < this.NODEES_IDLENESSES.size(); j++) {
					StringAndDouble memorized_item = this.NODEES_IDLENESSES
							.get(j);

					// if the memorized item is about the obtained node
					if (memorized_item.STRING.equals(node_id)) {
						// the node was found, so set its visit time
						current_visiting_time = memorized_item.double_value;

						// registers that the obtained node is already
						// memorized
						is_memorized = true;

						// quits the loop
						break;
					}
				}

				// if the obtained node is not memorized
				if (!is_memorized)
					// adds it to the memory of the agent
					this.NODEES_IDLENESSES.add(new StringAndDouble(node_id,
							current_visiting_time));

				// if the current visiting time is smaller than the oficial
				// visiting time
				if (current_visiting_time < visiting_time) {
					// updates the next node
					next_node = node_id;

					// updates the official visiting time
					visiting_time = current_visiting_time;
				}
			}
		}

		// returns the answer of the method
		return next_node;
	}

	/**
	 * Lets the agent visit the current node.
	 * 
	 * @param current_position
	 *            The current position of the agent, to be visited.
	 * @throws IOException
	 */
	private void visitCurrentPosition(StringAndDouble current_position)
			throws IOException {
		// updates the memory of the agent
		for (int i = 0; i < this.NODEES_IDLENESSES.size(); i++) {
			StringAndDouble memorized_item = this.NODEES_IDLENESSES.get(i);

			if (memorized_item.STRING.equals(current_position.STRING)) {
				memorized_item.double_value = this.time_counting;
				break;
			}
		}

		// send the order to the server to visit the current position
		String message = "<action type=\"2\"/>";
		this.connection.send(message);
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

		// while the agent is supposed to work
		while (!this.stop_working) {
			// 1st. lets the agent perceive...
			// the current position of the agent
			StringAndDouble current_position = null;

			// the neighborhood of where the agent is
			String[] neighbourhood = new String[0];

			// while the current position or neighborhood is not valid
			while (current_position == null || neighbourhood.length == 0) {
				// obtains the perceptions from the server
				String[] perceptions = this.connection.getBufferAndFlush();

				// tries to obtain the current position
				StringAndDouble current_current_position = this
						.perceiveCurrentPosition(perceptions);
				if (current_current_position != null)
					current_position = current_current_position;

				// tries to obtain the neighborhood
				String[] current_neighbourhood = this
						.perceiveNeighbourhood(perceptions);
				if (current_neighbourhood.length > 0)
					neighbourhood = current_neighbourhood;
			}

			// 2nd. lets the agent think
			// if the current position is on a node
			// (i.e. the elapsed length is 0), choose next node
			String next_node_id = null;
			if (current_position.double_value == 0)
				next_node_id = this.decideNextNode(current_position,
						neighbourhood);
			// else: the agent is walking on an edge,
			// so it doesn't have to decide its next position

			// 3rd. lets the agent act,
			// if the next node was decided
			if (next_node_id != null) {
				// 3.1. lets the agent visit the current node
				try {
					this.visitCurrentPosition(current_position);
				} catch (IOException e) {
					e.printStackTrace();
				}

				// 3.2. lets the agent go to the next node
				try {
					this.goTo(next_node_id);
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
	 * Turns this class into an executable one. Useful when running this agent
	 * in an individual machine.
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

	@Override
	public void update() {
		// while the agent is supposed to work
		if (!this.stop_working) {
			// 1st. lets the agent perceive...
			// the current position of the agent
			StringAndDouble current_position = null;

			// the neighborhood of where the agent is
			String[] neighbourhood = new String[0];

			// while the current position or neighborhood is not valid
			while (current_position == null || neighbourhood.length == 0) {
				// obtains the perceptions from the server
				String[] perceptions = this.connection.getBufferAndFlush();

				// tries to obtain the current position
				StringAndDouble current_current_position = this
						.perceiveCurrentPosition(perceptions);
				if (current_current_position != null)
					current_position = current_current_position;

				// tries to obtain the neighborhood
				String[] current_neighbourhood = this
						.perceiveNeighbourhood(perceptions);
				if (current_neighbourhood.length > 0)
					neighbourhood = current_neighbourhood;
			}

			// 2nd. lets the agent think
			// if the current position is on a node
			// (i.e. the elapsed length is 0), choose next node
			String next_node_id = null;
			if (current_position.double_value == 0)
				next_node_id = this.decideNextNode(current_position,
						neighbourhood);
			// else: the agent is walking on an edge,
			// so it doesn't have to decide its next position

			// 3rd. lets the agent act,
			// if the next node was decided
			if (next_node_id != null) {
				// 3.1. lets the agent visit the current node
				try {
					this.visitCurrentPosition(current_position);
				} catch (IOException e) {
					e.printStackTrace();
				}

				// 3.2. lets the agent go to the next node
				try {
					this.goTo(next_node_id);
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			// else: the agent is already walking, so do nothing

			// increments the agent's counting of time
			this.time_counting++;
		} else{

			// stops the connection of the agent
			try {
				this.connection.stopWorking();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
}

/** Internal class that holds together a string and a double value. */
final class StringAndDouble {
	/* Attributes */
	/** The string value. */
	public final String STRING;

	/** The double value. */
	public double double_value;

	/* Methods. */
	/**
	 * Constructor.
	 * 
	 * @param string
	 *            The string value of the pair.
	 * @param double_value
	 *            The double value of the pair.
	 */
	public StringAndDouble(String string, double double_value) {
		this.STRING = string;
		this.double_value = double_value;
	}
}