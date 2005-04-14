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
package org.springframework.richclient.preference;

import java.beans.PropertyChangeListener;
import java.io.IOException;
import java.io.OutputStream;

import org.springframework.core.enums.LabeledEnum;

/**
 * A <code>PreferenceStore</code> holds preferences.
 * 
 * @author Peter De Bruycker
 */
public interface PreferenceStore {

	public void addPropertyChangeListener(PropertyChangeListener listener);

	public boolean getBoolean(String name);

	public boolean getDefaultBoolean(String name);

	public double getDefaultDouble(String name);

	public float getDefaultFloat(String name);

	public int getDefaultInt(String name);

	public long getDefaultLong(String name);

	public String getDefaultString(String name);

	public LabeledEnum getDefaultLabeledEnum(String name);

	public double getDouble(String name);

	public float getFloat(String name);

	public int getInt(String name);

	public long getLong(String name);

	public String getString(String name);

	public LabeledEnum getLabeledEnum(String name);

	public boolean isDefault(String name);

	public void removePropertyChangeListener(PropertyChangeListener listener);

	public void setDefault(String name, double value);

	public void setDefault(String name, float value);

	public void setDefault(String name, int value);

	public void setDefault(String name, long value);

	public void setDefault(String name, String defaultObject);

	public void setDefault(String name, boolean value);

	public void setDefault(String name, LabeledEnum value);

	public void setToDefault(String name);

	public void setValue(String name, double value);

	public void setValue(String name, float value);

	public void setValue(String name, int value);

	public void setValue(String name, long value);

	public void setValue(String name, String value);

	public void setValue(String name, boolean value);

	public void setValue(String name, LabeledEnum value);

	public boolean isDirty();

	public void export(OutputStream out) throws IOException;

	public void save() throws IOException;

	public void load() throws IOException;

}