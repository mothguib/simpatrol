package tools.configurations_generator.client_types;

import tools.configurations_generator.AgentType;
import tools.configurations_generator.ClientType;

public class CRClientType extends ClientType {
	
	public CRClientType(int firstAgentTypeQuantity) {
		this.agentTypes = new AgentType[1];
		this.name = "cr";
		
		//Agentes normais
		int[] allowedPerceptions1 = {0, 3, 4};  //indeed, graph perception should be limited to depth 1
		int[] allowedActions1 = {1, 2, 3};
		this.agentTypes[0] = new AgentType(firstAgentTypeQuantity,
				allowedPerceptions1, allowedActions1, "a");
	}
}
