/* CycledSimulator.java */

/* The package of this class. */
package control.simulator;

/* Imported classes and/or interfaces. */
import model.agent.Agent;
import model.agent.AgentStates;
import model.agent.OpenSociety;
import model.agent.SeasonalAgent;
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
	 *  @param societies The societies of the simulation
	 *  @param analysis_report_daemon The daemon to attend requisitios for analysis reports.
	 *  @param simulation_log_daemon The daemon to produce logs of the simulation. */
	public CycledSimulator(int simulation_time, Graph graph, Society[] societies, AnalysisReportDaemon analysis_report_daemon, SimulationLogDaemon simulation_log_daemon) {
		super(simulation_time, graph, societies, analysis_report_daemon, simulation_log_daemon);
		this.elapsed_cycles = 0;
		this.considered_current_cycle = false;
	}
	
	/** Verifies in all the societies of the simulator if
	 *  all the agents just acted. */
	private boolean allAgentsJustActed() {				
		// obtains the societies of the simulator
		Object[] societies_array = this.societies.toArray();		
		
		// for each society
		for(int i = 0; i < societies_array.length; i++) {
			// obtains the agents of the current society
			Agent[] agents = ((Society) societies_array[i]).getAgents();
			
			// for each agent, verifies its state
			for(int j = 0; j < agents.length; j++)
				if(agents[j].getAgentState() != AgentStates.JUST_ACTED)
					return false;
		}
		
		return true;
	}
	
	/** Removes the seasonal agents that already died. */
	private void removeDeadAgents() {		
		// for each society
		Object[] societies_array = this.societies.toArray();
		for(int i = 0; i < societies_array.length; i++)
			// if it's an open society
			if(societies_array[i] instanceof OpenSociety) {
				// for each agent of it
				Agent[] agents = ((OpenSociety) societies_array[i]).getAgents();
				for(int j = 0; j < agents.length; j++) {
					// obtains the agent
					SeasonalAgent agent = (SeasonalAgent) agents[j];					
					
					// if the agent is dead
					if(agent.isDead())
						// removes it
						((OpenSociety) societies_array[i]).removeAgent(agent);
				}
			}
	}	
	
	/** Verifies what dynamic objects must
	 *  appear or disappear. */
	private void assuresSimulationDynamicity() {
		// obtains the dynamic objects of the simulation
		Dynamic[] dynamic_objects = this.getDynamicObjects();
		
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
		
		// 2. treats the others dynamic objects
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
	}
	
	public void startSimulation() {
		// starts the societies of agents of the simulator
		this.startSocieties();		
		
		// while not all the cycles were simulated
		while(this.simulation_time > this.elapsed_cycles) {
			// if all the agents just acted ant the current cycle
			// hasn't been considered yet
			if(this.allAgentsJustActed() && !this.considered_current_cycle) {
				// TODO completar!!
				
				// verifies which dynamic vertexes/edges/agents must appear/disappear
				this.assuresSimulationDynamicity();
				
				// removes the eventual dead agents
				this.removeDeadAgents();
				
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
		// stops the societies
		this.stopSocieties();
		
		// TODO completar!!!	
	}
}