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
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.prefs.BackingStoreException;
import java.util.prefs.Preferences;

import org.springframework.binding.value.support.PropertyChangeSupport;
import org.springframework.enums.CodedEnum;
import org.springframework.richclient.util.ClassUtils;

/**
 * Settings implementation using the J2SE Preferences API.<br>
 * Not using the PreferenceChangeListener to implement PropertyChangeListener support,
 * because we also need the old value.
 * @author Peter De Bruycker
 */
public class PreferencesSettings implements Settings {

	private Preferences prefs;
	private PreferencesSettings parent;
	private Properties defaults = new Properties();
	private Map children = new HashMap();
	private PropertyChangeSupport propertyChangeSupport = new PropertyChangeSupport(this);

	/**
	 * Create the root.
	 */
	public PreferencesSettings(String name) {
		prefs = Preferences.userRoot().node(name);
	}

	/**
	 * Create a child with the given name.
	 */
	public PreferencesSettings(PreferencesSettings parent, String name) {
		this.parent = parent;
		prefs = parent.getPreferences().node(name);
	}

	public Preferences getPreferences() {
		return prefs;
	}

	public boolean isDefault(String key) {
		return prefs.get(key, null) == null || prefs.get(key, null).equals(getDefaultString(key));
	}

	private void removeIfDefault(String key) {
		if (isDefault(key)) {
			prefs.remove(key);
		}
	}

	private void afterSet(String key, Object oldValue, Object newValue) {
		removeIfDefault(key);
		firePropertyChange(key, oldValue, newValue);
	}

	public void setString(String key, String value) {
		String oldValue = prefs.get(key, "");
		prefs.put(key, value);
		afterSet(key, oldValue, value);
	}
	public String getString(String key) {
		return prefs.get(key, getDefaultString(key));
	}

	public void setDefaultString(String key, String value) {
		defaults.setProperty(key, value);
	}
	public String getDefaultString(String key) {
		return defaults.getProperty(key, "");
	}

	public void setInt(String key, int value) {
		int oldValue = prefs.getInt(key, 0);
		prefs.putInt(key, value);
		afterSet(key, new Integer(oldValue), new Integer(value));
	}
	public int getInt(String key) {
		return prefs.getInt(key, 0);
	}

	public void setDefaultInt(String key, int value) {
		defaults.setProperty(key, Integer.toString(value));
	}
	public int getDefaultInt(String key) {
		String def = defaults.getProperty(key);
		return def != null ? Integer.parseInt(def) : 0;
	}

	public void setLong(String key, long value) {
		long oldValue = prefs.getLong(key, 0L);
		prefs.putLong(key, value);
		afterSet(key, new Long(oldValue), new Long(value));
	}
	public long getLong(String key) {
		return prefs.getLong(key, getDefaultLong(key));
	}

	public void setDefaultLong(String key, long value) {
		defaults.setProperty(key, Long.toString(value));
	}
	public long getDefaultLong(String key) {
		String def = defaults.getProperty(key);
		return def != null ? Long.parseLong(def) : 0L;
	}

	public void setFloat(String key, float value) {
		float oldValue = prefs.getFloat(key, 0.0f);
		prefs.putFloat(key, value);
		afterSet(key, new Float(oldValue), new Float(value));
	}
	public float getFloat(String key) {
		return prefs.getFloat(key, getDefaultFloat(key));
	}

	public void setDefaultFloat(String key, float value) {
		defaults.setProperty(key, Float.toString(value));
	}
	public float getDefaultFloat(String key) {
		String def = defaults.getProperty(key);
		return def != null ? Float.parseFloat(def) : 0.0f;
	}

	public void setDouble(String key, double value) {
		double oldValue = prefs.getDouble(key, 0.0);
		prefs.putDouble(key, value);
		afterSet(key, new Double(oldValue), new Double(value));
	}
	public double getDouble(String key) {
		return prefs.getDouble(key, getDefaultDouble(key));
	}

	public void setDefaultDouble(String key, double value) {
		defaults.setProperty(key, Double.toString(value));
	}
	public double getDefaultDouble(String key) {
		String def = defaults.getProperty(key);
		return def != null ? Double.parseDouble(def) : 0.0;
	}

	public void setBoolean(String key, boolean value) {
		boolean oldValue = prefs.getBoolean(key, false);
		prefs.putBoolean(key, value);
		afterSet(key, Boolean.valueOf(oldValue), Boolean.valueOf(value));
	}
	public boolean getBoolean(String key) {
		return prefs.getBoolean(key, getDefaultBoolean(key));
	}

	public void setDefaultBoolean(String key, boolean value) {
		defaults.setProperty(key, Boolean.toString(value));
	}
	public boolean getDefaultBoolean(String key) {
		String def = defaults.getProperty(key);
		return def != null ? Boolean.getBoolean(def) : false;
	}

	public void setCodedEnum(String key, CodedEnum value) {
		CodedEnum oldValue = stringToEnum(prefs.get(key, null));
		
		prefs.put(key,  value == null ? "" : ClassUtils.getClassFieldNameWithValue(value.getClass(), value));
		afterSet(key, oldValue, value);
	}
	public CodedEnum getCodedEnum(String key) {
		String value = prefs.get(key, null);

	        if (value == null || value.trim().equals("")) {
        	    return getDefaultCodedEnum(key);
	        }

	        return stringToEnum(value);
	}

	public void setDefaultCodedEnum(String key, CodedEnum value) {
		defaults.setProperty(key,  enumToString(value));
	}
	public CodedEnum getDefaultCodedEnum(String key) {
	        return stringToEnum(defaults.getProperty(key));
 	}

	private CodedEnum stringToEnum(String s) {
		System.out.println(s);
	        if (s == null || s.trim().equals("")) {
        	    return null;
	        }

	        return (CodedEnum) ClassUtils.getFieldValue(s);
	}

	private String enumToString(CodedEnum e) {
		return e == null ? "" : ClassUtils.getClassFieldNameWithValue(e.getClass(), e);
	}

	public String[] getKeys() {
		try {
			return prefs.keys();
		}
		catch (BackingStoreException e) {
			// TODO handle this exception
			throw new RuntimeException(e);
		}
	}

	public String[] getDefaultKeys() {
		return (String[]) defaults.keySet().toArray(new String[0]);
	}

	public String[] getAllKeys() {
		Set keys = new HashSet();
		keys.addAll(Arrays.asList(getKeys()));
		keys.addAll(Arrays.asList(getDefaultKeys()));

		return (String[]) keys.toArray(new String[0]);
	}

	public void save() throws IOException {
		try {
			prefs.flush();
		}
		catch (BackingStoreException e) {
			IOException ioe = new IOException("Unable to save settings");
			ioe.initCause(e);
			throw ioe;
		}
	}

	public void load() throws IOException {
		try {
			prefs.sync();
		}
		catch (BackingStoreException e) {
			IOException ioe = new IOException("Unable to save settings");
			ioe.initCause(e);
			throw ioe;
		}
	}

	public Settings getSettings(String name) {
		Settings result = (Settings) children.get(name);
		if (result == null)
		{
			result = new PreferencesSettings(this, name);
			children.put(name, result);
		}
		return result;
	}

	public String getName() {
		return prefs.name();
	}

	public Settings getParent()
	{
		return parent;
	}

	public void addPropertyChangeListener(PropertyChangeListener l) {
		propertyChangeSupport.addPropertyChangeListener(l);
	}
	public void addPropertyChangeListener(String key, PropertyChangeListener l) {
		propertyChangeSupport.addPropertyChangeListener(key, l);
	}

	public void removePropertyChangeListener(PropertyChangeListener l) {
		propertyChangeSupport.removePropertyChangeListener(l);
	}
	public void removePropertyChangeListener(String key, PropertyChangeListener l) {
		propertyChangeSupport.removePropertyChangeListener(key, l);
	}

	private void firePropertyChange(String key, Object oldValue, Object newValue) {
		propertyChangeSupport.firePropertyChange(key, oldValue, newValue);
	}
}