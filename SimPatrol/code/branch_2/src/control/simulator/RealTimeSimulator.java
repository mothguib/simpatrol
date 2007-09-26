/* RealTimeSimulator.java */

/* The package of this class. */
package control.simulator;

/* Imported classes and/or interfaces. */
import java.net.SocketException;
import java.util.Calendar;
import java.util.HashSet;
import java.util.Set;
import model.agent.Agent;
import model.graph.Vertex;
import model.interfaces.Dynamic;
import model.interfaces.Mortal;
import control.robot.DynamicityControllerRobot;
import control.robot.MortalityControllerRobot;
import control.robot.StaminaControllerRobot;
import util.timer.Chronometer;
import util.timer.Chronometerable;

/** Implements a real time simulator of the patrolling task.
 * 
 *  @developer This class must have its behaviour modelled. */
public final class RealTimeSimulator extends Simulator implements Chronometerable {
	/* Attributes. */
	/** The chronometer of the real time simulation. */
	private Chronometer chronometer;
	
	/** The robots that assure the dynamic objects the correct behaviour.
	 *  Its default value is NULL. */
	private Set<DynamicityControllerRobot> dynamic_robots = null;
	
	/** The robots that assure the mortal objects the correct behaviour.
	 *  Its default value is NULL. */
	private Set<MortalityControllerRobot> mortal_robots = null;
	
	/** The robots  that assure the agents the correct stamina values.
	 *  Its default value is NULL. */
	private Set<StaminaControllerRobot> stamina_robots = null;	
	
	/* Methods. */
	/** Constructor.
	 * 
	 *  @param local_socket_number The number of the UDP socket of the main connection.
	 *  @param actualization_time_rate The time rate, in seconds, to actualize the internal model of the simulation. 
	 *  @throws SocketException */	
	public RealTimeSimulator(int local_socket_number, double actualization_time_rate) throws SocketException {
		super(local_socket_number, actualization_time_rate);
		this.chronometer = null;
	}
	
	/** Obtains the dynamic objects and creates the
	 *  respective dymamicity controller robots. */
	private void createDynamicityControllerRobots() {
		// obtains the dynamic objects
		Dynamic[] dynamic_objects = this.getDynamicObjects();
		
		// if there are any dynamic objects
		if(dynamic_objects.length > 0) {
			// initiates the dynamic robots set
			this.dynamic_robots = new HashSet<DynamicityControllerRobot>();
			
			// for each one, creates a dynamicity controller robot
			for(int i = 0; i < dynamic_objects.length; i++)
				this.dynamic_robots.add(new DynamicityControllerRobot("dynamic robot " + String.valueOf(i), dynamic_objects[i]));
		}
		else this.dynamic_robots = null;
	}
	
	/** Obtains the mortal objects and creates the
	 *  respective mortality controller robots. */
	private void createMortalityControllerDaemons() {
		// obtains the mortal objects
		Mortal[] mortal_objects = this.getMortalObjects();
		
		// if there are any mortal objects
		if(mortal_objects.length > 0) {
			// initiates the mortal robots set
			this.mortal_robots = new HashSet<MortalityControllerRobot>();
			
			// for each one, creates a mortality controller robot
			for(int i = 0; i < mortal_objects.length; i++)
				this.mortal_robots.add(new MortalityControllerRobot("mortal robot " + String.valueOf(i), mortal_objects[i]));
		}
		else this.mortal_robots = null;
		
		// configures the simulator to the robots
		MortalityControllerRobot.setSimulator(this);
	}
	
	/** Obtains the agents that must have their stamina controlled
	 *  and creates their respective stamina controller robots. */
	private void createStaminaControllerDaemons() {
		// obtains the agents to be controlled
		Agent[] agents = this.getStaminaControlledAgents();
		
		// if there are any agents to be controlled
		if(agents.length > 0) {
			// initiates the stamina robots set
			this.stamina_robots = new HashSet<StaminaControllerRobot>();
			
			// for each one, creates a stamina controller robot
			for(int i = 0; i < agents.length; i++)
				this.stamina_robots.add(new StaminaControllerRobot("stamina robot", agents[i], this.getActionDaemon(agents[i])));
		}
		else this.stamina_robots = null;
	}	
	
	/** Starts each one of the current dynamicity controller robots. */
	private void startDynamicityControllerRobots() {
		if(this.dynamic_robots != null) {
			Object[] dynamic_robots_array = this.dynamic_robots.toArray();
			for(int i = 0; i < dynamic_robots_array.length; i++)
				((DynamicityControllerRobot) dynamic_robots_array[i]).start();
		}
	}
	
	/** Starts each one of the current mortality controller robots. */
	private void startMortalityControllerRobots() {
		if(this.mortal_robots != null) {
			Object[] mortal_robots_array = this.mortal_robots.toArray();
			for(int i = 0; i < mortal_robots_array.length; i++)
				((MortalityControllerRobot) mortal_robots_array[i]).start();
		}
	}
	
	/** Starts each one of the current stamina controller robots. */
	private void startStaminaControllerRobots() {
		if(this.stamina_robots != null) {
			Object[] stamina_robots_array = this.stamina_robots.toArray();
			for(int i = 0; i < stamina_robots_array.length; i++)
				((StaminaControllerRobot) stamina_robots_array[i]).start();
		}
	}
	
	/** Creates and starts a mortality controller robot, given its mortal object.
	 * 
	 *  @param object The mortal object to be controlled by the robot. */
	public void createAndStartMortalityControlerRobot(Mortal object) {
		MortalityControllerRobot robot = new MortalityControllerRobot("mortal robot", object);
		
		if(this.mortal_robots == null)
			this.mortal_robots = new HashSet<MortalityControllerRobot>();
		
		this.mortal_robots.add(robot);
		robot.start();
	}
	
	/** Creates and starts a stamina controller robot, given its agent.
	 * 
	 *  @param agent The agent to be controlled by the robot. */
	public void createAndStartStaminaControlerRobot(Agent agent) {
		// obtains the agents to be controlled
		Agent[] agents = this.getStaminaControlledAgents();
		
		// if one of these agents is the given one
		for(int i = 0; i < agents.length; i++)
			if(agents[i].equals(agent)) {
				StaminaControllerRobot robot = new StaminaControllerRobot("stamina robot", agent, this.getActionDaemon(agent));
				
				if(this.stamina_robots == null)
					this.stamina_robots = new HashSet<StaminaControllerRobot>();
				
				this.stamina_robots.add(robot);
				robot.start();
				
				return;
			}
	}
	
	/** Stops each one of the current dynamicity controller robots. */
	private void stopDynamicityControllerRobots() {
		if(this.dynamic_robots != null) {
			Object[] dynamic_robots_array = this.dynamic_robots.toArray();
			for(int i = 0; i < dynamic_robots_array.length; i++)
				((DynamicityControllerRobot) dynamic_robots_array[i]).stopWorking();
		}
	}
	
	/** Stops each one of the current mortality controller robots. */
	private void stopMortalityControllerRobots() {
		if(this.mortal_robots != null) {
			Object[] mortal_robots_array = this.mortal_robots.toArray();
			for(int i = 0; i < mortal_robots_array.length; i++)
				((MortalityControllerRobot) mortal_robots_array[i]).stopWorking();
		}
	}
	
	/** Stops each one of the current stamina controller robots. */
	private void stopStaminaControllerRobots() {
		if(this.stamina_robots != null) {
			Object[] stamina_robots_array = this.stamina_robots.toArray();
			for(int i = 0; i < stamina_robots_array.length; i++)
				((StaminaControllerRobot) stamina_robots_array[i]).stopWorking();
		}
	}
	
	/** Removes a given mortality controller robot from
	 *  the set of mortality controller robots.
	 *  
	 *  @param mortal_robot The mortality controller robot to be removed. */
	public void removeMortalityControllerRobot(MortalityControllerRobot mortal_robot) {
		this.mortal_robots.remove(mortal_robot);
	}
	
	/** Removes and stops the stamina controller robot that controls the given agent.
	 * 
	 *  @param agent The agent controlled by the robot to be removed. */
	public void removeAndStopStaminaControllerRobot(Agent agent) {
		// finds the stamina robot of the given agent
		if(this.stamina_robots != null) {
			Object[] stamina_robots_array = this.stamina_robots.toArray();
			for(int i = 0; i < stamina_robots_array.length; i++) {
				StaminaControllerRobot robot = (StaminaControllerRobot) stamina_robots_array[i];
				
				if(robot.getAgent().equals(agent)) {
					this.stamina_robots.remove(robot);
					return;
				}
			}				
		}
	}
	
	/** @modeller This method must be modelled. */
	public void startSimulation(int simulation_time) {
		// 0th. super code execution
		super.startSimulation(simulation_time);
		
		// 1st. creating things
		// creates the chronometer and sets it to the vertexes of the graph
		this.chronometer = new Chronometer("chronometer", this, simulation_time);
		Vertex.setTime_counter(this.chronometer);
		
		// creates the dynamicity controller robots
		this.createDynamicityControllerRobots();
		
		// creates the mortality controller robots
		this.createMortalityControllerDaemons();
		
		// creates the stamina controller robots
		this.createStaminaControllerDaemons();
		
		// 2nd. starting things
		// starts the chronometer
		this.chronometer.start();
		
		// starts the dynamicity controller robots
		this.startDynamicityControllerRobots();
		
		// starts the mortality controller robots
		this.startMortalityControllerRobots();
		
		// starts the stamina controller robots
		this.startStaminaControllerRobots();
	}
	
	public void stopSimulation() {
		// 0th super code execution
		super.stopSimulation();
		
		// 1st. stopping things
		// stops the dynamicity controller robots
		this.stopDynamicityControllerRobots();
		
		// stops the mortality controller robots
		this.stopMortalityControllerRobots();
		
		// stops the stamina controller robots
		this.stopStaminaControllerRobots();
	}
	
	public void startWorking() {
		// screen message
		System.out.println("[SimPatrol.Simulator]: simulation started at " + Calendar.getInstance().getTime().toString() + ".");
	}
	
	public void stopWorking() {
		// screen message
		System.out.println("[SimPatrol.Simulator]: simulation stopped at " + Calendar.getInstance().getTime().toString() + ".");
		
		// stops the simulator
		this.stopSimulation();
	}
}