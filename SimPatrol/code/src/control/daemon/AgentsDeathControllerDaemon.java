/* AgentsDeathControllerDaemon.java */

/* The package of this class. */
package control.daemon;

import java.util.HashSet;
import java.util.Set;

import model.agent.Agent;
import model.agent.OpenSociety;
import model.agent.SeasonalAgent;

/** Implements the unique daemon that collects the dead agent
 *  as a kind of garbage collector. */
public class AgentsDeathControllerDaemon extends ClockedDaemon {
	/* Attributes */
	/** The set of open societies of the simulation. */
	private Set<OpenSociety> societies;
	
	/** The set of dynamicity controller daemons of the application. */
	private Set<DynamicityControllerDaemon> control_daemons;
	
	/* Methods */
	/** Constructor.
	 *  @param societies The open societies od the simulation.
	 *  @param control_daemons The dynamicity controller daemons.*/
	public AgentsDeathControllerDaemon(OpenSociety[] societies, Set<DynamicityControllerDaemon> control_daemons) {
		super();

		this.societies = new HashSet<OpenSociety>();
		for(int i = 0; i < societies.length; i++)
			this.societies.add(societies[i]);
		
		this.control_daemons = control_daemons;		
	}

	public void act() {
		// for each open society
		Object[] societies_array = this.societies.toArray();
		for(int i = 0; i < societies_array.length; i++) {
			// obtains the current society
			OpenSociety society = (OpenSociety) societies_array[i];
			
			// obtains its seasonal agents
			Agent[] agents = society.getAgents();
			
			// for each agent
			for(int j = 0; j < agents.length; j++) {
				// obtains the current seasonal agent
				SeasonalAgent agent = (SeasonalAgent) agents[j];
				
				// if the agent is already dead
				if(agent.isDead()) {
					// removes it from the society
					society.removeAgent(agent);
					
					// finds its control daemon
					DynamicityControllerDaemon daemon = null;
					Object[] control_daemons_array = this.control_daemons.toArray();
					for(int k = 0; k < control_daemons_array.length; k++)
						if(((DynamicityControllerDaemon) control_daemons_array[k]).getDynamicObject().equals(agent)) {
							daemon = (DynamicityControllerDaemon) control_daemons_array[k];							
							break;
						}
					
					// stops and finishes the daemon
					if(daemon != null) {
						daemon.stopWorking();
						this.control_daemons.remove(daemon);
					}
				}
			}
		}
	}

}
