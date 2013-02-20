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

public class Open_15_14 {
	public static void main(String[] args) {
		String global_path = "/home/pouletc/experimentation/Simulations/islands/";
		String[] nbAgents = {"14", "15", "15_14"};
		
		String[] agents_name = { "Random Reactive",
				 "Conscientious Reactive",
				 "Cycled fast",
				 "Cycled fast open",
				 "Cycled slow",
				 "Cycled slow2",
				 "Cognitive Coordinated",		
				 "Heuristic Cognitive Coordinated"};
		
		int nb_log = 10;
		int start_log_num = 0;
		int last_cycle;
		int last_cycle_1 = 2999;
		int last_cycle_2 = 2999;
		
		int not_done = 0;
		
		AverageMetricsReport[] MyAvReports = new AverageMetricsReport[24];
		
		LogFileParser parser;
		MetricsReport metrics;
		int current = 0;
		
		for(String nb_agent : nbAgents){
			String[] logs_dir;
			if(nb_agent.equals("14")|| nb_agent.equals("15")){
				logs_dir = new String[]{
						"0_" + nb_agent + "_limited/logs_RR/log_", 
						"0_" + nb_agent + "_limited/logs_CR/log_",
						"1_" + nb_agent + "/logs_SC/fast/log_", 
						"1_" + nb_agent + "/logs_SC/fast/log_", 
						"1_" + nb_agent + "/logs_SC/fast/log_",
						"1_" + nb_agent + "/logs_SC/fast/log_",
						"1_" + nb_agent + "/logs_CC/log_", 
						"1_" + nb_agent + "/logs_HPCC/log_"};
				last_cycle = last_cycle_1;
			} else {
				logs_dir = new String[]{
						"0_" + nb_agent + "_open/logs_RR/log_", 
						"0_" + nb_agent + "_open/logs_CR/log_",
						"1_" + nb_agent + "/logs_SC/fast/log_",
						"1_" + nb_agent + "_open/logs_SC/log_",  
						"1_" + nb_agent + "/logs_SC/slow/log_", 
						"1_" + nb_agent + "/logs_SC/slow2/log_",
						"1_" + nb_agent + "/logs_CC/log_", 
						"1_" + nb_agent + "_open/logs_HPCC/log_"};
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
		for(int tAg = 0; tAg < agents_name.length; tAg++)
			System.out.println(MyAvReports[tAg].getAvMaxInterval());
		
		System.out.println(" - Maximum interval stable 1 agent: ");
		for(int tAg = 0; tAg < agents_name.length; tAg++)
			System.out.println(MyAvReports[tAg].getAvMaxInterval(start, end));
		
		System.out.println(" - Average interval 1 agent: ");
		for(int tAg = 0; tAg < agents_name.length; tAg++)
			System.out.println(MyAvReports[tAg].getAvAverageInterval());
		
		System.out.println(" - Average interval stable 1 agent: ");
		for(int tAg = 0; tAg < agents_name.length; tAg++)
			System.out.println(MyAvReports[tAg].getAvAverageInterval(start, end));
		
		System.out.println(" - Standard deviation of the intervals 1 agent: ");
		for(int tAg = 0; tAg < agents_name.length; tAg++)
			System.out.println(MyAvReports[tAg].getAvStdDevInterval());
		
		System.out.println(" - Standard deviation of the intervals stable 1 agent: ");
		for(int tAg = 0; tAg < agents_name.length; tAg++)
			System.out.println(MyAvReports[tAg].getAvStdDevInterval(start, end));
		
		System.out.println(" - Quadratic mean of interval 1 agent: ");
		for(int tAg = 0; tAg < agents_name.length; tAg++)
			System.out.println(MyAvReports[tAg].getAvQuadraticMeanOfIntervals());
		
		System.out.println(" - Quadratic mean of stable 1 agent: ");
		for(int tAg = 0; tAg < agents_name.length; tAg++)
			System.out.println(MyAvReports[tAg].getAvQuadraticMeanOfIntervals(start, end));
		
		
		System.out.println();
		
		System.out.println(" - Maximum instantaneous idleness 1 agent: ");
		for(int tAg = 0; tAg < agents_name.length; tAg++)
			System.out.println(MyAvReports[tAg].getAvMaxInstantaneousIdleness());
		
		System.out.println(" - Maximum instantaneous idleness stable 1 agent: ");
		for(int tAg = 0; tAg < agents_name.length; tAg++)
			System.out.println(MyAvReports[tAg].getAvMaxInstantaneousIdleness(start, end));
		
		System.out.println(" - Average idleness 1 agent: ");
		for(int tAg = 0; tAg < agents_name.length; tAg++)
			System.out.println(MyAvReports[tAg].getAvAverageIdleness());
		
		System.out.println(" - Average idleness stable 1 agent: ");
		for(int tAg = 0; tAg < agents_name.length; tAg++)
			System.out.println(MyAvReports[tAg].getAvAverageIdleness(start, end));
		
		System.out.println(" - std dev idleness 1 agent: ");
		for(int tAg = 0; tAg < agents_name.length; tAg++)
			System.out.println(MyAvReports[tAg].getAvStdDevOfIdleness());
		
		System.out.println(" - std dev idleness stable 1 agent: ");
		for(int tAg = 0; tAg < agents_name.length; tAg++)
			System.out.println(MyAvReports[tAg].getAvStdDevOfIdleness(start, end));
		
		
		System.out.println();
		
		System.out.println(" - Total number of visits 1 agent: ");
		for(int tAg = 0; tAg < agents_name.length; tAg++)
			System.out.println(MyAvReports[tAg].getAvTotalVisits());
		
		System.out.println(" - Average number of visits per node 1 agent: ");
		for(int tAg = 0; tAg < agents_name.length; tAg++)
			System.out.println(MyAvReports[tAg].getAvAverageVisits());
		
		System.out.println(" - Standard deviation of the number of visits per node 1 agent: ");
		for(int tAg = 0; tAg < agents_name.length; tAg++)
			System.out.println(MyAvReports[tAg].getAvStdDevVisits());
		
		System.out.println(" - Exploration time of the graph 1 agent: ");
		for(int tAg = 0; tAg < agents_name.length; tAg++)
			System.out.println(MyAvReports[tAg].getAvExplorationTime());
		
		System.out.println(" - Normalized exploration time of the graph 1 agent: ");
		for(int tAg = 0; tAg < agents_name.length; tAg++)
			System.out.println(MyAvReports[tAg].getAvNormExplorationTime(1));

		/*
		 * 2 agents
		 */
		System.out.println();
		System.out.println("2 AGENTS, stable at 1000");
		System.out.println();
		
		System.out.println(" - Maximum interval 2 agents: ");
		for(int tAg = 0; tAg < agents_name.length; tAg++)
			System.out.println(MyAvReports[tAg+ agents_name.length].getAvMaxInterval());
		
		System.out.println(" - Maximum interval stable 2 agents: ");
		for(int tAg = 0; tAg < agents_name.length; tAg++)
			System.out.println(MyAvReports[tAg+ agents_name.length].getAvMaxInterval(start, end));
		
		System.out.println(" - Average interval 2 agents: ");
		for(int tAg = 0; tAg < agents_name.length; tAg++)
			System.out.println(MyAvReports[tAg+ agents_name.length].getAvAverageInterval());
		
		System.out.println(" - Average interval stable 2 agents: ");
		for(int tAg = 0; tAg < agents_name.length; tAg++)
			System.out.println(MyAvReports[tAg+ agents_name.length].getAvAverageInterval(start, end));
		
		System.out.println(" - Standard deviation of the intervals 2 agents: ");
		for(int tAg = 0; tAg < agents_name.length; tAg++)
			System.out.println(MyAvReports[tAg+ agents_name.length].getAvStdDevInterval());
		
		System.out.println(" - Standard deviation of the intervals stable 2 agents: ");
		for(int tAg = 0; tAg < agents_name.length; tAg++)
			System.out.println(MyAvReports[tAg+ agents_name.length].getAvStdDevInterval(start, end));
		
		System.out.println(" - Quadratic mean of interval 2 agents: ");
		for(int tAg = 0; tAg < agents_name.length; tAg++)
			System.out.println(MyAvReports[tAg+ agents_name.length].getAvQuadraticMeanOfIntervals());
		
		System.out.println(" - Quadratic mean of stable 2 agents: ");
		for(int tAg = 0; tAg < agents_name.length; tAg++)
			System.out.println(MyAvReports[tAg+ agents_name.length].getAvQuadraticMeanOfIntervals(start, end));
		
		
		System.out.println();
		
		System.out.println(" - Maximum instantaneous idleness 2 agents: ");
		for(int tAg = 0; tAg < agents_name.length; tAg++)
			System.out.println(MyAvReports[tAg+ agents_name.length].getAvMaxInstantaneousIdleness());
		
		System.out.println(" - Maximum instantaneous idleness stable 2 agents: ");
		for(int tAg = 0; tAg < agents_name.length; tAg++)
			System.out.println(MyAvReports[tAg+ agents_name.length].getAvMaxInstantaneousIdleness(start, end));
		
		System.out.println(" - Average idleness 2 agents: ");
		for(int tAg = 0; tAg < agents_name.length; tAg++)
			System.out.println(MyAvReports[tAg+ agents_name.length].getAvAverageIdleness());
		
		System.out.println(" - Average idleness stable 2 agents: ");
		for(int tAg = 0; tAg < agents_name.length; tAg++)
			System.out.println(MyAvReports[tAg+ agents_name.length].getAvAverageIdleness(start, end));
		
		System.out.println(" - std dev idleness 2 agents: ");
		for(int tAg = 0; tAg < agents_name.length; tAg++)
			System.out.println(MyAvReports[tAg+ agents_name.length].getAvStdDevOfIdleness());
		
		System.out.println(" - std dev idleness stable 2 agents: ");
		for(int tAg = 0; tAg < agents_name.length; tAg++)
			System.out.println(MyAvReports[tAg+ agents_name.length].getAvStdDevOfIdleness(start, end));
		
		
		System.out.println();
		
		System.out.println(" - Total number of visits 2 agents: ");
		for(int tAg = 0; tAg < agents_name.length; tAg++)
			System.out.println(MyAvReports[tAg+ agents_name.length].getAvTotalVisits());
		
		System.out.println(" - Average number of visits per node 2 agents: ");
		for(int tAg = 0; tAg < agents_name.length; tAg++)
			System.out.println(MyAvReports[tAg+ agents_name.length].getAvAverageVisits());
		
		System.out.println(" - Standard deviation of the number of visits per node 2 agents: ");
		for(int tAg = 0; tAg < agents_name.length; tAg++)
			System.out.println(MyAvReports[tAg+ agents_name.length].getAvStdDevVisits());
		
		System.out.println(" - Exploration time of the graph 2 agents: ");
		for(int tAg = 0; tAg < agents_name.length; tAg++)
			System.out.println(MyAvReports[tAg+ agents_name.length].getAvExplorationTime());
		
		System.out.println(" - Normalized exploration time of the graph 2 agents: ");
		for(int tAg = 0; tAg < agents_name.length; tAg++)
			System.out.println(MyAvReports[tAg+ agents_name.length].getAvNormExplorationTime(2));
		
		
		/*
		 * 2 -> 1
		 */
		start = 2000;
		end = 3000;
		int start2 = 2000;
		int end2 = 3000;
		System.out.println();
		System.out.println("2 -> 1, stable at 2000");
		System.out.println();
		
		System.out.println(" - Average interval stable 1 : ");
		for(int tAg = 0; tAg < agents_name.length; tAg++)
			System.out.println(MyAvReports[tAg].getAvAverageInterval(start, end));
		
		System.out.println(" - Average interval stable 2 : ");
		for(int tAg = 0; tAg < agents_name.length; tAg++)
			System.out.println(MyAvReports[tAg+ agents_name.length].getAvAverageInterval(start, end));
		
		System.out.println(" - Average interval stable 2 -> 1 : ");
		for(int tAg = 0; tAg < agents_name.length; tAg++)
			System.out.println(MyAvReports[tAg+ 2*agents_name.length].getAvAverageInterval(start2, end2));
		
		System.out.println();
		System.out.println(" - Max interval stable 1 : ");
		for(int tAg = 0; tAg < agents_name.length; tAg++)
			System.out.println(MyAvReports[tAg].getAvMaxInterval(start, end));
		
		System.out.println(" - Max interval stable 2 : ");
		for(int tAg = 0; tAg < agents_name.length; tAg++)
			System.out.println(MyAvReports[tAg+ agents_name.length].getAvMaxInterval(start, end));
		
		System.out.println(" - Max interval stable 2 -> 1 : ");
		for(int tAg = 0; tAg < agents_name.length; tAg++)
			System.out.println(MyAvReports[tAg+ 2*agents_name.length].getAvMaxInterval(start2, end2));
		
		System.out.println();
		System.out.println(" - std dev interval stable 1 : ");
		for(int tAg = 0; tAg < agents_name.length; tAg++)
			System.out.println(MyAvReports[tAg].getAvStdDevInterval(start, end));
		
		System.out.println(" - std dev interval stable 2 : ");
		for(int tAg = 0; tAg < agents_name.length; tAg++)
			System.out.println(MyAvReports[tAg+ agents_name.length].getAvStdDevInterval(start, end));
		
		System.out.println(" - std dev interval stable 2 -> 1 : ");
		for(int tAg = 0; tAg < agents_name.length; tAg++)
			System.out.println(MyAvReports[tAg+ 2*agents_name.length].getAvStdDevInterval(start2, end2));
		
		System.out.println();
		System.out.println(" - Quadratic mean of interval stable 1 agent: ");
		for(int tAg = 0; tAg < agents_name.length; tAg++)
			System.out.println(MyAvReports[tAg].getAvQuadraticMeanOfIntervals(start, end));
		
		System.out.println(" - Quadratic mean of interval stable 2 agents: ");
		for(int tAg = 0; tAg < agents_name.length; tAg++)
			System.out.println(MyAvReports[tAg+ agents_name.length].getAvQuadraticMeanOfIntervals(start, end));
		
		System.out.println(" - Quadratic mean of interval stable 2->1 agents: ");
		for(int tAg = 0; tAg < agents_name.length; tAg++)
			System.out.println(MyAvReports[tAg+ 2*agents_name.length].getAvQuadraticMeanOfIntervals(start2, end2));
		
		
		System.out.println();
		System.out.println(" - Average idleness stable 1 : ");
		for(int tAg = 0; tAg < agents_name.length; tAg++)
			System.out.println(MyAvReports[tAg].getAvAverageIdleness(start, end));
		
		System.out.println(" - Average idleness stable 2 : ");
		for(int tAg = 0; tAg < agents_name.length; tAg++)
			System.out.println(MyAvReports[tAg+ agents_name.length].getAvAverageIdleness(start, end));
		
		System.out.println(" - Average idleness stable 2 -> 1 : ");
		for(int tAg = 0; tAg < agents_name.length; tAg++)
			System.out.println(MyAvReports[tAg+ 2*agents_name.length].getAvAverageIdleness(start2, end2));
		
		System.out.println();
		System.out.println(" - Max idleness stable 1 : ");
		for(int tAg = 0; tAg < agents_name.length; tAg++)
			System.out.println(MyAvReports[tAg].getAvMaxInstantaneousIdleness(start, end));
		
		System.out.println(" - Max idleness stable 2 : ");
		for(int tAg = 0; tAg < agents_name.length; tAg++)
			System.out.println(MyAvReports[tAg+ agents_name.length].getAvMaxInstantaneousIdleness(start, end));
		
		System.out.println(" - Max idleness stable 2 -> 1 : ");
		for(int tAg = 0; tAg < agents_name.length; tAg++)
			System.out.println(MyAvReports[tAg+ 2*agents_name.length].getAvMaxInstantaneousIdleness(start2, end2));
		
		System.out.println();
		System.out.println(" - std dev idleness stable 1 : ");
		for(int tAg = 0; tAg < agents_name.length; tAg++)
			System.out.println(MyAvReports[tAg].getAvStdDevOfIdleness(start, end));
		
		System.out.println(" - std dev idleness stable 2 : ");
		for(int tAg = 0; tAg < agents_name.length; tAg++)
			System.out.println(MyAvReports[tAg+ agents_name.length].getAvStdDevOfIdleness(start, end));
		
		System.out.println(" - std dev idleness stable 2 -> 1 : ");
		for(int tAg = 0; tAg < agents_name.length; tAg++)
			System.out.println(MyAvReports[tAg+ 2*agents_name.length].getAvStdDevOfIdleness(start2, end2));
		
		
		int freq = 25;
		last_cycle = 2999;
		Double[] myfreq = new Double[last_cycle/freq +1];
		for(int i = 0; i < myfreq.length; i++)
			myfreq[i] = (double) i * freq;
		
		Double[] myvalues1_0 = MyAvReports[0].getAvAverageIdleness_curb(freq);
		Double[] myvalues1_1 = MyAvReports[1].getAvAverageIdleness_curb(freq);
		Double[] myvalues1_2 = MyAvReports[2].getAvAverageIdleness_curb(freq);
		Double[] myvalues1_3 = MyAvReports[3].getAvAverageIdleness_curb(freq);
		Double[] myvalues1_4 = MyAvReports[4].getAvAverageIdleness_curb(freq);
		Double[] myvalues1_5 = MyAvReports[5].getAvAverageIdleness_curb(freq);
		Double[] myvalues1_6 = MyAvReports[6].getAvAverageIdleness_curb(freq);
		Double[] myvalues1_7 = MyAvReports[7].getAvAverageIdleness_curb(freq);
		
		Double[] myvalues2_0 = MyAvReports[0+ agents_name.length].getAvAverageIdleness_curb(freq);
		Double[] myvalues2_1 = MyAvReports[1+ agents_name.length].getAvAverageIdleness_curb(freq);
		Double[] myvalues2_2 = MyAvReports[2+ agents_name.length].getAvAverageIdleness_curb(freq);
		Double[] myvalues2_3 = MyAvReports[3+ agents_name.length].getAvAverageIdleness_curb(freq);
		Double[] myvalues2_4 = MyAvReports[4+ agents_name.length].getAvAverageIdleness_curb(freq);
		Double[] myvalues2_5 = MyAvReports[5+ agents_name.length].getAvAverageIdleness_curb(freq);
		Double[] myvalues2_6 = MyAvReports[6+ agents_name.length].getAvAverageIdleness_curb(freq);
		Double[] myvalues2_7 = MyAvReports[7+ agents_name.length].getAvAverageIdleness_curb(freq);
		
		Double[] myvalues21_0 = MyAvReports[0+ 2*agents_name.length].getAvAverageIdleness_curb(freq);
		Double[] myvalues21_1 = MyAvReports[1+ 2*agents_name.length].getAvAverageIdleness_curb(freq);
		Double[] myvalues21_2 = MyAvReports[2+ 2*agents_name.length].getAvAverageIdleness_curb(freq);
		Double[] myvalues21_3 = MyAvReports[3+ 2*agents_name.length].getAvAverageIdleness_curb(freq);
		Double[] myvalues21_4 = MyAvReports[4+ 2*agents_name.length].getAvAverageIdleness_curb(freq);
		Double[] myvalues21_5 = MyAvReports[5+ 2*agents_name.length].getAvAverageIdleness_curb(freq);
		Double[] myvalues21_6 = MyAvReports[6+ 2*agents_name.length].getAvAverageIdleness_curb(freq);
		Double[] myvalues21_7 = MyAvReports[7+ 2*agents_name.length].getAvAverageIdleness_curb(freq);
		
		
		System.out.println();
		System.out.println("Transition phase 1000 -> 1800");
		System.out.println();
		
		Double max0 = -1d, max1 = -1d, max2 = -1d, max3 = -1d, max4 = -1d, max5 = -1d, max6 = -1d, max7 = -1d;
		Double turnmax0 = 0d, turnmax1 = 0d, turnmax2 = 0d, turnmax3 = 0d, turnmax4 = 0d, turnmax5 = 0d, turnmax6 = 0d, turnmax7 = 0d;
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
			if(myvalues21_4[i] > max4){
				max4 = myvalues21_4[i];
				turnmax4 = myfreq[i];
			}
			if(myvalues21_5[i] > max5){
				max5 = myvalues21_5[i];
				turnmax5 = myfreq[i];
			}
			if(myvalues21_6[i] > max6){
				max6 = myvalues21_6[i];
				turnmax6 = myfreq[i];
			}

		}
		for(int i = 1500/freq; i < 2300/freq; i++){
			if(myvalues21_3[i] > max3){
				max3 = myvalues21_3[i];
				turnmax3 = myfreq[i];
			}
			if(myvalues21_7[i] > max7){
				max7 = myvalues21_7[i];
				turnmax7 = myfreq[i];
			}
		}
		System.out.println();
		System.out.println("Max mean Interval, time :");
		System.out.println("- " + max0 + ", " + turnmax0);
		System.out.println("- " + max1 + ", " + turnmax1);
		System.out.println("- " + max2 + ", " + turnmax2);
		System.out.println("- " + max3 + ", " + turnmax3);
		System.out.println("- " + max4 + ", " + turnmax4);
		System.out.println("- " + max5 + ", " + turnmax5);
		System.out.println("- " + max6 + ", " + turnmax6);
		System.out.println("- " + max7 + ", " + turnmax7);
		
		CurveViewer myviewer = new CurveViewer("Average Idleness");
		myviewer.addCurve(myfreq, myvalues1_0, Color.blue, DrawStyle.SHORT_DOTS, DrawStyle.POINT_CROSS);
		myviewer.addCurve(myfreq, myvalues2_0, Color.blue, DrawStyle.LONG_DOTS);
		myviewer.addCurve(myfreq, myvalues21_0, Color.blue, DrawStyle.POINT_ROUND);
		
		myviewer.addCurve(myfreq, myvalues1_1, Color.red, DrawStyle.SHORT_DOTS, DrawStyle.POINT_CROSS);
		myviewer.addCurve(myfreq, myvalues2_1, Color.red, DrawStyle.LONG_DOTS);
		myviewer.addCurve(myfreq, myvalues21_1, Color.red, DrawStyle.POINT_ROUND);
		
		myviewer.addCurve(myfreq, myvalues1_2, Color.cyan, DrawStyle.SHORT_DOTS, DrawStyle.POINT_CROSS);
		myviewer.addCurve(myfreq, myvalues2_2, Color.cyan, DrawStyle.LONG_DOTS);
		myviewer.addCurve(myfreq, myvalues21_2, Color.cyan, DrawStyle.POINT_ROUND);
		
		myviewer.addCurve(myfreq, myvalues1_3, Color.green, DrawStyle.SHORT_DOTS, DrawStyle.POINT_CROSS);
		myviewer.addCurve(myfreq, myvalues2_3, Color.green, DrawStyle.LONG_DOTS);
		myviewer.addCurve(myfreq, myvalues21_3, Color.green, DrawStyle.POINT_ROUND);
		
		myviewer.addCurve(myfreq, myvalues1_4, Color.orange, DrawStyle.SHORT_DOTS, DrawStyle.POINT_CROSS);
		myviewer.addCurve(myfreq, myvalues2_4, Color.orange, DrawStyle.LONG_DOTS);
		myviewer.addCurve(myfreq, myvalues21_4, Color.orange, DrawStyle.POINT_ROUND);
		
		myviewer.addCurve(myfreq, myvalues1_5, Color.gray, DrawStyle.SHORT_DOTS, DrawStyle.POINT_CROSS);
		myviewer.addCurve(myfreq, myvalues2_5, Color.gray, DrawStyle.LONG_DOTS);
		myviewer.addCurve(myfreq, myvalues21_5, Color.gray, DrawStyle.POINT_ROUND);
		
		myviewer.addCurve(myfreq, myvalues1_6, Color.pink, DrawStyle.SHORT_DOTS, DrawStyle.POINT_CROSS);
		myviewer.addCurve(myfreq, myvalues2_6, Color.pink, DrawStyle.LONG_DOTS);
		myviewer.addCurve(myfreq, myvalues21_6, Color.pink, DrawStyle.POINT_ROUND);
		
		myviewer.addCurve(myfreq, myvalues1_7, Color.BLACK, DrawStyle.SHORT_DOTS, DrawStyle.POINT_CROSS);
		myviewer.addCurve(myfreq, myvalues2_7, Color.BLACK, DrawStyle.LONG_DOTS);
		myviewer.addCurve(myfreq, myvalues21_7, Color.BLACK, DrawStyle.POINT_ROUND);
		
		myviewer.setXdivision(250);
		myviewer.setYdivision(50);
		myviewer.setVisible(true);
		
		
		
		Double[] mymaxvalues1_0 = MyAvReports[0].getAvMaxIdleness_curb(freq);
		Double[] mymaxvalues1_1 = MyAvReports[1].getAvMaxIdleness_curb(freq);
		Double[] mymaxvalues1_2 = MyAvReports[2].getAvMaxIdleness_curb(freq);
		Double[] mymaxvalues1_3 = MyAvReports[3].getAvMaxIdleness_curb(freq);
		Double[] mymaxvalues1_4 = MyAvReports[4].getAvMaxIdleness_curb(freq);
		Double[] mymaxvalues1_5 = MyAvReports[5].getAvMaxIdleness_curb(freq);
		Double[] mymaxvalues1_6 = MyAvReports[6].getAvMaxIdleness_curb(freq);
		Double[] mymaxvalues1_7 = MyAvReports[7].getAvMaxIdleness_curb(freq);
		
		Double[] mymaxvalues2_0 = MyAvReports[0+ agents_name.length].getAvMaxIdleness_curb(freq);
		Double[] mymaxvalues2_1 = MyAvReports[1+ agents_name.length].getAvMaxIdleness_curb(freq);
		Double[] mymaxvalues2_2 = MyAvReports[2+ agents_name.length].getAvMaxIdleness_curb(freq);
		Double[] mymaxvalues2_3 = MyAvReports[3+ agents_name.length].getAvMaxIdleness_curb(freq);
		Double[] mymaxvalues2_4 = MyAvReports[4+ agents_name.length].getAvMaxIdleness_curb(freq);
		Double[] mymaxvalues2_5 = MyAvReports[5+ agents_name.length].getAvMaxIdleness_curb(freq);
		Double[] mymaxvalues2_6 = MyAvReports[6+ agents_name.length].getAvMaxIdleness_curb(freq);
		Double[] mymaxvalues2_7 = MyAvReports[7+ agents_name.length].getAvMaxIdleness_curb(freq);
		
		Double[] mymaxvalues21_0 = MyAvReports[0+ 2*agents_name.length].getAvMaxIdleness_curb(freq);
		Double[] mymaxvalues21_1 = MyAvReports[1+ 2*agents_name.length].getAvMaxIdleness_curb(freq);
		Double[] mymaxvalues21_2 = MyAvReports[2+ 2*agents_name.length].getAvMaxIdleness_curb(freq);
		Double[] mymaxvalues21_3 = MyAvReports[3+ 2*agents_name.length].getAvMaxIdleness_curb(freq);
		Double[] mymaxvalues21_4 = MyAvReports[4+ 2*agents_name.length].getAvMaxIdleness_curb(freq);
		Double[] mymaxvalues21_5 = MyAvReports[5+ 2*agents_name.length].getAvMaxIdleness_curb(freq);
		Double[] mymaxvalues21_6 = MyAvReports[6+ 2*agents_name.length].getAvMaxIdleness_curb(freq);
		Double[] mymaxvalues21_7 = MyAvReports[7+ 2*agents_name.length].getAvMaxIdleness_curb(freq);
		
		Double maxmax0 = -1d, maxmax1 = -1d, maxmax2 = -1d, maxmax3 = -1d, maxmax4 = -1d, maxmax5 = -1d, maxmax6 = -1d, maxmax7 = -1d;
		Double turnmaxmax0 = 0d, turnmaxmax1 = 0d, turnmaxmax2 = 0d, turnmaxmax3 = 0d, turnmaxmax4 = 0d, turnmaxmax5 = 0d, turnmaxmax6 = 0d, turnmaxmax7 = 0d;
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
			if(mymaxvalues21_4[i] > maxmax4){
				maxmax4 = mymaxvalues21_4[i];
				turnmaxmax4 = myfreq[i];
			}
			if(mymaxvalues21_5[i] > maxmax5){
				maxmax5 = mymaxvalues21_5[i];
				turnmaxmax5 = myfreq[i];
			}
			if(mymaxvalues21_6[i] > maxmax6){
				maxmax6 = mymaxvalues21_6[i];
				turnmaxmax6 = myfreq[i];
			}

		}		
		for(int i = 1500/freq; i < 2300/freq; i++){
			if(mymaxvalues21_3[i] > maxmax3){
				maxmax3 = mymaxvalues21_3[i];
				turnmaxmax3 = myfreq[i];
			}
			if(mymaxvalues21_7[i] > maxmax7){
				maxmax7 = mymaxvalues21_7[i];
				turnmaxmax7 = myfreq[i];
			}
		}
		System.out.println();
		System.out.println("Max max Interval, time :");
		System.out.println("- " + maxmax0 + ", " + turnmaxmax0);
		System.out.println("- " + maxmax1 + ", " + turnmaxmax1);
		System.out.println("- " + maxmax2 + ", " + turnmaxmax2);
		System.out.println("- " + maxmax3 + ", " + turnmaxmax3);
		System.out.println("- " + maxmax4 + ", " + turnmaxmax4);
		System.out.println("- " + maxmax5 + ", " + turnmaxmax5);
		System.out.println("- " + maxmax6 + ", " + turnmaxmax6);
		System.out.println("- " + maxmax7 + ", " + turnmaxmax7);
		
		CurveViewer myviewer2 = new CurveViewer("Max Idleness");
		myviewer2.addCurve(myfreq, mymaxvalues1_0, Color.blue, DrawStyle.SHORT_DOTS, DrawStyle.POINT_CROSS);
		myviewer2.addCurve(myfreq, mymaxvalues2_0, Color.blue, DrawStyle.LONG_DOTS);
		myviewer2.addCurve(myfreq, mymaxvalues21_0, Color.blue, DrawStyle.POINT_ROUND);
		
		myviewer2.addCurve(myfreq, mymaxvalues1_1, Color.red, DrawStyle.SHORT_DOTS, DrawStyle.POINT_CROSS);
		myviewer2.addCurve(myfreq, mymaxvalues2_1, Color.red, DrawStyle.LONG_DOTS);
		myviewer2.addCurve(myfreq, mymaxvalues21_1, Color.red, DrawStyle.POINT_ROUND);
		
		myviewer2.addCurve(myfreq, mymaxvalues1_2, Color.cyan, DrawStyle.SHORT_DOTS, DrawStyle.POINT_CROSS);
		myviewer2.addCurve(myfreq, mymaxvalues2_2, Color.cyan, DrawStyle.LONG_DOTS);
		myviewer2.addCurve(myfreq, mymaxvalues21_2, Color.cyan, DrawStyle.POINT_ROUND);
		
		myviewer2.addCurve(myfreq, mymaxvalues1_3, Color.green, DrawStyle.SHORT_DOTS, DrawStyle.POINT_CROSS);
		myviewer2.addCurve(myfreq, mymaxvalues2_3, Color.green, DrawStyle.LONG_DOTS);
		myviewer2.addCurve(myfreq, mymaxvalues21_3, Color.green, DrawStyle.POINT_ROUND);
		
		myviewer2.addCurve(myfreq, mymaxvalues1_4, Color.orange, DrawStyle.SHORT_DOTS, DrawStyle.POINT_CROSS);
		myviewer2.addCurve(myfreq, mymaxvalues2_4, Color.orange, DrawStyle.LONG_DOTS);
		myviewer2.addCurve(myfreq, mymaxvalues21_4, Color.orange, DrawStyle.POINT_ROUND);
		
		myviewer2.addCurve(myfreq, mymaxvalues1_5, Color.gray, DrawStyle.SHORT_DOTS, DrawStyle.POINT_CROSS);
		myviewer2.addCurve(myfreq, mymaxvalues2_5, Color.gray, DrawStyle.LONG_DOTS);
		myviewer2.addCurve(myfreq, mymaxvalues21_5, Color.gray, DrawStyle.POINT_ROUND);
		
		myviewer2.addCurve(myfreq, mymaxvalues1_6, Color.pink, DrawStyle.SHORT_DOTS, DrawStyle.POINT_CROSS);
		myviewer2.addCurve(myfreq, mymaxvalues2_6, Color.pink, DrawStyle.LONG_DOTS);
		myviewer2.addCurve(myfreq, mymaxvalues21_6, Color.pink, DrawStyle.POINT_ROUND);
		
		myviewer2.addCurve(myfreq, mymaxvalues1_7, Color.black, DrawStyle.SHORT_DOTS, DrawStyle.POINT_CROSS);
		myviewer2.addCurve(myfreq, mymaxvalues2_7, Color.black, DrawStyle.LONG_DOTS);
		myviewer2.addCurve(myfreq, mymaxvalues21_7, Color.black, DrawStyle.POINT_ROUND);
		
		myviewer2.setXdivision(250);
		myviewer2.setYdivision(150);
		myviewer2.setVisible(true);

		
		
		System.out.println();
		System.out.println(" - Stabilization time : ");
		Double[] stab = new Double[25];
		for(int i = stab.length; i > 0; i--)
			stab[stab.length - i] = ((double)i / 100);
		
		start = 1000;
		end = 2999;
		
		int freq2 = 1;
		Double[] myfreq2 = new Double[(2999-1000)/freq2 +1];
		for(int i = 0; i < myfreq2.length; i++)
			myfreq2[i] = 1000 + (double) i * freq2;
		
		Double[] myfreq3 = new Double[(2999-1500)/freq2 +1];
		for(int i = 0; i < myfreq3.length; i++)
			myfreq3[i] = 1500 + (double) i * freq2;
		
		MetricsReport m;
		Double[] curve;
		Double mean;
		Double avturn;
		for(Double stabrate : stab){
			System.out.println("  - " + 100 * stabrate + "% :");
			for(int tAg = 0; tAg < agents_name.length; tAg++){
				avturn = 0d;
				for(int i = 0; i < MyAvReports[2*agents_name.length + tAg].size(); i++){
					if(tAg != 3 && tAg != 7){
						m = MyAvReports[2*agents_name.length + tAg].get(i);
						curve = m.getAverageIdleness_curb(freq2, start, end);
						mean = MetricsReport.MeanValue(2000, end, myfreq2, curve);
						avturn += MetricsReport.TimeToReachTargetValue(mean, stabrate, 1000, 3000, 100, myfreq2, curve);
					}
					else {
						m = MyAvReports[2*agents_name.length + tAg].get(i);
						curve = m.getAverageIdleness_curb(freq2, 1500, end);
						mean = MetricsReport.MeanValue(2500, end, myfreq3, curve);
						avturn += MetricsReport.TimeToReachTargetValue(mean, stabrate, 1500, 3000, 100, myfreq3, curve);
					}
				}
				System.out.println(avturn / MyAvReports[2*agents_name.length + tAg].size() - ((tAg != 3 && tAg != 7)? 1000 : 1500));
			}
		}
		
		
		
		/* transition curb */
		
		myvalues21_0 = MyAvReports[0+ 2*agents_name.length].getAvAverageIdleness_curb(freq);
		myvalues21_1 = MyAvReports[1+ 2*agents_name.length].getAvAverageIdleness_curb(freq);
		myvalues21_2 = MyAvReports[2+ 2*agents_name.length].getAvAverageIdleness_curb(freq);
		myvalues21_3 = MyAvReports[3+ 2*agents_name.length].getAvAverageIdleness_curb(freq);
		myvalues21_4 = MyAvReports[4+ 2*agents_name.length].getAvAverageIdleness_curb(freq);
		myvalues21_5 = MyAvReports[5+ 2*agents_name.length].getAvAverageIdleness_curb(freq);
		myvalues21_6 = MyAvReports[6+ 2*agents_name.length].getAvAverageIdleness_curb(freq);
		myvalues21_7 = MyAvReports[7+ 2*agents_name.length].getAvAverageIdleness_curb(freq);
		
		for(int j = 1500/25; j < myvalues21_3.length; j++){
			myvalues21_3[j - 500/25] = myvalues21_3[j];
			myvalues21_7[j - 500/25] = myvalues21_7[j];
		}
		
		CurveViewer myviewer3 = new CurveViewer("Transition");
		myviewer3.addCurve(myfreq, myvalues21_0, Color.blue, DrawStyle.POINT_ROUND);
		myviewer3.addCurve(myfreq, myvalues21_1, Color.red, DrawStyle.POINT_ROUND);
		//myviewer3.addCurve(myfreq, myvalues21_2, Color.cyan, DrawStyle.POINT_ROUND);
		myviewer3.addCurve(myfreq, myvalues21_3, Color.green, DrawStyle.POINT_ROUND);
		myviewer3.addCurve(myfreq, myvalues21_4, Color.orange, DrawStyle.POINT_ROUND);
		myviewer3.addCurve(myfreq, myvalues21_5, Color.gray, DrawStyle.POINT_ROUND);
		myviewer3.addCurve(myfreq, myvalues21_6, Color.pink, DrawStyle.POINT_ROUND);
		myviewer3.addCurve(myfreq, myvalues21_7, Color.black, DrawStyle.POINT_ROUND);
		
		myviewer3.setXdivision(250);
		myviewer3.setYdivision(50);
		myviewer3.setVisible(true);
		
	}

}