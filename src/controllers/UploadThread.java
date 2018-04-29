package controllers;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.Timer;
import javax.swing.border.EmptyBorder;

import org.apache.commons.net.ftp.FTPClient;

import views.RemoteDirPanel;

public class UploadThread implements Runnable, ActionListener {
	private FTPClient ftpClient;

	// đường dẫn nguồn
	private String localPath;

	// đường dẫn đích
	private String remotePath;

	// flag để xác định pause hay resume
	private boolean paused = false;
	private boolean cancel = false;

	// các biến đo phần trăm file đã đc upload
	private Timer t;
	private long count = 0;
	private long len;
	private int percent;

	// button pause, resume, cancel
	private JFrame frame;
	private JButton btnPause, btnResume, btnCancel;
	
	private RemoteDirPanel remoteDirPanel;
	public UploadThread(FTPClient ftpClient, String localPath, String remotePath, RemoteDirPanel remoteDirPanel) {
		this.ftpClient = ftpClient;
		this.localPath = localPath;
		this.remotePath = remotePath;
		this.remoteDirPanel = remoteDirPanel;
		
		File file = new File(localPath);
		String fileName = file.getName();
		
		frame = new JFrame();
		frame.setLayout(new BorderLayout());
		frame.setTitle("Uploading "+fileName+" 0%");
		frame.setSize(400, 150);
		frame.setResizable(true);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setLocationRelativeTo(null);

		JProgressBar progressBar = new JProgressBar(0, 100);
		progressBar.setValue(0);
		progressBar.setStringPainted(true);
		progressBar.setSize(400, 20);

		JPanel progressPanel = new JPanel(new BorderLayout());
		progressPanel.setBorder(new EmptyBorder(20, 10, 0, 20));
		progressPanel.add(progressBar, BorderLayout.CENTER);

		frame.add(progressPanel, BorderLayout.NORTH);
		frame.add(createButtonsPanel(), BorderLayout.CENTER);

		t = new Timer(100, new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {

				if (percent == 100) {
					t.stop();
					frame.dispose();
				} else {
					Double temp = ((double) count / len) * 100;
					percent = temp.intValue();
					progressBar.setValue(percent);
					frame.setTitle("Uploading " + fileName+" "+percent + "%");
				}
			}
		});
		t.start();
		
		frame.setVisible(true);
	}

	@Override
	public void run() {
		File localFile = new File(this.localPath);
		len = localFile.length();

		try {
			InputStream is = new FileInputStream(localFile);

			System.out.println("Starting upload file");
			OutputStream os = this.ftpClient.storeFileStream(this.remotePath);

			byte[] bytes = new byte[4096];
			int read = 0;
			count = 0;

			while (((read = is.read(bytes)) != -1)) {
				count += read;

				os.write(bytes, 0, read);

				if (paused) {
					waitIfPaused();
				}

				if (cancel) {
					break;
				}

			}

			is.close();
			os.close();

			if (!cancel) {
				boolean completed = ftpClient.completePendingCommand();
				if (completed) {
					System.out.println("file is uploaded successfully.");
				}
			} else {
				System.out.println("Cancel download");
				ftpClient.completePendingCommand();
				try {
					ftpClient.deleteFile(remotePath);
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				frame.dispose();

			}
			remoteDirPanel.listDirectory(remotePath.substring(0,remotePath.lastIndexOf('\\')+1));

		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	public synchronized void pause() {
		paused = true;
		System.out.println("paused");
	}

	public synchronized void resume() {
		paused = false;
		notifyAll();

		System.out.println("resume");
	}

	public synchronized void waitIfPaused() {
		while (paused) {
			try {
				wait();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	public JPanel createButtonsPanel() {
		JPanel p = new JPanel(new FlowLayout(0, 10, 30));
		btnPause = createButton("Pause");
		btnResume = createButton("Resume");
		btnResume.setEnabled(false);
		btnCancel = createButton("Cancel");

		p.add(btnPause);
		p.add(btnResume);
		p.add(btnCancel);

		return p;

	}

	public JButton createButton(String name) {
		JButton button = new JButton(name);
		button.addActionListener(this);
		return button;
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == btnPause) {
			pause();
			btnPause.setEnabled(false);
			btnResume.setEnabled(true);

		}

		if (e.getSource() == btnResume) {
			resume();
			btnPause.setEnabled(true);
			btnResume.setEnabled(false);
		}

		if (e.getSource() == btnCancel) {
			if (JOptionPane.showConfirmDialog(null, "Are you sure want to cancel?", "WARNING",
					JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
				cancel = true;

			} else {
				return;
			}

		}

	}

}
