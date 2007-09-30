/* Clock.java */

/* The package of this class. */
package util.clock;

/* Imported classes and/or interfaces. */
import java.util.Calendar;

/** Implements a real time clock, that with a specific periodicity,
 *  sends signal to a Clockable object in order to make it act.
 *  
 *  @see Clockable */
public final class Clock extends Thread {
	/* Attributes. */
	/** Registers if the clock shall stop working. */
	private boolean stop_working;
	
	/** The object to be clocked. */
	private Clockable object;
	
	/** The clock count step.
	 *  The default value is one second. */
	private int step = 1;
	
	/** The clock count unity, as in Calendar field.
	 *  The default value is Calendar.SECOND.
	 *  
	 *  @see Calendar */
	private int unity = Calendar.SECOND;
	
	/** The time interval used by the clock to let the clockable
	 *  object act. Its default value is 1000 milliseconds (1 second). */
	private long time_interval = 1000;
	
	/* Methods. */
	/** Constructor.
	 * 
	 *  @param name The name of the thread of the clock.
	 *  @param object The object to be clocked. */
	public Clock(String name, Clockable object) {
		super(name);
		this.object = object;
		this.stop_working = false;
	}
	
	/** Changes the clock's counting step.
	 * 
	 *  @param step The counting step. */
	public void setStep(int step) {		
		this.time_interval = (long) (this.time_interval * Math.pow(this.step, -1));
		this.step = step;
		this.time_interval = this.time_interval * this.step;
	}
	
	/** Changes the clock's counting unity.
	 * 
	 *  @param unity The java.util.Calendar's time unity. */
	public void setUnity(int unity) {
		this.unity = unity;
		
		if(this.unity == Calendar.HOUR)
			this.time_interval = this.step * 360000;
		else if(this.unity == Calendar.MINUTE)
			this.time_interval = this.step * 6000;
		else if(this.unity == Calendar.SECOND)
			this.time_interval = this.step * 1000;
		else this.time_interval = this.step;
	}

	/** Indicates that the clock must stop working. */
	public void stopWorking() {
		this.stop_working = true;
	}
			
	public void run() {
		while(!this.stop_working) {
			try {
				int prev_ref = Calendar.getInstance().get(this.unity);
				sleep(this.time_interval);
				int next_ref = Calendar.getInstance().get(this.unity);
				
				if(next_ref < prev_ref) next_ref = next_ref + prev_ref + this.step;
				int time_gap = (int) ((next_ref - prev_ref) * Math.pow(this.step, -1));
				this.object.act(time_gap);				
			}
			catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
}