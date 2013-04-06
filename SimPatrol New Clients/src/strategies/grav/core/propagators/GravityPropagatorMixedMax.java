package strategies.grav.core.propagators;

import util.graph2.Edge;
import util.graph2.Graph;
import util.heap2.BinHeapPQueue;
import util.heap2.PQueue;
import util.heap2.PQueueElement;


/**
 * In this implementation, the resulting force in a specific edge x->y (in this specific
 * direction) is calculated as the MAXIMUM of all forces produced in x->y.
 * 
 * @see GravityPropagatorMixed
 * @author Pablo A. Sampaio
 */
public class GravityPropagatorMixedMax extends GravityPropagatorMixed {
	private PQueue<GravInfo>[][] gravLists;
	

	@SuppressWarnings("unchecked")
	public GravityPropagatorMixedMax(Graph graph, double exponent) {
		super(graph, exponent);		
		gravLists = new PQueue[graph.getNumVertices()][graph.getNumVertices()];
	}

	
	@Override
	public void applyGravities(int attractor, double attractorMass) {
		assert (attractorMass >= 0.0d);
		assert (masses[attractor] == -1.0d);
		
		int numVertices = gravities.length;
		GravInfo gravInfo;

		for (int node = 0; node < numVertices; node++) {
			if (node == attractor) {
				continue;
			}
			for (Edge outEdge : super.graph.getOutEdges(node)) {
				int intermedNode = outEdge.getTargetIndex(); 
				double distIntermedAttractor = super.shortestPaths.getDistance(intermedNode, attractor);
				double force = attractorMass / Math.pow(outEdge.getLength()+distIntermedAttractor, distanceExponent);
				
				gravInfo = new GravInfo(attractor, force); 

				gravities[node][intermedNode] = addToGravList(node, intermedNode, gravInfo); 
			}
		}
		
		masses[attractor] = attractorMass;
	}
	
	
	// adiciona na lista/heap e retorna a gravidade máxima
	private double addToGravList(int attractedNode, int neighbor, GravInfo gravInfo) {
		if (gravLists[attractedNode][neighbor] == null) {
			gravLists[attractedNode][neighbor] = new BinHeapPQueue<GravInfo>(super.graph.getNumVertices());
		}
		
		gravLists[attractedNode][neighbor].add(gravInfo);
		
		return gravLists[attractedNode][neighbor].getMinimum().gravity; //obs: minimum key has maximum gravity
	}

	
	@Override
	public void undoGravities(int attractorNode) {
		assert (masses[attractorNode] >= 0.0d);

		int numVertices = gravities.length;

		for (int node = 0; node < numVertices; node++) {
			if (node == attractorNode) {
				continue;
			}
			for (Edge outEdge : super.graph.getOutEdges(node)) {
				int intermedNode = outEdge.getTargetIndex(); 
				gravities[node][intermedNode] = removeFromGravList(node, intermedNode, attractorNode);
			}
		}

		masses[attractorNode] = -1.0d;
	}
		
	private double removeFromGravList(int attracted, int neighbor, int attractor) {
		assert (gravLists[attracted][neighbor] != null);
		
		PQueue<GravInfo> gravList = gravLists[attracted][neighbor];
		
		//System.out.print("..... gravidade de " + attracted + " para " + neighbor + " mudou de " + gravList.getMinimum().gravity); 
		gravList.remove(new GravInfo(attractor, -1.0));
		//System.out.println(" para " + (gravList.isEmpty() ? 0.0d : gravList.getMinimum().gravity));
		
		return gravList.isEmpty()? 0.0d : gravList.getMinimum().gravity;
	}
	
	
	@Override
	public void undoAllGravities() {
		// specialized implementation
		for (int i = 0; i < gravLists.length; i++) {
			for (int j = 0; j < gravLists.length; j++) {
				gravLists[i][j] = null;
				gravities[i][j] = 0.0d;
			}
			masses[i] = -1.0d;
		}
	}

	public String toString() {
		StringBuilder builder = new StringBuilder();
		long FACTOR = 10000L;
		long aprox;
		
		builder.append("[ ");
		for (int i = 0; i < masses.length; i++) {
			builder.append(graph.getNode(i).getIdentifier().toUpperCase());
			builder.append(": ");
			builder.append(masses[i]);
			builder.append(", ");
		}
		builder.append("]\n");
		
		int attractor;
		
		for (int i = 0; i < masses.length; i++) {
			for (int j = 0; j < masses.length; j++) {
				aprox = (long)(FACTOR * getGravity(i,j));
				if (aprox != 0) {
					builder.append("\t| from " + graph.getNode(i) + " to " + graph.getNode(j) + ": ");
					builder.append(aprox);
					
					attractor = gravLists[i][j].getMinimum().attractorNode;
					
					builder.append(" (");
					builder.append(graph.getNode(attractor));
					builder.append(")");
					builder.append('\n');
				}
			}
		}
		
		return builder.toString();
	}

	// classe auxiliar
	private class GravInfo extends PQueueElement {
		int attractorNode;
		double gravity;
		
		GravInfo(int or, double gr) {
			attractorNode = or;
			gravity = gr;
		}
		
		@Override
		public int getKey() {
			return -(int)(1.0e5 * gravity);
		}
		
		public boolean equals(Object o) {
			return o instanceof GravInfo && ((GravInfo)o).attractorNode == attractorNode;
		}
	}

}
