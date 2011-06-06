package control.event;

import model.agent.OpenSociety;


/**
 * Implements the events that are related to the change of society of an agent.
 * 
 * @author Cyril Poulet
 * @since 2011/06/01
 */
public class AgentChangingSocietyEvent extends AgentEvent {

	/* the new society id */
	private String society;
	
	public AgentChangingSocietyEvent(String agent_id, OpenSociety society) {
		super(agent_id);
		this.society = society.getLabel();
	}

	public String fullToXML(int identation, double event_time) {
		// holds the answer for the method
		StringBuffer buffer = new StringBuffer();

		// applies the identation
		for (int i = 0; i < identation; i++)
			buffer.append("\t");

		// fills the buffer
		buffer.append("<event type=\"" + EventTypes.AGENT_ACTIVATING + "\" time=\""
				+ event_time + "\" agent_id=\"" + this.AGENT_ID + "\" society_id=\"" + this.society + "\"/>\n");

		// returns the answer
		return buffer.toString();
	}

}
