/*
 * Copyright (c) 2002-2004 JGoodies Karsten Lentzsch. All Rights Reserved.
 *
 * Redistribution and use in source and binary forms, with or without 
 * modification, are permitted provided that the following conditions are met:
 * 
 *  o Redistributions of source code must retain the above copyright notice, 
 *    this list of conditions and the following disclaimer. 
 *     
 *  o Redistributions in binary form must reproduce the above copyright notice, 
 *    this list of conditions and the following disclaimer in the documentation 
 *    and/or other materials provided with the distribution. 
 *     
 *  o Neither the name of JGoodies Karsten Lentzsch nor the names of 
 *    its contributors may be used to endorse or promote products derived 
 *    from this software without specific prior written permission. 
 *     
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" 
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, 
 * THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR 
 * PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR 
 * CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, 
 * EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, 
 * PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; 
 * OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, 
 * WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE 
 * OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, 
 * EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE. 
 */
package org.springframework.binding.swing;

import java.util.prefs.Preferences;

import org.springframework.binding.value.support.AbstractValueModel;

/**
 * An implementation of {@link com.jgoodies.binding.value.ValueModel}that reads
 * and writes values from/to a key of a given <code>Preferences</code> node.
 * Changes are observed and fire state changes.
 * 
 * @author Karsten Lentzcsh
 * @author Keith Donald
 */
public final class PreferencesAdapter extends AbstractValueModel {

    /**
     * Refers to the preferences node that is used to persist the bound data.
     */
    private final Preferences prefs;

    /**
     * Holds the preferences key that is used to access the stored value.
     */
    private final String key;

    /**
     * Refers to the type of accepted values.
     */
    private final Class type;

    /**
     * Holds the default value that is used if the preferences do not yet store
     * a value.
     */
    private final Object defaultValue;

    /**
     * Constructs a <code>PreferencesAdapter</code> on the given
     * <code>Preferences</code> using the specified key and default value.
     * 
     * @param prefs
     *            the <code>Preferences</code> used to store and retrieve
     * @param key
     *            the key used to get and set values in the Preferences
     * @param defaultValue
     *            the default value
     */
    public PreferencesAdapter(Preferences prefs, String key, Object defaultValue) {
        this.prefs = prefs;
        this.key = key;
        this.type = defaultValue.getClass();
        this.defaultValue = defaultValue;
    }

    /**
     * Looks up and returns the value from the preferences. The value is look up
     * under this adapter's key. It will be converted before it is returned.
     * 
     * @return the retrieved and converted value
     * @throws ClassCastException
     *             if the type of the default value cannot be read from the
     *             preferences
     */
    public Object getValue() {
        if (type == String.class) {
            return prefs.get(key, null);
        }
        else if (type == Boolean.class) {
            return Boolean.valueOf(getBoolean());
        }
        else if (type == Double.class) {
            return new Double(getDouble());
        }
        else if (type == Float.class) {
            return new Float(getFloat());
        }
        else if (type == Integer.class) {
            return new Integer(getInt());
        }
        else if (type == Long.class) {
            return new Long(getLong());
        }
        else if (type == String.class) {
            return getString();
        }
        else {
            throw new ClassCastException();
        }
    }

    /**
     * Converts the given value to a string and puts it into the preferences.
     * 
     * @param newValue
     *            the object to be stored
     * @throws IllegalArgumentException
     *             if the new value cannot be stored in the preferences due to
     *             an illegal type
     */
    public void setValue(Object newValue) {
        Object value = getValue();
        if (!hasChanged(value, newValue)) {
            return;
        }
        if (newValue instanceof Boolean) {
            setBoolean(((Boolean)newValue).booleanValue());
        }
        else if (newValue instanceof Float) {
            setFloat(((Float)newValue).floatValue());
        }
        else if (newValue instanceof Integer) {
            setInt(((Integer)newValue).intValue());
        }
        else if (newValue instanceof Long) {
            setLong(((Long)newValue).longValue());
        }
        else if (newValue instanceof String) {
            setString((String)newValue);
        }
        else {
            throw new IllegalArgumentException();
        }
    }

    // Convenience Accessors **************************************************

    /**
     * Looks up, converts and returns the stored value from the preferences.
     * Returns the default value if no value has been stored before.
     * 
     * @return the stored value or the default
     */
    public boolean getBoolean() {
        return prefs.getBoolean(key, ((Boolean)defaultValue).booleanValue());
    }

    /**
     * Looks up, converts and returns the stored value from the preferences.
     * Returns the default value if no value has been stored before.
     * 
     * @return the stored value or the default
     */
    public double getDouble() {
        return prefs.getDouble(key, ((Double)defaultValue).doubleValue());
    }

    /**
     * Looks up, converts and returns the stored value from the preferences.
     * Returns the default value if no value has been stored before.
     * 
     * @return the stored value or the default
     */
    public float getFloat() {
        return prefs.getFloat(key, ((Float)defaultValue).floatValue());
    }

    /**
     * Looks up, converts and returns the stored value from the preferences.
     * Returns the default value if no value has been stored before.
     * 
     * @return the stored value or the default
     */
    public int getInt() {
        return prefs.getInt(key, ((Integer)defaultValue).intValue());
    }

    /**
     * Looks up, converts and returns the stored value from the preferences.
     * Returns the default value if no value has been stored before.
     * 
     * @return the stored value or the default
     */
    public long getLong() {
        return prefs.getLong(key, ((Long)defaultValue).longValue());
    }

    /**
     * Looks up, converts and returns the stored value from the preferences.
     * Returns the default value if no value has been stored before.
     * 
     * @return the stored value or the default
     */
    public String getString() {
        return prefs.get(key, (String)defaultValue);
    }

    /**
     * Converts the given value to an Object and stores it in this adapter's
     * Preferences under this adapter's preferences key.
     * 
     * @param newValue
     *            the value to put into the Preferences
     */
    public void setBoolean(boolean newValue) {
        boolean oldValue = getBoolean();
        prefs.putBoolean(key, newValue);
        fireValueChanged(oldValue, newValue);
    }

    /**
     * Converts the given value to an Object and stores it in this adapter's
     * Preferences under this adapter's preferences key.
     * 
     * @param newValue
     *            the value to put into the Preferences
     */
    public void setDouble(double newValue) {
        double oldValue = getDouble();
        prefs.putDouble(key, newValue);
        fireValueChanged(oldValue, newValue);
    }

    /**
     * Converts the given value to an Object and stores it in this adapter's
     * Preferences under this adapter's preferences key.
     * 
     * @param newValue
     *            the value to put into the Preferences
     */
    public void setFloat(float newValue) {
        float oldValue = getFloat();
        prefs.putFloat(key, newValue);
        fireValueChanged(oldValue, newValue);
    }

    /**
     * Converts the given value to an Object and stores it in this adapter's
     * Preferences under this adapter's preferences key.
     * 
     * @param newValue
     *            the value to put into the Preferences
     */
    public void setInt(int newValue) {
        int oldValue = getInt();
        prefs.putInt(key, newValue);
        fireValueChanged(oldValue, newValue);
    }

    /**
     * Converts the given value to an Object and stores it in this adapter's
     * Preferences under this adapter's preferences key.
     * 
     * @param newValue
     *            the value to put into the Preferences
     */
    public void setLong(long newValue) {
        long oldValue = getLong();
        prefs.putLong(key, newValue);
        fireValueChanged(oldValue, newValue);
    }

    /**
     * Converts the given value to an Object and stores it in this adapter's
     * Preferences under this adapter's preferences key.
     * 
     * @param newValue
     *            the value to put into the Preferences
     */
    public void setString(String newValue) {
        String oldValue = getString();
        prefs.put(key, newValue);
        fireValueChanged(oldValue, newValue);
    }

}