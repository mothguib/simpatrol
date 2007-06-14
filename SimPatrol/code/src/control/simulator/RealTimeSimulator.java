/* RealTimeSimulator.java */

/* The package of this class. */
package control.simulator;

/* Imported classes and/or interfaces. */
import java.util.GregorianCalendar;
import java.util.HashSet;
import java.util.Set;
import model.agent.Society;
import model.graph.Graph;
import model.interfaces.Dynamic;
import model.interfaces.Mortal;
import control.daemon.MortalityControllerDaemon;
import control.daemon.AnalysisReportDaemon;
import control.daemon.DynamicityControllerDaemon;
import control.daemon.SimulationLogDaemon;
import util.chronometer.Chronometer;
import util.chronometer.Chronometerable;

/** Implements a real time simulator. */
public class RealTimeSimulator extends Simulator implements Chronometerable {
	/* Attributes. */
	/** The chronometer of the real time simulation. */
	private Chronometer chronometer;
	
	/** The daemons that assure the dynamic objects the correct behaviour.
	 *  Its default value is NULL. */
	private Set<DynamicityControllerDaemon> dynamic_daemons = null;
	
	/** The daemons that assure the mortal objects the correct beaviour.
	 *  Its default value is NULL. */
	private Set<MortalityControllerDaemon> mortal_daemons= null;
	
	/* Methods. */
	/** Constructor.
	 *  @param simulation_time The time of simulation.
	 *  @param graph The graph to be pattroled.
	 *  @param societies The societies of the simulation  
	 *  @param analysis_report_daemon The daemon to attend requisitios for analysis reports.
	 *  @param simulation_log_daemon The daemon to produce logs of the simulation. */	
	public RealTimeSimulator(int simulation_time, Graph graph, Society[] societies, AnalysisReportDaemon analysis_report_daemon, SimulationLogDaemon simulation_log_daemon) {
		super(simulation_time, graph, societies, analysis_report_daemon, simulation_log_daemon);
		
		// creates the chronometer
		this.chronometer = new Chronometer(this, this.simulation_time);
		
		// creates the dynamicity controller daemons, if necessary
		this.createDynamicityControllerDaemons();
		
		// creates the mortality controller daemons, if necessary
		this.createMortalityControllerDaemons();
		
		// TODO continue creating!!!
	}
	
	/** Obtains the dynamic objects and creates the
	 *  respective dymamicity controller daemons. */
	private void createDynamicityControllerDaemons() {
		// obtains the dynamic objects
		Dynamic[] dynamic_objects = this.getDynamicObjects();
		
		// if there are any dynamic objects
		if(dynamic_objects.length > 0) {
			// initiates the dynamic daemons set
			this.dynamic_daemons = new HashSet<DynamicityControllerDaemon>();
			
			// for each one, creates a dynamicity controller daemon
			for(int i = 0; i < dynamic_objects.length; i++)
				this.dynamic_daemons.add(new DynamicityControllerDaemon(dynamic_objects[i]));
		}
		else this.dynamic_daemons = null;
	}
	
	/** Obtains the mortal objects and creates the
	 *  respective mortality controller daemons. */
	private void createMortalityControllerDaemons() {
		// obtains the mortal objects
		Mortal[] mortal_objects = this.getMortalObjects();
		
		// if there are any mortal objects
		if(mortal_objects.length > 0) {
			// initiates the mortal daemons set
			this.mortal_daemons = new HashSet<MortalityControllerDaemon>();
			
			// for each one, creates a mortality controller daemon
			for(int i = 0; i < mortal_objects.length; i++)
				this.mortal_daemons.add(new MortalityControllerDaemon(mortal_objects[i], this));
		}
		else this.mortal_daemons = null;
	}
	
	/** Starts each one of the current dynamicity controller daemons. */
	private void startDynamicityControllerDaemons() {
		if(this.dynamic_daemons != null) {
			Object[] dynamic_daemons_array = this.dynamic_daemons.toArray();
			for(int i = 0; i < dynamic_daemons_array.length; i++)
				((DynamicityControllerDaemon) dynamic_daemons_array[i]).startWorking();
		}
	}
	
	/** Starts each one of the current mortality controller daemons. */
	private void startMortalityControllerDaemons() {
		if(this.mortal_daemons != null) {
			Object[] mortal_daemons_array = this.mortal_daemons.toArray();
			for(int i = 0; i < mortal_daemons_array.length; i++)
				((MortalityControllerDaemon) mortal_daemons_array[i]).startWorking();
		}
	}
	
	/** Stops each one of current dynamicity controller daemons. */
	private void stopDynamicityControllerDaemons() {
		if(this.dynamic_daemons != null) {
			Object[] dynamic_daemons_array = this.dynamic_daemons.toArray();
			for(int i = 0; i < dynamic_daemons_array.length; i++)
				((DynamicityControllerDaemon) dynamic_daemons_array[i]).stopWorking();
		}
	}
	
	/** Stops each one of current mortality controller daemons. */
	private void stopMortalityControllerDaemons() {
		if(this.mortal_daemons != null) {
			Object[] mortal_daemons_array = this.mortal_daemons.toArray();
			for(int i = 0; i < mortal_daemons_array.length; i++)
				((MortalityControllerDaemon) mortal_daemons_array[i]).stopWorking();
		}
	}
	
	/** Removes a given mortality controller daemon from
	 *  the set of mortality controller daemons.
	 *  @param mortal_daemon The mortality controller daemon to be removed. */
	public void removeMortalityControllerDaemon(MortalityControllerDaemon mortal_daemon) {
		this.mortal_daemons.remove(mortal_daemon);
	}
	
	public void startSimulation() {
		// starts the chronometer
		this.chronometer.start();
		
		// starts the dynamicity controller daemons
		this.startDynamicityControllerDaemons();
		
		// starts the mortality controller daemons
		this.startMortalityControllerDaemons();
		
		// starts the societies
		this.startSocieties();
		
		// TODO completar com outros starts!!!
	}
	
	public void stopSimulation() {
		// stops the dynamicity controller daemons
		this.stopDynamicityControllerDaemons();
		
		// stops the mortality controller daemons
		this.stopMortalityControllerDaemons();
		
		// stops the societies
		this.stopSocieties();
		
		// TODO continue stoppings!!!
	}

	public int getSimulatedTime() {
		return this.chronometer.getElapsedTime();
	}
	
	public void startWorking() { 		
		System.out.println(new GregorianCalendar().getTime().toString());
		// TODO Corrigir!		
	}

	public void stopWorking() {
		// stops the simulator
		this.stopSimulation();
		
		// TODO retirar linha abaixo
		System.out.println(new GregorianCalendar().getTime().toString());		
	}
}
