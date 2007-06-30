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
	/** Constructor. */
	public Daemon() {
		this.buffer = new Queue<String>();
		this.connection = new Connection(this.buffer);		
	}
	
	/** Returns the number of the local UDP socket
	 *  of the connection of the Daemon.
	 *  @return the number of the local UDP socket. */
	public int getUDPSocketNumber() {
		return this.connection.getUDPSocketNumber();
	}
	
	/** Returns the buffer listened by the daemon.
	 *  @return The buffer shared by the daemon with the connection, for the exchange of messages. */
	public Queue<String> getBuffer() {
		return this.buffer;
	}
	
	/** Starts the work of the daemon.
	 *  @param local_socket_number The number of the local UDP socket. 
	 *  @throws SocketException */
	public void start(int local_socket_number) throws SocketException {
		if(!this.connection.isAlive()) this.connection.start(local_socket_number);
		super.start();
	}
	
	/** Give preference to use this.start(int local_socket_number) */
	@Deprecated
	public void start() {
		super.start();
	}
}