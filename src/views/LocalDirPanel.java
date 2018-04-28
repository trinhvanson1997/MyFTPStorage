package views;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.List;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.io.File;
import java.util.Arrays;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.JTextField;

public class LocalDirPanel extends JPanel implements ActionListener, ItemListener {
	private JButton btnUpload, btnAdd, btnRename, btnDelete;
	private List list;
	private JTextField details;
	private File curDir;
	private String[] files;
	public String curPath;
	private JTextArea textArea;

	public LocalDirPanel(String path) {
		this.curPath = path;
		setPreferredSize(new Dimension(400, 400));
		setLayout(new BorderLayout(5, 5));

		add(createButtonPanel(), BorderLayout.NORTH);
		add(createListPanel(), BorderLayout.CENTER);
	}

	private JPanel createButtonPanel() {
		JPanel p = new JPanel(new FlowLayout(10, 10, 10));
		btnUpload = createButton("Upload");
		btnAdd = createButton("Add");
		btnDelete = createButton("Delete");
		btnRename = createButton("Rename");

		p.add(btnUpload);
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
		top.add(new JLabel("Local site"));
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
		File dir = new File(curPath);
		if (!dir.isDirectory()) {
			System.out.println("not a directory");
		} else {
			files = dir.list();
			if(files != null) {
				Arrays.sort(files);
			}

			list.removeAll();
			list.add("..");
			if(files!=null) {
				for (String string : files) {
					list.add(string);
				}
			}
			details.setText(curPath);
			curDir = dir;
		}
	}

	@Override
	public void itemStateChanged(ItemEvent e) {
		int i = list.getSelectedIndex() - 1;
		if (i < 0)
			return;
		String filename = files[i];
		filename = curPath +"\\" + filename;
		details.setText(filename);
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == list) {
			int i = list.getSelectedIndex() - 1;
			if (i == -1) {
				String parent = curDir.getParent();
				if(parent == null)
					return;
				curPath = parent;
				listDirectory(curPath);
			}
			else {
				String filename = files[i];
				File f = new File(curDir, filename);
				String fullname = f.getAbsolutePath();
				if(f.isDirectory()) {
					curPath = fullname;
					listDirectory(curPath);
				}
			}
		}

	}

	public JButton getBtnUpload() {
		return btnUpload;
	}

	public void setBtnUpload(JButton btnUpload) {
		this.btnUpload = btnUpload;
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

	public File getCurDir() {
		return curDir;
	}

	public void setCurDir(File curDir) {
		this.curDir = curDir;
	}

	public String[] getFiles() {
		return files;
	}

	public void setFiles(String[] files) {
		this.files = files;
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

	
}
