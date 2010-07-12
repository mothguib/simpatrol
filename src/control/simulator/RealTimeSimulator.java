/* RealTimeSimulator.java */

/* The package of this class. */
package control.simulator;

/* Imported classes and/or interfaces. */
import java.net.SocketException;
import java.util.Calendar;
import java.util.HashSet;
import java.util.Set;
import model.graph.Vertex;
import model.interfaces.Dynamic;
import model.interfaces.Mortal;
import control.robot.DynamicityControllerRobot;
import control.robot.MortalityControllerRobot;
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
	
	/** The robots that assure the mortal objects the correct beaviour.
	 *  Its default value is NULL. */
	private Set<MortalityControllerRobot> mortal_robots = null;
	
	/* Methods. */
	/** Constructor.
	 * 
	 *  @param local_socket_number The number of the UDP socket of the main connection.
	 *  @param cycle_duration The duration, in milliseconds, of a cycle of perceptions. 
	 *  @throws SocketException */	
	public RealTimeSimulator(int local_socket_number, int cycle_duration) throws SocketException {
		super(local_socket_number, cycle_duration);
		this.chronometer = null;
	}
	
	/** Obtains the dynamic objects and creates the
	 *  respective dymamicity controller robots. */
	private void createDynamicityControllerRobots() {
		// obtains the dynamic objects
		Dynamic[] dynamic_objects = this.getDynamicObjects();
		
		// if there are any dynamic objects
		if(dynamic_objects.length > 0) {
			// initiates the dynamic rebots set
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
	
	/** Removes a given mortality controller robot from
	 *  the set of mortality controller robots.
	 *  
	 *  @param mortal_robot The mortality controller robot to be removed. */
	public void removeMortalityControllerRobot(MortalityControllerRobot mortal_robot) {
		this.mortal_robots.remove(mortal_robot);
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
		
		// 2nd. starting things
		// starts the chronometer
		this.chronometer.start();
		
		// starts the dynamicity controller robots
		this.startDynamicityControllerRobots();
		
		// starts the mortality controller robots
		this.startMortalityControllerRobots();
	}
	
	public void stopSimulation() {
		// 0th super code execution
		super.stopSimulation();
		
		// 1st. stopping things
		// stops the dynamicity controller robots
		this.stopDynamicityControllerRobots();
		
		// stops the mortality controller robots
		this.stopMortalityControllerRobots();
	}
	
	public void startWorking() { 		
		// screen message
		System.out.println("[SimPatrol.Simulator] simulation started at " + Calendar.getInstance().getTime().toString());
	}
	
	public void stopWorking() {
		// screen message
		System.out.println("[SimPatrol.Simulator] simulation stopped at " + Calendar.getInstance().getTime().toString());
		
		// stops the simulator
		this.stopSimulation();
	}
}