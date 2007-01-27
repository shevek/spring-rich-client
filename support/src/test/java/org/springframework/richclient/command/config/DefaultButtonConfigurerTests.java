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

import junit.framework.TestCase;
import org.springframework.richclient.image.EmptyIcon;

/**
 * @author Peter De Bruycker
 */
public class DefaultButtonConfigurerTests extends TestCase {

    private CommandFaceDescriptor descriptor;

    private TestableIconInfo iconInfo;

    private CommandButtonLabelInfo labelInfo;

    /*
     * (non-Javadoc)
     * 
     * @see junit.framework.TestCase#setUp()
     */
    protected void setUp() throws Exception {
        descriptor = new CommandFaceDescriptor();
        iconInfo = new TestableIconInfo();
        labelInfo = CommandButtonLabelInfo.valueOf("test");
        descriptor.setIconInfo(iconInfo);
        descriptor.setLabelInfo(labelInfo);
        descriptor.setCaption("Tool tip");
    }

    public void testConfigureWithNullDescriptor() {
        DefaultCommandButtonConfigurer configurer = new DefaultCommandButtonConfigurer();
        try {
            configurer.configure(new JButton(), null, null);
            fail("Should throw IllegalArgumentException");
        }
        catch (IllegalArgumentException e) {
            pass();
        }
    }

    public void testConfigureWithNullButton() {
        DefaultCommandButtonConfigurer configurer = new DefaultCommandButtonConfigurer();
        try {
            configurer.configure(null, null, descriptor);
            fail("Should throw IllegalArgumentException");
        }
        catch (IllegalArgumentException e) {
            pass();
        }
    }

    public void testConfigure() {
        JButton button = new JButton();
        DefaultCommandButtonConfigurer configurer = new DefaultCommandButtonConfigurer();
        configurer.configure(button, null, descriptor);

        assertEquals(labelInfo.getText(), button.getText());
        assertEquals(iconInfo.configuredButton, button);
        assertEquals("Tool tip", button.getToolTipText());
    }

    private static void pass() {
        // test passes
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