/* Comparable.java */

/* The package of this class. */
package util.heap;

/* Imported classes and/or interfaces. */
import java.util.LinkedList;
import java.util.List;

/** Implements a minimum heap. */
public final class MinimumHeap {
	/* Attributes. */
	/** The dynamic array of the heap. */
	private List<Comparable> heap;
	
	/* Methods. */
	/** Constructor.
	 * 
	 *  @param objects_array The array of objects to form the minimum heap. */
	public MinimumHeap(Comparable[] objects_array) {
		this.heap = new LinkedList<Comparable>();
		
		// adds the comparable objects to the heap
		for(int i = 0; i < objects_array.length; i++)
			this.heap.add(objects_array[i]);
		
		// constructs the heap, using the bottom up strategy
		this.assureMinimumHeap();
	}
	
	/** Assures the heap is correct, using the bottom up strategy. */
	public void assureMinimumHeap() {
		for(int i = (this.heap.size() / 2) - 1; i >= 0; i--)
			this.heapfy(i);
	}
	
	/** Obtains the smallest object from the heap.
	 * 
	 *  @return The smallest object from the heap. */
	public Comparable getSmallest() {
		if(!this.heap.isEmpty())
			return this.heap.get(0);
		else return null;
	}
	
	/** "Heapfies" the dynamic array, starting from the given position.
	 * 
	 *  @param pos The position to start the heapfy. */
	private void heapfy(int pos) {
		pos++;
		
		while(pos <= heap.size() / 2) {
			int index_smallest_son = 2 * pos;
			
			if(pos < heap.size() * 0.5)
				if(this.heap.get(2 * pos + 1 - 1).isSmallerThan(this.heap.get(2 * pos - 1)))
					index_smallest_son = 2 * pos + 1;
			
			if(this.heap.get(index_smallest_son - 1).isSmallerThan(this.heap.get(pos - 1))) {
				Comparable temp = this.heap.get(pos - 1);
				this.heap.set(pos - 1, this.heap.get(index_smallest_son - 1));
				this.heap.set(index_smallest_son - 1, temp);
				
				pos = index_smallest_son;
			}
			else return;
		}
	}
}