package AverageMetrics;

import java.awt.Color;

import tools.metrics.LogFileParser;
import tools.metrics.MetricsReport;
import util.CurveViewer;
import util.DrawStyle;

public class Main {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		
		String Log_dir = args[0];
		String Log_gen_name = args[1];
		int log_num = Integer.parseInt(args[2]);
		int cycle_num = Integer.parseInt(args[3]);
	
		Log_dir = "/home/pouletc/experimentation/Simulations/corridor_corrige/0_long/logs_Minimax3";
		cycle_num = 19999;
		log_num = 15;
		
		AverageMetricsReport MyAvReport = new AverageMetricsReport();
		
		LogFileParser parser;
		MetricsReport metrics;
		
		int num_agents = 5;
		
		for(int i = 0; i < log_num; i++){
			try{
				parser = new LogFileParser();
				
				parser.parseFile(Log_dir + "/" + Log_gen_name + "_" + i + ".txt");
				
				//num_agents = parser.getNumAgents();
	
				metrics = new MetricsReport(parser.getNumNodes(), 0, cycle_num, parser.getVisitsList());
				MyAvReport.add(metrics);
				System.out.println("log "+ i + " read");
				
			} catch (Exception e){
				continue;

			}
		}
			
		System.out.println("Global :");
		System.out.println(" - Maximum interval: " + MyAvReport.getAvMaxInterval());
		System.out.println(" - Average interval: " + MyAvReport.getAvAverageInterval());
		System.out.println(" - Standard deviation of the intervals: " + MyAvReport.getAvStdDevInterval());
		System.out.println(" - MSI: " + MyAvReport.getAvQuadraticMeanOfIntervals());
		
		System.out.println();
		System.out.println(" - Maximum instantaneous idleness: " + MyAvReport.getAvMaxInstantaneousIdleness());
		System.out.println(" - Average idleness: " + MyAvReport.getAvAverageIdleness());
		System.out.println(" - Standard deviation of idleness: " + MyAvReport.getAvStdDevOfIdleness());
		System.out.println();
		System.out.println(" - Total number of visits: " + MyAvReport.getAvTotalVisits());
		System.out.println(" - Average number of visits per node: " + MyAvReport.getAvAverageVisits());
		System.out.println(" - Standard deviation of the number of visits per node: " + MyAvReport.getAvStdDevVisits());
		System.out.println(" - Exploration time of the graph: " + MyAvReport.getAvExplorationTime());
		System.out.println(" - Normalized exploration time of the graph: " + MyAvReport.getAvNormExplorationTime(num_agents));
		
		int start = 2000;
		int end = 3000;
		
		System.out.println("Stabilized :");
		System.out.println(" - Maximum interval: " + MyAvReport.getAvMaxInterval(start, end));
		System.out.println(" - Average interval: " + MyAvReport.getAvAverageInterval(start, end));
		System.out.println(" - Standard deviation of the intervals: " + MyAvReport.getAvStdDevInterval(start, end));
		System.out.println(" - MSI: " + MyAvReport.getAvQuadraticMeanOfIntervals(start, end));
		System.out.println();
		System.out.println(" - Maximum instantaneous idleness: " + MyAvReport.getAvMaxInstantaneousIdleness(start, end));
		System.out.println(" - Average idleness: " + MyAvReport.getAvAverageIdleness(start, end));
		System.out.println(" - Standard deviation of idleness: " + MyAvReport.getAvStdDevOfIdleness(start, end));
		
		
		int freq = 25;
		Double[] myfreq = new Double[cycle_num/freq +1];
		for(int i = 0; i < myfreq.length; i++)
			myfreq[i] = (double) i * freq;
		
		Double[] myvalues = MyAvReport.getAvAverageIdleness_curb(freq);
		Double[] mymaxvalues = MyAvReport.getAvMaxIdleness_curb(freq);
		Double[] mystdvalues = MyAvReport.getAvStdDevIdleness_curb(freq);
		
		
		CurveViewer myviewer = new CurveViewer("Idleness");
		myviewer.addCurve(myfreq, myvalues, Color.blue);
		myviewer.addCurve(myfreq, mymaxvalues, Color.red, DrawStyle.LONG_DOTS);
		myviewer.addCurve(myfreq, mystdvalues, Color.orange, DrawStyle.SHORT_DOTS);
		myviewer.setXdivision(250);
		myviewer.setYdivision(50);
		myviewer.setVisible(true);
		
		
		myvalues = MyAvReport.getAvIntervals_curb(freq);
		Double[] myvalues2 = MyAvReport.getAvMSI_curb(freq);
		CurveViewer myviewer2 = new CurveViewer("Visits");
		myviewer2.addCurve(myfreq, myvalues, Color.blue);
		myviewer2.addCurve(myfreq, myvalues2, Color.red);
		myviewer2.setXdivision(250);
		myviewer2.setYdivision(10);
		myviewer2.setVisible(true);
	}

}
