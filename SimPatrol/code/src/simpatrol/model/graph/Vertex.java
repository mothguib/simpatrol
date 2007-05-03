package simpatrol.model.graph;

import java.util.Collection;

import simpatrol.model.agent.Agent;
import simpatrol.model.interfaces.XMLable;

/**
 * @model.uin <code>design:node:::e2gmtf17uk14u-dyl3jm</code>
 */
public class Vertex implements XMLable {

	/**
	 * @model.uin <code>design:node:::a7glof17uk14ujzrw3k</code>
	 */
	public Collection<Edge> in;

	/**
	 * @model.uin <code>design:node:::a7glof17uk14ujzrw3k</code>
	 */
	public Collection<Edge> out;

	/**
	 * @model.uin <code>design:node:::32va9f17uk14u-45ywpq</code>
	 */
	public Stigma stigma;

	/**
	 * @model.uin <code>design:node:::i172kf17ujey8agupu8</code>
	 */
	public Collection<Agent> agent;

	/**
	 * @model.uin <code>design:node:::gjtoxf17uk14ugglvpc</code>
	 */
	public Graph graph;

	/**
	 * @model.uin <code>design:node:::bf408f17uk14uf4vorm:e2gmtf17uk14u-dyl3jm</code>
	 */
	private String label;

	/**
	 * @model.uin <code>design:node:::9qkinf17uk14ugabye5:e2gmtf17uk14u-dyl3jm</code>
	 */
	private int priority = 0;

	/**
	 * @model.uin <code>design:node:::fe8ckf17uk14u5zly7o:e2gmtf17uk14u-dyl3jm</code>
	 */
	private boolean visibility;

	/**
	 * @model.uin <code>design:node:::55wbf17uk14u-8yo3ww:e2gmtf17uk14u-dyl3jm</code>
	 */
	private int idleness;

	/**
	 * @model.uin <code>design:node:::9q45ff17uk14ue5oh7o:e2gmtf17uk14u-dyl3jm</code>
	 */
	private boolean fuel = false;
}
