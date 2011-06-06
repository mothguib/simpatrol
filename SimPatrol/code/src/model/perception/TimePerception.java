package model.perception;


/**
 * Implements the ability of an agent to perceive the internal time of the simulation.
 * This can be useful for synch ability (like activating or deactivating at certain time)
 * 
 * Time Perception is available to inactive agents 
 * 
 * @author Cyril Poulet
 * @date 2011/06/01
 */
public class TimePerception extends Perception {

	private double time;
	
	public TimePerception(double time){
		this.time = time;
	}
	
	
	public String fullToXML(int identation) {
		// holds the answer for the method
		StringBuffer buffer = new StringBuffer();

		// applies the identation and opens the "perception" tag
		for (int i = 0; i < identation; i++)
			buffer.append("\t");
		buffer.append("<perception type=\"" + PerceptionTypes.TIME + "\" time=\"" + this.time + "\">\n");
		
		// applies the identation and closes the "perception" tag
		for (int i = 0; i < identation; i++)
			buffer.append("\t");
		buffer.append("</perception>\n");
		// returns the answer
		return buffer.toString();
	}

}
