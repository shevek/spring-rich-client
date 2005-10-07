package org.springframework.richclient.settings.support;

import javax.swing.JTable;

import org.springframework.richclient.settings.Settings;

public class TableMemento implements Memento {
	private JTable table;

	private String key;

	public TableMemento(JTable table, String key) {
		this.table = table;
		if(key == null) {
			key = table.getName();
		}
		this.key = key;
	}

	public TableMemento(JTable table) {
		this(table, null);
	}

	public void saveState(Settings settings) {
		TableSettings.saveState(settings, key, table);
	}

	public void restoreState(Settings settings) {
		TableSettings.restoreState(settings, key, table);
	}
}
