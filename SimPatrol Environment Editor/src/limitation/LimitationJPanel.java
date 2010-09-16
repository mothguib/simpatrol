/* LimitationJPanel.java */

/** The package of this class. */
package limitation;

/* Imported classes and/or interfaces. */
import javax.swing.JPanel;
import model.limitation.Limitation;

/**
 * Implements the GUI panel able to configure Limitation objects.
 * 
 * @see Limitation
 */
public abstract class LimitationJPanel extends JPanel {
	/** Returns the configured Limitation object. */
	public abstract Limitation getLimitation();
}