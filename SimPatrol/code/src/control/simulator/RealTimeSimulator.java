/* RealTimeSimulator.java */

/* The package of this class. */
package control.simulator;

/* Imported classes and/or interfaces. */
import java.util.GregorianCalendar;
import java.util.HashSet;
import java.util.Set;

import model.agent.OpenSociety;
import model.agent.Society;
import model.graph.Graph;
import model.interfaces.Dynamic;
import control.daemon.AgentsDeathControllerDaemon;
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
	
	/** The daemon that collects the dead agents as a kind of
	 *  garbage collector. */
	private AgentsDeathControllerDaemon agents_death_daemon;
	
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
		
		// creates the agents' death controller daemon, if necessary
		this.createAgentsDeathControllerDaemon();
		
		// TODO continue creating!!!
	}
	
	/** Obtains the dynamic objects and creates the
	 *  respective dymamicity controller daemons. */
	private void createDynamicityControllerDaemons() {
		// obtains the dynamic objects
		Dynamic[] dynamic_objects = this.getDynamicObjects();
		
		// if there are dynamic objects
		if(dynamic_objects.length > 0) {
			// initiates the dynamic daemons set
			this.dynamic_daemons = new HashSet<DynamicityControllerDaemon>();
			
			// for each one, creates a dynamicity controller daemon
			for(int i = 0; i < dynamic_objects.length; i++)
				this.dynamic_daemons.add(new DynamicityControllerDaemon(dynamic_objects[i]));
		}
		else this.dynamic_daemons = null;
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
	
	/** Creates the agents' death controller daemon. */
	private void createAgentsDeathControllerDaemon() {
		// obtains the open societies
		OpenSociety[] open_societies = null;
		Set<OpenSociety> open_societies_set = new HashSet<OpenSociety>();
		
		Object[] societies_array = this.societies.toArray();
		for(int i = 0; i < societies_array.length; i++)
			if(societies_array[i] instanceof OpenSociety)
				open_societies_set.add((OpenSociety) societies_array[i]);
		
		if(open_societies_set.size() > 0) {
			open_societies = new OpenSociety[open_societies_set.size()];
			Object[] open_societies_array = open_societies_set.toArray();
			for(int i = 0; i < open_societies_array.length; i++)
				open_societies[i] = (OpenSociety) open_societies_array[i];			
		}
		
		// if there are open societies
		if(open_societies != null && open_societies.length > 0)
			this.agents_death_daemon = new AgentsDeathControllerDaemon(open_societies, this.dynamic_daemons);
	}
	
	public void startSimulation() {
		// starts the chronometer
		this.chronometer.start();
		
		// starts the agents' death controller daemon
		this.agents_death_daemon.start();
		
		// starts the dynamicity controller daemons
		this.startDynamicityControllerDaemons();
		
		// starts the societies
		this.startSocieties();
		
		// TODO completar com outros starts!!!
	}
	
	public void stopSimulation() {
		// stops the agents' death controller daemon
		this.agents_death_daemon.stopWorking();
		
		// stops the dynamicity controller daemons
		this.stopDynamicityControllerDaemons();
		
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
