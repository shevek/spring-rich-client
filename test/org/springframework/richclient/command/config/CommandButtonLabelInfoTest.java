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
package org.springframework.richclient.command.config;

import javax.swing.JButton;
import javax.swing.JMenuItem;
import javax.swing.KeyStroke;

import junit.framework.TestCase;

import org.springframework.richclient.factory.LabelInfo;

/**
 * @author Peter De Bruycker
 */
public class CommandButtonLabelInfoTest extends TestCase {

    private LabelInfo labelInfo;

    private KeyStroke accelerator;

    public static void pass() {
        // test passes
    }

    /**
     * @see junit.framework.TestCase#setUp()
     */
    protected void setUp() throws Exception {
        super.setUp();
        labelInfo = new LabelInfo("Test", 'T', 3);
        accelerator = KeyStroke.getKeyStroke("ctrl T");
        
        // make sure the keystroke did get parsed
        assertNotNull(accelerator);
    }

    public void testConstructorLabelInfo() {
        CommandButtonLabelInfo info = new CommandButtonLabelInfo(labelInfo,
                accelerator);

        assertEquals(labelInfo.getText(), info.getText());
        assertEquals(labelInfo.getMnemonic(), info.getMnemonic());
        assertEquals(labelInfo.getMnemonicIndex(), info.getMnemonicIndex());
        assertEquals(accelerator, info.getAccelerator());
    }

    public void testConstructorLabelInfoNoAccelerator() {
        CommandButtonLabelInfo info = new CommandButtonLabelInfo(labelInfo,
                null);

        assertEquals(labelInfo.getText(), info.getText());
        assertEquals(labelInfo.getMnemonic(), info.getMnemonic());
        assertEquals(labelInfo.getMnemonicIndex(), info.getMnemonicIndex());
        assertNull(info.getAccelerator());
    }

    public void testConstructorText() {
        CommandButtonLabelInfo info = new CommandButtonLabelInfo("Test");

        assertEquals("Test", info.getText());
        assertEquals(0, info.getMnemonic());
        assertEquals(0, info.getMnemonicIndex());
        assertNull(info.getAccelerator());
    }

    public void testConstructorNullAsLabelInfo() {
        try {
            new CommandButtonLabelInfo(null, accelerator);
            fail("Should throw IllegalArgumentException");
        } catch (IllegalArgumentException e) {
            pass();
        }
    }

    public void testConfigureNull() {
        CommandButtonLabelInfo info = new CommandButtonLabelInfo("test");
        try {
            info.configure(null);
            fail("Should throw IllegalArgumentException");
        } catch (IllegalArgumentException e) {
            pass();
        }
    }

    public void testConfigureJButton() {
        CommandButtonLabelInfo info = new CommandButtonLabelInfo(labelInfo,
                accelerator);

        // try a button
        JButton button = new JButton();
        info.configure(button);

        assertEquals(info.getText(), button.getText());
        assertEquals(info.getMnemonic(), button.getMnemonic());
        assertEquals(info.getMnemonicIndex(), button
                .getDisplayedMnemonicIndex());
    }

    public void testConfigureJMenuItem() {
        CommandButtonLabelInfo info = new CommandButtonLabelInfo(labelInfo,
                accelerator);

        // try a menu item
        JMenuItem button = new JMenuItem();
        info.configure(button);

        assertEquals(info.getText(), button.getText());
        assertEquals(info.getMnemonic(), button.getMnemonic());
        assertEquals(info.getMnemonicIndex(), button
                .getDisplayedMnemonicIndex());
        assertEquals(accelerator, button.getAccelerator());
    }

}
