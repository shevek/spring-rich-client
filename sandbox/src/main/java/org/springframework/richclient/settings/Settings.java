/*
 * Copyright 2002-2004 the original author or authors.
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
package org.springframework.richclient.settings;

import java.beans.PropertyChangeListener;
import java.io.IOException;

import org.springframework.core.enums.LabeledEnum;

/**
 * @author Peter De Bruycker
 */
public interface Settings {
	void setString(String key, String value);

	String getString(String key);

	void setDefaultString(String key, String value);

	String getDefaultString(String key);

	void setInt(String key, int value);

	int getInt(String key);

	void setLong(String key, long value);

	long getLong(String key);

	void setDefaultInt(String key, int value);

	int getDefaultInt(String key);

	void setDefaultLong(String key, long value);

	long getDefaultLong(String key);

	void setFloat(String key, float value);

	float getFloat(String key);

	void setDefaultFloat(String key, float value);

	float getDefaultFloat(String key);

	void setDouble(String key, double value);

	double getDouble(String key);

	void setDefaultDouble(String key, double value);

	double getDefaultDouble(String key);

	void setBoolean(String key, boolean value);

	boolean getBoolean(String key);

	void setDefaultBoolean(String key, boolean value);

	boolean getDefaultBoolean(String key);

	void setLabeledEnum(String key, LabeledEnum value);

	LabeledEnum getLabeledEnum(String key);

	void setDefaultLabeledEnum(String key, LabeledEnum value);

	LabeledEnum getDefaultLabeledEnum(String key);

	boolean isDefault(String key);

	/**
	 * Returns the keys in this <code>Settings</code>.
	 * 
	 * @return the keys
	 */
	String[] getKeys();

	/**
	 * Returns the registered default keys in this <code>Settings</code>.
	 * 
	 * @return the keys
	 */
	String[] getDefaultKeys();

	/**
	 * Returns the "sum" of {link #getKeys()} and {link #getDefaultKeys()}.
	 * 
	 * @return all keys
	 */
	String[] getAllKeys();

	void save() throws IOException;

	void load() throws IOException;

	Settings getSettings(String name);
    
    /**
     * Removes this <code>Settings</code> from the backing store.
     */
    void removeSettings();

	String getName();

	Settings getParent();

	void addPropertyChangeListener(PropertyChangeListener l);

	void addPropertyChangeListener(String key, PropertyChangeListener l);

	void removePropertyChangeListener(PropertyChangeListener l);

	void removePropertyChangeListener(String key, PropertyChangeListener l);

	boolean contains(String key);

	void remove(String key);

	boolean isRoot();
	
	String[] getChildSettings();
}