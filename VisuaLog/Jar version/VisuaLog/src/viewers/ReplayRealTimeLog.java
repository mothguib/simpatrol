package viewers;

import java.io.IOException;
import java.util.Date;

import visualparts.VisualCanvas;
import util.file.FileReader;

/* * * * * * * * * * * * * * * * * * * * * * * *
 * 
 *    log for real-time simulations that recreates 
 *    the simulation by reading a log file
 *    
 *    @author : Cyril Poulet
 * 
 * * * * * * * * * * * * * * * * * * * * * * * */
public class ReplayRealTimeLog extends AbstractRealTimeLog implements ReplayLog {

	/** The file reader associated to the log file    */
	private FileReader logfile;
	
	/** The speed at which the simulation is played   */
	public static double REPLAY_SPEED = 1;
	
	/** state variables of the player   */
	private boolean playing = false;
	private boolean started = false;
	
	/** 
	 * variables used to check that the display speed is not
	 * too quick compared to the capability of the player to 
	 * handle the events to display  
	 **/
	private int no_speed_check = 0;
	public static int NO_SPEED_CHECK_MAX = 5;
	
	/** 
	 * variables used to check that the displayed time is synchronized
	 * to the one used to handle events (2 different timers)
	 * TIME_CORRECTION_NUM is the number of events handled between 
	 * 2 checks and resync.
	 **/
	private int time_correction = 0;
	public static int TIME_CORRECTION_NUM = 10;
	
	
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
	public ReplayRealTimeLog(VisualCanvas canvas, String file_path) throws IOException {
		this.mycanvas = canvas;
		this.stop_working = false;
		this.logfile = new FileReader(file_path);
		
		elapsed_time = 0;

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
	 * manages the events, checks that there is no desynch between display and management timers
	 * checks that the display speed is not too quick for the events manager
	 */
	public void run() {

		while (!this.stop_working) {
			// String[] events = this.connection.getBufferAndFlush()
			long bla =new Date().getTime();
			long bla2 = bla - last_speed_change;
			double in_time = elapsed_time + bla2 * REPLAY_SPEED - next_event;
			if((playing && (in_time > 0)) || !canvas_configured){
				
				if(in_time > 2000)
					if(no_speed_check == NO_SPEED_CHECK_MAX) {
						REPLAY_SPEED /= 2;
						mycanvas.correctRTtime(Math.round(next_event/500)/2);
					}
					else 
						no_speed_check += 1;
				
				if(time_correction == TIME_CORRECTION_NUM){
					time_correction = 0;
					mycanvas.correctRTtime(Math.round(next_event/500)/2);
				} else
					time_correction += 1;
				
				try {
					line = getNextLine();
				} catch (IOException e1) {
					this.stopWorking();
					break;
				}
				
				String[] events = { line };
				
				if(canvas_configured && !line.equals("")){
					try {
						manage_events(events);
						mycanvas.updateDrawables();
						mycanvas.repaint();

					} catch (Exception e) {
						//System.out.println(e.getMessage());
						continue;
					}
				}
				
				if(graph == null || societies == null)
					configure_environment(events);
				
				if(!canvas_configured && graph != null && societies != null){
					configure_canvas();
				}
				
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
	public boolean isPlaying() {
		return playing;
	}

	/**
	 * "play" command
	 * start or restart the nodes timers
	 */
	public void play(){
		playing = true;
		last_speed_change = new Date().getTime();
		
		if(!started){
			started = true;
			mycanvas.restartNodes();
		} else
			mycanvas.startNodes();
	}
	
	/**
	 * "pause" command
	 *  stops the replay, updates the elapsed_time and pauses the nodes timers
	 */
	public void pause(){
		playing = false;
		elapsed_time += (new Date().getTime() - last_speed_change) * REPLAY_SPEED;
		mycanvas.pauseNodes();
	}

	@Override
	public double getSpeed() {
		return REPLAY_SPEED;
	}

	@Override
	/**
	 * changes the REPLAY_SPEED
	 * 
	 * updates elapsed_time, and changes as well the display timers
	 */
	public void setSpeed(double speed) {
		elapsed_time += (new Date().getTime() - last_speed_change) * REPLAY_SPEED;
		REPLAY_SPEED = speed;
		last_speed_change = new Date().getTime();
		mycanvas.updateRTdelay((int)(1000 / REPLAY_SPEED) );
		no_speed_check = 0;
		
	}

	@Override
	public String getType() {
		return "replay real-time";
	}

}
