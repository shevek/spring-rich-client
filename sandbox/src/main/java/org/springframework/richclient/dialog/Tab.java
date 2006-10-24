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
package org.springframework.richclient.dialog;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;

import javax.swing.Icon;
import javax.swing.JComponent;

/**
 * Represents a single tab in a tabbed pane.
 * <p>
 * TODO: move this to another package?
 * 
 * @author Peter De Bruycker
 */
public class Tab {
	private String title;
	private Icon icon;
	private String tooltip;
	private JComponent component;
	private boolean visible = true;
	private int mnemonic;

	private PropertyChangeSupport propertyChangeSupport = new PropertyChangeSupport(this);

	public Tab(String title, JComponent component) {
		this.title = title;
		this.component = component;
	}

	public Tab() {
		
	}

	public boolean isVisible() {
		return visible;
	}

	public void setVisible(boolean visible) {
		boolean old = this.visible;
		this.visible = visible;
		if (old != visible) {
			propertyChangeSupport.firePropertyChange("visible", old, visible);
		}
	}

	public JComponent getComponent() {
		return component;
	}

	public void setComponent(JComponent component) {
		JComponent old = this.component;
		this.component = component;
		if (old != component) {
			propertyChangeSupport.firePropertyChange("component", old, component);
		}
	}

	public Icon getIcon() {
		return icon;
	}

	public void setIcon(Icon icon) {
		Icon old = this.icon;
		this.icon = icon;
		if (old != icon) {
			propertyChangeSupport.firePropertyChange("icon", old, icon);
		}
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		String old = this.title;
		this.title = title;
		if(old != null && !old.equals(title) || title != null) {
			propertyChangeSupport.firePropertyChange("title", old, title);
		}
	}

	public String getTooltip() {
		return tooltip;
	}

	public void setTooltip(String tooltip) {
		String old = this.tooltip;
		this.tooltip = tooltip;
		if(old != null && !old.equals(tooltip) || tooltip != null) {
			propertyChangeSupport.firePropertyChange("tooltip", old, tooltip);
		}
	}

	public void addPropertyChangeListener(PropertyChangeListener l) {
		propertyChangeSupport.addPropertyChangeListener(l);
	}

	public void addPropertyChangeListener(String propertyName, PropertyChangeListener l) {
		propertyChangeSupport.addPropertyChangeListener(propertyName, l);
	}

	public void removePropertyChangeListener(PropertyChangeListener l) {
		propertyChangeSupport.addPropertyChangeListener(l);
	}

	public void removePropertyChangeListener(String propertyName, PropertyChangeListener l) {
		propertyChangeSupport.addPropertyChangeListener(propertyName, l);
	}

	public void setMnemonic(int mnemonic) {
		int old = this.mnemonic;
		this.mnemonic = mnemonic;
		if(mnemonic != old)  {
			propertyChangeSupport.firePropertyChange("mnemonic", old, mnemonic);
		}
	}
	
	public int getMnemonic() {
		return mnemonic;
	}
}
