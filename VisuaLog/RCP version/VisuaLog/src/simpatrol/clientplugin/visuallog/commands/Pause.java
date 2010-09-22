package simpatrol.clientplugin.visuallog.commands;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;

import simpatrol.clientplugin.visuallog.Application;
import simpatrol.clientplugin.visuallog.logger.AbstractLog;
import simpatrol.clientplugin.visuallog.logger.ReplayLog;

/* * * * * * * * * * * * * * * * * * * * * * * *
 * 
 *    Handler for the "pause" button.
 *    
 *    This handler stops the replay if it is 
 *    playing, and launches it it is stopped
 *    
 *    @author : Cyril Poulet
 * 
 * * * * * * * * * * * * * * * * * * * * * * * */
public class Pause extends AbstractHandler {

	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		
		synchronized(((AbstractLog)(Application.logger))){
			
			String logger_type = Application.logger.getType();
			
			if(logger_type.contains("replay")){
				if(((ReplayLog)(Application.logger)).isPlaying())
					((ReplayLog)(Application.logger)).pause();
				else
					((ReplayLog)(Application.logger)).play();
			}
		}
		return null;
	}


}
