/* TCPSocket.java */

/* The package of this class. */
package util.net;

/* Imported classes and/or interfaces. */
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;

/** Implements the TCP socket for connection oriented data sending
 *  from the server side. */
public class ServerSideTCPSocket {
	/* Attributes. */
	/** The input stream received from the client. */
	private ObjectInputStream input;
	
	/** The output stream sent to the client. */
	private ObjectOutputStream output;
	
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
	
	/** Establishes a connection between the local server and the
	 *  remote client.
	 *  
	 *  @throws IOException */
	public void connect() throws IOException {
		socket = server.accept();
		
		this.output = new ObjectOutputStream(this.socket.getOutputStream());
		this.output.flush();
		
		this.input = new ObjectInputStream(this.socket.getInputStream());
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
			this.output.writeObject(message);
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
			return (String) this.input.readObject();
		
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