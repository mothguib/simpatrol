package FBA;

import java.util.LinkedList;

public final class TransactionNodes {
	
	public String[] in_nodes;
	public String[] out_nodes;
	
	public int transaction_id;
	
	
	public TransactionNodes(int trans_id, String[] in, String[] out){
		transaction_id = trans_id;
		in_nodes = in;
		out_nodes = out;
	}
	
	public TransactionNodes(int trans_id, LinkedList<String> in, LinkedList<String> out){
		transaction_id = trans_id;
		in_nodes = new String[in.size()];
		for (int i = 0; i < in_nodes.length; i++)
			in_nodes[i] = in.get(i);
		
		out_nodes = new String[out.size()];
		for (int i = 0; i < out_nodes.length; i++)
			out_nodes[i] = out.get(i);

	}
	
	public TransactionNodes(int trans_id, LinkedList<String> in, ComplexBid out){
		transaction_id = trans_id;
		
		if(in != null){
			in_nodes = new String[in.size()];
			for (int i = 0; i < in_nodes.length; i++)
				in_nodes[i] = in.get(i);
		} else {
			in_nodes = new String[0];
		}
		
		LinkedList<String> outNodes = new LinkedList<String>();
		
		String[] nodes;
		if(out != null){
			if(out.getBidsForFirst() != null){
				nodes =  out.getBidsForFirst();
				for(int i = 0; i < nodes.length; i+= 2)
					if(outNodes.indexOf(nodes[i]) == -1)
						outNodes.add(nodes[i]);
			}
			if(out.getBidsForSecond() != null){
				nodes =  out.getBidsForSecond();
				for(int i = 0; i < nodes.length; i+= 2)
				if(outNodes.indexOf(nodes[i]) == -1)
					outNodes.add(nodes[i]);
			}
			if(out.getBidsForBoth() != null){
				nodes =  out.getBidsForBoth();
				for(int i = 0; i < nodes.length; i+= 2)
				if(outNodes.indexOf(nodes[i]) == -1)
					outNodes.add(nodes[i]);
			}
			if(out.getDoubleBidsForBoth() != null)
				for(String[] nodes2 : out.getDoubleBidsForBoth())
					for(int i = 0; i < nodes2.length; i+= 2)
						if(outNodes.indexOf(nodes2[i]) == -1)
							outNodes.add(nodes2[i]);
			
			out_nodes = new String[outNodes.size()];
			for (int i = 0; i < out_nodes.length; i++)
				out_nodes[i] = outNodes.get(i);
	
		}
		else
			out_nodes = new String[0];
	}
	
	
	
}