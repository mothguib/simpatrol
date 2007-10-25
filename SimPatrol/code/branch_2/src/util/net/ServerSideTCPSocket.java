/* TCPSocket.java */

/* The package of this class. */
package util.net;

/* Imported classes and/or interfaces. */
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.InterruptedIOException;
import java.io.PrintStream;
import java.net.ServerSocket;
import java.net.Socket;

/** Implements the TCP socket for connection oriented data sending
 *  from the server side. */
public class ServerSideTCPSocket {
	/* Attributes. */
	/** The input stream received from the client. */
	private BufferedReader input;
	
	/** The output stream sent to the client. */
	private PrintStream output;
	
	/** The TCP socket of the client. */
	private Socket client_socket;
	
	/** The local TCP socket (server's socket). */
	private final ServerSocket SERVER_SOCKET;
	
	/** Waiting time to read some data from the input stream. */
	private static final int READING_TIME_TOLERANCE = 5000; // 5 sec
	
	/* Methods. */
	/** Constructor.
	 * 
	 *  @param socket_number The number of the TCP socket.
	 *  @throws IOException */
	public ServerSideTCPSocket(int socket_number) throws IOException {
		this.SERVER_SOCKET = new ServerSocket(socket_number);
	}
	
	/** Returns the local number of the TCP socket.
	 * 
	 *  @return The number of the socket where this connection listens to messages. */
	public int getSocketNumber() {
		return this.SERVER_SOCKET.getLocalPort();
	}
	
	/** Establishes a connection between the local server and the
	 *  remote client.
	 *  
	 *  @throws IOException */
	public void connect() throws IOException {
		this.client_socket = SERVER_SOCKET.accept();
		this.client_socket.setSoTimeout(READING_TIME_TOLERANCE);
		
		this.input = new BufferedReader(new InputStreamReader(this.client_socket.getInputStream()));
		
		this.output = new PrintStream(this.client_socket.getOutputStream());
		this.output.flush();
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
			this.output.println(message);
			this.output.flush();
			
			return true;
		}
		
		return false;
	}
	
	/** Receives a message from the remote client.
	 * 
	 *  @return The message received from the client.
	 *  @throws IOException */
	public String receive() throws IOException {
		if(this.input != null) {
			try {
				return this.input.readLine(); 
			}
			catch(InterruptedIOException e) {
				return null;
			}
		}
		
		return null;
	}
	
	/** Disconnects the remote client.
	 *  
	 *  @throws IOException */
	public void disconnect() throws IOException {
		if(this.output != null)
			this.output.close();
		
		if(this.input != null)
			this.input.close();
		
		if(this.client_socket != null)
			this.client_socket.close();
		
		this.SERVER_SOCKET.close();
	}	
}