/* ServerSideTCPConnection.java */

/* The package of this class. */
package view.connection;

/* Imported classes and/or interfaces. */
import java.io.IOException;
import util.data_structures.Queue;
import util.net.ServerSideTCPSocket;
import control.daemon.MainDaemon;

/**
 * Implements the active TCP connection of the main daemon of SimPatrol.
 * 
 * @see MainDaemon
 */
public class ServerSideTCPConnection extends Connection {
	/* Attributes. */
	/** The TCP socket of the connection. */
	protected ServerSideTCPSocket socket;

	/* Methods. */
	/**
	 * Constructor.
	 * 
	 * @param name
	 *            The name of the thread of the connection.
	 * @param buffer
	 *            The buffer where the connection writes the received messages.
	 */
	public ServerSideTCPConnection(String name, Queue<String> buffer) {
		super(name, buffer);
		this.socket = null;
	}

	/**
	 * Verifies if this TCP connection is established with another remote one.
	 * 
	 * @return TRUE if the connection is established, FALSE if not.
	 */
	public boolean isConnected() {
		if (this.socket != null)
			return this.socket.isConnected();
		else
			return false;
	}

	/**
	 * Resets this TCP connection, in the case of the remote contact being down.
	 * 
	 * @throws IOException
	 */
	protected void reset() throws IOException {
		// 1. disconnects the TCP socket...
		this.socket.disconnect();

		// 2. resets it
		this.socket = new ServerSideTCPSocket(this.socket.getSocketNumber());
	}

	public boolean send(String message) {
		if (this.socket != null)
			return this.socket.send(message);

		return false;
	}

	public int getSocketNumber() {
		if (this.socket != null)
			return this.socket.getSocketNumber();
		else
			return -1;
	}

	public void start(int local_socket_number) throws IOException {
		this.socket = new ServerSideTCPSocket(local_socket_number);
		super.start(local_socket_number);
	}

	public void stopActing() {
		// used by AspectJ
		super.stopActing();
	}

	public void run() {
		// while the connection is supposed to work
		while (this.is_active) {
			// tries to establish the TCP connection, while it is supposed to
			// work
			boolean connected = false;
			while (this.is_active && !connected)
				try {
					connected = this.socket.connect();
				} catch (IOException e1) {
					connected = false;
					e1.printStackTrace(); // traced IO exception
				}

			// reads the eventual sent messages, while the connection
			// is supposed to work
			boolean client_disconnected = false;
			while (this.is_active && !client_disconnected) {
				try {
					String message;
					message = this.socket.receive();					
					if (message != null)
						this.BUFFER.insert(message);
				} catch (IOException e) {
					// the client probably disconnected
					client_disconnected = true;

					// resets the TCP socket
					try {
						this.reset();
					} catch (IOException e1) {
						e1.printStackTrace(); // traced IO Exception
					}
				}
			}

			// if the client didn't disconnect, disconnects it
			if (!client_disconnected)
				// disconnects the TCP connection
				try {
					this.socket.disconnect();
				} catch (IOException e) {
					e.printStackTrace(); // traced IO Exception
				}
		}
	}
}