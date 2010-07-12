/* MortalityControllerRobot.java */

/* The package of this class. */
package control.robot;

/* Imported classes and/or interfaces. */
import control.simulator.RealTimeSimulator;
import model.agent.Agent;
import model.etpd.EventTimeProbabilityDistribution;
import model.interfaces.Mortal;

/** Implements the robots that assure the death of the mortal objects
 *  of a simulation.
 *  
 *  Used by real time simulators.
 *   
 *  @see RealTimeSimulator */
public final class MortalityControllerRobot extends Robot {
	/* Attributes. */
	/** The mortal object to be controlled. */
	private Mortal object;

	/** The real time simulator of the patrolling task. */
	private static RealTimeSimulator simulator;
	
	/* Methods. */
	/** Constructor.
	 * 
	 *  @param clock_thread_name The name of the thread of the clock of this robot.
	 *  @param object The mortal object to be controlled. */
	public MortalityControllerRobot(String clock_thread_name, Mortal object) {
		super(clock_thread_name);
		this.object = object;
	}
	
	/** Configures the simulator of the patrolling task.
	 * 
	 *  @param rt_simulator The simulator of the patrolling task. */
	public static void setSimulator(RealTimeSimulator rt_simulator) {
		simulator = rt_simulator;
	}

	public void act(int time_gap) {
		for(int i = 0; i < time_gap; i++) {
			// obtains the probability distribution for the death of the mortal object
			EventTimeProbabilityDistribution death_tpd = this.object.getDeathTPD();
			
			// if there's a death tpd and the object must die now 
			if(death_tpd != null && death_tpd.nextBoolean()) {
				// kills the object
				this.object.die();
				
				// if the object is an agent, stops its agent_daemons
				if(this.object instanceof Agent)
					simulator.stopAgentDaemons((Agent) this.object);
				
				// stops this robot
				this.stopWorking();
				
				// removes this robot from the rt simulator
				simulator.removeMortalityControllerRobot(this);
				
				// quits the method
				return;
			}
		}
	}
}