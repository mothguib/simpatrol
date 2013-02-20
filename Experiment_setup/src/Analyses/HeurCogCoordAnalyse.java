package Analyses;

import java.awt.Color;
import java.io.FileNotFoundException;
import java.io.IOException;

import tools.metrics.LogFileParser;
import tools.metrics.MetricsReport;
import util.CurveViewer;
import util.DrawStyle;
import AverageMetrics.AverageMetricsReport;

import com.twicom.qdparser.XMLParseException;

public class HeurCogCoordAnalyse {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		String Log_dir = "/home/pouletc/experimentation/Simulations/mapA_1_";
		int[] nbAgents = {1, 2, 5, 10, 15, 25};
		int nb_log = 30;
		String Log_gen_name = "/logs_HeurCogCoord/log_";
		int last_cycle = 2999;
	
		AverageMetricsReport[] MyAvReports = new AverageMetricsReport[6];
		
		LogFileParser parser;
		MetricsReport metrics;
		
		for(int nAg = 0; nAg < nbAgents.length; nAg++){
			MyAvReports[nAg] = new AverageMetricsReport();
			for(int i = 0; i < nb_log; i++){
				parser = new LogFileParser();
				
				try {
					parser.parseFile(Log_dir + nbAgents[nAg] + Log_gen_name + i + ".txt");
				} catch (FileNotFoundException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (XMLParseException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
	
				metrics = new MetricsReport(parser.getNumNodes(), 0, last_cycle, parser.getVisitsList());
				MyAvReports[nAg].add(metrics);
				System.out.println("nb Agents : "+ nbAgents[nAg] + ", log "+ i + " read");
				
			}
		}
		
		System.out.println(" - Maximum interval: ");
		for(int nAg = 0; nAg < nbAgents.length; nAg++)
			System.out.println(nbAgents[nAg] + " : " + MyAvReports[nAg].getAvMaxInterval());
		
		System.out.println(" - Average interval: ");
		for(int nAg = 0; nAg < nbAgents.length; nAg++)
			System.out.println(nbAgents[nAg] + " : " + MyAvReports[nAg].getAvAverageInterval());
		
		System.out.println(" - Standard deviation of the intervals: ");
		for(int nAg = 0; nAg < nbAgents.length; nAg++)
			System.out.println(nbAgents[nAg] + " : " + MyAvReports[nAg].getAvStdDevInterval());
		
		System.out.println();
		
		System.out.println(" - Maximum instantaneous idleness: ");
		for(int nAg = 0; nAg < nbAgents.length; nAg++)
			System.out.println(nbAgents[nAg] + " : " + MyAvReports[nAg].getAvMaxInstantaneousIdleness());
		
		System.out.println(" - Average idleness: ");
		for(int nAg = 0; nAg < nbAgents.length; nAg++)
			System.out.println(nbAgents[nAg] + " : " + MyAvReports[nAg].getAvAverageIdleness());
		
		System.out.println();
		
		System.out.println(" - Total number of visits: ");
		for(int nAg = 0; nAg < nbAgents.length; nAg++)
			System.out.println(nbAgents[nAg] + " : " + MyAvReports[nAg].getAvTotalVisits());
		
		System.out.println(" - Average number of visits per node: ");
		for(int nAg = 0; nAg < nbAgents.length; nAg++)
			System.out.println(nbAgents[nAg] + " : " + MyAvReports[nAg].getAvAverageVisits());
		
		System.out.println(" - Standard deviation of the number of visits per node: ");
		for(int nAg = 0; nAg < nbAgents.length; nAg++)
			System.out.println(nbAgents[nAg] + " : " + MyAvReports[nAg].getAvStdDevVisits());
		
		System.out.println(" - Exploration time of the graph: ");
		for(int nAg = 0; nAg < nbAgents.length; nAg++)
			System.out.println(nbAgents[nAg] + " : " + MyAvReports[nAg].getAvExplorationTime());
		
		System.out.println(" - Normalized exploration time of the graph: ");
		for(int nAg = 0; nAg < nbAgents.length; nAg++)
			System.out.println(nbAgents[nAg] + " : " + MyAvReports[nAg].getAvNormExplorationTime(nbAgents[nAg]));

		
		
		int freq = 25;
		Double[] myfreq = new Double[last_cycle/freq +1];
		for(int i = 0; i < myfreq.length; i++)
			myfreq[i] = (double) i * freq;
		
		Double[] myvalues1 = MyAvReports[0].getAvAverageIdleness_curb(freq);
		Double[] myvalues2 = MyAvReports[1].getAvAverageIdleness_curb(freq);
		Double[] myvalues5 = MyAvReports[2].getAvAverageIdleness_curb(freq);
		Double[] myvalues10 = MyAvReports[3].getAvAverageIdleness_curb(freq);
		Double[] myvalues15 = MyAvReports[4].getAvAverageIdleness_curb(freq);
		Double[] myvalues25 = MyAvReports[5].getAvAverageIdleness_curb(freq);
		
		CurveViewer myviewer = new CurveViewer("Average Idleness");
		myviewer.addCurve(myfreq, myvalues1, Color.blue);
		myviewer.addCurve(myfreq, myvalues2, Color.red);
		myviewer.addCurve(myfreq, myvalues5, Color.cyan, DrawStyle.LONG_DOTS);
		myviewer.addCurve(myfreq, myvalues10, Color.orange, DrawStyle.LONG_DOTS);
		myviewer.addCurve(myfreq, myvalues15, Color.gray, DrawStyle.SHORT_DOTS);
		myviewer.addCurve(myfreq, myvalues25, Color.green, DrawStyle.SHORT_DOTS);
		myviewer.setXdivision(250);
		myviewer.setYdivision(150);
		myviewer.setVisible(true);
		
		
		
		Double[] mymaxvalues1 = MyAvReports[0].getAvMaxIdleness_curb(freq);
		Double[] mymaxvalues2 = MyAvReports[1].getAvMaxIdleness_curb(freq);
		Double[] mymaxvalues5 = MyAvReports[2].getAvMaxIdleness_curb(freq);
		Double[] mymaxvalues10 = MyAvReports[3].getAvMaxIdleness_curb(freq);
		Double[] mymaxvalues15 = MyAvReports[4].getAvMaxIdleness_curb(freq);
		Double[] mymaxvalues25 = MyAvReports[5].getAvMaxIdleness_curb(freq);
		
		CurveViewer myviewer2 = new CurveViewer("Max Idleness");
		myviewer2.addCurve(myfreq, mymaxvalues1, Color.blue);
		myviewer2.addCurve(myfreq, mymaxvalues2, Color.red);
		myviewer2.addCurve(myfreq, mymaxvalues5, Color.cyan, DrawStyle.LONG_DOTS);
		myviewer2.addCurve(myfreq, mymaxvalues10, Color.orange, DrawStyle.LONG_DOTS);
		myviewer2.addCurve(myfreq, mymaxvalues15, Color.gray, DrawStyle.SHORT_DOTS);
		myviewer2.addCurve(myfreq, mymaxvalues25, Color.green, DrawStyle.SHORT_DOTS);
		myviewer2.setXdivision(250);
		myviewer2.setYdivision(150);
		myviewer2.setVisible(true);

	}

}
