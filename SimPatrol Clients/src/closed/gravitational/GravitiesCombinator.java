package closed.gravitational;

import closed.gravitational.gravity_manager.GravityManager;
import closed.gravitational.gravity_manager.GravityManagerAdder;
import closed.gravitational.gravity_manager.GravityManagerMax;
import closed.gravitational.gravity_manager.VGravityManager;
import closed.gravitational.gravity_manager.VGravityManagerAdder;
import closed.gravitational.gravity_manager.VGravityManagerMax;
import util.graph2.Graph;


public enum GravitiesCombinator {
	
	SUM {
		@Override
		public GravityManager createGravityManager(Graph graph, double exp) {
			return new GravityManagerAdder(graph, exp);
		}
		@Override
		public VGravityManager createVGravityManager(Graph graph, double exp) {
			return new VGravityManagerAdder(graph, exp);
		}
	},	
	
	MAX {
		@Override
		public GravityManager createGravityManager(Graph graph, double exp) {
			return new GravityManagerMax(graph, exp);
		}
		@Override
		public VGravityManager createVGravityManager(Graph graph, double exp) {
			return new VGravityManagerMax(graph, exp);
		}
	}
	//, AVG
	;
	
	public abstract GravityManager createGravityManager(Graph graph, double exp);
	public abstract VGravityManager createVGravityManager(Graph graph, double exp);

}
