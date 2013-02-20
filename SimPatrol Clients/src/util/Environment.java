/* Environment.java */

/* The package of this class. */
package util;

/* Imported classes and/or interfaces. */
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import util.agents.AgentImage;
import util.agents.SocietyImage;
import util.graph.Graph;

/**
 * Implements the environment (graph + societies) where the patrolling task is
 * executed.
 */
public final class Environment {
	/* Attributes. */
	/** The graph of the simulation. */
	private Graph graph;

	/** The set of societies of agents involved with the simulation. */
	private Set<SocietyImage> societies;
	
	/** The society where inactive agents are put */
	private SocietyImage inactiveSociety;

	/* Methods. */
	/**
	 * Constructor.
	 * 
	 * @param graph
	 *            The graph to be pattroled.
	 * @param societies
	 *            The societies of patrollers of the simulation.
	 */
	public Environment(Graph graph, SocietyImage[] societies) {
		this.graph = graph;
		
		SocietyImage inactive_society = null;
		for(SocietyImage soc: societies){
			if(soc.id.equals("InactiveSociety"))
				inactive_society = soc;
		}
		
		// if so, we put it apart
		if(inactive_society != null){
			SocietyImage[] societies2 = new SocietyImage[societies.length - 1];
			int i = 0;
			int j = 0;
			while( i < societies.length){
				if(!(societies[i].id.equals("InactiveSociety"))){
					societies2[j] = societies[i];
					j++;
				}
				i++;
			}
			societies = societies2;
		}
		
		this.societies = Collections.synchronizedSet(new HashSet<SocietyImage>());
		for(SocietyImage soc : societies)
			this.societies.add(soc);
		
		if(inactive_society != null)
			this.inactiveSociety = inactive_society;
		else {
			inactiveSociety = new SocietyImage("InactiveSociety", "InactiveSociety", false, new AgentImage[0]);
			inactiveSociety.id = "InactiveSociety";
		}
	}
	

	/**
	 * Constructor.
	 * 
	 * @param graph
	 *            The graph to be pattroled.
	 * @param societies
	 *            The societies of patrollers of the simulation.
	 * @param inactive_society
	 * 				The open society containing the agents that are inactive at the beginning of the simulation
	 */ /*
	public Environment(Graph graph, Society[] societies, OpenSociety inactive_society) {
		this.graph = graph;

		this.societies = Collections.synchronizedSet(new HashSet<Society>());
		for (int i = 0; i < societies.length; i++)
			this.societies.add(societies[i]);
		
		inactiveSociety = inactive_society;
		inactiveSociety.setObjectId("InactiveSociety");
	} */

	/**
	 * Returns the graph of the environment.
	 * 
	 * @return The graph.
	 */
	public Graph getGraph() {
		return this.graph;
	}

	/**
	 * Returns all the societies of the environment, the InactiveSociety included.
	 * 
	 * @return The societies.
	 */
	public SocietyImage[] getSocieties() {
		SocietyImage[] mysoc1 = this.societies.toArray(new SocietyImage[0]);
		SocietyImage[] mysoc2 = new SocietyImage[mysoc1.length + 1];
		
		for(int i = 0; i < mysoc1.length; i++)
			mysoc2[i] = mysoc1[i];
		
		mysoc2[mysoc2.length - 1] = this.inactiveSociety;
		
		return mysoc2;
		
	}
	
	/**
	 * Returns the active societies of the environment
	 * 
	 * @return The active societies.
	 */
	public SocietyImage[] getActiveSocieties() {
		return this.societies.toArray(new SocietyImage[0]);
	}
	
	
	/**
	 * Returns the InactiveSociety of the environment
	 * 
	 * @return The inactive society.
	 */
	public SocietyImage getInactiveSociety() {
		return this.inactiveSociety;
	}

	public String getObjectId() {
		// an environment doesn't need an id
		return null;
	}

	public void setObjectId(String object_id) {
		// an environment doesn't need an id
		// so, do nothing
	}

}