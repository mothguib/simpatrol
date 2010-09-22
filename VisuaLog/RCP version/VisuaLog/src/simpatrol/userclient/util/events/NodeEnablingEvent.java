/* VertexEnablingEvent.java */

/* The package of this class. */
package simpatrol.userclient.util.events;

import java.io.IOException;

import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Element;
import org.xml.sax.SAXException;

import simpatrol.clientplugin.visuallog.visualparts.VisualCanvas;
import simpatrol.userclient.util.Translator;
import simpatrol.userclient.util.graph.Graph;
import simpatrol.userclient.util.graph.Node;

/** Implements the events that are related to the enabling of a vertex. */
public final class NodeEnablingEvent {
	/* Attributes. */
	/** The id of the vertex being enabled / disabled. */
	private Node vertex;

	/* Methods. */
	/**
	 * Constructor.
	 * 
	 * @param vertex_id
	 *            The id of the vertex being enabled / disabled.
	 * @throws IOException 
	 * @throws SAXException 
	 * @throws ParserConfigurationException 
	 */
	public NodeEnablingEvent(String event, Graph graph) throws ParserConfigurationException, SAXException, IOException {
		Element my_event = Translator.parseString(event);
		
		String vertex_id = my_event.getAttribute("vertex_id");
		
		for(Node vert : graph.getNodes()){
			if(vert.getObjectId().equals(vertex_id))
				vertex = vert;
		}
	}

	public boolean perform_event(VisualCanvas mycanvas){
		// TODO : understand how this event is used and implement accordingly
		return false;
	}
}
