package view.connection;

import model.agent.Agent;
import control.daemon.ActionDaemon;
import control.daemon.PerceptionDaemon;
import sun.awt.windows.ThemeReader;
import util.data_structures.Queue;
import util.ipc.MemMapFile;
import util.ipc.MemMapFileObserver;
import util.ipc.MemMapProxy;
import view.connection.IPCConnection;

public class ServerSideIPCConnection extends IPCConnection implements MemMapFileObserver {
	
	/* Attributes. */
	/** The buffer where the connection writes the received perception messages. */
	private final Queue<String> PERCEPTION_BUFFER;

	/** The buffer where the connection writes the received action messages. */
	private final Queue<String> ACTION_BUFFER;
	private ActionDaemon action;
	
	private static int count = 0;
	private int sendMessageCount = 0;
	private int mycount = 0;
	private int messagePointer = 0;
    private int mapFilePtr;
    private int viewPtr;
    public final int dwMemFileSize = 2 * 1024 * 1024;
    public int writen = 0;
    private String agentId;
    public static final String fileMappingObjName = "Mem_Map_File-{5B85BE0C-F89E-450e-80BE-5707813931E5}";
    private static final String messageDataReady = "UWM_DATA_READY_MSG-{7FDB2CB4-5510-4d30-99A9-CD7752E0D681}";
    private int messageDataReadyID = -1;
    protected MemMapProxy proxy;
	

	public ServerSideIPCConnection(String name,
			Queue<String> perception_buffer, Queue<String> action_buffer) {
		super(name, perception_buffer);
		agentId = name;
		mycount = count++;
		this.PERCEPTION_BUFFER = this.BUFFER;
		this.ACTION_BUFFER = action_buffer;
		String fileName = fileMappingObjName+agentId;
		//System.err.println(fileName);
		mapFilePtr = MemMapFile.createFileMapping(MemMapFile.PAGE_READWRITE, 0, dwMemFileSize, fileName);
		if(messageDataReadyID == -1 ) 
			messageDataReadyID = MemMapFile.registerMessage(messageDataReady+name+"A");
	    if(mapFilePtr != 0) {
	      viewPtr = MemMapFile.mapViewOfFile(mapFilePtr,
	                                         MemMapFile.FILE_MAP_READ |
	                                         MemMapFile.FILE_MAP_WRITE,
	                                         0, 0, 0);
	    }
	    MemMapFile.writeToMem(viewPtr + dwMemFileSize/2,"");
	    MemMapFile.writeToMem(viewPtr ,"");
	    proxy = MemMapProxy.getInstance(messageDataReady+name+"B");
	    //proxy = MemMapProxy.getInstance();
	    proxy.setObserver(this);
	}

	public void setAction(ActionDaemon action) {
		this.action = action;
	}
	@Override
	public boolean send(String message) {
		if(viewPtr != 0) {
			
			boolean notRead = true;
			
			 //MemMapFile.writeToMem(viewPtr+messagePointer,message );
			 MemMapFile.writeToMem(viewPtr,message );
			
			 //System.err.println("Send Perception "+this.agentId +" "+message);
			 //messagePointer += message.length()+1;
	          //sendMessageCount++;
	          //System.err.println("Server: " +System.currentTimeMillis());			
			 MemMapFile.broadcastNew(messageDataReadyID);
			 
			 
			/* while(notRead){
				 try {
					Thread.sleep(1);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				 String message2 = MemMapFile.readFromMem(viewPtr);
				 if( message2.equals("")) notRead = false;
			 }*/
			 
			 
			 
	        /*try {
				Thread.sleep(1);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}*/
	          //Thread.yield();
	          return true;
	        }
		return false;
	}

	@Override
	public int getSocketNumber() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public boolean onDataReady() {
		String allMessages = MemMapFile.readFromMem(viewPtr + messagePointer +dwMemFileSize/2);
		//Clear Memory buffer
		String message = "";
		if(!allMessages.equals("")){
		//MemMapFile.writeToMem(viewPtr + dwMemFileSize/2, "");
			if (allMessages.indexOf("<action ") > -1){
				//newcode
				
				int startIndex = 0;
				int endIndex = 0;
				while( (endIndex = allMessages.indexOf(">", startIndex)) != -1 ){
					message = allMessages.substring(startIndex, endIndex+1);
					startIndex = endIndex+1;					
					this.ACTION_BUFFER.insert(message);					
				}				
				synchronized (action) {
					action.notify();
				}				
			}
			else{
				this.PERCEPTION_BUFFER.insert(message);
			}
			if(messagePointer < 800000 )
				messagePointer += allMessages.length()+1;
			else messagePointer = 0;
		}
		return true;
		//}
	}
	public void start(int local_socket_number){
		//super.start();
	}
	
	
	public static void main(String[] args){
		
		Queue<String> ac = new Queue<String>();
		IPCConnection c = new ServerSideIPCConnection("coordinator", null, ac);
		/*IPCConnection c1 = new ServerSideIPCConnection("agent", null, null);
		//String message = "aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa";
		for(int k=0; k < 1000;k++){
			//message += "aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa";
		}
		System.err.println("Start: " +System.currentTimeMillis());
		for(int i=0; i< 1000;i++){
			
			System.err.println("Start: " +System.currentTimeMillis()+" "+i);
			
			c.send("message: "+i);
			c1.send("message: "+i);
		}*/
		while(true){
			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}		
	}
	
	/*public void run(){
		while(true){
			//System.err.println("Teste "+this);
			String message = MemMapFile.readFromMem(viewPtr + dwMemFileSize/2);
			//Clear Memory buffer
			if(!message.equals("")) this.onDataReady();
			Thread.yield();
		}
	}*/
	

}
