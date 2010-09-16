/* UDPSocket.java */

/* The package of this class. */
package util.net;

/* Imported classes and/or interfaces. */
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;

/** Implements the UDP socket for datagram oriented connections. */
public final class UDPSocket {
	/* Attributes. */
	/** The size of the buffer to be read from the UDP socket. */
	private static final int BUFFER_SIZE = 4096; // 2^12

	/** The java 2 native UDP socket. */
	private final DatagramSocket SOCKET;

	/** The number of the local socket. */
	private final int LOCAL_SOCKET_NUMBER;

	/** The number of the remote socket. */
	private int remote_socket_number;

	/** The address of the remote socket. */
	private InetAddress remote_socket_address;

	/* Methods. */
	/**
	 * Constructor.
	 * 
	 * @param socket_number
	 *            The number of the UDP socket.
	 * @throws SocketException
	 */
	public UDPSocket(int socket_number) throws SocketException {
		this.LOCAL_SOCKET_NUMBER = socket_number;
		this.SOCKET = new DatagramSocket(this.LOCAL_SOCKET_NUMBER);
		this.remote_socket_number = -1;
		this.remote_socket_address = null;
	}

	/**
	 * Implements the reception of a message.
	 * 
	 * @return The message read from the UDP socket.
	 * @throws IOException
	 */
	public String receive() throws IOException {
		byte[] buffer = new byte[UDPSocket.BUFFER_SIZE];
		DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
		this.SOCKET.receive(packet);

		this.remote_socket_number = packet.getPort();
		this.remote_socket_address = packet.getAddress();

		return new String(packet.getData(), 0, packet.getLength());
	}

	/**
	 * Implements the sending of a message to the last remote contact. If no
	 * last remote contact exists, do nothing.
	 * 
	 * @param message
	 *            The message to be sent.
	 * @return TRUE if the message was successfully sent, FALSE if not.
	 * @throws IOException
	 */
	public boolean send(String message) throws IOException {
		if (this.remote_socket_address != null
				&& this.remote_socket_number > -1) {
			DatagramPacket packet = new DatagramPacket(message.getBytes(),
					message.length(), this.remote_socket_address,
					this.remote_socket_number);
			this.SOCKET.send(packet);

			return true;
		}

		return false;
	}

	/**
	 * Implements the sending of a message.
	 * 
	 * @param message
	 *            The message to be sent.
	 * @param remote_socket_address
	 *            The remote address of the receiver (in IP format).
	 * @param remote_socket_number
	 *            The number of the remote socket of the receiver.
	 * @throws IOException
	 */
	public void send(String message, String remote_socket_address,
			int remote_socket_number) throws IOException {
		this.remote_socket_address = InetAddress
				.getByName(remote_socket_address);
		this.remote_socket_number = remote_socket_number;
		this.send(message);
	}

	/**
	 * Returns the number of the UDP socket.
	 * 
	 * @return The number of the UDP socket.
	 */
	public int getSocketNumber() {
		return this.LOCAL_SOCKET_NUMBER;
	}
}