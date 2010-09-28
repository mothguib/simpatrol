package simpatrol.clientplugin.visuallog.logger;

import java.awt.Point;
import java.io.IOException;

import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Element;
import org.xml.sax.SAXException;

import simpatrol.clientplugin.visuallog.visualparts.AgentTriangle;
import simpatrol.clientplugin.visuallog.visualparts.EdgeLine;
import simpatrol.clientplugin.visuallog.visualparts.NodeCircle;
import simpatrol.clientplugin.visuallog.visualparts.VisualCanvas;
import simpatrol.userclient.util.Translator;
import simpatrol.userclient.util.action.Action;
import simpatrol.userclient.util.action.ActionTranslator;
import simpatrol.userclient.util.agent.Agent;
import simpatrol.userclient.util.agent.Society;
import simpatrol.userclient.util.agent.SocietyTranslator;
import simpatrol.userclient.util.events.AgentCreationEvent;
import simpatrol.userclient.util.events.AgentDeathEvent;
import simpatrol.userclient.util.events.EdgeEnablingEvent;
import simpatrol.userclient.util.events.EventTypes;
import simpatrol.userclient.util.events.NodeEnablingEvent;
import simpatrol.userclient.util.file.FileWriter;
import simpatrol.userclient.util.graph.Edge;
import simpatrol.userclient.util.graph.Graph;
import simpatrol.userclient.util.graph.GraphTranslator;
import simpatrol.userclient.util.graph.Node;
import simpatrol.userclient.util.net.UDPClientConnection;


/* * * * * * * * * * * * * * * * * * * * * * * *
 * 
 *    Overall class for the log classes.
 *    It has all the methods used by all logs,
 *    such as those used to configure the 
 *    environment, or to manage an event
 *    
 *    @author : Cyril Poulet
 * 
 * * * * * * * * * * * * * * * * * * * * * * * */
public abstract class AbstractLog extends Thread {

	/* Attributes. */
	/** Registers if the log client shall stop working. */
	protected boolean stop_working;

	/** The UDP connection of the log client. */
	protected UDPClientConnection connection;

	/**
	 * The object that writes on the output file the obtained events.
	 */
	protected FileWriter file_writer;
	
	/** representation of the simulation environment */
	protected Graph graph;
	protected Society[] societies;
	
	/** the drawing object */
	protected VisualCanvas mycanvas;
	protected boolean canvas_configured = false;
	
	/** 
	 *  duration of the "skip" event
	 * */
	public static long SKIP_DURATION = 300;
	
	/** Indicates that the client must stop working. */
	public void stopWorking() {
		this.stop_working = true;
	}
	
	/** Represents the kind of log that is used. */
	public abstract String getType();
	
	
	/** 
	 *  translates the beginning of the file/stream into the simulation environment
	 *  (graph, societies)
	 *  
	 * @param events
	 * 			a String[] containing the graph and societies descriptions
	 */
	protected void configure_environment(String[] events){
		for(String event : events){
			try {
				if(graph != null && societies != null)
					return;
				if(!(event.contains("graph")) && !(event.contains("society")))
					continue;
				else {
					event = "<environment>" + event + "</environment>";
					Element first_str = Translator.parseString(event);
					if(graph == null)
						graph = GraphTranslator.getGraphs(first_str)[0];
					else
						societies = SocietyTranslator.getSocieties(first_str, graph);
				}
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
		}
	}
	
	/**
	 *  represents the objects of the environment (agents, nodes, edges) into 
	 *  visual representations.
	 */
	protected void configure_canvas(){
		int i = 50;
		int j = 50;
		for(Node vert : graph.getNodes()){
			NodeCircle node = new NodeCircle(new Point(i, j), vert, vert.getObjectId(), 0, this.getType().contains("turn")? false : true);
			node.setModified(true);
			mycanvas.addDrawable(node);
			i += 40;
			if(i > 200){
				i = 10;
				j += 40;
			}
		}
		
		for(Edge edge : graph.getEdges()){
			mycanvas.addDrawable(new EdgeLine(edge));
		}
		
		for(Society society : societies)
			for(Agent agent : society.getAgents()){
				String label = agent.getLabel();
				if(societies.length > 1) label = society.getLabel() + label;
				mycanvas.addDrawable(new AgentTriangle(agent, new Point(10,10), label));
			}
		
		mycanvas.updateDrawables();
		mycanvas.repaint();
		canvas_configured = true;
	}


	/**
	 * Manages a single event as read in the file/stream
	 * 
	 * @param event 
	 * 			a String containing the event as read in the file/stream
	 * @return the event as a parsed element, plus an additional information ("skip")
	 * 			that is used to know if the player must make a slight pause (for example
	 *          to make possible the observation of visits)
	 * @throws Exception : parser exception
	 */
	protected Element manage_event(String event) throws Exception{
		Element my_event;
		try{
			my_event = Translator.parseString(event);
		}
		catch (Exception e){
			return null;
		}
		
		String agent_id = my_event.getAttribute("agent_id");
		Agent current_agent = null;
		
		if(agent_id != ""){
			for(Society society: societies){
				for(Agent agent : society.getAgents()){
					if(agent.getObjectId().equals(agent_id)){
						current_agent = agent;
						continue;
					}
				}
			}
			
			if(current_agent == null)
				throw new Exception("agent " + agent_id + " introuvable");
			
		}
		
		String event_type = my_event.getAttribute("type");
		switch(EventTypes.fromString(event_type)){
		case 0:
			AgentCreationEvent creation = new AgentCreationEvent(current_agent);
			creation.perform_event();
			my_event.setAttribute("skip", "false");
			break;
		case 1:
			AgentDeathEvent death = new AgentDeathEvent(current_agent);
			death.perform_event();
			my_event.setAttribute("skip", "false");
			break;
		case 2:
		case 3:
		case 9:
			my_event.setAttribute("skip", "false");;
			break;
		case 4:
		case 5:
		case 6:
		case 7:
		case 8:
			Action current_action = ActionTranslator.getAction(event, graph);
			my_event.setAttribute("skip", Boolean.toString(current_action.perform_action(current_agent)));
			break;
		case 10 :
			NodeEnablingEvent vertEnable = new NodeEnablingEvent(event, graph);
			my_event.setAttribute("skip", Boolean.toString(vertEnable.perform_event(mycanvas)));
			break;
		case 11 :
			EdgeEnablingEvent edgeEnable = new EdgeEnablingEvent(event, graph);
			my_event.setAttribute("skip", Boolean.toString(edgeEnable.perform_event(mycanvas)));
			break;
			
		}
		
		return my_event;
	}


}
