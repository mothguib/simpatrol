package Analyses.long_sims;

import java.awt.Color;
import java.io.FileNotFoundException;
import java.io.IOException;

import tools.metrics.LogFileParser;
import tools.metrics.MetricsReport;
import util.CurveViewer;
import AverageMetrics.AverageMetricsReport;

import com.twicom.qdparser.XMLParseException;

public class islands {

	public static void main(String[] args) {
		String global_path = "/home/pouletc/experimentation/Simulations/islands/";

		/*
		[5,5,5,5,5,5,5,5,5,4,
		 4,4,4,4,4,4,4,4,7,7,
		 9,9,9,9,9,9,9,9,9,9,
		 9,9,7,7,7,7,7,7,7,4,
		 4,2,2,2,2,2,2,2,3,3,
		 3,3,3,4,4,4,4,5,5,5,
		 5,5,5,5,5,5,5,5,5,5,
		 5,5,5,5,5,5,5,5,5,5,
		 5,5,5,5,5,5,5,5,6,6,
		 6,6,6,6,6,6,4,4,4,4,
		 4,4,4,5,5,5,5,5,5,8,
		 8,8,10,10,10,10,10,10,10,10,
		 10,10,10,10,10,7,7,7,7,7,
		 7,7,5,5,5,5,5,5,5,5,
		 5,5,5,8,8,8,11,11,11,11,
		 8,8,8,8,8,8,4,4,4,4,
		 4,3,3,3,3,3,3,2,2,2,
		 2,2,2,2,2,6,6,6,6,6,
		 6,6,6,6,6,6,6,6,6,6,
		 6,6,6,6,6,6,6,6,6,6]
		 */
		
		String[] agents_name = { "SC", "HPCC", "Minisum", "Minisum 2", "Minisum 3"};

		int nb_log = 15;
		int start_log_num = 0;
		int last_cycle = 19999;

		int not_done = 0;

		AverageMetricsReport[] MyAvReports = new AverageMetricsReport[5];

		LogFileParser parser;
		MetricsReport metrics;
		int current = 0;


		String[] logs_dir = new String[]{
				"1_long/logs_OpenSC/log_", 
				"1_long/logs_OpenHPCC/log_",
				"0_long/logs_Minisum_corrected/log_",
				"0_long/logs_Minisum2/log_",
				"0_long/logs_Minisum3/log_"};


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
		Double[] myvaluesMinimax = MyAvReports[3].getAvAverageIdleness_curb(freq);
		Double[] myvaluesMinisum = MyAvReports[4].getAvAverageIdleness_curb(freq);
		
		Double[] myfreq = new Double[19999/freq +1];
		for(int i = 0; i < myfreq.length; i++)
			myfreq[i] = (double) i * freq;
		
		CurveViewer myviewer = new CurveViewer("Idleness");
		myviewer.addCurve(myfreq, myvaluesHPCC, Color.red);
		myviewer.addCurve(myfreq, myvaluesSC, Color.blue);
		myviewer.addCurve(myfreq, myvaluesFBA, Color.green);
		myviewer.addCurve(myfreq, myvaluesMinimax, Color.cyan);
		myviewer.addCurve(myfreq, myvaluesMinisum, Color.orange);
		myviewer.setXdivision(1000);
		myviewer.setYdivision(10);
		myviewer.setVisible(true);
		
		Double[] mymaxvaluesHPCC = MyAvReports[1].getAvMaxIdleness_curb(freq);
		Double[] mymaxvaluesSC = MyAvReports[0].getAvMaxIdleness_curb(freq);
		Double[] mymaxvaluesFBA = MyAvReports[2].getAvMaxIdleness_curb(freq);
		Double[] mymaxvaluesMinimax = MyAvReports[3].getAvMaxIdleness_curb(freq);
		Double[] mymaxvaluesMinisum = MyAvReports[4].getAvMaxIdleness_curb(freq);
		
		CurveViewer mymaxviewer = new CurveViewer("max Idleness");
		mymaxviewer.addCurve(myfreq, mymaxvaluesHPCC, Color.red);
		mymaxviewer.addCurve(myfreq, mymaxvaluesSC, Color.blue);
		mymaxviewer.addCurve(myfreq, mymaxvaluesFBA, Color.green);
		mymaxviewer.addCurve(myfreq, mymaxvaluesMinimax, Color.cyan);
		mymaxviewer.addCurve(myfreq, mymaxvaluesMinisum, Color.orange);
		mymaxviewer.setXdivision(1000);
		mymaxviewer.setYdivision(50);
		mymaxviewer.setVisible(true);
		
		
		int[] start = new int[]{1200, 2500, 6000, 11500, 17000};
		int[] end = new int[]{1800, 3200, 7000, 12500, 17500};
		
		
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
		
		
		int[] agents_num = new int[]{2, 3, 4, 5, 6, 7, 8, 9, 10, 11};
		start = new int[]{17000, 5000, 1300, 7000, 19000, 3500, 15300, 2300, 11500, 14300};
		end   = new int[]{17500, 5300, 1800, 8000, 20000, 3900, 15600, 3200, 12500, 14400};
		
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

		int[] transition_start = new int[]{900, 1800, 2000, 3200, 3900, 4100, 4800, 5300, 5700, 8800, 9600, 10300, 10900, 11200, 12500, 13200, 
				14300, 14600, 15000, 15600, 16100, 16700, 17500};
		int[] transition_end   = new int[]{1400, 2000, 2500, 3700, 4100, 4600, 5300, 5700, 6200, 9300, 10100, 10800, 11200, 11700, 13000, 13700, 
				14600, 15000, 15500, 16100, 16600, 17200, 18000};
		int[] nb_agents = new int[]{4, 7, 9, 7, 4, 2, 3, 4, 5, 6, 4, 5, 8, 10, 7, 5, 8, 11, 8, 4, 3, 2, 6};
		
		
		Double[] myvalues21_0 = MyAvReports[0].getAvAverageIdleness_curb(freq);
		Double[] myvalues21_1 = MyAvReports[1].getAvAverageIdleness_curb(freq);
		Double[] myvalues21_2 = MyAvReports[2].getAvAverageIdleness_curb(freq);
		Double[] myvalues21_3 = MyAvReports[3].getAvAverageIdleness_curb(freq);
		Double[] myvalues21_4 = MyAvReports[4].getAvAverageIdleness_curb(freq);

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
				if(myvalues21_3[i] > max3){
					max3 = myvalues21_3[i];
					turnmax3 = myfreq[i];
				}
				if(myvalues21_4[i] > max4){
					max4 = myvalues21_4[i];
					turnmax4 = myfreq[i];
				}
			}		
			
			System.out.println(max0);
			System.out.println(max1);
			System.out.println( max2);
			System.out.println( max3);
			System.out.println( max4);
			System.out.println();
		}


		Double[] mymaxvalues21_0 = MyAvReports[0].getAvMaxIdleness_curb(freq);
		Double[] mymaxvalues21_1 = MyAvReports[1].getAvMaxIdleness_curb(freq);
		Double[] mymaxvalues21_2 = MyAvReports[2].getAvMaxIdleness_curb(freq);
		Double[] mymaxvalues21_3 = MyAvReports[3].getAvMaxIdleness_curb(freq);
		Double[] mymaxvalues21_4 = MyAvReports[4].getAvMaxIdleness_curb(freq);

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
				if(mymaxvalues21_3[i] > maxmax3){
					maxmax3 = mymaxvalues21_3[i];
					turnmaxmax3 = myfreq[i];
				}
				if(mymaxvalues21_4[i] > maxmax4){
					maxmax4 = mymaxvalues21_4[i];
					turnmaxmax4 = myfreq[i];
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
		System.out.println();
	}

}

