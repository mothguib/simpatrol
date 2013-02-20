package open.SingleCycle;

import java.io.IOException;
import java.util.LinkedList;

import javax.xml.parsers.ParserConfigurationException;

import org.xml.sax.SAXException;

import util.AgentPosition;
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
	
	LinkedList<AgentsWatcher> watchlist;
	
	
	/** Constructor. */
	public OpenCycledCoordinator() {
		this.PLAN = new LinkedList<String>();
		this.solution_length = 0;
		this.AGENTS_POSITIONS = new LinkedList<AgentPosition>();
		this.graph = null;
		this.oriented_agents = new LinkedList<String>();
		must_reorganize = new LinkedList<String>();
		watchlist = new LinkedList<AgentsWatcher>();
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

		for (int i = 0; i < 250; i++) {
			//System.out.println("i " + i);
			Node[] solution = graph.getTSPSolution();
			double solution_length = 0;
			
			// on ajoute la contrainte qu'une meme arete ne peut etre parcourue 2 fois dans le meme sens dans le plan (sinon 
			// il est quasi impossible de placer correctement les agents...)
			boolean found_double = false;
			for(int j = 0; j < solution.length; j++){
				String node_j = solution[j].getObjectId();
				String node_j2 = solution[(j+1) % solution.length].getObjectId();
				for(int k = j+1; k < solution.length; k++){
					if(solution[k].getObjectId().equals(node_j) && solution[(k+1) % solution.length].getObjectId().equals(node_j2))
						found_double = true;
					if(found_double)
						break;
				}
				if(found_double)
					break;
			}
			if(found_double)
				continue;
			

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
		
		if(tsp_solution != null){
			for (int i = 0; i < tsp_solution.length; i++) {
				if (i < tsp_solution.length - 1)
					this.PLAN.add(tsp_solution[i].getObjectId());
			}
			this.solution_length = best_solution_length;
			
			
			System.err.println("Solution length: " + this.solution_length);
	
			// returns the success of such perception
			return true;
		}
		
		else {
			System.out.println("Need one more TSP round");
			return perceiveTSPSolution();
		}
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
			System.err.println("Cycle " + this.time + ": Perceived " + this.AGENTS_POSITIONS.size() + " agents");
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
			for (int i = 0; i < this.AGENTS_POSITIONS.size(); i++)
				if (this.AGENTS_POSITIONS.get(i).node.equals(plan) 
						&& !sorted_agents.contains(this.AGENTS_POSITIONS.get(i).agent_name)){
					sorted_agents.add(this.AGENTS_POSITIONS.get(i).agent_name);
				}

			// if all the agents were sorted, quits the loop
			if (sorted_agents.size() >= this.AGENTS_POSITIONS.size())
				break;
		}
		
		if(sorted_agents.size() / 2 > 2){
			while(sorted_agents.get(0).equals(sorted_agents.get(1))){
				sorted_agents = new LinkedList<String>();
				this.PLAN.add(this.PLAN.pop());
				for (String plan : this.PLAN) {
					for (int i = 0; i < this.AGENTS_POSITIONS.size(); i++)
						if (this.AGENTS_POSITIONS.get(i).node.equals(plan) 
								&& !sorted_agents.contains(this.AGENTS_POSITIONS.get(i).agent_name)){
							sorted_agents.add(this.AGENTS_POSITIONS.get(i).agent_name);
						}
					
					if (sorted_agents.size() >= this.AGENTS_POSITIONS.size())
						break;
				}
			}
		}
		
		int k = 0;
		if(sorted_agents.size() > 2)
			while(this.AGENTS_POSITIONS.get(this.index_of_agent_in_position_list(sorted_agents.get(0))).node.equals(
					this.AGENTS_POSITIONS.get(this.index_of_agent_in_position_list(sorted_agents.get(1))).node) && k < sorted_agents.size()){
				sorted_agents.add(sorted_agents.remove());
				sorted_agents.add(sorted_agents.remove());
				k++;
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
				if(i==0 || (sorted_agents.size() == 2 && this.AGENTS_POSITIONS.get(this.index_of_agent_in_position_list(sorted_agents.get(0))).node.equals(
					this.AGENTS_POSITIONS.get(this.index_of_agent_in_position_list(sorted_agents.get(1))).node)))
					orientation.append(0 + ";1;");
				else
					orientation.append(1 + ";1;");
				// time to wait : 0 for the first, the let_pass *perceiveAgentsPositions distance after the first agent has passed
				if (i == 0)
					orientation.append(0);
				else
					orientation.append((int) ((double)let_pass * this.current_distance));
				
				// decrements the let pass value
				let_pass--;
				sent++;	
				oriented_agents.add(0, sorted_agents.get(i));
				
				double total_watch_time = this.real_distance_between_agents(sorted_agents.get(i), sorted_agents.get(0));
				watchlist.add(new AgentsWatcher(sorted_agents.get(i), sorted_agents.get(0), this.time + total_watch_time));
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

		// we reverse the agent list
		LinkedList<String> reverse_sorted_agents_new = new LinkedList<String>();
		for(int i = sorted_agents_new.size() - 1; i >=0; i--)
			reverse_sorted_agents_new.add(sorted_agents_new.get(i));
			
		StringBuffer orientation = new StringBuffer();
		int nb_of_agents_counted = 0;
			
		for (int i = 0; i <  reverse_sorted_agents_new.size(); i++) {
			if(sent == 0)
				orientation.append("#!#");
			
			orientation.append(reverse_sorted_agents_new.get(i) + ";"); // the 0 is here to get the same format as in reorganize_different_agents();
			
			// time to wait : wait_time * the numero of the agent + the time it took him to stop
			orientation.append(wait_time * nb_of_agents_counted + ";");
			
			// decrements the let pass value
			sent++;	
			nb_of_agents_counted++;
			
				
				
			if((sent >= NB_AGENTS_PER_MESS) || (i == reverse_sorted_agents_new.size() - 1)){
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
		
		LinkedList<String> sorted_agents = new LinkedList<String>();
		
		// on classe les agents par leur position dans le plan
		for (int k = 0; k < this.PLAN.size(); k++) {
			for (int i = 0; i < this.AGENTS_POSITIONS.size(); i++)
				if (this.AGENTS_POSITIONS.get(i).node.equals(this.PLAN.get(k)) && 
						(this.AGENTS_POSITIONS.get(i).elapsed_length == 0 || 
								(this.graph.getEdge(this.graph.getNode(this.PLAN.get(k)), 
										this.graph.getNode(this.PLAN.get((k+1) % this.PLAN.size()))) != null &&
								 this.AGENTS_POSITIONS.get(i).edge.equals(
											this.graph.getEdge(this.graph.getNode(this.PLAN.get(k)), 
																this.graph.getNode(this.PLAN.get((k+1) % this.PLAN.size()))).getObjectId())))
						&& !sorted_agents.contains(this.AGENTS_POSITIONS.get(i).agent_name)){
					sorted_agents.add(this.AGENTS_POSITIONS.get(i).agent_name);
				}

			// if all the agents were sorted, quits the loop
			if (sorted_agents.size() >= this.AGENTS_POSITIONS.size())
				break;
		}
		
		// on vire ceux qui ne sont pas encore insérés
		for(int i = sorted_agents.size() - 1; i >= 0; i--)
			if(this.oriented_agents.indexOf(sorted_agents.get(i)) == -1 && !sorted_agents.get(i).equals(agent_id))
				sorted_agents.remove(i);
		
		// on rafine le positionnement par la distance sur l'arete. Si 2 agents sont sur 1 noeud et que le 2e est l'agent a placer, on le met avant
		for(int i = 0; i < sorted_agents.size(); i++){
			AgentPosition a1 = this.AGENTS_POSITIONS.get(this.index_of_agent_in_position_list(sorted_agents.get(i)));
			AgentPosition a2 = this.AGENTS_POSITIONS.get(this.index_of_agent_in_position_list(sorted_agents.get((i+1) % sorted_agents.size())));
		
			if(a1.node.equals(a2.node) && (a1.elapsed_length > a2.elapsed_length || (a1.elapsed_length == a2.elapsed_length && a2.agent_name.equals(agent_id)))){
				sorted_agents.set(i, a2.agent_name);
				sorted_agents.set((i+1) % sorted_agents.size(), a1.agent_name);
			}			
		}
		
		
		
		// tenir compte de l'ordre FUTUR via les watchers
		LinkedList<AgentsWatcher> watchlist_copy = new LinkedList<AgentsWatcher>();
		for(int i = 0; i < this.watchlist.size(); i++)
			watchlist_copy.add(watchlist.get(i));
		while(watchlist_copy.size() > 0)
			for(int i = watchlist_copy.size() - 1; i >=0; i--){
				int index_waiting = sorted_agents.indexOf(watchlist_copy.get(i).agent_waiting);
				int index_towait = sorted_agents.indexOf(watchlist_copy.get(i).agent_to_wait);
				
				if((index_waiting  == (index_towait + 1) % sorted_agents.size())){
					String swap = sorted_agents.get(index_waiting);
					sorted_agents.set(index_waiting, sorted_agents.get(index_towait));
					sorted_agents.set(index_towait, swap);
					watchlist_copy.remove(i);
				}
				
				if((index_waiting  == (index_towait + 2) % sorted_agents.size()) && sorted_agents.get((index_towait + 1) % sorted_agents.size()).equals(agent_id))
					watchlist_copy.remove(i);
			}
		
		int agent_index = sorted_agents.indexOf(agent_id);
		if(agent_index == -1)
			return;
		
		int nb_agents_to_count = 1;
		int starting_index = (agent_index - Math.min(nb_agents_to_count, sorted_agents.size() - 1)) % (sorted_agents.size());
		
		// on verifie que l'agent inséré n'est pas sur un noeud double, ou que s'il l'est on a tenu compte du fait que le 1er agent a le 
		// croiser peut être en fait apres lui
		int nb_agents_to_count_for_insertion = nb_agents_to_count;
		String agent_pos = this.AGENTS_POSITIONS.get(this.index_of_agent_in_position_list(agent_id)).node;
		int nb_times_in_plan = 0;
		for(String plan : this.PLAN)
			if(plan.equals(agent_pos))
				nb_times_in_plan++;
		if(nb_times_in_plan > 1){
			double[] dist = new double[sorted_agents.size()];
			for(int i = 0; i < dist.length; i++)
				dist[i] = this.real_distance_to_node(sorted_agents.get(i), agent_pos);
			
			int[] used = new int[dist.length];
			used[agent_index] = 1;
			boolean done = true;
			for(int k = (agent_index - nb_agents_to_count + used.length) % used.length; k % used.length < agent_index; k++)
				done &= (used[k % used.length] == 1);
			
			while(!done){
				int ind_start = 0;
				for(ind_start = 0; ind_start < dist.length; ind_start++)
					if(used[ind_start] == 0)
						break;
				double dist_min = dist[ind_start];
				int ind_min = ind_start;
				for(int i = ind_start + 1; i < dist.length; i++)
					if(used[i] ==0 && dist[i] < dist_min){
						dist_min = dist[i];
						ind_min = i;
					}
				used[ind_min] = 1;
				if(ind_min != (agent_index - 1 + used.length) % used.length)
					nb_agents_to_count_for_insertion++;
				
				done = true;
				for(int k = (agent_index - nb_agents_to_count + used.length) % used.length; k % used.length < agent_index; k++)
					done &= (used[k % used.length] == 1);
			}
			
		}
		
		int nb_times_distance_to_wait = 1;
		int i = 1;
		int first_waited = (agent_index - 1 + sorted_agents.size()) % sorted_agents.size();
		while(this.is_waited(sorted_agents.get(first_waited)))
			first_waited = (first_waited - 1 + sorted_agents.size()) % sorted_agents.size();
		while(this.is_waiting(sorted_agents.get((agent_index + i) %  sorted_agents.size()))){
			boolean corresponding = false;
			for(AgentsWatcher watcher : this.watchlist)
				if(watcher.agent_waiting.equals(sorted_agents.get((agent_index + i) %  sorted_agents.size())))
					corresponding |= watcher.agent_to_wait.equals(sorted_agents.get(first_waited));
			if(corresponding){
				nb_times_distance_to_wait++;
				i++;
			}
			else
				break;
		}
		
		// holds the distance that must exist between two consecutive agents
		double old_distance = this.current_distance;
		double new_distance = Math.ceil(this.solution_length * Math.pow(sorted_agents.size(), -1));
		
		StringBuffer orientation = new StringBuffer();
		for (String solution_step : this.PLAN)
			orientation.append(solution_step + ",");
		orientation.deleteCharAt(orientation.lastIndexOf(","));
		orientation.append("###");
		orientation.append(agent_id + ";");
		orientation.append(Math.min(nb_agents_to_count_for_insertion, sorted_agents.size() - 1) + ";");
		orientation.append(nb_times_distance_to_wait + ";" + new_distance);
		this.SendMessage(orientation.toString(), agent_id);
		
		// ajouter 1 watcher par agent a attendre...
		for(int j = 0; j < nb_agents_to_count; j++){
			int index = (starting_index + j) % sorted_agents.size();
			if(index < 0)
				index += sorted_agents.size();
			double total_watch_time = this.real_distance_between_agents(sorted_agents.get(agent_index), sorted_agents.get(index))-1;
			watchlist.add(new AgentsWatcher(sorted_agents.get(agent_index), sorted_agents.get(index), this.time + total_watch_time));
		}
		
		// holds how many agents must pass the current one
		double wait_time = (old_distance - new_distance);
		
		orientation = new StringBuffer();
		int nb_of_agents_counted = 0;
		int sent = 0;
			
		for (int j = 0; j <  sorted_agents.size(); j++) {
			if(sent == 0)
				orientation.append("#!#");
			
			if(!sorted_agents.get((j + starting_index + sorted_agents.size()) % sorted_agents.size()).equals(agent_id)){
				orientation.append(sorted_agents.get((j + starting_index + sorted_agents.size()) % sorted_agents.size()) + ";"); // the 0 is here to get the same format as in reorganize_different_agents();
				
				// si l'agent attend de voir passer un autre agent, on le recale sur la nouvelle distance. sinon, on le decalle
				if(this.is_waiting(sorted_agents.get((j + starting_index + sorted_agents.size()) % sorted_agents.size()))){
					int decallage = 1;
					for(int k = (j + starting_index + sorted_agents.size()) % sorted_agents.size(); 
									(k % sorted_agents.size())<= (agent_index - nb_agents_to_count - 1 + sorted_agents.size()) % sorted_agents.size() ; k++){
						if(sorted_agents.get((k + nb_agents_to_count + 1) % sorted_agents.size()).equals(agent_id)){
							decallage++;
							break;
						}
						else if(!this.is_waiting(sorted_agents.get((k+1) % sorted_agents.size()))){
							decallage = 1;
							break;
						}
						else
							decallage++;
								
					}
					orientation.append(decallage * new_distance + ";");
				}
				else
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
			
			LinkedList<String> dependant_agents = new LinkedList<String>();
			for(int i = this.watchlist.size() - 1; i >= 0; i--)
				if(this.watchlist.get(i).agent_to_wait.equals(agent_id)){
					dependant_agents.add(this.watchlist.get(i).agent_waiting);
					this.watchlist.remove(i);
				}
			
			if(dependant_agents.size() == 0)
				this.reorganize_fewer_agents(agent_id);
			else 
				for(String agent : dependant_agents)
					this.must_reorganize.add(agent);
		}
		else if(perception.contains("ENTER")){
			int message_index = perception.indexOf("message=\"");
			perception = perception.substring(message_index + 9);
			String agent_id = perception.substring(0, perception.indexOf("#"));
			
			this.must_reorganize.add(agent_id);
		}
		
		
	}
	
	
	// agent 1 is supposed to be before agent 2 in the cycle
	private double real_distance_between_agents(String agent1, String agent2){
		if(agent1.equals(agent2))
			return 0;
		
		String node_1 = "", node_2 = "";
		int ind_1 = -1, ind_2 = -1;
		for(int i = 0; i < this.AGENTS_POSITIONS.size(); i ++){
			if(this.AGENTS_POSITIONS.get(i).agent_name.equals(agent1)){
				node_1 = this.AGENTS_POSITIONS.get(i).node;
				ind_1 = i;
			}
			if(this.AGENTS_POSITIONS.get(i).agent_name.equals(agent2)){
				node_2 = this.AGENTS_POSITIONS.get(i).node;
				ind_2 = i;
			}
		}
		
		if(node_1.equals("") || node_2.equals(""))
			return -1;
		
		double distance = 0;
		int starting_index2;
		for(starting_index2 = 0; starting_index2 < this.PLAN.size(); starting_index2++)
			if(this.PLAN.get(starting_index2).equals(node_2))
				break;
		
		for(int i = 1; i <= this.PLAN.size(); i++){
			String node1 = this.PLAN.get((starting_index2 + i - 1) % this.PLAN.size());
			String node2 = this.PLAN.get((starting_index2 + i) % this.PLAN.size());
			distance += this.graph.getDistance(this.graph.getNode(node1), this.graph.getNode(node2));
			if(this.PLAN.get((starting_index2 + i) % this.PLAN.size()).equals(node_1))
				break;
		}
		
		if(this.AGENTS_POSITIONS.get(ind_1).elapsed_length != 0)
			distance += this.AGENTS_POSITIONS.get(ind_1).elapsed_length;
		
		if(this.AGENTS_POSITIONS.get(ind_2).elapsed_length != 0)
			distance -= this.AGENTS_POSITIONS.get(ind_2).elapsed_length;
		
		return distance;
				
		
	}
	
	
	// distance de l'agent à la prochaine occurence du noeud dans le plan
	private double real_distance_to_node(String agent, String node){

		String node_1 = "";
		String edge = "";
		int ind_1 = -1;
		double elapsed_length = -1;
		for(int i = 0; i < this.AGENTS_POSITIONS.size(); i ++){
			if(this.AGENTS_POSITIONS.get(i).agent_name.equals(agent)){
				node_1 = this.AGENTS_POSITIONS.get(i).node;
				elapsed_length = this.AGENTS_POSITIONS.get(i).elapsed_length;
				edge = this.AGENTS_POSITIONS.get(i).edge;
				ind_1 = i;
			}
		}
		
		if(node_1.equals(""))
			return -1;
		
		if(node_1.equals(node) && elapsed_length == 0)
			return 0;
		
		double distance = 0;
		int starting_index = 0;
		for (int k = 0; k < this.PLAN.size(); k++) {
			if (node_1.equals(this.PLAN.get(k)) && 
						(elapsed_length == 0 || edge.equals(this.graph.getEdge(this.graph.getNode(this.PLAN.get(k)), 
																this.graph.getNode(this.PLAN.get((k+1) % this.PLAN.size()))).getObjectId()))){
				starting_index = k;
				break;
			}
		}
		
		for(int i = 1; i <= this.PLAN.size(); i++){
			String node1 = this.PLAN.get((starting_index + i - 1) % this.PLAN.size());
			String node2 = this.PLAN.get((starting_index + i) % this.PLAN.size());
			distance += this.graph.getDistance(this.graph.getNode(node1), this.graph.getNode(node2));
			if(this.PLAN.get((starting_index + i) % this.PLAN.size()).equals(node))
				break;
		}
		
		//if(elapsed_length != 0)
		//	distance += this.graph.getEdge(this.graph.getNode(node_1), this.graph.getNode(this.PLAN.get((ind_1 + 1) % this.PLAN.size()))).getLength() - elapsed_length;
		
		return distance;	
	}
	
	private boolean is_waiting(String agent){
		for(int i = 0; i < this.watchlist.size(); i++)
			if(this.watchlist.get(i).agent_waiting.equals(agent))
				return true;
		return false;
	}
	
	private boolean is_waited(String agent){
		for(int i = 0; i < this.watchlist.size(); i++)
			if(this.watchlist.get(i).agent_to_wait.equals(agent))
				return true;
		return false;
	}
	
	private int index_of_agent_in_position_list(String agent){
		for(int i = 0; i < this.AGENTS_POSITIONS.size(); i++)
			if(this.AGENTS_POSITIONS.get(i).agent_name.equals(agent))
				return i;
		return -1;
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
					if(oriented_agents.size() ==  this.AGENTS_POSITIONS.size()){
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
					for( int j = this.watchlist.size() - 1; j >= 0 ; j--)
						if(this.watchlist.get(j).timer_end < this.time){
							int index_waiting = this.oriented_agents.indexOf(this.watchlist.get(j).agent_waiting);
							int index_towait = this.oriented_agents.indexOf(this.watchlist.get(j).agent_to_wait);
							if(index_waiting != -1){
								String agent = this.oriented_agents.get(index_waiting);
								this.oriented_agents.set(index_waiting, this.oriented_agents.get(index_towait));
								this.oriented_agents.set(index_towait, agent);
							}
							this.watchlist.remove(j);	
						}
				
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
					
					if(sentgraph != this.graph && sentgraph != null)
						this.graph = sentgraph;
					
					if(this.must_reorganize.size() > 0){
						this.AGENTS_POSITIONS.clear();
						if (this.perceiveAgentsPositions(perceptions[i]) && this.graph != null){
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

final class AgentsWatcher {
	
	public String agent_waiting, agent_to_wait;
	public double timer_end;
	
	public AgentsWatcher(String agent_waiting, String agent_to_wait, double time){
		this.agent_waiting = agent_waiting;
		this.agent_to_wait = agent_to_wait;
		this.timer_end = time;
	}
}

