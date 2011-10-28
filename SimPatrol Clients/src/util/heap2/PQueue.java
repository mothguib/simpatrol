package util.heap2;

/**
 * An interface for a complete priority queue.
 * 
 * @author Pablo Sampaio
 */
public interface PQueue<T extends PQueueElement> {
	
	public boolean isEmpty();
	public int size();
	
	public void add(T element);
	
	public T removeMinimum();
	public void remove(T element);
	
	public T getMinimum();
	
	public void decreaseKey(T e);
	
}
