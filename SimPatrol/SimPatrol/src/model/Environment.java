/* Environment.java */

/* The package of this class. */
package model;

/* Imported classes and/or interfaces. */
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import model.agent.OpenSociety;
import model.agent.SeasonalAgent;
import model.agent.Society;
import model.graph.Graph;
import view.XMLable;

/**
 * Implements the environment (graph + societies) where the patrolling task is
 * executed.
 */
public final class Environment implements XMLable {
	/* Attributes. */
	/** The graph of the simulation. */
	private Graph graph;

	/** The set of societies of agents involved with the simulation. */
	private Set<Society> societies;
	
	/** The society where inactive agents are put */
	private OpenSociety inactiveSociety;

	/* Methods. */
	/**
	 * Constructor.
	 * 
	 * @param graph
	 *            The graph to be pattroled.
	 * @param societies
	 *            The societies of patrollers of the simulation.
	 */
	public Environment(Graph graph, Society[] societies) {
		this.graph = graph;
		
		OpenSociety inactive_society = null;
		for(Society soc: societies){
			if(soc instanceof OpenSociety && soc.getObjectId().equals("InactiveSociety"))
				inactive_society = (OpenSociety)soc;
		}
		
		// if so, we put it apart
		if(inactive_society != null){
			Society[] societies2 = new Society[societies.length - 1];
			int i = 0;
			int j = 0;
			while( i < societies.length){
				if(!(societies[i].getObjectId().equals("InactiveSociety"))){
					societies2[j] = societies[i];
					j++;
				}
				i++;
			}
			societies = societies2;
		}
		
		this.societies = Collections.synchronizedSet(new HashSet<Society>());
		for(Society soc : societies)
			this.societies.add(soc);
		
		if(inactive_society != null)
			this.inactiveSociety = inactive_society;
		else {
			inactiveSociety = new OpenSociety("InactiveSociety", new SeasonalAgent[0]);
			inactiveSociety.setObjectId("InactiveSociety");
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
	public Society[] getSocieties() {
		Society[] mysoc1 = this.societies.toArray(new Society[0]);
		Society[] mysoc2 = new Society[mysoc1.length + 1];
		
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
	public Society[] getActiveSocieties() {
		return this.societies.toArray(new Society[0]);
	}
	
	
	/**
	 * Returns the InactiveSociety of the environment
	 * 
	 * @return The inactive society.
	 */
	public OpenSociety getInactiveSociety() {
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

	public String fullToXML(int identation) {
		// holds the answer being constructed
		StringBuffer buffer = new StringBuffer();

		// applies the identation and opens the "environment" tag
		for (int i = 0; i < identation; i++)
			buffer.append("\t");
		buffer.append("<environment>\n");

		// fills the buffer with the graph
		buffer.append(this.graph.fullToXML(identation + 1));

		// fills the buffer with the societies
		for (Society society : this.societies)
			buffer.append(society.fullToXML(identation + 1));
		
		// fills the buffer with the unactive society
		buffer.append(this.inactiveSociety.fullToXML(identation + 1));

		// applies the identation and closes the "environment" tag
		for (int i = 0; i < identation; i++)
			buffer.append("\t");
		buffer.append("</environment>\n");

		// returns the buffer content
		return buffer.toString();
	}

	public String reducedToXML(int identation) {
		// an environment doesn't have a lighter version
		return this.fullToXML(identation);
	}
}