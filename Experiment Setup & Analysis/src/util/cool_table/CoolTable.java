package util.cool_table;

import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;


public class CoolTable {
	private String name;
	
	private List<String> rows;
	private List<String> columns;
	
	private List<List<String>> values;
	
	public CoolTable() {
		this("");
	}
	
	public CoolTable(String tableName) {
		this.name = tableName;
		this.rows = new LinkedList<>();
		this.columns = new LinkedList<>();
		this.values = new LinkedList<>();
	}
	
	public String getName() {
		return this.name;
	}

	public int addColumn(String name) {
		int index = this.columns.indexOf(name);
		if (index == -1) {
			index = this.columns.size();
			this.columns.add(name);
		}
		return index;
	}
	
	public int addRow(String name) {
		int index = this.rows.indexOf(name);
		if (index == -1) {
			index = this.rows.size();
			this.rows.add(name);
			this.values.add(new ArrayList<String>());
		}
		return index;
	}
	
	public void set(int row, int column, String value) {
		List<String> theRow = this.values.get(row);
		int theRowSize = theRow.size();
		
		if (column >= theRowSize) {
			for (int i = theRowSize; i <= column; i ++) {
				theRow.add("");
			}
		}
		
		theRow.set(column, value);
	}
	
	public void set(String rowName, String columnName, String value) {
		int row = this.addRow(rowName);
		int col = this.addColumn(columnName);
		
		this.set(row, col, value);
	}
	
	public void exportToCsv(String fileName) throws IOException {
		PrintStream out = new PrintStream(fileName);
		this.exportToCsv(out);
		out.close();
	}
	
	public void exportToCsv(PrintStream out) throws IOException {	
		out.println(this.name);		
		out.print(";");
		
		for (int i = 0; i < columns.size(); i++) {
			out.print(columns.get(i));
			out.print(";");
		}
		
		out.println();
		
		for (int r = 0; r < rows.size(); r++) {
			out.print(rows.get(r));
			out.print(";");
			
			for (String v : this.values.get(r)) {
				out.print(v);
				out.print(";");
			}
			
			out.println();
		}
	}


	public static void main(String[] args) throws IOException {
		CoolTable table = new CoolTable("Teste");
		
		table.addColumn("Coluna1");
		table.addRow("Linha 1");
		
		table.set(0, 0, "val1");
		table.set("Linha 2", "Coluna 2", "val2");
		table.set("Linha 3", "Coluna 2", "val3");
		
		table.exportToCsv("test.csv");
		
		System.out.println("Done!");
	}

}
