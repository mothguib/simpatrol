package model.action;



/**
 * Implements the action of activating the agent (or entering the society)
 * 
 * It must be taken into account if the agent is seasonal
 * It does not need permission, just being seasonal.
 * 
 * @author Cyril Poulet
 * @since 2011-06-01
 */
public class ActivateAction extends AtomicAction {
	
	/* the society to join */
	private String society;
	
	public ActivateAction(String society){
		this.society = society;
	}
	
	public String getSocietyId(){
		return this.society;
	}

	public String fullToXML(int indentation) {
		// holds the answer for the method
		StringBuffer buffer = new StringBuffer();

		// applies the indentation
		for (int i = 0; i < indentation; i++)
			buffer.append("\t");

		// fills the buffer
		buffer.append("<action type=\"" + ActionTypes.ACTIVATE + "\" society_id=\"" + this.society + "\"/>\n");

		// returns the answer
		return buffer.toString();
	}

}
