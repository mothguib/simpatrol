package model.action;

/**
 * Implements the action of changing the agent of open society
 * 
 * It must be taken into account if the agent is seasonal
 * It does not need permission, just being seasonal.
 * 
 * @author Cyril Poulet
 * @since 2011-06-01
 */
public class ChangeSocietyAction extends AtomicAction {

	private String society;
	
	public ChangeSocietyAction(String society){
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
		buffer.append("<action type=\"" + ActionTypes.CHANGE_SOCIETY + "\" society_id=\"" + this.society + "\"/>\n");

		// returns the answer
		return buffer.toString();
	}

}
