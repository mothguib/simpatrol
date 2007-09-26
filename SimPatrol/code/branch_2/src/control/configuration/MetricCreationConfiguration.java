/* MetricCreationConfiguration.java */

/* The package of this class. */
package control.configuration;

/* Imported classes and/or interfaces. */
import model.metric.Metric;

/** Implements objects that express configurations to add
 *  a metric to a simulation.
 *  
 *  @see Metric */
public final class MetricCreationConfiguration extends Configuration {
	/* Attributes. */
	/** The new metric to be added to the simulation. */
	private Metric metric;
	
	/** The duration, in seconds, of a cycle of measurement of the metric. */
	private int cycle_duration;
	
	/* Methods. */
	/** Constructor.
	 * 
	 *  @param sender_address The The IP address of the sender of the configuration.
	 *  @param sender_socket The number of the UDP socket of the sender.
	 *  @param metric The metric to be added to the simulation.
	 *  @param cycle_duration The duration, in seconds, of a cycle of measurement of the metric.*/	
	public MetricCreationConfiguration(String sender_address, int sender_socket, Metric metric, int cycle_duration) {
		super(sender_address, sender_socket);
		this.metric = metric;
		this.cycle_duration = cycle_duration;
	}
	
	/** Returns the metric of the configuration.
	 * 
	 *  @return The metric of the configuration. */
	public Metric getMetric() {
		return this.metric;
	}
	
	/** Returns the duration, in seconds, of a cycle of
	 *  measurement of the metric.
	 *  
	 *  @return The duration of a cycle of measurement of the metric. */
	public int getCycle_duration() {
		return this.cycle_duration;
	}
	
	@Override
	protected int getType() {
		return ConfigurationTypes.METRIC_CREATION;
	}
	
	public String fullToXML(int identation) {
		// holds the answer to the method
		StringBuffer buffer = new StringBuffer();
		
		// applies the identation and fills the "configuration" tag
		for(int i = 0; i < identation; i++) buffer.append("/t");
		buffer.append("<configuration type=\"" + ConfigurationTypes.METRIC_CREATION +					  
					  "\" sender_adress=\"" + this.sender_address +
				      "\" sender_socket=\"" + this.sender_socket +
				      "\" parameter=\"" + this.cycle_duration +
				      "\">\n");
		
		// puts the metric
		buffer.append(this.metric.fullToXML(identation + 1));
		
		// closes the tag
		for(int i = 0; i < identation; i++) buffer.append("/t");
		buffer.append("</configuration>\n");
		
		// return the answer to the method
		return buffer.toString();
	}
}