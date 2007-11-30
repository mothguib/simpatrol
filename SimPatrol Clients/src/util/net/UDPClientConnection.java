/* UDPClientConnection.java */

/* The package of this class. */
package util.net;

/* Imported classes and/or interfaces. */
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;

/**
 * Implements a passive UDP connection, used mainly to receive remotely produced
 * data.
 */
public class UDPClientConnection extends ClientConnection {
	/* Attributes. */
	/** The java 2 native UDP socket. */
	private final DatagramSocket SOCKET;

	/** The address of the remote socket. */
	private final InetAddress REMOTE_SOCKET_ADDRESS;

	/** The number of the remote socket. */
	private final int REMOTE_SOCKET_NUMBER;

	/** The size of the buffer to be read from the UDP socket. */
	private static final int BUFFER_SIZE = 65536;

	/* Methods. */
	/**
	 * Constructor.
	 * 
	 * @param remote_socket_address
	 *            The address of the remote contatc (in IP format).
	 * @param remote_socket_number
	 *            The number of the socket of the remote contatc.
	 * @throws SocketException
	 * @throws UnknownHostException
	 */
	public UDPClientConnection(String remote_socket_address,
			int remote_socket_number) throws SocketException,
			UnknownHostException {
		super();

		this.SOCKET = new DatagramSocket();
		this.REMOTE_SOCKET_ADDRESS = InetAddress
				.getByName(remote_socket_address);
		this.REMOTE_SOCKET_NUMBER = remote_socket_number;
	}

	/**
	 * Sends a given string message to the remote contact.
	 * 
	 * @param message
	 *            The string message to be sent.
	 * @throws IOException
	 */
	public void send(String message) throws IOException {
		DatagramPacket packet = new DatagramPacket(message.getBytes(), message
				.length(), this.REMOTE_SOCKET_ADDRESS,
				this.REMOTE_SOCKET_NUMBER);
		this.SOCKET.send(packet);
	}

	/**
	 * Implements the receiving of a message.
	 * 
	 * @throws IOException
	 */
	protected void receive() throws IOException {
		byte[] buffer = new byte[BUFFER_SIZE];
		DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
		this.SOCKET.receive(packet);

		this.BUFFER.insert(new String(packet.getData(), 0, packet.getLength()));
	}

	public void run() {
		try {
			this.send("");
		} catch (IOException e) {
			e.printStackTrace();
		}

		while (!this.stop_working) {
			try {
				this.receive();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
}