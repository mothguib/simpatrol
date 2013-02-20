package Analyses.long_sims;

import java.awt.Color;
import java.io.FileNotFoundException;
import java.io.IOException;

import tools.metrics.LogFileParser;
import tools.metrics.MetricsReport;
import util.CurveViewer;
import AverageMetrics.AverageMetricsReport;

import com.twicom.qdparser.XMLParseException;

public class mapB {

	public static void main(String[] args) {
		String global_path = "/home/pouletc/experimentation/Simulations/mapB/";

		/*
		[5,5,5,5,5,5,5,5,5,5,
		 6,6,6,6,6,6,6,4,4,4,
		 4,4,4,4,8,8,5,5,5,5,
		 6,6,6,6,6,6,6,6,6,6,
		 6,6,6,6,6,6,6,6,6,6,
		 7,7,7,7,7,6,6,6,6,6,
		 6,6,6,6,6,6,6,5,5,5,
		 5,5,5,5,5,5,5,5,5,5,
		 5,5,5,5,5,6,9,9,9,9,
		 9,9,9,9,9,9,9,9,9,9,
		 9,9,5,5,5,5,5,5,5,5,
		 5,5,5,5,5,6,6,6,6,6,
		 6,6,6,6,6,7,7,7,7,7,
		 7,7,7,7,7,7,7,7,7,6,
		 6,6,6,6,6,6,6,6,2,2,
		 2,2,2,2,2,2,2,2,2,2,
		 2,2,2,2,2,2,4,4,4,4,
		 5,6,6,6,4,5,5,5,5,5,
		 5,5,5,6,6,6,6,6,6,6,
		 11,11,11,11,11,11,11,11,11,11]
		 */
		
		String[] agents_name = { "SC", "HPCC", "Minisum", "Minisum2", "Minisum3"};

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
		
 
		 
		int[] start = new int[]{3500, 5700, 7000, 9000, 12800, 15000};
		int[] end = new int[]{4500, 6700, 8000, 10000, 13800, 16000};
		
		
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
		start = new int[]{15000, 0, 1900, 7000, 4000, 12900, 0, 9000, 0, 19200};
		end   = new int[]{16000, 0, 2500, 8000, 5000, 13900, 0, 10000, 0, 20000};
		
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
		
		// moyenne (indicatif mais pas parfait)
		for(int i = 0; i < agents_name.length; i++){
			interval[i][1] = (interval[i][0] + interval[i][2])/2;
			interval[i][6] = (interval[i][5] + interval[i][7])/2;
			interval[i][8] = (interval[i][7] + interval[i][9])/2;
			MSI[i][1] = (MSI[i][0] + MSI[i][2])/2;
			MSI[i][6] = (MSI[i][5] + MSI[i][7])/2;
			MSI[i][8] = (MSI[i][7] + MSI[i][9])/2;
			idleness[i][1] = (idleness[i][0] + idleness[i][2])/2;
			idleness[i][6] = (idleness[i][5] + idleness[i][7])/2;
			idleness[i][8] = (idleness[i][7] + idleness[i][9])/2;
			max_idle[i][1] = (max_idle[i][0] + max_idle[i][2])/2;
			max_idle[i][6] = (max_idle[i][5] + max_idle[i][7])/2;
			max_idle[i][8] = (max_idle[i][7] + max_idle[i][9])/2;
			
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

		int[] transition_start = new int[]{1000, 1700, 2400, 2600, 3000, 5000, 5500, 6700, 8500, 8600, 10200, 11500, 12500, 13900, 14800, 16600, 17000, 
				17100, 17400, 17500, 18300, 19000};
		int[] transition_end   = new int[]{1500, 2300, 2600, 3000, 3500, 5500, 6000, 8200, 8600, 9100, 10700, 12000, 13000, 14400, 15300, 17000, 17100,
				17400, 17500, 18000, 18800, 19500};
		int[] nb_agents = new int[]{6, 4, 8, 5, 6, 7, 6, 5, 6, 9, 5, 6, 7, 6, 2, 4, 5, 6, 4, 5, 6, 11};
		
		
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
			System.out.println(maxmax4);
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

