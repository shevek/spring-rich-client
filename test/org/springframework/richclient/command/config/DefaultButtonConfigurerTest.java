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

import javax.swing.AbstractButton;
import javax.swing.JButton;

import org.springframework.richclient.image.EmptyIcon;

import junit.framework.TestCase;

/**
 * @author Peter De Bruycker
 */
public class DefaultButtonConfigurerTest extends TestCase {

    private CommandFaceDescriptor descriptor;
    private TestableIconInfo iconInfo;
    private TestableLabelInfo labelInfo;

    /*
     * (non-Javadoc)
     * 
     * @see junit.framework.TestCase#setUp()
     */
    protected void setUp() throws Exception {
        descriptor = new CommandFaceDescriptor();
        iconInfo = new TestableIconInfo();
        labelInfo = new TestableLabelInfo();
        descriptor.setCommandButtonIconInfo(iconInfo);
        descriptor.setCommandButtonLabelInfo(labelInfo);
        descriptor.setCaption("Tool tip");
    }

    public void testConfigureWithNullDescriptor() {
        DefaultButtonConfigurer configurer = new DefaultButtonConfigurer();
        try {
            configurer.configure(null, new JButton());
            fail("Should throw IllegalArgumentException");
        } catch (IllegalArgumentException e) {
            pass();
        }
    }
    
    public void testConfigureWithNullButton() {
        DefaultButtonConfigurer configurer = new DefaultButtonConfigurer();
        try {
            configurer.configure(descriptor, null);
            fail("Should throw IllegalArgumentException");
        } catch (IllegalArgumentException e) {
            pass();
        }
    }
    
    public void testConfigure() {
        JButton button = new JButton();
        DefaultButtonConfigurer configurer = new DefaultButtonConfigurer();
        configurer.configure(descriptor, button);
        
        assertEquals(labelInfo.configuredButton, button);
        assertEquals(iconInfo.configuredButton, button);
        assertEquals("Tool tip", button.getToolTipText());
    }
    
    private static void pass() {
        // test passes
    }

    private static class TestableLabelInfo extends CommandButtonLabelInfo {

        private AbstractButton configuredButton;

        public TestableLabelInfo() {
            super("test");
        }

        /*
         * (non-Javadoc)
         * 
         * @see org.springframework.richclient.command.config.CommandButtonLabelInfo#configure(javax.swing.AbstractButton)
         */
        public AbstractButton configure(AbstractButton button) {
            configuredButton = button;
            return button;
        }
    }

    private static class TestableIconInfo extends CommandButtonIconInfo {

        private AbstractButton configuredButton;

        public TestableIconInfo() {
            super(EmptyIcon.SMALL);
        }

        /*
         * (non-Javadoc)
         * 
         * @see org.springframework.richclient.command.config.CommandButtonIconInfo#configure(javax.swing.AbstractButton)
         */
        public AbstractButton configure(AbstractButton button) {
            configuredButton = button;
            return button;
        }
    }
}