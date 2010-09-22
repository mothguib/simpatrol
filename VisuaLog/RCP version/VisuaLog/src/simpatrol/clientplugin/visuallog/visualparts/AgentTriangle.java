package simpatrol.clientplugin.visuallog.visualparts;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.Rectangle;
import java.util.Iterator;
import java.util.List;

import simpatrol.userclient.util.agent.Agent;
import simpatrol.userclient.util.graph.Edge;
import simpatrol.userclient.util.graph.Node;

/* * * * * * * * * * * * * * * * * * * * * * * *
 * 
 *    Visual object associated with agents
 *    
 *    @author : Cyril Poulet
 * 
 * * * * * * * * * * * * * * * * * * * * * * * */
public class AgentTriangle implements IDrawable {

	
	/** visual size of the rectangle used to draw the triangle */
	public static int agent_vsize = 12;
	
	/* internal variables */
	
	private Rectangle rect;
	private Color state_color;
	private String label_str;
	
	/* associated agent */
	private Agent agent;
	
	/* colors used by the agent : 1 color / 1 state */
	public static Color DEATH_C = Color.WHITE;
	public static Color MOVING_C = Color.BLACK;
	public static Color VISITING_C = Color.RED;
	public static Color STIGMATIZING_C = Color.GREEN;
	public static Color RECHARGING_C = Color.LIGHT_GRAY;
	
	
	/**
	 * Constructor
	 * 
	 * @param agent
	 * 			associated agent
	 * @param pos
	 * 			starting position on the canvas (Point)
	 * @param label
	 * 			name of the agent
	 */
	public AgentTriangle(Agent agent, Point pos, String label){
		this.agent = agent;
		this.rect = new Rectangle(pos, new Dimension(agent_vsize, agent_vsize));
		this.label_str = label;
	}

	@Override
	public void draw(Graphics g) {
		Color c = g.getColor();
		
		int[] triangle_x = {rect.x, rect.x + rect.width, rect.x + rect.width/2};
		int[] triangle_y = {rect.y + rect.height, rect.y + rect.height, rect.y};
		g.setColor(state_color);
		g.fillPolygon(triangle_x, triangle_y, 3);
		g.setColor(Color.black);
		g.drawPolygon(triangle_x, triangle_y, 3);
		g.drawChars(label_str.toCharArray(), 0, label_str.length(), rect.x + (rect.width - label_str.length()*5)/2, rect.y + 30);
		
		if(agent.getAgentState() == 2){
			g.drawArc(rect.x-2, rect.y-2, rect.width + 4, rect.height + 4, 135, 90);
			g.drawArc(rect.x-2, rect.y-2, rect.width + 4, rect.height + 4, -45, 90);
			
			g.drawArc(rect.x-5, rect.y-5, rect.width + 10, rect.height + 10, 135, 90);
			g.drawArc(rect.x-5, rect.y-5, rect.width + 10, rect.height + 10, -45, 90);
			
			g.drawArc(rect.x-8, rect.y-8, rect.width + 16, rect.height + 16, 135, 90);
			g.drawArc(rect.x-8, rect.y-8, rect.width + 16, rect.height + 16, -45, 90);
		}
		
		
		g.setColor(c);

	}

	@Override
	public Rectangle getRectangle() {
		return (Rectangle) rect.clone();
	}

	@Override
	public void moveTo(Point p) {
		this.rect = new Rectangle(p, new Dimension(rect.width,rect.height));

	}
	
	
	/** 
	 * Updates the AgentTriangle from the internal state of the associated agent
	 * and the NodeCircles of the graph (used to calculate the position of the 
	 * AgentTriangle on the canvas)
	 * 
	 * @param Nodes
	 * 			the NodeCircles on the canvas
	 **/
	public void update_agent(List Nodes){
		
		Node vert = agent.getNode();
		Edge edge = agent.getEdge();
		double elapsed_length = agent.getElapsed_length();
		
		if(edge == null){
			for(Iterator iter = Nodes.iterator(); iter.hasNext();){
				IDrawable element = (IDrawable) iter.next(); 
				if(((NodeCircle)element).getNode().getObjectId() == vert.getObjectId()){
					this.rect = ((NodeCircle)element).getRectangle();	
					continue;
				}
			}
		} else {
			Node goto_vert = edge.getOtherNode(vert);
			Rectangle vert_rect = null;
			Rectangle gotovert_rect = null;
			for(Iterator iter = Nodes.iterator(); iter.hasNext();){
				IDrawable element = (IDrawable) iter.next(); 
				String id = ((NodeCircle)element).getNode().getObjectId();
				if(id == vert.getObjectId())
					vert_rect = ((NodeCircle)element).getRectangle();
				else if(id == goto_vert.getObjectId())
					gotovert_rect = ((NodeCircle)element).getRectangle();
				
			}
			//double dist_nodes = Math.sqrt((vert_rect.x - gotovert_rect.x)^2 + 
			//							(vert_rect.y - gotovert_rect.y)^2 );
			
			if(vert_rect != null && gotovert_rect != null){
				double new_x = vert_rect.x + (gotovert_rect.x - vert_rect.x)/edge.getLength() * elapsed_length;
				double new_y = vert_rect.y + (gotovert_rect.y - vert_rect.y)/edge.getLength() * elapsed_length;
				
				moveTo(new Point((int)new_x, (int)new_y));
			} 
			else {
				double new_x = rect.x + rect.width/edge.getLength() * elapsed_length;
				double new_y = rect.y + rect.height/edge.getLength() * elapsed_length;
				
				moveTo(new Point((int)new_x, (int)new_y));
			}
		}
		
		switch(agent.getAgentState()){
		case -1:
			state_color = DEATH_C;
			break;
		case 0 :
			state_color = MOVING_C;
			
			break;
		case 1:
			state_color = VISITING_C;
			for(Iterator iter = Nodes.iterator(); iter.hasNext();){
				IDrawable element = (IDrawable) iter.next(); 
				if(((NodeCircle)element).getNode().getObjectId() == vert.getObjectId()){
					((NodeCircle)element).resetTimer();
					continue;
				}
			}
			break;
		case 3:
			state_color = STIGMATIZING_C;
			break;
		case 4:
			state_color = RECHARGING_C;
			break;
		}
		
	}


	@Override
	public String getType() {
		return "agent";
	}
	
	public Agent getAgent(){
		return agent;
	}

}
