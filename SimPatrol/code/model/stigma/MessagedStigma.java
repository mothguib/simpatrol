/* MessagedStigma.java (2.0) */
package br.org.simpatrol.server.model.stigma;

/* Imported classes and/or interfaces. */
import br.org.simpatrol.server.model.graph.Edge;
import br.org.simpatrol.server.model.graph.Vertex;

/**
 * Implements stigmas that hold strings as if they were messages.
 * 
 * @author Daniel Henriques Moreira (dhenriques@gmail.com)
 */
public class MessagedStigma extends Stigma {
	/* Attributes. */
	/** The string held as a message. */
	private String message;

	/* Methods. */
	/**
	 * Constructor.
	 * 
	 * @param id
	 *            The id of the stigma.
	 * @param vertex
	 *            The vertex where the stigma was deposited.
	 * @param message
	 *            The string held by the stigma as a message.
	 */
	public MessagedStigma(String id, Vertex vertex, String message) {
		super(id, vertex);
		this.message = message;
	}

	/**
	 * Constructor.
	 * 
	 * @param id
	 *            The id of the stigma.
	 * @param edge
	 *            The edge where the stigma was deposited.
	 * @param message
	 *            The string held by the stigma as a message.
	 */
	public MessagedStigma(String id, Edge edge, String message) {
		super(id, edge);
		this.message = message;
	}

	public Stigma getCopy(Vertex vertexCopy) {
		MessagedStigma answer = new MessagedStigma(this.id, vertexCopy,
				this.message);
		answer.hashcode = this.hashCode();

		return answer;
	}

	public Stigma getCopy(Edge edgeCopy) {
		MessagedStigma answer = new MessagedStigma(this.id, edgeCopy,
				this.message);
		answer.hashcode = this.hashCode();

		return answer;
	}

	protected void initStigmaType() {
		this.stigmaType = StigmaTypes.MESSAGED;
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

		buffer.append(" message=\"" + this.message + "\"/>");

		// returns the buffer content
		return buffer.toString();
	}
}
