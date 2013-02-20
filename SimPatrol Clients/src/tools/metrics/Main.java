package tools.metrics;

import java.awt.Color;

import util.CurveViewer;
import util.DoubleList;
import util.DrawStyle;

public class Main {

	public static void main(String[] args) throws Exception {
		LogFileParser parser = new LogFileParser();
		
		parser.parseFile("/home/pouletc/experimentation/Simulations/high_scale/coord/logs_OpenHPCC/3_2/log_6.txt");

	
		int startCycle = 0;
		int finalCycle = 2999;
		
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
		
		
		System.out.println();
		System.out.println();
		System.out.println("METRICS: 1000 - 1500");
		System.out.println();

		System.out.println(" - Maximum interval: " + metrics.getMaxInterval(1000,1499));
		System.out.println(" - Average interval: " + metrics.getAverageInterval(1000,1499));
		System.out.println(" - Standard deviation of the intervals: " + metrics.getStdDevOfIntervals(1000,1499));
		System.out.println(" - Quadratic mean of the intervals: " + metrics.getQuadraticMeanOfIntervals(1000,1499));
		System.out.println();
		System.out.println(" - Maximum instantaneous idleness: " + metrics.getMaxInstantaeousIdleness(1000,1499));
		System.out.println(" - Average idleness: " + metrics.getAverageIdleness(1000,1499));
		System.out.println();

		System.out.println();
		System.out.println();
		System.out.println("METRICS: 3000 - 4000");
		System.out.println();
		
		int min_cycle = 11000;
		int max_cycle = 14000;

		System.out.println(" - Maximum interval: " + metrics.getMaxInterval(min_cycle,max_cycle));
		System.out.println(" - Average interval: " + metrics.getAverageInterval(min_cycle,max_cycle));
		System.out.println(" - Standard deviation of the intervals: " + metrics.getStdDevOfIntervals(min_cycle,max_cycle));
		System.out.println(" - Quadratic mean of the intervals: " + metrics.getQuadraticMeanOfIntervals(min_cycle,max_cycle));
		System.out.println();
		System.out.println(" - Maximum instantaneous idleness: " + metrics.getMaxInstantaeousIdleness(min_cycle,max_cycle));
		System.out.println(" - Average idleness: " + metrics.getAverageIdleness(min_cycle,max_cycle));
		System.out.println();
		
		
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
		myviewer.setXdivision(1000);
		myviewer.setYdivision(25);
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
	
	
	public static void main2(String[] args) throws Exception {
		LogFileParser parser = new LogFileParser();

		
		int startCycle = 0;
		int finalCycle = 19999;
		
		DoubleList priorities;
		
		parser.parseFile("/home/pouletc/experimentation/Simulations/mapA/1_long/logs_HPCC/log_0.txt");
		priorities = new DoubleList();
		for(int i = 0; i < parser.getNodePriorities().size(); i++)
			priorities.add(1.0);
		
		MetricsReport metricsHPCC = new MetricsReport(parser.getNumNodes(), startCycle, finalCycle,
				parser.getVisitsList(), priorities);
		
		parser.parseFile("/home/pouletc/experimentation/Simulations/mapA/1_long/logs_SC/log_0.txt");
		priorities = new DoubleList();
		for(int i = 0; i < parser.getNodePriorities().size(); i++)
			priorities.add(1.0);
		MetricsReport metricsSC = new MetricsReport(parser.getNumNodes(), startCycle, finalCycle,
				parser.getVisitsList(), priorities);
		
		parser.parseFile("/home/pouletc/experimentation/Simulations/mapA/0_long/logs_OpenFBAProximity/8/log_1.txt");
		priorities = new DoubleList();
		for(int i = 0; i < parser.getNodePriorities().size(); i++)
			priorities.add(1.0);
		MetricsReport metricsFBA = new MetricsReport(parser.getNumNodes(), startCycle, finalCycle,
				parser.getVisitsList(), priorities);
		
		
		int freq = 25;
		Double[] myvaluesHPCC = metricsHPCC.getAverageIdleness_curb(freq);
		Double[] myvaluesSC = metricsSC.getAverageIdleness_curb(freq);
		Double[] myvaluesFBA = metricsFBA.getAverageIdleness_curb(freq);
		Double[] myfreq = new Double[(finalCycle-startCycle)/freq +1];
		for(int i = 0; i < myfreq.length; i++)
			myfreq[i] = (double) i * freq;
		
		CurveViewer myviewer = new CurveViewer("Idleness");
		myviewer.addCurve(myfreq, myvaluesHPCC, Color.blue);
		myviewer.addCurve(myfreq, myvaluesSC, Color.red);
		myviewer.addCurve(myfreq, myvaluesFBA, Color.green);
		myviewer.setXdivision(1000);
		myviewer.setYdivision(10);
		myviewer.setVisible(true);
		
		
	}
}
