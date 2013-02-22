package strategies.grav;

import util.graph2.Graph;
import util.graph2.Node;
import agent_library.basic_agents.AgentStoppedException;
import agent_library.connections.ClientConnection;
import agent_library.coordinated_agents.CoordinatorAgent;


/**
 * Coordinator agent of the "Gravitational" strategies (Sampaio, Ramalho e Tedesco, 2010).
 * 
 * @author Pablo A. Sampaio
 */
public class GravCoordinatorAgent extends CoordinatorAgent {
	private Graph graph;  //this object is kept during the simulation to maintain the 
	                      //same nodes' indexes (because they may vary between perceptions)
	
	private GravityManager gravitationalForces;
	

	public GravCoordinatorAgent(String id, ClientConnection conn, GravityManager manager) {
		super(id, conn, true);
		this.gravitationalForces = manager;
	}

	@Override
	protected void onFirstGraphPerception() throws AgentStoppedException {
		this.graph = super.perceiveGraph2();
		gravitationalForces.setup(this.graph);
	}
	
	@Override
	protected void onNewGraphPerceptionAndNewRequests() throws AgentStoppedException {
		Graph newGraph = super.perceiveGraph2();
		//printDebug("New graph:" + newGraph);
		
		//updates the idlenesses of the nodes
		Node node;
		for (int nodeIndex = 0; nodeIndex < this.graph.getNumVertices(); nodeIndex++) {
			node = this.graph.getNode(nodeIndex);
			node.setIdleness( newGraph.getNode(node.getIdentifier()).getIdleness() );
		}
		
		gravitationalForces.update(this.graph);
	}

	@Override
	protected String selectGoalNode(String agentId, String agentCurrentNode) {
		return this.gravitationalForces.selectGoalNode(agentId, agentCurrentNode);
	}

}
