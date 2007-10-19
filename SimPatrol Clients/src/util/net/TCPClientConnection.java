/* TCPClientConnection.java */

/* The package of this class. */
package util.net;

/* Imported classes and/or interfaces. */
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.SocketException;
import java.net.UnknownHostException;

/** Implements a TCP client connection. */
public class TCPClientConnection extends ClientConnection {
	/* Attributes. */	
	/** The java 2 native TCP socket. */
	private final Socket SOCKET;
	
	/** The input stream received from the server. */
	private ObjectInputStream input;
	
	/** The output stream sent to the server. */
	private ObjectOutputStream output;
	
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
		
		this.output = new ObjectOutputStream(this.SOCKET.getOutputStream());
		this.output.flush();
		
		this.input = new ObjectInputStream(this.SOCKET.getInputStream());
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
		this.output.writeObject(message);
		this.output.flush();
	}
	
	/** Implements the receiving of a message.
	 *  @throws IOException */
	protected void receive() throws IOException, ClassNotFoundException {
		String message = null;
		try { message = (String) this.input.readObject(); }
		catch(SocketException e) {}
		
		if(message != null && message.length() > 0)
			this.buffer.insert(message);
	}
	
	public void run() {
		while(!this.stop_working) {
			try { this.receive(); }
			catch (IOException e) { e.printStackTrace(); }
			catch (ClassNotFoundException e) { e.printStackTrace(); }
		}
	}
}