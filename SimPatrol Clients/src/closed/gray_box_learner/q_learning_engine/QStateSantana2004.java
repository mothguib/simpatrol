package closed.gray_box_learner.q_learning_engine;

public class QStateSantana2004 extends QState {

	
	// id of the current node of the agent
	int node_id;
	
	
	// index of the node the agent comes from, in the vector of the neighbors of the current node ordered alphabetically
	// IMPORTANT : assumes that there is only a single edge connecting 2 nodes on the graph
	int origin_index;
	
	// index of the node with the biggest idleness, in the vector of the neighbors of the current node ordered alphabetically
	int biggest_index;
	
	
	// vector of the neighbors : 1 if another agent planned to visit it, 0 if not
	// the associated value is 0 if the vector is filled with 0, and a randomly chosen
	// position of a 1 in the vector
	int[] visited_neighbors;
	
	
	public QStateSantana2004(int nid, int or, int big, int[] vn){
		node_id = nid;
		origin_index = or;
		biggest_index = big;
		visited_neighbors = vn;
	}
	
	
	@Override
	public int getStateId() { 
		int state_contribution = node_id;
		for (int j = 1; j < 4; j++)
			state_contribution *= QState.state_items_cardinality.get(j).intValue();
		
		int origin_contribution = origin_index;
		for (int j = 2; j < 4; j++)
			origin_contribution *= QState.state_items_cardinality.get(j).intValue();
		
		int biggest_contribution = biggest_index;
		for (int j = 3; j < 4; j++)
			biggest_contribution *= QState.state_items_cardinality.get(j).intValue();
		
		int visited_contribution = 0;
		int vis = 0;
		for(int j = 0; j< visited_neighbors.length; j++)
			vis += visited_neighbors[j];
		if(vis > 0){
			int[] non_zero = new int[vis];
			int ind = 0;
			for(int j = 0; j< visited_neighbors.length; j++)
				if(visited_neighbors[j] != 0)
					non_zero[ind++] = j+1;
			
			visited_contribution = non_zero[(int)(Math.random() * non_zero.length)];
		}
			
		//for(int i = 0; i < visited_neighbors.length; i++)
		//	visited_contribution += visited_neighbors[i] * Math.pow(2, i);
		

		int answer = state_contribution + 
							origin_contribution + 
							biggest_contribution + 
							visited_contribution;
		
		return answer;
	}

}
