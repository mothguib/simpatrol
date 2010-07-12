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
import java.net.SocketTimeoutException;

/**
 * Implements the TCP socket for connection directed data sending from the
 * server side.
 */
public class ServerSideTCPSocket{
	/* Attributes. */
	/** The input stream received from the client. */
	private BufferedReader input;

	/** The output stream sent to the client. */
	private PrintStream output;

	/** The TCP socket of the client. */
	private Socket client_socket;

	/** The local TCP socket (server's socket). */
	private final ServerSocket SERVER_SOCKET;

	/** Waiting time to connect to the remote client. */
	private static final int CONNECTING_TIME_INTERVAL = 10000; // 10 sec

	/** Waiting time to read some data from the input stream. */
	private static final int READING_TIME_INTERVAL = 5000; // 5 sec

	/* Methods. */
	/**
	 * Constructor.
	 * 
	 * @param socket_number
	 *            The number of the TCP socket.
	 * @throws IOException
	 */
	public ServerSideTCPSocket(int socket_number) throws IOException {
		this.input = null;
		this.output = null;
		this.client_socket = null;

		this.SERVER_SOCKET = new ServerSocket(socket_number);
		this.SERVER_SOCKET
				.setSoTimeout(ServerSideTCPSocket.CONNECTING_TIME_INTERVAL);
	}

	/**
	 * Returns the local number of the TCP socket.
	 * 
	 * @return The number of the socket where this connection listens to
	 *         messages.
	 */
	public int getSocketNumber() {
		return this.SERVER_SOCKET.getLocalPort();
	}

	/**
	 * Verifies if this TCP socket connected successfully to a remote another
	 * one.
	 * 
	 * @return TRUE if the TCP socket is connected to a remote one, FALSE if
	 *         not.
	 */
	public boolean isConnected() {
		if (this.client_socket != null)
			return this.client_socket.isConnected();
		else
			return false;
	}

	/**
	 * Tries to establish a connection between the local server and the remote
	 * client, returning the success of such operation.
	 * 
	 * @return TRUE, if the connection was established, FALSE if not.
	 * @throws IOException
	 */
	public boolean connect() throws IOException {
		try {
			this.client_socket = SERVER_SOCKET.accept();
		} catch (SocketTimeoutException e) {
			this.input = null;
			this.output = null;
			this.client_socket = null;
			return false;
		}

		this.client_socket
				.setSoTimeout(ServerSideTCPSocket.READING_TIME_INTERVAL);

		this.input = new BufferedReader(new InputStreamReader(
				this.client_socket.getInputStream()));

		this.output = new PrintStream(this.client_socket.getOutputStream());
		this.output.flush();

		return true;
	}

	/**
	 * Sends a given message to the remote client.
	 * 
	 * If a connection was not established yet, returns FALSE.
	 * 
	 * @param message
	 *            The message to be sent to the remote client.
	 * @return TRUE if the message was sent sucessfully, FALSE if not.
	 */
	public boolean send(String message) {
		if (this.output != null) {
			this.output.println(message);
			this.output.flush();

			return true;
		}

		return false;
	}

	/**
	 * Receives a message from the remote client.
	 * 
	 * @return The message received from the client.
	 * @throws IOException
	 */
	public String receive() throws IOException {
		if (this.input != null) {
			try {
				return this.input.readLine();
			} catch (InterruptedIOException e) {
				return null;
			}
		}

		return null;
	}

	/**
	 * Disconnects the remote client, however does not close the TCP socket.
	 * 
	 * @throws IOException
	 */
	public void disconnect() throws IOException {
		if (this.output != null) {
			this.output.close();
			this.output = null;
		}

		if (this.input != null) {
			this.input.close();
			this.input = null;
		}

		if (this.client_socket != null) {
			this.client_socket.close();
			this.client_socket = null;
		}

		this.SERVER_SOCKET.close();
	}	
	
}