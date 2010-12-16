package tools.configuration_files.client_types;

public class CRClientType extends ClientType {
	
	public CRClientType(int firstAgentTypeQuantity) {
		this.agentTypes = new AgentType[1];
		this.name = "cr";
		
		//Agentes normais
		int[] allowedPerceptions1 = {0, 3, 4};
		int[] allowedActions1 = {1, 2, 3};
		this.agentTypes[0] = new AgentType(firstAgentTypeQuantity,
				allowedPerceptions1, allowedActions1, "a");
	}
}
