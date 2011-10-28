package open.HPCC;

import java.io.IOException;
import java.util.HashSet;
import java.util.LinkedList;

import javax.xml.parsers.ParserConfigurationException;

import org.xml.sax.SAXException;

import util.graph.Graph;
import util.graph.Node;
import util.heap.Comparable;
import util.heap.MinimumHeap;

import common.OpenAgent;

public class OpenHPCCCoordinator extends OpenAgent {

	public static boolean NO_COORD_HACK = true;
	
	/** The messages received by the coordinator. */
	private final LinkedList<String> RECEIVED_MESSAGES;
	/**
	 * List that holds in the ith position the id of the agent, and in the
	 * (i+1)th position the id of the vertex to be visited by such agent.
	 */
	private final LinkedList<String> AGENTS_GOALS;
	
	
	public OpenHPCCCoordinator() {
		super("coordinator");
		this.graph = null;
		this.RECEIVED_MESSAGES = new LinkedList<String>();
		this.AGENTS_GOALS = new LinkedList<String>();
	}
	
	/**
	 * Lets the coordinator do its job.
	 * 
	 * @throws IOException
	 */
	private void treatMessages() throws IOException {
		// while there are messages to be attended
		// and the coordinator perceived the graph
		if (this.RECEIVED_MESSAGES.size() > 0 && this.graph != null) {
			// holds the id of the agents already attended this time
			HashSet<String> attended_agents = new HashSet<String>();

			// for each message, attends it
			int message_number = this.RECEIVED_MESSAGES.size();
			for (int i = 0; i < message_number; i++) {
				// obtains the received message
				String message = this.RECEIVED_MESSAGES.remove();

				// obtains the id of the agent from the received message
				String agent_id = message.substring(0, message.indexOf("###"));

				if(message.indexOf("QUIT") > -1){
					int j = this.AGENTS_GOALS.indexOf(agent_id);
					this.AGENTS_GOALS.remove(j+1);
					this.AGENTS_GOALS.remove(j);
					
					attended_agents.add(agent_id);
				}
				
				
				// if such agent was not attended this time
				else if (!attended_agents.contains(agent_id)) {
					// obtains the id of the vertex that is the position of such
					// agent
					String reference_vertex_id = message.substring(message.indexOf("###") + 3);

					// creates a vertex with such id
					Node reference_vertex = new Node("");
					reference_vertex.setObjectId(reference_vertex_id);

					// configures the comparable objects
					ComparableNode.graph = this.graph;
					ComparableNode.reference_node = reference_vertex;

					// mounts a heap with the vertexes, based on their
					// idlenesses
					// and their distances to the reference position
					Node[] vertexes = this.graph.getNodes();
					ComparableNode[] comparable_vertexes = new ComparableNode[vertexes.length - 1];
					int comparable_vertexes_index = 0;
					for (int j = 0; j < vertexes.length; j++)
						if (!vertexes[j].equals(reference_vertex)) {
							comparable_vertexes[comparable_vertexes_index] = new ComparableNode(
									vertexes[j]);
							comparable_vertexes_index++;
						}

					MinimumHeap heap = new MinimumHeap(comparable_vertexes);
					String vertex_id = ((ComparableNode) heap
							.removeSmallest()).NODE.getObjectId();

					// chooses the vertex to be visited by such agent
					while (this.AGENTS_GOALS.contains(vertex_id)
							&& !heap.isEmpty())
						vertex_id = ((ComparableNode) heap.removeSmallest()).NODE
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
					this.SendMessage(agent_id + "###" + vertex_id, agent_id);

					// adds the id of the agent to the attended ones
					attended_agents.add(agent_id);
				}
			}
		}
		// else do nothing
		//else
			//this.connection.send("<action type=\"-1\"/>");
	}
	
	@Override
	protected void inactive_run() {
		// TODO Auto-generated method stub

	}

	@Override
	protected void activating_run() {
		// TODO Auto-generated method stub

	}

	@Override
	protected void active_run() {


		// lets the agent perceive
		boolean time_updated = false;
		boolean condition;
		if(OpenHPCCCoordinator.NO_COORD_HACK)
			condition = !time_updated;
		else 
			condition = !this.stop_working;
		
		while(condition){
			
			// obtains the perceptions from the connection
			String[] perceptions = this.connection.getBufferAndFlush();
	
			// for each perception, starting from the most recent one
			for (int i = perceptions.length - 1; i >= 0; i--) {
				
				// obtains the current perception
				String perception = perceptions[i];
				
				time_updated |= this.perceiveTime(perception);
	
				Graph perceived_graph = null;;
				try {
					perceived_graph = this.perceiveGraph(perception);
				} catch (ParserConfigurationException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (SAXException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				if(perceived_graph != null)
					this.graph = perceived_graph;
	
				// if failed to obtain a graph, tries to obtain a message
				int message_index = perception.indexOf("message=\"");
				if (message_index > -1) {
					perception = perception.substring(message_index + 9);
					String message = perception.substring(0, perception
							.indexOf("\""));
	
					this.RECEIVED_MESSAGES.add(message);
				}
			}
			
			// lets the agent act
			// if the perceptions changed
			if(!OpenHPCCCoordinator.NO_COORD_HACK)
				if (this.RECEIVED_MESSAGES.size()>0)
					try {
						this.treatMessages();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				
			if(OpenHPCCCoordinator.NO_COORD_HACK)
				condition = !time_updated;
			else 
				condition = !this.stop_working;
				
		}

		if(OpenHPCCCoordinator.NO_COORD_HACK)
			if (this.RECEIVED_MESSAGES.size()>0)
				try {
					this.treatMessages();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

		if(OpenHPCCCoordinator.NO_COORD_HACK)	
			this.Wait();

	}

	@Override
	protected void deactivating_run() {
		// TODO Auto-generated method stub

	}

}


/**
 * Internal class that extends a vertex, letting it be compared to another
 * vertex based not only on their idlenesses, but also on the distances between
 * each one of them and a given reference vertex.
 */
final class ComparableNode implements Comparable {
	/** The vertex. */
	public final Node NODE;

	/** The reference vertex, used to calculate the distances. */
	public static Node reference_node;

	/** The graph of the simulation. */
	public static Graph graph;

	/**
	 * Constructor.
	 * 
	 * @param vertex
	 *            The vertex to be compared.
	 */
	public ComparableNode(Node vertex) {
		this.NODE = vertex;
	}

	public boolean isSmallerThan(Comparable object) {
		if (object instanceof ComparableNode) {
			// obtains the other vertex to be compared
			Node other_vertex = ((ComparableNode) object).NODE;

			// obtains the bound idlenesses of the graph
			double[] bound_idlenesses = graph.getSmallestAndBiggestIdlenesses();

			// obtains the bound distances of the graph
			double[] bound_distances = graph.getSmallestAndBiggestDistances();

			// calculates the value of this vertex
			double this_norm_idleness = 0;
			if (bound_idlenesses[1] > bound_idlenesses[0])
				this_norm_idleness = (this.NODE.getIdleness() - bound_idlenesses[0])
						* Math.pow((bound_idlenesses[1] - bound_idlenesses[0]),
								-1);

			double this_norm_distance = 0;
			if (bound_distances[1] > bound_distances[0])
				this_norm_distance = (bound_distances[1] - graph.getDistance(
						this.NODE, reference_node))
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
						other_vertex, reference_node))
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
