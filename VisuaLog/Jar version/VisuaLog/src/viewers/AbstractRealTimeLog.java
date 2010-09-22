package viewers;

import java.io.IOException;
import java.util.Date;
import javax.xml.parsers.ParserConfigurationException;
import org.w3c.dom.Element;
import org.xml.sax.SAXException;

/* * * * * * * * * * * * * * * * * * * * * * * *
 * 
 *    Overall class for the logs of real-time simulations.
 *    It extends the managing of events
 *    
 *    @author : Cyril Poulet
 * 
 * * * * * * * * * * * * * * * * * * * * * * * */
public abstract class AbstractRealTimeLog extends AbstractLog {

	/* Attributes. */
	
	/** time of the occurence of the next event */
	protected long next_event;
	
	/** 
	 *  start of the "skip" event, used for events that need a certain time lapse 
	 *  like the visits 
	 * */
	protected long skip_start; 
	
	/** 
	 *  elapsed playing time since the start of the simulation replay (without pause time)
	 * */
	protected long elapsed_time;
	
	/** 
	 *  time of the last change of the REPLAY_SPEED
	 * */
	protected long last_speed_change;


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
				next_event = (long)(Double.valueOf(turn_val) * 1000);
				
				String skip_txt = my_event.getAttribute("skip");
				skip |= Boolean.valueOf(skip_txt);	
				if(skip)
					skip_start = new Date().getTime();
				else if(new Date().getTime() - skip_start < SKIP_DURATION)
					skip = true;
					
				
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
