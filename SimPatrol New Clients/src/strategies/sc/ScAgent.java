package strategies.sc;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.StringTokenizer;

import util.agents.AgentImage;
import util.agents.SocietyImage;
import agent_library.basic_agents.AgentStoppedException;
import agent_library.basic_agents.ThreadAgent;
import agent_library.connections.ClientConnection;
import agent_library.perceptions.AgentInformation;


public class ScAgent extends ThreadAgent {

	// the plan of walking through the graph, which is a TSP solution
	private final LinkedList<String> plan;

	// registers how many agents this one must let pass before start walking 
	private int agentsToPass;

	// how much time this agent must wait before start walking, after the last agent passed it
	private double waitTime;
	
	// registers the time the agent started counting up until the moment to
	// start walking on the graph.
	private double startTime;

	// set of agents that this one has already perceived
	private HashSet<String> perceivedAgents;

	// current info about this agent
	private AgentInformation selfInfo; 

	// registers the current step of the path planned by the agent
	private int currentPlanStep;
	
	// buffer for receiving messages (allocated once)
	ArrayList<String> bufferMsgs;
	

	public ScAgent(String id, ClientConnection conn) {
		super(id, conn, false);
		this.plan = new LinkedList<String>();
		this.agentsToPass = 0;
		this.waitTime = 0;
		this.startTime = -1;
		this.perceivedAgents = new HashSet<String>();
		this.currentPlanStep = -1;
		this.bufferMsgs = new ArrayList<String>(); 
	}

	/**
	 * The agent perceives the message sent by the coordinator.  
	 */
	private boolean receiveMessageFromCoordinator() throws AgentStoppedException {
		super.retrieveBroadcastsBlocking(this.bufferMsgs, 20000);
		
		for (String message : this.bufferMsgs) {
			// obtains the id of the agent to whom the message was broadcasted
			String agentId = message.substring(0, message.indexOf("###"));
			
			if (agentId.equals(this.identifier)) {
				message = message.substring(message.indexOf("###") + 3);

				String plan = message.substring(0, message.indexOf("###")); //the TSP solution
				StringTokenizer tokenizer = new StringTokenizer(plan, ",");

				while (tokenizer.hasMoreTokens()) {
					String nextStep = tokenizer.nextToken();
					this.plan.add(nextStep);
				}
				printDebug("Plan:", this.plan);

				this.currentPlanStep = this.plan.indexOf(this.selfInfo.node_id);
				printDebug("Started on step", this.currentPlanStep, ", node", this.selfInfo.node_id);

				// obtains the number of agents that this one must let pass
				message = message.substring(message.indexOf("###") + 3);
				this.agentsToPass = Integer.parseInt(message.substring(0, message.indexOf(";")));
				printDebug("I will let pass", this.agentsToPass, "agents.");

				// obtains the time this agent must wait after all agents have passed
				this.waitTime = Double.parseDouble(message.substring(message.indexOf(";") + 1));
				printDebug("I must wait", this.waitTime, " turns.");
				
				return true;
			}
		}
		
		return false;
	}

	/**
	 * Perceive and count the passing agents.
	 */
	private void perceivePassingAgents() throws AgentStoppedException {
		SocietyImage societyInfo = super.perceiveSocietyBlocking(20000);
		
		printDebug("Perceived agents:", societyInfo.agents.length);
		
		for (AgentImage agent : societyInfo.agents) {
			if (!this.perceivedAgents.contains(agent.id)) {
				this.perceivedAgents.add(agent.id);
	
				this.agentsToPass--;
	
				printDebug("Agent", agent.id, "passed me.");
			}
		}
		
		// if the number of agents to let pass is smaller than one, 
		// and "start time" is not set
		if (this.agentsToPass < 1 && this.startTime == -1) {
			this.startTime = super.getCurrentTurn();
			printDebug("Started counting down.");
		}
		
	}

	/**
	 * Visit the current position and go to the next step (i.e. node) of its plan.
	 */
	private void visitAndGoToNextStep() throws AgentStoppedException {
		super.actVisit();
		
		this.currentPlanStep = (this.currentPlanStep + 1) % this.plan.size();
		String nextNode = this.plan.get(this.currentPlanStep);
		
		super.actGoto(nextNode);

		printDebug("Going to step", this.currentPlanStep, ", node", nextNode);
	}
	
	/**
	 * Tests if the agent has completed the current step of the plan. 
	 */
	private boolean arrivedAtGoal() {
		if (this.selfInfo.elapsed_length != 0) {
			return false;
		}		
		String goalNode = this.plan.get(this.currentPlanStep);		
		return this.selfInfo.node_id.equals(goalNode);
	}
	
	/**
	 * Main method of the agent.
	 */
	public void run() {
		int currentTurn;
		
		try {
			this.selfInfo = super.perceiveSelfBlocking(20000);

			if (!this.receiveMessageFromCoordinator()) {
				printDebug("Can't continue: no message from coordinator. Stopping...");
				return;
			}

			while (!this.stopRequested) {
				this.perceivePassingAgents();

				currentTurn = super.getCurrentTurn();
				printDebug("Current turn:", currentTurn);
				
				if ((this.startTime > -1) 				
						&& (currentTurn - this.startTime > this.waitTime)) {
					printDebug("Here I go, on cycle", currentTurn);
					this.visitAndGoToNextStep();
					break;

				} else {
					super.actDoNothing();
					
				}
				
				this.selfInfo = super.perceiveSelfBlocking(20000);
			}


			while (!this.stopRequested) {			
				this.selfInfo = super.perceiveSelfBlocking(20000);
				
				if (this.arrivedAtGoal()) {
					printDebug("Arrived!");
					this.visitAndGoToNextStep();
				}			
			}
			
		} catch (AgentStoppedException e) {
			e.printStackTrace();
			
		}
		
		printDebug("Finished.");
		
	}
	
	protected void printDebug(Object ... messages) {
		StringBuilder builder = new StringBuilder();
		builder.append(identifier.toUpperCase());
		builder.append(": ");
		for (int i = 0; i < messages.length; i++) {
			builder.append(messages[i].toString());
			builder.append(" ");
		}
		System.out.println(builder.toString());
	}

}
