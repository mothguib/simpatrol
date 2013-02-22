package agent_library.basic_agents;


public class AgentStoppedException extends Exception {
	AgentStoppedException(String identifier) {
		super("Agent " + identifier + " already stopped execution!");
	}	
}
