/* EventTimeProbabilityDistributionJPanel.java */

/* The package of this class. */
package etpd;

/* Imported classes and/or interfaces. */
import javax.swing.JPanel;
import model.etpd.EventTimeProbabilityDistribution;

/**
 * Implements the GUI panels able to configure EventTimeProbabilityDistribution
 * objects.
 * 
 * @see EventTimeProbabilityDistribution
 */
public abstract class EventTimeProbabilityDistributionJPanel extends JPanel {
	/** Returns the configured EventTimeProbabilityDistribution object. */
	public abstract EventTimeProbabilityDistribution getETPD();
}
