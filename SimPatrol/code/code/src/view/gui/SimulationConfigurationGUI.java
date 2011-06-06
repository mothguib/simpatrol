/* SimulationConfiguration.java */

/* The package of this class. */
package view.gui;

/* Imported classes and/or interfaces. */
import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import javax.swing.JButton;
import javax.swing.JFormattedTextField;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JSpinner;
import javax.swing.JTextField;
import javax.swing.border.EtchedBorder;
import javax.swing.border.TitledBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

/** Implements the dialog window that configures the SimPatrol's simulator. */
public class SimulationConfigurationGUI extends javax.swing.JDialog {
	/* Attributes. */
	/** Generated serial version UID (by Eclipse) */
	private static final long serialVersionUID = 5834495324451587411L;

	/** Registers if the ok button was clicked. */
	private boolean button_ok_clicked = false;

	/* GUI components. */
	// configuration panel
	private JPanel configuration_panel;

	private JPanel simulation_mode_panel;

	private JRadioButton real_time_choice;

	private JRadioButton cycled_choice;

	private JPanel port_panel;

	private JSpinner port_input;

	private JPanel update_rate_panel;

	private JFormattedTextField update_rate_input;

	private JLabel sec_label;

	// button panel
	private JPanel ok_button_panel;

	private JButton ok_button;

	/* Methods. */
	/**
	 * Constructor.
	 * 
	 * @param parent
	 *            The SimPatrol's GUI.
	 */
	public SimulationConfigurationGUI(SimPatrolGUI parent) {
		super(parent, true);
		this.initWindow();
	}

	/** Initializes the dialog window. */
	private void initWindow() {
		// 1. configures this window
		this.setTitle("Configure simulation");
		this.setResizable(false);
		this.addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent evt) {
				exitWindow(evt);
			}
		});

		// 2. configures the "configuration panel"
		this.configuration_panel = new JPanel();
		this.configuration_panel.setLayout(new GridLayout(3, 1));

		// 2.1. configures the simulation mode panel
		this.simulation_mode_panel = new JPanel();
		this.real_time_choice = new JRadioButton();
		this.cycled_choice = new JRadioButton();
		this.simulation_mode_panel.setLayout(new GridLayout(1, 2));
		this.simulation_mode_panel.setBorder(new TitledBorder(
				new EtchedBorder(), "Simulation mode"));		
		this.real_time_choice.setText("Real time");
		this.real_time_choice.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent evt) {
				real_time_choiceStateChanged(evt);
			}
		});
		this.simulation_mode_panel.add(this.real_time_choice);
		this.cycled_choice.setText("Cycled");
		this.cycled_choice.setSelected(true);
		this.cycled_choice.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent evt) {
				cycled_choiceStateChanged(evt);
			}
		});
		this.simulation_mode_panel.add(this.cycled_choice);
		this.configuration_panel.add(this.simulation_mode_panel);

		// 2.2. configures the port number configuration
		this.port_panel = new JPanel();
		this.port_input = new JSpinner();
		this.port_panel.setLayout(new BorderLayout());
		this.port_panel.setBorder(new TitledBorder(new EtchedBorder(),
				"Port number"));
		this.port_input.setValue(new Integer(5000));
		this.port_input.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent evt) {
				port_inputStateChanged(evt);
			}
		});
		this.port_panel.add(this.port_input, BorderLayout.CENTER);
		this.configuration_panel.add(this.port_panel);

		// 2.3. configures the update rate panel
		this.update_rate_panel = new JPanel();
		this.update_rate_input = new JFormattedTextField(new Double(0.005));
		this.update_rate_input.setHorizontalAlignment(JTextField.RIGHT);
		this.sec_label = new JLabel();
		this.update_rate_panel.setLayout(new BorderLayout());
		this.update_rate_panel.setBorder(new TitledBorder(new EtchedBorder(),
				"Update rate"));
		this.update_rate_panel.add(this.update_rate_input, BorderLayout.CENTER);
		this.sec_label.setText(" sec");
		this.update_rate_panel.add(this.sec_label, BorderLayout.EAST);
		this.configuration_panel.add(this.update_rate_panel);

		// 2.4. adds the configuration panel to the dialog window
		this.getContentPane()
				.add(this.configuration_panel, BorderLayout.CENTER);

		// 3. configures the button panel
		this.ok_button_panel = new JPanel();
		this.ok_button = new JButton();
		this.ok_button.setText("Ok");
		this.ok_button.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				ok_buttonActionPerformed(evt);
			}
		});
		this.ok_button_panel.add(this.ok_button);
		this.getContentPane().add(this.ok_button_panel, BorderLayout.SOUTH);

		// finishes the configuration
		java.awt.Dimension screenSize = java.awt.Toolkit.getDefaultToolkit()
				.getScreenSize();
		setBounds((screenSize.width - 350) / 2, (screenSize.height - 207) / 2,
				350, 207);
	}

	/** Doesn't let the dialog window close. */
	private void exitWindow(WindowEvent evt) {
		this.dispose();

		if (!this.button_ok_clicked)
			new SimulationConfigurationGUI((SimPatrolGUI) this.getParent())
					.setVisible(true);
	}

	/** Executed when the ok button is pressed. */
	private void ok_buttonActionPerformed(ActionEvent evt) {
		// treats the update time rate
		double update_time_rate = ((Double) this.update_rate_input.getValue())
				.doubleValue();
		if (update_time_rate <= 0) {
			JOptionPane.showMessageDialog(this,
					"The update rate must be a real positive number.",
					"Update rate error", JOptionPane.ERROR_MESSAGE);
			return;
		}

		// registers that the ok button was clicked
		this.button_ok_clicked = true;

		// configures the main GUI
		((SimPatrolGUI) this.getParent()).configureSimulation(
				this.real_time_choice.isSelected(), ((Integer) this.port_input
						.getValue()).intValue(), update_time_rate);

		// closes this window
		this.dispose();
	}

	/** Executed when the port input changes. */
	private void port_inputStateChanged(ChangeEvent evt) {
		if (((Integer) this.port_input.getValue()) < 0)
			this.port_input.setValue(new Integer(0));
	}

	/** Executed when the cycled simulation mode is chosen. */
	private void cycled_choiceStateChanged(ChangeEvent evt) {
		boolean is_selected = this.cycled_choice.isSelected();
		if (this.real_time_choice.isSelected() == is_selected)
			this.real_time_choice.setSelected(!is_selected);
	}

	/** Executed when the real time simulation mode is chosen. */
	private void real_time_choiceStateChanged(ChangeEvent evt) {
		boolean is_selected = this.real_time_choice.isSelected();
		if (this.cycled_choice.isSelected() == is_selected)
			this.cycled_choice.setSelected(!is_selected);
	}
}
