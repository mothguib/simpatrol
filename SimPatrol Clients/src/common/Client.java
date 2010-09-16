/* Client.java */

/* The package of this class. */
package common;

/* Imported classes and/or interfaces. */
import java.io.IOException;
import java.net.UnknownHostException;
import java.util.LinkedList;
import java.util.Set;
import log_clients.LogFileClient;
import metric_clients.MetricFileClient;
import util.Keyboard;
import util.file.FileReader;
import util.net.TCPClientConnection;

/**
 * Implements a client that connects to the SimPatrol server and configures it,
 * letting agent clients connect to it, in the sequence.
 */
public abstract class Client extends Thread {
	/* Attributes. */
	/** The path of the file that contains the environment. */
	private final String ENVIRONMENT_FILE_PATH;

	/** The paths of the files that will save the collected metrics. */
	private final String[] METRICS_FILE_PATHS;

	/** The time interval used to collect the metrics. */
	private final double METRICS_COLLECTING_RATE;

	/** The path of the file to store the log of the simulation. */
	private final String LOG_FILE_PATH;

	/** The time of the simulation. */
	private final double TIME_OF_SIMULATION;

	/** Holds if the simulator is a real time one. */
	protected final boolean IS_REAL_TIME_SIMULATOR;

	/** The TCP connection with the server. */
	protected final TCPClientConnection CONNECTION;

	/** The set of agents acting in the simulation. */
	protected Set<Agent> agents;

	/** The metric collector clients added to the simulation. */
	private LinkedList<MetricFileClient> metric_clients;

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
	public Client(String remote_socket_address, int remote_socket_number,
			String environment_file_path, String[] metrics_file_paths,
			double metrics_collecting_rate, String log_file_path,
			double time_of_simulation, boolean is_real_time_simulator)
			throws UnknownHostException, IOException {
		this.ENVIRONMENT_FILE_PATH = environment_file_path;
		this.METRICS_FILE_PATHS = metrics_file_paths;
		this.METRICS_COLLECTING_RATE = metrics_collecting_rate;
		this.LOG_FILE_PATH = log_file_path;
		this.TIME_OF_SIMULATION = time_of_simulation;
		this.IS_REAL_TIME_SIMULATOR = is_real_time_simulator;
		this.CONNECTION = new TCPClientConnection(remote_socket_address,
				remote_socket_number);
		this.metric_clients = null;
		this.agents = null;
		this.log_client = null;
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
	private StringAndInt[] configureEnvironment() throws IOException {
		// screen message
		System.out.print("Creating the environment of the simulation... ");

		// the file reader that will obtain the environment from the given file
		FileReader file_reader = new FileReader(this.ENVIRONMENT_FILE_PATH);

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
	 * Configures the metrics to be collected during the simulation into the
	 * server.
	 * 
	 * @return The socket numbers for the metric clients be connected.
	 * @throws IOException
	 */
	private int[] configureMetrics() throws IOException {
		// the answer for the method
		int[] answer = { -1, -1, -1, -1 };

		// asks if a mean instantaneous idlenesses metric shall be created
		System.out
				.println("Should I create a \"mean instantaneous idlenesses metric\"? [y]es or [n]o?");
		String key = Keyboard.readLine();

		if (key.equalsIgnoreCase("y")) {
			// the message to create a metric that collects the
			// mean instantaneous idlenesses of the environment
			String message = "<configuration type=\"2\" parameter=\""
					+ String.valueOf(this.METRICS_COLLECTING_RATE)
					+ "\"><metric type=\"0\"/></configuration>";

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
			answer[0] = Integer.parseInt(server_answer[0].substring(0,
					server_answer[0].indexOf("\"")));

			// screen message
			System.out.println("Metric created.");
		}

		// asks if a max instantaneous idlenesses metric shall be created
		System.out
				.println("Should I create a \"max instantaneous idlenesses metric\"? [y]es or [n]o?");
		key = Keyboard.readLine();

		if (key.equalsIgnoreCase("y")) {
			
			// the message to create a metric that collects the
			// max instantaneous idlenesses of the environment
			String message = "<configuration type=\"2\" parameter=\""
					+ String.valueOf(this.METRICS_COLLECTING_RATE)
					+ "\"><metric type=\"1\"/></configuration>";

			// sends it to the server
			this.CONNECTION.send(message);

			// obtains the answer from the server
			String[] server_answer = this.CONNECTION.getBufferAndFlush();
			while (server_answer.length == 0)
				server_answer = this.CONNECTION.getBufferAndFlush();

			// adds it to the answer of the method
			System.out.println(System.currentTimeMillis());
			int metric_socket_index = server_answer[0].indexOf("message=\"");
			server_answer[0] = server_answer[0]
					.substring(metric_socket_index + 9);
			answer[1] = Integer.parseInt(server_answer[0].substring(0,
					server_answer[0].indexOf("\"")));
			System.out.println(System.currentTimeMillis());
			// screen message
			System.out.println("Metric created.");
		}

		// asks if a mean idlenesses metric shall be created
		System.out
				.println("Should I create a \"mean idlenesses metric\"? [y]es or [n]o?");
		key = Keyboard.readLine();

		if (key.equalsIgnoreCase("y")) {

			// the message to create a metric that collects the
			// mean idleness of the environment
			String message = "<configuration type=\"2\" parameter=\""
					+ String.valueOf(this.METRICS_COLLECTING_RATE)
					+ "\"><metric type=\"2\"/></configuration>";

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
			answer[2] = Integer.parseInt(server_answer[0].substring(0,
					server_answer[0].indexOf("\"")));

			// screen message
			System.out.println("Metric created.");
		}

		// asks if a max idlenesses metric shall be created
		System.out
				.println("Should I create a \"max idlenesses metric\"? [y]es or [n]o?");
		key = Keyboard.readLine();

		if (key.equalsIgnoreCase("y")) {
			// the message to create a metric that collects the
			// max idleness of the environment
			String message = "<configuration type=\"2\" parameter=\""
					+ String.valueOf(this.METRICS_COLLECTING_RATE)
					+ "\"><metric type=\"3\"/></configuration>";

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
			answer[3] = Integer.parseInt(server_answer[0].substring(0,
					server_answer[0].indexOf("\"")));

			// screen message
			System.out.println("Metric created.");
		}

		// returns the answer
		return answer;
	}

	/**
	 * Creates and starts the metric clients, given the numbers of the sockets
	 * for each client.
	 * 
	 * @param socket_numbers
	 *            The socket numbers offered by the server to connect to the
	 *            remote clients.
	 * @throws IOException
	 */
	private void createAndStartMetricClients(int[] socket_numbers)
			throws IOException {
		// verifies if there is any metric to be collected
		boolean there_is_metric = false;
		for (int i = 0; i < socket_numbers.length; i++)
			if (socket_numbers[i] > -1) {
				there_is_metric = true;
				break;
			}

		if (there_is_metric) {
			// asks if the client shall itself start the metric clients
			System.out
					.println("Should I myself create and start the metric clients? [y]es or [n]o?");
			String key = Keyboard.readLine();

			if (key.equalsIgnoreCase("y")) {
				// screen message
				System.out.print("Creating and starting metric clients... ");

				for (int i = 0; i < socket_numbers.length; i++)
					if (socket_numbers[i] > -1) {
						if (this.metric_clients == null)
							this.metric_clients = new LinkedList<MetricFileClient>();

						switch (i) {
						case 0: {
							this.metric_clients.add(new MetricFileClient(
									this.CONNECTION.getRemoteSocketAdress(),
									socket_numbers[0],
									this.METRICS_FILE_PATHS[0],
									"Mean instantaneous idlenesses"));
							break;
						}
						case 1: {
							this.metric_clients.add(new MetricFileClient(
									this.CONNECTION.getRemoteSocketAdress(),
									socket_numbers[1],
									this.METRICS_FILE_PATHS[1],
									"Max instantaneous idlenesses"));
							break;
						}
						case 2: {
							this.metric_clients.add(new MetricFileClient(
									this.CONNECTION.getRemoteSocketAdress(),
									socket_numbers[2],
									this.METRICS_FILE_PATHS[2],
									"Mean idlenesses"));
							break;
						}
						case 3: {
							this.metric_clients.add(new MetricFileClient(
									this.CONNECTION.getRemoteSocketAdress(),
									socket_numbers[3],
									this.METRICS_FILE_PATHS[3],
									"Max idlenesses"));
							break;
						}
						}
					}

				// starts the metric clients
				for (int i = 0; i < this.metric_clients.size(); i++)
					this.metric_clients.get(i).start();

				// screen message
				System.out.println("Finished.");
			} else
				for (int i = 0; i < socket_numbers.length; i++)
					if (socket_numbers[i] > -1)
						System.out
								.println("Port offered by SimPatrol to attend metric client: "
										+ socket_numbers[i]);
		}
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

		// asks if a log connection shall be established
		System.out.println("Should I log the simulation? [y]es or [n]o?");
		String key = Keyboard.readLine();

		if (key.equalsIgnoreCase("y")) {
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
		}

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
	private void createAndStartLogClient(int socket_number) throws IOException {
		// if the socket number is valid
		if (socket_number > -1) {
			// asks if the client shall itself start the log client
			System.out
					.println("Should I myself create and start the log client? [y]es or [n]o?");
			String key = Keyboard.readLine();

			if (key.equalsIgnoreCase("y")) {
				// screen message
				System.out.print("Creating and starting the log client... ");

				// creates the log client
				this.log_client = new LogFileClient(this.CONNECTION
						.getRemoteSocketAdress(), socket_number,
						this.LOG_FILE_PATH);

				// starts the log client
				this.log_client.start();

				// screen message
				System.out.println("Finished.");
			} else
				System.out
						.println("Port offered by SimPatrol to attend log client: "
								+ socket_number);
		}
	}

	/**
	 * Configures the server to start the simulation with the given time of
	 * duration.
	 * 
	 * @throws IOException
	 */
	private void configureStart() throws IOException {
		// waits for the user to press any key to start simulation
		System.out.print("Press [ENTER] to start simulation.");
		Keyboard.readLine();

		// the message to be sent to the server
		String message = "<configuration type=\"3\" parameter=\""
				+ this.TIME_OF_SIMULATION + "\"/>";

		// send the message to the server
		this.CONNECTION.send(message);

		// screen message
		System.out.println("Simulation started.");
	}

	/** Stops the metric clients. */
	private void stopMetricClients() {
		if (this.metric_clients != null)
			for (int i = 0; i < this.metric_clients.size(); i++)
				this.metric_clients.get(i).stopWorking();
	}

	/** Stops the agents. */
	private void stopAgents() {
		if (this.agents != null)
			for (Agent agent : this.agents)
				agent.stopWorking();
	}

	public void run() {
		// starts its TCP connection
		this.CONNECTION.start();

		// configures the environment of the simulation
		// and obtains the socket numbers for each agent
		StringAndInt[] agents_socket_numbers = new StringAndInt[0];
		try {
			agents_socket_numbers = this.configureEnvironment();
		} catch (IOException e) {
			e.printStackTrace();
		}

		// configures the metrics of the simulation
		// and obtains the socket numbers for each metric client
		int[] metrics_socket_numbers = new int[0];
		try {
			metrics_socket_numbers = this.configureMetrics();
		} catch (IOException e) {
			e.printStackTrace();
		}

		// creates, connects and starts the metric clients
		try {
			this.createAndStartMetricClients(metrics_socket_numbers);
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
			this.createAndStartLogClient(log_socket_number);
		} catch (IOException e2) {
			e2.printStackTrace();
		}

		// creates, connects and starts the agents
		// asks if the client should itself create and start the agents
		System.out
				.println("Should I myself create and start the agent clients? [y]es or [n]o?");
		String key = "";
		try {
			key = Keyboard.readLine();
		} catch (IOException e1) {
			e1.printStackTrace();
		}

		if (key.equalsIgnoreCase("y")) {
			try {
				String[] agents_ids = new String[agents_socket_numbers.length];
				int[] socket_numbers = new int[agents_socket_numbers.length];
				for (int i = 0; i < socket_numbers.length; i++) {
					agents_ids[i] = agents_socket_numbers[i].STRING;
					socket_numbers[i] = agents_socket_numbers[i].INTEGER;
				}

				this.createAndStartAgents(agents_ids, socket_numbers);
			} catch (IOException e) {
				e.printStackTrace();
			}
		} else
			for (int i = 0; i < agents_socket_numbers.length; i++) {
				System.out
						.println("SimPatrol is offering the following configuration: ");
				System.out.println("Agent ID: "
						+ agents_socket_numbers[i].STRING);
				System.out.println("Port    : "
						+ agents_socket_numbers[i].INTEGER);
			}

		// configures the simulation to start
		try {
			this.configureStart();
		} catch (IOException e) {
			e.printStackTrace();
		}

		// while the TCP connection is alive, waits...
		while (this.CONNECTION.getState() != Thread.State.TERMINATED){
			/*try {
				Thread.sleep(1);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}*/
		}

		// stops the agents
		this.stopAgents();

		// stops the metric clients
		this.stopMetricClients();

		// stops the log client
		if (this.log_client != null)
			this.log_client.stopWorking();

		// screen message
		System.out.println("Finished working.");
	}

	/**
	 * Creates and starts the agents, given the numbers of the sockets for each
	 * agent.
	 * 
	 * @param agents_ids
	 *            The ids of the agents to create and start.
	 * @param socket_numbers
	 *            The socket numbers offered by the server to connect to the
	 *            remote agents.
	 * @throws IOException
	 */
	protected abstract void createAndStartAgents(String[] agents_ids,
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
