package FBA;

import java.io.IOException;
import java.util.LinkedList;

import util.file.FileReader;

public class TransactionAnalyzer {
	
	public LinkedList<CompleteTransaction> transactions;

	
	public TransactionAnalyzer(){
		transactions = new LinkedList<CompleteTransaction>();
	}
	
	/**
	 * @param args
	 * @throws IOException 
	 */
	
	public void ReadDoc(String path, int nb_agents) throws IOException{
		
		transactions.clear();
		
		FileReader myFile = new FileReader(path);
		
		while(!myFile.isEndOfFile()){
			
			String line = myFile.readLine();
			
			if (line.indexOf("<event type=\"8\"") > -1) {
				String message = line.substring(line.indexOf("message=\"")+ 9, line.lastIndexOf("\"/>"));
				SpeechAct received_act = SpeechAct.fromString(message);		
				
				boolean added = false;
				for(CompleteTransaction myTrans: transactions)
					if(myTrans.trans_id == received_act.getTransactionId()){
						myTrans.addAct(received_act);
						added = true;
					}
				
				if(!added){
					String time_str = line.substring(line.indexOf("time=\"") + 6);
					time_str = time_str.substring(0, time_str.indexOf("."));
					int time = Integer.valueOf(time_str);
					CompleteTransaction newTrans = new CompleteTransaction(received_act.getTransactionId(), time, nb_agents);
					newTrans.addAct(received_act);
					transactions.add(newTrans);
				}
			}
		}
		
		myFile.close();
	}
	
	public void Analyze(){
		for(int i = 0; i < transactions.size(); i++)
			if(!transactions.get(i).isNormal()){
				System.out.println(transactions.get(i).whatIsWrong());
				System.out.println(transactions.get(i).toString());
			}
	}
	
	public void FullAnalysis(){
		System.out.println("Nb de transactions : " + transactions.size());
		for(int i = 0; i < transactions.size(); i++){
			CompleteTransaction trans = transactions.get(i);
			System.out.println(trans.toString());
		}
	}
	
	public void FullAnalysis(String s){
		int nbtrans = 0;
		for(int i = 0; i < transactions.size(); i++){
			CompleteTransaction trans = transactions.get(i);
			if(trans.toString().contains(s)){
				nbtrans++;
				System.out.println(trans.toString());
			}
		}
		System.out.println("Nb de transactions : " + nbtrans + "/" + transactions.size());
	}
	
	public void FullAnalysisByNode(String s){
		for(int i = 0; i < transactions.size(); i++){
			CompleteTransaction trans = transactions.get(i);
			if(trans.toString().contains(s + ';') || trans.toString().contains(s + '('))
					System.out.println(trans.toString());
		}
	}
	
	
	public void AnalyseOwnership(String nodeName){
		LinkedList<String> owners = new LinkedList<String>();
		
		for(int i = 0; i < transactions.size(); i++){
			CompleteTransaction trans = transactions.get(i);
			for(SpeechAct act : trans.acts)
				if((act.getPerformative() == SpeechActPerformative.INFORM || act.getPerformative() == SpeechActPerformative.PROPOSE ) && 
						(act.toString().contains(nodeName + ';') || act.toString().contains(nodeName + '('))){
					owners.add(act.getSender());
					break;
				}
			if(owners.size() > 0)
				break;
		}
		
		for(int i = 0; i < transactions.size(); i++){
			CompleteTransaction trans = transactions.get(i);
			for(SpeechAct act : trans.acts)
				if(act.getPerformative() == SpeechActPerformative.ACCEPT && 
						(act.toString().contains(nodeName + ";") || act.toString().contains(nodeName + "("))){
					String send = act.getSender();
					if(!owners.getLast().equals(send))
						owners.add(send);
					else
						owners.add(act.getReceiver());
					
					
				}
		}
		
		System.out.println("Ownership for node " + nodeName + " : ");
		System.out.print(owners.get(0));
		for(int i = 1; i < owners.size(); i++)
			System.out.print(" -> " + owners.get(i));
		System.out.print("\n");
				
	}
	
	
	
	public static void main(String[] args) {
		TransactionAnalyzer analyzer = new TransactionAnalyzer();
		
		try {
			analyzer.ReadDoc("/home/pouletc/experimentation/Simulations/corridor/0_25/logs_FBA2/log_0.txt", 25);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		//analyzer.Analyze();
		//analyzer.AnalyseOwnership("v25");
		//analyzer.FullAnalysisByNode("v25");
		analyzer.FullAnalysis("a25@");
	}

}

final class CompleteTransaction {
	
	public LinkedList<SpeechAct> acts;
	
	public int trans_id;
	public int nb_agents;
	public int creation_time;
	
	public int nb_inform = 0;
	public int nb_reject = 0;
	public int nb_propose = 0;
	public int nb_refuse = 0;
	public int nb_accept = 0;
	public int nb_not_understood = 0;
	
	public CompleteTransaction(int trans_id, int creation_time, int nb_agents){
		this.trans_id = trans_id;
		this.nb_agents = nb_agents;
		this.creation_time = creation_time;
		acts = new LinkedList<SpeechAct>();
	}
	
	public void addAct(SpeechAct act){
		acts.add(act);
		switch(act.getPerformative()){
			case INFORM :
				nb_inform++;
				break;
			case REJECT :
				nb_reject++;
				break;
			case PROPOSE :
				nb_propose++;
				break;
			case REFUSE :
				nb_refuse++;
				break;
			case ACCEPT :
				nb_accept++;
				break;
			case NOT_UNDERSTOOD :
				nb_not_understood++;
				break;		
		}
	}
	
	public boolean isNormal(){
		boolean normal = true;
		
		normal = (nb_inform == 1) && 
					(nb_refuse + nb_propose == nb_agents - 1) && 
					((nb_refuse == nb_agents - 1) || ((nb_reject + nb_accept == nb_propose) && (nb_accept <= 1))) && 
					(nb_not_understood == 0);
		
		return normal;
	}
	
	public String whatIsWrong(){
		String wrong = "Transaction #" + this.trans_id + " (time " + this.creation_time + "): ";
		if(nb_inform != 1)
			if(nb_inform ==0)
				wrong += "No INFORM message; ";
			else
				wrong += "too much INFORM messages (" + this.nb_inform + "); ";
		
		if(nb_refuse + nb_propose != nb_agents - 1)
			if(nb_refuse + nb_propose < nb_agents - 1)
				wrong += "Too few answers (" + this.nb_propose + " propose, " + this.nb_refuse + " refuse); ";
			else
				wrong += "Too many answers (" + this.nb_propose + " propose, " + this.nb_refuse + " refuse); ";
		
		if((nb_refuse != nb_agents - 1) && ((nb_reject + nb_accept != nb_propose) || (nb_accept > 1))){
			wrong += "Closing problem : ";
			if((nb_refuse != nb_agents - 1)){
				if(nb_reject + nb_accept != nb_propose)
					wrong += "Too few decisions (" + this.nb_reject + " reject, " + this.nb_accept + " accept for " + this.nb_propose + " propose); ";
				else
					wrong += "Too much accept ( " + this.nb_accept + "); ";
			}
			else
				wrong += "No accept and too few refuse (" + this.nb_refuse + "); ";
		}
		
		return wrong;
		
	}
	
	public String toString(){
		String str = "Transaction #" + this.trans_id + " (time " + this.creation_time + "): \n";
		for(int i = 0; i < acts.size(); i++)
			str += "    " + acts.get(i).toString() + "\n";
		
		return str;
	}
	
	
	
}
