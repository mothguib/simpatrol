/* Chronometer.java */

/* The package of this class. */
package util.timer;

/* Imported classes and/or interfaces. */
import java.util.Calendar;

/** Implements a real time chronometer. */
public final class Chronometer extends Thread implements TimedObject {
	/* Attributes. */
	/** The object to be chronometerized. */
	private Chronometerable object;
	
	/** Holds the elapsed time. */
	private int elapsed_time;
	
	/** Holds the deadline, when the chronometer must stop working. */
	private int deadline;		
	
	/** The chronometer count step.
	 *  The default value is one second.*/
	private int step = 1;
	
	/** The chronometer count unity, as in Calendar field.
	 *  The default value is java.util.Calendar.SECOND. */
	private int unity = Calendar.SECOND;
	
	/** The time interval used by the chronometer to count the time.
	 * Its default value is 1000 milliseconds (1 second). */
	private long time_interval = 1000;
	
	/* Methods. */
	/** Constructor.
	 * 
	 *  @param name The name of the thread of the chronometer.
	 *  @param object The object to be chronometrized.
	 *  @param deadline The deadline, when the chronometer must stop working. */
	public Chronometer(String name, Chronometerable object, int deadline) {
		super(name);
		this.object = object;
		this.deadline = deadline;
		this.elapsed_time = 0;
	}
	
	/** Changes the chronometer's counting step.
	 * 
	 *  @param step The counting step. */
	public void setStep(int step) {
		this.time_interval = (long) (this.time_interval * Math.pow(this.step, -1));
		this.step = step;
		this.time_interval = this.time_interval * this.step;
	}
	
	/** Changes the chronometer's counting unity.
	 * 
	 *  @param unity The java.util.Calendar's time unity.
	 *  @see Calendar */
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
	
	public int getElapsedTime() {
		return this.elapsed_time;
	}
	
	public void run() {
		// lets the chronometerized object start working
		this.object.startWorking();
		
		// screen message
		System.out.println("[SimPatrol.Chronometer(" + this.getName() + ")]: Started counting time.");
		
		// counts the elapsed time, oriented by the count unity
		while(true) {
			try {
				int prev_ref = Calendar.getInstance().get(this.unity);
				sleep(this.time_interval);
				int next_ref = Calendar.getInstance().get(this.unity);
				
				if(next_ref < prev_ref) next_ref = next_ref + prev_ref + this.step;
				this.elapsed_time = this.elapsed_time + (next_ref - prev_ref);				
			}
			catch (InterruptedException e) {
				e.printStackTrace();
			}
			
			// checks if the elapsed time hit the deadline
			if(this.elapsed_time >= this.deadline) {
				// lets the chronometrized object stop working
				this.object.stopWorking();
				
				// screen message
				System.out.println("[SimPatrol.Chronometer(" + this.getName() + ")]: Stopped counting time.");
				
				// ends the thread work
				return;
			}
		}
	}
}