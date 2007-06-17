/* SocietyManipulatorDaemon.java */

/* The package of this class. */
package control.daemon;

/* Imported classes and/or interfaces. */
import java.io.IOException;
import java.net.SocketException;
import java.util.HashSet;
import java.util.Set;
import javax.xml.parsers.ParserConfigurationException;
import org.xml.sax.SAXException;
import view.message.Message;
import view.message.MessageTypes;
import model.agent.Society;
import model.graph.Graph;
import control.parser.MessageTranslator;

/** Implements the daemon responsible for manipulating
 *  the societies of agents of a simulation. */
public final class SocietyManipulatorDaemon extends Daemon {
	/* Attributes. */
	/** Registers if the daemon shall stop working. */
	private boolean stop_working;
	
	/** The graph of the simulation. */
	private Graph graph;
	
	/** The societies of the simulation. */
	private Set<Society> societies;
	
	/** Registers if more societies can be added to the simulation. */
	private boolean can_add_societies;
	
	/* Methods. */
	/** Constructor.
	 * 	@param local_socket_number The number of the local UDP socket.
	 *  @param graph The graph of the simulation. 
	 *  @throws SocketException */	
	public SocietyManipulatorDaemon(int local_socket_number, Graph graph) throws SocketException {
		super(local_socket_number);
		this.graph = graph;
		this.societies = new HashSet<Society>();
		this.can_add_societies = true;
	}
	
	/** Indicates that the daemon must stop working. */
	public void stopWorking() {
		this.stop_working = true;
	}
	
	/** Treats the creation of a new society.
	 *  @param received_message The message asking for the creation of a new society. 
	 *  @throws IOException */
	private void createSociety(Message received_message) throws IOException, ClassCastException {
		// verifies if a new society can be created
		if(!this.can_add_societies) {
			// sends an error message
			Message answer = new Message(MessageTypes.NEW_SOCIETIES_NOT_ALLOWED, null);
			this.connection.send(answer.toXML(0), received_message.getSender_address(), received_message.getSender_socket_number());
			
			// quits the method
			return;
		}
		
		// the society to be created
		Society society = null;
		
		// obtains the society to be created
		try { society = (Society) received_message.getContent(); }
		catch(ClassCastException e) { throw e; }
		
		// adds the society to the set of societies
		this.societies.add(society);
		
		// sends a succesful message
		Message answer = new Message(MessageTypes.ANSWER, society);
		this.connection.send(answer.toXML(0), received_message.getSender_address(), received_message.getSender_socket_number());
	}
		
	public void run() {
		while(!this.stop_working) {
			// tries to obtain a message from the buffer
			String str_message = this.buffer.remove();
			
			// if there's a message
			if(str_message != null) {
				// obtains the message object
				Message message = null;
				try { message = MessageTranslator.getMessage(str_message, this.graph.getVertexes(), this.graph.getEdges()); }
				catch (ParserConfigurationException e) { e.printStackTrace(); }
				catch (SAXException e) { e.printStackTrace(); }
				catch (IOException e) { e.printStackTrace(); }
				
				// depending on the type of the message
				switch(message.getType()) {
					case(MessageTypes.SOCIETY_CREATION): {
						try { this.createSociety(message); }
						catch (ClassCastException e) { e.printStackTrace(); }
						catch (IOException e) { e.printStackTrace(); }
						
						break;
					}
				}				
			}
		}
		
		// stops the connection
		this.connection.stopWorking();
	}
}
