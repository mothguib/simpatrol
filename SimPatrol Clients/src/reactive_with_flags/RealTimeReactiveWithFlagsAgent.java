/* RealTimeReactiveWithFlagsAgent.java */

/* The package of this class. */
package reactive_with_flags;

/* Imported classes and/or interfaces. */
import java.net.SocketException;
import java.net.UnknownHostException;
import util.net.UDPClientConnection;

/**
 * Implements the reactive with flags agents, as it is described in the work of
 * [MACHADO, 2002], for a real time simulator.
 */
public class RealTimeReactiveWithFlagsAgent extends ReactiveWithFlagsAgent {
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
	public RealTimeReactiveWithFlagsAgent(String remote_socket_address,
			int remote_socket_number) throws SocketException,
			UnknownHostException {
		this.connection = new UDPClientConnection(remote_socket_address,
				remote_socket_number);
	}
}