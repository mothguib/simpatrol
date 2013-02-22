package tools.metrics_report;

import tools.metrics_report.core.LogFileParser;
import tools.metrics_report.core.MetricsReport;


public class ShowMetrics {
	public static String LOG_FILE = "..\\New Agent Library\\_finished\\grav(Edge,Ar,1.0,max)-a-01-1to1-0.xml.log";
	public static int START_CYCLE = 0;
	public static int FINAL_CYCLE = 2999;

	
	public static void main(String[] args) throws Exception {
		LogFileParser parser = new LogFileParser();

		parser.parseFile(LOG_FILE);
	
		if (FINAL_CYCLE > parser.getLastCycle()) {
			System.out.println("Final cycle given is bigger than the final cycle logged: " + parser.getLastCycle());
			return;
		}
		
		MetricsReport metrics = new MetricsReport(parser.getNumNodes(), START_CYCLE, FINAL_CYCLE,
				parser.getVisitsList(), parser.getNodePriorities());
		
		System.out.println();
		System.out.println();
		System.out.println("METRICS:");
		System.out.println();

		System.out.println(" - Quadratic mean of the intervals: " + metrics.getQuadraticMeanOfIntervals());
		System.out.println(" - Maximum interval: " + metrics.getMaxInterval());
		System.out.println(" - Maximum Relative interval: " + metrics.getMaxRelativeInterval());
		System.out.println(" - Average idleness: " + metrics.getAverageIdleness());
		System.out.println(" - Average Combined idleness: " + metrics.getAverageRelativeIdleness());
		System.out.println(" - Average interval: " + metrics.getAverageInterval());
		System.out.println(" - Average Relative interval: " + metrics.getAverageRelativeInterval());
		System.out.println(" - Standard deviation of the intervals: " + metrics.getStdDevOfIntervals());		
		System.out.println();
		System.out.println(" - Maximum instantaneous idleness: " + metrics.getMaxInstantaeousIdleness());		
		System.out.println();
		System.out.println(" - Total number of visits: " + metrics.getTotalVisits());
		System.out.println(" - Relative visits rates: " + metrics.getRelativeVisitRates(10));
		System.out.println(" - Average number of visits per node: " + metrics.getAverageVisits());
		System.out.println(" - Standard deviation of the number of visits per node: " + metrics.getStdDevVisits());
		System.out.println(" - Exploration time of the graph: " + metrics.getExplorationTime());
		System.out.println(" - Normalized exploration time of the graph: " + metrics.getNormExplorationTime(parser.getNumAgents()));
	
	}
	
}
