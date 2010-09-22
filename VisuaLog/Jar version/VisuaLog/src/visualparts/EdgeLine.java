package visualparts;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.Rectangle;
import java.util.Iterator;
import java.util.List;

import util.graph.Edge;


/* * * * * * * * * * * * * * * * * * * * * * * *
 * 
 *    Visual object associated with edges
 *    
 *    @author : Cyril Poulet
 * 
 * * * * * * * * * * * * * * * * * * * * * * * */
public class EdgeLine implements IDrawable {

	
	/* internal variables */
	private Rectangle rect;
	private Edge edge;
	
	
	/**
	 * Constructor
	 * 
	 * @param edge
	 * 			associated edge
	 */
	public EdgeLine(Edge edge){
		this.edge = edge;
		rect = new Rectangle(0,0,20,20);
	}
	
	@Override
	public void draw(Graphics g) {
		Color c = g.getColor();
		g.setColor(Color.BLACK);
		g.drawLine(rect.x + NodeCircle.node_vsize/2, 
						rect.y + NodeCircle.node_vsize/2, 
						rect.x + rect.width + NodeCircle.node_vsize/2,
						rect.y + rect.height + NodeCircle.node_vsize/2);
		g.drawString(edge.getObjectId()+ " : " + String.valueOf(edge.getLength()), rect.x + rect.width/2 + NodeCircle.node_vsize/2 , rect.y + rect.height/2 - 15 + NodeCircle.node_vsize/2);
		g.setColor(c);

	}

	@Override
	public Rectangle getRectangle() {
		return rect;
	}

	@Override
	public void moveTo(Point p) {
		this.rect = new Rectangle(p, new Dimension(rect.width,rect.height));

	}

	@Override
	public String getType() {
		return "edge";
	}
	
	/** 
	 * Updates the EdgeLine from the internal state of the associated edge
	 * and the NodeCircles of the graph (used to calculate the position of the 
	 * EdgeLine on the canvas)
	 * 
	 * @param Nodes
	 * 			the NodeCircles on the canvas
	 **/
	public void update_edge(List Nodes){
		String emitter = edge.getEmitter().getObjectId();
		String collector = edge.getOtherNode(edge.getEmitter()).getObjectId();
		
		Rectangle em_rect = null;
		Rectangle col_rect = null;
		
		
		for(Iterator iter = Nodes.iterator(); iter.hasNext();){
			IDrawable element = (IDrawable) iter.next();
			String id = ((NodeCircle)element).getNode().getObjectId();
			if(id == emitter)
				em_rect = ((NodeCircle)element).getRectangle();
			else if(id == collector)
				col_rect = ((NodeCircle)element).getRectangle();			
		}
		
		if(em_rect != null && col_rect != null){
			rect = new Rectangle(em_rect.x, 
								em_rect.y,
								col_rect.x - em_rect.x,
								col_rect.y - em_rect.y);
			return;
		}
		
		if(em_rect != null){
			int new_width = rect.x + rect.width - em_rect.x;
			int new_height = rect.y + rect.height - em_rect.y;
			rect = new Rectangle(em_rect.x, 
					em_rect.y,
					new_width,
					new_height);
			return;
		}
		
		if(col_rect != null){
			rect = new Rectangle(rect.x, 
					rect.y,
					col_rect.x - rect.x,
					col_rect.y - rect.y);
			return;
		}
	}
	
	public Edge getEdge(){
		return edge;
	}

}
