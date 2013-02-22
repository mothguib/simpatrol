package util.cool_table;

import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;


public class CoolTableList {
	private String name;
	private List<CoolTable> list;

	public CoolTableList(String name) {
		this.name = name;
		this.list = new ArrayList<>();
	}
	
	public CoolTableList(int numTables) {
		this.list = new ArrayList<>(numTables);
	}
	
	public void add(CoolTable table) {
		if (! this.hasTable(table.getName())) {
			this.list.add(table);
		}
	}
	
	public CoolTable createTable(String tableName) {
		CoolTable t = null;
		if (! this.hasTable(tableName)) {
			t = new CoolTable(tableName);
			this.list.add(t);
		}
		return t;
	}

	public CoolTable getTable(String tableName) {
		for (CoolTable table : this.list) {
			if (table.getName().equals(tableName)) {
				return table;
			}
		}
		return null;
	}

	private boolean hasTable(String tableName) {
		for (CoolTable table : this.list) {
			if (table.getName().equals(tableName)) {
				return true;
			}
		}
		return false;
	}
	
	public void exportToCsv(String fileName) throws IOException {
		PrintStream out = new PrintStream(fileName);
		
		out.println(";" + this.name.toUpperCase() + ";");
		out.println();

		for (CoolTable table : this.list) {
			table.exportToCsv(out);
			out.println();
			out.println();
		}
		
		out.close();
	}

	public static void main(String[] args) throws IOException {
		CoolTableList tableList = new CoolTableList("TABLE LIST");
		
		tableList.add(new CoolTable("Table 1"));  //equivalent forms
		tableList.createTable("Table 2");
		
		tableList.getTable("Table 1").set("Row 1", "Col 1", "val11");
		tableList.getTable("Table 1").set("Row 2", "Col 2", "val22");
		tableList.getTable("Table 1").set("Row 3", "Col 2", "val32");
		
		tableList.getTable("Table 2").set("Linha 1", "Coluna 1", "x11");
		tableList.getTable("Table 2").set("Linha 2", "Coluna 1", "x21");
		tableList.getTable("Table 2").set("Linha 3", "Coluna 2", "x32");
		
		tableList.exportToCsv("test-list.csv");
		
		System.out.println("Done!");

	}

}
