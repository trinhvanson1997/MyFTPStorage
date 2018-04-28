package controllers;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import javax.swing.JFrame;
import javax.swing.JProgressBar;
import javax.swing.Timer;

import org.apache.commons.net.ftp.FTPClient;

public class UploadThread implements Runnable {
	private FTPClient ftpClient;
	private String localPath;
	private String remotePath;
	public boolean paused = false;
	public Timer t;
	public long count = 0;
	public long len;
	public int percent;

	public UploadThread(FTPClient ftpClient, String localPath, String remotePath) {
		this.ftpClient = ftpClient;
		this.localPath = localPath;
		this.remotePath = remotePath;
		
		JFrame frame = new JFrame();
		frame.setLayout(new BorderLayout());
		frame.setUndecorated(true);
		frame.setSize(400,20);

		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setLocationRelativeTo(null);

		
		JProgressBar progressBar = new JProgressBar(0,100);
		progressBar.setValue(0);
		progressBar.setStringPainted(true);
		progressBar.setSize(400, 20);
		
		frame.add(progressBar,BorderLayout.CENTER);
		
		
		t = new Timer(100,new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				
				if(percent == 100) {
					t.stop();
					frame.dispose();
				}
				else {
					Double temp = ((double)count/len)*100;
					percent = temp.intValue();
					progressBar.setValue(percent);
					
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
					System.out.println(count);
						
					os.write(bytes, 0, read);
					
					if(paused) {
						waitIfPaused();
					}
				}
			

			is.close();
			os.close();

			boolean completed = ftpClient.completePendingCommand();
			if (completed) {
				System.out.println("file is uploaded successfully.");
			}

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

		System.out.println("continue");
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
}
