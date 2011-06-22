package util.agents;

public class AgentImage {
	
	public String id;
	public String label;
	
	public int state;
	public String node_id;
	public double stamina;
	public double max_stamina;
	public int[] allowed_perceptions;
	public int[] allowed_actions;
	
	public String Society_to_join;
	
	public int enter_time = -1;
	public int quit_time = -1;
	
	
	public AgentImage(String id, String label, int state, String node_id, double stamina, double max_stamina, 
				int[] allowed_perceptions, int[] allowed_actions){
		
		this.id = id;
		this.label = label;
		
		this.state = state;
		this.node_id = node_id;
		this.stamina = stamina;
		this.max_stamina = max_stamina;
		this.allowed_perceptions = allowed_perceptions;
		this.allowed_actions = allowed_actions;
		
	}
	
	public AgentImage(String id, String label, int state, String node_id, double stamina, double max_stamina, 
			int[] allowed_perceptions, int[] allowed_actions, int enter_time, int quit_time, String socToJoin){
	
		this.id = id;
		this.label = label;
		
		this.state = state;
		this.node_id = node_id;
		this.stamina = stamina;
		this.max_stamina = max_stamina;
		this.allowed_perceptions = allowed_perceptions;
		this.allowed_actions = allowed_actions;
		this.Society_to_join = socToJoin;
		
		if(quit_time > enter_time || quit_time == -1){
			this.enter_time = enter_time;
			this.quit_time = quit_time;
		}
		
	}
}
