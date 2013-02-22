package agent_library.connections;

import java.io.IOException;

import util.ipc.MemMapFile;
import util.ipc.MemMapFileObserver;
import util.ipc.MemMapProxy;


/** 
 * Implements a connection through the memory, using IPC (for Windows-only). 
 */
public class IpcConnection extends ClientConnection implements MemMapFileObserver {
	public static final String fileMappingObjName = "Mem_Map_File-{5B85BE0C-F89E-450e-80BE-5707813931E5}";
	
	private int mapFilePtr;
	private int viewPtr;
	public final int dwMemFileSize = 2 *1024 * 1024;
	public int writen = 0;
	private int messagePointer = 0;
	private final String messageDataReady  = "UWM_DATA_READY_MSG-{7FDB2CB4-5510-4d30-99A9-CD7752E0D681}";
	private int messageDataReadyID = -1;
	protected MemMapProxy proxy;

	public IpcConnection(String agentID) {
		String fileName = fileMappingObjName + agentID;

		mapFilePtr = MemMapFile.createFileMapping(MemMapFile.PAGE_READWRITE, 0, dwMemFileSize, fileName);
		
		if (messageDataReadyID == -1) {
			messageDataReadyID = MemMapFile.registerMessage(messageDataReady + agentID + "B");
		}
		
	    if (mapFilePtr != 0) {
	    	viewPtr = MemMapFile.mapViewOfFile(mapFilePtr,
	                                           MemMapFile.FILE_MAP_READ | MemMapFile.FILE_MAP_WRITE,
	                                           0, 0, 0);
	    }
	    
	    proxy = new MemMapProxy(messageDataReady+agentID+"A");
	    proxy.setObserver(this);
	}
	
	public void send(String message) {
		if (viewPtr != 0) {			 			 
			MemMapFile.writeToMem(viewPtr + messagePointer + dwMemFileSize/2,message);		        
			MemMapFile.broadcastNew(messageDataReadyID);
			if (messagePointer < 800000) {
				messagePointer += message.length()+1;
			} else {
				messagePointer = 0;			
			}
		}
	}

	public boolean onDataReady() {
		boolean ret = false;
		String allMessages = MemMapFile.readFromMem(viewPtr);
		int startIndex = 0;
		int endIndex = 0;

		if (!allMessages.equals("")) {
			String message = "";

			while ( (endIndex = allMessages.indexOf("\n\r", startIndex)) != -1 ) {
				message = allMessages.substring(startIndex, endIndex+2);
				startIndex = endIndex + 2;
				this.addReceivedMessage(message);									
			}
			
		} else {
			MemMapFile.writeToMem(viewPtr, "");
		
		}
		
		return ret;
	}


	@Override
	public void open() throws IOException {
		// TODO: put initialization here
	}
	
	@Override
	public void close() throws IOException {
		// TODO: not sure what to do...		
	}
	
	public static void main(String[] args){
		IpcConnection c = new IpcConnection("coordinator");

		while(true){
			try {
				Thread.sleep(0, Thread.MIN_PRIORITY);
				c.send("<action aaa \\><action bbb \\><action ccc\\>");
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}


}
