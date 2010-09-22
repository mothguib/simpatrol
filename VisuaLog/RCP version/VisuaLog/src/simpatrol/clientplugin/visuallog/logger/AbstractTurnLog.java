package simpatrol.clientplugin.visuallog.logger;

import java.io.IOException;
import javax.xml.parsers.ParserConfigurationException;
import org.w3c.dom.Element;
import org.xml.sax.SAXException;


/* * * * * * * * * * * * * * * * * * * * * * * *
 * 
 *    Overall class for the logs of turn-based simulations.
 *    It extends the managing of events
 *    
 *    @author : Cyril Poulet
 * 
 * * * * * * * * * * * * * * * * * * * * * * * */
public abstract class AbstractTurnLog extends AbstractLog {
	
	
	/* Attributes. */
	
	/** replay turn */
	protected int turn = 0;
	
	/** turn at which the next event is coming*/
	protected int incoming_turn = 0;
	
	
	
	/** 
	 * getter for the variable turn
	 * @return int turn
	 */
	public int get_turn(){
		return turn;
	}


	/**
	 *  Manages a list of events as read in the file/stream
	 * 
	 * @param event
	 * 			a String[] containing the events as read in the file/stream
	 * @return boolean skip, used to know if the player must make a slight pause 
	 *         (for example to make possible the observation of visits)
	 * @throws Exception : parser exception
	 */
	protected boolean manage_events(String[] events) throws Exception{
		boolean skip = false;
		for(String event : events){
			try {
				Element my_event = manage_event(event);
				
				String turn_val = my_event.getAttribute("time");
				incoming_turn = Double.valueOf(turn_val).intValue();
				
				String skip_txt = my_event.getAttribute("skip");
				skip |= Boolean.valueOf(skip_txt);	
				
			} catch (ParserConfigurationException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (SAXException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		}
		return skip;
	}

}
