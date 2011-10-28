package open.ConscientiousReactive;

import java.io.IOException;
import java.util.LinkedList;

import javax.xml.parsers.ParserConfigurationException;

import org.xml.sax.SAXException;

import util.StringAndDouble;
import util.graph.Graph;
import util.graph.Node;
import common.OpenAgent;

public class OpenCRAgent extends OpenAgent {

	
	/** Memorizes the last time this agent visited the vertexes. */
	private LinkedList<StringAndDouble> nodes_visit_time;

	/* Methods. */
	/** Constructor. */
	public OpenCRAgent(String id) {
		super(id);
		this.nodes_visit_time = new LinkedList<StringAndDouble>();

	}
	
	public OpenCRAgent(String id, double entering_time, double quitting_time, String society_id) {
		super(id, entering_time, quitting_time, society_id);

		this.nodes_visit_time = new LinkedList<StringAndDouble>();
	}
	
	private String[] CalculateNeighborhood(){
		Node myposition = this.graph.getNode(this.current_position.STRING);
		Node[] neighborhood = myposition.getNeighbourhood();
		
		String[] answer = new String[neighborhood.length];
		for(int i = 0; i < answer.length; i++)
			answer[i] = neighborhood[i].getObjectId();
		
		return answer;
	}
	
	private String decideNextVertex(String[] neighbourhood) {

		LinkedList<String> next_nodes = new LinkedList<String>();
		double visiting_time = Integer.MAX_VALUE;

		for (int i = 0; i < neighbourhood.length; i++) {
			String node_id = neighbourhood[i];

			boolean is_memorized = false;
			double current_visiting_time = -1;
	
			for (int j = 0; j < this.nodes_visit_time.size(); j++) {
				StringAndDouble memorized_item = this.nodes_visit_time.get(j);
				if (memorized_item.STRING.equals(node_id)) {
					current_visiting_time = memorized_item.double_value;
					is_memorized = true;
					break;
				}
			}
			if (!is_memorized)
				this.nodes_visit_time.add(new StringAndDouble(node_id, current_visiting_time));

			if (current_visiting_time < visiting_time) {
				next_nodes.clear();
				next_nodes.add(node_id);
				visiting_time = current_visiting_time;
			}
			else if(current_visiting_time == visiting_time)
				next_nodes.add(node_id);
		}
		
		int rand = (int) (Math.random() * next_nodes.size());
		String next_node = next_nodes.get(rand);
		
		// returns the answer of the method
		return next_node;
	}
	
	@Override
	protected void inactive_run() {
		boolean time_actualized = false;
		String[] perceptions;
    	
		/**
		 * perceive graph and position
		 */
		perceptions = this.connection.getBufferAndFlush();
		if(perceptions.length == 0)
			perceptions = new String[] {""};
		
		while (!time_actualized || perceptions.length != 0) {
			
			for(int i = perceptions.length - 1; i > -1; i--){
				time_actualized |= perceiveTime(perceptions[i]);
				if(time_actualized)
					break;
			}
			
			perceptions = this.connection.getBufferAndFlush();
		}

	}

	@Override
	protected void activating_run() {
		this.Activate(Society_id);
		this.inactive = false;

	}

	@Override
	protected void active_run() {
		boolean acted = false;
		boolean received_position = false;
		
		while(!acted){
			String[] perceptions = this.connection.getBufferAndFlush();

			// tries to perceive the current position of the agent
			for (int i = perceptions.length - 1; i >= 0; i--)
				if(perceiveTime(perceptions[i]))
					break;
				
			for (int i = perceptions.length - 1; i >= 0; i--){
				// tries to obtain the current position
				StringAndDouble sent_position = null;
				sent_position= this.perceiveCurrentPosition(perceptions[i]);
				
				if(sent_position != null && (this.current_position == null || !sent_position.STRING.equals(this.current_position.STRING))){
					this.current_position = sent_position;
					received_position = true;
					break;
				}
			}
			
			for (int i = perceptions.length - 1; i >= 0; i--){
				// tries to perceive the current graph
				Graph next_graph = null;
	
				try {
					next_graph = this.perceiveGraph(perceptions[i]);
				} catch (ParserConfigurationException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (SAXException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				// if obtained a graph, quits the loop
				if (next_graph != null){
					this.graph = next_graph;
					break;
				}
			}
			
			
			if(received_position && this.graph != null && this.current_position.double_value == 0){
				String[] neighbors = this.CalculateNeighborhood();
				String next_node = this.decideNextVertex(neighbors);
				try {
					this.visitCurrentNode(this.current_position.STRING);
					boolean found = false;
					for(int i = 0; i < this.nodes_visit_time.size(); i++)
						if(this.nodes_visit_time.get(i).STRING.equals(this.current_position.STRING)){
							this.nodes_visit_time.get(i).double_value = this.time;
							found = true;
						}
					
					if(!found)
						this.nodes_visit_time.add(new StringAndDouble(this.current_position.STRING, this.time));
					
					this.GoTo(next_node);
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				acted = true;
			}
		}
	}

	@Override
	protected void deactivating_run() {
		this.Deactivate();
		this.inactive = true;

	}

}
