/* ClientConnection.java */

/* The package of this class. */
package util.net;

/* Imported classes and/or interfaces. */
import java.io.IOException;
import util.Queue;

/** Implements a client of a remote connection. */
public abstract class ClientConnection extends Thread {
	/* Attributes. */
	/** Registers if the connection shall stop working. */
	protected boolean stop_working;

	/**
	 * The buffer where the connection writes the received messages.
	 */
	protected final Queue<String> BUFFER;

	/* Methods. */
	/** Constructor. */
	public ClientConnection() {
		this.stop_working = false;
		this.BUFFER = new Queue<String>();
	}

	/**
	 * Returns the content of the buffer of the connection and clears it.
	 */
	public String[] getBufferAndFlush() {
		String[] answer = new String[this.BUFFER.getSize()];

		for (int i = 0; i < answer.length; i++)
			answer[i] = this.BUFFER.remove();

		return answer;
	}

	/**
	 * Indicates that the connection must stop working.
	 * 
	 * @throws IOException
	 */
	public void stopWorking() throws IOException {
		this.stop_working = true;
	}

	/**
	 * Sends a given string message to the remote contact.
	 * 
	 * @param message
	 *            The string message to be sent.
	 * @throws IOException
	 */
	public abstract void send(String message) throws IOException;

	/**
	 * Implements the receiving of a message.
	 * 
	 * @throws IOException
	 */
	protected abstract void receive() throws IOException;
}
