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

public class OpenSC {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		String Log_dir = "/home/pouletc/experimentation/Simulations/mapA_1_";
		int[] nbAgents = {1, 2, 4, 5, 9, 10, 14, 15, 24, 25};
		int nb_log = 30;
		String Log_gen_name = "open/logs_SC/log_";
		int last_cycle = 2999;
	
		AverageMetricsReport[] MyAvReports = new AverageMetricsReport[5];
		
		LogFileParser parser;
		MetricsReport metrics;
		
		for(int nAg = 0; nAg < nbAgents.length; nAg+=2){
			MyAvReports[nAg] = new AverageMetricsReport();
			for(int i = 0; i < nb_log; i++){
				parser = new LogFileParser();
				
				try {
					parser.parseFile(Log_dir + nbAgents[nAg + 1] + "_" + nbAgents[nAg] + Log_gen_name + i + ".txt");
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
		
		/**
		 * first period (N agents)
		 */
		int start = 750;
		int end = 1499;
		
		System.out.println(" /** N agents, t=750 to 1499 **/");
		System.out.println();
		
		System.out.println(" - Average interval: ");
		for(int nAg = 0; nAg < MyAvReports.length; nAg++)
			System.out.println(nbAgents[2*nAg + 1] + " : " + MyAvReports[nAg].getAvAverageInterval(start, end));
		
		System.out.println(" - Maximum interval: ");
		for(int nAg = 0; nAg < MyAvReports.length; nAg++)
			System.out.println(nbAgents[2*nAg + 1] + " : " + MyAvReports[nAg].getAvMaxInterval(start, end));
		
		System.out.println(" - Standard deviation of the intervals: ");
		for(int nAg = 0; nAg < MyAvReports.length; nAg++)
			System.out.println(nbAgents[2*nAg + 1] + " : " + MyAvReports[nAg].getAvStdDevInterval(start, end));
		
		System.out.println(" - Quadratic mean of interval: ");
		for(int nAg = 0; nAg < MyAvReports.length; nAg++)
			System.out.println(MyAvReports[2*nAg + 1].getAvQuadraticMeanOfIntervals(start, end));
		
		System.out.println();
		
		System.out.println(" - Average idleness: ");
		for(int nAg = 0; nAg < MyAvReports.length; nAg++)
			System.out.println(nbAgents[2*nAg + 1] + " : " + MyAvReports[nAg].getAvAverageIdleness(start, end));
		
		System.out.println(" - Maximum instantaneous idleness: ");
		for(int nAg = 0; nAg < MyAvReports.length; nAg++)
			System.out.println(nbAgents[2*nAg + 1] + " : " + MyAvReports[nAg].getAvMaxInstantaneousIdleness(start, end));
		
		System.out.println(" - idleness std dev: ");
		for(int nAg = 0; nAg < MyAvReports.length; nAg++)
			System.out.println(nbAgents[2*nAg + 1] + " : " + MyAvReports[nAg].getAvStdDevOfIdleness(start, end));
		
		System.out.println();


		/**
		 * second period (N-1 agents)
		 */
		start = 2250;
		end = 2999;
		
		System.out.println(" /** N-1 agents, t=2250 to 2999 **/");
		System.out.println();
		
		System.out.println(" - Average interval: ");
		for(int nAg = 0; nAg < MyAvReports.length; nAg++)
			System.out.println(nbAgents[2*nAg + 1] + " : " + MyAvReports[nAg].getAvAverageInterval(start, end));
		
		System.out.println(" - Maximum interval: ");
		for(int nAg = 0; nAg < MyAvReports.length; nAg++)
			System.out.println(nbAgents[2*nAg + 1] + " : " + MyAvReports[nAg].getAvMaxInterval(start, end));
		
		System.out.println(" - Standard deviation of the intervals: ");
		for(int nAg = 0; nAg < MyAvReports.length; nAg++)
			System.out.println(nbAgents[2*nAg + 1] + " : " + MyAvReports[nAg].getAvStdDevInterval(start, end));
		
		System.out.println(" - Quadratic mean of interval: ");
		for(int nAg = 0; nAg < MyAvReports.length; nAg++)
			System.out.println(MyAvReports[2*nAg + 1].getAvQuadraticMeanOfIntervals(start, end));
		
		System.out.println();
		
		System.out.println(" - Average idleness: ");
		for(int nAg = 0; nAg < MyAvReports.length; nAg++)
			System.out.println(nbAgents[2*nAg + 1] + " : " + MyAvReports[nAg].getAvAverageIdleness(start, end));
		
		System.out.println(" - Maximum instantaneous idleness: ");
		for(int nAg = 0; nAg < MyAvReports.length; nAg++)
			System.out.println(nbAgents[2*nAg + 1] + " : " + MyAvReports[nAg].getAvMaxInstantaneousIdleness(start, end));
		
		System.out.println(" - idleness std dev: ");
		for(int nAg = 0; nAg < MyAvReports.length; nAg++)
			System.out.println(nbAgents[2*nAg + 1] + " : " + MyAvReports[nAg].getAvStdDevOfIdleness(start, end));
		
		System.out.println();
		
		
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
