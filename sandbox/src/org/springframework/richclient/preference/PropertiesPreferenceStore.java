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

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import org.springframework.core.enums.LabeledEnum;
import org.springframework.richclient.util.ClassUtils;
import org.springframework.util.Assert;
import org.springframework.util.ObjectUtils;

/**
 * @author Peter De Bruycker
 */
public class PropertiesPreferenceStore implements PreferenceStore {

    private Properties defaults = new Properties();

    private boolean dirty = false;

    private String fileName;

    private List listeners = new ArrayList();

    private Properties properties = new Properties();

    public PropertiesPreferenceStore() {
    }

    public void addPropertyChangeListener(PropertyChangeListener listener) {
        listeners.add(listener);
    }

    public void export(OutputStream out) throws IOException {
        // TODO use Assert.notNull("OutputStream cannot be null.", out);
        properties.store(out, null);
        dirty = false;
    }

    public void firePropertyChangeEvent(String name, Object oldValue, Object newValue) {

        if (listeners.size() > 0 && (oldValue == null || !oldValue.equals(newValue))) {
            final PropertyChangeEvent pe = new PropertyChangeEvent(this, name, oldValue, newValue);
            for (int i = 0; i < listeners.size(); ++i) {
                PropertyChangeListener l = (PropertyChangeListener)listeners.get(i);
                l.propertyChange(pe);
            }
        }
    }

    private boolean getBoolean(Properties p, String name) {
        return Boolean.valueOf(p.getProperty(name)).booleanValue();
    }

    public boolean getBoolean(String name) {
        if (isDefault(name)) {
            return getDefaultBoolean(name);
        }
        return getBoolean(properties, name);
    }

    private LabeledEnum getLabeledEnum(Properties p, String name) {
        String value = p.getProperty(name);
        if (value == null || value.trim().equals("")) {
            return null;
        }
        return (LabeledEnum)ClassUtils.getFieldValue(value);
    }

    public LabeledEnum getLabeledEnum(String name) {
        if (isDefault(name)) {
            return getDefaultLabeledEnum(name);
        }
        return getLabeledEnum(properties, name);
    }

    public boolean getDefaultBoolean(String name) {
        return getBoolean(defaults, name);
    }

    public LabeledEnum getDefaultLabeledEnum(String name) {
        return getLabeledEnum(defaults, name);
    }

    public double getDefaultDouble(String name) {
        return getDouble(defaults, name);
    }

    public float getDefaultFloat(String name) {
        return getFloat(defaults, name);
    }

    public int getDefaultInt(String name) {
        return getInt(defaults, name);
    }

    public long getDefaultLong(String name) {
        return getLong(defaults, name);
    }

    public String getDefaultString(String name) {
        return getString(defaults, name);
    }

    private double getDouble(Properties p, String name) {
        try {
            return Double.parseDouble(p.getProperty(name,"0.0"));
        }
        catch (NumberFormatException e) {
            return 0.0;
        }
    }

    public double getDouble(String name) {
        if (isDefault(name)) {
            return getDefaultDouble(name);
        }

        return getDouble(properties, name);
    }

    public String getFileName(String name) {
        return fileName;
    }

    private float getFloat(Properties p, String name) {
        try {
            return Float.parseFloat(p.getProperty(name,"0.0"));
        }
        catch (NumberFormatException e) {
            return 0.0f;
        }
    }

    public float getFloat(String name) {
        if (isDefault(name)) {
            return getDefaultFloat(name);
        }
        return getFloat(properties, name);
    }

    private int getInt(Properties p, String name) {
        try {
            return Integer.parseInt(p.getProperty(name,"0"));
        }
        catch (NumberFormatException e) {
            return 0;
        }
    }

    public int getInt(String name) {
        if (isDefault(name)) {
            return getDefaultInt(name);
        }
        return getInt(properties, name);
    }

    private long getLong(Properties p, String name) {
        try {
            return Long.parseLong(p.getProperty(name,"0"));
        }
        catch (NumberFormatException e) {
            return 0L;
        }
    }

    public long getLong(String name) {
        if (isDefault(name)) {
            return getDefaultLong(name);
        }
        return getLong(properties, name);
    }

    public List getPropertyChangeListeners() {
        return listeners;
    }

    private String getString(Properties p, String name) {
        String value = p.getProperty(name);
        return value == null ? "" : value;
    }

    public String getString(String name) {
        if (isDefault(name)) {
            return getDefaultString(name);
        }
        return getString(properties, name);
    }

    public boolean isDefault(String name) {
        return (!properties.containsKey(name) && defaults.containsKey(name));
    }

    public boolean isDirty() {
        return dirty;
    }

    public void load() throws IOException {
        Assert.notNull(fileName, "fileName cannot be null");
        FileInputStream in = new FileInputStream(fileName);
        properties.load(in);
        in.close();
    }

    public void removePropertyChangeListener(PropertyChangeListener listener) {
        listeners.remove(listener);
    }

    public void save() throws IOException {
        Assert.notNull(fileName, "fileName cannot be null");
        FileOutputStream out = null;
        try {
            out = new FileOutputStream(fileName);
            properties.store(out, null);
        }
        finally {
            if (out != null)
                out.close();
        }
    }

    public void setDefault(String name, boolean value) {
        setValue(defaults, name, value);
    }

    public void setDefault(String name, LabeledEnum value) {
        setValue(defaults, name, value);
    }

    public void setDefault(String name, double value) {
        setValue(defaults, name, value);
    }

    public void setDefault(String name, float value) {
        setValue(defaults, name, value);
    }

    public void setDefault(String name, int value) {
        setValue(defaults, name, value);
    }

    public void setDefault(String name, long value) {
        setValue(defaults, name, value);
    }

    public void setDefault(String name, String value) {
        setValue(defaults, name, value);
    }

    public void setFileName(String name) {
        fileName = name;
    }

    public void setToDefault(String name) {
        Object oldValue = properties.get(name);
        properties.remove(name);
        dirty = true;
        Object newValue = defaults.get(name);
        firePropertyChangeEvent(name, oldValue, newValue);
    }

    private void setValue(Properties p, String name, boolean value) {
        p.put(name, value == true ? "true" : "false");
    }

    private void setValue(Properties p, String name, LabeledEnum value) {
        p.put(name, value == null ? "" : ClassUtils.getClassFieldNameWithValue(value.getClass(), value));
    }

    private void setValue(Properties p, String name, double value) {
        p.put(name, Double.toString(value));
    }

    private void setValue(Properties p, String name, float value) {
        p.put(name, Float.toString(value));
    }

    private void setValue(Properties p, String name, int value) {
        p.put(name, Integer.toString(value));
    }

    private void setValue(Properties p, String name, long value) {
        p.put(name, Long.toString(value));
    }

    private void setValue(Properties p, String name, String value) {
        p.put(name, value);
    }

    public void setValue(String name, boolean value) {
        boolean oldValue = getBoolean(name);
        if (oldValue != value) {
            setValue(properties, name, value);
            dirty = true;
            firePropertyChangeEvent(name, new Boolean(oldValue), new Boolean(value));
        }
    }

    public void setValue(String name, LabeledEnum value) {
        LabeledEnum oldValue = getLabeledEnum(name);
        if (!ObjectUtils.nullSafeEquals(oldValue, value)) {
            setValue(properties, name, value);
            dirty = true;
            firePropertyChangeEvent(name, oldValue, value);
        }
    }

    public void setValue(String name, double value) {
        double oldValue = getDouble(name);
        if (oldValue != value) {
            setValue(properties, name, value);
            dirty = true;
            firePropertyChangeEvent(name, new Double(oldValue), new Double(value));
        }
    }

    public void setValue(String name, float value) {
        float oldValue = getFloat(name);
        if (oldValue != value) {
            setValue(properties, name, value);
            dirty = true;
            firePropertyChangeEvent(name, new Float(oldValue), new Float(value));
        }
    }

    public void setValue(String name, int value) {
        int oldValue = getInt(name);
        if (oldValue != value) {
            setValue(properties, name, value);
            dirty = true;
            firePropertyChangeEvent(name, new Integer(oldValue), new Integer(value));
        }
    }

    public void setValue(String name, long value) {
        long oldValue = getLong(name);
        if (oldValue != value) {
            setValue(properties, name, value);
            dirty = true;
            firePropertyChangeEvent(name, new Long(oldValue), new Long(value));
        }
    }

    public void setValue(String name, String value) {
        String oldValue = getString(name);
        if (oldValue == null || !oldValue.equals(value)) {
            setValue(properties, name, value);
            dirty = true;
            firePropertyChangeEvent(name, oldValue, value);
        }
    }
}