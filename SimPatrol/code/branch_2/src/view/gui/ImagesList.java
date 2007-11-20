/* ImagesList.java */

/* The package of this class. */
package view.gui;

/* Imported classes and/or interfaces. */
import java.net.URL;
import javax.swing.ImageIcon;

/** Points to all the images used by the GUI of the simulator. */
public abstract class ImagesList {
	/* Images. */
	/* SimPatrol's icon 1. */
	private static final URL ICON_1_URL = ImagesList.class.getClassLoader()
			.getResource("view/gui/res/icon_1.png");

	public static final ImageIcon ICON_1 = new ImageIcon(ICON_1_URL);

	/* SimPatrol's icon 2. */
	private static final URL ICON_2_URL = ImagesList.class.getClassLoader()
			.getResource("view/gui/res/icon_2.png");

	public static final ImageIcon ICON_2 = new ImageIcon(ICON_2_URL);

	/* SimPatrol's logo 1. */
	private static final URL LOGO_1_URL = ImagesList.class.getClassLoader()
			.getResource("view/gui/res/logo_1.png");

	public static final ImageIcon LOGO_1 = new ImageIcon(LOGO_1_URL);

	/* SimPatrol's icon 2. */
	private static final URL LOGO_2_URL = ImagesList.class.getClassLoader()
			.getResource("view/gui/res/logo_2.png");

	public static final ImageIcon LOGO_2 = new ImageIcon(LOGO_2_URL);
}
