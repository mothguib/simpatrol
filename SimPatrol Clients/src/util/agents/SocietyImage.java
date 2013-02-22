package util.agents;

public class SocietyImage {
	
	public String id;
	public String label;
	public boolean is_false;
	
	public AgentImage[] agents;
	
	public SocietyImage(String id, String label, boolean is_false, AgentImage[] agents){
		this.id = id;
		this.label = label;
		this.is_false = is_false;
		
		this.agents = agents;
	}
}
