/* Connection.java */

/* The package of this class. */
package view.connection;

/* Imported classes and/or interfaces. */
import java.io.IOException;
import util.Queue;

/**
 * The connections offered by SimPatrol in order to contact the remote actors.
 * 
 * @developer Subclasses of this one must use the attribute "stop_working" when
 *            implementing their run() method.
 */
public abstract class Connection extends Thread {
	/* Attributes. */
	/** Registers if the connection shall stop working. */
	protected boolean stop_working;

	/**
	 * The buffer where the connection writes the received messages.
	 */
	protected final Queue<String> BUFFER;

	/* Methods. */
	/**
	 * Constructor.
	 * 
	 * @param thread_name
	 *            The name of the thread of the connection.
	 * @param buffer
	 *            The buffer where the connection writes the received messages.
	 */
	public Connection(String thread_name, Queue<String> buffer) {
		super(thread_name);
		this.stop_working = false;
		this.BUFFER = buffer;
	}

	/**
	 * Returns if the connection will or is stopping work.
	 * 
	 * @return TRUE if the connection will or is stopping, FALSE if not.
	 */
	public boolean isStopWorking() {
		return this.stop_working;
	}

	/** Indicates that the connection must stop working. */
	public void stopWorking() throws IOException {
		this.stop_working = true;
	}

	/**
	 * Sends a given string message and returns the success of the sending.
	 * 
	 * @param message
	 *            The string message to be sent.
	 * @return TRUE if the message was successfully sent, FALSE if not.
	 * @throws IOException
	 */
	public abstract boolean send(String message) throws IOException;

	/**
	 * Returns the number of the socket (TCP or UDP) of the connection.
	 * 
	 * @return The number of the socket of the connection.
	 */
	public abstract int getSocketNumber();

	/**
	 * Starts the connection's work.
	 * 
	 * @param The
	 *            number of the socket of the connection.
	 * @throws IOException
	 */
	public void start(int local_socket_number) throws IOException {
		super.start();
	}

	/**
	 * Give preference to use this.start(int local_socket_number).
	 * 
	 * @deprecated
	 */
	public void start() {
		super.start();
	}
}
