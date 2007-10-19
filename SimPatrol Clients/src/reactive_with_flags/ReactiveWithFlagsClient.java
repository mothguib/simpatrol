/* ReactiveWithFlagsClient.java */

/* The package of this class. */
package reactive_with_flags;

/* Imported classes and/or interfaces. */
import java.io.IOException;
import java.net.UnknownHostException;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Set;
import metric_clients.MetricFileClient;
import util.file.FileReader;
import util.net.TCPClientConnection;

/** Implements a client that connects to the SimPatrol server
 *  and configures it, letting reactive with flags agent clients
 *  connect to it, in the sequence. */
public class ReactiveWithFlagsClient extends Thread {
	/* Attributes. */
	/** The path of the file that contains the environment. */
	private final String ENVIRONMENT_FILE_PATH;
	
	/** The path of the files that will save the collected metrics. */
	private final String[] METRICS_FILE_PATHS;
	
	/** The time of the simulation. */
	private final int TIME_OF_SIMULATION;
	
	/** Holds if the simulator is a real time one. */
	private final boolean IS_REAL_TIME_SIMULATOR;
	
	/** The TCP connection with the server. */
	private TCPClientConnection connection;
	
	/** The set of conscientious reactive agents acting in the simulation. */
	private Set<ReactiveWithFlagsAgent> agents;
	
	/** The metric collector clients added to the simulation. */
	private MetricFileClient[] metric_clients;
	
	/* Methods. */
	/** Constructor.
	 * 
	 *  @param remote_socket_address The IP address of the SimPatrol server.
	 *  @param remote_socket_number The number of the socket that the server is supposed to listen to this client.
	 *  @param environment_file_path The path of the file that contains the environment.
	 *  @param metrics_file_paths The paths of the files that will save the collected metrics:
	 *     index 0. The file that will save the mean instantaneous idlenesses;
	 *     index 1. The file that will save the max instantaneous idlenesses;
	 *     index 2. The file that will save the mean idlenesses;
	 *     index 3. The file that will save the max idlenesses;
	 *  @param time_of_simulation The time of simulation.
	 *  @param is_real_time_simulator TRUE if the simulator is a real time one, FALSE if not. 
	 *        
	 *  @throws IOException 
	 *  @throws UnknownHostException */  
	public ReactiveWithFlagsClient(String remote_socket_address, int remote_socket_number, String environment_file_path, String[] metrics_file_paths, int time_of_simulation, boolean is_real_time_simulator) throws UnknownHostException, IOException {
		this.connection = new TCPClientConnection(remote_socket_address, remote_socket_number);
		this.ENVIRONMENT_FILE_PATH = environment_file_path;
		this.METRICS_FILE_PATHS = metrics_file_paths;
		this.TIME_OF_SIMULATION = time_of_simulation;
		this.IS_REAL_TIME_SIMULATOR = is_real_time_simulator;
	}
	
	/** Obtains the environment from the referred file and configures it
	 *  into the server, returning the activated sockets for the remote agents.
	 *  
	 *  @return The socket numbers for the agents' connections.
	 *  @throws IOException */
	private int[] configureEnvironment() throws IOException {
		// the file reader that will obtain the environment from the given file
		FileReader file_reader = new FileReader(this.ENVIRONMENT_FILE_PATH);
		
		// holds the environment obtained from the file
		StringBuffer buffer = new StringBuffer();
		while(!file_reader.isEndOfFile()) {
			buffer.append(file_reader.readLine());
		}
		
		// closes the read file
		file_reader.close();
		
		// mounts the message of configuration
		String message = "<configuration type=\"0\">" + buffer.toString() + "</configuration>";
		
		// sends it to the server
		this.connection.send(message);
		
		// obtains the answer from the server
		String[] server_answer = this.connection.getBufferAndFlush();
		while(server_answer.length == 0)
			server_answer = this.connection.getBufferAndFlush();
		
		// from the answer, obtains the sockets activated for each agent
		LinkedList<Integer> socket_numbers = new LinkedList<Integer>();
		String ids_and_sockets = server_answer[0];
		int next_socket_index = ids_and_sockets.indexOf("socket=\"");
		while(next_socket_index > -1) {
			ids_and_sockets = ids_and_sockets.substring(next_socket_index + 8);
			String current_socket_str = ids_and_sockets.substring(0, ids_and_sockets.indexOf("\""));
			socket_numbers.add(new Integer(current_socket_str));
			next_socket_index = ids_and_sockets.indexOf("socket=\"");
		}
		
		// mounts the answer of the method
		int[] answer = new int[socket_numbers.size()];
		for(int i = 0; i < answer.length; i++)
			answer[i] = socket_numbers.get(i).intValue();
		
		// returns the answer
		return answer;
	}
	
	/** Creates and starts the conscientious reactive agents, given
	 *  the numbers of the sockets for each agent.
	 *   
	 *  @param socket_numbers The socket numbers offered by the server to connect to the remote agents.
	 *  @throws IOException */
	private void createAndStartAgents(int[] socket_numbers) throws IOException {
		this.agents = new HashSet<ReactiveWithFlagsAgent>();
		
		for(int i = 0; i < socket_numbers.length; i++) {
			ReactiveWithFlagsAgent agent = null;
			
			if(this.IS_REAL_TIME_SIMULATOR)
				agent = new RealTimeReactiveWithFlagsAgent(this.connection.getRemoteSocketAdress(), socket_numbers[i]);
			else
				agent = new CycledReactiveWithFlagsAgent(this.connection.getRemoteSocketAdress(), socket_numbers[i]);
			
			this.agents.add(agent);
			agent.start();
		}
	}
	
	/** Configures the metrics to be collected during the simulation into
	 *  the server.
	 *  
	 *  @return The socket numbers for the metric clients be connected.  
	 *  @throws IOException */
	private int[] configureMetrics() throws IOException {
		// the answer for the method
		int[] answer = new int[4];
		
		// the message to create a metric that collects the
		// mean instantaneous idlenesses of the environment
		String message = "<configuration type=\"2\" parameter=\"10\"><metric type=\"0\"/></configuration>";
		
		// sends it to the server
		this.connection.send(message);
		
		// obtains the answer from the server
		String[] server_answer = this.connection.getBufferAndFlush();
		while(server_answer.length == 0)
			server_answer = this.connection.getBufferAndFlush();
		
		// adds it to the answer of the method
		int metric_socket_index = server_answer[0].indexOf("message=\"");
		server_answer[0] = server_answer[0].substring(metric_socket_index + 9);
		answer[0] = Integer.parseInt(server_answer[0].substring(0, server_answer[0].indexOf("\"")));
		
		// the message to create a metric that collects the
		// max instantaneous idlenesses of the environment
		message = "<configuration type=\"2\" parameter=\"5\"><metric type=\"1\"/></configuration>";			
		
		// sends it to the server
		this.connection.send(message);
		
		// obtains the answer from the server
		server_answer = this.connection.getBufferAndFlush();
		while(server_answer.length == 0)
			server_answer = this.connection.getBufferAndFlush();
		
		// adds it to the answer of the method
		metric_socket_index = server_answer[0].indexOf("message=\"");
		server_answer[0] = server_answer[0].substring(metric_socket_index + 9);
		answer[1] = Integer.parseInt(server_answer[0].substring(0, server_answer[0].indexOf("\"")));
		
		// the message to create a metric that collects the
		// mean idleness of the environment
		message = "<configuration type=\"2\" parameter=\"5\"><metric type=\"2\"/></configuration>";			
		
		// sends it to the server
		this.connection.send(message);
		
		// obtains the answer from the server
		server_answer = this.connection.getBufferAndFlush();
		while(server_answer.length == 0)
			server_answer = this.connection.getBufferAndFlush();
		
		// adds it to the answer of the method
		metric_socket_index = server_answer[0].indexOf("message=\"");
		server_answer[0] = server_answer[0].substring(metric_socket_index + 9);
		answer[2] = Integer.parseInt(server_answer[0].substring(0, server_answer[0].indexOf("\"")));
		
		// the message to create a metric that collects the
		// max idleness of the environment
		message = "<configuration type=\"2\" parameter=\"5\"><metric type=\"3\"/></configuration>";			
		
		// sends it to the server
		this.connection.send(message);
		
		// obtains the answer from the server
		server_answer = this.connection.getBufferAndFlush();
		while(server_answer.length == 0)
			server_answer = this.connection.getBufferAndFlush();
		
		// adds it to the answer of the method
		metric_socket_index = server_answer[0].indexOf("message=\"");
		server_answer[0] = server_answer[0].substring(metric_socket_index + 9);
		answer[3] = Integer.parseInt(server_answer[0].substring(0, server_answer[0].indexOf("\"")));
		
		// returns the answer
		return answer;
	}
	
	/** Creates and starts the metric clients, given
	 *  the numbers of the sockets for each client.
	 *  
	 *  @param socket_numbers The socket numbers offered by the server to connect to the remote agents. 
	 *  @throws IOException */
	private void createAndStartMetricClients(int[] socket_numbers) throws IOException {
		this.metric_clients = new MetricFileClient[4];
		
		this.metric_clients[0] = new MetricFileClient(this.connection.getRemoteSocketAdress(), socket_numbers[0], this.METRICS_FILE_PATHS[0], "Mean instantaneous idlenesses");
		this.metric_clients[0].start();
		
		this.metric_clients[1] = new MetricFileClient(this.connection.getRemoteSocketAdress(), socket_numbers[1], this.METRICS_FILE_PATHS[1], "Max instantaneous idlenesses");
		this.metric_clients[1].start();
		
		this.metric_clients[2] = new MetricFileClient(this.connection.getRemoteSocketAdress(), socket_numbers[2], this.METRICS_FILE_PATHS[2], "Mean idlenesses");
		this.metric_clients[2].start();
		
		this.metric_clients[3] = new MetricFileClient(this.connection.getRemoteSocketAdress(), socket_numbers[3], this.METRICS_FILE_PATHS[3], "Max idlenesses");
		this.metric_clients[3].start();
	}
	
	/** Configures the server to start the simulation with the given time
	 *  of duration.
	 *    
	 *  @throws IOException */
	private void configureStart() throws IOException {
		// the message to be sent to the server
		String message = "<configuration type=\"3\" parameter=\"" + this.TIME_OF_SIMULATION + "\"/>";
		
		// send the message to the server
		this.connection.send(message);		
	}
	
	/** Stops the agents. */
	private void stopAgents() {
		Object[] agents_array = this.agents.toArray();
		for(int i = 0; i < agents_array.length; i++)
			((ReactiveWithFlagsAgent) agents_array[i]).stopWorking();
	}
	
	/** Stops the metric clients. */
	private void stopMetricClients() {
		for(int i = 0; i < this.metric_clients.length; i++)
			this.metric_clients[i].stopWorking();		
	}
	
	public void run() {
		// starts its TCP connection
		this.connection.start();
		
		// configures the environment of the simulation
		// and obtains the socket numbers for each agent
		int[] agents_socket_numbers = new int[0];
		try { agents_socket_numbers = this.configureEnvironment(); }
		catch (IOException e) { e.printStackTrace(); }
		
		// creates, connects and starts the agents
		try { this.createAndStartAgents(agents_socket_numbers); }
		catch (IOException e) { e.printStackTrace(); }
		
		// configures the metrics of the simulation
		// and obtains the socket numbers for each metric client
		int[] metrics_socket_numbers = new int[0];
		try { metrics_socket_numbers = this.configureMetrics(); }
		catch (IOException e) { e.printStackTrace(); }
		
		// creates, connects and starts the metric clients
		try { this.createAndStartMetricClients(metrics_socket_numbers); }
		catch (IOException e) { e.printStackTrace(); }
		
		// configures the simulation to start
		try { this.configureStart(); }
		catch (IOException e) { e.printStackTrace(); }
		
		// while the received orientation doesn't contains the
		// "Simulation ended." string, keep online
		boolean end_signal_found = false;
		while(!end_signal_found) {
			String[] orientations = this.connection.getBufferAndFlush();
			
			if(orientations.length > 0) {
				for(int i = 0; i < orientations.length; i++) {
					String current_message = orientations[i];
					if(current_message.indexOf("Simulation ended.") > -1) {
						end_signal_found = true;
						break;	
					}
				}	
			}
		}
		
		// stops its connection
		try { this.connection.stopWorking(); }
		catch (IOException e) { e.printStackTrace(); }
		
		// stops the agents
		this.stopAgents();
		
		// stops the metric clients
		this.stopMetricClients();
		
		// system exit
		System.exit(0);
	}	
}
