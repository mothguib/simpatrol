package util.agents;

public class SocietyImage {
	
	public String id;
	public String label;
	public boolean is_closed;
	
	public AgentImage[] agents;
	
	public SocietyImage(String id, String label, boolean is_closed, AgentImage[] agents){
		this.id = id;
		this.label = label;
		this.is_closed = is_closed;
		
		this.agents = agents;
	}
	
	public String toString(){
		String soc = "<society id=\"" + this.id + "\" label=\"" + this.label + "\" is_closed=\"" + this.is_closed + "\">\n";
		for(int i = 0; i < this.agents.length; i++)
			soc += this.agents[i].toString();
		
		soc += "</society>\n";
		
		return soc;
		
	}
}
