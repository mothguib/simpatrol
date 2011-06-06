package AverageMetrics;

import java.util.LinkedList;
import java.util.List;

import tools.metrics.MetricsReport;



public class AverageMetricsReport {

	List<MetricsReport>  metrics;
	
	public AverageMetricsReport(){
		metrics = new LinkedList<MetricsReport>();
	}
	
	
	public void add(MetricsReport mr){
		metrics.add(mr);
	}
	
	public MetricsReport get(int index){
		if((index>= 0) && (index < metrics.size()))
			return metrics.get(index);
		return null;
	}
	
	public int size(){
		return metrics.size();
	}
	
	
/***** Metrics based on intervals *****/
	
	/**
	 * Maximum interval between consecutive visits, considering all 
	 * intervals from all nodes.
	 */
	public double getAvMaxInterval() {
		double max = 0;
		for(MetricsReport m : metrics)
			max += m.getMaxInterval();
		return max/metrics.size();
	}
	
	public double getAvMaxInterval(int start, int end){
		double av = 0;
		for(MetricsReport m : metrics)
			av += m.getMaxInterval(start, end);
		return av/metrics.size();
	}

	/**
	 * Average interval between consecutive visits, considering all 
	 * intervals from all nodes.
	 */
	public double getAvAverageInterval() {
		double av = 0;
		for(MetricsReport m : metrics)
			av += m.getAverageInterval();
		return av/metrics.size();
	}
	
	public double getAvAverageInterval(int start, int end) {
		double av = 0;
		for(MetricsReport m : metrics)
			av += m.getAverageInterval(start, end);
		return av/metrics.size();
	}

	/**
	 * Standard deviation of the intervals between consecutive visits, 
	 * considering all intervals from all nodes.
	 */
	public double getAvStdDevInterval() {
		double std = 0;
		for(MetricsReport m : metrics)
			std += m.getStdDevOfIntervals();
		return std/metrics.size();
	}
	
	public double getAvStdDevInterval(int start, int end) {
		double std = 0;
		for(MetricsReport m : metrics)
			std += m.getStdDevOfIntervals(start, end);
		return std/metrics.size();
	}
	
	
	public double getAvQuadraticMeanOfIntervals() {
		double std = 0;
		for(MetricsReport m : metrics)
			std += m.getQuadraticMeanOfIntervals();
		return std/metrics.size();
	}
	
	public double getAvQuadraticMeanOfIntervals(int start, int end) {
		double std = 0;
		for(MetricsReport m : metrics)
			std += m.getQuadraticMeanOfIntervals(start, end);
		return std/metrics.size();
	}
	
	
	/***** Metrics based on idleness *****/

	/**
	 * Maximum instantaneous idleness of all nodes in all time instants.
	 * (It is the same as the maximum interval). 
	 */
	public double getAvMaxInstantaneousIdleness() {
		double max = 0;
		for(MetricsReport m : metrics)
			max += m.getMaxInstantaeousIdleness();
		return max/metrics.size();
	}
	
	public double getAvMaxInstantaneousIdleness(int start, int end){
		double max = 0;
		for(MetricsReport m : metrics)
			max += m.getMaxInstantaeousIdleness(start, end);
		return max/metrics.size();
	}
	
	/**
	 * Average idlenesses along the simulation, averaged by the number of nodes
	 * (average of nodes of average in time or vice-versa).  
	 */
	public double getAvAverageIdleness() {
		double av = 0;
		for(MetricsReport m : metrics)
			av += m.getAverageIdleness();
		return av/metrics.size();
		
	}
	
	
	public double getAvAverageIdleness(int start, int end) {
		double av = 0;
		for(MetricsReport m : metrics)
			av += m.getAverageIdleness(start, end);
		return av/metrics.size();
		
	}
	
	/**
	 * Average idlenesses along the simulation, averaged by the number of nodes
	 * (average of nodes of average in time or vice-versa).  
	 */
	public double getAvStdDevOfIdleness() {
		double av = 0;
		for(MetricsReport m : metrics)
			av += m.getStdDevOfIdleness();
		return av/metrics.size();
		
	}
	
	
	public double getAvStdDevOfIdleness(int start, int end) {
		double av = 0;
		for(MetricsReport m : metrics)
			av += m.getStdDevOfIdleness(start, end);
		return av/metrics.size();
		
	}
	
	
	
	/***** Metrics based on the number of visits per node *****/
	
	/**
	 * Total number of visits, considering all nodes.
	 */
	public double getAvTotalVisits() {
		double nb = 0;
		for(MetricsReport m : metrics)
			nb += m.getTotalVisits();
		return nb/metrics.size();
	}
	
	/**
	 * Average number of visits per node.
	 */
	public double getAvAverageVisits() {
		double nb = 0;
		for(MetricsReport m : metrics)
			nb += m.getAverageVisits();
		return nb/metrics.size();
	}
	
	/**
	 * Standard deviation of the numbers of visits per node. 
	 */
	public double getAvStdDevVisits() {
		double std = 0;
		for(MetricsReport m : metrics)
			std += m.getStdDevVisits();
		return std/metrics.size();
	}
	
	
	/**
	 * Exploration time (time at which each node has been visited at least once)
	 * 
	 *  returns -1 if the graph is not explored at the end of the simulation
	 */
	public double getAvExplorationTime() {
		double time = 0;
		for(MetricsReport m : metrics)
			time += m.getExplorationTime();
		return time/metrics.size();

	}
	
	/**
	 *  Normalized Exploration time ( exp_time * nbAgents/nbNodes)
	 * 
	 */
	public double getAvNormExplorationTime(int numAgents) {
		double time = 0;
		for(MetricsReport m : metrics)
			time += m.getNormExplorationTime(numAgents);
		return time/metrics.size();
	}
	
	
	/***** Metrics for curb representation *****/
	
	
	public Double[] getAvIntervals_curb(int freq) {
		Double[] curve = metrics.get(0).getIntervals_curb(freq);
		for(int i = 1; i < metrics.size(); i++){
			Double[] mc = metrics.get(i).getIntervals_curb(freq);
			for(int j = 0; j < curve.length; j++)
				curve[j] += mc[j];
		}
		
		for(int j = 0; j < curve.length; j++)
			curve[j] /= metrics.size();
		
		return curve;

	}
	
	
	public Double[] getAvMSI_curb(int freq) {
		Double[] curve = metrics.get(0).getMSI_curb(freq);
		for(int i = 1; i < metrics.size(); i++){
			Double[] mc = metrics.get(i).getMSI_curb(freq);
			for(int j = 0; j < curve.length; j++)
				curve[j] += mc[j];
		}
		
		for(int j = 0; j < curve.length; j++)
			curve[j] /= metrics.size();
		
		return curve;

	}
	
	/**
	 * Average interval as a function of time, considering all nodes.
	 * @param freq 
	 * 			The average interval is given every freq turn/seconds 
	 * 			(depending on type of simulation)
	 * 
	 * @author Cyril Poulet
	 */
	public Double[] getAvAverageIdleness_curb(int freq) {
		Double[] curve = metrics.get(0).getAverageIdleness_curb(freq);
		for(int i = 1; i < metrics.size(); i++){
			Double[] mc = metrics.get(i).getAverageIdleness_curb(freq);
			for(int j = 0; j < curve.length; j++)
				curve[j] += mc[j];
		}
		
		for(int j = 0; j < curve.length; j++)
			curve[j] /= metrics.size();
		
		return curve;

	}
	
	public Double[] getAvAverageIdleness_curb(int freq, int start, int end) {
		Double[] curve = metrics.get(0).getAverageIdleness_curb(freq, start, end);
		for(int i = 1; i < metrics.size(); i++){
			Double[] mc = metrics.get(i).getAverageIdleness_curb(freq, start, end);
			for(int j = 0; j < curve.length; j++)
				curve[j] += mc[j];
		}
		
		for(int j = 0; j < curve.length; j++)
			curve[j] /= metrics.size();
		
		return curve;

	}
	/**
	 * Maximum interval as a function of time, considering all nodes.
	 * @param freq 
	 * 			The maximum interval is given every freq turn/seconds 
	 * 			(depending on type of simulation)
	 * 
	 * @author Cyril Poulet
	 */
	public Double[] getAvMaxIdleness_curb(int freq) {
		Double[] curve = metrics.get(0).getMaxIdleness_curb(freq);
		for(int i = 1; i < metrics.size(); i++){
			Double[] mc = metrics.get(i).getMaxIdleness_curb(freq);
			for(int j = 0; j < curve.length; j++)
				curve[j] += mc[j];
		}
		
		for(int j = 0; j < curve.length; j++)
			curve[j] /= metrics.size();
		
		return curve;
	}
	
	public Double[] getAvMaxIdleness_curb(int freq, int start, int end) {
		Double[] curve = metrics.get(0).getMaxIdleness_curb(freq, start, end);
		for(int i = 1; i < metrics.size(); i++){
			Double[] mc = metrics.get(i).getMaxIdleness_curb(freq, start, end);
			for(int j = 0; j < curve.length; j++)
				curve[j] += mc[j];
		}
		
		for(int j = 0; j < curve.length; j++)
			curve[j] /= metrics.size();
		
		return curve;
	}
	
	/**
	 * Standart Deviation as a function of time, considering all nodes.
	 * @param freq 
	 * 			The standart deviation in interval is given every freq turn/seconds 
	 * 			(depending on type of simulation)
	 * 
	 * @author Cyril Poulet
	 */
	public Double[] getAvStdDevIdleness_curb(int freq) {
		Double[] curve = metrics.get(0).getStdDevIdleness_curb(freq);
		for(int i = 1; i < metrics.size(); i++){
			Double[] mc = metrics.get(i).getStdDevIdleness_curb(freq);
			for(int j = 0; j < curve.length; j++)
				curve[j] += mc[j];
		}
		
		for(int j = 0; j < curve.length; j++)
			curve[j] /= metrics.size();
		
		return curve;
	}
	
	public Double[] getAvStdDevIdleness_curb(int freq, int start, int end) {
		Double[] curve = metrics.get(0).getStdDevIdleness_curb(freq, start, end);
		for(int i = 1; i < metrics.size(); i++){
			Double[] mc = metrics.get(i).getStdDevIdleness_curb(freq, start, end);
			for(int j = 0; j < curve.length; j++)
				curve[j] += mc[j];
		}
		
		for(int j = 0; j < curve.length; j++)
			curve[j] /= metrics.size();
		
		return curve;
	}
	
	/**
	 * Number of visits as a function of time, considering all nodes.
	 * @param freq 
	 * 			The number of visits so far is given every freq turn/seconds 
	 * 			(depending on type of simulation)
	 * 
	 * @author Cyril Poulet
	 */
	public Double[] getAvVisitsNum_curb(int freq) {		
		Double[] curve = metrics.get(0).getVisitsNum_curb(freq);
		for(int i = 1; i < metrics.size(); i++){
			Double[] mc = metrics.get(i).getVisitsNum_curb(freq);
			for(int j = 0; j < curve.length; j++)
				curve[j] += mc[j];
		}
		
		for(int j = 0; j < curve.length; j++)
			curve[j] /= metrics.size();
		
		return curve;
	}
	
	public Double[] getAvVisitsNum_curb(int freq, int start, int end) {		
		Double[] curve = metrics.get(0).getVisitsNum_curb(freq, start, end);
		for(int i = 1; i < metrics.size(); i++){
			Double[] mc = metrics.get(i).getVisitsNum_curb(freq, start, end);
			for(int j = 0; j < curve.length; j++)
				curve[j] += mc[j];
		}
		
		for(int j = 0; j < curve.length; j++)
			curve[j] /= metrics.size();
		
		return curve;
	}
	
	/**
	 * Average Number of visits as a function of time, considering all nodes.
	 * @param freq 
	 * 			The average number of visits so far is given every freq turn/seconds 
	 * 			(depending on type of simulation)
	 * 
	 * @author Cyril Poulet
	 */
	public Double[] getAvVisitsAvg_curb(int freq) {	
		Double[] curve = metrics.get(0).getVisitsAvg_curb(freq);
		for(int i = 1; i < metrics.size(); i++){
			Double[] mc = metrics.get(i).getVisitsAvg_curb(freq);
			for(int j = 0; j < curve.length; j++)
				curve[j] += mc[j];
		}
		
		for(int j = 0; j < curve.length; j++)
			curve[j] /= metrics.size();
		
		return curve;
	}
	
	public Double[] getAvVisitsAvg_curb(int freq, int start, int end) {	
		Double[] curve = metrics.get(0).getVisitsAvg_curb(freq, start, end);
		for(int i = 1; i < metrics.size(); i++){
			Double[] mc = metrics.get(i).getVisitsAvg_curb(freq, start, end);
			for(int j = 0; j < curve.length; j++)
				curve[j] += mc[j];
		}
		
		for(int j = 0; j < curve.length; j++)
			curve[j] /= metrics.size();
		
		return curve;
	}
	
	/**
	 * Standard Deviation of the Number of visits as a function of time, considering all nodes.
	 * @param freq 
	 * 			The stdDev of the number of visits so far is given every freq turn/seconds 
	 * 			(depending on type of simulation)
	 * 
	 * @author Cyril Poulet
	 */
	public Double[] getAvVisitStdDev_curb(int freq) {	
		Double[] curve = metrics.get(0).getVisitStdDev_curb(freq);
		for(int i = 1; i < metrics.size(); i++){
			Double[] mc = metrics.get(i).getVisitStdDev_curb(freq);
			for(int j = 0; j < curve.length; j++)
				curve[j] += mc[j];
		}
		
		for(int j = 0; j < curve.length; j++)
			curve[j] /= metrics.size();
		
		return curve;

	}
	
	public Double[] getAvVisitStdDev_curb(int freq, int start, int end) {	
		Double[] curve = metrics.get(0).getVisitStdDev_curb(freq, start, end);
		for(int i = 1; i < metrics.size(); i++){
			Double[] mc = metrics.get(i).getVisitStdDev_curb(freq, start, end);
			for(int j = 0; j < curve.length; j++)
				curve[j] += mc[j];
		}
		
		for(int j = 0; j < curve.length; j++)
			curve[j] /= metrics.size();
		
		return curve;

	}
	

		

}
