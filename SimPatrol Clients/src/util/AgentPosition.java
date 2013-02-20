package util;

public class AgentPosition {

	public final String agent_name;
	public final String node;
	public final String edge;
	public final double elapsed_length;
	
	
	public AgentPosition(String agent, String node){
		this.agent_name = agent;
		this.node = node;
		this.edge = null;
		this.elapsed_length = 0;
	}
	
	public AgentPosition(String agent, String node, String edge, double length){
		this.agent_name = agent;
		this.node = node;
		this.edge = edge;
		this.elapsed_length = length;
	}
}
