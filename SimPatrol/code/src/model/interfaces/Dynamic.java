/* Dynamic.java */

/* The package of this interface. */
package model.interfaces;

/* Imported classes and/or interfaces. */
import util.etpd.EventTimeProbabilityDistribution;

/** Lets the objects that implement it have a dynamic
 *  behaviour. */
public interface Dynamic {	
	/** Returns the probability distribution for the appearing time
	 *  of the dynamic object.
	 *  @return The event time probability distribution for the appearing of the object. */
	public EventTimeProbabilityDistribution getAppearingTPD();
	
	/** Returns the probability distribution for the disappearing time
	 *  of the dynamic object.
	 *  @return The event time probability distribution for the disappearing of the object. */
	public EventTimeProbabilityDistribution getDisappearingTPD();
	
	/** Returns if the dynamic object is appearing.
	 *  @return TRUE, if the object is appearing, FALSE if not. */
	public boolean isAppearing();
	
	/** Configures if the dynamic object is appearing.
	 *  @param is_appearing TRUE, if the object is appearing, FALSE if not. */
	public void setIsAppearing(boolean is_appearing);
}