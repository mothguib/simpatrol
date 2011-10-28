package closed.FBA;

import java.util.Arrays;
import java.util.LinkedList;

/**
 * This class represent an entire message from an agent to another
 * 
 * @author pouletc
 *
 */
public class SpeechAct { 
	
	private SpeechActPerformative performative;
	private int transaction_id;
	private String sender_id;
	private String receiver_id;
	
	
	private LinkedList<String> contained_nodes = null;
	private LinkedList<String> propositions = null;
	private ComplexBid complexBid = null;
	
	// other variables not used here
	private double happiness = 0;
	private double happiness_before = 0;
	private int[] idleness = null;
	
	// this allow the parser to write in xml if false, or transforms the <> in () if true (otherwise it is misinterpreted by
	// Simpatrol
	public static boolean adapt_to_simpatrol = true;
	
	public SpeechAct(int id, SpeechActPerformative performative, String sender,
			String receiver, LinkedList<String> contained_nodes, int option){
		this.transaction_id = id;
		this.setPerformative(performative);
		this.setSender(sender);
		this.setReceiver(receiver);
		if(option == 0)
			this.setContainedNodes(contained_nodes);
		if(option == 1)
			this.setPropositions(contained_nodes);
	}

	public SpeechAct(int id, SpeechActPerformative performative, String sender,
			String receiver, LinkedList<String> contained_nodes, 
			double happiness){
		this.transaction_id = id;
		this.setPerformative(performative);
		this.setSender(sender);
		this.setReceiver(receiver);
		this.setContainedNodes(contained_nodes);
		this.setHappiness(happiness);
	}

	public SpeechAct(int id, SpeechActPerformative performative, String sender,
			String receiver, LinkedList<String> contained_nodes, 
			double happiness_a, double happiness_b){
		this.transaction_id = id;
		this.setPerformative(performative);
		this.setSender(sender);
		this.setReceiver(receiver);
		this.setContainedNodes(contained_nodes);
		this.setHappiness(happiness_a);
		this.setHappinessBefore(happiness_b);
	};

	public SpeechAct(int id, SpeechActPerformative performative, String sender,
			String receiver, int[] idleness){
		this.transaction_id = id;
		this.setPerformative(performative);
		this.setSender(sender);
		this.setReceiver(receiver);
		this.setIdleness(idleness);
	}

	public SpeechAct(int id, SpeechActPerformative performative, String sender,
			String receiver, ComplexBid bid){
		this.transaction_id = id;
		this.setPerformative(performative);
		this.setSender(sender);
		this.setReceiver(receiver);
		this.setComplexBid(bid);
	}
	
	public SpeechAct(int id, SpeechActPerformative performative, String sender,
			String receiver){
		this.transaction_id = id;
		this.setPerformative(performative);
		this.setSender(sender);
		this.setReceiver(receiver);
	}


	public void setPerformative(SpeechActPerformative performative){
		this.performative = performative;
	}
	
	public void setSender(String sender){
		this.sender_id = sender;
	}
	
	public void setReceiver(String receiver){
		this.receiver_id = receiver;
	}
	
	public void setContainedNodes(LinkedList<String> containedNodes){
		this.contained_nodes = containedNodes;
	}
	
	public void setHappiness(double happiness){
		this.happiness = happiness;
	}
	
	public void setHappinessBefore(double happiness){
		this.happiness_before = happiness;
	}
	
	public void setPropositions(LinkedList<String> propositions){
		this.propositions = propositions;
	}
	
	public void setIdleness(int[] idleness){
		this.idleness = idleness;
	}
	
	public void setComplexBid(ComplexBid complexBid){
		this.complexBid = complexBid;
	}

	public int getTransactionId(){
		return transaction_id;
	}
	
	public SpeechActPerformative getPerformative(){
		return this.performative;
	}
	
	public String getSender(){
		return this.sender_id;
	}
	
	public String getReceiver(){
		return this.receiver_id;
	}
	
	public LinkedList<String>  getContainedNodes(){
		return this.contained_nodes;
	}
	
	public double getHappiness(){
		return this.happiness;
	}
	
	public double getHappinessBefore(){
		return this.happiness_before;
	}
	
	
	public LinkedList<String> getPropositions(){
		return this.propositions;
	}
	
	public int[] getIdleness(){
		return this.idleness;
	}
	
	public ComplexBid getComplexBid(){
		return this.complexBid;
	}
	
	public String toString(){
		String str = "<ACT trans_id=\"" + this.transaction_id + "\" perf=\"" + performative + "\">";
		str += "<sender>" + sender_id + "</sender>";
		str += "<receiver>" + receiver_id + "</receiver>";
		
		if(contained_nodes != null){
			str += "<nodes>";
			for(int i = 0; i < this.contained_nodes.size(); i++)
				str += this.contained_nodes.get(i) + ";";
			if(contained_nodes.size() > 0)
				str = str.substring(0, str.length() - 1);
			str += "</nodes>";
		}

		str += "<happiness current=\"" + happiness + 
					"\" before=\"" + happiness_before + "\"/>";

		if(propositions != null){
			str += "<prop>";
			for(int i = 0; i < this.propositions.size(); i++)
				str += this.propositions.get(i) + ";";
			if(propositions.size() > 0)
				str = str.substring(0, str.length() - 1);
			str += "</prop>";
		}
		
		if(idleness != null){
			str += "<idleness>";
			for(int i = 0; i < this.idleness.length; i++)
				str += this.idleness[i] + ";";
			if(idleness.length > 0)
				str = str.substring(0, str.length() - 1);
			str += "</idleness>";
		}
		
		if(complexBid != null)
			str += complexBid.toString();
		
		str += "</ACT>";
		
		if(adapt_to_simpatrol){
			str = str.replace('<', '(');
			str = str.replace('>', ')');
	    	str = str.replace('"', '\'');
		}
		
		
		return str;
	}
	
	public static SpeechAct fromString(String str){	
		if(adapt_to_simpatrol){
			str = str.replace('(','<');
			str = str.replace(')','>');
	    	str = str.replace('\'', '"');
		}
		
		if((str.indexOf("<ACT") == -1)||(str.indexOf("</ACT>") == -1)){
			System.err.println("String does not contain a SpeechAct");
			return null;
		}
		
		str = str.substring(str.indexOf("<ACT"), str.indexOf("</ACT>") + 6);
		
		SpeechAct act;
		
		int trans_id;
		SpeechActPerformative perf = null;
		String sender, receiver;
		LinkedList<String> contained_nodes = null;
		double happiness, happiness_before;
		LinkedList<String> propositions = null;
		int[] idle = null;
		ComplexBid complexBid = null;
		
		String id_str = str.substring(str.indexOf("<ACT"), str.indexOf(">"));
		id_str = id_str.substring(id_str.indexOf("trans_id=\"") + 10);
		id_str = id_str.substring(0, id_str.indexOf("\""));
		trans_id = Integer.valueOf(id_str);
		
		String perf_str = str.substring(str.indexOf("<ACT"), str.indexOf(">"));
		perf_str = perf_str.substring(perf_str.indexOf("perf=\"") + 6);
		perf_str = perf_str.substring(0, perf_str.indexOf("\""));
		if(perf_str.equals("INFORM"))
			perf = SpeechActPerformative.INFORM;
		else if(perf_str.equals("REJECT"))
			perf = SpeechActPerformative.REJECT;
		else if(perf_str.equals("PROPOSE"))
			perf = SpeechActPerformative.PROPOSE;
		else if(perf_str.equals("ACCEPT"))
			perf = SpeechActPerformative.ACCEPT;
		else if(perf_str.equals("REFUSE"))
			perf = SpeechActPerformative.REFUSE;
		else if(perf_str.equals("NOT_UNDERSTOOD"))
			perf = SpeechActPerformative.NOT_UNDERSTOOD;
		else if(perf_str.equals("ENTER"))
			perf = SpeechActPerformative.ENTER;
		else if(perf_str.equals("QUIT"))
			perf = SpeechActPerformative.QUIT;
		else 
			return null;
		
		sender = str.substring(str.indexOf("<sender>") + 8, str.indexOf("</sender>"));
		receiver = str.substring(str.indexOf("<receiver>") + 10, str.indexOf("</receiver>"));
		
		if(str.indexOf("<nodes>") > -1){
			String[] node_list = str.substring(str.indexOf("<nodes>") + 7, str.indexOf("</nodes>")).split(";");
			contained_nodes = new LinkedList<String>();
			for(int i = 0; i < node_list.length; i++)
				contained_nodes.add(node_list[i]);
		}
		
		String happy = str.substring(str.indexOf("<happiness") + 10);
		happy = happy.substring(0, happy.indexOf("/>"));
		happy = happy.substring(happy.indexOf("current=\"") + 9);
		happiness = Double.valueOf(happy.substring(0, happy.indexOf("\"")));
		happy = happy.substring(happy.indexOf("before=\"") + 8);
		happiness_before = Double.valueOf(happy.substring(0, happy.indexOf("\"")));
		
		if(str.indexOf("<prop>") > -1){
			String[] prop_list = str.substring(str.indexOf("<prop>") + 6, str.indexOf("</prop>")).split(";");
			propositions = new LinkedList<String>();
			for(int i = 0; i < prop_list.length; i++)
				propositions.add(prop_list[i]);
		}
		
		if(str.indexOf("<idleness>") > -1){
			String[] idle_list = str.substring(str.indexOf("<idleness>") + 10, str.indexOf("</idleness>")).split(";");
			if((idle_list.length == 1) && idle_list[0].equals(""))
				idle_list = new String[0];
			idle = new int[idle_list.length];
			for(int i = 0; i < idle_list.length; i++)
				idle[i] = Integer.valueOf(idle_list[i]);
		}
		
		complexBid = ComplexBid.fromString(str);
		
		act = new SpeechAct(trans_id, perf, sender, receiver, complexBid);
		act.contained_nodes = contained_nodes;
		act.happiness = happiness;
		act.happiness_before = happiness_before;
		act.propositions = propositions;
		act.idleness = idle;
		
		return act;
		
	}


	public boolean equals(SpeechAct act){
		if(!this.performative.equals(act.performative))
			return false;
		if(this.transaction_id != act.transaction_id)
			return false;
		if(!this.sender_id.equals(act.sender_id))
			return false;
		if(!this.receiver_id.equals(act.receiver_id))
			return false;
		
		if(this.contained_nodes != null){
			if(act.contained_nodes == null)
				return false;
			if(this.contained_nodes.size() != act.contained_nodes.size())
				return false;
			for(int i = 0; i < this.contained_nodes.size(); i++)
				if(!this.contained_nodes.get(i).equals(act.contained_nodes.get(i)))
					return false;
		}
		else if(act.contained_nodes != null)
			return false;
		
		if(this.happiness != act.happiness)
			return false;
		if(this.happiness_before != act.happiness_before)
			return false;
		
		
		if(this.propositions != null){
			if(act.propositions == null)
				return false;
			if(this.propositions.size() != act.propositions.size())
				return false;
			for(int i = 0; i < this.propositions.size(); i++)
				if(!this.propositions.get(i).equals(act.propositions.get(i)))
					return false;
		}
		else if(act.propositions != null)
			return false;
		
		
		if(!Arrays.equals(this.idleness, act.idleness))
			return false;
		
		if(this.complexBid != null){
			if(act.complexBid == null)
				return false;
			if(!this.complexBid.equals(act.complexBid))
				return false;
		} 
		else if(act.complexBid != null)
			return false;
		
		return true;
	}
}
