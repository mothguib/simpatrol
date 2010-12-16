				
package visualparts;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.File;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;

import main.VisuaLog;


import viewers.ConnectedTurnLog;
import viewers.ReplayRealTimeLog;
import viewers.ReplayTurnLog;


/* * * * * * * * * * * * * * * * * * * * * * * *
 * 
 *    Setup wizard
 *    
 *    @author : Cyril Poulet
 * 
 * * * * * * * * * * * * * * * * * * * * * * * */
@SuppressWarnings("serial")
public class MySetup extends JDialog implements ActionListener {
		
	
	VisuaLog visualog;
	
	/* internal components */
	
	private JComboBox logger_type;
	private JPanel container;
	private JPanel button_panel;
	
	private JLabel file_path_lbl;
	private JTextField file_path;
	private JButton file_path_btn;
	private JLabel ip_adress_lbl;
	private JTextField ip_adress;
	private JLabel port_lbl;
	private JTextField port;

	
	private JButton validate;
	private JButton cancel;

	
	/**
	 * Constructor
	 */
	public MySetup(VisuaLog vl) {
		super();
		setTitle("Setup");
		this.setSize(new Dimension(540, 420));
		this.setResizable(false);
		this.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		visualog = vl;
		
		createControl();
	}

	
	/**
	 * creates the layout,
	 * adds the listeners
	 */
	public void createControl() {	
		this.getContentPane().setLayout(new BoxLayout(this.getContentPane(), BoxLayout.PAGE_AXIS));
		
		container = new JPanel();
		container.setLayout(new GridBagLayout());
		GridBagConstraints c = new GridBagConstraints();
		
		
		KeyListener textlistener = new KeyListener(){

			@Override
			public void keyPressed(KeyEvent arg0) {
				PageComplete();
				
			}

			@Override
			public void keyReleased(KeyEvent arg0) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void keyTyped(KeyEvent arg0) {
				// TODO Auto-generated method stub
				
			}
			
		};
		
		
		JLabel instructions = new JLabel("This wizard allows you to choose and configure the type of viewer you want to use.");
		instructions.setMinimumSize(new Dimension(1000, 200));
		c.fill = GridBagConstraints.HORIZONTAL;
		c.gridwidth = 3;
		c.ipady = 0;
		c.gridx = 0;
		c.gridy = 0;
		c.insets = new Insets(30,0,80,0);
		c.anchor = GridBagConstraints.LINE_START;
		container.add(instructions, c);

		
		JLabel label1 = new JLabel();
		label1.setText("Choose the type of viewer :");	
		c.ipady = 5;
		c.insets = new Insets(0,0,20,0);
		c.gridwidth = 1;
		c.gridx = 0;
		c.gridy = 1;
		container.add(label1, c);
		
		String view_types[] = {"Connected Turn-by-Turn viewer", "Replay Turn-by-Turn viewer", "Replay Real-Time viewer"};
		logger_type = new JComboBox(view_types);
		//logger_type.setMaximumSize(new Dimension(400, 25));
		logger_type.setSelectedIndex(0);
		logger_type.addActionListener(this);
		c.gridx = 1;
		c.gridy = 1;
		container.add(logger_type, c);
		
		file_path_lbl = new JLabel("Choose the file where you want the log to be saved :");
		c.gridwidth = 2;
		c.gridx = 0;
		c.gridy = 2;
		container.add(file_path_lbl, c);
		file_path = new JTextField();
		file_path.setMaximumSize(new Dimension(400, 25));
		file_path.setText("");
		file_path.addKeyListener(textlistener);
		c.gridwidth = 2;
		c.gridx = 0;
		c.gridy = 3;
		container.add(file_path, c);
		file_path_btn = new JButton();
		file_path_btn.setText("File");
		file_path_btn.addActionListener(this);
		file_path_btn.setMaximumSize(new Dimension(100,25));
		c.gridwidth = 1;
		c.gridx = 2;
		c.gridy = 3;
		container.add(file_path_btn, c);
		
		
		ip_adress_lbl = new JLabel();
		ip_adress_lbl.setText("IP address of the SimPatrol Server :");
		ip_adress_lbl.setMaximumSize(new Dimension(250,25));
		c.gridx = 0;
		c.gridy = 4;
		container.add(ip_adress_lbl, c);
		ip_adress = new JTextField();
		ip_adress.setMaximumSize(new Dimension(200,25));
		ip_adress.addKeyListener(textlistener);
		c.gridx = 1;
		c.gridy = 4;
		container.add(ip_adress, c);
		
		
		port_lbl = new JLabel();
		port_lbl.setText("Connecting port :");
		port_lbl.setMaximumSize(new Dimension(250,25));
		c.gridx = 0;
		c.gridy = 5;
		container.add(port_lbl, c);
		port = new JTextField();
		port.setMaximumSize(new Dimension(100,25));
		port.addKeyListener(textlistener);
		
		c.gridx = 1;
		c.gridy = 5;
		container.add(port, c);
		
		this.getContentPane().add(container);
		
		
		
		button_panel = new JPanel();
		button_panel.setLayout(new BoxLayout(button_panel, BoxLayout.LINE_AXIS));
		validate = new JButton("Ok");
		validate.setEnabled(false);
		validate.addActionListener(this);
		cancel = new JButton("Close");
		cancel.addActionListener(this);
		button_panel.add(cancel);
		button_panel.add(validate);
		
		this.getContentPane().add(button_panel);

	}
	
	/**
	 * checks if the fields are completed
	 */
	public void PageComplete(){
		boolean complete = true;
		complete &= !file_path.getText().contentEquals("");
		if(logger_type.getSelectedIndex() == 0){
			complete &= !ip_adress.getText().contentEquals("");
			complete &= !port.getText().contentEquals("");
		}
		
		validate.setEnabled(complete);
		
		//setPageComplete(complete);
		
			
	}

	/**
	 * checks if the inputs matches regex (port, ipv4 address)
	 *  
	 * @return true if no problem was detected
	 */
	public boolean VerifyInput() {
		boolean verified = true;
		String error_message = "";
		
		if(logger_type.getSelectedIndex() == 0){
			Pattern pattern = Pattern.compile("(\\d{1,3}\\.){3}(\\d{1,3})");
			Matcher matcher = pattern.matcher(ip_adress.getText());
			if(matcher.find() && (matcher.start() == 0) && (matcher.end() == ip_adress.getText().length()))
				verified &= true;
			else {
				verified &= false;
				error_message += "The IP address does not have the right format. \n";
			}
			
			pattern = Pattern.compile("\\D");
			matcher = pattern.matcher(port.getText());
			if(matcher.find()){
				verified &= false;
				error_message += "The port number contains a non-digit character. \n";
			}
		}
		
		if(!verified){
			JOptionPane.showMessageDialog(this, "Error in the fields");
		}
		
		return verified;
	}
	
	
	/**
	 * gets the various inputs of the page
	 * 
	 * @return a String[] containing the inputs : type of viewer, file path (, ip address, port)
	 */
	public String[] GetInput(){
		String[] results;
		if(logger_type.getSelectedIndex() == 0){
			results = new String[]{String.valueOf(logger_type.getSelectedIndex()), file_path.getText(), ip_adress.getText(), port.getText()};
			return results;
		} else
			results = new String[]{String.valueOf(logger_type.getSelectedIndex()), file_path.getText()};
			return results;
	}
	
	
	/**
	 * checks the input, then create the log viewer and configures it
	 * if an error is detected, the wizard doesn't close and a message appears
	 */
	public boolean performFinish() {
		if(!this.VerifyInput())
			return false;
		
		String[] results = this.GetInput();
		int log_type = Integer.valueOf(results[0]);
		visualog.getCanvas().clear();
		try {
			switch(log_type){
			case 0:
				int port_num = Integer.valueOf(results[3]);
				visualog.setLogger(new ConnectedTurnLog(visualog.getCanvas(), results[2], port_num, results[1]));
				break;
			case 1:
				visualog.setLogger(new ReplayTurnLog(visualog.getCanvas(), results[1]));
				break;
			case 2:
				visualog.setLogger(new ReplayRealTimeLog(visualog.getCanvas(), results[1]));
				break;
			}
		} catch (Exception e){
			JOptionPane.showMessageDialog(this, "Could not create the viewer. Please check the parameters.");
			return false;
		}
		
		visualog.getCanvas().configure_canvas();
		visualog.getLogger().start();

		return true;
	}


	@Override
	public void actionPerformed(ActionEvent e) {
		if(e.getSource() == logger_type){
			//setPageComplete(false);
			PageComplete();
			if(logger_type.getSelectedIndex() == 0){
				file_path_lbl.setVisible(true);
				file_path.setVisible(true);
				file_path_btn.setVisible(true);
				file_path_lbl.setText("Choose the file where you want the log to be saved :");
				
				ip_adress_lbl.setVisible(true);
				ip_adress.setVisible(true);
				port_lbl.setVisible(true);
				port.setVisible(true);
				
				
			} else {
				file_path_lbl.setVisible(true);
				file_path.setVisible(true);
				file_path_btn.setVisible(true);
				file_path_lbl.setText("Choose the log file to play :");
				
				ip_adress_lbl.setVisible(false);
				ip_adress.setVisible(false);
				port_lbl.setVisible(false);
				port.setVisible(false);

				
			}
		}
		
		if(e.getSource() == file_path_btn){
			JFileChooser fd = new JFileChooser(".");
			int validate = fd.showOpenDialog(this);
			
			if(validate == JFileChooser.APPROVE_OPTION){
				File file = fd.getSelectedFile();
				file_path.setText(file.getPath());
				PageComplete();
			}
		}
		if(e.getSource() == cancel){
			dispose();
		}
		if(e.getSource() == validate){
			if(performFinish()){
				dispose();
			}
		}
		
	}
}
