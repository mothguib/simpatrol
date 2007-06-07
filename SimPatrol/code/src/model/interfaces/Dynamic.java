/* Dynamic.java */

/* The package of this interface. */
package model.interfaces;

/* Imported classes and/or interfaces. */
import util.etpd.EventTimeProbabilityDistribution;

/** Lets the objects that implement it have a dynamic
 *  behaviour. */
public interface Dynamic {	
	/** Returns the appearing probability distribution of the dynamic object.
	 *  @return The time probability distribution for the object appearing. */	
	public EventTimeProbabilityDistribution getAppearingETPD();
	
	/** Returns the disappearing probability distribution of the dynamic object.
	 *  @return The time probability distribution for the object disappearing. */	
	public EventTimeProbabilityDistribution getDisappearingETPD();
	
	/** Returns if the dynamic object is appearing.
	 *  @return TRUE, if the object is appearing, FALSE if not. */
	public boolean isAppearing();
	
	/** Configures if the dynamic object is appearing.
	 *  @param is_appearing TRUE, if the object is appearing, FALSE if not. */
	public void setIsAppearing(boolean is_appearing);
}
