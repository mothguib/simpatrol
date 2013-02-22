package util.ipc;



/**
 * <p>Title: </p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2002</p>
 * <p>Company: </p>
 * @author Stanley  Wang
 * @version 1.0
 */

public class MemMapProxy {
	private MemMapFileObserver observer;

	static {
		System.loadLibrary("MemMapProxyLib");
	}

	public MemMapProxy(String message) {	   
		init(message);
		System.out.println("New Proxy Client: " + message);
	} 


	public void setObserver(MemMapFileObserver observer){
		this.observer = observer;
	}

	public void fireDataReadyEvent() {
		observer.onDataReady();
	}

	private native boolean init(String msg);
	public native void registerMessage(String name);
	public native void destroy();

}