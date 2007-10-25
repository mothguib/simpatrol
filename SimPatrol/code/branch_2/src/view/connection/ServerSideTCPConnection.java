/* ServerSideTCPConnection.java */

/* The package of this class. */
package view.connection;

/* Imported classes and/or interfaces. */
import java.io.IOException;
import java.net.SocketException;
import util.Queue;
import util.net.ServerSideTCPSocket;
import control.daemon.MainDaemon;

/** Implements the active TCP connection of the main daemon
 *  of SimPatrol.
 *  
 *  @see MainDaemon */
public class ServerSideTCPConnection extends Connection {
	/* Attributes. */
	/** The TCP socket of the connection. */
	protected ServerSideTCPSocket socket;
	
	/* Methods. */
	/** Constructor.
	 * 
	 *  @param name The name of the thread of the connection.
	 *  @param buffer The buffer where the connection writes the received messages. */
	public ServerSideTCPConnection(String name, Queue<String> buffer) {
		super(name, buffer);
		this.socket = null;
	}
	
	public boolean send(String message) throws IOException {
		if(this.socket != null)
			return this.socket.send(message);
		
		return false;
	}
	
	public int getSocketNumber() {
		return this.socket.getSocketNumber();
	}
	
	public void start(int local_socket_number) throws IOException {
		this.socket = new ServerSideTCPSocket(local_socket_number);
		super.start(local_socket_number);
		
		// screen message
		System.out.println("[SimPatrol.TCPConnection(" + this.getName() + ")]: Started listening to messages.");
	}
	
	public void stopWorking() throws IOException {
		if(!this.stop_working) {
			super.stopWorking();
			
			// disconnects the remote client
			this.socket.disconnect();
			
			// screen message
			System.out.println("[SimPatrol.TCPConnection(" + this.getName() + ")]: Server disconnected client.");
				
			// screen message
			System.out.println("[SimPatrol.TCPConnection(" + this.getName() + ")]: Stopped listening to messages. ");
		}
	}
	
	public void run() {
		try {
			// establishes the TCP connection
			this.socket.connect();
			
			// screen message
			System.out.println("[SimPatrol.TCPConnection(" + this.getName() + ")]: Client connected.");
			
			// reads the eventual sent messages, while the connection
			// is supposed to work
			while(!this.stop_working) {
				String message = this.socket.receive();					
				
				if(message != null)
					this.BUFFER.insert(message);
			}
		}
		catch(SocketException e1) {
			if(!this.stop_working) {
				// registers that the connection must stop working
				this.stop_working = true;
				
				// disconnects
				try { this.socket.disconnect(); }
				catch (IOException e) { e.printStackTrace(); }
				
				// screen message
				System.out.println("[SimPatrol.TCPConnection(" + this.getName() + ")]: Client disconnected.");
				
				// screen message
				System.out.println("[SimPatrol.TCPConnection(" + this.getName() + ")]: Stopped listening to messages. ");			
			}
		}
		catch(IOException e2) {
			e2.printStackTrace();
		}
	}
}