/* AgentDaemon.java */

/* The package of this class. */
package control.daemon;

/* Imported classes and/or interfaces. */
import java.net.SocketException;
import model.agent.Agent;

/** Implements the daemons that control the agents of SimPatrol. */
public final class AgentDaemon extends Daemon {
	/* Attributes. */
	/** The internal agent controlled by the daemon. */
	private Agent agent;
	
	/* Methods. */
	/** Constructor.
	 * 	@param local_socket_number The number of the local UDP socket.
	 *  @param agent The agent controlled by the daemon.
	 *  @throws SocketException */
	public AgentDaemon(int local_socket_number, Agent agent) throws SocketException {
		super(local_socket_number);
		this.agent = agent;
	}
	
	public void run() {
		// screen message
		System.out.println("[SimPatrol.Agent(" + this.agent.getObjectId()+ ")Daemon] Listening to some message... ");
		
		// TODO continuar!!!
	}
}