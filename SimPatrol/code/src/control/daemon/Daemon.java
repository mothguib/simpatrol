/* Daemon.java */

/* The package of this class. */
package control.daemon;

/* Imported classes and/or interfaces. */
import java.net.SocketException;
import util.Queue;
import view.connection.Connection;

/** Implements the daemons of SimPatrol. */
public abstract class Daemon extends Thread {
	/* Attributes. */
	/** The connection used by the daemon to listen to
	 *  new messages, as well as attend them. */
	protected Connection connection;
	
	/** The buffer shared by the daemon with the connection, for the
	 *  exchange of messages. */
	protected Queue<String> buffer;
	
	/* Methods. */
	/** Constructor.
	 *  @param local_socket_number The number of the local UDP socket.
	 *  @throws SocketException */
	public Daemon(int local_socket_number) throws SocketException {
		this.buffer = new Queue<String>();
		this.connection = new Connection(local_socket_number, this.buffer);
	}
	
	public void start() {
		super.start();
		this.connection.start();
	}
	
}
