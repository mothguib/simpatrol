package control.simulator;

import java.util.Collection;
import model.graph.Graph;
import control.daemon.SimulationLogDaemon;
import control.daemon.AnalysisReportDaemon;

/**
 * @model.uin <code>design:node:::5fdg2f17vcpxt33cmgu</code>
 */
public abstract class Simulator {

	/**
	 * @model.uin <code>design:node:::eskvbf17vaioy-vzb1mq</code>
	 */
	public AnalysisReportDaemon analysisReportDaemon;

	/**
	 * @model.uin <code>design:node:::f1mcnf17vaioyh63h3f</code>
	 */
	public Collection perceptionDaemon;

	/**
	 * @model.uin <code>design:node:::hyht6f17ujey8gc0qlv</code>
	 */
	public Collection society;

	/**
	 * @model.uin <code>design:node:::gjtoxf17uk14ugglvpc</code>
	 */
	public Graph graph;

	/**
	 * @model.uin <code>design:node:::7664yf17vaioyemex79</code>
	 */
	public Collection actionDaemon;

	/**
	 * @model.uin <code>design:node:::axoklf17vaioy-wrqfrm</code>
	 */
	public SimulationLogDaemon simulationLogDaemon;

	/**
	 * @model.uin <code>design:node:::co9mf17vcpxt-qsrxax:5fdg2f17vcpxt33cmgu</code>
	 */
	public void startSimulation() {
		/* default generated stub */;

	}
}
