package graph;

import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.table.DefaultTableModel;

import etpd.EventTimeProbabilityDistributionGUI;

import model.etpd.EmpiricalEventTimeProbabilityDistribution;
import model.etpd.EventTimeProbabilityDistribution;
import model.etpd.NormalEventTimeProbabilityDistribution;
import model.etpd.SpecificEventTimeProbabilityDistribution;
import model.etpd.UniformEventTimeProbabilityDistribution;
import model.graph.DynamicEdge;
import model.graph.DynamicNode;
import model.graph.Edge;
import model.graph.Graph;
import model.graph.Node;

import java.awt.Dimension;
import java.awt.FlowLayout;
import java.util.NoSuchElementException;
import java.util.StringTokenizer;

public class NodeJPanel extends JPanel{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -7193140398518522829L;
	private Graph graph;
	private int selected_node;
	private Node[] node;
	private Edge[] edges;
	private JDialog owner;
	
	private EventTimeProbabilityDistributionGUI death_gui;
	private boolean change_from_ui;
	
	
	private JPanel node_panel;
	private JScrollPane node_scroll;
	private JTable node_table;
	private JPanel buttonsnode_panel;
	private JButton addnode_button;
	private JButton removenode_button;
	private JPanel genprop_panel;
	private JPanel basicprop_panel;
	private JPanel id_panel;
	private JPanel label_panel;
	private JPanel priority_panel;
	private JPanel fuel_panel;
	private JPanel dynprop_panel;
	private JLabel id_label;
	private JLabel label_label;
	private JLabel priority_label;
	private JLabel fuel_label;
	private JTextField id_field;
	private JTextField label_field;
	private JTextField priority_field;
	private JComboBox fuel_field;
	private JPanel visible_panel;
	private JLabel visible_label;
	private JCheckBox visible_check;
	
	private JPanel dynamic_panel;
	private JLabel dynamic_label;
	private JPanel enable_panel;
	private JLabel enable_label;
	private JPanel enabtpd_panel;
	private JLabel enabtpd_label;
	private JPanel disabltpd_panel;
	private JLabel disabltpd_label;
	private JCheckBox dynamic_check;
	private JCheckBox enable_check;
	private JComboBox enabtpd_combo;
	private JComboBox disabltpd_combo;
	
	
	private JPanel edge_panel;
	private JScrollPane edges_scroll;
	private JTable edge_table;
	private JPanel buttonsedge_panel;
	private JButton addedge_button;
	private JButton modifyedge_button;
	private JButton removeedge_button;



	public NodeJPanel(JDialog owner, Graph graph) {
		initComponents();
		initComponents2(owner, graph);
	}
	
	private void initComponents() {
		node_panel = new javax.swing.JPanel();
        node_scroll = new javax.swing.JScrollPane();
        node_table = new javax.swing.JTable();
        buttonsnode_panel = new javax.swing.JPanel();
        addnode_button = new javax.swing.JButton();
        removenode_button = new javax.swing.JButton();
        
        genprop_panel = new javax.swing.JPanel();
        basicprop_panel = new javax.swing.JPanel();
        id_panel = new javax.swing.JPanel();
        id_label = new javax.swing.JLabel();
        id_field = new javax.swing.JTextField();
        label_panel = new javax.swing.JPanel();
        label_label = new javax.swing.JLabel();
        label_field = new javax.swing.JTextField();
        priority_panel = new javax.swing.JPanel();
        priority_label = new javax.swing.JLabel();
        priority_field = new javax.swing.JTextField();
        fuel_panel = new javax.swing.JPanel();
        fuel_label = new javax.swing.JLabel();
        fuel_field = new javax.swing.JComboBox();
    	visible_panel = new javax.swing.JPanel();
    	visible_label = new javax.swing.JLabel();
    	visible_check = new javax.swing.JCheckBox();
        
        
        dynprop_panel = new javax.swing.JPanel();
        dynamic_panel = new javax.swing.JPanel();
        dynamic_label = new javax.swing.JLabel();
        dynamic_check = new javax.swing.JCheckBox();
        enable_panel = new javax.swing.JPanel();
        enable_label = new javax.swing.JLabel();
        enable_check = new javax.swing.JCheckBox();
        enabtpd_panel = new javax.swing.JPanel();
        enabtpd_label = new javax.swing.JLabel();
        enabtpd_combo = new javax.swing.JComboBox();
        disabltpd_panel = new javax.swing.JPanel();
        disabltpd_label = new javax.swing.JLabel();
        disabltpd_combo = new javax.swing.JComboBox();
        
        edge_panel = new javax.swing.JPanel();
        edges_scroll = new javax.swing.JScrollPane();
        edge_table = new javax.swing.JTable();
        buttonsedge_panel = new javax.swing.JPanel();
        addedge_button = new javax.swing.JButton();
        modifyedge_button = new javax.swing.JButton();
        removeedge_button = new javax.swing.JButton();
        
        
        setLayout(new BoxLayout(this, BoxLayout.X_AXIS));
        setPreferredSize(new Dimension(600,250));
        setMaximumSize(new Dimension(600,300));

        
        /** 
         * vertices table
         * 
         */
        node_panel.setLayout(new BoxLayout(node_panel, BoxLayout.X_AXIS));
        node_panel.setBorder(new javax.swing.border.TitledBorder("Nodes"));
        node_panel.setPreferredSize(new Dimension(240, 250));
        node_panel.setMinimumSize(new Dimension(240, 250));
        
        node_table.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Nodes"
            }
        ) {
			private static final long serialVersionUID = -466062034300696031L;
			Class[] types = new Class [] {
                java.lang.String.class
            };
            boolean[] canEdit = new boolean [] {
                false
            };

            @SuppressWarnings("unchecked")
			public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        node_table.addMouseListener(new java.awt.event.MouseListener() {
			public void mouseReleased(MouseEvent e) {
				node_selected();
				
			}

			public void mouseClicked(MouseEvent e) {
				// TODO Auto-generated method stub
				
			}

			public void mouseEntered(MouseEvent e) {
				// TODO Auto-generated method stub
				
			}

			public void mouseExited(MouseEvent e) {
				// TODO Auto-generated method stub
				
			}

			public void mousePressed(MouseEvent e) {
				// TODO Auto-generated method stub
				
			}


        });
        node_scroll.setViewportView(node_table);
        //node_scroll.setMinimumSize(new Dimension(150, 250));
        node_scroll.setPreferredSize(new Dimension(150, 250));
        node_panel.add(node_scroll);
        
	    buttonsnode_panel.setLayout(new FlowLayout());
	    buttonsnode_panel.setPreferredSize(new Dimension(100, 150));
	    buttonsnode_panel.setMaximumSize(new Dimension(110, 150)); 
        
        addnode_button.setText("Add");
        addnode_button.setHorizontalAlignment(SwingConstants.CENTER);
        addnode_button.setHorizontalTextPosition(SwingConstants.CENTER);
        addnode_button.setMaximumSize(null);
        addnode_button.setPreferredSize(new Dimension(90, 25));
        addnode_button.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(ActionEvent e) {
				addnode_buttonactionPerformed(e);
				
			}

        });
        buttonsnode_panel.add(addnode_button);

        removenode_button.setText("Delete");
        removenode_button.setHorizontalAlignment(SwingConstants.CENTER);
        removenode_button.setHorizontalTextPosition(SwingConstants.CENTER);
        removenode_button.setMaximumSize(null);
        removenode_button.setPreferredSize(new Dimension(90, 25));
        removenode_button.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(ActionEvent e) {
				removenode_buttonactionPerformed(e);
				
			}

        });
        buttonsnode_panel.add(removenode_button);
        
        node_panel.add(buttonsnode_panel);
        
        add(node_panel);

        
        /** 
         * Properties panel
         */
        
        genprop_panel.setLayout(new BoxLayout(genprop_panel, BoxLayout.Y_AXIS));
        
        // Basic properties
        basicprop_panel.setLayout(new BoxLayout(basicprop_panel, BoxLayout.Y_AXIS));
        basicprop_panel.setBorder(new javax.swing.border.TitledBorder("Basic Properties"));
        basicprop_panel.setMaximumSize(new Dimension(250,160));
        //basicprop_panel.setPreferredSize(new Dimension(250,160));
        
        id_panel.setLayout(new BoxLayout(id_panel, BoxLayout.X_AXIS));
        id_label.setText("id");
        id_label.setPreferredSize(new Dimension(70, 25));
        id_panel.add(id_label);
        id_field.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
            	id_fieldKeyReleased(evt);
            }

        });
        id_panel.add(id_field);
        basicprop_panel.add(id_panel);
        
        label_panel.setLayout(new BoxLayout(label_panel, BoxLayout.X_AXIS));
        label_label.setText("Label");
        label_label.setPreferredSize(new Dimension(70, 25));
        label_panel.add(label_label);
        label_field.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                label_fieldKeyReleased(evt);
            }

        });
        label_panel.add(label_field);
        basicprop_panel.add(label_panel);
        
        priority_panel.setLayout(new BoxLayout(priority_panel, BoxLayout.X_AXIS));
        priority_label.setText("Priority");
        priority_label.setPreferredSize(new Dimension(70, 25));
        priority_panel.add(priority_label);
        priority_field.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                priority_fieldKeyReleased(evt);
            }

        });
        priority_panel.add(priority_field);
        basicprop_panel.add(priority_panel);
        
        
        fuel_panel.setLayout(new BoxLayout(fuel_panel, BoxLayout.X_AXIS));
        fuel_label.setText("fuel");
        fuel_label.setPreferredSize(new Dimension(70, 25));
        fuel_panel.add(fuel_label);
        fuel_field.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "No", "Yes" }));
        fuel_field.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(ActionEvent e) {
				fuel_fieldactionPerformed(e);
				
			}
        });
        fuel_panel.add(fuel_field);
        basicprop_panel.add(fuel_panel);
        
        visible_panel.setLayout(new FlowLayout());
        visible_panel.setPreferredSize(new Dimension(150, 30));
        visible_label.setText("visible ?");
        visible_label.setPreferredSize(new Dimension(120,30));
        visible_panel.add(visible_label);
        visible_check.setSelected(false);
        visible_check.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(ActionEvent e) {
				visible_checkactionPerformed(e);
				
			}

        });
        visible_panel.add(visible_check);
        basicprop_panel.add(visible_panel);
        
        genprop_panel.add(basicprop_panel);
        
        
        // Dynamicity properties
        dynprop_panel.setLayout(new BoxLayout(dynprop_panel, BoxLayout.Y_AXIS));
        dynprop_panel.setBorder(new javax.swing.border.TitledBorder("Dynamicity Properties"));
        dynprop_panel.setMaximumSize(new Dimension(250, 150));
        //dynprop_panel.setPreferredSize(new Dimension(250, 140));
        dynprop_panel.setMinimumSize(new Dimension(200, 100));
        
        dynamic_panel.setLayout(new FlowLayout());
        dynamic_panel.setMinimumSize(new Dimension(150,25));
        dynamic_label.setText("dynamic ?");
        dynamic_label.setHorizontalTextPosition(SwingConstants.LEFT);
        dynamic_label.setPreferredSize(new Dimension(120,25));
        dynamic_panel.add(dynamic_label);
        dynamic_check.setSelected(false);
        dynamic_check.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(ActionEvent e) {
				dynamic_checkactionPerformed(e);
				
			}

        });
        dynamic_panel.add(dynamic_check);
        
        
        enable_panel.setLayout(new FlowLayout());
        enable_label.setText("Enabled ?");
        enable_label.setPreferredSize(new Dimension(120, 25));
        enable_panel.add(enable_label);
        enable_check.setSelected(true);
        enable_check.setEnabled(false);
        enable_check.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(ActionEvent e) {
				enable_checkactionPerformed(e);
				
			}

        });
        enable_panel.add(enable_check);
        dynprop_panel.add(dynamic_panel, dynamic_panel.getName());
        dynprop_panel.add(enable_panel);
        
        enabtpd_panel.setLayout(new BoxLayout(enabtpd_panel, BoxLayout.X_AXIS));
        enabtpd_label.setText("Enabling law");
        enabtpd_label.setPreferredSize(new Dimension(100, 25));
        enabtpd_panel.add(enabtpd_label);
        enabtpd_combo.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "None", "Uniform", "Empirical", "Normal", "Specific time" }));
        enabtpd_combo.setPreferredSize(new Dimension(95, 25));
        enabtpd_combo.setEnabled(false);
        enabtpd_combo.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(ActionEvent e) {
				enabtpd_comboactionPerformed(e);
				
			}

        });
        enabtpd_panel.add(enabtpd_combo);
        dynprop_panel.add(enabtpd_panel);
        
        
        disabltpd_panel.setLayout(new BoxLayout(disabltpd_panel, BoxLayout.X_AXIS));
        disabltpd_label.setText("Disabling law");
        disabltpd_label.setPreferredSize(new Dimension(100, 25));
        disabltpd_panel.add(disabltpd_label);
        disabltpd_combo.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "None", "Uniform", "Empirical", "Normal", "Specific time" }));
        disabltpd_combo.setPreferredSize(new Dimension(95, 25));
        disabltpd_combo.setEnabled(false);
        disabltpd_combo.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(ActionEvent e) {
				disabltpd_comboactionPerformed(e);
				
			}

        });
        disabltpd_panel.add(disabltpd_combo);
        dynprop_panel.add(disabltpd_panel);
        
        genprop_panel.add(dynprop_panel);
        
        add(genprop_panel);
        
        
        /**
         * Edges visualization
         */
        
        edge_panel.setLayout(new BoxLayout(edge_panel, BoxLayout.X_AXIS));
        edge_panel.setBorder(new javax.swing.border.TitledBorder("Related Edges"));
        edge_panel.setPreferredSize(new Dimension(240, 250));
        edge_panel.setMinimumSize(new Dimension(240, 250));
        
        edge_table.setModel(new javax.swing.table.DefaultTableModel(
	            new Object [][] {
	
	            },
	            new String [] {
	                "Edges"
	            }
	        ) {
				private static final long serialVersionUID = -2554261176760234890L;
				Class[] types = new Class [] {
	                java.lang.String.class
	            };
	            boolean[] canEdit = new boolean [] {
	                false
	            };
	
	            @SuppressWarnings("unchecked")
				public Class getColumnClass(int columnIndex) {
	                return types [columnIndex];
	            }
	
	            public boolean isCellEditable(int rowIndex, int columnIndex) {
	                return canEdit [columnIndex];
	            }
	        });
	    edges_scroll.setViewportView(edge_table);
	    edges_scroll.setPreferredSize(new Dimension(140, 250));
	    edges_scroll.setMinimumSize(new Dimension(140, 250));
	    edge_panel.add(edges_scroll);
	    
	    buttonsedge_panel.setLayout(new FlowLayout());
	    buttonsedge_panel.setPreferredSize(new Dimension(100, 150));
	    buttonsedge_panel.setMaximumSize(new Dimension(110, 150)); 
        
        addedge_button.setText("Add");
        addedge_button.setHorizontalAlignment(SwingConstants.CENTER);
        addedge_button.setHorizontalTextPosition(SwingConstants.CENTER);
        addedge_button.setMaximumSize(null);
        addedge_button.setPreferredSize(new Dimension(90, 25));
        addedge_button.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(ActionEvent e) {
				addedge_buttonactionPerformed(e);
				
			}

        });
        buttonsedge_panel.add(addedge_button);
        
        modifyedge_button.setText("Modify");
        modifyedge_button.setHorizontalAlignment(SwingConstants.CENTER);
        modifyedge_button.setHorizontalTextPosition(SwingConstants.CENTER);
        modifyedge_button.setMaximumSize(null);
        modifyedge_button.setPreferredSize(new Dimension(90, 25));
        modifyedge_button.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(ActionEvent e) {
				modifyedge_buttonactionPerformed(e);
				
			}

        });
        buttonsedge_panel.add(modifyedge_button);

        removeedge_button.setText("Delete");
        removeedge_button.setHorizontalAlignment(SwingConstants.CENTER);
        removeedge_button.setHorizontalTextPosition(SwingConstants.CENTER);
        removeedge_button.setMaximumSize(null);
        removeedge_button.setPreferredSize(new Dimension(90, 25));
        removeedge_button.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(ActionEvent e) {
				removeedge_buttonactionPerformed(e);
				
			}

        });
        buttonsedge_panel.add(removeedge_button);
        
        edge_panel.add(buttonsedge_panel);
        
        add(edge_panel);
        
        
	}


	/** Complements the initiation of the components of the GUI.
     * 
     *  @param owner The owner of the panel
     *  @param graph The graph of the patrolling simulation. 
     **/
	private void initComponents2(JDialog owner, Graph graph) {
		this.owner = owner;
		this.graph = graph;
    	this.node = graph.getNodes();
    	
    	populate_node();
    	
    	change_from_ui = true;
				
	}
	
	
	/** Is called to refresh the graph
     * 
     *  @param graph The graph of the patrolling simulation. 
     **/
	public void refresh_graph(Graph graph){
		initComponents2(owner, graph);
	}
	
	
	/** * * * *
	 * These functions are called to fill in the different fields
	 * 
	 * * * * * */
	
	/**
	 * This function populates the node table from the node list
	 */
	private void populate_node(){
		
		int nrow = ((DefaultTableModel) this.node_table.getModel()).getRowCount();
    	for(int i=0; i < nrow ; i++)
    		((DefaultTableModel) this.node_table.getModel()).removeRow(0);
    	
		
    	for(int i = 0; i < node.length; i++) {
    		String[] content = { node[i].getObjectId() };
    		((DefaultTableModel) this.node_table.getModel()).addRow(content);
    	}
	}

	/**
	 * This function calls the next one using the selected 
	 * node in the node table as argument
	 */
	private void node_selected(){
		node_selected(this.node_table.getSelectedRow());
	}
	
	/**
	 * this function fills in the different fields from the selected node
	 * it enables the dynamicity if the node is dynamic
	 * 
	 * @param select : index of the selected node in the node list
	 */
	private void node_selected(int select){
		change_from_ui = false;
		selected_node = select;
		
		id_field.setText(node[selected_node].getObjectId());
		label_field.setText(node[selected_node].getLabel());
		priority_field.setText(String.valueOf(node[selected_node].getPriority()));
		if(node[selected_node].isFuel()==true) fuel_field.setSelectedIndex(1);
		else fuel_field.setSelectedIndex(0);
		visible_check.setSelected(node[selected_node].isVisible());
		
		if(node[selected_node] instanceof DynamicNode){
			dynamic_check.setSelected(true);
			enable_check.setSelected(((DynamicNode) node[selected_node]).isEnabled());
			enable_check.setEnabled(true);
			
			set_birth_tpd((DynamicNode) node[selected_node]);
			enabtpd_combo.setEnabled(true);
			
			set_death_tpd((DynamicNode) node[selected_node]);		
			disabltpd_combo.setEnabled(true);				
		}	
		else {
			dynamic_check.setSelected(false);
			enable_check.setSelected(false);
			enable_check.setEnabled(false);
			
			enabtpd_combo.setSelectedIndex(0);
			enabtpd_combo.setEnabled(false);
			
			disabltpd_combo.setSelectedIndex(0);		
			disabltpd_combo.setEnabled(false);				
		}
		
		populate_edges(node[selected_node]);
		
		this.revalidate();
		this.repaint();
		
		change_from_ui = true;
		
		
	}

	
	/**
	 * Sets the combobox for the birth time probability distribution
	 * 
	 * @param selected : the node selected
	 */
	private void set_birth_tpd(DynamicNode selected){
		EventTimeProbabilityDistribution chosen_birth_tpd = selected.getEnablingTPD();		
		if(chosen_birth_tpd == null) {
			this.enabtpd_combo.setSelectedIndex(0);
		}
		else if(chosen_birth_tpd instanceof UniformEventTimeProbabilityDistribution) {
			this.enabtpd_combo.setSelectedIndex(1);
		}
		else if(chosen_birth_tpd instanceof EmpiricalEventTimeProbabilityDistribution) {
			this.enabtpd_combo.setSelectedIndex(2);
		}
		else if(chosen_birth_tpd instanceof NormalEventTimeProbabilityDistribution) {
			this.enabtpd_combo.setSelectedIndex(3);
		}
		else
			this.enabtpd_combo.setSelectedIndex(4);
	}

	/**
	 * Sets the combobox for the death time probability distribution
	 * 
	 * @param selected : the node selected
	 */
	private void set_death_tpd(DynamicNode selected){
		EventTimeProbabilityDistribution chosen_death_tpd = selected.getDisablingTPD();		
		if(chosen_death_tpd == null) {
			this.disabltpd_combo.setSelectedIndex(0);
		}
		else if(chosen_death_tpd instanceof UniformEventTimeProbabilityDistribution) {
			this.disabltpd_combo.setSelectedIndex(1);
		}
		else if(chosen_death_tpd instanceof EmpiricalEventTimeProbabilityDistribution) {
			this.disabltpd_combo.setSelectedIndex(2);
		}
		else if(chosen_death_tpd instanceof NormalEventTimeProbabilityDistribution) {
			this.disabltpd_combo.setSelectedIndex(3);
		}
		else
			this.disabltpd_combo.setSelectedIndex(4);
	}
	
	/**
	 * This function populates the edge table from the selected node
	 * 
	 * @param selected : the node selected
	 */
	private void populate_edges(Node selected){
    	edges = selected.getEdges();
    	
    	int nrow = ((DefaultTableModel) this.edge_table.getModel()).getRowCount();
    	for(int i=0; i < nrow ; i++)
    		((DefaultTableModel) this.edge_table.getModel()).removeRow(0);
    		
    	for(int i = 0; i < edges.length; i++) {
    		String[] content = new String[1];
    		if(selected.isSourceOf(edges[i]))
    			 content[0] = edges[i].getObjectId() + ",<" + selected.getObjectId() + ", " + edges[i].getOtherNode(selected).getObjectId() + ">";
    		else 
    			content[0] = edges[i].getObjectId() + ",<" + edges[i].getOtherNode(selected).getObjectId() + ", " + selected.getObjectId() + ">";
    		((DefaultTableModel) this.edge_table.getModel()).addRow(content);
    	}
	}
	
	
	
	/** * * * * *
	 * The next functions are the different event listeners used here
	 * 
	 ** * * * * */
	
	/**
	 * Removes a node from the list and refreshes the GUI
	 * 
	 * @param e
	 */
	protected void removenode_buttonactionPerformed(ActionEvent e) {
		int vnum = node.length;
		Node[] new_node = new Node[vnum - 1];
		for(int i = 0; i < selected_node; i ++)
			new_node[i] = node[i];
		for(int i = selected_node + 1; i < vnum; i ++)
			new_node[i-1] = node[i];
		
		node = new_node;
		populate_node();
		
		node_selected(0);
	}

	/**
	 * Add a node to the list and refreshes the GUI
	 * 
	 * @param e
	 */
	protected void addnode_buttonactionPerformed(ActionEvent e) {
		int vnum = node.length;
		Node[] new_node = new Node[vnum + 1];
		for(int i = 0; i < vnum; i ++)
			new_node[i] = node[i];
		new_node[vnum] = new Node("n" + (vnum+1));
		new_node[vnum].setObjectId("n" + (vnum+1) + "@" + Long.toHexString(System.currentTimeMillis()));
		
		node = new_node;
		populate_node();
		
		
	}


	/**
	 *  Removes an edge
	 * 
	 * @param e
	 */
	protected void removeedge_buttonactionPerformed(ActionEvent evt) {
		Edge del_edge = node[selected_node].getEdges()[edge_table.getSelectedRow()];
		synchronized(node[selected_node]){
			del_edge.getOtherNode(node[selected_node]).RemoveEdge(del_edge.getObjectId());
			node[selected_node].RemoveEdge(del_edge.getObjectId());
		}
		
		node_selected(selected_node);
		
	}

	/**
	 *  Opens the edge GUI to modify the selected edge
	 * 
	 * @param e
	 */
	protected void modifyedge_buttonactionPerformed(ActionEvent evt) {
		Edge del_edge = node[selected_node].getEdges()[edge_table.getSelectedRow()];
		((GraphGUI)owner).mod_edge(del_edge.getObjectId());
		
	}

	/**
	 *  Adds an edge and opens the edge GUI
	 * 
	 * @param e
	 */
	
	
	protected void addedge_buttonactionPerformed(ActionEvent evt) {
		((GraphGUI)owner).mod_edge("");
		
	}

	/**
	 * Opens the EventTimeProbabilityDistributionGUI for the death TPD
	 * 
	 * @param evt
	 */
	protected void disabltpd_comboactionPerformed(ActionEvent evt) {
		if(node[selected_node] instanceof DynamicNode && change_from_ui){
			
			if(disabltpd_combo.getSelectedIndex() == 0){
				((DynamicNode)node[selected_node]).setDisablingTPD(null);
				return;
			}
			
	    	else if(this.disabltpd_combo.getSelectedIndex() == 1){
	    		if(((DynamicNode)node[selected_node]).getDisablingTPD() instanceof UniformEventTimeProbabilityDistribution)
	    			this.death_gui = new EventTimeProbabilityDistributionGUI(this.owner, ((DynamicNode)node[selected_node]).getDisablingTPD());
	    		else
	    			this.death_gui = new EventTimeProbabilityDistributionGUI(this.owner, new UniformEventTimeProbabilityDistribution((int) System.currentTimeMillis(), 0));
	    	}
			
	    	else if(this.disabltpd_combo.getSelectedIndex() == 2) {
	    		if(((DynamicNode)node[selected_node]).getDisablingTPD() instanceof EmpiricalEventTimeProbabilityDistribution)
	    			this.death_gui = new EventTimeProbabilityDistributionGUI(this.owner, ((DynamicNode)node[selected_node]).getDisablingTPD());
	    		else{
	    			double[] distribution = {1};
	    			this.death_gui = new EventTimeProbabilityDistributionGUI(this.owner, new EmpiricalEventTimeProbabilityDistribution((int) System.currentTimeMillis(), distribution));
	    		}
	    	}
			
	    	else if(this.disabltpd_combo.getSelectedIndex() == 3){
	    		if(((DynamicNode)node[selected_node]).getDisablingTPD() instanceof NormalEventTimeProbabilityDistribution)
	    			this.death_gui = new EventTimeProbabilityDistributionGUI(this.owner, ((DynamicNode)node[selected_node]).getDisablingTPD());
	    		else
	    			this.death_gui = new EventTimeProbabilityDistributionGUI(this.owner, new NormalEventTimeProbabilityDistribution((int) System.currentTimeMillis(), 0, 0));
	    	}
	    	
	    	else {
	    		if(((DynamicNode)node[selected_node]).getDisablingTPD() instanceof SpecificEventTimeProbabilityDistribution)
	    			this.death_gui = new EventTimeProbabilityDistributionGUI(this.owner, ((DynamicNode)node[selected_node]).getDisablingTPD());
	    		else
	    			this.death_gui = new EventTimeProbabilityDistributionGUI(this.owner, new SpecificEventTimeProbabilityDistribution((int) System.currentTimeMillis(), 0, 0));
	    	}
	    			
	    		
	    	this.death_gui.setVisible(true);	
	    	 	
	    	EventTimeProbabilityDistribution chosen_death_tpd = this.death_gui.getETPD();  
	    	if(chosen_death_tpd != null){
	    		((DynamicNode)node[selected_node]).setDisablingTPD(chosen_death_tpd);
	    	}
    		
	    	if(((DynamicNode)node[selected_node]).getDisablingTPD() == null) 
	    		disabltpd_combo.setSelectedIndex(0);
	    	else if(((DynamicNode)node[selected_node]).getDisablingTPD() instanceof UniformEventTimeProbabilityDistribution)
	    		disabltpd_combo.setSelectedIndex(1);
    		else if(((DynamicNode)node[selected_node]).getDisablingTPD() instanceof EmpiricalEventTimeProbabilityDistribution)
    			disabltpd_combo.setSelectedIndex(2);
    		else if(((DynamicNode)node[selected_node]).getDisablingTPD() instanceof NormalEventTimeProbabilityDistribution)
    			disabltpd_combo.setSelectedIndex(3);
    		else disabltpd_combo.setSelectedIndex(4);

		}
		
	}

	/**
	 * Opens the EventTimeProbabilityDistributionGUI for the birth TPD
	 * 
	 * @param evt
	 */
	protected void enabtpd_comboactionPerformed(ActionEvent evt) {	
		if(node[selected_node] instanceof DynamicNode && change_from_ui){
			
			if(enabtpd_combo.getSelectedIndex() == 0){
				((DynamicNode)node[selected_node]).setEnablingTPD(null);
				return;
			}
			
	    	else if(this.enabtpd_combo.getSelectedIndex() == 1){
	    		if(((DynamicNode)node[selected_node]).getEnablingTPD() instanceof UniformEventTimeProbabilityDistribution)
	    			this.death_gui = new EventTimeProbabilityDistributionGUI(this.owner, ((DynamicNode)node[selected_node]).getEnablingTPD());
	    		else
	    			this.death_gui = new EventTimeProbabilityDistributionGUI(this.owner, new UniformEventTimeProbabilityDistribution((int) System.currentTimeMillis(), 0));
	    	}
			
	    	else if(this.enabtpd_combo.getSelectedIndex() == 2) {
	    		if(((DynamicNode)node[selected_node]).getEnablingTPD() instanceof EmpiricalEventTimeProbabilityDistribution)
	    			this.death_gui = new EventTimeProbabilityDistributionGUI(this.owner, ((DynamicNode)node[selected_node]).getEnablingTPD());
	    		else{
	    			double[] distribution = {1};
	    			this.death_gui = new EventTimeProbabilityDistributionGUI(this.owner, new EmpiricalEventTimeProbabilityDistribution((int) System.currentTimeMillis(), distribution));
	    		}
	    	}
			
	    	else if(this.enabtpd_combo.getSelectedIndex() == 3){
	    		if(((DynamicNode)node[selected_node]).getEnablingTPD() instanceof NormalEventTimeProbabilityDistribution)
	    			this.death_gui = new EventTimeProbabilityDistributionGUI(this.owner, ((DynamicNode)node[selected_node]).getEnablingTPD());
	    		else
	    			this.death_gui = new EventTimeProbabilityDistributionGUI(this.owner, new NormalEventTimeProbabilityDistribution((int) System.currentTimeMillis(), 0, 0));
	    	}
	    	
	    	else {
	    		if(((DynamicNode)node[selected_node]).getEnablingTPD() instanceof SpecificEventTimeProbabilityDistribution)
	    			this.death_gui = new EventTimeProbabilityDistributionGUI(this.owner, ((DynamicNode)node[selected_node]).getEnablingTPD());
	    		else
	    			this.death_gui = new EventTimeProbabilityDistributionGUI(this.owner, new SpecificEventTimeProbabilityDistribution((int) System.currentTimeMillis(), 0, 0));
	    	}
	    			
	    		
	    	this.death_gui.setVisible(true);	
	    	 	
	    	EventTimeProbabilityDistribution chosen_death_tpd = this.death_gui.getETPD();  
	    	if(chosen_death_tpd != null){
	    		((DynamicNode)node[selected_node]).setEnablingTPD(chosen_death_tpd);
	    	}
    		
	    	if(((DynamicNode)node[selected_node]).getEnablingTPD() == null) 
	    		enabtpd_combo.setSelectedIndex(0);
	    	else if(((DynamicNode)node[selected_node]).getEnablingTPD() instanceof UniformEventTimeProbabilityDistribution)
    			enabtpd_combo.setSelectedIndex(1);
    		else if(((DynamicNode)node[selected_node]).getEnablingTPD() instanceof EmpiricalEventTimeProbabilityDistribution)
    			enabtpd_combo.setSelectedIndex(2);
    		else if(((DynamicNode)node[selected_node]).getEnablingTPD() instanceof NormalEventTimeProbabilityDistribution)
    			enabtpd_combo.setSelectedIndex(3);
    		else enabtpd_combo.setSelectedIndex(4);

		}	
	}

	/**
	 * Manages the Enabled property
	 * 
	 * @param evt
	 */
	protected void enable_checkactionPerformed(ActionEvent evt) {
		if(node[selected_node] != null)
			((DynamicNode)node[selected_node]).setIsEnabled(enable_check.isSelected());
	}

	/**
	 * Manages the dynamic properties, and transforms the node in DynamicNode or back
	 * 
	 * @param evt
	 */
	protected void dynamic_checkactionPerformed(ActionEvent evt) {
		if(node[selected_node] != null){
			if(dynamic_check.isSelected()){
				if(!(node[selected_node] instanceof DynamicNode)){
					DynamicNode answer = new DynamicNode(node[selected_node].getLabel(), null, null, true);
					answer.setObjectId(node[selected_node].getObjectId());
					answer.setPriority(node[selected_node].getPriority());
					answer.setVisibility(node[selected_node].isVisible());
					answer.setFuel(node[selected_node].isFuel());
					
					Edge[] answer_edge = node[selected_node].getEdges();			
					node[selected_node] = answer.getCopy();
					for(int i = 0; i < answer_edge.length; i++)
						node[selected_node].addEdge(answer_edge[i]);
				}
				enable_check.setEnabled(true);
				enable_check.setSelected(((DynamicNode)node[selected_node]).isEnabled());
				enabtpd_combo.setEnabled(true);
				disabltpd_combo.setEnabled(true);
			}
			
			else {
				if(node[selected_node] instanceof DynamicNode){
					Node answer = new Node(node[selected_node].getLabel());
					answer.setObjectId(node[selected_node].getObjectId());
					answer.setPriority(node[selected_node].getPriority());
					answer.setVisibility(node[selected_node].isVisible());
					answer.setFuel(node[selected_node].isFuel());
					
					Edge[] answer_edge = node[selected_node].getEdges();			
					node[selected_node] = answer.getCopy();
					for(int i = 0; i < answer_edge.length; i++)
						node[selected_node].addEdge(answer_edge[i]);
				}
				enable_check.setEnabled(false);
				enable_check.setSelected(((DynamicNode) node[selected_node]).isEnabled());
				enabtpd_combo.setEnabled(false);
				disabltpd_combo.setEnabled(false);				
			}
			
			
		}
		
	}

	
	
	/**
	 * Manages the visible property
	 * 
	 * @param evt
	 */
	protected void visible_checkactionPerformed(ActionEvent e) {
		if(node[selected_node] != null)
			((DynamicNode)node[selected_node]).setVisibility(visible_check.isSelected());
	}
	
	/**
	 * Manages the fuel property
	 * 
	 * @param evt
	 */
	protected void fuel_fieldactionPerformed(ActionEvent evt) {
		if(node[selected_node] != null){
			if(fuel_field.getSelectedIndex() == 0)
				node[selected_node].setFuel(false);
			else node[selected_node].setFuel(true);
		}
		
	}

	/**
	 * Manages the priority property
	 * 
	 * @param evt
	 */
	protected void priority_fieldKeyReleased(KeyEvent evt) {
		if(node[selected_node] != null)
			node[selected_node].setPriority(Integer.valueOf(priority_field.getText()));
		
	}

	/**
	 * Manages the label property
	 * 
	 * @param evt
	 */
	private void label_fieldKeyReleased(KeyEvent evt) {
		if(node[selected_node] != null)
	        node[selected_node].setLabel(label_field.getText());
		
	}
	
	
	/**
	 * Manages the id property
	 * 
	 * @param evt
	 */
	protected void id_fieldKeyReleased(KeyEvent evt) {
		if(node[selected_node] != null){
	        node[selected_node].setObjectId(id_field.getText());
	        populate_node();
	        populate_edges(node[selected_node]);
		}
	}
	
	/**
	 * Returns the actual graph
	 * 
	 * @return the graph
	 */
	public Graph getGraph() {
		return new Graph(graph.getObjectId(), node);
	}


}
