package closed.FBA2;

import java.util.LinkedList;

import closed.FBA.ComplexBid;
import closed.FBA.TransactionNodes;



/**
 * TransactionNodes2
 * 
 * This class extends the FBA.TransactionNodes class by keeping track of the time at which the instance was created
 * 
 * @author pouletc
 *
 */
public final class TransactionNodes2 extends TransactionNodes{
	
	public int transaction_time;
	
	public TransactionNodes2(TransactionTypes type, int trans_id, int time, String[] in, String[] out){
		super(type, trans_id, in, out);
		transaction_time = time;
	}
	
	public TransactionNodes2(TransactionTypes type, int trans_id, int time, LinkedList<String> in, LinkedList<String> out){
		super(type, trans_id, in, out);
		transaction_time = time;
	}
	
	public TransactionNodes2(TransactionTypes type, int trans_id, int time, LinkedList<String> in, ComplexBid out){
		super(type, trans_id, in, out);
		transaction_time = time;
	}

}

