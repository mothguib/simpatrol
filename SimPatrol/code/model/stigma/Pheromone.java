/* Pheromone.java (2.0) */
package br.org.simpatrol.server.model.stigma;

/* Imported classes and/or interfaces. */
import java.util.HashMap;
import java.util.Map;

import br.org.simpatrol.server.control.robot.Bot;
import br.org.simpatrol.server.model.etpd.EventTimeProbabilityDistribution;
import br.org.simpatrol.server.model.etpd.UniformEventTimeProbabilityDistribution;
import br.org.simpatrol.server.model.graph.Edge;
import br.org.simpatrol.server.model.graph.Vertex;
import br.org.simpatrol.server.model.interfaces.Dynamic;

/**
 * Implements stigmas that behave like pheromones deposited by ants.
 * 
 * @author Daniel Henriques Moreira (dhenriques@gmail.com)
 */
public class Pheromone extends Stigma implements Dynamic {
	/* Attributes. */
	/**
	 * Enumerates the dynamic methods of this class (i.e. the methods that are
	 * called automatically by a {@link Bot} object).
	 */
	public static enum DYN_METHODS {
		EVAPORATE("evaporate");

		private final String METHOD_NAME;

		private DYN_METHODS(String methodName) {
			this.METHOD_NAME = methodName;
		}

		public String getMethodName() {
			return this.METHOD_NAME;
		}
	}

	/** Holds the quantity of deposited pheromone. */
	private double quantity;

	/**
	 * Holds the evaporation rate of the pheromone. The quantity of deposited
	 * pheromone is reduced by the formula
	 * "quantity <= quantity * (1.0 - evaporationRate)", per 1.0 sec (by
	 * default).
	 */
	private double evaporationRate;

	/**
	 * Maps the name of the method to evaporate the pheromone with its
	 * {@link EventTimeProbabilityDistribution} object.
	 */
	private Map<String, EventTimeProbabilityDistribution> dynMap;

	/* Methods. */
	/**
	 * Constructor.
	 * 
	 * @param id
	 *            The id of the stigma.
	 * @param vertex
	 *            The vertex where the stigma was deposited.
	 * @param quantity
	 *            The quantity of deposited pheromone.
	 * @param evaporationRate
	 *            The evaporation rate of the pheromone. The quantity of
	 *            deposited pheromone is reduced by the formula
	 *            "quantity <= quantity * (1.0 - evaporationRate)", per 1.0
	 *            second (by default).
	 */
	public Pheromone(String id, Vertex vertex, double quantity,
			double evaporationRate) {
		super(id, vertex);
		this.quantity = quantity;
		this.evaporationRate = evaporationRate;

		this.initDynMap();
	}

	/**
	 * Constructor.
	 * 
	 * @param id
	 *            The id of the stigma.
	 * @param edge
	 *            The edge where the stigma was deposited.
	 * @param quantity
	 *            The quantity of deposited pheromone.
	 * @param evaporationRate
	 *            The evaporation rate of the pheromone. The quantity of
	 *            deposited pheromone is reduced by the formula
	 *            "quantity <= quantity * (1.0 - evaporationRate)", per 1.0
	 *            second (by default).
	 */
	public Pheromone(String id, Edge edge, double quantity,
			double evaporationRate) {
		super(id, edge);
		this.quantity = quantity;
		this.evaporationRate = evaporationRate;

		this.initDynMap();
	}

	/**
	 * Initiates the map that correlates the name of the method to evaporate the
	 * pheromone with its {@link EventTimeProbabilityDistribution} object.
	 */
	private void initDynMap() {
		this.dynMap = new HashMap<String, EventTimeProbabilityDistribution>();
		this.dynMap.put(Pheromone.DYN_METHODS.EVAPORATE.getMethodName(),
				new UniformEventTimeProbabilityDistribution(0, 1));
	}

	/**
	 * Configures the time interval that shall pass between two consecutive
	 * invocations of the {@link #evaporate()} method.
	 * 
	 * @param evaporationTimeInterval
	 *            Measured in seconds.
	 */
	public void setEvaporationTimeInterval(double evaporationTimeInterval) {
		this.dynMap.get(Pheromone.DYN_METHODS.EVAPORATE.getMethodName())
				.setSamplingTimeInterval(evaporationTimeInterval);
	}

	/**
	 * Evaporates the quantity of deposited pheromone, based on the
	 * {@link #evaporationRate} attribute. The quantity of deposited pheromone
	 * is reduced by the formula
	 * "quantity <= quantity * (1.0 - evaporationRate)", per 1.0 second (by
	 * default).
	 */
	public void evaporate() {
		this.quantity = this.quantity * (1.0 - this.evaporationRate);
	}

	public Stigma getCopy(Vertex vertexCopy) {
		Pheromone answer = new Pheromone(this.id, vertexCopy, this.quantity,
				this.evaporationRate);
		answer.dynMap = this.dynMap;
		answer.hashcode = this.hashCode();

		return answer;
	}

	public Stigma getCopy(Edge edgeCopy) {
		Pheromone answer = new Pheromone(this.id, edgeCopy, this.quantity,
				this.evaporationRate);
		answer.dynMap = this.dynMap;
		answer.hashcode = this.hashCode();

		return answer;
	}

	protected void initStigmaType() {
		this.stigmaType = StigmaTypes.PHEROMONE;
	}

	public String fullToXML() {
		// holds the answer being constructed
		StringBuffer buffer = new StringBuffer();

		// fills the buffer
		buffer.append("<stigma id=\"" + this.id + "\" type=\""
				+ this.stigmaType + "\"");

		if (this.vertex != null)
			buffer.append(" vertex_id=\"" + this.vertex.getId() + "\"");
		else
			buffer.append(" edge_id=\"" + this.edge.getId() + "\"");

		buffer.append(" quantity=\""
				+ this.quantity
				+ "\" evaporation_rate=\""
				+ this.evaporationRate
				+ "\" evaporation_time_interval=\""
				+ this.dynMap.get(
						Pheromone.DYN_METHODS.EVAPORATE.getMethodName())
						.getSamplingTimeInterval() + "\"/>");

		// returns the buffer content
		return buffer.toString();
	}

	public EventTimeProbabilityDistribution getETPD(String methodName) {
		return this.dynMap.get(methodName);
	}
}
