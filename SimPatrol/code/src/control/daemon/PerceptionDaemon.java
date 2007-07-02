/* PerceptionDaemon.java */

/* The package of this class. */
package control.daemon;

/* Imported classes and/or interfaces. */
import java.io.IOException;

import javax.xml.parsers.ParserConfigurationException;

import org.xml.sax.SAXException;

import view.message.Message;
import control.requisition.Answer;
import control.requisition.Requisition;
import control.translator.MessageTranslator;
import model.agent.Agent;
import model.agent.AgentStates;
import model.perception.EmptyPerception;
import model.perception.Perception;
import model.perception.PerceptionTypes;

/** Implements the daemons of SimPatrol that attend
 *  an agent's requisitions for perceptions. */
public final class PerceptionDaemon extends AgentDaemon {
	/* Attributes. */
	/** Registers if the daemon shall stop working. */
	private boolean stop_working;
	
	/** Registers if the daemon can attend requisitions for perceptions
	 *  at the moment. */
	private boolean can_attend;
	
	/* Methods. */
	/** Constructor.
	 * 
	 * 	Doesn't initiate its own connection, as it will be shared with an
	 *  ActionDaemon object. So the connection must be set by the
	 *  setConenction() method.
	 *  @see ActionDaemon
	 *  
	 *  @param agent The agent whose intentions are attended. */
	public PerceptionDaemon(Agent agent) {
		super(agent);
		this.stop_working = false;
		this.can_attend = true;
	}
	
	/** Indicates that the daemon must stop working. */
	public void stopWorking() {
		this.stop_working = true;
		
		// stops its connection
		this.connection.stopWorking();
		
		// screen message
		System.out.println("[SimPatrol.PerceptionDaemon(" + this.agent.getObjectId() + ")]: Stopped working.");
	}
	
	public void run() {
		// screen message
		System.out.println("[SimPatrol.PerceptionDaemon(" + this.agent.getObjectId() + ")]: Listening to some requisition...");
		
		while(!this.stop_working) {
			// obtains a string message from the buffer
			String str_message = null;
			while(str_message == null)
				str_message = this.buffer.remove();
						
			// obtains the message object
			Message message;
			try { message = MessageTranslator.getMessage(str_message); }
			catch (ParserConfigurationException e) { message = null; e.printStackTrace(); }
			catch (SAXException e) { message = null; e.printStackTrace(); }
			catch (IOException e) { message = null; e.printStackTrace(); }
			
			// if there's a message and its content is a requisition
			if(message != null && message.getContent() instanceof Requisition) {
				// obtains the requisition
				Requisition requisition = (Requisition) message.getContent();
				
				// screen message
				System.out.println("[SimPatrol.PerceptionDaemon(" + this.agent.getObjectId() + ")]: Requisition obtained:");
				System.out.print(requisition.toXML(0));
				
				// the perception to be sent to the remote agent
				Perception perception = null;
				
				// while can't attend the requisition, do nothing
				while(!this.can_attend);
				
				// depending on the type of the requisition
				switch(requisition.getPerception_type()) {
					// if the perception is an empty one
					case(PerceptionTypes.EMPTY): {
						// creates an empty perception
						perception = new EmptyPerception();						
						break;
					}
					
					// TODO continuar!!!
				}
				
				// if the perception is valid
				if(perception != null) {
					// mount an answer message
					Message answer_message = new Message(new Answer(perception));
					
					// tries to send it to the remote agent
					try {
						// sending
						this.connection.send(answer_message.toXML(0));
						
						// registers that the agent just perceived
						this.agent.setState(AgentStates.JUST_PERCEIVED);
						
						// screen message
						System.out.println("[SimPatrol.PerceptionDaemon(" + this.agent.getObjectId() + ")]: Perception produced:");
						System.out.print(perception.toXML(0));
					}
					catch (IOException e) { e.printStackTrace(); }
				}
			}
		}
	}
}