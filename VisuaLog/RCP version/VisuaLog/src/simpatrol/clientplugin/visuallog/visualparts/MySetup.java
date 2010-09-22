				
package simpatrol.clientplugin.visuallog.visualparts;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.eclipse.jface.wizard.Wizard;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

import simpatrol.clientplugin.visuallog.Application;
import simpatrol.clientplugin.visuallog.logger.ConnectedTurnLog;
import simpatrol.clientplugin.visuallog.logger.ReplayRealTimeLog;
import simpatrol.clientplugin.visuallog.logger.ReplayTurnLog;


/* * * * * * * * * * * * * * * * * * * * * * * *
 * 
 *    Setup wizard
 *    
 *    @author : Cyril Poulet
 * 
 * * * * * * * * * * * * * * * * * * * * * * * */
public class MySetup extends Wizard {

	private MyPageOne one;

	/**
	 * Constructor
	 */
	public MySetup() {
		super();
		setNeedsProgressMonitor(true);
	}

	@Override
	public void addPages() {
		one = new MyPageOne();
		addPage(one);
	}

	
	/**
	 * checks the input, then create the log viewer and configures it
	 * if an error is detected, the wizard doesn't close and a message appears
	 */
	@Override
	public boolean performFinish() {
		if(!one.VerifyInput())
			return false;
		
		String[] results = one.GetInput();
		int log_type = Integer.valueOf(results[0]);
		Application.canvas.clear();
		try {
			switch(log_type){
			case 0:
				int port_num = Integer.valueOf(results[3]);
				Application.logger = new ConnectedTurnLog(Application.canvas, results[2], port_num, results[1]);
				break;
			case 1:
				Application.logger = new ReplayTurnLog(Application.canvas, results[1]);
				break;
			case 2:
				Application.logger = new ReplayRealTimeLog(Application.canvas, results[1]);
				break;
			}
		} catch (Exception e){
			MessageBox box = new MessageBox(this.getShell(), SWT.ICON_ERROR | SWT.ABORT);
			box.setText("Error");
			box.setMessage("Could not create the viewer. Please check the parameters.");
			box.open();
			return false;
		}
		
		Application.canvas.configure_canvas();
		Application.logger.start();

		return true;
	}
	
	
	
	/* * * * * * * * * * * * * * * * * * * * * * * *
	 * 
	 *    Setup wizard page
	 *    
	 *    @author : Cyril Poulet
	 * 
	 * * * * * * * * * * * * * * * * * * * * * * * */
	private class MyPageOne extends WizardPage {
		
		
		/* internal components */
		
		private Combo logger_type;
		private Composite container;
		private Shell shell;
		
		private Label file_path_lbl;
		private Text file_path;
		private Button file_path_btn;
		private Label ip_adress_lbl;
		private Text ip_adress;
		private Label port_lbl;
		private Text port;
		
		private Composite file_group;
		private Composite ip_group;
		private Composite port_group;

		
		/**
		 * Constructor
		 */
		public MyPageOne() {
			super("Setup");
			setTitle("Setup");
			setDescription("This wizard allows you to choose and configure the type of viewer you want to use.");
		}

		
		/**
		 * creates the layout,
		 * adds the listeners
		 */
		@Override
		public void createControl(Composite parent) {			
			shell = this.getShell();
			container = new Composite(parent, SWT.NULL);
			GridLayout layout = new GridLayout();
			container.setLayout(layout);
			layout.numColumns = 1;
			
			
			Composite combo_group = new Composite(container, SWT.None);
			combo_group.setLayout(new RowLayout(SWT.HORIZONTAL));
			Label label1 = new Label(combo_group, SWT.NULL);
			label1.setText("Choose the type of viewer :");
			
			String view_types[] = {"Connected Turn-by-Turn viewer", "Replay Turn-by-Turn viewer", "Replay Real-Time viewer"};
			logger_type = new Combo(combo_group, SWT.READ_ONLY);
			logger_type.setItems(view_types);
			logger_type.addSelectionListener(new SelectionAdapter(){
													public void widgetSelected(SelectionEvent e){
														setPageComplete(false);
														PageComplete();
														if(logger_type.getSelectionIndex() == 0){
															file_path_lbl.setVisible(true);
															file_path.setVisible(true);
															file_path_btn.setVisible(true);
															file_path_lbl.setText("Choose the file where you want the log to be saved :");
															
															file_group.layout(true);
															
															ip_group.setVisible(true);
															port_group.setVisible(true);
															
															
															container.layout(true);
															
															
														} else {
															file_path_lbl.setVisible(true);
															file_path.setVisible(true);
															file_path_btn.setVisible(true);
															file_path_lbl.setText("Choose the log file to play :");
															
															file_group.layout(true);
															
															ip_group.setVisible(false);
															port_group.setVisible(false);
															
															container.layout(true);
															
														}
													}
												});
			
			
			file_group = new Composite(container, SWT.NONE);
			file_group.setLayout(new RowLayout(SWT.VERTICAL));
			file_path_lbl = new Label(file_group, SWT.NONE);
			file_path_lbl.setVisible(false);
			Composite file_subgroup = new Composite(file_group, SWT.NONE);
			file_subgroup.setLayout(new GridLayout(2, false));
			file_path = new Text(file_subgroup, SWT.BORDER | SWT.SINGLE);
			file_path.setText("");
			GridData gd = new GridData(GridData.FILL_BOTH);
			gd.widthHint = 400;
			gd.heightHint = 15;
			file_path.setLayoutData(gd);
			file_path.addKeyListener(new KeyListener() {

				@Override
				public void keyPressed(KeyEvent e) {
				}

				@Override
				public void keyReleased(KeyEvent e) {
						PageComplete();
				}

			});
			file_path.setVisible(false);
			file_path_btn = new Button(file_subgroup, SWT.PUSH);
			file_path_btn.setText("File");
			file_path_btn.addSelectionListener(new SelectionListener(){
													public void widgetSelected(SelectionEvent e){
														FileDialog fd = new FileDialog(shell, SWT.OPEN);
														fd.setText("File");
														String selected = fd.open();
														file_path.setText(selected);
														PageComplete();
														
													}

													@Override
													public void widgetDefaultSelected(SelectionEvent e) {
														// TODO Auto-generated method stub
														
													}
												});
			GridData gd2 = new GridData(GridData.FILL_BOTH);
			gd2.widthHint = 100;
			gd2.heightHint = 15;
			file_path_btn.setLayoutData(gd2);
			file_path_btn.setVisible(false);
			
			ip_group = new Composite(container, SWT.NONE);
			ip_group.setLayout(new GridLayout(2, false));
			ip_adress_lbl = new Label(ip_group, SWT.NONE);
			ip_adress_lbl.setText("IP address of the SimPatrol Server :");
			GridData gd3 = new GridData();
			gd3.widthHint = 250;
			gd3.heightHint = 15;
			ip_adress_lbl.setLayoutData(gd3);
			ip_adress = new Text(ip_group, SWT.BORDER | SWT.SINGLE);
			ip_adress.setText("");
			GridData gd4 = new GridData();
			gd4.widthHint = 200;
			gd4.heightHint = 15;
			ip_adress.setLayoutData(gd4);
			ip_adress.addKeyListener(new KeyListener() {

				@Override
				public void keyPressed(KeyEvent e) {
				}

				@Override
				public void keyReleased(KeyEvent e) {
						PageComplete();
				}

			});
			ip_group.setVisible(false);
			
			
			port_group = new Composite(container, SWT.NONE);
			port_group.setLayout(new GridLayout(2, false));
			port_lbl = new Label(port_group, SWT.NONE);
			port_lbl.setText("Connecting port :");
			port_lbl.setLayoutData(gd3);
			port = new Text(port_group, SWT.BORDER | SWT.SINGLE);
			port.setText("");
			GridData gd5 = new GridData();
			gd5.widthHint = 100;
			gd5.heightHint = 15;
			port.setLayoutData(gd5);
			port.addKeyListener(new KeyListener() {

				@Override
				public void keyPressed(KeyEvent e) {
				}

				@Override
				public void keyReleased(KeyEvent e) {
						PageComplete();
				}

			});
			port_group.setVisible(false);
			
			
			
			
			
			
			
			container.pack();
			setControl(container);
			setPageComplete(false);

		}
		
		/**
		 * checks if the fields are completed
		 */
		public void PageComplete(){
			boolean complete = true;
			complete &= !file_path.getText().contentEquals("");
			if(logger_type.getSelectionIndex() == 0){
				complete &= !ip_adress.getText().contentEquals("");
				complete &= !port.getText().contentEquals("");
			}
			
			setPageComplete(complete);
				
		}

		/**
		 * checks if the inputs matches regex (port, ipv4 address)
		 *  
		 * @return true if no problem was detected
		 */
		public boolean VerifyInput() {
			boolean verified = true;
			String error_message = "";
			
			if(logger_type.getSelectionIndex() == 0){
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
				MessageBox box = new MessageBox(shell, SWT.ICON_ERROR | SWT.ABORT);
				box.setText("Error in the fields");
				box.setMessage(error_message);
				box.open();
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
			if(logger_type.getSelectionIndex() == 0){
				results = new String[]{String.valueOf(logger_type.getSelectionIndex()), file_path.getText(), ip_adress.getText(), port.getText()};
				return results;
			} else
				results = new String[]{String.valueOf(logger_type.getSelectionIndex()), file_path.getText()};
				return results;
		}
	}
}
