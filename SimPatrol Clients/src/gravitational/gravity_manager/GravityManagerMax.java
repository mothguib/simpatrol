package gravitational.gravity_manager;

import util.graph2.Graph;
import util.heap2.BinHeapPQueue;
import util.heap2.PQueue;
import util.heap2.PQueueElement;


/**
 * Com esta classe, a gravidade atuando sobre uma direção específica
 * de uma aresta é dada pelo MÁXIMO das gravidades presentes naquela 
 * direção da aresta.
 * 
 * ATENÇÃO: Esta implementação assume que o grafo é simétrico.??
 * 
 * @author Pablo
 */
public class GravityManagerMax extends GravityManager {
	private PQueue<GravInfo>[][] gravLists;
	

	@SuppressWarnings("unchecked")
	public GravityManagerMax(Graph graph, double exponent) {
		super(graph, exponent);
		
		gravLists = new PQueue[graph.getNumVertices()][graph.getNumVertices()];
	}


	@Override
	public void applyGravity(int origin, double originMass) {
		assert (originMass >= 0.0d);
		assert (masses[origin] == -1.0d);
		
		int numVertices = gravities.length;

		int destinyPredecessor;
		GravInfo gravInfo;

		for (int destiny = 0; destiny < numVertices; destiny++) {
			if (destiny != origin) {
				destinyPredecessor = shortestPaths.getDestinyPredecessor(origin, destiny);
				
				gravInfo = new GravInfo(origin, originMass * propagationFactor[origin][destiny]); 

				gravities[destiny][destinyPredecessor] = addToGravList(destiny, destinyPredecessor, gravInfo); 
			}
		}
		
		masses[origin] = originMass;
	}
	
	
	// adiciona na lista/heap e retorna a gravidade máxima
	private double addToGravList(int destiny, int destinyPred, GravInfo gravInfo) {
		if (gravLists[destiny][destinyPred] == null) {
			gravLists[destiny][destinyPred] = new BinHeapPQueue<GravInfo>(getNumVertices());
		}
		
		gravLists[destiny][destinyPred].add(gravInfo);
		
		return gravLists[destiny][destinyPred].getMinimum().gravity; //obs: a chave mínima tem gravidade máxima
	}

	
	@Override
	public void undoGravity(int origin) {
		assert (masses[origin] >= 0.0d);
		
		int numVertices = gravities.length;
		int destinyPred;

		//System.out.println("... desfazendo gravidades partindo de " + origin);
		for (int destiny = 0; destiny < numVertices; destiny++) {
			if (destiny != origin) {
				destinyPred = shortestPaths.getDestinyPredecessor(origin, destiny);
				
				gravities[destiny][destinyPred] = removeFromGravList(destiny, destinyPred, origin); 
			}
		}

		masses[origin] = -1.0d;
	}
	
	private double removeFromGravList(int destiny, int destinyPred, int origin) {
		assert (gravLists[destiny][destinyPred] != null);
		
		PQueue<GravInfo> gravList = gravLists[destiny][destinyPred];
		
		//System.out.print("..... gravidade de " + destiny + " para " + destinyPred + " mudou de " + gravList.getMinimum().gravity); 
		gravList.remove(new GravInfo(origin, -1.0));
		//System.out.println(" para " + (gravList.isEmpty() ? -1.0d : gravList.getMinimum().gravity));
		
		return gravList.isEmpty()? 0.0d : gravList.getMinimum().gravity;
	}
	
	
	@Override
	public void undoAllGravities() {
		for (int i = 0; i < gravLists.length; i++) {
			for (int j = 0; j < gravLists.length; j++) {
				gravLists[i][j] = null;
				gravities[i][j] = 0.0d;
			}
			masses[i] = -1.0d;
		}
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
			return -(int)(1.0e5 * gravity);
		}
		
		public boolean equals(Object o) {
			return o instanceof GravInfo && ((GravInfo)o).origin == origin;
		}
	}

}
