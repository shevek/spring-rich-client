/*
 * Copyright 2002-2006 the original author or authors.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package org.springframework.richclient.dialog.control;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JComponent;
import javax.swing.JTabbedPane;

import org.springframework.richclient.factory.ControlFactory;

/**
 * Wrapper around <code>JTabbedPane</code>. When a <code>Tab</code> is made invisible, the tab is hidden from the ui, and
 * vice versa.
 * <p>
 * TODO: move this to another package?
 * 
 * @author Peter De Bruycker
 */
public class ExtTabbedPane implements ControlFactory {

	private List tabs = new ArrayList();

	private JTabbedPane tabbedPane;

	private PropertyChangeListener propertyChangeHandler = new PropertyChangeListener() {
		public void propertyChange(PropertyChangeEvent evt) {
			Tab tab = (Tab) evt.getSource();
			int index = tabs.indexOf(tab);

			if (evt.getPropertyName().equals("title")) {
				tabbedPane.setTitleAt(index, tab.getTitle());
			}
			if (evt.getPropertyName().equals("tooltip")) {
				tabbedPane.setToolTipTextAt(index, tab.getTooltip());
			}
			if (evt.getPropertyName().equals("icon")) {
				tabbedPane.setIconAt(index, tab.getIcon());
			}
			if (evt.getPropertyName().equals("component")) {
				tabbedPane.setComponentAt(index, tab.getComponent());
			}
			if (evt.getPropertyName().equals("mnemonic")) {
				tabbedPane.setMnemonicAt(index, tab.getMnemonic());
			}
			if (evt.getPropertyName().equals("visible")) {
				if (tab.isVisible()) {
					tabbedPane.insertTab(tab.getTitle(), tab.getIcon(), tab.getComponent(), tab.getTooltip(),
							determineUIIndex(tab));
				}
				else {
					tabbedPane.removeTabAt(determineUIIndex(tab));
				}
			}
		}
	};

	public ExtTabbedPane(JTabbedPane tabbedPane) {
		this.tabbedPane = tabbedPane;
	}

	public ExtTabbedPane() {
		this(new JTabbedPane());
	}

	public JComponent getControl() {
		return tabbedPane;
	}

	public void addTab(Tab tab) {
		if (tab.isVisible()) {
			tabbedPane.addTab(tab.getTitle(), tab.getIcon(), tab.getComponent(), tab.getTooltip());
		}
		tabs.add(tab);
		tab.addPropertyChangeListener(propertyChangeHandler);
	}

	public void removeTab(Tab tab) {
		removeTab(tabs.indexOf(tab));
	}

	public void addTab(int index, Tab tab) {
		if (tab.isVisible()) {
			tabbedPane.insertTab(tab.getTitle(), tab.getIcon(), tab.getComponent(), tab.getTooltip(), index);
		}
		tabs.add(index, tab);
		tab.addPropertyChangeListener(propertyChangeHandler);
	}

	public void removeTab(int index) {
		tabbedPane.removeTabAt(index);
		Tab tab = (Tab) tabs.remove(index);
		tab.removePropertyChangeListener(propertyChangeHandler);
	}

	public void selectTab(Tab tab) {
		if(tab.isVisible()) {
			int targetIndex = determineUIIndex(tab);
			if (targetIndex >= 0) {
				tabbedPane.setSelectedIndex(targetIndex);
			}
		}
	}

	private int determineUIIndex(Tab tab) {
		int result = 0;

		int index = tabs.indexOf(tab);
		for (int i = 0; i < index; i++) {
			Tab t = (Tab) tabs.get(i);
			if (t.isVisible()) {
				result++;
			}
		}

		return result;
	}

}
