package agent_library.basic_agents;

import java.io.IOException;
import java.util.ArrayList;

import org.w3c.dom.Element;

import util.agents.AgentImage;
import util.agents.SocietyImage;
import util.agents.SocietyTranslator;
import agent_library.perceptions.AgentInformation;


/**
 * This class manages the agents' (any subclass of AbstractAgent) connection. It is the only class that
 * directly sends and receives messages to/from the server, but this class should not be directly accessed 
 * by users of the library. 
 * <br><br>
 * When it receives messages (e.g. perceptions) from the server, it parses to high-level objects which are 
 * set in the agents' attributes. This manager requires reliable connections (like TCP and ICP).
 * <br><br>
 * Currently it has a "hack" to allow messages to be instantly received by the agents. (This
 * behaviour will be implemented in the server soon).
 *  
 * @author Pablo A. Sampaio
 */
class AbstractAgentManager extends Thread {
	private ArrayList<AbstractAgent> agents;
	
	private ArrayList<String> messagesToAttend;   	// auxiliary attribute (temporary)
	private boolean currentAgentChangedPerception; 	// auxiliary attribute
	
	
	private static AbstractAgentManager instance;	
	
	static AbstractAgentManager getInstance() {
		if (instance == null) {
			instance = new AbstractAgentManager();
			instance.start();
		}
		return instance;
	}
	
	private AbstractAgentManager() {
		agents = new ArrayList<AbstractAgent>();
		messagesToAttend = new ArrayList<String>(); //buffer allocated once
	}
	
	synchronized void addAgent(AbstractAgent ag) {
		try {
			ag.connection.open();
			agents.add(ag);
		} catch (IOException e) {
			printDebug("Agent " + ag.identifier + " not added.");
			e.printStackTrace();
		}
	}
	
	synchronized void getAgents(ArrayList<AbstractAgent> answer) {
		answer.clear();
		answer.addAll(agents);
	}
	
	synchronized void removeAgent(AbstractAgent ag) {
		agents.remove(ag);
		// attention to a possible (rare) problem that may abort the manager if:
		// 1. the agent is put in the buffer used in the main loop (in run()) before calling this method, 
		// 2. then (it's removed and) it's connection is closed here, 
		// 3. then its connection is used in the main loop 
		try {
			ag.connection.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	@Override
	public void run() {
		
		//super.setPriority(NORM_PRIORITY + 1); //higher or highest priority doesn't improve performance
		
		ArrayList<AbstractAgent> agentsBuffer = new ArrayList<AbstractAgent>();
		long timeToStop = -1;

		// stops if it runs 20 seconds without agents
		while (true) {
		
			this.getAgents(agentsBuffer);  //fill in an auxiliary buffer, to avoid concurrency problems
			
			// for each agent:  1. read/parse messages 
			//                  2. set fields
			//                  3. notify the agent
			//                  4. send its messages
			for (AbstractAgent agent : agentsBuffer) {
				String[] perceptionMessages = agent.connection.getBufferAndFlush();
				
				currentAgentChangedPerception = false;
				
				for (String message : perceptionMessages) {
					Element parsedPerception;
				
					try {
						// do generic XML DOM parsing (not specific for graph)
						parsedPerception = util.graph.GraphTranslator.parseString(message);
						
					} catch (Exception e) {
						printDebug("Error parsing: " + message);
						continue; //restart the loop
					}
					
					analysePerception(agent, parsedPerception);
				}
				
				if (currentAgentChangedPerception) {
					synchronized (agent) {
						agent.notify(); //to wake up the agent
					}
				}
				
				// send messages from agent to server
				sendMessagesToServer(agent);
			}
			
			// test stop condition
			if (agentsBuffer.isEmpty()) {
				if (timeToStop == -1) {
					timeToStop = System.currentTimeMillis() + 20000; //20s from now
				} else if (System.currentTimeMillis() >= timeToStop) {
					break;
				}
			} else {
				timeToStop = -1;
			}
			
			// the hack part: sends messages from agent to agent
			// TODO: change server and remove this
			//this.getAgents(agentsBuffer);  //fill in an auxiliary buffer, to avoid concurrency problems
			
			for (int i = 0; i < agentsBuffer.size(); i++) {
				agentsBuffer.get(i).retrieveBroadcastsToSend(messagesToAttend);
				if (messagesToAttend.size() > 0) {
					for (int j = 0; j < agentsBuffer.size(); j++) {
						AbstractAgent agent = agentsBuffer.get(j); 
						if (i != j) {
							agent.addBroadcasts(messagesToAttend);
						}
					}
				}
			}
			
		} // main loop
		
		AbstractAgentManager.instance = null;		
		printDebug("Finished!");		
	}	

	private void analysePerception(AbstractAgent agent, Element element) {
		if (!element.getLocalName().equals("perception")) {
			printDebug("Not a perception!");
			return;
		}
		
		int type = Integer.parseInt( element.getAttribute("type") );
			
		//_PRINT("TYPE: " + type);
		
		switch (type) {
		case -1:
			double time = Double.parseDouble(element.getAttribute("time"));
			agent.setCurrentTurn(time);
			break;
			
		case 0:
			if (agent.usingGraph2) {
				util.graph2.Graph[] graphs = util.graph2.GraphTranslator.getGraphs(element);
				if (graphs.length > 0) {
					agent.setGraph2(graphs[0]); //should have only one
					currentAgentChangedPerception = true;
				} else {
					printDebug("Perception type 0 w/out graph2!");
				}
			} else {
				util.graph.Graph[] graphs = util.graph.GraphTranslator.getGraphs(element);
				if (graphs.length > 0) {
					agent.setGraph(graphs[0]); //should have only one
					currentAgentChangedPerception = true;
				} else {
					printDebug("Perception type 0 w/out graph!");
				}
			}
			break;
		
		case 1:
			SocietyImage[] societyInfos = SocietyTranslator.getSocieties(element);
			if (societyInfos.length > 0) {
				agent.setSocietyInfo(societyInfos[0]); //should have only one
				currentAgentChangedPerception = true;
			} else {
				printDebug("Perception type 1 w/out society info!");
			}
			break;

		case 2: //NOT TESTED
			throw new Error("Node mark not implemented!");
			//TODO: implement node marks
			//break;
		
		case 3:
			agent.addBroadcast(element.getAttribute("message"));
			currentAgentChangedPerception = true;
			break;
		
		case 4:
			AgentImage[] selfInfo = SocietyTranslator.getAgents(element);
			if (selfInfo.length > 0) {
				agent.setSelfInfo(new AgentInformation(selfInfo[0])); //should have only one
				currentAgentChangedPerception = true;
			} else {
				printDebug("Perception type 4 w/out agent info!");
			}
			break;
		
		default:
			printDebug("Unknown perception type: " + type);
			
		}

	}

	private void sendMessagesToServer(AbstractAgent agent) {
		agent.retrieveMessagesToServer(messagesToAttend);
		
		for (String msg : messagesToAttend) {
			try {
				agent.connection.send(msg);
			} catch (IOException e) {
				printDebug("Couldn't send message from " + agent.identifier + ": " + msg);
			}
		}

	}
	
	private void printDebug(String message) {
		System.out.println("CONN-MANAGER: " + message);
	}
	
}
