package model.action;

/**
 * Implements the action quitting the society
 * 
 * It must be taken into account if the agent is seasonal
 * 
 * @author Cyril Poulet
 * @since 2010-12-01
 */
public class QuitAction extends Action {

	public String fullToXML(int indentation) {
		// holds the answer for the method
		StringBuffer buffer = new StringBuffer();

		// applies the indentation
		for (int i = 0; i < indentation; i++)
			buffer.append("\t");

		// fills the buffer
		buffer.append("<action type=\"" + ActionTypes.QUIT + "\"/>\n");

		// returns the answer
		return buffer.toString();
	}

}
