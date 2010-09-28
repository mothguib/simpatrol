package simpatrol.clientplugin.visuallog.visualparts;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;
import java.util.List;

/* * * * * * * * * * * * * * * * * * * * * * * *
 * 
 *    Mouse Listener associated to the VisualCanvas
 *    
 *    As of today (sept 2010) only the NodeCircles are 
 *    moved by the mouse listener
 *    
 *    @author : Cyril Poulet
 * 
 * * * * * * * * * * * * * * * * * * * * * * * */
public class VisualCanvasMouseListener extends MouseAdapter implements MouseMotionListener {

	protected VisualCanvas canvas;
	protected IDrawable drawable;

	
	/**
	 * Constructor
	 * 
	 * @param canvas
	 */
	public VisualCanvasMouseListener(VisualCanvas canvas) {
		super();
		this.canvas = canvas;
		canvas.addMouseListener(this);
		canvas.addMouseMotionListener(this);
	}
		
	public VisualCanvas getCanvas() {
		return canvas;
	}

	/**
	 * moves the IDrawable and set it to "modified"
	 * redraws the canvas
	 */
	public void mouseDragged(MouseEvent e) {
		if (drawable != null) {
			drawable.moveTo(e.getPoint());
			((NodeCircle)drawable).setModified(true);
			canvas.updateDrawables();
			canvas.repaint();
		}
	}

	
	/**
	 * Selects the IDrawable that's under the mouse when clicked,
	 * if there is one
	 */
	public void mousePressed(MouseEvent e) {
		List selectedDrawables = canvas.findNodes(e.getPoint());
		if (selectedDrawables.size() == 0)
			return;
		drawable = (IDrawable) selectedDrawables.get(0);
	}

	/**
	 * unselects the IDrawable after setting it to modified, then
	 * redraws the canvas 
	 */
	public void mouseReleased(MouseEvent e) {
		if(drawable != null){
			drawable.moveTo(e.getPoint());
			((NodeCircle)drawable).setModified(true);
			drawable = null;
			canvas.updateDrawables();
			canvas.repaint();
		}
	}

	
}