/* HeuristicConscientiousReactiveAgent.java */

/* The package of this class. */
package heuristic_conscientious_reactive;

/* Imported classes and/or interfaces. */
import java.io.IOException;
import java.util.LinkedList;
import common.Agent;

/**
 * Implements the heuristic conscientious reactive agents, as it is described in
 * the work of [ALMEIDA, 2003].
 */
public abstract class HeuristicConscientiousReactiveAgent extends Agent {
	/* Attributes. */
	/** Holds the weigth of the idlenesses in the decision of the agent. */
	private static final double IDLENESSES_WEIGHT = 0.5;

	/** Holds the weigth of the lengths in the decision of the agent. */
	private static final double LENGTHS_WEIGHT = 1 - IDLENESSES_WEIGHT;

	/** Memorizes the last position assumed by the agent. */
	private StringAndDouble last_position;

	/** Memorizes the last time this agent visited the vertexes. */
	private LinkedList<StringAndDouble> vertexes_idlenesses;

	/** Lets the agent count the time. */
	private int time_counting;

	/** Holds the biggest idleness ever found by the agent. */
	private int biggest_idleness;

	/** Holds the smallest idleness ever found by the agent. */
	private int smallest_idleness;

	/** Holds the biggest edge length ever perceived by the agent. */
	private double biggest_length;

	/** Holds the smallest edge length ever perceived by the agent. */
	private double smallest_length;

	/* Methods. */
	/** Constructor. */
	public HeuristicConscientiousReactiveAgent() {
		this.last_position = null;
		this.vertexes_idlenesses = new LinkedList<StringAndDouble>();
		this.time_counting = 0;
		this.biggest_idleness = -1;
		this.smallest_idleness = Integer.MAX_VALUE;
		this.biggest_length = -1;
		this.smallest_length = Double.MAX_VALUE;
	}

	/**
	 * Lets the agent parse its current position from the given perception.
	 * 
	 * @param perception
	 *            The current perception of the agent.
	 * @return The current position of the agent, as a pair "current vertex id -
	 *         elapsed length on the current edge".
	 */
	private StringAndDouble parseCurrentPosition(String perception) {
		// obtains the id of the current vertex
		int vertex_id_index = perception.indexOf("vertex_id=\"");
		perception = perception.substring(vertex_id_index + 11);
		String vertex_id = perception.substring(0, perception.indexOf("\""));

		// obtains the elapsed length on the current edge
		int elapsed_length_index = perception.indexOf("elapsed_length=\"");
		perception = perception.substring(elapsed_length_index + 16);
		double elapsed_length = Double.parseDouble(perception.substring(0,
				perception.indexOf("\"")));

		// returs the answer of the method
		return new StringAndDouble(vertex_id, elapsed_length);
	}

	/**
	 * Lets the agent parse the neighbourhood from the given perception.
	 * 
	 * @param current_vertex_id
	 *            The id of the current vertex of the agent.
	 * @param perception
	 *            The current perception of the agent.
	 * @return The ids of the vertexes in the neighbourhood, as well as the
	 *         length of the edges to reach each one of them.
	 */
	private StringAndDouble[] parseNeighbourhood(String current_vertex_id,
			String perception) {
		// holds the answer for the method
		LinkedList<StringAndDouble> vertexes_and_lengths = new LinkedList<StringAndDouble>();

		// while there are vertexes to be read
		int next_vertex_index = perception.indexOf("<vertex ");
		while (next_vertex_index > -1) {
			// obtains the id of the vertex
			int vertex_id_index = perception.indexOf("id=\"");
			perception = perception.substring(vertex_id_index + 4);
			String vertex_id = perception
					.substring(0, perception.indexOf("\""));

			// if the obtained vertex is not the current vertex of the agent
			if (!current_vertex_id.equals(vertex_id)) {
				// obtains the length to reach such vertex
				double length = Integer.MAX_VALUE;
				String perception_copy = perception.substring(0);

				// while there are edges to be read
				int edge_index = perception_copy.indexOf("<edge ");
				while (edge_index > -1) {
					// obtains the emitter id of the edge
					int emitter_id_index = perception_copy
							.indexOf("emitter_id=\"");
					perception_copy = perception_copy
							.substring(emitter_id_index + 12);
					String emitter_id = perception_copy.substring(0,
							perception_copy.indexOf("\""));

					// if the emitter id is the current vertex of the
					// obtained vertex
					if (emitter_id.equals(current_vertex_id)
							|| emitter_id.equals(vertex_id)) {
						// obtains the collector if of the edge
						int collector_id_index = perception_copy
								.indexOf("collector_id=\"");
						perception_copy = perception_copy
								.substring(collector_id_index + 14);
						String collector_id = perception_copy.substring(0,
								perception_copy.indexOf("\""));

						// if the collector id is the current vertex of the
						// obtained vertex
						if ((collector_id.equals(current_vertex_id) || collector_id
								.equals(vertex_id))) {
							// obtains the length of the edge, and sets it
							// if it's
							// smaller than the previoulsy held one
							int length_id_index = perception_copy
									.indexOf("length=\"");
							perception_copy = perception_copy
									.substring(length_id_index + 8);
							double obtained_length = Double
									.parseDouble(perception_copy.substring(0,
											perception_copy.indexOf("\"")));

							if (obtained_length < length)
								length = obtained_length;
						}
					}

					// reads next edge
					edge_index = perception_copy.indexOf("<edge ");
				}

				// adds the vertex id and the length to the answer for the
				// method
				vertexes_and_lengths
						.add(new StringAndDouble(vertex_id, length));

				// if the obtained vertex was not memorized by the agent yet
				// adds it to such memory
				boolean was_memorized = false;
				for (int i = 0; i < this.vertexes_idlenesses.size(); i++)
					if (this.vertexes_idlenesses.get(i).STRING
							.equals(vertex_id)) {
						was_memorized = true;
						break;
					}

				if (!was_memorized)
					this.vertexes_idlenesses.add(new StringAndDouble(vertex_id,
							-1));

				// atualizes the biggest and smallest lengths ever found
				if (length > this.biggest_length)
					this.biggest_length = length;
				if (length < this.smallest_length)
					this.smallest_length = length;
			}

			// reads next vertex id
			next_vertex_index = perception.indexOf("<vertex ");
		}

		// mounts and returns the answer for the method
		StringAndDouble[] answer = new StringAndDouble[vertexes_and_lengths
				.size()];
		for (int i = 0; i < answer.length; i++)
			answer[i] = vertexes_and_lengths.get(i);
		return answer;
	}

	/** Atualizes the biggest ans smallest idlenesses ever found by the agent. */
	private void atualizeIdlenessesBounds() {
		for (int i = 0; i < this.vertexes_idlenesses.size(); i++) {
			int idleness = this.time_counting
					- (int) this.vertexes_idlenesses.get(i).double_value;

			if (idleness < this.smallest_idleness)
				this.smallest_idleness = idleness;

			if (idleness > this.biggest_idleness)
				this.biggest_idleness = idleness;
		}
	}

	/**
	 * Calculates, for a given vertex, its weight in the decision of the next
	 * vertex.
	 * 
	 * @param vertex_id
	 *            The id of the vertex to be evaluated.
	 * @param length
	 *            The length of the edge to reach such vertex.
	 * @return The weight of the given vertex.
	 */
	private double calculateWeight(String vertex_id, double length) {
		// finds the visiting time of the given vertex in the memory of the
		// agent
		int visiting_time = -1;
		for (int i = 0; i < this.vertexes_idlenesses.size(); i++)
			if (this.vertexes_idlenesses.get(i).STRING.equals(vertex_id)) {
				visiting_time = (int) this.vertexes_idlenesses.get(i).double_value;
				break;
			}

		// calculates the idleness of such element
		int idleness = this.time_counting - visiting_time;

		// calculates the weight of such vertex
		// w = ((i - min_i) / (max_i - min_i)) * tx_i + (1 - tx_l) * ((l -
		// min_l) / (max_l - min_l))
		// [ALMEIDA, 2003]
		double idleness_part = 0;
		if (this.smallest_idleness != this.biggest_idleness)
			idleness_part = IDLENESSES_WEIGHT
					* ((idleness - this.smallest_idleness) * Math.pow(
							(this.biggest_idleness - this.smallest_idleness),
							-1));

		double length_part = 0;
		if (this.smallest_length != this.biggest_length)
			length_part = (1 - LENGTHS_WEIGHT)
					* ((length - this.smallest_length) * Math.pow(
							(this.biggest_length - this.smallest_length), -1));

		double weight = idleness_part + length_part;

		// returns the weight
		return weight;
	}

	/**
	 * Lets the agent think and decide its next vertex, based upon the given
	 * "neighbourhood" (vertexes and respective lengths to reach each one of
	 * them).
	 * 
	 * The id of the next chosen vertex is returned.
	 * 
	 * @param neighbourhood
	 *            The neighbourhood of where the agent is.
	 * @return The next vertex the agent must take, if needed.
	 */
	private String decideNextVertex(StringAndDouble[] neighbourhood) {
		// holds the biggest weight given to a vertex from the neighbourhood
		double biggest_weight = -1 * Double.MAX_VALUE;

		// holds the vertex with the biggest weight
		String chosen_vertex = null;

		// for each vertex from the neighbourhood, calculates its weight
		// and tries to set the biggest weight and chosen next vertex
		for (int i = 0; i < neighbourhood.length; i++) {
			String vertex_id = neighbourhood[i].STRING;
			double length = neighbourhood[i].double_value;

			if (this.calculateWeight(vertex_id, length) > biggest_weight)
				chosen_vertex = vertex_id;
		}

		// returns the chosen vertex
		return chosen_vertex;
	}

	/**
	 * Lets the agent visit the current vertex.
	 * 
	 * @param vertex_id
	 *            The id of the current vertex of the agent, to be visited.
	 * @throws IOException
	 */
	private void visitCurrentPosition(String vertex_id) throws IOException {
		// atualizes the memory of the agent
		for (int i = 0; i < this.vertexes_idlenesses.size(); i++) {
			StringAndDouble memorized_item = this.vertexes_idlenesses.get(i);

			if (memorized_item.STRING.equals(vertex_id)) {
				memorized_item.double_value = this.time_counting;
				break;
			}
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
		String message = "<action type=\"1\" vertex_id=\"" + next_vertex_id
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

			// the neighbourhood of where the agent is
			StringAndDouble[] neighbourhood = new StringAndDouble[0];

			// the perceptions the agent is having
			LinkedList<String> perceptions_list = new LinkedList<String>();

			// while the current position or neighbourhood are not valid
			while (current_position == null || neighbourhood.length == 0
					|| current_position.equals(this.last_position)) {
				// obtains the perceptions from the server
				String[] perceptions = this.connection.getBufferAndFlush();

				// adds the perceptions to the list of perceptions
				for (int i = 0; i < perceptions.length; i++)
					perceptions_list.add(perceptions[i]);

				// for each perception, tries to find the current position of
				// the agent,
				// as well as its neighbourhood
				for (int i = perceptions_list.size() - 1; i >= 0; i--) {
					String current_perception = perceptions_list.get(i);

					if (current_perception.indexOf("<perception type=\"4\"") > -1)
						current_position = this
								.parseCurrentPosition(current_perception);

					if (current_position != null
							&& current_perception
									.indexOf("<perception type=\"0\"") > -1)
						neighbourhood = this.parseNeighbourhood(
								current_position.STRING, current_perception);
				}
			}

			// memorizes the last position assumed by the agent
			this.last_position = current_position;

			// 2nd. atualizes the bounds of idlenesses
			this.atualizeIdlenessesBounds();

			// 3rd. lets the agent think
			// if the current position is on a vertex
			// (i.e. the elapsed length is 0), choose next vertex
			String next_vertex_id = null;
			if (current_position.double_value == 0)
				next_vertex_id = this.decideNextVertex(neighbourhood);
			// else: the agent is walking on an edge,
			// so it doesn't have to decide its next position

			// 4th. lets the agent act,
			// if the next vertex was decided
			if (next_vertex_id != null) {
				// 3.1. lets the agent visit the current vertex
				try {
					this.visitCurrentPosition(current_position.STRING);
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

	/** Compares a given StringAndDouble object to this one. */
	public boolean equals(StringAndDouble object) {
		if (object != null && object.STRING.equals(this.STRING)
				&& object.double_value == this.double_value)
			return true;

		return false;
	}
}
