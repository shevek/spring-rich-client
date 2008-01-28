package org.springframework.richclient.settings.support;

import javax.swing.JComponent;


public interface MementoFactory {
	Memento createMemento(JComponent component, String key);
}
