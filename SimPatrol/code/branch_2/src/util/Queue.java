/* Queue.java */

/* The package of this class. */
package util;

/* Imported classes and/or interfaces. */
import java.util.LinkedList;

/** Implements queues, obviously respecting the
 *  First In First Out access law. */
public final class Queue<E> {
	/* Attributes. */
	/** The linked list that holds the queue. */
	private LinkedList<E> queue;
	
	/* Methods. */
	/** Constructor. */
	public Queue() {
		this.queue = new LinkedList<E>();
	}
	
	/** Inserts an element on the queue.
	 * 
	 *  @param object The element to be inserted. */
	public synchronized void insert(E object) {
		this.queue.addLast(object);
	}
	
	/** Removes the first element from the queue.
	 * 
	 *  @return The first element of the queue. */
	public synchronized E remove() {
		if(this.queue.size() > 0) return this.queue.remove(0);
		else return null;
	}
	
	/** Removes all the elements from the queue. */
	public synchronized void clear() {
		this.queue.clear();
	}
	
	/** Returns the number of objects in the queue.
	 * 
	 *  @return The size of the queue. */
	public int getSize() {
		return this.queue.size();
	}	
}