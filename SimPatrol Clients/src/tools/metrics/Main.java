package tools.metrics;

import java.awt.Color;

import util.CurveViewer;
import util.DrawStyle;

public class Main {

	public static void main(String[] args) throws Exception {
		LogFileParser parser = new LogFileParser();
		
		parser.parseFile("tmp/simlog.log");
	
		int startCycle = 0;
		int finalCycle = 100;
		MetricsReport metrics = new MetricsReport(parser.getNumNodes(), startCycle, finalCycle,
				parser.getVisitsList(), parser.getNodePriorities());

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
		
		
		int freq = 1;
		Double[] myvalues = metrics.getAverageIdleness_curb(freq);
		Double[] mymaxvalues = metrics.getMaxIdleness_curb(freq);
		Double[] mystdvalues = metrics.getStdDev_curb(freq);
		Double[] myfreq = new Double[(finalCycle-startCycle)/freq +1];
		
		Double[] myNumVis = metrics.getVisitsNum_curb(freq);
		Double[] myvisitavg = metrics.getVisitsAvg_curb(freq);
		Double[] myvisitstddev = metrics.getVisitStdDev_curb(freq);
		
		Double[][] visitsbynode = metrics.getVisitsNum_bynode_curb(freq);
		
		for(int i = 0; i < myfreq.length; i++)
			myfreq[i] = (double) i * freq;
		
		CurveViewer myviewer = new CurveViewer("Idleness");
		myviewer.addCurve(myfreq, myvalues, Color.blue);
		myviewer.addCurve(myfreq, mymaxvalues, Color.red, DrawStyle.LONG_DOTS);
		myviewer.addCurve(myfreq, mystdvalues, Color.orange, DrawStyle.SHORT_DOTS);
		myviewer.setXdivision(10);
		myviewer.setYdivision(5);
		myviewer.setVisible(true);
		
		
		CurveViewer myviewer2 = new CurveViewer("Visits");
		myviewer2.addCurve(myfreq, myNumVis, Color.black, DrawStyle.ALTERNATE_DOTS);
		myviewer2.addCurve(myfreq, myvisitavg, Color.green, DrawStyle.POINT_CROSS);
		myviewer2.addCurve(myfreq, myvisitstddev, Color.black, DrawStyle.SHORT_DOTS, DrawStyle.POINT_ROUND);
		
		myviewer2.setXdivision(10);
		myviewer2.setYdivision(5);
		myviewer2.setVisible(true);
		
		CurveViewer myviewer3 = new CurveViewer("Visits");
		myviewer3.addCurve(myfreq, visitsbynode[0], Color.blue);
		myviewer3.addCurve(myfreq, visitsbynode[1], Color.black);
		
		myviewer3.setXdivision(10);
		myviewer3.setYdivision(5);
		myviewer3.setVisible(true);
		
		
	}
	
}
