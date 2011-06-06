package gravitational.gravity_manager;

import util.graph2.Graph;
import util.heap2.BinHeapPQueue;
import util.heap2.PQueue;
import util.heap2.PQueueElement;


public class VGravityManagerMax extends VGravityManager {
	private PQueue<GravInfo>[] gravLists;
	
	
	@SuppressWarnings("unchecked")
	public VGravityManagerMax(Graph graph, double exponent) {
		super(graph, exponent);
		
		gravLists = new PQueue[graph.getNumVertices()];
	}

	
	@Override
	public void applyGravity(int origin, double originMass) {
		assert (originMass >= 0.0d);
		assert (masses[origin] == -1.0d);
		
		int numVertices = vertexGravities.length;
		GravInfo gravInfo;

		for (int destiny = 0; destiny < numVertices; destiny++) {
			gravInfo = new GravInfo(origin, originMass * propagationFactor[destiny][origin]); 

			vertexGravities[destiny] = addToGravList(destiny, gravInfo); //obs: origin and destiny can be the same 
		}
		
		masses[origin] = originMass;
	}
	
	// adiciona na lista/heap e retorna a gravidade máxima
	private double addToGravList(int destiny, GravInfo gravInfo) {
		if (gravLists[destiny] == null) {
			gravLists[destiny] = new BinHeapPQueue<GravInfo>(getNumVertices());
		}
		
		gravLists[destiny].add(gravInfo);
		
		return gravLists[destiny].getMinimum().gravity; //obs: the minimum key has maximum gravity
	}

	
	
	@Override
	public void undoGravity(int origin) {
		assert (masses[origin] >= 0.0d);
		
		int numVertices = vertexGravities.length;

		for (int destiny = 0; destiny < numVertices; destiny++) {
			vertexGravities[destiny] = removeFromGravList(destiny, origin); 
		}

		masses[origin] = -1.0d;
	}
	
	private double removeFromGravList(int destiny, int origin) {
		assert (gravLists[destiny] != null);
		
		PQueue<GravInfo> gravList = gravLists[destiny];
		
		gravList.remove(new GravInfo(origin, -1.0)); //gravity value is not important here
		
		return gravList.isEmpty()? 0.0d : gravList.getMinimum().gravity;
	}

	
	@Override
	public void undoAllGravities() {
		for (int i = 0; i < gravLists.length; i++) {
			gravLists[i] = null;
			vertexGravities[i] = 0.0d;
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
			return (o instanceof GravInfo) && ((GravInfo)o).origin == origin;
		}
	}
}
