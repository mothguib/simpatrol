/* CycledReactiveWithFlagsAgent.java */

/* The package of this class. */
package reactive_with_flags;

/* Imported classes and/or interfaces. */
import java.io.IOException;
import java.net.UnknownHostException;
import util.net.TCPClientConnection;

/** Implements the reactive with flags agents, as it is described
 *  in the work of [MACHADO, 2002], for a cycled simulator. */
public class CycledReactiveWithFlagsAgent extends ReactiveWithFlagsAgent {
	/* Methods. */
	/** Constructor.
	 * 
	 *  @param remote_socket_address The IP address of the SimPatrol server.
	 *  @param remote_socket_number The number of the socket that the server listens to, related to this agent. 
	 *  @throws IOException 
	 *  @throws UnknownHostException */
	public CycledReactiveWithFlagsAgent(String remote_socket_address, int remote_socket_number) throws UnknownHostException, IOException {
		super();
		this.connection = new TCPClientConnection(remote_socket_address, remote_socket_number);
	}
}
