package visualparts;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.Timer;

import util.graph.Node;


/* * * * * * * * * * * * * * * * * * * * * * * *
 * 
 *    Visual object associated with nodes
 *    
 *    @author : Cyril Poulet
 * 
 * * * * * * * * * * * * * * * * * * * * * * * */
public class NodeCircle implements IDrawable {

	/** visual size of the rectangle used to draw the circle */
	public static int node_vsize = 15;
	
	/* internal variables */
	protected Rectangle rect ;
	protected String label_str;
	protected Node node;
	protected boolean hasTimer;
	
	/* internal representation of time, used to count the idleness of the node */
	protected MyTimer mytimer;
	protected int last_visit = 0;
	protected boolean modified = false;
	
	
	/**
	 * Constructor
	 * 
	 * @param pos
	 * 			starting position on the canvas (Point)
	 * @param vert
	 * 			the associated node
	 * @param label
	 * 			name of the node
	 * @param starttime
	 * 			original idleness of the node
	 */
	public NodeCircle(Point pos, Node vert, String label, int starttime, boolean withTimer){
		this.rect = new Rectangle(pos, new Dimension(node_vsize,node_vsize));
		node = vert;
		label_str = label;
		last_visit = starttime;
		hasTimer = withTimer;
		if(withTimer)
			mytimer = new MyTimer();
	}
	
	public Rectangle getRectangle(){
		return (Rectangle) rect.clone();
	}
	
	public Node getNode(){
		return node;
	}
	
	public void moveTo(Point p){
		this.rect = new Rectangle(p, new Dimension(node_vsize,node_vsize));
	}
	
	public void draw(Graphics g){
		Color c = g.getColor();
		g.setColor(Color.DARK_GRAY);
		g.fillOval(rect.x, rect.y, rect.width, rect.height);
		g.setColor(Color.black);
		g.drawChars(label_str.toCharArray(), 0, label_str.length(), rect.x + (rect.width - label_str.length()*5)/2, rect.y + 30);
		g.drawChars(String.valueOf(last_visit).toCharArray(), 0, String.valueOf(last_visit).length(), rect.x + rect.width, rect.y - 10);
		
		g.setColor(c);

	}
	
	@Override
	public String getType() {
		return "node";
	}
	
	public void setModified(boolean m){
		modified = m;
	}
	
	public boolean isModified(){
		return modified;
	}

	/** 
	 *  The following functions are used to deal with the internal timer, 
	 *  whether it is in turn-based (where only incrementTimer is used)
	 *  or real-time (where an internal timer is used for each node, and 
	 *  several functions are used to update its speed and manage it.) 
	 */
	
	public void incrementTimer(int i) {
		last_visit += i;
		
	}

	public void resetTimer() {
		last_visit = 0;
		if(hasTimer)
			mytimer.restart();
		
	}
	
	public void startTimer(){
		mytimer.start();
	}
	
	public void stopTimer(){
		mytimer.stop();
	}
	
	public void changeTimer(int d){
		mytimer.changeDelay(d);
	}
	
	
	/* * * * * * * * * * * * * * * * * * * * * * * *
	 * 
	 *    internal timer class for NodeCircle
	 *    
	 *    @author : Cyril Poulet
	 * 
	 * * * * * * * * * * * * * * * * * * * * * * * */
	@SuppressWarnings("serial")
	private class MyTimer extends Timer {

		public MyTimer(){
			super(1000, new ActionListener()
							{
								public void actionPerformed(ActionEvent event){
									incrementTimer(1);
								}
							});
		}
		
		public void changeDelay(int d){
			this.stop();
			this.setInitialDelay(d);
			this.setDelay(d);
			this.start();
		}
		
		
		
		
	}
	
	
}