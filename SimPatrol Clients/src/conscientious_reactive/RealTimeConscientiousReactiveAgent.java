/* RealTimeConscientiousReactiveAgent.java */

/* The package of this class. */
package conscientious_reactive;

/* Imported classes and/or interfaces. */
import java.net.SocketException;
import java.net.UnknownHostException;
import util.net.UDPClientConnection;

/**
 * Implements the conscientious reactive agents, as it is described in the work
 * of [MACHADO, 2002], for a real time simulator.
 */
public class RealTimeConscientiousReactiveAgent extends
		ConscientiousReactiveAgent {
	/* Methods. */
	/**
	 * Constructor.
	 * 
	 * @param remote_socket_address
	 *            The IP address of the SimPatrol server.
	 * @param remote_socket_number
	 *            The number of the socket that the server listens to, related
	 *            to this agent.
	 * @throws UnknownHostException
	 * @throws SocketException
	 */
	public RealTimeConscientiousReactiveAgent(String remote_socket_address,
			int remote_socket_number) throws SocketException,
			UnknownHostException {
		super();
		this.connection = new UDPClientConnection(remote_socket_address,
				remote_socket_number);
	}
}