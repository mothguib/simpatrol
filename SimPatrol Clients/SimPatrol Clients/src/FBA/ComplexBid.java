package FBA;

import java.util.Vector;

public class ComplexBid {
	
	private String[]  myBidsForFirst;
	private String[]  myBidsForSecond;
	private String[] myBidsForBoth;
	private Vector<String[]> myDoubleBidsForBoth;
	private double actual_idleness;
	
	public static final int UTILITY_SIZE = 10;
	private double[] utilities_myBidsForFirst ;
	private double[] utilities_myBidsForSecond ;
	private double[] utilities_myBidsForBoth;
	private double[] utilities_myDoubleBidsForBoth;
	
		
	public ComplexBid(String[] BidsForFirst,
				String[] BidsForSecond,
				String[]	BidsForBoth,
				Vector<String[]> DoubleBidsForBoth){
		this.myBidsForFirst = BidsForFirst;
		this.myBidsForSecond = BidsForSecond;
		this.myBidsForBoth = BidsForBoth;
		this.myDoubleBidsForBoth = DoubleBidsForBoth;
		
		utilities_myBidsForFirst = new double[UTILITY_SIZE];
		utilities_myBidsForSecond = new double[UTILITY_SIZE];
		utilities_myBidsForBoth = new double[UTILITY_SIZE];
		utilities_myDoubleBidsForBoth = new double[UTILITY_SIZE];
	}
	
	
	public ComplexBid(double actual_idleness){
		this.actual_idleness = actual_idleness;
		utilities_myBidsForFirst = new double[UTILITY_SIZE];
		utilities_myBidsForSecond = new double[UTILITY_SIZE];
		utilities_myBidsForBoth = new double[UTILITY_SIZE];
		utilities_myDoubleBidsForBoth = new double[UTILITY_SIZE];
		this.myBidsForFirst = null;
		this.myBidsForSecond = null;
		this.myBidsForBoth = null;
		this.myDoubleBidsForBoth = null;
	}


	public String[] getBidsForFirst(){
		return myBidsForFirst;
	}
	
	public String[] getBidsForSecond(){
		return myBidsForSecond;
	}
	
	public String[] getBidsForBoth(){
		return myBidsForBoth;
	}
	
	public Vector<String[]> getDoubleBidsForBoth(){
		return myDoubleBidsForBoth;
	}
	
	public double getIdleness(){
		return actual_idleness;
	}

	public void setBidsForFirst(String[] bidsForFirst){
		this.myBidsForFirst = bidsForFirst;
	}
	
	public void setBidsForSecond(String[] bidsForSecond){
		this.myBidsForSecond = bidsForSecond;
	}
	
	public void setBidsForBoth(String[] bidsForBoth){
		this.myBidsForBoth = bidsForBoth;
	}
	
	public void setDoubleBidsForBoth(Vector<String[]> doubleBidsForBoth){
		this.myDoubleBidsForBoth = doubleBidsForBoth;
	}

	public void addDoubleBidsForBoth(String[] theObjectSet){
		myDoubleBidsForBoth.add(theObjectSet);
	}
	
	public int sizeDoubleBidsForBoth(){
		return myDoubleBidsForBoth.size();
	}
	
	public void clearDoubleBidsForBoth(){
		myDoubleBidsForBoth.clear();
	}
	
	
	
	
	public String toString(){
		String desc = "<CB idleness=\"" + this.actual_idleness + "\">";
		
		if(this.myBidsForFirst != null){
			desc += "<FB>";
			for(int i = 0; i < this.myBidsForFirst.length; i++)
				desc += this.myBidsForFirst[i] + ";";
			if(myBidsForFirst.length > 0)
				desc = desc.substring(0, desc.length() - 1);
			desc += "</FB>";
		}
		
		if(this.myBidsForSecond != null){
			desc += "<SB>";
			for(int i = 0; i < this.myBidsForSecond.length; i++)
				desc += this.myBidsForSecond[i] + ";";
			if(myBidsForSecond.length > 0)
				desc = desc.substring(0, desc.length() - 1);
			desc += "</SB>";
		}
		
		if(this.myBidsForBoth != null){
			desc += "<BB>";
			for(int i = 0; i < this.myBidsForBoth.length; i++)
				desc += this.myBidsForBoth[i] + ";";
			if(myBidsForBoth.length > 0)
				desc = desc.substring(0, desc.length() - 1);
			desc += "</BB>";
		}
		
		if(this.myDoubleBidsForBoth != null){
			desc += "<DBB>";
			for(int i = 0; i < this.myDoubleBidsForBoth.size(); i++){
				desc += "<sDBB>";
				for(int j = 0; j < this.myDoubleBidsForBoth.get(i).length; j++)
					desc += this.myDoubleBidsForBoth.get(i)[j] + ";";
				if(myDoubleBidsForBoth.get(i).length > 0)
					desc = desc.substring(0, desc.length() - 1);
				desc += "</sDBB>";
			}
			desc += "</DBB>";
		}
		
		desc += "</CB>";
		
		if(SpeechAct.adapt_to_simpatrol){
			desc = desc.replace('<', '(');
			desc = desc.replace('>', ')');
			desc = desc.replace('"', '\'');
		}
		
		return desc;
	}
	
	public static ComplexBid fromString(String str){
		
		if(SpeechAct.adapt_to_simpatrol){
			str = str.replace('(','<');
			str = str.replace(')','>');
	    	str = str.replace('\'', '"');
		}
		
		if((str.indexOf("<CB") == -1)||(str.indexOf("</CB>") == -1)){
			//System.err.println("String does not contain a ComplexBid");
			return null;
		}
		
		ComplexBid answer;
		
		double idleness;
		String[] bidsforfirst = null, bidsforsecond = null, bidsforboth = null;
		Vector<String[]> doublebids = null;

		String rep = str.substring(str.indexOf("<CB"), str.indexOf("</CB>"));
		
		String idle_str = rep.substring(rep.indexOf("<CB"), rep.indexOf(">"));
		idle_str = idle_str.substring(idle_str.indexOf("idleness=\"") + 10);
		idle_str = idle_str.substring(0, idle_str.indexOf("\""));
		idleness = Double.valueOf(idle_str);
		
		if(rep.indexOf("<FB>") > -1){
			String fb = rep.substring(rep.indexOf("<FB>") + 4, rep.indexOf("</FB>"));
			bidsforfirst = fb.split(";");
			if((bidsforfirst.length == 1) && bidsforfirst[0].equals(""))
				bidsforfirst = new String[0];
		}
		
		if(rep.indexOf("<SB>") > -1){
			String sb = rep.substring(rep.indexOf("<SB>") + 4, rep.indexOf("</SB>"));
			bidsforsecond = sb.split(";");
			if((bidsforsecond.length == 1) && bidsforsecond[0].equals(""))
				bidsforsecond = new String[0];
		}
		
		if(rep.indexOf("<BB>") > -1){
			String bb = rep.substring(rep.indexOf("<BB>") + 4, rep.indexOf("</BB>"));
			bidsforboth = bb.split(";");
			if((bidsforboth.length == 1) && bidsforboth[0].equals(""))
				bidsforboth = new String[0];
		}
		
		if(rep.indexOf("<DBB>") > -1){
			doublebids = new Vector<String[]>();
			String dbb = rep.substring(rep.indexOf("<DBB>") + 5, rep.indexOf("</DBB>"));
			String sdbb;
			while(dbb.indexOf("<sDBB>") != -1){
				sdbb = dbb.substring(dbb.indexOf("<sDBB>") + 6, dbb.indexOf("</sDBB>"));
				String[] sdbb_list = sdbb.split(";");
				if((sdbb_list.length != 1) || !sdbb_list[0].equals(""))
					doublebids.add(sdbb_list);
				else
					doublebids.add(new String[0]);
				dbb = dbb.substring(dbb.indexOf("</sDBB>") + 7);
			}
		}
		
		if((bidsforfirst != null)||(bidsforsecond != null)||(bidsforboth != null)
				||(doublebids != null)){
			answer = new ComplexBid(bidsforfirst, bidsforsecond, bidsforboth, doublebids);
			answer.actual_idleness = idleness;
		} else {
			answer = new ComplexBid(idleness);
		}
		
		return answer;
	}

}
