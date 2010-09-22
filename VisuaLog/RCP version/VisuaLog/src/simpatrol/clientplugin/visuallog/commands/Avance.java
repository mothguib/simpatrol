package simpatrol.clientplugin.visuallog.commands;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;

import simpatrol.clientplugin.visuallog.Application;
import simpatrol.clientplugin.visuallog.logger.ReplayLog;


  /* * * * * * * * * * * * * * * * * * * * * * * *
   * 
   *    Handler for the "advance" button.
   *    
   *    This handler multiplies by 2 the replay
   *    speed each time the button is clicked 
   * 
   * * * * * * * * * * * * * * * * * * * * * * * */
public class Avance extends AbstractHandler {

	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		
		String logger_type = Application.logger.getType();
		
		if(logger_type.contains("replay")){
			((ReplayLog)Application.logger).setSpeed(((ReplayLog)Application.logger).getSpeed() *2);
		}
		return null;
	}

}
