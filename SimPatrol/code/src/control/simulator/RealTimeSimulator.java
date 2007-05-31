/* RealTimeSimulator.java */

/* The package of this class. */
package control.simulator;

/* Imported classes and/or interfaces. */
import java.util.GregorianCalendar;

import model.graph.Graph;
import control.daemon.AnalysisReportDaemon;
import control.daemon.SimulationLogDaemon;
import util.chronometer.Chronometer;
import util.chronometer.Chronometerable;

/** Implements a real time simulator. */
public class RealTimeSimulator extends Simulator implements Chronometerable {
	/* Attributes. */
	/** The chronometer of the real time simulation. */
	private Chronometer chronometer;
	
	/* Methods. */
	/** Constructor.
	 *  @param simulation_time The time of simulation.
	 *  @param graph The graph to be pattroled.
	 *  @param analysis_report_daemon The daemon to attend requisitios for analysis reports.
	 *  @param simulation_log_daemon The daemon to produce logs of the simulation. */	
	public RealTimeSimulator(int simulation_time, Graph graph, AnalysisReportDaemon analysis_report_daemon, SimulationLogDaemon simulation_log_daemon) {
		super(simulation_time, graph, analysis_report_daemon, simulation_log_daemon);
	}
	
	public void startSimulation() {
		// creates and starts the chronometer
		this.chronometer = new Chronometer(this, this.simulation_time);
		this.chronometer.start();
		
		// TODO completar!!!
	}

	public int getSimulatedTime() {
		return this.chronometer.getElapsedTime();
	}
	
	public void startWorking() { 		
		System.out.println(new GregorianCalendar().getTime().toString());
		// TODO Corrigir!		
	}

	public void stopWorking() {
		System.out.println(new GregorianCalendar().getTime().toString());
		// TODO Corrigir!		
	}
}
