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

	private DoubleList nodesVisitsCount;
	private DoubleList nodesIdlenesses;
	
	
	public MetricsReport(int nodes, int initialTime, int finalTime, VisitsList list) {
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
		for (int v = 0; v < numNodes; v++) {
			allIntervals.addAll(intervalsByNode[v]);
		}

		nodesIdlenesses = calculateNodeIdlenesses();
	}
	
	private DoubleList calculateIntervals(int node) {
		DoubleList intervals = new DoubleList();
		
		VisitsList nodeVisits = visits.filterByVertex(node);
		
		int lastVisitTime = startTime;
		int interval;
		
		Visit v;
		
		nodesVisitsCount.add(nodeVisits.getNumVisits());
		
		for (int i = 0; i < nodeVisits.getNumVisits(); i ++) {
			v = nodeVisits.getVisit(i);
			
			interval = v.time - lastVisitTime;
			intervals.add(interval);
			
			lastVisitTime = v.time;
		}
		
		interval = endTime + 1 - lastVisitTime;
		intervals.add(interval);
		
		return intervals;
	}
	
	private DoubleList calculateNodeIdlenesses() {
		DoubleList list = new DoubleList(numNodes);
			
		double vertexSumIdlenesses = 0.0d;
		int interval;
			
		for (int node = 0; node < numNodes; node++) {
			
			for (int i = 0; i < intervalsByNode[node].size(); i ++) {
				interval = (int)intervalsByNode[node].get(i);
					
				// during consecutive visits, the idleness grows in an arithmetic progression
				// this is the formula of the sum of this arithmetic progression  
				vertexSumIdlenesses += (interval*interval + interval) / 2; 
			}

			list.add(vertexSumIdlenesses / (endTime - startTime + 1));
			
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

	/**
	 * Average interval between consecutive visits, considering all 
	 * intervals from all nodes.
	 */
	public double getAverageInterval() {
		return allIntervals.mean();
	}

	/**
	 * Standard deviation of the intervals between consecutive visits, 
	 * considering all intervals from all nodes.
	 */
	public double getStdDevInterval() {
		return allIntervals.standardDeviation();
	}
	
	
	/***** Metrics based on idleness *****/

	/**
	 * Maximum instantaneous idleness of all nodes in all time instants.
	 * (It is the same as the maximum interval). 
	 */
	public double getMaxInstantaeousIdleness() {
		return getMaxInterval();
	}
	
	/**
	 * Average idlenesses along the simulation, averaged by the number of nodes
	 * (average of nodes of average in time or vice-versa).  
	 */
	public double getAverageIdleness() {
		return nodesIdlenesses.mean();
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
	public double getStdDevVists() {
		return nodesVisitsCount.standardDeviation();
	}
	
}
