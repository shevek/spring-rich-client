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

import java.util.Arrays;

import junit.framework.TestCase;

import org.springframework.core.enums.LabeledEnum;

/**
 * @author Peter De Bruycker
 */
public abstract class SettingsAbstractTests extends TestCase {
    private Settings settings;

    private TestablePropertyChangeListener propertyChangeListener;

    protected final void setUp() throws Exception {
        doSetUp();

        settings = createSettings();
        propertyChangeListener = new TestablePropertyChangeListener();
        settings.addPropertyChangeListener( propertyChangeListener );

        assertNotNull( "settings cannot be null", settings );
    }

    protected void doSetUp() throws Exception {

    }

    public final void testGetSettings() {
        Settings childSettings = settings.getSettings( "child" );
        assertTrue( Arrays.asList( settings.getChildSettings() ).contains( "child" ) );

        assertNotNull( childSettings );
        assertEquals( "child", childSettings.getName() );
        assertEquals( settings, childSettings.getParent() );
    }

    public final void testRemove() {
        settings.setString( "key", "value" );
        assertTrue( settings.contains( "key" ) );
        settings.remove( "key" );
        assertFalse( settings.contains( "key" ) );
    }

    public final void testRemoveSettings() {
        Settings childSettings = settings.getSettings( "child" );
        assertTrue( Arrays.asList( settings.getChildSettings() ).contains( "child" ) );

        childSettings.removeSettings();

        assertFalse( Arrays.asList( settings.getChildSettings() ).contains( "child" ) );
    }

    public final void testBoolean() {
        String key = "boolean-value";
        Boolean defaultValue = Boolean.FALSE;
        Boolean newValue = Boolean.TRUE;

        // default value
        assertEquals( defaultValue.booleanValue(), settings.getBoolean( key ) );
        assertEquals( defaultValue.booleanValue(), settings.getDefaultBoolean( key ) );
        assertTrue( settings.isDefault( key ) );
        assertFalse( settings.contains( key ) );

        // change the value
        settings.setBoolean( key, newValue.booleanValue() );

        assertEquals( newValue.booleanValue(), settings.getBoolean( key ) );
        assertFalse( settings.isDefault( key ) );
        assertTrue( settings.contains( key ) );

        // check property change event
        assertPropertyChangeEventFired( key, defaultValue, newValue );
        propertyChangeListener.reset();

        // change the value to the same value, no property change event should
        // be fired
        settings.setBoolean( key, newValue.booleanValue() );
        assertEquals( 0, propertyChangeListener.getCount() );
    }

    public final void testInt() {
        String key = "int-value";
        Integer defaultValue = new Integer( 0 );
        Integer newValue = new Integer( 5 );

        // default value
        assertEquals( defaultValue.intValue(), settings.getInt( key ) );
        assertEquals( defaultValue.intValue(), settings.getDefaultInt( key ) );
        assertTrue( settings.isDefault( key ) );
        assertFalse( settings.contains( key ) );

        // change the value
        settings.setInt( key, newValue.intValue() );

        assertEquals( newValue.intValue(), settings.getInt( key ) );
        assertFalse( settings.isDefault( key ) );
        assertTrue( settings.contains( key ) );

        // check property change event
        assertPropertyChangeEventFired( key, defaultValue, newValue );
        propertyChangeListener.reset();

        // change the value to the same value, no property change event should
        // be fired
        settings.setInt( key, newValue.intValue() );
        assertEquals( 0, propertyChangeListener.getCount() );
    }

    public final void testLong() {
        String key = "long-value";
        Long defaultValue = new Long( 0 );
        Long newValue = new Long( 555L );

        // default value
        assertEquals( defaultValue.longValue(), settings.getLong( key ) );
        assertEquals( defaultValue.longValue(), settings.getDefaultLong( key ) );
        assertTrue( settings.isDefault( key ) );
        assertFalse( settings.contains( key ) );

        // change the value
        settings.setLong( key, newValue.longValue() );

        assertEquals( newValue.longValue(), settings.getLong( key ) );
        assertFalse( settings.isDefault( key ) );
        assertTrue( settings.contains( key ) );

        // check property change event
        assertPropertyChangeEventFired( key, defaultValue, newValue );
        propertyChangeListener.reset();

        // change the value to the same value, no property change event should
        // be fired
        settings.setLong( key, newValue.longValue() );
        assertEquals( 0, propertyChangeListener.getCount() );
    }

    public final void testFloat() {
        String key = "float-value";
        Float defaultValue = new Float( 0.0f );
        Float newValue = new Float( 1.23f );

        // default value
        assertEquals( defaultValue.floatValue(), settings.getFloat( key ), 0.0f );
        assertEquals( defaultValue.floatValue(), settings.getDefaultFloat( key ), 0.0f );
        assertTrue( settings.isDefault( key ) );
        assertFalse( settings.contains( key ) );

        // change the value
        settings.setFloat( key, newValue.floatValue() );

        assertEquals( newValue.floatValue(), settings.getFloat( key ), 0.0f );
        assertFalse( settings.isDefault( key ) );
        assertTrue( settings.contains( key ) );

        // check property change event
        assertPropertyChangeEventFired( key, defaultValue, newValue );
        propertyChangeListener.reset();

        // change the value to the same value, no property change event should
        // be fired
        settings.setFloat( key, newValue.floatValue() );
        assertEquals( 0, propertyChangeListener.getCount() );
    }

    public final void testDouble() {
        String key = "double-value";
        Double defaultValue = new Double( 0.0 );
        Double newValue = new Double( 1.23 );

        // default value
        assertEquals( defaultValue.doubleValue(), settings.getDouble( key ), 0.0 );
        assertEquals( defaultValue.doubleValue(), settings.getDefaultDouble( key ), 0.0 );
        assertTrue( settings.isDefault( key ) );
        assertFalse( settings.contains( key ) );

        // change the value
        settings.setDouble( key, newValue.doubleValue() );

        assertEquals( newValue.doubleValue(), settings.getDouble( key ), 0.0 );
        assertFalse( settings.isDefault( key ) );
        assertTrue( settings.contains( key ) );

        // check property change event
        assertPropertyChangeEventFired( key, defaultValue, newValue );
        propertyChangeListener.reset();

        // change the value to the same value, no property change event should
        // be fired
        settings.setDouble( key, newValue.doubleValue() );
        assertEquals( 0, propertyChangeListener.getCount() );
    }

    public final void testString() {
        String key = "string-value";
        String defaultValue = "";
        String newValue = "value";

        // default value
        assertEquals( defaultValue, settings.getString( key ) );
        assertEquals( defaultValue, settings.getDefaultString( key ) );
        assertTrue( settings.isDefault( key ) );
        assertFalse( settings.contains( key ) );

        // change the value
        settings.setString( key, newValue );

        assertEquals( newValue, settings.getString( key ) );
        assertFalse( settings.isDefault( key ) );
        assertTrue( settings.contains( key ) );

        // check property change event
        assertPropertyChangeEventFired( key, defaultValue, newValue );
        propertyChangeListener.reset();

        // change the value to the same value, no property change event should
        // be fired
        settings.setString( key, newValue );
        assertEquals( 0, propertyChangeListener.getCount() );
    }

    public final void testEnum() {
        String key = "enum-value";
        LabeledEnum defaultValue = null;
        LabeledEnum newValue = TestEnum.ENUM2;

        // default value
        assertEquals( defaultValue, settings.getLabeledEnum( key ) );
        assertEquals( defaultValue, settings.getDefaultLabeledEnum( key ) );
        assertTrue( settings.isDefault( key ) );
        assertFalse( settings.contains( key ) );

        // change the value
        settings.setLabeledEnum( key, newValue );

        assertEquals( newValue, settings.getLabeledEnum( key ) );
        assertFalse( settings.isDefault( key ) );
        assertTrue( settings.contains( key ) );

        // check property change event
        assertPropertyChangeEventFired( key, defaultValue, newValue );
        propertyChangeListener.reset();

        // change the value to the same value, no property change event should
        // be fired
        settings.setLabeledEnum( key, newValue );
        assertEquals( 0, propertyChangeListener.getCount() );
    }

    private void assertPropertyChangeEventFired( String key, Object oldValue, Object newValue ) {
        assertEquals( 1, propertyChangeListener.getCount() );
        assertEquals( key, propertyChangeListener.getEvent().getPropertyName() );
        assertEquals( newValue, propertyChangeListener.getEvent().getNewValue() );
        assertEquals( oldValue, propertyChangeListener.getEvent().getOldValue() );
    }

    protected abstract Settings createSettings() throws Exception;
}
