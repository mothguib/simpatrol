package strategies.grav.core.propagators;

import util.graph2.Graph;
import util.heap2.BinHeapPQueue;
import util.heap2.PQueue;
import util.heap2.PQueueElement;


/**
 * The "combined mass" holds only the maximum of the values.
 * 
 * @see GravityPropagatorNode  
 * @author Pablo A. Sampaio
 */
public class GravityPropagatorNodeMax extends GravityPropagatorNode {
	private PQueue<GravInfo>[] gravLists;
	
	
	@SuppressWarnings("unchecked")
	public GravityPropagatorNodeMax(Graph graph, double exponent, boolean useDistance) {
		super(graph, exponent, useDistance);
		
		gravLists = new PQueue[graph.getNumVertices()];
	}

	
	@Override
	public void applyGravities(int attractor, double attractorMass) {
		assert (attractorMass >= 0.0d);
		assert (masses[attractor] == -1.0d);
		
		int numVertices = combinedMasses.length;
		GravInfo gravInfo;

		for (int attracted = 0; attracted < numVertices; attracted++) {
			gravInfo = new GravInfo(attractor, attractorMass * propagationFactor[attracted][attractor]); 

			combinedMasses[attracted] = addToGravList(attracted, gravInfo); //obs: origin and destiny can be the same 
		}
		
		masses[attractor] = attractorMass;
	}
	
	// adiciona na lista/heap e retorna a gravidade máxima
	private double addToGravList(int destiny, GravInfo gravInfo) {
		if (gravLists[destiny] == null) {
			gravLists[destiny] = new BinHeapPQueue<GravInfo>(super.graph.getNumVertices());
		}
		
		gravLists[destiny].add(gravInfo);
		
		return gravLists[destiny].getMinimum().gravity; //the minimum key has maximum gravity
	}

	
	
	@Override
	public void undoGravities(int attractor) {
		assert (masses[attractor] >= 0.0d);
		
		int numVertices = combinedMasses.length;

		for (int attracted = 0; attracted < numVertices; attracted++) {
			combinedMasses[attracted] = removeFromGravList(attracted, attractor); 
		}

		masses[attractor] = -1.0d;
	}
	
	private double removeFromGravList(int attracted, int attractor) {
		assert (gravLists[attracted] != null);
		
		PQueue<GravInfo> gravList = gravLists[attracted];
		
		gravList.remove(new GravInfo(attractor, -1.0)); //gravity value is not important here
		
		return gravList.isEmpty()? 0.0d : gravList.getMinimum().gravity;
	}

	
	@Override
	public void undoAllGravities() {
		for (int i = 0; i < gravLists.length; i++) {
			gravLists[i] = null;
			combinedMasses[i] = 0.0d;
			masses[i] = -1.0d;
		}
	}

	public String toString() {
		StringBuilder builder = new StringBuilder();

		builder.append("[ ");
		for (int i = 0; i < combinedMasses.length; i++) {
			builder.append(graph.getNode(i).getIdentifier().toUpperCase());
			builder.append(": ");
			builder.append(combinedMasses[i]);
			builder.append('(');
			builder.append( graph.getNode(gravLists[i].getMinimum().origin) );
			builder.append("), ");
		}
		builder.append("]\n");		
		
		builder.append(super.toString());
		
		return builder.toString();
	}
	
	// classe auxiliar
	private class GravInfo extends PQueueElement {
		int origin;
		double gravity;
		
		GravInfo(int or, double gr) {
			origin = or;
			gravity = gr;
		}
		
		@Override
		public int getKey() {
			return -(int)(1000000d * gravity);
		}
		
		public boolean equals(Object o) {
			return (o instanceof GravInfo) && ((GravInfo)o).origin == origin;
		}
	}
}
