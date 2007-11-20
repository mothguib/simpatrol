/* CycledHeuristicConscientiousReactiveAgent.java */

/* The package of this class. */
package heuristic_conscientious_reactive;

/* Imported classes and/or interfaces. */
import java.io.IOException;
import java.net.UnknownHostException;
import util.net.TCPClientConnection;

/**
 * Implements the heuristic conscientious reactive agents, as it is described in
 * the work of [ALMEIDA, 2003], for a cycled simulator.
 */
public class CycledHeuristicConscientiousReactiveAgent extends
		HeuristicConscientiousReactiveAgent {
	/* Methods. */
	/**
	 * Constructor.
	 * 
	 * @param remote_socket_address
	 *            The IP address of the SimPatrol server.
	 * @param remote_socket_number
	 *            The number of the socket that the server listens to, related
	 *            to this agent.
	 * @throws IOException
	 * @throws UnknownHostException
	 */
	public CycledHeuristicConscientiousReactiveAgent(
			String remote_socket_address, int remote_socket_number)
			throws UnknownHostException, IOException {
		this.connection = new TCPClientConnection(remote_socket_address,
				remote_socket_number);
	}
}