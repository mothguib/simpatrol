/* ServerSideTCPConnection.java */

/* The package of this class. */
package view.connection;

/* Imported classes and/or interfaces. */
import java.io.EOFException;
import java.io.IOException;
import util.Queue;
import util.net.ServerSideTCPSocket;
import control.daemon.MainDaemon;

/** Implements the active TCP connection of the main daemon
 *  of SimPatrol.
 *  
 *  @see MainDaemon */
public class ServerSideTCPConnection extends Thread {
	/* Attributes. */
	/** Registers if the connection shall stop working. */
	private boolean stop_working;
	
	/** The TCP socket of the connection. */
	private ServerSideTCPSocket socket;
	
	/** The buffer where the connection writes the received
	 *  messages. */
	private Queue<String> buffer;
	
	/* Methods. */
	/** Constructor.
	 * 
	 *  @param name The name of the thread of the connection.
	 *  @param buffer The buffer where the connection writes the received messages. */
	public ServerSideTCPConnection(String name, Queue<String> buffer) {
		super(name);
		this.stop_working = false;
		this.socket = null;
		this.buffer = buffer;
	}
	
	/** Indicates that the connection must stop working. */
	public void stopWorking() {
		this.stop_working = true;
		
		// screen message
		System.out.println("[SimPatrol.TCPConnection(" + this.getName() + ")]: Stopped listening to messages.");
	}
	
	/** Sends a given string message to the remote client.
	 * 
	 *  @param message The string message to be sent.
	 *  @return TRUE if the message was successfully sent, FALSE if not. 
	 *  @throws IOException */
	public boolean send(String message) throws IOException {
		if(this.socket != null) {
			// screen message
			System.out.println("[SimPatrol.TCPConnection(" + this.getName() + ")]: Sent message.");
			
			return this.socket.send(message);
		}
		
		return false;
	}
	
	/** Starts the work of the connection.
	 * 
	 *  @param The number of the TCP socket. 
	 *  @throws IOException */
	public void start(int local_socket_number) throws IOException {
		this.socket = new ServerSideTCPSocket(local_socket_number);
		super.start();
		
		// screen message
		System.out.println("[SimPatrol.TCPConnection(" + this.getName() + ")]: Started listening to messages.");
	}
	
	/** Give preference to use this.start(int local_socket_number).
	 * 
	 *  @deprecated */
	public void start() {
		super.start();
	}
	
	public void run() {
		while(!this.stop_working) {
			try {
				// establishes the TCP connection
				this.socket.connect();
				
				// screen message
				System.out.println("[SimPatrol.TCPConnection(" + this.getName() + ")]: Client connected.");
				
				// reads the eventual sent messages, while the connection
				// is supposed to work
				while(!this.stop_working) {
					String message = null;
					
					try { message = this.socket.receive(); }
					catch (ClassNotFoundException e) { e.printStackTrace(); }
					
					this.buffer.insert(message);
					
					// screen message
					System.out.println("[SimPatrol.TCPConnection(" + this.getName() + ")]: Received message.");
				}
				
				// disconnects
				this.socket.disconnect();
				
				// screen message
				System.out.println("[SimPatrol.TCPConnection(" + this.getName() + ")]: Server disconnected client.");
			}
			catch(EOFException e1) {
				// disconnects
				try { this.socket.disconnect(); }
				catch (IOException e) { e.printStackTrace(); }
				
				// screen message
				System.out.println("[SimPatrol.TCPConnection(" + this.getName() + ")]: Client disconnected.");
			}
			catch(IOException e2) {
				e2.printStackTrace();
			}
		}
	}
}