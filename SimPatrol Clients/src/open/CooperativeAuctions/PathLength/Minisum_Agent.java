package open.CooperativeAuctions.PathLength;

import java.util.LinkedList;
import java.util.Vector;

import open.CooperativeAuctions.Cooperative_Agent;

import util.StringAndDouble;
import util.graph.Node;

import closed.FBA.ComplexBid;
import closed.FBA.SpeechAct;
import closed.FBA.SpeechActPerformative;

public class Minisum_Agent extends Cooperative_Agent {
	
	double utility_without_0 = -1, utility_without_1 = -1, utility_without_both = -1;
	
	Vector<String[]> quitting_node_group;
	
	
	public Minisum_Agent(String id, int number_of_agents,
			LinkedList<String> nodes, double idleness_rate_for_path) {
		super(id, number_of_agents, nodes, idleness_rate_for_path);
	}
	
	public Minisum_Agent(String id, double entering_time, double quitting_time, 
			int number_of_agents, LinkedList<String> nodes,
			double idleness_rate_for_path, String society_id) {
		super(id, entering_time, quitting_time, number_of_agents, nodes, idleness_rate_for_path, society_id);
	}

	@Override
	protected double CalculateUtility(LinkedList<String> nodes) {
		return this.CalculatePathCost(nodes);
	}

	@Override
	protected LinkedList<String> GenerateAuctionLaunchMessage(LinkedList<String> proposed_nodes) {
		LinkedList<String> message = new LinkedList<String>();
		for(int i = 0; i < proposed_nodes.size(); i++)
			message.add(proposed_nodes.get(i));
		
		message.add(Double.toString(this.current_utility));
		
		LinkedList<String> new_set = this.getMyNodes();	
		for(int i = 0; i < new_set.size(); i++)
			if(new_set.get(i).equals(proposed_nodes.get(0))){
				new_set.remove(i);
				break;
			}	
		this.utility_without_0 = this.CalculateUtility(this.path_calculator.GetShortestPath(graph, new_set));
		message.add(Double.toString(this.current_utility - utility_without_0));
		
		if(proposed_nodes.size() == 1)
			return message;
		
		new_set = this.getMyNodes();	
		for(int i = 0; i < new_set.size(); i++)
			if(new_set.get(i).equals(proposed_nodes.get(1))){
				new_set.remove(i);
				break;
			}
		this.utility_without_1 = this.CalculateUtility(this.path_calculator.GetShortestPath(graph, new_set));
		message.add(Double.toString(this.current_utility - utility_without_1));
		
		for(int i = 0; i < new_set.size(); i++)
			if(new_set.get(i).equals(proposed_nodes.get(0))){
				new_set.remove(i);
				break;
			}
		this.utility_without_both = this.CalculateUtility(this.path_calculator.GetShortestPath(graph, new_set));
		message.add(Double.toString(this.current_utility - utility_without_both));
		
		
		return message;
	}

	

	@Override
	protected double getValueToSend(double new_utility) {
		return this.current_utility - new_utility;
	}
	
	protected LinkedList<String> getTransactionOutNodes(ComplexBid bid){
		LinkedList<String> outNodes = new LinkedList<String>();
		
		String[] nodes;
		if(bid != null){
			if(bid.getBidsForFirst() != null){
				nodes =  bid.getBidsForFirst();
				for(int i = 1; i < nodes.length; i+= 3)
					if(outNodes.indexOf(nodes[i]) == -1)
						outNodes.add(nodes[i]);
			}
			if(bid.getBidsForSecond() != null){
				nodes =  bid.getBidsForSecond();
				for(int i = 1; i < nodes.length; i+= 3)
				if(outNodes.indexOf(nodes[i]) == -1)
					outNodes.add(nodes[i]);
			}
			if(bid.getBidsForBoth() != null){
				nodes =  bid.getBidsForBoth();
				for(int i = 1; i < nodes.length; i+= 3)
				if(outNodes.indexOf(nodes[i]) == -1)
					outNodes.add(nodes[i]);
			}
			if(bid.getDoubleBidsForBoth() != null)
				for(String[] nodes2 : bid.getDoubleBidsForBoth())
					for(int i = 1; i < nodes2.length; i+= 2)
						if(outNodes.indexOf(nodes2[i]) == -1)
							outNodes.add(nodes2[i]);
		}
		
		return outNodes;

	}
	
	
	@Override
	protected ComplexBid emitProposition(LinkedList<String> received_offers) {
		String node1 = null, node2 = null;
		double proposer_utility, proposer_utility_diff_1, proposer_utility_diff_2 = 0, proposer_utility_diff_both = 0;
		String[] offers;
		
		if(received_offers.size() < 2)
			return new ComplexBid(null, null, null, null);
		
		node1 = received_offers.get(0);
		if(received_offers.size() == 3){
			proposer_utility = Double.valueOf(received_offers.get(1));
			proposer_utility_diff_1 = Double.valueOf(received_offers.get(2));
		}
		else {
			node2 = received_offers.get(1);
			proposer_utility = Double.valueOf(received_offers.get(2));
			proposer_utility_diff_1 = Double.valueOf(received_offers.get(3));
			proposer_utility_diff_both = Double.valueOf(received_offers.get(4));
		}

		offers = new String[(node2 == null ? 1 : 2)];
		offers[0] = node1;
		if(offers.length == 2)
			offers[1] = node2;
		
		/**
		 * Is there interesting exchanges ?
		 */
		ComplexBid exchanges = this.emitExchangeProposition(offers);
		
		if((exchanges.getBidsForFirst() != null && exchanges.getBidsForFirst().length > 0)
						|| (exchanges.getBidsForSecond() != null && exchanges.getBidsForSecond().length > 0)
						|| (exchanges.getBidsForBoth() != null && exchanges.getBidsForBoth().length > 0)
						|| (exchanges.getDoubleBidsForBoth() != null && exchanges.getDoubleBidsForBoth().size() > 0)){
			/**
    		 * There is an interesting exchange possible
    		 */
			return exchanges;
		}
    	else {
    		/**
    		 * There is no exchange possible. However if my difference of utility is inferior to his, I might help him anyway
    		 */
    		
    		if(this.current_utility >= proposer_utility)
    			return new ComplexBid(null, null, null, null);
    		
    		String[] subset_list = null, subset1_list = null, subset2_list = null;
    		String offer;
    		LinkedList<String> new_set = new LinkedList<String>();
    		// Is it interesting to accept 1 node ?
        	for(int k = 0; k < offers.length; k++){
        		offer = offers[k];
        		new_set.clear();
		
    			for(int j = 0; j < this.myNodes.size(); j++)
    				new_set.add(this.getMyNodes(j));
    			new_set.add(offer);
    			
    			double new_utility = this.CalculateUtility(this.path_calculator.GetShortestPath(graph, new_set));
        		double my_diff = Math.abs(new_utility - this.current_utility);
        		
        		double proposer_diff = (k == 0 ? proposer_utility_diff_1 : proposer_utility_diff_2);
        		
    			if(my_diff < proposer_diff){
    				if(k == 0)
    					subset_list = new String[]{Double.toString(my_diff)};
    				else
    					subset1_list = new String[]{Double.toString(my_diff)};
    			}
        		
        		
        	}
        	
        	// Is it interesting to accept 2 nodes ?
    		if(offers.length == 2){
	        	new_set.clear();
				for(int j = 0; j < this.myNodes.size(); j++)
					new_set.add(this.getMyNodes(j));
				new_set.add(offers[0]);
				new_set.add(offers[1]);
				double new_utility = this.CalculateUtility(this.path_calculator.GetShortestPath(graph, new_set));
				double my_diff = Math.abs(new_utility - this.current_utility);
				
				if(my_diff < proposer_utility_diff_both)
					subset2_list = new String[]{Double.toString(my_diff)};
    		}
    		
    		if(subset_list != null || subset1_list != null || subset2_list != null )
    			return new ComplexBid(subset_list, subset1_list, subset2_list, null);
    		
    		else 	
    			return new ComplexBid(null, null, null, null);
    	}
	}

	@Override
	protected void ProcessCorrectAnswers() {
		LinkedList<String> new_set = new LinkedList<String>();
    	boolean exchange_done = false;
    	
		SpeechAct reject = new SpeechAct(this.myCurrentTransaction, SpeechActPerformative.REJECT, this.agent_id, "", new LinkedList<String>(), 0);
		ComplexBid current_bid;
		String[] prop_for_1, prop_for_2, prop_for_both;
		Vector<String[]> double_prop;
		
		double actual_utility = 0;
		
		String best_offer1 = null, best_offer2 = null, special_offer = null;
		int best_offer1_idle = 0, best_offer2_idle = 0, special_offer_idle = 0;

    	double current_max_diff = Double.NEGATIVE_INFINITY;
    	int current_max_diff_index = -1;
    	int current_max_diff_type = -1;
    	boolean current_max_diff_is_tranfer = false;
    	
    	if(this.ComplexPropositions != null && this.ComplexPropositions.size()>0){
    		for(int i = 0; i < this.ComplexPropositions.size(); i++){
    			// first we find the type of message
        		int type = -1;
    			current_bid = this.ComplexPropositions.get(i);
    			prop_for_1 = current_bid.getBidsForFirst();
    			prop_for_2 = current_bid.getBidsForSecond();
    			prop_for_both = current_bid.getBidsForBoth();
    			double_prop = current_bid.getDoubleBidsForBoth();
    			
    			if((prop_for_1 != null && prop_for_1.length == 1)||
    					(prop_for_2 != null && prop_for_2.length == 1)||
    					(prop_for_both != null && prop_for_both.length == 1)){
    				type = 1;
    			}
    			else {
    				type = 2;
    			}
    				
    			
    			if(type == 1){
    				// the other agent is ready to accept some of the nodes as a transfer
    				// we look for the maximum utility difference
    				
    				// first check if utilities are correct
    				if(this.utility_without_0 == -1){
    					new_set = this.getMyNodes();	
    					for(int j = 0; j < new_set.size(); j++)
    						if(new_set.get(j).equals(this.best_proposition1)){
    							new_set.remove(j);
    							break;
    						}	
    					this.utility_without_0 = this.CalculateUtility(this.path_calculator.GetShortestPath(graph, new_set));
    				}
    				if(this.utility_without_1 == -1){
    					new_set = this.getMyNodes();	
	    				for(int j = 0; j < new_set.size(); j++)
	    					if(new_set.get(j).equals(this.best_proposition2)){
	    						new_set.remove(j);
	    						break;
	    					}
    				this.utility_without_1 = this.CalculateUtility(this.path_calculator.GetShortestPath(graph, new_set));
    				}
    				if(this.utility_without_both == -1){
    					new_set = this.getMyNodes();	
	    				for(int j = new_set.size() - 1; j >= 0; j--)
	    					if(new_set.get(j).equals(this.best_proposition1) || new_set.get(j).equals(this.best_proposition2) ){
	    						new_set.remove(j);
	    					}
	    				this.utility_without_both = this.CalculateUtility(this.path_calculator.GetShortestPath(graph, new_set));
    				}
    				
    				double utility_diff1 = -1, utility_diff2 = -1, utility_diff_both = -1;
        				
    				if(prop_for_1 != null)
    					utility_diff1 = Double.valueOf(prop_for_1[0]);
    				if(prop_for_2 != null)
    					utility_diff2 = Double.valueOf(prop_for_2[0]);
    				if(prop_for_both != null)
    					utility_diff_both = Double.valueOf(prop_for_both[0]);
    				
    				if(utility_diff1 != -1 && (this.current_utility - this.utility_without_0 + utility_diff1) > current_max_diff){
    					current_max_diff = this.current_utility - this.utility_without_0 + utility_diff1;
    					current_max_diff_type = 1;
    					current_max_diff_index = i;
    					current_max_diff_is_tranfer = true;
    				}
    				if(utility_diff2 != -1 && (this.current_utility - this.utility_without_1 + utility_diff2) > current_max_diff){
    					current_max_diff = this.current_utility - this.utility_without_1 + utility_diff2;
    					current_max_diff_type = 2;
    					current_max_diff_index = i;
    					current_max_diff_is_tranfer = true;
    				}
    				if(utility_diff_both != -1 && (this.current_utility - this.utility_without_both + utility_diff_both) > current_max_diff){
    					current_max_diff = this.current_utility - this.utility_without_both + utility_diff_both;
    					current_max_diff_type = 3;
    					current_max_diff_index = i;
    					current_max_diff_is_tranfer = true;
    				}    				


    			}
    			else {
    				// the proposition is an exchange proposition. 
    				// we try to maximise the sum of the two differences of utility
    				
    				// test all those proposed for 1rst and choose the best
        			if(prop_for_1 != null && prop_for_1.length > 0){
        				for(int k = 0; k <  prop_for_1.length; k+=3){
        					for(int j = 0; j < this.myNodes.size(); j++){
        						if(this.getMyNodes(j).equals(best_proposition1))
        							// we change only the node we proposed with the one proposed in exchange
        							new_set.add(prop_for_1[k+1]);
        						else
        							new_set.add(this.getMyNodes(j));
        							
        					}
        					
        					double other_agent_utility_diff = Double.valueOf(prop_for_1[k]);
        					// check if the cost decreases when exchanging my worst node
        					// with node offered
        					actual_utility = this.CalculateUtility(this.path_calculator.GetShortestPath(graph, new_set));
        					if(this.current_utility - actual_utility +  other_agent_utility_diff > current_max_diff){
        						best_offer1 = prop_for_1[k+1];
        						best_offer1_idle = Integer.valueOf(prop_for_1[k+2]);
        						best_offer2 = null;
        						best_offer2_idle = 0;
        						special_offer = null;
        						special_offer_idle = 0;
        						
        						current_max_diff = this.current_utility - actual_utility +  other_agent_utility_diff;
        						current_max_diff_index = i;
        						current_max_diff_is_tranfer = false;
        					}
        					
        					new_set.clear();
        					
        				}
        			}
        			
            		// test all those proposed for 2nd and choose the best
        			if(prop_for_2 != null && prop_for_2.length > 0 && best_proposition2 != null){
        				for(int k = 0; k <  prop_for_2.length; k+=3){
        					for(int j = 0; j < this.myNodes.size(); j++){
        						if(this.getMyNodes(j).equals(best_proposition2))
        							// we change only the node we proposed with the one proposed in exchange
        							new_set.add(prop_for_2[k+1]);
        						else
        							new_set.add(this.getMyNodes(j));
        							
        					}
        					double other_agent_utility_diff = Double.valueOf(prop_for_2[k]);	
        					// check if the cost decreases when exchanging my worst node
        					// with node offered
        					actual_utility = this.CalculateUtility(this.path_calculator.GetShortestPath(graph, new_set));
        					if(this.current_utility - actual_utility +  other_agent_utility_diff > current_max_diff){
        						best_offer1 = null;
        						best_offer1_idle = 0;
        						best_offer2 = prop_for_2[k+1];
        						best_offer2_idle = Integer.valueOf(prop_for_2[k+2]);
        						special_offer = null;
        						special_offer_idle = 0;
        						
        						current_max_diff = this.current_utility - actual_utility +  other_agent_utility_diff;
        						current_max_diff_index = i;
        						current_max_diff_is_tranfer = false;
        					}
        					
        					new_set.clear();
        				}
        			}

            		// test all those proposed for both and choose the best
        			if(best_proposition2 != null && prop_for_both != null && prop_for_both.length > 0){
        				for(int k = 0; k <  prop_for_both.length; k+=3){
        					for(int j = 0; j < this.myNodes.size(); j++){
        						if(!this.getMyNodes(j).equals(best_proposition1) && !this.getMyNodes(j).equals(best_proposition2))
        							new_set.add(this.getMyNodes(j));
        					}
        					
        					new_set.add(prop_for_both[k+1]);
        					
        					double other_agent_utility_diff = Double.valueOf(prop_for_both[k]);
        					// check if the cost decreases when exchanging my worst node
        					// with node offered
        					actual_utility = this.CalculateUtility(this.path_calculator.GetShortestPath(graph, new_set));
        					if(this.current_utility - actual_utility +  other_agent_utility_diff > current_max_diff){
        						best_offer1 = null;
        						best_offer1_idle = 0;
        						best_offer2 = null;
        						best_offer2_idle = 0;
        						special_offer = prop_for_both[k+1];
        						special_offer_idle = Integer.valueOf(prop_for_both[k+2]);
        						
        						current_max_diff = this.current_utility - actual_utility +  other_agent_utility_diff;
        						current_max_diff_index = i;
        						current_max_diff_is_tranfer = false;
        					}	
        					
        					new_set.clear();
        				}
        			}
        			
            		// test double exchanges (or 2 for 1)
        			if(double_prop != null && double_prop.size() > 0){
        				for(int k = 0; k <  double_prop.size(); k++){
        					String[] subprop = double_prop.get(k);
        					
        					for(int j = 0; j < this.myNodes.size(); j++){
        						if(!this.getMyNodes(j).equals(best_proposition1)  && (best_proposition2 == null || !this.getMyNodes(j).equals(best_proposition2)))
        							new_set.add(this.getMyNodes(j));
        					}
        					
        					new_set.add(subprop[1]);
        					new_set.add(subprop[3]);
        							
        					// check if the cost decreases when exchanging my worst node
        					// with node offered
        					double other_agent_utility_diff = Double.valueOf(subprop[0]);
        					actual_utility = this.CalculateUtility(this.path_calculator.GetShortestPath(graph, new_set));
        					if(this.current_utility - actual_utility +  other_agent_utility_diff > current_max_diff){
        						best_offer1 = subprop[1];
        						best_offer1_idle = Integer.valueOf(subprop[2]);
        						best_offer2 = subprop[3];
        						best_offer2_idle = Integer.valueOf(subprop[4]);
        						special_offer = null;
        						special_offer_idle = 0;
        						
        						current_max_diff = this.current_utility - actual_utility +  other_agent_utility_diff;
        						current_max_diff_index = i;
        						current_max_diff_is_tranfer = false;
        					}	
        					
        					new_set.clear();
        				}
        			}
    				
    				
    				
    			}
    		}

    		
    		/**
    		 * We found the proposition that maximizes the sum of the differences in utility
    		 * We can now attribute the nodes
    		 * 
    		 **/
    		
    		
    		if(current_max_diff <= 0)
    			for(int i = 0; i < ComplexPropositions.size(); i++){
    				String buyer = this.FBABuyers.get(i);
					reject.setReceiver(buyer);
					this.SendSpeechAct(reject);
    		}
    		else {
    			SpeechAct act;
    			
    			
	    		for(int i = 0; i < ComplexPropositions.size(); i++)
	    			if(i != current_max_diff_index){
	    				String buyer = this.FBABuyers.get(i);
						reject.setReceiver(buyer);
						this.SendSpeechAct(reject);
    				}
    			
	    		String buyer = this.FBABuyers.get(current_max_diff_index);
    	    	if(current_max_diff_is_tranfer){
    	    		if(current_max_diff_type == 1){
						String[] winner_bid = {};
						String[] bids_str = {bids.get(0), Integer.toString((int) this.estimatedIdleness(bids.get(0)))};
						ComplexBid troc = new ComplexBid(winner_bid, bids_str, null, null);
						act = new SpeechAct(this.myCurrentTransaction, SpeechActPerformative.ACCEPT, this.agent_id, buyer, troc);
						this.SendSpeechAct(act);
						
						this.exchanges++ ;
						exchange_done = true;
						
						this.removeFromMyNodes(best_proposition1);
						this.OrderMyNodes();
						this.current_utility = this.CalculateUtility(this.getMyNodes());
						
						System.out.println("Agent " + this.agent_id + " trans #" + reject.getTransactionId() + " : giving first " + best_proposition1 +
								". New utility : " + this.current_utility + ".");
						
					}
					else if(current_max_diff_type == 2){
						String[] winner_bid = {};
						String[] bids_str = {bids.get(1), Integer.toString((int) this.estimatedIdleness(bids.get(1)))};
						ComplexBid troc = new ComplexBid(winner_bid, bids_str, null, null);
						act = new SpeechAct(this.myCurrentTransaction, SpeechActPerformative.ACCEPT, this.agent_id, buyer, troc);
						this.SendSpeechAct(act);
						
						this.exchanges++ ;
						exchange_done = true;
						
						this.removeFromMyNodes(best_proposition2);
						this.OrderMyNodes();
						this.current_utility = this.CalculateUtility(this.getMyNodes());
						
						System.out.println("Agent " + this.agent_id + " trans #" + reject.getTransactionId() +  " : giving second " + best_proposition2 +
								". New utility : " + this.current_utility + ".");
						
					}
					else if(current_max_diff_type == 3){
						String[] winner_bid = {};
						String[] bids_str = new String[2*bids.size()];
						for(int i1 = 0; i1 < bids.size(); i1++){
							bids_str[2*i1] = bids.get(i1);	
							bids_str[2*i1+1] = Integer.toString((int) this.estimatedIdleness(bids.get(i1)));
						}
						ComplexBid troc = new ComplexBid(winner_bid, bids_str, null, null);
						act = new SpeechAct(this.myCurrentTransaction, SpeechActPerformative.ACCEPT, this.agent_id, buyer, troc);
						this.SendSpeechAct(act);
						
						this.exchanges +=2 ;
						exchange_done = true;
						
						this.removeFromMyNodes(best_proposition1);
						this.removeFromMyNodes(best_proposition2);
						
						this.OrderMyNodes();
						this.current_utility = this.CalculateUtility(this.getMyNodes());
						
						System.out.println("Agent " + this.agent_id + " trans #" + reject.getTransactionId() +  " : giving special " + best_proposition1 + " and " + best_proposition2 +
								". New utility : " + this.current_utility + ".");
						
					}
    	    		
    	    	}
    	    	else{
					if(best_offer1 != null && best_offer2 != null){
						String[] winner_bid = {best_offer1 , best_offer2};
						String[] bids_str = new String[2*bids.size()];
						for(int i1 = 0; i1 < bids.size(); i1++){
							bids_str[2*i1] = bids.get(i1);
							bids_str[2*i1+1] = Integer.toString((int) this.estimatedIdleness(bids.get(i1)));
						}
						ComplexBid troc = new ComplexBid(winner_bid, bids_str, null, null);		
						act = new SpeechAct(this.myCurrentTransaction, SpeechActPerformative.ACCEPT, this.agent_id, buyer, troc);
						this.SendSpeechAct(act);
						
						this.exchanges +=2 ;
						exchange_done = true;
						
						this.removeFromMyNodes(best_proposition1);
						this.removeFromMyNodes(best_proposition2);
						this.addToMyNodes(best_offer1, best_offer1_idle);
						this.addToMyNodes(best_offer2, best_offer2_idle);
						this.OrderMyNodes();
						this.current_utility = this.CalculateUtility(this.getMyNodes());
						
						System.out.println("Agent " + this.agent_id + " trans #" + reject.getTransactionId() + " : exchanging " + best_proposition1 + " and " + best_proposition2 +
								" for " + best_offer1 + "(" +  best_offer1_idle  + ") and " + best_offer2 + "(" + best_offer2_idle + "). New utility : " + this.current_utility + ".");
					}
					else if(best_offer1 != null){
						String[] winner_bid = {best_offer1};
						String[] bids_str = {bids.get(0), Integer.toString((int) this.estimatedIdleness(bids.get(0)))};
						ComplexBid troc = new ComplexBid(winner_bid, bids_str, null, null);
						act = new SpeechAct(this.myCurrentTransaction, SpeechActPerformative.ACCEPT, this.agent_id, buyer, troc);
						this.SendSpeechAct(act);
						
						this.exchanges++ ;
						exchange_done = true;
						
						this.removeFromMyNodes(best_proposition1);
						this.addToMyNodes(best_offer1, best_offer1_idle);
						this.OrderMyNodes();
						this.current_utility = this.CalculateUtility(this.getMyNodes());
						
						System.out.println("Agent " + this.agent_id + " trans #" + reject.getTransactionId() + " : exchanging first " + best_proposition1 +
								" for " + best_offer1  + "(" +  best_offer1_idle  + "). New utility : " + this.current_utility + ".");
						
					}
					else if(best_offer2 != null){
						String[] winner_bid = {best_offer2};
						String[] bids_str = {bids.get(1), Integer.toString((int) this.estimatedIdleness(bids.get(1)))};
						ComplexBid troc = new ComplexBid(winner_bid, bids_str, null, null);
						act = new SpeechAct(this.myCurrentTransaction, SpeechActPerformative.ACCEPT, this.agent_id, buyer, troc);
						this.SendSpeechAct(act);
						
						this.exchanges++ ;
						exchange_done = true;
						
						this.removeFromMyNodes(best_proposition2);
						this.addToMyNodes(best_offer2, best_offer2_idle);
						this.OrderMyNodes();
						this.current_utility = this.CalculateUtility(this.getMyNodes());
						
						System.out.println("Agent " + this.agent_id + " trans #" + reject.getTransactionId() +  " : exchanging second " + best_proposition2 +
								" for " + best_offer2  + "(" +  best_offer2_idle  + "). New utility : " + this.current_utility + ".");
					}
					else if(special_offer != null){
						String[] winner_bid = {special_offer};
						String[] bids_str = new String[2*bids.size()];
						for(int i1 = 0; i1 < bids.size(); i1++){
							bids_str[2*i1] = bids.get(i1);	
							bids_str[2*i1+1] = Integer.toString((int) this.estimatedIdleness(bids.get(i1)));
						}
						ComplexBid troc = new ComplexBid(winner_bid, bids_str, null, null);
						act = new SpeechAct(this.myCurrentTransaction, SpeechActPerformative.ACCEPT, this.agent_id, buyer, troc);
						this.SendSpeechAct(act);
						
						this.exchanges +=2 ;
						exchange_done = true;
						
						this.removeFromMyNodes(best_proposition1);
						this.removeFromMyNodes(best_proposition2);
						this.addToMyNodes(special_offer, special_offer_idle);
						
						this.OrderMyNodes();
						this.current_utility = this.CalculateUtility(this.getMyNodes());
						
						System.out.println("Agent " + this.agent_id + " trans #" + reject.getTransactionId() +  " : exchanging special " + best_proposition1 + " and " + best_proposition2 +
								" for " + special_offer  + "(" +  special_offer_idle  + "). New utility : " + this.current_utility + ".");
						
					}
    	    		
    	    	}
    			
    			
    		}

    		
    	}

    		
    	
    	// update nb_cycles without exchange
    	if(!exchange_done)
    		this.cycles_without_exchange++;
    	else
    		this.cycles_without_exchange = 0;
    	
    	
    	this.best_proposition1 = null;
		this.best_proposition2 = null;
		this.utility_without_0 = -1;
		this.utility_without_1 = -1;
		this.utility_without_both = 0;
		this.nb_engaged_nodes = this.NbEngagedNodes();
		
		
    	if(FBABuyers != null)
    		FBABuyers.clear();
    	
    	if(ComplexPropositions != null)
    		ComplexPropositions.clear();
    	
    	this.myCurrentTransaction = -1;
    	this.received_answers = 0;
	
	}

	@Override
	protected boolean enterCondition() {
		return true;
	}

	@Override
	protected SpeechAct enter_Message(int id) {
		return new SpeechAct(id, SpeechActPerformative.ENTER, this.agent_id, "all_agents");
	}

	@Override
	/**
	 * When receiving an ENTER message, the agent gives to the new agent some nodes
	 * the number of nodes given is chosen so that each agent in the system has the same number of nodes in average
	 * the nodes chosen are those who cost the most in the node list, according to the chosen heuristic
	 */
	protected SpeechAct manage_Enter_Message(SpeechAct entering_act) {
		if(this.myNodes.size() <= 2)
			return new SpeechAct(entering_act.getTransactionId(), SpeechActPerformative.PROPOSE, 
					this.agent_id, entering_act.getSender(), new ComplexBid(null, null, null, null));
		
		int nb_nodes_to_send = (int)((double)this.myNodes.size() * (1 - (double)this.agents_num / (double)(this.agents_num + 1)));
		if(nb_nodes_to_send == 0)
				nb_nodes_to_send = 1;
		
		LinkedList<StringAndDouble> utility_list = new LinkedList<StringAndDouble>();
		
		StringAndDouble current;
		double path_utility, path_gain;
		LinkedList<String> pathlist = new LinkedList<String>();
		

		for(int j = 0; j < this.myNodes.size(); j++){
			current = this.myNodes.get(j);				
			pathlist = this.getMyNodes();
			pathlist.remove(current.STRING);
			path_utility = this.CalculateUtility(this.path_calculator.GetShortestPath(graph, pathlist));
				
			path_gain = (this.current_utility - path_utility);

			utility_list.add(new StringAndDouble(current.STRING, path_gain));
		}
		
		LinkedList<StringAndDouble> ordered_utility_list = new LinkedList<StringAndDouble>();
		while(utility_list.size() > 0){
			double max_utility = - Double.MAX_VALUE;
			int index = -1;
			for(int j = 0; j < utility_list.size(); j++)
				if(utility_list.get(j).double_value > max_utility){
					max_utility = utility_list.get(j).double_value;
					index = j;
				}
			
			if(index != -1){
				ordered_utility_list.add(utility_list.get(index));
				utility_list.remove(index);
			}
		}
		
		
		String[] nodes_to_send = new String[2*nb_nodes_to_send];
		for(int i = 0; i < nb_nodes_to_send; i++){
			nodes_to_send[2*i] = ordered_utility_list.get(i).STRING;
			nodes_to_send[2*i + 1] = String.valueOf((int)this.estimatedIdleness(ordered_utility_list.get(i).STRING));
			this.removeFromMyNodes(ordered_utility_list.get(i).STRING);
		}
		
		ComplexBid bid = new ComplexBid(nodes_to_send, null, null, null);
		SpeechAct act = new SpeechAct(entering_act.getTransactionId(), SpeechActPerformative.PROPOSE, 
																	this.agent_id, entering_act.getSender(), bid);
		
		this.plan.clear();
		
		return act;
	}
	
	@Override
	protected void manage_Enter_Protocol() {
		if(this.ComplexPropositions != null && this.ComplexPropositions.size()>0){
			for(int i = 0; i < this.ComplexPropositions.size(); i++){
				String[] nodesToAdd = this.ComplexPropositions.get(i).getBidsForFirst();
				if(nodesToAdd != null && nodesToAdd.length != 0){
					String added_nodes = "";
					for(int j = 0; j < nodesToAdd.length; j+=2){
						this.addToMyNodes(nodesToAdd[j], Double.valueOf(nodesToAdd[j+1]));
						added_nodes += nodesToAdd[j] + " (" + nodesToAdd[j+1] + "), ";
					}
					
					if(!added_nodes.equals(""))
						added_nodes = added_nodes.substring(0, added_nodes.length() - 2) + ";";
					
					SpeechAct act = new SpeechAct(this.myCurrentTransaction, SpeechActPerformative.ACCEPT, 
							this.agent_id, this.FBABuyers.get(i));
					this.SendSpeechAct(act);
					
					System.out.println("Enter : from " + this.FBABuyers.get(i) + " : " + added_nodes);
				}
				else {
					SpeechAct act = new SpeechAct(this.myCurrentTransaction, SpeechActPerformative.ACCEPT, 
							this.agent_id, this.FBABuyers.get(i));
					this.SendSpeechAct(act);
				}
			}
    		
        	if(FBABuyers != null)
        		FBABuyers.clear();
        	if(ComplexPropositions != null)
        		ComplexPropositions.clear();

        	this.myCurrentTransaction = -1;
        	this.received_answers = 0;
			
		}
	}

	@Override
	protected SpeechAct quit_Message(int id) {
		int nb_nodes_by_group = Math.max(this.myNodes.size() / (this.agents_num - 1), 1);
		String[] proposed_nodes = new String[0];
		proposed_nodes = this.getMyNodes().toArray(proposed_nodes);
		Vector<String[]> groups = new Vector<String[]>();
		
		// on fait des groupes de tailles égales
		while(proposed_nodes.length > 0){	
			if(this.agents_num == 2){
				groups.add(proposed_nodes);
				proposed_nodes = new String[0];
				break;
			}
			if(this.agents_num > 2 && proposed_nodes.length - nb_nodes_by_group <= 0){
				break;
				
			}
			else {		
		
				LinkedList<String> best_group = new LinkedList<String>();
				double utility, min_utility = Double.MAX_VALUE;
				LinkedList<String> current_group = new LinkedList<String>();
				
				int[] current_index = new int[nb_nodes_by_group];
				for(int i = 0; i < nb_nodes_by_group; i++)
					current_index[i] = i;
				
				while(current_index[0] < proposed_nodes.length - nb_nodes_by_group){
					current_group.clear();
					for(int j = 0; j < current_index.length; j++)
						current_group.add(proposed_nodes[current_index[j]]);
					
					utility = this.CalculateUtility(this.path_calculator.GetShortestPath(graph, current_group));
					
					if(utility < min_utility){
						min_utility = utility;
						best_group.clear();
						for(int j = 0; j < current_index.length; j++)
							best_group.add(proposed_nodes[current_index[j]]);
					}
					
					
					current_index[current_index.length - 1]++;
					for(int j = current_index.length - 1; j > 0; j--)
						if(current_index[j] >= proposed_nodes.length - (current_index.length - j)){
							current_index[j-1]++;
							for(int k = j; k < current_index.length; k++)
								current_index[k] = current_index[k-1] + 1;
						}	
				}
				
				String[] newBid = new String[best_group.size()];
				for(int i = 0; i < best_group.size(); i++)
					newBid[i] = best_group.get(i);
				groups.add(newBid);
				
				String[] new_list = new String[proposed_nodes.length - best_group.size()];
				int i = 0;
				for(int j = 0; j < proposed_nodes.length; j++)
					if(!best_group.contains(proposed_nodes[j])){
						new_list[i] = proposed_nodes[j];
						i++;
					}
				proposed_nodes = new_list;           
				
			}
		}
		
		//on répartit ce qui reste
		if(proposed_nodes.length > 0){
			LinkedList<String> remaining_nodes = new LinkedList<String>();
			for(int i = 0; i < proposed_nodes.length; i++)
				remaining_nodes.add(proposed_nodes[i]);
			
			LinkedList<String> group = new LinkedList<String>();
			LinkedList<Integer> indexes = new LinkedList<Integer>();
			double utility, min_utility;
			while(remaining_nodes.size() > 0){
				min_utility = Double.MAX_VALUE;
				indexes.clear();
				for(int i = 0; i < groups.size(); i++){
					group.clear();
					for(int j = 0; j < groups.get(i).length; j++)
						group.add(groups.get(i)[j]);
					group.add(remaining_nodes.getFirst());
					
					utility = this.CalculateUtility(this.path_calculator.GetShortestPath(graph, group));
					
					if(Math.abs(utility - min_utility) < 0.001){
						indexes.add(i);
					}
					else if(utility < min_utility){
						min_utility = utility;
						indexes.clear();
						indexes.add(i);
					}
				}
				
				int winner_index = indexes.get((int)(Math.random() * indexes.size()));
				String[] new_group = new String[groups.get(winner_index).length + 1];
				for(int j = 0; j < groups.get(winner_index).length; j++)
					new_group[j] = groups.get(winner_index)[j];
				new_group[groups.get(winner_index).length] = remaining_nodes.getFirst();
				
				groups.remove(winner_index);
				groups.insertElementAt(new_group, winner_index);
				remaining_nodes.removeFirst();
			}
				
		}
		
		this.quitting_node_group = groups;
		
		ComplexBid bid = new ComplexBid(null, null, null, groups);
		SpeechAct act = new SpeechAct(id, SpeechActPerformative.QUIT, this.agent_id, "all_agents", bid);
		
		return act;
	}

	@Override
	/**
	 * when receiving a QUIT message with a list of nodes, the agent answers by giving its preferences over sets of nodes
	 * it calculates the set of nodes which minimizes the new path cost, record it, delete the set from the list, then iterates.
	 * This does not list all sets of nodes but limits the combinatorial explosion by ensuring that each node appears only
	 * once in the list of preferences.
	 */
	protected SpeechAct manage_Quit_Message(SpeechAct quitting_act) {
		Vector<String[]> proposed_nodegroup = quitting_act.getComplexBid().getDoubleBidsForBoth();
		double[] utility_list = new double[proposed_nodegroup.size()];
		
		LinkedList<String> mynodes_copy;
		Vector<String[]> bids = new Vector<String[]>();
		
		for(int i = 0; i < proposed_nodegroup.size(); i++){
			mynodes_copy = this.getMyNodes();
			for(int j = 0; j < proposed_nodegroup.get(i).length; j++)
				mynodes_copy.add(proposed_nodegroup.get(i)[j]);
			
			utility_list[i] = this.CalculateUtility(this.path_calculator.GetShortestPath(graph, mynodes_copy));
		}
			
		boolean[] used = new boolean[proposed_nodegroup.size()];
		boolean all_used = false;
		while(!all_used){
			double min_cost = Double.MAX_VALUE;
			int index = -1;
			for(int i = 0; i < proposed_nodegroup.size(); i++){
				if(!used[i] && utility_list[i] < min_cost){
					min_cost = utility_list[i];
					index = i;
				}
			}
			
			String[] bid = new String[2];
			bid[0] = Integer.toString(index);
			bid[1] = Double.toString(utility_list[index] - this.current_utility);
			bids.add(bid);
			used[index] = true;
		
			all_used = true;
			for(int j = 0; j < used.length; j++)
				all_used &= used[j];
			
		}	
		
		ComplexBid mybid = new ComplexBid(null, null, null, bids);
		
		return new SpeechAct(quitting_act.getTransactionId(), SpeechActPerformative.PROPOSE, this.agent_id, 
									quitting_act.getSender(), mybid);
	}

	@Override
	protected void manage_Quit_Protocol() {
		for(int i = this.FBABuyers.size() - 1; i > 0; i--)
    		if(!this.known_agents.contains(this.FBABuyers.get(i))){
    			SpeechAct reject = new SpeechAct(this.myCurrentTransaction, SpeechActPerformative.REJECT, this.agent_id, FBABuyers.get(i), new LinkedList<String>(), 0);
    			this.SendSpeechAct(reject);
    			this.FBABuyers.remove(i);
    			this.ComplexPropositions.remove(i);
    		}
		
		String[] agents = new String[this.FBABuyers.size()];
		int[][] nodes_preferences = new int[agents.length][];
		double[][] nodes_pref_costs = new double[agents.length][];

    	for(int i = 0; i < agents.length; i++){
    		agents[i] = this.FBABuyers.get(i);

    		Vector<String[]> preferences = this.ComplexPropositions.get(i).getDoubleBidsForBoth();
    		nodes_preferences[i] = new int[preferences.size()];
    		nodes_pref_costs[i] = new double[preferences.size()];
    		for(int j = 0; j < preferences.size(); j++){
    			String[] bid = preferences.get(j);
    			nodes_preferences[i][j] = Integer.valueOf(bid[0]);
    			nodes_pref_costs[i][j] = Double.valueOf(bid[1]);
    		}

    	}
    	
    	
    	int[] current_index = new int[agents.length];
    	String[][] distributed_nodes = new String[agents.length][this.myNodes.size()];
    	LinkedList<String> distributed_list = new LinkedList<String>();
    	int[] current_distributed_index = new int[agents.length];
    	int[] real_distributed_index = new int[agents.length];
    	boolean[] served = new boolean[agents.length];
    	
    	boolean finished = false;
    	
    	while(!finished){
    		double min_utility = Double.MAX_VALUE;
    		int index = -1;
    		for(int agent = 0; agent < agents.length; agent++){
    			if(current_index[agent] < nodes_pref_costs[agent].length){
	    			double utility = nodes_pref_costs[agent][current_index[agent]];
	    			if(utility < min_utility){
	    				min_utility = utility;
	    				index = agent;
	    			}
    			}
    		}
    		
    		if(index == -1)
    			break;
    		
    		String[] minimal_utility_set = this.quitting_node_group.get(nodes_preferences[index][current_index[index]]);
    		boolean not_ok = false;
    		for(int i = 0; i < minimal_utility_set.length; i++)
    			if(distributed_list.contains(minimal_utility_set[i]))
    				not_ok = true;
    		
    		if(not_ok){
    			current_index[index]++;
    			continue;
    		}
    		
    		for(int i = 0; i < minimal_utility_set.length; i++){
    			distributed_nodes[index][i] = minimal_utility_set[i];
    			current_distributed_index[index]++;
    			real_distributed_index[index]++;
    			distributed_list.add(minimal_utility_set[i]);
    		}
    		current_index[index] = nodes_pref_costs[index].length + 1;
    		served[index] = true;
    				
    		finished = true;
    		for(int j = 0; j < agents.length; j++)
    			finished &= (served[j]);
    		
    	}
    	
    	// les préférences étant incomplètes, on attribue les noeuds qui restent par proximité
    	LinkedList<String> mynodes_copy = this.getMyNodes();
    	for(int i = 0; i < distributed_list.size(); i++)
    		mynodes_copy.remove(distributed_list.get(i));
    	
    	while(mynodes_copy.size() > 0){
    		String current = mynodes_copy.getFirst();
    		Double min_dist = Double.MAX_VALUE;
    		LinkedList<Integer> index = new LinkedList<Integer>();
    		
    		for(int i = 0; i < agents.length; i++){
    			double dist = this.get_minDistance_to_nodes(current, distributed_nodes[i], current_distributed_index[i]);
    			if(Math.abs(dist - min_dist) < 0.001){
    				index.add(i);
    			}
    			else if(dist < min_dist){
    				min_dist = dist;
    				index.clear();
    				index.add(i);
    			}
    		}
    		
    		int winning_index = index.get((int)(Math.random() * index.size()));
    		distributed_nodes[winning_index][current_distributed_index[winning_index]] = current;
    		mynodes_copy.remove(current);
    		current_distributed_index[winning_index]++;
    		real_distributed_index[winning_index]++;
    	}
    	
    	for(int j = 0; j < agents.length; j++){
    		String[] nodes_to_send = new String[2 * real_distributed_index[j]];
    		for(int k = 0; k < real_distributed_index[j]; k++){
    			nodes_to_send[2*k] = distributed_nodes[j][k];
    			nodes_to_send[2*k+1] = String.valueOf((int)this.estimatedIdleness(distributed_nodes[j][k]));
    			
    			this.removeFromMyNodes(distributed_nodes[j][k]);
    		}
    		
    		ComplexBid bid = new ComplexBid(null, nodes_to_send, null, null);
    		SpeechAct act = new SpeechAct(myCurrentTransaction, SpeechActPerformative.ACCEPT, this.agent_id, agents[j], bid);
			this.SendSpeechAct(act);
    	}
    	
    	this.inactive = true;

	}

	
	
	protected double get_minDistance_to_nodes(String target, LinkedList<String> nodes){
		double min_dist = Double.MAX_VALUE;
		Node target_node = this.graph.getNode(target);
		
		for(int i = 0; i < nodes.size(); i++){
			double dist = this.graph.getDistance(target_node, this.graph.getNode(nodes.get(i)));
			if(dist < min_dist)
				min_dist = dist;
		}
		
		return min_dist;
	}
	
	protected double get_minDistance_to_nodes(String target, String[] nodes, int last_index){
		double min_dist = Double.MAX_VALUE;
		Node target_node = this.graph.getNode(target);
		
		for(int i = 0; i < last_index; i++){
			double dist = this.graph.getDistance(target_node, this.graph.getNode(nodes[i]));
			if(dist < min_dist)
				min_dist = dist;
		}
		
		return min_dist;
	}
	

}
