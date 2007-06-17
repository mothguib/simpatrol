/* Connection.java */

/* The package of this class. */
package view.connection;

/* Imported classes and/or interfaces. */
import java.io.IOException;
import java.net.SocketException;

import util.Queue;
import util.UDPSocket;

/** Implements the active connections of SimPatrol. */
public class Connection extends Thread {
	/* Attributes. */
	/** Registers if the agent shall stop working. */
	private boolean stop_working;
	
	/** The UDP socket of the connection. */
	private UDPSocket socket;
	
	/** The buffer where the connection writes the received
	 *  messages. */
	private Queue<String> buffer;
	
	/* Methods. */
	/** Constructor.
	 *  @param local_socket_number The number of the local UDP socket.
	 *  @param buffer The buffer where the connection writes the received messages.
	 *  @throws SocketException */
	public Connection(int local_socket_number, Queue<String> buffer) throws SocketException {
		this.socket = new UDPSocket(local_socket_number);
		this.buffer = buffer;
		this.stop_working = false;
	}

	/** Indicates that the connection must stop working. */
	public void stopWorking() {
		this.stop_working = true;
	}
	
	/** Sends a given string message to the last remote contact.
	 *  @param message The string message to be sent. 
	 *  @throws IOException */
	public void send(String message) throws IOException {
		this.socket.send(message);
	}
	
	/** Sends a given string message.
	 *  @param message The string message to be sent.
	 *  @param remote_socket_address The remote IP address of the receiver.
	 *  @param remote_socket_number The number of the UDP socket of the receiver. 
	 *  @throws IOException */
	public void send(String message, String remote_socket_address, int remote_socket_number) throws IOException {
		this.socket.send(message, remote_socket_address, remote_socket_number);
	}
	
	public void run() {
		while(!this.stop_working) {
			String message = null;
			
			try {
				message = this.socket.receive();
			}
			catch (IOException e) {
				e.printStackTrace();
			}
			
			this.buffer.insert(message);
		}
	}
}