package strategies.cr;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import util.agents.AgentImage;
import util.graph2.Edge;
import util.graph2.Graph;
import util.graph2.Node;
import agent_library.basic_agents.AgentStoppedException;
import agent_library.basic_agents.ThreadAgent;
import agent_library.connections.ClientConnection;


/**
 * The agent of the "Conscientious Reactive" (CR) strategy (Machado et al.,2002).
 *
 * @author Pablo A. Sampaio
 */
public class CrAgent extends ThreadAgent {
	private String currentNode;
	private Map<String,Integer> nodeVisitTimes; //last time this agent visited the nodes
	
	
	public CrAgent(String id, ClientConnection connection) {
		super(id, connection, true);
		this.nodeVisitTimes = new HashMap<String,Integer>();
	}

	@Override
	public void run() {
		AgentImage selfInfo;
		List<String> neighbors;
		String nextNodeId;
		int turn = -1;
		
		try {
			
			while (!this.stopRequested) {
				selfInfo = super.perceiveSelfBlocking(20000);
				
				turn ++;

				if (selfInfo.elapsed_length == 0) {
					this.currentNode = selfInfo.node_id;
					printDebug("Position: " + this.currentNode);
					
					neighbors = this.perceiveNeighbors();
					printDebug("Neighborhood: " + neighbors);
					
					nextNodeId = this.decideNextVertex(neighbors);
					
					//turn = super.getCurrentTurn();
					printDebug("Turno: " + turn);
					
					super.actVisit();
					
					//super.waitNewTurn(turn+1);
					
					this.nodeVisitTimes.put(this.currentNode, turn+1); 
					printDebug("Visited " + this.currentNode + " in " + super.getCurrentTurn());
					
					super.actGoto(nextNodeId);
				}
				
			}
		
		} catch (AgentStoppedException e) {
			//ok...
			
		}
		
		printDebug("Finished!");
	}

	private List<String> perceiveNeighbors() throws AgentStoppedException {
		Graph graph = super.perceiveGraph2Blocking(20000);
		List<String> nodes = new LinkedList<String>();
		
//		for (int i = 0; i < graph.getNumVertices(); i++) {
//			nodes.add(graph.getNode(i).getIdentifier());
//		}
		
		List<Edge> edges = graph.getOutEdges( graph.getNode(this.currentNode) );
		Node n;
		
		for (Edge e : edges) {
			n = graph.getNode(e.getTargetIndex());
			nodes.add(n.getIdentifier());
		}
		
		return nodes;
	}

	private String decideNextVertex(List<String> neighbors) {
		String lastVisitedNode = null;
		int lastVisitedTime    = Integer.MAX_VALUE;
		
		int nodeVisitTime;
		
		for (String nodeId : neighbors) {
			if (!nodeId.equals(this.currentNode)) {
				nodeVisitTime = -1;
				
				if (nodeVisitTimes.containsKey(nodeId)) {
					nodeVisitTime = this.nodeVisitTimes.get(nodeId);					
				} else {
					//inserir com tempo -1 ?? acho que não precisa...					
				}
				
				if (nodeVisitTime < lastVisitedTime) {
					lastVisitedNode = nodeId;
					lastVisitedTime = nodeVisitTime;
				}
			}
		}
		
		printDebug("Next node: " + lastVisitedNode + ", visited in: " + lastVisitedTime);

		return lastVisitedNode;
	}

	protected void printDebug(String message) {
		if (identifier != null) {
			System.out.println(identifier.toUpperCase() + ": " + message);
		}
	}

}
