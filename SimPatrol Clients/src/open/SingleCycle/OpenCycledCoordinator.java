package open.SingleCycle;

import java.io.IOException;
import java.util.LinkedList;

import javax.xml.parsers.ParserConfigurationException;

import org.xml.sax.SAXException;

import util.graph.Edge;
import util.graph.Graph;
import util.graph.Node;

import common.OpenAgent;

public class OpenCycledCoordinator extends OpenAgent {
	/**
	 * Expresses the quality of the network (i.e. the number of times the
	 * coordinator must send orientations to the other agents, due to UDP packet
	 * loss).
	 */
	private final static int NB_AGENTS_PER_MESS = 5;
	
	
	/**
	 * The sequence of nodes to be visited by the agents. Actually a TSP
	 * solution for the graph of the environment.
	 */
	protected final LinkedList<String> PLAN;

	/** The length of the TSP solution found by this agent. */
	protected double solution_length;
	protected double current_distance;
	
	LinkedList<String> oriented_agents;
	
	LinkedList<String> must_reorganize;
	
	
	/** Constructor. */
	public OpenCycledCoordinator() {
		this.PLAN = new LinkedList<String>();
		this.solution_length = 0;
		this.AGENTS_POSITIONS = new LinkedList<String>();
		this.graph = null;
		this.oriented_agents = new LinkedList<String>();
		must_reorganize = new LinkedList<String>();
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
	protected boolean perceiveTSPSolution(){

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
	protected boolean perceiveAgentsPositions(String perception) {
		if(super.perceiveAgentsPositions(perception)){
			System.err.println("Cycle " + this.time + ": Perceived " + this.AGENTS_POSITIONS.size() / 2
					+ " agents");
			return true;
		}
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
		this.current_distance = Math.ceil(this.solution_length
				* Math.pow(sorted_agents.size(), -1));

		// holds how many agents must pass the current one
		int let_pass = sorted_agents.size() - 1 - oriented_agents.size();
		int sent = 0;
		StringBuffer orientation;
		
		while(oriented_agents.size() != sorted_agents.size()){
			sent = 0;
			orientation = new StringBuffer();
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
				// time to wait : 0 for the first, the let_pass *perceiveAgentsPositions distance after the first agent has passed
				if (i == 0)
					orientation.append(0);
				else
					orientation.append((int) ((double)let_pass * this.current_distance));
				
				// decrements the let pass value
				let_pass--;
				sent++;	
				oriented_agents.add(0, sorted_agents.get(i));
			}
				
			this.BroadcastMessage(orientation.toString());
		}
		System.err.println("Sent orientation.");

	}
	
	/**
	 * Lets the coordinator send new orientations to the agents.
	 * Since there are fewer agents, the new distance is bigger so the important thing
	 * is to know how much time each agent has to wait
	 * 
	 * @throws IOException
	 */
	private void reorganize_fewer_agents(String agent_id) throws IOException{

		int index_leaving = this.oriented_agents.indexOf(agent_id);

		LinkedList<String> sorted_agents_new = new LinkedList<String>();
		
		while(index_leaving + 1 < this.oriented_agents.size())
			sorted_agents_new.add(this.oriented_agents.remove(index_leaving + 1));
			
		
		for(int i = 0; i < index_leaving; i++)
			sorted_agents_new.add(this.oriented_agents.get(i));
			
		// holds the distance that must exist between two consecutive agents
		double old_distance = this.current_distance;
		double new_distance = Math.ceil(this.solution_length * Math.pow(sorted_agents_new.size(), -1));

		// holds how many agents must pass the current one
		int wait_time = (int) (new_distance - old_distance);
		int sent = 0;

			
		StringBuffer orientation = new StringBuffer();
		int nb_of_agents_counted = 0;
			
		for (int i = 0; i <  sorted_agents_new.size(); i++) {
			if(sent == 0)
				orientation.append("#!#");
			
			orientation.append(sorted_agents_new.get(i) + ";"); // the 0 is here to get the same format as in reorganize_different_agents();
			
			// time to wait : wait_time * the numero of the agent + the time it took him to stop
			orientation.append(wait_time * nb_of_agents_counted + ";");
			
			// decrements the let pass value
			sent++;	
			nb_of_agents_counted++;
			
				
				
			if((sent >= NB_AGENTS_PER_MESS) || (i == sorted_agents_new.size() - 1)){
				this.BroadcastMessage(orientation.toString());
				sent = 0;
				orientation = new StringBuffer();
			}
		}
		
		this.oriented_agents = sorted_agents_new;
		this.current_distance = new_distance;
		System.err.println("Sent re-orientation for fewer agents.");
		
	}
	
	
	private void reorganize_more_agents(){

		String agent_id = this.must_reorganize.poll();
		String agent_position = this.must_reorganize.poll();
		
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
		
		for(int i = sorted_agents.size() - 1; i >= 0; i--)
			if(this.oriented_agents.indexOf(sorted_agents.get(i)) == -1 && !sorted_agents.get(i).equals(agent_id))
				sorted_agents.remove(i);
		
		int agent_index = sorted_agents.indexOf(agent_id);
		if(agent_index == -1)
			return;
		
		int starting_index = (agent_index - Math.min(3, sorted_agents.size() - 1)) % (sorted_agents.size());
		int i = 1;
		while(this.AGENTS_POSITIONS.get(
					this.AGENTS_POSITIONS.indexOf(
							sorted_agents.get((agent_index + i)  % (sorted_agents.size()))) + 1)
									.equals(agent_position)){
			starting_index--;
			i++;
		}	
		starting_index = starting_index  % (sorted_agents.size());
		if(starting_index < 0)
			starting_index += sorted_agents.size();
		
		
		// holds the distance that must exist between two consecutive agents
		double old_distance = this.current_distance;
		double new_distance = Math.ceil(this.solution_length * Math.pow(sorted_agents.size(), -1));
		
		StringBuffer orientation = new StringBuffer();
		for (String solution_step : this.PLAN)
			orientation.append(solution_step + ",");
		orientation.deleteCharAt(orientation.lastIndexOf(","));
		orientation.append("###");
		orientation.append(agent_id + ";");
		orientation.append(Math.min(3, sorted_agents.size() - 1) + ";");
		orientation.append(new_distance);
		this.SendMessage(orientation.toString(), agent_id);
		
		// holds how many agents must pass the current one
		double wait_time = (old_distance - new_distance);
		
		orientation = new StringBuffer();
		int nb_of_agents_counted = 0;
		int sent = 0;
			
		for (int j = 0; j <  sorted_agents.size(); j++) {
			if(sent == 0)
				orientation.append("#!#");
			
			if(!sorted_agents.get((j + starting_index) % sorted_agents.size()).equals(agent_id)){
				orientation.append(sorted_agents.get((j + starting_index) % sorted_agents.size()) + ";"); // the 0 is here to get the same format as in reorganize_different_agents();
				
				// time to wait : wait_time * the numero of the agent + the time it took him to stop
				orientation.append((int) (wait_time * (double)(1 + nb_of_agents_counted)) + ";");
				
				// decrements the let pass value
				sent++;	
				nb_of_agents_counted++;
			}
				
				
			if((sent >= NB_AGENTS_PER_MESS) || (j == sorted_agents.size() - 1)){
				this.BroadcastMessage(orientation.toString());
				sent = 0;
				orientation = new StringBuffer();
			}
		}
		
		this.oriented_agents = sorted_agents;
		this.current_distance = new_distance;
		System.err.println("Sent re-orientation for more agents.");
		
	}
	
	private void manageMessage(String perception) throws IOException{
		if(perception.contains("QUIT")){
			int message_index = perception.indexOf("message=\"");
			perception = perception.substring(message_index + 9);
			String agent_id = perception.substring(0, perception.indexOf("#"));
			
			this.reorganize_fewer_agents(agent_id);
		}
		else if(perception.contains("ENTER")){
			int message_index = perception.indexOf("message=\"");
			perception = perception.substring(message_index + 9);
			String agent_id = perception.substring(0, perception.indexOf("#"));
			String agent_position = perception.substring(perception.indexOf(",") + 1, perception.indexOf("\""));
			
			this.must_reorganize.add(agent_id);
			this.must_reorganize.add(agent_position);
			//this.reorganize_more_agents(agent_id, agent_position);
		}
		
		
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
		
		// registers if the coordinator already sent orientation to the other
		// agents
		boolean sent_orientation = false;
		boolean updated_time = false;
		// registers if the agent perceived the tsp solution
		boolean perceived_tsp_solution = false;

		// registers if the agent perceived the other agents
		boolean perceived_other_agents = false;
			
		while (!this.stop_working) {
			updated_time = false;
		
			
			if(!sent_orientation){
				String[] perceptions = this.connection.getBufferAndFlush();
				for (int i = perceptions.length - 1; i >= 0; i--) {
				
					updated_time |= perceiveTime(perceptions[i]);
				
					Graph sentgraph = null;
					try {
						sentgraph = this.perceiveGraph(perceptions[i]);
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
					
					if(sentgraph != null && sentgraph != this.graph)
						this.graph = sentgraph;
				}
				
				if (!perceived_tsp_solution && !sent_orientation && this.graph != null)
					perceived_tsp_solution = this.perceiveTSPSolution();
			
	
				// if the other agents were not perceived yet, tries to perceive
				// them
				if (!perceived_other_agents)
					for (int i = 0; i < perceptions.length; i++) {
						perceived_other_agents = this.perceiveAgentsPositions(perceptions[i]);
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
					if(oriented_agents.size() ==  this.AGENTS_POSITIONS.size() / 2){
						sent_orientation = true;
					}
				}
				
				if(updated_time){
					this.Wait();
					updated_time = false;
				}
			}
			
			else {
				// obtains the current perceptions
				String[] perceptions = this.connection.getBufferAndFlush();
	
				// for each one, tries to obtain the currently perceived graph
				for (int i = perceptions.length - 1; i >= 0; i--) {
					
					updated_time |= perceiveTime(perceptions[i]);
				
					Graph sentgraph = null;
					try {
						sentgraph = this.perceiveGraph(perceptions[i]);
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
					
					if(sentgraph != this.graph)
						this.graph = sentgraph;
					
					if(this.must_reorganize.size() > 0){
						this.AGENTS_POSITIONS.clear();
						if (this.perceiveAgentsPositions(perceptions[i])){
								this.reorganize_more_agents();
						}
					}
				}
				
				for (int i = 0; i < perceptions.length; i++)
					if (perceptions[i].indexOf("<perception type=\"3\"") > -1)
						try {
							this.manageMessage(perceptions[i]);
						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
				
				if(updated_time){
					this.Wait();
					updated_time = false;
				}
			}

		}

	}

	@Override
	protected void deactivating_run() {
		// TODO Auto-generated method stub

	}

}
