/*
 * Copyright 2002-2004 the original author or authors.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.springframework.richclient.command.config;

import java.awt.event.ActionEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.AbstractAction;
import javax.swing.AbstractButton;
import javax.swing.Action;
import javax.swing.JButton;

import org.springframework.richclient.factory.LabelInfoFactory;
import org.springframework.richclient.image.EmptyIcon;

import junit.framework.TestCase;

/**
 * @author Peter De Bruycker
 */
public class CommandFaceDescriptorTests extends TestCase {

    private CommandButtonLabelInfo buttonLabelInfo;

    private CommandFaceDescriptor descriptor;

    private TestPropertyChangeListener propertyChangeListener;

    public void testDefaultConstructor() {
        CommandFaceDescriptor descriptor = new CommandFaceDescriptor();

        assertEquals(CommandFaceDescriptor.EMPTY_LABEL, descriptor
                .getButtonLabelInfo());
        assertTrue(descriptor.isEmpty());
        assertEquals(CommandFaceDescriptor.EMPTY_LABEL.getText(), descriptor
                .getText());
        assertNull(descriptor.getDescription());
        assertEquals(CommandFaceDescriptor.EMPTY_ICON, descriptor
                .getButtonIconInfo());
        assertNull(descriptor.getCaption());
    }

    public void testConstructorWithNullButtonLabelInfo() {
        try {
            new CommandFaceDescriptor((CommandButtonLabelInfo) null);
            fail("Should throw IllegalArgumentException");
        } catch (IllegalArgumentException e) {
            pass();
        }
    }

    public void testConstructorWithEncodedLabel() {
        CommandFaceDescriptor descriptor = new CommandFaceDescriptor(
                "&Test@ctrl T");
        assertEquals("Test", descriptor.getText());
        assertFalse(CommandFaceDescriptor.EMPTY_LABEL.equals(descriptor
                .getButtonLabelInfo()));
        assertFalse(descriptor.isEmpty());
        assertNull(descriptor.getDescription());
        assertNull(descriptor.getCaption());
        assertEquals(CommandFaceDescriptor.EMPTY_ICON, descriptor
                .getButtonIconInfo());
    }

    public void testConstructorWithEmptyEncodedLabel() {
        CommandFaceDescriptor descriptor = new CommandFaceDescriptor("");
        assertEquals(CommandFaceDescriptor.EMPTY_LABEL, descriptor
                .getButtonLabelInfo());
    }

    public void testConstructorWithEncodedLabelAndIcon() {
        CommandFaceDescriptor descriptor = new CommandFaceDescriptor(
                "&Test@ctrl T", EmptyIcon.SMALL, "caption");

        assertEquals("Test", descriptor.getText());
        assertFalse(CommandFaceDescriptor.EMPTY_LABEL.equals(descriptor
                .getButtonLabelInfo()));
        assertFalse(descriptor.isEmpty());
        assertNull(descriptor.getDescription());
        assertEquals("caption", descriptor.getCaption());
        assertFalse(CommandFaceDescriptor.EMPTY_ICON.equals(descriptor
                .getButtonIconInfo()));
        assertEquals(EmptyIcon.SMALL, descriptor.getButtonIconInfo()
                .getIcon());
    }

    public void testSetDescription() {
        CommandFaceDescriptor descriptor = new CommandFaceDescriptor(
                "&Test@ctrl T", EmptyIcon.SMALL, "caption");
        descriptor.setDescription("Long description");

        assertEquals("Long description", descriptor.getDescription());
    }

    public void testSetCaption() {
        descriptor.setCaption("new caption");
        assertTrue(propertyChangeListener.changed);
        assertEquals(descriptor, propertyChangeListener.source);
        assertEquals("caption", propertyChangeListener.oldValue);
        assertEquals("new caption", propertyChangeListener.newValue);
        assertEquals(CommandFaceDescriptor.CAPTION_PROPERTY,
                propertyChangeListener.propertyName);

        propertyChangeListener.reset();

        // caption not changed
        descriptor.setCaption("new caption");
        assertFalse(propertyChangeListener.changed);
    }

    public void testSetIconNull() {
        descriptor.setIcon(null);
        assertNull(descriptor.getButtonIconInfo().getIcon());

        descriptor.setCommandButtonIconInfo(CommandFaceDescriptor.EMPTY_ICON);
        descriptor.setIcon(null);
        assertEquals(CommandFaceDescriptor.EMPTY_ICON, descriptor
                .getButtonIconInfo());
    }

    public void testSetIcon() {
        CommandButtonIconInfo oldIconInfo = descriptor
                .getButtonIconInfo();
        descriptor.setIcon(EmptyIcon.LARGE);
        assertEquals(EmptyIcon.LARGE, descriptor.getButtonIconInfo()
                .getIcon());

        assertTrue(propertyChangeListener.changed);
        assertEquals(descriptor, propertyChangeListener.source);
        assertEquals(oldIconInfo, propertyChangeListener.oldValue);
        assertEquals(descriptor.getButtonIconInfo(),
                propertyChangeListener.newValue);
        assertEquals(CommandFaceDescriptor.COMMAND_BUTTON_ICON_PROPERTY,
                propertyChangeListener.propertyName);

        propertyChangeListener.reset();
        // no change
        descriptor.setIcon(EmptyIcon.LARGE);
        assertFalse(propertyChangeListener.changed);
    }

    public void testSetNullIconInfo() {
        descriptor.setCommandButtonIconInfo(null);
        assertEquals(CommandFaceDescriptor.EMPTY_ICON, descriptor
                .getButtonIconInfo());
    }

    public void testSetNullLabelInfo() {
        descriptor.setCommandButtonLabelInfo((CommandButtonLabelInfo) null);
        assertEquals(CommandFaceDescriptor.EMPTY_LABEL, descriptor
                .getButtonLabelInfo());
    }
    
    public void testConfigureWithConfigurer() {
        JButton button = new JButton();
        TestCommandButtonConfigurer configurer = new TestCommandButtonConfigurer();
        descriptor.configure(button, configurer);
        assertEquals(button, configurer.button);
        assertEquals(descriptor, configurer.face);
    }
    
    public void testConfigureWithNullConfigurerAndNullButton() {
        try {
            descriptor.configure(new JButton(), null);
            fail("Should throw IllegalArgumentException");
        } catch (IllegalArgumentException e) {
            pass();
        }
        try {
            descriptor.configure(null, new TestCommandButtonConfigurer());
            fail("Should throw IllegalArgumentException");
        } catch (IllegalArgumentException e) {
            pass();
        }
    }
    
    private static class TestCommandButtonConfigurer implements CommandButtonConfigurer
    {

        private CommandFaceDescriptor face;
        private AbstractButton button;

        /* (non-Javadoc)
         * @see org.springframework.richclient.command.config.CommandButtonConfigurer#configure(org.springframework.richclient.command.config.CommandFaceDescriptor, javax.swing.AbstractButton)
         */
        public void configure(CommandFaceDescriptor face, AbstractButton button) {
            this.face = face;
            this.button = button;
        }
        
    }
    
    public void testSetLabelInfoAsText() {
        CommandButtonLabelInfo oldLabelInfo = descriptor.getButtonLabelInfo();
        descriptor.setCommandButtonLabelInfo("&Other Test@ctrl O");
        CommandButtonLabelInfo newLabelInfo = descriptor.getButtonLabelInfo();
        assertEquals(LabelInfoFactory.createButtonLabelInfo("&Other Test@ctrl O"), newLabelInfo);
        
        assertTrue(propertyChangeListener.changed);
        assertEquals(descriptor, propertyChangeListener.source);
        assertEquals(oldLabelInfo, propertyChangeListener.oldValue);
        assertEquals(newLabelInfo, propertyChangeListener.newValue);
        assertEquals(CommandFaceDescriptor.COMMAND_BUTTON_LABEL_PROPERTY,
                propertyChangeListener.propertyName);

        propertyChangeListener.reset();
        // no change
        descriptor.setCommandButtonLabelInfo("&Other Test@ctrl O");
        assertFalse(propertyChangeListener.changed);
    }
    
    public void testSetLabelInfo() {
        CommandButtonLabelInfo oldLabelInfo = descriptor.getButtonLabelInfo();
        CommandButtonLabelInfo newLabelInfo = LabelInfoFactory.createButtonLabelInfo("&Other Test@ctrl O");
        descriptor.setCommandButtonLabelInfo(newLabelInfo);
        assertEquals(newLabelInfo, descriptor.getButtonLabelInfo());
        
        assertTrue(propertyChangeListener.changed);
        assertEquals(descriptor, propertyChangeListener.source);
        assertEquals(oldLabelInfo, propertyChangeListener.oldValue);
        assertEquals(newLabelInfo, propertyChangeListener.newValue);
        assertEquals(CommandFaceDescriptor.COMMAND_BUTTON_LABEL_PROPERTY,
                propertyChangeListener.propertyName);

        propertyChangeListener.reset();
        // no change
        descriptor.setCommandButtonLabelInfo("&Other Test@ctrl O");
        assertFalse(propertyChangeListener.changed);
    }
    
    public void testConfigureNullAction() {
        try {
            descriptor.configure(null);
            fail("Should throw IllegalArgumentException");
        } catch (IllegalArgumentException e) {
            pass();
        }
    }
    
    public void testConfigure() {
        Action action = new AbstractAction() {
            public void actionPerformed(ActionEvent arg0) {
            }
        };

        descriptor.configure(action);
        assertEquals("name", descriptor.getButtonLabelInfo().getText(), action.getValue(Action.NAME));
        assertEquals("mnemonic", new Integer(descriptor.getButtonLabelInfo().getMnemonic()), action.getValue(Action.MNEMONIC_KEY));
        assertEquals("accelerator", descriptor.getButtonLabelInfo().getAccelerator(), action.getValue(Action.ACCELERATOR_KEY));
        assertEquals("icon", descriptor.getButtonIconInfo().getIcon(), action.getValue(Action.SMALL_ICON));
        assertEquals("caption", descriptor.getCaption(), action.getValue(Action.SHORT_DESCRIPTION));
        assertEquals("description", descriptor.getDescription(), action.getValue(Action.LONG_DESCRIPTION));
    }
    
    public void testSetIconInfo() {
        CommandButtonIconInfo oldIconInfo = descriptor
                .getButtonIconInfo();
        CommandButtonIconInfo newIconInfo = new CommandButtonIconInfo(
                EmptyIcon.LARGE);
        descriptor.setCommandButtonIconInfo(newIconInfo);
        assertEquals(newIconInfo, descriptor.getButtonIconInfo());

        assertTrue(propertyChangeListener.changed);
        assertEquals(descriptor, propertyChangeListener.source);
        assertEquals(oldIconInfo, propertyChangeListener.oldValue);
        assertEquals(newIconInfo, propertyChangeListener.newValue);
        assertEquals(CommandFaceDescriptor.COMMAND_BUTTON_ICON_PROPERTY,
                propertyChangeListener.propertyName);

        propertyChangeListener.reset();
        // no change
        descriptor.setCommandButtonIconInfo(newIconInfo);
        assertFalse(propertyChangeListener.changed);
    }

    private static class TestPropertyChangeListener implements
            PropertyChangeListener {

        private boolean changed = false;

        private String propertyName;

        private Object newValue;

        private Object oldValue;

        private Object source;

        /*
         * (non-Javadoc)
         * 
         * @see java.beans.PropertyChangeListener#propertyChange(java.beans.PropertyChangeEvent)
         */
        public void propertyChange(PropertyChangeEvent e) {
            changed = true;
            propertyName = e.getPropertyName();
            newValue = e.getNewValue();
            oldValue = e.getOldValue();
            source = e.getSource();
        }

        /**
         *  
         */
        public void reset() {
            changed = false;
            propertyName = null;
            newValue = null;
            oldValue = null;
            source = null;
        }

    }

    private static void pass() {
        // test passes
    }

    public void testConstructorWithButtonLabelInfo() {
        CommandFaceDescriptor descriptor = new CommandFaceDescriptor(
                buttonLabelInfo);

        assertEquals(buttonLabelInfo, descriptor.getButtonLabelInfo());
        assertFalse(descriptor.isEmpty());
        assertEquals("Test", descriptor.getText());
        assertNull(descriptor.getDescription());
        assertEquals(CommandFaceDescriptor.EMPTY_ICON, descriptor
                .getButtonIconInfo());
        assertNull(descriptor.getCaption());
    }

    /**
     * @see junit.framework.TestCase#setUp()
     */
    protected void setUp() throws Exception {
        buttonLabelInfo = LabelInfoFactory
                .createButtonLabelInfo("&Test@ctrl T");
        descriptor = new CommandFaceDescriptor("&Test@ctrl T", EmptyIcon.SMALL,
                "caption");
        descriptor.setDescription("long description");
        assertNotNull(descriptor.getButtonLabelInfo().getAccelerator());
        propertyChangeListener = new TestPropertyChangeListener();
        descriptor.addPropertyChangeListener(propertyChangeListener);
    }

}