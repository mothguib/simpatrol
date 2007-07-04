/* Chronometer.java */

/* The package of this class. */
package util.timemeter;

/* Imported classes and/or interfaces. */
import java.util.Calendar;

/** Implements a chronometer. */
public final class Chronometer extends Thread implements Timemeterable {
	/* Attributes. */
	/** The object to be chronometrized. */
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
	
	/* Methods. */
	/** Constructor.
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
	 *  @param step The counting step. */
	public void setStep(int step) {
		this.step = step;
	}
	
	/** Changes the chronometer's counting unity.
	 *  @param unity The java.util.Calendar's time unity.
	 *  @see Calendar */
	public void setUnity(int unity) {
		this.unity = unity;
	}
	
	public int getElapsedTime() {
		return this.elapsed_time;
	}
	
	public void run() {
		// lets the chronometrized object start working
		this.object.startWorking();
		
		// counts the elapsed time, oriented by the count unity
		int prev_ref = Calendar.getInstance().get(this.unity);		
		while(true) {						
			int next_ref = Calendar.getInstance().get(this.unity);
			
			if(next_ref < prev_ref) next_ref = next_ref + prev_ref + this.step;			
			if(next_ref - prev_ref > this.step - 1) {
				this.elapsed_time = this.elapsed_time + (next_ref - prev_ref);
				prev_ref = Calendar.getInstance().get(this.unity);				
			}
			
			// checks if the elapsed time hit the deadline
			if(this.elapsed_time >= this.deadline) {
				// lets the chronometrized object stop working
				this.object.stopWorking();
				
				// ends the thread work
				return;
			}
		}
	}
}