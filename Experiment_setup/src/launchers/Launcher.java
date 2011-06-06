/* Client.java */

/* The package of this class. */
package launchers;

/* Imported classes and/or interfaces. */
import java.io.IOException;
import java.net.UnknownHostException;
import java.util.LinkedList;
import java.util.Set;

import log_clients.LogFileClient;
import util.file.FileReader;
import util.net.TCPClientConnection;

import common.Agent;

import control.simulator.CycledSimulator;
import control.simulator.Simulator;

/**
 * Implements a client that connects to the SimPatrol server and configures it,
 * letting agent clients connect to it, in the sequence.
 */
public abstract class Launcher extends Thread {
	/* Attributes. */
	
	private Simulator simulator;
	
	
	protected final String ENVIRONMENT_DIR_PATH;
	
	protected final String ENVIRONMENT_GEN_NAME;
	
	protected final int NUM_ENV;

	private final String LOG_DIR_PATH;
	
	private final String LOG_GEN_NAME;

	/** The time of the simulation. */
	private final int TIME_OF_SIMULATION;

	/** Holds if the simulator is a real time one. */
	protected final boolean IS_REAL_TIME_SIMULATOR;

	/** The TCP connection with the server. */
	protected TCPClientConnection CONNECTION;

	/** The set of agents acting in the simulation. */
	protected Set<Agent> agents;

	/** The client added to log the simulation. */
	private LogFileClient log_client;

	/* Methods. */
	/**
	 * Constructor.
	 * 
	 * @param remote_socket_address
	 *            The IP address of the SimPatrol server.
	 * @param remote_socket_number
	 *            The number of the socket that the server is supposed to listen
	 *            to this client.
	 * @param environment_file_path
	 *            The path of the file that contains the environment.
	 * @param metrics_file_paths
	 *            The paths of the files that will save the collected metrics:
	 *            index 0: The file that will save the mean instantaneous
	 *            idlenesses; index 1: The file that will save the max
	 *            instantaneous idlenesses; index 2: The file that will save the
	 *            mean idlenesses; index 3: The file that will save the max
	 *            idlenesses;
	 * @param log_file_path
	 *            The path of the file to log the simulation.
	 * @param metrics_collection_rate
	 *            The time interval used to collect the metrics.
	 * @param time_of_simulation
	 *            The time of simulation.
	 * @param is_real_time_simulator
	 *            TRUE if the simulator is a real time one, FALSE if not.
	 * 
	 * @throws IOException
	 * @throws UnknownHostException
	 */
	public Launcher(String environment_dir_path, String env_gen_name, int numEnv,
			String log_dir_path,  String log_gen_name,
			int time_of_simulation)
			throws UnknownHostException, IOException {
		this.ENVIRONMENT_DIR_PATH = environment_dir_path;
		this.ENVIRONMENT_GEN_NAME = env_gen_name;
		this.NUM_ENV = numEnv;
		this.LOG_DIR_PATH = log_dir_path;
		this.LOG_GEN_NAME = log_gen_name;
		this.TIME_OF_SIMULATION = time_of_simulation;
		this.IS_REAL_TIME_SIMULATOR = false;
		this.agents = null;
		this.log_client = null;
	}
	
	
	private void createAndStartSimulator(){
		try {
			simulator = new CycledSimulator(5000, 0.005);
		} catch (IOException e) {
			// TODO Auto-generated catch block
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
	 * @throws IOException
	 */
	private StringAndInt[] configureEnvironment(int envnum) throws IOException {
		// screen message
		System.out.print("Creating the environment of the simulation... ");

		// the file reader that will obtain the environment from the given file
		String env_path = this.ENVIRONMENT_DIR_PATH + "/" + this.ENVIRONMENT_GEN_NAME + "_" + envnum + ".txt";
		FileReader file_reader = new FileReader(env_path);

		// holds the environment obtained from the file
		StringBuffer buffer = new StringBuffer();
		while (!file_reader.isEndOfFile()) {
			buffer.append(file_reader.readLine());
		}

		// closes the read file
		file_reader.close();

		// mounts the message of configuration
		String message = "<configuration type=\"0\">" + buffer.toString()
				+ "</configuration>";

		// sends it to the server
		this.CONNECTION.send(message);

		// obtains the answer from the server
		String[] server_answer = this.CONNECTION.getBufferAndFlush();
		while (server_answer.length == 0)
			server_answer = this.CONNECTION.getBufferAndFlush();

		// from the answer, obtains the sockets activated for each agent,
		// as well as the respective agent IDs
		LinkedList<StringAndInt> ids_and_sockets = new LinkedList<StringAndInt>();
		String received_message = server_answer[0];
		int next_agent_index = received_message.indexOf("agent_id=\"");
		while (next_agent_index > -1) {
			received_message = received_message
					.substring(next_agent_index + 10);
			String agent_id = received_message.substring(0, received_message
					.indexOf("\""));

			int next_socket_index = received_message.indexOf("socket=\"");
			received_message = received_message
					.substring(next_socket_index + 8);
			int socket = Integer.parseInt(received_message.substring(0,
					received_message.indexOf("\"")));

			ids_and_sockets.add(new StringAndInt(agent_id, socket));
			next_agent_index = received_message.indexOf("agent_id=\"");
		}

		// mounts the answer of the method
		StringAndInt[] answer = new StringAndInt[ids_and_sockets.size()];
		for (int i = 0; i < answer.length; i++)
			answer[i] = ids_and_sockets.get(i);

		// screen message
		System.out.println("Finished.");

		// returns the answer
		return answer;
	}


	/**
	 * Configures the collecting of events during the simulation.
	 * 
	 * @return The socket number for the log clients be connected.
	 * @throws IOException
	 */
	private int configureLogging() throws IOException {
		// the answer for the method
		int answer = -1;
		// the message to establish a connection to the server to log the
		// simulation
		String message = "<configuration type=\"5\"/>";

		// sends it to the server
		this.CONNECTION.send(message);

		// obtains the answer from the server
		String[] server_answer = this.CONNECTION.getBufferAndFlush();
		while (server_answer.length == 0)
			server_answer = this.CONNECTION.getBufferAndFlush();

		// adds it to the answer of the method
		int metric_socket_index = server_answer[0].indexOf("message=\"");
		server_answer[0] = server_answer[0]
				.substring(metric_socket_index + 9);
		answer = Integer.parseInt(server_answer[0].substring(0,
				server_answer[0].indexOf("\"")));

		// screen message
		System.out.println("Log connection established.");

		// returns the answer
		return answer;
	}

	/**
	 * Creates and starts the log client, given the numbers of the socket
	 * offered by the server.
	 * 
	 * @param socket_number
	 *            The socket number offered by the server to connect to the
	 *            remote client.
	 * @throws IOException
	 */
	private void createAndStartLogClient(int numlog, int socket_number) throws IOException {
		// if the socket number is valid
		if (socket_number > -1) {
				// screen message
			System.out.print("Creating and starting the log client... ");

			// creates the log client
			String log_path = this.LOG_DIR_PATH + "/" + this.LOG_GEN_NAME + "_" + numlog + ".txt";
			this.log_client = new LogFileClient(this.CONNECTION.getRemoteSocketAdress(), 
					socket_number, log_path);

			// starts the log client
			this.log_client.start();

			// screen message
			System.out.println("Finished.");
			
		}
	}

	/**
	 * Configures the server to start the simulation with the given time of
	 * duration.
	 * 
	 * @throws IOException
	 */
	private void configureStart() throws IOException {

		// the message to be sent to the server
		String message = "<configuration type=\"3\" parameter=\""
				+ this.TIME_OF_SIMULATION + "\"/>";

		// send the message to the server
		this.CONNECTION.send(message);

		// screen message
		System.out.println("Simulation started.");
	}


	/** Stops the agents. */
	protected void stopAgents() {
		if (this.agents != null) {
			Object[] agents_array = this.agents.toArray();
			for (int i = 0; i < agents_array.length; i++)
				((Agent) agents_array[i]).stopWorking();
			
			this.agents.clear();
		}
	}
	

	public void run() {
			
	System.out.println("Starting simulation n°" + NUM_ENV + ".");
	
	
	try {
		this.CONNECTION = new TCPClientConnection("127.0.0.1", 5000);
	} catch (UnknownHostException e1) {
		// TODO Auto-generated catch block
		e1.printStackTrace();
	} catch (IOException e1) {
		// TODO Auto-generated catch block
		e1.printStackTrace();
	}
	// starts its TCP connection
	this.CONNECTION.start();
	
	// configures the environment of the simulation
	// and obtains the socket numbers for each agent
	StringAndInt[] agents_socket_numbers = new StringAndInt[0];
	try {
		agents_socket_numbers = this.configureEnvironment(NUM_ENV);
	} catch (IOException e) {
		e.printStackTrace();
	}
	
	// configures the log client of the simulation, obtaining its socket
	// number
	int log_socket_number = -1;
	try {
		log_socket_number = this.configureLogging();
	} catch (IOException e2) {
		e2.printStackTrace();
	}
	
	// creates, connects and starts the log client
	try {
		this.createAndStartLogClient(NUM_ENV, log_socket_number);
	} catch (IOException e2) {
		e2.printStackTrace();
	}

	try {
		Thread.sleep(3000);
	} catch (InterruptedException e3) {
		// TODO Auto-generated catch block
		e3.printStackTrace();
	}
	
	// creates, connects and starts the agents
	// asks if the client should itself create and start the agents
	try {
		String[] agent_ids = new String[agents_socket_numbers.length];
		for (int j = 0; j < agent_ids.length; j++)
			agent_ids[j] = agents_socket_numbers[j].STRING;

		int[] socket_numbers = new int[agents_socket_numbers.length];
		for (int j = 0; j < socket_numbers.length; j++)
			socket_numbers[j] = agents_socket_numbers[j].INTEGER;

		this.createAndStartAgents(agent_ids, socket_numbers);
	} catch (IOException e) {
		e.printStackTrace();
	}
	
	// configures the simulation to start
	try {
		this.configureStart();
	} catch (IOException e) {
		e.printStackTrace();
	}

	// while the TCP connection is alive, waits...
	while (this.CONNECTION.getState() != Thread.State.TERMINATED)
		;
	
	
	try {
		this.CONNECTION.stopWorking();
	} catch (IOException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}
	this.CONNECTION = null;
	
	// stops the log client
	if(this.log_client != null)
		this.log_client.stopWorking();
	this.log_client = null;
	
	// stops the agents
	this.stopAgents();

	// screen message
	System.out.println("Finished simulation n° " + NUM_ENV + ". Waiting for shutdown of the simulator and agents.");
	
	System.exit(0);
	}

	/**
	 * Creates and starts the agents, given the numbers of the sockets for each
	 * agent.
	 * 
	 * @param socket_numbers
	 *            The socket numbers offered by the server to connect to the
	 *            remote agents.
	 * @throws IOException
	 */
	protected abstract void createAndStartAgents(String[] agent_ids,
			int[] socket_numbers) throws IOException;
}

/** Internal class that holds together a string and an integer. */
final class StringAndInt {
	/* Attributes */
	/** The string value. */
	public final String STRING;

	/** The integer value. */
	public final int INTEGER;

	/* Methods. */
	/**
	 * Constructor.
	 * 
	 * @param string
	 *            The string value of the pair.
	 * @param integer
	 *            The integer of the pair.
	 */
	public StringAndInt(String string, int integer) {
		this.STRING = string;
		this.INTEGER = integer;
	}
}
