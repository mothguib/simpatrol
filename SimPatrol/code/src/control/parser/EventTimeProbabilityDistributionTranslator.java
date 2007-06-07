/* EventTimeProbabilityDistributionTranslator.java */

/* The package of this class. */
package control.parser;

/* Imported classes and/or interfaces. */
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import util.etpd.EmpiricalTimeProbabilityDistribution;
import util.etpd.NormalTimeProbabilityDistribution;
import util.etpd.SpecificTimeProbabilityDistribution;
import util.etpd.EventTimeProbabilityDistribution;
import util.etpd.EventTimeProbabilityDistributionTypes;
import util.etpd.UniformTimeProbabilityDistribution;

/** Implements a translator thar obtains event time probability distributions
 *  from a given xml source.
 *  @see EventTimeProbabilityDistribution */
public abstract class EventTimeProbabilityDistributionTranslator extends Translator {
	/* Methods. */
	/** Obtains the event time probability distributions from the
	 *  given xml element.
	 *  @param xml_element The XML source containing the etpds to be translated.
	 *  @return The event time probability distributions. */
	public static EventTimeProbabilityDistribution[] getEventTimeProbabilityDistribution(Element xml_element) {		
		// obtains the nodes with the "etpd" tag
		NodeList etpd_nodes = xml_element.getElementsByTagName("etpd");
		
		// are there any etpd nodes?
		if(etpd_nodes.getLength() == 0)
			return new EventTimeProbabilityDistribution[0];
		
		// the answer of the method
		EventTimeProbabilityDistribution[] answer = new EventTimeProbabilityDistribution[2];
		
		// for the two ocurrences
		for(int i = 0; i < 2; i++) {
			// obtains the current etpd element
			Element etpd_element = (Element) etpd_nodes.item(i);
			
			// obtains the data
			String id = etpd_element.getAttribute("id");
			int seed = Integer.parseInt(etpd_element.getAttribute("seed"));
			int next_bool_count = Integer.parseInt(etpd_element.getAttribute("next_bool_count"));
			int type = Integer.parseInt(etpd_element.getAttribute("type"));
			
			// instantiates the new etpd and configures it
			switch(type) {
				case EventTimeProbabilityDistributionTypes.UNIFORM: {
					answer[i] = new UniformTimeProbabilityDistribution(seed, getUniformETPDParameter(etpd_element));
					break;
				}
				case EventTimeProbabilityDistributionTypes.EMPIRICAL: {
					answer[i] = new EmpiricalTimeProbabilityDistribution(seed, getEmpiricalETPDParameter(etpd_element));
					break;
				}
				case EventTimeProbabilityDistributionTypes.NORMAL: {
					double[] parameters = getNormalETPDParameter(etpd_element);
					answer[i] = new NormalTimeProbabilityDistribution(seed, parameters[0], parameters[1]);
					break;
				}
				case EventTimeProbabilityDistributionTypes.SPECIFIC: {
					double[] parameters = getSpecificETPDParameter(etpd_element);
					answer[i] = new SpecificTimeProbabilityDistribution(seed, parameters[0], (int) parameters[1]);
					break;
				}
			}
			
			// configures the new etpd
			answer[i].setObjectId(id);
			answer[i].setNext_bool_counter(next_bool_count);			
		}
		
		// returns the answer
		return answer;
	}
	
	/** Obtains the uniform event time probability distributions from the
	 *  given etpd element.
	 *  @param etpd_element The XML source containing the uniform etpd's parameters.
	 *  @return The uniform etpd parameter. */
	private static double getUniformETPDParameter(Element etpd_element) {
		// obtains the nodes with the "pd_parameter" tag
		NodeList etpd_parameter_nodes = etpd_element.getElementsByTagName("pd_parameter");
		
		// obtains the etpd parameter element
		Element etpd_parameter_element = (Element) etpd_parameter_nodes.item(0);
		
		// returns the parameter value
		return Double.parseDouble(etpd_parameter_element.getAttribute("value"));	
	}
	
	/** Obtains the empirical event time probability distributions from the
	 *  given etpd element.
	 *  @param etpd_element The XML source containing the empirical etpd's parameters.
	 *  @return The empirical etpd parameters. */
	private static double[] getEmpiricalETPDParameter(Element etpd_element) {		
		// obtains the nodes with the "pd_parameter" tag
		NodeList etpd_parameter_nodes = etpd_element.getElementsByTagName("pd_parameter");
		
		// the answer for the method
		double[] answer = new double[etpd_parameter_nodes.getLength()];
		
		// for each ocurrence
		for(int i = 0; i < answer.length; i++) {
			// obtains the current etpd parameter element
			Element etpd_parameter_element = (Element) etpd_parameter_nodes.item(i);
			
			// adds the current parameter value to the answer
			answer[i] =  Double.parseDouble(etpd_parameter_element.getAttribute("value"));
		}
		
		// returns the answer
		return answer;
	}
	
	/** Obtains the normal event time probability distributions from the
	 *  given etpd element.
	 *  @param etpd_element The XML source containing the normal etpd's parameters.
	 *  @return The normal etpd parameters; 1st the mean, 2nd the standard deviation. */
	private static double[] getNormalETPDParameter(Element etpd_element) {
		// obtains the nodes with the "pd_parameter" tag
		NodeList etpd_parameter_nodes = etpd_element.getElementsByTagName("pd_parameter");
		
		// the answer for the method
		double[] answer = new double[2];
		
		// for each ocurrence (mean and standard deviation)
		for(int i = 0; i < 2; i++) {
			// obtains the current etpd parameter element
			Element etpd_parameter_element = (Element) etpd_parameter_nodes.item(i);
			
			// adds the current parameter value to the answer
			answer[i] =  Double.parseDouble(etpd_parameter_element.getAttribute("value"));
		}
		
		// returns the answer
		return answer;
	}
	
	/** Obtains the specific event time probability distribution from the
	 *  given etpd element.
	 *  @param etpd_element The XML source containing the specific etpd's parameters.
	 *  @return The specific etpd parameters; 1st the probability, 2nd the time. */
	private static double[] getSpecificETPDParameter(Element etpd_element) {
		// obtains the nodes with the "pd_parameter" tag
		NodeList etpd_parameter_nodes = etpd_element.getElementsByTagName("pd_parameter");
		
		// the answer for the method
		double[] answer = new double[2];
		
		// for each ocurrence (probability and time)
		for(int i = 0; i < 2; i++) {
			// obtains the current etpd parameter element
			Element etpd_parameter_element = (Element) etpd_parameter_nodes.item(i);
			
			// adds the current parameter value to the answer
			answer[i] =  Double.parseDouble(etpd_parameter_element.getAttribute("value"));
		}
		
		// returns the answer
		return answer;
	}	
}