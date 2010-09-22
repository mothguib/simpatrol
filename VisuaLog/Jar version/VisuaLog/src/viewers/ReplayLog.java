package viewers;


/* * * * * * * * * * * * * * * * * * * * * * * *
 * 
 *    Interface for viewers based on the replay method
 *    (ie : recreate from a log file)
 *    
 *    @author : Cyril Poulet
 * 
 * * * * * * * * * * * * * * * * * * * * * * * */
public interface ReplayLog {

	public boolean isPlaying();
	
	public void play();
	
	public void pause();
	
	public double getSpeed();
	
	public void setSpeed(double speed);
	
}
