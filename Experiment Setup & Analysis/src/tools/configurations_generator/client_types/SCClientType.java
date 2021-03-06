package tools.configurations_generator.client_types;

import java.util.ArrayList;
import java.util.List;

import tools.configurations_generator.AgentType;
import tools.configurations_generator.ClientType;


public class SCClientType extends ClientType {
	
	public SCClientType(int firstAgentTypeQuantity) {
		this.agentTypes = new AgentType[2];
		this.name = "sc";
		
		//Agentes normais
		int[] allowedPerceptions1 = {1, /*0,*/ 3, 4, -1};  //don't change the order!
		int[] allowedActions1 = {1, 2};
		
		List<List<Integer>> allowedPerceptionLimitations = new ArrayList<List<Integer>>();
		allowedPerceptionLimitations.add(new ArrayList<Integer>());
		allowedPerceptionLimitations.get(0).add(0); //in the first perception type, set limitation 0-0
		
		List<List<Integer>> allowedActionLimitations = new ArrayList<List<Integer>>();
		allowedActionLimitations.add(new ArrayList<Integer>());
		allowedActionLimitations.get(0).add(1); //in the first action type, set limitation 0-1
		
		this.agentTypes[0] = new AgentType(firstAgentTypeQuantity,
				allowedPerceptions1, allowedActions1, "a");
		this.agentTypes[0].setAllowedPerceptionLimitations(allowedPerceptionLimitations);
		this.agentTypes[0].setAllowedActionLimitations(allowedActionLimitations);
		
		
		//Agente coordenador
		int[] allowedPerceptions2 = {0, 1};
		int[] allowedActions2 = {3};
		
		this.agentTypes[1] = new AgentType(1, allowedPerceptions2, allowedActions2,
				"coordinator");
	}
}
