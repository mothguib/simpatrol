/* Connection.java */

/* The package of this class. */
package view.connection;

/* Imported classes and/or interfaces. */
import java.io.IOException;
import java.util.ArrayList;

import control.daemon.IMessageObserver;

import control.daemon.IMessageSubject;
import util.data_structures.Queue;

/**
 * The connections offered by SimPatrol in order to contact the remote actors.
 * 
 * @developer Subclasses of this one must use the attribute "is_active" when
 *            implementing their run() method.
 */
public abstract class Connection extends Thread implements IMessageSubject {
	/* Attributes. */
	/** Registers if the connection is active. */
	protected boolean is_active;

	/** The buffer where the connection writes the received messages. */
	protected final Queue<String> BUFFER;
	
	protected ArrayList<IMessageObserver> observers;

	/* Methods. */
	/**
	 * Constructor.
	 * 
	 * @param thread_name
	 *            The name of the thread of the connection.
	 * @param buffer
	 *            The buffer where the connection writes the received messages.
	 */
	public Connection(String thread_name, Queue<String> buffer) {
		super(thread_name);
		this.is_active = true;
		this.BUFFER = buffer;
		this.observers = new ArrayList<IMessageObserver>();
	}

	/** Indicates that the connection must stop acting. */
	public void stopActing() {
		this.is_active = false;
	}

	/**
	 * Sends a given string message and returns the success of the sending.
	 * 
	 * @param message
	 *            The string message to be sent.
	 * @return TRUE if the message was sent successfully, FALSE if not.
	 */
	public abstract boolean send(String message);

	/**
	 * Returns the number of the socket (TCP or UDP) of the connection.
	 * 
	 * @return The number of the socket of the connection.
	 */
	public abstract int getSocketNumber();

	/**
	 * Starts the connection's work.
	 * 
	 * @param local_socket_number
	 *            The number of the socket of the connection.
	 * @throws IOException
	 */
	public void start(int local_socket_number) throws IOException {
		super.start();
	}

	/**
	 * Give preference to use this.start(int local_socket_number).
	 * 
	 * @deprecated
	 */
	public void start() {
		super.start();
	}
	
	/*
	 * Set the observers that will be called when receive a message;
	 */
	public void addObserver(IMessageObserver observer){
		this.observers.add(observer);
	}
	
	/*
	 * Update the observers, when a new packet arrives
	 */
	public void updateObservers(){
		for(int i=0; i<observers.size();i++){
			observers.get(i).update();
		}
	}
}