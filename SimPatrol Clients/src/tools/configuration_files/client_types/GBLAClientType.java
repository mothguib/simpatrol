package tools.configuration_files.client_types;

import java.util.ArrayList;
import java.util.List;

public class GBLAClientType extends ClientType {
	
	public GBLAClientType(int firstAgentTypeQuantity) {
		this.agentTypes = new AgentType[1];
		this.name = "gbla";
		
		//Agentes normais
		int[] allowedPerceptions1 = {0, 3, 4};
		int[] allowedActions1 = {1, 2, 3};
		
		List<List<Integer>> allowedPerceptionLimitations = new ArrayList<List<Integer>>();
		allowedPerceptionLimitations.add(new ArrayList<Integer>());
		allowedPerceptionLimitations.get(0).add(1);
		
		this.agentTypes[0] = new AgentType(firstAgentTypeQuantity,
				allowedPerceptions1, allowedActions1, "a");
		this.agentTypes[0].setAllowedPerceptionLimitations(allowedPerceptionLimitations);
	}
}
