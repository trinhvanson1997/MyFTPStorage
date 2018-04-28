package views;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.net.SocketException;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;

import org.apache.commons.net.ftp.FTPClient;

public class LoginBox extends JFrame {
	private FTPClient ftpClient;
	private JTextField tfHostname, tfUsername, tfPassword, tfPort;
	private JButton btnConnect;

	private String hostname,username,password;
	private int port;
	
	public LoginBox() {
		setTitle("Login");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setSize(300, 500);
		setLocationRelativeTo(null);
		setLayout(new BorderLayout(5, 5));

		add(createLabelsPanel(), BorderLayout.WEST);
		add(createTextfieldsPanel(), BorderLayout.CENTER);
		add(createButtonPanel(), BorderLayout.SOUTH);

		createActions();
		pack();
		setVisible(true);
	}

	private void createActions() {
		btnConnect.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				if (tfHostname.getText().equals(null) || tfHostname.getText().equals("")
						|| tfUsername.getText().equals(null) || tfUsername.getText().equals("")) {
					JOptionPane.showMessageDialog(null, "Please fill out hostname and username!");
				} else {
					boolean success;
					try {
						hostname = tfHostname.getText();
						username = tfUsername.getText();
						password = tfPassword.getText();
						port = Integer.parseInt(tfPort.getText());
								
						ftpClient = new FTPClient();
						ftpClient.connect(hostname, port);
					
						success = ftpClient.login(username, password);
						if(success) {
							ftpClient.enterLocalPassiveMode();
							ftpClient.setFileType(ftpClient.BINARY_FILE_TYPE);
							
							System.out.println("Connected and Logged in to server !");
							JOptionPane.showMessageDialog(null, "Logged in successfully");
					dispose();
							MainUI mainUI = new MainUI(ftpClient, hostname, username, port);
						
							
						
						}
						else {
							JOptionPane.showMessageDialog(null, "Failed to log in");
						}
						
						
					} catch (NumberFormatException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (SocketException e) {
						// TODO Auto-generated catch block
						JOptionPane.showMessageDialog(null, "Cannot connect to server");
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}
		});
	}

	private JPanel createLabelsPanel() {
		JPanel p = new JPanel(new GridLayout(4, 1, 5, 5));
		p.setBorder(new EmptyBorder(10, 10, 10, 10));
		p.add(new JLabel("Host Name"));
		p.add(new JLabel("Username"));
		p.add(new JLabel("Password"));
		p.add(new JLabel("Port"));

		return p;
	}

	private JPanel createTextfieldsPanel() {
		JPanel p = new JPanel(new GridLayout(4, 1, 5, 5));
		p.setBorder(new EmptyBorder(10, 10, 10, 10));
		tfHostname = new JTextField();
/*		tfHostname.setText("ftp.freevnn.com");
		tfUsername = new JTextField();
		tfUsername.setText("freev_21943005");
		tfPassword = new JTextField();
		tfPassword.setText("hslove");*/
		
		tfHostname.setText("127.0.0.1");
		tfUsername = new JTextField();
		tfUsername.setText("son");
		tfPassword = new JTextField();
		tfPassword.setText("");
		
		tfPort = new JTextField();
		tfPort.setText("21");

		p.add(tfHostname);
		p.add(tfUsername);
		p.add(tfPassword);
		p.add(tfPort);

		return p;
	}

	private JPanel createButtonPanel() {
		JPanel p = new JPanel(new BorderLayout(5, 5));
		p.setBorder(new EmptyBorder(10, 100, 10, 100));

		btnConnect = new JButton("Connect to server");

		p.add(btnConnect, BorderLayout.CENTER);
		return p;

	}

	public FTPClient getFtpClient() {
		return ftpClient;
	}

	public void setFtpClient(FTPClient ftpClient) {
		this.ftpClient = ftpClient;
	}

	public JTextField getTfHostname() {
		return tfHostname;
	}

	public void setTfHostname(JTextField tfHostname) {
		this.tfHostname = tfHostname;
	}

	public JTextField getTfUsername() {
		return tfUsername;
	}

	public void setTfUsername(JTextField tfUsername) {
		this.tfUsername = tfUsername;
	}

	public JTextField getTfPassword() {
		return tfPassword;
	}

	public void setTfPassword(JTextField tfPassword) {
		this.tfPassword = tfPassword;
	}

	public JTextField getTfPort() {
		return tfPort;
	}

	public void setTfPort(JTextField tfPort) {
		this.tfPort = tfPort;
	}

	public JButton getBtnConnect() {
		return btnConnect;
	}

	public void setBtnConnect(JButton btnConnect) {
		this.btnConnect = btnConnect;
	}

	public String getHostname() {
		return hostname;
	}

	public void setHostname(String hostname) {
		this.hostname = hostname;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public int getPort() {
		return port;
	}

	public void setPort(int port) {
		this.port = port;
	}

	
}
