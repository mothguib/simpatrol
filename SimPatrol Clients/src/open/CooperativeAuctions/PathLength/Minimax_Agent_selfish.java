package open.CooperativeAuctions.PathLength;

import java.util.LinkedList;
import java.util.Vector;

import open.CooperativeAuctions.Cooperative_Agent;

import util.StringAndDouble;
import util.graph.Node;

import closed.FBA.ComplexBid;
import closed.FBA.SpeechAct;
import closed.FBA.SpeechActPerformative;

public class Minimax_Agent_selfish extends Cooperative_Agent {
	
	protected double MAX_DIST;
	
	

	public Minimax_Agent_selfish(String id, int number_of_agents,
			LinkedList<String> nodes, double idleness_rate_for_path, double max_dist) {
		super(id, number_of_agents, nodes, idleness_rate_for_path);
		MAX_DIST = max_dist;
	}
	
	public Minimax_Agent_selfish(String id, double entering_time, double quitting_time, 
			int number_of_agents, LinkedList<String> nodes,
			double idleness_rate_for_path, double max_dist, String society_id) {
		super(id, entering_time, quitting_time, number_of_agents, nodes, idleness_rate_for_path, society_id);
		MAX_DIST = max_dist;
	}

	@Override
	protected double CalculateUtility(LinkedList<String> nodes) {
		return this.CalculatePathCost(nodes);
	}

	@Override
	protected LinkedList<String> GenerateAuctionLaunchMessage(LinkedList<String> proposed_nodes) {
		LinkedList<String> new_list = new LinkedList<String>();
		for(String node : proposed_nodes)
			new_list.add(node);
		
		LinkedList<String> new_set = new LinkedList<String>();
		for(int j = 0; j < this.myNodes.size(); j++)
			new_set.add(this.getMyNodes(j));
		new_set.remove(proposed_nodes.get(0));
		double new_utility = this.CalculateUtility(this.path_calculator.GetShortestPath(graph, new_set));
		new_list.add(Double.toString(new_utility));
		
		if(proposed_nodes.size() == 2){
			new_set.add(proposed_nodes.get(0));
			new_set.remove(proposed_nodes.get(1));
			new_utility = this.CalculateUtility(this.path_calculator.GetShortestPath(graph, new_set));
			new_list.add(Double.toString(new_utility));
		
			new_set.remove(proposed_nodes.get(0));
			new_utility = this.CalculateUtility(this.path_calculator.GetShortestPath(graph, new_set));
			new_list.add(Double.toString(new_utility));
		
		}
		
		return new_list;
	}

	

	@Override
	protected double getValueToSend(double new_utility) {
		return new_utility;
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
		double proposer_utility_1, proposer_utility_2, proposer_utility_both = 0;
		String[] offers;
		
		if(received_offers.size() < 2)
			return new ComplexBid(null, null, null, null);
		
		node1 = received_offers.get(0);
		if(received_offers.size() == 5)
			node2 = received_offers.get(1);
		if(received_offers.size() == 2) {
			proposer_utility_1 = Double.valueOf(received_offers.get(1));
			proposer_utility_2 = proposer_utility_1;
		}
		else {
			proposer_utility_1 = Double.valueOf(received_offers.get(2));
			proposer_utility_2 = Double.valueOf(received_offers.get(3));
			proposer_utility_both = Double.valueOf(received_offers.get(4));
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
    		 * There is no exchange possible. However if my utility is inferior to his, I might help him anyway
    		 */
    		
    		if(this.current_utility >= Math.max(proposer_utility_1, proposer_utility_2))
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
        		
    			if(new_utility < (k==0? proposer_utility_1 : proposer_utility_2)){
    				if(k == 0)
    					subset_list = new String[]{Double.toString(new_utility)};
    				else
    					subset1_list = new String[]{Double.toString(new_utility)};
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
	    		
				if(new_utility < proposer_utility_both)
					subset2_list = new String[]{Double.toString(new_utility)};
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
		double smallest_utility = this.current_utility;
		double biggest_utility = this.current_utility;
		
		String best_offer1 = null, best_offer2 = null, special_offer = null;
		int best_offer1_idle = 0, best_offer2_idle = 0, special_offer_idle = 0;
		int best_buyer = -1;
		
		String best_sympathy_offer1 = null, best_sympathy_offer2 = null, special_sympathy_offer = null;
		int best_sympathy_offer1_idle = 0, best_sympathy_offer2_idle = 0, special_sympathy_offer_idle = 0;
		int best_sympathy_buyer = -1;
		
		
    	
    	if(this.ComplexPropositions != null && this.ComplexPropositions.size()>0){
    		LinkedList<ComplexBid> proposed_exchanges = new LinkedList<ComplexBid>();
    		LinkedList<ComplexBid> proposed_transfers = new LinkedList<ComplexBid>();
    		
    		LinkedList<String> FBAbuyers_exchanges = new LinkedList<String>();
    		LinkedList<String> FBAbuyers_transfers = new LinkedList<String>();
    		
    		// first we separate the exchanges from the transfers
    		for(int i = 0; i < this.ComplexPropositions.size(); i++){
    			current_bid = this.ComplexPropositions.get(i);
    			prop_for_1 = current_bid.getBidsForFirst();
    			prop_for_2 = current_bid.getBidsForSecond();
    			prop_for_both = current_bid.getBidsForBoth();
    			double_prop = current_bid.getDoubleBidsForBoth();
    			
    			if((prop_for_1 != null && prop_for_1.length == 1)||
    					(prop_for_2 != null && prop_for_2.length == 1)||
    					(prop_for_both != null && prop_for_both.length == 1)){
    				proposed_transfers.add(current_bid);
    				FBAbuyers_transfers.add(this.FBABuyers.get(i));
    			}
    			else {
    				proposed_exchanges.add(current_bid);
    				FBAbuyers_exchanges.add(this.FBABuyers.get(i));
    			}
    				
    		}
    		
    		/**
    		 * For the minimax, exchange is always privileged
    		 * 
    		 * first mutually beneficial if it exists, then the ones that benefits the other agent.
    		 * 
    		 **/
    		/*
    		 * is there mutually beneficial exchanges ?
    		 */
    		
    		prop_for_1 = null;
    		prop_for_2 = null;
    		prop_for_both = null;
    		double_prop = null;
    		for(int i = 0; i < proposed_exchanges.size(); i++){
    			current_bid = proposed_exchanges.get(i);
    			prop_for_1 = current_bid.getBidsForFirst();
    			prop_for_2 = current_bid.getBidsForSecond();
    			prop_for_both = current_bid.getBidsForBoth();
    			double_prop = current_bid.getDoubleBidsForBoth();
    			
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
    					
    					double other_agent_utility = Double.valueOf(prop_for_1[k]);
    					// check if the cost decreases when exchanging my worst node
    					// with node offered
    					actual_utility = this.CalculateUtility(this.path_calculator.GetShortestPath(graph, new_set));
    					if(actual_utility < smallest_utility){
    						best_offer1 = prop_for_1[k+1];
    						best_offer1_idle = Integer.valueOf(prop_for_1[k+2]);
    						best_offer2 = null;
    						best_offer2_idle = 0;
    						special_offer = null;
    						special_offer_idle = 0;
    						
    						smallest_utility = actual_utility;
    						best_buyer = i;
    					}
    					else if(other_agent_utility > actual_utility && other_agent_utility > biggest_utility ){
    						best_sympathy_offer1 = prop_for_1[k+1];
    						best_sympathy_offer1_idle = Integer.valueOf(prop_for_1[k+2]);
    						best_sympathy_offer2 = null;
    						best_sympathy_offer2_idle = 0;
    						special_sympathy_offer = null;
    						special_sympathy_offer_idle = 0;
    						
    						biggest_utility = other_agent_utility;
    						best_sympathy_buyer = i;
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
    					double other_agent_utility = Double.valueOf(prop_for_2[k]);	
    					// check if the cost decreases when exchanging my worst node
    					// with node offered
    					actual_utility = this.CalculateUtility(this.path_calculator.GetShortestPath(graph, new_set));
    					if(actual_utility < smallest_utility){
    						best_offer1 = null;
    						best_offer1_idle = 0;
    						best_offer2 = prop_for_2[k+1];
    						best_offer2_idle = Integer.valueOf(prop_for_2[k+2]);
    						special_offer = null;
    						special_offer_idle = 0;
    						
    						smallest_utility = actual_utility;
    						best_buyer = i;
    					}
    					else if(other_agent_utility > actual_utility && other_agent_utility > biggest_utility ){
    						best_sympathy_offer1 = null;
    						best_sympathy_offer1_idle = 0;
    						best_sympathy_offer2 = prop_for_2[k+1];
    						best_sympathy_offer2_idle = Integer.valueOf(prop_for_2[k+2]);
    						special_sympathy_offer = null;
    						special_sympathy_offer_idle = 0;
    						
    						biggest_utility = other_agent_utility;
    						best_sympathy_buyer = i;
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
    					
    					double other_agent_utility = Double.valueOf(prop_for_both[k]);
    					// check if the cost decreases when exchanging my worst node
    					// with node offered
    					actual_utility = this.CalculateUtility(this.path_calculator.GetShortestPath(graph, new_set));
    					if(actual_utility < smallest_utility){
    						best_offer1 = null;
    						best_offer1_idle = 0;
    						best_offer2 = null;
    						best_offer2_idle = 0;
    						special_offer = prop_for_both[k+1];
    						special_offer_idle = Integer.valueOf(prop_for_both[k+2]);
    						
    						smallest_utility = actual_utility;
    						best_buyer = i;
    					}	
    					else if(other_agent_utility > actual_utility && other_agent_utility > biggest_utility ){
    						best_sympathy_offer1 = null;
    						best_sympathy_offer1_idle = 0;
    						best_sympathy_offer2 = null;
    						best_sympathy_offer2_idle = 0;
    						special_sympathy_offer = prop_for_both[k+1];
    						special_sympathy_offer_idle = Integer.valueOf(prop_for_both[k+2]);
    						
    						biggest_utility = other_agent_utility;
    						best_sympathy_buyer = i;
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
    					double other_agent_utility = Double.valueOf(subprop[0]);
    					actual_utility = this.CalculateUtility(this.path_calculator.GetShortestPath(graph, new_set));
    					if(actual_utility < smallest_utility){
    						best_offer1 = subprop[1];
    						best_offer1_idle = Integer.valueOf(subprop[2]);
    						best_offer2 = subprop[3];
    						best_offer2_idle = Integer.valueOf(subprop[4]);
    						special_offer = null;
    						special_offer_idle = 0;
    						
    						smallest_utility = actual_utility;
    						best_buyer = i;
    					}	
    					else if(other_agent_utility > actual_utility && other_agent_utility > biggest_utility ){
    						best_offer1 = subprop[1];
    						best_offer1_idle = Integer.valueOf(subprop[2]);
    						best_offer2 = subprop[3];
    						best_offer2_idle = Integer.valueOf(subprop[4]);
    						special_offer = null;
    						special_offer_idle = 0;
    						
    						biggest_utility = other_agent_utility;
    						best_sympathy_buyer = i;
    					}
    					
    					new_set.clear();
    				}
    			}	
    		}
    		
    		if(best_offer1 != null || best_offer2 != null || special_offer != null){
    			/**
    			 * There is a mutually beneficial exchange !
    			 * We proceed to the exchange
    			 */
    			SpeechAct act;
    			
    			if(FBAbuyers_transfers != null && FBAbuyers_transfers.size() > 0){
    	    		for(int i = 0; i < FBAbuyers_transfers.size(); i++){
    	    			String buyer = FBAbuyers_transfers.get(i);
    						reject.setReceiver(buyer);
    						this.SendSpeechAct(reject);
    				}
    			}
    	    	
    	    	if(FBAbuyers_exchanges != null && FBAbuyers_exchanges.size() > 0){
    	    		for(int i = 0; i < FBAbuyers_exchanges.size(); i++){
    	    			String buyer = FBAbuyers_exchanges.get(i);
    					if(i != best_buyer){
    						reject.setReceiver(buyer);
    						this.SendSpeechAct(reject);
    					}
    					else {
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
    							//LinkedList<String> old_positions = this.getMyNodes();
    							this.addToMyNodes(best_offer1, best_offer1_idle);
    							this.addToMyNodes(best_offer2, best_offer2_idle);
    							this.OrderMyNodes();
    							this.current_utility = this.CalculateUtility(this.getMyNodes());
    							
    							System.out.println("Agent " + this.agent_id + " trans #" + reject.getTransactionId() + " : exchanging " + best_proposition1 + " and " + best_proposition2 +
    									" for " + best_offer1 + "(" +  best_offer1_idle  + ") and " + best_offer2 + "(" + best_offer2_idle + "). New utility : " + this.current_utility + ".");
    							
    							
    							//this.restartAverageIdleness(old_positions);
    							//this.mountPriorityQueue();
    							//this.myPriorityQueueIndex = 0;
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
    							//LinkedList<String> old_positions = this.getMyNodes();
    							this.addToMyNodes(best_offer1, best_offer1_idle);
    							this.OrderMyNodes();
    							this.current_utility = this.CalculateUtility(this.getMyNodes());
    							
    							System.out.println("Agent " + this.agent_id + " trans #" + reject.getTransactionId() + " : exchanging first" + best_proposition1 +
    									" for " + best_offer1  + "(" +  best_offer1_idle  + "). New utility : " + this.current_utility + ".");
    							
    							//this.restartAverageIdleness(old_positions);
    							//this.mountPriorityQueue();
    							//this.myPriorityQueueIndex = 0;
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
    							//LinkedList<String> old_positions = this.getMyNodes();
    							this.addToMyNodes(best_offer2, best_offer2_idle);
    							this.OrderMyNodes();
    							this.current_utility = this.CalculateUtility(this.getMyNodes());
    							
    							System.out.println("Agent " + this.agent_id + " trans #" + reject.getTransactionId() +  " : exchanging second " + best_proposition2 +
    									" for " + best_offer2  + "(" +  best_offer2_idle  + "). New utility : " + this.current_utility + ".");
    							
    							//this.restartAverageIdleness(old_positions);
    							//this.mountPriorityQueue();
    							//this.myPriorityQueueIndex = 0;
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
    							//LinkedList<String> old_positions = this.getMyNodes();
    							this.addToMyNodes(special_offer, special_offer_idle);
    							
    							this.OrderMyNodes();
    							this.current_utility = this.CalculateUtility(this.getMyNodes());
    							
    							System.out.println("Agent " + this.agent_id + " trans #" + reject.getTransactionId() +  " : exchanging special " + best_proposition1 + " and " + best_proposition2 +
    									" for " + special_offer  + "(" +  special_offer_idle  + "). New utility : " + this.current_utility + ".");
    							
    							//this.restartAverageIdleness(old_positions);
    							//this.mountPriorityQueue();
    							//this.myPriorityQueueIndex = 0;
    						}
    						
    					}
    	    		}
    	    		
    	    	}
    			
    			
    		}
    		else if(best_sympathy_offer1 != null || best_sympathy_offer2 != null || special_sympathy_offer != null) {
    			/**
    			 * There is no mutually beneficial exchange, but ther is an agent I can unburden
    			 * 
    			 */
    			SpeechAct act;
    			
    			if(FBAbuyers_transfers != null && FBAbuyers_transfers.size() > 0){
    	    		for(int i = 0; i < FBAbuyers_transfers.size(); i++){
    	    			String buyer = FBAbuyers_transfers.get(i);
    						reject.setReceiver(buyer);
    						this.SendSpeechAct(reject);
    				}
    			}
    	    	
    	    	if(FBAbuyers_exchanges != null && FBAbuyers_exchanges.size() > 0){
    	    		for(int i = 0; i < FBAbuyers_exchanges.size(); i++){
    	    			String buyer = FBAbuyers_exchanges.get(i);
    					if(i != best_sympathy_buyer){
    						reject.setReceiver(buyer);
    						this.SendSpeechAct(reject);
    					}
    					else {
    						if(best_sympathy_offer1 != null){
    							String[] winner_bid = {best_sympathy_offer1};
    							String[] bids_str = {bids.get(0), Integer.toString((int) this.estimatedIdleness(bids.get(0)))};
    							ComplexBid troc = new ComplexBid(winner_bid, bids_str, null, null);
    							act = new SpeechAct(this.myCurrentTransaction, SpeechActPerformative.ACCEPT, this.agent_id, buyer, troc);
    							this.SendSpeechAct(act);
    							
    							this.exchanges++ ;
    							exchange_done = true;
    							
    							this.removeFromMyNodes(best_proposition1);
    							//LinkedList<String> old_positions = this.getMyNodes();
    							this.addToMyNodes(best_sympathy_offer1, best_sympathy_offer1_idle);
    							this.OrderMyNodes();
    							this.current_utility = this.CalculateUtility(this.getMyNodes());
    							
    							System.out.println("Agent " + this.agent_id + " trans #" + reject.getTransactionId() + " : exchanging by sympathy first" + best_proposition1 +
    									" for " + best_sympathy_offer1  + "(" +  best_sympathy_offer1_idle  + "). New utility : " + this.current_utility + ".");
    							
    							//this.restartAverageIdleness(old_positions);
    							//this.mountPriorityQueue();
    							//this.myPriorityQueueIndex = 0;
    						}
    						else if(best_sympathy_offer2 != null){
    							String[] winner_bid = {best_sympathy_offer2};
    							String[] bids_str = {bids.get(1), Integer.toString((int) this.estimatedIdleness(bids.get(1)))};
    							ComplexBid troc = new ComplexBid(winner_bid, bids_str, null, null);
    							act = new SpeechAct(this.myCurrentTransaction, SpeechActPerformative.ACCEPT, this.agent_id, buyer, troc);
    							this.SendSpeechAct(act);
    							
    							this.exchanges++ ;
    							exchange_done = true;
    							
    							this.removeFromMyNodes(best_proposition2);
    							//LinkedList<String> old_positions = this.getMyNodes();
    							this.addToMyNodes(best_sympathy_offer2, best_sympathy_offer2_idle);
    							this.OrderMyNodes();
    							this.current_utility = this.CalculateUtility(this.getMyNodes());
    							
    							System.out.println("Agent " + this.agent_id + " trans #" + reject.getTransactionId() +  " : exchanging by sympathy second " + best_proposition2 +
    									" for " + best_sympathy_offer2  + "(" +  best_sympathy_offer2_idle  + "). New utility : " + this.current_utility + ".");
    							
    							//this.restartAverageIdleness(old_positions);
    							//this.mountPriorityQueue();
    							//this.myPriorityQueueIndex = 0;
    						}
    						else if(special_sympathy_offer != null){
    							String[] winner_bid = {special_sympathy_offer};
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
    							//LinkedList<String> old_positions = this.getMyNodes();
    							this.addToMyNodes(special_sympathy_offer, special_sympathy_offer_idle);
    							
    							this.OrderMyNodes();
    							this.current_utility = this.CalculateUtility(this.getMyNodes());
    							
    							System.out.println("Agent " + this.agent_id + " trans #" + reject.getTransactionId() +  " : exchanging by sympathy special " + best_proposition1 + " and " + best_proposition2 +
    									" for " + special_sympathy_offer  + "(" +  special_sympathy_offer_idle  + "). New utility : " + this.current_utility + ".");
    							
    							//this.restartAverageIdleness(old_positions);
    							//this.mountPriorityQueue();
    							//this.myPriorityQueueIndex = 0;
    						}
    						
    					}
    	    		}
    	    		
    	    	}
    			
    			
    			
    		}
    		else {
    			/**
    			 * There was no exchange possible. is there an agent that can unburden me ?
    			 */
				double min_utility = this.current_utility;			
				int min_index = -1;
				int type = 0;
				
    			for(int i = 0; i < proposed_transfers.size(); i++){
    				double utility_1 = -1, utility_2 = -1, utility_both = -1;
    				current_bid = proposed_transfers.get(i);
    				
    				if(current_bid.getBidsForFirst() != null)
    					utility_1 = Double.valueOf(current_bid.getBidsForFirst()[0]);
    				if(current_bid.getBidsForSecond() != null)
    					utility_2 = Double.valueOf(current_bid.getBidsForSecond()[0]);
    				if(current_bid.getBidsForBoth() != null)
    					utility_both = Double.valueOf(current_bid.getBidsForBoth()[0]);
    				
    				if(utility_1 != -1 && utility_1 < min_utility){
    					min_utility = utility_1;
    					type = 1;
    					min_index = i;
    				}
    				if(utility_2 != -1 && utility_2 < min_utility){
    					min_utility = utility_2;
    					type = 2;
    					min_index = i;
    				}
    				if(utility_both != -1 && utility_both < min_utility){
    					min_utility = utility_both;
    					type = 3;
    					min_index = i;
    				}    				
    			}
    			
    			if(min_index != -1){
    				/**
    				 * There is an agent that will unburden me...
    				 */
    				SpeechAct act;
        			
        			if(FBAbuyers_exchanges != null && FBAbuyers_exchanges.size() > 0){
        	    		for(int i = 0; i < FBAbuyers_exchanges.size(); i++){
        	    			String buyer = FBAbuyers_exchanges.get(i);
        						reject.setReceiver(buyer);
        						this.SendSpeechAct(reject);
        				}
        			}
        	    	
        	    	if(proposed_transfers != null && proposed_transfers.size() > 0){
        	    		for(int i = 0; i < proposed_transfers.size(); i++){
        	    			String buyer = FBAbuyers_transfers.get(i);
        					if(i != min_index){
        						reject.setReceiver(buyer);
        						this.SendSpeechAct(reject);
        					}
        					else {
        						if(type == 1){
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
        							
        							System.out.println("Agent " + this.agent_id + " trans #" + reject.getTransactionId() + " : giving first" + best_proposition1 +
        									". New utility : " + this.current_utility + ".");
        							
        							//this.restartAverageIdleness(old_positions);
        							//this.mountPriorityQueue();
        							//this.myPriorityQueueIndex = 0;
        						}
        						else if(type == 2){
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
        							
        							//this.restartAverageIdleness(old_positions);
        							//this.mountPriorityQueue();
        							//this.myPriorityQueueIndex = 0;
        						}
        						else if(type == 3){
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
        							
        							//this.restartAverageIdleness(old_positions);
        							//this.mountPriorityQueue();
        							//this.myPriorityQueueIndex = 0;
        						}
        						
        					}
        	    		}
        	    		
        	    	}
    			}
    			else {
    				/**
    				 * there is absolutely no interesting move to make
    				 */
    				
    				if(this.FBABuyers != null && this.FBABuyers.size() > 0){
        	    		for(int i = 0; i < this.FBABuyers.size(); i++){
        	    			String buyer = this.FBABuyers.get(i);
        					reject.setReceiver(buyer);
        					this.SendSpeechAct(reject);
        				}
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
		if(this.current_position == null)
			return false;
		return true;
	}

	@Override
	protected SpeechAct enter_Message(int id) {
		LinkedList<String> my_position = new LinkedList<String>();
		my_position.add(this.current_position.STRING);
		return new SpeechAct(id, SpeechActPerformative.ENTER, this.agent_id, "all_agents", my_position, 1);
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
		
		int maxnb_nodes_to_send = this.myNodes.size() / 2;
		
		String agent_position = entering_act.getPropositions().getFirst();
		
		LinkedList<StringAndDouble> costlist = new LinkedList<StringAndDouble>();
		
		StringAndDouble current;
		double path_utility, path_gain;
		LinkedList<String> pathlist = new LinkedList<String>();
		

		for(int j = 0; j < this.myNodes.size(); j++){
			current = this.myNodes.get(j);
			if(!this.isNodeEngaged(current.STRING))
				if(this.graph.getDistance(this.graph.getNode(agent_position), this.graph.getNode(current.STRING)) <= this.MAX_DIST){
					pathlist = this.getMyNodes();
					pathlist.remove(current.STRING);
					path_utility = this.CalculateUtility(this.path_calculator.GetShortestPath(graph, pathlist));
					
					path_gain = (this.current_utility - path_utility);
	
					costlist.add(new StringAndDouble(current.STRING, path_gain));
				}
		}
		
		LinkedList<StringAndDouble> ordered_costlist = new LinkedList<StringAndDouble>();
		while(costlist.size() > 0){
			double cost_max = - Double.MAX_VALUE;
			int index = -1;
			for(int j = 0; j < costlist.size(); j++)
				if(costlist.get(j).double_value > cost_max){
					cost_max = costlist.get(j).double_value;
					index = j;
				}
			
			if(index != -1){
				ordered_costlist.add(costlist.get(index));
				costlist.remove(index);
			}
		}
		
		int nb_nodes_to_send = Math.min(maxnb_nodes_to_send, ordered_costlist.size());
		String[] nodes_to_send = new String[2*nb_nodes_to_send];
		for(int i = 0; i < nb_nodes_to_send; i++){
			nodes_to_send[2*i] = ordered_costlist.get(i).STRING;
			nodes_to_send[2*i + 1] = String.valueOf((int)this.estimatedIdleness(ordered_costlist.get(i).STRING));
			//this.removeFromMyNodes(ordered_costlist.get(i).STRING);
		}
		
		ComplexBid bid = null;
		if(nodes_to_send.length != 0){
			bid = new ComplexBid(nodes_to_send, null, null, null);
		}
		else {
			double min_dist = Double.MAX_VALUE;
			int index = -1;
			for(int i = 0; i < this.myNodes.size(); i++){
				if(!this.isNodeEngaged(this.myNodes.get(i).STRING)){
					double dist = this.graph.getDistance(this.graph.getNode(agent_position), this.graph.getNode(this.myNodes.get(i).STRING));
					if( dist < min_dist){
						index = i;
						min_dist = dist;
					}
				}
			}
			
			if(index != -1){
				String[] nodes = new String[]{this.myNodes.get(index).STRING, String.valueOf((int)this.estimatedIdleness(this.myNodes.get(index).STRING))};
				bid = new ComplexBid(null, nodes, null, null);
			}
		}
		
		if(bid == null)
			bid = new ComplexBid(null, null, null, null);
		
		SpeechAct act = new SpeechAct(entering_act.getTransactionId(), SpeechActPerformative.PROPOSE, 
																	this.agent_id, entering_act.getSender(), bid);
		
		this.plan.clear();
		
		return act;
	}

	@Override
	protected void manage_Enter_Protocol() {
		if(this.ComplexPropositions != null && this.ComplexPropositions.size()>0){
			LinkedList<StringAndDouble> second_choices = new LinkedList<StringAndDouble>();
			LinkedList<String> second_choices_agents = new LinkedList<String>();
			
			for(int i = 0; i < this.ComplexPropositions.size(); i++){
				String[] nodesToAdd = this.ComplexPropositions.get(i).getBidsForFirst();
				if(nodesToAdd != null && nodesToAdd.length > 0){
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
					
					System.out.println("Enter " + this.agent_id + ": from " + this.FBABuyers.get(i) + " : " + added_nodes);
				}
				else if(this.ComplexPropositions.get(i).getBidsForSecond() != null){
					String[] nodes = this.ComplexPropositions.get(i).getBidsForSecond();
					second_choices.add(new StringAndDouble(nodes[0], Double.valueOf(nodes[1])));
					second_choices_agents.add(this.FBABuyers.get(i));
				}
				else {
					SpeechAct act = new SpeechAct(this.myCurrentTransaction, SpeechActPerformative.ACCEPT, 
							this.agent_id, this.FBABuyers.get(i));
					this.SendSpeechAct(act);
				}
			}
			
			while(this.myNodes.size() < 4 && second_choices_agents.size() > 0){
				double min_utility = Double.MAX_VALUE;
				int index = -1;
				for(int i = 0; i < second_choices.size(); i++){
					LinkedList<String> pathlist = this.getMyNodes();
					pathlist.add(second_choices.get(i).STRING);
					double path_utility = this.CalculateUtility(this.path_calculator.GetShortestPath(graph, pathlist));
					if(path_utility < min_utility){
						min_utility = path_utility;
						index = i;
					}
				}
				if(index != -1){
					this.addToMyNodes(second_choices.get(index).STRING, Double.valueOf(second_choices.get(index).double_value));
					SpeechAct act = new SpeechAct(this.myCurrentTransaction, SpeechActPerformative.ACCEPT, 
							this.agent_id, second_choices_agents.get(index));
					this.SendSpeechAct(act);
					
					System.out.println("Enter " + this.agent_id + ": from " + second_choices_agents.get(index) + " : " + second_choices.get(index).STRING);
					
					second_choices.remove(index);
					second_choices_agents.remove(index);
				}	
			}
			
			for(int i = 0; i < second_choices_agents.size(); i++){
				SpeechAct act = new SpeechAct(this.myCurrentTransaction, SpeechActPerformative.REJECT, 
						this.agent_id, second_choices_agents.get(i));
				this.SendSpeechAct(act);
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
		String[] nodes = new String[this.myNodes.size()];
		for(int i = 0; i < this.myNodes.size(); i++)
			nodes[i] = this.getMyNodes(i);
		
		ComplexBid bid = new ComplexBid(nodes, null, null, null);
		SpeechAct act = new SpeechAct(id, SpeechActPerformative.QUIT, this.agent_id, "all_agents", bid);
		
		return act;
	}

	@Override
	protected SpeechAct manage_Quit_Message(SpeechAct quitting_act) {
		String[] proposed_nodes = quitting_act.getComplexBid().getBidsForFirst();
		
		double utility;
		LinkedList<StringAndDouble> utility_list = new LinkedList<StringAndDouble>();
		
		
		LinkedList<String> nodes = new LinkedList<String>();
		for(int j = 0; j < this.myNodes.size(); j++)
			nodes.add(this.getMyNodes(j));
		
		for(int i = 0; i < proposed_nodes.length; i++){
			if(this.get_minDistance_to_nodes(proposed_nodes[i], nodes) < this.MAX_DIST){
				nodes.add(proposed_nodes[i]);
				utility = this.CalculateUtility(this.path_calculator.GetShortestPath(graph, nodes));
				utility_list.add(new StringAndDouble(proposed_nodes[i], utility - this.current_utility));
				nodes.remove(proposed_nodes[i]);
			}
		}
		
		LinkedList<StringAndDouble> ordered_utility_list = new LinkedList<StringAndDouble>();
		while(utility_list.size() > 0){
			double cost_min = Double.MAX_VALUE;
			int index = -1;
			for(int j = 0; j < utility_list.size(); j++)
				if(utility_list.get(j).double_value < cost_min){
					cost_min = utility_list.get(j).double_value;
					index = j;
				}
			
			if(index != -1){
				ordered_utility_list.add(utility_list.get(index));
				utility_list.remove(index);
			}
		}
		
		String[] bid = new String[2 * ordered_utility_list.size()];
		for(int k = 0; k < ordered_utility_list.size(); k++){
			bid[2*k] = ordered_utility_list.get(k).STRING;
			bid[2*k + 1] = String.valueOf(ordered_utility_list.get(k).double_value);
		}
		
		ComplexBid mybid = new ComplexBid(bid, null, null, null);
		
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
		StringAndDouble[][] nodes_list = new StringAndDouble[agents.length][];
		
    	int[] nb_classified_node_by_agent = new int[agents.length];
    	for(int i = 0; i < agents.length; i++){
    		agents[i] = this.FBABuyers.get(i);

    		String[] nodes = this.ComplexPropositions.get(i).getBidsForFirst();
    		nodes_list[i] = new StringAndDouble[nodes.length / 2];
    		for(int k = 0; k < nodes_list[i].length; k++)
    			nodes_list[i][k] = new StringAndDouble(nodes[2*k], Double.valueOf(nodes[2*k + 1]));

    		nb_classified_node_by_agent[i] = nodes.length / 2;

    	}  	
    	
    	int[] current_index = new int[agents.length];
    	String[][] distributed_nodes = new String[agents.length][this.myNodes.size()];
    	int[] current_distributed_index = new int[agents.length];
    	LinkedList<String> mynodes_copy = this.getMyNodes();
    	boolean done = false;
    	// on attribue selon les préférences
    	while(!done){
    		int index = -1;
    		double cost_min = Double.MAX_VALUE;
    		
    		for(int i = 0; i < nodes_list.length; i++){
    			if(nodes_list[i] != null){
		    		while((current_index[i] < nodes_list[i].length) && 
		    				(mynodes_copy.indexOf(nodes_list[i][current_index[i]].STRING) == -1 ))
		    			current_index[i]++;
		    				
		    		if(current_index[i] < nodes_list[i].length)
		    			if(nodes_list[i][current_index[i]].double_value < cost_min){
		    				cost_min = nodes_list[i][current_index[i]].double_value;
		    				index = i;
		    			}		
    			}
    		}
    		
    		if(index != -1){
	    		distributed_nodes[index][current_distributed_index[index]] = nodes_list[index][current_index[index]].STRING;
	    		mynodes_copy.remove(nodes_list[index][current_index[index]].STRING);
	    		current_distributed_index[index]++;
	    		current_index[index]++;
    		}
    		
    		
    		if(mynodes_copy.size() == 0)
    			break;
    		done = true;
    		for(int i = 0; i < agents.length; i++)
    			done &= (current_index[i] >= nb_classified_node_by_agent[i]);
    	}
    	
    	// les préférences étant incomplètes, on attribue les noeuds qui restent par proximité
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
    	}
    	
    	
    	for(int j = 0; j < agents.length; j++){
    		String[] nodes_to_send = new String[2 * current_distributed_index[j]];
    		for(int k = 0; k < current_distributed_index[j]; k++){
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
