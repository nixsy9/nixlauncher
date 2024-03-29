package com.chopnix.minecraft.NixLauncher.gui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Map;
import java.util.Vector;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JToggleButton;
import javax.swing.table.AbstractTableModel;

import com.chopnix.minecraft.NixLauncher.NuxLauncher;
import com.chopnix.minecraft.NixLauncher.yml.YAMLNode;
import com.chopnix.minecraft.NixLauncher.yml.YAMLProcessor;


public class ConfigurationFrame extends JFrame {
	private static final long serialVersionUID = -8510556813877211562L;

	private static ConfigurationFrame frame;
	private static JTable table;
	private JButton saveButton;
	private JButton cancelButton;
	private JToggleButton forceUpdateButton;
	private JToggleButton testRepoButton;
	private Hashtable<String, Object[]> optionalList;

	private ConfigurationFrame() {
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		setTitle("Configuration");
		setLocationRelativeTo(null);

		optionalList = new Hashtable<String, Object[]>();

		final Vector<Vector<Object>> data = new Vector<Vector<Object>>();
		fillArray(data, "repository.highest", false);
		fillArray(data, "repository.high", false);
		fillArray(data, "repository.normal", false);
		fillArray(data, "repository.optional", true);

		Vector<String> titles = new Vector<String>();
		titles.add("Name");
		titles.add("Description");
		titles.add("Version");
		titles.add("Installation");

		ZModel model = new ZModel(data, titles);
		table = new JTable(model);
		table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
		TableColumnAdjuster tca = new TableColumnAdjuster(table);
		tca.adjustColumns();

		JPanel buttonsPanel = new JPanel();

		saveButton = new JButton("save");
		saveButton.setEnabled(false);
		saveButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				YAMLProcessor config = NuxLauncher.getConfig();
				config.removeProperty("optional");

				Enumeration<Object[]> e = optionalList.elements();
				while (e.hasMoreElements()) {
					Object[] current = e.nextElement();
					config.setProperty("optional." + (String) current[1] + ".enabled", data.elementAt((Integer) current[0]).elementAt(3));
				}
				config.setProperty("testrepo", testRepoButton.isSelected());
				config.setProperty("repository.version", 0);
				config.save();

				NuxLauncher.downloadRepo();

				frame.dispose();
			}
		});

		cancelButton = new JButton("Cancel");
		cancelButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				frame.dispose();
			}
		});

		forceUpdateButton = new JToggleButton("Force update");
		forceUpdateButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				saveButton.setEnabled(true);
			}
		});

		YAMLProcessor config = NuxLauncher.getConfig();
		testRepoButton = new JToggleButton("Use the test repository");
		testRepoButton.setSelected(config.getBoolean("testrepo", false));
		testRepoButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				saveButton.setEnabled(true);

				JOptionPane.showMessageDialog(frame, "Save the configuration and return in to see the new mods.\n\nN'deposit only use this for testing purposes and if you know what you are doing.", "Configuration - Test repository", JOptionPane.INFORMATION_MESSAGE);
			}
		});

		buttonsPanel.add(saveButton);
		buttonsPanel.add(cancelButton);
		buttonsPanel.add(forceUpdateButton);
		buttonsPanel.add(testRepoButton);

		this.getContentPane().add(new JScrollPane(table), BorderLayout.CENTER);
		this.getContentPane().add(buttonsPanel, BorderLayout.SOUTH);
	}

	public static void main() {
		frame = new ConfigurationFrame();
		frame.setVisible(true);

		int width = table.getWidth() + frame.getInsets().left + frame.getInsets().right;
		Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
		frame.setBounds((dim.width - width) / 2, (dim.height - 480) / 2, width, 480);

	}

	private void fillArray(Vector<Vector<Object>> data, String path, boolean optional) {
		YAMLProcessor repo = NuxLauncher.getRepo();
		YAMLProcessor config = NuxLauncher.getConfig();

		Map<String, YAMLNode> list = repo.getNodes(path);
		for (String index : list.keySet()) {
			YAMLNode current = list.get(index);

			Boolean bool;

			if (optional) {
				bool = new Boolean(config.getBoolean("optional." + index + ".enabled", false));
				Object[] array = { data.size(), index };
				optionalList.put(current.getString("name"), array);
			} else {
				bool = new Boolean(true);
			}

			Vector<Object> element = new Vector<Object>();
			element.add(current.getString("name"));
			element.add(current.getString("description"));
			element.add(current.getString("version"));
			element.add(bool);
			data.add(element);
		}
	}

	class ZModel extends AbstractTableModel {
		private static final long serialVersionUID = -4232487862570184082L;
		private Vector<Vector<Object>> data;
		private Vector<String> title;

		public ZModel(Vector<Vector<Object>> data, Vector<String> title) {
			this.data = data;
			this.title = title;
		}

		public int getColumnCount() {
			return this.title.size();
		}

		public int getRowCount() {
			return this.data.size();
		}

		public Object getValueAt(int row, int col) {
			return this.data.elementAt(row).elementAt(col);
		}

		public String getColumnName(int col) {
			return this.title.elementAt(col);
		}

		public Class<? extends Object> getColumnClass(int col) {
			return this.data.elementAt(1).elementAt(col).getClass();
		}

		public boolean isCellEditable(int row, int col) {
			if (optionalList.containsKey(this.data.elementAt(row).elementAt(0)) && col == 3) {
				return true;
			}
			return false;
		}

		public void setValueAt(Object aValue, int row, int column) {
			if (table.isCellEditable(row, column)) {
				if (table.getValueAt(row, column).toString().equalsIgnoreCase("true")) {
					aValue = new Boolean(false);
					saveButton.setEnabled(true);
				} else if (table.getValueAt(row, column).toString().equalsIgnoreCase("false")) {
					aValue = new Boolean(true);
					saveButton.setEnabled(true);
				}
			}

			data.elementAt(row).setElementAt(aValue, column);
			fireTableCellUpdated(row, column);
		}
	}
}
