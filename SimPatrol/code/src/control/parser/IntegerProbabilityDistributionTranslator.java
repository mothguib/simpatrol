/* IntegerProbabilityDistributionTranslator.java */

/* The package of this class. */
package control.parser;

/* Imported classes and/or interfaces. */
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import util.ipd.EmpiricalIntegerProbabilityDistribution;
import util.ipd.IntegerProbabilityDistribution;
import util.ipd.IntegerProbabilityDistributionTypes;
import util.ipd.NormalIntegerProbabilityDistribution;
import util.ipd.UniformIntegerProbabilityDistribution;

/** Implements a translator thar obtains integer probability distributions
 *  from a given xml source.
 *  @see IntegerProbabilityDistribution */
public abstract class IntegerProbabilityDistributionTranslator extends Translator {
	/* Methods. */
	/** Obtains the integer probability distributions from the
	 *  given xml element.
	 *  @param xml_element The XML source containing the ipds to be translated. */
	public static IntegerProbabilityDistribution[] getIntegerProbabilityDistribution(Element xml_element) {		
		// obtains the nodes with the "ipd" tag
		NodeList ipd_nodes = xml_element.getElementsByTagName("ipd");
		
		// is there any ipd node?
		if(ipd_nodes.getLength() == 0)
			return new IntegerProbabilityDistribution[0];
		
		// the answer of the method
		IntegerProbabilityDistribution[] answer = new IntegerProbabilityDistribution[ipd_nodes.getLength()];
		
		// for all the ocurrences
		for(int i = 0; i < answer.length; i++) {
			// obtains the current ipd element
			Element ipd_element = (Element) ipd_nodes.item(i);
			
			// obtains the data
			String id = ipd_element.getAttribute("id");
			int seed = Integer.parseInt(ipd_element.getAttribute("seed"));			
			int type = Integer.parseInt(ipd_element.getAttribute("type"));
			
			// instantiates the new ipd and configures it
			switch(type) {
				case IntegerProbabilityDistributionTypes.UNIFORM: {
					double[] parameters = getUniformIPDParameters(ipd_element);
					answer[i] = new UniformIntegerProbabilityDistribution(seed, (int) parameters[0], (int) parameters[1], parameters[2]);
					break;
				}
				case IntegerProbabilityDistributionTypes.EMPIRICAL: {
					// obtains the empirical ipd parameters
					double[] parameters = getEmpiricalIPDParameter(ipd_element);
					
					// separates the smallest value from the probabilities
					int smallest_value = (int) parameters[0];
					double[] distribution = new double[parameters.length - 1];
					for(int j = 0; j < distribution.length; j++)
						distribution[j] = parameters[j + 1];
					
					// sets the answer
					answer[i] = new EmpiricalIntegerProbabilityDistribution(seed, distribution, smallest_value);
					break;
				}
				case IntegerProbabilityDistributionTypes.NORMAL: {
					double[] parameters = getNormalIPDParameter(ipd_element);
					answer[i] = new NormalIntegerProbabilityDistribution(seed, (int) parameters[0], parameters[1]);
					break;
				}
			}
			
			// configures the new ipd
			answer[i].setObjectId(id);			
		}
		
		// returns the answer
		return answer;
	}
	
	/** Obtains the uniform integer probability distributions from the
	 *  given ipd element.
	 *  @param ipd_element The XML source containing the uniform ipd's parameters.
	 *  @return The uniform ipd parameters; 1st the smallest_value, 2nd the biggest and 3rd the probability. */
	private static double[] getUniformIPDParameters(Element ipd_element) {
		// obtains the nodes with the "pd_parameter" tag
		NodeList ipd_parameter_nodes = ipd_element.getElementsByTagName("pd_parameter");
		
		// the answer of the method
		double[] answer = new double[3];
		
		// obtains the 3 ipd parameter elements
		for(int i = 0; i < answer.length; i++) {
			Element ipd_parameter_element = (Element) ipd_parameter_nodes.item(i);
			answer[i] = Double.parseDouble(ipd_parameter_element.getAttribute("value"));
		}
				
		// returns the answer
		return answer;	
	}
	
	/** Obtains the empirical integer probability distributions from the
	 *  given ipd element.
	 *  @param ipd_element The XML source containing the empirical ipd's parameters.
	 *  @return The empirical ipd parameters; 1st the smallest value, 2nd the probabilities. */
	private static double[] getEmpiricalIPDParameter(Element ipd_element) {		
		// obtains the nodes with the "pd_parameter" tag
		NodeList ipd_parameter_nodes = ipd_element.getElementsByTagName("pd_parameter");
		
		// the answer for the method
		double[] answer = new double[ipd_parameter_nodes.getLength()];
		
		// for each ocurrence
		for(int i = 0; i < answer.length; i++) {
			// obtains the current ipd parameter element
			Element ipd_parameter_element = (Element) ipd_parameter_nodes.item(i);
			
			// adds the current parameter value to the answer
			answer[i] =  Double.parseDouble(ipd_parameter_element.getAttribute("value"));
		}
		
		// returns the answer
		return answer;
	}
	
	/** Obtains the normal integer probability distributions from the
	 *  given ipd element.
	 *  @param ipd_element The XML source containing the normal ipd's parameters.
	 *  @return The normal ipd parameters; 1st the mean, 2nd the standard deviation. */
	private static double[] getNormalIPDParameter(Element ipd_element) {
		// obtains the nodes with the "pd_parameter" tag
		NodeList ipd_parameter_nodes = ipd_element.getElementsByTagName("pd_parameter");
		
		// the answer for the method
		double[] answer = new double[2];
		
		// for each ocurrence (mean and standard deviation)
		for(int i = 0; i < 2; i++) {
			// obtains the current ipd parameter element
			Element ipd_parameter_element = (Element) ipd_parameter_nodes.item(i);
			
			// adds the current parameter value to the answer
			answer[i] = Double.parseDouble(ipd_parameter_element.getAttribute("value"));
		}
		
		// returns the answer
		return answer;
	}
}
