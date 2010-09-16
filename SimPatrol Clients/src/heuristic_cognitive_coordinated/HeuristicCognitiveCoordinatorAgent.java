/* HeuristicCognitiveCoordinatorAgent.java */

/* The package of this class. */
package heuristic_cognitive_coordinated;

/* Imported classes and/or interfaces. */
import java.io.IOException;
import java.util.HashSet;
import java.util.LinkedList;
import javax.xml.parsers.ParserConfigurationException;
import org.xml.sax.SAXException;
import util.Keyboard;
import util.graph.Graph;
import util.graph.GraphTranslator;
import util.graph.Vertex;
import util.heap.Comparable;
import util.heap.MinimumHeap;
import util.net.TCPClientConnection;
import util.net.UDPClientConnection;
import common.Agent;

/**
 * Implements a coordinator that decides, for each heuristic cognitive
 * coordinated agent contacting it, what is the next vertex to be visited, as it
 * is described in the work of [Almeida, 2003].
 */
public final class HeuristicCognitiveCoordinatorAgent extends Agent {
	/* Attributes. */
	/** The graph perceived by the coordinator. */
	private Graph graph;

	/** The messages received by the coordinator. */
	private final LinkedList<String> RECEIVED_MESSAGES;

	/**
	 * List that holds in the ith position the id of the agent, and in the
	 * (i+1)th position the id of the vertex to be visited by such agent.
	 */
	private final LinkedList<String> AGENTS_GOALS;

	/* Methods. */
	/** Constructor. */
	public HeuristicCognitiveCoordinatorAgent() {
		this.graph = null;
		this.RECEIVED_MESSAGES = new LinkedList<String>();
		this.AGENTS_GOALS = new LinkedList<String>();
	}

	/**
	 * Implements the perception process of the coordinator. Returns TRUE if the
	 * agent perceived something different, FALSE if not.
	 * 
	 * @return TRUE if the agent perceived something different, FALSE if not.
	 * @throws IOException
	 * @throws SAXException
	 * @throws ParserConfigurationException
	 */
	private boolean perceive() throws ParserConfigurationException,
			SAXException, IOException {
		// the answer for the method
		boolean answer = false;

		// obtains the perceptions from the connection
		String[] perceptions = this.connection.getBufferAndFlush();

		// for each perception, starting from the most recent one
		for (int i = perceptions.length - 1; i >= 0; i--) {
			// obtains the current perception
			String perception = perceptions[i];

			// tries to obtain a graph from the perception
			Graph[] graph_perception = GraphTranslator
					.getGraphs(GraphTranslator.parseString(perception));
			if (graph_perception.length > 0) {
				if (!graph_perception[0].equals(this.graph)) {
					this.graph = graph_perception[0];
					answer = true;
				}
			} else {
				// if failed to obtain a graph, tries to obtain a message
				int message_index = perception.indexOf("message=\"");
				if (message_index > -1) {
					perception = perception.substring(message_index + 9);
					String message = perception.substring(0, perception
							.indexOf("\""));

					this.RECEIVED_MESSAGES.add(message);

					// registers that the perceptions changed
					answer = true;
				}
			}
		}

		// returns the answer of the method
		return answer;
	}

	/**
	 * Lets the coordinator do its job.
	 * 
	 * @throws IOException
	 */
	private void act() throws IOException {
		// while there are messages to be attended
		// and the coordinator perceived the graph
		if (this.RECEIVED_MESSAGES.size() > 0 && this.graph != null) {
			// holds the id of the agents already attended this time
			HashSet<String> attended_agents = new HashSet<String>();

			// for each message, attends it
			for (int i = 0; i < this.RECEIVED_MESSAGES.size(); i++) {
				// obtains the received message
				String message = this.RECEIVED_MESSAGES.remove();

				// obtains the id of the agent from the received message
				String agent_id = message.substring(0, message.indexOf("###"));

				// if such agent was not attended this time
				if (!attended_agents.contains(agent_id)) {
					// obtains the id of the vertex that is the position of such
					// agent
					String reference_vertex_id = message.substring(message
							.indexOf("###") + 3);

					// creates a vertex with such id
					Vertex reference_vertex = new Vertex("");
					reference_vertex.setObjectId(reference_vertex_id);

					// configures the comparable objects
					ComparableVertex.graph = this.graph;
					ComparableVertex.reference_vertex = reference_vertex;

					// mounts a heap with the vertexes, based on their
					// idlenesses
					// and their distances to the reference position
					Vertex[] vertexes = this.graph.getVertexes();
					ComparableVertex[] comparable_vertexes = new ComparableVertex[vertexes.length - 1];
					int comparable_vertexes_index = 0;
					for (int j = 0; j < vertexes.length; j++)
						if (!vertexes[j].equals(reference_vertex)) {
							comparable_vertexes[comparable_vertexes_index] = new ComparableVertex(
									vertexes[j]);
							comparable_vertexes_index++;
						}

					MinimumHeap heap = new MinimumHeap(comparable_vertexes);
					String vertex_id = ((ComparableVertex) heap
							.removeSmallest()).VERTEX.getObjectId();

					// chooses the vertex to be visited by such agent
					while (this.AGENTS_GOALS.contains(vertex_id)
							&& !heap.isEmpty())
						vertex_id = ((ComparableVertex) heap.removeSmallest()).VERTEX
								.getObjectId();

					// updates the agents and vertexes memory
					int agent_index = this.AGENTS_GOALS.indexOf(agent_id);
					if (agent_index > -1)
						this.AGENTS_GOALS.set(agent_index + 1, vertex_id);
					else {
						this.AGENTS_GOALS.add(agent_id);
						this.AGENTS_GOALS.add(vertex_id);
					}

					// sends a message containig the chosen vertex
					String action = "<action type=\"3\" message=\"" + agent_id
							+ "###" + vertex_id + "\"/>";
					this.connection.send(action);

					// adds the id of the agent to the attended ones
					attended_agents.add(agent_id);
				}
			}
		}
		// else do nothing
		else
			this.connection.send("<action type=\"-1\"/>");
	}

	public void run() {
		// starts its connection
		this.connection.start();

		while (!this.stop_working) {
			// registers if the perceptions of the agent changed
			boolean changed_perception = false;

			// lets the agent perceive
			try {
				changed_perception = this.perceive();
			} catch (ParserConfigurationException e) {
				e.printStackTrace();
			} catch (SAXException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}

			// lets the agent act
			// if the perceptions changed
			if (changed_perception)
				try {
					this.act();
				} catch (IOException e) {
					e.printStackTrace();
				}
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

			HeuristicCognitiveCoordinatorAgent coordinator = new HeuristicCognitiveCoordinatorAgent();
			if (is_real_time_simulation)
				coordinator.setConnection(new UDPClientConnection(
						server_address, server_socket_number));
			else
				coordinator.setConnection(new TCPClientConnection(
						server_address, server_socket_number));

			coordinator.start();

			System.out.println("Press [t] key to terminate this agent.");
			String key = "";
			while (!key.equals("t"))
				key = Keyboard.readLine();

			coordinator.stopWorking();
		} catch (Exception e) {
			System.out
					.println("Usage \"java heuristic_cognitive_coordinated.HeuristicCognitiveCoordinatorAgent\n"
							+ "<IP address> <Remote socket number> <Is real time simulator? (true | false)>\"");
		}
	}
}

/**
 * Internal class that extends a vertex, letting it be compared to another
 * vertex based not only on their idlenesses, but also on the distances between
 * each one of them and a given reference vertex.
 */
final class ComparableVertex implements Comparable {
	/** The vertex. */
	public final Vertex VERTEX;

	/** The reference vertex, used to calculate the distances. */
	public static Vertex reference_vertex;

	/** The graph of the simulation. */
	public static Graph graph;

	/**
	 * Constructor.
	 * 
	 * @param vertex
	 *            The vertex to be compared.
	 */
	public ComparableVertex(Vertex vertex) {
		this.VERTEX = vertex;
	}

	public boolean isSmallerThan(Comparable object) {
		if (object instanceof ComparableVertex) {
			// obtains the other vertex to be compared
			Vertex other_vertex = ((ComparableVertex) object).VERTEX;

			// obtains the bound idlenesses of the graph
			double[] bound_idlenesses = graph.getSmallestAndBiggestIdlenesses();

			// obtains the bound distances of the graph
			double[] bound_distances = graph.getSmallestAndBiggestDistances();

			// calculates the value of this vertex
			double this_norm_idleness = 0;
			if (bound_idlenesses[1] > bound_idlenesses[0])
				this_norm_idleness = (this.VERTEX.getIdleness() - bound_idlenesses[0])
						* Math.pow((bound_idlenesses[1] - bound_idlenesses[0]),
								-1);

			double this_norm_distance = 0;
			if (bound_distances[1] > bound_distances[0])
				this_norm_distance = (bound_distances[1] - graph.getDistance(
						this.VERTEX, reference_vertex))
						* Math.pow((bound_distances[1] - bound_distances[0]),
								-1);

			double this_value = Graph.getIdlenessesWeight()
					* this_norm_idleness + (1 - Graph.getIdlenessesWeight())
					* this_norm_distance;

			// calculates the value of the other vertex
			double other_norm_idleness = 0;
			if (bound_idlenesses[1] > bound_idlenesses[0])
				other_norm_idleness = (other_vertex.getIdleness() - bound_idlenesses[0])
						* Math.pow((bound_idlenesses[1] - bound_idlenesses[0]),
								-1);

			double other_norm_distance = 0;
			if (bound_distances[1] > bound_distances[0])
				other_norm_distance = (bound_distances[1] - graph.getDistance(
						other_vertex, reference_vertex))
						* Math.pow((bound_distances[1] - bound_distances[0]),
								-1);

			double other_value = Graph.getIdlenessesWeight()
					* other_norm_idleness + (1 - Graph.getIdlenessesWeight())
					* other_norm_distance;

			/*
			 * Specially here, if a vertex has greater idleness than another
			 * one, then it is smaller than the another one (so we can use a
			 * minimum heap).
			 */
			if (this_value > other_value)
				return true;
		}

		return false;
	}
}