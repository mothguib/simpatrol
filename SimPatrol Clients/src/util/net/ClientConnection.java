package util.net;

import java.io.IOException;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import util.Queue;


/** 
 * Implements a client of a remote connection. 
 */
public abstract class ClientConnection extends Thread {
	/* Attributes. */
	/** Registers if the connection shall stop working. */
	protected boolean stop_working;

	/**
	 * The buffer where the connection writes the received messages.
	 */
	protected final List<String> BUFFER;

	/* Methods. */
	/** Constructor. */
	public ClientConnection() {
		this.stop_working = false;
		this.BUFFER = Collections.synchronizedList(new LinkedList<String>());
	}
	
	/** Constructor. */
	public ClientConnection(String name) {
		super(name);
		this.stop_working = false;
		this.BUFFER = Collections.synchronizedList(new LinkedList<String>());
	}

	/**
	 * Returns the content of the buffer of the connection and clears it.
	 */
	public String[] getBufferAndFlush() {
		String[] answer;

		synchronized (BUFFER) {
			answer = new String[this.BUFFER.size()];
			answer = BUFFER.toArray(answer);
			BUFFER.clear();
		}

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
	 * @return 
	 * 
	 * @throws IOException
	 */
	protected abstract boolean receive() throws IOException;
}
