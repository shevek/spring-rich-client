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
import java.beans.PropertyChangeSupport;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.springframework.core.enums.LabeledEnum;
import org.springframework.richclient.util.ClassUtils;
import org.springframework.util.Assert;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;

/**
 * Abstract <code>Settings</code> implementation.
 * 
 * @author Peter De Bruycker
 */
public abstract class AbstractSettings implements Settings {

    private PropertyChangeSupport listeners = new PropertyChangeSupport( this );

    private Map defaults = new HashMap();

    private Map children = new HashMap();

    private String name;

    private Settings parent;

    public AbstractSettings( Settings parent, String name ) {
        this.name = name;
        this.parent = parent;
    }

    public boolean contains( String key ) {
        return internalContains( key ) || defaults.containsKey( key );
    }

    protected abstract boolean internalContains( String key );

    /**
     * Should return the names of the child settings initially in this settings instance,
     * i.e. the children that were stored in the backend.
     * 
     * @return the names of the child settings
     */
    protected abstract String[] internalGetChildSettings();

    /*
     * (non-Javadoc)
     * 
     * @see org.springframework.richclient.settings.Settings#setString(java.lang.String,
     *      java.lang.String)
     */
    public void setString( String key, String value ) {
        Assert.notNull( key, "Key cannot be null" );

        String old = getString( key );
        internalSet( key, value );
        afterSet( key, old, value );

    }

    protected abstract Settings internalCreateChild( String key );

    public String[] getChildSettings() {
        if( !childSettingsLoaded ) {
            childSettingsLoaded = true;
            childSettingNames.addAll( Arrays.asList( internalGetChildSettings() ) );
        }
        return (String[]) childSettingNames.toArray( new String[childSettingNames.size()] );
    }

    private boolean childSettingsLoaded = false;

    private Set childSettingNames = new HashSet();

    public Settings getSettings( String name ) {
        if( !children.containsKey( name ) ) {
            children.put( name, internalCreateChild( name ) );
            childSettingNames.add( name );
        }
        return (Settings) children.get( name );
    }

    protected abstract void internalSet( String key, String value );

    /**
     * Return null if no value found for key
     */
    protected abstract String internalGet( String key );

    /*
     * (non-Javadoc)
     * 
     * @see org.springframework.richclient.settings.Settings#getString(java.lang.String)
     */
    public String getString( String key ) {
        Assert.notNull( key, "Key cannot be null" );

        String value = internalGet( key );
        if( !StringUtils.hasText( value ) ) {
            return getDefaultString( key );
        }
        return value;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.springframework.richclient.settings.Settings#setDefaultString(java.lang.String,
     *      java.lang.String)
     */
    public void setDefaultString( String key, String value ) {
        Assert.notNull( key, "Key cannot be null" );

        defaults.put( key, value );
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.springframework.richclient.settings.Settings#getDefaultString(java.lang.String)
     */
    public String getDefaultString( String key ) {
        Assert.notNull( key, "Key cannot be null" );

        if( !defaults.containsKey( key ) ) {
            return "";
        }
        return (String) defaults.get( key );

    }

    /*
     * (non-Javadoc)
     * 
     * @see org.springframework.richclient.settings.Settings#setInt(java.lang.String, int)
     */
    public void setInt( String key, int value ) {
        Assert.notNull( key, "Key cannot be null" );

        int old = getInt( key );
        internalSet( key, String.valueOf( value ) );
        afterSet( key, new Integer( old ), new Integer( value ) );
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.springframework.richclient.settings.Settings#getInt(java.lang.String)
     */
    public int getInt( String key ) {
        Assert.notNull( key, "Key cannot be null" );

        String value = internalGet( key );
        if( !StringUtils.hasText( value ) ) {
            return getDefaultInt( key );
        }
        return Integer.parseInt( value );
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.springframework.richclient.settings.Settings#setDefaultInt(java.lang.String,
     *      int)
     */
    public void setDefaultInt( String key, int value ) {
        Assert.notNull( key, "Key cannot be null" );

        defaults.put( key, String.valueOf( value ) );
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.springframework.richclient.settings.Settings#getDefaultInt(java.lang.String)
     */
    public int getDefaultInt( String key ) {
        Assert.notNull( key, "Key cannot be null" );

        if( !defaults.containsKey( key ) ) {
            return 0;
        }
        return Integer.parseInt( (String) defaults.get( key ) );

    }

    /*
     * (non-Javadoc)
     * 
     * @see org.springframework.richclient.settings.Settings#setDefaultLong(java.lang.String,
     *      long)
     */
    public void setDefaultLong( String key, long value ) {
        Assert.notNull( key, "Key cannot be null" );

        defaults.put( key, String.valueOf( value ) );
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.springframework.richclient.settings.Settings#getDefaultLong(java.lang.String)
     */
    public long getDefaultLong( String key ) {
        Assert.notNull( key, "Key cannot be null" );

        if( !defaults.containsKey( key ) ) {
            return 0L;
        }
        return Long.parseLong( (String) defaults.get( key ) );

    }

    /*
     * (non-Javadoc)
     * 
     * @see org.springframework.richclient.settings.Settings#setFloat(java.lang.String,
     *      float)
     */
    public void setFloat( String key, float value ) {
        Assert.notNull( key, "Key cannot be null" );

        float old = getFloat( key );
        internalSet( key, String.valueOf( value ) );
        afterSet( key, new Float( old ), new Float( value ) );
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.springframework.richclient.settings.Settings#getFloat(java.lang.String)
     */
    public float getFloat( String key ) {
        Assert.notNull( key, "Key cannot be null" );

        String value = internalGet( key );
        if( !StringUtils.hasText( value ) ) {
            return getDefaultFloat( key );
        }
        return Float.parseFloat( value );
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.springframework.richclient.settings.Settings#setDefaultFloat(java.lang.String,
     *      float)
     */
    public void setDefaultFloat( String key, float value ) {
        Assert.notNull( key, "Key cannot be null" );

        defaults.put( key, String.valueOf( value ) );
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.springframework.richclient.settings.Settings#getDefaultFloat(java.lang.String)
     */
    public float getDefaultFloat( String key ) {
        Assert.notNull( key, "Key cannot be null" );

        if( !defaults.containsKey( key ) ) {
            return 0.0f;
        }
        return Float.parseFloat( (String) defaults.get( key ) );

    }

    /*
     * (non-Javadoc)
     * 
     * @see org.springframework.richclient.settings.Settings#setDouble(java.lang.String,
     *      double)
     */
    public void setDouble( String key, double value ) {
        Assert.notNull( key, "Key cannot be null" );

        double old = getDouble( key );
        internalSet( key, String.valueOf( value ) );
        afterSet( key, new Double( old ), new Double( value ) );
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.springframework.richclient.settings.Settings#getDouble(java.lang.String)
     */
    public double getDouble( String key ) {
        Assert.notNull( key, "Key cannot be null" );

        String value = internalGet( key );
        if( !StringUtils.hasText( value ) ) {
            return getDefaultDouble( key );
        }
        return Double.parseDouble( value );
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.springframework.richclient.settings.Settings#setDefaultDouble(java.lang.String,
     *      double)
     */
    public void setDefaultDouble( String key, double value ) {
        Assert.notNull( key, "Key cannot be null" );

        defaults.put( key, String.valueOf( value ) );
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.springframework.richclient.settings.Settings#getDefaultDouble(java.lang.String)
     */
    public double getDefaultDouble( String key ) {
        Assert.notNull( key, "Key cannot be null" );

        if( !defaults.containsKey( key ) ) {
            return 0.0;
        }
        return Double.parseDouble( (String) defaults.get( key ) );

    }

    /*
     * (non-Javadoc)
     * 
     * @see org.springframework.richclient.settings.Settings#setBoolean(java.lang.String,
     *      boolean)
     */
    public void setBoolean( String key, boolean value ) {
        Assert.notNull( key, "Key cannot be null" );

        boolean old = getBoolean( key );
        internalSet( key, String.valueOf( value ) );
        afterSet( key, Boolean.valueOf( old ), Boolean.valueOf( value ) );
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.springframework.richclient.settings.Settings#getBoolean(java.lang.String)
     */
    public boolean getBoolean( String key ) {
        Assert.notNull( key, "Key cannot be null" );

        String value = internalGet( key );
        if( !StringUtils.hasText( value ) ) {
            return getDefaultBoolean( key );
        }
        return Boolean.valueOf( value ).booleanValue();
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.springframework.richclient.settings.Settings#setDefaultBoolean(java.lang.String,
     *      boolean)
     */
    public void setDefaultBoolean( String key, boolean value ) {
        Assert.notNull( key, "Key cannot be null" );

        if( value ) {
            defaults.put( key, String.valueOf( value ) );
        } else {
            defaults.remove( key );
        }
        removeIfDefault( key );
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.springframework.richclient.settings.Settings#getDefaultBoolean(java.lang.String)
     */
    public boolean getDefaultBoolean( String key ) {
        Assert.notNull( key, "Key cannot be null" );

        if( !defaults.containsKey( key ) ) {
            return false;
        }
        return Boolean.valueOf( (String) defaults.get( key ) ).booleanValue();
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.springframework.richclient.settings.Settings#setLabeledEnum(java.lang.String,
     *      org.springframework.enums.LabeledEnum)
     */
    public void setLabeledEnum( String key, LabeledEnum value ) {
        Assert.notNull( key, "Key cannot be null" );

        LabeledEnum old = getLabeledEnum( key );
        internalSet( key, enumToString( value ) );
        afterSet( key, old, value );
    }

    private LabeledEnum stringToEnum( String s ) {
        if( s == null || s.trim().equals( "" ) ) {
            return null;
        }
        return (LabeledEnum) ClassUtils.getFieldValue( s );
    }

    private String enumToString( LabeledEnum e ) {
        return e == null ? "" : ClassUtils.getClassFieldNameWithValue( e.getClass(), e );
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.springframework.richclient.settings.Settings#getLabeledEnum(java.lang.String)
     */
    public LabeledEnum getLabeledEnum( String key ) {
        Assert.notNull( key, "Key cannot be null" );

        String value = internalGet( key );
        if( !StringUtils.hasText( value ) ) {
            return getDefaultLabeledEnum( key );
        }
        return stringToEnum( value );
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.springframework.richclient.settings.Settings#setDefaultLabeledEnum(java.lang.String,
     *      org.springframework.enums.LabeledEnum)
     */
    public void setDefaultLabeledEnum( String key, LabeledEnum value ) {
        Assert.notNull( key, "Key cannot be null" );

        defaults.put( key, enumToString( value ) );
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.springframework.richclient.settings.Settings#getDefaultLabeledEnum(java.lang.String)
     */
    public LabeledEnum getDefaultLabeledEnum( String key ) {
        Assert.notNull( key, "Key cannot be null" );
        return stringToEnum( (String) defaults.get( key ) );
    }

    public boolean isDefault( String key ) {
        Assert.notNull( key, "Key cannot be null" );

        return internalGet( key ) == null || ObjectUtils.nullSafeEquals( internalGet( key ), defaults.get( key ) );
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.springframework.richclient.settings.Settings#getDefaultKeys()
     */
    public String[] getDefaultKeys() {
        return (String[]) defaults.keySet().toArray( new String[0] );
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.springframework.richclient.settings.Settings#getAllKeys()
     */
    public String[] getAllKeys() {
        Set keys = new HashSet();
        keys.addAll( Arrays.asList( getKeys() ) );
        keys.addAll( defaults.keySet() );

        return (String[]) keys.toArray( new String[0] );
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.springframework.richclient.settings.Settings#getName()
     */
    public String getName() {
        return name;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.springframework.richclient.settings.Settings#getParent()
     */
    public Settings getParent() {
        return parent;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.springframework.richclient.settings.Settings#addPropertyChangeListener(java.beans.PropertyChangeListener)
     */
    public void addPropertyChangeListener( PropertyChangeListener l ) {
        listeners.addPropertyChangeListener( l );
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.springframework.richclient.settings.Settings#addPropertyChangeListener(java.lang.String,
     *      java.beans.PropertyChangeListener)
     */
    public void addPropertyChangeListener( String key, PropertyChangeListener l ) {
        listeners.addPropertyChangeListener( key, l );
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.springframework.richclient.settings.Settings#removePropertyChangeListener(java.beans.PropertyChangeListener)
     */
    public void removePropertyChangeListener( PropertyChangeListener l ) {
        listeners.removePropertyChangeListener( l );
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.springframework.richclient.settings.Settings#removePropertyChangeListener(java.lang.String,
     *      java.beans.PropertyChangeListener)
     */
    public void removePropertyChangeListener( String key, PropertyChangeListener l ) {
        listeners.removePropertyChangeListener( key, l );
    }

    private void afterSet( String key, Object oldValue, Object newValue ) {
        removeIfDefault( key );
        firePropertyChange( key, oldValue, newValue );
    }

    private void firePropertyChange( String key, Object oldValue, Object newValue ) {
        listeners.firePropertyChange( key, oldValue, newValue );
    }

    protected abstract void internalRemove( String key );

    private void removeIfDefault( String key ) {
        if( isDefault( key ) ) {
            internalRemove( key );
        }
    }

    public void remove( String key ) {
        if( contains( key ) ) {
            internalRemove( key );
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.springframework.richclient.settings.Settings#setLong(java.lang.String,
     *      long)
     */
    public void setLong( String key, long value ) {
        Assert.notNull( key, "Key cannot be null" );

        long old = getLong( key );
        internalSet( key, String.valueOf( value ) );
        afterSet( key, new Long( old ), new Long( value ) );
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.springframework.richclient.settings.Settings#getLong(java.lang.String)
     */
    public long getLong( String key ) {
        Assert.notNull( key, "Key cannot be null" );

        String value = internalGet( key );
        if( !StringUtils.hasText( value ) ) {
            return getDefaultLong( key );
        }
        return Long.parseLong( value );
    }

    public boolean isRoot() {
        return getParent() == null;
    }

    public void removeSettings() {
        internalRemoveSettings();
        if( getParent() instanceof AbstractSettings ) {
            ((AbstractSettings) getParent()).childSettingNames.remove( getName() );
        }
    }

    protected abstract void internalRemoveSettings();
}
