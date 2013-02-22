package agent_library.launcher;

import java.io.IOException;
import java.util.LinkedList;
import java.util.Set;

import log_clients.LogFileClient;
import util.Keyboard;
import util.file.FileReader;
import agent_library.basic_agents.AbstractAgent;
import agent_library.connections.TcpConnection;


/**
 * Implements a client that connects to the SimPatrol server to: (1) launch a team of agents;
 * (2) launch a log client, to log the simulation (to calculate metrics, etc.); (3) start the simulation.
 * <br><br>
 * Subclasses should implement "createAndStartAgents" to create agents of a specific strategy.
 * <br><br>
 * To use instances of this class, first start the simulator, then run this class giving as parameter,
 * at least, the configuration files of the simulation.
 * 
 */
public abstract class Launcher extends Thread {
	/**
	 * String that explains how to use the launcher using the second constructor. Subclasses should add 
	 * their strategy-specific parameters, after the string commented below. They can display this 
	 * message in the command line to indicate the proper usage of the (sub)class.
	 */
	protected static final String USAGE =
			"\nUsage: \"[CLIENT_CLASS] <server address> <server port> <configuration file> [<simulation parameter> or <agents' parameter>]*\"\n\n"
					+ "\twhere <simulation parameter> can be:\n" 
					+ "\t\t -time <duration> Sets the time of simulation (default: 100)\n"
					+ "\t\t -nocreate        Indicates the client should not create and start the agents (it creates, by default)\n"	
					+ "\t\t -nostart         Indicates the client should not start the simulation (it starts, by default)\n"
					+ "\t\t -realtime        Setup a real time simulation (it is turn-base simulation, by default)\n"
					+ "\t\t -interactive     Activate interactive mode, asking each decision (it is not interactive, by default)\n"
					+ "\t\t -log <file>      Log simulation on given file (it doesn't save log, by default)\n\n";
					//+ "where <agents' parameter> can be:\n";
	
	protected TcpConnection CONNECTION;
	protected String CONFIGURATION_FILE_PATH;
	
	protected String LOG_FILE_PATH = "";	
	protected double TIME_OF_SIMULATION = 100;
	protected boolean IS_REAL_TIME_SIMULATOR = false;

	protected Set<AbstractAgent> agents;

	protected LogFileClient logClient;

	protected boolean CREATE_AGENTS = true;
	protected boolean START_SIMULATION = true;
	protected boolean INTERATIVE_MODE = false;
	
	
	/**
	 * High-level constructor. Receives all parameters as Java objects.
	 * 
	 * @param serverAddress The IP address of the SimPatrol server.
	 * @param serverPort The number of the port that the server is listening to.
	 * @param environmentPath The path of the file that contains the environment (or the configuration of the simulation).
	 * @param logfilePath The path of the file where the simulation will be logged.
	 * @param timeOfSimulation The time of simulation.
	 * @param realTimeSimulator TRUE if the simulator is a real time one, FALSE if not.
	 */
	public Launcher(String serverAddress, int serverPort, String environmentPath, 
			String logfilePath, double timeOfSimulation, boolean realTimeSimulator)
			throws IOException {
		this.CONFIGURATION_FILE_PATH = environmentPath;
		this.LOG_FILE_PATH = logfilePath;
		this.TIME_OF_SIMULATION = timeOfSimulation;
		this.IS_REAL_TIME_SIMULATOR = realTimeSimulator;
		this.CONNECTION = new TcpConnection(serverAddress, serverPort);
		this.agents = null;
		this.logClient = null;
	}
	
	/**
	 * Constructor that receives an array of strings with, at least, three parameters:
	 * <br><br>
	 * The index 0 must be server address.<br>
	 * The index 1 must be the server port.<br>
	 * The index 2 must be the environment (configuration) file.<br>
	 * <br>
	 * In the other indexes the following parameters may optionally appear:<br>
	 * "-time" followed by the duration, to set the time of simulation (default is 100)<br>
	 * "-nocreate", to indicate that the launcher should not create and start the agents (it creates, by default)<br>	
	 * "-nostart", to indicate that the launcher should not start the simulation (it starts, by default)<br>
	 * "-realtime", to setup a real time simulation (it setups a turn-base simulation, by default)<br>
	 * "-interactive", to activate interactive mode, asking each decision (it is not interactive, by default)<br>
	 * "-log" followed by a file path, to log simulation on the given file (it doesn't save log, by default)<br>
	 * Or other, strategy-specific parameters, defined by subclasses. <br>
 	 * <br>
	 */
	public Launcher(String cmdArgs[]) throws Exception {
		if (cmdArgs.length < 3){
			throw new Exception("Missing parameters!");
		
		} else {
			this.CONNECTION = new TcpConnection(cmdArgs[0], Integer.parseInt(cmdArgs[1]));
			this.CONFIGURATION_FILE_PATH = cmdArgs[2];
			this.agents = null;
			this.logClient = null;
			processCmdLine(cmdArgs, 3);
		}		
		
	}

	private void processCmdLine(String[] cmdArgs, int startIndex) throws Exception {
		int index = startIndex;
		
		while (index < cmdArgs.length){
			int nextIndex = processAgentSpecificCommand(cmdArgs, index);
			
			if (nextIndex > index) {
				index = nextIndex;
				
			} else if(cmdArgs[index].equals("-time")){
				this.TIME_OF_SIMULATION = Double.parseDouble(cmdArgs[index+1]);
				index+=2;
			
			} else if(cmdArgs[index].equals("-log")){
				this.LOG_FILE_PATH = cmdArgs[index+1];
				index+=2;
			
			} else if(cmdArgs[index].equals("-realtime")){
				this.IS_REAL_TIME_SIMULATOR = true;
				index++;
			
			} else if(cmdArgs[index].equals("-nocreate")){
				this.CREATE_AGENTS  = false;
				index++;
			
			} else if(cmdArgs[index].equals("-nostart")){
				this.START_SIMULATION   = false;
				index++;
			
			} else if(cmdArgs[index].equals("-interactive")){
				this.INTERATIVE_MODE    = true;
				index++;
			
			} else {
				throw new Exception("Unknown command: " + cmdArgs[index]);
				
			}
			
		}
	}
	
	/**
	 * May be overridden by subclasses to deal with client-specific commands (given in the command line). 
	 */
	protected int processAgentSpecificCommand(String[] cmdArgs, int i) throws Exception {
		return i;
	}

	/**
	 * Creates and starts the agents. Must be overriden by subclasses.
	 * 
	 * @param agentsIds The ids of the agents to create and start.
	 * @param portNumbers The ports offered by the server to connect to the agents.
	 */
	protected abstract void createAndStartAgents(String[] agentsIds, int[] portNumbers) throws IOException;
	
	
	/**
	 * Main method of the class. It initiates communication with the server, configures the 
	 * simulation, connects the agents and the log client, start the simulation, wait until 
	 * the end of the simulation, then stop the agents and the log client.
	 */
	public void run() {
		this.CONNECTION.open();

		StringAndInt[] agentsPorts = null;
		
		try {
			agentsPorts = this.configureEnvironment();
		} catch (IOException e) {
			e.printStackTrace();
			return;
		}
		
		try {
			int logClientPort;			
			logClientPort = this.configureLogging();
			this.createAndStartLogClient(logClientPort);			
		} catch (IOException e2) {
			e2.printStackTrace();
		}

		String key = "";
		
		if (INTERATIVE_MODE ){
			System.out.println("Do you want to create and start the agents now? [y]es or [n]o?");
			
			try {
				key = Keyboard.readLine();
			} catch (IOException e1) {
				e1.printStackTrace();
			}
		}
		
		if ((INTERATIVE_MODE && ! key.equalsIgnoreCase("n")) || CREATE_AGENTS ) {
			try {
				String[] ids = new String[agentsPorts.length];
				int[] ports = new int[agentsPorts.length];
				
				for (int i = 0; i < ports.length; i++) {
					ids[i] = agentsPorts[i].STRING;
					ports[i] = agentsPorts[i].INTEGER;
				}
				this.createAndStartAgents(ids, ports); 	//creates, connects and starts the agents
System.out.println("Agents created!");				
			} catch (IOException e) {
				e.printStackTrace();
			}
			
		} else {
			for (int i = 0; i < agentsPorts.length; i++) {
				System.out.println("SimPatrol is offering the following configuration: ");
				System.out.println("Agent ID: " + agentsPorts[i].STRING);
				System.out.println("Port    : " + agentsPorts[i].INTEGER);
			}
			
		}

		try {
			this.configureStart();  //ask the server to start the simulation
		} catch (IOException e) {
			e.printStackTrace();
		}

		try {
			this.CONNECTION.thread.join();  //wait connection's thread to stop
		} catch (InterruptedException e1) {
			e1.printStackTrace();
		} 
		
		this.stopAgents();
		System.out.println("Agents stopped!");	
		
		if (this.logClient != null) {
			printDebug("Stopping the log...");
			this.logClient.stopWorking();
		}

		printDebug("Finished working.");

		try {
			Thread.sleep(1000); //1s
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

	}

	/**
	 * Obtains the environment from the referred file and configures it into the
	 * server, returning the activated sockets for the remote agents, as well as
	 * the respective agent IDs.
	 * 
	 * @return The socket numbers for the agents' connections, as well as the
	 *         respective agent IDs.
	 */
	private StringAndInt[] configureEnvironment() throws IOException {
		System.out.print("Creating the environment of the simulation... ");
		FileReader environmentFileReader = new FileReader(this.CONFIGURATION_FILE_PATH);

		StringBuffer environment = new StringBuffer();
		while (!environmentFileReader.isEndOfFile()) {
			environment.append(environmentFileReader.readLine());
		}

		environmentFileReader.close();

		// sends the simulation configuration to the server
		String message = "<configuration type=\"0\">" + environment.toString()
				+ "</configuration>";
		this.CONNECTION.send(message);

		// obtains the answer from the server
		String[] serverAnswer = this.CONNECTION.getBufferAndFlush();
		while (serverAnswer.length == 0) {
			Thread.yield();
			serverAnswer = this.CONNECTION.getBufferAndFlush();
		}

		// from the answer, obtains the ports activated for each agent
		LinkedList<StringAndInt> idsAndPorts = new LinkedList<StringAndInt>();
		
		String receivedMessage = serverAnswer[0];
		int nextAgentIndex = receivedMessage.indexOf("agent_id=\"");
		int nextPortIndex;
		
		String agentId;
		int agentPort;
		
		while (nextAgentIndex > -1) {
			receivedMessage = receivedMessage.substring(nextAgentIndex + 10);
			agentId = receivedMessage.substring(0, receivedMessage.indexOf("\""));

			nextPortIndex = receivedMessage.indexOf("socket=\"");
			receivedMessage = receivedMessage.substring(nextPortIndex + 8);
			agentPort = Integer.parseInt(receivedMessage.substring(0, receivedMessage.indexOf("\"")));

			idsAndPorts.add(new StringAndInt(agentId, agentPort));
		
			nextAgentIndex = receivedMessage.indexOf("agent_id=\"");
		}

		StringAndInt[] answer = new StringAndInt[idsAndPorts.size()];
		for (int i = 0; i < answer.length; i++)
			answer[i] = idsAndPorts.get(i);

		return answer;
	}

	/**
	 * Configures the collecting of events during the simulation. Returns the port.
	 */
	private int configureLogging() throws IOException {
		int port = -1;
		String key = "";

		if (INTERATIVE_MODE){
			System.out.println("Do you want to log the simulation? [y]es or [n]o?");
			key = Keyboard.readLine();
		}
		
		if ((INTERATIVE_MODE && ! key.equalsIgnoreCase("n")) || !LOG_FILE_PATH.equals("")) {
			this.CONNECTION.send("<configuration type=\"5\"/>"); //message to establish a connection to log the simulation 

			String[] serverAnswer = this.CONNECTION.getBufferAndFlush();
			while (serverAnswer.length == 0) {
				serverAnswer = this.CONNECTION.getBufferAndFlush();
				Thread.yield();
			}

			int portIndex = serverAnswer[0].indexOf("message=\"");
			
			String portStr = serverAnswer[0].substring(portIndex + 9);
			portStr = portStr.substring(0, portStr.indexOf("\""));
			
			port = Integer.parseInt(portStr);
			System.out.println("Log connection established.");
		}

		return port;
	}

	/**
	 * Creates and starts the log client, with the port number offered by the server.
	 */
	private void createAndStartLogClient(int portNumber) throws IOException {
		String key = "";
		
		if (portNumber > -1) {			
			if (INTERATIVE_MODE ){
				System.out.println("Do you want to create and start the log client now? [y]es or [n]o?");
				key = Keyboard.readLine();
			}
			
			if ( (INTERATIVE_MODE && ! key.equalsIgnoreCase("n")) || !LOG_FILE_PATH.equals("") ) {
				System.out.print("Creating and starting the log client... ");

				this.logClient = new LogFileClient(this.CONNECTION.getRemoteAddress(), portNumber, this.LOG_FILE_PATH);
				this.logClient.start();

				System.out.println("Finished.");
				
			} else {
				System.out.println("Port offered by SimPatrol to attend log client: " + portNumber);

			}			
		}
		
	}

	/**
	 * Configures the server to start the simulation.
	 */
	private void configureStart() throws IOException {
		if (INTERATIVE_MODE || !START_SIMULATION ){
			System.out.print("Press [ENTER] to start simulation.");
			Keyboard.readLine();
		}

		try {
			Thread.sleep(1000); //1s
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

		String message = "<configuration type=\"3\" parameter=\""
				+ this.TIME_OF_SIMULATION + "\"/>";
		this.CONNECTION.send(message);

		System.out.println("Simulation started.");
	}


	private void stopAgents() {
		for (AbstractAgent agent : this.agents) {
			printDebug("Stopping " + agent.getIdentifier() + "...");
			agent.stopWorking();
		}
	}
	
	protected void printDebug(String str) {
		System.out.println("LAUNCHER: " + str);
	}

	
	/** 
	 * Internal class that holds together a string and an integer. 
	 */
	final class StringAndInt {
		public final String STRING;
		public final int INTEGER;

		public StringAndInt(String string, int integer) {
			this.STRING = string;
			this.INTEGER = integer;
		}
	}
	
}


