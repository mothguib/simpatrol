/* DepthLimitation.java */

/* The package of this class. */
package model.limitation;

/** Implements the limitations that control the depth
 *  of a permission. */
public final class DepthLimitation extends Limitation {
	/* Attributes. */
	/** The depth limit. */
	private int depth;
	
	/* Methods. */
	/** Constructor.
	 *  @param depth The depth limit. */
	public DepthLimitation(int depth) {
		super();
		this.depth = depth;
	}
	
	/** Returns the deth of this limitation.
	 *  @return The depth of this limitation. */
	public int getDepth() {
		return this.depth;
	}
	
	public String toXML(int identation) {
		// holds the answer for the method
		StringBuffer buffer = new StringBuffer();
		
		// applies the identation
		for(int i = 0; i < identation; i++) buffer.append("\t");
		
		// fills the buffer
		buffer.append("<limitation type=\"" + LimitationTypes.DEPTH_LIMITATION + "\">/n");
		
		// puts the parameters of the limitation
		for(int i = 0; i < identation + 1; i++) buffer.append("\t");
		buffer.append("<lmt_parameter value=\"" + this.depth + "\"/>\n");
		
		// closes the main tag
		for(int i = 0; i < identation; i++) buffer.append("\t");
		buffer.append("</limitation_type>");
		
		// returns the answer
		return buffer.toString();
	}
}