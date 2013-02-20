package Analyses;

import java.awt.Color;

import tools.metrics.LogFileParser;
import tools.metrics.MetricsReport;
import util.CurveViewer;
import util.DrawStyle;

public class annonceurs {

	public static void main(String[] args){
		String Log_dir = "/home/pouletc/experimentation/financeurs/";
		String[] Log_names = {"RR.txt", "CC.txt", "SCf.txt"};
		int cycle_num = 1500;
	
		MetricsReport[] MyReports = new MetricsReport[3];
		
		LogFileParser parser;
		MetricsReport metrics;
		
		int num_agents = 5;
		
		for(int i = 0; i < Log_names.length; i++){
			try{
				parser = new LogFileParser();
				
				parser.parseFile(Log_dir + Log_names[i]);
				
				num_agents = parser.getNumAgents();
	
				metrics = new MetricsReport(parser.getNumNodes(), 0, cycle_num, parser.getVisitsList());
				MyReports[i] = metrics;
				System.out.println("log "+ i + " read");
				
			} catch (Exception e){
				continue;
	
			}
		}
		
		
		int freq = 5;
		Double[] myfreq = new Double[cycle_num/freq + 1];
		for(int i = 0; i < myfreq.length; i++)
			myfreq[i] = (double) i * freq;
		
		Double[] SCf = MyReports[0].getAverageIdleness_curb(freq);
		Double[] SCs = MyReports[1].getAverageIdleness_curb(freq);
		Double[] HPCC = MyReports[2].getAverageIdleness_curb(freq);
		
		CurveViewer myviewer = new CurveViewer("Idleness");
		myviewer.addCurve(myfreq, SCf, Color.blue, DrawStyle.POINT_ROUND);
		myviewer.addCurve(myfreq, SCs, Color.black, DrawStyle.LONG_DOTS);
		myviewer.addCurve(myfreq, HPCC, Color.red, DrawStyle.SHORT_DOTS, DrawStyle.POINT_ROUND);
		myviewer.setXdivision(250);
		myviewer.setYdivision(10);
		myviewer.setVisible(true);
		
	}
}
