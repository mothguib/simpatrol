/* AgentUDPConnection.java */

/* The package of this class. */
package view.connection;

/* Imported classes and/or interfaces. */
import java.io.IOException;
import control.simulator.RealTimeSimulator;
import util.data_structures.Queue;

/**
 * Implements the UDP connections with the external agents. Used by real time
 * simulators.
 * 
 * @see RealTimeSimulator
 */
public final class AgentUDPConnection extends UDPConnection {
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
	public AgentUDPConnection(String name, Queue<String> perception_buffer,
			Queue<String> action_buffer) {
		super(name, perception_buffer);
		this.PERCEPTION_BUFFER = this.BUFFER;
		this.ACTION_BUFFER = action_buffer;
	}

	public void run() {
		while (this.is_active) {
			String message = null;

			try {
				message = this.socket.receive();
			} catch (IOException e) {
				e.printStackTrace(); // traced IO exception
			}

			if (message != null) {
				// decides if the message is a perception or action
				// related message
				if (message.indexOf("<action ") > -1)
					this.ACTION_BUFFER.insert(message);
				else
					this.PERCEPTION_BUFFER.insert(message);
			}
		}
	}
}