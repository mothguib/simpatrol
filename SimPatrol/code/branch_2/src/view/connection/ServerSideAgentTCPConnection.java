/* ServerSideAgentTCPConnection.java */

/* The package of this class. */
package view.connection;

/* Imported classes and/or interfaces. */
import java.io.IOException;
import java.net.SocketException;
import control.simulator.CycledSimulator;
import util.Queue;
import util.net.ServerSideTCPSocket;

/** Implements the TCP connections with the external agents.
 *  Used by cycled simulators. 
 *  
 *  @see CycledSimulator */
public final class ServerSideAgentTCPConnection extends ServerSideTCPConnection {
	/* Attributes. */
	/** The buffer where the connection writes the received
	 *  perception messages. */
	private final Queue<String> PERCEPTON_BUFFER;
	
	/** The buffer where the connection writes the received
	 *  action messages. */
	private final Queue<String> ACTION_BUFFER;
	
	/* Methods. */
	/** Constructor.
	 * 
	 *  @param name The name of the thread of the connection. 
	 *  @param perception_buffer The buffer where the connection writes the received perception messages.
	 *  @param action_buffer The buffer where the connection writes the received action messages. */
	public ServerSideAgentTCPConnection(String name, Queue<String> perception_buffer, Queue<String> action_buffer) {
		super(name, perception_buffer);
		this.PERCEPTON_BUFFER = this.BUFFER;
		this.ACTION_BUFFER = action_buffer;
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
				
				if(message != null) {
					// decides if the message is a perception or action
					// related message
					if(message.indexOf("<action ") > -1)
						this.ACTION_BUFFER.insert(message);
					else
						this.PERCEPTON_BUFFER.insert(message);
				}
			}
		}
		catch(SocketException e1) {
			if(!this.stop_working) {
				int local_socket_number = this.socket.getSocketNumber();
				
				// disconnects
				try { this.socket.disconnect(); }
				catch (IOException e) { e.printStackTrace(); }
				
				// screen message
				System.out.println("[SimPatrol.TCPConnection(" + this.getName() + ")]: Client disconnected.");
				
				// screen message
				System.out.println("[SimPatrol.TCPConnection(" + this.getName() + ")]: Waiting for new connections. ");
							
				// restarts the connection
				try { this.socket = new ServerSideTCPSocket(local_socket_number); }
				catch (IOException e) { e.printStackTrace(); }
				this.run();
			}			
		}
		catch(IOException e2) {
			e2.printStackTrace();
		}
	}
}