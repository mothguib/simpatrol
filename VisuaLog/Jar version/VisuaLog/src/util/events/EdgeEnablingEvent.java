/* EdgeEnablingEvent.java */

/* The package of this class. */
package util.events;

import java.io.IOException;

import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Element;
import org.xml.sax.SAXException;

import visualparts.VisualCanvas;
import util.Translator;
import util.graph.Edge;
import util.graph.Graph;

/** Implements the events that are related to the enabling of an edge. */
public final class EdgeEnablingEvent {
	/* Attributes. */
	/** The id of the edge being enabled / disabled. */
	private Edge Edge;

	/* Methods. */
	/**
	 * Constructor.
	 * 
	 * @param edge_id
	 *            The id of the edge being enabled / disabled.
	 * @throws IOException 
	 * @throws SAXException 
	 * @throws ParserConfigurationException 
	 */
	public EdgeEnablingEvent(String event, Graph graph) throws ParserConfigurationException, SAXException, IOException {
		Element my_event = Translator.parseString(event);
		
		String edge_id = my_event.getAttribute("edge_id");
		
		for(Edge edge : graph.getEdges()){
			if(edge.getObjectId().equals(edge_id))
				Edge = edge;
		}
	}
	
	public boolean perform_event(VisualCanvas mycanvas){
		// TODO : understand how this event is used and implement accordingly
		return false;
	}

}
