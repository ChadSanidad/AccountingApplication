package accountingApp;

import java.awt.BorderLayout;
import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

import loader.SQLoader;
import oracleConnection.ConnectionSettings;
import oracleConnection.DatabaseConn;

import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTextField;
import javax.swing.JButton;
import javax.swing.JFileChooser;

import java.awt.event.ActionListener;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.SQLException;
import java.awt.event.ActionEvent;
import javax.swing.JTextPane;

public class IntegrationApp extends JFrame {

	private JPanel contentPane;
	private final JLabel lblNewLabel = new JLabel("Step 1: Payroll Load Okay?");
	private JTextField binaryField;
	private JTextField txtField;
	private JButton btnCreate;
	private JButton btnDo;
	private JButton btnExport;
	private JButton OK;
	private JTextPane messagePane;
	DatabaseConn dc;
	JFileChooser fc;
	
	SQLoader loader;
	
	String txtPath;
	String loaderPath;
	String username;
	String password;
	String checkPayroll;
	String exportPath;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					IntegrationApp frame = new IntegrationApp();
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the frame.
	 */
	public IntegrationApp() {
		setTitle("Integration Assignment");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 619, 451);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);
		lblNewLabel.setBounds(12, 26, 171, 36);
		contentPane.add(lblNewLabel);
		
		JLabel lblStepCreate = new JLabel("Step 2: Create Delimited from Binary?");
		lblStepCreate.setBounds(12, 75, 221, 36);
		contentPane.add(lblStepCreate);
		
		JLabel lblBinaryFile = new JLabel("Binary File:");
		lblBinaryFile.setBounds(40, 124, 78, 36);
		contentPane.add(lblBinaryFile);
		
		JLabel lblDelimitedFile = new JLabel("Delimited File:");
		lblDelimitedFile.setBounds(23, 173, 99, 36);
		contentPane.add(lblDelimitedFile);
		
		binaryField = new JTextField();
		binaryField.setBounds(116, 131, 476, 22);
		contentPane.add(binaryField);
		binaryField.setColumns(10);
		
		txtField = new JTextField();
		txtField.setColumns(10);
		txtField.setBounds(116, 180, 476, 22);
		contentPane.add(txtField);
		
		JLabel lblStepPerform = new JLabel("Step 3: Perform Month End?");
		lblStepPerform.setBounds(12, 235, 164, 36);
		contentPane.add(lblStepPerform);
		
		JLabel lblStepExport = new JLabel("Step 4: Export Data");
		lblStepExport.setBounds(12, 288, 164, 36);
		contentPane.add(lblStepExport);
		//********************************************* Button 1 ******************************************************/
		JButton btnCheck = new JButton("Check");
		btnCheck.addActionListener(new ActionListener() 
		{
			public void actionPerformed(ActionEvent e) 
			{
				username = JOptionPane.showInputDialog("Enter a username: ");
				password = JOptionPane.showInputDialog("Enter a password: ");
				
				ConnectionSettings cs = new ConnectionSettings(username, password); 
				try
				{
					dc = new DatabaseConn();
					dc.setConnection(username, password);	

					checkPayroll =  dc.checkPayroll();
					if(checkPayroll.toUpperCase().equals("Y"))
					{
						dc.setConnection(username, password);
						dc.setPayrollFlagN();
						messagePane.setText("Hello, " + username + " Please select a bin file next.");
						btnCreate.setEnabled(true);
						btnCheck.setEnabled(false);
						dc.closeConnection();
					}
					else if(checkPayroll.toUpperCase().equals("N"))
					{
						messagePane.setText("Cannot process file.. already in use");
					}
					else
					{
						messagePane.setText("Something went wrong, please check the database and try again");
					}
				}
				catch(SQLException ex)
				{
					messagePane.setText("Invalid Credentials");
				} catch (ClassNotFoundException e1) 
				{	
					messagePane.setText("Something went wrong here, please reload and try again.");
				}
			}
		});
		btnCheck.setBounds(245, 32, 97, 25);
		contentPane.add(btnCheck);
		//********************************************* Button 2 ******************************************************/
		btnCreate = new JButton("Create");
		btnCreate.setEnabled(false);
		btnCreate.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) 
			{
				try
				{
					dc.setConnection(username, password);
					OK = new JButton();
					fc = new JFileChooser();
					fc.setCurrentDirectory(new java.io.File("C:"));
					messagePane.setText("Choose the path and filename to create a bin file.");
					fc.setDialogTitle("Choose bin file");
					fc.setFileSelectionMode(JFileChooser.FILES_ONLY);
					if(fc.showOpenDialog(OK) == JFileChooser.APPROVE_OPTION)
					{
						binaryField.setText(fc.getSelectedFile().getAbsolutePath());
					}
					
					JButton open = new JButton();
					
					messagePane.setText("Choose the path and filename to create a text file.");
					fc.setDialogTitle("Choose text file");
					fc.setCurrentDirectory(new java.io.File("loader.txt"));
					fc.setFileSelectionMode(JFileChooser.FILES_ONLY);
					if(fc.showOpenDialog(OK) == JFileChooser.APPROVE_OPTION)
					{
						txtField.setText(fc.getSelectedFile().getAbsolutePath());
					}
					
					PayRollRAF praf = new PayRollRAF();
					praf.getPraf(binaryField.getText());
					praf.writeTxt(txtField.getText());
					messagePane.setText("Loader.");
		
					JButton openLoader = new JButton();
					
					messagePane.setText("Choose the path and filename to create the loader controls.");
					
					fc.setDialogTitle("Choose the loader:");
					fc.setCurrentDirectory(new java.io.File("loader.txt"));
					fc.setFileSelectionMode(JFileChooser.FILES_ONLY);

						if(fc.showOpenDialog(openLoader) == JFileChooser.APPROVE_OPTION)
						{
							loader = new SQLoader();
							loaderPath = fc.getSelectedFile().getAbsolutePath();
							loader.createLoader(loaderPath,txtField.getText());	
							messagePane.setText("Loader file created, writing bin file to database.");
							loader.executeLoader(username, password, loaderPath);
							messagePane.setText("Text file written to database, You can now perform month end processes");
							dc.setPayrollFlagY();
						}
						btnDo.setEnabled(true);
						btnCreate.setEnabled(false);
						dc.closeConnection();


					
				}
				catch(SQLException | ClassNotFoundException exc)
				{
					messagePane.setText("Something went wrong here, please reload and try again.");
				}
				catch(FileNotFoundException fnfx)
				{
					messagePane.setText("Error choosing a file, please try again");
				} catch (IOException e1) {
					
					messagePane.setText("Error choosing a file, please try again");
				}
			}
		});
		btnCreate.setBounds(245, 81, 97, 25);
		contentPane.add(btnCreate);
		
		
		//********************************************* Button 3 ******************************************************/
		btnDo = new JButton("Do");
		btnDo.setEnabled(false);
		btnDo.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) 
			{
				try
				{
					dc.setConnection(username, password);
					String monthEnd;
					monthEnd =  dc.checkMonthEnd();
					if(monthEnd.toUpperCase().equals("Y"))
					{
						messagePane.setText("Month End processes can be performed. Starting....");
						dc.setConnection(username, password);
						dc.setMonthEndN();
						messagePane.setText("Zeroing account balances");
						dc.setConnection(username, password);
						dc.zeroAccounts();
						messagePane.setText("Accounts processed, file can now be exported.");
	
						dc.setConnection(username, password);
						dc.setMonthEndY();
						dc.closeConnection();
						
						btnExport.setEnabled(true);
						btnDo.setEnabled(false);
					}
					else
					{
						messagePane.setText("Cannot perform any functions at the moment. Please wait a minute");
					}
				}
				catch(SQLException| ClassNotFoundException ex2)
				{
					messagePane.setText("Something went wrong here, please reload and try again.");
				}

			}
		});
		btnDo.setBounds(238, 241, 97, 25);
		contentPane.add(btnDo);
		//********************************************* Button 4 ******************************************************/
		btnExport = new JButton("Export");
		btnExport.setEnabled(false);
		btnExport.addActionListener(new ActionListener() 
		{
			public void actionPerformed(ActionEvent e) 
			{
				try
				{
					messagePane.setText("Choose the path that you want the report to go into");
					fc.setCurrentDirectory(new java.io.File("C:"));
					
					fc.setDialogTitle("Choose exported file path");
					fc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
					if(fc.showOpenDialog(OK) == JFileChooser.APPROVE_OPTION)
					{
						exportPath = fc.getSelectedFile().getAbsolutePath();
					}
					
					messagePane.setText("Next choose the name of the file");
					String delimitedFile = JOptionPane.showInputDialog("Input name of the file (___.txt):");
					
					messagePane.setText("Choose the alias you want for the directory");
					String alias = JOptionPane.showInputDialog("Input name of the alias:");
					
					dc.setConnection(username, password);
					dc.createExport(alias, exportPath);
					dc.setConnection(username, password);
					dc.writeExport(delimitedFile, alias);
					
					messagePane.setText("Report written, you may now close the program.");
					dc.closeConnection();
				}
				catch(SQLException | ClassNotFoundException e3)
				{
					messagePane.setText("Something went wrong here, please reload and try again.");
				}
			}
		});
		btnExport.setBounds(238, 294, 97, 25);
		contentPane.add(btnExport);
		
		JButton btnClose = new JButton("Close");
		btnClose.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) 
			{
				System.exit(0);
			}
		});
		btnClose.setBounds(492, 373, 97, 23);
		contentPane.add(btnClose);
		
		messagePane = new JTextPane();
		messagePane.setText("Hit check to try and access the database.");
		messagePane.setBounds(12, 373, 439, 22);
		contentPane.add(messagePane);
	}
}
