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
	public void setString(String key, String value);

	public String getString(String key);

	public void setDefaultString(String key, String value);

	public String getDefaultString(String key);

	public void setInt(String key, int value);

	public int getInt(String key);

	public void setLong(String key, long value);

	public long getLong(String key);

	public void setDefaultInt(String key, int value);

	public int getDefaultInt(String key);

	public void setDefaultLong(String key, long value);

	public long getDefaultLong(String key);

	public void setFloat(String key, float value);

	public float getFloat(String key);

	public void setDefaultFloat(String key, float value);

	public float getDefaultFloat(String key);

	public void setDouble(String key, double value);

	public double getDouble(String key);

	public void setDefaultDouble(String key, double value);

	public double getDefaultDouble(String key);

	public void setBoolean(String key, boolean value);

	public boolean getBoolean(String key);

	public void setDefaultBoolean(String key, boolean value);

	public boolean getDefaultBoolean(String key);

	public void setLabeledEnum(String key, LabeledEnum value);

	public LabeledEnum getLabeledEnum(String key);

	public void setDefaultLabeledEnum(String key, LabeledEnum value);

	public LabeledEnum getDefaultLabeledEnum(String key);

	public boolean isDefault(String key);

	public String[] getKeys();

	public String[] getDefaultKeys();

	public String[] getAllKeys();

	public void save() throws IOException;

	public void load() throws IOException;

	public Settings getSettings(String name);

	public String getName();

	public Settings getParent();

	public void addPropertyChangeListener(PropertyChangeListener l);

	public void addPropertyChangeListener(String key, PropertyChangeListener l);

	public void removePropertyChangeListener(PropertyChangeListener l);

	public void removePropertyChangeListener(String key, PropertyChangeListener l);

	public boolean contains(String key);

	public void remove(String key);
}