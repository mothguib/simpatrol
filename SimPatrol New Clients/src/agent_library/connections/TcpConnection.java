package agent_library.connections;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.InterruptedIOException;
import java.io.PrintStream;
import java.net.Socket;
import java.net.UnknownHostException;


/** 
 * Implements a TCP client connection. 
 */
public class TcpConnection extends ClientConnection implements Runnable {
	private static final int READING_TIME_TOLERANCE = 100; // 0.1 sec
	
	public Thread thread;  //TODO: remove?

	private final Socket socket;
	private final BufferedReader socketInput;
	private final PrintStream socketOutput;
	private boolean stopWorking;
	

	public TcpConnection(String remoteSocketAddress,
			int remote_socket_number) throws UnknownHostException, IOException {
		super();
		this.socket = new Socket(remoteSocketAddress, remote_socket_number);
		this.socket.setSoTimeout(READING_TIME_TOLERANCE);

		this.socketInput = new BufferedReader(new InputStreamReader(this.socket
				.getInputStream()));

		this.socketOutput = new PrintStream(this.socket.getOutputStream());		
	}

	public String getRemoteAddress() {
		String completeAddress = this.socket.getRemoteSocketAddress().toString();
		int socketIndex = completeAddress.indexOf(":");
		return completeAddress.substring(1, socketIndex);
	}


	@Override
	public void open() {
		//TODO: mudar inicializações para cá!
		this.stopWorking = false;
		this.thread = new Thread(this);
		this.thread.start();
	}
	
	public void close() throws IOException {
		this.stopWorking = true;
		this.socketOutput.close();
		this.socketInput.close();
		this.socket.close();
	}

	/**
	 * Sends a given string message to the remote contact.
	 */
	public void send(String message) throws IOException {
		this.socketOutput.println(message);
		this.socketOutput.flush();
	}

	/**
	 * Implements the receiving of a message.
	 */
	protected boolean receive() throws IOException {
		StringBuffer buffer = new StringBuffer();

		String message_line = null;
		do {
			try {
				message_line = this.socketInput.readLine();				
				if (message_line != null) {
					buffer.append(message_line);					
					if (buffer.indexOf("</perception>") > -1)
						break;
					else if (buffer.indexOf("<perception ") > -1
							&& buffer.indexOf("message=\"") > -1
							&& buffer.indexOf("/>") > -1)
						break;
				} else {
					this.close();
				}
			} catch (InterruptedIOException e) {
				break;
			} catch (IOException e) {
				break;
			}
		} while (true);

		if (buffer.length() > 0){
			this.addReceivedMessage(buffer.toString());
		}
		return false;
	}

	public void run() {
		while (!this.stopWorking) {
			try {
				this.receive();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

}