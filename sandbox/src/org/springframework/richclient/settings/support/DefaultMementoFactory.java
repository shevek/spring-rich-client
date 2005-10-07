package org.springframework.richclient.settings.support;

import javax.swing.JComponent;
import javax.swing.JTable;


public class DefaultMementoFactory implements MementoFactory {
	public Memento createMemento(JComponent component, String key) {
		if (component instanceof JTable) {
			return new TableMemento((JTable) component, key);
		}
		return null;
	}
}
