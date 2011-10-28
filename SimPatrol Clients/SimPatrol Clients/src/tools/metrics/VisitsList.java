package tools.metrics;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;


/**
 * This class is a list of visits in a simulation. It is used to 
 * calculate metrics of the simulation (to measure how "efficiently"
 * the nodes were visited).
 * 
 * @author Pablo A. Sampaio
 */
public class VisitsList {
	private List<Visit> visitList;
	
	
	VisitsList() {
		visitList = new LinkedList<Visit>();
	}

	VisitsList(List<Visit> visits) {
		visitList = new ArrayList<Visit>(visits); 
	}

	
	void addVisit(Visit visit) {
		visitList.add(visit);
	}
	
	public int getNumVisits() {
		return visitList.size();
	}
	
	public Visit getVisit(int index) {
		return visitList.get(index);
	}

	public VisitsList filterByAgent(int agent) {
		List<Visit> filteredVisits = new LinkedList<Visit>();
		
		for (Visit visit : visitList) {
			if (visit.agent == agent) {
				filteredVisits.add(visit);
			}
		}
		
		return new VisitsList(filteredVisits);
	}

	public VisitsList filterByVertex(int vertex) {
		List<Visit> filteredVisits = new LinkedList<Visit>();
		
		for (Visit visit : visitList) {
			if (visit.vertex == vertex) {
				filteredVisits.add(visit);
			}
		}
		
		return new VisitsList(filteredVisits);
	}
	
	// inclusive limits (closed interval)
	public VisitsList filterByTime(int from, int to) {
		List<Visit> filteredVisits = new LinkedList<Visit>();
		
		for (Visit visit : visitList) {
			if (visit.time >= from && visit.time <= to) {
				filteredVisits.add(visit);
			}
		}
		
		return new VisitsList(filteredVisits);
	}

	// parameter 'from' is an inclusive limit (closed interval)
	public VisitsList filterByTime(int from) {
		List<Visit> filteredVisits = new LinkedList<Visit>();
		
		for (Visit visit : visitList) {
			if (visit.time >= from) {
				filteredVisits.add(visit);
			}
		}
		
		return new VisitsList(filteredVisits);
	}


}
