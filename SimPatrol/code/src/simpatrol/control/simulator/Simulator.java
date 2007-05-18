package simpatrol.control.simulator;

import java.util.Collection;

import simpatrol.control.daemon.ActionDaemon;
import simpatrol.control.daemon.AnalysisReportDaemon;
import simpatrol.control.daemon.PerceptionDaemon;
import simpatrol.control.daemon.SimulationLogDaemon;
import simpatrol.model.agent.Society;
import simpatrol.model.graph.Graph;

/**
 * @model.uin <code>design:node:::5fdg2f17vcpxt33cmgu</code>
 */
public abstract class Simulator {

	/**
	 * @model.uin <code>design:node:::hyht6f17ujey8gc0qlv</code>
	 */
	public Collection<Society> society;
	
	public Graph graph;
	
	public Collection<PerceptionDaemon> perceptionDaemon;
	
	public Collection<ActionDaemon> actionDaemon;
	
	public AnalysisReportDaemon analysisReportDaemon;
	
	public SimulationLogDaemon simulationLogDaemon;

	/**
	 * @model.uin <code>design:node:::co9mf17vcpxt-qsrxax:5fdg2f17vcpxt33cmgu</code>
	 */
	public void startSimulation() {
		/* default generated stub */;

	}
}
