package Analyses;

import java.awt.Color;
import java.io.FileNotFoundException;
import java.io.IOException;

import tools.metrics.LogFileParser;
import tools.metrics.MetricsReport;
import util.CurveViewer;
import AverageMetrics.AverageMetricsReport;

import com.twicom.qdparser.XMLParseException;

public class OpenFBArandom {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		int nb_log = 10;
		int last_cycle = 2999;
		
		LogFileParser parser;
		MetricsReport metrics;
		
		
		String Log_dir = "/home/pouletc/experimentation/Simulations/mapA/0_";
		String[] nbAgents = {"4", "5", "9", "10", "14", "15", "24", "25", "5_4_open", "10_9_open", "15_14_open", "25_24_open"};
		int[] nbAgents_int = {4, 5, 9, 10, 14, 15, 24, 25, 5, 10, 15, 25};
		String Log_gen_name1 = "/logs_FBA2/log_";
		String Log_gen_name2 = "/logs_OpenFBARandom/log_";
		
		AverageMetricsReport[] MyAvReports = new AverageMetricsReport[12];
		
		for(int nAg = 0; nAg < nbAgents.length; nAg++){
			MyAvReports[nAg] = new AverageMetricsReport();
			for(int i = 0; i < nb_log; i++){
				parser = new LogFileParser();
				
				try {
					parser.parseFile(Log_dir + nbAgents[nAg] + 
							(nbAgents[nAg].contains("open") ? Log_gen_name2 : Log_gen_name1) + i + ".txt");
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
		
		System.out.println();
		System.out.println();
		System.out.println("METRICS:");
		System.out.println();
		System.out.println(" - Maximum interval: ");
		for(int rep = 0; rep <  MyAvReports.length; rep++){
			System.out.println("    - " + nbAgents[rep] + " : " + MyAvReports[rep].getAvMaxInterval());
		}
		
		System.out.println();
		System.out.println(" - Average interval: ");
		for(int rep = 0; rep <  MyAvReports.length; rep++){
			System.out.println("    - " + nbAgents[rep] + " : " + MyAvReports[rep].getAvAverageInterval());
		}
		
		System.out.println();
		System.out.println(" - Standard deviation of the intervals: ");
		for(int rep = 0; rep <  MyAvReports.length; rep++){
			System.out.println("    - " + nbAgents[rep] + " : " + MyAvReports[rep].getAvStdDevInterval());
		}
		
		System.out.println();
		System.out.println(" - Quadratic mean of the intervals: ");
		for(int rep = 0; rep <  MyAvReports.length; rep++){
			System.out.println("    - " + nbAgents[rep] + " : " + MyAvReports[rep].getAvQuadraticMeanOfIntervals());
		}
		
		System.out.println();
		
		System.out.println();
		System.out.println(" - Maximum instantaneous idleness: ");
		for(int rep = 0; rep <  MyAvReports.length; rep++){
			System.out.println("    - " + nbAgents[rep] + " : " + MyAvReports[rep].getAvMaxInstantaneousIdleness());
		}
		
		System.out.println();
		System.out.println(" - Average idleness: ");
		for(int rep = 0; rep <  MyAvReports.length; rep++){
			System.out.println("    - " + nbAgents[rep] + " : " + MyAvReports[rep].getAvAverageIdleness());
		}
		
		System.out.println();
		System.out.println(" - std Dev idleness: ");
		for(int rep = 0; rep <  MyAvReports.length; rep++){
			System.out.println("    - " + nbAgents[rep] + " : " + MyAvReports[rep].getAvStdDevOfIdleness());
		}
		
		System.out.println();

		System.out.println();
		System.out.println(" - Exploration time of the graph: ");
		for(int rep = 0; rep <  MyAvReports.length; rep++){
			System.out.println("    - " + nbAgents[rep] + " : " + MyAvReports[rep].getAvExplorationTime());
		}
		
		System.out.println();
		System.out.println(" - Normalized exploration time of the graph: ");
		for(int rep = 0; rep <  MyAvReports.length; rep++){
			System.out.println("    - " + nbAgents[rep] + " : " + MyAvReports[rep].getAvNormExplorationTime(nbAgents_int[rep]));
		}
		
		
		/* *
		 *   courbes standards
		 */
				
		int freq = 25;
		Double[] myfreq = new Double[last_cycle/freq +1];
		for(int i = 0; i < myfreq.length; i++)
			myfreq[i] = (double) i * freq;
		
		Double[] myvalues4 = MyAvReports[0].getAvAverageIdleness_curb(freq);
		Double[] myvalues5 = MyAvReports[1].getAvAverageIdleness_curb(freq);
		Double[] myvalues9 = MyAvReports[2].getAvAverageIdleness_curb(freq);
		Double[] myvalues10 = MyAvReports[3].getAvAverageIdleness_curb(freq);
		Double[] myvalues14 = MyAvReports[4].getAvAverageIdleness_curb(freq);
		Double[] myvalues15 = MyAvReports[5].getAvAverageIdleness_curb(freq);
		Double[] myvalues24 = MyAvReports[6].getAvAverageIdleness_curb(freq);
		Double[] myvalues25 = MyAvReports[7].getAvAverageIdleness_curb(freq);
		Double[] myvalues5_4 = MyAvReports[8].getAvAverageIdleness_curb(freq);
		Double[] myvalues10_9 = MyAvReports[9].getAvAverageIdleness_curb(freq);
		Double[] myvalues15_14 = MyAvReports[10].getAvAverageIdleness_curb(freq);
		Double[] myvalues25_24 = MyAvReports[11].getAvAverageIdleness_curb(freq);
		
		/*
		CurveViewer myviewer = new CurveViewer("2->1 Average");
		myviewer.addCurve(myfreq, myvalues1, Color.blue);
		myviewer.addCurve(myfreq, myvalues2, Color.black);
		myviewer.addCurve(myfreq, myvalues2_1, Color.red);		
		myviewer.setXdivision(250);
		myviewer.setYdivision(50);
		myviewer.setVisible(true);
		*/
		
		CurveViewer myviewer2 = new CurveViewer("5->4 Average");
		myviewer2.addCurve(myfreq, myvalues4, Color.blue);
		myviewer2.addCurve(myfreq, myvalues5, Color.black);
		myviewer2.addCurve(myfreq, myvalues5_4, Color.red);
		myviewer2.setXdivision(250);
		myviewer2.setYdivision(10);
		myviewer2.setVisible(true);
		
		CurveViewer myviewer3 = new CurveViewer("10->9 Average");
		myviewer3.addCurve(myfreq, myvalues9, Color.blue);
		myviewer3.addCurve(myfreq, myvalues10, Color.black);
		myviewer3.addCurve(myfreq, myvalues10_9, Color.red);
		myviewer3.setXdivision(250);
		myviewer3.setYdivision(5);
		myviewer3.setVisible(true);
		
		CurveViewer myviewer4 = new CurveViewer("15->14 Average");
		myviewer4.addCurve(myfreq, myvalues14, Color.blue);
		myviewer4.addCurve(myfreq, myvalues15, Color.black);
		myviewer4.addCurve(myfreq, myvalues15_14, Color.red);
		myviewer4.setXdivision(250);
		myviewer4.setYdivision(2);
		myviewer4.setVisible(true);
		
		
		CurveViewer myviewer5 = new CurveViewer("25->24 Average");
		myviewer5.addCurve(myfreq, myvalues24, Color.blue);
		myviewer5.addCurve(myfreq, myvalues25, Color.black);
		myviewer5.addCurve(myfreq, myvalues25_24, Color.red);
		myviewer5.setXdivision(250);
		myviewer5.setYdivision(1);
		myviewer5.setVisible(true);
		
		
		
		Double[] mymaxvalues4 = MyAvReports[0].getAvMaxIdleness_curb(freq);
		Double[] mymaxvalues5 = MyAvReports[1].getAvMaxIdleness_curb(freq);
		Double[] mymaxvalues9 = MyAvReports[2].getAvMaxIdleness_curb(freq);
		Double[] mymaxvalues10 = MyAvReports[3].getAvMaxIdleness_curb(freq);
		Double[] mymaxvalues14 = MyAvReports[4].getAvMaxIdleness_curb(freq);
		Double[] mymaxvalues15 = MyAvReports[5].getAvMaxIdleness_curb(freq);
		Double[] mymaxvalues24 = MyAvReports[6].getAvMaxIdleness_curb(freq);
		Double[] mymaxvalues25 = MyAvReports[7].getAvMaxIdleness_curb(freq);
		Double[] mymaxvalues5_4 = MyAvReports[8].getAvMaxIdleness_curb(freq);
		Double[] mymaxvalues10_9 = MyAvReports[9].getAvMaxIdleness_curb(freq);
		Double[] mymaxvalues15_14 = MyAvReports[10].getAvMaxIdleness_curb(freq);
		Double[] mymaxvalues25_24 = MyAvReports[11].getAvMaxIdleness_curb(freq);
		
		/*
		CurveViewer myviewer6 = new CurveViewer("2->1 Max");
		myviewer6.addCurve(myfreq, mymaxvalues1, Color.blue);
		myviewer6.addCurve(myfreq, mymaxvalues2, Color.black);
		myviewer6.addCurve(myfreq, mymaxvalues2_1, Color.red);
		myviewer6.setXdivision(250);
		myviewer6.setYdivision(150);
		myviewer6.setVisible(true);
		*/
		
		CurveViewer myviewer7 = new CurveViewer("5->4 Max");
		myviewer7.addCurve(myfreq, mymaxvalues4, Color.blue);
		myviewer7.addCurve(myfreq, mymaxvalues5, Color.black);
		myviewer7.addCurve(myfreq, mymaxvalues5_4, Color.red);
		myviewer7.setXdivision(250);
		myviewer7.setYdivision(20);
		myviewer7.setVisible(true);
		
		CurveViewer myviewer8 = new CurveViewer("10->9 Max");
		myviewer8.addCurve(myfreq, mymaxvalues9, Color.blue);
		myviewer8.addCurve(myfreq, mymaxvalues10, Color.black);
		myviewer8.addCurve(myfreq, mymaxvalues10_9, Color.red);
		myviewer8.setXdivision(250);
		myviewer8.setYdivision(10);
		myviewer8.setVisible(true);
		
		CurveViewer myviewer9 = new CurveViewer("15->14 Max");
		myviewer9.addCurve(myfreq, mymaxvalues14, Color.blue);
		myviewer9.addCurve(myfreq, mymaxvalues15, Color.black);
		myviewer9.addCurve(myfreq, mymaxvalues15_14, Color.red);
		myviewer9.setXdivision(250);
		myviewer9.setYdivision(10);
		myviewer9.setVisible(true);
		
		
		CurveViewer myviewer10 = new CurveViewer("25->24 Max");
		myviewer10.addCurve(myfreq, mymaxvalues24, Color.blue);
		myviewer10.addCurve(myfreq, mymaxvalues25, Color.black);
		myviewer10.addCurve(myfreq, mymaxvalues25_24, Color.red);
		myviewer10.setXdivision(250);
		myviewer10.setYdivision(5);
		myviewer10.setVisible(true);
		
		
		/*
		int freq = 25;
		int start = 1000;
		int end = last_cycle;
		Double[] myfreq = new Double[(end-start)/freq +1];
		for(int i = 0; i < myfreq.length; i++)
			myfreq[i] = start + (double) i * freq;
		
		Double[] myvalues1 = MyAvReports[0].getAvAverageIdleness_curb(freq, start, end);
		Double[] myvalues2 = MyAvReports[1].getAvAverageIdleness_curb(freq, start, end);
		Double[] myvalues4 = MyAvReports[2].getAvAverageIdleness_curb(freq, start, end);
		Double[] myvalues5 = MyAvReports[3].getAvAverageIdleness_curb(freq, start, end);
		Double[] myvalues9 = MyAvReports[4].getAvAverageIdleness_curb(freq, start, end);
		Double[] myvalues10 = MyAvReports[5].getAvAverageIdleness_curb(freq, start, end);
		Double[] myvalues14 = MyAvReports[6].getAvAverageIdleness_curb(freq, start, end);
		Double[] myvalues15 = MyAvReports[7].getAvAverageIdleness_curb(freq, start, end);
		Double[] myvalues24 = MyAvReports[8].getAvAverageIdleness_curb(freq, start, end);
		Double[] myvalues25 = MyAvReports[9].getAvAverageIdleness_curb(freq, start, end);
		Double[] myvalues2_1 = MyAvReports[10].getAvAverageIdleness_curb(freq, start, end);
		Double[] myvalues5_4 = MyAvReports[11].getAvAverageIdleness_curb(freq, start, end);
		Double[] myvalues10_9 = MyAvReports[12].getAvAverageIdleness_curb(freq, start, end);
		Double[] myvalues15_14 = MyAvReports[13].getAvAverageIdleness_curb(freq, start, end);
		Double[] myvalues25_24 = MyAvReports[14].getAvAverageIdleness_curb(freq, start, end);
		
		CurveViewer myviewer = new CurveViewer("2->1 Average," + start + " to " + end);
		myviewer.addCurve(myfreq, myvalues1, Color.blue);
		myviewer.addCurve(myfreq, myvalues2, Color.black);
		myviewer.addCurve(myfreq, myvalues2_1, Color.red);
		myviewer.setXcenter(start);
		myviewer.setXdivision(250);
		myviewer.setYdivision(100);
		myviewer.setVisible(true);
		

		CurveViewer myviewer2 = new CurveViewer("5->4 Average," + start + " to " + end);
		myviewer2.addCurve(myfreq, myvalues4, Color.blue);
		myviewer2.addCurve(myfreq, myvalues5, Color.black);
		myviewer2.addCurve(myfreq, myvalues5_4, Color.red);
		myviewer2.setXcenter(start);
		myviewer2.setYcenter(35);
		myviewer2.setXdivision(250);
		myviewer2.setYdivision(5);
		myviewer2.setVisible(true);
		
		CurveViewer myviewer3 = new CurveViewer("10->9 Average," + start + " to " + end);
		myviewer3.addCurve(myfreq, myvalues9, Color.blue);
		myviewer3.addCurve(myfreq, myvalues10, Color.black);
		myviewer3.addCurve(myfreq, myvalues10_9, Color.red);
		myviewer3.setXcenter(start);
		myviewer3.setYcenter(15);
		myviewer3.setXdivision(250);
		myviewer3.setYdivision(5);
		myviewer3.setVisible(true);
		
		CurveViewer myviewer4 = new CurveViewer("15->14 Average," + start + " to " + end);
		myviewer4.addCurve(myfreq, myvalues14, Color.blue);
		myviewer4.addCurve(myfreq, myvalues15, Color.black);
		myviewer4.addCurve(myfreq, myvalues15_14, Color.red);
		myviewer4.setXcenter(start);
		myviewer4.setYcenter(11);
		myviewer4.setXdivision(250);
		myviewer4.setYdivision(1);
		myviewer4.setVisible(true);
		
		CurveViewer myviewer5 = new CurveViewer("25->24 Average," + start + " to " + end);
		myviewer5.addCurve(myfreq, myvalues24, Color.blue);
		myviewer5.addCurve(myfreq, myvalues25, Color.black);
		myviewer5.addCurve(myfreq, myvalues25_24, Color.red);
		myviewer5.setXcenter(start);
		myviewer5.setYcenter(6);
		myviewer5.setXdivision(250);
		myviewer5.setYdivision(1);
		myviewer5.setVisible(true);
		
		*/
		
		

		int start = 2000;
		int end = 3000;
		
		System.out.println();
		System.out.println(" - Stabilized (2000 to 2999: ");
		System.out.println();
	
		System.out.println(" - Maximum interval: ");
		for(int tAg = 0; tAg < MyAvReports.length; tAg++){
			System.out.println("    - " + nbAgents[tAg] + " : " + MyAvReports[tAg].getAvMaxInterval(start, end));
		}
		
		System.out.println(" - Average interval: ");
		for(int tAg = 0; tAg < MyAvReports.length; tAg++){
			System.out.println("    - " + nbAgents[tAg] + " : " + MyAvReports[tAg].getAvAverageInterval(start, end));
		}
		
		
		System.out.println(" - Standard deviation of the intervals: ");
		for(int tAg = 0; tAg < MyAvReports.length; tAg++){
			System.out.println("    - " + nbAgents[tAg] + " : " + MyAvReports[tAg].getAvStdDevInterval(start, end));
		}
		
		
		System.out.println();
		
		System.out.println(" - Maximum instantaneous idleness: ");
		for(int tAg = 0; tAg < MyAvReports.length; tAg++){
			System.out.println("    - " + nbAgents[tAg] + " : " + MyAvReports[tAg].getAvMaxInstantaneousIdleness(start, end));
		}
		
		System.out.println(" - Average idleness: ");
		for(int tAg = 0; tAg < MyAvReports.length; tAg++){
			System.out.println("    - " + nbAgents[tAg] + " : " + MyAvReports[tAg].getAvAverageIdleness(start, end));
		}
		
		System.out.println(" - std dev idleness: ");
		for(int tAg = 0; tAg < MyAvReports.length; tAg++){
			System.out.println("    - " + nbAgents[tAg] + " : " + MyAvReports[tAg].getAvStdDevOfIdleness(start, end));
		}
				
		
		System.out.println();
		System.out.println(" - Stabilization time : ");
		Double[] stab = new Double[25];
		for(int i = stab.length; i > 0; i--)
			stab[stab.length - i] = ((double)i / 100);
		
		start = 1000;
		end = 2999;
		
		int freq2 = 1;
		Double[] myfreq2 = new Double[(end-start)/freq2 +1];
		for(int i = 0; i < myfreq2.length; i++)
			myfreq2[i] = start + (double) i * freq2;
		
		MetricsReport m;
		Double[] curve;
		Double mean;
		Double avturn;
		for(Double stabrate : stab){
			System.out.println("  - " + 100 * stabrate + "% :");
			for(int tAg = 8; tAg < nbAgents.length; tAg++){
				avturn = 0d;
				for(int i = 0; i < MyAvReports[tAg].size(); i++){
						m = MyAvReports[tAg].get(i);
						curve = m.getAverageIdleness_curb(freq2, start, end);
						mean = MetricsReport.MeanValue(2000, end, myfreq2, curve);
						avturn += MetricsReport.TimeToReachTargetValue(mean, stabrate, 1000, 3000, 100, myfreq2, curve);
				}
				System.out.println(avturn / MyAvReports[tAg].size());
			}
		}
		
	}

}
