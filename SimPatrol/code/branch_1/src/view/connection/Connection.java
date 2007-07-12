/* Connection.java */

/* The package of this class. */
package view.connection;

/* Imported classes and/or interfaces. */
import java.io.IOException;
import java.net.SocketException;
import util.Queue;
import util.udp.UDPSocket;

/** Implements the active connections of SimPatrol. */
public class Connection extends Thread {
	/* Attributes. */
	/** Registers if the connection shall stop working. */
	protected boolean stop_working;
	
	/** The UDP socket of the connection. */
	protected UDPSocket socket;
	
	/** The buffer where the connection writes the received
	 *  messages. */
	protected Queue<String> buffer;
	
	/* Methods. */
	/** Constructor.
	 * 
	 *  @param name The name of the thread of the connection.
	 *  @param buffer The buffer where the connection writes the received messages. */
	public Connection(String name, Queue<String> buffer) {
		super(name);
		this.stop_working = false;
		this.socket = null;
		this.buffer = buffer;
	}

	/** Indicates that the connection must stop working. */
	public void stopWorking() {
		this.stop_working = true;
	}
	
	/** Sends a given string message to the last remote contact.
	 * 
	 *  @param message The string message to be sent. 
	 *  @throws IOException */
	public void send(String message) throws IOException {
		if(this.socket != null)
			this.socket.send(message);
	}
	
	/** Sends a given string message.
	 * 
	 *  @param message The string message to be sent.
	 *  @param remote_socket_address The remote IP address of the receiver.
	 *  @param remote_socket_number The number of the UDP socket of the receiver. 
	 *  @throws IOException */
	public void send(String message, String remote_socket_address, int remote_socket_number) throws IOException {
		if(this.socket != null)
			this.socket.send(message, remote_socket_address, remote_socket_number);
	}
	
	/** Return the number of UDP socket connection.
	 * 
	 *  @return The number of the UDP socket, if previously created; -1 if not. */
	public int getUDPSocketNumber() {
		if(this.socket != null)
			return this.socket.getSocketNumber();
		else return -1;
	}
	
	/** Starts the work of the connection.
	 * 
	 *  @param The number of the UDP socket.
	 *  @throws SocketException */
	public void start(int local_socket_number) throws SocketException {
		this.socket = new UDPSocket(local_socket_number);
		super.start();
	}
	
	/** Give preference to use this.start(int local_socket_number).
	 * 
	 *  @deprecated */
	public void start() {
		super.start();
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