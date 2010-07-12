/* GraphCreationConfiguration.java */

/* The package of this class. */
package control.configuration;

/* Imported classes and/or interfaces. */
import model.graph.Graph;

/** Implements objects that express configurations to create
 *  the graph of the simulation.
 *  
 *  @see Graph */
public final class GraphCreationConfiguration extends Configuration {
	/* Attributes. */
	/** The graph being created. */
	private Graph graph;
	
	/* Methods. */
	/** Constructor.
	 * 
	 *  @param sender_address The IP address of the sender of the configuration.
	 *  @param sender_socket The number of the UDP socket of the sender.
	 *  @param graph The graph being created by the configuration. */
	public GraphCreationConfiguration(String sender_address, int sender_socket, Graph graph) {
		super(sender_address, sender_socket);
		this.graph = graph;
	}
	
	/** Returns the graph of the configuration.
	 * 
	 *  @return The graph of the configuration. */
	public Graph getGraph() {
		return this.graph;
	}
	
	@Override
	protected int getType() {
		return ConfigurationTypes.GRAPH_CREATION;
	}
	
	public String fullToXML(int identation) {
		// holds the answer to the method
		StringBuffer buffer = new StringBuffer();
		
		// applies the identation and fills the "configuration" tag
		for(int i = 0; i < identation; i++) buffer.append("/t");
		buffer.append("<configuration type=\"" + ConfigurationTypes.GRAPH_CREATION +
				      "\" sender_adress=\"" + this.sender_address +
				      "\" sender_socket=\"" + this.sender_socket +
				      "\">\n");
		
		// puts the graph
		buffer.append(this.graph.fullToXML(identation + 1));
		
		// closes the tag
		for(int i = 0; i < identation; i++) buffer.append("/t");
		buffer.append("</configuration>\n");
		
		// return the answer to the method
		return buffer.toString();
	}
}