/* PerceptionDaemon.java */

/* The package of this class. */
package control.daemon;

/* Imported classes and/or interfaces. */
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import control.simulator.SimulatorStates;
import util.data_structures.Queue;
import model.agent.Agent;
import model.agent.AgentStates;
import model.agent.Society;
import model.graph.Edge;
import model.graph.Graph;
import model.graph.Vertex;
import model.limitation.DepthLimitation;
import model.limitation.Limitation;
import model.limitation.StaminaLimitation;
import model.perception.AgentsPerception;
import model.perception.BroadcastPerception;
import model.perception.GraphPerception;
import model.perception.Perception;
import model.perception.PerceptionTypes;
import model.perception.SelfPerception;
import model.perception.StigmasPerception;
import model.permission.PerceptionPermission;
import model.stigma.Stigma;

/**
 * Implements the daemons of SimPatrol that produces an agent's perceptions.
 * 
 * @developer New Perception classes must change this class.
 * @developer New Limitation classes can change this class.
 * @modeler This class must have its behavior modeled.
 */
public final class PerceptionDaemon extends AgentDaemon {
	/* Attributes. */
	/** Queue of received messages sent by other agents. */
	private Queue<String> received_messages;

	/** The amount of stamina to be spent due to the perceptions. */
	private double spent_stamina;

	/* Methods. */
	/**
	 * Constructor.
	 * 
	 * Doesn't initiate its own connection, as it will be shared with an
	 * ActionDaemon object. So the connection must be set by the setConnection()
	 * method.
	 * 
	 * @see PerceptionDaemon
	 * 
	 * @param thread_name
	 *            The name of the thread of the daemon.
	 * @param agent
	 *            The agent whose perceptions are produced.
	 */
	public PerceptionDaemon(String thread_name, Agent agent) {
		super(thread_name, agent);
		this.received_messages = null;
		this.spent_stamina = 0;

		// configures the clock of the daemon
		this.clock.setStep(simulator.getUpdate_time_rate());
	}

	/**
	 * Lets the agent of this daemon receive a message, if it has permission to
	 * do it.
	 * 
	 * @param message
	 *            The message to be received by the agent of this daemon.
	 */
	public void receiveMessage(String message) {
		// verifies if the agent has permission to receive broadcasted messages
		PerceptionPermission[] permissions = this.AGENT.getAllowedPerceptions();
		for (int i = 0; i < permissions.length; i++) {
			if (permissions[i].getPerception_type() == PerceptionTypes.BROADCAST) {
				this.insertMessage(message);
				return;
			}
		}
		// nullifies the received messages
		this.received_messages = null;
	}

	/**
	 * Inserts the given message into the queue of received messages.
	 * 
	 * @param message
	 *            The message to be enqueued.
	 */
	private void insertMessage(String message) {
		if (this.received_messages == null) {
			this.received_messages = new Queue<String>();
		}

		// adds the message to the received ones
		this.received_messages.insert(message);
	}

	/**
	 * Produces all the perceptions an agent is allowed to have at the moment.
	 * 
	 * @return The perceptions of the agent at the moment.
	 * @developer New Perception classes must change this method.
	 */
	private Perception[] producePerceptions() {
		// holds the eventually produced perceptions
		List<Perception> perceptions = new LinkedList<Perception>();

		// resets the spent stamina with perceptions
		this.spent_stamina = 0;

		// obtains the allowed perceptions for the agent
		PerceptionPermission[] allowed_perceptions = this.AGENT
				.getAllowedPerceptions();

		// for each allowed perception
		for (int i = 0; i < allowed_perceptions.length; i++) {
			// depending on the type of the current permission
			switch (allowed_perceptions[i].getPerception_type()) {
			// if it's a permission for "graph perceptions"
			case (PerceptionTypes.GRAPH): {
				// obtains the perception of the graph
				GraphPerception perception = this
						.produceGraphPerception(allowed_perceptions[i]
								.getLimitations());

				// if the perception is valid, adds it to the produced
				// perceptions
				if (perception != null)
					perceptions.add(perception);

				break;
			}

				// if it's a permission for "agents perceptions"
			case (PerceptionTypes.AGENTS): {
				// obtains the perception of the agents
				AgentsPerception perception = this
						.produceAgentsPerception(allowed_perceptions[i]
								.getLimitations());

				// if the perception is valid, adds it to the produced
				// perceptions
				if (perception != null)
					perceptions.add(perception);

				break;
			}

				// if it's a permission for "stigmas perceptions"
			case (PerceptionTypes.STIGMAS): {
				// obtains the perceptions of the stigmas
				StigmasPerception perception = this
						.produceStigmasPerception(allowed_perceptions[i]
								.getLimitations());

				// if the perception is valid, adds it to the produced
				// perceptions
				if (perception != null)
					perceptions.add(perception);

				break;
			}

				// if it's a permission for "broadcasted messages perceptions"
			case (PerceptionTypes.BROADCAST): {
				// obtains the perceptions of the messages
				BroadcastPerception[] broadcast_perceptions = this
						.produceBroadcastPerceptions(allowed_perceptions[i]
								.getLimitations());

				// adds each one to the produced perceptions
				for (int j = 0; j < broadcast_perceptions.length; j++)
					perceptions.add(broadcast_perceptions[j]);

				break;
			}

				// if it's a permission of itself
			case (PerceptionTypes.SELF): {
				// obtains the perceptions of itself
				SelfPerception self_perception = this
						.produceSelfPerception(allowed_perceptions[i]
								.getLimitations());

				// if the perception is valid, adds it to the produced
				// perceptions
				if (self_perception != null)
					perceptions.add(self_perception);

				break;
			}

				// developer: new perceptions must add code here
			}
		}

		// configures the amount of stamina to be spent with perceptions
		if (this.stamina_robot != null)
			this.stamina_robot.setPerceptions_spent_stamina(this.spent_stamina);
		else if (coordinator != null)
			coordinator.setPerceptionsSpentStamina(this.AGENT,
					this.spent_stamina);

		// returns the answer
		return perceptions.toArray(new Perception[0]);
	}

	/**
	 * Obtains the perception for the agent of itself, given the eventual
	 * limitations.
	 * 
	 * @param limitations
	 *            The limitations to the perception of the agent itself.
	 * @return The perception of the agent itself
	 * @developer New Limitation classes can change this method.
	 */
	private SelfPerception produceSelfPerception(Limitation[] limitations) {
		// holds an eventual stamina limitation
		double stamina = 0;

		// for each limitation, tries to set the stamina limitation
		for (int i = 0; i < limitations.length; i++) {
			if (limitations[i] instanceof StaminaLimitation)
				stamina = ((StaminaLimitation) limitations[i]).getCost();
			// developer: new limitations must add code here
		}

		// if there's enough stamina to perceive
		if (this.AGENT.getStamina() > this.spent_stamina + stamina) {
			// updates the spent stamina
			this.spent_stamina = this.spent_stamina + stamina;

			// returns the agent perceiving itself
			return new SelfPerception(this.AGENT);
		}

		// default answer
		return null;
	}

	/**
	 * Obtains the graph perception for the agent, given the eventual
	 * limitations.
	 * 
	 * @param limitations
	 *            The limitations to the perception of the graph.
	 * @return The perception of the graph.
	 * @developer New Limitation classes can change this method.
	 */
	private GraphPerception produceGraphPerception(Limitation[] limitations) {
		// holds an eventual depth limitation
		int depth = -1;

		// holds an eventual stamina limitation
		double stamina = 0;

		// for each limitation, tries to set the depth and stamina limitations
		for (int i = 0; i < limitations.length; i++) {
			if (limitations[i] instanceof DepthLimitation)
				depth = ((DepthLimitation) limitations[i]).getDepth();
			else if (limitations[i] instanceof StaminaLimitation)
				stamina = ((StaminaLimitation) limitations[i]).getCost();
			// developer: new limitations must add code here
		}

		// if there's enough stamina to perceive
		if (this.AGENT.getStamina() > this.spent_stamina + stamina) {
			// updates the spent stamina
			this.spent_stamina = this.spent_stamina + stamina;

			// returns the perceived subgraph
			return new GraphPerception(simulator.getEnvironment().getGraph()
					.getVisibleEnabledSubgraph(this.AGENT.getVertex(), depth));
		}

		// default answer
		return null;
	}

	/**
	 * Obtains the perception of other agents for the agent, given the eventual
	 * limitations.
	 * 
	 * @param limitations
	 *            The limitations to the perception of agents.
	 * @return The perception of the other agents of the simulation.
	 * @developer New Limitation classes can change this method.
	 */
	private AgentsPerception produceAgentsPerception(Limitation[] limitations) {
		// holds an eventual depth limitation
		int depth = -1;

		// holds an eventual stamina limitation
		double stamina = 0;

		// for each limitation, tries to set the depth and stamina limitations
		for (int i = 0; i < limitations.length; i++) {
			if (limitations[i] instanceof DepthLimitation)
				depth = ((DepthLimitation) limitations[i]).getDepth();
			else if (limitations[i] instanceof StaminaLimitation)
				stamina = ((StaminaLimitation) limitations[i]).getCost();
			// developer: new limitations must add code here
		}

		// if there's enough stamina to perceive
		if (this.AGENT.getStamina() > this.spent_stamina + stamina) {
			// updates the spent stamina
			this.spent_stamina = this.spent_stamina + stamina;

			// holds the perceived agents
			List<Agent> perceived_agents = new LinkedList<Agent>();

			// obtains the visible subgraph
			// with the given depth
			Graph subgraph = simulator.getEnvironment().getGraph()
					.getVisibleEnabledSubgraph(this.AGENT.getVertex(), depth);

			// obtains the societies of the simulation
			Society[] societies = simulator.getEnvironment().getSocieties();

			// for each society
			for (int i = 0; i < societies.length; i++) {
				// obtains its agents
				Agent[] agents = societies[i].getAgents();

				// for each agent
				for (int j = 0; j < agents.length; j++)
					// if the current agent is not the one that's perceiving
					if (!this.AGENT.equals(agents[j])) {
						// obtains the vertex that the current agent comes from
						Vertex vertex = agents[j].getVertex();

						// obtains the edge where the agent is
						Edge edge = agents[j].getEdge();

						// if the obtained vertex and edge are part of the
						// subgraph
						if (subgraph.hasVertex(vertex)
								&& (edge == null || subgraph.hasEdge(edge)))
							// adds the current agent to the perceived ones
							perceived_agents.add(agents[j]);
					}
			}

			// return the perceived agents
			return new AgentsPerception(perceived_agents.toArray(new Agent[0]));
		}

		// default answer
		return null;
	}

	/**
	 * Obtains the perception of the stigmas deposited on the graph of the
	 * simulation, given the eventual limitations.
	 * 
	 * @param limitations
	 *            The limitations to the perception of stigmas.
	 * @return The perception of the stigmas deposited on the graph.
	 * @developer New Limitation classes can change this method.
	 */
	private StigmasPerception produceStigmasPerception(Limitation[] limitations) {
		// holds an eventual depth limitation
		int depth = -1;

		// holds an eventual stamina limitation
		double stamina = 0;

		// for each limitation, tries to set the depth and stamina limitations
		for (int i = 0; i < limitations.length; i++) {
			if (limitations[i] instanceof DepthLimitation)
				depth = ((DepthLimitation) limitations[i]).getDepth();
			else if (limitations[i] instanceof StaminaLimitation)
				stamina = ((StaminaLimitation) limitations[i]).getCost();
			// developer: new limitations must add code here
		}

		// if there's enough stamina to perceive
		if (this.AGENT.getStamina() > this.spent_stamina + stamina) {
			// updates the spent stamina
			this.spent_stamina = this.spent_stamina + stamina;

			// obtains the visible subgraph
			// with the given depth
			Graph subgraph = simulator.getEnvironment().getGraph()
					.getVisibleEnabledSubgraph(this.AGENT.getVertex(), depth);

			// obtains the stigmas of the subgraph
			Stigma[] stigmas = subgraph.getStigmas();

			// returns the perceived stigmas
			return new StigmasPerception(stigmas);
		}

		// default answer
		return null;
	}

	/**
	 * Obtains the perception of broadcasted messages, given the eventual
	 * limitations.
	 * 
	 * @param limitations
	 *            The limitations to the perception of broadcasted messages.
	 * @return The perception of broadcasted messages.
	 * @developer New Limitation classes can change this method.
	 */
	private BroadcastPerception[] produceBroadcastPerceptions(
			Limitation[] limitations) {
		// holds the produced broadcast perceptions
		LinkedList<BroadcastPerception> broadcast_perceptions = new LinkedList<BroadcastPerception>();

		// holds an eventual stamina limitation
		double stamina = 0;

		// for each limitation, tries to set the stamina limitation
		for (int j = 0; j < limitations.length; j++)
			if (limitations[j] instanceof StaminaLimitation)
				stamina = ((StaminaLimitation) limitations[j]).getCost();
		// developer: new limitations can change the code here

		if (this.received_messages != null)
			// for each received message
			while (this.received_messages.getSize() > 0) {
				// obtains the current message
				String message = this.received_messages.remove();

				// if the agent has enough stamina to perceive
				if (this.AGENT.getStamina() > this.spent_stamina + stamina) {
					// updates the spent stamina
					this.spent_stamina = this.spent_stamina + stamina;

					// adds a new broadcast perception to the produced ones
					broadcast_perceptions.add(new BroadcastPerception(message));
				}
			}

		// mounts the answer of the method
		BroadcastPerception[] answer = new BroadcastPerception[broadcast_perceptions
				.size()];
		for (int i = 0; i < answer.length; i++)
			answer[i] = broadcast_perceptions.get(i);

		// returns the answer
		return answer;
	}

	public void start(int local_socket_number) throws IOException {
		super.start(local_socket_number);
	}

	public void stopActing() {
		// used by AspectJ
		super.stopActing();
	}

	/** @modeler This method must be modeled. */
	public void act() {
		synchronized (simulator) {
			// if the daemon can produce perceptions at the moment and the
			// simulator is already simulating
			if (simulator.getState() == SimulatorStates.SIMULATING
					&& !this.is_blocked) {
				// registers if some perception was successfully sent
				boolean sent_succesfully = false;

				// obtains all the perceptions the agent is supposed to have at
				// the moment
				Perception[] perceptions = this.producePerceptions();

				// for each perception, sends it to the remote agent
				for (int i = 0; i < perceptions.length; i++) {
					sent_succesfully = this.connection.send(perceptions[i]
							.fullToXML(0));
					if (perceptions[i] instanceof AgentsPerception) {
						this.connection.send(perceptions[i].fullToXML(0));
						this.connection.send(perceptions[i].fullToXML(0));
						this.connection.send(perceptions[i].fullToXML(0));
						this.connection.send(perceptions[i].fullToXML(0));
						this.connection.send(perceptions[i].fullToXML(0));
					}
				}

				// if the perceptions were successfully sent,
				// changes the agent's state to JUST_PERCEIVED
				if (sent_succesfully)
					this.AGENT.setState(AgentStates.JUST_PERCEIVED);
			}
		}
	}
}