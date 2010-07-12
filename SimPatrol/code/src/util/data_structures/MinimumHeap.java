/* Comparable.java */

/* The package of this class. */
package util.data_structures;

/* Imported classes and/or interfaces. */
import java.util.LinkedList;
import java.util.List;

/** Implements a minimum heap. */
public final class MinimumHeap {
	/* Attributes. */
	/** The dynamic array of the heap. */
	private final List<Comparable> HEAP;

	/* Methods. */
	/**
	 * Constructor.
	 * 
	 * @param objects_array
	 *            The array of objects to form the minimum heap.
	 */
	public MinimumHeap(Comparable[] objects_array) {
		this.HEAP = new LinkedList<Comparable>();

		// adds the comparable objects to the heap
		for (int i = 0; i < objects_array.length; i++)
			this.HEAP.add(objects_array[i]);

		// constructs the heap, using the bottom up strategy
		this.assureMinimumHeap();
	}

	/**
	 * Verifies if the heap has some element.
	 * 
	 * @return TRUE if the heap is empty, FALSE if not.
	 */
	public boolean isEmpty() {
		return this.HEAP.isEmpty();
	}

	/** Assures the heap is correct, using the bottom up strategy. */
	public void assureMinimumHeap() {
		for (int i = (this.HEAP.size() / 2) - 1; i >= 0; i--)
			this.heapfy(i);
	}

	/**
	 * Removes the smallest object from the heap.
	 * 
	 * @return The smallest object from the heap.
	 */
	public Comparable removeSmallest() {
		if (!this.HEAP.isEmpty()) {
			Comparable answer = this.HEAP.remove(0);

			if (!this.HEAP.isEmpty()) {
				Comparable last_element = this.HEAP
						.remove(this.HEAP.size() - 1);
				this.HEAP.add(0, last_element);
				this.heapfy(0);
			}

			return answer;
		} else
			return null;
	}

	/**
	 * "Heapfies" the dynamic array, starting from the given position.
	 * 
	 * @param pos
	 *            The position to start heapfying.
	 */
	private void heapfy(int pos) {
		pos++;

		while (pos <= HEAP.size() / 2) {
			int index_smallest_son = 2 * pos;

			if (pos < HEAP.size() * 0.5)
				if (this.HEAP.get(2 * pos + 1 - 1).isSmallerThan(
						this.HEAP.get(2 * pos - 1)))
					index_smallest_son = 2 * pos + 1;

			if (this.HEAP.get(index_smallest_son - 1).isSmallerThan(
					this.HEAP.get(pos - 1))) {
				Comparable temp = this.HEAP.get(pos - 1);
				this.HEAP.set(pos - 1, this.HEAP.get(index_smallest_son - 1));
				this.HEAP.set(index_smallest_son - 1, temp);

				pos = index_smallest_son;
			} else
				return;
		}
	}
}