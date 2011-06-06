/* CycledCoordinatorAgent.java */

/* The package of this class. */
package cycled;

/* Imported classes and/or interfaces. */
import java.io.IOException;
import java.util.LinkedList;

import org.xml.sax.SAXException;

import util.Keyboard;
import util.graph.Edge;
import util.graph.Graph;
import util.graph.GraphTranslator;
import util.graph.Node;
import util.net.TCPClientConnection;
import util.net.UDPClientConnection;

import common.Agent;

/**
 * Implements a coordinator that solves the TSP problem for a perceived graph,
 * in order to send the solution to the cycled agents in the environment, as
 * well synchronize them. Based in the work of [Chevaleyre, 2005].
 */
public class CycledCoordinatorAgent extends Agent {
	/* Attributes. */
	/**
	 * Expresses the quality of the network (i.e. the number of times the
	 * coordinator must send orientations to the other agents, due to UDP packet
	 * loss).
	 */
	private final static int NETWORK_QUALITY = 9;
	private final static int NB_AGENTS_PER_MESS = 5;

	/**
	 * The sequence of nodes to be visited by the agents. Actually a TSP
	 * solution for the graph of the environment.
	 */
	protected final LinkedList<String> PLAN;

	/** The length of the TSP solution found by this agent. */
	protected double solution_length;

	/**
	 * List that holds in the ith position the id of the agent, and in the
	 * (i+1)th position the id of the node where the agent is.
	 */
	private final LinkedList<String> AGENTS_POSITIONS;
	private int nbAgents = -1;

	/** Holds the graph currently perceived by this agent. */
	private Graph graph;
	
	// registers if the coordinator already sent orientation to the other
	// agents
	boolean sent_orientation = false;

	// registers if the agent perceived the tsp solution
	boolean perceived_tsp_solution = false;

	// registers if the agent perceived the other agents
	boolean perceived_other_agents = false;
	
	LinkedList<String> oriented_agents;
	
	/* Reorientation variables */
	// have a message telling the agents to stop been sent ?
	boolean stopped_agents = false;
	
	// are all agents on a node and not in between 2 nodes ?
	boolean agents_stabilized = false;
	
	// variables used to calculate precisely how much time it took to each agent to stop
	// this is useful to restart the cycle : each agent will wait the same time it took him to stop,
	// in order to recreate correctly the cycle.
	LinkedList<String> agents_name_time_to_stop;
	int[] agents_time_to_stop;
	int[] nb_turn_agents_on_node;
	int nbTurn_all_on_node;
	
	LinkedList<String> OLD_AGENTS_POSITIONS;
	
	// how much turn without any agent moving (ie all agents on nodes) before we can consider they all stopped ?
	// here : 1 they arrive, 1 they visit, 1 they move --> if no movement for 3 consecutive cycles, the agents stopped
	private final static int NB_TURN_TO_SYNCH = 3;

	
	// determines if we use the fast but less precise reorganize_fewer_agents and reorganize_different_agents
	// or the precise but slower reorganize_more_agents
	private final boolean use_precise_solution;
	
	
	private int current_cycle = 0;
	
	
	/* Methods. */
	/** Constructor. */
	public CycledCoordinatorAgent(boolean use_precise_solution) {
		this.PLAN = new LinkedList<String>();
		this.solution_length = 0;
		this.AGENTS_POSITIONS = new LinkedList<String>();
		this.graph = null;
		this.oriented_agents = new LinkedList<String>();
		this.use_precise_solution = use_precise_solution;
	}

	/**
	 * Lets the agent perceive the graph of the environment and calculate its
	 * TSP solution.
	 * 
	 * @param perception
	 *            The current perception of the agent.
	 * @return TRUE if the agent obtained a TSP solution, FALSE if not.
	 * @throws IOException
	 * @throws SAXException
	 */
	protected boolean perceiveTSPSolution(String perception)
			throws SAXException, IOException {
		// tries to obtain a graph from the perception
		Graph[] graph_perception = GraphTranslator.getGraphs(GraphTranslator
				.parseString(perception));

		// if a graph was perceived
		if (graph_perception.length > 0) {
			// obtains the graph
			this.graph = graph_perception[0];

			// obtains the TSP solution
			Node[] tsp_solution = null;
			double best_solution_length = Double.MAX_VALUE;

			for (int i = 0; i < 200; i++) {
				//System.out.println("i " + i);
				Node[] solution = graph.getTSPSolution();
				double solution_length = 0;

				for (int j = 1; j < solution.length; j++){
					Edge[] edges = graph.getDijkstraPath(solution[j - 1], solution[j]).getEdges();
					double length = 0;
					for(Edge edge : edges)
						length += Math.ceil(edge.getLength());
					solution_length += length;
				}
				
				//System.out.println("solution length " + solution_length);
				
				// we take into account the fact that the agents are taking 1 cycle to visit a node
				solution_length += solution.length - 1;

				if (solution_length < best_solution_length) {
					tsp_solution = solution;
					best_solution_length = solution_length;
				}
			}
			
			for (int i = 0; i < tsp_solution.length; i++) {
				if (i < tsp_solution.length - 1)
					this.PLAN.add(tsp_solution[i].getObjectId());
			}
			this.solution_length = best_solution_length;
			
			
			System.err.println("Solution length: " + this.solution_length);

			// returns the success of such perception
			return true;
		}

		// default answer
		return false;
	}

	/**
	 * Lets the coordinator perceive the position of the other agents.
	 * 
	 * IF its a reorganisation phase, it also counts how much time the agents took to stop
	 * and calculates if the agents are all stabilized on nodes 
	 * 
	 * @param perception
	 *            The current perception of the coordinator.
	 * @return TRUE if the coordinator perceived the other agents, FALSE if not.
	 */
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

				this.AGENTS_POSITIONS.add(agent_id);
				this.AGENTS_POSITIONS.add(agent_node_id);
				
				agent_id_index = perception.indexOf("<agent id=\"");
				
				if(stopped_agents){
					if(!agents_name_time_to_stop.contains(agent_id))
						agents_name_time_to_stop.add(agent_id);
					
					int index = (agent_id_index > -1)? Math.min(agent_id_index, perception.indexOf("</perception>")) : 
						perception.indexOf("</perception>");
					if((perception.indexOf("elapsed_length=\"") > -1) && (perception.indexOf("elapsed_length=\"") < index)){
						agents_time_to_stop[agents_name_time_to_stop.indexOf(agent_id)] += 1;
						if(nb_turn_agents_on_node[agents_name_time_to_stop.indexOf(agent_id)]!=0)
							agents_time_to_stop[agents_name_time_to_stop.indexOf(agent_id)] += nb_turn_agents_on_node[agents_name_time_to_stop.indexOf(agent_id)];
						nb_turn_agents_on_node[agents_name_time_to_stop.indexOf(agent_id)] = 0;
					}
					else
						nb_turn_agents_on_node[agents_name_time_to_stop.indexOf(agent_id)] += 1;
				}
			}
			
			if(stopped_agents){
				boolean all_on_nodes = true;
				for(int i = 0; i < nb_turn_agents_on_node.length; i++)
					all_on_nodes &= (nb_turn_agents_on_node[i] != 0);
				if(all_on_nodes)
					this.nbTurn_all_on_node += 1;
				
				this.agents_stabilized = (this.nbTurn_all_on_node > NB_TURN_TO_SYNCH);
			}

			System.err.println("Cycle " + this.current_cycle + ": Perceived " + this.AGENTS_POSITIONS.size() / 2
					+ " agents");

			// returns the success of the perception
			return true;
		}

		// default answer
		return false;
	}

	/**
	 * Lets the coordinator send orientations to the other agents.
	 * 
	 * @throws IOException
	 */
	private void sendOrientation() throws IOException {
		// sorts the agents based on their positions and the TSP calculated
		// solution
		LinkedList<String> sorted_agents = new LinkedList<String>();
		
		for (String plan : this.PLAN) {
			for (int i = 0; i < this.AGENTS_POSITIONS.size(); i = i + 2)
				if (this.AGENTS_POSITIONS.get(i + 1).equals(plan) 
						&& !sorted_agents.contains(this.AGENTS_POSITIONS.get(i))){
					sorted_agents.add(this.AGENTS_POSITIONS.get(i));
				}

			// if all the agents were sorted, quits the loop
			if (sorted_agents.size() >= this.AGENTS_POSITIONS.size() / 2)
				break;
		}
		
		if(sorted_agents.size() / 2 > 2){
			while(sorted_agents.get(0).equals(sorted_agents.get(1))){
				sorted_agents = new LinkedList<String>();
				this.PLAN.add(this.PLAN.pop());
				for (String plan : this.PLAN) {
					for (int i = 0; i < this.AGENTS_POSITIONS.size(); i = i + 2)
						if (this.AGENTS_POSITIONS.get(i + 1).equals(plan) 
								&& !sorted_agents.contains(this.AGENTS_POSITIONS.get(i))){
							sorted_agents.add(this.AGENTS_POSITIONS.get(i));
						}
					
					if (sorted_agents.size() >= this.AGENTS_POSITIONS.size() / 2)
						break;
				}
			}
		}

		// holds the distance that must exist between two consecutive agents
		double distance = Math.ceil(this.solution_length
				* Math.pow(sorted_agents.size(), -1));

		// holds how many agents must pass the current one
		int let_pass = sorted_agents.size() - 1 - oriented_agents.size();
		int sent = 0;

		StringBuffer orientation = new StringBuffer();
		for (String solution_step : this.PLAN)
			orientation.append(solution_step + ",");
		orientation.deleteCharAt(orientation.lastIndexOf(","));
		
		for (int i = sorted_agents.size() - 1; i >= 0; i--) {
			if(sent >= NB_AGENTS_PER_MESS)
				break;
			if(oriented_agents.contains(sorted_agents.get(i)))
				continue;
			
			orientation.append("###");
			orientation.append(sorted_agents.get(i) + ";");
			// let pass : 0 if it's the first, one for the others
			if(i==0)
				orientation.append(0 + ";");
			else
				orientation.append(1 + ";");
			// time to wait : 0 for the first, the let_pass * distance after the first agent has passed
			if (i == 0)
				orientation.append(0);
			else
				orientation.append(let_pass * distance);
			
			// decrements the let pass value
			let_pass--;
			sent++;	
			oriented_agents.add(sorted_agents.get(i));
		}
				
		this.connection.send("<action type=\"3\" message=\"" + orientation.toString() + "\"/>");
		try {
			sleep(5);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
			// if the simulation is a real time one, sends the message more 4
			// times
		if (this.connection instanceof UDPClientConnection)
			for (int j = 0; j < CycledCoordinatorAgent.NETWORK_QUALITY; j++) {
				/*try {
					this.sleep(5000);
				} catch (InterruptedException ie) {
					// do nothing
				}*/

				// sends a message with the orientation
				this.connection.send("<action type=\"3\" message=\""
						+ orientation.toString() + "\"/>");
			}
			
		System.err.println("Sent orientation.");

	}
	
	
	
	/**
	 * Sends a STOP message to all agents and starts the reorientation phase
	 * 
	 * @param agents_num
	 * @throws IOException
	 */
	private void sendStopMessage(int agents_num) throws IOException {
		if(!stopped_agents){
			this.connection.send("<action type=\"3\" message=\"#!#STOP\"/>");
			agents_time_to_stop = new int[agents_num];
			agents_name_time_to_stop = new LinkedList<String>();
			nb_turn_agents_on_node = new int[agents_num];
			this.nbTurn_all_on_node = 0;
			stopped_agents = true;
		}
		
	}
	
	/**
	 * Lets the coordinator send new orientations to the agents.
	 * Since there are fewer agents, the new distance is bigger so the important thing
	 * is to know how much time each agent has to wait
	 * 
	 * @throws IOException
	 */
	private boolean reorganize_fewer_agents2(String perception) throws IOException{
		if (perception.indexOf("<perception type=\"1\"") > -1){

			if(OLD_AGENTS_POSITIONS == null)
				OLD_AGENTS_POSITIONS = new LinkedList<String>();
			
			for(int i = 0; i < AGENTS_POSITIONS.size(); i+=2){
				if(!OLD_AGENTS_POSITIONS.contains(AGENTS_POSITIONS.get(i))){
					OLD_AGENTS_POSITIONS.add(AGENTS_POSITIONS.get(i));
					OLD_AGENTS_POSITIONS.add(AGENTS_POSITIONS.get(i+1));
				}
			}
			AGENTS_POSITIONS.clear();
			
			oriented_agents.clear();
			perceiveAgentsPositions(perception);
			if(!agents_stabilized)
				return false;
			
			LinkedList<String> OLD_AGENTS = new LinkedList<String>();
			for(int i = 0; i < OLD_AGENTS_POSITIONS.size(); i+=2)
				OLD_AGENTS.add(OLD_AGENTS_POSITIONS.get(i));
			
			
			// we identify the agents that left
			LinkedList<String> NEW_AGENTS = new LinkedList<String>();
			for(int i = 0; i < AGENTS_POSITIONS.size(); i+= 2)
				NEW_AGENTS.add(AGENTS_POSITIONS.get(i));
			
			for(int i = NEW_AGENTS.size() - 1; i > -1 ; i--){
				if(OLD_AGENTS.contains(NEW_AGENTS.get(i))){
					OLD_AGENTS.remove(NEW_AGENTS.get(i));
				}
			}
			
			// OLD_AGENTS now only contains the name of the agents that left
			// now we find the positions order
			LinkedList<String> sorted_agents_old = new LinkedList<String>();
			
			for (String plan : this.PLAN) {
				for (int i = 0; i < OLD_AGENTS_POSITIONS.size(); i = i + 2){
					if (OLD_AGENTS_POSITIONS.get(i + 1).equals(plan) 
							&& !sorted_agents_old.contains(OLD_AGENTS_POSITIONS.get(i))){
						sorted_agents_old.add(OLD_AGENTS_POSITIONS.get(i));
					}
				}

				// if all the agents were sorted, quits the loop
				if (sorted_agents_old.size() >= OLD_AGENTS_POSITIONS.size() / 2)
					break;
			}
			
			
			// we find the biggest number of consecutive agents still here
			// then rotate the list until they are first
			// that way, the first agent left is the one that must wait the less
			int[] is_out = new int[sorted_agents_old.size()];
			for(int i = 0; i < sorted_agents_old.size(); i++){
				if(!OLD_AGENTS.contains(sorted_agents_old.get(i)))
					is_out[i] = 1;
			}
			for(int i = is_out.length - 2; i >= 0; i--){
				if(is_out[i] > 0)
					is_out[i] += is_out[i+1];
			}
			if(is_out[0] > 0){
				int i = is_out.length - 1;
				while(is_out[i] > 0){
					is_out[i] += is_out[0];
					i--;
				}
			}
			
			int max_pos = 0;
			int max_nb = 0;
			for(int i = 0; i < is_out.length; i++){
				if(is_out[i] > max_nb){
					max_nb = is_out[i];
					max_pos = i;
				}
			}
			
			for(int i = 0; i < max_pos; i++)
				sorted_agents_old.add(sorted_agents_old.pop());
			
			
			// holds the distance that must exist between two consecutive agents
			double old_distance = Math.ceil(this.solution_length * Math.pow(nbAgents, -1));
			double new_distance = Math.ceil(this.solution_length
					* Math.pow(AGENTS_POSITIONS.size() / 2, -1));

			// holds how many agents must pass the current one
			int wait_time = (int) (new_distance - old_distance);
			int sent = 0;

			
			int min_wait = 0;
			if(agents_name_time_to_stop.size() > 0)
				min_wait = agents_time_to_stop[0];
			for(int i = 1; i < agents_name_time_to_stop.size(); i++)
				min_wait = Math.min(min_wait, agents_time_to_stop[i]);
			for(int i = 0; i < agents_name_time_to_stop.size(); i++)
				agents_time_to_stop[i] -= min_wait;
			
			StringBuffer orientation = new StringBuffer();
			int nb_of_agents_counted = 0;
			
			
			for (int i = 0; i <  sorted_agents_old.size(); i++) {
				if(sent == 0)
					orientation.append("#!#");
				if(oriented_agents.contains(sorted_agents_old.get(i)))
					continue;
				
				if(OLD_AGENTS.contains(sorted_agents_old.get(i))){
					nb_of_agents_counted--;
					sent++;
				} else {
					orientation.append(sorted_agents_old.get(i) + ";0;"); // the 0 is here to get the same format as in reorganize_different_agents();
					
					// time to wait : wait_time * the numero of the agent + the time it took him to stop
					orientation.append(wait_time * nb_of_agents_counted + agents_time_to_stop[agents_name_time_to_stop.indexOf(sorted_agents_old.get(i))] + ";");
					
					// decrements the let pass value
					sent++;	
					oriented_agents.add(sorted_agents_old.get(i));
					nb_of_agents_counted++;
				}
					
					
				if((sent >= NB_AGENTS_PER_MESS) || (oriented_agents.size() == sorted_agents_old.size())){
					this.connection.send("<action type=\"3\" message=\"" + orientation.toString() + "\"/>");
					try {
						sleep(5);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					
						// if the simulation is a real time one, sends the message more 4
						// times
					if (this.connection instanceof UDPClientConnection)
						for (int j = 0; j < CycledCoordinatorAgent.NETWORK_QUALITY; j++) {
							/*try {
								this.sleep(5000);
							} catch (InterruptedException ie) {
								// do nothing
							}*/

							// sends a message with the orientation
							this.connection.send("<action type=\"3\" message=\"" + orientation.toString() + "\"/>");
						}
					
					sent = 0;
					orientation = new StringBuffer();
				}
			}
				
			System.err.println("Sent re-orientation for fewer agents.");
			return true;
		}
		return false;
	}

	/**
	 * Lets the coordinator send new orientations to the agents.
	 * Since there are more agents, we stop each agent and restart them one by one
	 * 
	 * This is exactly sendOrientation, except with the sending of the message that is different
	 * 
	 * @throws IOException
	 */
	private void reorganize_more_agents() throws IOException{
		// sorts the agents based on their positions and the TSP calculated
		// solution
		LinkedList<String> sorted_agents = new LinkedList<String>();
		
		for (String plan : this.PLAN) {
			for (int i = 0; i < this.AGENTS_POSITIONS.size(); i = i + 2)
				if (this.AGENTS_POSITIONS.get(i + 1).equals(plan) 
						&& !sorted_agents.contains(this.AGENTS_POSITIONS.get(i))){
					sorted_agents.add(this.AGENTS_POSITIONS.get(i));
				}

			// if all the agents were sorted, quits the loop
			if (sorted_agents.size() >= this.AGENTS_POSITIONS.size() / 2)
				break;
		}
		
		if(sorted_agents.size() / 2 > 2){
			while(sorted_agents.get(0).equals(sorted_agents.get(1))){
				sorted_agents = new LinkedList<String>();
				this.PLAN.add(this.PLAN.pop());
				for (String plan : this.PLAN) {
					for (int i = 0; i < this.AGENTS_POSITIONS.size(); i = i + 2)
						if (this.AGENTS_POSITIONS.get(i + 1).equals(plan) 
								&& !sorted_agents.contains(this.AGENTS_POSITIONS.get(i))){
							sorted_agents.add(this.AGENTS_POSITIONS.get(i));
						}
					
					if (sorted_agents.size() >= this.AGENTS_POSITIONS.size() / 2)
						break;
				}
			}
		}

		// holds the distance that must exist between two consecutive agents
		double distance = Math.ceil(this.solution_length
				* Math.pow(sorted_agents.size(), -1));

		// holds how many agents must pass the current one
		int let_pass = sorted_agents.size() - 1 - oriented_agents.size();
		int sent = 0;

		StringBuffer orientation = new StringBuffer();
		
		for (int i = sorted_agents.size() - 1; i >= 0; i--) {
			if(sent == 0)
				orientation.append("#!#");
			if(oriented_agents.contains(sorted_agents.get(i)))
				continue;
			
			orientation.append(sorted_agents.get(i) + ";");
			// let pass : 0 if it's the first, one for the others
			if(i==0)
				orientation.append(0 + ";");
			else
				orientation.append(1 + ";");
			// time to wait : 0 for the first, the let_pass * distance after the first agent has passed
			if (i == 0)
				orientation.append(0 + ";");
			else
				// the last agent to be met by the first one is the agent that will wait the less
				// that way the system stabilize more quickly
				orientation.append((sorted_agents.size() - let_pass) * distance + ";");
			
			// decrements the let pass value
			let_pass--;
			sent++;	
			oriented_agents.add(sorted_agents.get(i));
			
			if((sent >= NB_AGENTS_PER_MESS) || (oriented_agents.size() == sorted_agents.size())){
				this.connection.send("<action type=\"3\" message=\"" + orientation.toString() + "\"/>");
				try {
					sleep(5);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
					// if the simulation is a real time one, sends the message more 4
					// times
				if (this.connection instanceof UDPClientConnection)
					for (int j = 0; j < CycledCoordinatorAgent.NETWORK_QUALITY; j++) {
						/*try {
							this.sleep(5000);
						} catch (InterruptedException ie) {
							// do nothing
						}*/

						// sends a message with the orientation
						this.connection.send("<action type=\"3\" message=\""
								+ orientation.toString() + "\"/>");
					}
				
				sent = 0;
				orientation = new StringBuffer();
			}
				
		}
				
		
			
		System.err.println("Sent re-orientation for more agents.");

	}
	
	
	/**
	 * Lets the coordinator send new orientations to the agents.
	 * Since there are more agents, we stop each agent and restart them one by one
	 * 
	 * This is exactly sendOrientation, except with the sending of the message that is different
	 * 
	 * @throws IOException
	 */
	private void reorganize_more_agents_2() throws IOException{
		// sorts the agents based on their positions and the TSP calculated
		// solution
		LinkedList<String> sorted_agents = new LinkedList<String>();
		
		for (String plan : this.PLAN) {
			for (int i = 0; i < this.AGENTS_POSITIONS.size(); i = i + 2)
				if (this.AGENTS_POSITIONS.get(i + 1).equals(plan) 
						&& !sorted_agents.contains(this.AGENTS_POSITIONS.get(i))){
					sorted_agents.add(this.AGENTS_POSITIONS.get(i));
				}

			// if all the agents were sorted, quits the loop
			if (sorted_agents.size() >= this.AGENTS_POSITIONS.size() / 2)
				break;
		}
		
		if(sorted_agents.size() / 2 > 2){
			while(sorted_agents.get(0).equals(sorted_agents.get(1))){
				sorted_agents = new LinkedList<String>();
				this.PLAN.add(this.PLAN.pop());
				for (String plan : this.PLAN) {
					for (int i = 0; i < this.AGENTS_POSITIONS.size(); i = i + 2)
						if (this.AGENTS_POSITIONS.get(i + 1).equals(plan) 
								&& !sorted_agents.contains(this.AGENTS_POSITIONS.get(i))){
							sorted_agents.add(this.AGENTS_POSITIONS.get(i));
						}
					
					if (sorted_agents.size() >= this.AGENTS_POSITIONS.size() / 2)
						break;
				}
			}
		}

		// holds the distance that must exist between two consecutive agents
		double distance = Math.ceil(this.solution_length
				* Math.pow(sorted_agents.size(), -1));

		// holds how many agents must pass the current one
		int let_pass = sorted_agents.size() - 1 - oriented_agents.size();
		int sent = 0;

		StringBuffer orientation = new StringBuffer();
		
		for (int i = sorted_agents.size() - 1; i >= 0; i--) {
			if(sent == 0)
				orientation.append("#!#");
			if(oriented_agents.contains(sorted_agents.get(i)))
				continue;
			
			orientation.append(sorted_agents.get(i) + ";");
			// let pass : 0 if it's the first, one for the others
			if(i==0)
				orientation.append(0 + ";");
			else
				orientation.append(1 + ";");
			// time to wait : 0 for the first, the let_pass * distance after the first agent has passed
			if (i == 0)
				orientation.append(0 + ";");
			else
				// the last agent to be met by the first one is the agent that will wait the most
				// inverse order from reorganize_more_agents
				orientation.append(let_pass * distance + ";");
			
			// decrements the let pass value
			let_pass--;
			sent++;	
			oriented_agents.add(sorted_agents.get(i));
			
			if((sent >= NB_AGENTS_PER_MESS) || (oriented_agents.size() == sorted_agents.size())){
				this.connection.send("<action type=\"3\" message=\"" + orientation.toString() + "\"/>");
				try {
					sleep(5);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
					// if the simulation is a real time one, sends the message more 4
					// times
				if (this.connection instanceof UDPClientConnection)
					for (int j = 0; j < CycledCoordinatorAgent.NETWORK_QUALITY; j++) {
						/*try {
							this.sleep(5000);
						} catch (InterruptedException ie) {
							// do nothing
						}*/

						// sends a message with the orientation
						this.connection.send("<action type=\"3\" message=\""
								+ orientation.toString() + "\"/>");
					}
				
				sent = 0;
				orientation = new StringBuffer();
			}
				
		}
				
		
			
		System.err.println("Sent re-orientation for more agents.");

	}
	
	
	/**
	 * Lets the coordinator send new orientations to the agents.
	 * Since there are the same number of agents, we find the gaps left by departing agents
	 * and fill them with new agents
	 * 
	 * Warning : this only recalculates the orientations if all the agents are on nodes
	 * => the new agents must enter on nodes, and not in between
	 * 
	 * @throws IOException
	 * @return TRUE if recalculated
	 * 			FALSE if some agents were between nodes
	 */
	
	/*
	 * NOT TESTED and INCOMPLETE : all the other agents have to wait the time they took to stop
	 * 
	 * should probably not be used, because of the difficulty of reorganise all the agents 
	 * (due to the fact that they didn't stop exactly at the same moment, so they should all
	 * have to wait a certain time according to how much time they took to stop)
	 */
	private boolean reorganize_different_agents(String perception) throws IOException {
		if (perception.indexOf("<perception type=\"1\"") > -1){
			if(OLD_AGENTS_POSITIONS == null)
				OLD_AGENTS_POSITIONS = new LinkedList<String>();
			
			for(int i = 0; i < AGENTS_POSITIONS.size(); i+=2){
				if(!OLD_AGENTS_POSITIONS.contains(AGENTS_POSITIONS.get(i))){
					OLD_AGENTS_POSITIONS.add(AGENTS_POSITIONS.get(i));
					OLD_AGENTS_POSITIONS.add(AGENTS_POSITIONS.get(i+1));
				}
			}
			AGENTS_POSITIONS.clear();
			
			oriented_agents.clear();
			perceiveAgentsPositions(perception);
			if(!agents_stabilized)
				return false;
			
			LinkedList<String> OLD_AGENTS = new LinkedList<String>();
			for(int i = 0; i < OLD_AGENTS_POSITIONS.size(); i+=2)
				OLD_AGENTS.add(OLD_AGENTS_POSITIONS.get(i));
			
			
			// we identify the agents that left, and those who entered
			LinkedList<String> NEW_AGENTS = new LinkedList<String>();
			for(int i = 0; i < AGENTS_POSITIONS.size(); i+= 2)
				NEW_AGENTS.add(AGENTS_POSITIONS.get(i));
			
			for(int i = OLD_AGENTS.size() - 2; i > -1 ; i-= 2){
				if(NEW_AGENTS.contains(OLD_AGENTS.get(i))){
					NEW_AGENTS.remove(i+1);
					NEW_AGENTS.remove(i);
					NEW_AGENTS.remove(i+1);
					NEW_AGENTS.remove(i);
				}
			}
			
			
			// now we find the positions to fill (old) and the positions to leave (new)
			LinkedList<String> sorted_agents_old = new LinkedList<String>();
			LinkedList<String> sorted_agents_new = new LinkedList<String>();
			
			for (String plan : this.PLAN) {
				for (int i = 0; i < this.AGENTS_POSITIONS.size(); i = i + 2){
					if (this.AGENTS_POSITIONS.get(i + 1).equals(plan) 
							&& !sorted_agents_old.contains(this.AGENTS_POSITIONS.get(i))){
						sorted_agents_old.add(this.AGENTS_POSITIONS.get(i));
					}
					if (this.AGENTS_POSITIONS.get(i + 1).equals(plan) 
							&& !sorted_agents_new.contains(this.AGENTS_POSITIONS.get(i))){
						sorted_agents_new.add(this.AGENTS_POSITIONS.get(i));
					}
				}

				// if all the agents were sorted, quits the loop
				if ((sorted_agents_old.size() >= this.AGENTS_POSITIONS.size() / 2)
						&& (sorted_agents_new.size() >= this.AGENTS_POSITIONS.size() / 2))
					break;
			}
			
			
			LinkedList<Integer> old_positions = new LinkedList<Integer>();
			LinkedList<Integer> new_positions = new LinkedList<Integer>();
			for(int i = 0; i < OLD_AGENTS.size(); i++)
				old_positions.add(sorted_agents_old.indexOf(OLD_AGENTS.get(i)));
			for(int i = 0; i < NEW_AGENTS.size(); i++)
				new_positions.add(sorted_agents_new.indexOf(NEW_AGENTS.get(i)));
			
			
			// we now have to calculate how long each new agent must wait to fill the gap
			double distance = Math.ceil(this.solution_length * Math.pow(this.AGENTS_POSITIONS.size() / 2, -1));
			LinkedList<Integer> nb_agents_to_let_pass = new LinkedList<Integer>();
			for(int i = new_positions.size() - 1; i > -1 ; i--){
				int nearest = Integer.MAX_VALUE;
				for(int j = 0; j < old_positions.size(); j++){
					int gap = old_positions.get(j) - new_positions.get(i);
					if(gap >= 0 && (gap < nearest - new_positions.get(i))){
						nearest = old_positions.get(j);
					}
				}
				if(nearest == Integer.MAX_VALUE)
					// if no old_position is greater than the one we look at, 
					// then the nearest position on the cycle is the smallest one 
					for(int j = 0; j < old_positions.size(); j++)
						nearest = Math.min(nearest, old_positions.get(j));
				
				
				nb_agents_to_let_pass.add(nearest - new_positions.get(i));
				new_positions.remove(i);
				old_positions.remove((Integer)nearest);
			}
			
			// finally, we send the messages
			for(int i = 0; i < OLD_AGENTS.size(); i++)
				oriented_agents.remove(OLD_AGENTS.get(i));
			
			
			StringBuffer orientation = new StringBuffer();
			int sent = 0;
			

			while(NEW_AGENTS.size() > 0) {
				if(oriented_agents.contains(NEW_AGENTS.get(0))){
					NEW_AGENTS.remove(0);
					nb_agents_to_let_pass.remove(0);
					continue;
				}
				
				if(sent == 0)
					orientation.append("#!#");
					
				orientation.append(NEW_AGENTS.get(0) + ";");
				// time to wait : 0 for the first, the let_pass * distance after the first agent has passed
				orientation.append(nb_agents_to_let_pass.get(0) + ";");
				orientation.append(distance + ";");
				
				// decrements the let pass value
				sent++;	
				oriented_agents.add(NEW_AGENTS.get(0));
				NEW_AGENTS.remove(0);
				nb_agents_to_let_pass.remove(0);
				
				if((sent >= NB_AGENTS_PER_MESS) || (oriented_agents.size() == OLD_AGENTS.size())){
					this.connection.send("<action type=\"3\" message=\"" + orientation.toString() + "\"/>");
					try {
						sleep(5);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					
						// if the simulation is a real time one, sends the message more 4
						// times
					if (this.connection instanceof UDPClientConnection)
						for (int j = 0; j < CycledCoordinatorAgent.NETWORK_QUALITY; j++) {
							/*try {
								this.sleep(5000);
							} catch (InterruptedException ie) {
								// do nothing
							}*/

							// sends a message with the orientation
							this.connection.send("<action type=\"3\" message=\"" + orientation.toString() + "\"/>");
						}
					
					sent = 0;
					orientation = new StringBuffer();
				}
				
			}
					
			System.out.println("Sent re-organization for different agents.");
			return true;
		}
		else
			return false;
	}
	
	
	
	
	public void run() {
		// starts its connection
		this.connection.start();

		// registers if the coordinator already sent orientation to the other
		// agents
		boolean sent_orientation = false;

		// registers if the agent perceived the tsp solution
		boolean perceived_tsp_solution = false;

		// registers if the agent perceived the other agents
		boolean perceived_other_agents = false;
		
		int cycle_num = -1;
		
		LinkedList<String> oriented_agents_OLD = new LinkedList<String>();

		while (!this.stop_working) {
			if (!sent_orientation) {
				// obtains the current perceptions
				String[] perceptions = this.connection.getBufferAndFlush();

				// if the tsp solution was not perceived yet, tries to perceive
				// it
				if (!perceived_tsp_solution)
					for (int i = 0; i < perceptions.length; i++) {
						try {
							perceived_tsp_solution = this.perceiveTSPSolution(perceptions[i]);
						} catch (SAXException e) {
							e.printStackTrace();
						} catch (IOException e) {
							e.printStackTrace();
						}

						if (perceived_tsp_solution)
							break;
					}

				// if the other agents were not perceived yet, tries to perceive
				// them
				if (!perceived_other_agents)
					for (int i = 0; i < perceptions.length; i++) {
						perceived_other_agents = this
								.perceiveAgentsPositions(perceptions[i]);

						if (perceived_other_agents){
							nbAgents = this.AGENTS_POSITIONS.size() / 2;
							break;
						}
					}

				// if the coordinator perceived everything
				if (perceived_tsp_solution && perceived_other_agents) {
					// sends a proper orientation
					try {
						this.sendOrientation();
					} catch (IOException e) {
						e.printStackTrace();
					}

					// registers such action
					if(oriented_agents.size() ==  this.AGENTS_POSITIONS.size() / 2){
						sent_orientation = true;
						// end of orientation turn
						try {
							this.connection.send("<action type=\"-1\"/>");
						} catch (IOException e) {
							e.printStackTrace();
						}
					}
				}
			}
			// else, lets the agent do nothing
			else {
				// obtains the current perceptions
				String[] perceptions = this.connection.getBufferAndFlush();

				// for each one, tries to obtain the currently perceived graph
				Graph[] current_graph = new Graph[0];
				for (int i = perceptions.length - 1; i >= 0; i--) {
					try {
						current_graph = GraphTranslator
								.getGraphs(GraphTranslator
										.parseString(perceptions[i]));
					} catch (SAXException e) {
						e.printStackTrace();
					} catch (IOException e) {
						e.printStackTrace();
					}
					
					// if obtained a graph
					if (current_graph.length > 0){
						int cycle_num_position = perceptions[i].indexOf("cycle=\"")+ 7;
						String blah = perceptions[i].substring(cycle_num_position);
						int bla2 = blah.indexOf("\"");
						cycle_num = Integer.valueOf(blah.substring(0, bla2));
						break;
					}
				}
				for (int i = 0; i < perceptions.length; i++) {
					if (perceptions[i].indexOf("<perception type=\"1\"") > -1) {
						if(oriented_agents_OLD.size() == 0)
							for(int j = 0; j < oriented_agents.size(); j++)
								oriented_agents_OLD.add(oriented_agents.get(j));
						int agent_num = 0;
						String perception_copy = perceptions[i];
						int next_agent_index = perception_copy.indexOf("<agent id=\"");
						boolean unknown_agent = false;
						while( next_agent_index > -1){
							agent_num++;
							perception_copy = perception_copy.substring(next_agent_index + 11);
							String agent_name = perception_copy.substring(0, perception_copy.indexOf("\""));
							if(!oriented_agents_OLD.contains(agent_name))
								unknown_agent = true;
							next_agent_index = perception_copy.indexOf("<agent id=\"");
						}
						
						if(agent_num != nbAgents || unknown_agent){
							if(agent_num == 1 && !unknown_agent){
								nbAgents = agent_num;
							} else {
								// the society have changed : we ask agents to wait at the next node for new instructions
								try {
									sendStopMessage(agent_num);
								} catch (IOException e1) {
									// TODO Auto-generated catch block
									e1.printStackTrace();
								}
								
								if(use_precise_solution){
									AGENTS_POSITIONS.clear();
									oriented_agents.clear();
									agents_stabilized = false;
									perceiveAgentsPositions(perceptions[i]);
									if(agents_stabilized){
										try {
											reorganize_more_agents();
											nbAgents = agent_num;
											stopped_agents = false;
											oriented_agents_OLD.clear();
										} catch (IOException e) {
											// TODO Auto-generated catch block
											e.printStackTrace();
										}
									}
									
								} else {
									if(agent_num < nbAgents && !unknown_agent){
										// there is fewer agents, and no new agents
										try {
											if(reorganize_fewer_agents2(perceptions[i])){
												nbAgents = agent_num;
												stopped_agents = false;
												oriented_agents_OLD.clear();
											}
										} catch (IOException e) {
											// TODO Auto-generated catch block
											e.printStackTrace();
										}
									}
									else if(agent_num == nbAgents){
										// same number of agents, but agents have changed -> 
										// we try to fill the gaps with new agents
										try {
											if(reorganize_different_agents(perceptions[i])){
												nbAgents = agent_num;
												stopped_agents = false;
												oriented_agents_OLD.clear();
											}
										} catch (IOException e) {
											// TODO Auto-generated catch block
											e.printStackTrace();
										}
									
									}
									else {
										// there are more agents, or fewer but with unknown one : we redistribute on the TSP cycle
										AGENTS_POSITIONS.clear();
										oriented_agents.clear();
										agents_stabilized = false;
										perceiveAgentsPositions(perceptions[i]);
										if(agents_stabilized){
											try {
												reorganize_more_agents();
												nbAgents = agent_num;
												stopped_agents = false;
												oriented_agents_OLD.clear();
											} catch (IOException e) {
												// TODO Auto-generated catch block
												e.printStackTrace();
											}
										}
									}
								}
							}
						}
					}
				}
				
				if(current_graph.length > 0 && cycle_num > this.current_cycle){
					// if the obtained graph is different from the current
					// one
					//if (!current_graph[0].equals(this.graph)) {
						// updates the current graph
					this.graph = current_graph[0];

					// lets the agent do nothing
					for(int i = this.current_cycle; i < cycle_num; i++){
						try {
							this.connection.send("<action type=\"-1\"/>");
						} catch (IOException e) {
							e.printStackTrace();
						}
					}
					
					this.current_cycle = cycle_num;
				
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


	public void update(){

		if(!this.stop_working) {
		if (!sent_orientation) {
			// obtains the current perceptions
			String[] perceptions = this.connection.getBufferAndFlush();

			// if the tsp solution was not perceived yet, tries to perceive
			// it
			if (!perceived_tsp_solution)
				for (int i = 0; i < perceptions.length; i++) {
					try {
						perceived_tsp_solution = this
								.perceiveTSPSolution(perceptions[i]);
					} catch (SAXException e) {
						e.printStackTrace();
					} catch (IOException e) {
						e.printStackTrace();
					}

					if (perceived_tsp_solution)
						break;
				}

			// if the other agents were not perceived yet, tries to perceive
			// them
			if (!perceived_other_agents)
				for (int i = 0; i < perceptions.length; i++) {
					perceived_other_agents = this
							.perceiveAgentsPositions(perceptions[i]);

					if (perceived_other_agents)
						break;
				}

			// if the coordinator perceived everything
			if (perceived_tsp_solution && perceived_other_agents) {
				// sends a proper orientation
				try {
					this.sendOrientation();
				} catch (IOException e) {
					e.printStackTrace();
				}

				// registers such action
				sent_orientation = true;
			}
		}
		// else, lets the agent do nothing
		else {
			// obtains the current perceptions
			String[] perceptions = this.connection.getBufferAndFlush();

			// for each one, tries to obtain the currently perceived graph
			Graph[] current_graph = new Graph[0];
			for (int i = 0; i < perceptions.length; i++) {
				try {
					current_graph = GraphTranslator
							.getGraphs(GraphTranslator
									.parseString(perceptions[i]));
				} catch (SAXException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				}

				// if obtained a graph
				if (current_graph.length > 0) {
					// if the obtained graph is different from the current
					// one
					if (!current_graph[0].equals(this.graph)) {
						// updates the current graph
						this.graph = current_graph[0];

						// lets the agent do nothing
						try {
							this.connection.send("<action type=\"-1\"/>");
						} catch (IOException e) {
							e.printStackTrace();
						}
					}

					// quits the loop
					break;
				}
			}
		}
	} else{
		// stops the connection of the agent
		try {
			this.connection.stopWorking();
		} catch (IOException e) {
			e.printStackTrace();
		}
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
	 *            is a real time one, "false" if not. index 3 : "true" if only using the precise 
	 *            reorientation method, "false" if using the fast ones 
	 */
	public static void main(String args[]) {
		try {
			String server_address = args[0];
			int server_socket_number = Integer.parseInt(args[1]);
			boolean is_real_time_simulation = Boolean.parseBoolean(args[2]);
			boolean use_precise_solution = Boolean.parseBoolean(args[3]);

			CycledCoordinatorAgent coordinator = new CycledCoordinatorAgent(use_precise_solution);
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