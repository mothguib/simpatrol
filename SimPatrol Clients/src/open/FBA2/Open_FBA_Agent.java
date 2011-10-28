package open.FBA2;

import java.io.IOException;
import java.util.LinkedList;

import closed.FBA.ComplexBid;
import closed.FBA.SpeechAct;
import closed.FBA.SpeechActPerformative;
import closed.FBA.TransactionNodes.TransactionTypes;
import closed.FBA2.FlexibleBidder2Agent;
import closed.FBA2.TransactionNodes2;

import util.StringAndDouble;
import util.graph.Edge;
import util.graph.Graph;

/**
 * Open Flexible Bidder Agent
 * 
 * This agent is based on the FBA2.FlexibleBidder2Agent class
 * 
 * it is designed to cope with the open setting system, by being able to enter or leave the active society,
 * and cope with the entry or leave mechanisms of other agents.
 * 
 * It is able to handle multiple simultaneous entry/leave events.
 * 
 * The entry and leave mechanisms are not implemented here, to allow the easy implementation of different mechanisms.
 * 
 * 
 * IMPORTANT : it is very important that the agent is instanciated with the right number of agents in the system when it 
 * activatesas argument. See the clients for an example of how to do that.
 * 
 * @author pouletc
 * @date 10/2011
 */

public abstract class Open_FBA_Agent extends FlexibleBidder2Agent {

	/** variables linked to the other agents $
	 * 
	 * The classic cycle is to add an agent in the entering_agents when receiving an ENTER message,
	 * and transfer hit in the known_agents at the end of the ENTER transaction.
	 * An agent is transferred in the quitting_agents when receiving the QUIT message, and removed from every list
	 * when the QUIT transaction finishes
	 * 
	 * At the beginning of the simulation, agents are detected when participating to auctions and put in known_agents
	 **/
	// known active agents
	protected LinkedList<String> known_agents = new LinkedList<String>();
	// known quitting agents
	protected LinkedList<String> quitting_agents = new LinkedList<String>();
	// known entering agents
	protected LinkedList<String> entering_agents = new LinkedList<String>();
	// counts the current transitions. Agents are not allowed to trade during ENTER or QUIT event, and must REFUSE or REJECT every proposition 
	// while transition_counter!=0
	protected int transition_counter = 0;
	
	
	/** variables linked to my own activation/deactivation mechanisms **/
	protected boolean activate_message_sent = false;
	protected boolean enter_message_sent = false;
	protected boolean quit_message_sent = false;
	
	/** variables linked to my transactions **/
	// agents active in the system when I send an INFORM message, and from which I expect an answer
	protected LinkedList<String> awaited_props = new LinkedList<String>();
	// answers received to my INFORM message
	protected LinkedList<String> received_props = new LinkedList<String>();	
	
	// used only during the ENTER phase, when I don't know which agents are likely to answer
	protected int nb_answer_awaited = -1;
	
	// period at which the agent consider that the transaction it's keeping track of is too old, and assumes
	// it has missed the closure of the transaction. At this time, the nodes associated are made available again
	protected int transaction_too_old = 200;
	
	
	
	public Open_FBA_Agent(String id, double entering_time, double quitting_time, 
			int number_of_agents, LinkedList<String> nodes,
			double idleness_rate_for_path, double idleness_rate_for_auction,
			String society_id) {
		super(id, entering_time, quitting_time, society_id, number_of_agents, nodes, idleness_rate_for_path, idleness_rate_for_auction);

		if(this.myNodes.size() == 0)
			this.nb_min_nodes = 2;

		received_messages = new LinkedList<String>();
		FBABuyers = new LinkedList<String>();
		ComplexPropositions = new LinkedList<ComplexBid>();

	}

	public Open_FBA_Agent(String id, int number_of_agents, LinkedList<String> nodes,
			double idleness_rate_for_path, double idleness_rate_for_auction) {
		super(id, number_of_agents, nodes, idleness_rate_for_path, idleness_rate_for_auction);

	}
	
	
	
	/**
     * Manages the messages received
     * It is the same function as the FlexibleBidderAgent2
     * 
     * @param perceptions
     * 					The received perceptions
     */
    protected void ManageAnswers(String[] perceptions){
		SpeechAct received_act, response_act;
		
		for (int i = 0; i < perceptions.length; i++) {
			if (perceptions[i].indexOf("<perception type=\"3\"") > -1) {
				String message = perceptions[i].substring(perceptions[i].indexOf("message=\"")+ 9, perceptions[i].lastIndexOf("\"/>"));
				received_act = SpeechAct.fromString(message);				
				if(received_act.getTransactionId() == this.myCurrentTransaction && received_act.getReceiver().equals(this.agent_id)){
					// the message is for me : it's an answer to my own auction
					this.negociate(received_act);
					received_answers++;
				}
				else if(received_act.getReceiver().equals(this.agent_id) || received_act.getReceiver().equals("all_agents")){
					received_act.setReceiver(this.agent_id);
					response_act = this.negociate(received_act);
					if(response_act != null)
						SendSpeechAct(response_act);
				}
				
			}
		}
    }
    
    
    
    /****
	 *  Takes into account the received speechAct and calculates the appropriate answer :
	 *  	- A trade proposition for an INFORM act
	 *  	- nothing for a PROPOSE act (but the propositions are added to those who need to be tested
	 * 		- the exchange is realized in case of an ACCEPT act
	 * 
	 * 	It also takes into account the ENTER and QUIT messages, manages the status of known agents and keeps track of the nodes proposed
	 * 
	 *  
	 * @param act the received SpeechAct
	 * @return the SpeechAct to send to the sender of the treated SpeechAct
	 */
	protected SpeechAct negociate(SpeechAct act){
		ComplexBid proposition, bid;
		SpeechAct answer;
		
		switch(act.getPerformative()){
		case INFORM :
			// an agent cannot participate in a new auction if it is entering or quitting the system
			if(this.engaging_in || this.engaging_out)
				return null;
			
			// keeping the status of the known agents
			if(!this.known_agents.contains(act.getSender())){
				if(!this.entering_agents.contains(act.getSender()) && !this.quitting_agents.contains(act.getSender())){
					this.known_agents.add(act.getSender());
					if(this.agents_num - 1 < this.known_agents.size())
						this.agents_num++;
				}
				else if(this.entering_agents.contains(act.getSender())){
					this.known_agents.add(act.getSender());
					this.entering_agents.remove(act.getSender());
					this.agents_num++;
					this.transition_counter = Math.max(0, this.transition_counter - 1);
				}
				else {
					this.known_agents.add(act.getSender());
					this.quitting_agents.remove(act.getSender());
					this.agents_num++;
					this.transition_counter = Math.max(0, this.transition_counter - 1);
				}
			}
			
			// if there is a transition, refuse
			if(this.transition_counter != 0){
				answer = new SpeechAct(act.getTransactionId(), SpeechActPerformative.REFUSE, this.agent_id, act.getSender(), act.getPropositions(), 1);
				return answer;
			}
			else {
				// check that there is a proposition
				proposition = this.emitProposition(act.getPropositions());
				// if there is something to propose
				if((proposition.getBidsForFirst() != null && proposition.getBidsForFirst().length > 0)
						|| (proposition.getBidsForSecond() != null && proposition.getBidsForSecond().length > 0)
						|| (proposition.getBidsForBoth() != null && proposition.getBidsForBoth().length > 0)
						|| (proposition.getDoubleBidsForBoth() != null && proposition.getDoubleBidsForBoth().size() > 0)){
					answer = new SpeechAct(act.getTransactionId(), SpeechActPerformative.PROPOSE, this.agent_id, act.getSender(), proposition);
					
					TransactionNodes2 mytrans = new TransactionNodes2(TransactionTypes.BID, act.getTransactionId(), (int)(this.time), act.getPropositions(), proposition);
					this.engaged_transactions.add(mytrans);
					this.nb_engaged_nodes = this.NbEngagedNodes();
				} else { //there is nothing to propose
					answer = new SpeechAct(act.getTransactionId(), SpeechActPerformative.REFUSE, this.agent_id, act.getSender(), act.getPropositions(), 1);
				}
				break;
			}
			
		case REJECT :
			
			TransactionNodes2 mytrans = this.getEngagedTransacFromId(act.getTransactionId());
			if(mytrans != null){
				// if nodes were engaged, free them for the next auctions 
				this.engaged_transactions.remove(mytrans);
				this.nb_engaged_nodes = this.NbEngagedNodes();
				
				// if it was an ENTER or QUIT transaction, change the status of the sender and count the transitions
				if(mytrans.Transaction_type == TransactionTypes.ENTER){
					this.entering_agents.remove(act.getSender());
					this.known_agents.add(act.getSender());
					this.transition_counter = Math.max(0, this.transition_counter - 1);
					this.agents_num++;
				}
				if(mytrans.Transaction_type == TransactionTypes.QUIT){
					this.quitting_agents.remove(act.getSender());
					if(this.awaited_props.contains(act.getSender()))
						this.awaited_props.remove(act.getSender());
					this.transition_counter = Math.max(0, this.transition_counter - 1);
					this.agents_num--;
				}

			}
			
			return null;
			
		case PROPOSE :
			// keeping the status of the known agents
			if(!this.known_agents.contains(act.getSender())){
				if(!this.entering_agents.contains(act.getSender()) && !this.quitting_agents.contains(act.getSender())){
					this.known_agents.add(act.getSender());
					if(this.agents_num - 1 < this.known_agents.size())
						this.agents_num++;
				}
				else if(this.entering_agents.contains(act.getSender())){
					this.known_agents.add(act.getSender());
					this.entering_agents.remove(act.getSender());
					this.agents_num++;
					this.transition_counter = Math.max(0, this.transition_counter - 1);
				}
				else {
					this.known_agents.add(act.getSender());
					this.quitting_agents.remove(act.getSender());
					this.agents_num++;
					this.transition_counter = Math.max(0, this.transition_counter - 1);
				}
			}
			// if there is a transition, refuse
			if(act.getTransactionId() != this.myCurrentTransaction){
				answer = new SpeechAct(this.myCurrentTransaction, SpeechActPerformative.REJECT, this.agent_id, act.getSender(), new LinkedList<String>(), 0);
				return answer;
			}
			// Add the proposition to the list of propositions to take into account
			if(FBABuyers == null)
				FBABuyers = new LinkedList<String>();
			FBABuyers.add(act.getSender());
			bid = act.getComplexBid();
			if(ComplexPropositions == null)
				ComplexPropositions = new LinkedList<ComplexBid>();
			if(bid != null)
				ComplexPropositions.add(bid);
			
			this.received_props.add(act.getSender());
			return null;
			
		case ACCEPT :
			TransactionNodes2 mytrans2 = this.getEngagedTransacFromId(act.getTransactionId());
			if(mytrans2.Transaction_type == TransactionTypes.BID){
			// Since the exchange has been accepted, we swap the exchanged nodes
			// for those given by the other part
				proposition = act.getComplexBid();
				LinkedList<String> NodeToDelete = new LinkedList<String>();
				LinkedList<String> NodeToAdd = new LinkedList<String>();
				
				if(proposition.getBidsForFirst() != null && proposition.getBidsForFirst().length > 0)
					for(int i = 0; i < proposition.getBidsForFirst().length; i++)
						NodeToDelete.add(proposition.getBidsForFirst()[i]);
					
				if(proposition.getBidsForSecond() != null && proposition.getBidsForSecond().length > 0)
					for(int i = 0; i < proposition.getBidsForSecond().length; i++)
						NodeToAdd.add(proposition.getBidsForSecond()[i]);
					
				for(int i = 0; i < NodeToDelete.size(); i++){
					this.removeFromMyNodes(NodeToDelete.get(i));
					this.exchanges++;		
				}
					
				for(int i = 0; i < NodeToAdd.size(); i += 2){
					this.addToMyNodes(NodeToAdd.get(i), Integer.valueOf(NodeToAdd.get(i+1)));
				}
					
				this.engaged_transactions.remove(mytrans2);
				this.nb_engaged_nodes = this.NbEngagedNodes();
					
				String message = "Agent " + this.agent_id + " : exchanging ACCEPT #" + act.getTransactionId() + " ";
				for(String bla : NodeToDelete)
					message += bla + " and ";
				message = message.substring(0, message.length() - 4) + " for ";
				for(int i = 0; i < NodeToAdd.size(); i+=2)
					message += NodeToAdd.get(i) + "(" +  NodeToAdd.get(i+1) + ") and ";
				message = message.substring(0, message.length() - 4) + ".";
				System.out.println(message);
			
			}
			else if(mytrans2.Transaction_type == TransactionTypes.QUIT){
				// we add the nodes sent by the quitting agent to our own list of nodes, and get the agent out of our list of agents
				proposition = act.getComplexBid();
				LinkedList<String> NodeToAdd = new LinkedList<String>();
					
				if(proposition.getBidsForSecond() != null && proposition.getBidsForSecond().length > 0)
					for(int i = 0; i < proposition.getBidsForSecond().length; i++)
						NodeToAdd.add(proposition.getBidsForSecond()[i]);
					
				for(int i = 0; i < NodeToAdd.size(); i += 2){
					this.addToMyNodes(NodeToAdd.get(i), Integer.valueOf(NodeToAdd.get(i+1)));
				}
					
				this.engaged_transactions.remove(mytrans2);
				this.nb_engaged_nodes = this.NbEngagedNodes();
				
				String message = "Agent " + this.agent_id + " : reorganising #" + act.getTransactionId() + " with : ";
				for(int i = 0; i < NodeToAdd.size(); i+=2)
					message += NodeToAdd.get(i) + "(" +  NodeToAdd.get(i+1) + ") and ";
				message = message.substring(0, message.length() - 4) + ".";
				System.out.println(message);
				
				this.quitting_agents.remove(act.getSender());
				if(this.awaited_props.contains(act.getSender()))
					this.awaited_props.remove(act.getSender());
				this.transition_counter = Math.max(0, this.transition_counter - 1);
				this.agents_num--;
			}
			else {
				//we remove the nodes sent to the entering agent, and change its status
				this.entering_agents.remove(act.getSender());
				this.known_agents.add(act.getSender());
				this.transition_counter = Math.max(0, this.transition_counter - 1);
				this.agents_num++;
				
				for(int i = 0; i < mytrans2.out_nodes.length; i++)
					this.removeFromMyNodes(mytrans2.out_nodes[i]);
				
				this.engaged_transactions.remove(mytrans2);
				this.nb_engaged_nodes = this.NbEngagedNodes();
				
			}
			// the list of watched nodes has changed, so we reorganize it
			if(this.myNodes.size() != 0){
				this.OrderMyNodes();
				this.visit_cost = this.CalculateMyPathCost();
				System.out.println("Agent " + this.agent_id + ", new visit cost : " + this.visit_cost);
			}
			return null;
			
		case REFUSE :
			// keeping the status of the known agents
			if(!this.known_agents.contains(act.getSender())){
				if(!this.entering_agents.contains(act.getSender()) && !this.quitting_agents.contains(act.getSender())){
					this.known_agents.add(act.getSender());
					if(this.agents_num - 1 < this.known_agents.size())
						this.agents_num++;
				}
				else if(this.entering_agents.contains(act.getSender())){
					this.known_agents.add(act.getSender());
					this.entering_agents.remove(act.getSender());
					this.agents_num++;
					this.transition_counter = Math.max(0, this.transition_counter - 1);
				}
				else {
					this.known_agents.add(act.getSender());
					this.quitting_agents.remove(act.getSender());
					this.agents_num++;
					this.transition_counter = Math.max(0, this.transition_counter - 1);
				}
			}
			// the proposition has been refused but we keep a trace of the answer
			if(act.getTransactionId() == this.myCurrentTransaction)
				this.received_props.add(act.getSender());
			return null;
			
		case ENTER :
			this.entering_agents.add(act.getSender());
			
			// an agent cannot participate in an ENTER mechanism if it is itself entering or quitting
			if(this.engaging_out || this.engaging_in)
				return null;
			
			this.transition_counter++;
			
			answer = manage_Enter_Message(act);
			mytrans = new TransactionNodes2(TransactionTypes.ENTER, act.getTransactionId(), (int)(this.time), act.getPropositions(), answer.getComplexBid());
			this.engaged_transactions.add(mytrans);
			
			break;
			
		case QUIT :
			this.quitting_agents.add(act.getSender());
			this.known_agents.remove(act.getSender());

			// an agent cannot participate in a QUIT mechanism if it is itself entering or quitting
			if(this.engaging_in || this.engaging_out)
				return null;
			
			this.transition_counter++;

			answer = manage_Quit_Message(act);
			mytrans = new TransactionNodes2(TransactionTypes.QUIT, act.getTransactionId(), (int)(this.time), act.getPropositions(), answer.getComplexBid());
			this.engaged_transactions.add(mytrans);
			break;
			
		default :
			// failure in communication
			answer = new SpeechAct(act.getTransactionId(), SpeechActPerformative.NOT_UNDERSTOOD, this.agent_id, 
										act.getSender(), act.getContainedNodes(), 0);
			
		}
		
		return answer;
		
	}
	
	
	/**
     * Processes the received answers
     *
     * It reject every offers if there is a transition going at that time.
     * If there is no transition going on, it reject all propositions from agents not in the know_agents list (**), then call the super function
     * 
     * ** this can happen if an agent answered with a proposition, then had the time to 
     * complete the whole QUIT process BEFORE we process that proposition. It is important then to remove this proposition from eligible propositions
     * 
     */
    protected void ProcessAnswers(){
    	if(this.transition_counter != 0){
    		SpeechAct reject = new SpeechAct(this.myCurrentTransaction, SpeechActPerformative.REJECT, this.agent_id, "", new LinkedList<String>(), 0);
    		if(this.FBABuyers != null)
	    		for(String buyer : this.FBABuyers){
	    			reject.setReceiver(buyer);
					this.SendSpeechAct(reject);
	    		}
    		
        	this.cycles_without_exchange++;  	
        	this.best_proposition1 = null;
    		this.best_proposition2 = null;
    		this.nb_engaged_nodes = this.NbEngagedNodes();
    		
        	if(FBABuyers != null)
        		FBABuyers.clear();
        	if(ComplexPropositions != null)
        		ComplexPropositions.clear();
        	
        	this.best_offer1 = null;
        	this.best_offer1_idle = 0;
        	this.best_offer2 = null;
        	this.best_offer2_idle = 0;
        	this.special_offer = null;
        	this.special_offer_idle = 0;
        	this.best_buyer = -1;
        	
        	this.myCurrentTransaction = -1;
        	this.received_answers = 0;
    		
    	}
    	else {
    		for(int i = this.FBABuyers.size() - 1; i > 0; i--)
    			if(!this.known_agents.contains(this.FBABuyers.get(i))){
    				SpeechAct reject = new SpeechAct(this.myCurrentTransaction, SpeechActPerformative.REJECT, this.agent_id, FBABuyers.get(i), new LinkedList<String>(), 0);
    				this.SendSpeechAct(reject);
    		    	
    				this.FBABuyers.remove(i);
    				this.ComplexPropositions.remove(i);
    			}
    		
    		super.ProcessAnswers();
    	}
    }
	
    
    /**
     * Sends the given speechAct. It makes a difference between a broadcast and a simple message
     * 
     * @param act : the SpeechAct to send
     * @throws IOException
     */
    protected void SendSpeechAct(SpeechAct act){
    	String act_str = act.toString();
    	
    	if(act.getReceiver().equals("all_agents"))
    		this.BroadcastMessage(act_str);
    	else
    		this.SendMessage(act_str, act.getReceiver());		
    }
   
    
    /************************************************************************************
     ************************************************************************************
     * 																				    *
     * 			Management of the ENTER protocol             						    *
     *														 						    *
     * The process is :																	*
     * 		- I send an ENTER message with some kind of information						*
     *      - Others send PROPOSE messages with lists of nodes for me					*
     *      - I choose the nodes I want to watch and send ACCEPT/REJECT accordingly 	*
     * 																				    *
     ************************************************************************************
     ************************************************************************************/
    
    /**
     * returns true if the enter protocol can be launched
     */
    protected abstract boolean enterCondition();
    
    /**
     * Message to send to the other agents to signal the beginning of the enter protocol
     * It must use the SpeechActPerformative.ENTER
     */
    protected abstract SpeechAct enter_Message(int id);
    
    /**
     * manage the answer to an ENTER message
     * It must be a SpeechActPerformative.PROPOSE    (no REFUSE, or it wont be correctly processed. If needed, use an empty proposition)
     * and some of the agents have to propose nodes for the new agent
     * 
     * @param entering_act : the ENTER message
     * @return the message to send back
     */
    protected abstract SpeechAct manage_Enter_Message(SpeechAct entering_act);
    
    /**
     * manages the PROPOSE messages 
     * it must select the nodes to keep between the propositions, and send ACCEPT or REJECT messages accordingly
     */
    protected abstract void manage_Enter_Protocol();
    
    
    /************************************************************************************
     ************************************************************************************
     * 																				    *
     * 			Management of the QUIT protocol             						    *
     *														 						    *
     * The process is :																	*
     * 		- I send an QUIT message with (usually) information on the nodes I need to 	*
     * 			distribute																*
     *      - Others send PROPOSE messages to signal that they are available to receive	* 
     *      nodes, and for example some lists of nodes they want						*
     *      - I distribute the nodes to the other agents and send ACCEPT or REJECT 		*
     *     messages accordingly 														*
     * 																				    *
     * Be sure to check that every PROPOSE is still valid (see ProcessAnswers()) before *
     * distributing the nodes															*
     * The manage_Quit_Protocol function MUST put this.inactive to true					*
     * 																					*
     ************************************************************************************
     ************************************************************************************/
    
    /**
     * Message to send to the other agents to signal the beginning of the quit protocol
     * It must use the SpeechActPerformative.QUIT
     */
    protected abstract SpeechAct quit_Message(int id);
    
    /**
     * manage the answer to an QUIT message
     * It must be a SpeechActPerformative.PROPOSE    (no REFUSE, or it wont be correctly processed. If needed, use an empty proposition)
     * 
     * @param quitting_act : the QUIT message
     * @return the message to send back
     */
    protected abstract SpeechAct manage_Quit_Message(SpeechAct quitting_act); 
    
    /**
     * manages the PROPOSE messages 
     * it must distribute ALL the nodes to the agents, and send ACCEPT or REJECT messages accordingly
     * 
     * Be sure to check that every PROPOSE is still valid (see ProcessAnswers()) before distributing the nodes
     * The manage_Quit_Protocol function MUST put this.inactive to true	
     */
    protected abstract void manage_Quit_Protocol();
     
    
    
    /**
     * manage the movement and its planification
     */
    protected void PlanAndMove(){
    	/**
		 * update idlenesses
		 */
		this.updateAllIdleness(length_of_last_edge);
		
		
		/**
		 *  choose where to move next when on a node
		 */
		
		int position_in_plan = plan.indexOf(current_position.STRING);
		
		if(position_in_plan > -1)
			for(int i = 0; i <= position_in_plan; i++)
				plan.poll();
			
		if(plan.size() > 0){
			try {
				this.visitCurrentNode(current_position.STRING);
				this.GoTo(plan.get(0));
				Edge edge = this.graph.getEdge(this.graph.getNode(current_position.STRING), this.graph.getNode(plan.get(0)));
				length_of_last_edge = 1 + Math.ceil(edge.getLength());
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		else {
			/**
			 * There is no plan, so we construct one
			 */
			int position = this.indexInMyNodes(current_position.STRING);
		
			// the agent knows the node it's on
			if(position > -1){
			
				String next_node = this.NextHeuristicNode(current_position.STRING, this.idleness_rate_p);
				this.Pathfinding(current_position.STRING, next_node, graph);
				
				try {
					this.visitCurrentNode(current_position.STRING);
					if(plan.get(0).equals(current_position.STRING))
						length_of_last_edge = 1;
					else {
						this.GoTo(plan.get(0));
						Edge edge = this.graph.getEdge(this.graph.getNode(current_position.STRING), this.graph.getNode(plan.get(0)));
						length_of_last_edge = 1 + Math.ceil(edge.getLength());
					}
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			else {
				// the agent losts his way
				String next_node = this.NextHeuristicNode(current_position.STRING, this.idleness_rate_p);
				this.Pathfinding(current_position.STRING, next_node, graph);
				try {
					this.visitCurrentNode(current_position.STRING);
					if(plan.get(0).equals(current_position.STRING))
						length_of_last_edge = 1;
					else {
						this.GoTo(plan.get(0));
						Edge edge = this.graph.getEdge(this.graph.getNode(current_position.STRING), this.graph.getNode(plan.get(0)));
						length_of_last_edge = 1 + Math.ceil(edge.getLength());
					}
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
    }
    
    
    protected void active_run(){
    	boolean pos_actualized = false;
    	boolean graph_actualized = false;
    	boolean time_actualized = false;
		String[] perceptions;
		
		if(this.myCurrentTransaction == -2)
			this.myCurrentTransaction = -1;
    	

		/**
		 * perceive graph and position
		 */
		perceptions = this.connection.getBufferAndFlush();
		if(perceptions.length == 0)
			perceptions = new String[] {""};
		
		while (!pos_actualized || !graph_actualized || !time_actualized || perceptions.length != 0) {
			
			for(int i = perceptions.length - 1; i > -1; i--){
			// tries to obtain the current position
				StringAndDouble current_current_position = this.perceiveCurrentPosition(perceptions[i]);
				if (current_current_position != null && 
						(current_position == null || 
								!current_current_position.STRING.equals(current_position.STRING)
								|| (this.myNodes.size() == 1))){
					current_position = current_current_position;
					pos_actualized = true;
					break;
				}
			}
			
			for(int i = perceptions.length - 1; i > -1; i--){
				// tries to obtain the current position
					Graph current_graph = null;
					try {
						if(!perceptions[i].equals(""))
							current_graph = this.perceiveGraph(perceptions[i]);
					} catch (Exception e) {
						// TODO Auto-generated catch block
						//e.printStackTrace();
					} 
					if (current_graph != null) {
						graph = current_graph;
						graph_actualized = true;
						break;
					}
				}
			
			for(int i = perceptions.length - 1; i > -1; i--){
				time_actualized |= perceiveTime(perceptions[i]);
				if(time_actualized)
					break;
			}
			
			for(int i = 0; i < perceptions.length; i++){
				this.perceiveMessages(perceptions[i]);
			}
			
			
			/**
			 * Auction time !
			 */
			
			if(!this.first_time){
				// manage answers to my auction
				String[] rec_mess_str = new String[received_messages.size()];
				for(int i = 0; i < received_messages.size(); i++)
					rec_mess_str[i] = received_messages.get(i);
					
				this.ManageAnswers(rec_mess_str);
				received_messages.clear();
				
				// if I received all the answers I waited for, I proceed the answers
				boolean all_answers_received = true;
				if(this.nb_answer_awaited != -1)
					all_answers_received = (this.received_props.size() >= this.nb_answer_awaited);
				else 
					for(int i = 0; i < this.awaited_props.size(); i++)
						all_answers_received &= (this.received_props.contains(this.awaited_props.get(i)) 
								|| this.quitting_agents.contains(this.awaited_props.get(i)));
				
				if(all_answers_received  && this.myCurrentTransaction >= 0){
					this.ProcessAnswers();
					this.nb_answer_awaited = -1;
					
				}
				
				// if I have no auction of my own pending and there is no transition, I launch a new auction
				if((this.transition_counter == 0) && this.myCurrentTransaction == -1 && this.agents_num > 1){
					if(this.FBABuyers != null)
						this.FBABuyers.clear();
					if(this.ComplexPropositions != null)
						this.ComplexPropositions.clear();
					this.received_props.clear();
					this.SendNewProposition();
					if(this.known_agents.size() > 0){
						this.awaited_props.clear();
						for(int i = 0; i < this.known_agents.size(); i++)
							this.awaited_props.add(this.known_agents.get(i));
					}
					else 
						this.nb_answer_awaited = this.agents_num - 1;
				}
			}
			
			perceptions = this.connection.getBufferAndFlush();
		}
		
    	if(this.first_time){
    		this.OrderMyNodes();
			this.visit_cost = this.CalculateMyPathCost();
    		this.first_time = false;
    	}

		
		if(current_position.double_value == 0){
			this.PlanAndMove();
		}	
		
		// this checks that no transaction closure was lost and that no node is blocked forever
		this.checkTransactionExpiration();
    }
    
    
    protected void deactivating_run(){
    	boolean pos_actualized = false;
    	boolean graph_actualized = false;
    	boolean time_actualized = false;
		String[] perceptions;
		
		if(this.myCurrentTransaction == -2)
			this.myCurrentTransaction = -1;
    	
		/**
		 * perceive graph and position
		 */
		perceptions = this.connection.getBufferAndFlush();
		if(perceptions.length == 0)
			perceptions = new String[] {""};
		
		while (!pos_actualized || !graph_actualized || !time_actualized || perceptions.length != 0) {

			for(int i = perceptions.length - 1; i > -1; i--){
			// tries to obtain the current position
				StringAndDouble current_current_position = this.perceiveCurrentPosition(perceptions[i]);
				if (current_current_position != null){
					current_position = current_current_position;
					pos_actualized = true;
					break;
				}
			}
			
			for(int i = perceptions.length - 1; i > -1; i--){
				// tries to obtain the current position
					Graph current_graph = null;
					try {
						if(!perceptions[i].equals(""))
							current_graph = this.perceiveGraph(perceptions[i]);
					} catch (Exception e) {
						// TODO Auto-generated catch block
						//e.printStackTrace();
					} 
					if (current_graph != null) {
						graph = current_graph;
						graph_actualized = true;
						break;
					}
				}
			
			for(int i = perceptions.length - 1; i > -1; i--){
				time_actualized |= perceiveTime(perceptions[i]);
				if(time_actualized)
					break;
			}
			
			for(int i = 0; i < perceptions.length; i++){
				this.perceiveMessages(perceptions[i]);
			}
			
			
			/**
			 * Quitting protocol !
			 */
			
			// I process the transactions I am engaged in until no transaction remains
			if(this.NbEngagedNodes() != 0){
				String[] rec_mess_str = new String[received_messages.size()];
				for(int i = 0; i < received_messages.size(); i++)
					rec_mess_str[i] = received_messages.get(i);
					
				this.ManageAnswers(rec_mess_str);
				received_messages.clear();
				
				
				boolean all_answers_received = true;
				for(int i = 0; i < this.awaited_props.size(); i++)
					all_answers_received &= (this.received_props.contains(this.awaited_props.get(i)) 
							|| this.quitting_agents.contains(this.awaited_props.get(i)));
				
				
				if(all_answers_received  && this.myCurrentTransaction >= 0)
					this.ProcessAnswers();
				
			}
			else {
				// then I launch the QUIT protocol
				if(!this.quit_message_sent){
					// I send the QUIT message
					int my_trans_id = this.getNewTransactionId();
			    	
			    	SpeechAct quit_act = this.quit_Message(my_trans_id);
			    	myCurrentTransaction = my_trans_id;
					this.SendSpeechAct(quit_act);
					this.awaited_props.clear();
					for(int i = 0; i < this.known_agents.size(); i++)
						this.awaited_props.add(this.known_agents.get(i));
					this.FBABuyers.clear();
					this.ComplexPropositions.clear();
					this.received_props.clear();
					this.quit_message_sent = true;
				}
				
				// I process the answers
				String[] rec_mess_str = new String[received_messages.size()];
				for(int i = 0; i < received_messages.size(); i++)
					rec_mess_str[i] = received_messages.get(i);
					
				this.ManageAnswers(rec_mess_str);
				received_messages.clear();
				
				// when all the answers have arrived, I finish the QUIT protocol
				boolean all_answers_received = true;
				for(int i = 0; i < this.awaited_props.size(); i++)
					all_answers_received &= (this.FBABuyers.contains(this.awaited_props.get(i)) 
							|| this.quitting_agents.contains(this.awaited_props.get(i)));
				
				
				if(all_answers_received && this.myCurrentTransaction != -1){
					this.manage_Quit_Protocol();
					this.known_agents.clear();
					this.entering_agents.clear();
					this.quitting_agents.clear();
					this.inactive = true;
					this.engaging_out = false;
				}
				
				if(this.inactive){
					this.Deactivate();
					this.quit_message_sent = false;
					return;
				}
			}
			
			
			perceptions = this.connection.getBufferAndFlush();
		}
		
		this.Wait();
		
		this.checkTransactionExpiration();
	}
    
    
    /**
     * used to know when to activate
     */
    protected void inactive_run(){
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
    
    protected void activating_run(){
    	if(!this.activate_message_sent){
    		this.Activate(this.Society_id);
			this.activate_message_sent = true;
    	}
    	
    	boolean pos_actualized = false;
    	boolean graph_actualized = false;
    	boolean time_actualized = false;
		String[] perceptions;
		
		if(this.myCurrentTransaction == -2)
			this.myCurrentTransaction = -1;
    	
		/**
		 * perceive graph and position
		 */
		perceptions = this.connection.getBufferAndFlush();
		if(perceptions.length == 0)
			perceptions = new String[] {""};
		
		while (!pos_actualized || !graph_actualized || !time_actualized || perceptions.length != 0) {
			
			for(int i = perceptions.length - 1; i > -1; i--){
			// tries to obtain the current position
				StringAndDouble current_current_position = this.perceiveCurrentPosition(perceptions[i]);
				if (current_current_position != null){
					current_position = current_current_position;
					pos_actualized = true;
					break;
				}
			}
			
			for(int i = perceptions.length - 1; i > -1; i--){
				// tries to obtain the current position
					Graph current_graph = null;
					try {
						if(!perceptions[i].equals(""))
							current_graph = this.perceiveGraph(perceptions[i]);
					} catch (Exception e) {
						// TODO Auto-generated catch block
						//e.printStackTrace();
					} 
					if (current_graph != null) {
						graph = current_graph;
						graph_actualized = true;
						break;
					}
				}
			
			for(int i = perceptions.length - 1; i > -1; i--){
				time_actualized |= perceiveTime(perceptions[i]);
				if(time_actualized)
					break;
			}
			
			for(int i = 0; i < perceptions.length; i++){
				this.perceiveMessages(perceptions[i]);
			}
			
			
			/**
			 * Entering protocol !
			 */
			if(!this.enter_message_sent){
				// I send the ENTER message
				if(this.enterCondition()){
					int my_trans_id = this.getNewTransactionId();
		    	
					SpeechAct enter_act = this.enter_Message(my_trans_id);
			    	myCurrentTransaction = my_trans_id;
					this.SendSpeechAct(enter_act);
					this.nb_answer_awaited = this.agents_num - 1;
					this.received_props.clear();
					this.enter_message_sent = true;
		    	}
			}
			
			// I process the answers
			String[] rec_mess_str = new String[received_messages.size()];
			for(int i = 0; i < received_messages.size(); i++)
				rec_mess_str[i] = received_messages.get(i);
				
			this.ManageAnswers(rec_mess_str);
			received_messages.clear();

			// if the right number of answers have arrived, I finish the ENTER protocol
			if(received_answers >= (this.nb_answer_awaited - this.quitting_agents.size())  && this.myCurrentTransaction >= 0){
				this.manage_Enter_Protocol();	
				this.inactive = false;
				this.enter_message_sent = false;
				this.current_position = null;
				this.engaging_in = false;
				break;
			}
			
			perceptions = this.connection.getBufferAndFlush();
		}
		
		this.Wait();
		
    }
    

	/**
	 * Checks if there are TransactionNodes that are too old. It means that the agent did not catch the end of a transaction and 
	 * did not free the nodes that were proposed.
	 * The nodes are made available to further transactions
	 */
    protected void checkTransactionExpiration(){
    	if(this.engaged_transactions != null)
	    	for(int i = this.engaged_transactions.size() - 1; i >= 0; i--)
	    		if(this.engaged_transactions.get(i).transaction_time < this.time - this.transaction_too_old)
	    			this.engaged_transactions.remove(i);
    }
    
}
