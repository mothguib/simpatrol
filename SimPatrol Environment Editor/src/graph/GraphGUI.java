package graph;

import javax.swing.BoxLayout;
import javax.swing.JDialog;
import javax.swing.JFrame;

import editor.Editor;

import model.graph.Graph;
import java.awt.Dimension;
import java.awt.event.KeyEvent;

public class GraphGUI extends JDialog {
	
	
    /**
	 * 
	 */
	private static final long serialVersionUID = 3930981224424337879L;

	Graph graph;
    
    NodeJPanel vertex_panel;
    EdgeJPanel node_panel;
    int actual_panel;
    
    
    private javax.swing.JPanel buttons_internal_frame;
    private javax.swing.JPanel buttons_panel;
    private javax.swing.JButton cancel_button;
    private javax.swing.JButton ok_button;
    private javax.swing.JTabbedPane tabbed_panel;
    private javax.swing.JTextArea xml_area;
    private javax.swing.JScrollPane xml_scroll;
    
    private javax.swing.JPanel label_panel;
    private javax.swing.JLabel label_label;
    private javax.swing.JTextField label_field;
    
    
    
	
	/** Constructor.
     * 
     *  @param owner The GUI that called this one.
	 *  @param society The Society object to be configured by the GUI.
	 *  @param graph The graph of the patrolling simulation. */
    public GraphGUI(JFrame owner, Graph graph) {
        super(owner, true);
        this.initComponents();
        this.initComponents2(graph);
    }
    
    
    private void initComponents() {//GEN-BEGIN:initComponents
        xml_scroll = new javax.swing.JScrollPane();
        xml_area = new javax.swing.JTextArea();
        tabbed_panel = new javax.swing.JTabbedPane();
        buttons_panel = new javax.swing.JPanel();
        buttons_internal_frame = new javax.swing.JPanel();
        cancel_button = new javax.swing.JButton();
        ok_button = new javax.swing.JButton();
        label_panel = new javax.swing.JPanel();
        label_label = new javax.swing.JLabel();
        label_field = new javax.swing.JTextField();

        xml_area.setEditable(false);
        xml_scroll.setViewportView(xml_area);

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        this.setPreferredSize(new Dimension(750, 500));
        this.setMinimumSize(new Dimension(750, 500));
        //this.setResizable(false);
        setTitle("SimPatrol: Graph editor");
        
        this.label_panel.setMaximumSize(new Dimension(400,30));
        this.label_panel.setLayout(new BoxLayout(label_panel, BoxLayout.X_AXIS));
        this.label_label.setText("Graph Label");
        this.label_label.setPreferredSize(new Dimension(100,25));
        this.label_panel.add(this.label_label);
        this.label_field.setMinimumSize(new Dimension(300,25));
        this.label_field.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
            	label_fieldKeyReleased(evt);
            }

        });
        this.label_panel.add(this.label_field);
        getContentPane().add(label_panel,java.awt.BorderLayout.NORTH);

        this.tabbed_panel.addTab("Nodes", null);
        this.tabbed_panel.addTab("Edges", null);
        this.tabbed_panel.addTab("XML", this.xml_scroll);
        tabbed_panel.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                tabbed_panelStateChanged(evt);
            }
        });

        getContentPane().add(tabbed_panel, java.awt.BorderLayout.CENTER);

        buttons_panel.setLayout(new java.awt.BorderLayout());

        buttons_internal_frame.setLayout(new java.awt.GridLayout(1, 2));

        cancel_button.setText("Cancel");
        cancel_button.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cancel_buttonActionPerformed(evt);
            }
        });

        buttons_internal_frame.add(cancel_button);

        ok_button.setText("Ok");
        ok_button.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                ok_buttonActionPerformed(evt);
            }
        });

        buttons_internal_frame.add(ok_button);

        buttons_panel.add(buttons_internal_frame, java.awt.BorderLayout.EAST);

        getContentPane().add(buttons_panel, java.awt.BorderLayout.SOUTH);

        java.awt.Dimension screenSize = java.awt.Toolkit.getDefaultToolkit().getScreenSize();
        //setBounds((screenSize.width-600)/2, (screenSize.height-550)/2, 600, 550);
    }
    

	/** Complements the initiation of the components of the GUI.
     * 
     *  @param graph The graph of the patrolling simulation. */
    private void initComponents2(Graph graph) {
    	this.graph = graph;
    	
    	this.label_field.setText(graph.getLabel());
    	
    	this.vertex_panel = new NodeJPanel(this, graph);
    	this.tabbed_panel.setComponentAt(0, this.vertex_panel);
    	
    	this.node_panel = new EdgeJPanel(this, graph);
    	this.tabbed_panel.setComponentAt(1, this.node_panel);
    	
    	this.xml_area.setText(this.graph.fullToXML(0));
    	
    	actual_panel = 0;
    	
    }
    
    
    /** Executed when the label field is modified.
     * 
     * @param evt
     */
    protected void label_fieldKeyReleased(KeyEvent evt) {
		graph.setLabel(label_field.getText());
		
	}

    /** Executed when the ok button is pressed.
     * 
     * @param evt
     */
    private void ok_buttonActionPerformed(java.awt.event.ActionEvent evt) {
    	String myname = graph.getLabel();
    	if(actual_panel == 0)
			this.graph = this.vertex_panel.getGraph();
		if(actual_panel == 1)
			this.graph = this.node_panel.getGraph();
        if(this.getOwner() != null)
        	this.graph.setLabel(myname);
        	((Editor)this.getOwner()).setGraph(this.graph);
    	this.dispose();
    }
    
    /** Executed when the cancel button is pressed.
     * 
     * @param evt
     */
    private void cancel_buttonActionPerformed(java.awt.event.ActionEvent evt) {
    	this.dispose();
    }
    
    /** Executed when the tabbed_panel changes.
     *   
     * @param evt
     */
    private void tabbed_panelStateChanged(javax.swing.event.ChangeEvent evt) {
    	String myname = graph.getLabel();
		if(actual_panel == 0)
			this.graph = this.vertex_panel.getGraph();
		if(actual_panel == 1)
			this.graph = this.node_panel.getGraph();
		
		graph.setLabel(myname);
    	this.xml_area.setText(this.graph.fullToXML(0));
    		
    	actual_panel = this.tabbed_panel.getSelectedIndex();
    	switch(actual_panel){
    	case 0 : 
    		this.vertex_panel.refresh_graph(this.graph);
    		break;
    	case 1 : 
    		this.node_panel.refresh_graph(this.graph);
    		break;
    	default :
    		break;			
    	}
    }
    
    
    /** Called by the vertex panel buttons add_edge and modify_edge
     *   
     * @param edge_id : the ObjectId of the edge to modify when the edge panel is opened
     * 					if "", a new edge is created
     */
    public void mod_edge(String edge_id){
    	this.graph = this.vertex_panel.getGraph();
    	this.tabbed_panel.setSelectedIndex(1);
    	actual_panel = 1;
    	this.node_panel.refresh_graph(this.graph, edge_id);
    }
    
    

}
