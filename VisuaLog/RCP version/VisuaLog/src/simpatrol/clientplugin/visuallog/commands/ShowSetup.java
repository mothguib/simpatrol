package simpatrol.clientplugin.visuallog.commands;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.ui.handlers.HandlerUtil;

import simpatrol.clientplugin.visuallog.visualparts.MySetup;


/* * * * * * * * * * * * * * * * * * * * * * * *
 * 
 *    Handler for the "Setup" menu file.
 *    
 *    This handler launches the setup wizard
 *    
 *    @author : Cyril Poulet
 * 
 * * * * * * * * * * * * * * * * * * * * * * * */
public class ShowSetup extends AbstractHandler {

	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		MySetup wizard = new MySetup();
		WizardDialog dialog = new WizardDialog(HandlerUtil
				.getActiveShell(event), wizard);
		dialog.open();
		return null;
	}

}

