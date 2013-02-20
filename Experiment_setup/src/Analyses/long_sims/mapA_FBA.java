package Analyses.long_sims;

import java.awt.Color;
import java.io.FileNotFoundException;
import java.io.IOException;

import tools.metrics.LogFileParser;
import tools.metrics.MetricsReport;
import util.CurveViewer;
import AverageMetrics.AverageMetricsReport;

import com.twicom.qdparser.XMLParseException;

public class mapA_FBA {

	public static void main(String[] args) {
		String global_path = "/home/pouletc/experimentation/Simulations/mapA/";

		String[] agents_name = { "SC", "HPCC", "FBA"};

		int nb_log = 15;
		int start_log_num = 0;
		int last_cycle = 19999;

		int not_done = 0;

		AverageMetricsReport[] MyAvReports = new AverageMetricsReport[3];

		LogFileParser parser;
		MetricsReport metrics;
		int current = 0;


		String[] logs_dir = new String[]{
				"1_long/logs_OpenSC/log_", 
				"1_long/logs_HPCC/log_",
				"0_long/logs_Open_FBA_custom/log_"};


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

		System.out.println("Not done : " + not_done);
		
		int freq = 25;
		Double[] myvaluesHPCC = MyAvReports[1].getAvAverageIdleness_curb(freq);
		Double[] myvaluesSC = MyAvReports[0].getAvAverageIdleness_curb(freq);
		Double[] myvaluesFBA = MyAvReports[2].getAvAverageIdleness_curb(freq);
		
		Double[] myfreq = new Double[19999/freq +1];
		for(int i = 0; i < myfreq.length; i++)
			myfreq[i] = (double) i * freq;
		
		CurveViewer myviewer = new CurveViewer("Idleness");
		myviewer.addCurve(myfreq, myvaluesHPCC, Color.red);
		myviewer.addCurve(myfreq, myvaluesSC, Color.blue);
		myviewer.addCurve(myfreq, myvaluesFBA, Color.green);
		myviewer.setXdivision(1000);
		myviewer.setYdivision(10);
		myviewer.setVisible(true);
		
		Double[] mymaxvaluesHPCC = MyAvReports[1].getAvMaxIdleness_curb(freq);
		Double[] mymaxvaluesSC = MyAvReports[0].getAvMaxIdleness_curb(freq);
		Double[] mymaxvaluesFBA = MyAvReports[2].getAvMaxIdleness_curb(freq);
		
		CurveViewer mymaxviewer = new CurveViewer("max Idleness");
		mymaxviewer.addCurve(myfreq, mymaxvaluesHPCC, Color.red);
		mymaxviewer.addCurve(myfreq, mymaxvaluesSC, Color.blue);
		mymaxviewer.addCurve(myfreq, mymaxvaluesFBA, Color.green);
		mymaxviewer.setXdivision(1000);
		mymaxviewer.setYdivision(50);
		mymaxviewer.setVisible(true);
		
		
		int[] start = new int[]{1500, 4000, 10300, 14000, 16400};
		int[] end = new int[]{2500, 5000, 11300, 15000, 17400};
		
		
		for(int i = 0; i < start.length; i++){
			System.out.println(start[i] + " - " + end[i]);

			System.out.println(" - Average interval stable: ");
			for(int j = 0; j < MyAvReports.length; j++)
				System.out.println(MyAvReports[j].getAvAverageInterval(start[i], end[i]));

			System.out.println(" - Maximum interval stable: ");
			for(int j = 0; j < MyAvReports.length; j++)
				System.out.println(MyAvReports[j].getAvMaxInterval(start[i], end[i]));

			System.out.println(" - Standard deviation of the intervals stable: ");
			for(int j = 0; j < MyAvReports.length; j++)
				System.out.println(MyAvReports[j].getAvStdDevInterval(start[i], end[i]));

			System.out.println(" - Quadratic mean of stable: ");
			for(int j = 0; j < MyAvReports.length; j++)
				System.out.println(MyAvReports[j].getAvQuadraticMeanOfIntervals(start[i], end[i]));


			System.out.println();

			System.out.println(" - Maximum instantaneous idleness stable: ");
			for(int j = 0; j < MyAvReports.length; j++)
				System.out.println(MyAvReports[j].getAvMaxInstantaneousIdleness(start[i], end[i]));

			System.out.println(" - Average idleness stable: ");
			for(int j = 0; j < MyAvReports.length; j++)
				System.out.println(MyAvReports[j].getAvAverageIdleness(start[i], end[i]));

			System.out.println(" - std dev idleness stable: ");
			for(int j = 0; j < MyAvReports.length; j++)
				System.out.println(MyAvReports[j].getAvStdDevOfIdleness(start[i], end[i]));

		}
		
		/*
		 * 2 agents
		 */

		
		
		
		int[] agents_num = new int[]{2, 3, 4, 5, 6, 7, 8, 9, 10};
		start = new int[]{16800, 19000, 7600, 14000, 12200, 9000, 500, 2000, 4300};
		end   = new int[]{17800, 20000, 8100, 15000, 12400, 9800, 1000, 3000, 5300};
		
		double[][] interval = new double[agents_name.length][agents_num.length];
		double[][] MSI = new double[agents_name.length][agents_num.length];
		double[][] idleness = new double[agents_name.length][agents_num.length];
		double[][] max_idle = new double[agents_name.length][agents_num.length];
		
		for(int i = 0; i < start.length; i++){
			for(int j = 0; j < MyAvReports.length; j++)
				interval[j][i] = MyAvReports[j].getAvAverageInterval(start[i], end[i]);

			for(int j = 0; j < MyAvReports.length; j++)
				MSI[j][i] = MyAvReports[j].getAvQuadraticMeanOfIntervals(start[i], end[i]);

			for(int j = 0; j < MyAvReports.length; j++)
				idleness[j][i] = MyAvReports[j].getAvAverageIdleness(start[i], end[i]);
			
			for(int j = 0; j < MyAvReports.length; j++)
				max_idle[j][i] = MyAvReports[j].getAvMaxInstantaneousIdleness(start[i], end[i]);

		}
		
		System.out.println("stats by agent num");
		for(int i = 0; i < agents_num.length; i++){
			System.out.println(agents_num[i]);

			System.out.println(" - interval: ");
			for(int j = 0; j < MyAvReports.length; j++)
				System.out.println(interval[j][i]);
			
			System.out.println(" - MSI: ");
			for(int j = 0; j < MyAvReports.length; j++)
				System.out.println(MSI[j][i]);
			
			System.out.println(" - idleness: ");
			for(int j = 0; j < MyAvReports.length; j++)
				System.out.println(idleness[j][i]);
			
			System.out.println(" - max idleness: ");
			for(int j = 0; j < MyAvReports.length; j++)
				System.out.println(max_idle[j][i]);
			
			System.out.println();
		}
		
		

		freq = 25;
		myfreq = new Double[19999/freq +1];
		for(int i = 0; i < myfreq.length; i++)
			myfreq[i] = (double) i * freq;

		int[] transition_start = new int[]{200, 1000, 3600, 5400, 5700, 5800, 6500, 6600, 7100, 8100, 8400, 10000, 11800, 12400, 12600, 16200, 17900};
		int[] transition_end   = new int[]{700, 1500, 4100, 5700, 5800, 6300, 6600, 7100, 7600, 8400, 8900, 10500, 12300, 12600, 13100, 16700, 18400};
		int[] nb_agents = new int[]{8, 9, 10, 9, 10, 9, 7, 6, 4, 6, 7, 5, 6, 7, 5, 2, 3};
		
		
		Double[] myvalues21_0 = MyAvReports[0].getAvAverageIdleness_curb(freq);
		Double[] myvalues21_1 = MyAvReports[1].getAvAverageIdleness_curb(freq);
		Double[] myvalues21_2 = MyAvReports[2].getAvAverageIdleness_curb(freq);

		System.out.println();
		System.out.println("Max mean Interval, time :");
		for(int trans = 0; trans < transition_start.length; trans++){
		
			Double max0 = -1d, max1 = -1d, max2 = -1d, max3 = -1d, max4 = -1d;
			Double turnmax0 = 0d, turnmax1 = 0d, turnmax2 = 0d, turnmax3 = 0d, turnmax4 = 0d;
			for(int i = transition_start[trans]/freq; i < transition_end[trans]/freq; i++){
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
			}		
			
			System.out.println(max0);
			System.out.println(max1);
			System.out.println( max2);
			System.out.println();
		}


		Double[] mymaxvalues21_0 = MyAvReports[0].getAvMaxIdleness_curb(freq);
		Double[] mymaxvalues21_1 = MyAvReports[1].getAvMaxIdleness_curb(freq);
		Double[] mymaxvalues21_2 = MyAvReports[2].getAvMaxIdleness_curb(freq);

		System.out.println();
		System.out.println("Max max Interval, time :");
		for(int trans = 0; trans < transition_start.length; trans++){
			
			Double maxmax0 = -1d, maxmax1 = -1d, maxmax2 = -1d, maxmax3 = -1d, maxmax4 = -1d;
			Double turnmaxmax0 = 0d, turnmaxmax1 = 0d, turnmaxmax2 = 0d, turnmaxmax3 = 0d, turnmaxmax4 = 0d;
			for(int i = transition_start[trans]/freq; i < transition_end[trans]/freq; i++){
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
			}		
		
			System.out.println(maxmax0);
			System.out.println(maxmax1);
			System.out.println(maxmax2);
			System.out.println(maxmax3);
			System.out.println(maxmax4 );
			System.out.println();
		}

		
		
		System.out.println();
		System.out.println(" - Stabilization time : ");
		double stab = 0.01;

		for(int i = 0; i < transition_start.length; i++){
			int start_t = transition_start[i];
			int end_t = transition_end[i];
	
			int freq2 = 1;
			Double[] myfreq2 = new Double[(end_t-start_t)/freq2 +1];
			for(int j = 0; j < myfreq2.length; j++)
				myfreq2[j] = start_t + (double) j * freq2;
	
			MetricsReport m;
			Double[] curve;
			Double mean;
			Double avturn;
			for(int tAg = 0; tAg < agents_name.length; tAg++){
				avturn = 0d;
				for(int j = 0; j < MyAvReports[tAg].size(); j++){
					m = MyAvReports[tAg].get(j);
					curve = m.getAverageIdleness_curb(freq2, start_t, end_t);
					mean = idleness[tAg][nb_agents[i]-2];
					avturn += MetricsReport.TimeToReachTargetValue(mean, stab, start_t, end_t, 50, myfreq2, curve);
				}
				System.out.println(avturn / MyAvReports[tAg].size() - start_t);
			}
		
		}
	}

}

