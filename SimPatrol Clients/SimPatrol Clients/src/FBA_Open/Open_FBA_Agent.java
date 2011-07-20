package FBA_Open;

import java.io.IOException;
import java.util.LinkedList;

import util.StringAndDouble;
import util.graph.Edge;
import util.graph.Graph;
import FBA.ComplexBid;
import FBA.FlexibleBidder2Agent;
import FBA.SpeechAct;
import FBA.SpeechActPerformative;
import FBA.TransactionNodes;

public abstract class Open_FBA_Agent extends FlexibleBidder2Agent {
	
	public static int NB_AGENTS_AT_START;
	
	protected String Society_id;
	
	protected int nb_answer_awaited;
	
	protected int transition_counter = 0;
	protected LinkedList<String> Quitting_agents = new LinkedList<String>();
	
	protected boolean enter_message_sent = false;
	protected boolean first_message_in = false;
	protected boolean first_message_out = false;
	
	boolean nb_agent_set = false;

	public Open_FBA_Agent(String id, double entering_time, double quitting_time, 
									int number_of_agents, LinkedList<String> nodes,
									double idleness_rate_for_path, double idleness_rate_for_auction,
									String society_id) {
		super(id, entering_time, quitting_time, society_id, number_of_agents, nodes, idleness_rate_for_path, idleness_rate_for_auction);
		
		if(this.myNodes.size() == 0)
			this.nb_min_nodes = 1;
		
		received_messages = new LinkedList<String>();
		
		this.setScenario();
	}
	
	public Open_FBA_Agent(String id, int number_of_agents, LinkedList<String> nodes,
			double idleness_rate_for_path, double idleness_rate_for_auction) {
		super(id, number_of_agents, nodes, idleness_rate_for_path, idleness_rate_for_auction);
		
		this.setScenario();
	}
	
	public static void setNB_AGENTS_AT_START(int nb){
		Open_FBA_Agent.NB_AGENTS_AT_START = nb;
	}

	
    /**
     * Manages the messages received
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
				//else if(!this.inactive && (received_act.getReceiver().equals(this.agent_id) || received_act.getReceiver().equals("all_agents"))){
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
	 *  
	 * @param act the received SpeechAct
	 * @return the SpeechAct to send to the sender of the treated SpeechAct
	 */
	protected SpeechAct negociate(SpeechAct act){
		ComplexBid proposition, bid;
		SpeechAct answer;
		
		switch(act.getPerformative()){
		case INFORM :
			if(this.engaging_in || this.engaging_out || (this.transition_counter != 0)){
				answer = new SpeechAct(act.getTransactionId(), SpeechActPerformative.REFUSE, this.agent_id, act.getSender(), act.getPropositions(), 1);
				break;
			} else {
				// check that there is a proposition
				proposition = this.emitProposition(act.getPropositions());
				// if there is something to propose
				if((proposition.getBidsForFirst() != null && proposition.getBidsForFirst().length > 0)
						|| (proposition.getBidsForSecond() != null && proposition.getBidsForSecond().length > 0)
						|| (proposition.getBidsForBoth() != null && proposition.getBidsForBoth().length > 0)
						|| (proposition.getDoubleBidsForBoth() != null && proposition.getDoubleBidsForBoth().size() > 0)){
					answer = new SpeechAct(act.getTransactionId(), SpeechActPerformative.PROPOSE, this.agent_id, act.getSender(), proposition);
					
					TransactionNodes mytrans = new TransactionNodes(act.getTransactionId(), act.getPropositions(), proposition);
					this.engaged_transactions.add(mytrans);
					this.nb_engaged_nodes = this.NbEngagedNodes();
				} else { //there is nothing to propose
					answer = new SpeechAct(act.getTransactionId(), SpeechActPerformative.REFUSE, this.agent_id, act.getSender(), act.getPropositions(), 1);
				}
				break;
			}
			
		case REJECT :
			
			TransactionNodes mytrans = this.getEngagedTransacFromId(act.getTransactionId());
			if(mytrans != null){
				this.engaged_transactions.remove(mytrans);
				this.nb_engaged_nodes = this.NbEngagedNodes();
			}
			
			return null;
			
		case PROPOSE :
			// check the proposition
			if(FBABuyers == null)
				FBABuyers = new LinkedList<String>();
			FBABuyers.add(act.getSender());
			bid = act.getComplexBid();
			if(ComplexPropositions == null)
				ComplexPropositions = new LinkedList<ComplexBid>();
			if(bid != null)
				ComplexPropositions.add(bid);
			return null;
			
		case ACCEPT :
			proposition = act.getComplexBid();
			// Since the exchange has been accepted, we swap the exchanged nodes
			// for those given by the other part
			if(proposition != null){
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
					
				TransactionNodes mytrans2 = this.getEngagedTransacFromId(act.getTransactionId());
				if(mytrans2 != null){
					this.engaged_transactions.remove(mytrans2);
					this.nb_engaged_nodes = this.NbEngagedNodes();
				}
					
				if(NodeToDelete.size() != 0){
					String message = "Agent " + this.agent_id + " : exchanging ACCEPT #" + act.getTransactionId() + " ";
					for(String bla : NodeToDelete)
						message += bla + " and ";
					message = message.substring(0, message.length() - 4) + " for ";
					for(int i = 0; i < NodeToAdd.size(); i+=2)
						message += NodeToAdd.get(i) + "(" +  NodeToAdd.get(i+1) + ") and ";
					message = message.substring(0, message.length() - 4) + ".";
					System.out.println(message);
				}
				else {
					this.transition_counter--;
					String message = "Agent " + this.agent_id + " : reorganising #" + act.getTransactionId() + " with : ";
					for(int i = 0; i < NodeToAdd.size(); i+=2)
						message += NodeToAdd.get(i) + "(" +  NodeToAdd.get(i+1) + ") and ";
					message = message.substring(0, message.length() - 4) + ".";
					System.out.println(message);
					this.Quitting_agents.remove(act.getSender());
				}
			}
			else {
				this.transition_counter--;
				this.agents_num++;
				TransactionNodes mytrans2 = this.getEngagedTransacFromId(act.getTransactionId());
				if(mytrans2 != null){
					this.engaged_transactions.remove(mytrans2);
					this.nb_engaged_nodes = this.NbEngagedNodes();
				}
			}
			this.OrderMyNodes();
			this.visit_cost = this.CalculateMyPathCost();
			
//			String message = "Agent " + this.agent_id + " 's nodes : ";
//			for(String bla : this.getMyNodes())
//				message += bla + ", ";
//			message = message.substring(0, message.length() - 2) + ".";
//			System.out.println(message);
			System.out.println("Agent " + this.agent_id + ", new visit cost : " + this.visit_cost);
			
			return null;
			
		case REFUSE :
			return null;
			
		case ENTER :
			if(this.engaging_out)
				return null;
			this.transition_counter++;
			answer = manage_Enter_Message(act);
			mytrans = new TransactionNodes(act.getTransactionId(), act.getPropositions(), answer.getComplexBid());
			this.engaged_transactions.add(mytrans);
			//if(!this.engaging_in)
			
			break;
			
		case QUIT :
			this.Quitting_agents.add(act.getSender());
			this.agents_num--;
			if(this.engaging_in)
				return null;
			this.transition_counter++;
			answer = manage_Quit_Message(act);
			mytrans = new TransactionNodes(act.getTransactionId(), act.getPropositions(), new ComplexBid(0));
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
     * Calculates which of the received answers is the most interesting
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
    	else
    		super.ProcessAnswers();
    }

    /**
     * Sends the given speechAct
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
   
    
    protected abstract SpeechAct quit_Message(int id);
    
    protected abstract SpeechAct manage_Quit_Message(SpeechAct quitting_act); 
    
    protected abstract void manage_Quit_Protocol();
    

    
    
    protected abstract SpeechAct enter_Message(int id);
    
    protected abstract SpeechAct manage_Enter_Message(SpeechAct entering_act);
    
    protected abstract void manage_Enter_Protocol();
    
    
    
    
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
			int position = this.indexInMyNodes(current_position.STRING);
		
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

			/*if(!this.nb_agent_set){
				this.AGENTS_POSITIONS = new LinkedList<String>();
				for(int i = perceptions.length - 1; i > -1; i--){
					this.perceiveAgentsPositions(perceptions[i]);
					if(this.AGENTS_POSITIONS.size() != 0){
						this.agents_num = this.AGENTS_POSITIONS.size()/2 + 1;
						this.AGENTS_POSITIONS = null;
						this.nb_agent_set = true;
						break;
					}
				}
			}*/
			
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
				String[] rec_mess_str = new String[received_messages.size()];
				for(int i = 0; i < received_messages.size(); i++)
					rec_mess_str[i] = received_messages.get(i);
					
				this.ManageAnswers(rec_mess_str);
				received_messages.clear();
				
				if(received_answers >= this.nb_answer_awaited  && this.myCurrentTransaction != -1)
					this.ProcessAnswers();
				
				
				if((this.transition_counter == 0) && this.myCurrentTransaction == -1 && agents_num > 1){
				//if(this.myCurrentTransaction == -1 && agents_num > 1){
					this.SendNewProposition();
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
			if(this.NbEngagedNodes() != 0){
				String[] rec_mess_str = new String[received_messages.size()];
				for(int i = 0; i < received_messages.size(); i++)
					rec_mess_str[i] = received_messages.get(i);
					
				this.ManageAnswers(rec_mess_str);
				received_messages.clear();
				
				if(received_answers >= this.nb_answer_awaited  && this.myCurrentTransaction != -1)
					this.ProcessAnswers();
				
			}
			else {
				if(!this.first_message_out){
					int my_trans_id = FlexibleBidder2Agent.transaction_id;
			    	FlexibleBidder2Agent.transaction_id++;
			    	
			    	SpeechAct quit_act = this.quit_Message(my_trans_id);
			    	myCurrentTransaction = my_trans_id;
					this.SendSpeechAct(quit_act);
					this.nb_answer_awaited = this.agents_num - 1;
					
					this.first_message_out = true;
				}
				
				String[] rec_mess_str = new String[received_messages.size()];
				for(int i = 0; i < received_messages.size(); i++)
					rec_mess_str[i] = received_messages.get(i);
					
				this.ManageAnswers(rec_mess_str);
				received_messages.clear();
				
				if(received_answers >= this.nb_answer_awaited  && this.myCurrentTransaction != -1){
					this.manage_Quit_Protocol();
					this.Quitting_agents.clear();
					this.inactive = true;
					this.engaging_out = false;
				}
				
				if(this.inactive){
					this.Deactivate();
					this.first_message_out = false;
					return;
				}
			}
			
			
			perceptions = this.connection.getBufferAndFlush();
		}
		
		// send "wait" action
		String message_1 = "<action type=\"-1\"/>\n";
		try {
			this.connection.send(message_1);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
    
    
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
    	if(!this.enter_message_sent){
    		this.Activate(this.Society_id);
			this.enter_message_sent = true;
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
			
			if(!this.nb_agent_set){
				this.AGENTS_POSITIONS = new LinkedList<String>();
				for(int i = perceptions.length - 1; i > -1; i--){
					this.perceiveAgentsPositions(perceptions[i]);
					if(this.AGENTS_POSITIONS.size() != 0){
						this.agents_num = this.AGENTS_POSITIONS.size()/2 + 1;
						this.AGENTS_POSITIONS = null;
						this.nb_agent_set = true;
						break;
					}
				}
			}
			
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
			if(!this.first_message_in && this.nb_agent_set){
				int my_trans_id = FlexibleBidder2Agent.transaction_id;
		    	FlexibleBidder2Agent.transaction_id++;
		    	
		    	SpeechAct enter_act = this.enter_Message(my_trans_id);
		    	myCurrentTransaction = my_trans_id;
				this.SendSpeechAct(enter_act);
				this.nb_answer_awaited = this.agents_num - 1;
				
				this.first_message_in = true;
			}
			
			
			String[] rec_mess_str = new String[received_messages.size()];
			for(int i = 0; i < received_messages.size(); i++)
				rec_mess_str[i] = received_messages.get(i);
				
			this.ManageAnswers(rec_mess_str);
			received_messages.clear();

			if(received_answers >= (this.nb_answer_awaited - this.Quitting_agents.size())  && this.myCurrentTransaction != -1){
				this.manage_Enter_Protocol();	
				this.inactive = false;
				this.first_message_in = false;
				this.current_position = null;
				this.engaging_in = false;
				break;
			}
			
			perceptions = this.connection.getBufferAndFlush();
		}
		
		this.Wait();
		
    }
    
    
    
    /**
     * This fuction implements the scenario, if needed.
     * it should set for each agent the entering_time, quitting_time and agents_num
     * it should also set Society_id
     * 
     * 
     * @return
     */
    protected abstract void setScenario();
    
    	
}

