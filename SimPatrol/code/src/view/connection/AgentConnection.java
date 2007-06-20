/* AgentConnection.java */

/* The package of this class. */
package view.connection;

/* Imported classes and/or interfaces. */
import java.io.IOException;
import java.net.SocketException;
import util.Queue;

/** Implements the connections with the external agents. */
public final class AgentConnection extends Connection {
	/* Attributes. */
	/** The buffer where the connection writes the received
	 *  perception messages. */
	private Queue<String> perception_buffer;
	
	/** The buffer where the connection writes the received
	 *  action messages. */
	private Queue<String> action_buffer;
	
	/* Methods. */
	/** Constructor.
	 *  @throws SocketException */
	public AgentConnection(int local_socket_number, Queue<String> perception_buffer, Queue<String> action_buffer) throws SocketException {
		super(local_socket_number, perception_buffer);
		this.perception_buffer = this.buffer;
		this.action_buffer = action_buffer;
	}
	
	public void run() {
		while(!this.stop_working) {
			String message = null;
			
			try {
				message = this.socket.receive();
			}
			catch (IOException e) {
				e.printStackTrace();
			}
			
			// decides if the message is a perception or action
			// related message
			StringBuffer buffer = new StringBuffer(message);
			int ref_index = buffer.indexOf("<requisition");
			buffer.delete(0, ref_index);
			ref_index = buffer.indexOf("type");
			buffer.delete(0, ref_index);
			ref_index = buffer.indexOf("\"");
			buffer.delete(0, ref_index);
			ref_index = buffer.indexOf("\"");
			String ref = buffer.substring(0, ref_index);
			
			// TODO corrigir abaixo, colocando requisition types!!!
			if(Integer.parseInt(ref) == 0) this.perception_buffer.insert(message);
			else this.action_buffer.insert(message);
		}
	}	
}
