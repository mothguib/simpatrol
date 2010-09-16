/* TCPClientConnection.java */

/* The package of this class. */
package util.net;

/* Imported classes and/or interfaces. */
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.InterruptedIOException;
import java.io.PrintStream;
import java.net.Socket;
import java.net.UnknownHostException;

/** Implements a TCP client connection. */
public class TCPClientConnection extends ClientConnection {
	/* Attributes. */
	/** The java 2 native TCP socket. */
	private final Socket SOCKET;

	/** The input stream received from the server. */
	private final BufferedReader INPUT;

	/** The output stream sent to the server. */
	private final PrintStream OUTPUT;

	/** Waiting time to read some data from the input stream. */
	private static final int READING_TIME_TOLERANCE = 5000; // 5 sec

	/* Methods. */
	/**
	 * Constructor.
	 * 
	 * @param remote_socket_address
	 *            The address of the remote contact (in IP format).
	 * @param remote_socket_number
	 *            The number of the socket of the remote contact.
	 * @throws IOException
	 * @throws UnknownHostException
	 */
	public TCPClientConnection(String remote_socket_address,
			int remote_socket_number) throws UnknownHostException, IOException {
		super();
		this.SOCKET = new Socket(remote_socket_address, remote_socket_number);
		this.SOCKET.setSoTimeout(READING_TIME_TOLERANCE);

		this.INPUT = new BufferedReader(new InputStreamReader(this.SOCKET
				.getInputStream()));

		this.OUTPUT = new PrintStream(this.SOCKET.getOutputStream());
		this.OUTPUT.flush();
	}

	/** Returns the socket address of the remote contact (in IP format). */
	public String getRemoteSocketAdress() {
		String complete_address = this.SOCKET.getRemoteSocketAddress()
				.toString();

		int socket_index = complete_address.indexOf(":");
		return complete_address.substring(1, socket_index);
	}

	/**
	 * Indicates that the connection must stop working.
	 * 
	 * @throws IOException
	 */
	public void stopWorking() throws IOException {
		super.stopWorking();

		this.OUTPUT.close();
		this.INPUT.close();
		this.SOCKET.close();
	}

	/**
	 * Sends a given string message to the remote contact.
	 * 
	 * @param message
	 *            The string message to be sent.
	 * @throws IOException
	 */
	public void send(String message) throws IOException {
		this.OUTPUT.println(message);
		this.OUTPUT.flush();
	}

	/**
	 * Implements the receiving of a message.
	 * 
	 * @throws IOException
	 */
	protected void receive() throws IOException {
		StringBuffer buffer = new StringBuffer();

		String message_line = null;
		do {
			try {
				message_line = this.INPUT.readLine();
				if (message_line != null) {
					buffer.append(message_line);

					if (buffer.indexOf("</perception>") > -1)
						break;
					else if (buffer.indexOf("<perception ") > -1
							&& buffer.indexOf("message=\"") > -1
							&& buffer.indexOf("/>") > -1)
						break;
				} else
					this.stopWorking();
			} catch (InterruptedIOException e) {
				break;
			} catch (IOException e) {
				break;
			}
		} while (true);

		if (buffer.length() > 0)
			this.BUFFER.insert(buffer.toString());
	}

	public void run() {
		while (!this.stop_working) {
			try {
				this.receive();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
}