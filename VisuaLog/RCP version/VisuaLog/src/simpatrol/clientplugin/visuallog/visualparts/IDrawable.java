package simpatrol.clientplugin.visuallog.visualparts;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.Rectangle;

import simpatrol.userclient.util.graph.Node;


/* * * * * * * * * * * * * * * * * * * * * * * *
 * 
 *    Visual objects used to draw on the canvas
 *    
 *    @author : Cyril Poulet
 * 
 * * * * * * * * * * * * * * * * * * * * * * * */
public interface IDrawable {
	
	/** draws the object on the canvas */
	public  void draw(Graphics g);

	/** returns the rectangle in which the object is drawn */
	public Rectangle getRectangle();
	
	/** moves the rectangle (and the object) to point p */
	public void moveTo(Point p);
	
	/** returns the type of object */
	public String getType();
	
	
}