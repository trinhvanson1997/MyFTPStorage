package views;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPFile;

import controllers.UploadThread;

public class MainUI extends JFrame {
	private String hostname, username; // hien thi host, user va port tren giao dien
	private int port;
	private FTPClient ftpClient;
	private UploadThread upload;
	private LocalDirPanel localDirPanel;
	private RemoteDirPanel remoteDirPanel;

	public Object lock = new Object();

	public MainUI(FTPClient ftpClient, String hostname, String username, int port) {
		this.ftpClient = ftpClient;
		this.hostname = hostname;
		this.username = username;
		this.port = port;

		localDirPanel = new LocalDirPanel("D:\\");
		remoteDirPanel = new RemoteDirPanel("/", ftpClient);

		setTitle("FTP CLient");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setSize(1000, 600);
		setLocationRelativeTo(null);
		setLayout(new BorderLayout(5, 5));

		add(createInfoPanel(), BorderLayout.NORTH);
		add(createCenterPanel(), BorderLayout.CENTER);

		setResizable(false);
		setVisible(true);

		createActions();
	}

	private JPanel createCenterPanel() {
		JPanel p = new JPanel(new BorderLayout(10, 10));
		p.add(localDirPanel, BorderLayout.WEST);
		p.add(remoteDirPanel, BorderLayout.CENTER);
		return p;
	}

	private JPanel createInfoPanel() {
		JPanel p = new JPanel(new FlowLayout(10, 10, 10));
		p.setBorder(new EmptyBorder(5, 5, 5, 5));
		p.add(new JLabel("Host:  " + this.hostname));
		p.add(new JLabel("Username:  " + this.username));
		p.add(new JLabel("Port: " + this.port));
		return p;
	}

	private void createActions() {
		localDirPanel.getBtnUpload().addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {

				String local = localDirPanel.getDetails().getText();
				String remote;

				FTPFile f;
				File dir = new File(local);
				if (dir.isFile()) {
					local = local.replace('\\', '/');
				} else {
					JOptionPane.showMessageDialog(null, "Choose file to upload");
					return;
				}
				String name = dir.getName();
				remote = remoteDirPanel.getCurDir().getName() + "/" + name;

				try {
					InputStream inputStream = ftpClient.retrieveFileStream(remote);
					int returnCode = ftpClient.getReplyCode();
					
					if (inputStream == null || returnCode == 550) {
						upload = new UploadThread(ftpClient, local, remote, remoteDirPanel);

						Thread upThread = new Thread(upload);
						upThread.start();
					}
					else {
						JOptionPane.showMessageDialog(null, "File "+name+" existed!");
					}
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}

				
			}
		});

		localDirPanel.getBtnAdd().addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {

			}

		});
		localDirPanel.getBtnDelete().addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {

			}

		});
	}
}
