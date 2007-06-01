/* RealTimeSimulator.java */

/* The package of this class. */
package control.simulator;

/* Imported classes and/or interfaces. */
import java.util.GregorianCalendar;
import java.util.HashSet;
import java.util.Set;
import model.graph.Graph;
import model.interfaces.Dynamic;
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
	
	/** The daemons that assure the dynamic objects correct behaviour.
	 *  Its default value is NULL. */
	private Set<DynamicityControllerDaemon> dynamic_daemons = null;
	
	/* Methods. */
	/** Constructor.
	 *  @param simulation_time The time of simulation.
	 *  @param graph The graph to be pattroled.
	 *  @param analysis_report_daemon The daemon to attend requisitios for analysis reports.
	 *  @param simulation_log_daemon The daemon to produce logs of the simulation. */	
	public RealTimeSimulator(int simulation_time, Graph graph, AnalysisReportDaemon analysis_report_daemon, SimulationLogDaemon simulation_log_daemon) {
		super(simulation_time, graph, analysis_report_daemon, simulation_log_daemon);
		
		// creates the chronometer
		this.chronometer = new Chronometer(this, this.simulation_time);
		
		// creates the dynamicity controller daemons, if necessary
		this.createDynamicityControllerDaemons();
		
		// TODO continue creating!!!
	}
	
	/** Obtains the dynamic objects and creates the
	 *  respective dymamicity controller daemons. */
	private void createDynamicityControllerDaemons() {
		// iniates the dynamic daemons set, if necessary
		if(this.dynamic_daemons == null) this.dynamic_daemons = new HashSet<DynamicityControllerDaemon>();
		
		// obtains the dynamic objects
		Dynamic[] dynamic_objects = this.getDynamicObjects();
		
		// for each one, creates a dynamicity controller daemon
		for(int i = 0; i < dynamic_objects.length; i++)
			this.dynamic_daemons.add(new DynamicityControllerDaemon(dynamic_objects[i]));
	}
	
	/** Starts each one of the current dynamicity controller daemons. */
	private void startDynamicityControllerDaemons() {
		if(this.dynamic_daemons != null) {
			Object[] dynamic_daemons_array = this.dynamic_daemons.toArray();
			for(int i = 0; i < dynamic_daemons_array.length; i++)
				((DynamicityControllerDaemon) dynamic_daemons_array[i]).startWorking();
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
	
	public void startSimulation() {
		// starts the chronometer
		this.chronometer.start();
		
		// starts the dynamicity controller daemons
		this.startDynamicityControllerDaemons();
		
		// TODO completar com outros starts!!!
	}
	
	public void stopSimulation() {
		// stops the dynamicity controller daemons
		this.stopDynamicityControllerDaemons();
		
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
		
		System.out.println(new GregorianCalendar().getTime().toString());
		// TODO Corrigir!		
	}
}
