/* CycledSimulator.java */

/* The package of this class. */
package control.simulator;

/* Imported classes and/or interfaces. */
import model.agent.Agent;
import model.agent.AgentStates;
import model.agent.Society;
import model.graph.DynamicEdge;
import model.graph.DynamicVertex;
import model.graph.Graph;
import control.daemon.AnalysisReportDaemon;
import control.daemon.SimulationLogDaemon;

/** Implements the simulator that counts the simulation time
 *  based on the agents' reasoning cycle. */
public class CycledSimulator extends Simulator {
	/* Attributes. */
	/** Holds how many cycles were simulated. */
	private int elapsed_cycles;
	
	/** Registers if the current cycle has already been considered. */
	private boolean considered_current_cycle;	
	
	/* Methods. */
	/** Constructor.
	 *  @param simulation_time The time of simulation.
	 *  @param graph The graph to be pattroled.
	 *  @param analysis_report_daemon The daemon to attend requisitios for analysis reports.
	 *  @param simulation_log_daemon The daemon to produce logs of the simulation. */
	public CycledSimulator(int simulation_time, Graph graph, AnalysisReportDaemon analysis_report_daemon, SimulationLogDaemon simulation_log_daemon) {
		super(simulation_time, graph, analysis_report_daemon, simulation_log_daemon);
		this.elapsed_cycles = 0;
		this.considered_current_cycle = false;
	}
	
	/** Verifies in all the societies of the simulator if
	 *  all the agents just acted. */
	private boolean allAgentsJustActed() {				
		// obtains the societies of the simulator
		Object[] societies_array = this.societies.toArray();		
		
		// verifies if there is at least one agent
		boolean there_are_agents = false;
		
		// for each society
		for(int i = 0; i < societies_array.length; i++) {
			// obtains the agents of the current society
			Agent[] agents = ((Society) societies_array[i]).getAgents();
			
			// for each agent, verifies its state
			for(int j = 0; j < agents.length; j++) {
				if(!there_are_agents) there_are_agents = true;
				
				if(agents[j].getAgentState() != AgentStates.JUST_ACTED)
					return false;
			}
		}
		
		// return true, if there is at least one agent		
		if(there_are_agents) return true;
		else return false;
	}	
	
	public void startSimulation() {
		// while not all the cycles were simulated
		while(this.simulation_time > this.elapsed_cycles) {
			// if all the agents just acted ant the current cycle
			// hasn't been considered yet
			if(this.allAgentsJustActed() && !this.considered_current_cycle) {
				// TODO completar!!
				
				// verifies what dynamic vertexes/edges must appear/disappear
				this.assuresGaphDynamicity();
				
				// registers that the current cycle was considered
				this.considered_current_cycle = true;
				
				// increments the elapsed cycles
				this.elapsed_cycles++;
			}
			// else, resets to false the considered_current_cycle flag
			else this.considered_current_cycle = false;
		}
	}
	
	/** Verifies what dynamic vertexes and/or 
	 *  edges must appear or disappear. */
	private void assuresGaphDynamicity() {
		// 1. verifies which edges must appear/disappear
		// obtains the dynamic edges
		DynamicEdge[] dynamic_edges = this.graph.getDynamicEdges();
		
		// for each dynamic edge
		for(int i = 0; i < dynamic_edges.length; i++) {
			// verifies if the edge is appearing
			if(dynamic_edges[i].isAppearing()) {
				// atualizes the appearing tpd
				dynamic_edges[i].getAppearingTPD().nextBoolean();
				
				// verifies if the edge must disappear now
				if(dynamic_edges[i].getDisappearingTPD().nextBoolean())
					dynamic_edges[i].setIsAppearing(false);
			}
			// else
			else {
				// verifies if the edge must appear now
				if(dynamic_edges[i].getAppearingTPD().nextBoolean())
					dynamic_edges[i].setIsAppearing(true);
				
				// atualizes the disappearing tpd
				dynamic_edges[i].getDisappearingTPD().nextBoolean();
			}
		}
		
		// 2. verifies which vertexes must appear/disappear
		// obtains the dynamic vertexes
		DynamicVertex[] dynamic_vertexes = this.graph.getDynamicVertexes();
		
		//for each dynamic vertex
		for(int i = 0; i < dynamic_vertexes.length; i++) {
			// verifies if the vertex is appearing
			if(dynamic_vertexes[i].isAppearing()) {
				// atualizes the appearing tpd
				dynamic_vertexes[i].getAppearingTPD().nextBoolean();
				
				// verifies if the vertex must disappear now
				if(dynamic_vertexes[i].getDisappearingTPD().nextBoolean())
					dynamic_vertexes[i].setIsAppearing(false);
			}
			// else
			else {
				// verifies if the vertex must appear now
				if(dynamic_vertexes[i].getAppearingTPD().nextBoolean())
					dynamic_vertexes[i].setIsAppearing(true);
				
				// atualizes the disappearing tpd
				dynamic_vertexes[i].getDisappearingTPD().nextBoolean();
			}
		}		
	}

	public int getSimulatedTime() {
		return this.elapsed_cycles;
	}
}
