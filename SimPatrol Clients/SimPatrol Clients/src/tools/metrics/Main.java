package tools.metrics;

import java.awt.Color;
import java.util.LinkedList;

import util.CurveViewer;
import util.DoubleList;
import util.DrawStyle;

public class Main {

	public static void main(String[] args) throws Exception {
		LogFileParser parser = new LogFileParser();
		
		parser.parseFile("/home/pouletc/experimentation/Simulations/corridor/0_25/logs_FBA2/log_4.txt");

	
		int startCycle = 0;
		int finalCycle = 4999;
		
		DoubleList priorities = new DoubleList();
		for(int i = 0; i < parser.getNodePriorities().size(); i++)
			priorities.add(1.0);
		
		MetricsReport metrics = new MetricsReport(parser.getNumNodes(), startCycle, finalCycle,
				parser.getVisitsList(), priorities);
		
		

		/*
		VisitsList mylist = parser.getVisitsList();
		mylist.filterByTime(0, 999);
		VisitsList mylist0 = mylist.filterByAgent(0);
		LinkedList<Integer> mynodes0 = new LinkedList<Integer>();
		
		for(int i = 0; i < mylist0.getNumVisits(); i++){
			if(!mynodes0.contains(mylist0.getVisit(i).vertex))
				mynodes0.add(mylist0.getVisit(i).vertex);
		}
		
		VisitsList mylist1 = mylist.filterByAgent(1);
		LinkedList<Integer> mynodes1 = new LinkedList<Integer>();
		
		for(int i = 0; i < mylist1.getNumVisits(); i++){
			if(!mynodes1.contains(mylist1.getVisit(i).vertex))
				mynodes1.add(mylist1.getVisit(i).vertex);
		}
		
		System.out.println(mynodes0.size());
		System.out.println(mynodes1.size());
		*/
		
		System.out.println();
		System.out.println();
		System.out.println("METRICS:");
		System.out.println();

		System.out.println(" - Maximum interval: " + metrics.getMaxInterval());
		System.out.println(" - Average interval: " + metrics.getAverageInterval());
		System.out.println(" - Standard deviation of the intervals: " + metrics.getStdDevOfIntervals());
		System.out.println(" - Quadratic mean of the intervals: " + metrics.getQuadraticMeanOfIntervals());
		System.out.println();
		System.out.println(" - Maximum instantaneous idleness: " + metrics.getMaxInstantaeousIdleness());
		System.out.println(" - Average idleness: " + metrics.getAverageIdleness());
		System.out.println();
		System.out.println(" - Total number of visits: " + metrics.getTotalVisits());
		System.out.println(" - Average number of visits per node: " + metrics.getAverageVisits());
		System.out.println(" - Standard deviation of the number of visits per node: " + metrics.getStdDevVisits());
		System.out.println(" - Exploration time of the graph: " + metrics.getExplorationTime());
		System.out.println(" - Normalized exploration time of the graph: " + metrics.getNormExplorationTime(parser.getNumAgents()));
		
		
		
		
		int freq = 25;
		Double[] myvalues = metrics.getAverageIdleness_curb(freq);
		Double[] mymaxvalues = metrics.getMaxIdleness_curb(freq);
		Double[] mystdvalues = metrics.getStdDevIdleness_curb(freq);
		Double[] myfreq = new Double[(finalCycle-startCycle)/freq +1];
		
		/*
		Double[] myNumVis = metrics.getVisitsNum_curb(freq);
		Double[] myvisitavg = metrics.getVisitsAvg_curb(freq);
		Double[] myvisitstddev = metrics.getVisitStdDev_curb(freq);
		*/
		Double[][] visitsbynode = metrics.getVisitsNum_bynode_curb(freq);
		
		for(int i = 0; i < myfreq.length; i++)
			myfreq[i] = (double) i * freq;
		
		//System.out.println("- Average interval between t=1000 and t=2999:" + MetricsReport.MeanValue(1000, 2999, myfreq, myvalues));
		
		CurveViewer myviewer = new CurveViewer("Idleness");
		myviewer.addCurve(myfreq, myvalues, Color.blue);
		myviewer.addCurve(myfreq, mymaxvalues, Color.red, DrawStyle.LONG_DOTS);
		myviewer.addCurve(myfreq, mystdvalues, Color.orange, DrawStyle.SHORT_DOTS);
		myviewer.setXdivision(100);
		myviewer.setYdivision(10);
		//myviewer.setYcenter(10);
		myviewer.setVisible(true);
		
		/*
		CurveViewer myviewer2 = new CurveViewer("Visits");
		myviewer2.addCurve(myfreq, myNumVis, Color.black, DrawStyle.ALTERNATE_DOTS);
		myviewer2.addCurve(myfreq, myvisitavg, Color.green, DrawStyle.POINT_CROSS);
		myviewer2.addCurve(myfreq, myvisitstddev, Color.black, DrawStyle.SHORT_DOTS, DrawStyle.POINT_ROUND);
		
		int start = 1000;
		int end = 2999;
		
		System.out.println();
		System.out.println();
		System.out.println("METRICS: " + start + " - " + end);
		System.out.println();

		
		System.out.println(" - Maximum interval: " + metrics.getMaxInterval(start, end));
		System.out.println(" - Average interval: " + metrics.getAverageInterval(start, end));
		System.out.println(" - Standard deviation of the intervals: " + metrics.getStdDevOfIntervals(start, end));
		System.out.println(" - Quadratic mean of the intervals: " + metrics.getQuadraticMeanOfIntervals(start, end));
		System.out.println();
		System.out.println(" - Maximum instantaneous idleness: " + metrics.getMaxInstantaeousIdleness(start, end));
		System.out.println(" - Average idleness: " + metrics.getAverageIdleness(start, end));
		*/
		
	}
	
}
