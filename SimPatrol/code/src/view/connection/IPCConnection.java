package view.connection;

import util.data_structures.Queue;

public class IPCConnection extends Connection {

	public IPCConnection(String thread_name, Queue<String> buffer) {
		super(thread_name, buffer);
		// TODO Auto-generated constructor stub
	}

	@Override
	public boolean send(String message) {
		return true;
		
	}

	@Override
	public int getSocketNumber() {
		// TODO Auto-generated method stub
		return 0;
	}

	public boolean onDataReady() {
		return true;
		// TODO Auto-generated method stub
		
	}

}
