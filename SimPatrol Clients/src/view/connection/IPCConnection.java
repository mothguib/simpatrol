package view.connection;

import java.io.IOException;

import util.ipc.MemMapFile;
import util.ipc.MemMapFileObserver;
import util.ipc.MemMapProxy;
import util.net.ClientConnection;


public class IPCConnection extends ClientConnection implements MemMapFileObserver /*, IMessageSubject*/ {
	
	protected boolean stop_working;
	private int mycount = 0;
	private int mapFilePtr;
	private int viewPtr;
	private String agentId;
	public final int dwMemFileSize = 2 *1024 * 1024;
	public int writen = 0;
	private int messagePointer = 0;
	public static final String fileMappingObjName = "Mem_Map_File-{5B85BE0C-F89E-450e-80BE-5707813931E5}";
	private final String messageDataReady  = "UWM_DATA_READY_MSG-{7FDB2CB4-5510-4d30-99A9-CD7752E0D681}";
	private int messageDataReadyID = -1;
	private Object myAgent = null;
	protected MemMapProxy proxy;

	public IPCConnection(String agentID, Object agent) {
		super(agentID);
		myAgent = agent;
		this.agentId = agentID;
		String fileName = fileMappingObjName+agentID;
		//System.err.println(fileName);
		mapFilePtr = MemMapFile.createFileMapping(MemMapFile.PAGE_READWRITE, 0, dwMemFileSize, fileName);
		if(messageDataReadyID == -1 ) messageDataReadyID = MemMapFile.registerMessage(messageDataReady+agentID+"B");
		
	    if(mapFilePtr != 0) {
	      viewPtr = MemMapFile.mapViewOfFile(mapFilePtr,
	                                         MemMapFile.FILE_MAP_READ |
	                                         MemMapFile.FILE_MAP_WRITE,
	                                         0, 0, 0);
	    }
	    proxy = MemMapProxy.getInstance(messageDataReady+agentID+"A");
	    //proxy = MemMapProxy.getInstance();
	    proxy.setObserver(this);
	}
	
	public void send(String message) {
		if(viewPtr != 0) {			 			 
			 MemMapFile.writeToMem(viewPtr+ messagePointer + dwMemFileSize/2,message);		        
			 MemMapFile.broadcastNew(messageDataReadyID);
			 if(messagePointer < 800000 )
					messagePointer += message.length()+1;
				else messagePointer = 0;			
	      }
	}

	public boolean onDataReady() {
		boolean ret = false;
		try {
			ret = receive();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return ret;
	}

	@Override
	protected boolean receive() throws IOException {
		boolean ret = false;
		//String allMessages = MemMapFile.readFromMem(viewPtr+messagePointer);
		String allMessages = MemMapFile.readFromMem(viewPtr);
		//System.err.println(agentId+" " +System.currentTimeMillis()+" "+allMessages);
		int startIndex = 0;
		int endIndex = 0;
		if( !allMessages.equals("")) {
			String message = "";
			while( (endIndex = allMessages.indexOf("\n\r", startIndex)) != -1 ){
				message = allMessages.substring(startIndex, endIndex+2);
				startIndex = endIndex + 2;
				this.BUFFER.add(message);									
			}
			
			synchronized (this.myAgent) {
				this.myAgent.notify();
			}			
		} else {
			MemMapFile.writeToMem(viewPtr, "");
		}
		
		/*if( writen == 999 ){
			System.out.println("Mensagens "+this.BUFFER.getSize() );
			while(this.BUFFER.getSize() > 0){
				String str = this.BUFFER.remove();
				System.out.println("\t"+str);
			}
		}*/
		
		//Clear Memory buffer		
		//System.err.println(mycount+" "+System.currentTimeMillis());
		
		
		/*if( !message.equals("")) {
			this.BUFFER.insert(message);
			
			MemMapFile.writeToMem(viewPtr, "");
			if( this.myAgent != null )
				synchronized (this.myAgent) {
					this.myAgent.notify();
				}
		}*/
		//System.err.println("Client2: " +System.currentTimeMillis()+message);
		
		// TODO Auto-generated method stub
		return ret;
	}
	public static void main(String[] args){
		IPCConnection c = new IPCConnection("coordinator", null);
		//IPCConnection c3 = new IPCConnection("agent", null);
		while(true){
			try {
				Thread.sleep(0, MIN_PRIORITY);
				c.send("<action aaa \\><action bbb \\><action ccc\\>");
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

//	@Override
//	public void updateObservers() {
//		
//		// TODO Auto-generated method stub
//		
//	}
//
//	@Override
//	public void addObserver(IMessageObserver observer) {
//		// TODO Auto-generated method stub
//		
//	}

}
