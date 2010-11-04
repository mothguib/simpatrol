package gravitational;

import gravitational.gravity_manager.GravityManager;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

import util.graph2.Graph;
import util.graph2.GraphTranslator;
import dummy_client.TcpConnection;


public class GravitationalCoordinatorAgent implements Runnable {
	protected String identifier;
	protected TcpConnection connection;
	protected boolean working;
	
	private MassGrowth massGrowth;
	private double distanceExponent;
	private GravitiesCombinator gravityCombinator;
	
	private Graph graph;
	private GravityManager gravityManager;

	private List<String>[] visitsScheduledPerNode; //for each node, a list of agents' identifiers


	public GravitationalCoordinatorAgent(String agentId, TcpConnection tcpConnection,
			MassGrowth growthType, double distExponent, GravitiesCombinator combinator) {
		this.identifier = agentId;
		this.connection = tcpConnection;		
		this.massGrowth = growthType;
		this.distanceExponent = distExponent;
		this.gravityCombinator = combinator;
	}

	public GravitationalCoordinatorAgent(String agentId, TcpConnection tcpConnection) {
		this(agentId, tcpConnection, MassGrowth.ARITHMETIC, 2.0d, GravitiesCombinator.SUM); //standard model
	}

	
	public void startWorking() {
		connection.start();	
		working = true;
		
		Thread thread = new Thread(this);
		thread.start();
	}	

	public void stopWorking() {
		working = false;
		connection.stopWorking();
	}
	
	public void run() {		
		
		try {
			 
			waitForGraph();        
			_PRINT("graph perceived, setting up...");	
			
			setupGravityManager(); 
			_PRINT("starting to attend requests...");

			while (working) {
				perceiveAndAct();				
				Thread.yield();
			}

		} catch (Exception exc) {
			working = false;
			exc.printStackTrace();
			
		}
		
		_PRINT("finished");	
	}
	
	private void waitForGraph() {
		boolean received = false;
		String[] messages;
		
		_PRINT("awaiting graph");
		
		while (graph == null && this.working) {
			//syncPrint("Waiting for perception of the graph...");
			Thread.yield();
			
			messages = connection.retrieveMessages();

			for (int i = 0; i < messages.length; i++) {
				received = perceiveGraph(messages[i]);
				if (received) {
					break;
				}
			}
		}

	}	
	
	private boolean perceiveGraph(String message) {
		try {			
			Graph[] perceivedGraphs = GraphTranslator.getGraphs(message);
			if (perceivedGraphs.length > 0) {
				this.graph = perceivedGraphs[0];
				return true;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		return false;
	}
	
	@SuppressWarnings("unchecked")
	private void setupGravityManager() throws IOException {
		gravityManager = gravityCombinator.createGravityManager(graph, distanceExponent);		

		visitsScheduledPerNode = new List[graph.getNumVertices()];
		for (int i = 0; i < visitsScheduledPerNode.length; i++) {
			visitsScheduledPerNode[i] = new LinkedList<String>();
		}

		recalculateGravities();
		
		//do nothing for the rest of this cycle
		connection.send("<action type=\"-1\"/>");
	}
	
	private void perceiveAndAct() throws IOException {
		String[] messages = connection.retrieveMessages();
		boolean perceived;
		
		for (int i = 0; i < messages.length; i++) {
			_PRINT("received: " + messages[i]);

			perceived = perceiveGraph(messages[i]);
			if (perceived) {
				_PRINT("graph perceived, updating gravities");
				recalculateGravities();
				connection.send("<action type=\"-1\"/>");
			}

			if (!perceived) {
				attendRequestForGoalNode(messages[i]);
			}
		}
		
	}

	private void attendRequestForGoalNode(String perception) throws IOException {
		String agentId;
		String currentNode;
		String goalNode;

		int markIndex = perception.indexOf("message=\"REQ##");

		if (markIndex > -1) {
			perception = perception.substring(markIndex + 14);
			perception = perception.substring(0, perception.indexOf("\""));			

			markIndex = perception.indexOf("###");
			
			agentId     = perception.substring(0, markIndex);
			currentNode = perception.substring(markIndex + 3);

			// selects and sends the goal node
			goalNode = selectGoalNode(agentId, currentNode);
			connection.send("<action type=\"3\" message=\"ANS##" + agentId + "###" + goalNode + "\"/>");

			_PRINT("sending goal node - <action type=\"3\" message=\"ANS##" + agentId + "###" + goalNode + "\"/>");
		}

	}	
	
	private String selectGoalNode(String agentId, String currNodeId) {
		int currNode = graph.getNode(currNodeId).getIndex();
		
		int goalNode = currNode;
		double goalGravity = -1.0d;
		
		// chooses the neighbor with higher gravity 
		for (Integer neighbor : graph.getSuccessors(currNode)) {
			if (gravityManager.getGravity(currNode,neighbor) > goalGravity) {
				goalNode = neighbor;
				goalGravity = gravityManager.getGravity(currNode,neighbor);
			}
		}
		
		visitsScheduledPerNode[currNode].remove(agentId);
		visitsScheduledPerNode[goalNode].add(agentId); 

		// if it is the only agent going to the node, undo the gravity (mass is zeroed)
		if (visitsScheduledPerNode[goalNode].size() == 1) {
			_PRINT("undoing gravity from " + graph.getNode(goalNode));
			gravityManager.undoGravity(goalNode);
		}
		
		return graph.getNode(goalNode).getIdentifier();
	}

	private void recalculateGravities() {
		int numVertices = gravityManager.getNumVertices();

		gravityManager.undoAllGravities();

		double idleness;
		double nodeMass;
		
		// for each node, applies its gravity to attract agents
		for (int node = 0; node < numVertices; node++) {
			if (visitsScheduledPerNode[node].size() > 0) {
				nodeMass = 0.0d;
			} else {	
				idleness   = graph.getNode(node).getIdleness();
				nodeMass = massGrowth.getVertexMass(1.0d, idleness);
			}
			
			gravityManager.applyGravity(node, nodeMass);
		}
		
	}

	private void _PRINT(String message) {
		System.out.println(identifier.toUpperCase() + ": " + message);
	}
	
}

