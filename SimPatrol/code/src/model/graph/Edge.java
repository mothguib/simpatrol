package model.graph;

import model.interfaces.XMLable;

/**
 * @model.uin <code>design:node:::a7glof17uk14ujzrw3k</code>
 */
public class Edge implements XMLable {

	/**
	 * @model.uin <code>design:node:::32va9f17uk14u-45ywpq</code>
	 */
	public Stigma stigma;

	/**
	 * @model.uin <code>design:node:::e2gmtf17uk14u-dyl3jm</code>
	 */
	public Vertex emitter;

	/**
	 * @model.uin <code>design:node:::e2gmtf17uk14u-dyl3jm</code>
	 */
	public Vertex collector;

	/**
	 * @model.uin <code>design:node:::95nu3f17uk14u37bb5j:a7glof17uk14ujzrw3k</code>
	 */
	private double length;

	/**
	 * @model.uin <code>design:node:::8kgff17uk14u-sspk1p:a7glof17uk14ujzrw3k</code>
	 */
	private boolean visibility;
}
