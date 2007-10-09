/* AuxiliaryDaemon.java */

/* The package of this class. */
package control.daemon;

/* Imported classes and/or interfaces. */
import java.net.SocketException;
import view.connection.UDPConnection;

/** Implements the auxiliary daemons of SimPatrol. */
public abstract class AuxiliaryDaemon extends Daemon {
	/* Attributes. */
	/** The UDP connection used by the daemon to listen to
	 *  new messages, as well as attend them. */
	protected UDPConnection connection;
	
	/* Methods. */
	/** Constructor.
	 * 
	 * @param thread_name The name of the thread of the daemon. */
	public AuxiliaryDaemon(String thread_name) {
		super(thread_name);
		this.connection = new UDPConnection(thread_name + "'s connection", this.buffer);
	}
	
	/** Starts the work of the daemon.
	 * 
	 *  @param local_socket_number The number of the local UDP socket. 
	 *  @throws SocketException */
	@SuppressWarnings("deprecation")
	public void start(int local_socket_number) throws SocketException {
		if(!this.connection.isAlive()) this.connection.start(local_socket_number);
		this.start();
	}
	
	/** Stops the work of the daemon. */
	public void stopWorking() {
		super.stopWorking();
		this.connection.stopWorking();
	}
	
	/** Returns the number of the UDP socket connection.
	 * 
	 *  @return The number of the UDP socket connection. */
	public int getUDPSocketNumber() {
		return this.connection.getUDPSocketNumber();
	}
}
