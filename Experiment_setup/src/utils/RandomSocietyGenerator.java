package utils;

import java.io.IOException;
import java.util.LinkedList;

import util.agents.AgentImage;
import util.agents.SocietyImage;
import util.file.FileWriter;

public class RandomSocietyGenerator {

	
	public RandomSocietyGenerator(String soc_name, int startnum, int min_agents, int max_agents, 
										int sim_length, int interval, double rand_factor, double[] proba,
										String filename){
		int[] society_sizes = new int[sim_length / interval + (sim_length % interval == 0 ? 0 : 1)];
		
		society_sizes[0] = startnum;
		for(int i = 1; i < society_sizes.length; i++){
			// if an event happens
			if(Math.random() > (double)(1 - rand_factor)){
				// if there are a lot of agents, the probability that the event is a QUIT is higher
				int nb_agents_changing = 0;
				double rand = Math.random();
				if(rand < proba[0])
					nb_agents_changing = 1;
				else if(rand < proba[0] + proba[1])
					nb_agents_changing = 2;
				else if(rand < proba[0] + proba[1] + proba[2])
					nb_agents_changing = 3;
				else if(rand < proba[0] + proba[1] + proba[2] + proba[3])
					nb_agents_changing = 4;
				else if(rand < proba[0] + proba[1] + proba[2] + proba[3] + proba[4])
					nb_agents_changing = 5;
				
				double rand2 = Math.random();
				if(rand2 > (double)(max_agents - society_sizes[i-1])/(double)(max_agents - min_agents)){
					// QUIT event
					if(society_sizes[i-1] - nb_agents_changing < min_agents)
						nb_agents_changing = society_sizes[i-1] - min_agents;
					
					society_sizes[i] = society_sizes[i-1] - nb_agents_changing;
				}
				else {
					// ENTER event
					if(society_sizes[i-1] + nb_agents_changing > max_agents)
						nb_agents_changing = max_agents - society_sizes[i-1];
					
					society_sizes[i] = society_sizes[i-1] + nb_agents_changing;
				}
			}
			else 
				society_sizes[i] = society_sizes[i-1];
		}
		
		System.out.print("[");
		for(int i = 0; i < society_sizes.length - 1; i++)
			System.out.print(society_sizes[i] + ",");
		System.out.print(society_sizes[society_sizes.length - 1] + "]\n");
		
		
		int entering_agents = startnum;
		for(int i = 1; i < society_sizes.length; i++)
			if(society_sizes[i] > society_sizes[i-1])
				entering_agents += society_sizes[i] - society_sizes[i-1];
				
		AgentImage[] myagents = new AgentImage[entering_agents];
		LinkedList<String> agents_in_sim = new LinkedList<String>();
		int current_agent = 0;
		
		for(int i = 0; i < startnum; i++){
			String label = "a" + (current_agent + 1);
			String id = label + "@";
			int[] perceptions = new int[]{-1, 0, 1, 3, 4};
			int[] actions = new int[]{1, 2, 3, 8};
			myagents[current_agent] = new AgentImage(id, label, 1, "v1", 1.0, 1.0, perceptions, actions);
			myagents[current_agent].Society_to_join = soc_name;
			
			agents_in_sim.add(id);
			current_agent++;
		}
		
		for(int i = 1; i < society_sizes.length; i++){
			if(society_sizes[i] > society_sizes[i-1]){
				for(int j = society_sizes[i-1]; j < society_sizes[i]; j++){
					String label = "a" + (current_agent + 1);
					String id = label + "@";
					int[] perceptions = new int[]{-1, 0, 1, 3, 4};
					int[] actions = new int[]{1, 2, 3, 8};
					myagents[current_agent] = new AgentImage(id, label, 1, "v1", 1.0, 1.0, perceptions, actions);
					myagents[current_agent].Society_to_join = soc_name;
					myagents[current_agent].enter_time = i*interval;
					         
					agents_in_sim.add(id);
					current_agent++;
				}
			}
			else if(society_sizes[i] < society_sizes[i-1]){
				for(int j = society_sizes[i]; j < society_sizes[i-1]; j++){
					int rand_agent = (int) (Math.random() * agents_in_sim.size());
					String agent_out = agents_in_sim.remove(rand_agent);
					for(int k = 0; k < current_agent; k++){
						if(myagents[k].id.equals(agent_out)){
							myagents[k].quit_time = i*interval;
						}
					}
				}
			}
		}
		
		
		AgentImage[] myagents_start = new AgentImage[startnum];
		AgentImage[] myagents_inactive = new AgentImage[entering_agents - startnum];
		for(int i = 0; i < startnum; i++)
			myagents_start[i] = myagents[i];
		for(int i = 0; i < entering_agents - startnum; i++)
			myagents_inactive[i] = myagents[i + startnum];
				
		SocietyImage mysoc = new SocietyImage(soc_name, soc_name, false, myagents_start);
		SocietyImage mysoc_inactive = new SocietyImage("InactiveSociety", "InactiveSociety", false, myagents_inactive);
		
		FileWriter ofile;
		try {
			ofile = new FileWriter(filename);
			ofile.print("[");
			for(int i = 0; i < society_sizes.length - 1; i++)
				ofile.print(society_sizes[i] + ",");
			ofile.print(society_sizes[society_sizes.length - 1] + "]\n");
			
			ofile.print(mysoc.toString());
			ofile.print(mysoc_inactive.toString());
			ofile.close();
			
			System.out.println("File created. You should now add a coordinator if needed and randomize the starting nodes");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
	}
	
	
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {

		try {
			/*String soc_id = args[0];
			int startnum = Integer.parseInt(args[1]);
			int minnum  = Integer.parseInt(args[2]);
			int maxnum  = Integer.parseInt(args[3]);
			int nbturn  = Integer.parseInt(args[4]);
			int interval  = Integer.parseInt(args[5]);
			double event_probability = Double.parseDouble(args[6]);
			double proba1 = Double.parseDouble(args[7]);
			double proba2 = Double.parseDouble(args[8]);
			double proba3 = Double.parseDouble(args[9]);
			double proba4 = Double.parseDouble(args[10]);
			double proba5 = Double.parseDouble(args[11]);
			String path = args[12];
			
			double[] probas = {proba1, proba2, proba3, proba4, proba5};

			
			new RandomSocietyGenerator(soc_id, startnum, minnum, maxnum, nbturn, interval, 
					event_probability, probas, path);
			*/		
			new RandomSocietyGenerator("small", 100, 60, 140, 100000, 100, 
							0.2, new double[]{0.4, 0.3, 0.15, 0.1, 0.05}, "/home/pouletc/experimentation/Simulations/small.txt");
					
		} catch (Exception e) {
			System.out
					.println("Usage \"java Society Generator\"\n"
							+ "<society id> <nb agents at start> <minimimum nb agents> <maximum nb agents> \n" 
							+ "<Simulation length in turn> <interval between events> \n"
							+ "<probability of event (0..1)> <probability 1 agent in event> <probability 2 agents in event>\n" 
							+ "<probability 3 agents in event> <probability 4 agents in event> <probability 5 agents in event>\n"
							+"<path + filename to file to create>\n \n"
							+ "Creates the society file (active + inactive) corresponding to random events (Enter / Quit) concerning 1 to 5 agents \n"
							+ "The events can happen every interval during the whole duration of the simulation. \n"
							+ "Probabilities for nb of agents should add up to 1.\n");
		}
	}

}
