package main;

import java.awt.ComponentOrientation;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;


import visualparts.VisualCanvasMouseListener;
import viewers.ReplayLog;
import viewers.AbstractLog;
import viewers.ReplayRealTimeLog;
import viewers.ReplayTurnLog;
import visualparts.MySetup;
import visualparts.VisualCanvas;

@SuppressWarnings("serial")
public class VisuaLog extends javax.swing.JFrame implements ActionListener{

	/* internal elements */
	
	private AbstractLog logger;
	private VisualCanvas canvas;
	private JMenuBar jJMenuBar;
	private JMenu FileMenu;
	private JMenuItem SetupWizard;
	private JMenuItem Close;
	
	private JPanel button_panel;
	private JButton play_button;
	private JButton pause_button;
	private JButton advance_button;
	private JButton back_button;
	
	
	
	public AbstractLog getLogger(){
		return logger;
	}
	
	public void setLogger(AbstractLog log){
		logger = log;
	}
	
	public VisualCanvas getCanvas(){
		return canvas;
	}
	
	public VisuaLog() {
		super();
		initialize();
	}

	/**
	 * This method initializes this
	 * 
	 */
	private void initialize() {
        this.setSize(new Dimension(500, 400));
        this.setResizable(true);
        this.setJMenuBar(getJJMenuBar());
        this.setTitle("VisuaLog");
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        
        this.initComponents();

			
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
			FileMenu.setText("Commands");
			FileMenu.add(getSetupWizard());
			FileMenu.add(getClose());

		}
		return FileMenu;
	}
	
	/**
	 * This method initializes OpenFile	
	 * 	
	 * @return javax.swing.JMenuItem	
	 */
	private JMenuItem getSetupWizard() {
		if (SetupWizard == null) {
			SetupWizard = new JMenuItem();
			SetupWizard.setText("Setup");
			SetupWizard.addActionListener(this);
		}
		return SetupWizard;
	}
	
	private JMenuItem getClose() {
		if (Close == null) {
			Close = new JMenuItem();
			Close.setText("Close");
			Close.addActionListener(this);
		}
		return Close;
	}
	
	
    private void initComponents() {
    	
    	this.getContentPane().setLayout(new BoxLayout(this.getContentPane(), BoxLayout.PAGE_AXIS));
    	
    	canvas = new VisualCanvas(this);
    	new VisualCanvasMouseListener(canvas);
    	canvas.setMinimumSize(new Dimension(this.getSize().width -10, this.getSize().height - 100));
    	this.getContentPane().add(canvas);
    	
    	
    	button_panel = new JPanel();
    	button_panel.setLayout(new BoxLayout(button_panel, BoxLayout.LINE_AXIS));
    	button_panel.setComponentOrientation(ComponentOrientation.RIGHT_TO_LEFT);
    	button_panel.setMaximumSize(new Dimension(220,30));
    	
    	play_button = new JButton();
    	play_button.setIcon(new ImageIcon("icons/bouton_play.png"));
    	play_button.setActionCommand("Play");
    	play_button.addActionListener(this);
    	play_button.setMaximumSize(new Dimension(30,30));
    	
    	pause_button = new JButton();
    	pause_button.setIcon(new ImageIcon("icons/bouton_pause.png"));
    	pause_button.setActionCommand("Pause");
    	pause_button.addActionListener(this);
    	pause_button.setMaximumSize(new Dimension(30,30));
    	
    	advance_button = new JButton();
    	advance_button.setIcon(new ImageIcon("icons/bouton_avance.png"));
    	advance_button.setActionCommand("Advance");
    	advance_button.addActionListener(this);
    	advance_button.setMaximumSize(new Dimension(30,30));
    	
    	back_button = new JButton();
    	back_button.setIcon(new ImageIcon("icons/bouton_retour.png"));
    	back_button.setActionCommand("Back");
    	back_button.addActionListener(this);
    	back_button.setMaximumSize(new Dimension(30,30));
    	
    	button_panel.add(advance_button);
    	button_panel.add(play_button);
    	button_panel.add(pause_button);
    	button_panel.add(back_button);
    	
    	this.getContentPane().add(button_panel);
    	
    	
    }
	
	
	public void actionPerformed(ActionEvent e) {
		if(e.getSource() == SetupWizard){
			MySetup setup = new MySetup(this);
			setup.setVisible(true);
		}
		if(e.getSource() == Close){
			this.dispose();
		}
		if(e.getSource() == play_button){
			synchronized(logger){
				
				String logger_type = logger.getType();
				
				if(logger_type.contains("replay")){
					if(logger_type.contains("turn")){
						if(!((ReplayTurnLog) logger).isPlaying())
							((ReplayTurnLog)logger).play();
						
						((ReplayTurnLog)logger).setSpeed(1);
					}
					else {
						if(!((ReplayRealTimeLog) logger).isPlaying())
							((ReplayRealTimeLog)logger).play();
						
						((ReplayRealTimeLog)logger).setSpeed(1);
					}
				}
			}
		}
		if(e.getSource() == pause_button){
			synchronized(logger){
				
				String logger_type = logger.getType();
				
				if(logger_type.contains("replay")){
					if(logger_type.contains("turn")){
						if(((ReplayTurnLog) logger).isPlaying())
							((ReplayTurnLog)logger).pause();
						else
							((ReplayTurnLog)logger).play();
					}
					else {
						if(((ReplayRealTimeLog) logger).isPlaying())
							((ReplayRealTimeLog)logger).pause();
						else
							((ReplayRealTimeLog)logger).play();
					}
				}
			}
		}
		if(e.getSource() == advance_button){
			String logger_type = logger.getType();
			
			if(logger_type.contains("replay")){
				if(logger_type.contains("turn"))
					((ReplayTurnLog)logger).setSpeed(((ReplayTurnLog)logger).getSpeed() *2);
				else
					((ReplayRealTimeLog)logger).setSpeed(((ReplayRealTimeLog)logger).getSpeed() *2);
			}
		}
		if(e.getSource() == back_button){
			String logger_type = logger.getType();
			
			if(logger_type.contains("replay")){
				if(logger_type.contains("turn"))
					((ReplayTurnLog)logger).setSpeed(((ReplayTurnLog)logger).getSpeed() /2);
				else 
					((ReplayRealTimeLog)logger).setSpeed(((ReplayRealTimeLog)logger).getSpeed() /2);
			}
		}
		
	}
	
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		VisuaLog visualog = new VisuaLog();
		visualog.setVisible(true);
		
		//MySetup setup = new MySetup();
		//setup.setVisible(true);

	}

}
