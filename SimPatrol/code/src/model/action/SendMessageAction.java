package model.action;

import model.limitation.DepthLimitation;
import model.limitation.StaminaLimitation;


/**
 * Implements the action of sending a message to an other agent IN THE SAME SOCIETY.
 * 
 * Its effect can be controlled by depth and stamina limitations.
 * 
 * @see DepthLimitation
 * @see StaminaLimitation
 * 
 * @author Cyril Poulet
 * @since 01/06/2011
 */
public class SendMessageAction extends AtomicAction {
	/* Attributes. */
	/** The id of the agent the message is sent to */
	private String target_agent;
	
	/** The id of the agent sending the message */
	private String sender_agent;
	
	/** The message to be sent. */
	private String message;

	/** The depth of communication. */
	private int message_depth;

	/* Methods. */
	/**
	 * Constructor.
	 * 
	 * @param agent
	 * 			  The agent to send the message to
	 * @param message
	 *            The message to be broadcasted.
	 * @param message_depth
	 *            The depth of the graph that the broadcasted message must
	 *            reach.
	 */
	public SendMessageAction(String sender_agent, String target_agent, String message, int message_depth) {
		this.sender_agent = sender_agent;
		this.target_agent = target_agent;
		this.message = message;
		this.message_depth = message_depth;
	}

	/**
	 * Constructor.
	 * The "sender" field can be filled later
	 * 
	 * @param agent
	 * 			  The agent to send the message to
	 * @param message
	 *            The message to be broadcasted.
	 * @param message_depth
	 *            The depth of the graph that the broadcasted message must
	 *            reach.
	 */
	public SendMessageAction(String target_agent, String message, int message_depth) {
		this.target_agent = target_agent;
		this.message = message;
		this.message_depth = message_depth;
	}
	
	/**
	 * returns the agent that sends the message
	 * 
	 * @return
	 * 		the id of the agent sending the message
	 */
	public String getSenderAgent(){
		return this.sender_agent;
	}
	
	/**
	 * returns the agent that sends the message
	 * 
	 * @return
	 * 		the id of the agent sending the message
	 */
	public void setSenderAgent(String sender){
		this.sender_agent = sender;
	}
	
	
	/**
	 * returns the agent that must get the message
	 * 
	 * @return
	 * 		the id of the agent to reach
	 */
	public String getTargetAgent(){
		return this.target_agent;
	}
	
	/**
	 * Returns the broadcasted message.
	 * 
	 * @return The message to be broadcasted.
	 */
	public String getMessage() {
		return this.message;
	}

	/**
	 * Returns the depth of the graph that the broadcasted message must reach.
	 * 
	 * @return The depth of the graph that the broadcasted message must reach.
	 */
	public int getMessage_depth() {
		return this.message_depth;
	}

	public String fullToXML(int identation) {
		// holds the answer for the method
		StringBuffer buffer = new StringBuffer();

		// applies the identation
		for (int i = 0; i < identation; i++)
			buffer.append("\t");

		// fills the buffer
		buffer.append("<action type=\"" + ActionTypes.SEND_MESSAGE
				+ "\" sender_agent=\"" + this.sender_agent 
				+ "\" target_agent=\"" + this.target_agent 
				+ "\" message=\"" + this.message + "\" message_depth=\""
				+ this.message_depth + "\"/>\n");

		// returns the answer
		return buffer.toString();
	}
}
