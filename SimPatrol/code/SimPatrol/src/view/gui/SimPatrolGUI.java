/* SimPatrolGUI.java */

/* The package of this class. */
package view.gui;

/* Imported classes and/or interfaces. */
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.net.BindException;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.UIManager;
import javax.swing.border.EtchedBorder;
import javax.swing.border.TitledBorder;
import control.simulator.CycledSimulator;
import control.simulator.RealTimeSimulator;
import control.simulator.Simulator;

/** Implements the GUI of the SimPatrol simulator. */
public class SimPatrolGUI extends javax.swing.JFrame {
	/* Attributes. */
	/** Generated serial version UID (by Eclipse) */
	private static final long serialVersionUID = -6843025417490639893L;

	/** The SimPatrol's simulator. */
	private Simulator simulator;

	/* GUI components. */
	// configuration panel
	private JPanel configuration_panel;

	private JPanel configuration_internal_panel;

	private JPanel port_panel;

	private JLabel port_label;

	private JPanel update_rate_panel;

	private JLabel update_rate_label;

	// output panel
	private JPanel output_panel;

	private JScrollPane output_scroll;

	private JTextArea output;

	// logo and buttons panel
	private JPanel logo_buttons_panel;

	private JPanel logo_panel;

	private JPanel buttons_panel;

	private JButton reset_button;

	private JButton exit_button;

	/* Methods. */
	/** Constructor. */
	public SimPatrolGUI() {
		// initializes this window
		this.initWindow();
	}

	/** Initializes the simulator's main window. */
	private void initWindow() {
		// changes the look and feel of the window, if running on MS Windows
		UIManager.LookAndFeelInfo[] lookings = UIManager
				.getInstalledLookAndFeels();
		try {
			UIManager.setLookAndFeel(lookings[2].getClassName());
		} catch (Exception e) {
			e.printStackTrace(); // traced LookAndFeel exception
		}

		// configures this window
		this.setTitle("SimPatrol v1.0");
		this.setIconImage(ImagesList.ICON_2.getImage());
		this.addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent evt) {
				exitWindow(evt);
			}
		});

		// configures the configuration panel
		this.configuration_panel = new JPanel();
		this.configuration_internal_panel = new JPanel();
		this.port_panel = new JPanel();
		this.port_label = new JLabel();
		this.update_rate_panel = new JPanel();
		this.update_rate_label = new JLabel();
		this.configuration_panel.setLayout(new BorderLayout());
		this.configuration_panel.setBorder(new TitledBorder(new EtchedBorder(),
				""));
		this.configuration_internal_panel
				.setLayout(new GridLayout(1, 2, 15, 0));
		this.port_panel.setLayout(new BorderLayout());
		this.port_panel.add(this.port_label, BorderLayout.WEST);
		this.configuration_internal_panel.add(this.port_panel);
		this.update_rate_panel.setLayout(new BorderLayout());
		this.update_rate_panel.add(this.update_rate_label, BorderLayout.WEST);
		this.configuration_internal_panel.add(this.update_rate_panel);
		this.configuration_panel.add(this.configuration_internal_panel,
				BorderLayout.WEST);
		this.getContentPane().add(this.configuration_panel, BorderLayout.NORTH);

		// configures the output panel
		this.output_panel = new JPanel();
		this.output_scroll = new JScrollPane();
		this.output = new JTextArea();
		this.output_panel.setLayout(new BorderLayout());
		this.output.setEditable(false);
		this.output_scroll.setViewportView(this.output);
		this.output_panel.add(this.output_scroll, BorderLayout.CENTER);
		this.getContentPane().add(this.output_panel, BorderLayout.CENTER);

		// configures the "logo and buttons" panel
		this.logo_buttons_panel = new JPanel();
		this.logo_panel = new JPanel();
		this.buttons_panel = new JPanel();
		this.reset_button = new JButton();
		this.exit_button = new JButton();
		this.logo_buttons_panel.setLayout(new BorderLayout());
		this.logo_panel.setLayout(new BorderLayout());
		this.logo_panel.setBorder(new EtchedBorder());
		this.logo_panel.add(new JLabel(ImagesList.LOGO_1), BorderLayout.CENTER);
		this.logo_panel.setToolTipText("About SimPatrol...");
		this.logo_panel.addMouseListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent evt) {
				logo_panelMouseClicked(evt);
			}
		});
		this.logo_buttons_panel.add(this.logo_panel, BorderLayout.CENTER);
		this.buttons_panel.setLayout(new GridLayout(2, 0));
		this.reset_button.setText("Reset");
		this.reset_button.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				reset_buttonActionPerformed(evt);
			}
		});
		this.buttons_panel.add(this.reset_button);
		this.exit_button.setText("Exit");
		this.exit_button.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				exit_buttonActionPerformed(evt);
			}
		});
		this.buttons_panel.add(this.exit_button);
		this.logo_buttons_panel.add(this.buttons_panel, BorderLayout.SOUTH);
		this.getContentPane().add(this.logo_buttons_panel, BorderLayout.EAST);

		// redirects the default system output to the text area
		OutputStream output_stream = new OutputStream() {
			public void write(int b) {
				output.append(String.valueOf((char) b));
			};
		};
		System.setOut(new PrintStream(output_stream));

		// resizes this window
		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		this.setBounds((screenSize.width - 600) / 2,
				(screenSize.height - 400) / 2, 600, 400);
	}

	public void setVisible(boolean visibility) {
		super.setVisible(visibility);

		if (visibility)
			new SimulationConfigurationGUI(this).setVisible(true);
	}

	/**
	 * Configures the simulation mode, as well as the port number of the
	 * simulator and its update time rate.
	 * 
	 * @param is_real_time_simulation
	 *            TRUE is the simulation mode is real time, FALSE if not.
	 * @param port_number
	 *            The number of the port of the simulator.
	 * @param update_time_rate
	 *            The update time rate of the simulator.
	 */
	public void configureSimulation(boolean is_real_time_simulation,
			int port_number, double update_time_rate) {
		this.port_label.setText("Port: " + port_number);
		this.update_rate_label.setText("Update rate: " + update_time_rate
				+ " sec");

		try {
			if (is_real_time_simulation) {
				((TitledBorder) this.configuration_panel.getBorder())
						.setTitle("Real time simulator");
				this.simulator = new RealTimeSimulator(port_number,
						update_time_rate);
			} else {
				((TitledBorder) this.configuration_panel.getBorder())
						.setTitle("Cycled simulator");
				this.simulator = new CycledSimulator(port_number,
						update_time_rate);
			}
		} catch (BindException e) {
			JOptionPane.showMessageDialog(this,
					"Port number already in use. The program will be closed.",
					"Port error", JOptionPane.ERROR_MESSAGE);
			e.printStackTrace(); // traced bind exception
			System.exit(0);
		} catch (IOException e) {
			JOptionPane.showMessageDialog(this,
					"IO error ocurred. The program will be closed.",
					"IO error", JOptionPane.ERROR_MESSAGE);
			e.printStackTrace(); // traced io exception
			System.exit(0);
		}
	}

	/** Executed when the SimPatrol logo is clicked. */
	private void logo_panelMouseClicked(MouseEvent evt) {
		// TODO
	}

	/** Executed when the reset button is clicked. */
	private void reset_buttonActionPerformed(ActionEvent evt) {
		try {
			this.simulator.stopSimulation();
		} catch (IOException e) {
			JOptionPane.showMessageDialog(this,
					"IO error ocurred. The program will be closed.",
					"IO error", JOptionPane.ERROR_MESSAGE);
			e.printStackTrace(); // traced io exception
			System.exit(0);
		}
	}

	/** Executed when the exit button is clicked. */
	private void exit_buttonActionPerformed(ActionEvent evt) {
		try {
			this.simulator.exit();
		} catch (IOException e) {
			e.printStackTrace(); // traced io exception
		}
		System.exit(0);
	}

	/** Exits the window and terminates the application. */
	private void exitWindow(WindowEvent evt) {
		try {
			this.simulator.exit();
		} catch (IOException e) {
			e.printStackTrace(); // traced io exception
		}
		System.exit(0);
	}

	/** Turns this class into an executable one. */
	public static void main(String args[]) {
		new SimPatrolGUI().setVisible(true);
	}
}