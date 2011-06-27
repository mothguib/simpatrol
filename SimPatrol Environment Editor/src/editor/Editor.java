package editor;

import graph.GraphGUI;

import java.awt.Dimension;
import java.awt.Event;

import javax.swing.JPanel;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import javax.swing.JFileChooser;
import javax.swing.JMenuBar;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.KeyStroke;
import javax.xml.parsers.ParserConfigurationException;

import org.xml.sax.SAXException;

import society.SocietyGUI;

import model.Environment;
import model.agent.*;
import model.graph.Graph;
import model.graph.Node;
import control.exception.EdgeNotFoundException;
import control.exception.NodeNotFoundException;
import control.translator.EnvironmentTranslator;
import javax.swing.JButton;
import java.awt.FlowLayout;
import javax.swing.JFrame;
import java.awt.ComponentOrientation;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;

import javax.swing.SwingConstants;

public class Editor extends javax.swing.JFrame implements ActionListener{
	
	
	/** 
	 * 
	 */
	String environment_file = null;
	boolean saved = true;
	Graph graph = null;
	Society[] societies;
	JButton[] societybuttons;

	/**
	 * 
	 */
	private static final long serialVersionUID = 1167263098901718377L;
	private JMenuBar jJMenuBar = null;
	private JMenu FileMenu = null;
	private JMenuItem OpenFile = null;
	private JMenuItem SaveFile = null;
	private JMenuItem SaveAsFile = null;
	private JMenuItem CloseFile = null;
	private JPanel ButtonsPane = null;
	private JButton GraphButton = null;
	private JButton CreateSocietyButton = null;
	/**
	 * This method initializes 
	 * 
	 */
	public Editor() {
		super();
		initialize();
	}

	/**
	 * This method initializes this
	 * 
	 */
	private void initialize() {
        this.setSize(new Dimension(170, 283));
        this.setContentPane(getButtonsPane());
        this.setJMenuBar(getJJMenuBar());
        this.setTitle("SimPatrol Environment Editor");
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

			
	}

	/**
	 * This method initializes jJMenuBar	
	 * 	
	 * @return javax.swing.JMenuBar	
	 */
	private JMenuBar getJJMenuBar() {
		if (jJMenuBar == null) {
			jJMenuBar = new JMenuBar();
			jJMenuBar.add(getFileMenu());
		}
		return jJMenuBar;
	}

	/**
	 * This method initializes FileMenu	
	 * 	
	 * @return javax.swing.JMenu	
	 */
	private JMenu getFileMenu() {
		if (FileMenu == null) {
			FileMenu = new JMenu();
			FileMenu.setText("Environment");
			FileMenu.add(getOpenFile());
			FileMenu.add(getSaveFile());
			FileMenu.add(getSaveAsFile());
			FileMenu.add(getCloseFile());
			
		}
		return FileMenu;
	}

	/**
	 * This method initializes OpenFile	
	 * 	
	 * @return javax.swing.JMenuItem	
	 */
	private JMenuItem getOpenFile() {
		if (OpenFile == null) {
			OpenFile = new JMenuItem("Open", 'O');
			OpenFile.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_O, Event.CTRL_MASK));
			OpenFile.addActionListener(this);
		}
		return OpenFile;
	}

	/**
	 * This method initializes SaveFile	
	 * 	
	 * @return javax.swing.JMenuItem	
	 */
	private JMenuItem getSaveFile() {
		if (SaveFile == null) {
			SaveFile = new JMenuItem("Save", 'S');
			SaveFile.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S, Event.CTRL_MASK));
			SaveFile.addActionListener(this);
		}
		return SaveFile;
	}
	
	/**
	 * This method initializes SaveFile	
	 * 	
	 * @return javax.swing.JMenuItem	
	 */
	private JMenuItem getSaveAsFile() {
		if (SaveAsFile == null) {
			SaveAsFile = new JMenuItem("Save As...");
			SaveAsFile.addActionListener(this);
		}
		return SaveAsFile;
	}

	/**
	 * This method initializes CloseFile	
	 * 	
	 * @return javax.swing.JMenuItem	
	 */
	private JMenuItem getCloseFile() {
		if (CloseFile == null) {
			CloseFile = new JMenuItem();
			CloseFile.setText("Close");
			CloseFile.addActionListener(this);
		}
		return CloseFile;
	}
	
	/**
	 * This method initializes ButtonsPane	
	 * 	
	 * @return javax.swing.JPanel	
	 */
	private JPanel getButtonsPane() {
		if (ButtonsPane == null) {
			ButtonsPane = new JPanel();
			ButtonsPane.setLayout(new FlowLayout());
			Refresh_buttons();
		}
		return ButtonsPane;
	}

	/**
	 * This method initializes GraphButton	
	 * 	
	 * @return javax.swing.JButton	
	 */
	private JButton getGraphButton() {
		if (GraphButton == null) {
			GraphButton = new JButton();
			GraphButton.setText("Create Graph");
			GraphButton.setHorizontalAlignment(SwingConstants.CENTER);
			GraphButton.setComponentOrientation(ComponentOrientation.UNKNOWN);
			GraphButton.setPreferredSize(new Dimension(150, 25));
			GraphButton.setHorizontalTextPosition(SwingConstants.CENTER);
			GraphButton.setMaximumSize(null);
			GraphButton.addActionListener(this);
		}
		return GraphButton;
	}

	/**
	 * This method initializes CreateSocietyButton	
	 * 	
	 * @return javax.swing.JButton	
	 */
	private JButton getCreateSocietyButton() {
		if (CreateSocietyButton == null) {
			CreateSocietyButton = new JButton();
			CreateSocietyButton.setText("Create Society");
			CreateSocietyButton.setHorizontalTextPosition(SwingConstants.CENTER);
			CreateSocietyButton.setPreferredSize(new Dimension(150, 25));
			CreateSocietyButton.addActionListener(this);
		}
		return CreateSocietyButton;
	}
	
	
	/**
	 * This method refreshes the buttons to display in function of whether a graph and some societies exist or not
	 * 	
	 * 
	 */
	private void Refresh_buttons() {
		ButtonsPane.removeAll();
		ButtonsPane.add(getGraphButton());
		if(graph != null){
			GraphButton.setText("Modify Graph");
			ButtonsPane.add(getCreateSocietyButton());
		}
		if(societies != null) {
			societybuttons = new JButton[societies.length];
			for(int i = 0; i < societies.length; i++){
				societybuttons[i] = new JButton();
				societybuttons[i].setName("SocietyButton_" + i);
				societybuttons[i].setText("Modify " + societies[i].getObjectId());
				societybuttons[i].setHorizontalAlignment(SwingConstants.CENTER);
				societybuttons[i].setComponentOrientation(ComponentOrientation.UNKNOWN);
				societybuttons[i].setPreferredSize(new Dimension(150, 25));
				societybuttons[i].setHorizontalTextPosition(SwingConstants.CENTER);
				societybuttons[i].setMaximumSize(null);
				societybuttons[i].addActionListener(this);
				
				ButtonsPane.add(societybuttons[i]);
			}
		}
		ButtonsPane.revalidate();
		ButtonsPane.repaint();
	}
	
	
	/**
	 * This method is called each time an event happens, and manages it
	 * 	
	 * 
	 */
	public void actionPerformed(java.awt.event.ActionEvent e) {
		String source = "";
		if(e.getSource() instanceof JButton){
			source = ((JButton)e.getSource()).getText();
			if( source == "Create Graph"){
				Create_Graph();
			}
			else if(source == "Modify Graph"){
				Modify_Graph();
			}
			else if(source.contains("Modify")){
				Modify_Society(source.substring(7));
			}
			if(source == "Create Society"){
				Create_Society();
			}
		}
		else if(e.getSource() instanceof JMenuItem){
			source = ((JMenuItem)e.getSource()).getText();
			if(source == "Open"){
				try {
					OpenFile();
				} catch (ParserConfigurationException e1) {
					e1.printStackTrace();
				} catch (SAXException e1) {
					e1.printStackTrace();
				} catch (IOException e1) {
					e1.printStackTrace();
				} catch (NodeNotFoundException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				} catch (EdgeNotFoundException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}
			if(source == "Save As..."){
				try {
					SaveFileAs();
				} catch (ParserConfigurationException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				} catch (SAXException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}
			if(source == "Save"){
				try {
					SaveFile();
				} catch (ParserConfigurationException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				} catch (SAXException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				
			}
			if(source == "Close"){
				try {
					closeFile();
				} catch (ParserConfigurationException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				} catch (SAXException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}

			}
		}
	}

	/**
	 * This method opens a file, and gets the environment described
	 * @throws IOException 
	 * @throws SAXException 
	 * @throws ParserConfigurationException 
	 * @throws EdgeNotFoundException 
	 * @throws NodeNotFoundException 
	 */
	private void OpenFile() throws ParserConfigurationException, SAXException, IOException, NodeNotFoundException, EdgeNotFoundException{
		JFileChooser fc = new JFileChooser();
		if(environment_file != null)
			fc.setCurrentDirectory(new File(environment_file));
		int returnVal = fc.showOpenDialog(this);

        if (returnVal == JFileChooser.APPROVE_OPTION) {
        	environment_file = fc.getSelectedFile().getPath();
        	
        	Environment env = EnvironmentTranslator.getEnvironment(environment_file);
    		graph = env.getGraph();
    		
    		societies = env.getSocieties();
    		
    		Refresh_buttons();
            

        }

	}
	
	/**
	 * This method saves a file, describing the environment configured
	 * @throws IOException 
	 * @throws SAXException 
	 * @throws ParserConfigurationException 
	 */
	private void SaveFile() throws ParserConfigurationException, SAXException, IOException{
		Environment myenv = new Environment(graph, societies);
		if(environment_file == null){
			//Create a file chooser
			final JFileChooser fc = new JFileChooser();
			if(environment_file != null)
				fc.setCurrentDirectory(new File(environment_file));
			int returnVal = fc.showSaveDialog(this);
			if (returnVal == JFileChooser.APPROVE_OPTION) {
				environment_file = fc.getSelectedFile().getPath();

			}
			else return;
		}
		try { 
			BufferedWriter out = new BufferedWriter(new FileWriter(environment_file)); 
			out.write(myenv.fullToXML(0)); 
			out.close(); 
			saved = true;
		} catch (IOException e2) { 
			JOptionPane.showMessageDialog(this,
				    "Could not save",
				    "Error",
				    JOptionPane.WARNING_MESSAGE);
			return;

		}

	}
	
	private void SaveFileAs() throws ParserConfigurationException, SAXException, IOException{
		String old_env = environment_file;
		environment_file = null;
		SaveFile();
		if(!saved)
			environment_file = old_env;
	}
	
	
	/**
	 * This method close the environment
	 * @throws IOException 
	 * @throws SAXException 
	 * @throws ParserConfigurationException 
	 */
	private void closeFile() throws ParserConfigurationException, SAXException, IOException{
		if(!saved){
			Object[] options = {"Save Environment", "Close Environment", "Cancel"};
			int n = JOptionPane.showOptionDialog(this,
					"Environment has not been saved, do you want to save now ?",
					"Close",
					JOptionPane.YES_NO_CANCEL_OPTION,
					JOptionPane.QUESTION_MESSAGE,
					null,
					options,
					options[2]);
			if(n == JOptionPane.CANCEL_OPTION) return;
			if(n == JOptionPane.OK_OPTION){
				try {
					SaveFile();
				} catch (ParserConfigurationException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
					return;
				} catch (SAXException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
					return;
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
					return;
				}
			}
		}
		graph = null;
		societies = null;
		societybuttons = new JButton[0];
		environment_file = null;
		
		Refresh_buttons();

	}
	
	
	/**
	 * This method create a new OpenSociety and launches the associated GUI
	 */
	private void Create_Society(){
		if(societies == null)
			societies = new Society[1];
		else {
			Society[] clone = societies.clone();
			societies = new Society[clone.length + 1];
			for (int i = 0; i < clone.length; i++)
				societies[i] = clone[i];
		}
		int i = societies.length;
		
		boolean has_inactive = false;
		for(int j = 0; j < societies.length; j++)
			if(societies[j] != null && societies[j].getObjectId().equals("InactiveSociety"))
				has_inactive = true;
		if(!has_inactive && societies[0] == null)
			has_inactive = true;
		
		Object[] options;
		if(!has_inactive)
			options = new Object[]{"Open Society", "Closed Society", "Inactive Society", "Cancel"};
		else
			options = new Object[]{"Open Society", "Closed Society", "Cancel"};
		
		int n = JOptionPane.showOptionDialog(this,
				"What kind of society do you want to create ?",
				"Society creator",
				JOptionPane.YES_NO_CANCEL_OPTION,
				JOptionPane.QUESTION_MESSAGE,
				null,
				options,
				options[2]);
		
		if((has_inactive && n == 2) || (!has_inactive && n == 3)) return;
		if(n == 0){
			societies[i-1] = new OpenSociety("Society " + (i-1), new SeasonalAgent[0]);
			societies[i-1].setObjectId("s" + (i-1));
		}
		if(n == 1){
			societies[i-1] = new ClosedSociety("Society " + (i-1), new PerpetualAgent[0]);
			societies[i-1].setObjectId("s" + (i-1));
		}
		if(!has_inactive && n == 2){
			societies[i-1] = new OpenSociety("InactiveSociety", new SeasonalAgent[0]);
			societies[i-1].setObjectId("InactiveSociety");
		}

		SocietyGUI society_gui = new SocietyGUI(this, societies[i-1], i-1, new Environment(this.graph, societies));
		society_gui.setVisible(true);
	}
	
	
	/**
	 * This method launches the GUI associated to the i_th society
	 * @param i : number of the society to change
	 */
	private void Modify_Society(String s_id){
		int i;
		for(i = 0; i < societies.length; i++)
			if(societies[i] != null && societies[i].getObjectId().equals(s_id))
					break;
		SocietyGUI society_gui = new SocietyGUI(this, societies[i], i, new Environment(this.graph, societies));
		society_gui.setVisible(true);
	}

	/**
	 * This method create a new graph and launches the associated GUI
	 */
	private void Create_Graph() {
		this.graph = new Graph("", new Node[0]);
		Modify_Graph();
		
	}
	
	/**
	 * This method launches the GUI associated to the graph
	 *
	 */
	private void Modify_Graph() {
		GraphGUI graph_gui = new GraphGUI(this, this.graph);
		graph_gui.setVisible(true);
		
	}


	
	/**
	 * This method is used by SocietyGUI to save changes before closing
	 */
	public void setSociety(int soc_num, Society soc){
		societies[soc_num] = soc;
		saved = false;
		Refresh_buttons();
	}

	public void setGraph(Graph graph2) {
		graph = graph2;
		saved = false;
		Refresh_buttons();
		
	}
	
	
	public static void main(String args[]) {
		Editor myeditor = new Editor();
		myeditor.setVisible(true);
		
	}
}  //  @jve:decl-index=0:visual-constraint="10,10"
