package simpatrol.clientplugin.visuallog.visualparts;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import javax.swing.JPanel;
import javax.swing.Timer;

import simpatrol.clientplugin.visuallog.Application;
import simpatrol.clientplugin.visuallog.logger.ReplayLog;
import simpatrol.userclient.util.graph.Edge;
import simpatrol.userclient.util.graph.Node;


/* * * * * * * * * * * * * * * * * * * * * * * *
 * 
 *    Canvas used to draw and manage the visual objects
 *    (IDrawable)
 *    
 *    @author : Cyril Poulet
 * 
 * * * * * * * * * * * * * * * * * * * * * * * */
@SuppressWarnings("serial")
public class VisualCanvas extends JPanel {
	
	/** lists of IDrawables */
	private List nodes = new LinkedList();
	private List agents = new LinkedList();
	private List edges = new LinkedList();
	
	
	/** internal variables for time management */
	private boolean display_speed = false;
	private int turn = 0;
	private boolean display_turn = false;
	private double time = 0;
	private MyTimer mytimer;

	/**
	 * Constructor
	 */
	public VisualCanvas(){
		super();
		mytimer = new MyTimer();
	}
	
	/**
	 * configures the display from the log type
	 */
	public void configure_canvas(){
		// reset of the variables
		turn = 0;
		time = 0;
		mytimer = new MyTimer();
		
		if(Application.logger.getType().contains("replay"))
			display_speed = true;
		else 
			display_speed = false;
		if(Application.logger.getType().contains("turn"))
			display_turn = true;
		else 
			display_turn = false;
		
	}
	
	
	/**
	 * manage the drawing.
	 * draws the IDrawables, then writes the header (time/turn, speed, pause)
	 */
	public void paint(Graphics g) {
		
		g.setColor(Color.white);
        g.fillRect(0, 0, this.getWidth(), this.getHeight());

		for (Iterator iter = edges.iterator(); iter.hasNext();) {
			IDrawable d = (IDrawable) iter.next();
			d.draw(g);	
		}
		for (Iterator iter = nodes.iterator(); iter.hasNext();) {
			IDrawable d = (IDrawable) iter.next();
			d.draw(g);	
		}
		for (Iterator iter = agents.iterator(); iter.hasNext();) {
			IDrawable d = (IDrawable) iter.next();
			d.draw(g);	
		}
		
		g.setColor(Color.black);
		if(display_turn){
			String turn_str = "Turn : " + String.valueOf(turn);
			g.drawChars(turn_str.toCharArray(), 0, turn_str.length(), 5, 15);
		} else {
			String time_str = "Time : " + String.valueOf(time);
			g.drawChars(time_str.toCharArray(), 0, time_str.length(), 5, 15);
		}
		if(display_speed){
			String speed_str = "Speed : " + String.valueOf(((ReplayLog)Application.logger).getSpeed());
			g.drawChars(speed_str.toCharArray(), 0, speed_str.length(), 5, 30);
			if(!((ReplayLog)Application.logger).isPlaying())
				g.drawChars("PAUSE".toCharArray(), 0, "PAUSE".length(), 5, 45);
		}
		
	}

	/**
	 * Adds a IDrawable to the display
	 * 
	 * @param d
	 * 		the IDrawable to add
	 */
	public void addDrawable(IDrawable d) { 
		if(d.getType() == "agent")
			agents.add(d);
		else if(d.getType() == "node")
			nodes.add(d);
		else 
			edges.add(d);
		//repaint();
	}

	/**
	 * Removes a IDrawable to the display
	 * 
	 * @param d
	 * 		the IDrawable to remove
	 */
	public void removeDrawable(IDrawable d) {
		if(d.getType() == "agent")
			agents.remove(d);
		else if(d.getType() == "node")
			nodes.remove(d);
		else edges.remove(d);
		repaint();
	}

	/**
	 * clears the display of all the IDrawables
	 */
	public void clear() {
		agents.clear();
		nodes.clear();
		edges.clear();
		repaint();
	}
	
	/**
	 * Finds all the IDrawables that have the point p in their drawing rectangle
	 * 
	 * @param p
	 * 			The point that must be in the IDrawable rectangle
	 * @return
	 * 			The list of IDrawables that have p in their rectangle
	 */
	public List findNodes(Point p) { 
		List l = new ArrayList(); 
		for (Iterator iter = nodes.iterator(); iter.hasNext();) { 
			IDrawable element = (IDrawable) iter.next(); 
			if(element.getRectangle().contains(p)){ 
				l.add(element); 
				} 
			} 
		return l; 
	}
	
	/**
	 * Finds the visual object associated to a graph object (node, edge, agent)
	 * 
	 * @param object
	 * 			the object which associated visual object we want to find
	 * @return
	 * 			the visual object as an IDrawable
	 */
	public IDrawable findAssociatedDrawable(Object object){
		if(object.getClass().getName().equals("Node")){
			for(Iterator iter = nodes.iterator(); iter.hasNext();) {
				NodeCircle d = (NodeCircle) iter.next();
				if(d.getNode().equals((Node)object))
					return d;
			}
		} else if(object.getClass().getName().equals("Edge")){
			for(Iterator iter = edges.iterator(); iter.hasNext();) {
				EdgeLine d = (EdgeLine) iter.next();
				if(d.getEdge().equals((Edge)object))
					return d;
			}
		} else if(object.getClass().getName().equals("Agent")){
			for(Iterator iter = agents.iterator(); iter.hasNext();) {
				AgentTriangle d = (AgentTriangle) iter.next();
				if(d.getAgent().equals((Edge)object))
					return d;
			}
		}
		return null;
	}

	/**
	 * updates the IDrawables
	 * For better speed, only the modified nodes are used
	 */
	public void updateDrawables(){
		
		List modified_nodes = new LinkedList();
		for(Iterator iter = nodes.iterator(); iter.hasNext();){
			IDrawable element = (IDrawable) iter.next(); 
			if(((NodeCircle)element).isModified())
				modified_nodes.add(element);
		}
		
		for(Iterator iter = agents.iterator(); iter.hasNext();){
			IDrawable element = (IDrawable) iter.next(); 
			((AgentTriangle)element).update_agent(nodes);
		}
		
		for(Iterator iter = edges.iterator(); iter.hasNext();){
			IDrawable element = (IDrawable) iter.next(); 
			((EdgeLine)element).update_edge(modified_nodes);
		}
		
		for(Iterator iter = modified_nodes.iterator(); iter.hasNext();){
			IDrawable element = (IDrawable) iter.next(); 
			((NodeCircle)element).setModified(false);
		}
	}

	
	/**
	 * updates the internal timer of nodes, and increments the turn
	 * used on turn-based simulation
	 */
	public void updateTime() {
		for(Iterator iter = nodes.iterator(); iter.hasNext();){
			NodeCircle element = (NodeCircle) iter.next(); 
			element.incrementTimer(1);
			
		}
		turn += 1;
		
	}
	
	/**
	 * updates the internal timer of nodes, and increments the turn
	 * used on turn-based simulation
	 * 
	 * @param i
	 * 			number of turns to add
	 */
	public void updateTime(int i) {
		for(Iterator iter = nodes.iterator(); iter.hasNext();){
			NodeCircle element = (NodeCircle) iter.next(); 
			element.incrementTimer(i);
			
		}
		turn += i;
		
	}
	
	
	/**
	 * forces the canvas clock to t
	 * used to correct the time lapse between canvas time and events management time
	 * 
	 * @param t
	 * 			the event management time
	 */
	public void correctRTtime(double t){
		time = t;
	}
	
	/**
	 * changes the speed of the timers of the visual objects to 
	 * match the REPLAY_SPEED change
	 * 
	 * the canvas timer is twice as fast as the nodecircle timers
	 * 
	 * @param d
	 * 			the duration of a tick, in ms
	 */
	public void updateRTdelay(int d){
		for(Iterator iter = nodes.iterator(); iter.hasNext();){
			NodeCircle element = (NodeCircle) iter.next(); 
			element.changeTimer(d);
		}
		mytimer.setInitialDelay(d/2);
		mytimer.setDelay(d/2);
	}

	/**
	 * starts the timers of the nodecircles and the canvas
	 */
	public void startNodes() {
		for(Iterator iter = nodes.iterator(); iter.hasNext();){
			NodeCircle element = (NodeCircle) iter.next(); 
			element.startTimer();
		}
		mytimer.start();
		
	}
	
	/**
	 * resets and restarts the timers of the nodecircles and the canvas
	 */
	public void restartNodes(){
		for(Iterator iter = nodes.iterator(); iter.hasNext();){
			NodeCircle element = (NodeCircle) iter.next(); 
			element.resetTimer();
		}
		mytimer.restart();
	}
	
	/**
	 * stops the timers of the nodecircles and the canvas
	 */
	public void pauseNodes(){
		for(Iterator iter = nodes.iterator(); iter.hasNext();){
			NodeCircle element = (NodeCircle) iter.next(); 
			element.stopTimer();
		}
		mytimer.stop();
	}
	
	
	
	/* * * * * * * * * * * * * * * * * * * * * * * *
	 * 
	 *    internal timer class for VisualCanvas
	 *    
	 *    @author : Cyril Poulet
	 * 
	 * * * * * * * * * * * * * * * * * * * * * * * */
	private class MyTimer extends Timer {
		
		
		public MyTimer(){
			super(500, new ActionListener()
							{
								public void actionPerformed(ActionEvent event){
									time += 0.5;
								}
							});
		}
		
	}



	
}