package simpatrol.clientplugin.visuallog.commands;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;

import simpatrol.clientplugin.visuallog.Application;
import simpatrol.clientplugin.visuallog.logger.AbstractLog;
import simpatrol.clientplugin.visuallog.logger.ReplayLog;


/* * * * * * * * * * * * * * * * * * * * * * * *
 * 
 *    Handler for the "Play" button.
 *    
 *    This handler launches the replay with a 
 *    speed of 1
 *    
 *     @author : Cyril Poulet
 * 
 * * * * * * * * * * * * * * * * * * * * * * * */
public class Play extends AbstractHandler {

	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		
		
		synchronized(((AbstractLog)(Application.logger))){
			
			String logger_type = Application.logger.getType();
			
			if(logger_type.contains("replay")){
				if(!((ReplayLog)(Application.logger)).isPlaying())
					((ReplayLog)(Application.logger)).play();
				
				((ReplayLog)(Application.logger)).setSpeed(1);
			}
		}
		return null;
	}


}
