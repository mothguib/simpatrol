/* ServerSideAgentTCPConnection.java */

/* The package of this class. */
package view.connection;

/* Imported classes and/or interfaces. */
import java.io.IOException;
import control.simulator.CycledSimulator;
import util.data_structures.Queue;

/**
 * Implements the TCP connections with the external agents. Used by cycled
 * simulators.
 * 
 * @see CycledSimulator
 */
public final class ServerSideAgentTCPConnection extends ServerSideTCPConnection {
	/* Attributes. */
	/** The buffer where the connection writes the received perception messages. */
	private final Queue<String> PERCEPTION_BUFFER;

	/** The buffer where the connection writes the received action messages. */
	private final Queue<String> ACTION_BUFFER;

	/* Methods. */
	/**
	 * Constructor.
	 * 
	 * @param name
	 *            The name of the thread of the connection.
	 * @param perception_buffer
	 *            The buffer where the connection writes the received perception
	 *            messages.
	 * @param action_buffer
	 *            The buffer where the connection writes the received action
	 *            messages.
	 */
	public ServerSideAgentTCPConnection(String name,
			Queue<String> perception_buffer, Queue<String> action_buffer) {
		super(name, perception_buffer);
		this.PERCEPTION_BUFFER = this.BUFFER;
		this.ACTION_BUFFER = action_buffer;
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
					String message = this.socket.receive();

					if (message != null){
						// decides if the message is a perception or action
						// related message
						if (message.indexOf("<action ") > -1)
							this.ACTION_BUFFER.insert(message);
						else
							this.PERCEPTION_BUFFER.insert(message);
						
						this.updateObservers();
					}
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