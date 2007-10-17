/* UDPConnection.java */

/* The package of this class. */
package view.connection;

/* Imported classes and/or interfaces. */
import java.io.IOException;
import util.Queue;
import util.net.UDPSocket;

/** Implements the active UDP connections of SimPatrol. */
public class UDPConnection extends Connection {
	/* Attributes. */
	/** The UDP socket of the connection. */
	protected UDPSocket socket;
	
	/* Methods. */
	/** Constructor.
	 * 
	 *  @param name The name of the thread of the connection.
	 *  @param buffer The buffer where the connection writes the received messages. */
	public UDPConnection(String name, Queue<String> buffer) {
		super(name, buffer);
		this.socket = null;
	}
	
	public boolean send(String message) throws IOException {
		if(this.socket != null)
			return this.socket.send(message);
		
		return false;
	}
	
	public int getSocketNumber() {
		if(this.socket != null)
			return this.socket.getSocketNumber();
		else return -1;
	}
	
	public void start(int local_socket_number) throws IOException {
		this.socket = new UDPSocket(local_socket_number);
		super.start(local_socket_number);
		
		// screen message
		System.out.println("[SimPatrol.UDPConnection(" + this.getName() + ")]: Started listening to messages.");
	}
	
	public void stopWorking() {
		super.stopWorking();
		
		// screen message
		System.out.println("[SimPatrol.UDPConnection(" + this.getName() + ")]: Stopped listening to messages.");
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