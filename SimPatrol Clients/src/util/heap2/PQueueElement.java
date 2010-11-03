package util.heap2;

public abstract class PQueueElement {
	private int index;
	
	public PQueueElement() {
		index = -1;
	}
	
	// recebe a posição no array
	void setIndex(int i) {
		index = i;
	}
	
	// retorna a posição no array
	int getIndex() {
		return index;
	}
	
	public abstract int getKey();
	
}
