/* Chronometerable.java */

/* The package of this class. */
package util.chronometer;

/* Imported classes and/or interfaces. */
import java.util.Calendar;

/** Implements a chronometer. */
public class Chronometer extends Thread {
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
	 *  @param object The object to be chronometrized.
	 *  @param deadline The deadline, when the chronometer must stop working. */
	public Chronometer(Chronometerable object, int deadline) {
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
	 *  @param unity The java.util.Calendar's time unity. */
	public void setUnity(int unity) {
		this.unity = unity;
	}
	
	/** Returns the elapsed time.
	 * @return The elapsed chronometrized time. */
	public int getElapsedTime() {
		return this.elapsed_time;
	}	
	
	public void run() {
		super.run();
		
		// lets the chronometrized object start working
		this.object.startWorking();
		
		// counts the elapsed time, oriented by the count unity
		int prev_ref = Calendar.getInstance().get(this.unity);		
		while(true) {						
			int next_ref = Calendar.getInstance().get(this.unity);
			
			if(next_ref < prev_ref) next_ref = next_ref + prev_ref + this.step;			
			if(next_ref - prev_ref > this.step - 1) {
				prev_ref = Calendar.getInstance().get(this.unity);
				this.elapsed_time++;
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