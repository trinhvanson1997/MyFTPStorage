package views;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.List;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.JTextField;

import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPFile;

public class RemoteDirPanel extends JPanel implements ActionListener, ItemListener {
	private JButton btnDownload, btnAdd, btnRename, btnDelete;
	private List list; 				//danh sach ten cac thu muc va file trong thu muc hien tai
	private JTextField details;		//duong dan den thu muc hien tai tu thu muc goc
	private FTPFile curDir;			//thu muc hien tai
	private FTPFile[] files;		//danh sach cac file va thu muc htai
	private String[] fileNames;		
	private String curDirectory;
	private String curPath;			//duong dan tuyet doi den thu muc htai
	private JTextArea textArea;
	private FTPClient ftpClient;

	public RemoteDirPanel(String path, FTPClient ftpClient) {
		this.curPath = path;
		this.ftpClient = ftpClient;

		setPreferredSize(new Dimension(400, 400));
		setLayout(new BorderLayout(5, 5));

		add(createButtonPanel(), BorderLayout.NORTH);
		add(createListPanel(), BorderLayout.CENTER);
	}

	private JPanel createButtonPanel() {
		JPanel p = new JPanel(new FlowLayout(10, 10, 10));
		btnDownload = createButton("Download");
		btnAdd = createButton("Add");
		btnDelete = createButton("Delete");
		btnRename = createButton("Rename");

		p.add(btnDownload);
		p.add(btnAdd);
		p.add(btnDelete);
		p.add(btnRename);

		return p;
	}

	private JPanel createListPanel() {
		// TODO Auto-generated method stub
		JPanel p = new JPanel(new BorderLayout(10, 10));

		list = new List(10, false);
		list.addActionListener(this);
		list.addItemListener(this);
		list.setFont(new Font("MonoSpaced", Font.PLAIN, 14));

		details = new JTextField(30);
		details.setEditable(false);
		details.setFont(new Font("MonoSpaced", Font.PLAIN, 12));

		JPanel top = new JPanel(new FlowLayout());
		top.add(new JLabel("Remote site"));
		top.add(details);
		listDirectory(curPath);

		JPanel center = new JPanel(new BorderLayout());
		center.add(list, BorderLayout.CENTER);

		p.add(top, BorderLayout.NORTH);
		p.add(center, BorderLayout.CENTER);
		return p;
	}

	private JButton createButton(String name) {
		JButton b = new JButton(name);
		b.addActionListener(this);
		return b;
	}

	public void listDirectory(String curPath) {
		FTPFile dir;
		try {
			dir = ftpClient.mlistFile(curPath);
			if (!dir.isDirectory()) {
				System.out.println("not a directory");
			} else {
				FTPFile []tmp = ftpClient.listFiles(curPath); //lay danh sach file trong thu muc curPath
				
				// khi lấy ds file từ server có thêm thư mục .  .. nên -2
				// khi lấy ở localhost không có .  ..
			//files = new FTPFile[tmp.length -2];
			
			files = new FTPFile[tmp.length];	
				int index = 0;
				for (int i = 0; i < tmp.length; i++) {
			
					if (tmp[i].getName().equals(".") || tmp[i].getName().equals("..")) {
						continue;
					} else {
						files[index] = tmp[i];
						index++;
					}

				}
		
		

				list.removeAll();
				list.add("..");
				for (FTPFile file : files) {
					list.add(file.getName());
				}
				details.setText(curPath);
				curDir = dir;
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	@Override
	public void itemStateChanged(ItemEvent e) {
		int i = list.getSelectedIndex() - 1;
		if (i < 0)
			return;
		String filename = files[i].getName();
		
		if(curPath.equals("/"))
		{
			filename = curPath + filename;
		}
		else
			filename = curPath + "/" + filename;
		details.setText(filename);
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == list) {
			int i = list.getSelectedIndex() - 1;
			if (i == -1) {

				if(curPath.equals("/")) 
					return;
				else if((curPath.split("/").length-1) == 1) {  //vd /htdoc   /abc
					curPath = "/";
				}
				else {
					curPath = curPath.substring(0, curDir.getName().lastIndexOf('/'));	
				}
				
				
				listDirectory(curPath);
			} else {
				String filename = files[i].getName();
				String fullname;
				if(curPath.equals("/"))
				{
					fullname = curPath + filename;
				}
				else
					fullname = curPath + "/" + filename;
				
				FTPFile f;
				try {
					f = ftpClient.mlistFile(fullname);
					if (f.isDirectory()) {
						curPath = fullname;
						listDirectory(curPath);
					}

				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}

			}
		}

	}


	public JButton getBtnUpload() {
		return btnDownload;
	}

	public void setBtnUpload(JButton btnUpload) {
		this.btnDownload = btnUpload;
	}

	public JButton getBtnAdd() {
		return btnAdd;
	}

	public void setBtnAdd(JButton btnAdd) {
		this.btnAdd = btnAdd;
	}

	public JButton getBtnRename() {
		return btnRename;
	}

	public void setBtnRename(JButton btnRename) {
		this.btnRename = btnRename;
	}

	public JButton getBtnDelete() {
		return btnDelete;
	}

	public void setBtnDelete(JButton btnDelete) {
		this.btnDelete = btnDelete;
	}

	public List getList() {
		return list;
	}

	public void setList(List list) {
		this.list = list;
	}

	public JTextField getDetails() {
		return details;
	}

	public void setDetails(JTextField details) {
		this.details = details;
	}

	public String getCurPath() {
		return curPath;
	}

	public void setCurPath(String curPath) {
		this.curPath = curPath;
	}

	public JTextArea getTextArea() {
		return textArea;
	}

	public void setTextArea(JTextArea textArea) {
		this.textArea = textArea;
	}

	public FTPFile getCurDir() {
		return curDir;
	}

	public void setCurDir(FTPFile curDir) {
		this.curDir = curDir;
	}

}
