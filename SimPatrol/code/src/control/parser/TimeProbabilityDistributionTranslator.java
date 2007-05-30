/* TimeProbabilityDistributionTranslator.java */

/* The package of this class. */
package control.parser;

/* Imported classes and/or interfaces. */
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import util.tpd.EmpiricalTimeProbabilityDistribution;
import util.tpd.NormalTimeProbabilityDistribution;
import util.tpd.TimeProbabilityDistribution;
import util.tpd.TimeProbabilityDistributionType;
import util.tpd.UniformTimeProbabilityDistribution;

/** Implements a translator thar obtains time probability distributions
 *  from a given xml source.
 *  @see TimeProbabilityDistribution */
public abstract class TimeProbabilityDistributionTranslator extends Translator {
	/* Methods. */
	/** Obtains the time probability distributions from the
	 *  given xml element.
	 *  @param xml_element The XML source containing the tpds to be translated. */
	public static TimeProbabilityDistribution[] getTimeProbabilityDistribution(Element xml_element) {		
		// obtains the nodes with the "tpd" tag
		NodeList tpd_nodes = xml_element.getElementsByTagName("tpd");
		
		// are there any tpd nodes?
		if(tpd_nodes.getLength() == 0)
			return null;
		
		// the answer of the method
		TimeProbabilityDistribution[] answer = new TimeProbabilityDistribution[2];
		
		// for the two ocurrences
		for(int i = 0; i < 2; i++) {
			// obtains the current tpd element
			Element tpd_element = (Element) tpd_nodes.item(i);
			
			// obtains the data
			String id = tpd_element.getAttribute("id");
			int seed = Integer.parseInt(tpd_element.getAttribute("seed"));
			int next_bool_count = Integer.parseInt(tpd_element.getAttribute("next_bool_count"));
			int type = Integer.parseInt(tpd_element.getAttribute("type"));
			
			// instantiates the new tpd and configures it
			switch(type) {
				case TimeProbabilityDistributionType.UNIFORM: {
					answer[i] = new UniformTimeProbabilityDistribution(seed, getUniformTPDParameter(tpd_element));
					break;
				}
				case TimeProbabilityDistributionType.EMPIRICAL: {
					answer[i] = new EmpiricalTimeProbabilityDistribution(seed, getEmpiricalTPDParameter(tpd_element));
					break;
				}
				case TimeProbabilityDistributionType.NORMAL: {
					double[] parameters = getNormalTPDParameter(tpd_element);
					answer[i] = new NormalTimeProbabilityDistribution(seed, parameters[0], parameters[1]);
					break;
				}
			}
			
			// configures the new tpd
			answer[i].setObjectId(id);
			answer[i].setNext_bool_counter(next_bool_count);			
		}
		
		// returns the answer
		return answer;
	}
	
	/** Obtains the uniform time probability distributions from the
	 *  given tpd element.
	 *  @param tpd_element The XML source containing the uniform tpd's parameters.
	 *  @see UniformTimeProbabilityDistribution */
	private static double getUniformTPDParameter(Element tpd_element) {
		// obtains the nodes with the "tpd_parameter" tag
		NodeList tpd_parameter_nodes = tpd_element.getElementsByTagName("tpd_parameter");
		
		// obtains the tpd parameter element
		Element tpd_parameter_element = (Element) tpd_parameter_nodes.item(0);
		
		// returns the parameter value
		return Double.parseDouble(tpd_parameter_element.getAttribute("value"));	
	}
	
	/** Obtains the empirical time probability distributions from the
	 *  given tpd element.
	 *  @param tpd_element The XML source containing the empirical tpd's parameters.
	 *  @see EmpiricalTimeProbabilityDistribution */
	private static double[] getEmpiricalTPDParameter(Element tpd_element) {		
		// obtains the nodes with the "tpd_parameter" tag
		NodeList tpd_parameter_nodes = tpd_element.getElementsByTagName("tpd_parameter");
		
		// the answer for the method
		double[] answer = new double[tpd_parameter_nodes.getLength()];
		
		// for each ocurrence
		for(int i = 0; i < answer.length; i++) {
			// obtains the current tpd parameter element
			Element tpd_parameter_element = (Element) tpd_parameter_nodes.item(i);
			
			// adds the current parameter value to the answer
			answer[i] =  Double.parseDouble(tpd_parameter_element.getAttribute("value"));
		}
		
		// returns the answer
		return answer;
	}
	
	/** Obtains the normal time probability distributions from the
	 *  given tpd element.
	 *  @param tpd_element The XML source containing the normal tpd's parameters.
	 *  @see NormalTimeProbabilityDistribution */
	private static double[] getNormalTPDParameter(Element tpd_element) {
		// obtains the nodes with the "tpd_parameter" tag
		NodeList tpd_parameter_nodes = tpd_element.getElementsByTagName("tpd_parameter");
		
		// the answer for the method
		double[] answer = new double[2];
		
		// for each ocurrence (mean and standard deviation)
		for(int i = 0; i < 2; i++) {
			// obtains the current tpd parameter element
			Element tpd_parameter_element = (Element) tpd_parameter_nodes.item(i);
			
			// adds the current parameter value to the answer
			answer[i] =  Double.parseDouble(tpd_parameter_element.getAttribute("value"));
		}
		
		// returns the answer
		return answer;
	}
}