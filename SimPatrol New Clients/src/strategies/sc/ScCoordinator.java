package strategies.sc;

import java.util.LinkedList;

import util.agents.AgentImage;
import util.agents.SocietyImage;
import util.graph.Graph;
import util.graph.Node;
import agent_library.basic_agents.AgentStoppedException;
import agent_library.basic_agents.ThreadAgent;
import agent_library.connections.ClientConnection;


/**
 * This is the coordinator agent of the "Single Cycle" strategy, which is based on
 * Chrystofides' 3/2-approximation algorithm for the TSP (Chevaleyre, 2004).
 */
public class ScCoordinator extends ThreadAgent {

	protected final LinkedList<String> PLAN;  // approximated TSP solution
	protected double solution_length;

	// holds, in the i-th position, the id of the agent, and in the
	// (i+1)-th position the id of the node where the agent is
	private final LinkedList<String> AGENTS_POSITIONS;

	
	public ScCoordinator(String agentId, ClientConnection connection) {
		super("coordinator", connection, false);
		this.PLAN = new LinkedList<String>();
		this.solution_length = 0;
		this.AGENTS_POSITIONS = new LinkedList<String>();
	}
	
	
	@Override
	public void run() {
		Graph graph;
		
		try {
			
			//1. perceives the graph
			graph = super.perceiveGraphBlocking(20000);
		
			//2. calculates TSP solution
			this.calculatesTspCycle(graph);
			
			//3. percebe os agentes
			this.perceiveAgentsPositions("TODO!!!");
			
			//4. envia a solução do TSP
			this.sendTspSolution();
			
		} catch (AgentStoppedException e) {
			e.printStackTrace();
			
		}
		
		printDebug("Finishing...");
	}

	private void calculatesTspCycle(Graph graph) {
		Node[] tsp_solution = graph.getTSPSolution();
		double minDistance;

		for (int i = 0; i < tsp_solution.length; i++) {
			if (i < tsp_solution.length - 1) {
				this.PLAN.add(tsp_solution[i].getObjectId());
			}

			if (i > 0) {
				//TODO: pre-calculate paths with Floyd-Warshall algorithm and use them here (to fill in the gaps)
				minDistance = graph.getDijkstraPath(tsp_solution[i - 1], tsp_solution[i]).getEdges()[0].getLength();
				this.solution_length = this.solution_length + Math.ceil(minDistance);
			}
		}

		printDebug("Solution length: " + this.solution_length);		
	}

	private void perceiveAgentsPositions(String perception) throws AgentStoppedException {
		SocietyImage society = super.perceiveSocietyBlocking(20000);
		
		for (AgentImage agent : society.agents) {
			this.AGENTS_POSITIONS.add(agent.id);
			this.AGENTS_POSITIONS.add(agent.node_id);
		}
		
		printDebug("Perceived " + this.AGENTS_POSITIONS.size() / 2 + " agents");
	}
	
	private void sendTspSolution() throws AgentStoppedException {
		// sorts the agents based on their positions and the TSP calculated solution
		LinkedList<String> sorted_agents = new LinkedList<String>();

		for (String solution_step : this.PLAN) {
			for (int i = 0; i < this.AGENTS_POSITIONS.size(); i = i + 2)
				if (this.AGENTS_POSITIONS.get(i + 1).equals(solution_step)){
					String currentSorted = this.AGENTS_POSITIONS.get(i);
					boolean alreadySorted = false;
					for(int k = 0; k < sorted_agents.size(); k++){
						if( sorted_agents.get(k).equals(currentSorted) ) {
							alreadySorted = true;
							break;
						}
					}
					if(!alreadySorted) sorted_agents.add(this.AGENTS_POSITIONS.get(i));
				}

			// if all the agents were sorted, quits the loop
			if (sorted_agents.size() == this.AGENTS_POSITIONS.size() / 2) {
				break;
			}
		}

		// holds the distance that must exist between two consecutive agents
		/*double distance = Math.ceil(this.solution_length
				* Math.pow(sorted_agents.size(), -1));*/
		
		double distance = Math.ceil(this.solution_length/sorted_agents.size());

		// holds how many agents must pass the current one
		int let_pass = sorted_agents.size() - 1;

		StringBuffer appendPlan = new StringBuffer();
		
		for (String solution_step : this.PLAN) {
			appendPlan.append(solution_step + ",");
		}
		
		// for each agent, in the reverse order
		for (int i = sorted_agents.size() - 1; i >= 0; i--) {
			// mounts an orientation message
			StringBuffer orientation = new StringBuffer();
			orientation.append(sorted_agents.get(i) + "###");
			orientation.append(appendPlan);
			/*for (String solution_step : this.PLAN)
				orientation.append(solution_step + ",");*/

			orientation.deleteCharAt(orientation.lastIndexOf(","));
			orientation.append("###");
			orientation.append(let_pass + ";");

			if (i == 0) {
				orientation.append(0);
			} else {
				orientation.append(distance);
			}

			let_pass--;

			// sends a message with the orientation
			super.actSendBroadcast(orientation.toString());

//			try {
//				Thread.sleep(110);
//			} catch (InterruptedException e) {
//				e.printStackTrace();
//			}
			
			printDebug("Sent orientation. " + orientation.toString());
		}
	}
	
	protected void printDebug(String message) {
		System.out.println(identifier.toUpperCase() + ": " + message);
	}
	
}
