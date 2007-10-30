/* ReactiveWithFlagsAgent.java */

/* The package of this class. */
package reactive_with_flags;

/* Imported classes and/or interfaces. */
import java.io.IOException;
import java.util.LinkedList;
import util.net.ClientConnection;

/** Implements the reactive with flags agents, as it is described
 *  in the work of [MACHADO, 2002]. */
public abstract class ReactiveWithFlagsAgent extends Thread {
	/* Attributes. */
	/** Registers if the agent shall stop working. */
	private boolean stop_working;
	
	/** The connection of the agent. */
	protected ClientConnection connection;
	
	/* Methods. */
	/** Constructor. */
	public ReactiveWithFlagsAgent() {
		this.stop_working = false;		
	}
	
	/** Lets the agent perceive its current position.
	 * 
	 *  @param perceptions The current perceptions of the agent.
	 *  @return The current position of the agent, as a pair "current vertex id - elapsed length on the current edge". */
	private StringAndDouble perceiveCurrentPosition(String[] perceptions) {
		// tries to obtain the most recent self perception of the agent
		int perceptions_count = perceptions.length;
		
		for(int i = perceptions_count - 1; i > -1; i--) {
			String current_perception = perceptions[i];
			
			if(current_perception.indexOf("<perception type=\"4\"") > -1) {
				// obtains the id of the current vertex 
				int vertex_id_index = current_perception.indexOf("vertex_id=\"");				
				current_perception = current_perception.substring(vertex_id_index + 11);
				String vertex_id = current_perception.substring(0, current_perception.indexOf("\""));
				
				// obtains the elapsed length on the current edge
				int elapsed_length_index = current_perception.indexOf("elapsed_length=\"");
				current_perception = current_perception.substring(elapsed_length_index + 16);
				double elapsed_length = Double.parseDouble(current_perception.substring(0, current_perception.indexOf("\"")));
				
				// returs the answer of the method
				return new StringAndDouble(vertex_id, elapsed_length);
			}
		}
		
		// default answer
		return null;
	}
	
	/** Lets the agent perceive the idlenesses of each vertex in the neighbourhood.
	 * 
	 *  @param perceptions The current perceptions of the agent.
	 *  @return The pairs vertex - idleness of the neighbourhood. */
	private StringAndDouble[] perceiveNeighbourhoodIdlenesses(String[] perceptions) {
		// tries to obtain the most recent perception of the neighbourhood
		int perceptions_count = perceptions.length;
				
		for(int i = perceptions_count - 1; i > -1; i--) {
			String current_perception = perceptions[i];
			
			if(current_perception.indexOf("<perception type=\"0\"") > -1) {
				// holds the answer for the method
				LinkedList<StringAndDouble> vertexes_and_idlenesses = new LinkedList<StringAndDouble>(); 
				
				// holds the index of the next vertex
				int next_vertex_index = current_perception.indexOf("<vertex ");
				
				// while there are vertexes to have their idlenesses read
				while(next_vertex_index > -1) {										
					// obtains the id of the current vertex
					int current_vertex_id_index = current_perception.indexOf("id=\"");
					current_perception = current_perception.substring(current_vertex_id_index + 4);
					String vertex_id = current_perception.substring(0, current_perception.indexOf("\""));
					
					// obtains the idleness of the current vertex
					int current_vertex_idleness_index = current_perception.indexOf("idleness=\"");
					current_perception = current_perception.substring(current_vertex_idleness_index + 10);
					double idleness = Double.parseDouble(current_perception.substring(0, current_perception.indexOf("\"")));
					
					// adds the pair vertex-idleness to the list of obtained pairs
					vertexes_and_idlenesses.add(new StringAndDouble(vertex_id, idleness));
					
					// obtains the index of the next vertex
					next_vertex_index = current_perception.indexOf("<vertex ");
				}
				
				// mounts and returns the answer of the method
				StringAndDouble[] answer = new StringAndDouble[vertexes_and_idlenesses.size()];
				for(int j = 0; j < answer.length; j++)
					answer[j] = vertexes_and_idlenesses.get(j);
				
				return answer;
			}
		}
		
		// default answer
		return new StringAndDouble[0];
	}
	
	/** Lets the agent think and decide its next vertex, based upon
	 *  the given current position, and the given idlenesses of the 
	 *  neighbourhood.
	 *  
	 *  The id of the next chosen vertex is returned.
	 *  
	 *  @param current_position The current position of the agent.
	 *  @param neighbourhood The nighbourhood of where the agent is.
	 *  @return The next vertex the agent must take, if needed. */
	private String decideNextVertex(StringAndDouble current_position, StringAndDouble[] neighbourhood) {
		// obtains the current vertex where the agent is or comes from
		String current_vertex = current_position.STRING;
		
		// holds the vertex with the highest idleness
		// in the neighbourhood
		String next_vertex = null;
		
		// holds the highest idleness found in the neighbourhood
		double highest_idleness = -1;
		
		// for each vertex in the neighbourhood, finds the one
		// with the highest idleness, ignoring the current vertex
		// of the agent
		for(int i = 0; i < neighbourhood.length; i++) {
			if(!neighbourhood[i].STRING.equals(current_vertex))
				if(highest_idleness < neighbourhood[i].DOUBLE) {					
					next_vertex = neighbourhood[i].STRING;
					highest_idleness = neighbourhood[i].DOUBLE;
				}
		}
		
		// returns the answer of the method
		return next_vertex;
	}
	
	/** Lets the agent visit the current vertex.
	 *  
	 *  @throws IOException */
	private void visitCurrentPosition() throws IOException {
		String message = "<action type=\"2\"/>";
		this.connection.send(message);
	}
	
	/** Lets the agent go to the give vertex.
	 * 
	 *  @param The next vertex to where the agent is supposed to go. 
	 *  @throws IOException */
	private void goTo(String next_vertex_id) throws IOException {
		String message = "<action type=\"1\" vertex_id=\"" + next_vertex_id + "\"/>";
		this.connection.send(message);
	}
	
	/** Indicates that the agent must stop working. */
	public void stopWorking() {
		this.stop_working = true;
	}
	
	public void run() {
		// starts its connection
		this.connection.start();
		
		// while the agent is supposed to work
		while(!this.stop_working) {
			// 1st. lets the agent perceive			
			// the current position of the agent
			StringAndDouble current_position = null;
			
			// the neighbourhood of where the agent is
			StringAndDouble[] neighbourhood = new StringAndDouble[0];
			
			// while the current position or neighbourhood are not valid
			while(current_position == null || neighbourhood.length == 0) {
				// obtains the perceptions from the server
				String[] perceptions = this.connection.getBufferAndFlush();
				
				// tries to obtain the current position
				StringAndDouble current_current_position = this.perceiveCurrentPosition(perceptions);
				if(current_current_position != null)
					current_position = current_current_position;
				
				// tries to obtain the neighbourhood
				StringAndDouble[] current_neighbourhood = this.perceiveNeighbourhoodIdlenesses(perceptions);
				if(current_neighbourhood.length > 0)
					neighbourhood = current_neighbourhood;
			}
			
			// 2nd. lets the agent think
			// if the current position is on a vertex
			// (i.e. the elapsed length is 0), choose next vertex
			String next_vertex_id = null;
			if(current_position != null && current_position.DOUBLE == 0)
				next_vertex_id = this.decideNextVertex(current_position, neighbourhood);
			// else: the agent is walking on an edge,
			// so it doesn't have to decide its next position
			
			// 3rd. lets the agent act,
			// if the next vertex was decided
			if(next_vertex_id != null) {
				// 3.1. lets the agent visit the current vertex
				try { this.visitCurrentPosition(); }
				catch (IOException e) { e.printStackTrace(); }
				
				// 3.2. lets the agent goe to the next vertex
				try { this.goTo(next_vertex_id); }
				catch (IOException e) { e.printStackTrace(); }
			}
			// else: the agent is already walking, so do nothing
		}
		
		// stops the connection of the agent
		try { this.connection.stopWorking(); }
		catch (IOException e) { e.printStackTrace(); }
	}
}

/** Internal class that holds together a string and a double value. */
final class StringAndDouble {
	/* Attributes */
	/** The string value. */
	public final String STRING;
	
	/** The double value. */
	public final double DOUBLE;
	
	/* Methods. */
	/** Constructor.
	 * 
	 *  @param string The string value of the pair.
	 *  @param double_value The double value of the pair. */
	public StringAndDouble(String string, double double_value) {
		this.STRING = string;
		this.DOUBLE = double_value;
	}
}