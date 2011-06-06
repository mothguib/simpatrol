package tools.metrics;

import util.DoubleList;


/**
 * Calculate metrics based on the intervals between 
 * consecutive visits.
 * 
 * @author Pablo A. Sampaio
 */
public class MetricsReport {
	private int numNodes;
	private int startTime, endTime;  // closed interval

	private VisitsList visits;
	
	private DoubleList[] intervalsByNode; // each list has the intervals of a node
	private DoubleList   allIntervals;
	private DoubleList   allWeights; 

	private DoubleList nodesVisitsCount;
	private DoubleList nodesIdlenesses;


	public MetricsReport(int nodes, int initialTime, int finalTime, VisitsList list,
			DoubleList nodePriorities) {
		init(nodes, initialTime, finalTime, list, nodePriorities);
	}

	public MetricsReport(int nodes, int initialTime, int finalTime, VisitsList list) {
		DoubleList priorities = new DoubleList();
		for (int v = 0; v < nodes; v++) {
			priorities.add(1.0d);
		}
		init(nodes, initialTime, finalTime, list, priorities);
	}
	
	private void init(int nodes, int initialTime, int finalTime, VisitsList list,
			DoubleList nodePriorities) {
		numNodes = nodes;
		startTime = initialTime;
		endTime = finalTime;
		
		visits = list.filterByTime(startTime, endTime);
		
		intervalsByNode = new DoubleList[nodes];
		nodesVisitsCount = new DoubleList(nodes);
		
		int sumSize = 0;
		
		for (int v = 0; v < numNodes; v++) {
			intervalsByNode[v] = calculateIntervals(v);
			sumSize += intervalsByNode[v].size();
		}

		allIntervals = new DoubleList(sumSize);
		allWeights = new DoubleList(sumSize);
		
		for (int v = 0; v < numNodes; v++) {
			allIntervals.addAll(intervalsByNode[v]);
			
			for (int i = 0; i < intervalsByNode[v].size(); i++) {
				allWeights.add(nodePriorities.get(v));
			}
		}

		nodesIdlenesses = calculateNodeIdlenesses();
	}

	
	private DoubleList calculateIntervals(int node) {
		DoubleList intervals = new DoubleList();
		
		VisitsList nodeVisits = visits.filterByVertex(node);
		
		int lastVisitTime = startTime;
		double interval;
		
		Visit v;
		
		nodesVisitsCount.add(nodeVisits.getNumVisits());
		
		for (int i = 0; i < nodeVisits.getNumVisits(); i ++) {
			v = nodeVisits.getVisit(i);
			
			interval = (v.time - lastVisitTime);
			intervals.add(interval);
			
			lastVisitTime = v.time;
		}
		
		interval = (endTime + 1 - lastVisitTime);
		intervals.add(interval);
		
		return intervals;
	}
	
	private DoubleList calculateNodeIdlenesses() {
		DoubleList list = new DoubleList(numNodes);
			
		double vertexSumIdlenesses = 0.0d;
		int interval;
			
		for (int node = 0; node < numNodes; node++) {
			
			vertexSumIdlenesses= 0.0d;
			
			for (int i = 0; i < intervalsByNode[node].size(); i ++) {
				interval = (int)intervalsByNode[node].get(i);
					
				// during consecutive visits, the idleness grows in an arithmetic progression
				// this is the formula of the sum of this arithmetic progression  
				vertexSumIdlenesses += (interval*interval - interval) / 2; 
			}

			list.add(vertexSumIdlenesses / (endTime - startTime + 1));
			
		}

		return list;
	}

	
	private DoubleList calculateNodeIdlenesses(int start, int end) {
		DoubleList list = new DoubleList(numNodes);
			
		double vertexSumIdlenesses = 0.0d;
		double elapsed = 0;
		int interval;
			
		for (int node = 0; node < numNodes; node++) {
			
			elapsed = 0;
			vertexSumIdlenesses= 0.0d;
			
			for (int i = 0; i < intervalsByNode[node].size(); i ++) {
				interval = (int)intervalsByNode[node].get(i);
				elapsed += interval;
				if(elapsed > end){
					if(elapsed - interval <= end){
						double diff = end - (elapsed - interval);
						vertexSumIdlenesses += (diff*diff - diff) / 2; 
					}
					break;
				}
				if(elapsed > start){
					interval = (int)intervalsByNode[node].get(i);
					if(elapsed - interval < start){
						double diff = start - (elapsed - interval) - 1;
						vertexSumIdlenesses -= (diff*diff - diff) / 2; 
					}
						
					// during consecutive visits, the idleness grows in an arithmetic progression
					// this is the formula of the sum of this arithmetic progression  
					vertexSumIdlenesses += (interval*interval - interval) / 2; 
				}
			}

			list.add(vertexSumIdlenesses / (end - start + 1));
			
		}

		return list;
	}
	
	/***** Metrics based on intervals *****/
	
	/**
	 * Maximum interval between consecutive visits, considering all 
	 * intervals from all nodes.
	 */
	public double getMaxInterval() {
		return allIntervals.max();
	}
	
	
	public double getMaxInterval(int start, int end){
		DoubleList intervals = new DoubleList();
		for(int i = 0; i < intervalsByNode.length; i++){
			Double sum = 0d;
			for(int j = 0; j < intervalsByNode[i].size(); j++){
				sum += intervalsByNode[i].get(j);
				if(sum > end)
					break;	
				if(sum > start)
					intervals.add(intervalsByNode[i].get(j));	
			}
		}
		
		return intervals.max();
	}
	

	/**
	 * Average interval between consecutive visits, considering all 
	 * intervals from all nodes.
	 */
	public double getAverageInterval() {
		return allIntervals.mean();
	}
	
	public double getAverageInterval(int start, int end){
		DoubleList intervals = new DoubleList();
		for(int i = 0; i < intervalsByNode.length; i++){
			Double sum = 0d;
			for(int j = 0; j < intervalsByNode[i].size(); j++){
				sum += intervalsByNode[i].get(j);
				if(sum > end)
					break;
				if(sum > start)
					intervals.add(intervalsByNode[i].get(j));		
			}
		}
		
		return intervals.mean();
	}

	/**
	 * Standard deviation of the intervals between consecutive visits, 
	 * considering all intervals from all nodes.
	 */
	public double getStdDevOfIntervals() {
		return allIntervals.standardDeviation();
	}
	
	public double getStdDevOfIntervals(int start, int end){
		DoubleList intervals = new DoubleList();
		for(int i = 0; i < intervalsByNode.length; i++){
			Double sum = 0d;
			for(int j = 0; j < intervalsByNode[i].size(); j++){
				sum += intervalsByNode[i].get(j);
				if(sum > end)
					break;
				if(sum > start)
					intervals.add(intervalsByNode[i].get(j));		
			}
		}
		
		return intervals.standardDeviation();
	}
	
	/**
	 * Quadratic mean of the intervals between consecutive visits,
	 * considering all intervals from all nodes.
	 */
	public double getQuadraticMeanOfIntervals() {
		return allIntervals.generalizedMean(2.0d, this.allWeights);
	}
	
	
	public double getQuadraticMeanOfIntervals(int start, int end){
		DoubleList intervals = new DoubleList();
		for(int i = 0; i < intervalsByNode.length; i++){
			Double sum = 0d;
			for(int j = 0; j < intervalsByNode[i].size(); j++){
				sum += intervalsByNode[i].get(j);
				if(sum > end)
					break;
				if(sum > start)
					intervals.add(intervalsByNode[i].get(j));		
			}
		}
		
		
		DoubleList new_weights = new DoubleList();
		for(int i = 0; i < intervals.size(); i++)
			new_weights.add(1.0);
		
		return intervals.generalizedMean(2.0d, new_weights);
	}
	
	
	/**
	 * Generalized mean of the intervals between consecutive visits,
	 * considering all intervals from all nodes.
	 * The priorities of the nodes are used as weights for each interval.
	 */
	public double getGeneralizedMeanOfIntervals(double p) {
		return allIntervals.generalizedMean(p, this.allWeights);
	}
	
	public double getGeneralizedMeanOfIntervals(double p, int start, int end){
		DoubleList intervals = new DoubleList();
		for(int i = 0; i < intervalsByNode.length; i++){
			Double sum = 0d;
			for(int j = 0; j < intervalsByNode[i].size(); j++){
				sum += intervalsByNode[i].get(j);
				if(sum > end)
					break;
				if(sum > start)
					intervals.add(intervalsByNode[i].get(j));		
			}
		}
		
		DoubleList new_weights = new DoubleList();
		for(int i = 0; i < intervals.size(); i++)
			new_weights.add(1.0);
		
		return intervals.generalizedMean(p, new_weights);
	}
	
	
	/***** Metrics based on idleness *****/

	/**
	 * Maximum instantaneous idleness of all nodes in all time instants.
	 * (It is the same as the maximum interval). 
	 */
	public double getMaxInstantaeousIdleness() {
		return getMaxInterval();
	}
	
	public double getMaxInstantaeousIdleness(int start, int end){
		return getMaxInterval(start, end);
	}
	
	
	/**
	 * Average idlenesses along the simulation, averaged by the number of nodes
	 * (average of nodes of average in time or vice-versa).
	 */
	public double getAverageIdleness() {
		return nodesIdlenesses.mean();
	}
	
	public double getAverageIdleness(int start, int end) {
		return this.calculateNodeIdlenesses(start, end).mean();
	}
	
	/**
	 * standart deviation of the idlenesses along the simulation, averaged by the number of nodes
	 * (average of nodes of average in time or vice-versa).  
	 */
	public double getStdDevOfIdleness() {
		return nodesIdlenesses.standardDeviation();
	}
	
	public double getStdDevOfIdleness(int start, int end) {
		return this.calculateNodeIdlenesses(start, end).standardDeviation();
	}
	
	
	/***** Metrics based on the number of visits per node *****/
	
	/**
	 * Total number of visits, considering all nodes.
	 */
	public double getTotalVisits() {
		return nodesVisitsCount.sum();
	}
	
	/**
	 * Average number of visits per node.
	 */
	public double getAverageVisits() {
		return nodesVisitsCount.mean();
	}
	
	/**
	 * Standard deviation of the numbers of visits per node. 
	 */
	public double getStdDevVisits() {
		return nodesVisitsCount.standardDeviation();
	}
	
	
	/**
	 * Exploration time (time at which each node has been visited at least once)
	 * 
	 *  returns -1 if the graph is not explored at the end of the simulation
	 */
	public double getExplorationTime() {
		boolean[] explored = new boolean[numNodes]; //all false, initially
		
		int numVis = 0;
		Visit visit ;
		boolean test = true;
		
		while(numVis < visits.getNumVisits()){
			visit = visits.getVisit(numVis);
			if(!explored[visit.vertex]){
				explored[visit.vertex] = true;
				test = true;
				for(boolean exp : explored)
					test &= exp;
				if(test)
					return visit.time;			
			}
			numVis++;
		}
		
		return -1;
	}
	
	/**
	 *  Normalized Exploration time ( exp_time * nbAgents/nbNodes)
	 * 
	 */
	public double getNormExplorationTime(int numAgents) {
		double exp_time = getExplorationTime();
		if(exp_time == -1)
			return -1;
		
		return exp_time * numAgents / numNodes;
	}
	
	
	/***** Metrics for curb representation *****/
	
	/**
	 * Average Interval as a function of time, considering all nodes.
	 * @param freq 
	 * 			The average Interval is given every freq turn/seconds 
	 * 			(depending on type of simulation)
	 * 
	 * @author Cyril Poulet
	 */
	public Double[] getIntervals_curb(int freq) {
		int[] idlenesses = new int[numNodes];		
		for(int i = 0; i < numNodes; i++)
			idlenesses[i] = -1;
		Double[] values = new Double[(endTime - startTime)/freq + 1];
		for(int i = 0; i < values.length; i++)
				values[i] = 0.0;
		int[] nbvalues = new int[(endTime - startTime)/freq + 1];
		
		int numVis = 0;
		Visit visit = visits.getVisit(numVis);
		
		for(int i = startTime; i<= endTime; i++){
			for(int j = 0; j < numNodes; j++)
				idlenesses[j]++;
			while(visit.time == i){
				values[(i - startTime)/freq] += idlenesses[visit.vertex];
				nbvalues[(i - startTime)/freq]++;
				idlenesses[visit.vertex] = 0;
				
				numVis++;
				if(numVis < visits.getNumVisits())
					visit = visits.getVisit(numVis);
				else
					break;
			}
		}
		
		for(int i = 0; i < values.length; i++){
			if(nbvalues[i] != 0)
				values[i] /= nbvalues[i];
			else
				values[i] = 0.0;
		}
		
		return values;
	}
	
	
	
	public Double[] getMSI_curb(int freq) {
		int[] idlenesses = new int[numNodes];		
		for(int i = 0; i < numNodes; i++)
			idlenesses[i] = -1;
		Double[] values = new Double[(endTime - startTime)/freq + 1];
		for(int i = 0; i < values.length; i++)
				values[i] = 0.0;
		int[] nbvalues = new int[(endTime - startTime)/freq + 1];
		
		int numVis = 0;
		Visit visit = visits.getVisit(numVis);
		
		for(int i = startTime; i<= endTime; i++){
			for(int j = 0; j < numNodes; j++)
				idlenesses[j]++;
			while(visit.time == i){
				values[(i - startTime)/freq] += idlenesses[visit.vertex]*idlenesses[visit.vertex];
				nbvalues[(i - startTime)/freq]++;
				idlenesses[visit.vertex] = 0;
				
				numVis++;
				if(numVis < visits.getNumVisits())
					visit = visits.getVisit(numVis);
				else
					break;
			}
		}
		
		for(int i = 0; i < values.length; i++){
			if(nbvalues[i] != 0){
				values[i] /= nbvalues[i];
				values[i] = Math.sqrt(values[i]);
			}
			else
				values[i] = 0.0;
		}
		
		return values;
	}
	
	
	/**
	 * Average Interval as a function of time, considering all nodes.
	 * @param freq 
	 * 			The average Interval is given every freq turn/seconds 
	 * 			(depending on type of simulation)
	 * 
	 * @author Cyril Poulet
	 */
	public Double[] getAverageIdleness_curb(int freq) {
		int[] idlenesses = new int[numNodes];		
		for(int i = 0; i < numNodes; i++)
			idlenesses[i] = -1;
		Double[] values = new Double[(endTime - startTime)/freq + 1];
		
		int numVis = 0;
		Visit visit = visits.getVisit(numVis);
		
		for(int i = startTime; i<= endTime; i++){
			for(int j = 0; j < numNodes; j++)
				idlenesses[j]++;
			while(visit.time == i){
				idlenesses[visit.vertex] = 0;
				numVis++;
				if(numVis < visits.getNumVisits())
					visit = visits.getVisit(numVis);
				else
					break;
			}
			
			if(i % freq == 0){
				double sum = 0.0;
				for(int interval : idlenesses)
					sum += interval;
				
				values[(i - startTime)/freq] = sum/numNodes;
			}			
		}
		
		return values;
	}
	
	
	public Double[] getAverageIdleness_curb(int freq, int start, int end){
		Double[] curb = getAverageIdleness_curb(freq);
		
		Double[] new_curb = new Double[(end-start)/freq + 1];
		int start_index = (start / freq) + ((start % freq == 0)? 0 : 1);
		for(int i = start_index; i <= end / freq ; i++){
			new_curb[i - start_index] = curb[i];
		}
		
		return new_curb;
	}
	/**
	 * Maximum Interval as a function of time, considering all nodes.
	 * @param freq 
	 * 			The maximum Interval is given every freq turn/seconds 
	 * 			(depending on type of simulation)
	 * 
	 * @author Cyril Poulet
	 */
	public Double[] getMaxIdleness_curb(int freq) {
		int[] idlenesses = new int[numNodes];		
		for(int i = 0; i < numNodes; i++)
			idlenesses[i] = -1;
		Double[] values = new Double[(endTime - startTime)/freq + 1 +
		                             (((endTime - startTime) % freq ==0)? 1 : 0)];
		
		int numVis = 0;
		Visit visit = visits.getVisit(numVis);
		
		for(int i = startTime; i<= endTime; i++){
			for(int j = 0; j < numNodes; j++)
				idlenesses[j]++;
			while(visit.time == i){
				idlenesses[visit.vertex] = 0;
				numVis++;
				if(numVis < visits.getNumVisits())
					visit = visits.getVisit(numVis);
				else
					break;
			}
			
			if(i % freq == 0){
				double max = idlenesses[0];
				for(int interval : idlenesses)
					if(interval > max)
						max = interval;
				
				values[(i - startTime)/freq] = max;
			}			
		}
		
		return values;
	}
	
	
	public Double[] getMaxIdleness_curb(int freq, int start, int end){
		Double[] curb = getMaxIdleness_curb(freq);
		
		Double[] new_curb = new Double[(end-start)/freq + 1];
		int start_index = (start / freq) + ((start % freq == 0)? 0 : 1);
		for(int i = start_index; i <= end / freq ; i++){
			new_curb[i - start_index] = curb[i];
		}
		
		return new_curb;
	}
	/**
	 * Standart Deviation of Intervals as a function of time, considering all nodes.
	 * @param freq 
	 * 			The standart deviation in Interval is given every freq turn/seconds 
	 * 			(depending on type of simulation)
	 * 
	 * @author Cyril Poulet
	 */
	public Double[] getStdDevIdleness_curb(int freq) {
		int[] idlenesses = new int[numNodes];	
		for(int i = 0; i < numNodes; i++)
			idlenesses[i] = -1;
		Double[] values = new Double[(endTime - startTime)/freq + 1 +
		                             (((endTime - startTime) % freq ==0)? 1 : 0)];
		
		int numVis = 0;
		Visit visit = visits.getVisit(numVis);
		
		for(int i = startTime; i<= endTime; i++){
			for(int j = 0; j < numNodes; j++)
				idlenesses[j]++;
			while(visit.time == i){
				idlenesses[visit.vertex] = 0;
				numVis++;
				if(numVis < visits.getNumVisits())
					visit = visits.getVisit(numVis);
				else
					break;
			}
			
			if(i % freq == 0){
				double sum = 0.0;
				for(int interval : idlenesses)
					sum += interval;
				
				double stddev = 0;
				double diff = 0;
				for(int interval : idlenesses){
					diff = (interval - sum/numNodes);
					stddev += diff*diff;
				}
					
				
				values[(i - startTime)/freq] = Math.sqrt(stddev/numNodes);
			}			
		}
		
		return values;
	}
	
	public Double[] getStdDevIdleness_curb(int freq, int start, int end){
		Double[] curb = getStdDevIdleness_curb(freq);
		
		Double[] new_curb = new Double[(end-start)/freq + 1];
		int start_index = (start / freq) + ((start % freq == 0)? 0 : 1);
		for(int i = start_index; i <= end / freq ; i++){
			new_curb[i - start_index] = curb[i];
		}
		
		return new_curb;
	}
	
	
	
	/**
	 * Number of visits as a function of time, considering all nodes.
	 * @param freq 
	 * 			The number of visits so far is given every freq turn/seconds 
	 * 			(depending on type of simulation)
	 * 
	 * @author Cyril Poulet
	 */
	public Double[] getVisitsNum_curb(int freq) {		
		Double[] values = new Double[(endTime - startTime)/freq + 1 +
		                             (((endTime - startTime) % freq ==0)? 1 : 0)];
		
		int numVis = 0;
		Visit visit = visits.getVisit(numVis);
		
		for(int i = startTime; i<= endTime; i++){
			while(visit.time == i){
				numVis++;
				if(numVis < visits.getNumVisits())
					visit = visits.getVisit(numVis);
				else
					break;
			}
			
			if(i % freq == 0){
				values[(i - startTime)/freq] = numVis/1.0;
			}			
		}
		
		return values;
	}
	
	public Double[] getVisitsNum_curb(int freq, int start, int end){
		Double[] curb = getVisitsNum_curb(freq);
		
		Double[] new_curb = new Double[(end-start)/freq + 1];
		int start_index = (start / freq) + ((start % freq == 0)? 0 : 1);
		for(int i = start_index; i <= end / freq ; i++){
			new_curb[i - start_index] = curb[i];
		}
		
		return new_curb;
	}
	/**
	 * Average Number of visits as a function of time, considering all nodes.
	 * @param freq 
	 * 			The average number of visits so far is given every freq turn/seconds 
	 * 			(depending on type of simulation)
	 * 
	 * @author Cyril Poulet
	 */
	public Double[] getVisitsAvg_curb(int freq) {	
		int[] visitnums = new int[numNodes];
		Double[] values = new Double[(endTime - startTime)/freq + 1 +
		                             (((endTime - startTime) % freq ==0)? 1 : 0)];
		
		int numVis = 0;
		Visit visit = visits.getVisit(numVis);
		
		for(int i = startTime; i<= endTime; i++){
			while(visit.time == i){
				visitnums[visit.vertex]++;
				numVis++;
				if(numVis < visits.getNumVisits())
					visit = visits.getVisit(numVis);
				else
					break;
			}
			
			if(i % freq == 0){
				Double sum = 0.0;
				for(int visitnum : visitnums)
					sum += visitnum;
				values[(i-startTime)/freq] = sum/numNodes;
					
			}			
		}
		
		return values;
	}
	
	public Double[] getVisitsAvg_curb(int freq, int start, int end){
		Double[] curb = getVisitsAvg_curb(freq);
		
		Double[] new_curb = new Double[(end-start)/freq + 1];
		int start_index = (start / freq) + ((start % freq == 0)? 0 : 1);
		for(int i = start_index; i <= end / freq ; i++){
			new_curb[i - start_index] = curb[i];
		}
		
		return new_curb;
	}
	/**
	 * Standard Deviation of the Number of visits as a function of time, considering all nodes.
	 * @param freq 
	 * 			The stdDev of the number of visits so far is given every freq turn/seconds 
	 * 			(depending on type of simulation)
	 * 
	 * @author Cyril Poulet
	 */
	public Double[] getVisitStdDev_curb(int freq) {	
		int[] visitnums = new int[numNodes];
		Double[] values = new Double[(endTime - startTime)/freq + 1 +
		                             (((endTime - startTime) % freq ==0)? 1 : 0)];
		
		int numVis = 0;
		Visit visit = visits.getVisit(numVis);
		
		for(int i = startTime; i<= endTime; i++){
			while(visit.time == i){
				visitnums[visit.vertex]++;
				numVis++;
				if(numVis < visits.getNumVisits())
					visit = visits.getVisit(numVis);
				else
					break;
			}
			
			if(i % freq == 0){
				double sum = 0.0;
				for(int visitnum : visitnums)
					sum += visitnum;
				
				double stddev = 0;
				double diff = 0;
				for(int visitnum : visitnums){
					diff = (visitnum - sum/numNodes);
					stddev += diff*diff;
				}
				
				
				values[(i - startTime)/freq] = Math.sqrt(stddev/numNodes);
					
			}			
		}
		
		return values;
	}
	
	public Double[] getVisitStdDev_curb(int freq, int start, int end){
		Double[] curb = getVisitStdDev_curb(freq);
		
		Double[] new_curb = new Double[(end-start)/freq + 1];
		int start_index = (start / freq) + ((start % freq == 0)? 0 : 1);
		for(int i = start_index; i <= end / freq ; i++){
			new_curb[i - start_index] = curb[i];
		}
		
		return new_curb;
	}
	/**
	 * Number of visits as a function of time, node by node
	 * @param freq 
	 * 			The numbers of visits per node are given every freq turn/seconds 
	 * 			(depending on type of simulation)
	 * 
	 * @return Double[][] 
	 * 			first dim : nodes
	 * 			second dim : visits by time on considered node
	 * 
	 * @author Cyril Poulet
	 */
	public Double[][] getVisitsNum_bynode_curb(int freq) {	
		int[] visitnums = new int[numNodes];
		Double[][] values = new Double[numNodes][(endTime - startTime)/freq + 1 +
		                             (((endTime - startTime) % freq ==0)? 1 : 0)];
		
		int numVis = 0;
		Visit visit = visits.getVisit(numVis);
		
		for(int i = startTime; i<= endTime; i++){
			while(visit.time == i){
				visitnums[visit.vertex]++;
				numVis++;
				if(numVis < visits.getNumVisits())
					visit = visits.getVisit(numVis);
				else
					break;
			}
			
			if(i % freq == 0){
				for(int j = 0; j < numNodes; j++)
					values[j][(i - startTime)/freq] = visitnums[j]/1.0;
			}			
		}
		
		return values;
	}
	
	public Double[][] getVisitsNum_bynode_curb(int freq, int start, int end){
		Double[][] curb = getVisitsNum_bynode_curb(freq);
		
		Double[][] new_curb = new Double[numNodes][(end-start)/freq + 1];
		int start_index = (start / freq) + ((start % freq == 0)? 0 : 1);
		
		for(int node = 0; node < numNodes; node++){
			for(int i = start_index; i <= end / freq ; i++){
				new_curb[node][i - start_index] = curb[node][i];
			}
		}
		
		return new_curb;
	}
	
	
	public static Double MeanValue(int start, int end, Double[] freq, Double[] values){
		if(freq.length != values.length)
			return Double.MAX_VALUE;
		
		Double mean = 0.0d;
		int numvalues = 0;
		boolean first = true;
		
		for(int i = 0; i < freq.length; i++){
			if(freq[i] >= start && freq[i] < end){
				if(first){
					mean = values[i];
					first = false;
				} else
					mean += values[i];
				numvalues++;
			}
		}
		
		return mean / numvalues;
	}
	
	public static Double TimeToReachTargetValue(Double target, Double dev, int start, int end, int nbVal, Double[] freq, Double[] values){
		if(freq.length != values.length)
			return -1.0;
		
		
		for(int i = 0; i < freq.length - nbVal; i++){
			if(freq[i] > start && freq[i] < end){
				Double mean = MeanValue(freq[i].intValue(), freq[i + nbVal].intValue(), freq, values);
				if((Math.abs(mean) >= Math.abs(target) * (1 - dev)) && (Math.abs(mean) <= Math.abs(target)*(1 + dev))){
					return freq[i];
				}
			}
		}
		
		return freq[freq.length - 1] + 1;
		
	}
	
}
