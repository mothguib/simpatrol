/* StaminaControllerRobot.java */

/* The package of this class. */
package control.robot;

/* Imported classes and/or interfaces. */
import control.simulator.RealTimeSimulator;
import model.action.CompoundAction;
import model.agent.Agent;

/** Implements the robots that assure the stamina correct value to the agents.
 *  
 *  Used by real time simulators.
 *   
 *  @see RealTimeSimulator */
public final class StaminaControllerRobot extends Robot {
	/* Attributes. */
	/** The agent whose stamina is to be controlled. */
	private Agent agent;
	
	/** Registers how much stamina must be decremented from the agent,
	 *  due to the execution of compound actions.
	 *  
	 *  @see CompoundAction */
	private double actions_spent_stamina;
	
	/** Registers how much stamina must be decremented from the agent,
	 *  due to the production of perceptions. */
	private double perceptions_spent_stamina;
	
	/* Methods. */
	/** Constructor.
	 * 
	 *  @param clock_thread_name The name of the thread of the clock of this robot.
	 *  @param agent The agent whose stamina is to be controlled. */
	public StaminaControllerRobot(String clock_thread_name, Agent agent) {
		super(clock_thread_name);
		this.agent = agent;
		this.actions_spent_stamina = 0;
		this.perceptions_spent_stamina = 0;
	}
	
	/** Returns the agent controlled by the robot.
	 *  
	 *  @return The agent controlled by the robot. */
	public Agent getAgent() {
		return this.agent;
	}
	
	/** Configures how much stamina must be spent due to
	 *  the execution of compound actions.
	 *  
	 *  @param spent_stamina The amount of stamina to be spent due to the execution of compound actions.*/
	public void setActions_spent_stamina(double spent_stamina) {
		this.actions_spent_stamina = spent_stamina;
	}
	
	/** Returns how much stamina must be spent due to
	 *  the execution of compound actions.
	 *  
	 *  @return The amount of stamina to be spent due to the execution of compound actions.*/
	public double getActions_spent_stamina() {
		return this.actions_spent_stamina;
	}
	
	/** Configures how much stamina must be spent due to
	 *  the production of perceptions.
	 *  
	 *  @param spent_stamina The amount of stamina to be spent due to the production of perceptions.*/
	public void setPerceptions_spent_stamina(double spent_stamina) {
		this.perceptions_spent_stamina = spent_stamina;
	}
	
	public void act(int time_gap) {
		double spent_stamina = this.actions_spent_stamina + this.perceptions_spent_stamina;
		
		if(spent_stamina > 0)
			for(int i = 0; i < time_gap; i++)
				this.agent.decStamina(spent_stamina);
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