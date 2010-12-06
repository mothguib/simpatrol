/* UDPConnection.java */

/* The package of this class. */
package view.connection;

/* Imported classes and/or interfaces. */
import java.io.IOException;
import util.data_structures.Queue;
import util.net.UDPSocket;

/** Implements the active UDP connections of SimPatrol. */
public class UDPConnection extends Connection {
	/* Attributes. */
	/** The UDP socket of the connection. */
	protected UDPSocket socket;

	
	
	/* Methods. */
	/**
	 * Constructor.
	 * 
	 * @param name
	 *            The name of the thread of the connection.
	 * @param buffer
	 *            The buffer where the connection writes the received messages.
	 */
	public UDPConnection(String name, Queue<String> buffer) {
		super(name, buffer);
		this.socket = null;
	}

	public boolean send(String message) {
		if (this.socket != null)
			try {
				return this.socket.send(message);
			} catch (IOException e) {
				e.printStackTrace(); // traced IO error
				return false;
			}

		return false;
	}

	public int getSocketNumber() {
		if (this.socket != null)
			return this.socket.getSocketNumber();
		else
			return -1;
	}

	public void start(int local_socket_number) throws IOException {
		this.socket = new UDPSocket(local_socket_number);
		super.start(local_socket_number);
	}

	public void stopActing() {
		// used by AspectJ
		super.stopActing();
	}

	public void run() {
		while (this.is_active) {
			String message = null;

			try {
				message = this.socket.receive();
			} catch (IOException e) {
				e.printStackTrace(); // traced IO exception
			}

			if (message != null && this.BUFFER != null){
				this.BUFFER.insert(message);
				this.updateObservers();
			}
		}
	}
}