package tools.configuration_files.client_types;

public class CCClientType extends ClientType {
	
	public CCClientType(int firstAgentTypeQuantity) {
		this.agentTypes = new AgentType[2];
		this.name = "cc";
		
		//Agentes normais
		int[] allowedPerceptions1 = {0, 3, 4};
		int[] allowedActions1 = {1, 2, 3};
		this.agentTypes[0] = new AgentType(firstAgentTypeQuantity,
				allowedPerceptions1, allowedActions1, "a");
		
		//Agente coordenador
		int[] allowedPerceptions2 = {0, 3};
		int[] allowedActions2 = {3};
		this.agentTypes[1] = new AgentType(1, allowedPerceptions2, allowedActions2,
				"coordinator");
	}
}
