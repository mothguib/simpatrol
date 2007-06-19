/* Clock.java */

/* The package of this class. */
package util.clock;

/* Imported classes and/or interfaces. */
import java.util.Calendar;

/** Implements a real time clock. */
public final class Clock extends Thread {
	/* Attributes. */
	/** Registers if the clock shall stop working. */
	private boolean stop_working;
	
	/** The object to be clocked. */
	private Clockable object;
	
	/** Holds the current time being clocked. */
	private int current_time;
	
	/** The clock count step.
	 *  The default value is one second.*/
	private int step = 1;
	
	/** The clock count unity, as in Calendar field.
	 *  The default value is java.util.Calendar.SECOND. */
	private int unity = Calendar.SECOND;	
	
	/* Methods. */
	/** Constructor.
	 *  @param object The object to be clocked. */
	public Clock(Clockable object) {
		this.object = object;
		this.current_time = 0;
		this.stop_working = false;
	}
	
	/** Changes the clock's counting step.
	 *  @param step The counting step. */
	public void setStep(int step) {
		this.step = step;
	}
	
	/** Changes the clock's counting unity.
	 *  @param unity The java.util.Calendar's time unity. */
	public void setUnity(int unity) {
		this.unity = unity;
	}
	
	/** Returns the current clocked time.
	 * @return The current clocked time. */
	public int getCurrentTime() {
		return this.current_time;
	}
	
	/** Indicates that the clock must stop working. */
	public void stopWorking() {
		this.stop_working = true;
	}
			
	public void run() {
		// calls the object's act method, when it is time
		int prev_ref = Calendar.getInstance().get(this.unity);
		while(!this.stop_working) {
			int next_ref = Calendar.getInstance().get(this.unity);
			
			if(next_ref < prev_ref) next_ref = next_ref + prev_ref + this.step;
			if(next_ref - prev_ref > this.step - 1) {
				prev_ref = Calendar.getInstance().get(this.unity);
				this.object.act();
				this.current_time++;
			}
		}
	}
}