package common;

public interface IMessageSubject {
	
	public void updateObservers();
	public void addObserver(IMessageObserver observer);

}
