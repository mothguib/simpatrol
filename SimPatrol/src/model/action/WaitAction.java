package model.action;

public class WaitAction extends Action {

	public WaitAction() {		
	}
	
	
	public String fullToXML(int identation) {
		// holds the answer for the method
		StringBuffer buffer = new StringBuffer();

		// applies the identation
		for (int i = 0; i < identation; i++)
			buffer.append("\t");

		// fills the buffer
		buffer.append("<action type=\"" + ActionTypes.WAIT + "\"/>\n");

		// returns the answer
		return buffer.toString();
	}

}
