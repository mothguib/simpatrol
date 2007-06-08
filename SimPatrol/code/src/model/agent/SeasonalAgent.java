/* SeasonalAgent.java */

/* The package of this class. */
package model.agent;

/* Imported classes and/or interfaces. */
import util.etpd.EventTimeProbabilityDistribution;
import model.graph.Vertex;
import model.interfaces.Dynamic;

/** Implements the agents that compound the 
 *  open societies of SimPatrol. */
public class SeasonalAgent extends Agent implements Dynamic {
	/* Attributes. */
	/** The event time probability distribution for the death of the agent. */
	private EventTimeProbabilityDistribution death_time_pd;
	
	/** The society of the agent. */
	private OpenSociety society;
	
	/* Methods. */
	/** Constructor.
	 *  @param vertex The vertex that the agent comes from.
	 *  @param death_time_pd The event time probability distribution for the death of the agent. */
	public SeasonalAgent(Vertex vertex, EventTimeProbabilityDistribution death_time_pd) {
		super(vertex);
		this.death_time_pd = death_time_pd;
	}
	
	/** Configures the open society of the agent.
	 *  @param society The society of the agent. */
	public void setSociety(OpenSociety society) {
		this.society = society;
	}
	
	
	public String toXML(int identation) {
		// holds the answer being constructed
		StringBuffer buffer = new StringBuffer(super.toXML(identation));
		
		// atualizes the answer, if necessary
		if(this.death_time_pd != null) {
			// deletes the closing tag
			int last_valid_index = buffer.lastIndexOf("/>");
			buffer.delete(last_valid_index, buffer.length());
			
			// applies the identation
			for(int j = 0; j < identation + 1; j++)
				buffer.append("\t");
			
			// writes the death tpd
			buffer.append(this.death_time_pd.toXML(identation + 1));			
		}
		
		// returns the answer
		return buffer.toString();
	}

	public EventTimeProbabilityDistribution getAppearingETPD() {
		// the agent never appears again, after disappearing
		// (actually, it dies)
		return null;
	}

	public EventTimeProbabilityDistribution getDisappearingETPD() {
		return this.death_time_pd;
	}

	public boolean isAppearing() {
		// the agent is always appearing
		return true;
	}

	public void setIsAppearing(boolean is_appearing) {
		// if the agent is supposed to disappear, it dies
		if(!is_appearing) {
			// stops the thread working
			this.stopWorking();
			
			// removes the agent from its society
			this.society.removeAgent(this);
			
			// nullifies the agent
			try {
				this.finalize();
			}
			catch (Throwable e) {
				e.printStackTrace();
			}
		}
	}		
}
