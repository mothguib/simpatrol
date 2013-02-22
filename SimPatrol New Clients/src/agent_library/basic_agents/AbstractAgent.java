package agent_library.basic_agents;

import java.util.ArrayList;
import java.util.List;

import util.agents.SocietyImage;
import agent_library.connections.ClientConnection;
import agent_library.perceptions.AgentInformation;


/**
 * Superclass for all agents in the library.
 * <br><br>
 * It keeps perceptions as high-level objects. This class treats this "perceptions objects" as objects 
 * to be "consumed":
 * <ul>
 * <li>The <b>perceive[Perception]</b> methods access (consume) a perception object of the given type.</li>
 * <li>To ask if there is a (new) perception of that kind to be consumed, there are methods 
 * named <b>hasNew[Perception]Perception</b>.</li>
 * <li> So, after a perception is accessed by <b>perceive[Perception]</b>, the <b>hasNew[Perception]Perception</b>
 * method returns "false", until a new perception arrives (e.g. in the next turn).
 * <li>A perception object should not be accessed if it is not a "new" perception. Future implementations 
 * may return "null" in this case (currently, it returns the last perception).</li>
 * </ul>
 * This idea of consumption was adopted because the perception objects may change (or arrive) at any 
 * time. Therefore, subclasses should not rely of the objects kept by the class -- they should assign 
 * it to a subclass field or local variable if the same perception object is to be used many times.
 * 
 * TODO do "synchronized" in more specific objects (e.g. the perception objects) 
 * 
 * @author Pablo A. Sampaio
 */
public abstract class AbstractAgent {
	protected final String identifier;
	
	ClientConnection connection;         // only the manager should access
	ArrayList<String> messagesToServer;  // only the manager should access
	ArrayList<String> broadcastsToSend;  // only the manager should access (TODO: remove)

	private int currentTurn;

	protected boolean stopRequested;
	protected boolean usingGraph2;
	
	// attributes below represent perceptions and should be 
	// directly accessed only by this class
	
	private AgentInformation selfInfo; 
	boolean receivedNewSelfInfo;

	private SocietyImage societyInfo;  // TODO: create a (better) class named SocietyInfo
	boolean receivedNewSocietyInfo;
	
	private util.graph.Graph graph;
	private util.graph2.Graph graph2;  // TODO: unify graph libraries
	boolean receivedNewGraph;

	List<String> broadcastsReceived;
	

	protected AbstractAgent(String id, ClientConnection conn, boolean useGraph2) {
		identifier = id;
		connection = conn;
		messagesToServer = new ArrayList<String>();
		broadcastsToSend = new ArrayList<String>();

		usingGraph2 = useGraph2;
		
		broadcastsReceived = new ArrayList<String>();
		
		receivedNewSelfInfo    = false;
		receivedNewSocietyInfo = false;
		receivedNewGraph       = false;
		
		currentTurn = 0;
	}
	
	public void startWorking() {
		AbstractAgentManager.getInstance().addAgent(this);
		printDebug("Started working.");
	}
	
	public void stopWorking() {
		this.stopRequested = true;
		AbstractAgentManager.getInstance().removeAgent(this);
	}
	
	public String getIdentifier() {
		return this.identifier;
	}

	protected final int getCurrentTurn() {
		return currentTurn;
	}

	protected final synchronized boolean hasNewSelfPerception() throws AgentStoppedException {
		if (stopRequested) { throw new AgentStoppedException(this.identifier); }
		return receivedNewSelfInfo;
	}
	protected final synchronized AgentInformation perceiveSelf() throws AgentStoppedException {
		if (stopRequested) { throw new AgentStoppedException(this.identifier); }
		receivedNewSelfInfo = false;
		return selfInfo;
	}

	protected final synchronized boolean hasNewSocietyPerception() throws AgentStoppedException {
		if (stopRequested) { throw new AgentStoppedException(this.identifier); }
		return receivedNewSocietyInfo;
	}
	protected final synchronized SocietyImage perceiveSociety() throws AgentStoppedException {
		if (stopRequested) { throw new AgentStoppedException(this.identifier); }
		receivedNewSocietyInfo = false;
		return societyInfo;
	}
	
	protected final synchronized boolean hasNewGraphPerception() throws AgentStoppedException {
		if (stopRequested) { throw new AgentStoppedException(this.identifier); }
		return receivedNewGraph;
	}
	protected final synchronized util.graph.Graph perceiveGraph() throws AgentStoppedException {
		if (stopRequested) { 
			throw new AgentStoppedException(this.identifier); 
		}
		if (usingGraph2) { 
			throw new RuntimeException("Improper call to perceiveGraph()");  //temporary
		}
		receivedNewGraph = false;
		return graph;
	}
	protected final synchronized util.graph2.Graph perceiveGraph2() throws AgentStoppedException {
		if (stopRequested) { 
			throw new AgentStoppedException(this.identifier); 
		}
		if (!usingGraph2) { 
			throw new RuntimeException("Improper call to perceiveGraph2()"); //temporary
		}
		receivedNewGraph = false;
		return graph2;
	}
	protected final boolean usingGraph2() {
		return usingGraph2;
	}
	
	protected final synchronized boolean hasNewBroadcasts() throws AgentStoppedException {
		if (stopRequested) { throw new AgentStoppedException(this.identifier); }
		return broadcastsReceived.size() != 0;
	}
	
	protected final synchronized void retrieveBroadcasts(ArrayList<String> bufferMsgs) throws AgentStoppedException {
		if (stopRequested) { throw new AgentStoppedException(this.identifier); }
		bufferMsgs.clear();
		bufferMsgs.addAll(this.broadcastsReceived);
		this.broadcastsReceived.clear();
	}

	protected final synchronized void actVisit() throws AgentStoppedException {
		if (stopRequested) { throw new AgentStoppedException(this.identifier); }
		messagesToServer.add("<action type=\"2\"/>");
	}

	protected final synchronized void actGoto(String nodeId) throws AgentStoppedException {
		if (stopRequested) { throw new AgentStoppedException(this.identifier); }
		messagesToServer.add("<action type=\"1\" node_id=\""+ nodeId + "\"/>");
	}

	protected final synchronized void actSendBroadcast(String message) throws AgentStoppedException {
		if (stopRequested) { throw new AgentStoppedException(this.identifier); }
		broadcastsToSend.add(message);
		//messagesToServer.add("<action type=\"3\" message=\"" + message + "\"/>");		
	}

	// teleport action
	//protected synchronized void actStepGoto(String nodeId) {
	//}
	
	//protected synchronized void actMarkNode(String nodeId) {
	//}
	
	protected final synchronized void actDoNothing() throws AgentStoppedException {
		if (stopRequested) { throw new AgentStoppedException(this.identifier); }
		messagesToServer.add("<action type=\"-1\"/>");
	}
	
	protected void printDebug(String message) {
		if (identifier != null) {
			System.out.println(identifier.toUpperCase() + ": " + message);
		}
	}
	
	/** Methods used only by the manager **/
	
	final void setCurrentTurn(double time) {
		this.currentTurn = (int)time;
	}
	
	synchronized final void retrieveMessagesToServer(ArrayList<String> answer) {
		answer.clear();
		answer.addAll(messagesToServer);
		messagesToServer.clear();
	}
	
	synchronized final void retrieveBroadcastsToSend(ArrayList<String> answer) {
		answer.clear();
		answer.addAll(broadcastsToSend);
		broadcastsToSend.clear();
	}
	
	synchronized final void setSelfInfo(AgentInformation agentInformation) {
		this.selfInfo = agentInformation;
		this.receivedNewSelfInfo = true;
	}

	synchronized final void setSocietyInfo(SocietyImage socInfo) {
		this.societyInfo = socInfo;
		this.receivedNewSocietyInfo = true;
	}

	synchronized final void setGraph(util.graph.Graph g) {
		if (usingGraph2) { throw new RuntimeException("Improper call to setGraph()"); }
		this.graph = g;
		this.receivedNewGraph = true;
	}
	
	synchronized final void setGraph2(util.graph2.Graph g) {
		if (!usingGraph2) { throw new RuntimeException("Improper call to setGraph2()"); }
		this.graph2 = g;
		this.receivedNewGraph = true;
	}
	
	synchronized final void addBroadcast(String message) {
		this.broadcastsReceived.add(message);
	}

	synchronized final void addBroadcasts(ArrayList<String> messages) { //TODO: remove
		this.broadcastsReceived.addAll(messages);
	}
	
}
