/* EnvironmentJPanel.java */

/* The package of this class. */
package environment;

/* Imported classes and/or interfaces. */
import java.io.IOException;
import java.util.LinkedList;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;
import javax.xml.parsers.ParserConfigurationException;

import org.xml.sax.SAXException;

import control.exception.EdgeNotFoundException;
import control.exception.VertexNotFoundException;
import society.SocietyGUI;
import util.GraphTranslator;
import model.Environment;
import model.agent.ClosedSociety;
import model.agent.OpenSociety;
import model.agent.PerpetualAgent;
import model.agent.SeasonalAgent;
import model.agent.Society;
import model.graph.Graph;

/**
 * Implements the GUI panel able to configure Environment objects.
 * 
 * @see Environment
 */
public class EnvironmentJPanel extends javax.swing.JPanel {
	/* Methods. */
	/**
	 * Constructor.
	 * 
	 * @param owner
	 *            The GUI that called this one.
	 * @param environment
	 *            The Environment object to be configured.
	 */
	public EnvironmentJPanel(JFrame owner, Environment environment) {
		this.initComponents();
		this.initComponents2(owner, environment);
	}

	/** Initiates the components of the GUI. Generated by NetBeans IDE 3.6. */
	private void initComponents() {
		graph_panel = new javax.swing.JPanel();
		graph_internal_panel_1 = new javax.swing.JPanel();
		graph_path_label = new javax.swing.JLabel();
		graph_label_label = new javax.swing.JLabel();
		graph_path_field = new javax.swing.JTextField();
		graph_label_field = new javax.swing.JTextField();
		graph_internal_panel_2 = new javax.swing.JPanel();
		graph_internal_panel_3 = new javax.swing.JPanel();
		graph_buttons_panel = new javax.swing.JPanel();
		load_graph_button = new javax.swing.JButton();
		societies_panel = new javax.swing.JPanel();
		societies_scroll = new javax.swing.JScrollPane();
		societies_table = new javax.swing.JTable();
		societies_buttons_panel = new javax.swing.JPanel();
		societies_buttons_internal_panel = new javax.swing.JPanel();
		add_closed_society_button = new javax.swing.JButton();
		add_open_society_button = new javax.swing.JButton();
		edit_society_button = new javax.swing.JButton();
		delete_societies_button = new javax.swing.JButton();

		setLayout(new java.awt.BorderLayout());

		graph_panel.setLayout(new java.awt.GridLayout(3, 1));

		graph_panel.setBorder(new javax.swing.border.TitledBorder(
				new javax.swing.border.EtchedBorder(), "Graph"));
		graph_internal_panel_1.setLayout(new java.awt.BorderLayout());
		graph_internal_panel_3.setLayout(new java.awt.BorderLayout());

		graph_path_label.setText("Path ");
		graph_internal_panel_1
				.add(graph_path_label, java.awt.BorderLayout.WEST);

		graph_label_label.setText("Label ");
		graph_internal_panel_3.add(graph_label_label,
				java.awt.BorderLayout.WEST);

		graph_path_field.setEditable(false);
		graph_label_field.setEditable(false);
		graph_internal_panel_1.add(graph_path_field,
				java.awt.BorderLayout.CENTER);
		graph_internal_panel_3.add(graph_label_field,
				java.awt.BorderLayout.CENTER);

		graph_panel.add(graph_internal_panel_1);
		graph_panel.add(graph_internal_panel_3);

		graph_internal_panel_2.setLayout(new java.awt.BorderLayout());

		graph_buttons_panel.setLayout(new java.awt.GridLayout(1, 1));

		load_graph_button.setText("Load");
		load_graph_button
				.addActionListener(new java.awt.event.ActionListener() {
					public void actionPerformed(java.awt.event.ActionEvent evt) {
						load_graph_buttonActionPerformed(evt);
					}
				});

		graph_buttons_panel.add(load_graph_button);

		graph_internal_panel_2.add(graph_buttons_panel,
				java.awt.BorderLayout.EAST);

		graph_panel.add(graph_internal_panel_2);

		add(graph_panel, java.awt.BorderLayout.NORTH);

		societies_panel.setLayout(new java.awt.BorderLayout());

		societies_panel.setBorder(new javax.swing.border.TitledBorder(
				new javax.swing.border.EtchedBorder(), "Societies"));
		societies_table.setModel(new javax.swing.table.DefaultTableModel(
				new Object[][] {

				}, new String[] { "Society id", "Society type" }) {
			private static final long serialVersionUID = -1777814226659693545L;
			@SuppressWarnings("unchecked")
			Class[] types = new Class[] { java.lang.String.class,
					java.lang.String.class };
			boolean[] canEdit = new boolean[] { false, false };

			@SuppressWarnings("unchecked")
			public Class getColumnClass(int columnIndex) {
				return types[columnIndex];
			}

			public boolean isCellEditable(int rowIndex, int columnIndex) {
				return canEdit[columnIndex];
			}
		});
		societies_scroll.setViewportView(societies_table);

		societies_panel.add(societies_scroll, java.awt.BorderLayout.CENTER);

		societies_buttons_panel.setLayout(new java.awt.BorderLayout());

		societies_buttons_internal_panel
				.setLayout(new java.awt.GridLayout(4, 0));

		add_closed_society_button.setText("Add closed society");
		add_closed_society_button
				.addActionListener(new java.awt.event.ActionListener() {
					public void actionPerformed(java.awt.event.ActionEvent evt) {
						add_closed_society_buttonActionPerformed(evt);
					}
				});

		societies_buttons_internal_panel.add(add_closed_society_button);

		add_open_society_button.setText("Add open society");
		add_open_society_button
				.addActionListener(new java.awt.event.ActionListener() {
					public void actionPerformed(java.awt.event.ActionEvent evt) {
						add_open_society_buttonActionPerformed(evt);
					}
				});

		societies_buttons_internal_panel.add(add_open_society_button);

		edit_society_button.setText("Edit society");
		edit_society_button
				.addActionListener(new java.awt.event.ActionListener() {
					public void actionPerformed(java.awt.event.ActionEvent evt) {
						edit_society_buttonActionPerformed(evt);
					}
				});

		societies_buttons_internal_panel.add(edit_society_button);

		delete_societies_button.setText("Remove societies");
		delete_societies_button
				.addActionListener(new java.awt.event.ActionListener() {
					public void actionPerformed(java.awt.event.ActionEvent evt) {
						delete_societies_buttonActionPerformed(evt);
					}
				});

		societies_buttons_internal_panel.add(delete_societies_button);

		societies_buttons_panel.add(societies_buttons_internal_panel,
				java.awt.BorderLayout.NORTH);

		societies_panel
				.add(societies_buttons_panel, java.awt.BorderLayout.EAST);

		add(societies_panel, java.awt.BorderLayout.CENTER);
	}

	/**
	 * Complements the initiation of the components of the GUI.
	 * 
	 * @param owner
	 *            The GUI that called this one.
	 * @param society
	 *            The Society object to be configured.
	 * @param graph
	 *            The graph of the patrolling simulation.
	 */
	private void initComponents2(JFrame owner, Environment environment) {
		this.owner = owner;
		this.societies = new LinkedList<Society>();
		this.graph = null;

		if (environment != null) {
			this.graph = environment.getGraph();

			Society[] societies = environment.getSocieties();
			for (int i = 0; i < societies.length; i++) {
				String society_type = "open";
				if (societies[i] instanceof ClosedSociety)
					society_type = "closed";

				String[] content = { societies[i].getObjectId(), society_type };
				((DefaultTableModel) this.societies_table.getModel())
						.addRow(content);
				this.societies.add(societies[i]);
			}

			this.graph_label_field.setText(this.graph.getLabel());
		} else
			this.graph_label_field.setText("");

		this.graph_path_field.setText("");
		this.file_chooser = new JFileChooser();
	}

	/**
	 * Executed when the remove_button is pressed. Generated by NetBeans IDE
	 * 3.6.
	 */
	private void delete_societies_buttonActionPerformed(
			java.awt.event.ActionEvent evt) {
		DefaultTableModel table_model = (DefaultTableModel) this.societies_table
				.getModel();

		int selected_rows_count = this.societies_table.getSelectedRowCount();
		int[] selected_rows = this.societies_table.getSelectedRows();
		for (int i = 0; i < selected_rows_count; i++) {
			String society_id = (String) this.societies_table.getValueAt(
					selected_rows[i], 0);
			for (int j = 0; j < this.societies.size(); j++)
				if (this.societies.get(j).getObjectId().equals(society_id)) {
					this.societies.remove(j);
					break;
				}

			table_model.removeRow(selected_rows[i]);

			for (int j = i + 1; j < selected_rows_count; j++)
				selected_rows[j]--;
		}
	}

	/** Executed when the edit_button is pressed. Generated by NetBeans IDE 3.6. */
	private void edit_society_buttonActionPerformed(
			java.awt.event.ActionEvent evt) {
		if (this.societies_table.getSelectedRowCount() == 1) {
			int selected_row = this.societies_table.getSelectedRow();
			String society_id = (String) this.societies_table.getValueAt(
					selected_row, 0);

			for (int i = 0; i < this.societies.size(); i++)
				if (this.societies.get(i).getObjectId().equals(society_id)) {
					this.society_gui = new SocietyGUI(this.owner,
							this.societies.get(i), this.graph);
					this.society_gui.setVisible(true);

					Society obtained_society = this.society_gui.getSociety();
					if (obtained_society != null) {
						DefaultTableModel table_model = (DefaultTableModel) this.societies_table
								.getModel();

						table_model.removeRow(selected_row);
						this.societies.remove(this.societies.get(i));

						String society_type = "open";
						if (obtained_society instanceof ClosedSociety)
							society_type = "closed";

						String[] content = { obtained_society.getObjectId(),
								society_type };
						table_model.addRow(content);

						this.societies.add(obtained_society);
					}

					break;
				}
		} else if (this.societies_table.getSelectedRowCount() > 1) {
			JOptionPane.showMessageDialog(this,
					"Please select only one society.", "Selection error",
					JOptionPane.ERROR_MESSAGE);
		}
	}

	/**
	 * Executed when the add_opean_society button is pressed. Generated by
	 * NetBeans IDE 3.6.
	 */
	private void add_open_society_buttonActionPerformed(
			java.awt.event.ActionEvent evt) {
		if (this.graph == null) {
			JOptionPane.showMessageDialog(this.owner,
					"Please, add a graph to the environment first.",
					"Environment error", JOptionPane.ERROR_MESSAGE);
			return;
		}

		OpenSociety added_society = new OpenSociety("", new SeasonalAgent[0]);

		added_society.setObjectId(added_society.getClass().getName() + "@"
				+ Integer.toHexString(added_society.hashCode()) + "#"
				+ Long.toHexString(System.currentTimeMillis()));

		this.society_gui = new SocietyGUI(this.owner, added_society, this.graph);
		this.society_gui.setVisible(true);

		Society obtained_society = this.society_gui.getSociety();
		if (obtained_society != null) {
			String[] content = { obtained_society.getObjectId(), "open" };
			((DefaultTableModel) this.societies_table.getModel())
					.addRow(content);
			this.societies.add(obtained_society);
		}
	}

	/**
	 * Executed when the add_closed_society button is pressed. Generated by
	 * NetBeans IDE 3.6.
	 */
	private void add_closed_society_buttonActionPerformed(
			java.awt.event.ActionEvent evt) {
		if (this.graph == null) {
			JOptionPane.showMessageDialog(this.owner,
					"Please, add a graph to the environment first.",
					"Environment error", JOptionPane.ERROR_MESSAGE);
			return;
		}

		ClosedSociety added_society = new ClosedSociety("",
				new PerpetualAgent[0]);

		added_society.setObjectId(added_society.getClass().getName() + "@"
				+ Integer.toHexString(added_society.hashCode()) + "#"
				+ Long.toHexString(System.currentTimeMillis()));

		this.society_gui = new SocietyGUI(this.owner, added_society, this.graph);
		this.society_gui.setVisible(true);

		Society obtained_society = this.society_gui.getSociety();
		if (obtained_society != null) {
			String[] content = { obtained_society.getObjectId(), "closed" };
			((DefaultTableModel) this.societies_table.getModel())
					.addRow(content);
			this.societies.add(obtained_society);
		}
	}

	private void load_graph_buttonActionPerformed(java.awt.event.ActionEvent evt) {
		if (this.graph_label_field.getText().trim().length() > 0)
			if (JOptionPane.showConfirmDialog(this.owner,
					"Current graph will be discarded. Are you shure?",
					"Confirm action", JOptionPane.YES_NO_OPTION,
					JOptionPane.QUESTION_MESSAGE) == JOptionPane.NO_OPTION)
				return;

		this.file_chooser.setDialogTitle("Load graph");
		this.file_chooser.setDialogType(JFileChooser.OPEN_DIALOG);
		this.file_chooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
		if (this.file_chooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
			String path = this.file_chooser.getSelectedFile().getPath();

			Graph obtained_graph = null;
			try {
				obtained_graph = GraphTranslator.getGraphFromSimPatrolXML(path)[0];
			} catch (ParserConfigurationException e) {
			} catch (SAXException e) {
			} catch (IOException e) {
			} catch (VertexNotFoundException e) {
			} catch (EdgeNotFoundException e) {
			}

			if (obtained_graph == null)
				try {
					obtained_graph = GraphTranslator
							.getGraphFromMachadoModel(path);
				} catch (Exception e) {
					obtained_graph = null;
				}

			if (obtained_graph == null)
				JOptionPane
						.showMessageDialog(
								this.owner,
								"Errors happened while trying to obtain a graph from the given file. No changes were done.",
								"Graph Error", JOptionPane.ERROR_MESSAGE);
			else {
				this.graph = obtained_graph;
				this.graph_label_field.setText(this.graph.getLabel());
				this.graph_path_field.setText(path);
			}
		}
	}

	/** Returns the environment configured by this panel. */
	public Environment getEnvironment() {
		if (this.graph != null) {
			Society[] societies_array = new Society[this.societies.size()];
			int i = 0;
			for (Society society : this.societies) {
				societies_array[i] = society;
				i++;
			}

			return new Environment(this.graph, societies_array);
		}

		JOptionPane
				.showMessageDialog(
						this.owner,
						"An environment could not be obtained, once there was not a proper graph to add to it.",
						"Environment error", JOptionPane.ERROR_MESSAGE);
		return null;
	}

	/* Attributes. */
	// added manually
	private JFrame owner;
	private SocietyGUI society_gui;
	private Graph graph;
	private LinkedList<Society> societies;
	private JFileChooser file_chooser;

	// added by Eclipse
	private static final long serialVersionUID = 1711373944848644861L;

	// added by Eclipse
	// Variables declaration - do not modify
	private javax.swing.JButton add_closed_society_button;
	private javax.swing.JButton add_open_society_button;
	private javax.swing.JButton delete_societies_button;
	private javax.swing.JButton edit_society_button;
	private javax.swing.JPanel graph_buttons_panel;
	private javax.swing.JPanel graph_internal_panel_1;
	private javax.swing.JPanel graph_internal_panel_2;
	private javax.swing.JPanel graph_internal_panel_3;
	private javax.swing.JPanel graph_panel;
	private javax.swing.JTextField graph_path_field;
	private javax.swing.JTextField graph_label_field;
	private javax.swing.JLabel graph_path_label;
	private javax.swing.JLabel graph_label_label;
	private javax.swing.JButton load_graph_button;
	private javax.swing.JPanel societies_buttons_internal_panel;
	private javax.swing.JPanel societies_buttons_panel;
	private javax.swing.JPanel societies_panel;
	private javax.swing.JScrollPane societies_scroll;
	private javax.swing.JTable societies_table;
	// End of variables declaration
}
