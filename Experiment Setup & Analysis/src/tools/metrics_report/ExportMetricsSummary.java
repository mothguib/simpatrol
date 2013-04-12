package tools.metrics_report;

import java.io.File;
import java.io.FilenameFilter;
import java.text.DecimalFormat;
import java.util.Set;
import java.util.TreeSet;

import tools.metrics_report.core.LogFileParser;
import tools.metrics_report.core.MetricsReport;
import util.cool_table.CoolTable;
import util.cool_table.CoolTableList;


public class ExportMetricsSummary {
	public static String LOG_DIR = "..\\..\\_experiments-pos-tese";
	public static int START_CYCLE = 0;
	public static int FINAL_CYCLE = 2999;
		
	public static FilenameFilter LOG_FILE_FILTER = new FilenameFilter() {			
		@Override
		public boolean accept(File dir, String name) {
			return name.endsWith(".log");
		}
	};
	
	
	public static Set<String> findMaps() {
		File logDir = new File(LOG_DIR);
		Set<String> result = new TreeSet<>();

		File[] logFiles = logDir.listFiles(LOG_FILE_FILTER);

		for (File file : logFiles) {
			String[] parts = file.getName().split("-");
			//System.out.println(parts[0] + " | " + parts[1] + " | " + parts[2] + " | " + parts[3]);
			
			result.add(parts[1]);
		}
		
		System.out.println("Mapas encontrados: " + result);		
		return result;
	}
	
	public static void generateReport(String mapName) throws Exception {
		File logDir = new File(LOG_DIR);
		
		CoolTableList results = new CoolTableList("Results for map " + mapName);
		
		CoolTable tableINTqmean = results.createTable("INTqmean"),
				  tableINTavg   = results.createTable("INTavg"),
		          tableINTstdev = results.createTable("INTstdev"),		          
		          tableINTmax   = results.createTable("INTmax"),
		          tableNvisits  = results.createTable("Nvisits"),
		          tableOavg     = results.createTable("Oavg"),
		          tableINTspecial = results.createTable("(INTqmean)^2/INTavg");
		          //tableINTpmi4  = results.createTable("INTpmi-4");
		
		File[] logFiles = logDir.listFiles(LOG_FILE_FILTER);
		DecimalFormat df= new DecimalFormat("#0.0000");
		
		LogFileParser parser = new LogFileParser(false);

		for (File file : logFiles) {
			String[] parts = file.getName().split("-");
			String algorithm = parts[0];
			String map = parts[1];
			String numAgents = parts[2];
			
			System.out.println("File: " + file);			
			
			if (map.equals(mapName)) {
				try {
					System.out.println("Parsing...");

					parser.parseFile(file.getAbsolutePath());

					if (parser.getLastCycle() < FINAL_CYCLE) {
						System.out.println("Warning: Last cycle reported is lower than expected: " + parser.getLastCycle());
						Thread.sleep(1000);
					}

					MetricsReport metrics = new MetricsReport(parser.getNumNodes(), 
							START_CYCLE, FINAL_CYCLE, parser.getVisitsList());

					tableINTqmean.set(algorithm, numAgents, df.format(metrics.getQuadraticMeanOfIntervals()));
					tableINTavg.set(algorithm, numAgents, df.format(metrics.getAverageInterval()));
					tableINTstdev.set(algorithm, numAgents, df.format(metrics.getStdDevOfIntervals()));
					tableINTmax.set(algorithm, numAgents, df.format(metrics.getMaxInterval()));
					tableNvisits.set(algorithm, numAgents, df.format(metrics.getTotalVisits()));
					tableOavg.set(algorithm, numAgents, df.format(metrics.getAverageIdleness2()));
					tableINTspecial.set(algorithm, numAgents, df.format(metrics.getSpecialMeanOfIntervals()));
					
					System.out.println("INTqmean: " + df.format(metrics.getQuadraticMeanOfIntervals()));
				
				} catch (Exception e) {
					e.printStackTrace();
					Thread.sleep(100);
				}
			}
			
		}
		
		String resultsFileName = "results-" + mapName +".csv";
		
		results.exportToCsv(resultsFileName);
		System.out.println("Exported to " + resultsFileName + ".");
	}
	
	public static void generateHistogramReport(String mapName, String algorithmName) throws Exception {
		File logDir = new File(LOG_DIR);
		
		CoolTableList results = new CoolTableList("Results for map " + mapName);
		
		File[] logFiles = logDir.listFiles(LOG_FILE_FILTER);
		DecimalFormat df= new DecimalFormat("#0.0000");
		
		LogFileParser parser = new LogFileParser(false);

		for (File file : logFiles) {
			String[] parts = file.getName().split("-");
			String algorithm = parts[0];
			String map = parts[1];
			String numAgents = parts[2];
			
			System.out.println("File: " + file);			
			
			if (map.equals(mapName) && algorithm.equals(algorithmName)) {
				try {
					System.out.println("Parsing...");

					parser.parseFile(file.getAbsolutePath());

					if (parser.getLastCycle() < FINAL_CYCLE) {
						System.out.println("Warning: Last cycle reported is lower than expected: " + parser.getLastCycle());
						Thread.sleep(1000);
					}

					MetricsReport metrics = new MetricsReport(parser.getNumNodes(), 
							START_CYCLE, FINAL_CYCLE, parser.getVisitsList());

					String tableName = numAgents + " agents";
					if (!results.hasTable(tableName)) {
						results.createTable(tableName);
					}
					
					CoolTable table = results.getTable(tableName);
					int[] histogram = metrics.getIntervalsHistogram();
					
					for (int i = 0; i < histogram.length; i++) {
						table.set(algorithm, ""+i, ""+histogram[i]);
					}
				
				} catch (Exception e) {
					e.printStackTrace();
					Thread.sleep(100);
				}
			}
			
		}
		
		String resultsFileName = "results-" + mapName +".csv";
		
		results.exportToCsv(resultsFileName);
		System.out.println("Exported to " + resultsFileName + ".");
	}
	
	public static void main(String[] args) throws Exception {
		File logDir = new File(LOG_DIR);

		if (!logDir.isDirectory()) {
			System.out.println("The path given is not a directory!");
			return;
		}
		
		Set<String> maps = findMaps(); 
//				new TreeSet<>();
//				maps.add("city_traffic2");

		Thread.sleep(2000);
		
		for (String mapName : maps) {
			generateReport(mapName);
			//generateHistogramReport(mapName, "grav(Node,Ar,2.0,sum)");
		}
		
	}

}
