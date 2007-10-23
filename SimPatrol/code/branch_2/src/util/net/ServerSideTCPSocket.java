/* TCPSocket.java */

/* The package of this class. */
package util.net;

/* Imported classes and/or interfaces. */
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

/** Implements the TCP socket for connection oriented data sending
 *  from the server side. */
public class ServerSideTCPSocket {
	/* Attributes. */
	/** The input stream received from the client. */
	private DataInputStream input;
	
	/** The output stream sent to the client. */
	private DataOutputStream output;
	
	/** The java 2 native TCP socket. */
	private Socket socket;
	
	/** The java 2 native TCP server. */
	private ServerSocket server;
	
	/* Methods. */
	/** Constructor.
	 * 
	 *  @param socket_number The number of the TCP socket.
	 *  @throws IOException */
	public ServerSideTCPSocket(int socket_number) throws IOException {
		this.server = new ServerSocket(socket_number);
	}
	
	/** Returns the local number of the TCP socket.
	 * 
	 *  @return The number of the socket where this connection listens to messages. */
	public int getSocketNumber() {
		return this.server.getLocalPort();
	}
	
	/** Establishes a connection between the local server and the
	 *  remote client.
	 *  
	 *  @throws IOException */
	public void connect() throws IOException {
		socket = server.accept();
		
		this.output = new DataOutputStream(this.socket.getOutputStream());
		this.output.flush();
		
		this.input = new DataInputStream(this.socket.getInputStream());
	}
	
	/** Sends a given message to the remote client.
	 * 
	 *  If a connection was not established yet, returns FALSE.
	 *  
	 *  @param message The message to be sent to the remote client.
	 *  @return TRUE if the message was sent sucessfully, FALSE if not.
	 *  @throws IOException */
	public boolean send(String message) throws IOException {
		if(this.output != null) {
			this.output.writeUTF(message);
			this.output.flush();
			
			return true;
		}
		
		return false;
	}
	
	/** Receives a message from the remote client.
	 * 
	 *  @return The message received from the client.
	 *  @throws ClassNotFoundException 
	 *  @throws IOException */
	public String receive() throws IOException, ClassNotFoundException {
		if(this.input != null)
			return this.input.readUTF();
		
		return null;
	}
	
	/** Disconnects the remote client.
	 *  
	 *  @throws IOException */
	public void disconnect() throws IOException {
		this.output.close();
		this.input.close();
		this.socket.close();
	}	
}