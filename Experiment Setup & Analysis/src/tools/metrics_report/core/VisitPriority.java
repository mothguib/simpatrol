package tools.metrics_report.core;

import java.util.HashMap;

public class VisitPriority {
	
	public int priority = 0;
	public int count = 0;
	private double visitRate = 0;
	private double relativeVisitRate = 0;
	private HashMap<Integer, Integer> map;
	
	public VisitPriority() {		
		this.priority = 0;
		this.count =0;
		map = new HashMap<Integer, Integer>();
	}
	
	public VisitPriority(int priority, int count) {		
		this.priority = priority;
		this.count = count;
		map = new HashMap<Integer, Integer>();
	}
	
	public int getCount() {
		return count;
	}

	public void setCount(int count) {
		this.count = count;
	}
	
	public void incCount() {
		this.count++;
	}

	public double getVisitRate() {
		return visitRate;
	}

	public int getPriority() {
		return priority;
	}
	public void setPriority(int priority) {
		this.priority = priority;
	}
	
	public void putNode(int node) {
		map.put(new Integer(node), new Integer(node));
	}
	public boolean contains(int node) {
		return map.containsKey(node);
	}
	public void calcVisitRate(){
		if( map.size() > 0 )
			visitRate = count/map.size();
	}
	public double getRelativeVisitRate() {
		return relativeVisitRate;
	}

	public void calcRelativeVisitRate(double priorityOneRate){
		if( visitRate == 0 ) calcVisitRate();
		if( priorityOneRate != 0)
			relativeVisitRate = visitRate/priorityOneRate;
		else relativeVisitRate = visitRate;
	}
}
