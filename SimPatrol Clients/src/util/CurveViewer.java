package util;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

import javax.imageio.ImageIO;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.filechooser.FileFilter;


/**
 * Simple curve viewer
 * you can add as many curves as you want to plot, as 2 Double[] (X and Y axis).
 * You can attach to them some DrawStyle (line style, point style)
 * 
 * The range of the axis is automatically calculated, but you can set the graduation
 * 
 * The generated plot can be saved as a .png file
 * 
 * 
 * @author Cyril Poulet
 *
 */

@SuppressWarnings("serial")
public class CurveViewer extends JFrame implements ActionListener{

	private List<Curve> curbs;
	private String title;
	
	private JPanel canvas;
	private JMenuItem close;
	private JMenuItem save;
	
	/** Axis variables **/
	private int max_x;
	private int min_x;
	private int max_y;
	private int min_y;
	
	private int x_division;
	private int y_division;
	
	// number of pixel for 1 division
	private int x_pix;
	private int y_pix;
	
	// coordinates of the origin
	private int Zero_x;
	private int Zero_y;
	
	
	// margin of the drawing
	private int margin = 50;
	
	
	public CurveViewer(String t){
		super();
		
		title = t;
		this.setSize(new Dimension(500, 500));
        this.setTitle(title);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        
        JMenuBar menubar = new JMenuBar();
        JMenu file = new JMenu("File");
        save = new JMenuItem("Save");
        save.addActionListener(this);
        close = new JMenuItem("Close");
        close.addActionListener(this);
        
        file.add(save);
        file.addSeparator();
        file.add(close);
        
        menubar.add(file);
        this.setJMenuBar(menubar);
        
        
        
        
        canvas = new JPanel();
        this.getContentPane().add(canvas);
        canvas.setBackground(Color.white);
        
        curbs = new LinkedList<Curve>();
        
        min_x = 0;
        max_x = 0;
        min_y = 0;
        max_y = 0;
        
        x_division = 1;
        y_division = 1;
        
	}
	
	
	/**
	 * Add a curve to the plot. Checks that there is as much X and Y coordinates
	 * 
	 * @param x
	 * 			Double[] : the X axis coordinates of the points to plot
	 * @param y
	 * 			Double[] : the Y axis coordinates of the points to plot
	 * @param col
	 * 			the color the curve is plotted in
	 * @param d
	 * 			0 to 2 drawstyles
	 */
	public void addCurve(Double[] x, Double[] y, Color col, DrawStyle... d){
		if(x.length != y.length){
			System.out.println("Not the same number of doubles in the lists");
			return;
		}
		
		Curve c = new Curve(x, y, col, d);
		curbs.add(c);
		
		if(c.max_x() > max_x)
			max_x = c.max_x();
		if(c.min_x() < min_x)
			min_x = c.min_x();
		if(c.max_y() > max_y)
			max_y = c.max_y();
		if(c.min_y() < min_y)
			min_y = c.min_y();	
			
	}
	
	/**
	 * set the X axis graduation
	 * @param div
	 * 			graduation
	 */
	public void setXdivision(int div){
		x_division = div;
	}

	
	
	/**
	 * get the X axis graduation
	 */
	public int getXdivision(){
		return x_division;
	}
	
	
	/**
	 * set the Y axis graduation
	 * @param div
	 * 			graduation
	 */
	public void setYdivision(int div){
		y_division = div;
	}
	
	
	/**
	 * get the X axis graduation
	 */
	public int getYdivision(){
		return y_division;
	}
	
	
	
	public void paint(Graphics g){
		super.paint(g);
		paint_graph(g);
		
		
	}
	
	
	/**
	 * paints the curves and axis on the given graphics
	 * 
	 * @param g
	 * 			Graphics to paint on
	 */
	public void paint_graph(Graphics g){
		Graphics2D g2d = (Graphics2D) g;
		
		int height = this.getHeight();
		int width = this.getWidth();
		
		g.translate(0, 20);
		
		
		g.setColor(Color.black);
		paintAxis(g, height, width);
		
		
		for(Curve c : curbs){
			g2d.setColor(c.getColor());
			g2d.setStroke(getStroke(c));
			
			int[] c_x_pix = new int[c.length()];
			int[] c_y_pix = new int[c.length()];
			
			for(int i = 0; i < c.length(); i++){
				c_x_pix[i] = (int)(Zero_x + c.x_i(i) * x_pix/x_division);
				c_y_pix[i] = (int)(Zero_y - c.y_i(i) * y_pix/y_division);
			}
			
			g2d.drawPolyline(c_x_pix, c_y_pix, c.length());	
			
			for(int i = 0; i < c.length(); i++)
				paintPoint(g, c_x_pix[i], c_y_pix[i], c);
		}
		
		
	}
	
	/**
	 * paints the axis on the given graphics
	 * 
	 * @param g
	 * 			Graphics to paint on
	 * @param h
	 * 			height of the canvas g refers to
	 * @param w 
	 * 			width of the canvas g refers to
	 */
	private void paintAxis(Graphics g, int h, int w){
		int x_min_div = min_x / x_division - (((min_x % x_division != 0)&&(min_x < 0))? 1 : 0);
		int x_max_div = max_x / x_division + (((max_x % x_division == 0)&&(max_x > 0))? 0 : 1);
		
		int y_min_div = min_y / y_division - (((min_y % y_division != 0)&&(min_y < 0))? 1 : 0);
		int y_max_div = max_y / y_division + (((max_y % y_division == 0)&&(max_y > 0))? 0 : 1);
		
		x_pix = (w - 2 * margin)/(x_max_div - x_min_div);
		y_pix = (h - 2 * margin)/(y_max_div - y_min_div);
		
		Zero_x = margin + x_pix * (-x_min_div);
		Zero_y = margin + y_pix * y_max_div;
		
		g.drawLine(Zero_x, margin, Zero_x, h-margin - 1);
		g.drawLine(margin, Zero_y, w-margin + 1, Zero_y);
	
		
		for(int i = x_min_div; i <= x_max_div; i++){
			g.drawLine(Zero_x + i * x_pix, Zero_y - 5, Zero_x + i * x_pix, Zero_y + 5);
			g.drawString(String.valueOf(i * x_division), Zero_x + i * x_pix - 5, Zero_y + 20);
		}
		
		for(int i = y_min_div; i <= y_max_div; i++){
			g.drawLine(Zero_x - 5, Zero_y - i * y_pix, Zero_x + 5, Zero_y - i * y_pix);
			String val = String.valueOf(i * y_division);
			g.drawString(val, Zero_x - 10 * val.length() , Zero_y - i * y_pix + 5);
		}
		
	}
	
	/**
	 * returns the stroke used to draw the curve c
	 * @param c
	 * 			the curve to plot
	 * @return
	 * 			the stroke to use
	 */
	private BasicStroke getStroke(Curve c){
		if((c.getLineStyle() == DrawStyle.NONE)||(c.getLineStyle() == DrawStyle.PLAIN))
			return new BasicStroke(1, BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL);
		if(c.getLineStyle() == DrawStyle.SHORT_DOTS)
			return new BasicStroke(1, BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL, 0, new float[]{5 , 10}, 0);
		if(c.getLineStyle() == DrawStyle.LONG_DOTS)
			return new BasicStroke(1, BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL, 0, new float[]{15, 10}, 0);
		else
			return new BasicStroke(1, BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL, 0, new float[]{5, 10, 15, 10}, 0);
	}
	
	
	/**
	 * paints the point int the style choosed for the curve c
	 * @param g
	 * 			the graphics to paint on
	 * @param x
	 * 			X coordinates of the point
	 * @param y
	 * 			Y coordinates of the point
	 * @param c
	 * 			the curve to plot
	 */
	private void paintPoint(Graphics g, int x, int y, Curve c){
		if((c.getPointStyle() == DrawStyle.NONE)||(c.getPointStyle() == DrawStyle.POINT_PLUS)){
			g.drawLine(x - 5, y, x + 5, y);
			g.drawLine(x, y - 5, x, y + 5);
		}
		else if(c.getPointStyle() == DrawStyle.POINT_CROSS){
			g.drawLine(x - 5, y - 5, x + 5, y + 5);
			g.drawLine(x + 5, y - 5, x - 5, y + 5);
		}
		else {
			g.fillOval(x - 3, y - 3, 6, 6);
		}
	}
	
	
	@Override
	public void actionPerformed(ActionEvent e) {
		if(e.getSource() == close)
			this.dispose();
		if(e.getSource() == save){
			final JFileChooser fc = new JFileChooser();
			
			// filters .png files in the filechooser
			FileFilter filter = new FileFilter(){

				public String getExtension(File f){
					String ext = null;
					String s = f.getName();
					int i = s.lastIndexOf('.');
					
					if(i>0 && i < s.length() - 1){
						ext = s.substring(i+1).toLowerCase();
					}
					return ext;
				}
				
				
				@Override
				public boolean accept(File f) {
					if(f.isDirectory())
						return true;
					String extension = getExtension(f);
					if(extension.equals("png"))
						return true;
					return false;
				}

				@Override
				public String getDescription() {
					return ".png";
				}
				
										};
			
			fc.addChoosableFileFilter(filter);
			fc.setAcceptAllFileFilterUsed(false);
			
			int returnVal = fc.showSaveDialog(this);
			if (returnVal == JFileChooser.APPROVE_OPTION) {
				
				// saves the plot to the chosen png file
				String savefile = fc.getSelectedFile().getPath();
				
				BufferedImage Image = new BufferedImage(this.getWidth(), this.getHeight(), BufferedImage.TYPE_INT_RGB);
				Graphics g = Image.getGraphics();
				
				g.setColor(Color.white);
				g.fillRect(0, 0, this.getWidth(), this.getHeight());
				this.paint_graph(g);
				// g.dispose();
				File file = new File(savefile);
				try{
					ImageIO.write( Image, "png", file);
				}
				catch(IOException evt){
					evt.printStackTrace();
				}
			}
		}
		
	}
	
	
	/**
	 * Internal class to represent curves
	 * 
	 * 
	 * @author pouletc
	 *
	 */
	class Curve {
		private Double[] x;
		private Double[] y;
		
		private int max_x;
		private int min_x;
		private int max_y;
		private int min_y;
		
		private Color color;
		private DrawStyle line_style;
		private DrawStyle point_style;
		
		
		public Curve(Double[] x_s, Double[] y_s, Color col, DrawStyle... styles){
			x = x_s;
			y = y_s;
			color = col;
			
			line_style = DrawStyle.NONE;
			point_style = DrawStyle.NONE;
			
			for(DrawStyle d : styles){
				if((d == DrawStyle.LONG_DOTS)||(d == DrawStyle.ALTERNATE_DOTS)||
						(d == DrawStyle.SHORT_DOTS)||(d == DrawStyle.PLAIN))
					line_style = d;
				else if((d == DrawStyle.POINT_CROSS)||(d == DrawStyle.POINT_PLUS)||
						(d == DrawStyle.POINT_ROUND))
					point_style = d;						
			}
			
			min_x = (int)Math.floor(x[0]);
			max_x = (int)Math.ceil(x[0]);
			
			for(Double x_i : x){
				if(x_i < min_x)
					min_x = (int)Math.floor(x_i);
				if(x_i > max_x)
					max_x = (int)Math.ceil(x_i);					
			}
			
			min_y = (int)Math.floor(y[0]);
			max_y = (int)Math.ceil(y[0]);
			
			for(Double y_i : y){
				if(y_i < min_y)
					min_y = (int)Math.floor(y_i);
				if(y_i > max_y)
					max_y = (int)Math.ceil(y_i);					
			}
				
		}
		
		public int length(){
			return x.length;
		}
		
		public Double[] getx(){
			return x;	
		}
		
		public Double x_i(int i){
			return x[i];
		}
		
		public int max_x(){
			return max_x;
		}
		
		public int min_x(){
			return min_x;
		}
		
		public Double[] gety(){
			return y;	
		}
		
		public Double y_i(int i){
			return y[i];
		}
		
		public int max_y(){
			return max_y;
		}
		
		public int min_y(){
			return min_y;
		}
		
		public Color getColor(){
			return color;
		}
		
		public DrawStyle getLineStyle(){
			return line_style;
		}
		
		public DrawStyle getPointStyle(){
			return point_style;
		}
	}


}
