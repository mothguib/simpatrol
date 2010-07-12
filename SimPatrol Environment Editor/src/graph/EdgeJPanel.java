package graph;

import java.awt.Dimension;
import java.awt.FlowLayout;
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
import model.graph.DynamicVertex;
import model.graph.Edge;
import model.graph.Graph;
import model.graph.Vertex;

public class EdgeJPanel extends JPanel{
	
	Graph graph;
	JDialog owner;
	Vertex[] vertex;
	Edge[] edges;
	boolean[] oriented;
	int selected_edge;
	int collector_index;
	
	private EventTimeProbabilityDistributionGUI death_gui;
	private boolean change_from_ui;

	private JPanel edge_panel;
	private JScrollPane edges_scroll;
	private JTable edge_table;
	private JPanel buttonsedge_panel;
	private JButton addedge_button;
	private JButton removeedge_button;
	
	private JPanel genprop_panel;
	private JPanel basicprop_panel;
	private JPanel id_panel;
	private JLabel id_label;
	private JTextField id_field;
	private JPanel length_panel;
	private JLabel length_label;
	private JTextField length_field;
	private JPanel visible_panel;
	private JLabel visible_label;
	private JCheckBox visible_check;

	private JPanel dynprop_panel;
	private JPanel dynamic_panel;
	private JLabel dynamic_label;
	private JCheckBox dynamic_check;
	private JPanel enable_panel;
	private JLabel enable_label;
	private JCheckBox enable_check;
	private JPanel enabtpd_panel;
	private JLabel enabtpd_label;
	private JComboBox enabtpd_combo;
	private JPanel disabltpd_panel;
	private JLabel disabltpd_label;
	private JComboBox disabltpd_combo;
	
	private JPanel locprop_panel;
	private JPanel emitter_panel;
	private JLabel emitter_label;
	private JComboBox emitter_combo;
	private JPanel collector_panel;
	private JLabel collector_label;
	private JComboBox collector_combo;
	private JPanel oriented_panel;
	private JLabel oriented_label;
	private JCheckBox oriented_check;
	
	

	public EdgeJPanel(JDialog owner, Graph graph) {
		initialize();
		initialize2(owner, graph);
	}

	/**
	 * This method initializes this
	 * 
	 */
	private void initialize() {
        edge_panel = new javax.swing.JPanel();
        edges_scroll = new javax.swing.JScrollPane();
        edge_table = new javax.swing.JTable();
        buttonsedge_panel = new javax.swing.JPanel();
        addedge_button = new javax.swing.JButton();
        removeedge_button = new javax.swing.JButton();
        
        genprop_panel = new javax.swing.JPanel();
        basicprop_panel = new javax.swing.JPanel();
        id_panel = new javax.swing.JPanel();
        id_label = new javax.swing.JLabel();
        id_field = new javax.swing.JTextField();
        length_panel = new javax.swing.JPanel();
        length_label = new javax.swing.JLabel();
        length_field = new javax.swing.JTextField();
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
        
    	locprop_panel = new javax.swing.JPanel();
    	emitter_panel = new javax.swing.JPanel();
    	emitter_label = new javax.swing.JLabel();
    	emitter_combo  = new javax.swing.JComboBox();
    	collector_panel = new javax.swing.JPanel();
    	collector_label = new javax.swing.JLabel();
    	collector_combo = new javax.swing.JComboBox();
    	oriented_panel = new javax.swing.JPanel();
    	oriented_label = new javax.swing.JLabel();
    	oriented_check = new javax.swing.JCheckBox();
    	
    	
        setLayout(new BoxLayout(this, BoxLayout.X_AXIS));
        setPreferredSize(new Dimension(500,450));
        setMaximumSize(new Dimension(600,500));
        
        
        /**
         * Edges visualization
         */
        
        edge_panel.setLayout(new BoxLayout(edge_panel, BoxLayout.X_AXIS));
        edge_panel.setBorder(new javax.swing.border.TitledBorder("Related Edges"));
        edge_panel.setMaximumSize(new Dimension(300, 350));
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
        edge_table.addMouseListener(new java.awt.event.MouseListener() {
			public void mouseReleased(MouseEvent e) {
				edge_selected();
				
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
        
        
        
        /** 
         * Properties panel
         */
        
        genprop_panel.setLayout(new BoxLayout(genprop_panel, BoxLayout.Y_AXIS));
        
        // Basic properties
        basicprop_panel.setLayout(new BoxLayout(basicprop_panel, BoxLayout.Y_AXIS));
        basicprop_panel.setBorder(new javax.swing.border.TitledBorder("Basic Properties"));
        //basicprop_panel.setMaximumSize(new Dimension(300,70));
        basicprop_panel.setPreferredSize(new Dimension(300,100));
        
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
        
        length_panel.setLayout(new BoxLayout(length_panel, BoxLayout.X_AXIS));
        length_label.setText("Length");
        length_label.setPreferredSize(new Dimension(70, 25));
        length_panel.add(length_label);
        length_field.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
            	length_fieldKeyReleased(evt);
            }

        });
        length_panel.add(length_field);
        basicprop_panel.add(length_panel);
        
        
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
        
        
        
        /**
         * location properties panel
         */
        
        locprop_panel.setLayout(new BoxLayout(locprop_panel, BoxLayout.Y_AXIS));
        locprop_panel.setBorder(new javax.swing.border.TitledBorder("Localization Properties"));
        //locprop_panel.setMaximumSize(new Dimension(300, 110));
        locprop_panel.setPreferredSize(new Dimension(300, 120));
        locprop_panel.setMinimumSize(new Dimension(200, 80));
        
    	emitter_panel.setLayout(new BoxLayout(emitter_panel, BoxLayout.X_AXIS));
    	emitter_label.setText("Emitter");
    	emitter_label.setPreferredSize(new Dimension(100, 25));
        emitter_panel.add(emitter_label);
        emitter_combo.setPreferredSize(new Dimension(95, 25));
        emitter_combo.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(ActionEvent e) {
				emitter_comboactionPerformed(e);
				
			}

        });
        emitter_panel.add(emitter_combo);
        locprop_panel.add(emitter_panel);
        
        
    	collector_panel.setLayout(new BoxLayout(collector_panel, BoxLayout.X_AXIS));
    	collector_label.setText("collector");
    	collector_label.setPreferredSize(new Dimension(100, 25));
    	collector_panel.add(collector_label);
        collector_combo.setPreferredSize(new Dimension(95, 25));
        collector_combo.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(ActionEvent e) {
				collector_comboactionPerformed(e);
				
			}

        });
        collector_panel.add(collector_combo);
        locprop_panel.add(collector_panel);
        
        
    	oriented_panel.setLayout(new FlowLayout());
    	oriented_label.setText("oriented ?");
    	oriented_label.setPreferredSize(new Dimension(100, 25));
    	oriented_panel.add(oriented_label);
    	oriented_check.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(ActionEvent e) {
				oriented_checkactionPerformed(e);
				
			}

        });
    	oriented_panel.add(oriented_check);
    	locprop_panel.add(oriented_panel);
    	
    	
    	genprop_panel.add(locprop_panel);
    	
    	
        // Dynamicity properties
        dynprop_panel.setLayout(new BoxLayout(dynprop_panel, BoxLayout.Y_AXIS));
        dynprop_panel.setBorder(new javax.swing.border.TitledBorder("Dynamicity Properties"));
        //dynprop_panel.setMaximumSize(new Dimension(300, 160));
        dynprop_panel.setPreferredSize(new Dimension(300, 160));
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
			
	}
	

	/** Complements the initiation of the components of the GUI.
     * 
     *  @param owner The owner of the panel
     *  @param graph The graph of the patrolling simulation. 
     **/
	private void initialize2(JDialog owner, Graph graph){
		this.owner = owner;
		this.graph = graph;
		this.vertex = graph.getVertexes();
		this.edges = graph.getEdges();
		
		this.oriented = new boolean[edges.length];
		
		for(int i = 0; i < edges.length; i++)
			if(edges[i].getEmitter().isCollectorOf(edges[i])
				&& edges[i].getEmitter().isEmitterOf(edges[i])
				&& edges[i].getCollector().isCollectorOf(edges[i])
				&& edges[i].getCollector().isEmitterOf(edges[i]))
				oriented[i] = false;
			else
				oriented[i] = true;
		
		if(edges.length > 0){
			populate_edges();
			selected_edge = 0;
			edge_selected(0);
		}
		
		change_from_ui = true;
		
	}
	
	/** Is called to refresh the graph
     * 
     *  @param graph The graph of the patrolling simulation. 
     **/
	public void refresh_graph(Graph graph){
		initialize2(owner, graph);
	}
		
	
	/** Is called to refresh the graph and choose/create an edge
     * 
     *  @param graph The graph of the patrolling simulation. 
     *  @param edge_id The id of the edge to modify. If "", create a new one
     **/
	public void refresh_graph(Graph graph , String edge_id ){
		change_from_ui = false;
		
		if(edge_id != ""){
			for(int i = 0; i < edges.length; i++)
				if(edges[i].getObjectId() == edge_id) 
					selected_edge = i;
		} else {
			addedge_buttonactionPerformed(new ActionEvent(owner, 0, edge_id ));
		}
		
		edge_selected(selected_edge);
		change_from_ui = true;
	}
	
	
	
	/** * * * *
	 * These functions are called to fill in the different fields
	 * 
	 * * * * * */
	
	/**
	 * This function populates the edge table from the edge list
	 */
	private void populate_edges(){
		
		int nrow = ((DefaultTableModel) this.edge_table.getModel()).getRowCount();
    	for(int i=0; i < nrow ; i++)
    		((DefaultTableModel) this.edge_table.getModel()).removeRow(0);
    	
		
    	for(int i = 0; i < edges.length; i++) {
    		String[] content = { edges[i].getObjectId() + ",<" + edges[i].getEmitter().getObjectId() + "," +  edges[i].getCollector().getObjectId() + ">"};
    		((DefaultTableModel) this.edge_table.getModel()).addRow(content);
    	}
	}
	
	
	/**
	 * This function calls the next one using the selected 
	 * edge in the vertex table as argument
	 */
	private void edge_selected(){
		edge_selected(this.edge_table.getSelectedRow());
	}
	
	/**
	 * this function fills in the different fields from the selected edge
	 * it enables the dynamicity if the edge is dynamic
	 * 
	 * @param select : index of the selected edge in the vertex list
	 */
	private void edge_selected(int select){
		change_from_ui = false;
		selected_edge = select;
		
		id_field.setText(edges[selected_edge].getObjectId());		
		length_field.setText(String.valueOf(edges[selected_edge].getLength()));
		visible_check.setSelected(edges[selected_edge].isVisible());
		
		if(edges[selected_edge] instanceof DynamicEdge){
			dynamic_check.setSelected(true);
			enable_check.setSelected(((DynamicEdge) edges[selected_edge]).isEnabled());
			if(edges[selected_edge].getEmitter() instanceof DynamicVertex
					&& ((DynamicVertex) edges[selected_edge].getEmitter()).isEnabled()
					&& edges[selected_edge].getCollector() instanceof DynamicVertex
					&& ((DynamicVertex) edges[selected_edge].getCollector()).isEnabled())
				enable_check.setEnabled(true);
			
			set_birth_tpd((DynamicEdge) edges[selected_edge]);
			enabtpd_combo.setEnabled(true);
			
			set_death_tpd((DynamicEdge) edges[selected_edge]);		
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

		populate_collector(edges[selected_edge]);
		populate_emitter(edges[selected_edge]);
		oriented_check.setSelected(oriented[selected_edge]);
		
		this.revalidate();
		this.repaint();
		
		change_from_ui = true;
		
		
	}
	
	/**
	 * this function fills in the combo list for the collector and select the good one
	 * 
	 * @param edge : the selected edge
	 */
	private void populate_collector(Edge edge) {
		String[] model = new String[vertex.length];
		int i = 0;
		collector_index = 0;
		for(Vertex vert : vertex){
			model[i] = vert.getObjectId() + ", " + vert.getLabel();
			if(vert.isCollectorOf(edge)) collector_index = i;
			i++;
		}
		
		collector_combo.setModel(new javax.swing.DefaultComboBoxModel(model));
		collector_combo.setSelectedIndex(collector_index);
		
	}

	/**
	 * this function fills in the combo list for the emitter and select the good one
	 * 
	 * @param edge : the selected edge
	 */
	private void populate_emitter(Edge edge) {
		String[] model = new String[vertex.length];
		int i = 0;
		int option = 0;
		for(Vertex vert : vertex){
			model[i] = vert.getObjectId() + ", " + vert.getLabel();
			if(vert.isEmitterOf(edge) && i!=collector_index) option = i;
			i++;
		}
		
		
		
		emitter_combo.setModel(new javax.swing.DefaultComboBoxModel(model));
		emitter_combo.setSelectedIndex(option);
		
	}

	/**
	 * Sets the combobox for the birth time probability distribution
	 * 
	 * @param selected : the edge selected
	 */
	private void set_birth_tpd(DynamicEdge selected){
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
	 * @param selected : the edge selected
	 */
	private void set_death_tpd(DynamicEdge selected){
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
	
	
	protected void oriented_checkactionPerformed(ActionEvent e) {
		if(edges[selected_edge] != null)
			oriented[selected_edge] = oriented_check.isSelected();
		
	}

	protected void emitter_comboactionPerformed(ActionEvent e) {
		if(edges[selected_edge] != null && change_from_ui){
			change_origins();
			populate_edges();
		}
		
	}
	
	protected void collector_comboactionPerformed(ActionEvent e) {
		if(edges[selected_edge] != null && change_from_ui){
			change_origins();
			populate_edges();
		}
		
	}
	
	/**
	 * This method change the emitter and collector of the edge by creating a new one to replace it
	 * 
	 */
	private void change_origins(){
		Edge mynewedge = null;
		if(edges[selected_edge] instanceof DynamicEdge){
			mynewedge = new DynamicEdge(vertex[emitter_combo.getSelectedIndex()], 
										vertex[collector_combo.getSelectedIndex()], 
										oriented[selected_edge], 
										edges[selected_edge].getLength(),
										((DynamicEdge)edges[selected_edge]).getEnablingTPD(),
										((DynamicEdge)edges[selected_edge]).getDisablingTPD(),
										((DynamicEdge)edges[selected_edge]).isEnabled());
		} else {
			mynewedge = new Edge(vertex[emitter_combo.getSelectedIndex()], 
									vertex[collector_combo.getSelectedIndex()], 
									oriented[selected_edge], 
									edges[selected_edge].getLength());
		}
		mynewedge.setObjectId(edges[selected_edge].getObjectId());
		mynewedge.setVisibility(edges[selected_edge].isVisible());
		
		edges[selected_edge] = mynewedge;
	}

	
	/**
	 * Opens the EventTimeProbabilityDistributionGUI for the death TPD
	 * 
	 * @param evt
	 */
	protected void disabltpd_comboactionPerformed(ActionEvent e) {
		if(edges[selected_edge] instanceof DynamicEdge && change_from_ui){
			
			if(disabltpd_combo.getSelectedIndex() == 0){
				((DynamicEdge)edges[selected_edge]).setDisablingTPD(null);
				return;
			}
			
	    	else if(this.disabltpd_combo.getSelectedIndex() == 1){
	    		if(((DynamicEdge)edges[selected_edge]).getDisablingTPD() instanceof UniformEventTimeProbabilityDistribution)
	    			this.death_gui = new EventTimeProbabilityDistributionGUI(this.owner, ((DynamicEdge)edges[selected_edge]).getDisablingTPD());
	    		else
	    			this.death_gui = new EventTimeProbabilityDistributionGUI(this.owner, new UniformEventTimeProbabilityDistribution((int) System.currentTimeMillis(), 0));
	    	}
			
	    	else if(this.disabltpd_combo.getSelectedIndex() == 2) {
	    		if(((DynamicEdge)edges[selected_edge]).getDisablingTPD() instanceof EmpiricalEventTimeProbabilityDistribution)
	    			this.death_gui = new EventTimeProbabilityDistributionGUI(this.owner, ((DynamicEdge)edges[selected_edge]).getDisablingTPD());
	    		else{
	    			double[] distribution = {1};
	    			this.death_gui = new EventTimeProbabilityDistributionGUI(this.owner, new EmpiricalEventTimeProbabilityDistribution((int) System.currentTimeMillis(), distribution));
	    		}
	    	}
			
	    	else if(this.disabltpd_combo.getSelectedIndex() == 3){
	    		if(((DynamicEdge)edges[selected_edge]).getDisablingTPD() instanceof NormalEventTimeProbabilityDistribution)
	    			this.death_gui = new EventTimeProbabilityDistributionGUI(this.owner, ((DynamicEdge)edges[selected_edge]).getDisablingTPD());
	    		else
	    			this.death_gui = new EventTimeProbabilityDistributionGUI(this.owner, new NormalEventTimeProbabilityDistribution((int) System.currentTimeMillis(), 0, 0));
	    	}
	    	
	    	else {
	    		if(((DynamicEdge)edges[selected_edge]).getDisablingTPD() instanceof SpecificEventTimeProbabilityDistribution)
	    			this.death_gui = new EventTimeProbabilityDistributionGUI(this.owner, ((DynamicEdge)edges[selected_edge]).getDisablingTPD());
	    		else
	    			this.death_gui = new EventTimeProbabilityDistributionGUI(this.owner, new SpecificEventTimeProbabilityDistribution((int) System.currentTimeMillis(), 0, 0));
	    	}
	    			
	    		
	    	this.death_gui.setVisible(true);	
	    	 	
	    	EventTimeProbabilityDistribution chosen_death_tpd = this.death_gui.getETPD();  
	    	if(chosen_death_tpd != null){
	    		((DynamicEdge)edges[selected_edge]).setDisablingTPD(chosen_death_tpd);
	    	}
    		
	    	if(((DynamicEdge)edges[selected_edge]).getDisablingTPD() == null) 
	    		disabltpd_combo.setSelectedIndex(0);
	    	else if(((DynamicEdge)edges[selected_edge]).getDisablingTPD() instanceof UniformEventTimeProbabilityDistribution)
	    		disabltpd_combo.setSelectedIndex(1);
    		else if(((DynamicEdge)edges[selected_edge]).getDisablingTPD() instanceof EmpiricalEventTimeProbabilityDistribution)
    			disabltpd_combo.setSelectedIndex(2);
    		else if(((DynamicEdge)edges[selected_edge]).getDisablingTPD() instanceof NormalEventTimeProbabilityDistribution)
    			disabltpd_combo.setSelectedIndex(3);
    		else disabltpd_combo.setSelectedIndex(4);

		}
		
		
	}

	
	/**
	 * Opens the EventTimeProbabilityDistributionGUI for the birth TPD
	 * 
	 * @param evt
	 */
	protected void enabtpd_comboactionPerformed(ActionEvent e) {
		if(edges[selected_edge] instanceof DynamicEdge && change_from_ui){
			
			if(enabtpd_combo.getSelectedIndex() == 0){
				((DynamicEdge)edges[selected_edge]).setEnablingTPD(null);
				return;
			}
			
	    	else if(this.enabtpd_combo.getSelectedIndex() == 1){
	    		if(((DynamicEdge)edges[selected_edge]).getEnablingTPD() instanceof UniformEventTimeProbabilityDistribution)
	    			this.death_gui = new EventTimeProbabilityDistributionGUI(this.owner, ((DynamicEdge)edges[selected_edge]).getEnablingTPD());
	    		else
	    			this.death_gui = new EventTimeProbabilityDistributionGUI(this.owner, new UniformEventTimeProbabilityDistribution((int) System.currentTimeMillis(), 0));
	    	}
			
	    	else if(this.enabtpd_combo.getSelectedIndex() == 2) {
	    		if(((DynamicEdge)edges[selected_edge]).getEnablingTPD() instanceof EmpiricalEventTimeProbabilityDistribution)
	    			this.death_gui = new EventTimeProbabilityDistributionGUI(this.owner, ((DynamicEdge)edges[selected_edge]).getEnablingTPD());
	    		else{
	    			double[] distribution = {1};
	    			this.death_gui = new EventTimeProbabilityDistributionGUI(this.owner, new EmpiricalEventTimeProbabilityDistribution((int) System.currentTimeMillis(), distribution));
	    		}
	    	}
			
	    	else if(this.enabtpd_combo.getSelectedIndex() == 3){
	    		if(((DynamicEdge)edges[selected_edge]).getEnablingTPD() instanceof NormalEventTimeProbabilityDistribution)
	    			this.death_gui = new EventTimeProbabilityDistributionGUI(this.owner, ((DynamicEdge)edges[selected_edge]).getEnablingTPD());
	    		else
	    			this.death_gui = new EventTimeProbabilityDistributionGUI(this.owner, new NormalEventTimeProbabilityDistribution((int) System.currentTimeMillis(), 0, 0));
	    	}
	    	
	    	else {
	    		if(((DynamicEdge)edges[selected_edge]).getEnablingTPD() instanceof SpecificEventTimeProbabilityDistribution)
	    			this.death_gui = new EventTimeProbabilityDistributionGUI(this.owner, ((DynamicEdge)edges[selected_edge]).getEnablingTPD());
	    		else
	    			this.death_gui = new EventTimeProbabilityDistributionGUI(this.owner, new SpecificEventTimeProbabilityDistribution((int) System.currentTimeMillis(), 0, 0));
	    	}
	    			
	    		
	    	this.death_gui.setVisible(true);	
	    	 	
	    	EventTimeProbabilityDistribution chosen_death_tpd = this.death_gui.getETPD();  
	    	if(chosen_death_tpd != null){
	    		((DynamicEdge)edges[selected_edge]).setEnablingTPD(chosen_death_tpd);
	    	}
    		
	    	if(((DynamicEdge)edges[selected_edge]).getEnablingTPD() == null) 
	    		enabtpd_combo.setSelectedIndex(0);
	    	else if(((DynamicEdge)edges[selected_edge]).getEnablingTPD() instanceof UniformEventTimeProbabilityDistribution)
    			enabtpd_combo.setSelectedIndex(1);
    		else if(((DynamicEdge)edges[selected_edge]).getEnablingTPD() instanceof EmpiricalEventTimeProbabilityDistribution)
    			enabtpd_combo.setSelectedIndex(2);
    		else if(((DynamicEdge)edges[selected_edge]).getEnablingTPD() instanceof NormalEventTimeProbabilityDistribution)
    			enabtpd_combo.setSelectedIndex(3);
    		else enabtpd_combo.setSelectedIndex(4);

		}	

		
	}

	protected void enable_checkactionPerformed(ActionEvent e) {
		if(edges[selected_edge] != null)
			edges[selected_edge].setIsEnabled(enable_check.isSelected());
		
	}

	protected void dynamic_checkactionPerformed(ActionEvent e) {
		if(edges[selected_edge] != null){
			if(dynamic_check.isSelected()){
				if(!(edges[selected_edge] instanceof DynamicEdge)){
					DynamicEdge answer = new DynamicEdge(edges[selected_edge].getEmitter(), 
															edges[selected_edge].getCollector(), 
															edges[selected_edge].getLength(), 
															null, 
															null, 
															true);
					answer.setObjectId(edges[selected_edge].getObjectId());
					answer.setVisibility(edges[selected_edge].isVisible());
					
					edges[selected_edge] = answer;
					
				}
				enable_check.setSelected(((DynamicEdge) edges[selected_edge]).isEnabled());
				if(edges[selected_edge].getEmitter() instanceof DynamicVertex
						&& ((DynamicVertex) edges[selected_edge].getEmitter()).isEnabled()
						&& edges[selected_edge].getCollector() instanceof DynamicVertex
						&& ((DynamicVertex) edges[selected_edge].getCollector()).isEnabled())
					enable_check.setEnabled(true);
				enabtpd_combo.setEnabled(true);
				disabltpd_combo.setEnabled(true);				

			}
			
			else {
				if(edges[selected_edge] instanceof DynamicEdge){
					Edge answer = new Edge(edges[selected_edge].getEmitter(), edges[selected_edge].getCollector(), edges[selected_edge].getLength());
					answer.setObjectId(edges[selected_edge].getObjectId());
					answer.setVisibility(edges[selected_edge].isVisible());
					
					edges[selected_edge] = answer;
				}
				
				enable_check.setEnabled(false);
				enable_check.setSelected(false);
				enabtpd_combo.setEnabled(false);
				disabltpd_combo.setEnabled(false);				
			}
			
			
		}
		
	}

	protected void visible_checkactionPerformed(ActionEvent e) {
		if(edges[selected_edge] != null)
			edges[selected_edge].setVisibility(visible_check.isSelected());
		
	}

	protected void length_fieldKeyReleased(KeyEvent evt) {
		if(edges[selected_edge] != null)
			edges[selected_edge].setLength(Double.valueOf(length_field.getText()));
		
	}

	protected void id_fieldKeyReleased(KeyEvent evt) {
		if(edges[selected_edge] != null){
			edges[selected_edge].setObjectId(id_field.getText());
			populate_edges();
		}
	}

	protected void removeedge_buttonactionPerformed(ActionEvent e) {
		Edge[] new_edge = new Edge[edges.length - 1];
		boolean[] new_oriented = new boolean[edges.length - 1];
		for(int i = 0; i < selected_edge; i ++){
			new_edge[i] = edges[i];
			new_oriented[i] = oriented[i];
		}
		for(int i = selected_edge + 1; i < edges.length; i ++){
			new_edge[i-1] = edges[i];
			new_oriented[i-1] = oriented[i];
		}
		
		edges = new_edge;
		oriented = new_oriented;
		
		populate_edges();
		edge_selected(0);
		
	}

	protected void addedge_buttonactionPerformed(ActionEvent e) {
		int ednum = edges.length;
		Edge[] new_edges = new Edge[ednum + 1];
		boolean[] new_oriented = new boolean[ednum + 1];
		for(int i = 0; i < ednum; i ++){
			new_edges[i] = edges[i];
			new_oriented[i] = oriented[i];
		}
		new_edges[ednum] = new Edge(vertex[0], vertex[0], false, 1);
		new_edges[ednum].setObjectId("e" + (ednum+1) + "@" + Long.toHexString(System.currentTimeMillis()));
		new_oriented[ednum] = false;
		
		
		edges = new_edges;
		oriented = new_oriented;
		
		populate_edges();
		edge_selected(ednum);
		
	}


	public Graph getGraph(){
		Vertex[] new_vert = new Vertex[vertex.length];
		for(int i = 0; i < vertex.length; i++){
			if(vertex[i] instanceof DynamicVertex){
				DynamicVertex myvert = ((DynamicVertex)vertex[i]).getCopy();
				new_vert[i] = myvert;
			} else {
				Vertex myvert = vertex[i].getCopy();
				new_vert[i] = myvert;
			}
		}

		
		vertex = new_vert;
		
		for(int i = 0; i < edges.length; i++){
			int emitter = -1;
			int collector = -1;
			Edge mynewedge = null;
			for(int j = 0; j < vertex.length; j++){
				if(edges[i].getEmitter().getObjectId() == vertex[j].getObjectId()){
					emitter = j;
				}
				if(edges[i].getCollector().getObjectId() == vertex[j].getObjectId()){
					collector = j;
				}
			}
			if(edges[i] instanceof DynamicEdge){
				mynewedge = new DynamicEdge(vertex[emitter], 
											vertex[collector], 
											oriented[i], 
											edges[i].getLength(),
											((DynamicEdge)edges[i]).getEnablingTPD(),
											((DynamicEdge)edges[i]).getDisablingTPD(),
											((DynamicEdge)edges[i]).isEnabled());
			} else {
				mynewedge = new Edge(vertex[emitter], 
										vertex[collector], 
										oriented[i], 
										edges[i].getLength());
			}
			mynewedge.setObjectId(edges[i].getObjectId());
			mynewedge.setVisibility(edges[i].isVisible());
			
			edges[i] = mynewedge;
			
		}
		

		
		Graph mynewgraph = new Graph(graph.getObjectId(), vertex);
		return mynewgraph;
	}

}
