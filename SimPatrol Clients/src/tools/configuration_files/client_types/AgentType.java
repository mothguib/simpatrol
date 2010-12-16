package tools.configuration_files.client_types;

import java.util.ArrayList;
import java.util.List;

public class AgentType {
	/** The number of agents of this type */
	private int quantity;
	
	/** The perception types allowed to each agent of this type. */
	private int[] allowedPerceptions;
	
	/** Optional limitations for each of the agent's perceptions */
	private List<List<Integer>> allowedPerceptionLimitations;
	
	/** Optional limitations for each of the agent's actions */
	private List<List<Integer>> allowedActionLimitations;
	
	/** The action types allowed to each agent of this type. */
	private int[] allowedActions;
	
	/** Name of the agent */
	private String name;
	
	public AgentType(int quantity, int[] allowedPerceptions,
			int[] allowedActions, String name) {
		this.quantity = quantity;
		this.allowedPerceptions = allowedPerceptions;
		this.allowedPerceptionLimitations = new ArrayList<List<Integer>>();
		this.allowedActionLimitations = new ArrayList<List<Integer>>();
		this.allowedActions = allowedActions;
		this.name = name;
	}

	public int getQuantity() {
		return quantity;
	}

	public int[] getAllowedPerceptions() {
		return allowedPerceptions;
	}

	public int[] getAllowedActions() {
		return allowedActions;
	}
	
	public String getName() {
		return this.name;
	}

	public List<List<Integer>> getAllowedPerceptionLimitations() {
		return allowedPerceptionLimitations;
	}

	public void setAllowedPerceptionLimitations(List<List<Integer>> allowedPerceptionLimitations) {
		this.allowedPerceptionLimitations = allowedPerceptionLimitations;
	}
	
	public List<List<Integer>> getAllowedActionLimitations() {
		return allowedActionLimitations;
	}

	public void setAllowedActionLimitations(List<List<Integer>> allowedActionLimitations) {
		this.allowedActionLimitations = allowedActionLimitations;
	}
}
