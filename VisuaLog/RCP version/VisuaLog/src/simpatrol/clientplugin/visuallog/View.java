package simpatrol.clientplugin.visuallog;


import java.io.IOException;

import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.awt.SWT_AWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.part.ViewPart;

import simpatrol.clientplugin.visuallog.logger.ConnectedTurnLog;
import simpatrol.clientplugin.visuallog.logger.ReplayRealTimeLog;
import simpatrol.clientplugin.visuallog.logger.ReplayTurnLog;
import simpatrol.clientplugin.visuallog.visualparts.VisualCanvas;
import simpatrol.clientplugin.visuallog.visualparts.VisualCanvasMouseListener;

public class View extends ViewPart {
	public static final String ID = "simpatrol.clientplugin.visuallog.view";

	private VisualCanvas viewer;
	// ReplayTurnLog logger;

	/**
	 * The content provider class is responsible for providing objects to the
	 * view. It can wrap existing objects in adapters or simply return objects
	 * as-is. These objects may be sensitive to the current input of the view,
	 * or ignore it and always show the same content (like Task List, for
	 * example).
	 */
	class ViewContentProvider implements IStructuredContentProvider {
		public void inputChanged(Viewer v, Object oldInput, Object newInput) {
		}

		public void dispose() {
		}

		public Object[] getElements(Object parent) {
			if (parent instanceof Object[]) {
				return (Object[]) parent;
			}
	        return new Object[0];
		}
	}

	class ViewLabelProvider extends LabelProvider implements
			ITableLabelProvider {
		public String getColumnText(Object obj, int index) {
			return getText(obj);
		}

		public Image getColumnImage(Object obj, int index) {
			return getImage(obj);
		}

		public Image getImage(Object obj) {
			return PlatformUI.getWorkbench().getSharedImages().getImage(
					ISharedImages.IMG_OBJ_ELEMENT);
		}
	}

	/**
	 * This is a callback that will allow us to create the viewer and initialize
	 * it.
	 */
	public void createPartControl(Composite parent) {
		Composite swtAwtComponent = new Composite(parent, SWT.EMBEDDED);
		java.awt.Frame frame = SWT_AWT.new_Frame( swtAwtComponent );
		Application.canvas = new VisualCanvas();
		frame.add(Application.canvas);
		new VisualCanvasMouseListener(Application.canvas);
		
		/*
		try {
			//Application.logger = new ConnectedTurnLog(viewer, "132.227.205.51", 5635,"/home/pouletc/workspace_brezil/test_sim/events.txt");
			//Application.logger = new ReplayRealTimeLog(viewer, "/home/pouletc/workspace_brezil/test_sim/events.txt");
			//Application.logger = new ReplayTurnLog(viewer, "/home/pouletc/workspace_brezil/test_sim/events.txt");

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
		viewer.configure_canvas();
		Application.logger.start();
		*/
		
		
		
 
	}

	@Override
	public void setFocus() {
		// TODO Auto-generated method stub
		
	}

}
