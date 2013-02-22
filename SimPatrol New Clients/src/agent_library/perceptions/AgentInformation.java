package agent_library.perceptions;

import util.agents.AgentImage;

// just an alias class
// TODO: reimplement
public class AgentInformation extends AgentImage {

	public AgentInformation(AgentImage ag) {
		super(ag.id, ag.label, ag.state, ag.node_id, ag.edge_id, ag.elapsed_length, 
				ag.stamina, ag.max_stamina, ag. allowed_perceptions, ag.allowed_actions);
	}

}
