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

public class Cross1Analyse {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		String global_path = "/home/pouletc/experimentation/Simulations/mapA_";
		int nbAgents = 1;
		String[] agents_name = { "Random Reactive",
								 "Conscientious Reactive",
								 "Cycled",
								 "Cognitive Coordinated",		
								 "Heuristic Cognitive Coordinated"};
		String[] logs_dir = {"0_" + nbAgents + "_limited/logs_RandReact/log_", 
								"0_" + nbAgents + "_limited/logs_ConsReact/log_",
								"1_" + nbAgents + "/logs_Cycled/log_", 
								"1_" + nbAgents + "/logs_CognCoord/log_", 
								"1_" + nbAgents + "/logs_HeurCogCoord/log_"};
		
		int nb_log = 30;
		int last_cycle = 2999;
	
		AverageMetricsReport[] MyAvReports = new AverageMetricsReport[6];
		
		LogFileParser parser;
		MetricsReport metrics;
		
		for(int type = 0; type < logs_dir.length; type++){
			MyAvReports[type] = new AverageMetricsReport();
			for(int i = 0; i < nb_log; i++){
				parser = new LogFileParser();
				
				try {
					parser.parseFile(global_path + logs_dir[type] + i + ".txt");
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
				MyAvReports[type].add(metrics);
				System.out.println("Agents : "+ agents_name[type]  + ", log "+ i + " read");
				
			}
		}
		
		System.out.println(" - Maximum interval: ");
		for(int tAg = 0; tAg < agents_name.length; tAg++)
			System.out.println(agents_name[tAg] + " : " + MyAvReports[tAg].getAvMaxInterval());
		
		System.out.println(" - Average interval: ");
		for(int tAg = 0; tAg < agents_name.length; tAg++)
			System.out.println(agents_name[tAg] + " : " + MyAvReports[tAg].getAvAverageInterval());
		
		System.out.println(" - Standard deviation of the intervals: ");
		for(int tAg = 0; tAg < agents_name.length; tAg++)
			System.out.println(agents_name[tAg] + " : " + MyAvReports[tAg].getAvStdDevInterval());
		
		System.out.println();
		
		System.out.println(" - Maximum instantaneous idleness: ");
		for(int tAg = 0; tAg < agents_name.length; tAg++)
			System.out.println(agents_name[tAg] + " : " + MyAvReports[tAg].getAvMaxInstantaneousIdleness());
		
		System.out.println(" - Average idleness: ");
		for(int tAg = 0; tAg < agents_name.length; tAg++)
			System.out.println(agents_name[tAg] + " : " + MyAvReports[tAg].getAvAverageIdleness());
		
		System.out.println();
		
		System.out.println(" - Total number of visits: ");
		for(int tAg = 0; tAg < agents_name.length; tAg++)
			System.out.println(agents_name[tAg] + " : " + MyAvReports[tAg].getAvTotalVisits());
		
		System.out.println(" - Average number of visits per node: ");
		for(int tAg = 0; tAg < agents_name.length; tAg++)
			System.out.println(agents_name[tAg] + " : " + MyAvReports[tAg].getAvAverageVisits());
		
		System.out.println(" - Standard deviation of the number of visits per node: ");
		for(int tAg = 0; tAg < agents_name.length; tAg++)
			System.out.println(agents_name[tAg] + " : " + MyAvReports[tAg].getAvStdDevVisits());
		
		System.out.println(" - Exploration time of the graph: ");
		for(int tAg = 0; tAg < agents_name.length; tAg++)
			System.out.println(agents_name[tAg] + " : " + MyAvReports[tAg].getAvExplorationTime());
		
		System.out.println(" - Normalized exploration time of the graph: ");
		for(int tAg = 0; tAg < agents_name.length; tAg++)
			System.out.println(agents_name[tAg] + " : " + MyAvReports[tAg].getAvNormExplorationTime(nbAgents));

		
		
		int freq = 25;
		Double[] myfreq = new Double[last_cycle/freq +1];
		for(int i = 0; i < myfreq.length; i++)
			myfreq[i] = (double) i * freq;
		
		Double[] myvalues0 = MyAvReports[0].getAvAverageIdleness_curb(freq);
		Double[] myvalues1 = MyAvReports[1].getAvAverageIdleness_curb(freq);
		Double[] myvalues2 = MyAvReports[2].getAvAverageIdleness_curb(freq);
		Double[] myvalues3 = MyAvReports[3].getAvAverageIdleness_curb(freq);
		Double[] myvalues4 = MyAvReports[4].getAvAverageIdleness_curb(freq);
		
		CurveViewer myviewer = new CurveViewer("Average Idleness");
		myviewer.addCurve(myfreq, myvalues0, Color.blue);
		myviewer.addCurve(myfreq, myvalues1, Color.red);
		myviewer.addCurve(myfreq, myvalues2, Color.cyan, DrawStyle.LONG_DOTS);
		myviewer.addCurve(myfreq, myvalues3, Color.orange, DrawStyle.LONG_DOTS);
		myviewer.addCurve(myfreq, myvalues4, Color.gray, DrawStyle.SHORT_DOTS);
		myviewer.setXdivision(250);
		myviewer.setYdivision(150);
		myviewer.setVisible(true);
		
		
		
		Double[] mymaxvalues0= MyAvReports[0].getAvMaxIdleness_curb(freq);
		Double[] mymaxvalues1 = MyAvReports[1].getAvMaxIdleness_curb(freq);
		Double[] mymaxvalues2 = MyAvReports[2].getAvMaxIdleness_curb(freq);
		Double[] mymaxvalues3 = MyAvReports[3].getAvMaxIdleness_curb(freq);
		Double[] mymaxvalues4 = MyAvReports[4].getAvMaxIdleness_curb(freq);
		
		CurveViewer myviewer2 = new CurveViewer("Max Idleness");
		myviewer2.addCurve(myfreq, mymaxvalues0, Color.blue);
		myviewer2.addCurve(myfreq, mymaxvalues1, Color.red);
		myviewer2.addCurve(myfreq, mymaxvalues2, Color.cyan, DrawStyle.LONG_DOTS);
		myviewer2.addCurve(myfreq, mymaxvalues3, Color.orange, DrawStyle.LONG_DOTS);
		myviewer2.addCurve(myfreq, mymaxvalues4, Color.gray, DrawStyle.SHORT_DOTS);
		myviewer2.setXdivision(250);
		myviewer2.setYdivision(150);
		myviewer2.setVisible(true);

		
		
		System.out.println();
		System.out.println(" - Stabilized (1000 to 2999: ");
		System.out.println();
		
		int start = 1000;
		int end = 3000;
		
		System.out.println(" - Maximum interval: ");
		for(int tAg = 0; tAg < agents_name.length; tAg++){
			System.out.println(agents_name[tAg] + " : " + MyAvReports[tAg].getAvMaxInterval(start, end));
		}
		
		System.out.println(" - Average interval: ");
		for(int tAg = 0; tAg < agents_name.length; tAg++){
			System.out.println(agents_name[tAg] + " : " + MyAvReports[tAg].getAvAverageInterval(start, end));
		}
		
		
		System.out.println(" - Standard deviation of the intervals: ");
		for(int tAg = 0; tAg < agents_name.length; tAg++){
			System.out.println(agents_name[tAg] + " : " + MyAvReports[tAg].getAvStdDevInterval(start, end));
		}
		
		
		System.out.println();
		
		System.out.println(" - Maximum instantaneous idleness: ");
		for(int tAg = 0; tAg < agents_name.length; tAg++){
			System.out.println(agents_name[tAg] + " : " + MyAvReports[tAg].getAvMaxInstantaneousIdleness(start, end));
		}
		
		System.out.println(" - Average idleness: ");
		for(int tAg = 0; tAg < agents_name.length; tAg++){
			System.out.println(agents_name[tAg] + " : " + MyAvReports[tAg].getAvAverageIdleness(start, end));
		}
		

	}

}
