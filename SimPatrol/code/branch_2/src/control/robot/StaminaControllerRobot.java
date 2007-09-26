/* StaminaControllerRobot.java */

/* The package of this class. */
package control.robot;

/* Imported classes and/or interfaces. */
import control.daemon.ActionDaemon;
import control.simulator.RealTimeSimulator;
import model.agent.Agent;
import model.limitation.Limitation;
import model.limitation.StaminaLimitation;
import model.permission.PerceptionPermission;

/** Implements the robots that assure the stamina correct value to the agents.
 *  
 *  Used by real time simulators.
 *   
 *  @see RealTimeSimulator */
public class StaminaControllerRobot extends Robot {
	/* Attributes. */
	/** The agent whose stamina is to be controlled. */
	private Agent agent;
	
	/** The stamina value to be decremented related to the perceptions
	 *  of the agent. */
	private double perceptions_stamina_cost;
	
	/** The action daemon that attends the agent's intentions of actions. */
	private ActionDaemon action_daemon;
	
	/* Methods. */
	/** Constructor.
	 * 
	 *  @param clock_thread_name The name of the thread of the clock of this robot.
	 *  @param agent The agent whose stamina is to be controlled.
	 *  @param action_daemon The action daemon that controls the agent. */
	public StaminaControllerRobot(String clock_thread_name, Agent agent, ActionDaemon action_daemon) {
		super(clock_thread_name);
		this.agent = agent;
		this.action_daemon = action_daemon;
		
		// obtains the stamina value to be decremented
		// related to the perceptions
		this.perceptions_stamina_cost = 0;		
		PerceptionPermission[] allowed_perceptions = this.agent.getAllowedPerceptions();
		for(int i = 0; i < allowed_perceptions.length; i++) {
			Limitation[] limitations = allowed_perceptions[i].getLimitations();
			for(int j = 0; j < limitations.length; j++)
				if(limitations[j] instanceof StaminaLimitation) {
					this.perceptions_stamina_cost = this.perceptions_stamina_cost + ((StaminaLimitation) limitations[j]).getCost();
					break;
				}
		}
	}
	
	/** Returns the agent controlled by the robot.
	 *  
	 *  @return The agent controlled by the robot. */
	public Agent getAgent() {
		return this.agent;
	}

	public void act(int time_gap) {
		for(int i = 0; i < time_gap; i++) {			
			this.agent.decStamina(this.perceptions_stamina_cost + this.action_daemon.getPlanning_stamina_cost());
			
			// screen message
			if(this.perceptions_stamina_cost + this.action_daemon.getPlanning_stamina_cost() > 0)
				System.out.println("[SimPatrol.Event] agent " + this.agent.getObjectId() + " spent stamina.");
		}
	}
	
	public void start() {
		super.start();
		
		// screen message
		System.out.println("[SimPatrol.StaminaRobot(" + this.agent.getObjectId() + ")]: Started working.");
	}
	
	public void stopWorking() {
		super.stopWorking();
		
		// screen message
		System.out.println("[SimPatrol.StaminaRobot(" + this.agent.getObjectId() + ")]: Stopped working.");
	}	
}