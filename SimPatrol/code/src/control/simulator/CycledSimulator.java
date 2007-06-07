/* CycledSimulator.java */

/* The package of this class. */
package control.simulator;

/* Imported classes and/or interfaces. */
import model.agent.Agent;
import model.agent.AgentStates;
import model.agent.Society;
import model.graph.Graph;
import model.graph.Vertex;
import model.interfaces.Dynamic;
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
		
		// registers if there is at least one agent
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
		
		// returns true, if there is at least one agent		
		if(there_are_agents) return true;
		else return false;
	}
	
	/** Verifies what dynamic objects must
	 *  appear or disappear. */
	private void assuresGraphDynamicity() {
		// obtains the dynamic objects of the graph
		Dynamic[] dynamic_objects = this.graph.getDynamicComponents();
		
		// 1. treats the vertexes appearing/disappearing events
		for(int i = 0; i < dynamic_objects.length; i++)
			if(dynamic_objects[i] instanceof Vertex)
				// verifies if the vertex is appearing
				if(dynamic_objects[i].isAppearing()) {
					// atualizes the appearing etpd
					dynamic_objects[i].getAppearingETPD().nextBoolean();
					
					// verifies if the vertex must disappear now
					if(dynamic_objects[i].getDisappearingETPD().nextBoolean())
						dynamic_objects[i].setIsAppearing(false);					
				}
				// else
				else {
					// verifies if the vertex must appear now
					if(dynamic_objects[i].getAppearingETPD().nextBoolean())
						dynamic_objects[i].setIsAppearing(true);
					
					// atualizes the disappearing etpd
					dynamic_objects[i].getDisappearingETPD().nextBoolean();
				}
		
		// 2. treats the others graph's objects
		for(int i = 0; i < dynamic_objects.length; i++)
			if(!(dynamic_objects[i] instanceof Vertex))
				// verifies if the object is appearing
				if(dynamic_objects[i].isAppearing()) {
					// atualizes the appearing etpd
					dynamic_objects[i].getAppearingETPD().nextBoolean();
					
					// verifies if the object must disappear now
					if(dynamic_objects[i].getDisappearingETPD().nextBoolean())
						dynamic_objects[i].setIsAppearing(false);					
				}
				// else
				else {
					// verifies if the object must appear now
					if(dynamic_objects[i].getAppearingETPD().nextBoolean())
						dynamic_objects[i].setIsAppearing(true);
					
					// atualizes the disappearing etpd
					dynamic_objects[i].getDisappearingETPD().nextBoolean();
				}
		
		// TODO trata outros objetos dinamicos (ex: agentes) 
	}	
	
	public void startSimulation() {
		// while not all the cycles were simulated
		while(this.simulation_time > this.elapsed_cycles) {
			// if all the agents just acted ant the current cycle
			// hasn't been considered yet
			if(this.allAgentsJustActed() && !this.considered_current_cycle) {
				// TODO completar!!
				
				// verifies which dynamic vertexes/edges must appear/disappear
				this.assuresGraphDynamicity();
				
				// registers that the current cycle was considered
				this.considered_current_cycle = true;
				
				// increments the elapsed cycles
				this.elapsed_cycles++;
				
				// TODO retirar linha abaixo
				System.out.println("ciclo " + this.elapsed_cycles);
			}
			// else, resets to false the considered_current_cycle flag
			else this.considered_current_cycle = false;
		}
		
		// stops the simulation
		this.stopSimulation();
	}
	
	public int getSimulatedTime() {
		return this.elapsed_cycles;
	}
	
	public void stopSimulation() {
		// TODO Auto-generated method stub	
	}
}
