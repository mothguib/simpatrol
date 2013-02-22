package agent_library.connections;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;


/** 
 * Implements a client of a remote connection. 
 */
public abstract class ClientConnection {
	private final List<String> receivedMessages;

	public ClientConnection() {
		this.receivedMessages = new LinkedList<String>();
	}
	

	public abstract void open() throws IOException;
	
	public abstract void close() throws IOException;

	
	/**
	 * Returns the content of the buffer of the connection and clears it.
	 */
	public String[] getBufferAndFlush() {
		String[] answer;

		synchronized (receivedMessages) {
			answer = new String[this.receivedMessages.size()];
			answer = receivedMessages.toArray(answer);
			receivedMessages.clear();
		}

		return answer;
	}
	
	protected void addReceivedMessage(String message) {
		synchronized (receivedMessages) {
			receivedMessages.add(message);
		}
	}

	/**
	 * Sends the given string message to the remote contact.
	 */
	public abstract void send(String message) throws IOException;

	
}

