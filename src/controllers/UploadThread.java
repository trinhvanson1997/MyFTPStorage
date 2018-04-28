package controllers;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import org.apache.commons.net.ftp.FTPClient;

public class UploadThread implements Runnable {
	private FTPClient ftpClient;
	private String localPath;
	private String remotePath;
	public boolean paused = false;

	public long count = 0;
	public long len;
	public int percent;
	Object lock;

	public UploadThread(FTPClient ftpClient, String localPath, String remotePath, Object lock) {
		this.ftpClient = ftpClient;
		this.localPath = localPath;
		this.remotePath = remotePath;
		this.lock = lock;
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

			while ((read = is.read(bytes))  != -1) {
				count += read;
				System.out.println(count);
				os.write(bytes, 0, read);
				if(paused)
					this.wait();
				
				
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
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
}
