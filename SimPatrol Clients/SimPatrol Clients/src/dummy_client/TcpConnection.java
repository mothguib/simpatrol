
package dummy_client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.InterruptedIOException;
import java.io.PrintStream;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.LinkedList;


/**
 *  Implements a TCP client connection.
 *  It is a simplified version of class "util.net.TCPClientConnection".
 *  
 * @author Pablo Sampaio
 */
public class TcpConnection extends Thread {

	// Waiting time to read some data from the input stream
	private static final int READING_TIME_TOLERANCE = 10; // 0.01 sec

	private final Socket serverSocket;

	// Streams to receive/send data
	private final BufferedReader serverInput;
	private final PrintStream serverOutput;

	protected boolean working;

	// Buffer of received messages
	protected LinkedList<String> messagesReceived;

	// List of observers to be notified when there is data
	protected ArrayList<TcpConnectionObserver> observers;
	

	/**
	 * Constructor.
	 * 
	 * @param remoteSocketAddress The address of the remote contact (in IP format).
	 * @param remoteSocketNumber The number of the port that the server is listening.
	 */
	public TcpConnection(String remoteSocketAddress,
			int remoteSocketNumber) throws UnknownHostException, IOException {

		this.serverSocket = new Socket(remoteSocketAddress, remoteSocketNumber);
		this.serverSocket.setSoTimeout(READING_TIME_TOLERANCE);

		this.serverInput = new BufferedReader(
								new InputStreamReader(this.serverSocket.getInputStream()));

		this.serverOutput = new PrintStream(this.serverSocket.getOutputStream());
		this.serverOutput.flush();
		
		this.working = true;
		this.messagesReceived = new LinkedList<String>();
		this.observers = new ArrayList<TcpConnectionObserver>();
		
		super.setDaemon(true);
	}
	
	
	/**
	 * Set the observers that will be called when receive a message.
	 */
	public void addObserver(TcpConnectionObserver observer){
		this.observers.add(observer);
	}
	
	
	/**
	 * Update the observers, when a new packet arrives.
	 */
	public void updateObservers(){
		for(int i=0; i < observers.size();i++){
			observers.get(i).update();
		}
	}
	
	public boolean isWorking() {
		return working;
	}

	/**
	 * Returns and clears the list of unread messages.
	 */
	public synchronized String[] retrieveMessages() {
		String[] answer = new String[this.messagesReceived.size()];

		for (int i = 0; i < answer.length; i++) {
			answer[i] = this.messagesReceived.removeFirst();
		}

		return answer;
	}
	
	/**
	 * Synchronously waits for messages. Only returns when at 
	 * least one message is received.
	 */
	public String[] syncRetrieveMessages() {
		String[] answer;
		
		answer = retrieveMessages();
		
		while (answer.length == 0 && this.working)  {
			Thread.yield();			
			answer = retrieveMessages();
		} 
		
		return answer;
	}

	/**
	 * Returns the socket address of the remote contact (in IP format). 
	 */
	public String getRemoteSocketAdress() {
		String completeAddress = this.serverSocket.getRemoteSocketAddress().toString();
		int socketIndex = completeAddress.indexOf(":");

		return completeAddress.substring(1, socketIndex);
	}

	/**
	 * Indicates that the connection must be closed.
	 */
	public void stopWorking() {
		this.working = false;

		try {
			this.serverOutput.close();
			this.serverInput.close();
			this.serverSocket.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Sends a given string message to the remote contact.
	 */
	public void send(String message) throws IOException {
		this.serverOutput.println(message);
		this.serverOutput.flush();
	}


	/**
	 * Actively receives messages from the socket and stores them.
	 */
	public void run() {
		while (this.working) {
			
			try {				
				this.receive();
			} catch (IOException e) {
				//e.printStackTrace();
				this.working = false;
			}
			
			Thread.yield();			
		}
		
		System.out.println("Connection's thread stopped!");
		
		this.updateObservers();
	}
	
	protected void receive() throws IOException {
		StringBuffer buffer = new StringBuffer();
		String messageLine = null;

		do {
			try {
				
				messageLine = this.serverInput.readLine();
				
				if (messageLine != null) {
					buffer.append(messageLine);

					if (buffer.indexOf("</perception>") > -1) {
						break;
					} else if (buffer.indexOf("<perception ") > -1 
								&& buffer.indexOf("message=\"") > -1 && buffer.indexOf("/>") > -1) {
						break;
					} else if (buffer.toString().trim().equals("")) {
						break;
					}
					
				} else {
					this.stopWorking();
					
				}
				
			} catch (InterruptedIOException e) {
				break;
			}
			
		} while (true);

		if (buffer.length() > 0) {
			synchronized (this) {
				this.messagesReceived.add(buffer.toString());
			}
			this.updateObservers();			
		}		
	}

	
}
