/* Simulator.java */

/* The package of this class. */
package control.simulator;

/* Imported classes and/or interfaces. */
import java.util.HashSet;
import java.util.Set;
import model.agent.Society;
import model.graph.Graph;
import model.interfaces.Dynamic;
import control.daemon.ActionDaemon;
import control.daemon.PerceptionDaemon;
import control.daemon.SimulationLogDaemon;
import control.daemon.AnalysisReportDaemon;

/** Implements the simulator of the patrolling task. */
public abstract class Simulator {
	/* Atributes. */
	/** The time of simulation.
	 * 
	 *  Measured in cycles, if the simulator is a cycled one,
	 *  or in seconds, if it is a real time one. */
	protected int simulation_time;

	/** The graph of the simulation. */
	protected Graph graph;
	
	/** The set of societies of agents involved with the simulation. */
	protected Set<Society> societies;
	
	/** The set of daemons that attend requisitions of perceptions. */
	private Set<PerceptionDaemon> perception_daemons;

	/** The set of daemons that attend requisitions of actions. */
	private Set<ActionDaemon> action_daemons;
	
	/** The daemon that attends requisitions of analysis reports. */
	private AnalysisReportDaemon analysis_report_daemon;
	
	/** The daemon that produces logs of the simulation. */
	private SimulationLogDaemon simulation_log_daemon;
	
	/* Methods. */
	/** Constructor.
	 *  @param simulation_time The time of simulation.
	 *  @param graph The graph to be pattroled.
	 *  @param analysis_report_daemon The daemon to attend requisitios for analysis reports.
	 *  @param simulation_log_daemon The daemon to produce logs of the simulation. */
	public Simulator(int simulation_time, Graph graph, AnalysisReportDaemon analysis_report_daemon, SimulationLogDaemon simulation_log_daemon) {
		this.simulation_time = simulation_time;
		this.graph = graph;
		this.analysis_report_daemon = analysis_report_daemon;
		this.simulation_log_daemon = simulation_log_daemon;
		
		this.societies = new HashSet<Society>();
		this.perception_daemons = new HashSet<PerceptionDaemon>();
		this.action_daemons = new HashSet<ActionDaemon>();
	}
	
	/** Adds a given society to the simulator.
	 *  @param society The society to be added. */	
	public void addSociety(Society society) {
		this.societies.add(society);
	}
	
	/** Obtains the dynamic objects of the simulation. */
	protected Dynamic[] getDynamicObjects() {
		// holds the dynamic objects
		Set<Dynamic> dynamic_objects = new HashSet<Dynamic>();
		
		// obtains the dynamic objects in the graph
		Dynamic[] dynamic_from_graph = this.graph.getDynamicComponents();
		for(int i = 0; i < dynamic_from_graph.length; i++)
			dynamic_objects.add(dynamic_from_graph[i]);
		
		// TODO completar com outros objetos dinamicos (ex: agentes!)
		
		// returns the answer
		Object[] dynamic_objects_array = dynamic_objects.toArray();
		Dynamic[] answer = new Dynamic[dynamic_objects_array.length];
		for(int i = 0; i <answer.length; i++)
			answer[i] = (Dynamic) dynamic_objects_array[i];		
		return answer;
	}
	
	/** Starts the simulation. */
	public abstract void startSimulation();
	
	/** Stops the simulation. */
	public abstract void stopSimulation();
		
	/** Returns the simulated time.
	 *  @return The simulated time, in cycles or in seconds. */
	public abstract int getSimulatedTime();
}