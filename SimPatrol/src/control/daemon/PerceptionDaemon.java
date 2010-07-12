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
import model.graph.Graph;
import model.limitation.DepthLimitation;
import model.limitation.Limitation;
import model.limitation.StaminaLimitation;
import model.perception.EmptyPerception;
import model.perception.GraphPerception;
import model.perception.Perception;
import model.perception.PerceptionTypes;
import model.permission.PerceptionPermission;

/** Implements the daemons of SimPatrol that attend
 *  an agent's requisitions for perceptions. */
public final class PerceptionDaemon extends AgentDaemon {
	/* Attributes. */
	/** Registers if the daemon shall stop working. */
	private boolean stop_working;
	
	/** Registers if the daemon can attend requisitions for perceptions
	 *  at the moment. */
	private boolean can_attend;
	
	/** The graph of the simulation. */
	private Graph graph;
	
	/* Methods. */
	/** Constructor.
	 * 
	 * 	Doesn't initiate its own connection, as it will be shared with an
	 *  ActionDaemon object. So the connection must be set by the
	 *  setConenction() method.
	 *  @see ActionDaemon
	 *  
	 *  @param The name of the thread of the daemon.
	 *  @param agent The agent whose intentions are attended.
	 *  @param graph The graph of the simulation. */
	public PerceptionDaemon(String name, Agent agent, Graph graph) {
		super(name, agent);
		this.stop_working = false;
		this.can_attend = true;
		this.graph = graph;
	}
	
	/** Indicates that the daemon must stop working. */
	public void stopWorking() {
		this.stop_working = true;
		
		// stops its connection
		this.connection.stopWorking();
		
		// screen message
		System.out.println("[SimPatrol.PerceptionDaemon(" + this.agent.getObjectId() + ")]: Stopped working.");
	}
	
	/** Obtains the correspondent message for the given requisition for
	 *  perceptions of the graph of the simulation.
	 *  @param requisition The requisition for a perception of the graph of the simulation.
	 *  @return The perception of the graph, if allowed, or the empty perception, if not. */
	private Perception getGraphPerception(Requisition requisition) {
		// obtains the allowed perceptions for the agent
		PerceptionPermission[] permissions = this.agent.getAllowedPerceptions();
		
		// if one of the permissions is for perceptions of graph
		for(int i = 0; i < permissions.length; i++) {
			if(permissions[i].getPerception_type() == PerceptionTypes.GRAPH_PERCEPTION) {
				// holds an eventual depth limitation
				int depth = -1;
				
				// holds an eventual stamina limitaion
				double stamina = 0;										
				
				// obtains the limitations for this permission
				Limitation[] limitations = permissions[i].getLimitations();
				
				// for each limitation, sets the depth and stamina values
				for(int j = 0; j < limitations.length; j++)
					if(limitations[j] instanceof DepthLimitation)
						depth = ((DepthLimitation) limitations[j]).getDepth();
					else if(limitations[j] instanceof StaminaLimitation)
						stamina = ((StaminaLimitation) limitations[j]).getCost();						
     				// Warning: new limitations should change the code here!
				
				// if there's enough stamina to perceive
				if(stamina <= this.agent.getStamina()) {
					// decrements the stamina of the agent
					this.agent.decStamina(stamina);
					
					// obtains the perceived subgraph
					Graph subgraph = this.graph.getVisibleSubgraph(this.agent.getVertex(), depth);
					
					// returns the graph perception
					return new GraphPerception(subgraph);
				}
			}
		}
		
		// the agent is not allowed to perceive graphs
		return new EmptyPerception();
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
					case(PerceptionTypes.EMPTY_PERCEPTION): {
						// creates an empty perception
						perception = new EmptyPerception();						
						break;
					}
					
					// if the perception is a graph one
					case(PerceptionTypes.GRAPH_PERCEPTION): {
						// obtains the graph perception
						perception = this.getGraphPerception(requisition);
						break;
					}
					
					// Warning: new perceptions shall change the code here!
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