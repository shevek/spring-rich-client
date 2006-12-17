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
package org.springframework.richclient.factory;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import junit.framework.TestCase;

/**
 * @author Peter De Bruycker
 */
public class LabelInfoTests extends TestCase {

    public void testConstructor() {
        LabelInfo info = new LabelInfo("test");
        assertEquals("test", info.getText());
        assertEquals(0, info.getMnemonic());
        assertEquals(0, info.getMnemonicIndex());

        info = new LabelInfo("test", 't');
        assertEquals("test", info.getText());
        assertEquals('t', info.getMnemonic());
        assertEquals(0, info.getMnemonicIndex());

        info = new LabelInfo("test", 't', 3);
        assertEquals("test", info.getText());
        assertEquals('t', info.getMnemonic());
        assertEquals(3, info.getMnemonicIndex());
    }
    
    public void testEquals() throws Exception {
        LabelInfo info1 = new LabelInfo("test", 0, 0);
        LabelInfo info2 = new LabelInfo("test", 0, 0);
        assertTrue(info1.equals(info2));
        info2 = new LabelInfo("test", 1, 0);
        assertFalse(info1.equals(info2));
        info2 = new LabelInfo("test", 0, 1);
        assertFalse(info1.equals(info2));
        info2 = new LabelInfo("test2", 0, 0);
        assertFalse(info1.equals(info2));
        assertFalse(info1.equals(null));
        info2 = new LabelInfo("test", 0,0) {};
        assertFalse(info1.equals(info2));
    }

    public void testHashCode() throws Exception {
        LabelInfo info1 = new LabelInfo("test", 0, 0);
        LabelInfo info2 = new LabelInfo("test", 0, 0);
        assertTrue(info1.hashCode() == info2.hashCode());
        info2 = new LabelInfo("test", 1, 0);
        assertFalse(info1.hashCode() == info2.hashCode());
        LabelInfo info3 = new LabelInfo("test", 0, 1);
        assertFalse(info1.hashCode() == info2.hashCode());
        assertFalse(info2.hashCode() == info3.hashCode());
        info2 = new LabelInfo("test2", 0, 0);
        assertFalse(info1.hashCode() == info2.hashCode());
    }

    public void testConstructorEmptyText() {
        LabelInfo info = new LabelInfo("", 'a', 5);
        assertEquals("", info.getText());
        assertEquals('a', info.getMnemonic());
        assertEquals(-1, info.getMnemonicIndex());
    }

    public void testConstructorNullText() {
        try {
            new LabelInfo(null);
            fail("no null text");
        }
        catch (IllegalArgumentException e) {
            pass();
        }
    }

    public void testConstructorNegativeMnemonic() {
        try {
            new LabelInfo("test", -5);
            fail("No negative mnemonics");
        }
        catch (IllegalArgumentException e) {
            pass();
        }
    }

    public void testConfigureLabel() {
        JLabel label = new JLabel();
        LabelInfo info = new LabelInfo("Save As", 'A', 5);
        info.configureLabel(label);

        assertEquals("Save As", label.getText());
        assertEquals('A', label.getDisplayedMnemonic());
        assertEquals(5, label.getDisplayedMnemonicIndex());
    }

    public void testConfigureLabelNull() {
        LabelInfo info = new LabelInfo("Test");
        try {
            info.configureLabel(null);
            fail("Should throw IllegalArgumentException");
        }
        catch (IllegalArgumentException e) {
            pass();
        }
    }

    public void testConfigureLabelFor() {
        JTextField field = new JTextField();
        JLabel label = new JLabel();
        LabelInfo info = new LabelInfo("Name", 'N');
        info.configureLabelFor(label, field);

        assertEquals("LabelInfo must add colon if none present", "Name:", label.getText());
        assertEquals('N', label.getDisplayedMnemonic());
        assertEquals(field, label.getLabelFor());
    }

    public void testConfigureLabelForWithColon() {
        JTextField field = new JTextField();
        JLabel label = new JLabel();
        LabelInfo info = new LabelInfo("Name:", 'N');
        info.configureLabelFor(label, field);

        assertEquals("Name:", label.getText());
        assertEquals('N', label.getDisplayedMnemonic());
        assertEquals(field, label.getLabelFor());
    }

    public void testConfigureLabelForJPanel() {
        JPanel panel = new JPanel();
        JLabel label = new JLabel();
        LabelInfo info = new LabelInfo("Name", 'N');
        info.configureLabelFor(label, panel);

        assertEquals("No colon for panel", "Name", label.getText());
        assertEquals('N', label.getDisplayedMnemonic());
        assertEquals(panel, label.getLabelFor());
    }

    public void testConstructorMnemonicIndexGreaterThanLength() {
        try {
            new LabelInfo("test", 't', 15);
            fail("Mnemonic index < text.length()");
        }
        catch (IllegalArgumentException e) {
            pass();
        }
    }

    public void testConstructorNegativeMnemonicIndex() {
        try {
            new LabelInfo("test", 't', -2);
            fail("index must be >= -1");
        }
        catch (IllegalArgumentException e) {
            pass();
        }
    }

    public static final void pass() {
        // test passes
    }
}