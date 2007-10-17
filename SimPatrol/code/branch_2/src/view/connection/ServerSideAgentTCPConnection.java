/* ServerSideAgentTCPConnection.java */

/* The package of this class. */
package view.connection;

/* Imported classes and/or interfaces. */
import java.io.EOFException;
import java.io.IOException;
import java.net.SocketException;

import control.simulator.CycledSimulator;
import util.Queue;

/** Implements the TCP connections with the external agents.
 *  Used by cycled simulators. 
 *  
 *  @see CycledSimulator */
public final class ServerSideAgentTCPConnection extends ServerSideTCPConnection {
	/* Attributes. */
	/** The buffer where the connection writes the received
	 *  perception messages. */
	private Queue<String> perception_buffer;
	
	/** The buffer where the connection writes the received
	 *  action messages. */
	private Queue<String> action_buffer;
	
	/* Methods. */
	/** Constructor.
	 * 
	 *  @param name The name of the thread of the connection. 
	 *  @param perception_buffer The buffer where the connection writes the received perception messages.
	 *  @param action_buffer The buffer where the connection writes the received action messages. */
	public ServerSideAgentTCPConnection(String name, Queue<String> perception_buffer, Queue<String> action_buffer) {
		super(name, perception_buffer);
		this.perception_buffer = this.buffer;
		this.action_buffer = action_buffer;
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
				String message = null;
				
				try { message = this.socket.receive(); }
				catch (ClassNotFoundException e) { e.printStackTrace(); }
				
				// decides if the message is a perception or action
				// related message
				if(message.indexOf("<action ") > -1)
					this.action_buffer.insert(message);
				else this.perception_buffer.insert(message);
				
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
		catch(SocketException e2) {
			// disconnects
			try { this.socket.disconnect(); }
			catch (IOException e) { e.printStackTrace(); }
			
			// screen message
			System.out.println("[SimPatrol.TCPConnection(" + this.getName() + ")]: Client disconnected.");
		}
		catch(IOException e3) {
			e3.printStackTrace();
		}
	}
}