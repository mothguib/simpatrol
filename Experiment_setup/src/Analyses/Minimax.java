package Analyses;

import java.awt.Color;
import java.io.FileNotFoundException;
import java.io.IOException;

import tools.metrics.LogFileParser;
import tools.metrics.MetricsReport;
import util.CurveViewer;
import AverageMetrics.AverageMetricsReport;

import com.twicom.qdparser.XMLParseException;

public class Minimax {

	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		int nb_log = 10;
		int last_cycle = 2999;
		
		LogFileParser parser;
		MetricsReport metrics;
		
		
		String Log_dir = "/home/pouletc/experimentation/Simulations/mapA/0_";
		String[] nbAgents = {"1", "2", "4", "5", "9", "10", "14", "15", "2_1", "5_4", "10_9", "15_14"};//, "25_24"};
		int[] nbAgents_int = {1, 2, 4, 5, 9, 10, 14, 15, 2, 5, 10, 15};//, 15, 25};
		String Log_gen_name = "_open/logs_Minimax3/log_";
	
		AverageMetricsReport[] MyAvReports = new AverageMetricsReport[12];
		
		for(int nAg = 0; nAg < nbAgents.length; nAg++){
			MyAvReports[nAg] = new AverageMetricsReport();
			for(int i = 0; i < nb_log; i++){
				parser = new LogFileParser();
				
				try {
					parser.parseFile(Log_dir + nbAgents[nAg] + Log_gen_name + i + ".txt");
					metrics = new MetricsReport(parser.getNumNodes(), 0, last_cycle, parser.getVisitsList());
					MyAvReports[nAg].add(metrics);
					System.out.println("nb Agents : "+ nbAgents[nAg] + ", log "+ i + " read");
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
		System.out.println(" - Total number of visits : ");
		for(int rep = 0; rep < MyAvReports.length; rep++)
			System.out.println("    - " + nbAgents[rep] + " : " + MyAvReports[rep].getAvTotalVisits());
		
		System.out.println();
		System.out.println(" - Average number of visits per node : ");
		for(int rep = 0; rep < MyAvReports.length; rep++)
			System.out.println("    - " + nbAgents[rep] + " : " + MyAvReports[rep].getAvAverageVisits());
		
		System.out.println();
		System.out.println(" - Standard deviation of the number of visits per node : ");
		for(int rep = 0; rep < MyAvReports.length; rep++)
			System.out.println("    - " + nbAgents[rep] + " : " + MyAvReports[rep].getAvStdDevVisits());
		
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
		
		Double[] myvalues1 = MyAvReports[0].getAvAverageIdleness_curb(freq);
		Double[] myvalues2 = MyAvReports[1].getAvAverageIdleness_curb(freq);
		Double[] myvalues4 = MyAvReports[2].getAvAverageIdleness_curb(freq);
		Double[] myvalues5 = MyAvReports[3].getAvAverageIdleness_curb(freq);
		Double[] myvalues9 = MyAvReports[4].getAvAverageIdleness_curb(freq);
		Double[] myvalues10 = MyAvReports[5].getAvAverageIdleness_curb(freq);
		Double[] myvalues14 = MyAvReports[6].getAvAverageIdleness_curb(freq);
		Double[] myvalues15 = MyAvReports[7].getAvAverageIdleness_curb(freq);
//		Double[] myvalues24 = MyAvReports[8].getAvAverageIdleness_curb(freq);
//		Double[] myvalues25 = MyAvReports[9].getAvAverageIdleness_curb(freq);
		Double[] myvalues2_1 = MyAvReports[8].getAvAverageIdleness_curb(freq);
		Double[] myvalues5_4 = MyAvReports[9].getAvAverageIdleness_curb(freq);
		Double[] myvalues10_9 = MyAvReports[10].getAvAverageIdleness_curb(freq);
		Double[] myvalues15_14 = MyAvReports[11].getAvAverageIdleness_curb(freq);
//		Double[] myvalues25_24 = MyAvReports[14].getAvAverageIdleness_curb(freq);
//		
		CurveViewer myviewer = new CurveViewer("2->1 Average");
		myviewer.addCurve(myfreq, myvalues1, Color.blue);
		myviewer.addCurve(myfreq, myvalues2, Color.black);
		myviewer.addCurve(myfreq, myvalues2_1, Color.red);		
		myviewer.setXdivision(250);
		myviewer.setYdivision(50);
		myviewer.setVisible(true);
		
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
		
//		CurveViewer myviewer5 = new CurveViewer("25->24 Average");
//		myviewer5.addCurve(myfreq, myvalues24, Color.blue);
//		myviewer5.addCurve(myfreq, myvalues25, Color.black);
//		myviewer5.addCurve(myfreq, myvalues25_24, Color.red);
//		myviewer5.setXdivision(250);
//		myviewer5.setYdivision(1);
//		myviewer5.setVisible(true);
		
		
		
		Double[] mymaxvalues1 = MyAvReports[0].getAvMaxIdleness_curb(freq);
		Double[] mymaxvalues2 = MyAvReports[1].getAvMaxIdleness_curb(freq);
		Double[] mymaxvalues4 = MyAvReports[2].getAvMaxIdleness_curb(freq);
		Double[] mymaxvalues5 = MyAvReports[3].getAvMaxIdleness_curb(freq);
		Double[] mymaxvalues9 = MyAvReports[4].getAvMaxIdleness_curb(freq);
		Double[] mymaxvalues10 = MyAvReports[5].getAvMaxIdleness_curb(freq);
		Double[] mymaxvalues14 = MyAvReports[6].getAvMaxIdleness_curb(freq);
		Double[] mymaxvalues15 = MyAvReports[7].getAvMaxIdleness_curb(freq);
//		Double[] mymaxvalues24 = MyAvReports[8].getAvMaxIdleness_curb(freq);
//		Double[] mymaxvalues25 = MyAvReports[9].getAvMaxIdleness_curb(freq);
		Double[] mymaxvalues2_1 = MyAvReports[8].getAvMaxIdleness_curb(freq);
		Double[] mymaxvalues5_4 = MyAvReports[9].getAvMaxIdleness_curb(freq);
		Double[] mymaxvalues10_9 = MyAvReports[10].getAvMaxIdleness_curb(freq);
		Double[] mymaxvalues15_14 = MyAvReports[11].getAvMaxIdleness_curb(freq);
//		Double[] mymaxvalues25_24 = MyAvReports[14].getAvMaxIdleness_curb(freq);
//		
		CurveViewer myviewer6 = new CurveViewer("2->1 Max");
		myviewer6.addCurve(myfreq, mymaxvalues1, Color.blue);
		myviewer6.addCurve(myfreq, mymaxvalues2, Color.black);
		myviewer6.addCurve(myfreq, mymaxvalues2_1, Color.red);
		myviewer6.setXdivision(250);
		myviewer6.setYdivision(150);
		myviewer6.setVisible(true);
		
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
		
//		CurveViewer myviewer10 = new CurveViewer("25->24 Max");
//		myviewer10.addCurve(myfreq, mymaxvalues24, Color.blue);
//		myviewer10.addCurve(myfreq, mymaxvalues25, Color.black);
//		myviewer10.addCurve(myfreq, mymaxvalues25_24, Color.red);
//		myviewer10.setXdivision(250);
//		myviewer10.setYdivision(5);
//		myviewer10.setVisible(true);

//		
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
		System.out.println(" - Quadratic mean of the intervals: ");
		for(int rep = 0; rep <  MyAvReports.length; rep++){
			System.out.println("    - " + nbAgents[rep] + " : " + MyAvReports[rep].getAvQuadraticMeanOfIntervals(start, end));
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
		System.out.println("Transition phase 1000 -> 1800");
		System.out.println();
		
		Double max0 = -1d, max1 = -1d, max2 = -1d, max3 = -1d;
		Double turnmax0 = 0d, turnmax1 = 0d, turnmax2 = 0d, turnmax3 = 0d;
		for(int i = 1000/freq; i < 1800/freq; i++){
			if(myvalues2_1[i] > max0){
				max0 = myvalues2_1[i];
				turnmax0 = myfreq[i];
			}
			if(myvalues5_4[i] > max1){
				max1 = myvalues5_4[i];
				turnmax1 = myfreq[i];
			}
			if(myvalues10_9[i] > max2){
				max2 = myvalues10_9[i];
				turnmax2 = myfreq[i];
			}
			if(myvalues15_14[i] > max3){
				max3 = myvalues15_14[i];
				turnmax3 = myfreq[i];
			}
		}		
		System.out.println();
		System.out.println("Max mean Interval, time :");
		System.out.println(max0 + ", " + turnmax0);
		System.out.println(max1 + ", " + turnmax1);
		System.out.println(max2 + ", " + turnmax2);
		System.out.println(max3 + ", " + turnmax3);
		
		
		Double maxmax0 = -1d, maxmax1 = -1d, maxmax2 = -1d, maxmax3 = -1d;
		Double turnmaxmax0 = 0d, turnmaxmax1 = 0d, turnmaxmax2 = 0d, turnmaxmax3 = 0d;
		for(int i = 1000/freq; i < 1800/freq; i++){
			if(mymaxvalues2_1[i] > maxmax0){
				maxmax0 = mymaxvalues2_1[i];
				turnmaxmax0 = myfreq[i];
			}
			if(mymaxvalues5_4[i] > maxmax1){
				maxmax1 = mymaxvalues5_4[i];
				turnmaxmax1 = myfreq[i];
			}
			if(mymaxvalues10_9[i] > maxmax2){
				maxmax2 = mymaxvalues10_9[i];
				turnmaxmax2 = myfreq[i];
			}
			if(mymaxvalues15_14[i] > maxmax3){
				maxmax3 = mymaxvalues15_14[i];
				turnmaxmax3 = myfreq[i];
			}
		}		
		System.out.println();
		System.out.println("Max max Interval, time :");
		System.out.println(maxmax0 + ", " + turnmaxmax0);
		System.out.println(maxmax1 + ", " + turnmaxmax1);
		System.out.println( maxmax2 + ", " + turnmaxmax2);
		System.out.println(maxmax3 + ", " + turnmaxmax3);

		
		
		System.out.println();
		System.out.println(" - Stabilization time : ");
		Double[] stab = {0.25, 0.24, 0.23, 0.22, 0.21, 0.2, 0.19, 0.18, 0.17, 0.16, 0.15, 0.14, 0.13, 0.12, 0.11, 0.1, 0.09, 0.08, 0.07, 0.06, 0.05, 0.04, 0.03, 0.02, 0.01};
		
		
		start = 999;
		end = last_cycle;
		
		int freq2 = 1;
		Double[] myfreq2 = new Double[(end-start)/freq2 +1];
		for(int i = 0; i < myfreq2.length; i++)
			myfreq2[i] = start + (double) i * freq2;
		
		
		for(Double stabrate : stab){
			System.out.println("  - " + 100 * stabrate + "% :");
			
			double avturn = 0d;
			MetricsReport m;
			Double[] curve;
			Double mean;
			for(int i = 0; i < MyAvReports[8].size(); i++){
					m = MyAvReports[8].get(i);
					curve = m.getAverageIdleness_curb(freq2, start, end);
					mean = MetricsReport.MeanValue(2000, end, myfreq2, curve);
					avturn += MetricsReport.TimeToReachTargetValue(mean, stabrate, 1000, 3000, 100, myfreq2, curve);
			}
			System.out.println("    - " + nbAgents[8] + " : " +  (avturn / MyAvReports[8].size() - 1000));
			
			
			avturn = 0d;
			for(int i = 0; i < MyAvReports[9].size(); i++){
				m = MyAvReports[9].get(i);
				curve = m.getAverageIdleness_curb(freq2, start, end);
				mean = MetricsReport.MeanValue(2000, end, myfreq2, curve);
				avturn += MetricsReport.TimeToReachTargetValue(mean, stabrate, 1000, 3000, 100, myfreq2, curve);
			}
			System.out.println("    - " + nbAgents[9] + " : " +  (avturn / MyAvReports[9].size() - 1000));
			
			avturn = 0d;
			for(int i = 0; i < MyAvReports[10].size(); i++){
				m = MyAvReports[10].get(i);
				curve = m.getAverageIdleness_curb(freq2, start, end);
				mean = MetricsReport.MeanValue(2000, end, myfreq2, curve);
				avturn += MetricsReport.TimeToReachTargetValue(mean, stabrate, 1000, 3000, 100, myfreq2, curve);
			}
			System.out.println("    - " + nbAgents[10] + " : " +  (avturn / MyAvReports[10].size() - 1000));

			avturn = 0d;
			for(int i = 0; i < MyAvReports[11].size(); i++){
				m = MyAvReports[11].get(i);
				curve = m.getAverageIdleness_curb(freq2, start, end);
				mean = MetricsReport.MeanValue(2000, end, myfreq2, curve);
				avturn += MetricsReport.TimeToReachTargetValue(mean, stabrate, 1000, 3000, 100, myfreq2, curve);
			}
			System.out.println("    - " + nbAgents[11] + " : " +  (avturn / MyAvReports[11].size() - 1000));
			
		}
		
	}

}
