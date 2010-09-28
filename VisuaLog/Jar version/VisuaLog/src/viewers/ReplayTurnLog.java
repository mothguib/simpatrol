package viewers;

import java.io.IOException;

import visualparts.VisualCanvas;
import util.file.FileReader;

/* * * * * * * * * * * * * * * * * * * * * * * *
 * 
 *    log for turn-based simulations that recreates 
 *    the simulation by reading a log file
 *    
 *    @author : Cyril Poulet
 * 
 * * * * * * * * * * * * * * * * * * * * * * * */
public class ReplayTurnLog extends AbstractTurnLog implements ReplayLog {

	/** The file reader associated to the log file    */
	private FileReader logfile;
	
	/** The speed at which the simulation is played   */
	public static double REPLAY_SPEED = 1;
	
	/** state variable of the player   */
	private boolean playing = false;
	
	private String line = "";
	
	
	/**
	 * Constructor.
	 * 
	 * @param canvas
	 * 			  The object that manages the visual effects.
	 * @param file_path
	 *            the path to the log file that is to be played
	 * @throws IOException
	 */
	public ReplayTurnLog(VisualCanvas canvas, String file_path) throws IOException {
		this.mycanvas = canvas;
		this.stop_working = false;
		this.logfile = new FileReader(file_path);

	}
	
	
	/** 
	 * Reads the next line. If it begins with the tags "graph" or "society", it
	 * reads until the whole graph / society is read (used by configuration)
	 * 
	 * @return the next line of the file
	 * @throws IOException
	 */
	public String getNextLine() throws IOException{
		String next_line = logfile.readLine();
		
		if(next_line == null)
			return "";
		if(next_line.contains("graph")){
			String continue_line = logfile.readLine();
			while(!continue_line.contains("graph")){
				next_line += continue_line;
				continue_line = logfile.readLine();
			}
			next_line += continue_line;
			return next_line;
		}
		if(next_line.contains("society")){
			String continue_line = logfile.readLine();
			while(!continue_line.contains("society")){
				next_line += continue_line;
				continue_line = logfile.readLine();
			}
			next_line += continue_line;
			return next_line;
		}
		return next_line;
	}
	
	
	
	/** 
	 * Main function
	 * 
	 * configure the environment and display
	 * manages the events
	 */
	public void run() {

		while (!this.stop_working) {
			if((playing && (incoming_turn == turn)) || !canvas_configured){
				
				try {
					line = getNextLine();
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					this.stopWorking();
					break;
				}
				
				String[] events = { line };
				
				if(canvas_configured && !line.equals("")){
					try {
						boolean skip = manage_events(events);
						if(incoming_turn != turn){
							Thread.sleep((int)(1000/REPLAY_SPEED));
							mycanvas.updateTime();
							turn += 1;
							mycanvas.updateDrawables();
							mycanvas.repaint();
						} else {
							mycanvas.updateDrawables();
							mycanvas.repaint();
							if(skip)
								Thread.sleep((int)(300/REPLAY_SPEED));
						}
					} catch (Exception e) {
						//System.out.println(e.getMessage());
						continue;
					}
				}
				
				if(graph == null || societies == null)
					configure_environment(events);
				
				if(!canvas_configured && graph != null && societies != null)
					configure_canvas();
			}

			
			if(line == "")
				this.stopWorking();
					
		}


		try {
			this.logfile.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Override
	public String getType() {
		return "replay turn";
	}	
	
	public boolean isPlaying(){
		return playing;
	}
	
	public void play(){
		playing = true;
	}
	
	public void pause(){
		playing = false;
	}

	@Override
	public double getSpeed() {
		return REPLAY_SPEED;
	}

	@Override
	public void setSpeed(double speed) {
		REPLAY_SPEED = speed;
		
	}
}
