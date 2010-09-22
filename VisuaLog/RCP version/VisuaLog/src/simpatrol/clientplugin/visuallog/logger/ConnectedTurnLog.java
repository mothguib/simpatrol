/* LogClient.java */

/* The package of this class. */
package simpatrol.clientplugin.visuallog.logger;

/* Imported classes and/or interfaces. */
import java.io.IOException;

import simpatrol.clientplugin.visuallog.visualparts.VisualCanvas;
import simpatrol.userclient.util.file.FileWriter;
import simpatrol.userclient.util.net.UDPClientConnection;



/* * * * * * * * * * * * * * * * * * * * * * * *
 * 
 *    log for turn-based simulations that is connected 
 *    directly to SimPatrol and use the resulting stream 
 *    as input for the viewer.
 *    
 *    @author : Cyril Poulet
 * 
 * * * * * * * * * * * * * * * * * * * * * * * */
public class ConnectedTurnLog extends AbstractTurnLog {

	/* Methods. */
	/**
	 * Constructor.
	 * 
	 * @param canvas
	 * 			  The object that manages the visual effects.
	 * @param remote_socket_address
	 *            The IP address of the SimPatrol server.
	 * @param remote_socket_number
	 *            The number of the socket that the server writes to, related to
	 *            this client.
	 * @param file_path
	 *            The path of the file where the events will be saved.
	 * @throws IOException
	 */
	public ConnectedTurnLog(VisualCanvas canvas, String remote_socket_address,
			int remote_socket_number, String file_path) throws IOException {
		this.mycanvas = canvas;
		this.stop_working = false;
		this.connection = new UDPClientConnection(remote_socket_address,
				remote_socket_number);
		this.file_writer = new FileWriter(file_path);
	}

	/** 
	 * Main function
	 * 
	 * Connects to SimPatrol, configures the viewer and manages the handling
	 * of events.
	 */
	public void run() {
		// starts its connection
		 this.connection.start();
		 boolean skip = false;
		 boolean old_skip = false;

		while (!this.stop_working) {
			String[] events = this.connection.getBufferAndFlush();
			
			if(graph == null && societies == null)
				configure_environment(events);
			
			if(!canvas_configured && graph != null && societies != null)
				configure_canvas();
			
			if(canvas_configured){
				for(String event : events){
					try {
						String[] event_2 = { event };
						skip = manage_events(event_2);
						
						// if skip, the display is blocked for SKIP_DURATION
						// to allow the visualization of the event
						if(!skip && old_skip)
							Thread.sleep((int)SKIP_DURATION);
						
						if(turn != incoming_turn){
							mycanvas.updateTime(incoming_turn - turn);
							turn = incoming_turn;
						}
						mycanvas.updateDrawables();
						mycanvas.repaint();

						old_skip = skip;
					} catch (Exception e) {
						this.file_writer.println(e.getMessage());
						continue;
					}
				}	
			}

			// here we record the events in a log file
			for (int i = 0; i < events.length; i++) {
				String event = events[i];
				if(event == "") continue;
				event = event.substring(0, event.lastIndexOf("\n"));
				this.file_writer.println(event);
			}
			
		}

		try {
			this.connection.stopWorking();
		} catch (IOException e) {
			e.printStackTrace();
		}

		this.file_writer.close();
	}


	@Override
	public String getType() {
		return "connected turn";
	}

}