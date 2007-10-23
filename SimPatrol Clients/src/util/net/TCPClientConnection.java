/* TCPClientConnection.java */

/* The package of this class. */
package util.net;

/* Imported classes and/or interfaces. */
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;

/** Implements a TCP client connection. */
public class TCPClientConnection extends ClientConnection {
	/* Attributes. */	
	/** The java 2 native TCP socket. */
	private final Socket SOCKET;
	
	/** The input stream received from the server. */
	private DataInputStream input;
	
	/** The output stream sent to the server. */
	private DataOutputStream output;
	
	/* Methods. */
	/** Constructor.
	 * 
	 *  @param remote_socket_address The address of the remote contatc (in IP format).
	 *  @param remote_socket_number The number of the socket of the remote contatc. 
	 *  @throws IOException 
	 *  @throws UnknownHostException */ 
	public TCPClientConnection(String remote_socket_address, int remote_socket_number) throws UnknownHostException, IOException {
		super();
		this.SOCKET = new Socket(remote_socket_address, remote_socket_number);
		
		this.output = new DataOutputStream(this.SOCKET.getOutputStream());
		this.output.flush();
		
		this.input = new DataInputStream(this.SOCKET.getInputStream());
	}
	
	/** Returns the socket address of the remote contact (in IP format). */
	public String getRemoteSocketAdress() {
		String complete_address = this.SOCKET.getRemoteSocketAddress().toString();
		
		int socket_index = complete_address.indexOf(":");				
		return complete_address.substring(1, socket_index);		
	}
	
	/** Indicates that the connection must stop working.
	 * 
	 *  @throws IOException */
	public void stopWorking() throws IOException {
		super.stopWorking();
		
		this.output.close();
		this.input.close();
		this.SOCKET.close();
	}
	
	/** Sends a given string message to the remote contact.
	 * 
	 *  @param message The string message to be sent. 
	 *  @throws IOException */
	public void send(String message) throws IOException {
		this.output.writeUTF(message);
		this.output.flush();
	}
	
	/** Implements the receiving of a message.
	 *  @throws IOException */
	protected void receive() throws IOException {
		String message = this.input.readUTF();
		
		if(message != null && message.length() > 0)
			this.buffer.insert(message);
	}
	
	public void run() {
		while(!this.stop_working) {
			try { this.receive(); }
			catch (IOException e) { e.printStackTrace(); }
		}
	}
}