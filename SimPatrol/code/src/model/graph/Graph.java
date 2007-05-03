package model.Graph1;

import java.util.Collection;

import model.interfaces.XMLable;
import model.persistence.ExperimentalScenery;

/**
 * @model.uin <code>design:node:::gjtoxf17uk14ugglvpc</code>
 */
public class Graph implements XMLable {

	/**
	 * @model.uin <code>design:node:::a7glof17uk14ujzrw3k</code>
	 */
	public Collection<Edge> edge;

	/**
	 * @model.uin <code>design:node:::b55asf17vi6plhuj2cm</code>
	 */
	public ExperimentalScenery experimentalScenery;

	/**
	 * @model.uin <code>design:node:::e2gmtf17uk14u-dyl3jm</code>
	 */
	public Collection<Vertex> vertex;

	/**
	 * @model.uin <code>design:node:::29k9yf17uk14u447lft:gjtoxf17uk14ugglvpc</code>
	 */
	private String label;
}
