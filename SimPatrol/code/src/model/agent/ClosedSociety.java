package model.agent;

import java.util.Collection;

/**
 * @model.uin <code>design:node:::61xy5f17ujey8-szti53</code>
 */
public class ClosedSociety extends Society {

	Agent[] agents = {new PerpetualAgent()};		
	
	/**
	 * @model.uin <code>design:node:::1c8f6f17ujey8-7t467k</code>
	 */
	public Collection perpetualAgent;

	public String toXML(int identation) {
		// TODO Auto-generated method stub
		return null;
	}
	
	public void start() {
		agents[0].start();
	}
	
	public void stop() {
		agents[0].stop();
	}

	@Override
	public Agent[] getAgents() {
		// TODO Auto-generated method stub
		return agents;
	}

	public String getObjectId() {
		// TODO Auto-generated method stub
		return null;
	}

	public void setObjectId(String object_id) {
		// TODO Auto-generated method stub
		
	}
}
