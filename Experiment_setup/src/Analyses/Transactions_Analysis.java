package Analyses;

import java.awt.Color;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.LinkedList;

import closed.FBA2.TransactionAnalyzer;

import util.CurveViewer;

public class Transactions_Analysis {

	/**
	 * @param args
	 */
	public static void main2(String[] args) {
		
		String Log_dir = args[0];
		String Log_gen_name = args[1];
		int log_num = Integer.parseInt(args[2]);
		int cycle_num = Integer.parseInt(args[3]);
	
		Log_dir = "/home/pouletc/experimentation/Simulations/mapB/0_15_14_open/logs_OpenFBARandom";
		cycle_num = 2999;
		
		LinkedList<TransactionAnalyzer> Analyzers = new LinkedList<TransactionAnalyzer>();

		TransactionAnalyzer analyzer;
		
		int num_agents = 10;
		
		for(int i = 0; i < log_num; i++){
			try{
				analyzer = new TransactionAnalyzer();
				analyzer.ReadDoc_openSystem(Log_dir + "/" + Log_gen_name + "_" + i + ".txt");
				
				Analyzers.add(analyzer);
				System.out.println("log "+ i + " read");
				//analyzer.Analyze();
			} catch (Exception e){
				System.out.print(e.getMessage());
				continue;

			}
		}
			
		int freq = 10;
		Double[] myfreq = new Double[2999/freq +1];
		for(int i = 0; i < myfreq.length; i++)
			myfreq[i] = (double) i * freq;
		Double[] my_distrib = Analyzers.get(0).getTransactionDistribution(0, 2999, 10);
		
		for(int i = 1; i < Analyzers.size(); i++){
			Double[] distrib = Analyzers.get(i).getTransactionDistribution(0, 2999, 10);
			for(int j = 0; j < my_distrib.length; j++)
				my_distrib[j] += distrib[j];
		}
		
		for(int j = 0; j < my_distrib.length; j++)
			my_distrib[j] /= Analyzers.size();
			
		
		CurveViewer myviewer = new CurveViewer("exchanges");
		myviewer.addCurve(myfreq, my_distrib, Color.blue);
		myviewer.setXdivision(100);
		myviewer.setYdivision(1);
		myviewer.setVisible(true);
	}
	
	public static void main3(String[] args) {
		String global_path = "/home/pouletc/experimentation/Simulations/circle/";
		
		int num_agents = 15;
		String[] agents_name = {"Minisum"};
		
		int nb_log = 15;
		int[] start_log_num = new int[agents_name.length];
		int last_cycle = 19999;
		
		int not_done = 0;
		
		LinkedList<TransactionAnalyzer> Analyzers = new LinkedList<TransactionAnalyzer>();
		TransactionAnalyzer analyzer;
		
		String[] logs_dir = new String[]{
				"0_long/logs_Minisum_corrected/log_"};

		int current = 0;
		for(int type = 0; type < logs_dir.length; type++){
			for(int i = 0; i < nb_log; i++){
				analyzer = new TransactionAnalyzer();
				
				try {
					analyzer = new TransactionAnalyzer();
					analyzer.ReadDoc_openSystem(global_path + logs_dir[type] + i + ".txt");
					
					Analyzers.add(analyzer);
					System.out.println("Agents : "+ agents_name[type]  + ", log "+ i + " read");
					current++;
				} catch (FileNotFoundException e) {
					// TODO Auto-generated catch block
					not_done++;
					e.printStackTrace();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					not_done++;
					e.printStackTrace();
				}
				
			}
			
			if(type < agents_name.length - 1)
				start_log_num[type+1] = current;
			
		}
		

		System.out.println("Not done : " + not_done);
		
		for(int Ag = 0; Ag < agents_name.length; Ag++){
			int nb_messages = 0;
			for(int i = start_log_num[Ag]; i < ((Ag == agents_name.length - 1)? Analyzers.size() : start_log_num[Ag+1] - 1); i++){
				nb_messages += Analyzers.get(i).nb_messages;
			}
		
			nb_messages /= ((Ag == agents_name.length - 1)? Analyzers.size() : start_log_num[Ag+1] - 1) - start_log_num[Ag];
			System.out.println(agents_name[Ag] + " : " + nb_messages + " messages");
		
		}
		
/*		
		int freq = 25;
		int first_cicle = 1000;
		last_cycle = 2000;
		Double[] myfreq = new Double[(last_cycle - first_cicle)/freq];
		for(int i = 0; i < myfreq.length; i++)
			myfreq[i] = (double) i * freq;
		
		Double[][] my_distribs = new Double[agents_name.length][];
		
		for(int type = 0; type < agents_name.length; type++){
			my_distribs[type] = Analyzers.get(start_log_num[type]).getTransactionDistribution(first_cicle, last_cycle, freq);
			
			if(type < agents_name.length - 1){
				for(int i = start_log_num[type] + 1; i < start_log_num[type + 1]; i++){
					Double[] distrib = Analyzers.get(i).getTransactionDistribution(first_cicle, last_cycle, freq);
					for(int j = 0; j < my_distribs[type].length; j++)
						my_distribs[type][j] += distrib[j];
				}
				
				for(int j = 0; j < my_distribs[type].length; j++)
					my_distribs[type][j] /= (start_log_num[type + 1] - start_log_num[type]);
			}
			else {
				for(int i = start_log_num[type] + 1; i < Analyzers.size(); i++){
					Double[] distrib = Analyzers.get(i).getTransactionDistribution(first_cicle, last_cycle, freq);
					for(int j = 0; j < my_distribs[type].length; j++)
						my_distribs[type][j] += distrib[j];
				}
				
				for(int j = 0; j < my_distribs[type].length; j++)
					my_distribs[type][j] /= (Analyzers.size() - start_log_num[type]);
			}
				
		}
				
				
			
		CurveViewer myviewer = new CurveViewer("exchanges");
		myviewer.addCurve(myfreq, my_distribs[0], Color.blue);
		myviewer.addCurve(myfreq, my_distribs[1], Color.BLACK);
		myviewer.addCurve(myfreq, my_distribs[2], Color.cyan);
		myviewer.addCurve(myfreq, my_distribs[3], Color.MAGENTA);
		myviewer.addCurve(myfreq, my_distribs[4], Color.orange);
		myviewer.addCurve(myfreq, my_distribs[5], Color.pink);
//		myviewer.addCurve(myfreq, my_distribs[6], Color.green);
		myviewer.setXdivision(100);
		myviewer.setYdivision(1);
		myviewer.setVisible(true);
		
		System.out.println();
		for(int i = 0; i < myfreq.length; i++){
			System.out.println(myfreq[i]);
			for(int j = 0; j < agents_name.length; j++)
				System.out.println( my_distribs[j][i]);
			
			System.out.println();
		}
		*/
	}
	
	
	public static void main(String[] args) {
		
		String Log_dir = args[0];
		String Log_gen_name = args[1];
		int log_num = Integer.parseInt(args[2]);
		int cycle_num = Integer.parseInt(args[3]);
	
		Log_dir = "/home/pouletc/experimentation/Simulations/islands/0_14_15_open/logs_OpenFBARandom";
		Log_dir = "/home/pouletc/experimentation/Simulations/islands/0_14_15_open/logs_OpenFBANodes";
		Log_dir = "/home/pouletc/experimentation/Simulations/islands/0_14_15_open/logs_OpenFBANodesGroup";
		Log_dir = "/home/pouletc/experimentation/Simulations/islands/0_14_15_open/logs_OpenFBANodes2/8";
		Log_dir = "/home/pouletc/experimentation/Simulations/islands/0_14_15_open/logs_OpenFBANodes2/16";
		Log_dir = "/home/pouletc/experimentation/Simulations/islands/0_14_15_open/logs_OpenFBANodes2/24";
		cycle_num = 2999;
		log_num = 10;
		
		LinkedList<TransactionAnalyzer> Analyzers = new LinkedList<TransactionAnalyzer>();

		TransactionAnalyzer analyzer;
		
		int num_agents = 5;
		
		for(int i = 0; i < log_num; i++){
			try{
				analyzer = new TransactionAnalyzer();
				analyzer.ReadDoc_openSystem(Log_dir + "/" + Log_gen_name + "_" + i + ".txt");
				
				Analyzers.add(analyzer);
				System.out.println("log "+ i + " read");
				//analyzer.Analyze();
			} catch (Exception e){
				System.out.print(e.getMessage());
				continue;

			}
		}
			
		int start_time = 1000;
		int end_time = 2000;
		
		double nbTrans = 0;
		for(int i = 0; i < Analyzers.size(); i++)
			nbTrans += Analyzers.get(i).getNbSuccessfulTransaction(start_time, end_time, true);
		
		System.out.print(nbTrans/Analyzers.size());
	}
}
