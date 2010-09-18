package util.debug_client;

import java.io.IOException;
import java.net.UnknownHostException;
import java.util.LinkedList;
import java.util.List;

import log_clients.LogFileClient;

import util.net.TCPClientConnection;

import common.Agent;
import common.IMessageObserver;

import cycled.CycledAgent;
import cycled.CycledCoordinatorAgent;


public class DebugClient implements IMessageObserver {
	private TcpConnection connection;
	private int totalCycles;
	private List<DebugAgent> agentsList;
	private LogFileClient logClient;
	private int numAgents;
	
	
	public DebugClient(int agents, int cycles, String serverIp, int serverPort) throws UnknownHostException, IOException {
		numAgents = agents;
		totalCycles = cycles;
		connection = new TcpConnection(serverIp, serverPort);
		agentsList = null;
	}
	
	
	public void start() {
		
		try {

			this.connection.start();		

			// 1. sends the environment and connects agents

			System.out.print("1. Sending the environment... ");
			
			String graph = 
				"<graph label=\"graph_perceived\">" 
				+ "  <node id=\"a\" label=\"a\" idleness=\"0\" visibility=\"true\" priority=\"0\" fuel=\"false\" is_enabled=\"true\"/>"
				+ "  <node id=\"b\" label=\"b\" idleness=\"8\" visibility=\"true\" priority=\"0\" fuel=\"false\" is_enabled=\"true\"/>"
				+ "  <node id=\"c\" label=\"c\" idleness=\"17\" visibility=\"true\" priority=\"0\" fuel=\"false\" is_enabled=\"true\"/>"
				+ "  <edge id=\"ab\" source=\"a\" target=\"b\" directed=\"false\" length=\"6\" visibility=\"true\" is_enabled=\"true\" is_in_dynamic_source_memory=\"false\" is_in_dynamic_target_memory=\"false\"/>"
				+ "  <edge id=\"bc\" source=\"b\" target=\"c\" directed=\"false\" length=\"4\" visibility=\"true\" is_enabled=\"true\" is_in_dynamic_source_memory=\"false\" is_in_dynamic_target_memory=\"false\"/>"
				+ "  <edge id=\"ca\" source=\"c\" target=\"a\" directed=\"false\" length=\"8\" visibility=\"true\" is_enabled=\"true\" is_in_dynamic_source_memory=\"false\" is_in_dynamic_target_memory=\"false\"/>"
				+ "</graph>";
			
			String society = "<society id=\"soc1\" label=\"soc1\">";

			//TODO: tirar percepção de agentes...
			char startNode; 
			for (int index = 0; index < numAgents; index++) {
				startNode = (char)('a' + (index % 3));
				society = society
					+ "<agent id=\"agent" + index + "\" label=\"ag" + index + "\" node_id=\"" + startNode + "\" state=\"1\" stamina=\"1.0\" max_stamina=\"1.0\">"
					+ "  <allowed_perception type=\"4\"/> <allowed_perception type=\"3\"/> <allowed_perception type=\"1\"/> <allowed_perception type=\"0\"/>"
					+ "  <allowed_action type=\"3\"/> <allowed_action type=\"1\"/> <allowed_action type=\"2\"/>"
					+ "</agent>";
			}
		
			society += "</society>"; 

			//System.out.println(society);
			
			String message1 = 
				"<configuration type=\"0\">" 
				+ "<environment>"
				+ graph
				+ society
				+ "</environment>" 
				+ "</configuration>";

			this.connection.send(message1);
			
			System.out.print("waiting for information about agent's connection... ");
			AgentInfo[] agentsInfo = receiveAgentsConnectionInfo();

			System.out.print("creating agents... ");
			createAgents(agentsInfo);
			
			System.out.println("ok!");
			
			// 2. starts the log client
			
			System.out.print("2. Configuring log client... ");
			
			String message2 = "<configuration type=\"5\"/>";
			this.connection.send(message2);
			
			System.out.print("receiving connection information... ");
			int logClientPort = receiveLogConnectionInfo();
			
			System.out.print("creating... ");
			
			this.logClient = new LogFileClient(connection.getRemoteSocketAdress(), logClientPort, "tmp\\simlog.log");

			System.out.println("ok!");

			// 3. starts up the simulation

			System.out.print("3. Starting up the agents and the log... ");
			
			for (DebugAgent agent : agentsList) {
				agent.startWorking();
			}
			logClient.start();
			
			System.out.print(" sending message to start the simulation... ");
			
			String message3 = "<configuration type=\"3\" parameter=\"" + this.totalCycles + "\"/>";
			this.connection.send(message3);

			System.out.println("ok, simulation started!");
			
			// necessary to be notified about incoming messages  
			this.connection.addObserver(this);
		
		} catch (IOException e) {

			e.printStackTrace();
		}
		
	}
	
	private int receiveLogConnectionInfo() {
		String[] serverAnswer = this.connection.getBufferAndFlush();
		while (serverAnswer == null) {
			serverAnswer = this.connection.getBufferAndFlush();
		}

		int metricSocketIndex = serverAnswer[0].indexOf("message=\"");
		serverAnswer[0] = serverAnswer[0].substring(metricSocketIndex + 9);

		// returns the port number
		return Integer.parseInt( serverAnswer[0].substring(0, serverAnswer[0].indexOf("\"")) );
	}


	private AgentInfo[] receiveAgentsConnectionInfo() {
		// obtains the answer from the server
		String[] serverAnswer = this.connection.getBufferAndFlush();
		while (serverAnswer == null) {
			serverAnswer = this.connection.getBufferAndFlush();
			Thread.yield();
		}
		
		System.out.print("processing answer... ");

		// reads the sockets activated for each agent, as well as agent's IDs
		LinkedList<AgentInfo> agentsInfo = new LinkedList<AgentInfo>();
		String receivedMessage = serverAnswer[0];
		
		int nextAgentIndex = receivedMessage.indexOf("agent_id=\"");
		
		while (nextAgentIndex > -1) {
			receivedMessage = receivedMessage.substring(nextAgentIndex + 10);
			
			String agentId = receivedMessage.substring( 0, receivedMessage.indexOf("\"") );
			int nextSocketIndex = receivedMessage.indexOf("socket=\"");
			
			receivedMessage = receivedMessage.substring(nextSocketIndex + 8);
			int socket = Integer.parseInt(
							receivedMessage.substring(0, receivedMessage.indexOf("\"")) );

			agentsInfo.add(new AgentInfo(agentId, socket));
			
			nextAgentIndex = receivedMessage.indexOf("agent_id=\"");
		}

		// mounts the answer of the method
		AgentInfo[] answer = new AgentInfo[agentsInfo.size()];
		for (int i = 0; i < answer.length; i++) {
			answer[i] = agentsInfo.get(i);
		}
		
		return answer;
	}

	private void createAgents(AgentInfo[] agentsInfo) throws IOException {
		DebugAgent agent;
		TcpConnection agentConnection;
		String startNode, nextNode;
		
		String serverAddres = this.connection.getRemoteSocketAdress();
		
		this.agentsList = new LinkedList<DebugAgent>();

		for (int index = 0; index < agentsInfo.length; index++) {
			agentConnection = new TcpConnection(serverAddres, agentsInfo[index].port);
			startNode = "" + (char)('a' + (index % 3));
			nextNode  = "" + (char)('a' + ((index+1) % 3));
			
			agent = new DebugAgent(agentsInfo[index].id, agentConnection, startNode, nextNode);

			this.agentsList.add(agent);
		}
	}
	
	// called by the the connection (observer)
	public void update(){
		if (this.connection.getState() == Thread.State.TERMINATED){

			for (DebugAgent agent : this.agentsList) {
				agent.stopWorking();
			}

			this.logClient.stopWorking();

			System.out.println("Clients finished.");
		
		} else {
			//System.out.println("Update: nothing to do.");
			
		}
		
	}
	
	
	// auxiliary inner class
	final class AgentInfo {
		public final String id;
		public final int port;

		public AgentInfo(String string, int integer) {
			this.id = string;
			this.port = integer;
		}
	}
	
	
	public static void main(String[] args) throws UnknownHostException, IOException {
		DebugClient client = new DebugClient(2, 20, "127.0.0.1", 5000);
		
		client.start();
	}

	
}
