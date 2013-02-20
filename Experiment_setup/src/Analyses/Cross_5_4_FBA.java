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

public class Cross_5_4_FBA {
	public static void main(String[] args) {
		String global_path = "/home/pouletc/experimentation/Simulations/islands/";
		String[] nbAgents = {"15", "14", "15_14"};
		
		String[] agents_name = { "proximity 8", "proximity 16", "proximity 24", "group 2"};
		
		int nb_log = 10;
		int start_log_num = 0;
		int last_cycle;
		int last_cycle_1 = 2999;
		int last_cycle_2 = 2999;
		
		int not_done = 0;
		
		AverageMetricsReport[] MyAvReports = new AverageMetricsReport[6];
		
		LogFileParser parser;
		MetricsReport metrics;
		int current = 0;
		
		for(String nb_agent : nbAgents){
			String[] logs_dir;
			if(nb_agent.equals("14")|| nb_agent.equals("15")){
				logs_dir = new String[]{
					"0_" + nb_agent + "_open/logs_FBA/log_"};
				last_cycle = last_cycle_1;
			} else {
				logs_dir = new String[]{
						"0_" + nb_agent + "_open/logs_OpenFBANodes2/8/log_", 
						"0_" + nb_agent + "_open/logs_OpenFBANodes2/16/log_",
						"0_" + nb_agent + "_open/logs_OpenFBANodes2/24/log_",
						"0_" + nb_agent + "_open/logs_OpenFBANodesGroup2/log_"};
				last_cycle = last_cycle_2;
			}
			
			for(int type = 0; type < logs_dir.length; type++){
				MyAvReports[current] = new AverageMetricsReport();
				for(int i = start_log_num; i < nb_log + start_log_num; i++){
					parser = new LogFileParser();
					
					try {
						parser.parseFile(global_path + logs_dir[type] + i + ".txt");
						metrics = new MetricsReport(parser.getNumNodes(), 0, last_cycle, parser.getVisitsList());
						MyAvReports[current].add(metrics);
						System.out.println("Agents : "+ agents_name[type]  + ", log "+ i + " read");
					} catch (FileNotFoundException e) {
						// TODO Auto-generated catch block
						not_done++;
						e.printStackTrace();
					} catch (XMLParseException e) {
						// TODO Auto-generated catch block
						not_done++;
						e.printStackTrace();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						not_done++;
						e.printStackTrace();
					}
					
				}
				current++;
			}
		}
		
		System.out.println("Not done : " + not_done);
		/*
		 * 1 agent
		 */
		int start = 1000;
		int end = 3000;
		System.out.println();
		System.out.println("1 AGENT, stable at 1000");
		System.out.println();
		System.out.println(" - Maximum interval 1 agent: ");
			System.out.println(MyAvReports[0].getAvMaxInterval());
		
		System.out.println(" - Maximum interval stable 1 agent: ");
			System.out.println(MyAvReports[0].getAvMaxInterval(start, end));
		
		System.out.println(" - Average interval 1 agent: ");
			System.out.println(MyAvReports[0].getAvAverageInterval());
		
		System.out.println(" - Average interval stable 1 agent: ");
			System.out.println(MyAvReports[0].getAvAverageInterval(start, end));
		
		System.out.println(" - Standard deviation of the intervals 1 agent: ");
			System.out.println(MyAvReports[0].getAvStdDevInterval());
		
		System.out.println(" - Standard deviation of the intervals stable 1 agent: ");
			System.out.println(MyAvReports[0].getAvStdDevInterval(start, end));
		
		System.out.println(" - Quadratic mean of interval 1 agent: ");
			System.out.println(MyAvReports[0].getAvQuadraticMeanOfIntervals());
		
		System.out.println(" - Quadratic mean of stable 1 agent: ");
			System.out.println(MyAvReports[0].getAvQuadraticMeanOfIntervals(start, end));
		
		
		System.out.println();
		
		System.out.println(" - Maximum instantaneous idleness 1 agent: ");
			System.out.println(MyAvReports[0].getAvMaxInstantaneousIdleness());
		
		System.out.println(" - Maximum instantaneous idleness stable 1 agent: ");
			System.out.println(MyAvReports[0].getAvMaxInstantaneousIdleness(start, end));
		
		System.out.println(" - Average idleness 1 agent: ");
			System.out.println(MyAvReports[0].getAvAverageIdleness());
		
		System.out.println(" - Average idleness stable 1 agent: ");
			System.out.println(MyAvReports[0].getAvAverageIdleness(start, end));
		
		System.out.println(" - std dev idleness 1 agent: ");
			System.out.println(MyAvReports[0].getAvStdDevOfIdleness());
		
		System.out.println(" - std dev idleness stable 1 agent: ");
			System.out.println(MyAvReports[0].getAvStdDevOfIdleness(start, end));
		
		
		System.out.println();
		
		System.out.println(" - Total number of visits 1 agent: ");
			System.out.println(MyAvReports[0].getAvTotalVisits());
		
		System.out.println(" - Average number of visits per node 1 agent: ");
			System.out.println(MyAvReports[0].getAvAverageVisits());
		
		System.out.println(" - Standard deviation of the number of visits per node 1 agent: ");
			System.out.println(MyAvReports[0].getAvStdDevVisits());
		
		System.out.println(" - Exploration time of the graph 1 agent: ");
			System.out.println(MyAvReports[0].getAvExplorationTime());
		
		System.out.println(" - Normalized exploration time of the graph 1 agent: ");
			System.out.println(MyAvReports[0].getAvNormExplorationTime(1));

		/*
		 * 2 agents
		 */
		System.out.println();
		System.out.println("2 AGENTS, stable at 1000");
		System.out.println();
		
		System.out.println(" - Maximum interval 2 agents: ");
			System.out.println(MyAvReports[1].getAvMaxInterval());
		
		System.out.println(" - Maximum interval stable 2 agents: ");
			System.out.println(MyAvReports[1].getAvMaxInterval(start, end));
		
		System.out.println(" - Average interval 2 agents: ");
			System.out.println(MyAvReports[1].getAvAverageInterval());
		
		System.out.println(" - Average interval stable 2 agents: ");
			System.out.println(MyAvReports[1].getAvAverageInterval(start, end));
		
		System.out.println(" - Standard deviation of the intervals 2 agents: ");
			System.out.println(MyAvReports[1].getAvStdDevInterval());
		
		System.out.println(" - Standard deviation of the intervals stable 2 agents: ");
			System.out.println(MyAvReports[1].getAvStdDevInterval(start, end));
		
		System.out.println(" - Quadratic mean of interval 2 agents: ");
			System.out.println(MyAvReports[1].getAvQuadraticMeanOfIntervals());
		
		System.out.println(" - Quadratic mean of stable 2 agents: ");
			System.out.println(MyAvReports[1].getAvQuadraticMeanOfIntervals(start, end));
		
		
		System.out.println();
		
		System.out.println(" - Maximum instantaneous idleness 2 agents: ");
			System.out.println(MyAvReports[1].getAvMaxInstantaneousIdleness());
		
		System.out.println(" - Maximum instantaneous idleness stable 2 agents: ");
			System.out.println(MyAvReports[1].getAvMaxInstantaneousIdleness(start, end));
		
		System.out.println(" - Average idleness 2 agents: ");
			System.out.println(MyAvReports[1].getAvAverageIdleness());
		
		System.out.println(" - Average idleness stable 2 agents: ");
			System.out.println(MyAvReports[1].getAvAverageIdleness(start, end));
		
		System.out.println(" - std dev idleness 2 agents: ");
			System.out.println(MyAvReports[1].getAvStdDevOfIdleness());
		
		System.out.println(" - std dev idleness stable 2 agents: ");
			System.out.println(MyAvReports[1].getAvStdDevOfIdleness(start, end));
		
		
		System.out.println();
		
		System.out.println(" - Total number of visits 2 agents: ");
			System.out.println(MyAvReports[1].getAvTotalVisits());
		
		System.out.println(" - Average number of visits per node 2 agents: ");
			System.out.println(MyAvReports[1].getAvAverageVisits());
		
		System.out.println(" - Standard deviation of the number of visits per node 2 agents: ");
			System.out.println(MyAvReports[1].getAvStdDevVisits());
		
		System.out.println(" - Exploration time of the graph 2 agents: ");
			System.out.println(MyAvReports[1].getAvExplorationTime());
		
		System.out.println(" - Normalized exploration time of the graph 2 agents: ");
			System.out.println(MyAvReports[1].getAvNormExplorationTime(2));
		
		
		/*
		 * 2 -> 1
		 */
		start = 2000;
		end = 3000;
		int start2 = 2000;
		int end2 = 3000;
		System.out.println();
		System.out.println("2 -> 1, stable at 3000");
		System.out.println();
		
		System.out.println(" - Average interval stable 1 : ");
			System.out.println(MyAvReports[0].getAvAverageInterval(start, end));
		
		System.out.println(" - Average interval stable 2 : ");
			System.out.println(MyAvReports[1].getAvAverageInterval(start, end));
		
		System.out.println(" - Average interval stable 2 -> 1 : ");
		for(int tAg = 2; tAg < 2 + agents_name.length; tAg++)
			System.out.println(MyAvReports[tAg].getAvAverageInterval(start2, end2));
		
		System.out.println();
		System.out.println(" - Max interval stable 1 : ");
			System.out.println(MyAvReports[0].getAvMaxInterval(start, end));
		
		System.out.println(" - Max interval stable 2 : ");
			System.out.println(MyAvReports[1].getAvMaxInterval(start, end));
		
		System.out.println(" - Max interval stable 2 -> 1 : ");
		for(int tAg = 2; tAg < 2 + agents_name.length; tAg++)
			System.out.println(MyAvReports[tAg].getAvMaxInterval(start2, end2));
		
		System.out.println();
		System.out.println(" - std dev interval stable 1 : ");
			System.out.println(MyAvReports[0].getAvStdDevInterval(start, end));
		
		System.out.println(" - std dev interval stable 2 : ");
			System.out.println(MyAvReports[1].getAvStdDevInterval(start, end));
		
		System.out.println(" - std dev interval stable 2 -> 1 : ");
		for(int tAg = 2; tAg < 2 + agents_name.length; tAg++)
			System.out.println(MyAvReports[tAg].getAvStdDevInterval(start2, end2));
		
		System.out.println();
		System.out.println(" - Quadratic mean of interval stable 1 agent: ");
			System.out.println(MyAvReports[0].getAvQuadraticMeanOfIntervals(start, end));
		
		System.out.println(" - Quadratic mean of interval stable 2 agents: ");
			System.out.println(MyAvReports[1].getAvQuadraticMeanOfIntervals(start, end));
		
		System.out.println(" - Quadratic mean of interval stable 2->1 agents: ");
		for(int tAg = 2; tAg < 2 + agents_name.length; tAg++)
			System.out.println(MyAvReports[tAg].getAvQuadraticMeanOfIntervals(start2, end2));
		
		
		System.out.println();
		System.out.println(" - Average idleness stable 1 : ");
			System.out.println(MyAvReports[0].getAvAverageIdleness(start, end));
		
		System.out.println(" - Average idleness stable 2 : ");
			System.out.println(MyAvReports[1].getAvAverageIdleness(start, end));
		
		System.out.println(" - Average idleness stable 2 -> 1 : ");
		for(int tAg = 2; tAg < 2 + agents_name.length; tAg++)
			System.out.println(MyAvReports[tAg].getAvAverageIdleness(start2, end2));
		
		System.out.println();
		System.out.println(" - Max idleness stable 1 : ");
			System.out.println(MyAvReports[0].getAvMaxInstantaneousIdleness(start, end));
		
		System.out.println(" - Max idleness stable 2 : ");
			System.out.println(MyAvReports[1].getAvMaxInstantaneousIdleness(start, end));
		
		System.out.println(" - Max idleness stable 2 -> 1 : ");
		for(int tAg = 2; tAg < 2 + agents_name.length; tAg++)
			System.out.println(MyAvReports[tAg].getAvMaxInstantaneousIdleness(start2, end2));
		
		System.out.println();
		System.out.println(" - std dev idleness stable 1 : ");
			System.out.println(MyAvReports[0].getAvStdDevOfIdleness(start, end));
		
		System.out.println(" - std dev idleness stable 2 : ");
			System.out.println(MyAvReports[1].getAvStdDevOfIdleness(start, end));
		
		System.out.println(" - std dev idleness stable 2 -> 1 : ");
		for(int tAg = 2; tAg < 2 + agents_name.length; tAg++)
			System.out.println(MyAvReports[tAg].getAvStdDevOfIdleness(start2, end2));
		
		
		int freq = 25;
		last_cycle = 2999;
		Double[] myfreq = new Double[last_cycle/freq +1];
		for(int i = 0; i < myfreq.length; i++)
			myfreq[i] = (double) i * freq;
		
		Double[] myvalues1_0 = MyAvReports[0].getAvAverageIdleness_curb(freq);
		Double[] myvalues2_0 = MyAvReports[1].getAvAverageIdleness_curb(freq);
		
		Double[] myvalues21_0 = MyAvReports[2].getAvAverageIdleness_curb(freq);
		Double[] myvalues21_1 = MyAvReports[3].getAvAverageIdleness_curb(freq);
		Double[] myvalues21_2 = MyAvReports[4].getAvAverageIdleness_curb(freq);
		Double[] myvalues21_3 = MyAvReports[5].getAvAverageIdleness_curb(freq);
		
		System.out.println();
		System.out.println("Transition phase 1000 -> 1800");
		System.out.println();
		
		Double max0 = -1d, max1 = -1d, max2 = -1d, max3 = -1d;
		Double turnmax0 = 0d, turnmax1 = 0d, turnmax2 = 0d, turnmax3 = 0d;
		for(int i = 1000/freq; i < 1800/freq; i++){
			if(myvalues21_0[i] > max0){
				max0 = myvalues21_0[i];
				turnmax0 = myfreq[i];
			}
			if(myvalues21_1[i] > max1){
				max1 = myvalues21_1[i];
				turnmax1 = myfreq[i];
			}
			if(myvalues21_2[i] > max2){
				max2 = myvalues21_2[i];
				turnmax2 = myfreq[i];
			}
			if(myvalues21_3[i] > max3){
				max3 = myvalues21_3[i];
				turnmax3 = myfreq[i];
			}
		}		
		System.out.println();
		System.out.println("Max mean Interval, time :");
		System.out.println("- " + max0 + ", " + turnmax0);
		System.out.println("- " + max1 + ", " + turnmax1);
		System.out.println("- " + max2 + ", " + turnmax2);
		System.out.println("- " + max3 + ", " + turnmax3);
		
		CurveViewer myviewer = new CurveViewer("Average Idleness");
		myviewer.addCurve(myfreq, myvalues1_0, Color.black, DrawStyle.SHORT_DOTS, DrawStyle.POINT_CROSS);
		myviewer.addCurve(myfreq, myvalues2_0, Color.black, DrawStyle.LONG_DOTS);
		myviewer.addCurve(myfreq, myvalues21_0, Color.blue, DrawStyle.POINT_ROUND);
		myviewer.addCurve(myfreq, myvalues21_1, Color.red, DrawStyle.POINT_ROUND);
		myviewer.addCurve(myfreq, myvalues21_2, Color.cyan, DrawStyle.POINT_ROUND);
		myviewer.addCurve(myfreq, myvalues21_3, Color.green, DrawStyle.POINT_ROUND);
		myviewer.setXdivision(250);
		myviewer.setYdivision(50);
		myviewer.setVisible(true);
		
		
		
		Double[] mymaxvalues1_0 = MyAvReports[0].getAvMaxIdleness_curb(freq);
		Double[] mymaxvalues2_0 = MyAvReports[1].getAvMaxIdleness_curb(freq);
		Double[] mymaxvalues21_0 = MyAvReports[2].getAvMaxIdleness_curb(freq);
		Double[] mymaxvalues21_1 = MyAvReports[3].getAvMaxIdleness_curb(freq);
		Double[] mymaxvalues21_2 = MyAvReports[4].getAvMaxIdleness_curb(freq);
		Double[] mymaxvalues21_3 = MyAvReports[5].getAvMaxIdleness_curb(freq);
		
		Double maxmax0 = -1d, maxmax1 = -1d, maxmax2 = -1d, maxmax3 = -1d;
		Double turnmaxmax0 = 0d, turnmaxmax1 = 0d, turnmaxmax2 = 0d, turnmaxmax3 = 0d;
		for(int i = 1000/freq; i < 1800/freq; i++){
			if(mymaxvalues21_0[i] > maxmax0){
				maxmax0 = mymaxvalues21_0[i];
				turnmaxmax0 = myfreq[i];
			}
			if(mymaxvalues21_1[i] > maxmax1){
				maxmax1 = mymaxvalues21_1[i];
				turnmaxmax1 = myfreq[i];
			}
			if(mymaxvalues21_2[i] > maxmax2){
				maxmax2 = mymaxvalues21_2[i];
				turnmaxmax2 = myfreq[i];
			}
			if(mymaxvalues21_3[i] > maxmax3){
				maxmax3 = mymaxvalues21_3[i];
				turnmaxmax3 = myfreq[i];
			}
		}		
		System.out.println();
		System.out.println("Max max Interval, time :");
		System.out.println("- " + maxmax0 + ", " + turnmaxmax0);
		System.out.println("- " + maxmax1 + ", " + turnmaxmax1);
		System.out.println("- " + maxmax2 + ", " + turnmaxmax2);
		System.out.println("- " + maxmax3 + ", " + turnmaxmax3);
		
		CurveViewer myviewer2 = new CurveViewer("Max Idleness");
		myviewer2.addCurve(myfreq, mymaxvalues1_0, Color.black, DrawStyle.SHORT_DOTS, DrawStyle.POINT_CROSS);
		myviewer2.addCurve(myfreq, mymaxvalues2_0, Color.black, DrawStyle.LONG_DOTS);
		myviewer2.addCurve(myfreq, mymaxvalues21_0, Color.blue, DrawStyle.POINT_ROUND);
		myviewer2.addCurve(myfreq, mymaxvalues21_1, Color.red, DrawStyle.POINT_ROUND);
		myviewer2.addCurve(myfreq, mymaxvalues21_2, Color.cyan, DrawStyle.POINT_ROUND);
		myviewer2.addCurve(myfreq, mymaxvalues21_3, Color.green, DrawStyle.POINT_ROUND);
		myviewer2.setXdivision(250);
		myviewer2.setYdivision(50);
		myviewer2.setVisible(true);

		
		
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
			for(int tAg = 2; tAg < 2 + agents_name.length; tAg++){
				avturn = 0d;
				for(int i = 0; i < MyAvReports[tAg].size(); i++){
						m = MyAvReports[tAg].get(i);
						curve = m.getAverageIdleness_curb(freq2, start, end);
						mean = MetricsReport.MeanValue(2000, end, myfreq2, curve);
						avturn += MetricsReport.TimeToReachTargetValue(mean, stabrate, 1000, 3000, 100, myfreq2, curve);
				}
				System.out.println(avturn / MyAvReports[tAg].size() - 1000);
			}
		}
	}

}
