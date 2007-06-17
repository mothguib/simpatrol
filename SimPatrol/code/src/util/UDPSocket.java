/* UDPSocket.java */

/* The package of this class. */
package util;

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
	private static final int BUFFER_SIZE = 1024;
	
	/** The java 2 native UDP socket. */
	private DatagramSocket socket;
	
	/** The number of the local socket. */
	private int local_socket_number;
	
	/** The number of the remote socket. */
	private int remote_socket_number;
	
	/** The address of the remote socket. */
	private InetAddress remote_socket_address;

	/* Methods. */
	/** Constructor.
	 *  @param socket_number The number of the local UDP socket. 
	 *  @throws SocketException */
	public UDPSocket(int local_socket_number) throws SocketException {
		this.local_socket_number = local_socket_number;
		this.socket = new DatagramSocket(this.local_socket_number);
	}
	
	/** Implements the receiving of a message.
	 *  @return The message read from the UDP socket.
	 *  @throws IOException */
	public String receive() throws IOException {
		byte[] buffer = new byte[UDPSocket.BUFFER_SIZE];
		DatagramPacket packet = new DatagramPacket(buffer, buffer.length);		
		this.socket.receive(packet);
		
		this.remote_socket_number = packet.getPort();
		this.remote_socket_address = packet.getAddress();
		
		return new String(packet.getData(), 0, packet.getLength());
	}
	
	/** Implements the sending of a message to the last remote contact.
	 *  @param message The message to be sent. 
	 *  @throws IOException */
	public void send(String message) throws IOException {
		DatagramPacket packet = new DatagramPacket(message.getBytes(), message.length(), this.remote_socket_address, this.remote_socket_number);
		this.socket.send(packet);
	}
	
	/** Implements the sending of a message.
	 *  @param message The message to be sent.
	 *  @param remote_socket_address The remote address of the receiver (in IP format).
	 *  @param remote_socket_number The number of the remote socket of the receiver. 
	 *  @throws IOException */
	public void send(String message, String remote_socket_address, int remote_socket_number) throws IOException {
		this.remote_socket_address = InetAddress.getByName(remote_socket_address);
		this.remote_socket_number = remote_socket_number;
		this.send(message);
	}
}
